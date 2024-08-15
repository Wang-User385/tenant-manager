package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.partner.service.IYLMessageNoticeService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Component
public class PrjProjectFormalServiceTask implements JavaDelegate, IActivitiBean {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;
    @Autowired
    FndCodingRuleValuesService codingRuleValuesService;
    @Autowired
    IYLMessageNoticeService messageNoticeService;

    @Autowired
    IYLMessageNoticeService iylMessageNoticeService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(DelegateExecution execution) {
        Long projectId = Long.parseLong(execution.getProcessInstanceBusinessKey());
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        IRequest requestCtx = (IRequest) execution.getVariable("iRequest");

        String result = (String) execution.getVariable("approveResult");
        if("APPROVED".equals(result)){
            hlsCusPrjProject.setProjectStatus("APPROVED");
            hlsCusPrjProject.setApprovedDate(new Date());
            hlsCusPrjProjectMapper.updateByPrimaryKeySelective(hlsCusPrjProject);
        }else if("REJECTED".equals(result)){
            hlsCusPrjProject.setProjectStatus("REJECTED");
            hlsCusPrjProjectMapper.updateByPrimaryKeySelective(hlsCusPrjProject);
        }
        iylMessageNoticeService.orderAuditResult(projectId,"PRE_RISK",requestCtx);



    }


}
