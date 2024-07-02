package com.hand.hls.app.controllers;

import com.alibaba.fastjson.JSON;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.app.dto.SysIfInvokeInboundDto;
import com.hand.hls.app.entity.HlsCusItfcResponseData;
import com.hand.hls.app.mapper.SysIfInvokeInboundMapper;
import com.hand.hls.app.service.SysIfInvokeInboundService;
import com.hand.hls.app.service.impl.HlsCashflowAyncServiceImpl;
import com.hand.hls.app.service.impl.SysIfInvokeInboundServiceImpl;
import com.hand.hls.app.utils.HLsCashFlowAyncEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.Date;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

@Controller
public class HlsCashflowAyncController extends BaseController {

    @Autowired
    HlsCashflowAyncServiceImpl hlsCashflowAyncService;

    @Autowired
    SysIfInvokeInboundServiceImpl sysIfInvokeInboundService;

    @RequestMapping(value = "/r/api/hls/app/controller/repayment",method = RequestMethod.POST)
    @ResponseBody
    public HlsCusItfcResponseData repayment(HttpServletRequest httpServletRequest, @RequestBody net.sf.json.JSONObject params){
        IRequest iRequest = this.createRequestContext(httpServletRequest);
        SysIfInvokeInboundDto inboundDto = new SysIfInvokeInboundDto();
        HlsCusItfcResponseData hlsCusItfcResponseData = new HlsCusItfcResponseData();
        hlsCusItfcResponseData.setPrnd(""+ System.currentTimeMillis());
        String fromDate=null;
        String toDate=null;

        inboundDto.setInterfaceName(httpServletRequest.getServletPath());
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        HashMap<String, String> header = new HashMap<>();
        while (headerNames.hasMoreElements()) {
            String s = (String) headerNames.nextElement();
            header.put(s,httpServletRequest.getHeader(s));
        }
        inboundDto.setRequestHeaderParameter(header.toString());
        inboundDto.setRequestMethod(httpServletRequest.getMethod());
        inboundDto.setRequestBodyParameter(params.toString());
        inboundDto.setInterfaceUrl(httpServletRequest.getRequestURI());
        inboundDto.setIp(httpServletRequest.getHeaders("host").nextElement());
        if (params.size() == 0) {
            hlsCusItfcResponseData.setMsg(HLsCashFlowAyncEnum.NULL.getMessage());
            hlsCusItfcResponseData.setData(null);
            hlsCusItfcResponseData.setCode(HLsCashFlowAyncEnum.NULL.getCode());
            return hlsCusItfcResponseData;
        }
        if (params.isEmpty()){

        } else {
            fromDate = params.getString("fromDate");
            toDate = params.getString("toDate");
        }
        try {
            HlsCusItfcResponseData result = hlsCashflowAyncService.getResult(iRequest, fromDate, toDate);
            hlsCusItfcResponseData.setMsg(result.getMsg());
            hlsCusItfcResponseData.setCode(result.getCode());
            hlsCusItfcResponseData.setData(result.getData());
            inboundDto.setResponseTime(System.currentTimeMillis());
            inboundDto.setResponseContent(result.getData().toString());
        } catch (NullPointerException e){
            hlsCusItfcResponseData.setMsg(HLsCashFlowAyncEnum.NULL.getMessage());
            hlsCusItfcResponseData.setData(null);
            hlsCusItfcResponseData.setCode(HLsCashFlowAyncEnum.NULL.getCode());
            inboundDto.setStacktrace(e.toString());
            sysIfInvokeInboundService.insertSelective(iRequest,inboundDto);
            return hlsCusItfcResponseData;
        }
        sysIfInvokeInboundService.insertSelective(iRequest,inboundDto);
        return hlsCusItfcResponseData;
    }
}
