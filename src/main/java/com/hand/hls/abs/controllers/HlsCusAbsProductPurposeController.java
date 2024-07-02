package com.hand.hls.abs.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.abs.dto.HlsCusAbsProductPurpose;
import com.hand.hls.abs.service.HlsCusAbsProductPurposeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 资金用途
 * </p>
 *
 * @author yuanyuan 2019/04/16 10:22 AM
 */
@Controller
public class HlsCusAbsProductPurposeController extends BaseController {

    @Autowired
    private HlsCusAbsProductPurposeService service;


    /**
     * 查询
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/ct/abs/product/purpose/query")
    @ResponseBody
    public ResponseData query(HlsCusAbsProductPurpose dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectAbsProductPurpose(requestContext, dto, page, pageSize));
    }

    /**
     * 保存
     * @param dto
     * @param result
     * @param request
     * @return
     */
    @RequestMapping(value = "/ct/abs/product/purpose/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<HlsCusAbsProductPurpose> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    /**
     * 删除
     * @param request
     * @param dto
     * @return
     */
    @RequestMapping(value = "/ct/abs/product/purpose/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusAbsProductPurpose> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }
}