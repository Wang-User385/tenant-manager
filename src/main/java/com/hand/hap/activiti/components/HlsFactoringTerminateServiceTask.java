package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.mapper.FndAttachmentMapper;
import com.hand.hls.atm.mapper.FndAttachmentMultiMapper;
import com.hand.hls.fct.dto.HlsChanceBusinessAccessCompare;
import com.hand.hls.fct.dto.HlsCreditLineChanceCondition;
import com.hand.hls.fct.dto.HlsCreditLineLease;
import com.hand.hls.fct.dto.HlsCreditLineReceivable;
import com.hand.hls.fct.dto.HlsCreditPlanLine;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChanceAttach;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChanceBp;
import com.hand.hls.fct.mapper.HlsChanceBusinessAccessCompareMapper;
import com.hand.hls.fct.mapper.HlsCreditLineChanceConditionMapper;
import com.hand.hls.fct.mapper.HlsCreditLineLeaseMapper;
import com.hand.hls.fct.mapper.HlsCreditLineReceivableMapper;
import com.hand.hls.fct.mapper.HlsCreditPlanLineMapper;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceAttachMapper;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceBpMapper;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceService;
import com.hand.hls.fct.service.HlsICreditPlanLineService;
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
import com.hand.hls.prj.mapper.HlsCreditPlanMapper;
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

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 终止事件
 * </p>
 *
 * @author JINGZHOU.LI@hand-china.com 2024/8/30 15:45
 */

@Component
@Transactional(rollbackFor = Exception.class)
public class HlsFactoringTerminateServiceTask implements JavaDelegate, IActivitiBean {

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

    //终止
    private static final String TERMINATE = "TERMINATE";

    private static final String HLS_CREDIT_LINE_ATTACH = "HLS_CREDIT_LINE_ATTACH";
    private static final String PRJ_PROJECT_ATTACHMENT = "PRJ_PROJECT_ATTACHMENT";


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

    @Autowired
    private FndAttachmentMultiMapper fndAttachmentMultiMapper;
    @Autowired
    private FndAttachmentMapper fndAttachmentMapper;



    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long chanceId = Long.parseLong(delegateExecution.getProcessInstanceBusinessKey());
        //Long processInstanceId = Long.parseLong(delegateExecution.getProcessInstanceId());
        HlsCusHlsCreditLineChance chance = new HlsCusHlsCreditLineChance();
        chance.setChanceId(chanceId);
        chance = hlsCusHlsCreditLineChanceService.selectByPrimaryKey(requestCtx, chance);
        if (isValid(chance)) {
            if (APPROVED.equalsIgnoreCase(result)) {
                chance.setCreditLineStatus(APPROVED);
                chance.setApprovedDate(new Date());
                copyFactoringToProjectApproval(requestCtx, chance.getChanceId(), chance);
            } else if (REJECTED.equalsIgnoreCase(result)) {
                chance.setCreditLineStatus(TERMINATE);
            } else if (PEER_REJECTED.equalsIgnoreCase(result)) {
                chance.setCreditLineStatus(PEER_REJECTED);
            } else if (DELEGATE.equalsIgnoreCase(result)) {
                chance.setCreditLineStatus(DELEGATE);
            }
            hlsCusHlsCreditLineChanceService.updateByPrimaryKeySelective(requestCtx, chance);
        }
    }


    private void copyFactoringToProjectApproval(IRequest requestCtx, Long chanceId, HlsCusHlsCreditLineChance chance) {
        // 项目基本信息
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        copyProjectInfo(requestCtx, chanceId, chance, prjProject);
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

    private void copyProjectInfo(IRequest requestCtx, Long chanceId, HlsCusHlsCreditLineChance chance, HlsCusPrjProject prjProject) {
        BeanUtils.copyProperties(chance, prjProject);
        prjProject.setTenantId(chance.getBpId());
        prjProject.setLeaseItemAmount(chance.getCreditLineAmt());
        prjProject.setProjectStatus("NEW");
        prjProject.setProjectName(chance.getCreditLineName());
        prjProject.setProjectNumber(chance.getCreditLineNumber());
        prjProject.setDocumentCategory(PRJ_PROJECT);
        prjProject.setDocumentType("FACTORING");
        prjProject.setSourceDocumentId(chanceId);
        prjProject.setApprovedDate(null);
        prjProject.setDataClass("VIRTUAL_CON");
        prjProject.setHostProjectManager(chance.getProposerEmployeeId());
        prjProject.setAssistProjectManager(chance.getProjectAssistant());
        hlsCusPrjProjectService.insert(requestCtx, prjProject);
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
                hlsCusPrjProjectBp.setProjectId(prjProject.getProjectId());
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
                approvalQuotation.setSourceDocumentCategory(PRJ_PROJECT);
                approvalQuotation.setSourceDocumentId(prjProject.getProjectId());
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
        hlsCreditPlan.setChanceId(chanceId);
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
                    hlsCreditPlanLineApproval.setCreditPlanId(hlsCreditPlanApproval.getCreditPlanId());
                    hlsCreditPlanLineService.insert(requestCtx, hlsCreditPlanLineApproval);
                });
            }
        }
    }

    private void copyReceivableInfo(IRequest requestCtx, Long chanceId, HlsCusPrjProject prjProject) {
        HlsCreditLineReceivable hlsCreditLineReceivable = new HlsCreditLineReceivable();
        hlsCreditLineReceivable.setChanceId(chanceId);
        List<HlsCreditLineReceivable> factoringInfoReceivable = hlsCreditLineReceivableMapper.findFactoringInfo(hlsCreditLineReceivable);
        if (!factoringInfoReceivable.isEmpty()) {
            factoringInfoReceivable.forEach(v -> {
                HlsCusPrjProjectReceivable hlsCusPrjProjectReceivable = new HlsCusPrjProjectReceivable();
                BeanUtils.copyProperties(v, hlsCusPrjProjectReceivable);
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
                // 复制附件
                //获取原来的附件
                FndAttachmentMulti fndAttachmentMulti = new FndAttachmentMulti();
                fndAttachmentMulti.setTableName(HLS_CREDIT_LINE_ATTACH);
                fndAttachmentMulti.setTablePkValue(v.getChanceAttachmentId().toString());
                List<FndAttachmentMulti> fndAttachmentMultis = fndAttachmentMultiMapper.select(fndAttachmentMulti);
                if (!fndAttachmentMultis.isEmpty()){
                    fndAttachmentMultis.forEach(attachmentMulti -> {
                        FndAttachment fndAttachment = fndAttachmentMapper.selectByPrimaryKey(attachmentMulti.getAttachmentId());
                        if(fndAttachment==null){
                            return;
                        }
                        FndAttachment newAttachment = new FndAttachment();
                        BeanUtils.copyProperties(fndAttachment,newAttachment);
                        newAttachment.setAttachmentId(null);
                        fndAttachmentMapper.insertSelective(newAttachment);
                        FndAttachmentMulti newMulti = new FndAttachmentMulti();
                        BeanUtils.copyProperties(attachmentMulti,newMulti);
                        newMulti.setTablePkValue(hlsCusPrjProjectAttachment.getProjectAttachmentId().toString());
                        newMulti.setRecordId(null);
                        newMulti.setAttachmentId(newAttachment.getAttachmentId());
                        newMulti.setTableName(PRJ_PROJECT_ATTACHMENT);
                        fndAttachmentMultiMapper.insertSelective(newMulti);
                        newAttachment.setSourcePkValue(newMulti.getRecordId().toString());
                        fndAttachmentMapper.updateByPrimaryKeySelective(newAttachment);
                    });
                }
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
