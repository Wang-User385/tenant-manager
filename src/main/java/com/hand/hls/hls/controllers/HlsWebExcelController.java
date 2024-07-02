package com.hand.hls.hls.controllers;

import com.hand.hap.core.exception.TokenException;
import com.hand.hls.common.utils.GzipUtil;
import com.hand.hls.hls.mapper.HlsWebExcelMapper;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.hls.dto.HlsWebExcel;
import com.hand.hls.hls.service.IHlsWebExcelService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

    @Controller
    public class HlsWebExcelController extends BaseController{

    @Autowired
    private IHlsWebExcelService service;

    @Autowired
    private HlsWebExcelMapper mapper;

    @RequestMapping(value = "/hls/web/excel/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsWebExcel dto = param.toJavaObject(HlsWebExcel.class);
        return new ResponseData(service.selectHlsWebExcelInfo(requestContext,dto,pagenum,pagesize));
    }

    @RequestMapping(value = "/hls/web/excel/query/sheets", produces = "application/javascript;charset=utf-8")
    @ResponseBody
    public String querySheet(HttpServletRequest request, HlsWebExcel hcc) {
        IRequest requestCtx = createRequestContext(request);
        HlsWebExcel hlsWebExcel = service.selectByPrimaryKey(requestCtx, hcc);
        if (hlsWebExcel.getSheets() == null) {

            //没有定义sheets时，初始化一个sheet让其可以维护
            return null;
        } else {
            return hlsWebExcel.getSheets();
        }

    }

    @RequestMapping(value = "/hls/web/excel/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsWebExcel> list = param.toJavaList(HlsWebExcel.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/hls/web/excel/save/sheets", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData saveSheet(HttpServletRequest request, Long excelId, @RequestBody String sheets)
            throws TokenException {
        ResponseData rd = null;
        try {
            String stringSheets = GzipUtil.atob(sheets);
            String unzipSheets = GzipUtil.unCompress(stringSheets.getBytes("iso-8859-1"));
            String jsonSheets = URLDecoder.decode(unzipSheets,"utf-8");
            service.updateSheets(excelId, jsonSheets,sheets);
            rd = new ResponseData();
        } catch (Exception e) {
            rd = new ResponseData(false);
            rd.setMessage(e.getMessage());
        }

        return rd;
    }

    @RequestMapping(value = "/hls/web/excel/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<HlsWebExcel> dto = parameter.toJavaList(HlsWebExcel.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }

        /*
         * 查询sheet个数
         *
         * */
        @RequestMapping(value = "/hls/web/excel/query/sheetInfo")
        @ResponseBody
        public ResponseData queryExcelSheet(HttpServletRequest request,
                                            @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws UnsupportedEncodingException {
            IRequest requestCtx = createRequestContext(request);
            JSONObject param = (JSONObject) requestData.get("parameter");
            HlsWebExcel hlsCalcConfig = param.toJavaObject(HlsWebExcel.class);

            List<Map> list = service.getSheetNames(hlsCalcConfig);
            return new ResponseData(list);
        }
}