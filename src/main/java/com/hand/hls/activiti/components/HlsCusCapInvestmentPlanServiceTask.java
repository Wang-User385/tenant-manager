package com.hand.hls.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.cap.dto.HlsCusCapitalInvestmentPlanHd;
import com.hand.hls.cap.service.HlsCusCapitalInvestmentPlanHdService;
import hls.core.sys.event.service.SysEventService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Robert8900
 * @Date: 2020/2/14 15:13
 * @Description:
 * @Purpose:
 **/

@Component
public class HlsCusCapInvestmentPlanServiceTask implements JavaDelegate, IActivitiBean {
    public static final String CAP_CAPITAL_INVESTMENT_PLAN_SUBMIT = "CAP_CAPITAL_INVESTMENT_PLAN.SUBMIT";
    @Autowired
    private HlsCusCapitalInvestmentPlanHdService hlsCusCapitalInvestmentPlanHdService;

    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Autowired
    private SysEventService sysEventService;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        String flag = null;
        String flagDesc = null;
        IRequest requestCtx = (IRequest)delegateExecution.getVariable("iRequest");
        String result = (String)delegateExecution.getVariable("approveResult");
        long planHeadId = (Long) delegateExecution.getVariable("business_key");
        HlsCusCapitalInvestmentPlanHd hlsCusCapitalInvestmentPlanHd = new HlsCusCapitalInvestmentPlanHd();
        hlsCusCapitalInvestmentPlanHd.setPlanHeadId(planHeadId);
        hlsCusCapitalInvestmentPlanHd = hlsCusCapitalInvestmentPlanHdService.selectByPrimaryKey(requestCtx, hlsCusCapitalInvestmentPlanHd);
        databaseLockProvider.lock(hlsCusCapitalInvestmentPlanHd);
        if ("APPROVED".equalsIgnoreCase(result)) {
            flag = "APPROVED";
            flagDesc = "通过";
            hlsCusCapitalInvestmentPlanHd.setApproveStatus("APPROVED");
            hlsCusCapitalInvestmentPlanHdService.updateByPrimaryKeySelective(requestCtx, hlsCusCapitalInvestmentPlanHd);
        } else {
            flag = "REJECT";
            flagDesc = "拒绝";
            hlsCusCapitalInvestmentPlanHd.setApproveStatus("REJECT");
            hlsCusCapitalInvestmentPlanHdService.updateByPrimaryKeySelective(requestCtx, hlsCusCapitalInvestmentPlanHd);
        }


        Map<String, Object> evenParams = new HashMap();
        evenParams.put("message", "投放计划编号为" + hlsCusCapitalInvestmentPlanHd.getPlanNumber() + "的审批" + flagDesc);
        evenParams.put("noticeTitle", "合同文本审批");
        evenParams.put("url", "");
        evenParams.put("level", 1L);
        evenParams.put("noticeType", "NOTICE");
        sysEventService.eventSave(requestCtx, hlsCusCapitalInvestmentPlanHd.getPlanHeadId(), hlsCusCapitalInvestmentPlanHd.getDocumentCategory(), hlsCusCapitalInvestmentPlanHd.getDocumentType(), "CAP", CAP_CAPITAL_INVESTMENT_PLAN_SUBMIT, "P2D", evenParams);
    }
}
