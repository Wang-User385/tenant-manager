package com.hand.hls.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Eugene Song
 * @Date: 2020/5/21
 * @Description: 尽调审查当前进度 更新
 * @Purpose:
 **/
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjProjectReviewStatusServiceTask implements JavaDelegate, IActivitiBean {

    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest iRequest = (IRequest) delegateExecution.getVariable("iRequest");
        String projectId = String.valueOf(delegateExecution.getVariable("projectId"));

        String currentActivityId = delegateExecution.getCurrentActivityId();

        Map<String, String> reviewStatusMap = new HashMap<>(15);
        /**
         * 1.0尚未受理
         * 1.1初审阶段-尚未发出问题反馈
         * 1.2初审阶段-已提问题反馈，等待业务部门回复
         * 1.3初审阶段-收到业务部门回复、正在审查中
         * 1.4初审阶段-收到初审问题最终回复、报告撰写中
         * 2.1复审阶段-审核中
         * 2.2复审阶段-反馈复审问题
         * 2.3部门负责人审核阶段-审核中
         * 3.1业审委及总裁审批中
         * 4.1审批完结-总裁已批
         * 4.2审批完结-业审委已批
         * 4.3审批完结-风险部退卷
         * 4.4审批完结-风险部否决、暂缓
         * 4.5审批完结-业审委退卷、否决、暂缓
         * 4.6审批完结-总裁退卷、否决、暂缓
         * 4.7审批完结-业务部门撤卷
         */
        reviewStatusMap.put("sid-zQiEsIZx-KZuf-4PdM-8HvG-HyNpdWqTBA1f", "110");
        reviewStatusMap.put("sid-QcrMP2VI-Inhc-4Rmf-8mWS-b0buyu5RxL4F", "120");
        reviewStatusMap.put("sid-1llamIoj-2SKg-4LXz-8THM-HTipzmc9endG", "130");
        reviewStatusMap.put("", "140");
        reviewStatusMap.put("sid-YdGQBb95-3h6Q-4VQt-81Rh-AIYJ2ozsMaIj", "210");
//        reviewStatusMap.put("sid-7WWngGGG-xePO-4yee-8Xyr-sXfx22Ld8yS0", "210");
        reviewStatusMap.put("sid-o8XfhJGG-RDzo-4tAV-9rKs-yOqUJX6GDz00", "220");
        reviewStatusMap.put("sid-8X0aBxKG-lbzW-4gNV-8lei-2q87v5Vd7D0w", "230");
        reviewStatusMap.put("sid-RV5751FD-MMIg-45XI-8gZD-9SvEf1r1DKu1", "240");
        reviewStatusMap.put("sid-LM5e46bO-kBao-4zCC-8D5g-t3a8YLlBAOB9", "310");
        reviewStatusMap.put("sid-hVDcPGYI-4ts2-4JED-8npQ-g0MYA5vcEfVL", "410");
        reviewStatusMap.put("sid-XbXV60dF-f0WC-4z3B-81f6-9gu9KFLbBXnv", "420");
        reviewStatusMap.put("", "430");
        reviewStatusMap.put("sid-cjPI4PMk-qc4D-4P9x-8EIo-mFJRd4eN482g", "440");
        reviewStatusMap.put("sid-cjPI4PMk-qc4D-4P9x-8EIo-mFJRd4eN481g", "440");
        reviewStatusMap.put("sid-0Mp85sRp-sqd7-4TDo-85KA-3dpezBYpBpdK", "450");
        reviewStatusMap.put("sid-6yGzj2lJ-pNeH-4fOp-8JOB-CMkT7Q6LuSuf", "450");
        reviewStatusMap.put("sid-OxmOyerH-6Vwj-4ouX-B7cE-gUJCKcT876x5", "460");
        reviewStatusMap.put("sid-FxXY3VPJ-2dJT-4kKF-80k9-2m8wsMTA8PbQ", "460");
        reviewStatusMap.put("", "470");

        String reviewStatus = "100";
        for (String activityId : reviewStatusMap.keySet()) {
            if (currentActivityId.equalsIgnoreCase(activityId)) {
                reviewStatus = reviewStatusMap.get(activityId);
                HlsCusPrjProject prjProject = new HlsCusPrjProject();
                prjProject.setProjectId(Long.parseLong(projectId));
                prjProject.setReviewStatus(reviewStatus);
                hlsCusPrjProjectService.updateByPrimaryKeySelective(iRequest, prjProject);
            }
        }
    }
}
