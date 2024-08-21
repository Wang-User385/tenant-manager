package com.hand.hls.activiti.service.impl;


import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hls.GENER.dto.HlsGeneralIssue;
import com.hand.hls.GENER.mapper.HlsGeneralIssueMapper;
import com.hand.hls.activiti.dto.HlsCusActAssigneeNode;
import com.hand.hls.activiti.dto.HlsCusActOnlineMeetingMember;
import com.hand.hls.activiti.mapper.HlsCusActOnlineMeetingMemberMapper;
import com.hand.hls.activiti.service.HlsCusActAssigneeNodeService;
import com.hand.hls.activiti.service.HlsCusActOnlineMeetingMemberService;
import com.hand.hls.activiti.service.HlsCusActivitiEntityService;
import com.hand.hls.bill.dto.hlsBillRequest;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceMapper;
import com.hand.hls.fnd.dto.HlsCusEmployee;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.dto.PrjMeetingJudge;
import com.hand.hls.fnd.mapper.HlsCusEmployeeMapper;
import com.hand.hls.fnd.mapper.PrjMeetingJudgeMapper;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.ProjectMeetingApprover;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.mapper.ProjectMeetingApproverMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.rw.dto.HlsCusRiskWarning;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.mapper.FndEmployeeMapper;
import com.hand.hls.sys.mapper.HlsCusSysAppointApproverMapper;
import com.hand.hls.sys.mapper.SysUserMapper;
import com.hand.hls.sys.service.SysUserService;
import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import  com.hand.hls.bill.mapper.hlsBillRequestMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * 工作流审批规则二开实现类
 * Created by qixiang.shao on 2017/11/3
 */
@Service
public class HlsCusActivitiEntityServiceImpl implements HlsCusActivitiEntityService, IActivitiBean {

    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;
    @Autowired
    private HlsCusEmployeeMapper hlsCusEmployeeMapper;
    @Autowired
    private HlsCusActAssigneeNodeService hlsCusActAssigneeNodeService;
    @Autowired
    private HlsCusActOnlineMeetingMemberService hlsCusActOnlineMeetingMemberService;
    @Autowired
    private HlsCusActOnlineMeetingMemberMapper hlsCusActOnlineMeetingMemberMapper;
    @Autowired
    private PrjMeetingJudgeMapper prjMeetingJudgeMapper;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private FndEmployeeMapper fndEmployeeMapper;
    @Autowired
    private hlsBillRequestMapper hlsBillRequestMapper;
    @Autowired
    private ProjectMeetingApproverMapper projectMeetingApproverMapper;
    @Autowired
    private HlsCusPrjProjectMapper prjProjectMapper;
    @Autowired
    private HlsCusPrjProjectService prjProjectService;
    @Autowired
    private HlsGeneralIssueMapper hlsgeneralissuemapper;
    @Autowired
    private HlsCusSysAppointApproverMapper hlsCusSysAppointApproverMapper;
    /*@Autowired
    private HlsCusFiveClassificationContractMapper fiveClassificationContractMapper;
    @Autowired
    private HlsCusRiskWarningMapper riskWarningMapper;
    @Autowired
    private HlsCusPostloanInspectionMapper postloanInspectionMapper;*/

    @Override
    public String getBeanName() {
        return "hlsCusEmpService";
    }

    /*多个项目经理取值 去重复*/
    @Override
    public List<String> getPrjAssistant(DelegateExecution delegateExecution){
        List<String> assistantString = new ArrayList();
        HlsCusPrjProject warning = new HlsCusPrjProject();
        String riskWarningId = delegateExecution.getVariable("riskWarningId").toString();
        String companyId = delegateExecution.getVariable("companyId").toString();
        warning.setRiskWarningId(Long.valueOf(riskWarningId));
        List<HlsCusPrjProject> hlsCusPrjProjects = new ArrayList<>();
        IRequest iRequest = RequestHelper.getCurrentRequest(true);
        iRequest.setAttribute("wflRuleControlFlag", "Y");
        hlsCusPrjProjects = prjProjectService.prjManager(iRequest,warning);
        if( hlsCusPrjProjects.size() > 0){
            for(HlsCusPrjProject prjProject : hlsCusPrjProjects){
                List<HlsEmployee> hlsEmployeeList = hlsCusEmployeeMapper.selectProjectAssistant(Long.parseLong(companyId), prjProject.getHostProjectManager());
                for (HlsEmployee dt : hlsEmployeeList) {
                    assistantString.add(dt.getName());
                }
            }
        }else{
            assistantString.add("1375");
        }
        return assistantString;
    }

    /*多个项目经理 部门负责人取值 去重复*/
    @Override
    public List<String> getPrjAssistantManager(DelegateExecution delegateExecution){
        List<String> assistantString = new ArrayList();
        HlsCusPrjProject warning = new HlsCusPrjProject();
        String riskWarningId = delegateExecution.getVariable("riskWarningId").toString();
        String companyId = delegateExecution.getVariable("companyId").toString();
        warning.setRiskWarningId(Long.valueOf(riskWarningId));
        List<HlsCusPrjProject> hlsCusPrjProjects = new ArrayList<>();
        IRequest iRequest = RequestHelper.getCurrentRequest(true);
        iRequest.setAttribute("wflRuleControlFlag", "Y");
        hlsCusPrjProjects = prjProjectService.prjManagers(iRequest,warning);
        if( hlsCusPrjProjects.size() > 0){
            for(HlsCusPrjProject prjProject : hlsCusPrjProjects){
                HlsCusEmployee hlsCusEmployee = new HlsCusEmployee();
                hlsCusEmployee.setUnitId(prjProject.getHostUnitId().toString());
                hlsCusEmployee.setCompanyId(Long.valueOf(companyId));
                List<HlsCusEmployee> hlsCusEmployees = hlsCusEmployeeMapper.queryUnitManagerByUnitIdNew(hlsCusEmployee);
                for (HlsCusEmployee dt : hlsCusEmployees) {
                    assistantString.add(dt.getUserName());
                }
            }
        }
        return assistantString;
    }

    /**
     * 协办
     *
     * @param delegateExecution
     * @return
     */
    @Override
    public List<String> getAssistantManager(DelegateExecution delegateExecution) {
        List<String> assistantString = new ArrayList();
        String companyId = delegateExecution.getVariable("companyId").toString();
       // String employeeAssistantAssignsId = delegateExecution.getVariable("employeeAssistantAssignsId").toString();
        //List<HlsEmployee> hlsEmployeeList = hlsCusEmployeeMapper.selectUserNameByEmployeeAssignId(Long.parseLong(companyId), Long.parseLong(employeeAssistantAssignsId));
        /*for (HlsEmployee dt : hlsEmployeeList) {
            assistantString.add(dt.getName());
        }*/
        return assistantString;
    }

    /*协办项目经理B 用于项目立项-注意拆入的参数 在HlsCusHlsCreditLineSubmitActivitiStartServiceImpl.java*/
    @Override
    public List<String> getProjectAssistant(DelegateExecution delegateExecution) {
        List<String> assistantString = new ArrayList();
        String companyId = delegateExecution.getVariable("companyId").toString();
        String projectAssistant = delegateExecution.getVariable("projectAssistant").toString();
        List<HlsEmployee> hlsEmployeeList = hlsCusEmployeeMapper.selectProjectAssistant(Long.parseLong(companyId), Long.parseLong(projectAssistant));
        for (HlsEmployee dt : hlsEmployeeList) {
            assistantString.add(dt.getName());
        }
        return assistantString;
    }

