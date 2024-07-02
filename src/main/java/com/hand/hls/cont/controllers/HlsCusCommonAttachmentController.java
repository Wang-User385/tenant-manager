package com.hand.hls.cont.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.cont.dto.HlsCusCommonAttachment;
import com.hand.hls.cont.service.IHlsCusCommonAttachmentService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

    @Controller
    public class HlsCusCommonAttachmentController extends BaseController{

    @Autowired
    private IHlsCusCommonAttachmentService service;


    @RequestMapping(value = "/jczl/common/attachment/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusCommonAttachment dto = param.toJavaObject(HlsCusCommonAttachment.class);
        return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));
    }

    @RequestMapping(value = "/jczl/common/attachment/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusCommonAttachment> list = param.toJavaList(HlsCusCommonAttachment.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/jczl/common/attachment/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<HlsCusCommonAttachment> dto = parameter.toJavaList(HlsCusCommonAttachment.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }

    /**
     * 关税资料清单文本批量下载
     *
     * @return
     */
    @RequestMapping(value = "/tariff/contract/content/package")
    @ResponseBody
    public String attachmentPackageDownload(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, HlsCusCommonAttachment attachmentPara,
                                            final HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        return service.contractContextPackage(httpServletRequest, httpServletResponse, iRequest, attachmentPara);
    }

    /**
     * 关税资料清单初始化
     *
     * @return
     */
    @RequestMapping(value = "/tariff/contract/content/init")
    @ResponseBody
    public ResponseData tariffContractContentInit(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws Exception {
        IRequest iRequest = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusCommonAttachment hlsCusCommonAttachment = param.toJavaObject(HlsCusCommonAttachment.class);
        service.tariffContractContextInit(iRequest,hlsCusCommonAttachment);
        List<HlsCusCommonAttachment> list = new ArrayList<>();
        return new ResponseData(list);
    }


        @RequestMapping(value = "/csh/contract/content/package")
        @ResponseBody
        public String cshAttachmentPackageDownload(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, HlsCusCommonAttachment attachmentPara,
                                                final HttpServletRequest request) {
            IRequest iRequest = createRequestContext(request);
            RequestHelper.setCurrentRequest(iRequest);
            return service.cshContractContextPackage(httpServletRequest, httpServletResponse, iRequest, attachmentPara);
        }
}