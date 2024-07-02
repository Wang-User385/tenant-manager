package com.hand.hap.activiti.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Throwables;
import com.hand.hap.account.dto.User;
import com.hand.hap.account.mapper.UserMapper;
import com.hand.hap.account.service.IUserService;
import com.hand.hap.activiti.core.IActivitiConstants;
import com.hand.hap.activiti.custom.ForecastActivityCmd;
import com.hand.hap.activiti.custom.GetExpressionValueCmd;
import com.hand.hap.activiti.custom.ICustomTaskProcessor;
import com.hand.hap.activiti.custom.JumpActivityCmd;
import com.hand.hap.activiti.custom.process.CustomHistoricProcessInstanceQueryRequest;
import com.hand.hap.activiti.custom.process.CustomHistoricProcessInstanceQueryResource;
import com.hand.hap.activiti.custom.task.CustomTaskQueryResource;
import com.hand.hap.activiti.dto.*;
import com.hand.hap.activiti.event.TaskRecallEvent;
import com.hand.hap.activiti.event.dto.TaskRecallInfo;
import com.hand.hap.activiti.exception.TaskActionException;
import com.hand.hap.activiti.exception.WflSecurityException;
import com.hand.hap.activiti.exception.dto.ActiviException;
import com.hand.hap.activiti.exception.mapper.ActiviExceptionMapper;
import com.hand.hap.activiti.listeners.TaskCreateNotificationListener;
import com.hand.hap.activiti.mapper.ActHiTaskinstMapper;
import com.hand.hap.activiti.mapper.HiIdentitylinkMapper;
import com.hand.hap.activiti.mapper.TaskNewMapper;
import com.hand.hap.activiti.service.IActivitiEntityService;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.activiti.service.IApproveChainHeaderService;
import com.hand.hap.activiti.util.ActivitiUtils;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.hr.dto.Employee;
import com.hand.hap.hr.service.IEmployeeService;
import com.hand.hap.message.IMessagePublisher;
import com.hand.hap.mybatis.util.StringUtil;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.ICodeService;
import com.hand.hls.activiti.components.HlsCusActivitiBeanProvider;
import com.hand.hls.activiti.components.IHlsCusActivitiBean;
import com.hand.hls.activiti.dto.HlsCusProcess;
import com.hand.hls.app.event.service.AppWflTodoNoticeService;
import com.hand.hls.bp.mapper.HlsSysDocumentHistoryBlobMapper;
import com.hand.hls.bp.mapper.HlsSysDocumentHistoryDetailMapper;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.csh.dto.ProjectCreditCondition;
import com.hand.hls.csh.service.ICshPaymentReqHdService;
import com.hand.hls.csh.service.IProjectCreditConditionService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChanceBp;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceBpMapper;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceBpService;
import com.hand.hls.fnd.dto.HlsCusEmployee;
import com.hand.hls.fnd.mapper.HlsCusEmployeeMapper;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectBp;
import com.hand.hls.prj.dto.ProjectMeetingApprover;
import com.hand.hls.prj.mapper.HlsCusPrjProjectBpMapper;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.prj.service.IProjectMeetingApproverService;
import com.hand.hls.rw.dto.HlsCusRiskWarning;
import com.hand.hls.rw.mapper.HlsCusRiskWarningMapper;
import com.hand.hls.sys.dto.HlsSystemNotice;
import com.hand.hls.sys.dto.HlsSystemNoticeOwner;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.mapper.*;
import com.hand.hls.sys.service.IHlsSystemNoticeOwnerService;
import com.hand.hls.sys.service.IHlsSystemNoticeService;
import com.hand.hls.sys.service.SysUserAllocationService;
import hls.core.sys.event.service.SysEventService;
import hls.core.sys.event.utils.SysEventCodeUtil;
import leaf.bm.components.RecordHelper;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.UserTask;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.*;
import org.activiti.engine.form.FormData;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.identity.Group;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.CommentEntityImpl;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.activiti.engine.impl.persistence.entity.data.GroupDataManager;
import org.activiti.engine.impl.persistence.entity.data.UserDataManager;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.activiti.rest.common.api.DataResponse;
import org.activiti.rest.service.api.RestResponseFactory;
import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.activiti.rest.service.api.history.HistoricProcessInstanceCollectionResource;
import org.activiti.rest.service.api.history.HistoricProcessInstanceQueryRequest;
import org.activiti.rest.service.api.history.HistoricTaskInstanceQueryRequest;
import org.activiti.rest.service.api.history.HistoricTaskInstanceQueryResource;
import org.activiti.rest.service.api.management.DeadLetterJobCollectionResource;
import org.activiti.rest.service.api.runtime.process.ExecutionVariableCollectionResource;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCollectionResource;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceResponse;
import org.activiti.rest.service.api.runtime.task.TaskActionRequest;
import org.activiti.rest.service.api.runtime.task.TaskQueryRequest;
import org.activiti.rest.service.api.runtime.task.TaskResource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.hand.hap.core.util.CommonUtils.indexOf;

/**
 * @author shengyang.zhou@hand-china.com
 * @author njq.niu@hand-china.com
 */
@Service
@Transactional
public class ActivitiServiceImpl implements IActivitiService, IActivitiConstants, InitializingBean {

    private static final String WORK_FLOW_TYPE = "workFlowType";
    private static final String BUSINESS_KEY = "BUSINESS_KEY";
    public static final String DURATION_APPROVED = "DURATION_APPROVED";

    private final String SYS_MODULE = "WFL";

    private final String ADD_AND_ASSIGN = "addAssign";

    private final String ADD_APPOINT = "APPOINT";
    private final String APPROVED_GENERAL = "APPROVED_GENERAL";

    private final String POWERFUL_APPROVED = "POWERFUL_APPROVED";

    private final String END_TASK_APPROVED = "END_TASK_APPROVED";

    private final String PAY_END_TASK_APPROVED = "PAY_END_TASK_APPROVED";

    private final String CONDITION_APPROVED = "CONDITION_APPROVED";

    private final String WITHDRAW = "WITHDRAW";

    private final String REVIEW = "REVIEW";

    private final String APPROVED_RETURN = "APPROVED_RETURN";

    private final String WARRANT_IN_STOCK = "WARRANT_IN_STOCK";

    private final String WARRANT_OUT_STOCK = "WARRANT_OUT_STOCK";
    private static final String DOCUMENT_CATEGORY_BP = "HLS_BP_MASTER_CHANGE";
    private static final String DOCUMENT_CATEGORY_CON = "CONTRACT_CHANGE";

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private GroupDataManager groupDataManager;

    @Autowired
    private UserDataManager userDataManager;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private FormService formService;

    @Autowired
    private HlsSysDocumentHistoryDetailMapper documentHistoryDetailMapper;
    @Autowired
    private HlsSysDocumentHistoryBlobMapper documentHistoryBlobMapper;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RestResponseFactory restResponseFactory;

    @Autowired
    private ProcessEngineConfigurationImpl processEngineConfiguration;

    @Autowired
    private ActiviExceptionMapper exceptionMapper;

    @Autowired
    private SysUserAllocationMapper sysUserAllocationMapper;

    @Autowired
    private IEmployeeService employeeService;

    @Autowired
    private SysUserAllocationService sysUserAllocationService;

    @Autowired
    private IUserService userService;

    @Autowired
    @Qualifier(value = "activitiUserServiceImpl")
    private IActivitiEntityService entityService;

    @Autowired
    private ForecastActivityCmd forecastActivityCmd;

    @Autowired
    private HiIdentitylinkMapper hiIdentitylinkMapper;

    @Autowired
    private TaskCreateNotificationListener taskCreateNotificationListener;

    @Autowired
    private IApproveChainHeaderService approveChainHeaderService;
    @Autowired
    private HlsCusHlsCreditLineChanceBpMapper hlsCusHlsCreditLineChanceBpMapper;

    @Autowired
    private IMessagePublisher messagePublisher;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private IHlsSystemNoticeService hlsSystemNoticeService;
    @Autowired
    private HlsCusPrjProjectBpMapper hlsCusPrjProjectBpMapper;
    @Autowired
    private IHlsSystemNoticeOwnerService hlsSystemNoticeOwnerService;
    @Autowired
    private SysEventService sysEventService;
    @Autowired
    private TaskNewMapper taskNewMapper;
    @Autowired
    private FndEmployeeMapper fndEmployeeMapper;
    @Autowired
    private ActHiTaskinstMapper actHiTaskinstMapper;
    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;

    @Autowired
    private HlsCusActivitiBeanProvider hlsCusActivitiBeanProvider;

    @Autowired
    private HlsCusEmployeeMapper hlsCusEmployeeMapper;
    @Autowired
    private HlsCusHlsCreditLineChanceBpService creditLineChanceBpService;
    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private IProjectMeetingApproverService projectMeetingApproverService;

    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private ICshPaymentReqHdService cshPaymentReqHdService;
    @Autowired
    private HlsCusRiskWarningMapper hlsCusRiskWarningMapper;
    @Autowired
    private ICodeService codeService;

    /* 以下 为 手动注入的 bean */
    private TaskResource taskResource = new TaskResource();

    private ExecutionVariableCollectionResource executionVariableCollectionResource = new ExecutionVariableCollectionResource();

    private CustomTaskQueryResource taskQueryResource = new CustomTaskQueryResource();

    private CustomHistoricProcessInstanceQueryResource historicProcessInstanceQueryResource = new CustomHistoricProcessInstanceQueryResource();

    private ProcessInstanceCollectionResource processInstanceCollectionResource = new ProcessInstanceCollectionResource();

    private HistoricTaskInstanceQueryResource historicTaskInstanceQueryResource = new HistoricTaskInstanceQueryResource();

    private HistoricProcessInstanceCollectionResource historicProcessInstanceCollectionResource = new HistoricProcessInstanceCollectionResource();

    private DeadLetterJobCollectionResource deadLetterJobCollectionResource = new DeadLetterJobCollectionResource();

    /* Fake request,response,used to call rest api */
    private HttpServletRequest fakeRequest = new MockHttpServletRequest();
    private HttpServletResponse fakeResponse = new MockHttpServletResponse();

    private List<ICustomTaskProcessor> taskProcessors;

    @Autowired
    private AppWflTodoNoticeService appWflTodoNoticeService;
    @Autowired
    private UserMapper userMapper;
//    @Autowired
//    private TaskNewMapper taskNewMapper;
    private String riskName;
    private String riskId;
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public ProcessInstanceResponse startProcess(IRequest iRequest, ProcessInstanceCreateRequest createRequest) {
        try {
            //修改使用allocationId作为activiti用户标识
            Long allocationId = iRequest.getAttribute("allocationId");
            Authentication.setAuthenticatedUserId(String.valueOf(allocationId));
            return processInstanceCollectionResource.createProcessInstance(createRequest, fakeRequest, fakeResponse);
        } finally {
            Authentication.setAuthenticatedUserId(null);
        }
    }

    public DataResponse getInvolvedProcess(IRequest request, Map<String, String> allParameters) {
        return processInstanceCollectionResource.getProcessInstances(allParameters, fakeRequest);
    }

    @Override
    public Model deployModel(String modelId) throws Exception {
        Model model = repositoryService.getModel(modelId);

        byte[] modelData = repositoryService.getModelEditorSource(modelId);
        JsonNode jsonNode = objectMapper.readTree(modelData);
        BpmnModel bpmnModel = new BpmnJsonConverter().convertToBpmnModel(jsonNode);

        // byte[] xmlBytes = new BpmnXMLConverter().convertToXML(bpmnModel,
        // "UTF-8");

        Deployment deploy = repositoryService.createDeployment().category(model.getCategory()).name(model.getName())
                .key(model.getKey()).addBpmnModel(model.getKey() + ".bpmn20.xml", bpmnModel).deploy();

        model.setDeploymentId(deploy.getId());
        repositoryService.saveModel(model);
        return model;

    }

    @Override
    public void completeTask(IRequest request, Task taskEntity, TaskActionRequestExt actionRequest)
            throws TaskActionException {
        if (!TaskActionRequest.ACTION_COMPLETE.equalsIgnoreCase(actionRequest.getAction())) {
            return;
        }
        Long allocationId = request.getAttribute("allocationId");
        Authentication.setAuthenticatedUserId(String.valueOf(allocationId));
        String action = null;// 本次操作执行的动作
        String actionDesc = null;
        String taskId = taskEntity.getId();
        List<RestVariable> vars = actionRequest.getVariables();
        if (vars != null) {
            for (RestVariable rv : vars) {
                if (PROP_APPROVE_RESULT.equalsIgnoreCase(rv.getName())) {
                    action = String.valueOf(rv.getValue());
                }
                if("approveResultDesc".equalsIgnoreCase(rv.getName())){
                    if(rv.getValue() != null) {
                        actionDesc = String.valueOf(rv.getValue());
                    }
                }
            }
        }
        actionRequest.setAssignee(String.valueOf(request.getUserId()));
        taskService.addComment(taskId, taskEntity.getProcessInstanceId(), COMMENT_ACTION, action);
        taskService.addComment(taskId, taskEntity.getProcessInstanceId(), "actionDesc", actionDesc);
        taskService.addComment(taskId, taskEntity.getProcessInstanceId(), PROP_COMMENT, actionRequest.getComment() == null ? "" : actionRequest.getComment());
        taskResource.executeTaskAction(taskId, actionRequest);
        String s = taskNewMapper.queryDocumentName(taskEntity.getProcessInstanceId());
        if(StringUtils.isNotEmpty(s)&&s.contains("风险预警")){
            riskId=s.substring(s.indexOf("Y"));
            if("REJECTED".equals(action)){
                HlsCusRiskWarning hlsCusRiskWarning=new HlsCusRiskWarning();
                hlsCusRiskWarning.setRiskWarningNumber(riskId);
                hlsCusRiskWarning.setStatus("REJECTED");
                hlsCusRiskWarning.setSubmitStatus("F");
                hlsCusRiskWarningMapper.updateRiskWarningStatusByRiskWarningNumber(hlsCusRiskWarning);
            }
            if(("风险管理部负责人审核".equals(taskEntity.getName()))&&("APPROVED".equals(action))){
                HlsCusRiskWarning hlsCusRiskWarning=new HlsCusRiskWarning();
                hlsCusRiskWarning.setRiskWarningNumber(riskId);
                hlsCusRiskWarning.setStatus("APPROVED");
                hlsCusRiskWarning.setSubmitStatus("F");
                hlsCusRiskWarningMapper.updateRiskWarningStatusByRiskWarningNumber(hlsCusRiskWarning);
            }
        }
        if (StringUtils.isNotEmpty(actionRequest.getAction())) {
            taskCreateNotificationListener.sendMessage(actionRequest.getAssignee());
        }
        taskCreateNotificationListener.sendMessage(taskEntity.getAssignee());
        /*
         * if (StringUtils.isEmpty(taskEntity.getAssignee())) {
         * actionRequest.setAssignee(String.valueOf(request.getUserId())); // 自动 claim
         * taskService.claim(taskId, String.valueOf(request.getUserId()));
         * taskService.addComment(taskId, taskEntity.getProcessInstanceId(),
         * COMMENT_ACTION, action); taskService.addComment(taskId,
         * taskEntity.getProcessInstanceId(), PROP_COMMENT, actionRequest.getComment());
         * taskResource.executeTaskAction(taskId, actionRequest); } else {
         * actionRequest.setAssignee(String.valueOf(request.getUserId()));
         * taskService.addComment(taskId, taskEntity.getProcessInstanceId(),
         * COMMENT_ACTION, action); taskService.addComment(taskId,
         * taskEntity.getProcessInstanceId(), PROP_COMMENT, actionRequest.getComment());
         * taskResource.executeTaskAction(taskId, actionRequest); }
         */
    }