    /**
     * 主办
     *
     * @param delegateExecution
     * @return
     */
    @Override
    public List<String> getProjectManager(DelegateExecution delegateExecution) throws HlsCusException {
        List<String> assistantString = new ArrayList();
        String companyId = delegateExecution.getVariable("companyId").toString();
        String employeeAssistantAssignsId = delegateExecution.getVariable("employeeManagerAssignsId").toString();

        List<HlsEmployee> hlsEmployeeList = hlsCusEmployeeMapper.selectUserNameByEmployeeAssignId(Long.parseLong(companyId), Long.parseLong(employeeAssistantAssignsId));
        if (CollectionUtils.isEmpty(hlsEmployeeList)) {
            throw new HlsCusException("未找到对应的主办项目经理");
        }
        for (HlsEmployee dt : hlsEmployeeList) {
            assistantString.add(dt.getName());
        }
        return assistantString;
    }

    /**
     * 进件项目经理
     *
     * @param delegateExecution
     * @return
     */
    @Override
    public List<String> getProjectManager1(DelegateExecution delegateExecution) throws HlsCusException {
        List<String> assistantString = new ArrayList();
        String projectId = delegateExecution.getVariable("projectId").toString();
        HlsCusPrjProject hlsCusPrjProject=new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(Long.parseLong(projectId));
        hlsCusPrjProject=prjProjectMapper.selectByPrimaryKey(hlsCusPrjProject);
        HlsEmployee hlsEmployee=new HlsEmployee();
        hlsEmployee.setEmployeeId(hlsCusPrjProject.getEmployeeId());
        hlsEmployee=hlsCusEmployeeMapper.selectByPrimaryKey(hlsEmployee);
        SysUser sysUser=new SysUser();
        sysUser=sysUserMapper.queryUserByDescription(hlsEmployee.getName());
//        assistantString.add(sysUser.getUserId().toString());
//        assistantString.add(hlsCusPrjProject.getEmployeeId().toString());
//        Long employeeId=hlsEmployee.getEmployeeId();
        Long userId=sysUser.getUserId();
        Long companyId=hlsEmployee.getCompanyId();
        List<HlsEmployee> hlsEmployeeList = hlsCusEmployeeMapper.selectUserNameByEmployeeId(companyId, userId);
        if (CollectionUtils.isEmpty(hlsEmployeeList)) {
            throw new HlsCusException("未找到对应的主办项目经理");
        }
        for (HlsEmployee dt : hlsEmployeeList) {
            assistantString.add(dt.getName());
        }
//        assistantString.add("44");
//        if (CollectionUtils.isEmpty(hlsEmployeeList)) {
//            throw new HlsCusException("未找到对应的主办项目经理");
//        }
//        for (HlsEmployee dt : hlsEmployeeList) {
//            assistantString.add(dt.getName());
//        }
//        hlsEmployee=hlsCusEmployeeMapper.selectByPrimaryKey(hlsEmployee);
//        String employeeAssistantAssignsId=hlsCusEmployeeMapper.selectEmployeeAssignIdByEmployeeId(hlsEmployee);
//        assistantString.add(hlsEmployee.getName());
//        if (CollectionUtils.isEmpty(hlsEmployeeList)) {
//            throw new HlsCusException("未找到对应的主办项目经理");
//        }
//        for (HlsEmployee dt : hlsEmployeeList) {
//            assistantString.add(dt.getName());
//        }
        return assistantString;
    }

    /**
     * 获取业务协办经理/主办项目经理（协办或者主办）
     *
     * @param delegateExecution
     * @return
     */
    @Override
    public List<String> getEmployeeManager(DelegateExecution delegateExecution) {
        List<String> assistantString = new ArrayList();
        String companyId = delegateExecution.getVariable("companyId").toString();
        String employeeAssignsId = delegateExecution.getVariable("employeeAssignsId").toString();
        List<HlsEmployee> hlsEmployeeList = hlsCusEmployeeMapper.selectUserNameByEmployeeAssignId(Long.parseLong(companyId), Long.parseLong(employeeAssignsId));
        for (HlsEmployee dt : hlsEmployeeList) {
            assistantString.add(dt.getName());
        }
        return assistantString;
    }

    /**
     * 获取主办项目经理(贷后)
     *
     * @param delegateExecution
     * @return
     */
    @Override
    public List<String> getPlmPliProjectManager(DelegateExecution delegateExecution) {
        List<String> assistantString = new ArrayList();
        String companyId = delegateExecution.getVariable("companyId").toString();
        List<Long> employeeAssignIdList = JSON.parseArray(delegateExecution.getVariable("employeeAssignIdList").toString(), Long.class);
        List<HlsEmployee> hlsEmployeeList = hlsCusEmployeeMapper.selectUserNameByEmployeeAssignIdList(Long.parseLong(companyId), employeeAssignIdList);
        for (HlsEmployee dt : hlsEmployeeList) {
            assistantString.add(dt.getName());
        }
        return assistantString;
    }

    /**
     * 获取指定的信用审查人
     *
     * @param delegateExecution
     * @return
     */
    @Override
    public String getCreditDesignatedManager(DelegateExecution delegateExecution) {
        String assigneeString = null;
        String assigneeType = "CREDIT";//经办人类型
        Long processInstanceId = Long.parseLong(delegateExecution.getProcessInstanceId());
        Long projectId = Long.parseLong(delegateExecution.getVariable("projectId").toString());
        String projectDocumentCategory = (String) delegateExecution.getVariable("projectDocumentCategory");
        HlsCusActAssigneeNode hlsCusActAssigneeNode = new HlsCusActAssigneeNode();
        hlsCusActAssigneeNode.setAssigneeType(assigneeType);
        hlsCusActAssigneeNode.setProcessInstanceId(processInstanceId);
        hlsCusActAssigneeNode.setProjectId(projectId);
        hlsCusActAssigneeNode.setProjectDocumentCategory(projectDocumentCategory);
        /*查询userName*/
        List<HlsCusActAssigneeNode> hlsCusActAssigneeNodeList = hlsCusActAssigneeNodeService.selectNodeByIdAndType(hlsCusActAssigneeNode);
        if (CollectionUtils.isNotEmpty(hlsCusActAssigneeNodeList)) {
            assigneeString = hlsCusActAssigneeNodeList.get(0).getUserName();
        }
        return assigneeString;
    }

    /**
     * 获取指定秘书处
     */
    @Override
    public String getSeco(DelegateExecution delegateExecution) {
        Long projectId = null;
        //获取工作流类型
        String workflowType = (String) delegateExecution.getVariable("workflowType");
        if ("FCT_CON_CHANGE_WFL".equalsIgnoreCase(workflowType) || "FCT_PP_CHANGE_WFL".equalsIgnoreCase(workflowType)
                || ("PRJ_PROJECT_AUDIT_CHANGE_WFL").equalsIgnoreCase(workflowType)) {
            //租赁项目变更
            //合同变更-担保变更
            projectId = Long.parseLong(delegateExecution.getProcessInstanceBusinessKey());
        } else {
            projectId = Long.parseLong(delegateExecution.getVariable("projectId").toString());
        }

        String assigneeString = null;
        //经办人类型
        String assigneeType = "SECO";
        Long processInstanceId = Long.parseLong(delegateExecution.getProcessInstanceId());
        String projectDocumentCategory = (String) delegateExecution.getVariable("projectDocumentCategory");
        HlsCusActAssigneeNode hlsCusActAssigneeNode = new HlsCusActAssigneeNode();
        hlsCusActAssigneeNode.setAssigneeType(assigneeType);
        hlsCusActAssigneeNode.setProcessInstanceId(processInstanceId);
        hlsCusActAssigneeNode.setProjectId(projectId);
        hlsCusActAssigneeNode.setProjectDocumentCategory(projectDocumentCategory);
        /*查询userName*/
        List<HlsCusActAssigneeNode> hlsCusActAssigneeNodeList = hlsCusActAssigneeNodeService.selectNodeByIdAndType(hlsCusActAssigneeNode);
        if (CollectionUtils.isNotEmpty(hlsCusActAssigneeNodeList)) {
            assigneeString = hlsCusActAssigneeNodeList.get(0).getUserName();
        }
        return assigneeString;
    }

