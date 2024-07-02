package com.hand.hls.gld.controllers;

import com.hand.hls.gld.dto.HlsCusFinIncomePkg;
import com.hand.hls.gld.dto.HlsCusGldLonContractFinCost;
import com.hand.hls.gld.service.HlsCusCtDocumentFinIncomeService;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.gld.dto.LonContractFinCost;
import com.hand.hls.gld.service.ILonContractFinCostService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

@Controller
public class LonContractFinCostController extends BaseController{

    @Autowired
    private ILonContractFinCostService service;

    @Autowired
    private HlsCusCtDocumentFinIncomeService hlsCusCtDocumentFinIncomeService;

    @RequestMapping(value = "/gld/lon/contract/fin/cost/query")
    @ResponseBody
    public ResponseData queryLonContractFinCost(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                                @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                                @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,
                                                HttpServletRequest request) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        if (param == null) {
            return new ResponseData(false, "请求参数缺失!");
        }
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        LonContractFinCost dto = param.toJavaObject(LonContractFinCost.class);
        return new ResponseData(service.queryLonContractFinCost(iRequest,dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/gld/lon/contract/fin/cost/cf/item/query")
    @ResponseBody
    public ResponseData queryCfItemForComb(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int pageSize,
                                           LonContractFinCost dto, HttpServletRequest request) {
        IRequest requestContext = this.createRequestContext(request);
        return new ResponseData(this.service.queryCfItemForComb(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/gld/lon/contract/fin/cost/submit")
    @ResponseBody
    public ResponseData updateContractFinanceIncome(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                                    HttpServletRequest request,BindingResult result) {
        JSONArray jsonArray = (JSONArray)requestData.get("parameter");
        List<HlsCusGldLonContractFinCost> dto = jsonArray.toJavaList(HlsCusGldLonContractFinCost.class);
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        List<HlsCusGldLonContractFinCost> incomes = new ArrayList<>();
        for (HlsCusGldLonContractFinCost dt : dto) {
            dt = service.queryLonFinCostByKey(requestCtx, dt);
            incomes.add(dt);
        }
        HlsCusFinIncomePkg hlsCusFinIncomePkg = new HlsCusFinIncomePkg();
        hlsCusFinIncomePkg.setHlsCusGldLonContractFinCostList(incomes);
        hlsCusFinIncomePkg.setFinType("LON_CONTRACT");
        hlsCusCtDocumentFinIncomeService.insertCtFin(requestCtx, hlsCusFinIncomePkg);
        return new ResponseData(dto);
    }

    @RequestMapping(value = "/gld/month/fin/cost/query")
    @ResponseBody
    public ResponseData monthFinCostQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                          @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                          @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,
                                          HttpServletRequest request) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        if (param == null) {
            return new ResponseData(false, "请求参数缺失!");
        }
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        HlsCusGldLonContractFinCost dto = param.toJavaObject(HlsCusGldLonContractFinCost.class);
        return new ResponseData(service.monthFinCostQuery(iRequest, dto, pagenum, pagesize));
    }
}