    @Override
    public void delegateTask(IRequest request, Task taskEntity, TaskActionRequestExt actionRequest)
            throws TaskActionException {
        if (!TaskActionRequest.ACTION_DELEGATE.equalsIgnoreCase(actionRequest.getAction())) {
            return;
        }
        Long allocationId = request.getAttribute("allocationId");
        Authentication.setAuthenticatedUserId(String.valueOf(allocationId));
        String assignee = actionRequest.getAssignee();
        if (StringUtils.isEmpty(assignee)) {
            throw new TaskActionException(TaskActionException.DELEGATE_NO_ASSIGNEE);
        }

        String taskId = taskEntity.getId();
        String message = getEmployeeName(String.valueOf((Long) request.getAttribute("allocationId"))) + "转交给" + getEmployeeName(assignee) + "  " + actionRequest.getComment();
        taskService.addComment(taskId, taskEntity.getProcessInstanceId(), COMMENT_ACTION, DELEGATE);
        taskService.addComment(taskId, taskEntity.getProcessInstanceId(), PROP_COMMENT, message);
        /*
         * taskService.addComment(taskId, taskEntity.getProcessInstanceId(),
         * COMMENT_DELEGATE_BY, actionRequest.getComment());
         */
        taskService.setAssignee(taskId, assignee);
        taskCreateNotificationListener.sendMessage(taskEntity.getAssignee());
        taskCreateNotificationListener.sendMessage(assignee);


//        转交待办通知
        User assigUser = userService.queryUserByAllocationId(assignee);
        if (assigUser == null) {
            throw new TaskActionException(TaskActionException.DELEGATE_NO_ASSIGNEE);
        }
        Map params = new HashMap();
        String url = "/MYWFL/MYWFL001/task_detail.lview?taskId=" + taskEntity.getId() + "&processInstanceId=" + taskEntity.getProcessInstanceId();
        request.setUserId(assigUser.getUserId());
        params.put("message", message);
        params.put("user", assigUser);
        params.put("taskId", taskEntity.getId());
        params.put("noticeTitle", "工作流转交");
        params.put("level", 1);
        params.put("noticeType", "TODO");
        params.put("url", url);
        this.sysEventService.eventSave(request, Long.valueOf(taskEntity.getProcessInstanceId()), "AUTO_DELEGATE", "AUTO_DELEGATE", "WFL", "WFL.TO_DO", "D2D", params);

        /*
         * DelegationState state = taskEntity.getDelegationState(); if (state != null &&
         * state == DelegationState.PENDING) { // 正在转交中 throw new
         * TaskActionException(TaskActionException.DELEGATE_IN_PENDING); }
         *
         * if (taskEntity.getOwner() != null) {
         *
         * if (eq(taskEntity.getOwner(), assignee)) { throw new
         * TaskActionException(TaskActionException.DELEGATE_TO_OWNER); }
         *
         * if (!hasRight(request.getEmployeeCode(), taskEntity.getOwner())) { throw new
         * TaskActionException(TaskActionException.DELEGATE_NEED_OWNER_OR_ADMIN); } }
         *
         * taskEntity.setOwner(assignee);// change owner when delegate
         * taskService.saveTask(taskEntity);
         *
         * if (StringUtils.isEmpty(taskEntity.getAssignee())) { //
         * actionRequest.setAssignee(request.getEmployeeCode());
         * taskService.addComment(taskId, taskEntity.getProcessInstanceId(),
         * COMMENT_DELEGATE_BY, actionRequest.getComment());
         * taskResource.executeTaskAction(taskId, actionRequest); } else if
         * (hasRight(request.getEmployeeCode(), taskEntity.getAssignee())) { //
         * actionRequest.setAssignee(request.getEmployeeCode());
         * taskService.addComment(taskId, taskEntity.getProcessInstanceId(),
         * COMMENT_DELEGATE_BY, actionRequest.getComment());
         * taskResource.executeTaskAction(taskId, actionRequest); } else { throw new
         * TaskActionException(TaskActionException.COMPLETE_TASK_NEED_ASSIGNEE_OR_ADMIN)
         * ; }
         */
    }

    @Override
    public void resolveTask(IRequest request, Task taskEntity, TaskActionRequestExt actionRequest)
            throws TaskActionException {
        if (!TaskActionRequest.ACTION_RESOLVE.equalsIgnoreCase(actionRequest.getAction())) {
            return;
        }
        Long allocationId = request.getAttribute("allocationId");
        Authentication.setAuthenticatedUserId(String.valueOf(allocationId));

        String taskId = taskEntity.getId();

        /*
         * if (!hasRight(request.getEmployeeCode(), taskEntity.getOwner())) { throw new
         * TaskActionException(TaskActionException.RESOLVE_NEED_OWNER_OR_ADMIN); }
         * taskResource.executeTaskAction(taskId, actionRequest);
         */
        String action = null;// 本次操作执行的动作

        List<RestVariable> vars = actionRequest.getVariables();
        if (vars != null) {
            for (RestVariable rv : vars) {
                if (PROP_APPROVE_RESULT.equalsIgnoreCase(rv.getName())) {
                    action = String.valueOf(rv.getValue());
                    break;
                }
            }
        }
        taskService.addComment(taskId, taskEntity.getProcessInstanceId(), COMMENT_ACTION, action);
        taskService.addComment(taskId, taskEntity.getProcessInstanceId(), PROP_COMMENT, actionRequest.getComment());
        taskService.resolveTask(taskId);
        taskCreateNotificationListener.sendMessage(taskEntity.getOwner());
        taskCreateNotificationListener.sendMessage(taskEntity.getAssignee());
    }

    public void addSignTask(IRequest request, Task taskEntity, TaskActionRequestExt actionRequest) {
        if (!ACTION_ADD_SIGN.equalsIgnoreCase(actionRequest.getAction())) {
            return;
        }
        Long allocationId = request.getAttribute("allocationId");
        Authentication.setAuthenticatedUserId(String.valueOf(allocationId));
        String assignee = actionRequest.getAssignee();

        String taskId = taskEntity.getId();
        taskService.addComment(taskId, taskEntity.getProcessInstanceId(), COMMENT_ACTION, ADD_SIGN);
        taskService.addComment(taskId, taskEntity.getProcessInstanceId(), PROP_COMMENT, actionRequest.getComment());
        taskService.delegateTask(taskId, assignee);
        taskCreateNotificationListener.sendMessage(assignee);
        taskCreateNotificationListener.sendMessage(taskEntity.getAssignee());
    }


    public void addAssignTask(IRequest request, Task taskEntity, TaskActionRequestExt actionRequest) throws TaskActionException {
/*
        if (!ADD_AND_ASSIGN.equalsIgnoreCase(actionRequest.getAction())) {
            return;
        }
        Long allocationId = Long.valueOf(request.getAttribute("assign"));
//        Authentication.setAuthenticatedUserId(String.valueOf(allocationId));
        String assignee = actionRequest.getAssignee();

        String taskId = taskEntity.getId();
        taskService.addComment(taskId, taskEntity.getProcessInstanceId(), COMMENT_ACTION, ADD_AND_ASSIGN);
        taskService.addComment(taskId, taskEntity.getProcessInstanceId(), PROP_COMMENT, actionRequest.getComment());
//        taskService.delegateTask(taskId, assignee);
        taskCreateNotificationListener.sendMessage(assignee);
        taskCreateNotificationListener.sendMessage(taskEntity.getAssignee());


        //构造actionRequest
        List<RestVariable> variables = new ArrayList<RestVariable>();
        RestVariable restVariable1 = new RestVariable();
        restVariable1.setName("approveResult");
        restVariable1.setValue("APPROVED");
        variables.add(restVariable1);

        RestVariable restVariable2= new RestVariable();
        restVariable2.setName("approveResultDesc");
        restVariable2.setValue("提交");
        variables.add(restVariable2);

        RestVariable restVariable3 = new RestVariable();
        restVariable3.setName("comment");
        restVariable3.setValue(null);
        variables.add(restVariable3);

        RestVariable restVariable4 = new RestVariable();
        restVariable4.setName("assign");
        restVariable4.setValue(allocationId);
        variables.add(restVariable4);

        actionRequest.setVariables(variables);
        actionRequest.setAction("complete");

        activitiService.executeTaskAction(request, taskId, actionRequest, true);

*/

        if (!ADD_AND_ASSIGN.equalsIgnoreCase(actionRequest.getAction())) {
            return;
        }
        Long allocationId =  Long.valueOf(request.getAttribute("assign"));
        String taskId = taskEntity.getId();
        String assignee = actionRequest.getAssignee();
        taskCreateNotificationListener.sendMessage(assignee);
        taskCreateNotificationListener.sendMessage(taskEntity.getAssignee());


        Map<String, Object> vMap = new HashMap<String, Object>(20);
        vMap.put("approveResult", "addAssign");
        vMap.put("approveResultResult", "指派");
        vMap.put("assign", allocationId);
        // 添加审批备注
        taskService.addComment(taskId, taskEntity.getProcessInstanceId(), IActivitiConstants.COMMENT_ACTION, "addAssign");
        taskService.addComment(taskId, taskEntity.getProcessInstanceId(), IActivitiConstants.PROP_COMMENT, actionRequest.getComment());
        taskService.complete(taskId, vMap);
    }


    public void addAppointTask(IRequest request, Task taskEntity, TaskActionRequestExt actionRequest) throws TaskActionException {
        if (!ADD_APPOINT.equalsIgnoreCase(actionRequest.getAction())) {
            return;
        }
        Long allocationIdRisk = Long.valueOf(request.getAttribute("appointRisk"));
        //Long allocationIdPrice = Long.valueOf(request.getAttribute("appointPrice"));
        String taskId = taskEntity.getId();

        //保存审查人
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        String requestData = request.getAttributeMap().get("_request_data").toString();
        JSONObject jsonObject = JSONObject.parseObject(requestData);
        JSONArray param = (JSONArray)jsonObject.get("parameter");
        JSONObject param0 = (JSONObject)param.get(0);

        String documentCategory = String.valueOf(param0.get("documentCategory"));
        if(WARRANT_IN_STOCK.equalsIgnoreCase(documentCategory) || WARRANT_OUT_STOCK.equalsIgnoreCase(documentCategory) ){
            //权证指派 ，不操作

        }else{
            Long projectId = Long.parseLong(param0.get("projectId").toString());
            hlsCusPrjProject.setProjectId(projectId);
            hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(request,hlsCusPrjProject);
            if(hlsCusPrjProject != null){
                hlsCusPrjProject.setAllocationIdRisk(allocationIdRisk);
                hlsCusPrjProjectService.updateByPrimaryKeySelective(request,hlsCusPrjProject);
            }

        }


        Map<String, Object> vMap = new HashMap<String, Object>(2);
        vMap.put("approveResult", "APPOINT");
        vMap.put("approveResultResult", "指派");
        vMap.put("allocationIdRisk", allocationIdRisk);
        //vMap.put("allocationIdPrice", allocationIdPrice);
        // 添加审批备注
        taskService.addComment(taskId, taskEntity.getProcessInstanceId(), IActivitiConstants.COMMENT_ACTION, "APPOINT");
        taskService.addComment(taskId, taskEntity.getProcessInstanceId(), IActivitiConstants.PROP_COMMENT, actionRequest.getComment());
        taskService.complete(taskId, vMap);

    }

    public void approvalGeneralTask(IRequest request, Task taskEntity, TaskActionRequestExt actionRequest) throws TaskActionException {
        if (!APPROVED_GENERAL.equalsIgnoreCase(actionRequest.getAction())) {
            return;
        }
        Long allocationIdRisk = Long.valueOf(request.getAttribute("appointRisk"));
        //Long allocationIdPrice = Long.valueOf(request.getAttribute("appointPrice"));
        String taskId = taskEntity.getId();
        String requestData = request.getAttributeMap().get("_request_data").toString();
        JSONObject jsonObject = JSONObject.parseObject(requestData);
        JSONArray param = (JSONArray)jsonObject.get("parameter");
        JSONObject param0 = (JSONObject)param.get(0);
        String documentCategory = String.valueOf(param0.get("documentCategory"));

        Map<String, Object> vMap = new HashMap<String, Object>(2);
        vMap.put("approveResult", "APPROVED_GENERAL");
        vMap.put("approveResultResult", "同意");
        vMap.put("allocationId", allocationIdRisk);
        vMap.put("assign", allocationIdRisk);
        //vMap.put("allocationIdPrice", allocationIdPrice);
        // 添加审批备注
        taskService.addComment(taskId, taskEntity.getProcessInstanceId(), IActivitiConstants.COMMENT_ACTION, "APPOINT");
        taskService.addComment(taskId, taskEntity.getProcessInstanceId(), IActivitiConstants.PROP_COMMENT, actionRequest.getComment());
        taskService.complete(taskId, vMap);

    }

