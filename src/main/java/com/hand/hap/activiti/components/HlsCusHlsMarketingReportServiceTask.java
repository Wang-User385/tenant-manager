package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.hls.dto.HlsCusHlsMarketingReport;
import com.hand.hls.hls.dto.HlsCusHlsMarketingReportBp;
import com.hand.hls.hls.dto.HlsCusHlsReportAttachment;
import com.hand.hls.hls.mapper.HlsCusHlsMarketingReportBpMapper;
import com.hand.hls.hls.mapper.HlsCusHlsReportAttachmentMapper;
import com.hand.hls.hls.service.HlsCusHlsMarketingReportBpService;
import com.hand.hls.hls.service.HlsCusHlsMarketingReportService;
import com.hand.hls.hls.service.HlsCusHlsReportAttachmentService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class HlsCusHlsMarketingReportServiceTask implements JavaDelegate, IActivitiBean {

    public static final String APPROVED = "APPROVED";
    public static final String REJECTED = "REJECTED";
    @Autowired
    HlsCusHlsMarketingReportService hlsMarketingReportService;
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
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String financePurchase = (String) delegateExecution.getVariable("hlsCusInvFinancePurchase");
        HlsCusHlsMarketingReport hlsCusHlsMarketingReport = JSON.parseObject(financePurchase, HlsCusHlsMarketingReport.class);

        HlsCusHlsMarketingReport hmr = new HlsCusHlsMarketingReport();
        hmr.setMarketingReportId(hlsCusHlsMarketingReport.getMarketingReportId());
        if (APPROVED.equalsIgnoreCase(result)) {
            flag = APPROVED;
            hmr.setApprovedDate(new Date());
            HlsCusHlsMarketingReportBp hlsCusHlsMarketingReportBp = new HlsCusHlsMarketingReportBp();
            hlsCusHlsMarketingReportBp.setMarketingReportId(hlsCusHlsMarketingReport.getMarketingReportId());
            List<HlsCusHlsMarketingReportBp> hlsCusHlsMarketingReportBps = hlsCusHlsMarketingReportBpMapper.select(hlsCusHlsMarketingReportBp);
            for (int i = 0; i < hlsCusHlsMarketingReportBps.size(); i++) {
                HlsCusHlsMarketingReportBp marketingReportBp = new HlsCusHlsMarketingReportBp();
                marketingReportBp.setMarketingReportBpId(hlsCusHlsMarketingReportBps.get(i).getMarketingReportBpId());
                marketingReportBp.setStatus("APPROVED");
                hlsCusHlsMarketingReportBpService.updateByPrimaryKeySelective(requestCtx,marketingReportBp);

            }
            HlsCusHlsReportAttachment hlsCusHlsReportAttachment = new HlsCusHlsReportAttachment();
            hlsCusHlsReportAttachment.setMarketingReportId(hlsCusHlsMarketingReport.getMarketingReportId());
            List<HlsCusHlsReportAttachment> hlsCusHlsReportAttachments = hlsCusHlsReportAttachmentMapper.select(hlsCusHlsReportAttachment);
            for (int i = 0; i < hlsCusHlsReportAttachments.size(); i++) {
                HlsCusHlsReportAttachment reportAttachment = new HlsCusHlsReportAttachment();
                reportAttachment.setMarketingAttachmentId(hlsCusHlsReportAttachments.get(i).getMarketingAttachmentId());
                reportAttachment.setStatus("APPROVED");
                hlsCusHlsReportAttachmentService.updateByPrimaryKeySelective(requestCtx,reportAttachment);

            }
        } else {
            flag = REJECTED;
        }

        hmr.setStatus(flag);
        hlsMarketingReportService.updateByPrimaryKeySelective(requestCtx, hmr);
    }
}
