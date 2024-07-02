package com.hand.hls.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChanceBp;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceBpMapper;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceBpService;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceService;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMeetingMapper;
import com.hand.hls.prj.mapper.ProjectApprovalConditionMapper;
import com.hand.hls.prj.mapper.ProjectMeetingApproverMapper;
import com.hand.hls.prj.service.*;
import lombok.SneakyThrows;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by xuju on 2018/04/19.
 * modify by xuju on 2018/05/22
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjProjectSubmitServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private ProjectMeetingApproverMapper approverMapper;
    @Autowired
    private IProjectMeetingApproverService approverService;

    @Autowired
    HlsCreditPlanService hlsCreditPlanService;

    @Autowired
    private HlsCusHlsCreditLineChanceBpMapper hlsCusHlsCreditLineChanceBpMapper;

    @Autowired
    HlsCusHlsCreditLineChanceBpService hlsCusHlsCreditLineChanceBpService;

    @Autowired
    private HlsCusPrjProjectBpService hlsCusPrjProjectBpService;

    @Autowired
    HlsCusHlsCreditLineChanceService hlsCusHlsCreditLineChanceService;

    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;
    @Autowired
    private HlsCusPrjQuotationService hlsCusPrjQuotationService;

    @Autowired
    private IProjectApprovalService projectApprovalService;

    private static final String END_APPROVE = "sid-NKAjjjAD-Blfw-48iN-8aby-1oa3KFZUaYB7";
    private static final String END_VETO = "sid-2hT8b4MS-cggJ-43ga-8vig-D1tjDZnhWks6";
    @Autowired
    private HlsCusPrjProjectMeetingMapper meetingMapper;
    @Autowired
    private ProjectApprovalConditionMapper conditionMapper;
    @Autowired
    private ProjectApprovalConditionService conditionService;

    public HlsCusPrjProjectSubmitServiceTask() {
    }

    @SneakyThrows
    @Override
    public void execute(DelegateExecution delegateExecution) {

        String flag;
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String hlsCusPrjProjectPrams = (String) delegateExecution.getVariable("hlsCusPrjProject");
        HlsCusPrjProject hlsCusPrjProject = JSON.parseObject(hlsCusPrjProjectPrams, HlsCusPrjProject.class);
        HlsCusPrjProject resultHlsCusPrjProject = new HlsCusPrjProject();
        Double financeAmount = (Double) delegateExecution.getVariable("financeAmount");
        System.out.println(financeAmount);
        //根据状态修改立项信息
        resultHlsCusPrjProject.setProjectId(hlsCusPrjProject.getProjectId());
        IRequest request = RequestHelper.getCurrentRequest(true);
        request.setAttribute("wflRuleControlFlag", "Y");
        resultHlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(request, resultHlsCusPrjProject);
        //databaseLockProvider.lock(resultHlsCusPrjProject);

        //更新项目流程结束标志
        String instanceEndFlag = "Y";
        resultHlsCusPrjProject.setInstanceEndFlag(instanceEndFlag);
        hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, resultHlsCusPrjProject);
        if ("APPROVING".equalsIgnoreCase(resultHlsCusPrjProject.getProjectStatus())) {
            if ("APPROVED".equalsIgnoreCase(result)) {
                flag = "APPROVED";
                resultHlsCusPrjProject.setProjectStatus(flag);
                //结束节点不更新项目状态
                hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, resultHlsCusPrjProject);
                if("N".equals(resultHlsCusPrjProject.getCreditFlag())){
                    //项目经营班子报价和上会信息 更新到项目上
                    HlsCusPrjQuotation quotationNormal = new HlsCusPrjQuotation();
                    quotationNormal.setSourceDocumentId(resultHlsCusPrjProject.getProjectId());
                    quotationNormal.setSourceDocumentCategory("PRJ_PROJECT");
                    quotationNormal.setDataClass("PRJ_PROJECT_INVEST");
                    quotationNormal = hlsCusPrjQuotationService.selectSelective(requestCtx,quotationNormal).get(0);
                    //经营班子报价
                    HlsCusPrjQuotation quotationManage = new HlsCusPrjQuotation();
                    quotationManage.setSourceDocumentId(resultHlsCusPrjProject.getProjectId());
                    quotationManage.setSourceDocumentCategory("PRJ_PROJECT_MANAGE");
                    quotationManage.setDataClass("PRJ_PROJECT_INVEST");
                    quotationManage = hlsCusPrjQuotationService.selectSelective(requestCtx,quotationManage).get(0);

                    quotationManage.setQuotationId(quotationNormal.getQuotationId());
                    quotationManage.setSourceDocumentId(resultHlsCusPrjProject.getProjectId());
                    quotationManage.setSourceDocumentCategory("PRJ_PROJECT");
                    hlsCusPrjQuotationService.updateByPrimaryKeySelective(request, quotationManage);

                    PrjProjectApproval approvalNormal = new PrjProjectApproval();
                    approvalNormal.setProjectId(hlsCusPrjProject.getProjectId().toString());
                    approvalNormal.setApprovalType("NORMAL");
                    approvalNormal = projectApprovalService.selectSelective(requestCtx,approvalNormal).get(0);
                    //经营班子 上会
                    PrjProjectApproval approvalManage = new PrjProjectApproval();
                    approvalManage.setProjectId(hlsCusPrjProject.getProjectId().toString());
                    approvalManage.setApprovalType("MANAGE");
                    approvalManage = projectApprovalService.selectSelective(requestCtx,approvalManage).get(0);
                    Long manageApprovalId = approvalManage.getApprovalId();
                    approvalManage.setApprovalId(approvalNormal.getApprovalId());
                    approvalManage.setApprovalType("NORMAL");
                    projectApprovalService.updateByPrimaryKeySelective(request, approvalManage);
                    //end
                    // 3、复制投放前提条件、投后管理要求
                    ProjectApprovalCondition manageCondition = new ProjectApprovalCondition();
                    manageCondition.setApprovalId(manageApprovalId);
                    List<ProjectApprovalCondition> manageConditions = conditionMapper.select(manageCondition);

                    ProjectApprovalCondition normalCondition = new ProjectApprovalCondition();
                    normalCondition.setApprovalId(approvalNormal.getApprovalId());
                    List<ProjectApprovalCondition> normalConditions = conditionMapper.select(normalCondition);
                    conditionService.batchDelete(normalConditions);

                    for (ProjectApprovalCondition approvalCondition : manageConditions) {
                        approvalCondition.setApprovalId(approvalNormal.getApprovalId());
                        conditionService.insertSelective(requestCtx, approvalCondition);
                    }
                }
                //项目上会更新
                HlsCusPrjProjectMeeting meeting = new HlsCusPrjProjectMeeting();
                meeting.setProjectId(resultHlsCusPrjProject.getProjectId());
                List<HlsCusPrjProjectMeeting> meetingNormalList = meetingMapper.queryInfo(meeting);
                if (meetingNormalList.size() > 0) {
                    for (HlsCusPrjProjectMeeting meeting1 : meetingNormalList) {
                        //上会审批人员更新
                        ProjectMeetingApprover approver = new ProjectMeetingApprover();
                        approver.setProjectId(resultHlsCusPrjProject.getProjectId());
                        List<ProjectMeetingApprover> approverNormalList = approverMapper.queryAllByProjectId(approver);
                        if (approverNormalList.size() > 0) {
                            for (ProjectMeetingApprover approver1 : approverNormalList) {
                                approver1.setProjectMeetingId(meeting1.getProjectMeetingId());
                                approverService.updateByPrimaryKeySelective(request, approver1);
                            }
                        }
                    }
                }

                // 如果是授信项目,把授信方案与客户信息同步到立项
                if("Y".equals(resultHlsCusPrjProject.getCreditFlag())){
                    Long chanceId = resultHlsCusPrjProject.getChanceId();
                    Long projectId = resultHlsCusPrjProject.getProjectId();
                    //获取尽调授信方案
                    HlsCreditPlan hcpPrj = new HlsCreditPlan();
                    hcpPrj.setSourceDocumentId(projectId);
                    hcpPrj.setSourceDocumentCategory("PRJ_PROJECT");
                    List<HlsCreditPlan> hcpPrjSelect = hlsCreditPlanService.select(request, hcpPrj, 1, 999);
                    if(hcpPrjSelect.size() > 0){
                        hcpPrj = hcpPrjSelect.get(0);
                    }
                    //同步到立项授信方案(update)
                    HlsCreditPlan hcpChance = new HlsCreditPlan();
                    hcpChance.setSourceDocumentId(chanceId);
                    hcpChance.setSourceDocumentCategory("HLS_CREDIT_LINE_CHANCE");
                    List<HlsCreditPlan> hcpChanceSelect = hlsCreditPlanService.select(request, hcpChance, 1, 999);
                    if(hcpChanceSelect.size() > 0){
                        hcpChance = hcpChanceSelect.get(0);
                    }
                    hcpPrj.setCreditPlanId(hcpChance.getCreditPlanId());
                    hcpPrj.setSourceDocumentId(chanceId);
                    hcpPrj.setSourceDocumentCategory("HLS_CREDIT_LINE_CHANCE");
                    hlsCreditPlanService.updateByPrimaryKeySelective(request, hcpPrj);

                    //获取尽调客户信息
                    HlsCusPrjProjectBp hcppb = new HlsCusPrjProjectBp();
                    hcppb.setProjectId(projectId);
                    List<HlsCusPrjProjectBp> hcppbSelect = hlsCusPrjProjectBpService.select(request, hcppb, 1, 999);
                    //同步到立项客户信息(删除后新增)
                    HlsCusHlsCreditLineChanceBp hclcb = new HlsCusHlsCreditLineChanceBp();
                    hclcb.setChanceId(chanceId);
                    hlsCusHlsCreditLineChanceBpMapper.delete(hclcb);
                    for (HlsCusPrjProjectBp hlsCusPrjProjectBp : hcppbSelect) {
                        HlsCusHlsCreditLineChanceBp record = new HlsCusHlsCreditLineChanceBp();
                        Map<String, String> map2 = hlsBeanRefUtilService.getFieldValueMap(hlsCusPrjProjectBp);
                        hlsBeanRefUtilService.setFieldValue(record , map2);
                        record.setChanceId(chanceId);
                        hlsCusHlsCreditLineChanceBpService.insertSelective(request, record);
                    }

                    //复制一份立项数据作为授信  不这样实现，一开始是因为授信管理字段与立项字段一致才做了一套重复的，现在不用
                    /*HlsCusHlsCreditLineChance hlsCusChance = new HlsCusHlsCreditLineChance();
                    hlsCusChance.setChanceId(chanceId);
                    hlsCusChance = hlsCusHlsCreditLineChanceService.selectByPrimaryKey(request, hlsCusChance);
                    HlsCusHlsCreditLineChance prjChance = new HlsCusHlsCreditLineChance();
                    Map<String, String> map1 = hlsBeanRefUtilService.getFieldValueMap(hlsCusChance);
                    hlsBeanRefUtilService.setFieldValue(prjChance, map1);
                    prjChance.setRefChanceId(chanceId);
                    prjChance.setChanceId(null);
                    prjChance.setDataType("NORMAL");
                    prjChance.setCreditLineStatus("APPROVED");
                    prjChance.setChanceType("CREDIT");
                    prjChance.setJdProjectId(projectId);
                    prjChance = hlsCusHlsCreditLineChanceService.insertSelective(request, prjChance);
                    hlsCusHlsCreditLineChanceService.chanceBackUp(request, prjChance);*/
                }

            } else if ("SUSPEND".equalsIgnoreCase(result)) {
                flag = "SUSPEND";
                resultHlsCusPrjProject.setProjectStatus(flag);
                resultHlsCusPrjProject.setSuspendFlag("Y");

                //复议设置复议时间, 复议次数
                resultHlsCusPrjProject.setReturnDate(new Date());
                if(resultHlsCusPrjProject.getReturnTimes() == null){
                    resultHlsCusPrjProject.setReturnTimes(1);
                }else{
                    resultHlsCusPrjProject.setReturnTimes(resultHlsCusPrjProject.getReturnTimes() + 1);
                }

                //结束节点不更新项目状态
                hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, resultHlsCusPrjProject);
            } else if ("REJECTED".equalsIgnoreCase(result)) {
                flag = "REJECTED";
                resultHlsCusPrjProject.setProjectStatus(flag);
                //结束节点不更新项目状态
                hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, resultHlsCusPrjProject);
            }else if ("APPROVED_RETURN".equalsIgnoreCase(result)) {
                flag = "APPROVED_RETURN";
                resultHlsCusPrjProject.setProjectStatus(flag);
                //结束节点不更新项目状态
                hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, resultHlsCusPrjProject);
            }
        }


    }

}