    /**
     * 获取指定的合规审查人
     *
     * @param delegateExecution
     * @return
     */
    @Override
    public String getComplianceDesignatedManager(DelegateExecution delegateExecution) {
        String assigneeString = null;
        String assigneeType = "COMPLIANCE";//经办人类型
        Long processInstanceId = Long.parseLong(delegateExecution.getProcessInstanceId());
        Long projectId = Long.parseLong(delegateExecution.getVariable("projectId").toString());
        String projectDocumentCategory = (String) delegateExecution.getVariable("projectDocumentCategory");
        HlsCusActAssigneeNode hlsCusActAssigneeNode = new HlsCusActAssigneeNode();
        hlsCusActAssigneeNode.setAssigneeType(assigneeType);
        hlsCusActAssigneeNode.setProcessInstanceId(processInstanceId);
        hlsCusActAssigneeNode.setProjectId(projectId);
        hlsCusActAssigneeNode.setProjectDocumentCategory(projectDocumentCategory);
        /*查询userName*/
        List<HlsCusActAssigneeNode> hlsCusActAssigneeNodeList = hlsCusActAssigneeNodeService.selectNodeByIdAndType(hlsCusActAssigneeNode);
        if (CollectionUtils.isNotEmpty(hlsCusActAssigneeNodeList)) {
            assigneeString = hlsCusActAssigneeNodeList.get(0).getUserName();
        }
        return assigneeString;
    }

    /**
     * 获取指定的风控主审
     *
     * @param delegateExecution
     * @return
     */
    @Override
    public String getRiskDesignatedManager(DelegateExecution delegateExecution) {
        String assigneeString;
        String assigneeType = "RISK";//经办人类型
        Long processInstanceId = Long.parseLong(delegateExecution.getProcessInstanceId());
        Long projectId = Long.parseLong(delegateExecution.getVariable("projectId").toString());
        String projectDocumentCategory = (String) delegateExecution.getVariable("projectDocumentCategory");
        HlsCusActAssigneeNode hlsCusActAssigneeNode = new HlsCusActAssigneeNode();
        hlsCusActAssigneeNode.setAssigneeType(assigneeType);
        hlsCusActAssigneeNode.setProcessInstanceId(processInstanceId);
        hlsCusActAssigneeNode.setProjectId(projectId);
        hlsCusActAssigneeNode.setProjectDocumentCategory(projectDocumentCategory);
        /*查询userName*/
        List<HlsCusActAssigneeNode> hlsCusActAssigneeNodeList = hlsCusActAssigneeNodeService.selectNodeByIdAndType(hlsCusActAssigneeNode);
        assigneeString = hlsCusActAssigneeNodeList.get(0).getUserName();
        return assigneeString;
    }

    /**
     * 获取指定的风控协审信息
     *
     * @param delegateExecution
     * @return
     */
    @Override
    public String getRiskDesignatedManagerAt(DelegateExecution delegateExecution) {
        String assigneeString;
        String assigneeType = "RISK";//经办人类型
        Long processInstanceId = Long.parseLong(delegateExecution.getProcessInstanceId());
        Long projectId = Long.parseLong(delegateExecution.getVariable("projectId").toString());
        String projectDocumentCategory = (String) delegateExecution.getVariable("projectDocumentCategory");
        HlsCusActAssigneeNode hlsCusActAssigneeNode = new HlsCusActAssigneeNode();
        hlsCusActAssigneeNode.setAssigneeType(assigneeType);
        hlsCusActAssigneeNode.setProcessInstanceId(processInstanceId);
        hlsCusActAssigneeNode.setProjectId(projectId);
        hlsCusActAssigneeNode.setProjectDocumentCategory(projectDocumentCategory);
        List<HlsCusActAssigneeNode> hlsCusActAssigneeNodeList = hlsCusActAssigneeNodeService.selectNodeByIdAndType(hlsCusActAssigneeNode);
        assigneeString = hlsCusActAssigneeNodeList.get(0).getUserNameAt();
        return assigneeString;
    }

    /**
     * 获取指定的法务主审
     *
     * @param delegateExecution
     * @return
     */
    @Override
    public String getLegalDesignatedManager(DelegateExecution delegateExecution) {
        String assigneeString;
        String assigneeType = "LEGAL";//经办人类型
        Long processInstanceId = Long.parseLong(delegateExecution.getProcessInstanceId());
        Long projectId = Long.parseLong(delegateExecution.getVariable("projectId").toString());
        String projectDocumentCategory = (String) delegateExecution.getVariable("projectDocumentCategory");
        HlsCusActAssigneeNode hlsCusActAssigneeNode = new HlsCusActAssigneeNode();
        hlsCusActAssigneeNode.setAssigneeType(assigneeType);
        hlsCusActAssigneeNode.setProcessInstanceId(processInstanceId);
        hlsCusActAssigneeNode.setProjectId(projectId);
        hlsCusActAssigneeNode.setProjectDocumentCategory(projectDocumentCategory);
        List<HlsCusActAssigneeNode> hlsCusActAssigneeNodeList = hlsCusActAssigneeNodeService.selectNodeByIdAndType(hlsCusActAssigneeNode);
        assigneeString = hlsCusActAssigneeNodeList.get(0).getUserName();
        return assigneeString;
    }

    /**
     * 获取指定的法务协审
     *
     * @param delegateExecution
     * @return
     */
    @Override
    public String getLegalDesignatedManagerAt(DelegateExecution delegateExecution) {
        String assigneeString;
        String assigneeType = "LEGAL";//经办人类型
        Long processInstanceId = Long.parseLong(delegateExecution.getProcessInstanceId());
        Long projectId = Long.parseLong(delegateExecution.getVariable("projectId").toString());
        String projectDocumentCategory = (String) delegateExecution.getVariable("projectDocumentCategory");
        HlsCusActAssigneeNode hlsCusActAssigneeNode = new HlsCusActAssigneeNode();
        hlsCusActAssigneeNode.setAssigneeType(assigneeType);
        hlsCusActAssigneeNode.setProcessInstanceId(processInstanceId);
        hlsCusActAssigneeNode.setProjectId(projectId);
        hlsCusActAssigneeNode.setProjectDocumentCategory(projectDocumentCategory);
        List<HlsCusActAssigneeNode> hlsCusActAssigneeNodeList = hlsCusActAssigneeNodeService.selectNodeByIdAndType(hlsCusActAssigneeNode);
        assigneeString = hlsCusActAssigneeNodeList.get(0).getUserNameAt();
        return assigneeString;
    }

    @Override
    public List<String> getMeetingJudgeMember(DelegateExecution delegateExecution) {
        Long processInstanceId = Long.valueOf(delegateExecution.getProcessInstanceId());
        Long projectId = Long.parseLong(delegateExecution.getVariable("projectId").toString());
        List<String> empString = new ArrayList();
        ProjectMeetingApprover meetingApprover = new ProjectMeetingApprover();
        meetingApprover.setProjectId(projectId);
        List<ProjectMeetingApprover>  projectMeetingApprovers = projectMeetingApproverMapper.queryAllByProjectId(meetingApprover);
//        List<HlsCusActOnlineMeetingMember> hlsCusActOnlineMeetingMemberList = hlsCusActOnlineMeetingMemberService.queryMeetingMemberByProcessInstanceId(processInstanceId);
        Long companyId = Long.parseLong(delegateExecution.getVariable("companyId").toString());
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");

        PrjMeetingJudge prjMeetingJudge = new PrjMeetingJudge();
        prjMeetingJudge.setCompanyId(companyId);

        for (ProjectMeetingApprover approver : projectMeetingApprovers) {

            empString.add(String.valueOf(approver.getApproverUserId()));

        }
        return empString;
    }

