package com.hand.hls.ast.controllers;

import com.alibaba.fastjson.JSON;
import com.hand.hls.ast.mapper.NoticeManageMapper;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.service.IConContractCashflowService;
import com.hand.hls.fnd.dto.HlsDocFileTemplet;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.ast.dto.NoticeManage;
import com.hand.hls.ast.service.INoticeManageService;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

@Controller
public class NoticeManageController extends BaseController {

    @Autowired
    private INoticeManageService service;
    @Autowired
    private NoticeManageMapper mapper;


    @RequestMapping(value = "/ast/notice/manage/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        NoticeManage dto = param.toJavaObject(NoticeManage.class);
        return new ResponseData(service.select(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/ast/notice/manage/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<NoticeManage> list = param.toJavaList(NoticeManage.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/ast/notice/manage/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<NoticeManage> dto = parameter.toJavaList(NoticeManage.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }

    @RequestMapping(value = "/ast/notice/manage/cashflow/query")
    @ResponseBody
    public ResponseData selectCashflowInfo(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                           @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        NoticeManage dto = param.toJavaObject(NoticeManage.class);
        return new ResponseData(service.cashflowInfo(dto, requestContext, pagenum, pagesize));
    }

    @RequestMapping(value = "/ast/contract/cashflow/queryLov")
    @ResponseBody
    public ResponseData queryCashflow(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                              HttpServletRequest request,
                              @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        NoticeManage metadataRelation = param.toJavaObject(NoticeManage.class);

        IRequest requestCtx = createRequestContext(request);
        List<NoticeManage> list = service.queryContractCashflowLov(requestCtx, metadataRelation, pagenum, pagesize);
        if (list.size() > 0) {
            list.stream().forEach(noticeManage -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                noticeManage.setDueDateFormat(sdf.format(noticeManage.getDueDate()));
                if(noticeManage.getCalcDate() == null) {
                    noticeManage.setCalcDateFormat(sdf.format(noticeManage.getDueDate()));
                }else{
                    noticeManage.setCalcDateFormat(sdf.format(noticeManage.getCalcDate()));
                }
            });
        }
        return new ResponseData(list);
    }

    @RequestMapping(value = "/ast/notice/templet/queryLov")
    @ResponseBody
    public ResponseData queryTemplet(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                              HttpServletRequest request, HttpServletResponse response,
                              HlsDocFileTemplet hlsDocFileTemplet,
                              @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        NoticeManage metadataRelation = param.toJavaObject(NoticeManage.class);

        //用来接收setLovPara 参数
        if (hlsDocFileTemplet.getTempletNameStr() != null) {
            metadataRelation.setTempletNameStr(hlsDocFileTemplet.getTempletNameStr());
        }

        IRequest requestCtx = createRequestContext(request);
        List<NoticeManage> list = service.queryNoticeTempLov(requestCtx, metadataRelation, pagenum, pagesize);

        return new ResponseData(list);
    }

    @RequestMapping(value = "/ast/notice/print/file/download")
    @ResponseBody
    public ResponseData downloadNoticePrintFile(String saveData, HttpServletRequest request, HttpServletResponse response) {
        IRequest requestContext = createRequestContext(request);
        List<String> strList = Arrays.asList(saveData.split(","));
        List<NoticeManage> noticeManageList = new ArrayList<NoticeManage>();
        strList.forEach(item->{
            NoticeManage noticeManage = new NoticeManage();
            noticeManage.setNoticeManageId(Long.valueOf(item));
            List<NoticeManage> dto = mapper.queryAll(noticeManage);
            noticeManageList.add(dto.get(0));
        });
        return service.downloadNoticePrintFile(noticeManageList, requestContext, request, response);
    }

    @RequestMapping(value = "/ast/contract/cashflow/item/queryLov")
    @ResponseBody
    public ResponseData queryCashflowItem(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                      HttpServletRequest request,
                                      @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        NoticeManage metadataRelation = param.toJavaObject(NoticeManage.class);

        IRequest requestCtx = createRequestContext(request);
        List<NoticeManage> list = service.queryContractCashflowItemLov(requestCtx, metadataRelation, pagenum, pagesize);
        return new ResponseData(list);
    }
}