package com.hand.hls.hls.controllers;

import com.hand.hls.hls.dto.HlsCusHlsMarketingReportBp;
import com.hand.hls.hls.dto.HlsCusHlsReportAttachment;
import com.hand.hls.utils.ResMessageException;
import leaf.service.validation.ParameterNullException;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.hls.dto.MarketingReportChange;
import com.hand.hls.hls.service.IMarketingReportChangeService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;

import java.rmi.NoSuchObjectException;
import java.util.List;
import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

    @Controller
    public class MarketingReportChangeController extends BaseController{

    @Autowired
    private IMarketingReportChangeService service;


    @RequestMapping(value = "/hls/marketing/report/change/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        MarketingReportChange dto = param.toJavaObject(MarketingReportChange.class);
        return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));
    }

    @RequestMapping(value = "/hls/marketing/report/change/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<MarketingReportChange> list = param.toJavaList(MarketingReportChange.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/hls/marketing/report/change/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<MarketingReportChange> dto = parameter.toJavaList(MarketingReportChange.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }

        @RequestMapping(value = "/marketing/report/change/save")
        @ResponseBody
        public ResponseData save(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) throws ResMessageException, ParameterNullException, NoSuchObjectException {
            IRequest iRequest = createRequestContext(request);
            RequestHelper.setCurrentRequest(iRequest);

            JSONObject param = (JSONObject) requestData.get("parameter");
            MarketingReportChange marketingReportChange = param.toJavaObject(MarketingReportChange.class);
            return new ResponseData(service.changeCreate(iRequest, marketingReportChange));
        }
        @RequestMapping(value = "/marketing/report/change/submit")
        @ResponseBody
        public ResponseData submit(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) throws ResMessageException, ParameterNullException, NoSuchObjectException {
            IRequest iRequest = createRequestContext(request);
            RequestHelper.setCurrentRequest(iRequest);
            JSONObject param = (JSONObject) requestData.get("parameter");
            MarketingReportChange marketingReportChange = param.toJavaObject(MarketingReportChange.class);
            marketingReportChange = service.selectByPrimaryKey(iRequest,marketingReportChange);
            return new ResponseData(service.changeSubmit(iRequest, marketingReportChange));
        }
        @RequestMapping(value = "/marketing/report/change/cancel")
        @ResponseBody
        public ResponseData cancel(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData leafRequestData) {
            Long changeReqId = ((JSONObject)leafRequestData.get("parameter")).getLong("change_req_id");
            try {
                this.service.cancelChangeReq(this.createRequestContext(request), changeReqId);
            } catch (NoSuchObjectException var5) {
                return new ResponseData(false, var5.getMessage());
            }
            return new ResponseData();

        }
        @RequestMapping(value = "/marketing/report/bp/change/delete")
        @ResponseBody
        public ResponseData bpDelete(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData leafRequestData) throws Exception {
            Long marketingReportId = ((JSONObject)leafRequestData.get("parameter")).getLong("marketing_report_id");
            Long marketingReportBpId = ((JSONObject)leafRequestData.get("parameter")).getLong("marketing_report_bp_id");
            List<HlsCusHlsMarketingReportBp> hlsMarketingReportBps = this.service.deleteBpChangeReq(this.createRequestContext(request), marketingReportId,marketingReportBpId);
            if(hlsMarketingReportBps.size() != 0 ){
                ResponseData responseData = new ResponseData(true);
                responseData.setMessage("不能删除该单据");
                return responseData;
            }
            return new ResponseData(hlsMarketingReportBps);

        }
        @RequestMapping(value = "/marketing/report/att/change/delete")
        @ResponseBody
        public ResponseData attDelete(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws Exception {
            JSONArray parameter = (JSONArray) requestData.get("parameter");
            List<HlsCusHlsReportAttachment> list = parameter.toJavaList(HlsCusHlsReportAttachment.class);
            int result = this.service.deleteAttachmentChangeReq(this.createRequestContext(request),list);
            if(result != 0){
                ResponseData responseData = new ResponseData(true);
                responseData.setMessage("不能删除该单据");
                return responseData;
            }
            return new ResponseData(list);
        }
}