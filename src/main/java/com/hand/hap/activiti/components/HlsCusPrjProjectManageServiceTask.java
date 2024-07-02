package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.cont.utils.BeanRefUtils;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationDetailsMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.mapper.PrjProjectApprovalMapper;
import com.hand.hls.prj.mapper.ProjectApprovalConditionMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.prj.service.HlsCusPrjQuotationService;
import com.hand.hls.prj.service.IProjectApprovalService;
import com.hand.hls.prj.service.ProjectApprovalConditionService;
import lombok.SneakyThrows;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjProjectManageServiceTask implements TaskListener, IActivitiBean {
    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private HlsCusPrjQuotationMapper hlsCusPrjQuotationMapper;
    @Autowired
    private HlsCusPrjQuotationService hlsCusPrjQuotationService;
    @Autowired
    private HlsCusPrjQuotationDetailsMapper prjQuotationDetailsMapper;
    @Autowired
    private IProjectApprovalService projectApprovalService;
    @Autowired
    private PrjProjectApprovalMapper prjProjectApprovalMapper;
    @Autowired
    private ProjectApprovalConditionMapper conditionMapper;
    @Autowired
    private ProjectApprovalConditionService conditionService;

    @SneakyThrows
    @Override
    public void notify(DelegateTask delegateTask) {
        IRequest requestCtx = RequestHelper.getCurrentRequest();
        Long projectId = Long.parseLong(delegateTask.getExecution().getVariable("projectId").toString());

        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setProjectId(projectId);
        prjProject = hlsCusPrjProjectService.selectByPrimaryKey(requestCtx, prjProject);

        //1 报价复制 PRJ_PROJECT_AUDIT->PRJ_PROJECT_MANAGE
        HlsCusPrjQuotation auditQuotation = new HlsCusPrjQuotation();
        auditQuotation.setSourceDocumentId(prjProject.getProjectId());
        auditQuotation.setDocumentId(prjProject.getProjectId());
        auditQuotation.setSourceDocumentCategory("PRJ_PROJECT_AUDIT");
        auditQuotation.setDataClass("PRJ_PROJECT_INVEST");
        List<HlsCusPrjQuotation> auditQuotationList = hlsCusPrjQuotationMapper.select(auditQuotation);
        if (CollectionUtils.isNotEmpty(auditQuotationList)) {
            HlsCusPrjQuotation manageQuotation = new HlsCusPrjQuotation();
            manageQuotation.setDocumentId(prjProject.getProjectId());
            manageQuotation.setSourceDocumentId(prjProject.getProjectId());
            manageQuotation.setSourceDocumentCategory("PRJ_PROJECT_MANAGE");
            manageQuotation.setDataClass("PRJ_PROJECT_INVEST");
            List<HlsCusPrjQuotation> manageQuotationList = hlsCusPrjQuotationService.selectSelective(requestCtx, manageQuotation);
            for (HlsCusPrjQuotation item : manageQuotationList) {
                prjQuotationDetailsMapper.deleteDetailsById(item);
                hlsCusPrjQuotationService.deleteByPrimaryKey(item);
            }

            for (HlsCusPrjQuotation item : auditQuotationList) {
                BeanRefUtils.beanToBean(item, manageQuotation, hlsBeanRefUtilService);
                manageQuotation.setDocumentId(prjProject.getProjectId());
                manageQuotation.setSourceDocumentId(prjProject.getProjectId());
                manageQuotation.setSourceDocumentCategory("PRJ_PROJECT_MANAGE");
                manageQuotation.setDataClass("PRJ_PROJECT_INVEST");
                manageQuotation = hlsCusPrjQuotationService.insertSelective(requestCtx, manageQuotation);

                //查询报价明细
                HlsCusPrjQuotationDetails auditQuotationDetails = new HlsCusPrjQuotationDetails();
                auditQuotationDetails.setQuotationId(item.getQuotationId());
                List<HlsCusPrjQuotationDetails> auditQuotationDetailsList = prjQuotationDetailsMapper.queryDetailsById(auditQuotationDetails);
                if (CollectionUtils.isNotEmpty(auditQuotationDetailsList)) {
                    HlsCusPrjQuotationDetails auditDetailItem = auditQuotationDetailsList.get(0);
                    //项目报价明细
                    HlsCusPrjQuotationDetails manageQuotationDetails = new HlsCusPrjQuotationDetails();
                    BeanRefUtils.beanToBean(auditDetailItem, manageQuotationDetails, hlsBeanRefUtilService);
                    //设置项目报价明细外键（报价ID）
                    manageQuotationDetails.setQuotationId(manageQuotation.getQuotationId());
                    prjQuotationDetailsMapper.insertSelective(manageQuotationDetails);
                }
            }
        }

        //2.复制项目上会 -> 业审会 AUDIT->MANAGE
        PrjProjectApproval auditApproval = new PrjProjectApproval();
        auditApproval.setProjectId(prjProject.getProjectId().toString());
        auditApproval.setApprovalType("AUDIT");
        List<PrjProjectApproval> auditApprovals = projectApprovalService.selectSelective(requestCtx, auditApproval);

        PrjProjectApproval manageApproval = new PrjProjectApproval();
        manageApproval.setProjectId(prjProject.getProjectId().toString());
        manageApproval.setApprovalType("MANAGE");
        List<PrjProjectApproval> manageApprovals = prjProjectApprovalMapper.select(manageApproval);
        projectApprovalService.batchDelete(manageApprovals);
        if (CollectionUtils.isNotEmpty(auditApprovals)) {
            BeanRefUtils.beanToBean(auditApprovals.get(0), manageApproval, hlsBeanRefUtilService);
            manageApproval.setApprovalType("MANAGE");
            manageApproval = projectApprovalService.insertSelective(requestCtx, manageApproval);

            // 3、复制投放前提条件、投后管理要求
            ProjectApprovalCondition auditCondition = new ProjectApprovalCondition();
            auditCondition.setApprovalId(auditApprovals.get(0).getApprovalId());
            List<ProjectApprovalCondition> auditConditions = conditionMapper.select(auditCondition);

            ProjectApprovalCondition manageCondition = new ProjectApprovalCondition();
            manageCondition.setApprovalId(manageApproval.getApprovalId());
            List<ProjectApprovalCondition> manageConditions = conditionMapper.select(manageCondition);
            conditionService.batchDelete(manageConditions);

            for (ProjectApprovalCondition approvalCondition : auditConditions) {
                approvalCondition.setApprovalId(manageApproval.getApprovalId());
                conditionService.insertSelective(requestCtx, approvalCondition);
            }
        }
    }
}