    public void addPowerfulPersonTask(IRequest request, Task taskEntity, TaskActionRequestExt actionRequest) throws TaskActionException {
        if (!POWERFUL_APPROVED.equalsIgnoreCase(actionRequest.getAction())) {
            return;
        }
        String powerfulPerson = request.getAttribute("powerfulPerson");
        String powerfulEmployee = request.getAttribute("powerfulEmployee");
        String taskId = taskEntity.getId();

        //保存有权人
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        String requestData = request.getAttributeMap().get("_request_data").toString();
        JSONObject jsonObject = JSONObject.parseObject(requestData);
        JSONArray param = (JSONArray) jsonObject.get("parameter");
        JSONObject param0 = (JSONObject) param.get(0);
        Long projectId = Long.parseLong(param0.get("projectId").toString());
        hlsCusPrjProject.setProjectId(projectId);
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(request, hlsCusPrjProject);
        hlsCusPrjProject.setPowerfulPerson(powerfulPerson);
        hlsCusPrjProjectService.updateByPrimaryKeySelective(request, hlsCusPrjProject);


        Map<String, Object> vMap = new HashMap<String, Object>(2);
        vMap.put("approveResult", "APPROVED");
        vMap.put("approveResultResult", "同意");
        vMap.put("powerfulPerson", powerfulPerson);
        vMap.put("powerfulEmployee", powerfulEmployee);
        //vMap.put("allocationIdPrice", allocationIdPrice);
        // 添加审批备注
        taskService.addComment(taskId, taskEntity.getProcessInstanceId(), IActivitiConstants.COMMENT_ACTION, "APPROVED");
        taskService.addComment(taskId, taskEntity.getProcessInstanceId(), IActivitiConstants.PROP_COMMENT, actionRequest.getComment());
        taskService.complete(taskId, vMap);
    }
    public void addDurationPersonTask(IRequest request, Task taskEntity, TaskActionRequestExt actionRequest) throws TaskActionException {
        if (!DURATION_APPROVED.equalsIgnoreCase(actionRequest.getAction())) {
            return;
        }
        String durationPerson = request.getAttribute("durationPerson");
        String durationEmployee = request.getAttribute("durationEmployee");
        String durationEmployeeN = request.getAttribute("durationEmployeeN");
        String durationEmployeeTwo = request.getAttribute("durationEmployeeTwo");
        String durationEmployeeTwoN = request.getAttribute("durationEmployeeTwoN");
        String taskId = taskEntity.getId();

        //保存有权人
        /*HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        String requestData = request.getAttributeMap().get("_request_data").toString();
        JSONObject jsonObject = JSONObject.parseObject(requestData);
        JSONArray param = (JSONArray) jsonObject.get("parameter");
        JSONObject param0 = (JSONObject) param.get(0);
        Long projectId = Long.parseLong(param0.get("projectId").toString());
        hlsCusPrjProject.setProjectId(projectId);
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(request, hlsCusPrjProject);
        hlsCusPrjProject.setPowerfulPerson(powerfulPerson);
        hlsCusPrjProjectService.updateByPrimaryKeySelective(request, hlsCusPrjProject);*/


        Map<String, Object> vMap = new HashMap<String, Object>(2);
        vMap.put("approveResult", "DURATION_APPROVED");
        vMap.put("approveResultResult", "同意");
        vMap.put("durationPerson", durationPerson);
        vMap.put("durationEmployee", durationEmployee);
        vMap.put("durationEmployeeN", durationEmployeeN);
        vMap.put("durationEmployeeTwo", durationEmployeeTwo);
        vMap.put("durationEmployeeTwoN", durationEmployeeTwoN);
        //vMap.put("allocationIdPrice", allocationIdPrice);
        // 添加审批备注
        taskService.addComment(taskId, taskEntity.getProcessInstanceId(), IActivitiConstants.COMMENT_ACTION, "APPROVED");
        taskService.addComment(taskId, taskEntity.getProcessInstanceId(), IActivitiConstants.PROP_COMMENT, actionRequest.getComment());
        taskService.complete(taskId, vMap);
    }
    public void chooesConEndTask(IRequest request, Task taskEntity, TaskActionRequestExt actionRequest) throws TaskActionException {
        if (!END_TASK_APPROVED.equalsIgnoreCase(actionRequest.getAction())) {
            return;
        }
        String conEndTask = request.getAttribute("conEndTask");
        String taskId = taskEntity.getId();

        //保存结束节点
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        String requestData = request.getAttributeMap().get("_request_data").toString();
        JSONObject jsonObject = JSONObject.parseObject(requestData);
        JSONArray param = (JSONArray) jsonObject.get("parameter");
        JSONObject param0 = (JSONObject) param.get(0);
        Long projectId = Long.parseLong(param0.get("projectId").toString());
        hlsCusPrjProject.setProjectId(projectId);
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(request, hlsCusPrjProject);
        hlsCusPrjProject.setConEndTask(conEndTask);
        hlsCusPrjProjectService.updateByPrimaryKeySelective(request, hlsCusPrjProject);

        Map<String, Object> vMap = new HashMap<String, Object>(2);
        vMap.put("approveResult", "APPROVED");
        vMap.put("approveResultResult", "同意");
        vMap.put("conEndTask", conEndTask);
        //vMap.put("allocationIdPrice", allocationIdPrice);
        // 添加审批备注
        taskService.addComment(taskId, taskEntity.getProcessInstanceId(), IActivitiConstants.COMMENT_ACTION, "APPROVED");
        taskService.addComment(taskId, taskEntity.getProcessInstanceId(), IActivitiConstants.PROP_COMMENT, actionRequest.getComment());
        taskService.complete(taskId, vMap);
    }
    public void chooesPayEndTask(IRequest request, Task taskEntity, TaskActionRequestExt actionRequest) throws TaskActionException {
        if (!PAY_END_TASK_APPROVED.equalsIgnoreCase(actionRequest.getAction())) {
            return;
        }
        String conEndTask = request.getAttribute("conEndTask");
        String taskId = taskEntity.getId();

        //保存结束节点
        HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd = new HlsCusCshPaymentReqHd();
        String requestData = request.getAttributeMap().get("_request_data").toString();
        JSONObject jsonObject = JSONObject.parseObject(requestData);
        JSONArray param = (JSONArray) jsonObject.get("parameter");
        JSONObject param0 = (JSONObject) param.get(0);
        Long paymentReqId = Long.parseLong(param0.get("paymentReqId").toString());
        hlsCusCshPaymentReqHd.setPaymentReqId(paymentReqId);
        hlsCusCshPaymentReqHd = cshPaymentReqHdService.selectByPrimaryKey(request, hlsCusCshPaymentReqHd);
        hlsCusCshPaymentReqHd.setConEndTask(conEndTask);
        cshPaymentReqHdService.updateByPrimaryKeySelective(request, hlsCusCshPaymentReqHd);

        Map<String, Object> vMap = new HashMap<String, Object>(2);
        vMap.put("approveResult", "PAY_END_TASK_APPROVED");
        vMap.put("approveResultResult", "同意");
        vMap.put("conEndTask", conEndTask);
        //vMap.put("allocationIdPrice", allocationIdPrice);
        // 添加审批备注
        taskService.addComment(taskId, taskEntity.getProcessInstanceId(), IActivitiConstants.COMMENT_ACTION, "APPROVED");
        taskService.addComment(taskId, taskEntity.getProcessInstanceId(), IActivitiConstants.PROP_COMMENT, actionRequest.getComment());
        taskService.complete(taskId, vMap);
    }

    @Autowired
    private IProjectCreditConditionService projectCreditConditionService;

    public void chooseConditionTask(IRequest request, Task taskEntity, TaskActionRequestExt actionRequest) throws TaskActionException {
        if (!CONDITION_APPROVED.equalsIgnoreCase(actionRequest.getAction())) {
            return;
        }
        String endTaskNodeId = request.getAttribute("endTaskNodeId");
        String creditConditionIds = request.getAttribute("creditConditionIds");
        String conditionEmployee = request.getAttribute("conditionEmployee");
        String taskId = taskEntity.getId();

        //保存结束节点
        String requestData = request.getAttributeMap().get("_request_data").toString();
        JSONObject jsonObject = JSONObject.parseObject(requestData);
        JSONArray param = (JSONArray)jsonObject.get("parameter");
        JSONObject param0 = (JSONObject)param.get(0);
        Long projectId = Long.parseLong(param0.get("projectId").toString());

        List<String> ids = Arrays.asList(creditConditionIds.split(","));
        ids.forEach(item->{
            ProjectCreditCondition projectCreditCondition = new ProjectCreditCondition();
            projectCreditCondition.setCreditConditionId(Long.parseLong(item));
            projectCreditCondition.setEndTaskNodeId(endTaskNodeId);
            projectCreditConditionService.updateByPrimaryKeySelective(request, projectCreditCondition);
        });

        Map<String, Object> vMap = new HashMap<String, Object>(2);
        vMap.put("approveResult", "CONDITION_APPROVED");
        vMap.put("approveResultResult", "同意");
        vMap.put("endTaskNodeId", endTaskNodeId);
        vMap.put("conditionEmployee", conditionEmployee);
        //vMap.put("allocationIdPrice", allocationIdPrice);
        // 添加审批备注
        taskService.addComment(taskId, taskEntity.getProcessInstanceId(), IActivitiConstants.COMMENT_ACTION, "APPROVED");
        taskService.addComment(taskId, taskEntity.getProcessInstanceId(), IActivitiConstants.PROP_COMMENT, actionRequest.getComment());
        taskService.complete(taskId, vMap);
    }

    public void withdrawTask(IRequest request, Task taskEntity, TaskActionRequestExt actionRequest) throws TaskActionException {
        if (!WITHDRAW.equalsIgnoreCase(actionRequest.getAction())) {
            return;
        }
        String requestData = request.getAttributeMap().get("_request_data").toString();
        JSONObject jsonObject = JSONObject.parseObject(requestData);
        JSONArray param = (JSONArray)jsonObject.get("parameter");
        JSONObject param0 = (JSONObject)param.get(0);
        Long projectId = Long.parseLong(param0.get("projectId").toString());
        //LeafRequestData _request_data = JSONObject.toJavaObject(jsonObject, LeafRequestData.class) ;
        //Long projectId = Long.parseLong(_request_data.getParameter().get("projectId").toString());
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        hlsCusPrjProject.setApprovalStatus("CANCEL");
        hlsCusPrjProjectService.updateByPrimaryKeySelective(request,hlsCusPrjProject);
        String taskId = taskEntity.getId();
        ProjectMeetingApprover projectMeetingApprover = new ProjectMeetingApprover();
        projectMeetingApprover.setProjectId(hlsCusPrjProject.getProjectId());
        projectMeetingApprover.setApprovalId(hlsCusPrjProject.getApprovalId());
        List<ProjectMeetingApprover> projectMeetingApproverList = projectMeetingApproverService.queryAllByProjectId(request,projectMeetingApprover);
        //List<ProjectMeetingApprover> projectMeetingApproverList = new ArrayList<>();
        //projectMeetingApproverService
        /*for(ProjectMeetingApprover dt:projectMeetingApproverListOld){
            dt.setApproveStatus("CANCEL");
            projectMeetingApproverList.add(dt);
        }*/
        if(projectMeetingApproverList.size()>0){
            projectMeetingApproverService.batchDelete(projectMeetingApproverList);
        }


        Map<String, Object> vMap = new HashMap<String, Object>(2);
        vMap.put("approveResult", "WITHDRAW");
        vMap.put("approveResultResult", "项目撤回");
        vMap.put("withdrawFlag", "Y");
        // 添加审批备注
        taskService.addComment(taskId, taskEntity.getProcessInstanceId(), IActivitiConstants.COMMENT_ACTION, "WITHDRAW");
        taskService.addComment(taskId, taskEntity.getProcessInstanceId(), IActivitiConstants.PROP_COMMENT, actionRequest.getComment());
        taskService.complete(taskId, vMap);

    }

    public void approvedReturnTask(IRequest request, Task taskEntity, TaskActionRequestExt actionRequest) throws TaskActionException {
        if (!APPROVED_RETURN.equalsIgnoreCase(actionRequest.getAction())) {
            return;
        }
        String requestData = request.getAttributeMap().get("_request_data").toString();
        JSONObject jsonObject = JSONObject.parseObject(requestData);
        JSONArray param = (JSONArray)jsonObject.get("parameter");
        JSONObject param0 = (JSONObject)param.get(0);

        String taskId = taskEntity.getId();



        Map<String, Object> vMap = new HashMap<String, Object>(2);
        vMap.put("approveResult", "APPROVED_RETURN");
        vMap.put("approveResultResult", "退回项目经理修改");
        vMap.put("withdrawFlag", "Y");
        // 添加审批备注
        taskService.addComment(taskId, taskEntity.getProcessInstanceId(), IActivitiConstants.COMMENT_ACTION, "APPROVED_RETURN");
        taskService.addComment(taskId, taskEntity.getProcessInstanceId(), IActivitiConstants.PROP_COMMENT, actionRequest.getComment());
        taskService.complete(taskId, vMap);

    }

    public void ReviewTask(IRequest request, Task taskEntity, TaskActionRequestExt actionRequest) throws TaskActionException {
        if (!REVIEW.equalsIgnoreCase(actionRequest.getAction())) {
            return;
        }
        String requestData = request.getAttributeMap().get("_request_data").toString();
        JSONObject jsonObject = JSONObject.parseObject(requestData);
        JSONArray param = (JSONArray)jsonObject.get("parameter");
        JSONObject param0 = (JSONObject)param.get(0);

        String taskId = taskEntity.getId();



        Map<String, Object> vMap = new HashMap<String, Object>(2);
        vMap.put("approveResult", "REVIEW");
        vMap.put("approveResultResult", "提交业审委审议");
        // 添加审批备注
        taskService.addComment(taskId, taskEntity.getProcessInstanceId(), IActivitiConstants.COMMENT_ACTION, "REVIEW");
        taskService.addComment(taskId, taskEntity.getProcessInstanceId(), IActivitiConstants.PROP_COMMENT, actionRequest.getComment());
        taskService.complete(taskId, vMap);

    }

    @Autowired
    private IActivitiService activitiService;


    public void carbonCopy(IRequest request, Task taskEntity, TaskActionRequestExt actionRequest)
            throws TaskActionException {

        Long allocationId = request.getAttribute("allocationId");
        Authentication.setAuthenticatedUserId(String.valueOf(allocationId));
        String assignee = actionRequest.getCarbonCopyUsers();
        if (StringUtil.isNotEmpty(assignee)) {
            Set<String> userCodes = org.springframework.util.StringUtils.commaDelimitedListToSet(assignee);
            String processInstanceId = taskEntity.getProcessInstanceId();
            Map<String, Object> params = new HashMap<>();
            params.put(MSG_PAEM_PROCESSINSTANCEID, processInstanceId);
            params.put(MSG_PARM_USERS, userCodes);

            String employeeName = getEmployeeName(allocationId.toString());
            String carbonCopyNames = userCodes.stream().map(o -> fndEmployeeMapper.selectEmployeeByCode(o).getName()).collect(Collectors.joining(","));
            String comment = employeeName + " 抄送给 " + carbonCopyNames;
            taskService.addComment(taskEntity.getId(), processInstanceId, COMMENT_ACTION, CARBON_COPY);
            Comment comment1 = taskService.addComment(taskEntity.getId(), processInstanceId, PROP_COMMENT, comment);

            userCodes.forEach(userCode -> {
                Map<String, Object> curParams = new HashMap<>();
                curParams.put(MSG_PAEM_PROCESSINSTANCEID, processInstanceId);
                curParams.put(MSG_PARM_USERS, userCodes);

//                messagePublisher.message(IActivitiConstants.CHANNEL_CARBON_COPY, curParams);
                String url = "WFL/WFL003/process_instance_detail.lview?taskId=" + taskEntity.getId() + "&id=" + processInstanceId;
                curParams.put("message", String.valueOf(request.getEmployeeCode()) + " 抄送给 " + userCode);
                curParams.put("noticeTitle", "工作流抄送");
                curParams.put("level", 1);
                curParams.put("noticeType", "NOTICE");
                curParams.put("url", url);

                if (userService.selectUserByEmployeeCode(userCode).size() > 0) {
                    User user = userService.selectUserByEmployeeCode(userCode).get(0);
                    curParams.put("eventUserId", user.getUserId());
                    sysEventService.eventSave(request, Long.valueOf(processInstanceId), CARBON_COPY, CARBON_COPY, SYS_MODULE, SysEventCodeUtil.PROPERTY_WFL_CARBONCOPY, SysEventCodeUtil.PROPERTITY_RELATIONTYPE_D2D, curParams);
                }
            });
        }
    }

