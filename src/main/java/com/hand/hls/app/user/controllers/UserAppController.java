package com.hand.hls.app.user.controllers;

import com.alibaba.fastjson.JSONObject;
import com.codahale.metrics.annotation.Timed;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.app.user.service.IUserAppService;
import com.hand.hls.app.utils.generalUtils.AppConstantUtils;
import com.hand.hls.app.utils.generalUtils.AppJsonParseUtil;
import com.hand.hls.app.utils.generalUtils.AppResponseData;
import com.hand.hls.fnd.dto.CockpitImport;
import com.hand.hls.fnd.mapper.CockpitImportMapper;
import com.hand.hls.web.logs.dto.HlsWsRequests;
import com.hand.hls.web.logs.service.IHlsWsRequestsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author liao
 * 这里 不实用 / 配置 ，只能通过 /r/api 的路径访问，不然会跟业务系统动态拼接出来的路径冲突
 */
@Controller
@RequestMapping(value = {"/r/api"})
public class UserAppController extends BaseController {


    @Autowired
    private IUserAppService userAppService;
    @Autowired
    private IHlsWsRequestsService hlsWsRequestsService;
    @Autowired
    private CockpitImportMapper mapper;

    /***
     *  用户名密码验证woxiansh
     * */
    @ResponseBody
    @Timed
    @RequestMapping(value = "/user/info/check")
    public ResponseData checkLogin(HttpServletRequest request,
                                   @RequestBody JSONObject jsonObject) throws Exception {
        IRequest iRequest = createRequestContext(request);


        // 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (jsonObject != null) {
            hlsWsRequests.setRequestJson(jsonObject.toString());
        }
        //
        hlsWsRequests.setFunctionName("用户名密码验证");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        ResponseData result = userAppService.checkLogin(iRequest, jsonObject);

