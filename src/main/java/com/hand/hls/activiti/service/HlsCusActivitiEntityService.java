package com.hand.hls.activiti.service;


import com.hand.hls.exception.HlsCusException;
import org.activiti.engine.delegate.DelegateExecution;

import java.util.List;

/**
 * 工作流审批规则二开
 * Created by yy.chen on 2018/4/15
 */
public interface HlsCusActivitiEntityService {

    /**
     * 风险预警工作流提交 获取项目经理
     * @param delegateExecution
     * @return
     */
    List<String> getPrjAssistant(DelegateExecution delegateExecution);
    /**
     * 风险预警工作流提交 获取项目经理部门负责人
     * @param delegateExecution
     * @return
     */
    List<String> getPrjAssistantManager(DelegateExecution delegateExecution);

    /**
     * 获取业务协办经理
     * @param delegateExecution
     * @return
     */
    List<String> getAssistantManager(DelegateExecution delegateExecution);
    /**
     * 获取业务协办经理 项目立项 可复用 注意传入参数
     * @param delegateExecution
     * @return
     */
    List<String> getProjectAssistant(DelegateExecution delegateExecution);

    /**
     * 获取业务主办经理
     *
     * @param delegateExecution
     * @return
     */
    List<String> getProjectManager(DelegateExecution delegateExecution) throws HlsCusException;
    List<String> getProjectManager1(DelegateExecution delegateExecution) throws HlsCusException;

    /**
     * 获取业务协办经理/主办项目经理（协办或者主办）
     *
     * @param delegateExecution
     * @return
     */
    List<String> getEmployeeManager(DelegateExecution delegateExecution);

    /**
     * 获取主办项目经理（贷后）
     *
     * @param delegateExecution
     * @return
     */
    List<String> getPlmPliProjectManager(DelegateExecution delegateExecution);

        /**
         * 获取指定的信用审查人
         *
         * @param delegateExecution
         * @return
         */
    String getCreditDesignatedManager(DelegateExecution delegateExecution);

    /**
     * 获取指定秘书处
     *
     * @param delegateExecution
     * @return
     */
    String getSeco(DelegateExecution delegateExecution);

    /**
     * 获取指定的合规审查人
     *
     * @param delegateExecution
     * @return
     */
    String getComplianceDesignatedManager(DelegateExecution delegateExecution);

    /**
     * 获取指定的风控主审
     *
     * @param delegateExecution
     * @return
     */
    String getRiskDesignatedManager(DelegateExecution delegateExecution);

    /**
     * 获取指定的风控协审信息
     *
     * @param delegateExecution
     * @return
     */
    String getRiskDesignatedManagerAt(DelegateExecution delegateExecution);

    /**
     * 获取指定的法务主审
     *
     * @param delegateExecution
     * @return
     */
    String getLegalDesignatedManager(DelegateExecution delegateExecution);

    /**
     * 获取指定的法务复核信息
     *
     * @param delegateExecution
     * @return
     */
    String getLegalDesignatedManagerAt(DelegateExecution delegateExecution);

    /**
     * 项目上会获取评审会成员
     *
     * @param delegateExecution
     * @return
     */
    List<String> getMeetingJudgeMember(DelegateExecution delegateExecution);

    /**
     * 变更上会获取项目审批时评审会成员
     *
     * @param delegateExecution
     * @return
     */
    List<String> getHistoryMeetingJudgeMember(DelegateExecution delegateExecution);


    /**
     * 贷后管理获取主审
     *
     * @param delegateExecution
     * @return
     */
//    List<String> getPostLoanManager(DelegateExecution delegateExecution);

    /**
     * 获取主办所在部门的负责人
     *
     * @param delegateExecution
     * @return
     */
    String getManageDeptDirector(DelegateExecution delegateExecution);/**
     * 获取协办所在部门的负责人
     *
     * @param delegateExecution
     * @return
     */
    String getManageAssistDirector(DelegateExecution delegateExecution);

    /**
     * 获取主办所在部门的分管领导
     *
     * @param delegateExecution
     * @return
     */
    String getParentDeptManager(DelegateExecution delegateExecution);

