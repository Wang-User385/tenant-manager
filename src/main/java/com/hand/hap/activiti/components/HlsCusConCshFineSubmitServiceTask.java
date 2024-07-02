package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.csh.dto.HlsCusConDebtExemptionReq;
import com.hand.hls.csh.dto.HlsCusConDebtExemptionReqCf;
import com.hand.hls.csh.service.IConDebtExemptionReqCfService;
import com.hand.hls.csh.service.IConDebtExemptionReqService;
import com.hand.hls.docx4J.component.handler.TableBlockBookmarkHandler;
import com.hand.hls.gld.service.HlsCusConContractService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by xuju on 2018/04/27.
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusConCshFineSubmitServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private HlsCusConContractService hlsCusConContractService;
    @Autowired
    private HlsCusConContractCashflowService hlsCusConContractCashflowService;
    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;

    @Autowired
    private IConDebtExemptionReqService iConDebtExemptionReqService;
    @Autowired
    private IConDebtExemptionReqCfService iConDebtExemptionReqCfService;


    private final Logger log = LoggerFactory.getLogger(HlsCusConCshFineSubmitServiceTask.class);

    public HlsCusConCshFineSubmitServiceTask(){}
    @Override
    public void execute(DelegateExecution delegateExecution) {

        log.info("HlsCusConCshFineSubmitServiceTask被调用,instanceId:{}", delegateExecution.getProcessInstanceId());

        //解决跳转时结束监听器多次被调用问题
        if(((ExecutionEntityImpl) delegateExecution).getStartTime()==null){
            return;
        }

        String flag = null;
        String changeReqId = delegateExecution.getProcessInstanceBusinessKey();
        IRequest requestCtx = (IRequest)delegateExecution.getVariable("iRequest");
        String result = (String)delegateExecution.getVariable("approveResult");
        String employeeCode = (String)delegateExecution.getVariable("startUserName");
        HlsCusConDebtExemptionReq resultConDebtExemptionReq = new HlsCusConDebtExemptionReq();
        //根据状态修改罚息信息
        resultConDebtExemptionReq.setChangeReqId(Long.parseLong(changeReqId));
        if(iConDebtExemptionReqService.selectByPrimaryKey(requestCtx,resultConDebtExemptionReq)==null){
            return;
        }
        resultConDebtExemptionReq = iConDebtExemptionReqService.selectByPrimaryKey(requestCtx,resultConDebtExemptionReq);

        //增加使用退回后重复调用结束监听处理
        if("Y".equals(resultConDebtExemptionReq.getInstanceEndFlag())){
            return;
        }

//        databaseLockProvider.lock(resultConDebtExemptionReq);
        HlsCusConContract hlsCusConContract = new HlsCusConContract();
        hlsCusConContract.setContractId(resultConDebtExemptionReq.getDocumentId());
        hlsCusConContract = hlsCusConContractService.selectByPrimaryKey(requestCtx,hlsCusConContract);
//        databaseLockProvider.lock(hlsCusConContract);
        if("APPROVED".equalsIgnoreCase(result)){
            flag = "APPROVED";
            HlsCusConContractCashflow hlsCusConContractCashflow=new HlsCusConContractCashflow();
            hlsCusConContractCashflow.setContractId(hlsCusConContract.getContractId());
            hlsCusConContractCashflow.setCfItem(9L);
            List<HlsCusConContractCashflow> hlsCusConContractCashflowList=hlsCusConContractCashflowService.select(requestCtx,hlsCusConContractCashflow,1,1000);
            HlsCusConDebtExemptionReqCf hlsCusConDebtExemptionReqCf = new HlsCusConDebtExemptionReqCf();
            hlsCusConDebtExemptionReqCf.setChangeReqId(Long.parseLong(changeReqId));
            IRequest request = RequestHelper.getCurrentRequest(true);
            request.setAttribute("wflRuleControlFlag", "Y");
            List<HlsCusConDebtExemptionReqCf> hlsCusConDebtExemptionReqCfList = iConDebtExemptionReqCfService.select(request,hlsCusConDebtExemptionReqCf,1,100);
//            List<Long> contractId = hlsCusConDebtExemptionReqCfList.stream().map(m -> m.getContractId()).collect(Collectors.toList());
            for(HlsCusConDebtExemptionReqCf dt:hlsCusConDebtExemptionReqCfList){
                HlsCusConContractCashflow cusConContractCashflow = new HlsCusConContractCashflow();
                if(dt.getCashflowId() != null){
                    cusConContractCashflow.setCashflowId(dt.getCashflowId());
                    cusConContractCashflow = hlsCusConContractCashflowService.selectByPrimaryKey(requestCtx,cusConContractCashflow);
                    cusConContractCashflow.setFineStatus("APPROVED");
                    if(dt.getExemptionAmount() >0 ){
                        cusConContractCashflow.setFineReduceAmount(cusConContractCashflow.getDueAmount() - dt.getExemptionAmount());
                        cusConContractCashflow.setDueAmount(cusConContractCashflow.getDueAmount() - dt.getExemptionAmount());
                        cusConContractCashflow.setReduceAmount(cusConContractCashflow.getReduceAmount() + dt.getExemptionAmount());
                        cusConContractCashflow = hlsCusConContractCashflowService.updateByPrimaryKeySelective(requestCtx,cusConContractCashflow);
                    }

                }
            }
//            for(Long cid : contractId){
//                HlsCusConContract hlsCusConContract2=new HlsCusConContract();
//                hlsCusConContract2=hlsCusConContractMapper.selectByPrimaryKey(cid);
//                hlsCusConContract2.setFineReduceStatus("APPROVED");
//                hlsCusConContractService.updateByPrimaryKeySelective(requestCtx,hlsCusConContract2);
//            }
            hlsCusConContract.setFineReduceStatus("APPROVED");
            hlsCusConContract=hlsCusConContractService.updateByPrimaryKeySelective(requestCtx,hlsCusConContract);
            resultConDebtExemptionReq.setReqStatus("APPROVED");
            resultConDebtExemptionReq.setInstanceEndFlag("Y");
            resultConDebtExemptionReq = iConDebtExemptionReqService.updateByPrimaryKeySelective(requestCtx,resultConDebtExemptionReq);

        }else {
            flag = "REJECTED";
//            HlsCusConDebtExemptionReqCf hlsCusConDebtExemptionReqCf = new HlsCusConDebtExemptionReqCf();
//            hlsCusConDebtExemptionReqCf.setChangeReqId(Long.parseLong(changeReqId));//           IRequest request = RequestHelper.getCurrentRequest(true);
//            request.setAttribute("wflRuleControlFlag", "Y");
//            List<HlsCusConDebtExemptionReqCf> hlsCusConDebtExemptionReqCfList = iConDebtExemptionReqCfService.select(request,hlsCusConDebtExemptionReqCf,1,100);
//            List<Long> contractId = hlsCusConDebtExemptionReqCfList.stream().map(m -> m.getContractId()).collect(Collectors.toList());
//            for(Long cid : contractId){
//                HlsCusConContract hlsCusConContract2=new HlsCusConContract();
//                hlsCusConContract2=hlsCusConContractMapper.selectByPrimaryKey(cid);
//                hlsCusConContract2.setFineReduceStatus("REJECTED");
//                hlsCusConContract2=hlsCusConContractService.updateByPrimaryKeySelective(requestCtx,hlsCusConContract2);
//                hlsCusConContract2=hlsCusConContractService.updateByPrimaryKeySelective(requestCtx,hlsCusConContract2);
//            }
            hlsCusConContract.setFineReduceStatus("REJECTED");
            hlsCusConContract=hlsCusConContractService.updateByPrimaryKeySelective(requestCtx,hlsCusConContract);
            resultConDebtExemptionReq.setReqStatus("REJECTED");
            resultConDebtExemptionReq = iConDebtExemptionReqService.updateByPrimaryKeySelective(requestCtx,resultConDebtExemptionReq);

        }

    }

}