    @Override
    public List<String> getHistoryMeetingJudgeMember(DelegateExecution delegateExecution) {
        List<String> empString = new ArrayList();

        Long projectId = Long.parseLong(delegateExecution.getVariable("projectId").toString());
        String projectDocumentCategory = (String) delegateExecution.getVariable("projectDocumentCategory");
        List<HlsCusActOnlineMeetingMember> hlsCusActOnlineMeetingMemberList = hlsCusActOnlineMeetingMemberMapper.queryMeetingMemberByDocumentInfo(projectId, projectDocumentCategory);
        Long companyId = Long.parseLong(delegateExecution.getVariable("companyId").toString());
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        Boolean flag = true;

        PrjMeetingJudge prjMeetingJudge = new PrjMeetingJudge();
        prjMeetingJudge.setCompanyId(companyId);
        List<PrjMeetingJudge> prjMeetingJudgeList = prjMeetingJudgeMapper.select(prjMeetingJudge);
        for (HlsCusActOnlineMeetingMember hlsCusActOnlineMeetingMember : hlsCusActOnlineMeetingMemberList) {
            for (PrjMeetingJudge dt : prjMeetingJudgeList) {
                SysUser sysUser = new SysUser();
                sysUser.setUserId(dt.getJudgeUserId());
                if (sysUserService.selectByPrimaryKey(requestCtx, sysUser).getUserName().equalsIgnoreCase(hlsCusActOnlineMeetingMember.getMemberName())) {
                    flag = false;
                }
            }
            if (!flag) {
                empString.add(hlsCusActOnlineMeetingMember.getMemberName());
            }

        }
        return empString;
    }

    /**
     * @Description: 贷后管理获取主审
     * @Author: Wty
     * @Date: Created om 下午2:33 2018/7/5
     * @param: [delegateExecution]
     * @return: java.util.List<java.lang.String> 返回主审名单
     */
    /*@Override
    public List<String> getPostLoanManager(DelegateExecution delegateExecution) {
        List<String> assistantString = new ArrayList<>();
        String companyId = delegateExecution.getVariable("companyId").toString();
        String plmType = delegateExecution.getVariable("plmType").toString();
        Long postLoanId = Long.parseLong(delegateExecution.getVariable("postLoanId").toString());
        HlsCusFiveClassificationContract fiveClassificationContract = new HlsCusFiveClassificationContract();
        fiveClassificationContract.setPostLoanId(postLoanId);
        fiveClassificationContract.setPlmType(plmType);
        List<HlsCusFiveClassificationContract> classificationContracts;
        if ("FC".equals(plmType)) {
            classificationContracts = fiveClassificationContractMapper.selectFcContractsByPlmType(fiveClassificationContract);
        } else {
            classificationContracts = fiveClassificationContractMapper.selectContractsByPlmType(fiveClassificationContract);
        }
        List<Long> fctContractIds = new ArrayList<>();
        List<Long> conContractIds = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(classificationContracts)) {
            for (HlsCusFiveClassificationContract f : classificationContracts) {
                if ("CON".equals(f.getContractType())) {
                    //租赁
                    conContractIds.add(f.getContractId());
                } else {
                    //保理
                    fctContractIds.add(f.getContractId());
                }
            }
        } else {
            //没有合同则取所有客户下的的起租合同
            if ("PLI".equals(plmType)) {
                HlsCusPostloanInspection postloanInspection = new HlsCusPostloanInspection();
                postloanInspection.setPostloanInspectionId(postLoanId);
                List<HlsCusPostloanInspection> postloanInspections = postloanInspectionMapper.queryAll(postloanInspection);
                if (CollectionUtils.isNotEmpty(postloanInspections)) {
                    HlsCusFiveClassificationContract fcContract = new HlsCusFiveClassificationContract();
                    fcContract.setCompanyId(Long.parseLong(companyId));
                    fcContract.setContractBpId(postloanInspections.get(0).getBpId());

                    List<HlsCusFiveClassificationContract> fcContracts = fiveClassificationContractMapper.selectAllContracts(fcContract);
                    if (CollectionUtils.isNotEmpty(fcContracts)) {
                        for (HlsCusFiveClassificationContract f : fcContracts) {
                            if ("CON".equals(f.getContractType())) {
                                //租赁
                                conContractIds.add(f.getContractId());
                            } else {
                                //保理
                                fctContractIds.add(f.getContractId());
                            }
                        }
                    }
                }
            } else if ("RW".equals(plmType)) {
                HlsCusRiskWarning riskWarning = new HlsCusRiskWarning();
                riskWarning.setRiskWarningId(postLoanId);
                List<HlsCusRiskWarning> riskWarnings = riskWarningMapper.selectRiskWarningALL(riskWarning);
                if (CollectionUtils.isNotEmpty(riskWarnings)) {
                    HlsCusFiveClassificationContract fcContract = new HlsCusFiveClassificationContract();
                    fcContract.setCompanyId(Long.parseLong(companyId));
                    fcContract.setContractBpId(riskWarnings.get(0).getBpId());

                    List<HlsCusFiveClassificationContract> fcContracts = fiveClassificationContractMapper.selectAllContracts(fcContract);
                    if (CollectionUtils.isNotEmpty(fcContracts)) {
                        for (HlsCusFiveClassificationContract f : fcContracts) {
                            if ("CON".equals(f.getContractType())) {
                                //租赁
                                conContractIds.add(f.getContractId());
                            } else {
                                //保理
                                fctContractIds.add(f.getContractId());
                            }
                        }
                    }
                }
            }
        }

        //返回对应的审批人
        if ("FC".equals(plmType)) {
            //五级分类
            //取所有合同的username
            List<String> userNames = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(conContractIds)) {
                Long[] conIds = new Long[conContractIds.size()];
                fiveClassificationContract.setConContractIds(conContractIds.toArray(conIds));
                List<String> conUserNames = fiveClassificationContractMapper.selectUserNameByConContractIds(fiveClassificationContract);
                if (CollectionUtils.isNotEmpty(conUserNames)) {
                    userNames.addAll(conUserNames);
                }
            }
            if (CollectionUtils.isNotEmpty(fctContractIds)) {
                Long[] fctIds = new Long[fctContractIds.size()];
                fiveClassificationContract.setFctContractIds(fctContractIds.toArray(fctIds));
                List<String> fctUserNames = fiveClassificationContractMapper.selectUserNameByFctContractIds(fiveClassificationContract);
                if (CollectionUtils.isNotEmpty(fctUserNames)) {
                    userNames.addAll(fctUserNames);
                }
            }
            Set<String> set = new HashSet<>(userNames);
            assistantString = new ArrayList<>(set);
        } else if ("PLI".equals(plmType) || "RW".equals(plmType)) {
            //贷后检查 || 风险预警
            //去最新合同的username
            String latestUserName = null;
            Date maxDate = null;
            List<HlsCusFiveClassificationContract> allLatestList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(conContractIds)) {
                Long[] conIds = new Long[conContractIds.size()];
                fiveClassificationContract.setConContractIds(conContractIds.toArray(conIds));
                List<HlsCusFiveClassificationContract> latestConUserName = fiveClassificationContractMapper.selectUserNameLatestByConContractIds(fiveClassificationContract);
                if (CollectionUtils.isNotEmpty(latestConUserName)) {
                    allLatestList.addAll(latestConUserName);
                }
            }
            if (CollectionUtils.isNotEmpty(fctContractIds)) {
                Long[] fctIds = new Long[fctContractIds.size()];
                fiveClassificationContract.setFctContractIds(fctContractIds.toArray(fctIds));
                List<HlsCusFiveClassificationContract> latestFctUserName = fiveClassificationContractMapper.selectUserNameLatestByFctContractIds(fiveClassificationContract);
                if (CollectionUtils.isNotEmpty(latestFctUserName)) {
                    allLatestList.addAll(latestFctUserName);
                }
            }
            for (int i = 0; i < allLatestList.size(); i++) {
                if (i == 0) {
                    maxDate = allLatestList.get(i).getCreationDate();
                    latestUserName = allLatestList.get(i).getUserName();
                } else {
                    if (maxDate.before(allLatestList.get(i).getCreationDate())) {
                        latestUserName = allLatestList.get(i).getUserName();
                    }
                }
            }
            assistantString.add(latestUserName);
        }

        return assistantString;
    }*/

