package com.hand.hls.cont.controllers;

import com.hand.hls.utils.DocumentValidate;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.cont.dto.ContentNumberHead;
import com.hand.hls.cont.service.IContentNumberHeadService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

@Controller
public class ContentNumberHeadController extends BaseController{

    @Autowired
    private IContentNumberHeadService service;


    @RequestMapping(value = "/con/content/number/head/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        ContentNumberHead dto = param.toJavaObject(ContentNumberHead.class);
        return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));
    }

    @RequestMapping(value = "/con/content/number/head/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<ContentNumberHead> list = param.toJavaList(ContentNumberHead.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/con/content/number/head/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<ContentNumberHead> dto = parameter.toJavaList(ContentNumberHead.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }

    @RequestMapping(value = "/con/content/number/head/submit/wfl")
    @ResponseBody
    public ResponseData submitWfl(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws Exception {
        IRequest iRequest = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        ContentNumberHead head = param.toJavaObject(ContentNumberHead.class);

        //校验单据状态
        head = service.selectByPrimaryKey(iRequest, head);
        List<String> list = new ArrayList<>();
        //以下状态才可以提交审批
        list.add(DocumentValidate.NEW);
        list.add(DocumentValidate.APPROVED_RETURN);
        list.add(DocumentValidate.CANCEL);
        list.add(DocumentValidate.REJECTED);
        DocumentValidate.statusValidate(head.getStatus(), list);

        service.submitWfl(iRequest,head);

        return new ResponseData(true);
    }

}