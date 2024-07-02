package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.gld.service.IContractFinanceIncomeService;
import com.hand.hls.gld.service.IGldContractCashflowService;
import com.hand.hls.hls.dto.HlsDurationCompare;
import com.hand.hls.hls.dto.HlsDurationHd;
import com.hand.hls.hls.dto.HlsDurationLn;
import com.hand.hls.hls.mapper.HlsDurationCompareMapper;
import com.hand.hls.hls.mapper.HlsDurationLnMapper;
import com.hand.hls.hls.service.HlsDurationHdService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.utils.HlsCusConstant;
import lombok.SneakyThrows;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description 合同变更结束过程
 *
 * @author Eugene Song 2020年6月11日
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusDurationExecuteEndServiceTask implements JavaDelegate, IActivitiBean {

    private static final String REJECTED = "REJECTED";

    private static final String APPROVED = "APPROVED";

    private static final String ET = "ET";

    private static final String PREPAYMENT = "PREPAYMENT";

    private static final String FINANCIAL_TERMS = "FINANCIAL_TERMS";

    Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    private HlsDurationHdService hdService;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private IContractFinanceIncomeService iContractFinanceIncomeService;
    @Autowired
    private HlsDurationCompareMapper hlsDurationCompareMapper;
    @Autowired
    private JeTrxCommonService jeTrxCommonService;
    @Autowired
    private IGldContractCashflowService gldContractCashflowService;
    @Autowired
    private HlsCusConContractMapper contractMapper;
    @Autowired
    private HlsDurationLnMapper hlsDurationLnMapper;
    @Autowired
    private HlsCusPrjProjectService prjProjectService;

    public HlsCusDurationExecuteEndServiceTask() {

    }

    @SneakyThrows
    @Override
    public void execute(DelegateExecution delegateExecution) {
        String flag = "";
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long hdId = (Long) delegateExecution.getVariable("hdId");
        String endTaskFlag = "N";
        if (delegateExecution.getVariable("endTaskFlag") != null) {
            endTaskFlag = delegateExecution.getVariable("endTaskFlag").toString();
        }

        Long processInstanceId = Long.parseLong(delegateExecution.getProcessInstanceId());
        HlsDurationHd hlsDurationHd = new HlsDurationHd();

        hlsDurationHd.setHdId(hdId);
        hlsDurationHd = hdService.selectByPrimaryKey(requestCtx, hlsDurationHd);

        databaseLockProvider.lock(hlsDurationHd);
        if("APPROVING".equalsIgnoreCase(hlsDurationHd.getExecuteStatus())){
            if (APPROVED.equalsIgnoreCase(result)) {
                flag = "APPROVED";
                String executeType = hlsDurationHd.getExecuteType();

                if (endTaskFlag.equals("N")) {
                    //监听器执行了两遍，很奇怪，用标志来使其执行一遍
                    if (ET.equals(executeType) || PREPAYMENT.equals(executeType) || FINANCIAL_TERMS.equals(executeType)) {
                        //校验
                        hdService.executeCheck(hlsDurationHd.getHdId());
                        //更新现金流
                        List<HlsCusConContract> conContractList = hdService.executeCashflow(requestCtx, hlsDurationHd);
                        //重算收益分摊 com.hand.hls.gld.service.IGldContractCashflowService#clacFinanceIncome
//                        iContractFinanceIncomeService.financeIncomeSharing(requestCtx, conContractList);
                        if(FINANCIAL_TERMS.equalsIgnoreCase(hlsDurationHd.getExecuteType())){
                            HlsDurationLn ln = new HlsDurationLn();
                            ln.setHdId(hlsDurationHd.getHdId());
                            ln.setSourceType(hlsDurationHd.getExecuteType());
                            List<HlsDurationLn> hlsDurationLnList = hlsDurationLnMapper.select(ln);
                            for(HlsDurationLn durationLn : hlsDurationLnList){
                                HlsCusConContract cusConContract = new HlsCusConContract();
                                cusConContract.setContractId(durationLn.getContractId());
                                List<HlsCusConContract> conContractLists = contractMapper.conGetContract(cusConContract);
                                for(HlsCusConContract ct : conContractLists ){
                                    //更新支付表状态未起租
                                    ct.setContractStatus("INCEPT");
                                    contractMapper.updateByPrimaryKeySelective(ct);
                                    gldContractCashflowService.clacFinanceIncome(requestCtx,ct.getContractId(),ct.getVatRate(),ct.getXirr());
                                }
                            }


                            //变更通过 则原业务申请单据更新
                            HlsDurationHd durationHd = new HlsDurationHd();
                            durationHd.setHdId(hlsDurationHd.getSourceHdId());
                            durationHd.setDurationStatus("PASS");
                            hdService.updateByPrimaryKey(requestCtx,durationHd);
                            //更新虚拟合同状态为起租
                            HlsCusPrjProject prjProject = new HlsCusPrjProject();
                            prjProject.setProjectId(hlsDurationHd.getProjectId());
                            prjProject.setContractStatus("SIGN");
                            prjProject.setConApplicationChangeStatus("NEW");
                            prjProjectService.updateByPrimaryKeySelective(requestCtx,prjProject);
                        }
                        //生成凭证
//                        createJeTrx(requestCtx, hlsDurationHd);
                    }
                    //客户信息变更
                    if (FINANCIAL_TERMS.equals(executeType) && HlsCusConstant.FLAG.Y.equals(hlsDurationHd.getBpInfoChangeFlag())) {
                        hdService.executeBpInfo(requestCtx, hlsDurationHd);
                    }
                    //租赁物 抵质押物 变更
                    //只更新数量，金额 后续功能 价值评估中 进行
                    hdService.executeLeaseItem(requestCtx, hlsDurationHd);

                }
            } else if (REJECTED.equalsIgnoreCase(result)) {
                flag = "REJECTED";
            }
            hlsDurationHd.setExecuteStatus(flag);
            hlsDurationHd.setExecuteInstanceId(processInstanceId);
            hdService.updateByPrimaryKeySelective(requestCtx, hlsDurationHd);
            delegateExecution.setVariable("endTaskFlag", "Y");
        }
    }

    /**
     * @Title: createJeTrx
     * @Discription: 生成凭证
     * @Param: [requestCtx, hd]
     * @Return: void
     */
    private void createJeTrx(IRequest requestCtx, HlsDurationHd hd) {
        HlsDurationCompare compare = new HlsDurationCompare();
        compare.setHdId(hd.getHdId());
        List<HlsDurationCompare> hlsDurationCompareList = hlsDurationCompareMapper.select(compare);

        for (HlsDurationCompare hlsDurationCompare : hlsDurationCompareList) {
            //生成凭证
            Map<String, AbstractJeTrxService> interfaceMaps = jeTrxCommonService.map;
            AbstractJeTrxService contractChangeTrxService = interfaceMaps.get("CON_CONTRACT_CHANGE");
            Map contractChangeParams = new HashMap<>();
            contractChangeParams.put("jeTrxId", hlsDurationCompare.getCompareId());
            contractChangeParams.put("companyId", requestCtx.getCompanyId());
            contractChangeParams.put("hdId", hd.getHdId());
            contractChangeParams.put("compareId", hlsDurationCompare.getCompareId());
            contractChangeParams.put("jeSourceId", hlsDurationCompare.getCompareId());
            contractChangeParams.put("sourceDoc", "CON_CONTRACT_CHANGE");
            contractChangeTrxService.process(requestCtx, contractChangeParams);
        }
    }
}