    /**
     * 主办部门负责人
     *
     * @Description: 需要返回allocationId，所以修改了 {@link HlsCusEmployeeMapper} 的 queryManageDeptDirector sql
     */
    @Override
    public String getManageDeptDirector(DelegateExecution delegateExecution) {
        Long unitId = Long.parseLong(delegateExecution.getVariable("unitId").toString());
        Long companyId = Long.parseLong(delegateExecution.getVariable("companyId").toString());
        HlsCusEmployee hlsCusEmployee = new HlsCusEmployee();
        hlsCusEmployee.setCompanyId(companyId);
        hlsCusEmployee.setManageUnitId(unitId);
        List<HlsCusEmployee> hlsCusEmployees = hlsCusEmployeeMapper.queryManageDeptDirector(hlsCusEmployee);
        if (CollectionUtils.isNotEmpty(hlsCusEmployees)) {
            return hlsCusEmployees.get(0).getName();
        }
        return null;
    }
    /**
     * 协办部门负责人
     *
     * @Description: 需要返回allocationId，所以修改了 {@link HlsCusEmployeeMapper} 的 queryManageDeptDirector sql
     */
    @Override
    public String getManageAssistDirector(DelegateExecution delegateExecution) {
        Long unitId = Long.parseLong(delegateExecution.getVariable("assistUnitId").toString());
        Long companyId = Long.parseLong(delegateExecution.getVariable("companyId").toString());
        HlsCusEmployee hlsCusEmployee = new HlsCusEmployee();
        hlsCusEmployee.setCompanyId(companyId);
        hlsCusEmployee.setManageUnitId(unitId);
        List<HlsCusEmployee> hlsCusEmployees = hlsCusEmployeeMapper.queryManageDeptDirector(hlsCusEmployee);
        if (CollectionUtils.isNotEmpty(hlsCusEmployees)) {
            return hlsCusEmployees.get(0).getName();
        }
        return null;
    }

    /**
     * 分管领导
     *
     * @param delegateExecution
     * @return
     * @Description: 需要返回allocationId，所以修改了 {@link HlsCusEmployeeMapper} 的 queryParentManager sql
     */
    @Override
    public String getParentDeptManager(DelegateExecution delegateExecution) {
        Long unitId = Long.parseLong(delegateExecution.getVariable("unitId").toString());
        HlsCusEmployee hlsCusEmployee = new HlsCusEmployee();
        hlsCusEmployee.setUnitId(unitId.toString());
        List<HlsCusEmployee> hlsCusEmployees = hlsCusEmployeeMapper.queryParentManager(hlsCusEmployee);
        if (CollectionUtils.isNotEmpty(hlsCusEmployees)) {
            return hlsCusEmployees.get(0).getName();
        }
        return null;
    }

    /**
     * 部门领导
     *
     * @param delegateExecution
     * @return
     */
    @Override
    public List<String> getDeptDirector(DelegateExecution delegateExecution) {
        Long companyId = Long.parseLong(delegateExecution.getVariable("companyId").toString());
        String employeeCode = delegateExecution.getVariable("employeeCode").toString();
        List<String> hlsCusEmployees = hlsCusEmployeeMapper.getUnitPositionUsername(employeeCode);
        if (hlsCusEmployees.size() > 0) {
            return hlsCusEmployees;
        } else {
            List<String> lists = new ArrayList<>();
            lists.add("ADMIN");
            return lists;
        }
    }

    /**
     * 部门负责人
     *
     * @param delegateExecution
     * @return
     */
    @Override
    public String getUnitManager(DelegateExecution delegateExecution) {
        Long companyId = Long.parseLong(delegateExecution.getVariable("companyId").toString());
        Long unitId = Long.parseLong(delegateExecution.getVariable("unitId").toString());
        HlsCusEmployee hlsCusEmployee = new HlsCusEmployee();
        hlsCusEmployee.setUnitId(unitId.toString());
        hlsCusEmployee.setCompanyId(companyId);
        List<HlsCusEmployee> hlsCusEmployees = hlsCusEmployeeMapper.queryUnitManagerByUnitId(hlsCusEmployee);
        if (hlsCusEmployees.size() > 0) {
            return hlsCusEmployees.get(0).getUserName();
        } else {
            return "ADMIN";
        }
    }

    /**
     * 申请人部门负责人
     *
     * @param delegateExecution
     * @return
     */
    @Override
    public String getTransforUnitManager(DelegateExecution delegateExecution) {
        Long companyId = Long.parseLong(delegateExecution.getVariable("companyId").toString());
        Long unitId = Long.parseLong(delegateExecution.getVariable("transforSourceUnitId").toString());
        HlsCusEmployee hlsCusEmployee = new HlsCusEmployee();
        hlsCusEmployee.setUnitId(unitId.toString());
        hlsCusEmployee.setCompanyId(companyId);
        List<HlsCusEmployee> hlsCusEmployees = hlsCusEmployeeMapper.queryUnitManagerByUnitId(hlsCusEmployee);
        if (hlsCusEmployees.size() > 0) {
            return hlsCusEmployees.get(0).getUserName();
        } else {
            return "ADMIN";
        }
    }

    /**
     * 接收人部门负责人
     *
     * @param delegateExecution
     * @return
     */
    @Override
    public String getTargetUnitManager(DelegateExecution delegateExecution) {
        Long companyId = Long.parseLong(delegateExecution.getVariable("companyId").toString());
        Long unitId = Long.parseLong(delegateExecution.getVariable("transforTargetUnitId").toString());
        HlsCusEmployee hlsCusEmployee = new HlsCusEmployee();
        hlsCusEmployee.setUnitId(unitId.toString());
        hlsCusEmployee.setCompanyId(companyId);
        List<HlsCusEmployee> hlsCusEmployees = hlsCusEmployeeMapper.queryUnitManagerByUnitId(hlsCusEmployee);
        if (hlsCusEmployees.size() > 0) {
            return hlsCusEmployees.get(0).getUserName();
        } else {
            return "ADMIN";
        }
    }

    /**
     * 接收人查询
     *
     * @param delegateExecution
     * @return
     */
    @Override
    public String getTargetUser(DelegateExecution delegateExecution) {
        Long userId = Long.parseLong(delegateExecution.getVariable("transforTargetId").toString());
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser = sysUserService.selectUserById(userId);
        if (sysUser != null && sysUser.getUserName() != null) {
            return sysUser.getUserName();
        } else {
            return "ADMIN";
        }
    }


    @Override
    public List<String> getProjectUnitManager(DelegateExecution delegateExecution) {
        List<String> assistantString = new ArrayList();
        String companyId = delegateExecution.getVariable("companyId").toString();
        List<Long> employeeAssignIdList = JSON.parseArray(delegateExecution.getVariable("employeeAssignIdList").toString(), Long.class);
        ;
        List<HlsEmployee> hlsEmployeeList = hlsCusEmployeeMapper.selectManagerUserNameByEmployeeAssignIdList(Long.parseLong(companyId), employeeAssignIdList);
        for (HlsEmployee dt : hlsEmployeeList) {
            assistantString.add(dt.getName());
        }
        return assistantString;
    }


