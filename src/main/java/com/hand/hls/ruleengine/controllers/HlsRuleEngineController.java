package com.hand.hls.ruleengine.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.ruleengine.dto.HlsRuleEngine;
import com.hand.hls.ruleengine.service.IHlsRuleEngineService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class HlsRuleEngineController extends BaseController {

    @Autowired
    private IHlsRuleEngineService service;


    @RequestMapping(value = "/hls/rule/engine/leaf/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsRuleEngine dto = param.toJavaObject(HlsRuleEngine.class);
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext, dto, pagenum, pageSize));
    }

    @RequestMapping(value = "/hls/rule/engine/leaf/selectInfo/query")
    @ResponseBody
    public ResponseData selectInfo(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsRuleEngine dto = param.toJavaObject(HlsRuleEngine.class);
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectInfo(requestContext, dto, pagenum, pageSize));
    }

    @RequestMapping(value = "/hls/rule/engine/leaf/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsRuleEngine> dto = param.toJavaList(HlsRuleEngine.class);
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/hls/rule/engine/leaf/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsRuleEngine> dto = param.toJavaList(HlsRuleEngine.class);
        dto.forEach(item -> {
            if ("update".equals(item.get__status())) {
                item.set__status("delete");
            }
        });
        service.batchDelete(dto);
        return new ResponseData(dto);
    }

    @RequestMapping(value = "/hls/rule/engine/resultLov")
    @ResponseBody
    public ResponseData resultLov(HttpServletRequest request, @RequestParam Map param, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest requestCtx = createRequestContext(request);
        ResponseData responseData = new ResponseData();
//        RequestHelper.setCurrentRequest(requestCtx);
        List<Map<String, Object>> listSort = new ArrayList<>();
        List<Map<String, Object>> hlsResultList = service.getHlsRuleEngine(requestCtx, param);
        int size = hlsResultList.size();
        int pageStart = pagenum == 1 ? 0 : (pagenum-1) * pagesize;//截取的开始位置
        int pageEnd = size < pagenum*pagesize ? size : pagenum*pagesize;//截取的结束位置
        if(size > pageStart){
            listSort = hlsResultList.subList(pageStart, pageEnd);
        }
        //总页数
        int totalPage = hlsResultList.size()/pagesize;

        responseData.setRows(listSort);
        responseData.setSuccess(true);
        responseData.setTotal(Long.valueOf(hlsResultList.size()));

        return responseData;
    }
}