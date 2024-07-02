package com.hand.hls.vat.controllers;


import com.alibaba.fastjson.JSONArray;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.vat.dto.AcrInvoiceBill;
import com.hand.hls.vat.exception.AcrInvoiceException;
import com.hand.hls.vat.service.AcrInvoiceBillService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Controller
public class AcrInvoiceBillController extends BaseController {

    @Autowired
    private AcrInvoiceBillService service;

    @RequestMapping(value = "/acr/invoice/bill/save")
    @ResponseBody
    public ResponseData save(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                HttpServletRequest request) throws AcrInvoiceException {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<AcrInvoiceBill> list = param.toJavaList(AcrInvoiceBill.class);
        for (AcrInvoiceBill acrInvoiceBill : list) {
            if (acrInvoiceBill.getBankAccountId() == null) {
                service.insert(requestCtx,acrInvoiceBill);

            }else {
                service.updateByPrimaryKeySelective(requestCtx,acrInvoiceBill);
            }
        }
        return new ResponseData();
    }
}
