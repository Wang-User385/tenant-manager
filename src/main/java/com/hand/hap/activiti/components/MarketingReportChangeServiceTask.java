package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.hls.dto.HlsCusHlsMarketingReport;
import com.hand.hls.hls.dto.HlsCusHlsMarketingReportBp;
import com.hand.hls.hls.dto.HlsCusHlsReportAttachment;
import com.hand.hls.hls.dto.MarketingReportChange;
import com.hand.hls.hls.mapper.HlsCusHlsMarketingReportBpMapper;
import com.hand.hls.hls.mapper.HlsCusHlsReportAttachmentMapper;
import com.hand.hls.hls.service.HlsCusHlsMarketingReportBpService;
import com.hand.hls.hls.service.HlsCusHlsMarketingReportService;
import com.hand.hls.hls.service.HlsCusHlsReportAttachmentService;
import com.hand.hls.hls.service.IMarketingReportChangeService;
import com.hand.hls.wfl.service.IActivitiStartService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2019/7/2
 * @description: 融资合同变更结束方法
 */
@Component
public class MarketingReportChangeServiceTask implements JavaDelegate, IActivitiBean {


    @Autowired
    private IMarketingReportChangeService marketingReportChangeService;

    @Autowired
    private HlsCusHlsMarketingReportService hlsCusHlsMarketingReportService;
    @Autowired
    private HlsCusHlsMarketingReportBpMapper hlsCusHlsMarketingReportBpMapper;
    @Autowired
    private HlsCusHlsMarketingReportBpService hlsCusHlsMarketingReportBpService;

    @Autowired
    private HlsCusHlsReportAttachmentMapper hlsCusHlsReportAttachmentMapper;

    @Autowired
    private HlsCusHlsReportAttachmentService hlsCusHlsReportAttachmentService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(DelegateExecution delegateExecution) {
        String flag;
        String conFlag;
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String marketingReportChange = (String) delegateExecution.getVariable("marketingReportChange");
        MarketingReportChange changeReq = JSON.parseObject(marketingReportChange, MarketingReportChange.class);

        if ("APPROVED".equalsIgnoreCase(result)) {
            flag = "APPROVED";
            conFlag = "APPROVED";
            try {
                marketingReportChangeService.leaveHistory(requestCtx, changeReq, Long.parseLong(delegateExecution.getProcessInstanceId()));

                HlsCusHlsMarketingReportBp hlsCusHlsMarketingReportBp = new HlsCusHlsMarketingReportBp();
                hlsCusHlsMarketingReportBp.setMarketingReportId(changeReq.getMarketingReportId());
                List<HlsCusHlsMarketingReportBp> hlsCusHlsMarketingReportBps = hlsCusHlsMarketingReportBpMapper.select(hlsCusHlsMarketingReportBp);
                for (int i = 0; i < hlsCusHlsMarketingReportBps.size(); i++) {
                    HlsCusHlsMarketingReportBp marketingReportBp = new HlsCusHlsMarketingReportBp();
                    marketingReportBp.setMarketingReportBpId(hlsCusHlsMarketingReportBps.get(i).getMarketingReportBpId());
                    marketingReportBp.setStatus("APPROVED");
                    hlsCusHlsMarketingReportBpService.updateByPrimaryKeySelective(requestCtx,marketingReportBp);

                }
                HlsCusHlsReportAttachment hlsCusHlsReportAttachment = new HlsCusHlsReportAttachment();
                hlsCusHlsReportAttachment.setMarketingReportId(changeReq.getMarketingReportId());
                List<HlsCusHlsReportAttachment> hlsCusHlsReportAttachments = hlsCusHlsReportAttachmentMapper.select(hlsCusHlsReportAttachment);
                for (int i = 0; i < hlsCusHlsReportAttachments.size(); i++) {
                    HlsCusHlsReportAttachment reportAttachment = new HlsCusHlsReportAttachment();
                    reportAttachment.setMarketingAttachmentId(hlsCusHlsReportAttachments.get(i).getMarketingAttachmentId());
                    reportAttachment.setStatus("APPROVED");
                    hlsCusHlsReportAttachmentService.updateByPrimaryKeySelective(requestCtx,reportAttachment);

                }
            } catch (Exception e) {
                logger.error("leave history error:", e);
            }
        } else if ("CANCEL".equalsIgnoreCase(result)) {
            flag = "CANCEL";
            conFlag = "APPROVED";
        } else {
            flag = "REJECTED";
            conFlag = "PENDING";
        }

        MarketingReportChange mrc = new MarketingReportChange();
        mrc.setChangeReqId(changeReq.getChangeReqId());
        mrc.setProcessInstanceId(Long.parseLong(delegateExecution.getProcessInstanceId()));
        mrc.setStatus(flag);
        marketingReportChangeService.updateByPrimaryKeySelective(requestCtx, mrc);

        HlsCusHlsMarketingReport hlsCusHlsMarketingReport = new HlsCusHlsMarketingReport();
        hlsCusHlsMarketingReport.setMarketingReportId(changeReq.getMarketingReportId());

        hlsCusHlsMarketingReport.setStatus(conFlag);
        hlsCusHlsMarketingReportService.updateByPrimaryKeySelective(requestCtx,hlsCusHlsMarketingReport);
    }

}
