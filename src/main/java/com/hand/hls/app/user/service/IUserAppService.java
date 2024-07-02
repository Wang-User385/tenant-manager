package com.hand.hls.app.user.service;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.dto.TaskNew;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;

/**
 * @author liao
 */
public interface IUserAppService extends IBaseService<TaskNew>, ProxySelf<IUserAppService> {


    /**
     * 根据用户id 查询用户信息
     *
     * @param jsonObject jsonObject
     * @return 返回结果集
     */
    ResponseData queryUserById(IRequest iRequest, JSONObject jsonObject) throws Exception;

    /**
     * 根据用户id 查询用户信息
     *
     * @param jsonObject jsonObject
     * @return 返回结果集
     */
    ResponseData queryUserByName(IRequest iRequest, JSONObject jsonObject) throws Exception;

    /**
     * 验证用户名密码
     *
     * @param iRequest   请求
     * @param jsonObject 请求参数
     * @return 返回
     */
    ResponseData checkLogin(IRequest iRequest, JSONObject jsonObject);


    /**
     * 根据用户id查询信息
     *
     * @param iRequest   请求头
     * @param jsonObject 用户id
     * @return 返回信息
     */
    ResponseData allocationQueryByUserId(IRequest iRequest, JSONObject jsonObject);

    /**
     * 待办事项,个人
     * 不区分角色 根据用户id查询
     * 区分的话 就使用allocationId 查询
     *
     * @param iRequest   请求信息
     * @param jsonObject 请求信息
     * @return 返回结果
     */
    ResponseData queryTask(IRequest iRequest, JSONObject jsonObject);

    /**
     * 报价计算
     *
     * @param iRequest
     * @param jsonObject
     * @return
     */
    ResponseData quotationCalc(IRequest iRequest, JSONObject jsonObject);

    /**
     * 查询报价现金流
     *
     * @param iRequest
     * @param jsonObject
     * @return
     */
    ResponseData queryCashflow(IRequest iRequest, JSONObject jsonObject);

    /**
     * 查询下拉框的值
     *
     * @param iRequest
     * @param jsonObject
     * @return
     */
    ResponseData selectComboBox(IRequest iRequest, JSONObject jsonObject);

    /**
     * 业务报表
     *
     * @param iRequest
     * @param jsonObject
     * @return
     */
    ResponseData selectBusinessReport(IRequest iRequest, JSONObject jsonObject);

    /**
     * 回款报表
     *
     * @param iRequest
     * @param jsonObject
     * @return
     */
    ResponseData selectCollectionReport(IRequest iRequest, JSONObject jsonObject);

    /**
     * 管理驾驶舱
     *
     * @param iRequest
     * @param jsonObject
     * @return
     */
    ResponseData selectCockpitReport(IRequest iRequest, JSONObject jsonObject);

    /**
     * 客户管理驾驶舱
     *
     * @param iRequest
     * @param jsonObject
     * @return
     */
    ResponseData bpCockpitReport(IRequest iRequest, JSONObject jsonObject);

    /**
     * 项目经理管理驾驶舱
     *
     * @param iRequest
     * @param jsonObject
     * @return
     */
    ResponseData projectCockpitReport(IRequest iRequest, JSONObject jsonObject);

    /**
     * 查询审批事项详情
     *
     * @param iRequest
     * @param jsonObject
     * @return
     */
    ResponseData queryWorkflowDetail(IRequest iRequest, JSONObject jsonObject);

    /**
     * 项目方案审批
     *
     * @param workProcessId
     * @return
     */
    JSONObject prjMarketingReportWfl(Long workProcessId);

    /**
     * 项目立项审批
     *
     * @param workProcessId
     * @return
     */
    JSONObject fctProjectcreateWfl(Long workProcessId);

    /**
     * 项目审查和评审流程
     *
     * @param workProcessId
     * @return
     */
    JSONObject fctProjectreviewWfl(Long workProcessId);

    /**
     * 客户信用评级审批流程
     *
     * @param workProcessId
     * @return
     */
    JSONObject customerRateWfl(Long workProcessId);

    /**
     * 租赁合同审查流程
     *
     * @param workProcessId
     * @return
     */
    JSONObject conContractCreateWfl(Long workProcessId);

    /**
     * 合同核保申请审批流程
     *
     * @param workProcessId
     * @return
     */
    JSONObject conContractSignWfl(Long workProcessId);