        //更新日志表状态
        hlsWsRequestsService.updateResult(result, hlsWsRequests, iRequest, AppConstantUtils.Interface.DATA_JSON);
        return result;

    }

    /***
     * 根据用户id 查询相关信息
     * */
    @RequestMapping(value = "/user/info/queryById")
    public ResponseData queryUserById(HttpServletRequest request,
                                      @RequestBody JSONObject jsonObject) throws Exception {
        IRequest iRequest = createRequestContext(request);


        // 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (jsonObject != null) {
            hlsWsRequests.setRequestJson(jsonObject.toString());
        }
        //
        hlsWsRequests.setFunctionName("根据用户id 查询相关信息");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        ResponseData result = userAppService.queryUserById(iRequest, jsonObject);

        //更新日志表状态
        hlsWsRequestsService.updateResult(result, hlsWsRequests, iRequest, AppConstantUtils.Interface.DATA_JSON);
        return result;

    }

    /***
     * 根据用户name 查询相关信息
     * */
    @RequestMapping(value = "/user/info/queryByName")
    public ResponseData queryUserByName(HttpServletRequest request,
                                        @RequestBody JSONObject jsonObject) throws Exception {
        IRequest iRequest = createRequestContext(request);


        // 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (jsonObject != null) {
            hlsWsRequests.setRequestJson(jsonObject.toString());
        }
        //
        hlsWsRequests.setFunctionName("根据用户名称 查询相关信息");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        ResponseData result = userAppService.queryUserByName(iRequest, jsonObject);

        //更新日志表状态
        hlsWsRequestsService.updateResult(result, hlsWsRequests, iRequest, AppConstantUtils.Interface.DATA_JSON);
        return result;

    }

    /**
     * 根据用户id 查询 allocationId相关
     */
    @RequestMapping(value = "/allocation/edit/query")
    public ResponseData allocationQuery(HttpServletRequest request,
                                        @RequestBody JSONObject jsonObject) throws Exception {
        IRequest iRequest = createRequestContext(request);


        // 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (jsonObject != null) {
            hlsWsRequests.setRequestJson(jsonObject.toString());
        }
        //
        hlsWsRequests.setFunctionName("根据用户id 查询 allocationId相关");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        ResponseData result = userAppService.allocationQueryByUserId(iRequest, jsonObject);

        //更新日志表状态
        hlsWsRequestsService.updateResult(result, hlsWsRequests, iRequest, AppConstantUtils.Interface.DATA_JSON);
        return result;

    }

    /**
     * 待办事项,个人
     * 不区分角色 根据用户id查询
     * 区分的话 就使用allocationId 查询
     * 如果一起传过来就是区分角色
     */
    @RequestMapping(value = "/user/tasks/query")
    public AppResponseData queryTask(HttpServletRequest request,
                                     @RequestBody JSONObject jsonObject) throws Exception {
        IRequest iRequest = createRequestContext(request);


        // 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (jsonObject != null) {
            hlsWsRequests.setRequestJson(jsonObject.toString());
        }
        //
        hlsWsRequests.setFunctionName(" 待办事项个人");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        ResponseData result = userAppService.queryTask(iRequest, jsonObject);
        AppResponseData appResponseData = AppJsonParseUtil.toAppResponseData(result);
        appResponseData.setRows(appResponseData.getRows().replace("null", ""));
        //更新日志表状态
        hlsWsRequestsService.updateResult(result, hlsWsRequests, iRequest, AppConstantUtils.Interface.DATA_JSON);

        return appResponseData;

    }

    /**
     * 审批事项详情
     * processInstanceId
     * workflowType
     */
    @RequestMapping(value = "/activiti/workflow/detail")
    public AppResponseData queryWorkflowDetail(HttpServletRequest request,
                                               @RequestBody JSONObject jsonObject) throws Exception {
        IRequest iRequest = createRequestContext(request);


        // 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (jsonObject != null) {
            hlsWsRequests.setRequestJson(jsonObject.toString());
        }
        //
        hlsWsRequests.setFunctionName(" 审批事项详情");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        ResponseData result = userAppService.queryWorkflowDetail(iRequest, jsonObject);
        AppResponseData appResponseData = AppJsonParseUtil.toAppResponseData(result);
        if (appResponseData.getRows() != null) {
            appResponseData.setRows(appResponseData.getRows().replace("null", ""));
        }
        //更新日志表状态
        hlsWsRequestsService.updateResult(result, hlsWsRequests, iRequest, AppConstantUtils.Interface.DATA_JSON);

        return appResponseData;

    }

    /**
     * 历史流程
     */
    @RequestMapping(value = "/user/process-instances/my/query")
    public AppResponseData queryProcessInstances(HttpServletRequest request,
                                                 @RequestBody JSONObject jsonObject) throws Exception {
        IRequest iRequest = createRequestContext(request);


        // 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (jsonObject != null) {
            hlsWsRequests.setRequestJson(jsonObject.toString());
        }
        //
        hlsWsRequests.setFunctionName("历史流程");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        ResponseData result = userAppService.queryProcessInstances(iRequest, jsonObject);

        AppResponseData appResponseData = AppJsonParseUtil.toAppResponseData(result);
        if (appResponseData.getRows() != null) {
            appResponseData.setRows(appResponseData.getRows().replace("null", ""));
        }


        //更新日志表状态
        hlsWsRequestsService.updateResult(result, hlsWsRequests, iRequest, AppConstantUtils.Interface.DATA_JSON);
        return appResponseData;

    }

    /**
     * app 待办详情
     */
    @RequestMapping(value = "/runtime/tasks/details")
    public AppResponseData taskDetails(HttpServletRequest request,
                                       @RequestBody JSONObject jsonObject) throws Exception {
        IRequest iRequest = createRequestContext(request);


        // 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (jsonObject != null) {
            hlsWsRequests.setRequestJson(jsonObject.toString());
        }
        //
        hlsWsRequests.setFunctionName(" app 待办详情");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        AppResponseData result = AppJsonParseUtil.toAppResponseData(userAppService.taskDetails(iRequest, jsonObject));
        if (result.getRows() != null) {
            result.setRows(result.getRows().replace("null", ""));
        }
        //更新日志表状态
        hlsWsRequestsService.updateResult(result, hlsWsRequests, iRequest, AppConstantUtils.Interface.DATA_JSON);
        return result;

    }

    /**
     * app  审批 同意 拒绝
     */
    @RequestMapping(value = "/runtime/tasks/executeTaskAction")
    public AppResponseData executeTaskAction(HttpServletRequest request,
                                             @RequestBody JSONObject jsonObject) throws Exception {
        IRequest iRequest = createRequestContext(request);


        // 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (jsonObject != null) {
            hlsWsRequests.setRequestJson(jsonObject.toString());
        }
        //
        hlsWsRequests.setFunctionName(" app  审批 同意、拒绝等操作");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        AppResponseData result = AppJsonParseUtil.toAppResponseData(userAppService.executeTaskAction(iRequest, jsonObject));
        if (result.getRows() != null) {
            result.setRows(result.getRows().replace("null", ""));
        }

        //更新日志表状态
        hlsWsRequestsService.updateResult(result, hlsWsRequests, iRequest, AppConstantUtils.Interface.DATA_JSON);
        return result;

    }

    /**
     * app   转交 、价签操作
     */
    @RequestMapping(value = "/runtime/tasks/executeTaskForward")
    public ResponseData executeTaskForward(HttpServletRequest request,
                                           @RequestBody JSONObject jsonObject) throws Exception {
        IRequest iRequest = createRequestContext(request);


        // 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (jsonObject != null) {
            hlsWsRequests.setRequestJson(jsonObject.toString());
        }
        //
        hlsWsRequests.setFunctionName("app   转交 、价签操作");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        ResponseData result = userAppService.executeTaskForward(iRequest, jsonObject);

        //更新日志表状态
        hlsWsRequestsService.updateResult(result, hlsWsRequests, iRequest, AppConstantUtils.Interface.DATA_JSON);
        return result;

    }

    /**
     * app   查看 工作流 审批流程
     */
    @RequestMapping(value = "/definition/userTasks")
    public ResponseData userTask(HttpServletRequest request,
                                 @RequestBody JSONObject jsonObject) throws Exception {
        IRequest iRequest = createRequestContext(request);


        // 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (jsonObject != null) {
            hlsWsRequests.setRequestJson(jsonObject.toString());
        }
        //
        hlsWsRequests.setFunctionName("app   查看 工作流 审批流程");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        ResponseData result = userAppService.userTaskQusery(iRequest, jsonObject);

        //更新日志表状态
        hlsWsRequestsService.updateResult(result, hlsWsRequests, iRequest, AppConstantUtils.Interface.DATA_JSON);
        return result;

    }

    /**
     * app 工作流节点跳转
     */
    @RequestMapping(value = "/runtime/prc/jump")
    public ResponseData prcJump(HttpServletRequest request,
                                @RequestBody JSONObject jsonObject) throws Exception {
        IRequest iRequest = createRequestContext(request);


        // 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (jsonObject != null) {
            hlsWsRequests.setRequestJson(jsonObject.toString());
        }
        //
        hlsWsRequests.setFunctionName("app   工作流节点跳转");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        ResponseData result = userAppService.prcJump(iRequest, jsonObject);

        //更新日志表状态
        hlsWsRequestsService.updateResult(result, hlsWsRequests, iRequest, AppConstantUtils.Interface.DATA_JSON);
        return result;

    }

    /**
     * app 转交时 用户查询
     */
    @RequestMapping(value = "/leaf/runtime/query/employees")
    public ResponseData queryEmployees(HttpServletRequest request,
                                       @RequestBody JSONObject jsonObject) throws Exception {
        IRequest iRequest = createRequestContext(request);


        // 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (jsonObject != null) {
            hlsWsRequests.setRequestJson(jsonObject.toString());
        }
        //
        hlsWsRequests.setFunctionName("app 转交时 用户查询");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        ResponseData result = userAppService.queryEmployees(iRequest, jsonObject);

        //更新日志表状态
        hlsWsRequestsService.updateResult(result, hlsWsRequests, iRequest, AppConstantUtils.Interface.DATA_JSON);
        return result;

    }

    /**
     * 查询app 版本信息
     */
    @RequestMapping(value = "/leaf/app/version")
    public ResponseData queryAppVersion(HttpServletRequest request,
                                        @RequestBody JSONObject jsonObject) throws Exception {
        IRequest iRequest = createRequestContext(request);


        // 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (jsonObject != null) {
            hlsWsRequests.setRequestJson(jsonObject.toString());
        }
        //
        hlsWsRequests.setFunctionName("查询app 版本信息");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        ResponseData result = userAppService.queryAppVersion(iRequest, jsonObject);

        //更新日志表状态
        hlsWsRequestsService.updateResult(result, hlsWsRequests, iRequest, AppConstantUtils.Interface.DATA_JSON);
        return result;

    }

    /**
     * 修改app  版本
     */
    @RequestMapping(value = "/leaf/app/version/update")
    public ResponseData updateAppVersion(HttpServletRequest request,
                                         @RequestBody JSONObject jsonObject) throws Exception {
        IRequest iRequest = createRequestContext(request);


        // 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (jsonObject != null) {
            hlsWsRequests.setRequestJson(jsonObject.toString());
        }
        //
        hlsWsRequests.setFunctionName("修改app  版本");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        ResponseData result = userAppService.updateAppVersion(iRequest, jsonObject);

        //更新日志表状态
        hlsWsRequestsService.updateResult(result, hlsWsRequests, iRequest, AppConstantUtils.Interface.DATA_JSON);
        return result;

    }

    /**
     * app 个人所有消息通知
     */
    @RequestMapping(value = "/app/leaf/user/all/notice")
    public AppResponseData queryUserAllNotice(HttpServletRequest request,
                                              @RequestBody JSONObject jsonObject) throws Exception {
        IRequest iRequest = createRequestContext(request);


        // 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (jsonObject != null) {
            hlsWsRequests.setRequestJson(jsonObject.toString());
        }
        //
        hlsWsRequests.setFunctionName("app 消息通知");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        AppResponseData result = AppJsonParseUtil.toAppResponseData(userAppService.queryUserAllNotice(iRequest, jsonObject));
        if (result.getRows() != null) {
            result.setRows(result.getRows().replace("null", ""));
        }

        //更新日志表状态
        hlsWsRequestsService.updateResult(result, hlsWsRequests, iRequest, AppConstantUtils.Interface.DATA_JSON);
        return result;

    }

    /**
     * app 个人所有未读消息
     */
    @RequestMapping(value = "/app/leaf/user/unRead/notice")
    public AppResponseData queryUnReadNotice(HttpServletRequest request,
                                             @RequestBody JSONObject jsonObject) throws Exception {
        IRequest iRequest = createRequestContext(request);


        // 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (jsonObject != null) {
            hlsWsRequests.setRequestJson(jsonObject.toString());
        }
        //
        hlsWsRequests.setFunctionName("app 个人所有未读消息");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        AppResponseData result = AppJsonParseUtil.toAppResponseData(userAppService.queryUnReadNotice(iRequest, jsonObject));
        if (result.getRows() != null) {
            result.setRows(result.getRows().replace("null", ""));
        }

        //更新日志表状态
        hlsWsRequestsService.updateResult(result, hlsWsRequests, iRequest, AppConstantUtils.Interface.DATA_JSON);
        return result;

    }

    /**
     * app 消息已读
     */
    @RequestMapping(value = "/app/leaf/user/notice/setRead")
    public AppResponseData setRead(HttpServletRequest request,
                                   @RequestBody JSONObject jsonObject) throws Exception {
        IRequest iRequest = createRequestContext(request);
        if (jsonObject.get("userId") != null && !"".equals(jsonObject.get("userId"))) {
            iRequest.setUserId(jsonObject.getLong("userId"));
        }
        if (jsonObject.get("allocationId") != null && !"".equals(jsonObject.get("allocationId"))) {
            iRequest.setAttribute("allocationId", jsonObject.getLong("allocationId"));
        }


        // 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (jsonObject != null) {
            hlsWsRequests.setRequestJson(jsonObject.toString());
        }
        //
        hlsWsRequests.setFunctionName("app 消息已读");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        AppResponseData result = AppJsonParseUtil.toAppResponseData(userAppService.setRead(iRequest, jsonObject));
        if (result.getRows() != null) {
            result.setRows(result.getRows().replace("null", ""));
        }

        //更新日志表状态
        hlsWsRequestsService.updateResult(result, hlsWsRequests, iRequest, AppConstantUtils.Interface.DATA_JSON);
        return result;

    }

    /**
     * app 消息删除
     */
    @RequestMapping(value = "/app/leaf/user/notice/delete")
    public AppResponseData deleteNotice(HttpServletRequest request,
                                        @RequestBody JSONObject jsonObject) throws Exception {
        IRequest iRequest = createRequestContext(request);


        // 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (jsonObject != null) {
            hlsWsRequests.setRequestJson(jsonObject.toString());
        }
        //
        hlsWsRequests.setFunctionName("app 消息删除");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        AppResponseData result = AppJsonParseUtil.toAppResponseData(userAppService.deleteNotice(iRequest, jsonObject));
        if (result.getRows() != null) {
            result.setRows(result.getRows().replace("null", ""));
        }

        //更新日志表状态
        hlsWsRequestsService.updateResult(result, hlsWsRequests, iRequest, AppConstantUtils.Interface.DATA_JSON);
        return result;

    }

    /**
     * app 新增 指派审批人
     */
    @RequestMapping(value = "/app/leaf/user/insert/designated")
    public AppResponseData insertDesignated(HttpServletRequest request,
                                            @RequestBody JSONObject jsonObject) throws Exception {
        IRequest iRequest = createRequestContext(request);


        // 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (jsonObject != null) {
            hlsWsRequests.setRequestJson(jsonObject.toString());
        }
        //
        hlsWsRequests.setFunctionName("app 新增 指派审批人 ");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        AppResponseData result = AppJsonParseUtil.toAppResponseData(userAppService.insertDesignated(iRequest, jsonObject));

        //更新日志表状态
        hlsWsRequestsService.updateResult(result, hlsWsRequests, iRequest, AppConstantUtils.Interface.DATA_JSON);
        return result;

    }

    /**
     * app 查询 指派审批人
     */
    @RequestMapping(value = "/app/leaf/user/query/designated")
    public AppResponseData queryDesignated(HttpServletRequest request,
                                           @RequestBody JSONObject jsonObject) throws Exception {
        IRequest iRequest = createRequestContext(request);


        // 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (jsonObject != null) {
            hlsWsRequests.setRequestJson(jsonObject.toString());
        }
        //
        hlsWsRequests.setFunctionName("app 查询 指派审批人 ");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        AppResponseData result = AppJsonParseUtil.toAppResponseData(userAppService.queryDesignated(iRequest, jsonObject));

        //更新日志表状态
        hlsWsRequestsService.updateResult(result, hlsWsRequests, iRequest, AppConstantUtils.Interface.DATA_JSON);
        return result;

    }

    /**
     * app 删除 指派审批人
     */
    @RequestMapping(value = "/app/leaf/user/delete/designated")
    public AppResponseData deleteDesignated(HttpServletRequest request,
                                            @RequestBody JSONObject jsonObject) throws Exception {
        IRequest iRequest = createRequestContext(request);


        // 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (jsonObject != null) {
            hlsWsRequests.setRequestJson(jsonObject.toString());
        }
        //
        hlsWsRequests.setFunctionName("app 删除 指派审批人 ");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        AppResponseData result = AppJsonParseUtil.toAppResponseData(userAppService.deleteDesignated(iRequest, jsonObject));

        //更新日志表状态
        hlsWsRequestsService.updateResult(result, hlsWsRequests, iRequest, AppConstantUtils.Interface.DATA_JSON);
        return result;

    }

    /**
     * app 保存 指派审批人 有的话就删除再新增
     */
    @RequestMapping(value = "/app/leaf/user/save/designated")
    public AppResponseData saveDesignated(HttpServletRequest request,
                                          @RequestBody JSONObject jsonObject) throws Exception {
        IRequest iRequest = createRequestContext(request);


        // 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (jsonObject != null) {
            hlsWsRequests.setRequestJson(jsonObject.toString());
        }
        //
        hlsWsRequests.setFunctionName("app 保存 指派审批人 ");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        AppResponseData result = AppJsonParseUtil.toAppResponseData(userAppService.saveDesignated(iRequest, jsonObject));

        //更新日志表状态
        hlsWsRequestsService.updateResult(result, hlsWsRequests, iRequest, AppConstantUtils.Interface.DATA_JSON);
        return result;

    }

    /**
     * app  角色选择
     */
    @RequestMapping(value = "/app/leaf/user/role")
    public AppResponseData queryUserRole(HttpServletRequest request,
                                         @RequestBody JSONObject jsonObject) throws Exception {
        IRequest iRequest = createRequestContext(request);


        // 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (jsonObject != null) {
            hlsWsRequests.setRequestJson(jsonObject.toString());
        }
        //
        hlsWsRequests.setFunctionName("app 角色选择 ");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        AppResponseData result = AppJsonParseUtil.toAppResponseData(userAppService.queryUserRole(iRequest, jsonObject));

        //更新日志表状态
        hlsWsRequestsService.updateResult(result, hlsWsRequests, iRequest, AppConstantUtils.Interface.DATA_JSON);
        return result;

    }

    /**
     * app  审批历史查询
     */
    @RequestMapping(value = "/app/wfl/leaf/history/approvedInfo")
    public AppResponseData queryApprovedInfo(HttpServletRequest request,
                                             @RequestBody JSONObject jsonObject) throws Exception {
        IRequest iRequest = createRequestContext(request);


        // 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (jsonObject != null) {
            hlsWsRequests.setRequestJson(jsonObject.toString());
        }
        //
        hlsWsRequests.setFunctionName("app 审批历史查询 ");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        AppResponseData result = AppJsonParseUtil.toAppResponseData(userAppService.queryApprovedInfo(iRequest, jsonObject));
        if (result.getRows() != null) {
            result.setRows(result.getRows().replace("null", ""));
        }
        //更新日志表状态
        hlsWsRequestsService.updateResult(result, hlsWsRequests, iRequest, AppConstantUtils.Interface.DATA_JSON);
        return result;

    }

    @RequestMapping(value = "/app/ct/prj/quotation/re/calc")
    public AppResponseData quotationCalc(HttpServletRequest request,
                                         @RequestBody JSONObject jsonObject) throws Exception {
//        IRequest iRequest = createRequestContext(request);
//        return userAppService.quotationCalc(iRequest, jsonObject);

        IRequest iRequest = createRequestContext(request);


        // 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (jsonObject != null) {
            hlsWsRequests.setRequestJson(jsonObject.toString());
        }
        //
        hlsWsRequests.setFunctionName("报价测算");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        ResponseData result = userAppService.quotationCalc(iRequest, jsonObject);
        AppResponseData appResponseData = AppJsonParseUtil.toAppResponseData(result);
        if (appResponseData.getRows() != null) {
            appResponseData.setRows(appResponseData.getRows().replace("null", ""));
        }
        //更新日志表状态
        hlsWsRequestsService.updateResult(result, hlsWsRequests, iRequest, AppConstantUtils.Interface.DATA_JSON);

        return appResponseData;
    }

    @RequestMapping(value = "/app/query/prj/quotation/cashflow")
    public AppResponseData queryCashflow(HttpServletRequest request,
                                         @RequestBody JSONObject jsonObject) throws Exception {
        IRequest iRequest = createRequestContext(request);


        // 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (jsonObject != null) {
            hlsWsRequests.setRequestJson(jsonObject.toString());
        }
        //
        hlsWsRequests.setFunctionName("查询报价现金流");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        ResponseData result = userAppService.queryCashflow(iRequest, jsonObject);
        AppResponseData appResponseData = AppJsonParseUtil.toAppResponseData(result);
        if (appResponseData.getRows() != null) {
            appResponseData.setRows(appResponseData.getRows().replace("null", ""));
        }
        //更新日志表状态
        hlsWsRequestsService.updateResult(result, hlsWsRequests, iRequest, AppConstantUtils.Interface.DATA_JSON);

        return appResponseData;
    }

    @RequestMapping(value = "/app/select/combo/box")
    public AppResponseData selectComboBox(HttpServletRequest request,
                                          @RequestBody JSONObject jsonObject) throws Exception {
        IRequest iRequest = createRequestContext(request);


        // 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (jsonObject != null) {
            hlsWsRequests.setRequestJson(jsonObject.toString());
        }
        //
        hlsWsRequests.setFunctionName("查询下拉框");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        ResponseData result = userAppService.selectComboBox(iRequest, jsonObject);
        AppResponseData appResponseData = AppJsonParseUtil.toAppResponseData(result);
        if (appResponseData.getRows() != null) {
            appResponseData.setRows(appResponseData.getRows().replace("null", ""));
        }
        //更新日志表状态
        hlsWsRequestsService.updateResult(result, hlsWsRequests, iRequest, AppConstantUtils.Interface.DATA_JSON);

        return appResponseData;
    }

    @RequestMapping(value = "/app/select/business/report")
    public AppResponseData selectBusinessReport(HttpServletRequest request,
                                                @RequestBody JSONObject jsonObject) throws Exception {
        IRequest iRequest = createRequestContext(request);


        // 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (jsonObject != null) {
            hlsWsRequests.setRequestJson(jsonObject.toString());
        }
        //
        hlsWsRequests.setFunctionName("业务报表");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        ResponseData result = userAppService.selectBusinessReport(iRequest, jsonObject);
        AppResponseData appResponseData = AppJsonParseUtil.toAppResponseData(result);
        if (appResponseData.getRows() != null) {
            appResponseData.setRows(appResponseData.getRows().replace("null", ""));
        }
        //更新日志表状态
        hlsWsRequestsService.updateResult(result, hlsWsRequests, iRequest, AppConstantUtils.Interface.DATA_JSON);

        return appResponseData;
    }

    @RequestMapping(value = "/app/select/collection/report")
    public AppResponseData selectCollectionReport(HttpServletRequest request,
                                                  @RequestBody JSONObject jsonObject) throws Exception {
        IRequest iRequest = createRequestContext(request);


        // 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (jsonObject != null) {
            hlsWsRequests.setRequestJson(jsonObject.toString());
        }
        //
        hlsWsRequests.setFunctionName("回款报表");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        ResponseData result = userAppService.selectCollectionReport(iRequest, jsonObject);
        AppResponseData appResponseData = AppJsonParseUtil.toAppResponseData(result);
        if (appResponseData.getRows() != null) {
            appResponseData.setRows(appResponseData.getRows().replace("null", ""));
        }
        //更新日志表状态
