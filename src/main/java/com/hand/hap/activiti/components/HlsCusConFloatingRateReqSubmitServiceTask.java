package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.lock.components.DatabaseLockProvider;

import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.dto.HlsCusConFloatingRateReq;
import com.hand.hls.cont.dto.HlsCusConFloatingRateReqLn;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.mapper.HlsCusConFloatingRateReqLnMapper;
import com.hand.hls.cont.mapper.HlsCusConFloatingRateReqMapper;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.cont.service.HlsCusConFloatingRateReqLnService;
import com.hand.hls.cont.service.HlsCusConFloatingRateReqService;
import com.hand.hls.cont.utils.BeanRefUtils;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.gld.service.IContractFinanceIncomeService;
import com.hand.hls.gld.service.IGldContractCashflowService;
import com.hand.hls.hls.dto.HlsDurationLn;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotationCashflow;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationCashflowMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.prj.service.HlsCusPrjQuotationService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by zhangyu on 2018/6/4.
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusConFloatingRateReqSubmitServiceTask implements JavaDelegate, IActivitiBean {
    public static final String CON_DOCUMENT_TYPE = "CON_CONTRACT";
    public static final String LON_CON_DOCUMENT_TYPE = "LON_CONTRACT";
    @Autowired
    private HlsCusConFloatingRateReqService hlsCusConFloatingRateReqService;
    @Autowired
    private HlsCusConFloatingRateReqLnService hlsCusConFloatingRateReqLnService;
    @Autowired
    private HlsCusConFloatingRateReqMapper hlsCusConFloatingRateReqMapper;
    @Autowired
    private HlsCusConFloatingRateReqLnMapper hlsCusConFloatingRateReqLnMapper ;

    @Autowired
    private HlsCusConContractCashflowMapper conContractCashflowMapper;

    @Autowired
    private HlsCusConContractCashflowService conContractCashflowService;

    @Autowired
    private HlsCusPrjQuotationCashflowMapper hlsCusPrjQuotationCashflowMapper;

    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Autowired
    private HlsCusPrjProjectService projectService;

    @Autowired
    private IContractFinanceIncomeService iContractFinanceIncomeService;

    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;


    @Autowired
    private JeTrxCommonService commonService;

    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;

    @Autowired
    private HlsCusPrjQuotationMapper hlsCusPrjQuotationMapper;

    private static final String LPR_ADJUST_PERIOD_THIS = "THIS_PERIOD";
    private static final String LPR_ADJUST_PERIOD_NEXT = "NEXT_PERIOD";
    private static final String NEXT_YDAY = "NEXT_YDAY";

    @Autowired
    private IGldContractCashflowService gldContractCashflowService;


    @Autowired
    private HlsCusPrjQuotationService hlsCusPrjQuotationService;


    public HlsCusConFloatingRateReqSubmitServiceTask() {
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        List<HlsCusConContract> conContractList = new ArrayList<>();

        String flag = null;
        IRequest requestCtx = RequestHelper.getCurrentRequest();
        String result = (String) delegateExecution.getVariable("approveResult");
        String employeeCode = (String) delegateExecution.getVariable("startUserName");
        //String conFloatingRateReqPrams = (String) delegateExecution.getVariable("conFloatingRateReq");
        //HlsCusConFloatingRateReq conFloatingRateReq = JSON.parseObject(conFloatingRateReqPrams, HlsCusConFloatingRateReq.class);
        HlsCusConFloatingRateReq conFloatingRateReq = new HlsCusConFloatingRateReq();
        Long fltReqId = (Long) delegateExecution.getVariable("BUSINESS_KEY");
        conFloatingRateReq.setFltReqId(fltReqId);
        conFloatingRateReq =  hlsCusConFloatingRateReqService.selectByPrimaryKey(requestCtx , conFloatingRateReq);

        HlsCusConFloatingRateReq resultConFloatingRateReq = new HlsCusConFloatingRateReq();
        //根据状态修改合同信息
        resultConFloatingRateReq.setFltReqId(conFloatingRateReq.getFltReqId());
        resultConFloatingRateReq = hlsCusConFloatingRateReqService.selectByPrimaryKey(requestCtx, resultConFloatingRateReq);
        databaseLockProvider.lock(resultConFloatingRateReq);

        if ("APPROVED".equalsIgnoreCase(result)) {
            flag = "APPROVED";
        } else if ("CANCEL".equalsIgnoreCase(result)) {
            flag = "CANCEL";
        } else if ("APPROVED_RETURN".equalsIgnoreCase(result)) {
            flag = "APPROVED_RETURN";
        }else if ("REJECTED".equalsIgnoreCase(result)) {
            flag = "REJECTED";
            //拒绝单据需要更新合同状态为起租
            HlsCusConFloatingRateReqLn hlsCusConFloatingRateReqLn = new HlsCusConFloatingRateReqLn();
            hlsCusConFloatingRateReqLn.setFltReqId(conFloatingRateReq.getFltReqId());
            List<HlsCusConFloatingRateReqLn> cusConFloatingRateReqLnList =  hlsCusConFloatingRateReqLnMapper.queryFloatByReqId(hlsCusConFloatingRateReqLn);
            for(HlsCusConFloatingRateReqLn floatingRateReqLn :  cusConFloatingRateReqLnList ){
                HlsCusConContract hlsCusConContract = new HlsCusConContract();
                hlsCusConContract.setContractId(floatingRateReqLn.getContractId());
                hlsCusConContract.setContractStatus("INCEPT");
                hlsCusConContractMapper.updateByPrimaryKeySelective(hlsCusConContract);
            }
        }

        //钢制多次循环 添加状态为approving校验
        if ("APPROVED".equalsIgnoreCase(result) && "APPROVING".equalsIgnoreCase(resultConFloatingRateReq.getStatus())) {
            //hlsCusConFloatingRateReqLnService.confirmFltReq(requestCtx, resultConFloatingRateReq);
            updateCashflowByQuotation(requestCtx , conFloatingRateReq);

            //出调息凭证
            HlsCusConFloatingRateReqLn hlsCusConFloatingRateReqLn = new HlsCusConFloatingRateReqLn();
            hlsCusConFloatingRateReqLn.setFltReqId(conFloatingRateReq.getFltReqId());
            List<HlsCusConFloatingRateReqLn> cusConFloatingRateReqLnList =  hlsCusConFloatingRateReqLnMapper.queryFloatByReqId(hlsCusConFloatingRateReqLn);
            for(HlsCusConFloatingRateReqLn floatingRateReqLn :  cusConFloatingRateReqLnList ){

                //更新lpr_link_date 和 next_adjustment_date ;
                if(floatingRateReqLn.getContractId() != null){
                    HlsCusConContract hlsCusConContract = hlsCusConContractMapper.selectByPrimaryKey(floatingRateReqLn.getContractId());

                    HlsCusPrjQuotation cusPrjQuotation = new HlsCusPrjQuotation();
                    cusPrjQuotation.setQuotationId(floatingRateReqLn.getQuotationId());
                    HlsCusPrjQuotation prjQuotationOld =  hlsCusPrjQuotationMapper.selectByPrimaryKey(cusPrjQuotation);

                    HlsCusPrjQuotation cusPrjQuotationNew = new HlsCusPrjQuotation();
                    cusPrjQuotationNew.setQuotationId(floatingRateReqLn.getQuotationIdNew());
                    HlsCusPrjQuotation prjQuotationNew =  hlsCusPrjQuotationMapper.selectByPrimaryKey(cusPrjQuotationNew);


                    //旧的lpr_link_date + 调息期限 = 新的 lpr_link_date (即lpr挂钩日期)
//                    Calendar calendar = Calendar.getInstance();
//                    calendar.setTime(prjQuotationOld.getLprLinkDate());
//                    calendar.add(Calendar.MONTH, Integer.parseInt(prjQuotationOld.getLprAdjustmentTerm()));
//                    Date LprLinkeDateNew  = calendar.getTime();
//                    hlsCusConContract.setLprLinkDate(LprLinkeDateNew);

                    if(floatingRateReqLn.getNewBaseRate() != null){
                        hlsCusConContract.setBaseRate(floatingRateReqLn.getNewBaseRate() );
                    }
                    if(floatingRateReqLn.getNewIntRate() != null){
                        hlsCusConContract.setIntRate(floatingRateReqLn.getNewIntRate());
                    }

                    HlsCusPrjProject prjProject = new HlsCusPrjProject();
                    prjProject.setProjectId(hlsCusConContract.getProjectId());
                    prjProject = projectService.selectByPrimaryKey(requestCtx,prjProject);
                    if(NEXT_YDAY.equalsIgnoreCase(prjProject.getFloatingRangeMethod())){
                        //下次调息日期 = 本次调息日 + 1年
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(hlsCusConContract.getNextAdjustmentDate());
                        cal.add(Calendar.YEAR,1);
                        Date nextAdjustmentDate = cal.getTime();
                        hlsCusConContract.setNextAdjustmentDate(nextAdjustmentDate);
//                        prjQuotationOld.setLprLinkDate(LprLinkeDateNew);
                        prjQuotationOld.setNextAdjustmentDate(nextAdjustmentDate);
                        hlsCusPrjQuotationService.updateByPrimaryKeySelective(requestCtx , prjQuotationOld);
                    }
                    //更新支付表状态未起租
                    hlsCusConContract.setContractStatus("INCEPT");
                    hlsCusConContract.setQuotationId(floatingRateReqLn.getQuotationIdNew());
                    hlsCusConContractMapper.updateByPrimaryKeySelective(hlsCusConContract);

                    //摊销list
                    conContractList.add(hlsCusConContract);

                }
            }
            //摊销
            try {
                for(HlsCusConContract cusConContract : conContractList){
                    List<HlsCusConContract> conContractLists = hlsCusConContractMapper.conGetContract(cusConContract);
                    for(HlsCusConContract ct : conContractLists ){
                        gldContractCashflowService.clacFinanceIncome(requestCtx,ct.getContractId(),ct.getVatRate(),ct.getXirr());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }
        resultConFloatingRateReq.setStatus(flag);
        hlsCusConFloatingRateReqService.updateByPrimaryKeySelective(requestCtx, resultConFloatingRateReq);
    }

    public void updateCashflowByQuotation(IRequest requestCtx , HlsCusConFloatingRateReq conFloatingRateReq){

        HlsCusConFloatingRateReqLn hlsCusConFloatingRateReqLn = new HlsCusConFloatingRateReqLn();
        hlsCusConFloatingRateReqLn.setFltReqId(conFloatingRateReq.getFltReqId());
        List<HlsCusConFloatingRateReqLn> hlsCusConFloatingRateReqLnList =  hlsCusConFloatingRateReqLnMapper.queryFloatByReqId(hlsCusConFloatingRateReqLn);

        //遍历调息行表 ，更新原来的con_contract_cashflow
        for(HlsCusConFloatingRateReqLn cusConFloatingRateReqLn  : hlsCusConFloatingRateReqLnList){

            Long quotationIdOld = cusConFloatingRateReqLn.getQuotationId();
            Long quotationIdNew = cusConFloatingRateReqLn.getQuotationIdNew();
            Long contractId = cusConFloatingRateReqLn.getContractId();

            //更新原来的con_contract_cashflow  - 现有的prj_quotation_cashflow 找到原来的con_contract_cashflow  , update
            HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
            HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = new HlsCusPrjQuotationCashflow();
            hlsCusPrjQuotation.setQuotationId(cusConFloatingRateReqLn.getQuotationIdNew());

            hlsCusPrjQuotationCashflow.setQuotationId(quotationIdNew);
            List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationCashflowList =  hlsCusPrjQuotationCashflowMapper.queryCashByQuotation(hlsCusPrjQuotationCashflow);
            for(HlsCusPrjQuotationCashflow cusPrjQuotationCashflow : hlsCusPrjQuotationCashflowList){

                HlsCusConContractCashflow contractCashflow = new HlsCusConContractCashflow();
                contractCashflow.setContractId(contractId);
//                contractCashflow.setQuotationId(quotationIdOld);
                contractCashflow.setDueDate(cusPrjQuotationCashflow.getDueDate());
                contractCashflow.setTimes(cusPrjQuotationCashflow.getTimes());
                contractCashflow.setCfType(cusPrjQuotationCashflow.getCfType());
                contractCashflow.setCfItem(cusPrjQuotationCashflow.getCfItem());

                //根据  due_date ,contract_id , quotation_id_old , cf_item ,cf_type ,times ,找到原来的进行update
                List<HlsCusConContractCashflow> contractCashflows =  conContractCashflowMapper.queryOldCashflowByQuotation(contractCashflow);
                if(contractCashflows.size() > 0){
                    for(HlsCusConContractCashflow cash : contractCashflows ){

                        BeanRefUtils.beanToBean(cusPrjQuotationCashflow, contractCashflow, hlsBeanRefUtilService);
                        contractCashflow.setQuotationId(cash.getQuotationId());
                        contractCashflow.setCashflowId(cash.getCashflowId());
                        contractCashflow.setGeneratedSourceDocId(cusPrjQuotationCashflow.getQuotationId());
                        contractCashflow.setGeneratedSourceDocLineId(cusPrjQuotationCashflow.getQuotationCashflowId());
                        contractCashflow.set__status("update");
                    }
                }
                conContractCashflowService.updateByPrimaryKeySelective(requestCtx, contractCashflow);
            }

        }


    }

}