    /**
     * 付款申请审批流程
     *
     * @param workProcessId
     * @return
     */
    JSONObject conPaymentWfl(Long workProcessId);

    /**
     * 款项支付
     *
     * @param workProcessId
     * @return
     */
    JSONObject cshPaymentWfl(Long workProcessId);

    /**
     * 退款支付
     *
     * @param processInstanceId
     * @return
     */
    JSONObject cshTransationRefundPayWfl(Long processInstanceId);

    /**
     * 退款申请
     *
     * @param processInstanceId
     * @return
     */
    JSONObject cshTransationRefundWfl(Long processInstanceId);

    /**
     * 关税付款申请
     *
     * @param processInstanceId
     * @return
     */
    JSONObject tariffPaymentWfl(Long processInstanceId);

    /**
     * 合同支付表确认
     *
     * @param processInstanceId
     * @return
     */
    JSONObject conContractCashconform(Long processInstanceId);

    /**
     * 业务变更申请
     *
     * @param processInstanceId
     * @return
     */
    JSONObject durationWflSpecial(Long processInstanceId);

    /**
     * 合同变更执行
     *
     * @param processInstanceId
     * @return
     */
    JSONObject contractChangeWfl(Long processInstanceId);

    /**
     * 提前结清
     *
     * @param processInstanceId
     * @return
     */
    JSONObject durationWflEt(Long processInstanceId);

    /**
     * 合同终止
     *
     * @param processInstanceId
     * @return
     */
    JSONObject durationWflTerminate(Long processInstanceId);

    /**
     * 合同结束
     *
     * @param processInstanceId
     * @return
     */
    JSONObject conContractEndWfl(Long processInstanceId);

    /**
     * 合同调息
     *
     * @param processInstanceId
     * @return
     */
    JSONObject conContractInterestAdjustment(Long processInstanceId);

    /**
     * 融资提款（变更）申请流程
     *
     * @param processInstanceId
     * @return
     */
    JSONObject lonContractWithdrawWfl(Long processInstanceId);

    /**
     * 票据开具
     *
     * @param processInstanceId
     * @return
     */
    JSONObject billApplicationWfl(Long processInstanceId);

    /**
     * 资产价值重估审批流程
     *
     * @param processInstanceId
     * @return
     */
    JSONObject propertyEvaluateWfl(Long processInstanceId);

    /**
     * 资产处置审批流程
     *
     * @param processInstanceId
     * @return
     */
    JSONObject assetsDisposalWfl(Long processInstanceId);

    /**
     * 诉讼管理审批流程
     *
     * @param processInstanceId
     * @return
     */
    JSONObject lawsuitsManagementWfl(Long processInstanceId);

    /**
     * 租后检查
     *
     * @param processInstanceId
     * @return
     */
    JSONObject rentCheckWfl(Long processInstanceId);

    /**
     * 风险预警审批流程
     *
     * @param processInstanceId
     * @return
     */
    JSONObject riskWarningWfl(Long processInstanceId);

    /**
     * 租赁物巡查流程
     *
     * @param processInstanceId
     * @return
     */
    JSONObject leaseInspectWfl(Long processInstanceId);

    /**
     * 资产分类审批流程
     *
     * @param processInstanceId
     * @return
     */
    JSONObject assetsClassificationWfl(Long processInstanceId);

    /**
     * 资产分类应计提拨备审批流程
     *
     * @param processInstanceId
     * @return
     */
    JSONObject provisionShallBeMadeWfl(Long processInstanceId);

    /**
     * 通用事项审批
     *
     * @param processInstanceId
     * @return
     */
    JSONObject generalMattersWfl(Long processInstanceId);

    /**
     * 转账申请审批流程
     *
     * @param processInstanceId
     * @return
     */
    JSONObject transferApplicationWfl(Long processInstanceId);

    /**
     * 项目批复变更审批流程
     *
     * @param processInstanceId
     * @return
     */
    JSONObject fctProjectChangeWfl(Long processInstanceId);

    /**
     * 资金计划填报审批流程（业务部）
     *
     * @param processInstanceId
     * @return
     */
    JSONObject fundingPlanWflNew(Long processInstanceId);

    /**
     * 资金计划汇总审批工作流
     *
     * @param processInstanceId
     * @return
     */
    JSONObject fundingPlanWflNewZj(Long processInstanceId);

    /**
     * 保证金抵扣/退还申请审批流程
     *
     * @param processInstanceId
     * @return
     */
    JSONObject durationWflBond(Long processInstanceId);

