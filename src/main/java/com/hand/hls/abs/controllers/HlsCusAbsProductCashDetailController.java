package com.hand.hls.abs.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.abs.service.HlsCusAbsProductCashDetailService;
import com.hand.hls.exception.HlsCusException;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class HlsCusAbsProductCashDetailController extends BaseController {

    @Autowired
    private HlsCusAbsProductCashDetailService service;

    //兑付计划导入
    @RequestMapping(value = "/hls/cus/ct/abs/product/cash/import", method = RequestMethod.POST)
    public Map<String, Object> lonContractRepaymentImport(HttpServletRequest request, Long headerId , Long productId) throws IOException {
        IRequest iRequest = createRequestContext(request);
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("success", false);
        try {
            service.ctAbsProductCashImport(iRequest, headerId ,productId);
            response.put("message", "导入成功");
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "导入失败！" + e.getMessage());
        }
        return response;
    }

    //确认兑付
    @RequestMapping(value = "/hls/cus/ct/abs/product/cash/confirm")
    @ResponseBody
    public ResponseData confirm(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData , HttpServletRequest request) throws HlsCusException {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        if (param.get("cash_detail_id") != null) {
            Long cash_detail_id = Long.parseLong(param.get("cash_detail_id").toString());
            service.ctAbsProductCashConfirm(requestCtx,cash_detail_id);
        }
        return new ResponseData();
    }
}