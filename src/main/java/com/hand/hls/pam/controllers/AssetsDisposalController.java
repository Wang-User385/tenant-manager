package com.hand.hls.pam.controllers;

import com.hand.hls.bp.dto.HlsScoreCalculation;
import com.hand.hls.utils.ResMessageException;
import hls.core.utils.exception.HlsCusException;
import leaf.service.validation.ParameterNullException;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.pam.dto.AssetsDisposal;
import com.hand.hls.pam.service.IAssetsDisposalService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

    @Controller
    public class AssetsDisposalController extends BaseController{

    @Autowired
    private IAssetsDisposalService service;


    @RequestMapping(value = "/assets/disposal/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        AssetsDisposal dto = param.toJavaObject(AssetsDisposal.class);
        return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));
    }

    @RequestMapping(value = "/assets/disposal/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<AssetsDisposal> list = param.toJavaList(AssetsDisposal.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/assets/disposal/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<AssetsDisposal> dto = parameter.toJavaList(AssetsDisposal.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }


        @RequestMapping(value = "/assets/disposal/create")
        @ResponseBody
        public ResponseData assetsDisposalCreate(AssetsDisposal dto, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData ,
                                                     @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                     @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) throws HlsCusException {
            IRequest requestContext = createRequestContext(request);
            JSONObject param1 = (JSONObject)requestData.get("parameter");
            JSONObject param = (JSONObject)param1.get("param");
            String assets_appl_number =(String.valueOf(param.get("assets_appl_number")));
            String contract_number = (String.valueOf(param.get("contract_number")));
            //String contract_status = (String.valueOf(param.get("contract_status")));
            Long project_id = Long.valueOf(String.valueOf(param.get("project_id")));
            Long employee_id = Long.valueOf(String.valueOf(param.get("employee_id")));
           String from_date = (String.valueOf(param.get("from_date")));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            try {
                dto.setFromDate(formatter.parse(from_date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dto.setEmployeeId(employee_id);
            dto.setProjectId(project_id);
            dto.setContractNumber(contract_number);
            dto.setAssetsApplNumber(assets_appl_number);
            //dto.setContractStatus(contract_status);
            AssetsDisposal assetsDisposal = service.assetsDisposalCreate(requestContext,dto);
            List<AssetsDisposal> assetsDisposals = new ArrayList<>();
            assetsDisposals.add(assetsDisposal);
            return new ResponseData(assetsDisposals);
        }



        @RequestMapping(value = "/assets/disposal/approval")
        @ResponseBody
        public ResponseData conInceptSubmit(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, final HttpServletRequest request) throws ParameterNullException, ResMessageException {
            IRequest requestContext = createRequestContext(request);
            JSONObject param = (JSONObject) requestData.get("parameter");
            AssetsDisposal assetsDisposal = param.toJavaObject(AssetsDisposal.class);
            service.conInceptSubmit(requestContext, assetsDisposal);
            return new ResponseData();
        }

        @RequestMapping(value = "/assets/disposal/cal")
        @ResponseBody
        public ResponseData cal(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, final HttpServletRequest request) throws ParameterNullException, ResMessageException {
            IRequest requestContext = createRequestContext(request);
            JSONObject param = (JSONObject) requestData.get("parameter");
            AssetsDisposal assetsDisposal = param.toJavaObject(AssetsDisposal.class);
            assetsDisposal.setContractStatus("END");
            service.updateByPrimaryKeySelective(requestContext, assetsDisposal);
            return new ResponseData();
        }

}