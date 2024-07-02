package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hls.hls.dto.HlsCusHlsMarketingReport;
import com.hand.hls.hls.mapper.HlsCusHlsMarketingReportMapper;
import com.hand.hls.hls.service.HlsCusHlsMarketingReportService;
import com.hand.hls.wfl.components.WflGetProcessInstanceComponents;
import com.hand.hls.wfl.service.IActivitiCommonService;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author : qzk
 * @date : 2020/4/11
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusHlsMarketingReportActivitiStartServiceImpl implements IActivitiCommonService {

    private static final String WORK_FLOW_TYPE = "PRJ_MARKETING_REPORT_WFL";
    @Autowired
    private IActivitiService activitiService;


    @Autowired
    WflGetProcessInstanceComponents wflGetProcessInstanceComponents;

    @Autowired
    HlsCusHlsMarketingReportService hlsMarketingReportService;
    @Autowired
    HlsCusHlsMarketingReportMapper hlsCusHlsMarketingReportMapper;

    @Override
    public String getWorkFlowType() {
        return WORK_FLOW_TYPE;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = wflGetProcessInstanceComponents.getProcessInstance(iRequest, params);
        activitiService.startProcess(iRequest, processInstanceCreateRequest);

    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        long id = Long.parseLong(businessKey);
        HlsCusHlsMarketingReport hlsCusHlsMarketingReport = new HlsCusHlsMarketingReport();
        hlsCusHlsMarketingReport.setMarketingReportId(id);
        hlsCusHlsMarketingReport.setStatus(CANCEL_STATUS);
        hlsMarketingReportService.updateByPrimaryKeySelective(iRequest, hlsCusHlsMarketingReport);
    }
}
