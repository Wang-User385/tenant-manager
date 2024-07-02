package com.hand.hls.csh.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.csh.dto.CshAllocation;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.service.ICshAllocationService;
import com.hand.hls.utils.ResMessageException;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CshAllocationController extends BaseController {

    @Autowired
    private ICshAllocationService service;


    @RequestMapping(value = "/csh/allocation/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        String sortName=null;
        String sortOrder=null;
        if(param.get("sort_name")!=null){
            sortName = param.get("sort_name").toString();
        }
        if(param.get("sort_name")!=null){
            sortOrder=param.get("sort_order").toString();
        }

        CshAllocation dto = param.toJavaObject(CshAllocation.class);
        String allocationIdStr = dto.getAllocationIdStr();

        if (allocationIdStr != null && !"".equals(allocationIdStr)) {
            List<Long> allocationIdS = new ArrayList<>();

            String[] str = allocationIdStr.split(",");

            for (int i = 0; i < str.length; i++) {
                if (!"undefined".equals(str[i])) {
                    allocationIdS.add(Long.parseLong(str[i]));
                }
            }
            if (allocationIdS.size() > 0)  {
                dto.setAllocationIdS(allocationIdS);
            }
        }

        return new ResponseData(service.allocationQuery(requestContext, dto, pagenum, pagesize,sortName,sortOrder));
    }

    @RequestMapping(value = "/csh/allocation/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<CshAllocation> list = param.toJavaList(CshAllocation.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/csh/allocation/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<CshAllocation> dto = parameter.toJavaList(CshAllocation.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }

    @RequestMapping(value = "/csh/allocation/match")
    @ResponseBody
    public ResponseData allocationMatch(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) throws ParseException, ResMessageException {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusCshTransaction dto = param.toJavaObject(HlsCusCshTransaction.class);

        String transactionIdStr = dto.getTransactionIdStr();
        List<CshAllocation> cshAllocations = service.autoAllocation(requestCtx, transactionIdStr);
        if(cshAllocations.size() == 0){
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage("沒有匹配到符合条件的债权");
            return responseData;
        }
        return new ResponseData(cshAllocations);
    }
}