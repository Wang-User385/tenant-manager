package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.fct.dto.HlsCreditPlanLine;
import com.hand.hls.fct.mapper.HlsCreditPlanLineMapper;
import com.hand.hls.fct.service.HlsICreditPlanLineService;
import com.hand.hls.prj.dto.HlsCreditPlan;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectBp;
import com.hand.hls.prj.dto.HlsCusPrjProjectLease;
import com.hand.hls.prj.dto.HlsCusPrjProjectReceivable;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotationCashflow;
import com.hand.hls.prj.mapper.HlsCreditPlanMapper;
import com.hand.hls.prj.mapper.HlsCusPrjProjectBpMapper;
import com.hand.hls.prj.mapper.HlsCusPrjProjectLeaseMapper;
import com.hand.hls.prj.mapper.HlsCusPrjProjectReceivableMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationCashflowMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.HlsCreditPlanService;
import com.hand.hls.prj.service.HlsCusPrjIProjectLeaseService;
import com.hand.hls.prj.service.HlsCusPrjIProjectReceivableService;
import com.hand.hls.prj.service.HlsCusPrjProjectBpService;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.prj.service.HlsCusPrjQuotationService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 保理理想审批终止
 * </p>
 *
 * @author JINGZHOU.LI@hand-china.com 2024/9/4 17:17
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsFactoringApprovalTerminateServiceTask implements JavaDelegate, IActivitiBean {

    private static final String APPROVED = "APPROVED";

    private static final String REJECTED = "REJECTED";

    //点对点提交
    private static final String PEER_REJECTED = "PEER_REJECTED";

    //移交
    private static final String DELEGATE = "DELEGATE";

    //终止
    private static final String TERMINATE = "TERMINATE";

    public static final String PRJ_PROJECT = "PRJ_PROJECT";


    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private HlsCusPrjProjectBpService hlsCusPrjProjectBpService;
    @Autowired
    private HlsCusPrjQuotationMapper hlsCusPrjQuotationMapper;
    @Autowired
    private HlsCusPrjQuotationCashflowMapper hlsCusPrjQuotationCashflowMapper;
    @Autowired
    private HlsCreditPlanMapper hlsCreditPlanMapper;
    @Autowired
    private HlsCreditPlanLineMapper hlsCreditPlanLineMapper;

    @Autowired
    private HlsCusPrjIProjectReceivableService hlsCusPrjIProjectReceivableService;
    @Autowired
    private HlsCusPrjIProjectLeaseService hlsCusPrjIProjectLeaseService;

    @Autowired
    private HlsICreditPlanLineService hlsCreditPlanLineService;

    @Autowired
    private HlsCreditPlanService hlsCreditPlanService;

    @Autowired
    private HlsCusPrjQuotationService hlsCusPrjQuotationService;

    @Autowired
    private HlsCusPrjProjectBpMapper hlsCusPrjProjectBpMapper;

    @Autowired
    private HlsCusPrjProjectReceivableMapper hlsCusPrjIProjectReceivableMapper;

    @Autowired
    private HlsCusPrjProjectLeaseMapper hlsCusPrjProjectLeaseMapper;


    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long projectId = Long.parseLong(delegateExecution.getProcessInstanceBusinessKey());
        //Long processInstanceId = Long.parseLong(delegateExecution.getProcessInstanceId());
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setProjectId(projectId);
        prjProject = hlsCusPrjProjectService.selectByPrimaryKey(requestCtx, prjProject);
        if (isValid(prjProject)) {
            if (APPROVED.equalsIgnoreCase(result)) {
                prjProject.setProjectStatus(APPROVED);
                prjProject.setApprovedDate(new Date());
                hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, prjProject);
                copyFactoringToProjectContractApproval(requestCtx, prjProject.getProjectId(), prjProject);
            } else if (REJECTED.equalsIgnoreCase(result)) {
                prjProject.setProjectStatus(TERMINATE);
                hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, prjProject);
            } else if (PEER_REJECTED.equalsIgnoreCase(result)) {
                prjProject.setProjectStatus(PEER_REJECTED);
                hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, prjProject);
            } else if (DELEGATE.equalsIgnoreCase(result)) {
                prjProject.setProjectStatus(DELEGATE);
                hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, prjProject);
            }
        }
    }

    private void copyFactoringToProjectContractApproval(IRequest requestCtx, Long projectId, HlsCusPrjProject root) {
        // 项目基本信息
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        copyProjectInfo(requestCtx, projectId, root, prjProject);
        // 复制客户信息
        copyCustomerInfo(requestCtx, projectId, prjProject.getProjectId());
        // 复制报价基本方案
        copyQuotationInfo(requestCtx, projectId, prjProject.getProjectId());
        // 复制授信方案
        copyCreditPlanInfo(requestCtx, projectId, prjProject.getProjectId());
        // 复制应收账款
        copyReceivableInfo(requestCtx, projectId, prjProject.getProjectId());
        // 复制抵押信息
        copyLeaseInfo(requestCtx, projectId, prjProject.getProjectId());
    }

    private void copyProjectInfo(IRequest requestCtx, Long projectId, HlsCusPrjProject root, HlsCusPrjProject prjProject) {
        BeanUtils.copyProperties(root, prjProject);
        prjProject.setProjectStatus("NEW");
        prjProject.setSourceDocumentId(projectId);
        prjProject.setApprovedDate(null);
        prjProject.setDataClass("VIRTUAL_CON");
        hlsCusPrjProjectService.insert(requestCtx, prjProject);
    }

    private void copyCustomerInfo(IRequest requestCtx, Long oldProjectId, Long newProjectId) {
        HlsCusPrjProjectBp cusPrjProjectBp = new HlsCusPrjProjectBp();
        cusPrjProjectBp.setProjectId(oldProjectId);
        List<HlsCusPrjProjectBp> factoringBPInfoList = hlsCusPrjProjectBpMapper.findFactoringBPInfo(cusPrjProjectBp);
        if (!factoringBPInfoList.isEmpty()) {
            factoringBPInfoList.forEach(v -> {
                HlsCusPrjProjectBp hlsCusPrjProjectBp = new HlsCusPrjProjectBp();
                BeanUtils.copyProperties(v, hlsCusPrjProjectBp);
                hlsCusPrjProjectBp.setProjectId(newProjectId);
                hlsCusPrjProjectBpService.insert(requestCtx, hlsCusPrjProjectBp);
            });
        }
    }

    private void copyQuotationInfo(IRequest requestCtx, Long oldProjectId, Long newProjectId) {
        HlsCusPrjQuotation quotation = new HlsCusPrjQuotation();
        quotation.setProjectId(oldProjectId);
        List<HlsCusPrjQuotation> factoringQuotation = hlsCusPrjQuotationMapper.findFactoringApprovalQuotation(quotation);
        if (!factoringQuotation.isEmpty()) {
            HlsCusPrjQuotation approvalQuotation = new HlsCusPrjQuotation();
            factoringQuotation.forEach(v -> {
                BeanUtils.copyProperties(v, approvalQuotation);
                approvalQuotation.setSourceDocumentCategory(PRJ_PROJECT);
                approvalQuotation.setSourceDocumentId(newProjectId);
                hlsCusPrjQuotationService.insert(requestCtx, approvalQuotation);
            });
            // 复制现金流信息
            HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = new HlsCusPrjQuotationCashflow();
            hlsCusPrjQuotationCashflow.setQuotationId(factoringQuotation.get(0).getQuotationId());
            List<HlsCusPrjQuotationCashflow> factoringCashflowInfo = hlsCusPrjQuotationCashflowMapper.findFactoringApprovalInfo(hlsCusPrjQuotationCashflow);
            if (!factoringCashflowInfo.isEmpty()) {
                factoringCashflowInfo.forEach(v -> {
                    HlsCusPrjQuotationCashflow quotationApprovalCashflow = new HlsCusPrjQuotationCashflow();
                    BeanUtils.copyProperties(v, quotationApprovalCashflow);
                    quotationApprovalCashflow.setQuotationId(approvalQuotation.getQuotationId());
                    hlsCusPrjQuotationCashflowMapper.insert(quotationApprovalCashflow);
                });
            }
        }
    }

    private void copyCreditPlanInfo(IRequest requestCtx, Long oldProjectId, Long newProjectId) {
        HlsCreditPlan hlsCreditPlan = new HlsCreditPlan();
        hlsCreditPlan.setProjectId(oldProjectId);
        List<HlsCreditPlan> factoringCreditPlanInfo = hlsCreditPlanMapper.findFactoringApprovalInfo(hlsCreditPlan);
        if (!factoringCreditPlanInfo.isEmpty()) {
            HlsCreditPlan hlsCreditPlanApproval = new HlsCreditPlan();
            factoringCreditPlanInfo.forEach(v -> {
                BeanUtils.copyProperties(v, hlsCreditPlanApproval);
                hlsCreditPlanApproval.setSourceDocumentCategory(PRJ_PROJECT);
                hlsCreditPlanApproval.setSourceDocumentId(newProjectId);
                hlsCreditPlanService.insert(requestCtx, hlsCreditPlanApproval);
            });
            // 复制授信方案明细
            HlsCreditPlanLine hlsCreditPlanLine = new HlsCreditPlanLine();
            hlsCreditPlanLine.setCreditPlanId(factoringCreditPlanInfo.get(0).getCreditPlanId());
            List<HlsCreditPlanLine> factoringCreditPlanLineInfo = hlsCreditPlanLineMapper.findFactoringApprovalInfo(hlsCreditPlanLine);
            if (!factoringCreditPlanLineInfo.isEmpty()) {
                factoringCreditPlanLineInfo.forEach(v -> {
                    HlsCreditPlanLine hlsCreditPlanLineApproval = new HlsCreditPlanLine();
                    BeanUtils.copyProperties(v, hlsCreditPlanLineApproval);
                    hlsCreditPlanLineApproval.setCreditPlanId(hlsCreditPlanApproval.getCreditPlanId());
                    hlsCreditPlanLineService.insert(requestCtx, hlsCreditPlanLineApproval);
                });
            }
        }
    }

    private void copyReceivableInfo(IRequest requestCtx, Long oldProjectId, Long newProjectId) {
        HlsCusPrjProjectReceivable prjProjectReceivable = new HlsCusPrjProjectReceivable();
        prjProjectReceivable.setProjectId(oldProjectId);
        List<HlsCusPrjProjectReceivable> factoringInfoReceivable = hlsCusPrjIProjectReceivableMapper.findFactoringApprovalInfo(prjProjectReceivable);
        if (!factoringInfoReceivable.isEmpty()) {
            factoringInfoReceivable.forEach(v -> {
                HlsCusPrjProjectReceivable hlsCusPrjProjectReceivable = new HlsCusPrjProjectReceivable();
                BeanUtils.copyProperties(v, hlsCusPrjProjectReceivable);
                hlsCusPrjProjectReceivable.setProjectId(newProjectId);
                hlsCusPrjIProjectReceivableService.insert(requestCtx, hlsCusPrjProjectReceivable);
            });
        }
    }

    private void copyLeaseInfo(IRequest requestCtx, Long oldProjectId, Long newProjectId) {
        HlsCusPrjProjectLease prjProjectLease = new HlsCusPrjProjectLease();
        prjProjectLease.setProjectId(oldProjectId);
        List<HlsCusPrjProjectLease> factoringInfoLease = hlsCusPrjProjectLeaseMapper.findFactoringApprovalInfo(prjProjectLease);
        if (!factoringInfoLease.isEmpty()) {
            factoringInfoLease.forEach(v -> {
                HlsCusPrjProjectLease hlsCusPrjProjectLease = new HlsCusPrjProjectLease();
                BeanUtils.copyProperties(v, hlsCusPrjProjectLease);
                hlsCusPrjProjectLease.setProjectId(newProjectId);
                hlsCusPrjIProjectLeaseService.insert(requestCtx, hlsCusPrjProjectLease);
            });
        }
    }


    private boolean isValid(HlsCusPrjProject prjProject) {
        return !APPROVED.equalsIgnoreCase(prjProject.getProjectStatus()) &&
                !REJECTED.equalsIgnoreCase(prjProject.getProjectStatus()) &&
                !PEER_REJECTED.equalsIgnoreCase(prjProject.getProjectStatus()) &&
                !DELEGATE.equalsIgnoreCase(prjProject.getProjectStatus());
    }

}
