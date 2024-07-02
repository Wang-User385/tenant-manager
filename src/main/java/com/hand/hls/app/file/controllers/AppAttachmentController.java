package com.hand.hls.app.file.controllers;


import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.app.file.service.IAppAttachmentService;
import com.hand.hls.app.utils.generalUtils.AppConstantUtils;
import com.hand.hls.web.logs.dto.HlsWsRequests;
import com.hand.hls.web.logs.service.IHlsWsRequestsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author liao
 */
@Controller
@RequestMapping(value = {"/r/api", "/"})
public class AppAttachmentController extends BaseController {
    @Autowired
    private IAppAttachmentService appAttachmentService;
    @Autowired
    private IHlsWsRequestsService hlsWsRequestsService;

    /***
     *  附件查询
     * */
    @RequestMapping(value = "/app/attachment/query")
    public ResponseData checkLogin(HttpServletRequest request,
                                   @RequestBody JSONObject jsonObject) throws Exception {
        IRequest iRequest = createRequestContext(request);


        // 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (jsonObject != null) {
            hlsWsRequests.setRequestJson(jsonObject.toString());
        }
        //
        hlsWsRequests.setFunctionName("附件查询");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        ResponseData result = appAttachmentService.queryAttachment(iRequest, jsonObject);

        //更新日志表状态
        hlsWsRequestsService.updateResult(result, hlsWsRequests, iRequest, AppConstantUtils.Interface.DATA_JSON);
        return result;

    }

    /***
     *  附件下载
     * */
    @RequestMapping(value = "/app/attachment/download")
    public ResponseEntity<byte[]> downloadAtt(HttpServletRequest request,
                                              @RequestBody JSONObject jsonObject) throws Exception {
        IRequest iRequest = createRequestContext(request);


        // 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (jsonObject != null) {
            hlsWsRequests.setRequestJson(jsonObject.toString());
        }
        //
        hlsWsRequests.setFunctionName("附件下载");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        ResponseEntity<byte[]> result = appAttachmentService.downloadAtt(iRequest, jsonObject);

        //更新日志表状态
        hlsWsRequestsService.updateResultFile(result, hlsWsRequests, iRequest);
        return result;

    }


    /***
     *  附件删除
     * */
    @RequestMapping(value = "/app/attachment/delete")
    public ResponseData deleteAtt(HttpServletRequest request,
                                  @RequestBody JSONObject jsonObject) throws Exception {
        IRequest iRequest = createRequestContext(request);


// 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (jsonObject != null) {
            hlsWsRequests.setRequestJson(jsonObject.toString());
        }
        //
        hlsWsRequests.setFunctionName("附件删除");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        ResponseData result = appAttachmentService.deleteAtt(iRequest, jsonObject);

        //更新日志表状态
        hlsWsRequestsService.updateResult(result, hlsWsRequests, iRequest, AppConstantUtils.Interface.DATA_JSON);

        return result;

    }
    /***
     *  附件上传
     * */
    @RequestMapping(value = "/app/attachment/uploadFile", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData uploadFile(HttpServletRequest request ) throws Exception {
        IRequest iRequest = createRequestContext(request);


        // 接口日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        if (request != null) {
            hlsWsRequests.setRequestJson( hlsWsRequestsService.getFileInfo(request));
        }
        //
        hlsWsRequests.setFunctionName("app附件上传");
        hlsWsRequests = hlsWsRequestsService.interfaceSaveAll(hlsWsRequests, request, iRequest);

        ResponseData result = appAttachmentService.uploadFile(request,iRequest);

        //更新日志表状态
        hlsWsRequestsService.updateResult(result, hlsWsRequests, iRequest, AppConstantUtils.Interface.DATA_JSON);

        return result;

    }
}
