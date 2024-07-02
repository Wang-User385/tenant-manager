package com.hand.hls.abs.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.abs.dto.HlsCusAbsProductStructure;
import com.hand.hls.abs.service.HlsCusAbsProductStructureService;
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
public class HlsCusAbsProductStructureController extends BaseController {

    @Autowired
    private HlsCusAbsProductStructureService productStructureService;


    @RequestMapping(value = "/ct/abs/product/structure/query")
    @ResponseBody
    public ResponseData query(HlsCusAbsProductStructure dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(productStructureService.selectProductStructureData(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/ct/abs/product/structure/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<HlsCusAbsProductStructure> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(productStructureService.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/ct/abs/product/structure/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusAbsProductStructure> dto) {
        if(productStructureService.selectStructureQuoteCount(dto.get(0))>0){
            ResponseData rd=new ResponseData(false);
            rd.setMessage("资产转让款项收款确认已引用该分层，请先删除！");
            return rd;
        }
        productStructureService.batchDelete(dto);
        return new ResponseData();
    }
}