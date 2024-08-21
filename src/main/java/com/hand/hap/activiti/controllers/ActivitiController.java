package com.hand.hap.activiti.controllers;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hand.hap.account.dto.User;
import com.hand.hap.account.service.IUserService;
import com.hand.hap.activiti.components.ApprovalRule;
import com.hand.hap.activiti.core.IActivitiConstants;
import com.hand.hap.activiti.custom.process.CustomHistoricProcessInstanceQueryRequest;
import com.hand.hap.activiti.custom.task.CustomTaskQueryRequest;
import com.hand.hap.activiti.dto.*;
import com.hand.hap.activiti.exception.TaskActionException;
import com.hand.hap.activiti.exception.WflSecurityException;
import com.hand.hap.activiti.exception.dto.ActiviException;
import com.hand.hap.activiti.service.IActiviCancelService;
import com.hand.hap.activiti.service.IActivitiEntityService;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.exception.HlsCusException;
import leaf.bean.LeafRequestData;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.ModelQueryProperty;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.util.io.InputStreamSource;
import org.activiti.engine.query.QueryProperty;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.rest.common.api.DataResponse;
import org.activiti.rest.service.api.RestResponseFactory;
import org.activiti.rest.service.api.history.HistoricTaskInstanceQueryRequest;
import org.activiti.rest.service.api.repository.ModelRequest;
import org.activiti.rest.service.api.repository.ModelResponse;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author shengyang.zhou@hand-china.com
 */
@Controller
@RequestMapping(value = {"/wfl", "/api/wfl"})
public class ActivitiController extends BaseController {
    private static Map<String, QueryProperty> allowedSortProperties = new HashMap<String, QueryProperty>();

    static {
        allowedSortProperties.put("id", ModelQueryProperty.MODEL_ID);
        allowedSortProperties.put("category", ModelQueryProperty.MODEL_CATEGORY);
        allowedSortProperties.put("createTime", ModelQueryProperty.MODEL_CREATE_TIME);
        allowedSortProperties.put("key", ModelQueryProperty.MODEL_KEY);
        allowedSortProperties.put("lastUpdateTime", ModelQueryProperty.MODEL_LAST_UPDATE_TIME);
        allowedSortProperties.put("name", ModelQueryProperty.MODEL_NAME);
        allowedSortProperties.put("version", ModelQueryProperty.MODEL_VERSION);
        allowedSortProperties.put("tenantId", ModelQueryProperty.MODEL_TENANT_ID);
    }

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RestResponseFactory restResponseFactory;

    @Autowired
    @Qualifier(value = "activitiUserServiceImpl")
    private IActivitiEntityService customEntityService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IActivitiService activitiService;

    @Autowired
    protected ProcessEngineConfigurationImpl processEngineConfiguration;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ApprovalRule approvalRule;

    @Autowired
    private IUserService userService;

    @Autowired
    private IActiviCancelService activiCancelService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 创建流程,使用当前登录用户.
     *
     * @param request
     * @param httpRequest
     * @param response
     * @return
     */
    @RequestMapping(value = "/runtime/process-instances", method = RequestMethod.POST, produces = "application/json")
    public ProcessInstanceResponse createProcessInstance(@RequestBody ProcessInstanceCreateRequest request,
                                                         HttpServletRequest httpRequest, HttpServletResponse response) {
        IRequest iRequest = createRequestContext(httpRequest);
        String userId = httpRequest.getParameter("userId");
        if (StringUtils.isNotEmpty(userId) && "ADMIN".equalsIgnoreCase(iRequest.getUserName())) {
            // allow ADMIN to simulate other user
            // FOR TEST ONLY
            iRequest.setUserId(Long.valueOf(userId));
        }
        return activitiService.startProcess(iRequest, request);
    }

    @RequestMapping(value = "/definition/user-tasks/{processInstanceId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData getAvailableUserTask(HttpServletRequest request, @PathVariable String processInstanceId) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(activitiService.getProcessNodes(iRequest, processInstanceId));
    }

    /**
     * 待办事项,个人（强制根据员工号过滤）
     *
     * @param request
     * @param requestParams
     * @param httpRequest
     * @return
     */
    @RequestMapping(value = "/query/tasks", method = RequestMethod.POST, produces = "application/json")
    public DataResponse getQueryResult(@RequestBody CustomTaskQueryRequest request,
                                       @RequestParam Map<String, String> requestParams, HttpServletRequest httpRequest) throws WflSecurityException {
        IRequest iRequest = createRequestContext(httpRequest);
        if (iRequest.getUserId() == null) {
            throw new WflSecurityException("错误的用户ID");
        }
        request.setCandidateOrAssigned(String.valueOf(iRequest.getUserId()));
        DataResponse dataResponse = activitiService.queryTaskList(iRequest, request, requestParams);
        return dataResponse;
    }

