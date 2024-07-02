package com.hand.hls.fct.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fct.dto.HlsCusHlsCreditLine;
import com.hand.hls.fct.service.HlsCreditLineService;
import com.hand.hls.prj.dto.HlsCusCreditInfo;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HlsCreditLineController extends BaseController {

    @Autowired
    private HlsCreditLineService service;

    @RequestMapping(value = "/hls/credit/line/query")
    @ResponseBody
    public ResponseData creditLineDetailQuery(@ModelAttribute("_request_data") LeafRequestData requestData, HttpSession session) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusHlsCreditLine dto = param.toJavaObject(HlsCusHlsCreditLine.class);
        dto.setCompanyId((Long) session.getAttribute("companyId"));
        return new ResponseData(service.creditLineDetailQuery(dto));
    }

    @RequestMapping(value = "/hls/credit/line/finstatment/info/query")
    @ResponseBody
    public ResponseData queryFinStatementByBpId(@ModelAttribute("_request_data") LeafRequestData requestData) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusHlsCreditLine hlsCusHlsCreditLine = param.toJavaObject(HlsCusHlsCreditLine.class);
        return new ResponseData(service.queryFinStatementByBpId(hlsCusHlsCreditLine));
    }

    @RequestMapping(value = "/hls/credit/line/detail/query")
    @ResponseBody
    public ResponseData selectCreditLineInfo(@ModelAttribute("_request_data") LeafRequestData requestData, HttpSession session, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) throws ParseException {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusHlsCreditLine hlsCusHlsCreditLine = param.toJavaObject(HlsCusHlsCreditLine.class);
        hlsCusHlsCreditLine.setCompanyId((Long) session.getAttribute("companyId"));
        List<HlsCusHlsCreditLine> list = service.selectCreditLineInfo(hlsCusHlsCreditLine, pagenum, pagesize);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/hls/credit/line/initialization/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusHlsCreditLine hlsCusHlsCreditLine = param.toJavaObject(HlsCusHlsCreditLine.class);
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext, hlsCusHlsCreditLine, pagenum, pagesize));
    }

    @RequestMapping(value = "/hls/credit/line/info/save")
    @ResponseBody
    public ResponseData HlsCreditLineSave(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request){
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusCreditInfo hlsCusCreditInfo = param.toJavaObject(HlsCusCreditInfo.class);
        List<HlsCusHlsCreditLine> list = new ArrayList<>(0);
        list.add(service.hlscreditLineSave(requestCtx,hlsCusCreditInfo));
        return new ResponseData(list);
    }

    @RequestMapping(value = "/hls/credit/line/info/submit")
    @ResponseBody
    public ResponseData HlsCreditLineSubmit(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request){
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusCreditInfo hlsCusCreditInfo = param.toJavaObject(HlsCusCreditInfo.class);
        IRequest requestCtx = createRequestContext(request);
        List<HlsCusHlsCreditLine> list = new ArrayList<>(1);
        list.add(service.hlscreditLineSubmit(requestCtx,hlsCusCreditInfo));
        return new ResponseData(list);
    }


}
