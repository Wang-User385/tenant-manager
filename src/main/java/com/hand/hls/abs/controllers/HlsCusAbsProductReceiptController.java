package com.hand.hls.abs.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.abs.dto.HlsCusAbsProduct;
import com.hand.hls.abs.dto.HlsCusAbsProductReceipt;
import com.hand.hls.abs.service.HlsCusAbsProductReceiptService;
import com.hand.hls.abs.service.HlsCusAbsProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class HlsCusAbsProductReceiptController extends BaseController {

    @Autowired
    private HlsCusAbsProductReceiptService service;

    @Autowired
    private HlsCusAbsProductService productService;

    @RequestMapping(value = "/ct/abs/product/receipt/query")
    @ResponseBody
    public ResponseData query(HlsCusAbsProductReceipt dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        if(dto.getProductId()!=null){
            HlsCusAbsProduct product=new HlsCusAbsProduct();
            product.setProductId(dto.getProductId());
            product=productService.selectByPrimaryKey(requestContext,product);
            if(product!=null){
                if(!"NORMAL".equals(product.getDataClass())){
                    dto.setProductId(product.getRefProductId());
                }
            }
        }
        return new ResponseData(service.selectProductReceiptData(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/ct/abs/product/receipt/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<HlsCusAbsProductReceipt> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/ct/abs/product/receipt/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusAbsProductReceipt> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }
}