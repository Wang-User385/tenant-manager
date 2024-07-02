package com.hand.hls.pam.controllers;

import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.common.components.HlsWordToPdfComponent;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fct.dto.HlsCusCreditProject;
import com.hand.hls.fct.service.HlsCusFctProjectAttachmentService;
import com.hand.hls.pam.dto.HlsWarrantStockTran;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.pam.dto.HlsWarrantStockHd;
import com.hand.hls.pam.service.IHlsWarrantStockHdService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
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
public class HlsWarrantStockHdController extends BaseController {

    @Autowired
    private IHlsWarrantStockHdService service;

    @Autowired
    private HlsCusFctProjectAttachmentService hlsCusFctProjectAttachmentService;

    @Autowired
    private HlsWordToPdfComponent hlsWordToPdfComponent;


    @RequestMapping(value = "/warrant/stock/hd/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsWarrantStockHd dto = param.toJavaObject(HlsWarrantStockHd.class);
        return new ResponseData(service.select(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/warrant/stock/hd/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsWarrantStockHd> list = param.toJavaList(HlsWarrantStockHd.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/warrant/stock/hd/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsWarrantStockHd> dto = parameter.toJavaList(HlsWarrantStockHd.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }

    @RequestMapping(value = "/warrant/stock/submit/wfl")
    @ResponseBody
    public ResponseData warrantSubmitWfl(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsWarrantStockTran warrantStockTranList = param.toJavaObject(HlsWarrantStockTran.class);
        List<HlsWarrantStockTran> list = new ArrayList<>(1);
        list.add(service.warrantStockSubmitWfl(iRequest , warrantStockTranList ));
        return new ResponseData(list);

    }


    @RequestMapping(value = "/warrant/create/download")
    @ResponseBody
    public ResponseData prjApprovalNotice(HttpServletRequest request, Long projectId,String templateCode,Long warrantStockId) throws HlsCusException {
        IRequest requestCtx = createRequestContext(request);
        List<FndAttachmentMulti> fndAttachmentMultiList = service.warrantNoticeSave(requestCtx, projectId,templateCode,warrantStockId);
        hlsWordToPdfComponent.wordToPdfAttachmentMuti(requestCtx,fndAttachmentMultiList);
        return new ResponseData(fndAttachmentMultiList);
    }



}