    /**
     * 待办事项,个人（强制根据员工号过滤）
     *
     * @param requestParams
     * @param httpRequest
     * @return
     */
//    public ResponseData leafGetQueryResult(@RequestParam Map<String, String> requestParams,
//                                           HttpServletRequest httpRequest) throws WflSecurityException {
//        CustomTaskQueryRequest request = new CustomTaskQueryRequest();
//        IRequest iRequest = createRequestContext(httpRequest);
//        if (StringUtils.isEmpty(String.valueOf(iRequest.getUserId()))) {
//            throw new WflSecurityException(WflSecurityException.USER_NOT_RELATE_EMP);
//        }
//        if (!(StringUtil.isNotEmpty(httpRequest.getParameter("isAdmin")) && "Y".equals(httpRequest.getParameter("isAdmin")))) {
//            request.setCandidateOrAssigned(String.valueOf(iRequest.getUserName()));
//        }
//        DataResponse dataResponse = activitiService.queryTaskList(iRequest, request, requestParams);
//        ResponseData responseData = new ResponseData();
//        if (dataResponse != null) {
//            responseData.setSuccess(true);
//            responseData.setRows((List<RestResponseFactory>) dataResponse.getData());
//            responseData.setTotal(dataResponse.getTotal());
//        }
//        return responseData;
//    }(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
    @RequestMapping(value = "/leaf/query/tasks", method = RequestMethod.POST, produces = "application/json")
    public ResponseData leafGetQueryResult(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                           @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                           @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,
                                           HttpServletRequest request) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        if (param == null) {
            return new ResponseData(false, "请求参数缺失!");
        }
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        TaskNew dto = param.toJavaObject(TaskNew.class);

        Object ownerLike = param.get("ownerLike");
        if (ownerLike != null) {
            dto.setStart_user_name(String.valueOf(ownerLike));
        }

        String nameLike = (String) param.get("nameLike");
        if (nameLike != null && !"".equalsIgnoreCase(nameLike)) {
            dto.setProcess_name(nameLike);
        }
        String createdAfter = (String) param.get("createdAfter");
        if (createdAfter != null && !"".equalsIgnoreCase(createdAfter)) {
            dto.setCreate_time_after(createdAfter);
        }
        String createdBefore = (String) param.get("createdBefore");
        if (createdBefore != null && !"".equalsIgnoreCase(createdBefore)) {
            dto.setCreate_time_before(createdBefore);
        }

        if (!"Y".equals(request.getParameter("isAdmin"))) {
            Long assignee = iRequest.getAttribute("allocationId");
            if (assignee != null) {
                dto.setAssignee(String.valueOf(assignee));
            }
        }