    @Override
    public List<String> getEmployeeAssignManager(DelegateExecution delegateExecution) {
        List<String> assistantString = new ArrayList();
        String companyId = delegateExecution.getVariable("companyId").toString();
        String employeeAssignsId = delegateExecution.getVariable("employeeAssignsId").toString();
        String assistantAssignsId = delegateExecution.getVariable("assistantAssignsId").toString();
        List<HlsEmployee> hlsEmployeeList = hlsCusEmployeeMapper.selectProjectMajorAndAssistant(Long.parseLong(companyId), Long.parseLong(employeeAssignsId), Long.parseLong(assistantAssignsId));
        for (HlsEmployee dt : hlsEmployeeList) {
            assistantString.add(dt.getName());
        }
        return assistantString;
    }

    /**
     * 分管领导(多个)
     *
     * @param delegateExecution
     * @return
     */
    @Override
    public List<String> getManyParentDeptManager(DelegateExecution delegateExecution) {
        // Long unitId = Long.parseLong(delegateExecution.getVariable("unitId").toString());
        // String unitIdList = delegateExecution.getVariable("unitIdList").toString();
        List<String> unitListString = new ArrayList();
        List<Long> unitIdList = JSON.parseArray(delegateExecution.getVariable("unitIdList").toString(), Long.class);
        ;
        List<HlsEmployee> hlsCusEmployees = hlsCusEmployeeMapper.queryManyParentManager(unitIdList);
        for (HlsEmployee dt : hlsCusEmployees) {
            unitListString.add(dt.getName());
        }
        return unitListString;
    }

    private static final String  FCT_PROJECTCREATE_WFL ="FCT_PROJECTCREATE_WFL";
    private static final String  CREDIT_CHANCE_CREATE_WFL ="CREDIT_CHANCE_CREATE_WFL";

    @Autowired
    private HlsCusHlsCreditLineChanceMapper hlsCusHlsCreditLineChanceMapper;

    @Override
    public String getAssignApprover(DelegateExecution delegateExecution) {


        Long allocationId = Long.parseLong(delegateExecution.getVariable("assign").toString());
        Long companyId = Long.parseLong(delegateExecution.getVariable("companyId").toString());
        HlsCusEmployee hlsCusEmployee = new HlsCusEmployee();
        hlsCusEmployee.setCompanyId(companyId);
        hlsCusEmployee.setAllocationId(allocationId);
        String[] split = delegateExecution.getProcessDefinitionId().split(":");
        if(FCT_PROJECTCREATE_WFL.equalsIgnoreCase(split[0]) || CREDIT_CHANCE_CREATE_WFL.equalsIgnoreCase(split[0])){
            Long chanceId = Long.valueOf(delegateExecution.getParent().getProcessInstanceBusinessKey());
            HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance = new HlsCusHlsCreditLineChance();
            hlsCusHlsCreditLineChance.setChanceId(chanceId);
            hlsCusHlsCreditLineChance = hlsCusHlsCreditLineChanceMapper.selectByPrimaryKey(hlsCusHlsCreditLineChance);

            hlsCusHlsCreditLineChance.setProjectReviewer(String.valueOf(allocationId));
            hlsCusHlsCreditLineChanceMapper.updateByPrimaryKey(hlsCusHlsCreditLineChance);
        }
        List<HlsCusEmployee> hlsCusEmployees = hlsCusEmployeeMapper.queryAssignByAllocation(hlsCusEmployee);
        if (CollectionUtils.isNotEmpty(hlsCusEmployees)) {
            return hlsCusEmployees.get(0).getName();
        }
        return null;
    }

    @Override
    public String getAssignGeneralApprover(DelegateExecution delegateExecution) {

        Long allocationId = Long.parseLong(delegateExecution.getVariable("assign").toString());
        IRequest request = RequestHelper.getCurrentRequest();
        Long companyId = request.getCompanyId();
//        Long companyId = Long.parseLong(delegateExecution.getVariable("companyId").toString());
        HlsCusEmployee hlsCusEmployee = new HlsCusEmployee();
        hlsCusEmployee.setCompanyId(companyId);
        hlsCusEmployee.setAllocationId(allocationId);
        List<HlsCusEmployee> hlsCusEmployees = hlsCusEmployeeMapper.queryAssignByAllocation(hlsCusEmployee);
        if (CollectionUtils.isNotEmpty(hlsCusEmployees)) {
            return hlsCusEmployees.get(0).getName();
        }
        return null;
    }

    @Override
    public String getAppointApproverRisk(DelegateExecution delegateExecution) {

        Long allocationId = Long.parseLong(delegateExecution.getVariable("allocationIdRisk").toString());
        Long companyId = Long.parseLong(delegateExecution.getVariable("companyId").toString());
        HlsCusEmployee hlsCusEmployee = new HlsCusEmployee();
        //hlsCusEmployee.setCompanyId(companyId);
        hlsCusEmployee.setAllocationId(allocationId);
        String[] split = delegateExecution.getProcessDefinitionId().split(":");

        List<HlsCusEmployee> hlsCusEmployees = hlsCusEmployeeMapper.queryAssignByAllocation(hlsCusEmployee);
        if (CollectionUtils.isNotEmpty(hlsCusEmployees)) {
            return hlsCusEmployees.get(0).getName();
        }

        return null;
    }

    @Override
    public String getAppointApproverLegal(DelegateExecution delegateExecution) {

        Long allocationId = Long.parseLong(delegateExecution.getVariable("allocationIdRisk").toString());
        Long companyId = Long.parseLong(delegateExecution.getVariable("companyId").toString());
        HlsCusEmployee hlsCusEmployee = new HlsCusEmployee();
        //hlsCusEmployee.setCompanyId(companyId);
        hlsCusEmployee.setAllocationId(allocationId);
        String[] split = delegateExecution.getProcessDefinitionId().split(":");

        List<HlsCusEmployee> hlsCusEmployees = hlsCusEmployeeMapper.queryAssignByAllocation(hlsCusEmployee);
        if (CollectionUtils.isNotEmpty(hlsCusEmployees)) {
            return hlsCusEmployees.get(0).getName();
        }
        return null;
    }

    @Override
    public String getAppointApproverPrice(DelegateExecution delegateExecution) {
        Long allocationId = Long.parseLong(delegateExecution.getVariable("allocationIdPrice").toString());
        Long companyId = Long.parseLong(delegateExecution.getVariable("companyId").toString());
        HlsCusEmployee hlsCusEmployee = new HlsCusEmployee();
        hlsCusEmployee.setCompanyId(companyId);
        hlsCusEmployee.setAllocationId(allocationId);
        String[] split = delegateExecution.getProcessDefinitionId().split(":");

        List<HlsCusEmployee> hlsCusEmployees = hlsCusEmployeeMapper.queryAssignByAllocation(hlsCusEmployee);
        if (CollectionUtils.isNotEmpty(hlsCusEmployees)) {
            return hlsCusEmployees.get(0).getName();
        }

        return null;
    }

