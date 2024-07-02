package com.hand.hls.gld.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.gld.dto.GldFinanceIncomeDay;
import com.hand.hls.gld.service.IGldFinanceIncomeDayInterfaceService;
import com.hand.hls.gld.service.IGldFinanceIncomeDayService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@Controller
public class GldFinanceIncomeDayController extends BaseController {

    @Autowired
    private IGldFinanceIncomeDayService service;

    @Autowired
    private IGldFinanceIncomeDayInterfaceService iGldFinanceIncomeDayInterfaceService;


    @RequestMapping(value = "/gld/finance/income/day/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        GldFinanceIncomeDay dto = param.toJavaObject(GldFinanceIncomeDay.class);
        return new ResponseData(service.select(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/gld/finance/income/day/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<GldFinanceIncomeDay> list = param.toJavaList(GldFinanceIncomeDay.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/gld/finance/income/day/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<GldFinanceIncomeDay> dto = parameter.toJavaList(GldFinanceIncomeDay.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }


    @RequestMapping(value = "/gld/finance/income/day/sharing")
    @ResponseBody
    public ResponseData sharing(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws Exception {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);

        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusConContract> list = param.toJavaList(HlsCusConContract.class);
        HashMap map = new HashMap();
        if (list.size() > 0) {
            map.put("shareType", list.stream().findFirst().get().getShareType());
            map.put("bizType", list.stream().findFirst().get().getBizType());
            map.put("cfItem", list.stream().findFirst().get().getCfItem());
            iGldFinanceIncomeDayInterfaceService.start(iRequest, list, map);
        }
        return new ResponseData();
    }



    @RequestMapping(value = "/gld/finance/income/day/report")
    @ResponseBody
    public ResponseData reportQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        GldFinanceIncomeDay dto = param.toJavaObject(GldFinanceIncomeDay.class);
        return new ResponseData(service.reportQuery(requestContext, dto, pagenum, pagesize));
    }

}