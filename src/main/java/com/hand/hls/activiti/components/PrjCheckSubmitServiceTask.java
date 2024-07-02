package com.hand.hls.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.activiti.dto.HlsCusProcess;
import com.hand.hls.ast.dto.AstFcEstimate;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.mapper.FndAttachmentMapper;
import com.hand.hls.atm.mapper.FndAttachmentMultiMapper;
import com.hand.hls.bill.dto.hlsBillRequest;
import com.hand.hls.bill.mapper.hlsBillRequestMapper;
import com.hand.hls.bill.service.IhlsBillRequestService;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceService;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.hn.dto.PrjCheck;
import com.hand.hls.hn.dto.PrjCheckPlan;
import com.hand.hls.hn.dto.PrjLeaseInspect;
import com.hand.hls.hn.mapper.PrjCheckMapper;
import com.hand.hls.hn.mapper.PrjCheckPlanMapper;
import com.hand.hls.hn.service.IPrjCheckService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.risk.dto.RiskAttachment;
import com.hand.hls.risk.mapper.RiskAttachmentMapper;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
@Transactional(rollbackFor = Exception.class)
public class PrjCheckSubmitServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private PrjCheckMapper prjCheckMapper;
    @Autowired
    private PrjCheckPlanMapper prjCheckPlanMapper;
    public static final String APPROVED = "APPROVED";
    public static final String REJECTED = "REJECTED";
    public static final String APPROVED_RETURN = "APPROVED_RETURN";
    @Autowired
    private IPrjCheckService prjCheckService;
    @Autowired
    private HlsCusPrjProjectMapper prjProjectMapper;
    @Autowired
    FndCodingRuleValuesService codingRuleValuesService;
    @Autowired
    private RiskAttachmentMapper riskAttachmentMapper;
    @Autowired
    private FndAttachmentMultiMapper fndAttachmentMultiMapper;
    @Autowired
    private FndAttachmentMapper fndAttachmentMapper;
    public PrjCheckSubmitServiceTask() {
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long checkId = Long.valueOf(delegateExecution.getProcessInstanceBusinessKey());

        PrjCheck prjCheck = prjCheckMapper.selectByPrimaryKey(checkId);

        String processInstanceId = delegateExecution.getProcessInstanceId();
        if (processInstanceId != null) {
            prjCheck.setProcessInstanceId(Long.valueOf(processInstanceId));
        }

        if (APPROVED.equalsIgnoreCase(result)) {
            prjCheck.setApproveSuggest("APPROVED");
            copyPrjCheck(requestCtx,prjCheck);
        } else if (REJECTED.equalsIgnoreCase(result)) {
            prjCheck.setApproveSuggest("REJECTED");
        } else if (APPROVED_RETURN.equalsIgnoreCase(result)) {
            prjCheck.setApproveSuggest(APPROVED_RETURN);
        }
        prjCheckService.updateByPrimaryKeySelective(requestCtx, prjCheck);
        // 复制其他租后检查记录
    }

    private void copyPrjCheck(IRequest requestCtx,PrjCheck prjCheck){
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setCheckId(prjCheck.getCheckId());
        List<HlsCusPrjProject> prjProjects = prjProjectMapper.prjCheckContract(hlsCusPrjProject);
        PrjCheckPlan prjCheckPlan = prjCheckPlanMapper.selectByPrimaryKey(prjCheck.getPlanId());
        List<RiskAttachment> riskAttachments = riskAttachmentMapper.queryPrjCheckAttachment(prjCheck);
        boolean attachmentFlag = CollectionUtils.isNotEmpty(riskAttachments);
        // 数据不全跳过
        if(CollectionUtils.isEmpty(prjProjects) || prjCheckPlan == null){
            return;
        }
        for(HlsCusPrjProject prjProject : prjProjects){
            if(prjCheck.getContractId().equals(prjProject.getProjectId())){
                continue;
            }
            // 查找合同对应的检查计划
            PrjCheckPlan newPlan = new PrjCheckPlan();
            newPlan.setPlanCheckDate(prjCheckPlan.getPlanCheckDate());
            newPlan.setContractId(prjProject.getProjectId());
            List<PrjCheckPlan> plans = prjCheckPlanMapper.select(newPlan);
            if(CollectionUtils.isEmpty(plans)){
                return;
            }
            // copy出新的检查
            PrjCheck newCheck = new PrjCheck();
            BeanUtils.copyProperties(prjCheck,newCheck);
            newCheck.setCheckId(null);
            newCheck.setContractId(prjProject.getProjectId());
            newCheck.setPlanId(plans.get(0).getPlanId());
            String checkNumber=codingRuleValuesService.getCodeRuleValue(requestCtx, "RENT_CHECK","RENT_CHECK", "RENT_CHECK", null);
            newCheck.setCheckNumber(checkNumber);
            newCheck.setManualFlag(0L);
            prjCheckMapper.insertSelective(newCheck);
            // copy检查附件
            if(attachmentFlag){
                riskAttachments.forEach(riskAttachment -> {
                    RiskAttachment newRiskAttachment = new RiskAttachment();
                    BeanUtils.copyProperties(riskAttachment,newRiskAttachment);
                    newRiskAttachment.setCheckId(newCheck.getCheckId());
                    newRiskAttachment.setRiskAttachmentId(null);
                    riskAttachmentMapper.insertSelective(newRiskAttachment);
                    // 复制附件
                    FndAttachmentMulti fndAttachmentMulti = new FndAttachmentMulti();
                    fndAttachmentMulti.setTableName("prj_check");
                    fndAttachmentMulti.setTablePkValue(riskAttachment.getRiskAttachmentId().toString());
                    List<FndAttachmentMulti> fndAttachmentMultis = fndAttachmentMultiMapper.select(fndAttachmentMulti);
                    if(CollectionUtils.isNotEmpty(fndAttachmentMultis)){
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
                            newMulti.setTablePkValue(newRiskAttachment.getRiskAttachmentId().toString());
                            newMulti.setRecordId(null);
                            newMulti.setAttachmentId(newAttachment.getAttachmentId());
                            fndAttachmentMultiMapper.insertSelective(newMulti);
                            newAttachment.setSourcePkValue(newMulti.getRecordId().toString());
                            fndAttachmentMapper.updateByPrimaryKeySelective(newAttachment);
                        });
                    }
                });
            }
        }
    }

}
