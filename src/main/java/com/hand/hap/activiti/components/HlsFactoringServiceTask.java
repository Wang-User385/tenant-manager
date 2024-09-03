package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.fct.dto.HlsChanceBusinessAccessCompare;
import com.hand.hls.fct.dto.HlsCreditLineAttach;
import com.hand.hls.fct.dto.HlsCreditLineChanceCondition;
import com.hand.hls.fct.dto.HlsCreditLineLease;
import com.hand.hls.fct.dto.HlsCreditLineReceivable;
import com.hand.hls.fct.dto.HlsCreditPlanLine;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChanceAttach;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChanceBp;
import com.hand.hls.fct.mapper.HlsChanceBusinessAccessCompareMapper;
import com.hand.hls.fct.mapper.HlsCreditLineAttachMapper;
import com.hand.hls.fct.mapper.HlsCreditLineChanceConditionMapper;
import com.hand.hls.fct.mapper.HlsCreditLineLeaseMapper;
import com.hand.hls.fct.mapper.HlsCreditLineReceivableMapper;
import com.hand.hls.fct.mapper.HlsCreditPlanLineMapper;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceAttachMapper;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceBpMapper;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceAttachService;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceBpService;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceService;
import com.hand.hls.fct.service.HlsICreditPlanLineService;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.partner.service.IYLMessageNoticeService;
import com.hand.hls.prj.dto.HlsCreditPlan;
import com.hand.hls.prj.dto.HlsCusPrjBusinessAccessCompare;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectAttachment;
import com.hand.hls.prj.dto.HlsCusPrjProjectBp;
import com.hand.hls.prj.dto.HlsCusPrjProjectCondition;
import com.hand.hls.prj.dto.HlsCusPrjProjectLease;
import com.hand.hls.prj.dto.HlsCusPrjProjectReceivable;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotationCashflow;
import com.hand.hls.prj.dto.PrjQuotation;
import com.hand.hls.prj.mapper.HlsCreditPlanMapper;
import com.hand.hls.prj.mapper.HlsCusPrjBusinessAccessCompareMapper;
import com.hand.hls.prj.mapper.HlsCusPrjProjectConditionMapper;
import com.hand.hls.prj.mapper.HlsCusPrjProjectLeaseMapper;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationCashflowMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.HlsCreditPlanService;
import com.hand.hls.prj.service.HlsCusPrjIBusinessAccessCompareService;
import com.hand.hls.prj.service.HlsCusPrjIProjectConditionService;
import com.hand.hls.prj.service.HlsCusPrjIProjectLeaseService;
import com.hand.hls.prj.service.HlsCusPrjIProjectReceivableService;
import com.hand.hls.prj.service.HlsCusPrjProjectAttachmentService;
import com.hand.hls.prj.service.HlsCusPrjProjectBpService;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.prj.service.HlsCusPrjQuotationService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * description
 * </p>
 *
 * @author JINGZHOU.LI@hand-china.com 2024/8/30 9:28
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsFactoringServiceTask implements JavaDelegate, IActivitiBean {

    @Autowired
    private HlsCusHlsCreditLineChanceService hlsCusHlsCreditLineChanceService;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private HlsCusHlsCreditLineChanceBpMapper hlsCusHlsCreditLineChanceBpMapper;
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
    private HlsCreditLineReceivableMapper hlsCreditLineReceivableMapper;
    @Autowired
    private HlsCusPrjIProjectReceivableService hlsCusPrjIProjectReceivableService;
    @Autowired
    private HlsCreditLineLeaseMapper hlsCreditLineLeaseMapper;
    @Autowired
    private HlsCusPrjIProjectLeaseService hlsCusPrjIProjectLeaseService;
    @Autowired
    private HlsChanceBusinessAccessCompareMapper hlsChanceBusinessAccessCompareMapper;
    @Autowired
    private HlsCusPrjIBusinessAccessCompareService hlsCusPrjIBusinessAccessCompareService;
    @Autowired
    private HlsCreditLineChanceConditionMapper hlsCreditLineChanceConditionMapper;
    @Autowired
    private HlsCusPrjIProjectConditionService hlsCusPrjIProjectConditionService;

    @Autowired
    private HlsCusHlsCreditLineChanceAttachMapper hlsCusHlsCreditLineChanceAttachMapper;

    @Autowired
    private HlsCusPrjProjectAttachmentService hlsCusPrjProjectAttachmentService;

    @Autowired
    private HlsICreditPlanLineService hlsCreditPlanLineService;

    @Autowired
    private HlsCreditPlanService hlsCreditPlanService;

    @Autowired
    private HlsCusPrjQuotationService hlsCusPrjQuotationService;

    public static final String CHANCE_PRJ_ATT = "CHANCE_PRJ_ATT";

    public static final String HLS_CREDIT_LINE_CHANCE = "HLS_CREDIT_LINE_CHANCE";

    public static final String PRJ_PROJECT = "PRJ_PROJECT";
    public static final String STAGE_PRE = "STAGE_PRE";
    public static final String STAGE_AFTER = "STAGE_AFTER";
    private static final String APPROVED = "APPROVED";
    private static final String REJECTED = "REJECTED";
    //点对点提交
    private static final String PEER_REJECTED = "PEER_REJECTED";
    //移交
    private static final String DELEGATE = "DELEGATE";

    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long chanceId = Long.parseLong(delegateExecution.getProcessInstanceBusinessKey());
        HlsCusHlsCreditLineChance chance = new HlsCusHlsCreditLineChance();
        chance.setChanceId(chanceId);
        chance = hlsCusHlsCreditLineChanceService.selectByPrimaryKey(requestCtx, chance);
        if (isValid(chance)) {
            updateCreditLineStatus(requestCtx, chance, result);
            copyFactoringToProjectApproval(requestCtx, chanceId, chance);
        }
    }

    private void updateCreditLineStatus(IRequest requestCtx, HlsCusHlsCreditLineChance chance, String result) {
        if (APPROVED.equalsIgnoreCase(result)) {
            chance.setCreditLineStatus(APPROVED);
        } else if (REJECTED.equalsIgnoreCase(result)) {
            chance.setCreditLineStatus(REJECTED);
        } else if (PEER_REJECTED.equalsIgnoreCase(chance.getCreditLineStatus())) {
            chance.setCreditLineStatus(PEER_REJECTED);
        } else if (DELEGATE.equalsIgnoreCase(chance.getCreditLineStatus())) {
            chance.setCreditLineStatus(DELEGATE);
        }
        hlsCusHlsCreditLineChanceService.updateByPrimaryKeySelective(requestCtx, chance);
    }

    private void copyFactoringToProjectApproval(IRequest requestCtx, Long chanceId, HlsCusHlsCreditLineChance chance) {
        // 项目基本信息
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        BeanUtils.copyProperties(chance, prjProject);
        prjProject.setProjectNumber(chance.getCreditLineNumber());
        prjProject.setDocumentCategory(PRJ_PROJECT);
        prjProject.setDocumentType("FACTORING");
        prjProject.setSourceDocumentId(chanceId);
        prjProject.setHostProjectManager(chance.getProposerEmployeeId());
        prjProject.setAssistProjectManager(chance.getProjectAssistant());
        hlsCusPrjProjectService.insert(requestCtx, prjProject);
        // 复制客户信息
        copyCustomerInfo(requestCtx, chanceId, prjProject);
        // 复制报价基本方案
        copyQuotationInfo(requestCtx, chanceId, prjProject);
        // 复制授信方案
        copyCreditPlanInfo(requestCtx, chanceId, prjProject);
        // 复制应收账款
        copyReceivableInfo(requestCtx, chanceId, prjProject);
        // 复制抵押信息
        copyLeaseInfo(requestCtx, chanceId, prjProject);
        // 复制业务准入审核
        copyBusinessAccessCompareInfo(requestCtx, chanceId, prjProject);
        // 复制投放前
        copyConditionInfo(requestCtx, chanceId, prjProject, STAGE_PRE);
        // 复制投放后
        copyConditionInfo(requestCtx, chanceId, prjProject, STAGE_AFTER);
        // 复制附件
        copyAttachmentInfo(requestCtx, chanceId, prjProject);
    }

    private void copyCustomerInfo(IRequest requestCtx, Long chanceId, HlsCusPrjProject prjProject) {
        HlsCusHlsCreditLineChanceBp hlsCusHlsCreditLineChanceBp = new HlsCusHlsCreditLineChanceBp();
        hlsCusHlsCreditLineChanceBp.setChanceId(chanceId);
        List<HlsCusHlsCreditLineChanceBp> factoringBPInfoList = hlsCusHlsCreditLineChanceBpMapper.findCustomer(hlsCusHlsCreditLineChanceBp);
        if (!factoringBPInfoList.isEmpty()) {
            factoringBPInfoList.forEach(v -> {
                HlsCusPrjProjectBp hlsCusPrjProjectBp = new HlsCusPrjProjectBp();
                BeanUtils.copyProperties(v, hlsCusPrjProjectBp);
                hlsCusPrjProjectBp.setDescription(v.getNote());
                hlsCusPrjProjectBpService.insert(requestCtx, hlsCusPrjProjectBp);
            });
        }
    }

    private void copyQuotationInfo(IRequest requestCtx, Long chanceId, HlsCusPrjProject prjProject) {
        HlsCusPrjQuotation quotation = new HlsCusPrjQuotation();
        quotation.setChanceId(chanceId);
        List<HlsCusPrjQuotation> factoringQuotation = hlsCusPrjQuotationMapper.findFactoringQuotation(quotation);
        if (!factoringQuotation.isEmpty()) {
            HlsCusPrjQuotation approvalQuotation = new HlsCusPrjQuotation();
            factoringQuotation.forEach(v -> {
                BeanUtils.copyProperties(v, approvalQuotation);
                approvalQuotation.setProjectId(prjProject.getProjectId());
                hlsCusPrjQuotationService.insert(requestCtx, approvalQuotation);
            });
            // 复制现金流信息
            HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = new HlsCusPrjQuotationCashflow();
            hlsCusPrjQuotationCashflow.setQuotationId(factoringQuotation.get(0).getQuotationId());
            List<HlsCusPrjQuotationCashflow> factoringCashflowInfo = hlsCusPrjQuotationCashflowMapper.findFactoringInfo(hlsCusPrjQuotationCashflow);
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

    private void copyCreditPlanInfo(IRequest requestCtx, Long chanceId, HlsCusPrjProject prjProject) {
        HlsCreditPlan hlsCreditPlan = new HlsCreditPlan();
        hlsCreditPlan.setSourceDocumentId(chanceId);
        hlsCreditPlan.setSourceDocumentCategory(HLS_CREDIT_LINE_CHANCE);
        List<HlsCreditPlan> factoringCreditPlanInfo = hlsCreditPlanMapper.findFactoringInfo(hlsCreditPlan);
        if (!factoringCreditPlanInfo.isEmpty()) {
            HlsCreditPlan hlsCreditPlanApproval = new HlsCreditPlan();
            factoringCreditPlanInfo.forEach(v -> {
                BeanUtils.copyProperties(v, hlsCreditPlanApproval);
                hlsCreditPlanApproval.setSourceDocumentCategory(PRJ_PROJECT);
                hlsCreditPlanApproval.setSourceDocumentId(prjProject.getProjectId());
                hlsCreditPlanService.insert(requestCtx, hlsCreditPlanApproval);
            });
            // 复制授信方案明细
            HlsCreditPlanLine hlsCreditPlanLine = new HlsCreditPlanLine();
            hlsCreditPlanLine.setCreditPlanId(factoringCreditPlanInfo.get(0).getCreditPlanId());
            List<HlsCreditPlanLine> factoringCreditPlanLineInfo = hlsCreditPlanLineMapper.findFactoringInfo(hlsCreditPlanLine);
            if (!factoringCreditPlanLineInfo.isEmpty()) {
                factoringCreditPlanLineInfo.forEach(v -> {
                    HlsCreditPlanLine hlsCreditPlanLineApproval = new HlsCreditPlanLine();
                    BeanUtils.copyProperties(v, hlsCreditPlanLineApproval);
                    hlsCreditPlanApproval.setCreditPlanId(hlsCreditPlanApproval.getCreditPlanId());
                    hlsCreditPlanLineService.insert(requestCtx, hlsCreditPlanLineApproval);
                });
            }
        }
    }

    private void copyReceivableInfo(IRequest requestCtx, Long chanceId, HlsCusPrjProject prjProject) {
        HlsCreditLineReceivable hlsCreditLineReceivable = new HlsCreditLineReceivable();
        List<HlsCreditLineReceivable> factoringInfoReceivable = hlsCreditLineReceivableMapper.findFactoringInfo(hlsCreditLineReceivable);
        if (!factoringInfoReceivable.isEmpty()) {
            factoringInfoReceivable.forEach(v -> {
                HlsCusPrjProjectReceivable hlsCusPrjProjectReceivable = new HlsCusPrjProjectReceivable();
                BeanUtils.copyProperties(v, hlsCreditLineReceivable);
                hlsCusPrjProjectReceivable.setProjectId(prjProject.getProjectId());
                hlsCusPrjIProjectReceivableService.insert(requestCtx, hlsCusPrjProjectReceivable);
            });
        }
    }

    private void copyLeaseInfo(IRequest requestCtx, Long chanceId, HlsCusPrjProject prjProject) {
        HlsCreditLineLease hlsCreditLineLease = new HlsCreditLineLease();
        hlsCreditLineLease.setChanceId(chanceId);
        List<HlsCreditLineLease> factoringInfoLease = hlsCreditLineLeaseMapper.findFactoringInfo(hlsCreditLineLease);
        if (!factoringInfoLease.isEmpty()) {
            factoringInfoLease.forEach(v -> {
                HlsCusPrjProjectLease hlsCusPrjProjectLease = new HlsCusPrjProjectLease();
                BeanUtils.copyProperties(v, hlsCusPrjProjectLease);
                hlsCusPrjProjectLease.setProjectId(prjProject.getProjectId());
                hlsCusPrjIProjectLeaseService.insert(requestCtx, hlsCusPrjProjectLease);
            });
        }
    }

    private void copyBusinessAccessCompareInfo(IRequest requestCtx, Long chanceId, HlsCusPrjProject prjProject) {
        HlsChanceBusinessAccessCompare hlsChanceBusinessAccessCompare = new HlsChanceBusinessAccessCompare();
        hlsChanceBusinessAccessCompare.setChanceId(chanceId);
        List<HlsChanceBusinessAccessCompare> factoringInfoCompare = hlsChanceBusinessAccessCompareMapper.findFactoringInfo(hlsChanceBusinessAccessCompare);
        if (!factoringInfoCompare.isEmpty()) {
            factoringInfoCompare.forEach(v -> {
                HlsCusPrjBusinessAccessCompare hlsCusPrjBusinessAccessCompare = new HlsCusPrjBusinessAccessCompare();
                BeanUtils.copyProperties(v, hlsCusPrjBusinessAccessCompare);
                hlsCusPrjBusinessAccessCompare.setProjectId(prjProject.getProjectId());
                hlsCusPrjIBusinessAccessCompareService.insert(requestCtx, hlsCusPrjBusinessAccessCompare);
            });
        }
    }

    private void copyConditionInfo(IRequest requestCtx, Long chanceId, HlsCusPrjProject prjProject, String stage) {
        HlsCreditLineChanceCondition hlsCreditLineChanceCondition = new HlsCreditLineChanceCondition();
        hlsCreditLineChanceCondition.setChanceId(chanceId);
        hlsCreditLineChanceCondition.setStage(stage);
        List<HlsCreditLineChanceCondition> factoringConditionInfo = hlsCreditLineChanceConditionMapper.findFactoringCondition(hlsCreditLineChanceCondition);
        if (!factoringConditionInfo.isEmpty()) {
            factoringConditionInfo.forEach(v -> {
                HlsCusPrjProjectCondition hlsCusPrjProjectCondition = new HlsCusPrjProjectCondition();
                BeanUtils.copyProperties(v, hlsCusPrjProjectCondition);
                hlsCusPrjProjectCondition.setProjectId(prjProject.getProjectId());
                hlsCusPrjIProjectConditionService.insert(requestCtx, hlsCusPrjProjectCondition);
            });
        }
    }

    private void copyAttachmentInfo(IRequest requestCtx, Long chanceId, HlsCusPrjProject prjProject) {
        HlsCusHlsCreditLineChanceAttach hlsCreditLineAttach = new HlsCusHlsCreditLineChanceAttach();
        hlsCreditLineAttach.setChanceId(chanceId);
        List<HlsCusHlsCreditLineChanceAttach> factoringInfoAttach = hlsCusHlsCreditLineChanceAttachMapper.findFactoringInfo(hlsCreditLineAttach);
        if (!factoringInfoAttach.isEmpty()) {
            factoringInfoAttach.forEach(v -> {
                HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment = new HlsCusPrjProjectAttachment();
                BeanUtils.copyProperties(v, hlsCusPrjProjectAttachment);
                hlsCusPrjProjectAttachment.setProjectId(prjProject.getProjectId());
                hlsCusPrjProjectAttachment.setProjectAttachmentCategory(CHANCE_PRJ_ATT);
                hlsCusPrjProjectAttachmentService.insert(requestCtx, hlsCusPrjProjectAttachment);
            });
        }
    }

    private boolean isValid(HlsCusHlsCreditLineChance chance) {
        return !APPROVED.equalsIgnoreCase(chance.getCreditLineStatus()) &&
                !REJECTED.equalsIgnoreCase(chance.getCreditLineStatus()) &&
                !PEER_REJECTED.equalsIgnoreCase(chance.getCreditLineStatus()) &&
                !DELEGATE.equalsIgnoreCase(chance.getCreditLineStatus());
    }

}
