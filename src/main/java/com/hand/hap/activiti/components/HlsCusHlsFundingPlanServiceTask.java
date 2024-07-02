package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqDt;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqDtMapper;
import com.hand.hls.eas.dto.HlsCusEasBasicData;
import com.hand.hls.eas.dto.HlsCusEasLogin;
import com.hand.hls.eas.dto.HlsCusEasSourceRecord;
import com.hand.hls.eas.mapper.HlsCusEasSourceRecordMapper;
import com.hand.hls.eas.service.IHlsCusEasLoginService;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.hls.dto.*;
import com.hand.hls.hls.mapper.HlsCusFundingPlanLnMapper;
import com.hand.hls.hls.mapper.HlsCusHlsMarketingReportBpMapper;
import com.hand.hls.hls.mapper.HlsCusHlsReportAttachmentMapper;
import com.hand.hls.hls.service.HlsCusHlsMarketingReportBpService;
import com.hand.hls.hls.service.HlsCusHlsMarketingReportService;
import com.hand.hls.hls.service.HlsCusHlsReportAttachmentService;
import com.hand.hls.hls.service.IFundingPlanService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author : qzk
 * @date : 2020/4/11
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusHlsFundingPlanServiceTask implements JavaDelegate, IActivitiBean {

    public static final String APPROVED = "APPROVED";
    public static final String REJECTED = "REJECTED";
    @Autowired
    HlsCusHlsMarketingReportService hlsMarketingReportService;
    @Autowired
    private IFundingPlanService iFundingPlanService;
    @Autowired
    HlsCusFundingPlanLnMapper hlsCusFundingPlanLnMapper;
    @Autowired
    HlsCusConContractCashflowService hlsCusConContractCashflowService;
    @Autowired
    HlsCusCshPaymentReqDtMapper hlsCusCshPaymentReqDtMapper;

    @Autowired
    private EasBasicSycnExecutor easBasicSycnExecutor;

    @Autowired
    private HlsCusConContractService hlsCusConContractService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(DelegateExecution delegateExecution) {
        String flag;
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String hlsCusFundingPlan = (String) delegateExecution.getVariable("hlsCusFundingPlan");
        HlsCusFundingPlan fundingPlan = JSON.parseObject(hlsCusFundingPlan, HlsCusFundingPlan.class);

        HlsCusFundingPlan fp = new HlsCusFundingPlan();
        fp.setFundingPlanId(fundingPlan.getFundingPlanId());
        if (APPROVED.equalsIgnoreCase(result)) {
            flag = APPROVED;

            //金蝶基础资料同步接口
            try {
                easBasicSycnExecutor.fun(requestCtx,fundingPlan.getContractId());
            }catch(Exception e) {
                e.printStackTrace();
            }


        } else {
            flag = REJECTED;
        }

        fp.setStatus(flag);
        iFundingPlanService.updateByPrimaryKeySelective(requestCtx, fp);

        HlsCusFundingPlanLn hlsCusFundingPlanLn = new HlsCusFundingPlanLn();
        hlsCusFundingPlanLn.setFundingPlanId(fundingPlan.getFundingPlanId());
        List<HlsCusFundingPlanLn> fundingPlanLns = hlsCusFundingPlanLnMapper.select(hlsCusFundingPlanLn);
        for (int i = 0; i < fundingPlanLns.size(); i++) {
            HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
            hlsCusConContractCashflow.setCashflowId(fundingPlanLns.get(i).getPlanId());
            if(APPROVED.equalsIgnoreCase(result)) {
                hlsCusConContractCashflow.setFundingPlanStatus("REPORTED");
                hlsCusConContractCashflow.setProcessStatus("REPORTED");
            }else{
                hlsCusConContractCashflow.setFundingPlanStatus("REPORTING");
                hlsCusConContractCashflow.setProcessStatus("REPORTING");
            }
            hlsCusConContractCashflowService.updateByPrimaryKeySelective(requestCtx,hlsCusConContractCashflow);


            HlsCusCshPaymentReqDt hlsCusCshPaymentReqDt = new HlsCusCshPaymentReqDt();
            hlsCusCshPaymentReqDt.setFundingPlanLnId(fundingPlanLns.get(i).getFundingPlanLnId());
            List<HlsCusCshPaymentReqDt> hlsCusCshPaymentReqDts =hlsCusCshPaymentReqDtMapper.select(hlsCusCshPaymentReqDt);
            for (int j = 0; j < hlsCusCshPaymentReqDts.size(); j++) {
                HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
                conContractCashflow.setCashflowId(hlsCusCshPaymentReqDts.get(j).getSourceDocLineId());
                if(APPROVED.equalsIgnoreCase(result)) {
                    conContractCashflow.setFundingPlanStatus("REPORTED");
                    conContractCashflow.setProcessStatus("REPORTED");
                }else{
                    conContractCashflow.setFundingPlanStatus("REPORTING");
                    conContractCashflow.setProcessStatus("REPORTING");
                }
                conContractCashflow.setWhetherDeduct("Y");
                hlsCusConContractCashflowService.updateByPrimaryKeySelective(requestCtx,conContractCashflow);
            }

        }

        //更改租金支付表状态-报备完成，报备否决
        HlsCusFundingPlan fundingPlanNew = iFundingPlanService.selectByPrimaryKey(requestCtx , fp);
        if(fundingPlanNew.getContractId() != null){
            HlsCusConContract hlsCusConContract = new HlsCusConContract();
            hlsCusConContract.setContractId(fundingPlanNew.getContractId());
            HlsCusConContract hlsCusConContractNew = hlsCusConContractService.selectByPrimaryKey(requestCtx , hlsCusConContract);
            if(APPROVED.equalsIgnoreCase(flag)){
                if(!"Y".equalsIgnoreCase(hlsCusConContractNew.getInceptFlag())) {
                    hlsCusConContractNew.setContractStatus("REPORTED");
                }
            }else if (REJECTED.equalsIgnoreCase(flag)){
                if(!"Y".equalsIgnoreCase(hlsCusConContractNew.getInceptFlag())) {
                    hlsCusConContractNew.setContractStatus("UNREPORTED");
                }
            }
            hlsCusConContractService.updateByPrimaryKeySelective(requestCtx , hlsCusConContractNew );

        }


    }

}
