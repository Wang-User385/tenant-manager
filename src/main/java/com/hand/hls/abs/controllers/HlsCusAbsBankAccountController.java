package com.hand.hls.abs.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.abs.dto.HlsCusAbsBankAccount;
import com.hand.hls.abs.service.HlsCusAbsBankAccountService;
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
public class HlsCusAbsBankAccountController extends BaseController {

    @Autowired
    private HlsCusAbsBankAccountService service;


    @RequestMapping(value = "/ct/abs/bank/account/query")
    @ResponseBody
    public ResponseData query(HlsCusAbsBankAccount dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectAbsBankAccountData(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/ct/abs/bank/account/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<HlsCusAbsBankAccount> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/ct/abs/bank/account/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusAbsBankAccount> dto) {
        IRequest requestCtx = createRequestContext(request);
        if(service.selectOtherDataQuoteCount(requestCtx,dto.get(0))>0){
            ResponseData rd=new ResponseData(false);
            rd.setMessage("资产转让款项收款确认已引用该账户，请先删除！");
            return rd;
        }
        service.batchDelete(dto);
        return new ResponseData();
    }


    /**
     * 查询账户主体（去重）
     * @param request
     * @param dto
     * @return
     */
    @RequestMapping(value = "/ct/abs/bank/account/company")
    @ResponseBody
    public ResponseData queryAbsCompany(HttpServletRequest request, HlsCusAbsBankAccount dto) {
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.selectDistinctAbsCompany(requestCtx,dto));
    }
}