    //上会指定业审委委员
    @Override
    public String getPowerfulEmployee(DelegateExecution delegateExecution) {

        Long allocationId = Long.parseLong(delegateExecution.getVariable("powerfulEmployee").toString());
        Long companyId = Long.parseLong(delegateExecution.getVariable("companyId").toString());
        HlsCusEmployee hlsCusEmployee = new HlsCusEmployee();
        hlsCusEmployee.setCompanyId(companyId);
        hlsCusEmployee.setAllocationId(allocationId);
//        String[] split = delegateExecution.getProcessDefinitionId().split(":");

        List<HlsCusEmployee> hlsCusEmployees = hlsCusEmployeeMapper.queryAssignByAllocation(hlsCusEmployee);
        if (CollectionUtils.isNotEmpty(hlsCusEmployees)) {
            return hlsCusEmployees.get(0).getName();
        }

        return null;
    }
    //存续期副总裁
    @Override
    public String getDurationEmployee(DelegateExecution delegateExecution) {

        Long allocationId = Long.parseLong(delegateExecution.getVariable("durationEmployee").toString());
        Long companyId = Long.parseLong(delegateExecution.getVariable("companyId").toString());
        HlsCusEmployee hlsCusEmployee = new HlsCusEmployee();
        hlsCusEmployee.setCompanyId(companyId);
        hlsCusEmployee.setAllocationId(allocationId);
//        String[] split = delegateExecution.getProcessDefinitionId().split(":");

        List<HlsCusEmployee> hlsCusEmployees = hlsCusEmployeeMapper.queryAssignByAllocation(hlsCusEmployee);
        if (CollectionUtils.isNotEmpty(hlsCusEmployees)) {
            return hlsCusEmployees.get(0).getName();
        }

        return null;
    }
    //存续期副总裁二
    @Override
    public String getDurationEmployeeTwo(DelegateExecution delegateExecution) {

        Long allocationId = Long.parseLong(delegateExecution.getVariable("durationEmployeeTwo").toString());
        Long companyId = Long.parseLong(delegateExecution.getVariable("companyId").toString());
        HlsCusEmployee hlsCusEmployee = new HlsCusEmployee();
        hlsCusEmployee.setCompanyId(companyId);
        hlsCusEmployee.setAllocationId(allocationId);
//        String[] split = delegateExecution.getProcessDefinitionId().split(":");

        List<HlsCusEmployee> hlsCusEmployees = hlsCusEmployeeMapper.queryAssignByAllocation(hlsCusEmployee);
        if (CollectionUtils.isNotEmpty(hlsCusEmployees)) {
            return hlsCusEmployees.get(0).getName();
        }

        return null;
    }

    //付款前提条件指定副总裁
    @Override
    public String getConditionEmployee(DelegateExecution delegateExecution) {

        Long allocationId = Long.parseLong(delegateExecution.getVariable("conditionEmployee").toString());
        Long companyId = Long.parseLong(delegateExecution.getVariable("companyId").toString());
        HlsCusEmployee hlsCusEmployee = new HlsCusEmployee();
        hlsCusEmployee.setCompanyId(companyId);
        hlsCusEmployee.setAllocationId(allocationId);
//        String[] split = delegateExecution.getProcessDefinitionId().split(":");

        List<HlsCusEmployee> hlsCusEmployees = hlsCusEmployeeMapper.queryAssignByAllocation(hlsCusEmployee);
        if (CollectionUtils.isNotEmpty(hlsCusEmployees)) {
            return hlsCusEmployees.get(0).getName();
        }

        return null;
    }

    @Override
    public List<String> getDeptRiskOrAviation(DelegateExecution delegateExecution) {
        String aviationFlag = delegateExecution.getVariable("aviationFlag").toString();
        IRequest request = RequestHelper.getCurrentRequest();
        Long companyId = request.getCompanyId();
        //风险管理部复核岗-风险管理部
        String positionCode = "A0080";
        /*if(aviationFlag.equals("Y")){
            //风险管理部复核岗-航空事业部
            positionCode = "A0055";
        }*/
        return fndEmployeeMapper.getPositionEmp(positionCode, companyId);
    }

    @Override
    public List<String> getPrincipalRiskOrAviation(DelegateExecution delegateExecution) {
        String aviationFlag = delegateExecution.getVariable("aviationFlag").toString();
        IRequest request = RequestHelper.getCurrentRequest();
        Long companyId = request.getCompanyId();
        //风险管理部负责人
        String positionCode = "A0160";
        /*if(aviationFlag.equals("Y")){
            //业务部门负责人-航空事业部
            positionCode = "A0480";
        }*/
        return fndEmployeeMapper.getPositionEmp(positionCode, companyId);
    }

    @Override
    public List<String> getPriceApprover(DelegateExecution delegateExecution) {
        Long unitId = Long.parseLong(delegateExecution.getVariable("unitId").toString());
        List<String> priceApprovers = new ArrayList<>();
        String priceApprover1 = "817";
        priceApprovers.add(priceApprover1);
        String priceApprover2 = "818";
        priceApprovers.add(priceApprover2);
        String priceApprover3 = "";
        if(unitId == 157L || unitId == 158L || unitId == 119L){
            priceApprover3 = "814";
            priceApprovers.add(priceApprover3);
        }
        if(unitId == 108L || unitId == 116L || unitId == 118L){
            priceApprover3 = "815";
            priceApprovers.add(priceApprover3);
        }
        if(unitId == 160L || unitId == 161L || unitId == 120L || unitId == 162L || unitId == 163L || unitId == 114L){
            priceApprover3 = "816";
            priceApprovers.add(priceApprover3);
        }
        return priceApprovers;
    }

    @Override
    public String billEmployee(DelegateExecution delegateExecution) {
        Long billId = Long.parseLong(delegateExecution.getVariable("billId").toString());
        hlsBillRequest hlsCusEmployee = new hlsBillRequest();
        hlsCusEmployee.setBillId(billId);
        List<hlsBillRequest> hlsCusEmployees = hlsBillRequestMapper.queryEmployee(hlsCusEmployee);
        if (CollectionUtils.isNotEmpty(hlsCusEmployees)) {
            return hlsCusEmployees.get(0).getName();
        }

        return null;
    }


    @Override
    public List<String> queryNextApprovePerson(DelegateExecution delegateExecution) {
        List<String> assistantString = new ArrayList();
        Long generalId = Long.valueOf(delegateExecution.getVariable("generalId").toString());
        HlsGeneralIssue hlsgeneralissue = new HlsGeneralIssue();
        hlsgeneralissue.setGeneralId(generalId);
        List<HlsGeneralIssue> hlsCusEmployees = hlsgeneralissuemapper.queryHlsGeneralIssue(hlsgeneralissue);
        if (CollectionUtils.isNotEmpty(hlsCusEmployees)) {
//            return hlsCusEmployees.get(0).getNextApprovePerson().toString();
            assistantString.add(hlsCusEmployees.get(0).getNextApprovePerson().toString());
        }

        return assistantString;
    }

    @Override
    public List<String> getUserParent(DelegateExecution delegateExecution) {
        List<String> assistantString = new ArrayList();
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        Long userId = requestCtx.getUserId();
        Long unitId = Long.parseLong(delegateExecution.getVariable("unitId").toString());
        Long companyId = Long.parseLong(delegateExecution.getVariable("companyId").toString());
        HlsCusEmployee hlsCusEmployee = new HlsCusEmployee();
        hlsCusEmployee.setCompanyId(companyId);
        hlsCusEmployee.setUnitId(unitId.toString());

        List<HlsCusEmployee> hlsCusEmployees = hlsCusEmployeeMapper.queryBusinessLeader(hlsCusEmployee);
        if (CollectionUtils.isNotEmpty(hlsCusEmployees)) {
            for(HlsCusEmployee employee : hlsCusEmployees){
                assistantString.add(employee.getName());
            }
            return assistantString ;
        }

        return assistantString;
    }

    @Override
    public List<String> getPositionIgnoreCompany(String positionCode) {
        //风险管理部复核岗-风险管理部
        return fndEmployeeMapper.getPositionEmp(positionCode, null);
    }

