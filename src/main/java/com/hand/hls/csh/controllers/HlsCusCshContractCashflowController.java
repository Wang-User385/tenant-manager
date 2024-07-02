package com.hand.hls.csh.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;


import com.hand.hls.csh.dto.HlsCusCshContractCashflow;
import com.hand.hls.csh.service.HlsCusCshContractCashflowService;
import leaf.bean.LeafRequestData;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class HlsCusCshContractCashflowController extends BaseController {

    @Autowired
    private HlsCusCshContractCashflowService hlsCusCshContractCashflowService;

    /**
     * 收付管理查询未完全核销的现金流主页面查询
     */
    @RequestMapping(value = "/ct/csh/con/cashflow/list/query/home")
    @ResponseBody
    public ResponseData queryNotFullCashFlowHome(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request,
                                                 @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum, @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        IRequest requestCtx = createRequestContext(request);
        HlsCusCshContractCashflow metadataRelation = param.toJavaObject(HlsCusCshContractCashflow.class);
        return new ResponseData(hlsCusCshContractCashflowService.selectNotFullContractCashflowHome(requestCtx, metadataRelation, pagenum, pagesize));
    }

    /**
     * 应收查询
     */
    @RequestMapping(value = "/ct/csh/all/con/cashflow/home")
    @ResponseBody
    public ResponseData queryAllCashFlowHome(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request,
                                                 @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum, @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        String sortName=null;
        String sortOrder=null;
        if(param.get("sort_name")!=null){
            sortName = param.get("sort_name").toString();
        }
        if(param.get("sort_name")!=null){
            sortOrder=param.get("sort_order").toString();
        }

        IRequest requestCtx = createRequestContext(request);
        HlsCusCshContractCashflow metadataRelation = param.toJavaObject(HlsCusCshContractCashflow.class);
        return new ResponseData(hlsCusCshContractCashflowService.selectAllContractCashflowHome(requestCtx, metadataRelation, pagenum, pagesize,sortName,sortOrder));
    }


    /**
     * 租金回收率明细表
     */
    @RequestMapping(value = "/ct/csh/recovery/rate/home")
    @ResponseBody
    public ResponseData queryRecoveryRate(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request,
                                             @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum, @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        IRequest requestCtx = createRequestContext(request);
        HlsCusCshContractCashflow metadataRelation = param.toJavaObject(HlsCusCshContractCashflow.class);
        return new ResponseData(hlsCusCshContractCashflowService.selectRecoveryRate(requestCtx, metadataRelation, pagenum, pagesize));
    }

    /**
     * 租金回收率统计
     */
    @RequestMapping(value = "/ct/csh/unit/recovery/rate/home")
    @ResponseBody
    public ResponseData queryUnitRecoveryRate(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request,
                                          @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum, @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        String sortName=null;
        String sortOrder=null;
        if(param.get("sort_name")!=null){
            sortName = param.get("sort_name").toString();
        }
        if(param.get("sort_name")!=null){
            sortOrder=param.get("sort_order").toString();
        }


        IRequest requestCtx = createRequestContext(request);
        HlsCusCshContractCashflow metadataRelation = param.toJavaObject(HlsCusCshContractCashflow.class);
        return new ResponseData(hlsCusCshContractCashflowService.selectUnitRecoveryRate(requestCtx, metadataRelation, pagenum, pagesize,sortName,sortOrder));
    }

    @RequestMapping(value = "/csh/unit/query/lov")
    @ResponseBody
    public ResponseData queryUnitLov(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                              HttpServletRequest request, HttpServletResponse response,
                              @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");

        IRequest requestCtx = createRequestContext(request);
        HlsCusCshContractCashflow metadataRelation = param.toJavaObject(HlsCusCshContractCashflow.class);
        List<HlsCusCshContractCashflow> list = hlsCusCshContractCashflowService.selectUnitLov(requestCtx, metadataRelation, pagenum, pagesize);
        return new ResponseData(list);
    }

}