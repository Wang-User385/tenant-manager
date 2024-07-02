package com.hand.hap.activiti.service;

import com.hand.hap.activiti.custom.process.CustomHistoricProcessInstanceQueryRequest;
import com.hand.hap.activiti.dto.*;
import com.hand.hap.activiti.exception.TaskActionException;
import com.hand.hap.activiti.exception.WflSecurityException;
import com.hand.hap.activiti.exception.dto.ActiviException;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.exception.HlsCusException;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.repository.Model;
import org.activiti.engine.task.Task;
import org.activiti.rest.common.api.DataResponse;
import org.activiti.rest.service.api.history.HistoricProcessInstanceQueryRequest;
import org.activiti.rest.service.api.history.HistoricTaskInstanceQueryRequest;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceResponse;
import org.activiti.rest.service.api.runtime.task.TaskQueryRequest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author shengyang.zhou@hand-china.com
 */
public interface IActivitiService extends ProxySelf<IActivitiService> {

    ProcessInstanceResponse startProcess(IRequest iRequest, ProcessInstanceCreateRequest createRequest);

    Model deployModel(String modelId) throws Exception;

    void completeTask(IRequest request, Task taskEntity, TaskActionRequestExt actionRequest) throws TaskActionException;

    void delegateTask(IRequest request, Task taskEntity, TaskActionRequestExt actionRequest) throws TaskActionException;

    void carbonCopy(IRequest request, Task taskEntity, TaskActionRequestExt actionRequest) throws TaskActionException;

    void resolveTask(IRequest request, Task taskEntity, TaskActionRequestExt actionRequest) throws TaskActionException;

    void jumpTo(IRequest request, Task taskEntity, TaskActionRequestExt actionRequest);

    void passTo(IRequest iRequest, String procId) throws HlsCusException;

    void rejectTo(IRequest iRequest, String procId) throws HlsCusException;

    void jumpActivitiTo(IRequest iRequest, String procId, String jumpTarget, String jumpTargetName) throws HlsCusException;

    @Transactional
    void executeTaskAction(IRequest request, String taskId, TaskActionRequestExt taskActionRequest, boolean isAdmin)
            throws TaskActionException;

    List<ActivitiNode> getProcessNodes(IRequest request, String processDefinitionId);

    List<ActivitiNode> getUserTaskFromModelSource(IRequest request, String modelId);

    String getEmployeeName(String userId);

    String getUserName(String userId);

    String getGroupName(String groupId);

    TaskResponseExt getTaskDetails(IRequest request, String taskId) throws WflSecurityException;

    TaskResponseExt getTaskDetails(IRequest request, String taskId, boolean isAdmin) throws WflSecurityException;

    HistoricProcessInstanceResponseExt getInstanceDetail(IRequest request, String processInstanceId);

    DataResponse queryTaskList(IRequest iRequest, TaskQueryRequest taskQueryRequest,
                               Map<String, String> requestParams);

     List<TaskNew> queryTaskListNew(TaskNew dto, int pageNum, int pageSize);

    public DataResponse queryProcessInstances(IRequest iRequest, CustomHistoricProcessInstanceQueryRequest historicProcessInstanceQueryRequest, Map<String, String> requestParams, boolean showRetract);

  /*  DataResponse queryHistoricProcessInstance(IRequest iRequest, Map<String, String> params);*/

    DataResponse queryHistoricTaskInstances(IRequest iRequest, HistoricTaskInstanceQueryRequest queryRequest,
                                            @RequestParam Map<String, String> allRequestParams);

    Boolean isStartRecall(String procId, String employeeCode);

    Boolean isTaskRecall(String procId, String employeeCode);

    @Transactional(rollbackFor = Exception.class)
    void taskRecall(IRequest iRequest, String procId, String employeeCode);

    @Transactional(rollbackFor = Exception.class)
    void startRecall(IRequest iRequest, String procId, String employeeCode);

    List<ActiviException> queryException(IRequest iRequest, ActiviException exception, int page, int pagesize);

    void executeTaskByAdmin(IRequest request, String procId, TaskActionRequestExt taskActionRequest)
            throws TaskActionException;

    List<ProcessInstanceForecast> processInstanceForecast(IRequest request, String processInstanceId);

    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = Exception.class)
    void saveException(String taskId, ActivitiException exception);

    void deleteDeployment(String deploymentId, Boolean cascade);

    void deleteProcessInstance(String processInstanceId);

    void processCarbonCopyRead(String processInstanceId, String employeeCode);

    boolean queryTaskByTaskId(HttpServletRequest request, String taskId);

    void carbonCopyWorkFlow(IRequest request, String carbonCopyUsers, String taskId, String processInstanceId);

    ResponseData queryWflHis(IRequest iRequest,String businessKey, String workFlowType);

    void contractChangeEndDataTransferTask (String approveResult,Long changeReqId,String documentCategory);

    ResponseData queryWflHistoryList(Map<String, Object> taskInfo);
}