    @Override
    public List<String> getChanceAssist(DelegateExecution delegateExecution) {
        String projectAssistant = delegateExecution.getVariable("projectAssistant").toString();
        List<hlsBillRequest> hlsCusEmployees = hlsBillRequestMapper.queryAllocationIdByUserId(Long.valueOf(projectAssistant));
        List<String> assignees = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(hlsCusEmployees)) {
            assignees.add(hlsCusEmployees.get(0).getName());
        }
        String projectAssistant2 = delegateExecution.getVariable("projectAssistant2").toString();
        if(StringUtils.isNotEmpty(projectAssistant2)){
            hlsCusEmployees = hlsBillRequestMapper.queryAllocationIdByUserId(Long.valueOf(projectAssistant));
            if (CollectionUtils.isNotEmpty(hlsCusEmployees)) {
                assignees.add(hlsCusEmployees.get(0).getName());
            }
        }
        return assignees;
    }


    @Override
    public List<String> getAssignByUserName(String names) {
        ArrayList<String> returnList = new ArrayList<>();
        String[] nameArray = names.split(",");
        for (String name : nameArray) {
            returnList.add(hlsCusSysAppointApproverMapper.getAssignByUserName(name));
        }
        return returnList;
    }


    @Override
    public String getReviewAppointApproverRisk(DelegateExecution delegateExecution) {
        //获取项目id
        Long projectId = Long.parseLong(delegateExecution.getVariable("projectId").toString());
        //从项目表中获取risk_allocation_id
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        hlsCusPrjProject = prjProjectMapper.selectByPrimaryKey(hlsCusPrjProject);
        //将risk_allocation_id设置为审批人
        Long allocationId = hlsCusPrjProject.getRiskAllocationId();
        if (allocationId != null) {
            return allocationId.toString();
        }
        return null;
    }

    @Override
    public String getReviewAppointApproverLegal(DelegateExecution delegateExecution) {
        //获取项目id
        Long projectId = Long.parseLong(delegateExecution.getVariable("projectId").toString());
        //从项目表中获取risk_allocation_id
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        hlsCusPrjProject = prjProjectMapper.selectByPrimaryKey(hlsCusPrjProject);
        //将risk_allocation_id设置为审批人
        Long allocationId = hlsCusPrjProject.getLegalAllocationId();
        if (allocationId != null) {
            return allocationId.toString();
        }
        return null;
    }

    @Override
    public List<String> getFirstRisk(DelegateExecution delegateExecution) {
        Long projectId = Long.parseLong(delegateExecution.getProcessInstanceBusinessKey());
        HlsCusPrjProject hlsCusPrjProject = prjProjectMapper.selectByPrimaryKey(projectId);
        Long riskAssistantFirst = hlsCusPrjProject.getRiskAssistantFirst();
        if(riskAssistantFirst == null){
            IRequest request = RequestHelper.getCurrentRequest();
            Long companyId = request.getCompanyId();
            return fndEmployeeMapper.getPositionEmp("A0170", companyId);
        }else{
            ArrayList<String> returnList = new ArrayList<>();
            returnList.add(riskAssistantFirst.toString());
            return returnList;
        }

    }

    @Override
    public List<String> getReviewRisk(DelegateExecution delegateExecution) {
        Long projectId = Long.parseLong(delegateExecution.getProcessInstanceBusinessKey());
        HlsCusPrjProject hlsCusPrjProject = prjProjectMapper.selectByPrimaryKey(projectId);
        Long riskHost = hlsCusPrjProject.getRiskHost();
        if(riskHost == null){
            IRequest request = RequestHelper.getCurrentRequest();
            Long companyId = request.getCompanyId();
            List<String> list = fndEmployeeMapper.getPositionEmp("A0170", companyId);
            ArrayList<String> returnList = new ArrayList<>();
            String riskAssistantFirst = hlsCusPrjProject.getRiskAssistantFirst().toString();
            for(String assignee : list){
                if(!assignee.equals(riskAssistantFirst)){
                    returnList.add(assignee);
                }
            }
            return returnList;
        }else{
            ArrayList<String> returnList = new ArrayList<>();
            returnList.add(riskHost.toString());
            return returnList;
        }
    }



    @Override
    public List<String> getFirstRiskContract(DelegateExecution delegateExecution) {
        Long contractId = Long.parseLong(delegateExecution.getProcessInstanceBusinessKey());
        HlsCusConContract hlsCusConContract = hlsCusConContractMapper.selectByPrimaryKey(contractId);
        Long riskAssistantFirst = hlsCusConContract.getRiskAssistantFirst();
        if(riskAssistantFirst == null){
            IRequest request = RequestHelper.getCurrentRequest();
            Long companyId = request.getCompanyId();
            return fndEmployeeMapper.getPositionEmp("A0170", companyId);
        }else{
            ArrayList<String> returnList = new ArrayList<>();
            returnList.add(riskAssistantFirst.toString());
            return returnList;
        }

    }

    @Override
    public List<String> getReviewRiskContract(DelegateExecution delegateExecution) {
        Long contractId = Long.parseLong(delegateExecution.getProcessInstanceBusinessKey());
        HlsCusConContract hlsCusConContract = hlsCusConContractMapper.selectByPrimaryKey(contractId);
        Long riskHost = hlsCusConContract.getRiskHost();
        if(riskHost == null){
            IRequest request = RequestHelper.getCurrentRequest();
            Long companyId = request.getCompanyId();
            List<String> list = fndEmployeeMapper.getPositionEmp("A0170", companyId);
            ArrayList<String> returnList = new ArrayList<>();
            String riskAssistantFirst = hlsCusConContract.getRiskAssistantFirst().toString();
            for(String assignee : list){
                if(!assignee.equals(riskAssistantFirst)){
                    returnList.add(assignee);
                }
            }
            return returnList;
        }else{
            ArrayList<String> returnList = new ArrayList<>();
            returnList.add(riskHost.toString());
            return returnList;
        }
    }

    @Override
    public String getCreditChanceAssistant(DelegateExecution delegateExecution){
        Long chanceId = Long.parseLong(delegateExecution.getProcessInstanceBusinessKey());
        HlsCusHlsCreditLineChance chance = hlsCusHlsCreditLineChanceMapper.selectByPrimaryKey(chanceId);
        return chance.getProjectAssistant().toString();
    }

    @Override
    public String getCreditChanceExamine(DelegateExecution delegateExecution){
        Long chanceId = Long.parseLong(delegateExecution.getProcessInstanceBusinessKey());
        HlsCusHlsCreditLineChance chance = hlsCusHlsCreditLineChanceMapper.selectByPrimaryKey(chanceId);
        return chance.getExamineHost().toString();
    }

    @Override
    public String getCreditChanceRisk(DelegateExecution delegateExecution){
        Long chanceId = Long.parseLong(delegateExecution.getProcessInstanceBusinessKey());
        HlsCusHlsCreditLineChance chance = hlsCusHlsCreditLineChanceMapper.selectByPrimaryKey(chanceId);
        return chance.getRiskHost().toString();
    }

    @Override
    public String getCreditChanceLegal(DelegateExecution delegateExecution){
        Long chanceId = Long.parseLong(delegateExecution.getProcessInstanceBusinessKey());
        HlsCusHlsCreditLineChance chance = hlsCusHlsCreditLineChanceMapper.selectByPrimaryKey(chanceId);
        return chance.getLegalHost().toString();
    }

    @Override
    public List<String> getCreditChanceJudges(DelegateExecution delegateExecution){
        ArrayList<String> returnList = new ArrayList<>();

        PrjMeetingJudge prjMeetingJudge = new PrjMeetingJudge();
        prjMeetingJudge.setEnabledFlag("Y");
        List<PrjMeetingJudge> judgeList =  prjMeetingJudgeMapper.select(prjMeetingJudge);
        for(PrjMeetingJudge judge : judgeList){
            returnList.add(judge.getJudgeUserAllocationId().toString());
        }

        return returnList;
    }

}
