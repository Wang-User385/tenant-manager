package com.hand.hls.fin.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fin.dto.HlsCusCtLonContractPledge;
import com.hand.hls.fin.dto.HlsCusLonContractGuarantor;
import com.hand.hls.fin.dto.HlsCusLonContractWithdraw;
import com.hand.hls.fin.service.HlsCusCtLonContractPledgeService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class HlsCusCtLonContractPledgeController extends BaseController {

    @Autowired
    private HlsCusCtLonContractPledgeService service;


    @RequestMapping(value = "/hlsLon/contract/pledge/query")
    @ResponseBody
    public ResponseData query(HlsCusCtLonContractPledge dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectLonContractPledge(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/hlsLon/contract/pledge/submit")
    @ResponseBody
    public ResponseData defaultUpdate(@RequestBody List<HlsCusCtLonContractPledge> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/hlsLon/contract/pledge/remove")
    @ResponseBody
    public ResponseData defaultDelete(HttpServletRequest request, @RequestBody List<HlsCusCtLonContractPledge> dto) {
        IRequest requestCtx = createRequestContext(request);
        service.deleteContractPledge(requestCtx,dto);
        return new ResponseData();
    }

    @RequestMapping(value = "/hlsLon/contract/pledge/queryContract")
    @ResponseBody
    public ResponseData queryContract(HttpServletRequest request, @RequestBody HlsCusCtLonContractPledge dto) {
        return new ResponseData(service.selectContractSurpusAmount(dto));
    }


    @RequestMapping(value = "/hlsLon/contract/pledge/queryTimes")
    @ResponseBody
    public ResponseData queryTimes(HlsCusCtLonContractPledge dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectContractCashFlow(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/hlsLon/contract/pledge/release")
    @ResponseBody
    public ResponseData contractPledgeRelease(@RequestBody List<HlsCusCtLonContractPledge> dto, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        service.contractPledgeRelease(requestCtx, dto);
        return new ResponseData();
    }


    @RequestMapping({"/hls/cus/lon/queryForAmount"})
    @ResponseBody
    public ResponseData queryForAmount(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) {

        IRequest requestCtx = createRequestContext(request);

        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusCtLonContractPledge hlsCusCtLonContractPledge = param.toJavaObject(HlsCusCtLonContractPledge.class);
        return new ResponseData(this.service.queryForAmount(hlsCusCtLonContractPledge));
    }

    /**
     * 校验质押是否重复
     * @param requestData
     * @param request
     */
    @RequestMapping(value = "/hls/cus/lon/queryContractNumber")
    @ResponseBody
    public ResponseData checkContractNumber(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                    HttpServletRequest request){
        IRequest requestContext = createRequestContext(request);
        //System.out.println("合同编号：==================================》"+contractId);
        JSONArray param = (JSONArray) requestData.get("parameter");
//        String contractNumber = param.getString("contract_number");
        List<HlsCusCtLonContractPledge> list = param.toJavaList(HlsCusCtLonContractPledge.class);
        List<HlsCusCtLonContractPledge> resp = service.queryContractNumber(requestContext, list);
        if(resp.size()>0){
            return new ResponseData(false,"合同编号:"+resp.get(0).getPledgeContractNumber()+"重复质押,请确认");
        }else{
            return new ResponseData();
        }
    }
}