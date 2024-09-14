package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.mapper.FndAttachmentMapper;
import com.hand.hls.atm.mapper.FndAttachmentMultiMapper;
import com.hand.hls.fct.dto.*;
import com.hand.hls.fct.mapper.HlsChanceBusinessAccessCompareMapper;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceAttachMapper;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceBpMapper;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceService;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.mapper.*;
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
public class HlsCreditTerminateServiceTask implements JavaDelegate, IActivitiBean {

    private static final String APPROVED = "APPROVED";

    private static final String REJECTED = "REJECTED";

    //点对点提交
    private static final String PEER_REJECTED = "PEER_REJECTED";

    //移交
    private static final String DELEGATE = "DELEGATE";

    //终止
    private static final String TERMINATE = "TERMINATE";

    @Autowired
    private HlsCusHlsCreditLineChanceService hlsCusHlsCreditLineChanceService;


    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;
    @Autowired
    private HlsCusHlsCreditLineChanceBpMapper hlsCusHlsCreditLineChanceBpMapper;
    @Autowired
    private HlsCusPrjProjectBpMapper hlsCusPrjProjectBpMapper;
    @Autowired
    private HlsCreditPlanMapper hlsCreditPlanMapper;
    @Autowired
    private HlsChanceBusinessAccessCompareMapper hlsChanceBusinessAccessCompareMapper;
    @Autowired
    private HlsCusPrjBusinessAccessCompareMapper hlsCusPrjBusinessAccessCompareMapper;
    @Autowired
    private HlsCusHlsCreditLineChanceAttachMapper hlsCusHlsCreditLineChanceAttachMapper;
    @Autowired
    private HlsCusPrjProjectAttachmentMapper hlsCusPrjProjectAttachmentMapper;
    @Autowired
    private FndAttachmentMultiMapper fndAttachmentMultiMapper;
    @Autowired
    private FndAttachmentMapper fndAttachmentMapper;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long chanceId = Long.parseLong(delegateExecution.getProcessInstanceBusinessKey());
        HlsCusHlsCreditLineChance chance = new HlsCusHlsCreditLineChance();
        chance.setChanceId(chanceId);
        chance = hlsCusHlsCreditLineChanceService.selectByPrimaryKey(requestCtx, chance);
        if (isValid(chance)) {
            if (APPROVED.equalsIgnoreCase(result)) {
                createProjectData(chance);
                chance.setCreditLineStatus(APPROVED);
                chance.setApprovedDate(new Date());
            } else if (REJECTED.equalsIgnoreCase(result)) {
                chance.setCreditLineStatus(TERMINATE);
            } else if (PEER_REJECTED.equalsIgnoreCase(chance.getCreditLineStatus())) {
                chance.setCreditLineStatus(PEER_REJECTED);
            } else if (DELEGATE.equalsIgnoreCase(chance.getCreditLineStatus())) {
                chance.setCreditLineStatus(DELEGATE);
            }
        }
        hlsCusHlsCreditLineChanceService.updateByPrimaryKeySelective(requestCtx, chance);

    }

    private void createProjectData(HlsCusHlsCreditLineChance chance) {
        // 项目基本信息
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        BeanUtils.copyProperties(chance, prjProject);
        prjProject.setTenantId(chance.getBpId());
        prjProject.setLeaseItemAmount(chance.getCreditLineAmt());
        prjProject.setProjectStatus("NEW");
        prjProject.setProjectName(chance.getCreditLineName());
        prjProject.setProjectNumber(chance.getCreditLineNumber());
        prjProject.setDocumentCategory("PRJ_PROJECT");
        prjProject.setDocumentType("CREDIT");
        prjProject.setHostProjectManager(chance.getProposerEmployeeId());
        prjProject.setAssistProjectManager(chance.getProjectAssistant());
        hlsCusPrjProjectMapper.insertSelective(prjProject);
        // 复制客户信息
        HlsCusHlsCreditLineChanceBp hlsCusHlsCreditLineChanceBp = new HlsCusHlsCreditLineChanceBp();
        hlsCusHlsCreditLineChanceBp.setChanceId(chance.getChanceId());
        List<HlsCusHlsCreditLineChanceBp> bpList = hlsCusHlsCreditLineChanceBpMapper.select(hlsCusHlsCreditLineChanceBp);
        bpList.forEach(v -> {
            HlsCusPrjProjectBp hlsCusPrjProjectBp = new HlsCusPrjProjectBp();
            BeanUtils.copyProperties(v, hlsCusPrjProjectBp);
            hlsCusPrjProjectBp.setDescription(v.getNote());
            hlsCusPrjProjectBp.setProjectId(prjProject.getProjectId());
            hlsCusPrjProjectBpMapper.insertSelective(hlsCusPrjProjectBp);
        });
        // 复制授信方案
        HlsCreditPlan hlsCreditPlan = new HlsCreditPlan();
        hlsCreditPlan.setSourceDocumentCategory("HLS_CREDIT_LINE_CHANCE");
        hlsCreditPlan.setSourceDocumentId(chance.getChanceId());
        List<HlsCreditPlan> planList = hlsCreditPlanMapper.select(hlsCreditPlan);
        planList.forEach(v -> {
            HlsCreditPlan newHlsCreditPlan = new HlsCreditPlan();
            BeanUtils.copyProperties(v, newHlsCreditPlan);
            newHlsCreditPlan.setSourceDocumentCategory("PRJ_PROJECT");
            newHlsCreditPlan.setSourceDocumentId(prjProject.getProjectId());
            hlsCreditPlanMapper.insertSelective(newHlsCreditPlan);
        });
        // 复制业务准入审核
        HlsChanceBusinessAccessCompare hlsChanceBusinessAccessCompare = new HlsChanceBusinessAccessCompare();
        hlsChanceBusinessAccessCompare.setChanceId(chance.getChanceId());
        List<HlsChanceBusinessAccessCompare> compareList = hlsChanceBusinessAccessCompareMapper.select(hlsChanceBusinessAccessCompare);
        compareList.forEach(v -> {
            HlsCusPrjBusinessAccessCompare hlsCusPrjBusinessAccessCompare = new HlsCusPrjBusinessAccessCompare();
            BeanUtils.copyProperties(v, hlsCusPrjBusinessAccessCompare);
            hlsCusPrjBusinessAccessCompare.setProjectId(prjProject.getProjectId());
            hlsCusPrjBusinessAccessCompareMapper.insertSelective(hlsCusPrjBusinessAccessCompare);
        });
        // 复制附件
        HlsCusHlsCreditLineChanceAttach hlsCreditLineAttach = new HlsCusHlsCreditLineChanceAttach();
        hlsCreditLineAttach.setChanceId(chance.getChanceId());
        List<HlsCusHlsCreditLineChanceAttach> attachList = hlsCusHlsCreditLineChanceAttachMapper.select(hlsCreditLineAttach);
        attachList.forEach(v -> {
            HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment = new HlsCusPrjProjectAttachment();
            BeanUtils.copyProperties(v, hlsCusPrjProjectAttachment);
            hlsCusPrjProjectAttachment.setProjectId(prjProject.getProjectId());
            hlsCusPrjProjectAttachmentMapper.insertSelective(hlsCusPrjProjectAttachment);
            //基础附件表
            FndAttachmentMulti fndAttachmentMulti = new FndAttachmentMulti();
            fndAttachmentMulti.setTableName("HLS_CREDIT_LINE_ATTACH");
            fndAttachmentMulti.setTablePkValue(v.getChanceAttachmentId().toString());
            List<FndAttachmentMulti> fndAttachmentMultis = fndAttachmentMultiMapper.select(fndAttachmentMulti);
            fndAttachmentMultis.forEach(attachmentMulti -> {
                FndAttachment fndAttachment = fndAttachmentMapper.selectByPrimaryKey(attachmentMulti.getAttachmentId());

                FndAttachment newAttachment = new FndAttachment();
                BeanUtils.copyProperties(fndAttachment,newAttachment);
                fndAttachmentMapper.insertSelective(newAttachment);

                FndAttachmentMulti newMulti = new FndAttachmentMulti();
                BeanUtils.copyProperties(attachmentMulti,newMulti);
                newMulti.setTableName("PRJ_PROJECT_ATTACHMENT");
                newMulti.setTablePkValue(hlsCusPrjProjectAttachment.getProjectAttachmentId().toString());
                newMulti.setAttachmentId(newAttachment.getAttachmentId());
                fndAttachmentMultiMapper.insertSelective(newMulti);

                newAttachment.setSourcePkValue(newMulti.getRecordId().toString());
                fndAttachmentMapper.updateByPrimaryKeySelective(newAttachment);
            });
        });
    }



    private boolean isValid(HlsCusHlsCreditLineChance chance) {
        return !APPROVED.equalsIgnoreCase(chance.getCreditLineStatus()) &&
                !REJECTED.equalsIgnoreCase(chance.getCreditLineStatus()) &&
                !PEER_REJECTED.equalsIgnoreCase(chance.getCreditLineStatus()) &&
                !DELEGATE.equalsIgnoreCase(chance.getCreditLineStatus());
    }

}