    public void processCarbonCopyRead(String processInstanceId, String employeeCode) {
        HiIdentitylink dto = new HiIdentitylink();
        dto.setProcInstId_(processInstanceId);
        dto.setReadFlag_("Y");
        dto.setUserId_(employeeCode);
        hiIdentitylinkMapper.updateReadFlag(dto);
        taskCreateNotificationListener.sendMessageForCC(employeeCode);
    }

    @Override
    public boolean queryTaskByTaskId(HttpServletRequest request, String taskId) {
        //根据tackId查看审批人
        TaskNew taskNew = taskNewMapper.queryTaskByTaskId(taskId);
        if (taskNew != null) {
            Long userId = getUserIdByAlloctionId(taskNew.getAssignee());
            //获取当前登录用户
            Long currentUserId = RequestHelper.getCurrentRequest().getUserId();
            if (userId.equals(currentUserId)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /*
     * public void updateCarbonCopyMsg(String user){
     * CustomHistoricProcessInstanceQueryRequest historicProcessInstanceQueryRequest
     * = new CustomHistoricProcessInstanceQueryRequest();
     * historicProcessInstanceQueryRequest.setCarbonCopyUser(user);
     * historicProcessInstanceQueryRequest.setReadFlag("N"); Map<String, String>
     * requestParams = new HashMap<>(); long count =
     * historicProcessInstanceQueryResource.queryProcessInstances(
     * historicProcessInstanceQueryRequest, requestParams, fakeRequest).getTotal();
     * taskCreateNotificationListener.sendMessageForCC(user,count); }
     */

    @Override
    public void jumpTo(IRequest request, Task taskEntity, TaskActionRequestExt actionRequest) {
        if (!ACTION_JUMP.equalsIgnoreCase(actionRequest.getAction())) {
            return;
        }
        Long allocationId = request.getAttribute("allocationId");
        Authentication.setAuthenticatedUserId(String.valueOf(allocationId));
        String taskId = taskEntity.getId();
        taskService.addComment(taskId, taskEntity.getProcessInstanceId(), COMMENT_ACTION, JUMP);
        taskService.addComment(taskId, taskEntity.getProcessInstanceId(), PROP_COMMENT,
                "从<" + taskEntity.getName() + ">跳转至<" + actionRequest.getJumpTargetName() + ">");
        JumpActivityCmd cmd = new JumpActivityCmd(taskId, actionRequest.getJumpTarget());
        processEngineConfiguration.getCommandExecutor().execute(cmd);
        if (StringUtils.isNotEmpty(taskEntity.getAssignee())) {
            taskCreateNotificationListener.sendMessage(taskEntity.getAssignee());
        }
    }

    @Override
    public void passTo(IRequest iRequest, String procId) throws HlsCusException {
        excuteActivitiBean(iRequest, procId, "PASS");
        self().deleteProcessInstance(procId);
    }

    @Override
    public void rejectTo(IRequest iRequest, String procId) throws HlsCusException {
        excuteActivitiBean(iRequest, procId, "REJECT");
        self().deleteProcessInstance(procId);
    }

    public void jumpActivitiTo(IRequest iRequest, String procId, String jumpTarget, String jumpTargetName) throws HlsCusException {
        Long allocationId = iRequest.getAttribute("allocationId");
        Authentication.setAuthenticatedUserId(String.valueOf(allocationId));
        if (procId == null) {
            throw new HlsCusException("not found target proc_id");
        }
        Map map = new HashMap();
        map.put("proc_inst_id_", procId);
        List<Map> procList = RecordHelper.select("act_hi_procinst", map);
        List<Map> taskList = RecordHelper.select("act_ru_task", map);
        if (taskList != null && taskList.size() > 0) {
            for (int i = 0; i < taskList.size(); i++) {
                taskService.addComment(taskList.get(i).get("id_").toString(), procId, COMMENT_ACTION, JUMP);
                taskService.addComment(taskList.get(i).get("id_").toString(), procId, PROP_COMMENT,
                        "从<" + taskList.get(i).get("name_").toString() + ">跳转至<" + jumpTargetName + ">");
                JumpActivityCmd cmd = new JumpActivityCmd(taskList.get(i).get("id_").toString(), jumpTarget);
                processEngineConfiguration.getCommandExecutor().execute(cmd);
                if (taskList.get(i).get("assignee_") != null && StringUtils.isNotEmpty(taskList.get(i).get("assignee_").toString())) {
                    taskCreateNotificationListener.sendMessage(taskList.get(i).get("assignee_").toString());
                }
            }
        }
        excuteActivitiBean(iRequest, procId, "JUMP");
    }

    void excuteActivitiBean(IRequest iRequest, String procId, String type) throws HlsCusException {
        Long allocationId = iRequest.getAttribute("allocationId");
        Authentication.setAuthenticatedUserId(String.valueOf(allocationId));
        if (procId == null) {
            throw new HlsCusException("not found target proc_id");
        }
        Map map = new HashMap();
        map.put("proc_inst_id_", procId);
        List<Map> procList = RecordHelper.select("act_hi_procinst", map);
        List<Map> taskList = RecordHelper.select("act_ru_task", map);
        List<Map> variableList = RecordHelper.select("act_ru_variable", map);
        if (procList != null && procList.size() > 0) {
            String procDefId = procList.get(0).get("proc_def_id_").toString();
            String proc = procDefId.split(":")[0];
            map.put("workflow_code", proc);
            map.put("type", type);
            List<Map> processMethods = RecordHelper.select("act_process_method", map, Arrays.asList("workflow_code", "type"));
            if (CollectionUtils.isEmpty(processMethods)) {
                if ("JUMP".equals(type)) {
                    return;
                }
                throw new HlsCusException("请配置对应的执行监听器");
            }
            for (int i = 0; i < processMethods.size(); i++) {
                if (processMethods.get(i).get("method_name") != null) {
                    HlsCusProcess hlsCusProcess = new HlsCusProcess();
                    hlsCusProcess.setType(type);
                    hlsCusProcess.setBussinessKey(procList.get(0).get("business_key_").toString());
                    hlsCusProcess.setProcessMap(procList);
                    hlsCusProcess.setTaskMap(taskList);
                    hlsCusProcess.setVariableMap(variableList);
                    String className = processMethods.get(i).get("method_name").toString().replace("${", "").replace("}", "");
                    IHlsCusActivitiBean iHlsCusActivitiBean = (IHlsCusActivitiBean) hlsCusActivitiBeanProvider.get(className);
                    if (iHlsCusActivitiBean == null) {
                        throw new HlsCusException(className + " not implement IHlsCusActivitiBean");
                    }
                    iHlsCusActivitiBean.excute(iRequest, hlsCusProcess);
                }
            }
            for (int i = 0; i < taskList.size(); i++) {
                taskService.addComment(taskList.get(i).get("id_").toString(), procId, COMMENT_ACTION, type);
                taskService.addComment(taskList.get(i).get("id_").toString(), procId, PROP_COMMENT,
                        "从<" + taskList.get(i).get("name_").toString() + ">" + type + "<" + taskList.get(i).get("name_").toString() + ">");
            }
        }
    }

    protected boolean hasRight(String a, String b, boolean isAdmin) {
        return isAdmin || eq(a, b);
    }

    protected boolean eq(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }

    @Override
    public void executeTaskAction(IRequest request, String taskId, TaskActionRequestExt actionRequest, boolean isAdmin)
            throws TaskActionException {


        //解决转交更新转交代办
        List<HlsSystemNotice> hsnList = hlsSystemNoticeService.queryNoticeByTodoAndDocId(Long.parseLong(taskId));
        HlsSystemNoticeOwner hlsSystemNoticeOwner = new HlsSystemNoticeOwner();
        List<HlsSystemNoticeOwner> hsnoList = new ArrayList<>();
        if (hsnList.size() > 0) {
            hlsSystemNoticeOwner.setNotice_id(hsnList.get(0).getNoticeId());
            hlsSystemNoticeOwner.setOwner_user_id(hsnList.get(0).getSourceUserId());
            hlsSystemNoticeOwner.setRead_flag("Y");
            hlsSystemNoticeOwner.setDone_flag("Y");
            List<HlsSystemNoticeOwner> hlsSystemNoticeOwnerList = hlsSystemNoticeOwnerService.queryOwnerNoticeById(hlsSystemNoticeOwner);
            if (hlsSystemNoticeOwnerList.size() > 0) {
                hlsSystemNoticeOwner.set__status("update");
            } else {
                hlsSystemNoticeOwner.set__status("insert");
            }
            hsnoList.add(hlsSystemNoticeOwner);
            hlsSystemNoticeOwnerService.batchUpdate(request, hsnoList);
        }

        if (StringUtils.isEmpty(actionRequest.getAction())) {
            throw new IllegalArgumentException("Action is required.");
        }
        Task taskEntity = getTaskById(taskId);
        // 处理配置候选人候选组的情况
        List<IdentityLink> idList = null;
        //TODO:检查
        if (taskEntity.getAssignee() == null) {
            // 自动 claim
            idList = taskService.getIdentityLinksForTask(taskId);
            if (isAdmin) {
                taskService.claim(taskId, String.valueOf(request.getUserId()));
            } else {
                Set<String> nameList = new HashSet<>();
                List<Group> userGroup = null;
                boolean isCandi = false;
                for (IdentityLink il : idList) {
                    if (il.getUserId() != null) {
                        if (eq(String.valueOf(request.getUserId()), il.getUserId())) {
                            isCandi = true;
                            break;
                        }
                    } else if (il.getGroupId() != null) {
                        userGroup = processEngineConfiguration.getUserDataManager()
                                .findGroupsByUser(String.valueOf(request.getUserId()));
                        for (Group g : userGroup) {
                            if (eq(g.getId(), il.getGroupId())) {
                                isCandi = true;
                                break;
                            }
                        }
                    }
                }
                if (!isCandi) {
                    throw new TaskActionException(WflSecurityException.NEED_ASSIGNEE_OR_ADMIN);
                }
                taskService.claim(taskId, String.valueOf(request.getUserId()));
                taskEntity.setAssignee(String.valueOf(request.getUserId()));
            }
        }
        /*if (!hasRight(String.valueOf(request.getUserId()), taskEntity.getAssignee(), isAdmin)) {
            throw new TaskActionException(TaskActionException.COMPLETE_TASK_NEED_ASSIGNEE_OR_ADMIN);
        }*/
        Long allocationId = request.getAttribute("allocationId");
        Authentication.setAuthenticatedUserId(String.valueOf(allocationId));

        try {
            // 处理抄送
            carbonCopy(request, taskEntity, actionRequest);
            if (TaskActionRequest.ACTION_COMPLETE.equalsIgnoreCase(actionRequest.getAction())) {
                completeTask(request, taskEntity, actionRequest);
//                yunzhijiaDealTodo(taskId,allocationId);
                boolean flag=true;

                updateUsedAmountByChance(taskEntity,flag);

                return;
            }
            if (TaskActionRequest.ACTION_DELEGATE.equalsIgnoreCase(actionRequest.getAction())) {
                delegateTask(request, taskEntity, actionRequest);
                processCandidateMsg(idList);
//                yunzhijiaDealTodo(taskId,allocationId);
                return;
            }

            if (TaskActionRequest.ACTION_RESOLVE.equalsIgnoreCase(actionRequest.getAction())) {
                resolveTask(request, taskEntity, actionRequest);
                return;
            }

            if (ACTION_JUMP.equalsIgnoreCase(actionRequest.getAction())) {
                jumpTo(request, taskEntity, actionRequest);
//                yunzhijiaDealTodo(taskId,allocationId);
                return;
            }
            if (ACTION_ADD_SIGN.equals(actionRequest.getAction())) {
                addSignTask(request, taskEntity, actionRequest);
                processCandidateMsg(idList);
                return;
            }
            if(ADD_AND_ASSIGN.equals(actionRequest.getAction())){
                addAssignTask(request, taskEntity, actionRequest);
                processCandidateMsg(idList);
                return;
            }
            if(ADD_APPOINT.equals(actionRequest.getAction())){
                addAppointTask(request, taskEntity, actionRequest);
                processCandidateMsg(idList);
                return;
            }
            if(APPROVED_GENERAL.equals(actionRequest.getAction())){
                approvalGeneralTask(request, taskEntity, actionRequest);
                processCandidateMsg(idList);
                return;
            }
            if(POWERFUL_APPROVED.equals(actionRequest.getAction())){
                addPowerfulPersonTask(request, taskEntity, actionRequest);
                processCandidateMsg(idList);
                return;
            }
            if(DURATION_APPROVED.equals(actionRequest.getAction())){
                addDurationPersonTask(request, taskEntity, actionRequest);
                processCandidateMsg(idList);
                return;
            }
            if(END_TASK_APPROVED.equals(actionRequest.getAction())){
                chooesConEndTask(request, taskEntity, actionRequest);
                processCandidateMsg(idList);
                return;
            }
            if(PAY_END_TASK_APPROVED.equals(actionRequest.getAction())){
                chooesPayEndTask(request, taskEntity, actionRequest);
                processCandidateMsg(idList);
                return;
            }
            if(CONDITION_APPROVED.equals(actionRequest.getAction())){
                chooseConditionTask(request, taskEntity, actionRequest);
                processCandidateMsg(idList);
                return;
            }
            if(WITHDRAW.equals(actionRequest.getAction())){
                withdrawTask(request, taskEntity, actionRequest);
                processCandidateMsg(idList);
                return;
            }
            if(APPROVED_RETURN.equalsIgnoreCase(actionRequest.getAction())){
                approvedReturnTask(request, taskEntity, actionRequest);
                processCandidateMsg(idList);
                boolean flag=false;
                updateUsedAmountByChance(taskEntity,flag);
                return;
            }
            if(REVIEW.equalsIgnoreCase(actionRequest.getAction())){
                ReviewTask(request, taskEntity, actionRequest);
                processCandidateMsg(idList);
                return;
            }


        } catch (ActivitiException e) {
            self().saveException(taskId, e);
            throw e;
        }


    }

    private void yunzhijiaDealTodo(String taskId,Long allocationId){
        try {
            //云之家待办需要置为已办
            Long sourceId = Long.valueOf(taskId);
            appWflTodoNoticeService.dealTodoNotice(sourceId,null);
        } catch (Exception e) {
            logger.error("--------------------------云之家待办置为已办报错------------------------");
            e.printStackTrace();
        }
    }

    private void processCandidateMsg(List<IdentityLink> idList) {
        if (idList != null) {
            for (IdentityLink il : idList) {
                if (il.getUserId() != null) {
                    taskCreateNotificationListener.sendMessage(il.getUserId());
                } else if (il.getGroupId() != null) {
                    taskCreateNotificationListener.sendMessageByGroup(il.getGroupId());
                }
            }
        }
    }

    public void saveException(String taskId, ActivitiException e) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String mess = Throwables.getStackTraceAsString(e);
       /* byte[] mesb = null;
        try {
            mesb = mess.getBytes("utf-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }*/
        exceptionMapper.insertSelective(new ActiviException(task.getProcessInstanceId(), mess, new Date()));
    }

    @Override
    public void deleteDeployment(String deploymentId, Boolean cascade) {

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deploymentId).singleResult();
        String processDefinitionId = processDefinition.getId();
        List<Task> taskList = taskService.createTaskQuery().processDefinitionId(processDefinitionId).list();
        Set<String> users = new HashSet<>();
        taskList.forEach(t -> {
            if (t.getAssignee() != null) {
                users.add(t.getAssignee());
            }
        });
        if (cascade) {
            repositoryService.deleteDeployment(deploymentId, true);
        } else {
            repositoryService.deleteDeployment(deploymentId);
        }
        for (String user : users) {
            taskCreateNotificationListener.sendMessage(user);
            taskCreateNotificationListener.sendMessageForCC(user);
        }
    }

    @Override
    public void deleteProcessInstance(String processInstanceId) {
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        Set<String> users = new HashSet<>();
        taskList.forEach(t -> {
            if (t.getAssignee() != null) {
                users.add(t.getAssignee());
            }
        });
        runtimeService.deleteProcessInstance(processInstanceId, IActivitiConstants.ACT_STOP);
        for (String user : users) {
            taskCreateNotificationListener.sendMessage(user);
        }
    }

    @Override
    public List<ActivitiNode> getProcessNodes(IRequest request, String processInstanceId) {
        /*
         * Process process = ProcessDefinitionUtil.getProcess(processDefinitionId);
         * List<ActivitiNode> list = new ArrayList<>(); Collection<FlowElement> eles =
         * process.getFlowElements(); for (FlowElement fe : eles) { if (fe instanceof
         * UserTask) { ActivitiNode node = new ActivitiNode();
         * node.setName(fe.getName()); node.setNodeId(fe.getId());
         * node.setType("UserTask"); list.add(node); } }
         */
        List<HistoricActivityInstance> historicActivityInstanceList = historyService
                .createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).activityType("userTask")
                .orderByHistoricActivityInstanceEndTime().asc().list();
        String processDefinitionId = historicActivityInstanceList.get(0).getProcessDefinitionId();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        List<ActivitiNode> list = new ArrayList<>();
        Collection<FlowElement> flowElements = bpmnModel.getMainProcess().getFlowElements();
        for (FlowElement flowElement : flowElements) {
            if (flowElement instanceof UserTask) {
                ActivitiNode node = new ActivitiNode();
                node.setName(flowElement.getName());
                node.setNodeId(flowElement.getId());
                node.setType("UserTask");
                list.add(node);
            }
        }
        return list;
    }

