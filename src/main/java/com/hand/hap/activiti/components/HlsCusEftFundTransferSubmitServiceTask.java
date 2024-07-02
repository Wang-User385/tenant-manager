package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqHdMapper;
import com.hand.hls.csh.service.CshPaymentReqHdService;
import com.hand.hls.eft.dto.HlsCusFundTransfer;
import com.hand.hls.eft.service.HlsCusFundTransferService;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.hls.dto.HlsCusFundingPlanLn;
import com.hand.hls.hls.mapper.HlsCusFundingPlanLnMapper;
import com.hand.hls.utils.HlsCusConstant;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * description
 *
 * @author yuanyuan 2019/07/25 5:16 PM
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusEftFundTransferSubmitServiceTask implements JavaDelegate, IActivitiBean {

    @Autowired
    private HlsCusFundTransferService fundTransferService;
    @Autowired
    private CshPaymentReqHdService cshPaymentReqHdService;

    @Autowired
    private HlsCusConContractService hlsCusConContractService;

    @Autowired
    private HlsCusCshPaymentReqHdMapper hlsCusCshPaymentReqHdMapper;

    @Autowired
    private HlsCusFundingPlanLnMapper hlsCusFundingPlanLnMapper;

    @Autowired
    private HlsCusConContractCashflowService hlsCusConContractCashflowService;
    @Override
    public void execute(DelegateExecution delegateExecution) {
        String flag;
        IRequest requestCtx =  RequestHelper.getCurrentRequest(true);
        requestCtx.setAttribute("wflRuleControlFlag", "Y");

        String result = (String) delegateExecution.getVariable("approveResult");
        String hlsCusCshPaymentReqHd = (String) delegateExecution.getVariable("hlsCusCshPaymentReqHd");
        HlsCusCshPaymentReqHd CshPaymentReqHd = JSON.parseObject(hlsCusCshPaymentReqHd, HlsCusCshPaymentReqHd.class);
        Long processInstanceId = Long.parseLong(delegateExecution.getProcessInstanceId());

        HlsCusCshPaymentReqHd hd = new HlsCusCshPaymentReqHd();
        hd.setPaymentReqId(CshPaymentReqHd.getPaymentReqId());
        if (HlsCusConstant.WORKFLOW_STATUS.APPROVED.equalsIgnoreCase(result)) {
            flag = HlsCusConstant.WORKFLOW_STATUS.APPROVED;

        } else {
            flag = HlsCusConstant.WORKFLOW_STATUS.REJECTED;
        }
        hd.setEftProcessInstanceId(processInstanceId);
        hd.setTransferStatus(flag);
        cshPaymentReqHdService.updateByPrimaryKeySelective(requestCtx, hd);

        HlsCusFundingPlanLn hlsCusFundingPlanLn = new HlsCusFundingPlanLn();
        hlsCusFundingPlanLn.setFundingPlanId(CshPaymentReqHd.getFundingPlanId());
        List<HlsCusFundingPlanLn> hlsCusFundingPlanLns = hlsCusFundingPlanLnMapper.selectPaymentPlanInfo(hlsCusFundingPlanLn);

        for (int i = 0; i < hlsCusFundingPlanLns.size(); i++) {
            HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
            conContractCashflow.setCashflowId(hlsCusFundingPlanLns.get(i).getPlanId());
            if (HlsCusConstant.WORKFLOW_STATUS.APPROVED.equalsIgnoreCase(result)) {
                conContractCashflow.setProcessStatus("FUND_APPROVED");
            }else{
                conContractCashflow.setProcessStatus("FUND_APPROVING");
            }

            hlsCusConContractCashflowService.updateByPrimaryKeySelective(requestCtx, conContractCashflow);
        }

        //IRequest request = RequestHelper.getCurrentRequest(true);
        //request.setAttribute("wflRuleControlFlag", "Y");
        //更新支付表状态
        List<HlsCusCshPaymentReqHd> cusCshPaymentReqHdList =   hlsCusCshPaymentReqHdMapper.queryContractIdByFundPlan(hd);

        //更新支付表状态 - 资金调拨状态
        if( HlsCusConstant.WORKFLOW_STATUS.APPROVED.equalsIgnoreCase(flag)) {

            if(cusCshPaymentReqHdList.size() == 1){
                if(cusCshPaymentReqHdList.get(0).getContractId() != null ){
                    HlsCusConContract hlsCusConContract = new HlsCusConContract();
                    hlsCusConContract.setContractId(cusCshPaymentReqHdList.get(0).getContractId() );
                    HlsCusConContract hlsCusConContractNew = hlsCusConContractService.selectByPrimaryKey(requestCtx , hlsCusConContract);

                    if(hlsCusConContractNew.getInceptFlag() == null ){
                        hlsCusConContractNew.setContractStatus("FUNDED");
                    }else if(!"Y".equalsIgnoreCase(hlsCusConContractNew.getInceptFlag())) {
                        hlsCusConContractNew.setContractStatus("FUNDED");
                    }

                    hlsCusConContractService.updateByPrimaryKeySelective(requestCtx , hlsCusConContractNew );
                }
            }
        }else if(HlsCusConstant.WORKFLOW_STATUS.REJECTED.equalsIgnoreCase(flag)){
            if(cusCshPaymentReqHdList.size() == 1){
                if(cusCshPaymentReqHdList.get(0).getContractId() != null ){
                    HlsCusConContract hlsCusConContract = new HlsCusConContract();
                    hlsCusConContract.setContractId(cusCshPaymentReqHdList.get(0).getContractId() );
                    HlsCusConContract hlsCusConContractNew = hlsCusConContractService.selectByPrimaryKey(requestCtx , hlsCusConContract);
                    if(!"Y".equalsIgnoreCase(hlsCusConContractNew.getInceptFlag())) {
                        hlsCusConContractNew.setContractStatus("UNFUNDED");
                    }
                    hlsCusConContractService.updateByPrimaryKeySelective(requestCtx , hlsCusConContractNew );
                }
            }

        }

    }

}
