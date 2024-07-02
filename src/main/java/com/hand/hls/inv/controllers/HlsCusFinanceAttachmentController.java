package com.hand.hls.inv.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.core.IRequest;
import com.hand.hap.security.TokenUtils;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.inv.dto.HlsCusFinanceAttachment;
import com.hand.hls.inv.dto.HlsCusFinancePurchase;
import com.hand.hls.inv.service.HlsCusIFinanceAttachmentService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HlsCusFinanceAttachmentController extends BaseController {

    @Autowired
    private HlsCusIFinanceAttachmentService service;


    @RequestMapping(value = "/inv/finance/attachment/query")
    @ResponseBody
    public ResponseData query(HlsCusFinanceAttachment dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/inv/finance/attachment/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute("_request_data") LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);

        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusFinanceAttachment dto = param.toJavaObject(HlsCusFinanceAttachment.class);
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }

        if (dto.getAttachmentId() == null && (dto.getStatus() == null || "".equals(dto.getStatus()))) {
            dto.setStatus("NEW");
        }
        List<HlsCusFinanceAttachment> list = new ArrayList<>();
        list.add(dto);
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/inv/finance/attachment/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest requestCtx = createRequestContext(request);

        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusFinanceAttachment dto = param.toJavaObject(HlsCusFinanceAttachment.class);
        List<HlsCusFinanceAttachment> list = new ArrayList<>();
        list.add(dto);
        service.batchDelete(list);
        return new ResponseData();
    }

    @RequestMapping(value = "/inv/finance/attachment/query/info")
    @ResponseBody
    public ResponseData invAttachmentDetailQuery(HlsCusFinanceAttachment dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        List<HlsCusFinanceAttachment> list = service.invAttachmentDetailQuery(requestContext, dto, page, pageSize);
        SysFile file = new SysFile();
        String securityKey = TokenUtils.getSecurityKey(request.getSession());
        for (HlsCusFinanceAttachment f : list) {
            if (f.getFileId() == null || "".equals(f.getFileId())) {

            } else {
                file.setFileId(Long.valueOf(f.getFileId()));
                f.set_token(TokenUtils.generateToken(securityKey, file));
            }
        }
        return new ResponseData(list);
    }

    /**
     * @Description:附件补录工作流
     * @Author: Wty
     * @Date: Created om 17:10 2018/4/26
     */
    @RequestMapping(value = "/inv/finance/purchase/attachment/submit/wfl")
    @ResponseBody
    public ResponseData purchaseAttachmentSubmitWfl(HttpServletRequest request, @RequestBody HlsCusFinanceAttachment dto) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(service.purchaseAttachmentSubmitWfl(iRequest, dto));
    }
}