    @Override
    public List<ActivitiNode> getUserTaskFromModelSource(IRequest request, String modelId) {
        List<ActivitiNode> list = new ArrayList<>();
        byte[] data = repositoryService.getModelEditorSource(modelId);
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(data);
            BpmnModel bpmnModel = new BpmnJsonConverter().convertToBpmnModel(jsonNode);
            Process process = bpmnModel.getMainProcess();
            Collection<FlowElement> elements = process.getFlowElements();
            for (FlowElement flowElement : elements) {
                if (flowElement instanceof UserTask) {
                    ActivitiNode node = new ActivitiNode();
                    node.setNodeId(flowElement.getId());
                    node.setName(flowElement.getName());
                    node.setType("UserTask");
                    list.add(node);
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return list;
    }

    @Override
    public String getEmployeeName(String allcationId) {
        if (StringUtils.isNumeric(allcationId)) {
            UserEntity userEntity = userDataManager.findById(allcationId);
            if (userEntity != null && StringUtils.isNotEmpty(userEntity.getFirstName())) {
                return userEntity.getFirstName();
            }
        }
        return allcationId;
    }

    @Override
    public String getUserName(String userId) {
        UserEntity userEntity = userDataManager.findById(userId);
        if (userEntity != null && StringUtils.isNotEmpty(userEntity.getFirstName())) {
            return userEntity.getFirstName();
        }
        return userId;
    }

    @Override
    public String getGroupName(String groupId) {
        Group group = groupDataManager.findById(groupId);
        if (group != null) {
            return group.getName();
        }
        return groupId;
    }

    @Override
    public HistoricProcessInstanceResponseExt getInstanceDetail(IRequest request, String processInstanceId) {
        HistoricProcessInstanceResponseExt historicProcessInstanceResponseExt = new HistoricProcessInstanceResponseExt();
        // 查询流程实例历史
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).list().iterator().next();
        // 设置申请人，流程名称
        historicProcessInstanceResponseExt.setStartUserId(historicProcessInstance.getStartUserId());
        historicProcessInstanceResponseExt.setStartUserName(getEmployeeName(historicProcessInstance.getStartUserId()));
        historicProcessInstanceResponseExt.setProcessName(historicProcessInstance.getProcessDefinitionName());
        historicProcessInstanceResponseExt.setStartTime(historicProcessInstance.getStartTime());

        String processDefinitionKey = historicProcessInstance.getProcessDefinitionKey();
        String businessKey = historicProcessInstance.getBusinessKey();

        // 获取流程活动历史
        List<HistoricActivityInstance> historicActivityInstanceList = new ArrayList<>();
        if(processDefinitionKey != null){
            if(processDefinitionKey.equals("PROJECT_REVIEW_WFL") || processDefinitionKey.equals("CREDIT_PROJECT_REVIEW_WFL") ){
                IRequest iRequest = RequestHelper.getCurrentRequest(true);
                request.setAttribute("wflRuleControlFlag", "Y");
                HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
                hlsCusPrjProject.setProjectId(Long.parseLong(businessKey));
                hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest,hlsCusPrjProject);
                if(hlsCusPrjProject.getChanceId() != null){
                    //立项审批历史
                    List<String> chanceProcessInstanceIdList = actHiTaskinstMapper.selectHistoryProcessInstanceIdList("CREDIT_LINE_CHANCE_SUBMIT", hlsCusPrjProject.getChanceId().toString());
                    for (String id : chanceProcessInstanceIdList) {
                        historicActivityInstanceList = historyService
                                .createHistoricActivityInstanceQuery().processInstanceId(id).list();
                    }
                    //项目审批历史
                    List<HistoricActivityInstance> prjHistoricActivityInstanceList = historyService
                            .createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();
                    historicActivityInstanceList.addAll(prjHistoricActivityInstanceList);
                }else{
                    historicActivityInstanceList = historyService
                            .createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();
                }
            }else{
                historicActivityInstanceList = historyService
                        .createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();
            }

        }else{
            historicActivityInstanceList = historyService
                    .createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();
        }
        List<HistoricTaskInstanceResponseExt> list = new ArrayList<>();
        for (HistoricActivityInstance historicActivityInstance : historicActivityInstanceList) {
            setHistoricActivityInstanceResponseExt(historicActivityInstance, list, processInstanceId);
        }
        list.sort(Comparator.comparing(HistoricTaskInstanceResponseExt::getEndTime));
        historicProcessInstanceResponseExt.getHistoricTaskList().addAll(list);

        // 设置全局表单url
        String key = formService.getStartFormKey(historicProcessInstance.getProcessDefinitionId());
        historicProcessInstanceResponseExt.setBusinessKey(historicProcessInstance.getBusinessKey());
        historicProcessInstanceResponseExt.setFormKey(key);

        List<HistoricVariableInstance> variables = historyService.createHistoricVariableInstanceQuery().processInstanceId(historicProcessInstance.getId()).list();
        historicProcessInstanceResponseExt.setDescription(getProcessDescription(historicProcessInstance.getProcessDefinitionId()
                , variables.stream().collect(Collectors.toMap(vk -> vk.getVariableName(), value -> value.getValue() == null ? "" : value.getValue(), (existingValue, newValue) -> newValue))));


        //add by Marshal,get history variables for processInstance process monitor which is over
        List<HistoricVariableInstance> variableInstances = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId).list();
        historicProcessInstanceResponseExt.setVariableInstances(variableInstances);

