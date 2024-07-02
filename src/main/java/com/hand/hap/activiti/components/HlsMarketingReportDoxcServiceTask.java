package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.hls.dto.HlsCusHlsMarketingReport;
import com.hand.hls.hls.mapper.HlsCusHlsMarketingReportMapper;
import com.hand.hls.hls.service.HlsCusHlsMarketingReportService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.prj.service.IPrjProjectService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.TaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sun.misc.Request;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.List;

//项目方案 合同文本生成
@Component
public class HlsMarketingReportDoxcServiceTask implements JavaDelegate, IActivitiBean {
    private static final String BUSINESS_KEY = "BUSINESS_KEY";
    private static final String Y = "Y";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HlsCusHlsMarketingReportMapper mapper;
    @Autowired
    private HlsCusHlsMarketingReportService service;

    @Value("${jacob.start:false}")
    private Boolean jacob;
    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest iRequest = RequestHelper.getCurrentRequest();
        //获取项目方案id
        Long marketingReprotId = Long.valueOf(delegateExecution.getVariable(BUSINESS_KEY).toString());
        HlsCusHlsMarketingReport hlsMarketingReport = new HlsCusHlsMarketingReport();
        hlsMarketingReport.setMarketingReportId(marketingReprotId);
        //设置合同文本可打印节点
        hlsMarketingReport.setDocxFlag(Y);
        hlsMarketingReport.setContractTextStatus("CREATED");
        mapper.updateByPrimaryKeySelective(hlsMarketingReport);

        //自动生成合同文本
        List<FndAttachment> list = new ArrayList<>();
        try {
            list = service.reportCreateDocx(iRequest, hlsMarketingReport);
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        //转pdf
//        if(jacob){
//            service.wordToPdf(iRequest,list);
//        }
    }
}
