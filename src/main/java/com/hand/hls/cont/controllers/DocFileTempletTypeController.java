//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.cont.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.BaseException;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.cont.dto.DocFileTempletType;
import com.hand.hls.cont.service.IDocFileTempletRuleService;
import com.hand.hls.cont.service.IDocFileTempletTypeService;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.hand.hls.ruleengine.service.IRuleEngineTypeService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DocFileTempletTypeController extends BaseController {
    @Autowired
    private IDocFileTempletTypeService service;
    @Autowired
    private IDocFileTempletRuleService fileTempletRuleService;

    public DocFileTempletTypeController() {
    }

    @RequestMapping({"/hls/doc/file/templet/type/query"})
    @ResponseBody
    public ResponseData query(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request, HttpServletResponse response, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) {
        JSONObject param = (JSONObject)requestData.get("parameter");
        DocFileTempletType dto = (DocFileTempletType)param.toJavaObject(DocFileTempletType.class);
        IRequest requestCtx = this.createRequestContext(request);
        return new ResponseData(this.service.select(requestCtx, dto, pagenum, pagesize));
    }

    @RequestMapping({"/hls/doc/file/templet/type/lov"})
    @ResponseBody
    public ResponseData queryLov(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request, HttpServletResponse response, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) {
        JSONObject param = (JSONObject)requestData.get("parameter");
        DocFileTempletType dto = (DocFileTempletType)param.toJavaObject(DocFileTempletType.class);
        IRequest requestCtx = this.createRequestContext(request);
        return new ResponseData(this.fileTempletRuleService.selectTempletTypeLov(dto));
    }

    @PostMapping({"/hls/doc/file/templet/type/submit"})
    public ResponseData submit(HttpServletRequest request, HttpSession session, @ModelAttribute("_request_data") LeafRequestData requestData, BindingResult result) throws BaseException {
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<DocFileTempletType> dtos = parameter.toJavaList(DocFileTempletType.class);
        ResponseData rd = null;
        IRequest requestCtx = null;
        requestCtx = this.createRequestContext(request);
        return new ResponseData(this.service.batchUpdate(requestCtx, dtos));
    }

    @RequestMapping({"/remove"})
    @ResponseBody
    public ResponseData delete(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request) {
        JSONArray param = (JSONArray)requestData.get("parameter");
        List<DocFileTempletType> dto = param.toJavaList(DocFileTempletType.class);
        IRequest requestContext = this.createRequestContext(request);
        this.service.batchDelete(requestContext, dto);
        return new ResponseData(dto);
    }
}
