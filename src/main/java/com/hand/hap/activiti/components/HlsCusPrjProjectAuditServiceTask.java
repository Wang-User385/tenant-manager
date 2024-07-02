package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.cont.utils.BeanRefUtils;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotationDetails;
import com.hand.hls.prj.dto.PrjProjectApproval;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationDetailsMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.mapper.PrjProjectApprovalMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.prj.service.HlsCusPrjQuotationService;
import com.hand.hls.prj.service.IProjectApprovalService;
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
public class HlsCusPrjProjectAuditServiceTask implements TaskListener, IActivitiBean {
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

    @SneakyThrows
    @Override
    public void notify(DelegateTask delegateTask) {
        IRequest requestCtx = RequestHelper.getCurrentRequest();
        Long projectId = Long.parseLong(delegateTask.getExecution().getVariable("projectId").toString());

        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setProjectId(projectId);
        prjProject = hlsCusPrjProjectService.selectByPrimaryKey(requestCtx, prjProject);

        //1 报价复制 PRJ_PROJECT->PRJ_PROJECT_AUDIT
        HlsCusPrjQuotation normalQuotation = new HlsCusPrjQuotation();
        normalQuotation.setSourceDocumentId(prjProject.getProjectId());
        normalQuotation.setDocumentId(prjProject.getProjectId());
        normalQuotation.setSourceDocumentCategory("PRJ_PROJECT");
        normalQuotation.setDataClass("PRJ_PROJECT_INVEST");
        List<HlsCusPrjQuotation> normalQuotationList = hlsCusPrjQuotationMapper.select(normalQuotation);
        if (CollectionUtils.isNotEmpty(normalQuotationList)) {
            HlsCusPrjQuotation auditQuotation = new HlsCusPrjQuotation();
            auditQuotation.setDocumentId(prjProject.getProjectId());
            auditQuotation.setSourceDocumentId(prjProject.getProjectId());
            auditQuotation.setSourceDocumentCategory("PRJ_PROJECT_AUDIT");
            auditQuotation.setDataClass("PRJ_PROJECT_INVEST");
            List<HlsCusPrjQuotation> auditQuotationList = hlsCusPrjQuotationService.selectSelective(requestCtx, auditQuotation);
            for (HlsCusPrjQuotation item : auditQuotationList) {
                prjQuotationDetailsMapper.deleteDetailsById(item);
                hlsCusPrjQuotationService.deleteByPrimaryKey(item);
            }

            for (HlsCusPrjQuotation item : normalQuotationList) {
                BeanRefUtils.beanToBean(item, auditQuotation, hlsBeanRefUtilService);
                auditQuotation.setDocumentId(prjProject.getProjectId());
                auditQuotation.setSourceDocumentId(prjProject.getProjectId());
                auditQuotation.setSourceDocumentCategory("PRJ_PROJECT_AUDIT");
                auditQuotation.setDataClass("PRJ_PROJECT_INVEST");
                auditQuotation = hlsCusPrjQuotationService.insertSelective(requestCtx, auditQuotation);

                //查询报价明细
                HlsCusPrjQuotationDetails normalQuotationDetails = new HlsCusPrjQuotationDetails();
                normalQuotationDetails.setQuotationId(item.getQuotationId());
                List<HlsCusPrjQuotationDetails> normalQuotationDetailsList = prjQuotationDetailsMapper.queryDetailsById(normalQuotationDetails);
                if (CollectionUtils.isNotEmpty(normalQuotationDetailsList)) {
                    HlsCusPrjQuotationDetails normalDetailItem = normalQuotationDetailsList.get(0);
                    //项目报价明细
                    HlsCusPrjQuotationDetails quotationDetails = new HlsCusPrjQuotationDetails();
                    BeanRefUtils.beanToBean(normalDetailItem, quotationDetails, hlsBeanRefUtilService);
                    //设置项目报价明细外键（报价ID）
                    quotationDetails.setQuotationId(auditQuotation.getQuotationId());
                    prjQuotationDetailsMapper.insertSelective(quotationDetails);
                }
            }
        }

        //2.复制项目上会 -> 业审会 NORMAL->AUDIT
        PrjProjectApproval normalApproval = new PrjProjectApproval();
        normalApproval.setProjectId(prjProject.getProjectId().toString());
        normalApproval.setApprovalType("NORMAL");
        List<PrjProjectApproval> normalApprovals = projectApprovalService.selectSelective(requestCtx, normalApproval);

        PrjProjectApproval auditApproval = new PrjProjectApproval();
        auditApproval.setProjectId(prjProject.getProjectId().toString());
        auditApproval.setApprovalType("AUDIT");
        List<PrjProjectApproval> auditApprovals = prjProjectApprovalMapper.select(auditApproval);
        projectApprovalService.batchDelete(auditApprovals);
        if (CollectionUtils.isNotEmpty(normalApprovals)) {
            BeanRefUtils.beanToBean(normalApprovals.get(0), auditApproval, hlsBeanRefUtilService);
            auditApproval.setApprovalType("AUDIT");
            projectApprovalService.insertSelective(requestCtx, auditApproval);
        }
    }
}