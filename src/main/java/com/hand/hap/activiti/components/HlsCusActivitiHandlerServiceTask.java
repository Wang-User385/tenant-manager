package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectParam;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.mapper.HlsCreditPlanMapper;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class HlsCusActivitiHandlerServiceTask implements TaskListener, IActivitiBean {

    @Autowired
    private HlsCusPrjQuotationMapper prjQuotationMapper;
    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;
    @Autowired
    private HlsCreditPlanMapper hlsCreditPlanMapper;

    private static final String PRJ_PROJECT_AUDIT = "PRJ_PROJECT_AUDIT";
    private static final String PRJ_PROJECT_MANAGE = "PRJ_PROJECT_MANAGE";
    private static final String PRJ_PROJECT = "PRJ_PROJECT";

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void notify(DelegateTask delegateTask) {
        logger.info("-----------------------------进入监听，开始处理金额--------------------------------------");
        Double leaseItemAmount = (Double) delegateTask.getVariable("leaseItemAmount");
        logger.info("原有leaseItemAmount值："+leaseItemAmount);
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        Long projectId = (Long)delegateTask.getVariable("projectId");
        hlsCusPrjProject.setProjectId(projectId);
        if("Y".equals(delegateTask.getVariable("creditFlag"))){
            HlsCusPrjProjectParam hlsCusPrjProjectParam = new HlsCusPrjProjectParam();
            hlsCusPrjProjectParam.setProjectId(projectId.intValue());
            List<Map> results = hlsCreditPlanMapper.queryPaymentCreditPlanAuditInfo(hlsCusPrjProjectParam);
            if (!results.isEmpty()) {
                Map map = results.get(0);
                leaseItemAmount = Double.parseDouble(map.get("credit_amt").toString());
            }
        }else{
            List<HlsCusPrjQuotation> hlsCusPrjQuotations = prjQuotationMapper.selectAllQuotationByProjectId(hlsCusPrjProject);
            HlsCusPrjQuotation hlsCusPrjQuotation = hlsCusPrjQuotations.stream().max(Comparator.comparingInt(o -> o.getQuotationId().intValue())).get();
            leaseItemAmount = hlsCusPrjQuotation.getLeaseItemAmount();
        }
        delegateTask.setVariable("leaseItemAmount",leaseItemAmount);
        logger.info("更新后leaseItemAmount值："+leaseItemAmount);
        logger.info("-----------------------------结束监听，处理完毕--------------------------------------");
    }
}