    /**
     * 我参与的流程
     *
     * @param iRequest   请求信息
     * @param jsonObject 请求体
     * @return 返回结果集
     */
    ResponseData queryProcessInstances(IRequest iRequest, JSONObject jsonObject);

    /**
     * 待办详情查询
     *
     * @param iRequest   请求
     * @param jsonObject 请求体
     * @return 返回结果
     */
    ResponseData taskDetails(IRequest iRequest, JSONObject jsonObject);

    /**
     * app  审批 同意 拒绝
     *
     * @param iRequest   请求
     * @param jsonObject 请求体
     * @return 返回结果
     */
    ResponseData executeTaskAction(IRequest iRequest, JSONObject jsonObject);

    /**
     * app  审批  转交 、价签操作
     *
     * @param iRequest   请求
     * @param jsonObject 请求体
     * @return 返回结果
     */
    ResponseData executeTaskForward(IRequest iRequest, JSONObject jsonObject);

    /**
     * app   查看 工作流 审批流程
     *
     * @param iRequest   请求
     * @param jsonObject 请求体
     * @return 返回结果
     */
    ResponseData userTaskQusery(IRequest iRequest, JSONObject jsonObject);

    /**
     * app  工作流节点跳转
     *
     * @param iRequest   请求
     * @param jsonObject 请求体
     * @return 返回结果
     */
    ResponseData prcJump(IRequest iRequest, JSONObject jsonObject);

    /**
     * app 转交时 用户查询
     *
     * @param iRequest   请求
     * @param jsonObject 请求体
     * @return 返回结果
     */
    ResponseData queryEmployees(IRequest iRequest, JSONObject jsonObject);

    /**
     * 查询app 版本信息
     *
     * @param iRequest   请求
     * @param jsonObject 请求体
     * @return 返回结果
     */
    ResponseData queryAppVersion(IRequest iRequest, JSONObject jsonObject);

    /**
     * 查询app 版本信息
     *
     * @param iRequest   请求
     * @param jsonObject 请求体
     * @return 返回结果
     */
    ResponseData updateAppVersion(IRequest iRequest, JSONObject jsonObject);

    /**
     * app 消息通知
     *
     * @param iRequest   请求
     * @param jsonObject 请求体
     * @return 返回结果
     */

    ResponseData queryUserAllNotice(IRequest iRequest, JSONObject jsonObject);

    /**
     * app 消息已读
     *
     * @param iRequest   请求
     * @param jsonObject 请求体
     * @return 返回结果
     */
    ResponseData setRead(IRequest iRequest, JSONObject jsonObject);

    /**
     * app 消息删除
     *
     * @param iRequest   请求
     * @param jsonObject 请求体
     * @return 返回结果
     */
    ResponseData deleteNotice(IRequest iRequest, JSONObject jsonObject);

    /**
     * app 新增 指派审批人
     *
     * @param iRequest   请求
     * @param jsonObject 请求体
     * @return 返回结果
     */
    ResponseData insertDesignated(IRequest iRequest, JSONObject jsonObject);

    /**
     * app 指派审批人
     *
     * @param iRequest   请求
     * @param jsonObject 请求体
     * @return 返回结果
     */
    ResponseData queryDesignated(IRequest iRequest, JSONObject jsonObject);

    /**
     * app 指派审批人
     *
     * @param iRequest   请求
     * @param jsonObject 请求体
     * @return 返回结果
     */
    ResponseData deleteDesignated(IRequest iRequest, JSONObject jsonObject);

    /**
     * app 指派审批人
     *
     * @param iRequest   请求
     * @param jsonObject 请求体
     * @return 返回结果
     */
    ResponseData saveDesignated(IRequest iRequest, JSONObject jsonObject);

    /**
     * app 角色选择
     *
     * @param iRequest   请求
     * @param jsonObject 请求体
     * @return 返回结果
     */
    ResponseData queryUserRole(IRequest iRequest, JSONObject jsonObject);

    /**
     * app 审批历史查询
     *
     * @param iRequest   请求
     * @param jsonObject 请求体
     * @return 返回结果
     */
    ResponseData queryApprovedInfo(IRequest iRequest, JSONObject jsonObject);

    /**
     * app 个人所有未读消息
     *
     * @param iRequest   请求
     * @param jsonObject 请求体
     * @return 返回结果
     */
    ResponseData queryUnReadNotice(IRequest iRequest, JSONObject jsonObject);


}