    /**
     * 获取部门领导
     * @param delegateExecution
     * @return
     */
    List<String> getDeptDirector(DelegateExecution delegateExecution);

    /**
     * 获取部门负责人
     *
     * @param delegateExecution
     * @return
     */
    String getUnitManager(DelegateExecution delegateExecution);

    /**
     * 获取申请人部门负责人
     *
     * @param delegateExecution
     * @return
     */
    String getTransforUnitManager(DelegateExecution delegateExecution);

    /**
     * 获取接收人部门负责人
     *
     * @param delegateExecution
     * @return
     */
    String getTargetUnitManager(DelegateExecution delegateExecution);

    /**
     * 获取接收人
     *
     * @param delegateExecution
     * @return
     */
    String getTargetUser(DelegateExecution delegateExecution);


    /**
     * 获取部门领导 多个
     * @param delegateExecution
     * @return
     */
    List<String> getProjectUnitManager(DelegateExecution delegateExecution);


    /**
     * 主办和协办
     * @param delegateExecution
     * @return
     */
    List<String> getEmployeeAssignManager(DelegateExecution delegateExecution);

    /**
     * 分管领导(多个)
     *
     * @param delegateExecution
     * @return
     */
    public List<String> getManyParentDeptManager(DelegateExecution delegateExecution);

    /**
     * 指派审批人
     * @param delegateExecution
     * @return
     */
    String getAssignApprover(DelegateExecution delegateExecution);

    /**
     * 通用指派审批人
     * @param delegateExecution
     * @return
     */
    String getAssignGeneralApprover(DelegateExecution delegateExecution);

    /**
     * 项目指派审批岗
     * @param delegateExecution
     * @return
     */
    String getAppointApproverRisk(DelegateExecution delegateExecution);

    String getAppointApproverLegal(DelegateExecution delegateExecution);

    /**
     * 项目指派审批岗
     * @param delegateExecution
     * @return
     */
    String getAppointApproverPrice(DelegateExecution delegateExecution);


    /**
     *上会指定业审委委员
     * @param delegateExecution
     * @return
     */
    String getPowerfulEmployee(DelegateExecution delegateExecution);
    /**
     * 存续期副总裁
     * @param delegateExecution
     * @return
     */
    String getDurationEmployee(DelegateExecution delegateExecution);
    /**
     *存续期副总裁二
     * @param delegateExecution
     * @return
     */
    String getDurationEmployeeTwo(DelegateExecution delegateExecution);

    /**
     *付款前提条件指定副总裁
     * @param delegateExecution
     * @return
     */
    String getConditionEmployee(DelegateExecution delegateExecution);

    /**
     * 风险管理部复核岗或航空管理部复核岗
     * @param delegateExecution
     * @return
     */
    List<String> getDeptRiskOrAviation(DelegateExecution delegateExecution);

    /**
     * 风险管理部负责人或航空管理部负责人
     * @param delegateExecution
     * @return
     */
    List<String> getPrincipalRiskOrAviation(DelegateExecution delegateExecution);


    /**
     * 项目价格审查岗
     * @param delegateExecution
     * @return
     */
    List<String> getPriceApprover(DelegateExecution delegateExecution);
    String billEmployee(DelegateExecution delegateExecution);
    List<String> queryNextApprovePerson(DelegateExecution delegateExecution);

    /**
     * 查找业务分管领导
     * @param delegateExecution
     * @return
     */
    List<String> getUserParent(DelegateExecution delegateExecution);

    List<String> getPositionIgnoreCompany(String positionCode);

    List<String> getChanceAssist(DelegateExecution delegateExecution);

    List<String> getAssignByUserName(String names);

    String getReviewAppointApproverRisk(DelegateExecution delegateExecution);

    String getReviewAppointApproverLegal(DelegateExecution delegateExecution);

    List<String> getFirstRisk(DelegateExecution delegateExecution);

    List<String> getReviewRisk(DelegateExecution delegateExecution);

    List<String> getFirstRiskContract(DelegateExecution delegateExecution);

    List<String> getReviewRiskContract(DelegateExecution delegateExecution);

}