//        hlsWsRequestsService.updateResult(result,hlsWsRequests,iRequest, AppConstantUtils.Interface.DATA_JSON);

        return appResponseData;
    }

    @RequestMapping(value = "/app/select/cockpit/report")
    public AppResponseData selectCockpitReport(HttpServletRequest request,
                                               @RequestBody JSONObject jsonObject) throws Exception {
        IRequest iRequest = createRequestContext(request);


        // 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (jsonObject != null) {
            hlsWsRequests.setRequestJson(jsonObject.toString());
        }
        //
        hlsWsRequests.setFunctionName("管理驾驶舱报表");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        ResponseData result = userAppService.selectCockpitReport(iRequest, jsonObject);
        AppResponseData appResponseData = AppJsonParseUtil.toAppResponseData(result);
        if (appResponseData.getRows() != null) {
            appResponseData.setRows(appResponseData.getRows().replace("null", ""));
        }
        //更新日志表状态
        hlsWsRequestsService.updateResult(result, hlsWsRequests, iRequest, AppConstantUtils.Interface.DATA_JSON);

        return appResponseData;
    }

    //驾驶舱 客户维度
    @RequestMapping(value = "/app/bp/cockpit/report")
    public AppResponseData bpCockpitReport(HttpServletRequest request,
                                           @RequestBody JSONObject jsonObject) throws Exception {
        IRequest iRequest = createRequestContext(request);


        // 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (jsonObject != null) {
            hlsWsRequests.setRequestJson(jsonObject.toString());
        }
        //
        hlsWsRequests.setFunctionName("客户管理驾驶舱报表");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        ResponseData result = userAppService.bpCockpitReport(iRequest, jsonObject);
        AppResponseData appResponseData = AppJsonParseUtil.toAppResponseData(result);
        if (appResponseData.getRows() != null) {
            appResponseData.setRows(appResponseData.getRows().replace("null", ""));
        }
        //更新日志表状态
        hlsWsRequestsService.updateResult(result, hlsWsRequests, iRequest, AppConstantUtils.Interface.DATA_JSON);

        return appResponseData;
    }

    //驾驶舱 项目经理维度
    @RequestMapping(value = "/app/project/cockpit/report")
    public AppResponseData projectCockpitReport(HttpServletRequest request,
                                                @RequestBody JSONObject jsonObject) throws Exception {
        IRequest iRequest = createRequestContext(request);


        // 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (jsonObject != null) {
            hlsWsRequests.setRequestJson(jsonObject.toString());
        }
        //
        hlsWsRequests.setFunctionName("项目经理管理驾驶舱报表");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        ResponseData result = userAppService.projectCockpitReport(iRequest, jsonObject);
        AppResponseData appResponseData = AppJsonParseUtil.toAppResponseData(result);
        if (appResponseData.getRows() != null) {
            appResponseData.setRows(appResponseData.getRows().replace("null", ""));
        }
        //更新日志表状态
        hlsWsRequestsService.updateResult(result, hlsWsRequests, iRequest, AppConstantUtils.Interface.DATA_JSON);

        return appResponseData;
    }
}
