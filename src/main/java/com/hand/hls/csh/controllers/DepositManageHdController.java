package com.hand.hls.csh.controllers;

import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.csh.dto.DepositManageHd;
import com.hand.hls.csh.service.IDepositManageHdService;
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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

    @Controller
    public class DepositManageHdController extends BaseController{

    @Autowired
    private IDepositManageHdService iDepositManageHdService;


        /*保证金管理查询*/
        @RequestMapping(value = "/csh/deposit/manage/hd/queryByField")
        @ResponseBody
        public ResponseData selectCreditLineChanceByStatus(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request, HttpServletResponse response, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) throws ParseException {
            JSONObject param = (JSONObject) requestData.get("parameter");

            String sortName = null;
            String sortOrder = null;
            if (param.get("sort_name") != null) {
                sortName = param.get("sort_name").toString();
            }
            if (param.get("sort_name") != null) {
                sortOrder = param.get("sort_order").toString();
            }

            DepositManageHd dto = param.toJavaObject(DepositManageHd.class);
            IRequest requestCtx = createRequestContext(request);
            return new ResponseData(iDepositManageHdService.selectDepositManageByField(requestCtx, dto, pagenum, pagesize, sortName, sortOrder));
        }

        /**
         * 保证金代付提交和工作流开始事件
         */
        @RequestMapping(value = "/csh/deposit/manage/hd/submitApproval")
        @ResponseBody
        public ResponseData submitApproval(HttpServletRequest request, @RequestBody DepositManageHd dto) throws Exception {
            try {
                IRequest requestCtx = createRequestContext(request);
                DepositManageHd cusHlsCreditLineChance = iDepositManageHdService.submitApproval(requestCtx, dto,"BZJ_DFQZZJ");
                List<DepositManageHd> cusHlsCreditLineChanceList = iDepositManageHdService.selectManageHdById1(cusHlsCreditLineChance);
                return new ResponseData(cusHlsCreditLineChanceList);
            } catch (IllegalArgumentException e) {
                ResponseData error = new ResponseData(false);
                error.setMessage(e.getMessage());
                return error;
            }
        }

        /**
         * 保证金处理方式变更提交和工作流开始事件
         */
        @RequestMapping(value = "/csh/deposit/manage/hd/changeSubmitApproval")
        @ResponseBody
        public ResponseData changeSubmitApproval(HttpServletRequest request, @RequestBody DepositManageHd dto) throws Exception {
            try {
                IRequest requestCtx = createRequestContext(request);
                DepositManageHd cusHlsCreditLineChance = iDepositManageHdService.submitApproval(requestCtx, dto,"BZJ_CLFSBG");
                List<DepositManageHd> cusHlsCreditLineChanceList = iDepositManageHdService.selectManageHdById1(cusHlsCreditLineChance);
                return new ResponseData(cusHlsCreditLineChanceList);
            } catch (IllegalArgumentException e) {
                ResponseData error = new ResponseData(false);
                error.setMessage(e.getMessage());
                return error;
            }
        }
        /**
         * 保证金处理方式变更重新计算变更现金流
         */
        @RequestMapping(value = "/csh/deposit/manage/hd/changeCashflow")
        @ResponseBody
        public ResponseData changeCashflow(HttpServletRequest request, @RequestBody DepositManageHd dto) throws Exception {
            try {
                IRequest requestCtx = createRequestContext(request);
                DepositManageHd cusHlsCreditLineChance = iDepositManageHdService.changeCashflow(requestCtx, dto);
//                List<DepositManageHd> cusHlsCreditLineChanceList = iDepositManageHdService.selectManageHdById1(cusHlsCreditLineChance);
                List<DepositManageHd> cusHlsCreditLineChanceList = new ArrayList<>();
                cusHlsCreditLineChanceList.add(cusHlsCreditLineChance);
                return new ResponseData(cusHlsCreditLineChanceList);
            } catch (IllegalArgumentException e) {
                ResponseData error = new ResponseData(false);
                error.setMessage(e.getMessage());
                return error;
            }
        }

        /**
         * 保证金处理方式变更创建变更合同和现金流数据
         */
        @RequestMapping(value = "/csh/con/manage/change/req/submit")
        @ResponseBody
        public ResponseData conManageChangeReqSubmit(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws Exception {
            try {
                IRequest requestCtx = createRequestContext(request);
                JSONObject param = (JSONObject) requestData.get("parameter");
                DepositManageHd depositManageHd = param.toJavaObject(DepositManageHd.class);
                DepositManageHd depositManageHd2 = iDepositManageHdService.changeCreate(requestCtx, depositManageHd);
                List<DepositManageHd> cusHlsCreditLineChanceList = new ArrayList<>();
                cusHlsCreditLineChanceList.add(depositManageHd2);
                return new ResponseData(cusHlsCreditLineChanceList);
            } catch (IllegalArgumentException e) {
                ResponseData error = new ResponseData(false);
                error.setMessage(e.getMessage());
                return error;
            }
        }

        /**
         * 保证金代付期中租金 校验 不能重复创建
         */
        @RequestMapping(value = "/csh/con/manage/change/req/check")
        @ResponseBody
        public ResponseData conManageChangeReqCheck(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws Exception {
            try {
                IRequest requestCtx = createRequestContext(request);
                JSONObject param = (JSONObject) requestData.get("parameter");
                DepositManageHd depositManageHd = param.toJavaObject(DepositManageHd.class);
                DepositManageHd depositManageHd2 = iDepositManageHdService.changeCheck(requestCtx, depositManageHd);
                List<DepositManageHd> cusHlsCreditLineChanceList = new ArrayList<>();
                cusHlsCreditLineChanceList.add(depositManageHd2);
                return new ResponseData(cusHlsCreditLineChanceList);
            } catch (IllegalArgumentException e) {
                ResponseData error = new ResponseData(false);
                error.setMessage(e.getMessage());
                return error;
            }
        }


    @RequestMapping(value = "/csh/deposit/manage/hd/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        DepositManageHd dto = param.toJavaObject(DepositManageHd.class);
        return new ResponseData(iDepositManageHdService.select(requestContext,dto,pagenum,pagesize));
    }

    @RequestMapping(value = "/csh/deposit/manage/hd/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<DepositManageHd> list = param.toJavaList(DepositManageHd.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(iDepositManageHdService.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/csh/deposit/manage/hd/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<DepositManageHd> dto = parameter.toJavaList(DepositManageHd.class);
        iDepositManageHdService.batchDelete(dto);
        return new ResponseData(dto);
    }
}