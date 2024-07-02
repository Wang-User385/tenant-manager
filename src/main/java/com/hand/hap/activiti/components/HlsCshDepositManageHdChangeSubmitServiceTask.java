package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.activiti.components.IHlsCusActivitiBean;
import com.hand.hls.activiti.dto.HlsCusProcess;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.csh.dto.DepositManageHd;
import com.hand.hls.csh.mapper.DepositManageHdMapper;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceBpService;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.prj.mapper.HlsCusPrjProjectBpMapper;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by junL on 2022年8月6日.
 * 保证金处理方式变更审批流程
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCshDepositManageHdChangeSubmitServiceTask implements JavaDelegate, IActivitiBean,IHlsCusActivitiBean {

    @Autowired
    HlsCusHlsCreditLineChanceBpService hlsCusHlsCreditLineChanceBpService;
    @Autowired
    HlsCusPrjProjectBpMapper hlsCusPrjProjectBpMapper;
    @Autowired
    private DepositManageHdMapper depositManageHdMapper;
    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;
    @Autowired
    private HlsCusConContractService hlsCusConContractService;
    @Autowired
    private HlsCusConContractCashflowService hlsCusConContractCashflowService;
    @Autowired
    private HlsCusConContractCashflowMapper hlsCusConContractCashflowMapper;

    private final static String PASS = "PASS";
    private final static String REJECT = "REJECT";

    private Logger logger = LoggerFactory.getLogger(getClass());

    public HlsCshDepositManageHdChangeSubmitServiceTask() {
    }


    /**
     * 结束监听
     * @param delegateExecution
     */
    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");

        //解决跳转时结束监听器多次被调用问题
        if(((ExecutionEntityImpl) delegateExecution).getStartTime()==null){
            return;
        }

        Long manageHdId = Long.valueOf(delegateExecution.getProcessInstanceBusinessKey());
        DepositManageHd depositManage = depositManageHdMapper.selectByPrimaryKey(manageHdId);
        //设置审批中的状态(执行结果(APPROVED_RETURN-退回,NEW-新建，APPROVING-审批中，APPROVED-审批通过，WRITE_OFF-已核销，CANCEL-取消，REJECTED-拒绝))
        /*if("APPROVED".equals(result)){
            depositManage.setExecutionResult("APPROVED");
            depositManageHdMapper.updateByPrimaryKey(depositManage);
        }else if("REJECTED".equals(result)){
            depositManage.setExecutionResult("REJECTED");
            depositManageHdMapper.updateByPrimaryKey(depositManage);
        }*/
        //业务数据处理
        if("APPROVED".equals(result) || "REJECTED".equals(result) || "APPROVED_RETURN".equals(result)) {
            updateCon(requestCtx, result, depositManage);
        }
    }


    //合同现金流备份
    private void chanceBackUp(IRequest iRequest, HlsCusConContract hlsCusConContract){
        HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
        hlsCusConContractCashflow.setContractId((Long) hlsCusConContract.getRefContractId());
        List<HlsCusConContractCashflow> cashflowList = hlsCusConContractCashflowService.queryContractRepCashflowByContractId(hlsCusConContractCashflow, 1, 99999);
        logger.debug("现金流条数[{}]",cashflowList.size());
        for (HlsCusConContractCashflow cashflow : cashflowList) {
            HlsCusConContractCashflow hlsCashflow = cashflow;
            hlsCashflow.setContractId(hlsCusConContract.getContractId());
            hlsCusConContractCashflowService.insertSelective(iRequest, hlsCashflow);
        }

    }

    private void updateCon(IRequest request, String result,DepositManageHd depositManage){

        if("APPROVED".equals(result)){

            //step1-1:备份history类型的合同和现金流
            HlsCusConContract hlsCusConContractNew = new HlsCusConContract();
            hlsCusConContractNew.setContractId(depositManage.getContractId());
            hlsCusConContractNew = hlsCusConContractMapper.selectByPrimaryKey(hlsCusConContractNew);
            hlsCusConContractNew.setRefContractId(depositManage.getContractId());
            hlsCusConContractNew.setContractId(null);
            hlsCusConContractNew.setDataClass("HISTORY");
            hlsCusConContractNew = hlsCusConContractService.insertSelective(request, hlsCusConContractNew);
            logger.debug("NORMAL旧合同id[{}]",hlsCusConContractNew.getRefContractId());
            logger.debug("HISTORY新合同id[{}]",hlsCusConContractNew.getContractId());
            //step1-2:合同现金流备份
            chanceBackUp(request,hlsCusConContractNew);
            logger.info("保证金处理方式变更审批流程end备份HISTORY合同成功[{}{}]",hlsCusConContractNew.getContractId(),hlsCusConContractNew.getContractNumber());

            //step2:更新现金流 只是更新退还类型的现金流即可
            //step2-1:查询NORMAL现金流(52保证金退还类型的现金流)
            HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
            hlsCusConContractCashflow.setContractId((Long) depositManage.getContractId());
            hlsCusConContractCashflow.setSubsectionStartTime(0L);
            hlsCusConContractCashflow.setSubsectionEndTime(9999L);
            hlsCusConContractCashflow.setCfItem(52L);//保证金退还
            List<HlsCusConContractCashflow> cashflowListNormal52 = hlsCusConContractCashflowMapper.querySubsectionCashflow(hlsCusConContractCashflow);
            logger.info("保证金处理方式变更审批流程end-NORMAL保证金退还类型现金流条数[{}]",cashflowListNormal52.size());

            //step2-2:查询CHANGE_REQ现金流(52保证金退还类型的现金流)
            hlsCusConContractCashflow.setContractId((Long) depositManage.getChangeContractId());
            List<HlsCusConContractCashflow> cashflowListChangeReq52 = hlsCusConContractCashflowMapper.querySubsectionCashflow(hlsCusConContractCashflow);
            logger.info("保证金处理方式变更审批流程end-CHANGE_REQ保证金退还类型现金流条数[{}]",cashflowListChangeReq52.size());

            logger.info("保证金处理方式变更审批流程end[step4-1]将NORMAL下的52删除");
            //step3-1:将NORMAL下的52删除;
            for (HlsCusConContractCashflow cashflow1 : cashflowListNormal52) {
                hlsCusConContractCashflowService.deleteByPrimaryKey(cashflow1);
            }

            logger.info("保证金处理方式变更审批流程end[step4-2]将CHANGE_REQ的退还类型现金流数据插入到NORMAL的现金流中");
            //step3-2:将CHANGE_REQ的退还类型现金流数据插入到NORMAL的现金流中
            for (HlsCusConContractCashflow cashflow2 : cashflowListChangeReq52) {
                cashflow2.setCashflowId(null);
                cashflow2.setContractId(depositManage.getContractId());
                hlsCusConContractCashflowService.insertSelective(request, cashflow2);
            }

            //step4:查询NORMAL合同
            HlsCusConContract hlsCusConContractNormal = new HlsCusConContract();
            hlsCusConContractNormal.setContractId(depositManage.getContractId());
            hlsCusConContractNormal = hlsCusConContractMapper.selectByPrimaryKey(hlsCusConContractNormal);
            //step4-1:还原NORMAL合同状态为起租，跟新保证金变更方式
            hlsCusConContractNormal.setDepositDeduction(depositManage.getAfterChangeWay());
            //step5:还原NORMAL合同状态为起租并更新
            if("PENDING".equals(hlsCusConContractNormal.getContractStatus())) {
                hlsCusConContractNormal.setContractStatus("INCEPT");//暂挂NORMAL合同
            }
            hlsCusConContractNormal.setDepositDeductionChangedFlag("Y");
            hlsCusConContractMapper.updateByPrimaryKey(hlsCusConContractNormal);
            logger.info("保证金处理方式变更审批流程end[step4-2]现金流变更结束");
        }else{
            //step4:查询NORMAL合同
            HlsCusConContract hlsCusConContractNormal = new HlsCusConContract();
            hlsCusConContractNormal.setContractId(depositManage.getContractId());
            hlsCusConContractNormal = hlsCusConContractMapper.selectByPrimaryKey(hlsCusConContractNormal);
            //step4-1:还原NORMAL合同状态为起租，跟新保证金变更方式
            //非审批通过不更新保证金处理方式 hlsCusConContractNormal.setDepositDeduction(depositManage.getAfterChangeWay());
            //step5:还原NORMAL合同状态为起租并更新
            if("PENDING".equals(hlsCusConContractNormal.getContractStatus())) {
                hlsCusConContractNormal.setContractStatus("INCEPT");//暂挂NORMAL合同
            }
            hlsCusConContractNormal.setDepositDeductionChangedFlag("N");
            hlsCusConContractMapper.updateByPrimaryKey(hlsCusConContractNormal);
        }

        //step6:更新保证金管理数据
        depositManage.setExecutionResult(result);
        depositManageHdMapper.updateByPrimaryKey(depositManage);
    }


    /**
     * 一键通过/一键拒绝
     * @param iRequest
     * @param hlsCusProcess
     */
    @Override
    public void excute(IRequest iRequest, HlsCusProcess hlsCusProcess) {
        Long bussinessKey = Long.valueOf(hlsCusProcess.getBussinessKey());
        Long processInstanceId = Long.parseLong(hlsCusProcess.getProcessMap().get(0).get("proc_inst_id_").toString());
        DepositManageHd depositManage = depositManageHdMapper.selectByPrimaryKey(bussinessKey);
        String result = "";
        //设置审批中的状态(执行结果(NEW-新建，APPROVING-审批中，APPROVED-审批通过，WRITE_OFF-已核销，CANCEL-取消，REJECTED-拒绝))
        if (PASS.equals(hlsCusProcess.getType())) {
            result = "APPROVED";
            //业务数据处理
            updateCon(iRequest, result,depositManage);
        } else if (REJECT.equals(hlsCusProcess.getType())) {
            result = "REJECTED";
            //业务数据处理
            updateCon(iRequest, result,depositManage);
        }else{
            result = hlsCusProcess.getType();
        }
    }
}