        return historicProcessInstanceResponseExt;
    }

    @Override
    public TaskResponseExt getTaskDetails(IRequest request, String taskId, boolean isAdmin)
            throws WflSecurityException {
        Task task = getTaskById(taskId);

        TaskResponseExt taskExt = new TaskResponseExt(task);
        List<Group> userGroup = null;

        // display name of assignee or group
        if (StringUtils.isNotEmpty(taskExt.getAssignee())) {
            // privilege check
            /*if (!hasRight(String.valueOf(request.getUserId()), taskExt.getAssignee(), isAdmin)) {
                throw new WflSecurityException(WflSecurityException.NEED_ASSIGNEE_OR_ADMIN);
            }*/
            taskExt.setAssigneeName(getEmployeeName(taskExt.getAssignee()));
        } else {
            List<IdentityLink> idList = taskService.getIdentityLinksForTask(task.getId());
            List<String> nameList = new ArrayList<>();
            boolean isCandi = isAdmin;
            for (IdentityLink il : idList) {
                if (il.getGroupId() != null) {
                    // privilege check
                    if (!isCandi) {
                        if (userGroup == null) {
                            userGroup = processEngineConfiguration.getUserDataManager()
                                    .findGroupsByUser(String.valueOf(request.getUserId()));
                        }
                        for (Group g : userGroup) {
                            if (eq(g.getId(), il.getGroupId())) {
                                isCandi = true;
                                break;
                            }
                        }
                    }
                    // privilege check end

                    nameList.add(getGroupName(il.getGroupId()));
                } else if (il.getUserId() != null) {
                    if (!isCandi && eq(String.valueOf(request.getUserId()), il.getUserId())) {
                        // privilege check
                        isCandi = true;
                    }
                    nameList.add(getEmployeeName(il.getUserId()));
                }
            }
            if (!isCandi) {
//                if (!hasRight(String.valueOf(request.getUserId()), taskExt.getAssignee(), isAdmin)) {
//                    throw new WflSecurityException(WflSecurityException.NEED_ASSIGNEE_OR_ADMIN);
//                }
            }
            taskExt.setAssigneeName(StringUtils.join(nameList.toArray(), ";"));
        }

        // attachment
        List<org.activiti.engine.task.Attachment> attaList = taskService.getTaskAttachments(taskId);
        taskExt.setAttachments(attaList);

        // form data:formVariables
        FormData formData = formService.getTaskFormData(taskId);
        taskExt.setFormData(restResponseFactory.createFormDataResponse(formData));

        //单据历史审批记录
        List<HistoricVariableInstance> variableInstances = historyService.createHistoricVariableInstanceQuery().processInstanceId(task.getProcessInstanceId()).list();

        List<HistoricVariableInstance> params = variableInstances.stream().filter(o -> WORK_FLOW_TYPE.equals(o.getVariableName()) || BUSINESS_KEY.equals(o.getVariableName())).collect(Collectors.toList());
        String businessKey = null;
        String workflowType = null;
        for (HistoricVariableInstance item : params) {
            if (WORK_FLOW_TYPE.equals(item.getVariableName())) {
                workflowType = (String) item.getValue();
            } else {
                businessKey = item.getValue().toString();
            }
        }
        List<String> historyProcessInstanceIdList = new ArrayList<>();
        if(workflowType != null){
            if(workflowType.equals("PROJECT_REVIEW_WFL")||workflowType.equals("CREDIT_PROJECT_REVIEW_WFL")){
                IRequest iRequest = RequestHelper.getCurrentRequest(true);
                request.setAttribute("wflRuleControlFlag", "Y");
                HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
                hlsCusPrjProject.setProjectId(Long.parseLong(businessKey));
                hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest,hlsCusPrjProject);
                if(hlsCusPrjProject.getChanceId() != null){
                    historyProcessInstanceIdList = actHiTaskinstMapper.selectHistoryProcessInstanceIdList("CREDIT_LINE_CHANCE_SUBMIT", hlsCusPrjProject.getChanceId().toString());
                    List<String> prjHistoryProcessInstanceIdList = actHiTaskinstMapper.selectHistoryProcessInstanceIdList(workflowType, businessKey);
                    historyProcessInstanceIdList.addAll(prjHistoryProcessInstanceIdList);
                }else{
                    historyProcessInstanceIdList = actHiTaskinstMapper.selectHistoryProcessInstanceIdList(workflowType, businessKey);
                }
            }else{
                historyProcessInstanceIdList = actHiTaskinstMapper.selectHistoryProcessInstanceIdList(workflowType, businessKey);
            }
        }else{
            historyProcessInstanceIdList = actHiTaskinstMapper.selectHistoryProcessInstanceIdList(workflowType, businessKey);
        }


        List<HistoricTaskInstanceResponseExt> list = new ArrayList<>();
        if (historyProcessInstanceIdList.size() > 0) {
            for (String id : historyProcessInstanceIdList) {
                List<HistoricActivityInstance> historicActivityInstanceList = historyService
                        .createHistoricActivityInstanceQuery().processInstanceId(id).list();
                for (HistoricActivityInstance historicActivityInstance : historicActivityInstanceList) {
                    setHistoricActivityInstanceResponseExt(historicActivityInstance, list, task.getProcessInstanceId());
                }
            }
        }

        list.sort(Comparator.comparing(HistoricTaskInstanceResponseExt::getEndTime));
        taskExt.getHistoricTaskList().addAll(list);

        // delegate
        List<Comment> comments = taskService.getTaskComments(task.getId(), COMMENT_DELEGATE_BY);
        if (!comments.isEmpty()) {
            Comment comment = comments.get(comments.size() - 1);
            TaskDelegate taskDelegate = new TaskDelegate();

            taskDelegate.setFromUserId(comment.getUserId());
            taskDelegate.setFromUserName(getEmployeeName(comment.getUserId()));
            taskDelegate.setTime(comment.getTime());
            taskDelegate.setReason(comment.getFullMessage());
            taskExt.setTaskDelegate(taskDelegate);
        }

        // processInstance
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId()).singleResult();
        ProcessInstanceResponseExt processInstanceResp = (ProcessInstanceResponseExt) restResponseFactory
                .createProcessInstanceResponse(processInstance);
        processInstanceResp.setStartUserName(getEmployeeName(processInstanceResp.getStartUserId()));
        taskExt.setProcessInstance(processInstanceResp);

        //流程描述
        taskExt.setDescription(getProcessDescription(processInstance.getProcessDefinitionId(), taskExt.getExecutionId()));

        // execution variable
        List<RestVariable> vars = executionVariableCollectionResource.getVariables(task.getExecutionId(), null,
                fakeRequest);

        taskExt.setExecutionVariables(vars);
        return taskExt;
    }

    @Override
    public TaskResponseExt getTaskDetails(IRequest request, String taskId) throws WflSecurityException {
        return getTaskDetails(request, taskId, false);
    }

    @Override
    public DataResponse queryTaskList(IRequest iRequest, TaskQueryRequest taskQueryRequest,
                                      Map<String, String> requestParams) {
        taskQueryRequest.setSort("createTime");
        taskQueryRequest.setOrder("desc");
        DataResponse dataResponse = taskQueryResource.getQueryResult(taskQueryRequest, requestParams, fakeRequest);
        List<TaskResponseExt> list = (List<TaskResponseExt>) dataResponse.getData();
        for (TaskResponseExt taskResponse : list) {
            if (StringUtils.isNotEmpty(taskResponse.getOwner())) {
                taskResponse.setOwner(getEmployeeName(taskResponse.getOwner()));
            }
            if (StringUtils.isNotEmpty(taskResponse.getAssignee())) {
                taskResponse.setAssigneeName(getEmployeeName(taskResponse.getAssignee()));
            } else {
                List<IdentityLink> idList = taskService.getIdentityLinksForTask(taskResponse.getId());
                List<String> nameList = new ArrayList<>();
                for (IdentityLink il : idList) {
                    if (il.getGroupId() != null) {
                        nameList.add(getGroupName(il.getGroupId()));
                    } else if (il.getUserId() != null) {
                        nameList.add(processApproveName(il.getUserId()));
                    }
                }
                taskResponse.setAssigneeName(StringUtils.join(nameList.toArray(), ";"));
            }
            ProcessInstance procInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(taskResponse.getProcessInstanceId()).list().iterator().next();

            taskResponse.setProcessName(procInstance.getProcessDefinitionName());
            taskResponse.setStartUserId(procInstance.getStartUserId());
            taskResponse.setStartUserName(getEmployeeName(procInstance.getStartUserId()));

            //流程描述
            taskResponse.setDescription(getProcessDescription(procInstance.getProcessDefinitionId(), taskResponse.getExecutionId()));

            Date dueDate = taskResponse.getDueDate();
            if (dueDate != null) {
                Long dueTime = ActivitiUtils.secondsBetweenDate(taskResponse.getCreateTime(), dueDate);
                for (ICustomTaskProcessor processor : taskProcessors) {
                    taskResponse.setDueTime(processor.getDueTime(taskResponse.getCreateTime(), dueTime));
                    if (!processor.processorContinue()) {
                        break;
                    }
                }
            }
        }
        return dataResponse;
    }

    @Override
    public List<TaskNew> queryTaskListNew(TaskNew dto, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<TaskNew> taskList = new ArrayList<>();
        String DocumentNames = dto.getDocument_name();
        if(DocumentNames != null && !"".equalsIgnoreCase(DocumentNames)){
            List<TaskNew> taskListNew = taskNewMapper.queryTasketail(dto);
            for (TaskNew item : taskListNew) {
                item.setStart_user_name(getEmployeeName(item.getStart_user_name()));
                item.setAssignee(getEmployeeName(item.getAssignee()));
                // execution variable
                List<RestVariable> vars = executionVariableCollectionResource.getVariables(item.getProcess_instance_id(), null,
                        fakeRequest);

                item.setExecutionVariables(vars);

                //单据名称
                String documentName = taskNewMapper.queryDocumentNameNew(item.getProcess_instance_id(),DocumentNames);
                if( documentName != null && !"".equalsIgnoreCase(documentName)){
                    item.setDocument_name(documentName);
                    taskList.add(item);
                }
            }
        }else{
            taskList = taskNewMapper.queryTasketail(dto);
            for (TaskNew item : taskList) {
                item.setStart_user_name(getEmployeeName(item.getStart_user_name()));
                item.setAssignee(getEmployeeName(item.getAssignee()));
                // execution variable
                List<RestVariable> vars = executionVariableCollectionResource.getVariables(item.getProcess_instance_id(), null,
                        fakeRequest);

                item.setExecutionVariables(vars);
                //单据名称
                String documentName = taskNewMapper.queryDocumentName(item.getProcess_instance_id());
                item.setDocument_name(documentName);
            }
        }

        return taskList;
    }

    @Override
    public DataResponse queryProcessInstances(IRequest iRequest,
                                              CustomHistoricProcessInstanceQueryRequest historicProcessInstanceQueryRequest, Map<String, String> requestParams,
                                              boolean showRetract) {
        DataResponse dataResponse = historicProcessInstanceQueryResource
                .queryProcessInstances(historicProcessInstanceQueryRequest, requestParams, fakeRequest);
        for (HistoricProcessInstanceResponseExt his : (List<HistoricProcessInstanceResponseExt>) dataResponse
                .getData()) {
            if (StringUtils.isNotEmpty(his.getStartUserId())) {
                his.setStartUserName(getUserName(his.getStartUserId()));
            }
            if (StringUtils.isNotEmpty(his.getTaskDefKey())) {
                BpmnModel bpmnModel = repositoryService.getBpmnModel(his.getProcessDefinitionId());
                Collection<FlowElement> flowElements = bpmnModel.getMainProcess().getFlowElements();
                for (FlowElement flowElement : flowElements) {
                    if (his.getTaskDefKey().equals(flowElement.getId())) {
                        his.setTaskName(flowElement.getName());
                        break;
                    }
                }
            }
            // TODO: 优化！
            List<Execution> list1 = runtimeService.createExecutionQuery().processInstanceId(his.getId()).list();
            for (Execution ls : list1) {
                if (ls.isSuspended()) {
                    his.setSuspended(true);
                    break;
                }
            }
            //设置最后审批人
            List<HistoricActivityInstance> historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery().processInstanceId(his.getId()).list();
            List<HistoricTaskInstanceResponseExt> list = new ArrayList<>();
            for (HistoricActivityInstance historicActivityInstance : historicActivityInstanceList) {
                setHistoricActivityInstanceResponseExt(historicActivityInstance, list, his.getId());
            }
            list.sort(Comparator.comparing(HistoricTaskInstanceResponseExt::getEndTime).reversed());
            Optional<HistoricTaskInstanceResponseExt> lastAssignee = list.stream().filter(v -> StringUtils.isNotEmpty(v.getAssigneeName())).max(Comparator.comparing(HistoricTaskInstanceResponseExt::getEndTime));
            lastAssignee.ifPresent(historicTaskInstanceResponseExt -> his.setLastApprover(historicTaskInstanceResponseExt.getAssigneeName()));

            //最后审批动作
            Map map = new HashMap();
            String lastApproveAction = "";
            map.put("procInstId", his.getId());
            lastApproveAction = actHiTaskinstMapper.selectLastApproveAction(map);
            his.setLastApproveAction(lastApproveAction);

            //单据名称
            List<HistoricVariableInstance> processInstanceVariables = historyService.createHistoricVariableInstanceQuery().processInstanceId(his.getId()).list();

            List<Object> documentName = processInstanceVariables.stream().filter(o -> "documentName".equals(o.getVariableName())).map(o -> o.getValue()).collect(Collectors.toList());
            if (documentName != null && documentName.size() >= 1) {
                his.setDocumentName((String) documentName.get(0));
            }
            //当前任务信息
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(his.getId()).list();
            if (!tasks.isEmpty()) {
                String[] currentApprovers = new String[tasks.size()];
                List<TaskInfo> currentTasks = new ArrayList<>();
                StringBuilder taskName = new StringBuilder();
                for (int i = 0; i < tasks.size(); i++) {
                    Task task = tasks.get(i);
                    taskName.append(task.getName()).append(",");
                    currentApprovers[i] = getTaskApprove(task);
                    currentTasks.add(new TaskInfo(task.getId(), task.getName(), task.getAssignee(),
                            getUserName(task.getAssignee())));
                }
                //设置当前节点
                his.setTaskName(taskName.substring(0, taskName.length() - 1));
                // 设置当前审批人
                his.setCurrentApprover(StringUtils.join(currentApprovers, ","));
                his.setCurrentTasks(currentTasks);
                //流程描述
                his.setDescription(getProcessDescription(his.getProcessDefinitionId(), tasks.iterator().next().getExecutionId()));
            } else {
                //任务结束,处理流程描述
                if (his.getEndTime() != null) {
                    List<HistoricVariableInstance> variables = historyService.createHistoricVariableInstanceQuery().processInstanceId(his.getId()).list();
                    his.setDescription(getProcessDescription(his.getProcessDefinitionId()
                            , variables.stream().collect(Collectors.toMap(key -> key.getVariableName(), value -> value.getValue() == null ? "" : value.getValue(), (existingValue, newValue) -> newValue))));
                }
            }
//            if (showRetract && his.getEndTime() == null) {
//                his.setRecall(isStartRecall(his.getId(), iRequest.getEmployeeCode())
//                        || isTaskRecall(his.getId(), iRequest.getEmployeeCode()));
//            }
        }
        return dataResponse;
    }

    private String getProcessDescription(String processDefinitionId, Map<String, Object> variables) {
        return getProcessDescription(processDefinitionId, null, variables);
    }

    private String getProcessDescription(String processDefinitionId, String executionId) {
        return getProcessDescription(processDefinitionId, executionId, null);
    }

    private String getProcessDescription(String processDefinitionId, String
            executionId, Map<String, Object> variables) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        String description = bpmnModel.getMainProcess().getDocumentation();
        if (StringUtils.isNotEmpty(description)) {
            description = processEngineConfiguration.getCommandExecutor()
                    .execute(new GetExpressionValueCmd(executionId, description, variables)).toString();
        }
        return description;
    }


    /*
     * @Override public DataResponse queryHistoricProcessInstance(IRequest iRequest,
     * Map<String, String> params) {
     *
     * if ("involve".equalsIgnoreCase(params.get("queryType"))) {
     * params.put("involvedUser", iRequest.getEmployeeCode());
     * params.remove("startedBy"); } else if
     * ("create".equalsIgnoreCase(params.get("queryType"))) {
     * params.put("startedBy", iRequest.getEmployeeCode());
     * params.remove("involvedUser"); } else if
     * ("any".equalsIgnoreCase(params.get("queryType"))) { if
     * (!isAdmin(iRequest.getEmployeeCode())) { throw new RuntimeException(new
     * WflSecurityException(WflSecurityException.NEED_ASSIGNEE_OR_ADMIN)); } }
     *
     * DataResponse dataResponse; List<HistoricProcessInstanceResponseExt> list; if
     * ("true".equalsIgnoreCase(params.get("suspend"))) { Map<String, String> param
     * = new HashMap<>(); param.put("suspended", "true"); dataResponse =
     * processInstanceCollectionResource.getProcessInstances(param, fakeRequest); //
     * List<ProcessInstance> processInstances =
     * runtimeService.createProcessInstanceQuery().suspended().list(); list = new
     * ArrayList<>(); for (ProcessInstanceResponseExt processInstance :
     * (List<ProcessInstanceResponseExt>) dataResponse.getData()) {
     * HistoricProcessInstanceResponseExt historicTaskInstanceExt = new
     * HistoricProcessInstanceResponseExt();
     * historicTaskInstanceExt.setId(processInstance.getId());
     * historicTaskInstanceExt.setStartTime(processInstance.getStartTime());
     * historicTaskInstanceExt.setStartUserName(processInstance.getStartUserId());
     * historicTaskInstanceExt.setSuspended(true);
     * historicTaskInstanceExt.setProcessName(processInstance.
     * getProcessDefinitionName());
     * historicTaskInstanceExt.setProcessDefinitionId(processInstance.
     * getProcessDefinitionId()); list.add(historicTaskInstanceExt); }
     * dataResponse.setData(list); } else { dataResponse =
     * historicProcessInstanceCollectionResource.getHistoricProcessInstances(params,
     * fakeRequest); }
     *
     *
     * for (HistoricProcessInstanceResponseExt his :
     * (List<HistoricProcessInstanceResponseExt>) dataResponse.getData()) { if
     * (StringUtils.isNotEmpty(his.getStartUserId())) {
     * his.setStartUserName(getEmployeeName(his.getStartUserId())); } //设置全局表单url
     * String key = formService.getStartFormKey(his.getProcessDefinitionId());
     * his.setFormKey(key);
     *
     * List<Task> tasks =
     * taskService.createTaskQuery().processInstanceId(his.getId()).list(); if
     * (!tasks.isEmpty()) { if( tasks.get(0).getName() != null) { StringBuilder
     * taskName = new StringBuilder(tasks.get(0).getName());
     * his.setTaskName(taskName.toString()); } StringBuilder currentApprover = new
     * StringBuilder(getTaskApprove(tasks.get(0))); for (int i = 1; i <
     * tasks.size(); i++) { currentApprover =
     * currentApprover.append(",").append(getTaskApprove(tasks.get(i))); } //设置当前审批人
     * his.setCurrentApprover(currentApprover.toString());
     * his.setRecall(isStartRecall(his.getId(), iRequest.getEmployeeCode()) ||
     * isTaskRecall(his.getId(), iRequest.getEmployeeCode())); } List<Execution>
     * list1 =
     * runtimeService.createExecutionQuery().processInstanceId(his.getId()).list();
     * for (Execution ls : list1) { if (ls.isSuspended()) { his.setSuspended(true);
     * break; } } // 最后审批人
     *//*
     * List<HistoricTaskInstance> historicTaskInstanceList =
     * historyService.createHistoricTaskInstanceQuery()
     * .processInstanceId(his.getId()).orderByHistoricTaskInstanceEndTime().desc().
     * list(); if (historicTaskInstanceList != null &&
     * !historicTaskInstanceList.isEmpty()) { for (int i = 0; i <
     * historicTaskInstanceList.size(); i++) { HistoricTaskInstanceResponseExt
     * taskLastResponse = new
     * HistoricTaskInstanceResponseExt(historicTaskInstanceList.get(i));
     * List<Comment> comments = getCommentOfType(taskLastResponse.getId(),
     * COMMENT_ACTION); if (comments == null) { continue; } if
     * (StringUtil.isNotEmpty(taskLastResponse.getAssignee()) &&
     * StringUtil.isNotEmpty(comments.get(comments.size() - 1).getFullMessage())) {
     * if (StringUtil.isNotEmpty(comments.get(comments.size() - 1).getUserId())) {
     * his.setLastApprover(getEmployeeName(comments.get(comments.size() -
     * 1).getUserId())); his.setLastApproverCode(comments.get(comments.size() -
     * 1).getUserId()); his.setLastApproveAction(comments.get(comments.size() -
     * 1).getFullMessage()); } break; }
     *
     * } }
     *//*
     * }
     *
     * return dataResponse; }
     */
    @Override
    public DataResponse queryHistoricTaskInstances(IRequest iRequest, HistoricTaskInstanceQueryRequest
            queryRequest,
                                                   Map<String, String> allRequestParams) {
        if (allRequestParams == null) {
            allRequestParams = Collections.emptyMap();
        }

        List<HistoricActivityInstance> datas = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(queryRequest.getProcessInstanceId()).orderByHistoricActivityInstanceStartTime().asc()
                .list();

        DataResponse dataResponse = new DataResponse();
        List<HistoricTaskInstanceResponseExt> list = new ArrayList<>();

        for (HistoricActivityInstance historicActivityInstance : datas) {
            setHistoricActivityInstanceResponseExt(historicActivityInstance, list, queryRequest.getProcessInstanceId());
        }

        Iterator<HistoricTaskInstanceResponseExt> it = list.iterator();
        while (it.hasNext()) {
            HistoricTaskInstanceResponseExt i = it.next();
            if (null == i.getEndTime()) {
                it.remove();
            }
        }
        boolean key = false;
        HistoricActivityInstance endDom = null;
        for (HistoricActivityInstance ext : datas) {
            if (ext.getDeleteReason() != null) {
                key = true;
                endDom = ext;
                break;
            }
        }
        if (key) {
            HistoricTaskInstanceResponseExt ext = new HistoricTaskInstanceResponseExt();
            if (IActivitiConstants.ACT_RETRACT.equals(endDom.getDeleteReason())) {
                ext.setName("用户撤销");
            } else {
                ext.setName("管理员关闭");
            }
            ext.setAction("终止");
            ext.setEndTime(datas.get(datas.size() - 1).getEndTime());
            list.add(ext);
        }

        dataResponse.setData(list);
        return dataResponse;
    }

    /*
     * protected boolean isAdmin(String userId) { return
     * "ADMIN".equalsIgnoreCase(userId); }
     */

    protected Task getTaskById(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new ActivitiObjectNotFoundException("Could not find a task with id '" + taskId + "'.", Task.class);
        }
        return task;
    }

    private void setHistoricActivityInstanceResponseExt(HistoricActivityInstance historicActivityInstance,
                                                        List<HistoricTaskInstanceResponseExt> list, String processInstanceId) {
        String activityType = historicActivityInstance.getActivityType();
        if ("userTask".equals(activityType)) {
            List<Comment> comments = getCommentOfType(historicActivityInstance.getTaskId(), PROP_COMMENT);
            List<Comment> actions = getCommentOfType(historicActivityInstance.getTaskId(), COMMENT_ACTION);
            List<Comment> actionDescs = getCommentOfType(historicActivityInstance.getTaskId(), "actionDesc");

            if (comments != null && comments.size() != 0) {
                for (int index = comments.size() - 1; index >= 0; index--) {
                    HistoricTaskInstanceResponseExt historicTaskInstanceResponseExt = new HistoricTaskInstanceResponseExt(
                            historicActivityInstance);
                    CommentEntityImpl commentEntity = (CommentEntityImpl) comments.get(index);
                    historicTaskInstanceResponseExt.setComment(commentEntity.getFullMessage());
                    if(actionDescs != null && index < actionDescs.size() && actionDescs.get(index) != null){
                        historicTaskInstanceResponseExt.setActionDesc(actionDescs.get(index).getFullMessage());
                    }
                    historicTaskInstanceResponseExt.setAction(actions.get(index).getFullMessage());
                    historicTaskInstanceResponseExt.setAssignee(actions.get(index).getUserId());
                    StringBuilder sb = new StringBuilder();
                    String temp = getEmployeeName(actions.get(index).getUserId());
                    sb.append(StringUtil.isEmpty(temp) ? "" : temp);
                    String loginName ="";
                    if(NumberUtils.isNumber(actions.get(index).getUserId())){
                        loginName = sysUserAllocationService.selectUserNameByAllocationId(actions.get(index).getUserId());
                    }
                    //去除括号中内容
                    //sb.append(StringUtil.isEmpty(loginName) ? "" : "(" + loginName + ")");
                    historicTaskInstanceResponseExt.setAssigneeName(sb.toString());
                    historicTaskInstanceResponseExt.setEndTime(actions.get(index).getTime());
                    list.add(historicTaskInstanceResponseExt);
                }
            }
            return;
        }
        HistoricTaskInstanceResponseExt historicTaskInstanceResponseExt = new HistoricTaskInstanceResponseExt(
                historicActivityInstance);
        if ("startEvent".equalsIgnoreCase(activityType)
                && !StringUtil.isEmpty(historicActivityInstance.getActivityName())) {
            String startUser = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId)
                    .list().get(0).getStartUserId();
            historicTaskInstanceResponseExt.setAssignee(startUser);
            //historicTaskInstanceResponseExt.setAssigneeName(getEmployeeName(startUser) + "(" + startUser + ")");
            //去除括号中内容
            historicTaskInstanceResponseExt.setAssigneeName(getEmployeeName(startUser));
            historicTaskInstanceResponseExt.setAction(getEmployeeName(startUser) + "发起了审批流程");
            historicTaskInstanceResponseExt.setName("开始");
        }
        if ("endEvent".equalsIgnoreCase(activityType)
                && !StringUtil.isEmpty(historicActivityInstance.getActivityName()) ) {
            historicTaskInstanceResponseExt.setName("结束");
        }
        if (!"exclusiveGateway".equals(activityType) && !"parallelGateway".equals(activityType)
                && !"eventBasedGateway".equals(activityType) && !"inclusiveGateway".equals(activityType)
                && !("sid-activiti-workflow-end".equals(historicActivityInstance.getActivityId()))
                && null == historicActivityInstance.getDeleteReason()) {
            list.add(historicTaskInstanceResponseExt);
        }
    }

    protected List<Comment> getCommentOfType(String taskId, String type) {
        List<Comment> list = taskService.getTaskComments(taskId, type);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        AutowireCapableBeanFactory autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();
        autowireCapableBeanFactory.autowireBean(taskResource);
        autowireCapableBeanFactory.autowireBean(executionVariableCollectionResource);
        autowireCapableBeanFactory.autowireBean(taskQueryResource);
        autowireCapableBeanFactory.autowireBean(historicProcessInstanceQueryResource);
        autowireCapableBeanFactory.autowireBean(processInstanceCollectionResource);
        autowireCapableBeanFactory.autowireBean(historicTaskInstanceQueryResource);
        autowireCapableBeanFactory.autowireBean(historicProcessInstanceCollectionResource);
        autowireCapableBeanFactory.autowireBean(deadLetterJobCollectionResource);

        Map<String, ICustomTaskProcessor> listeners = applicationContext.getBeansOfType(ICustomTaskProcessor.class);
        taskProcessors = new ArrayList<>();
        taskProcessors.addAll(listeners.values());
        Collections.sort(taskProcessors);
    }

    @Override
    public Boolean isStartRecall(String procId, String allocationId) {
        List<Comment> list = taskService.getProcessInstanceComments(procId);
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(procId)
                .suspended().singleResult();
        String startAllocationId = historyService.createHistoricProcessInstanceQuery().processInstanceId(procId).list().get(0)
                .getStartUserId();
        /*String userId =
                historyService.createHistoricVariableInstanceQuery().processInstanceId(procId).list().stream().filter(historicVariableInstance -> historicVariableInstance.getVariableName().equalsIgnoreCase("startUserId")).collect(Collectors.toList()).get(0).getValue().toString();*/
        if (list.isEmpty() && null == processInstance && allocationId.equalsIgnoreCase(startAllocationId)) {
            return true;
        }
        return false;
    }

    @Override
    public Boolean isTaskRecall(String procId, String employeeCode) {
        //TODO： 检查逻辑
        try {
            // 处于挂起或者结束状态的流程 不能撤回
            ProcessInstance suspendedProc = runtimeService.createProcessInstanceQuery().processInstanceId(procId)
                    .suspended().singleResult();
            if (suspendedProc != null) {
                return false;
            }

            HistoricProcessInstance finishedProc = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(procId).finished().singleResult();
            if (finishedProc != null) {
                return false;
            }
            // 加签的任务不能撤回
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(procId).list();
            if (tasks != null) {
                for (Task task : tasks) {
                    if (task.getOwner() != null) {
                        return false;
                    }
                }
            }


            List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(procId).orderByHistoricActivityInstanceStartTime().asc().list();

            int nodeNum = 0;
            for (HistoricActivityInstance hisActInstance : historicActivityInstances) {
                String activityType = hisActInstance.getActivityType();
                if ("userTask".equalsIgnoreCase(activityType)) {
                    nodeNum++;
                }
            }

            HistoricActivityInstance lastHistoricActivityInstance = historicActivityInstances
                    .get(historicActivityInstances.size() - 1);
            // 获取活动历史表最后一条记录的executionId 根据executionId找到execution的父execution
            // 找出父execution的孩子execution
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(procId).list();
            if (taskList.size() == 0) {
                return false;
            }
            String executionId = taskList.iterator().next().getExecutionId();
            Execution execution = runtimeService.createExecutionQuery().processInstanceId(procId).executionId(executionId)
                    .singleResult();
            List<Execution> childExecutionList = runtimeService.createExecutionQuery().processInstanceId(procId)
                    .parentId(execution.getParentId()).list();
            // 获取当前处于激活状态的执行器
            String activityId = lastHistoricActivityInstance.getActivityId();
            List<Execution> activeExecutionList = runtimeService.createExecutionQuery().processInstanceId(procId)
                    .activityId(activityId).list();
            // 孩子execution>1 表示当前是多实例 比较孩子execution和激动状态的execution数量 如果数量不一致 表示多实例情况下 有人审批了
            // 不允许撤回
            if (childExecutionList.size() > 1 && activeExecutionList.size() != childExecutionList.size()) {
                return false;
            }

            if ("userTask".equalsIgnoreCase(lastHistoricActivityInstance.getActivityType())
                    && lastHistoricActivityInstance.getEndTime() == null) {
                String multiActivityId = "";
                boolean isLastApprove = true;
                for (int i = historicActivityInstances.size() - 1; i >= 0; i--) {
                    HistoricActivityInstance historicActivityInstance = historicActivityInstances.get(i);
                    String assignee = historicActivityInstance.getAssignee();
                    String activityType = historicActivityInstance.getActivityType();
                    if ("userTask".equalsIgnoreCase(activityType) && historicActivityInstance.getEndTime() == null) {
                        continue;
                    }
                    if ("parallelGateway".equalsIgnoreCase(activityType)
                            || "exclusiveGateway".equalsIgnoreCase(activityType)
                            || "eventBasedGateway".equalsIgnoreCase(activityType)
                            || "inclusiveGateway".equalsIgnoreCase(activityType)) {
                        continue;
                    }
                    // 最近审批的那个人
                    if (isLastApprove) {
                        // 如果当前任务审批人与最近的一次任务审批人为同一人 获取最近审批的act_id
                        if ("userTask".equalsIgnoreCase(activityType) && assignee != null
                                && assignee.equalsIgnoreCase(employeeCode)) {
                            multiActivityId = historicActivityInstance.getActivityId();
                            isLastApprove = false;
                            // 连续两个任务都是同一人审批，且撤回了，最近一次审批可能是撤回操作
                            String deleteReson = historicActivityInstance.getDeleteReason();
                            if ("jump".equalsIgnoreCase(deleteReson)) {
                                return false;
                            }
                            BpmnModel bpmnModel = repositoryService.getBpmnModel(historicActivityInstance.getProcessDefinitionId());
                            FlowElement flowElement = bpmnModel.getMainProcess().getFlowElement(historicActivityInstance.getActivityId());
                            if (!ActivitiUtils.isEnabledRevoke((UserTask) flowElement) && nodeNum > 1) {
                                return false;
                            }
                            continue;
                        } else {
                            return false;
                        }
                    }
                    // 如果相同act_id 连续数量超过1 表示前面是个多实例任务 不允许撤回
                    if (multiActivityId.equalsIgnoreCase(historicActivityInstance.getActivityId())) {
                        return false;
                    } else {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public void taskRecall(IRequest iRequest, String procId, String employeeCode) {
        List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(procId).activityType("userTask").orderByHistoricActivityInstanceStartTime().asc().list();
        String taskId = historicActivityInstances.get(historicActivityInstances.size() - 1).getTaskId();
        String activityId = "";
        for (int i = historicActivityInstances.size() - 1; i >= 0; i--) {
            HistoricActivityInstance historicActivityInstance = historicActivityInstances.get(i);
            String assignee = historicActivityInstance.getAssignee();
            String activityType = historicActivityInstance.getActivityType();
            if ("userTask".equalsIgnoreCase(activityType) && historicActivityInstance.getEndTime() == null) {
                continue;
            }
            if ("userTask".equalsIgnoreCase(activityType) && assignee.equalsIgnoreCase(employeeCode)) {
                activityId = historicActivityInstance.getActivityId();
            }
            break;
        }
        ProcessInstance procInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(procId).list().iterator().next();
        TaskRecallEvent event = new TaskRecallEvent(new TaskRecallInfo(procInstance, taskService.getVariables(taskId), employeeCode));
        event.setiRequest(iRequest);
        Authentication.setAuthenticatedUserId(employeeCode);
        taskService.addComment(taskId, procId, COMMENT_ACTION, RECALL);
        taskService.addComment(taskId, procId, PROP_COMMENT, employeeCode + "撤回审批");
        JumpActivityCmd cmd = new JumpActivityCmd(taskId, activityId);
        processEngineConfiguration.getCommandExecutor().execute(cmd);
        publisher.publishEvent(event);
    }

    @Override
    public void startRecall(IRequest iRequest, String procId, String employeeCode) {
        String taskId = taskService.createTaskQuery().processInstanceId(procId).list().iterator().next().getId();
        ProcessInstance procInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(procId).list().iterator().next();
        TaskRecallEvent event = new TaskRecallEvent(new TaskRecallInfo(procInstance, taskService.getVariables(taskId), employeeCode, true));
        event.setiRequest(iRequest);
        runtimeService.deleteProcessInstance(procId, IActivitiConstants.ACT_RETRACT);
        publisher.publishEvent(event);
    }

    @Override
    public List<ProcessInstanceForecast> processInstanceForecast(IRequest request, String processInstanceId) {


        Map<String, List<HistoricTaskInstanceResponseExt>> history = new HashMap<>();
        // 获取流程活动历史
        List<HistoricActivityInstance> historicActivityInstanceList = historyService
                .createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).activityType("userTask")
                .orderByHistoricActivityInstanceEndTime().asc().list();
        if (historicActivityInstanceList == null || historicActivityInstanceList.isEmpty()) {
            return null;
        }
        String processDefinitionId = historicActivityInstanceList.get(0).getProcessDefinitionId();
        for (HistoricActivityInstance historicActivityInstance : historicActivityInstanceList) {
            List<HistoricTaskInstanceResponseExt> list = new ArrayList<>();
            setHistoricActivityInstanceResponseExt(historicActivityInstance, list, processInstanceId);
            List<HistoricTaskInstanceResponseExt> value = history.get(historicActivityInstance.getActivityId());
            if (value != null && !value.isEmpty()) {
                value.addAll(list);
            } else {
                value = list;
            }
            if (!value.isEmpty()) {
                for (HistoricTaskInstanceResponseExt hisExt : value) {
                    //根据用户ID查询职位和部门
                    Employee employee = entityService.queryInfoByUserId(hisExt.getAssignee());
                    hisExt.setPositionName(employee.getPositionName());
                    hisExt.setUnitName(employee.getUnitName());
                }
                history.put(historicActivityInstance.getActivityId(), value);
            }
        }

        List<ProcessInstanceForecast> processInstanceForecastList = new ArrayList<>();
        // 获取当前活动节点execution
        List<Execution> executions = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).list();
        boolean isFinish = false;
        if (executions == null || executions.isEmpty()) {
            isFinish = true;
        } else {
            Execution execution = executions.get(executions.size() - 1);
            ForecastActivityCmd.executionId.set(execution.getId());
          /*  // 获取当前节点审批人
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
            if (!tasks.isEmpty()) {
                currentApprovers = new LinkedHashSet<>();
                currentTaskDefKey = tasks.get(0).getTaskDefinitionKey();
                for (int i = 0; i < tasks.size(); i++) {
                    currentApprovers.addAll(getTaskApproveInfo(tasks.get(i)));
                }
            }*/
        }
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        // 从model中获取用户任务信息
        for (UserTask flowNode : bpmnModel.getProcesses().get(0).findFlowElementsOfType(UserTask.class)) {
            ProcessInstanceForecast forecast = new ProcessInstanceForecast();
            String key = flowNode.getId();
            forecast.setGraphicInfo(bpmnModel.getGraphicInfo(key));
            forecast.setTaskId(key);
            forecast.setTaskName(flowNode.getName());
            if (isFinish) {
                forecast.setExecuted(true);
            }
            // 判断当前节点是否审批过
            List<HistoricTaskInstanceResponseExt> historyInfo = history.get(key);
            if (historyInfo != null) {
                HistoricTaskInstanceResponseExt info = historyInfo.get(0);
                // 如果是撤回操作，当前节点其实还未审批
                if (!RECALL.equalsIgnoreCase(info.getAction())) {
                    forecast.setExecuted(true);
                }
                forecast.setHistory(historyInfo);
            }
            boolean needFrecast = !forecast.isExecuted();
            boolean isApproveChain = isApproveChain(flowNode, processDefinitionId);
            int executedCount = 0;
            // 审批链情况 获取执行过的审批链数量
            if (isApproveChain) {
                if (historyInfo != null) {
                    boolean hasAddSign = false;
                    for (HistoricTaskInstanceResponseExt index : historyInfo) {
                        if (ADD_SIGN.equalsIgnoreCase(index.getAction())) {
                            hasAddSign = true;
                        }
                        if (ADD_SIGN.equalsIgnoreCase(index.getAction())
                                || DELEGATE.equalsIgnoreCase(index.getAction())
                                || JUMP.equalsIgnoreCase(index.getAction())
                                || RECALL.equalsIgnoreCase(index.getAction())
                                || AUTO_DELEGATE.equalsIgnoreCase(index.getAction())) {
                        } else {
                            // 加签的人审批
                            if (hasAddSign) {
                                hasAddSign = false;
                            } else {
                                executedCount++;
                            }
                        }
                    }
                }
            }

            ForecastActivityCmd.executedCount.set(executedCount);
            if (needFrecast) {
                ForecastActivityCmd.userTask.set(flowNode);
                boolean unknown = false;
                Set<Employee> approve = (Set) processEngineConfiguration.getCommandExecutor()
                        .execute(forecastActivityCmd);
                if (approve == null || approve.isEmpty()) {
                    unknown = true;
                }
                // 审批链情况，预测结果是当前审批者和表达式预测
                if (forecast.getForecast() == null) {
                    forecast.setForecast(approve);
                } else if (!unknown) {
                    forecast.getForecast().addAll(approve);
                }
                ForecastActivityCmd.userTask.remove();
            }
            processInstanceForecastList.add(forecast);
        }
        if (ForecastActivityCmd.executionId.get() != null) {
            ForecastActivityCmd.executionId.remove();
        }
        if (ForecastActivityCmd.executedCount.get() != null) {
            ForecastActivityCmd.executedCount.remove();
        }
        return processInstanceForecastList;
    }

    private boolean isApproveChain(UserTask task, String processdefinitionid) {
        if (ActivitiUtils.isUseNewModelEditor(task) || !ActivitiUtils.isAddApproveChain(task)) {
            return false;
        }
        ApproveChainHeader approveChainHeader = approveChainHeaderService
                .selectByUserTask(StringUtils.substringBefore(processdefinitionid, ":"), task.getId());
        if (approveChainHeader == null) {
            return false;
        }
        return true;
    }

    @Override
    public List<ActiviException> queryException(IRequest iRequest, ActiviException exception, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        List<ActiviException> list = exceptionMapper.selectAllException(exception);
        return list;
    }

    @Override
    public void executeTaskByAdmin(IRequest request, String procId, TaskActionRequestExt taskActionRequest)
            throws TaskActionException {
        // Task task =
        // taskService.createTaskQuery().processInstanceId(procId).list().get(0);
        this.executeTaskAction(request, taskActionRequest.getCurrentTaskId(), taskActionRequest, true);
    }

    private String processApproveName(String allocationId) {
        String userName = getUserName(allocationId);
        //获取登录名
        String loginName = sysUserAllocationService.selectUserNameByAllocationId(allocationId);
        if (org.apache.commons.lang3.StringUtils.isNoneBlank(userName, loginName)) {
            return userName + "(" + loginName + ")";
        } else {
            return allocationId;
        }
    }

    private String getTaskApprove(Task task) {
        if (StringUtils.isNotEmpty(task.getAssignee())) {
            return processApproveName(task.getAssignee());
        } else {
            List<IdentityLink> idList = taskService.getIdentityLinksForTask(task.getId());
            List<String> nameList = new ArrayList<>();
            for (IdentityLink il : idList) {
                if (il.getGroupId() != null) {
                    nameList.add(getGroupName(il.getGroupId()));
                } else if (il.getUserId() != null) {
                    nameList.add(processApproveName(il.getUserId()));
                }
            }
            return StringUtils.join(nameList.toArray(), ";");
        }
    }

//    private Set<Employee> getTaskApproveInfo(Task task) {
//        Set<Employee> employees = new LinkedHashSet<>();
//        if (StringUtils.isNotEmpty(task.getAssignee())) {
//            Employee e = employeeService.queryInfoByCode(task.getAssignee());
//            if (e != null) {
//                employees.add(e);
//            }
//        } else {
//            List<IdentityLink> idList = taskService.getIdentityLinksForTask(task.getId());
//            for (IdentityLink il : idList) {
//                if (il.getGroupId() != null) {
//                    List<Employee> emps = employeeService.selectByPostionCode(il.getGroupId());
//                    employees.addAll(emps);
//                } else if (il.getUserId() != null) {
//                    Employee e = employeeService.queryInfoByCode(il.getUserId());
//                    if (e != null) {
//                        employees.add(e);
//                    }
//                }
//            }
//        }
//        return employees;
//    }


    /**
     * 根据allocationId查找用户id
     *
     * @param allocationId
     * @return
     */
    private Long getUserIdByAlloctionId(String allocationId) {
        if (allocationId != null && !allocationId.equalsIgnoreCase("")) {
            Long userId = sysUserAllocationMapper.selectUserIdByAllocationId(allocationId);
            if (userId != null) {
                return userId;
            }
        }

        return null;
    }

    public void carbonCopyWorkFlow(IRequest request, String carbonCopyUsers, String taskId, String processInstanceId) {

        Authentication.setAuthenticatedUserId(request.getUserName());
        if (StringUtil.isNotEmpty(carbonCopyUsers)) {
            HlsCusEmployee employee = new HlsCusEmployee();
            employee.setEmployeeCode(request.getUserName());
            List<HlsCusEmployee> carbonCopyFrom = hlsCusEmployeeMapper.selectEmployeeNameByCode(employee);

            String[] employeeCodes = carbonCopyUsers.split(",");
            employee.setEmployeeCodes(employeeCodes);
            employee.setEmployeeCode(null);
            List<HlsCusEmployee> carbonCopyTo = hlsCusEmployeeMapper.selectEmployeeNameByCode(employee);

            Set<String> users = org.springframework.util.StringUtils.commaDelimitedListToSet(carbonCopyUsers);
            Map<String, Object> params = new HashMap<>();
            params.put("processInstanceId", processInstanceId);
            params.put("users", users);
            String comment = carbonCopyFrom.get(0).getName() + " 抄送给 " + carbonCopyTo.get(0).getName();
            taskService.addComment(taskId, processInstanceId, COMMENT_ACTION, CARBON_COPY);
            Comment comment1 = taskService.addComment(taskId, processInstanceId, PROP_COMMENT, comment);
            if (params == null) {
                params = new HashMap<>();
            }
            //保留原始的userId
            Long originalUserId = request.getUserId();
            Set<String> usersSet = org.springframework.util.StringUtils.commaDelimitedListToSet(carbonCopyUsers);
            List<SysUser> sysUserList = sysUserMapper.queryCarbonCopyByAssignee(usersSet);
            String url = "/wfl/wfl_maintenance.view?commentId=" + comment1.getId() + "&employeeCode=" + carbonCopyUsers + "&taskId=" + taskId + "&currentProcessInstanceId=" + processInstanceId;
            String message = comment;
            params.put("message", message);
            params.put("noticeTitle", "工作流抄送");
            params.put("level", 3);
            params.put("noticeType", "TODO");
            params.put("url", url);
            sysEventService.eventSave(request, Long.valueOf(processInstanceId), CARBON_COPY, CARBON_COPY, SYS_MODULE, SysEventCodeUtil.PROPERTY_WFL_CARBONCOPY, SysEventCodeUtil.PROPERTITY_RELATIONTYPE_D2D, params);
            //循环发送给抄送人
            for (SysUser dto : sysUserList) {
                request.setUserId(dto.getUserId());
                sysEventService.eventSave(request, Long.valueOf(processInstanceId), CARBON_COPY, CARBON_COPY, SYS_MODULE, SysEventCodeUtil.PROPERTY_WFL_CARBONCOPY, SysEventCodeUtil.PROPERTITY_RELATIONTYPE_D2D, params);

            }
            //还原request中的userId
            request.setUserId(originalUserId);

        }
    }


    public ResponseData queryWflHis(IRequest iRequest,String businessKey, String workFlowType){
        List<String> historyProcessInstanceIdList = actHiTaskinstMapper.selectHistoryProcessInstanceIdList(workFlowType, businessKey);
        List<HistoricTaskInstanceResponseExt> list = new ArrayList<>();
        if (historyProcessInstanceIdList.size() > 0) {
            for (String id : historyProcessInstanceIdList) {
                List<HistoricActivityInstance> historicActivityInstanceList = historyService
                        .createHistoricActivityInstanceQuery().processInstanceId(id).list();
                for (HistoricActivityInstance historicActivityInstance : historicActivityInstanceList) {
                    setHistoricActivityInstanceResponseExt(historicActivityInstance, list, id);
                }
            }
        }
        for(HistoricTaskInstanceResponseExt responseExt:list){
            String actionDesc = codeService.getCodeMeaningByValue(iRequest,"WLF.ACTION",responseExt.getAction());
            responseExt.setActionDesc(actionDesc);
        }
        list.sort(Comparator.comparing(HistoricTaskInstanceResponseExt::getEndTime));
        //过滤空行
        return new ResponseData(list.stream().filter(htire -> htire.getAction() != null && htire.getAssigneeName() != null).collect(Collectors.toList()));
    }

    //用来更新授信主体的、已用金额
    public void updateUsedAmountByChance(Task task,boolean flag){
        String pName = taskNewMapper.queryTaskProcessName(task.getProcessInstanceId());
        if("额度占用审批流程".equalsIgnoreCase(pName)&&("退回经办人".equals(task.getName())||!flag)){
            List<TaskNew> taskNews = taskNewMapper.queryTasks(task.getProcessInstanceId());
            Long businessKey=0L;
            for (TaskNew taskNew : taskNews) {
                if(taskNew.getBusinessKey()!=null){
                    businessKey= Long.valueOf(taskNew.getBusinessKey());
                    break;
                }
            }
            HlsCusPrjProjectBp hlsCusPrjProjectBp=new HlsCusPrjProjectBp();
            hlsCusPrjProjectBp.setProjectId(businessKey);
            List<HlsCusPrjProjectBp> reportBps
                    = hlsCusPrjProjectBpMapper.select(hlsCusPrjProjectBp);
            for (HlsCusPrjProjectBp reportBp : reportBps) {
                if("TENANT".equalsIgnoreCase(reportBp.getBpType())){
                    HlsCusPrjProject prjProject =new HlsCusPrjProject();
                    prjProject.setProjectId(businessKey);
                    List<Map> prjProjects = hlsCusPrjProjectMapper.queryPrjDetailSecond(prjProject);
                    HlsCusHlsCreditLineChanceBp chanceBp=new HlsCusHlsCreditLineChanceBp();
                    chanceBp.setChanceId(reportBp.getChanceId());
                    List<HlsCusHlsCreditLineChanceBp> hlsCusHlsCreditLineChanceBps = hlsCusHlsCreditLineChanceBpMapper.selectByForeignKey(chanceBp);
                    for (HlsCusHlsCreditLineChanceBp hlsCusHlsCreditLineChanceBp : hlsCusHlsCreditLineChanceBps) {
                        if(hlsCusHlsCreditLineChanceBp.getBpId().equals(reportBp.getBpId())){
                            chanceBp=hlsCusHlsCreditLineChanceBp;
                        }
                    }
                    BigDecimal leaseItemAmount = (BigDecimal) prjProjects.get(0).get("lease_item_amount");
                    if(flag){
                        chanceBp.setCreditAmountUsed(chanceBp.getCreditAmountUsed()+leaseItemAmount.doubleValue());
                    }else{
                        chanceBp.setCreditAmountUsed(chanceBp.getCreditAmountUsed()-leaseItemAmount.doubleValue());
                    }
                    creditLineChanceBpService.updateUsedAmountByBpId(chanceBp);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void contractChangeEndDataTransferTask(String approveResult,Long changeReqId,String documentCategory)  {
        logger.info("========== 变更结束-迁移数 ==========");
        logger.info("changeReqId:{},approveResult:{}",changeReqId,approveResult);
        if (StringUtils.equals(approveResult, APPROVED)) {
            long start = System.currentTimeMillis();
            if (DOCUMENT_CATEGORY_CON.equals(documentCategory)) {
                documentHistoryDetailMapper.dataTransfer(changeReqId);
                //删除已迁移数据
                documentHistoryDetailMapper.deleteTransferData(changeReqId);
                //迁移 sys_document_history_blob 数据
                documentHistoryBlobMapper.dataTransfer(changeReqId);
                //删除已迁移数据
                documentHistoryBlobMapper.deleteTransferData(changeReqId);
            } else if (DOCUMENT_CATEGORY_BP.equals(documentCategory)) {
                documentHistoryDetailMapper.bpDataTransfer(changeReqId);
                //删除已迁移数据
//                documentHistoryDetailMapper.bpDeleteTransferData(changeReqId);
                //迁移 sys_document_history_blob 数据
                documentHistoryBlobMapper.bpDataTransfer(changeReqId);
                //删除已迁移数据
                documentHistoryBlobMapper.bpDeleteTransferData(changeReqId);
            }

            logger.info("========== 合同变更结束迁移变更数据结束：耗时{} ==========",System.currentTimeMillis() - start);
        }
    }

    @Override
    public ResponseData queryWflHistoryList(Map<String, Object> taskInfo) {
        if(taskInfo.containsKey("project_id")){
            if(taskInfo.get("project_id") != null){
                Long projectId = Long.valueOf((String) taskInfo.get("project_id"));
                String processIds = taskNewMapper.getProcessIdsByprojectId(projectId);
                if(StringUtils.isNotEmpty(processIds)){
                    taskInfo.put("processIds",processIds.split(","));
                }else {
                    taskInfo.put("processIdNullFlag","Y");
                }
            }
        }

        List<Map> taskList = taskNewMapper.queryWflHistoryInfo(taskInfo);
        return new ResponseData(taskList);
    }
}