        return new ResponseData(activitiService.queryTaskListNew(dto, pagenum, pagesize));
    }

    /**
     * 待办事项，管理员用(可以任意查询)
     *
     * @param request
     * @param requestParams
     * @param httpRequest
     * @return
     */
    @RequestMapping(value = "/query/tasks/admin", method = RequestMethod.POST, produces = "application/json")
    public DataResponse taskListAdmin(@RequestBody CustomTaskQueryRequest request,
                                      @RequestParam Map<String, String> requestParams,
                                      HttpServletRequest httpRequest) {
        IRequest iRequest = createRequestContext(httpRequest);
        DataResponse dataResponse = activitiService.queryTaskList(iRequest, request, requestParams);
        return dataResponse;
    }

    /**
     * 审批记录
     *
     * @param queryRequest
     * @param allRequestParams
     * @param request
     * @return
     */
    @RequestMapping(value = "/query/historic-task-instances")
    public DataResponse queryProcessInstances(HistoricTaskInstanceQueryRequest queryRequest,
                                              @RequestParam Map<String, String> allRequestParams, HttpServletRequest request) {

        IRequest iRequest = createRequestContext(request);
        return activitiService.queryHistoricTaskInstances(iRequest, queryRequest, allRequestParams);
    }

    /**
     * 我参与的流程
     */
    @RequestMapping(value = "/leaf/query/process-instances/my")
    public DataResponse queryProcessInstances(HttpServletRequest httpRequest,
                                              @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws WflSecurityException {
        JSONObject param = (JSONObject) requestData.get("parameter");
        CustomHistoricProcessInstanceQueryRequest historicProcessInstanceQueryRequest = param.toJavaObject(CustomHistoricProcessInstanceQueryRequest.class);
        IRequest iRequest = createRequestContext(httpRequest);
        Long allocationId = iRequest.getAttribute("allocationId");

        if (iRequest.getUserId() < 0) {
            throw new WflSecurityException(WflSecurityException.USER_NOT_RELATE_EMP);
        }

        //管理员可以查看全部，否则只能查看自己的
        User user = userService.selectByUserName(iRequest.getUserName());
        List<String> codeList = user.getRoleCode();
        Boolean ADMINFlag = false;
        for (String item : codeList) {
            if ("ADMIN".equalsIgnoreCase(item)) {
                ADMINFlag = true;
            }
        }
        if (!ADMINFlag) {
            historicProcessInstanceQueryRequest.setInvolvedUser(allocationId.toString());
        }

        //状态查询
        String status = (String) param.get("process_instance_status");
        if (status != null && !"".equals(status)) {
            if ("已结束".equals(status)) {
                historicProcessInstanceQueryRequest.setFinished(true);
            } else {
                historicProcessInstanceQueryRequest.setFinished(false);
            }
        }

        if (param.get("involved") != null) {
            if ((Boolean) param.get("involved")) {
                historicProcessInstanceQueryRequest.setStartedBy(allocationId.toString());
            } else {
                historicProcessInstanceQueryRequest.setInvolvedUser(allocationId.toString());
            }
        }

        historicProcessInstanceQueryRequest.setStart((Integer.parseInt(httpRequest.getParameter("pagenum")) - 1) * Integer.parseInt(httpRequest.getParameter("pageSize")));
        historicProcessInstanceQueryRequest.setSize(Integer.parseInt(httpRequest.getParameter("pageSize")));
        historicProcessInstanceQueryRequest.setSort("startTime");
        historicProcessInstanceQueryRequest.setOrder("desc");
        Map<String, String> requestParams = new HashMap<String, String>();

        DataResponse dataResponse = activitiService.queryProcessInstances(iRequest, historicProcessInstanceQueryRequest, requestParams, true);
        dataResponse.getData();
        return dataResponse;
    }
//    @RequestMapping(value = "/query/process-instances/my")
//    public DataResponse queryProcessInstances(@RequestBody CustomHistoricProcessInstanceQueryRequest historicProcessInstanceQueryRequest,
//                                              @RequestParam Map<String, String> requestParams, HttpServletRequest httpRequest) throws WflSecurityException {
//
//        IRequest iRequest = createRequestContext(httpRequest);
//        if (StringUtils.isEmpty(String.valueOf(iRequest.getUserId()))) {
//            throw new WflSecurityException(WflSecurityException.USER_NOT_RELATE_EMP);
//        }
//        if (Boolean.valueOf(requestParams.get("involved"))) {
//            historicProcessInstanceQueryRequest.setInvolvedUser(String.valueOf(iRequest.getUserId()));
//        } else if (Boolean.valueOf(requestParams.get("startedBy"))) {
//            historicProcessInstanceQueryRequest.setStartedBy(String.valueOf(iRequest.getUserId()));
//        } else if (Boolean.valueOf(requestParams.get("carbonCopy"))) {
//            historicProcessInstanceQueryRequest.setCarbonCopyUser(String.valueOf(iRequest.getUserId()));
//        } else {
//            historicProcessInstanceQueryRequest.setInvolvedUser(String.valueOf(iRequest.getUserId()));
//        }
//        return activitiService.queryProcessInstances(iRequest, historicProcessInstanceQueryRequest, requestParams, true);
//    }

    @RequestMapping(value = "/activiti/process_history_carbon.html")
    public ModelAndView carbonCopy(HttpServletRequest httpRequest) {
        ModelAndView mv = new ModelAndView(getViewPath() + "/activiti/process_history");
        mv.addObject("carbonCopy", true);
        return mv;
    }

    @RequestMapping(value = "/activiti/process_history_start.html")
    public ModelAndView startedBy(HttpServletRequest httpRequest) {
        ModelAndView mv = new ModelAndView(getViewPath() + "/activiti/process_history");
        mv.addObject("startedBy", true);
        return mv;
    }


    /**
     * 流程监控查询.
     *
     * @param historicProcessInstanceQueryRequest
     * @param requestParams
     * @param httpRequest
     * @return dataResponse
     * //
     */
    @RequestMapping(value = "/query/process-instances/monitor")
    public DataResponse queryAllProcessInstances(@RequestBody CustomHistoricProcessInstanceQueryRequest historicProcessInstanceQueryRequest,
                                                 @RequestParam Map<String, String> requestParams, HttpServletRequest httpRequest) {

        IRequest iRequest = createRequestContext(httpRequest);
        return activitiService.queryProcessInstances(iRequest, historicProcessInstanceQueryRequest, requestParams, false);
    }

    @RequestMapping(value = "/leaf/query/process-instances/monitor")
    public DataResponse queryAllProcessInstances(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest httpRequest) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        StringBuilder sb = new StringBuilder();
        String documentName =(String) param.get("documentName");
        if (!StringUtils.isEmpty(documentName)){
            for (int i = 0; i <  documentName.length(); i++) {
                char c = documentName.charAt(i);
                if (c >= 'A' && c <= 'Z' ){
                    sb.append(String.valueOf(c).toLowerCase());
                }else {
                    sb.append(c);
                }
            }
            param.put("documentName",sb.toString());
        }
        CustomHistoricProcessInstanceQueryRequest historicProcessInstanceQueryRequest = param.toJavaObject(CustomHistoricProcessInstanceQueryRequest.class);
        //状态查询
        String status = (String) param.get("process_instance_status");
        if (status != null && !"".equals(status)) {
            if ("已结束".equals(status)) {
                historicProcessInstanceQueryRequest.setFinished(true);
            } else if ("挂起中".equals(status)) {
                historicProcessInstanceQueryRequest.setSuspended(true);
            } else {
                historicProcessInstanceQueryRequest.setSuspended(false);
                historicProcessInstanceQueryRequest.setFinished(false);
            }
        }

        String started_by_name = (String) param.get("started_by_name");
        if (started_by_name != null && !"".equalsIgnoreCase(started_by_name)) {
            historicProcessInstanceQueryRequest.setStartedBy(started_by_name);
        }

        //改为allocationId
        Integer startAllocationId = (Integer) param.get("started_by");
        if (startAllocationId != null) {
            historicProcessInstanceQueryRequest.setStartedBy(String.valueOf(startAllocationId));
        }

        historicProcessInstanceQueryRequest.setStart((Integer.parseInt(httpRequest.getParameter("pagenum")) - 1) * Integer.parseInt(httpRequest.getParameter("pageSize")));
        historicProcessInstanceQueryRequest.setSize(Integer.parseInt(httpRequest.getParameter("pageSize")));
        historicProcessInstanceQueryRequest.setSort("startTime");
        historicProcessInstanceQueryRequest.setOrder("desc");
        Map<String, String> requestParams = new HashMap<String, String>();
        IRequest iRequest = createRequestContext(httpRequest);
        return activitiService.queryProcessInstances(iRequest, historicProcessInstanceQueryRequest, requestParams, false);
    }

    @RequestMapping(value = "/activiti/admin/task_detail.html")
    public ModelAndView adminTask(HttpServletRequest httpRequest, String taskId) {
        ModelAndView mv = new ModelAndView(getViewPath() + "/activiti/task_detail");
        mv.addObject("taskId", taskId);
        mv.addObject("isAdmin", true);
        return mv;
    }

    /**
     * 完成,转交...任务
     *
     * @param taskId
     * @param actionRequest
     * @param request
     * @param response
     */
    @RequestMapping(value = "/runtime/tasks/{taskId}", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void executeTaskAction(@PathVariable String taskId, @RequestBody TaskActionRequestExt actionRequest,
                                  HttpServletRequest request, HttpServletResponse response) throws TaskActionException {
        IRequest iRequest = createRequestContext(request);
        activitiService.executeTaskAction(iRequest, taskId, actionRequest, false);
    }

    @RequestMapping(value = "/runtime/admin/tasks/{taskId}", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void executeTaskActionAdmin(@PathVariable String taskId, @RequestBody TaskActionRequestExt actionRequest,
                                       HttpServletRequest request, HttpServletResponse response) throws TaskActionException {
        IRequest iRequest = createRequestContext(request);
        activitiService.executeTaskAction(iRequest, taskId, actionRequest, true);
    }

    @RequestMapping(value = "/runtime/admin/tasks/batch-delegate", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void executeBatchDelegate(@RequestBody List<TaskActionRequestExt> actionRequests,
                                     HttpServletRequest request, HttpServletResponse response) throws TaskActionException {
        IRequest iRequest = createRequestContext(request);
        for (TaskActionRequestExt actionRequest : actionRequests) {
            actionRequest.setComment("");
            activitiService.executeTaskAction(iRequest, actionRequest.getCurrentTaskId(), actionRequest, true);
        }
    }

    /**
     * 新建模型(editor)
     */
    @RequestMapping(value = "/repository/models", method = RequestMethod.POST, produces = "application/json")
    public ResponseData createModel(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray para = (JSONArray) requestData.get("parameter");
        List<ModelRequest> modelRequests = para.toJavaList(ModelRequest.class);
        List<ModelResponse> models = new ArrayList<ModelResponse>();
        if(!CollectionUtils.isEmpty(modelRequests)) {
            if(!"update".equals(((Map)para.get(0)).get("_status"))) {
                ModelRequest modelRequest = modelRequests.get(0);
                Model model = repositoryService.newModel();
                model.setCategory(modelRequest.getCategory());
                model.setDeploymentId(modelRequest.getDeploymentId());
                model.setKey(modelRequest.getKey());
                model.setMetaInfo(modelRequest.getMetaInfo());
                model.setName(modelRequest.getName());
                model.setVersion(modelRequest.getVersion());
                model.setTenantId(modelRequest.getTenantId());

                repositoryService.saveModel(model);
                response.setStatus(HttpStatus.CREATED.value());

                HashMap<String, Object> content = new HashMap<>();
                content.put("resourceId", model.getId());

                HashMap<String, String> properties = new HashMap<>();
                properties.put("process_id", modelRequest.getKey());
                properties.put("name", modelRequest.getName());
                properties.put("process_namespace", modelRequest.getCategory());
                content.put("properties", properties);

                HashMap<String, String> stencilset = new HashMap<>();
                stencilset.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
                content.put("stencilset", stencilset);

                try {
                    repositoryService.addModelEditorSource(model.getId(), objectMapper.writeValueAsBytes(content));
                } catch (JsonProcessingException e) {
                    logger.error(e.getMessage(), e);
                }

                models.add(restResponseFactory.createModelResponse(model));
            }
        }

        return new ResponseData(models);

    }

    @RequestMapping("/repository/model/node")
    public ResponseData getUserTaskFromModelSource(HttpServletRequest request, String modelId) {
        IRequest iRequest = createRequestContext(request);
        List<?> list = activitiService.getUserTaskFromModelSource(iRequest, modelId);
        return new ResponseData(list);
    }

    /**
     * 部署流程
     *
     * @param modelId
     * @throws IOException
     */
    @RequestMapping("/repository/model/{modelId}/deploy")
    @ResponseBody
    public ResponseData modelDeployment(@PathVariable String modelId, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        Model model = null;
        try {
            model = activitiService.deployModel(modelId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            ResponseData rd = new ResponseData(false);
            rd.setMessage(e.getMessage());
            return rd;
        }
        return new ResponseData(Arrays.asList(model));
    }

    @RequestMapping(value = "/repository/model/{modelId}/export", produces = "text/xml")
    public ResponseEntity<byte[]> modelExport(@PathVariable String modelId,
                                              @RequestParam(defaultValue = "") String type) throws IOException {
        Model model = repositoryService.getModel(modelId);
        byte[] modelData = repositoryService.getModelEditorSource(modelId);
        JsonNode jsonNode = objectMapper.readTree(modelData);
        BpmnModel bpmnModel = new BpmnJsonConverter().convertToBpmnModel(jsonNode);
        byte[] xmlBytes;
        try {
            xmlBytes = new BpmnXMLConverter().convertToXML(bpmnModel, "UTF-8");
        } catch (Exception e) {
            throw new ActivitiIllegalArgumentException("Error exporting diagram", e);
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        String id = model.getId();
        if (bpmnModel.getMainProcess() != null) {
            id = bpmnModel.getMainProcess().getId();
        }
        if ("bpmn20".equalsIgnoreCase(type)) {
            responseHeaders.set("Content-Disposition", "attachment;filename=" + id + ".bpmn20.xml");
            responseHeaders.set("Content-Type", "application/octet-stream");
        } else {
            responseHeaders.set("Content-Type", "text/xml;charset=utf8");
        }
        try {
            return new ResponseEntity<>(xmlBytes, responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            throw new ActivitiIllegalArgumentException("Error exporting diagram", e);
        }
    }

    @RequestMapping("/repository/deploy/{deployId}/export")
    public ResponseEntity<byte[]> deployExport(@PathVariable String deployId) throws IOException {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(deployId);
        byte[] xmlBytes = new BpmnXMLConverter().convertToXML(bpmnModel, "UTF-8");
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "text/xml;charset=utf8");
        try {
            return new ResponseEntity<>(xmlBytes, responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            throw new ActivitiIllegalArgumentException("Error exporting diagram", e);
        }
    }

    @RequestMapping(value = "/repository/model/import", produces = "text/html;charset=UTF-8")
    public String importModel(HttpServletRequest request, @RequestParam String callback) throws FileUploadException, IOException {
        ResponseData responseData = new ResponseData();
        if (!ServletFileUpload.isMultipartContent(request)) {
            responseData.setSuccess(false);
            responseData.setMessage("NOT a Multipart Request");
        }
        InputStream fileInputStream = null;
        String name = null;
        String key = null;
        String category = null;
        if (request instanceof MultipartHttpServletRequest) {
            MultipartFile file = ((MultipartHttpServletRequest) request).getFile("file");
            if (file != null) {
                fileInputStream = file.getInputStream();
            }
            name = request.getParameter("name");
            key = request.getParameter("key");
            category = request.getParameter("category");
        } else {
            ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
            List<FileItem> items = upload.parseRequest(request);
            FileItem item = null;
            Map<String, String> parameters = new HashMap<>();
            for (FileItem fi : items) {
                if (!fi.isFormField()) {
                    item = fi;
                } else {
                    parameters.put(fi.getFieldName(), fi.getString("UTF-8"));
                }
            }
            if (item != null && StringUtils.isNotEmpty(item.getName())) {
                fileInputStream = item.getInputStream();
                name = parameters.get("name");
                key = parameters.get("key");
                category = parameters.get("category");
            }
        }
        if (fileInputStream == null) {
            responseData.setSuccess(false);
            responseData.setMessage("File Content is Null");
        }
        //解决中文乱码问题
        if (StringUtils.isNotEmpty(name)) {
            name = new String(name.getBytes(StandardCharsets.ISO_8859_1), "UTF-8");
        }
        if (StringUtils.isNotEmpty(key)) {
            key = new String(key.getBytes(StandardCharsets.ISO_8859_1), "UTF-8");
        }
        if (StringUtils.isNotEmpty(category)) {
            category = new String(category.getBytes(StandardCharsets.ISO_8859_1), "UTF-8");
        }

        try (InputStream inputStream = fileInputStream) {
            InputStreamSource source = new InputStreamSource(fileInputStream);
            BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(source, false, false, "UTF-8");
            bpmnModel.getMainProcess().setId(StringUtils.defaultIfEmpty(key, bpmnModel.getMainProcess().getId()));
            bpmnModel.getMainProcess().setName(StringUtils.defaultIfEmpty(name, bpmnModel.getMainProcess().getName()));

            Model model = repositoryService.newModel();
            model.setCategory(StringUtils.defaultIfEmpty(category, "default"));
            model.setDeploymentId(null);
            model.setKey(bpmnModel.getMainProcess().getId());
            model.setName(bpmnModel.getMainProcess().getName());

            String metaInfo = model.getMetaInfo();
            if (StringUtils.isEmpty(metaInfo)) {
                HashMap<String, String> map = new HashMap<>();
                map.put("name", model.getName());
                map.put("version", String.valueOf(model.getVersion()));
                metaInfo = objectMapper.writeValueAsString(map);
            }
            model.setMetaInfo(metaInfo);

            repositoryService.saveModel(model);

            ObjectNode content = new BpmnJsonConverter().convertToJson(bpmnModel);
            content = approvalRule.processCustomProperties(content);

            repositoryService.addModelEditorSource(model.getId(), objectMapper.writeValueAsBytes(content));
            restResponseFactory.createModelResponse(model);
            responseData.setSuccess(true);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            responseData.setSuccess(false);
            responseData.setMessage("请上传符合 BPMN 规范的文件");
        }
        return "<script>" + callback + "(" + JSONObject.toJSONString(responseData) + ")</script>";
    }

    @RequestMapping("/runtime/tasks/{taskId}/details")
    @ResponseBody
    public ResponseData taskDetails(HttpServletRequest request, @PathVariable String taskId)
            throws WflSecurityException {

        IRequest iRequest = createRequestContext(request);
        List<TaskResponseExt> resultList = new ArrayList<TaskResponseExt>();
        resultList.add(activitiService.getTaskDetails(iRequest, taskId));
        return new ResponseData(resultList);
    }

    @RequestMapping("/runtime/admin/tasks/{taskId}/details")
    @ResponseBody
    public TaskResponseExt taskDetailsAdmin(HttpServletRequest request, @PathVariable String taskId)
            throws WflSecurityException {

        IRequest iRequest = createRequestContext(request);
        return activitiService.getTaskDetails(iRequest, taskId, true);
    }

    @RequestMapping(value = "/runtime/process-instances/{processInstanceId}/forecast")
    public List<ProcessInstanceForecast> getProcessInstanceForecast(HttpServletRequest request, @PathVariable String processInstanceId) {
        IRequest iRequest = createRequestContext(request);
        return activitiService.processInstanceForecast(iRequest, processInstanceId);

    }

    @RequestMapping(value = "/runtime/process-instances/{processInstanceId}/diagram", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getProcessInstanceDiagram(@PathVariable String processInstanceId,
                                                            HttpServletResponse response) {

        boolean isActivity = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult() == null ? false : true;

        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).list().iterator().next();

        ProcessDefinition pde = repositoryService.getProcessDefinition(processInstance.getProcessDefinitionId());

        if (pde != null && pde.hasGraphicalNotation()) {
            BpmnModel bpmnModel = repositoryService.getBpmnModel(pde.getId());
            ProcessDiagramGenerator diagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();

            List<HistoricActivityInstance> finished = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(processInstanceId).finished().list();
            List<String> ids = new ArrayList<>(finished.stream()
                    .map(e -> e.getActivityId() + IActivitiConstants.HISTORY_SUFFIX).collect(Collectors.toList()));
            if (isActivity) {
                ids.addAll(runtimeService.getActiveActivityIds(processInstance.getId()));
            }

            List<String> flows = new ArrayList<>();
            bpmnModel.getMainProcess().getFlowElements();
            Collection<SequenceFlow> flowElement = bpmnModel.getMainProcess().findFlowElementsOfType(SequenceFlow.class);

            Map<String, Boolean> idMap = new HashedMap();

            for (String id : ids) {
                if (id.endsWith(IActivitiConstants.HISTORY_SUFFIX)) {
                    id = id.substring(0, id.indexOf(IActivitiConstants.HISTORY_SUFFIX));
                }

                if (id.endsWith(IActivitiConstants.HISTORY_SUFFIX)) {
                    id = id.substring(0, id.indexOf(IActivitiConstants.HISTORY_SUFFIX));
                }

                idMap.put(id, true);
            }

            String startId;
            String endId;

            Boolean isStartId;
            Boolean isEndId;

            for (SequenceFlow flowElement1 : flowElement) {
                startId = flowElement1.getSourceRef();
                endId = flowElement1.getTargetRef();

                isStartId = idMap.get(startId);
                isEndId = idMap.get(endId);

                if (BooleanUtils.isTrue(isStartId) && BooleanUtils.isTrue(isEndId)) {
                    flows.add(flowElement1.getId());
                }
            }

            InputStream resource = diagramGenerator.generateDiagram(bpmnModel, "svg", ids,
                    flows, processEngineConfiguration.getActivityFontName(),
                    processEngineConfiguration.getLabelFontName(), processEngineConfiguration.getAnnotationFontName(),
                    processEngineConfiguration.getClassLoader(), 1.0);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Content-Type", "image/svg+xml");
            try {
                return new ResponseEntity<>(IOUtils.toByteArray(resource), responseHeaders, HttpStatus.OK);
            } catch (Exception e) {
                throw new ActivitiIllegalArgumentException("Error exporting diagram", e);
            }

        } else {
            throw new ActivitiIllegalArgumentException(
                    "Process instance with id '" + processInstance.getId() + "' has no graphical notation defined.");
        }
    }

    @RequestMapping("/history/form/details/{procId}")
    @ResponseBody
    public ResponseData getFromVar(HttpServletRequest request, @PathVariable String procId) {
        List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery().processInstanceId(procId).list();
//        for (HistoricVariableInstance variable : list) {
//            System.out.println("variable: " + variable.getVariableName() + " = " + variable.getValue());
//        }

        return new ResponseData(list);
    }

    @RequestMapping("/instance/{processInstanceId}")
    @ResponseBody
    public HistoricProcessInstanceResponseExt instanceDetail(HttpServletRequest request, @PathVariable String processInstanceId)
            throws WflSecurityException {
        IRequest iRequest = createRequestContext(request);
        return activitiService.getInstanceDetail(iRequest, processInstanceId);
    }

    @RequestMapping("/leaf/instance/{processInstanceId}")
    @ResponseBody
    public ResponseData instanceDetailLeaf(HttpServletRequest request, @PathVariable String processInstanceId)
            throws WflSecurityException {
        IRequest iRequest = createRequestContext(request);
        List<HistoricProcessInstanceResponseExt> resultList = new ArrayList<HistoricProcessInstanceResponseExt>();
        resultList.add(activitiService.getInstanceDetail(iRequest, processInstanceId));
        return new ResponseData(resultList);
    }

    @RequestMapping("/instance/carbon-copy-read")
    @ResponseBody
    public void carbonCopyRead(HttpServletRequest request, @RequestBody HistoricProcessInstanceResponseExt processInstanceResponseExt)
            throws WflSecurityException {
        IRequest iRequest = createRequestContext(request);
        if ("N".equalsIgnoreCase(processInstanceResponseExt.getReadFlag())) {
            activitiService.processCarbonCopyRead(processInstanceResponseExt.getId(), String.valueOf(iRequest.getUserId()));
        }
    }


    @RequestMapping("/runtime/prc/suspend/{procId}")
    public void suspendProc(HttpServletRequest request, @PathVariable String procId) {
        runtimeService.suspendProcessInstanceById(procId);
    }

    @RequestMapping("/runtime/prc/active/{procId}")
    public void activeProc(HttpServletRequest request, @PathVariable String procId) {
        runtimeService.activateProcessInstanceById(procId);
    }

    @RequestMapping(value = "/runtime/prc/end/{procId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData endProc(HttpServletRequest request, @PathVariable String procId) throws HlsCusException {
        /* runtimeService.deleteProcessInstance(procId, IActivitiConstants.ACT_STOP);*/
        IRequest iRequest = createRequestContext(request);
        activitiService.rejectTo(iRequest, procId);
        return new ResponseData(true);
    }

    @RequestMapping(value = "/runtime/prc/pass/{procId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData passProc(HttpServletRequest request, @PathVariable String procId) throws HlsCusException {
        IRequest iRequest = createRequestContext(request);
        activitiService.passTo(iRequest, procId);
        return new ResponseData(true);
    }

    @RequestMapping(value = "/runtime/prc/jump/{procId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData jumpTo(HttpServletRequest request, @PathVariable String procId, String jumpTarget, String jumpTargetName) throws HlsCusException {
        IRequest iRequest = createRequestContext(request);
        try {
            activitiService.jumpActivitiTo(iRequest, procId, jumpTarget, jumpTargetName);
        } catch (Exception e) {
            return new ResponseData(false, e.getMessage());
        }
        return new ResponseData(true);
    }

    @RequestMapping(value = "/runtime/prc/back/{procId}", method = RequestMethod.POST)
    @ResponseBody
    public Map backProc(HttpServletRequest request, @PathVariable String procId) throws Exception {
        IRequest iRequest = createRequestContext(request);
        Long allocationId = iRequest.getAttribute("allocationId");
        Map result = new HashMap<>();
        result.put("success", true);
        result.put("message", "撤回成功");
        if (activitiService.isStartRecall(procId, allocationId.toString())) {
            activiCancelService.cancelWorkFlow(procId, iRequest);
            runtimeService.deleteProcessInstance(procId, IActivitiConstants.ACT_RETRACT);
        } else if (activitiService.isTaskRecall(procId, allocationId.toString())) {
            activiCancelService.cancelWorkFlow(procId, iRequest);
            activitiService.taskRecall(iRequest, procId, allocationId.toString());
        } else {
            result.put("success", false);
            result.put("message", "已发生过审批的流程不允许发起人撤回");
        }
        return result;
    }

    @RequestMapping(value = "/runtime/exception/search", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData searchException(HttpServletRequest request, ActiviException exception, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(activitiService.queryException(iRequest, exception, page, pagesize));
    }

    @RequestMapping(value = "/leaf/runtime/exception/search", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData searchExceptionLeaf(HttpServletRequest request,
                                            @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                            @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest iRequest = createRequestContext(request);
        //RequestHelper.setCurrentRequest(iRequest);
        JSONObject param = (JSONObject) requestData.get("parameter");
        ActiviException exception = param.toJavaObject(ActiviException.class);
        return new ResponseData(activitiService.queryException(iRequest, exception, pagenum, pagesize));
    }


    @RequestMapping(value = "/runtime/execute/{procId}", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public ResponseData executeTaskActionByAdmin(@PathVariable String procId, @RequestBody TaskActionRequestExt actionRequest,
                                                 HttpServletRequest request, HttpServletResponse response) throws TaskActionException {
        actionRequest.setComment("");
        IRequest iRequest = createRequestContext(request);
        activitiService.executeTaskByAdmin(iRequest, procId, actionRequest);
        return new ResponseData();
    }

    //转交。。加签,以leaf的方式提交
    @RequestMapping(value = "/leaf/runtime/execute/{procId}", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public ResponseData executeTaskActionByAdmin(@PathVariable String procId, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                                 HttpServletRequest request, HttpServletResponse response) throws TaskActionException {
        JSONArray param = (JSONArray) requestData.get("parameter");
        JSONObject beans = (JSONObject) param.get(0);
        TaskActionRequestExt actionRequest = new TaskActionRequestExt();
        Long allocationId = beans.getLong("assign");
        actionRequest.setAssignee(String.valueOf(allocationId));
        actionRequest.setCurrentTaskId(beans.getString("taskId"));
        actionRequest.setAction(beans.getString("action"));
        if(beans.getString("comment") != null){
            actionRequest.setComment(beans.getString("comment"));
        }else{
            actionRequest.setComment("");
        }

        IRequest iRequest = createRequestContext(request);
        activitiService.executeTaskByAdmin(iRequest, procId, actionRequest);
        return new ResponseData();
    }

    //转交。。加签,以leaf的方式提交
    @RequestMapping(value = "/leaf/runtime/execute/general/{procId}", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public ResponseData executeTaskActionGeneral(@PathVariable String procId, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                                 HttpServletRequest request, HttpServletResponse response) throws TaskActionException {
        JSONArray param = (JSONArray) requestData.get("parameter");
        JSONObject beans = (JSONObject) param.get(0);
        TaskActionRequestExt actionRequest = new TaskActionRequestExt();
        Long allocationId = beans.getLong("assign");
        actionRequest.setAssignee(String.valueOf(allocationId));
        actionRequest.setCurrentTaskId(beans.getString("taskId"));
        actionRequest.setAction(beans.getString("action"));
        if(beans.getString("comment") != null){
            actionRequest.setComment(beans.getString("comment"));
        }else{
            actionRequest.setComment("");
        }

        IRequest iRequest = createRequestContext(request);
        activitiService.executeTaskByAdmin(iRequest, procId, actionRequest);
        return new ResponseData();
    }

    @RequestMapping(value = "/repository/deployments/{deploymentId}", method = RequestMethod.DELETE, produces = "application/json")
    public void deleteDeployment(@PathVariable String deploymentId, @RequestParam(value = "cascade", required = false, defaultValue = "false") Boolean cascade, HttpServletResponse response) {

        activitiService.deleteDeployment(deploymentId, cascade);
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

    @RequestMapping(value = "/leaf/repository/deployments/{deploymentId}", method = RequestMethod.DELETE, produces = "application/json")
    public void deleteDeploymentLeaf(@PathVariable String deploymentId, @RequestParam(value = "cascade", required = false, defaultValue = "false") Boolean cascade, HttpServletResponse response) {

        activitiService.deleteDeployment(deploymentId, cascade);
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

    /**
     * 根据taskId查找formUrl,
     * 当流程结束时默认拿最后一个节点的表单
     *
     * @param taskId
     * @return
     */
    @GetMapping("/formKey/{processInstanceId}/{taskId}")
    public ResponseData getFormKeyByTaskId(@PathVariable String processInstanceId,
                                           @PathVariable String taskId) {
        String businessKey = null;
        String formKey = null;
        String url = null;
        //流程结束的情况
        if ("-1".equals(taskId)) {
            businessKey = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult().getBusinessKey();

            List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).orderByHistoricTaskInstanceStartTime().desc().list();
            HistoricTaskInstance taskInstance = historicTaskInstances.get(0);
            formKey = taskInstance.getFormKey();

        } else {
            HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            businessKey = processInstance.getBusinessKey();
            if (taskService.createTaskQuery().taskId(taskId).singleResult() != null) {
                formKey = taskService.createTaskQuery().taskId(taskId).singleResult().getFormKey();
            } else {
                formKey = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult().getFormKey();
            }

        }
        url = formKey + "?businessKey=" + businessKey + "&processInstanceId=" + processInstanceId;
        return new ResponseData(true, url);
    }


    /**
     * 验证当前的登录用户是否是正确的审批人员
     *
     * @param request
     * @param taskId
     * @return
     */
    @GetMapping("/check/audit_user/{taskId}")
    public ResponseData checkCurrentUserIsAuditUser(HttpServletRequest request, @PathVariable("taskId") String taskId) {

        if (taskId != null && !taskId.equalsIgnoreCase("")) {
            boolean flag = activitiService.queryTaskByTaskId(request, taskId);
            if (flag) {
                return new ResponseData(true);
            } else {
                return new ResponseData(false, "请用正确的审批人审批");
            }
        } else {
            return new ResponseData(false, "taskId为空");
        }
    }


    @RequestMapping(value = "/queryWflHis")
    @ResponseBody
    public ResponseData queryWflHis(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        String businessKey=null;
        String workFlowType=null;
        if(param.get("businessKey")!=null){
            businessKey = param.get("businessKey").toString();
        }
        if(param.get("workFlowType")!=null){
            workFlowType=param.get("workFlowType").toString();
        }
        return activitiService.queryWflHis(iRequest,businessKey, workFlowType);
    }

    @RequestMapping(value = "/leaf/history/query")
    @ResponseBody
    public ResponseData queryWflHistoryList(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        String project_id=null;
        if(param.get("project_id")!=null){
            project_id = param.get("project_id").toString();
        }
        Map<String, Object> taskInfo=new HashMap<>();
        taskInfo.put("project_id",project_id);

        if(param.get("involved")!=null){
            taskInfo.put("involved",param.get("involved"));
        }
        if(param.get("started_after")!=null){
            taskInfo.put("started_after",param.get("started_after"));
        }
        if(param.get("started_before")!=null){
            taskInfo.put("started_before",param.get("started_before"));
        }
        if(param.get("process_instance_status")!=null){
            taskInfo.put("process_instance_status",param.get("process_instance_status"));
        }
        if(param.get("finished_after")!=null){
            taskInfo.put("finished_after",param.get("finished_after"));
        }
        if(param.get("finished_before")!=null){
            taskInfo.put("finished_before",param.get("finished_before"));
        }
        return activitiService.queryWflHistoryList(taskInfo);
    }
}
