package com.hand.hls.eft.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.eft.dto.HlsCusFundTransfer;
import com.hand.hls.eft.service.HlsCusFundTransferService;
import com.hand.hls.exception.HlsCusException;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Controller
public class HlsCusFundTransferController extends BaseController {

    @Autowired
    private HlsCusFundTransferService service;


    @RequestMapping(value = "/eft/fund/transfer/query")
    @ResponseBody
    public ResponseData query(HlsCusFundTransfer dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectFundTransferData(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/eft/fund/transfer/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) throws HlsCusException {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusFundTransfer dto = param.toJavaObject(HlsCusFundTransfer.class);
        IRequest requestCtx = createRequestContext(request);
        service.updateFundTransferData(requestCtx, dto);
        return new ResponseData();
    }

    @RequestMapping(value = "/eft/fund/transfer/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusFundTransfer> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }


    /**
     * 资金 查询变更事项
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/eft/fund/transfer/queryChange")
    @ResponseBody
    public ResponseData queryTransferChange(HlsCusFundTransfer dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectFundTransferChangeData(requestContext, dto, page, pageSize));
    }


    /**
     * 资金缺口手动新增调拨
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping(value = "/eft/gap/transfer/create")
    @ResponseBody
    public ResponseData createTransferGap(HlsCusFundTransfer dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(Arrays.asList(service.createFundTransferGap(requestContext, dto)));
    }


    /**
     * 提交审批
     * @param request
     * @return
     * @throws HlsCusException
     */
    @RequestMapping(value = "/eft/fund/transfer/submitApproval")
    @ResponseBody
    public ResponseData submitApprovalFundTransfer(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws  HlsCusException {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusFundTransfer dto = param.toJavaObject(HlsCusFundTransfer.class);
        return new ResponseData(service.submitApprovalFundTransfer(requestCtx,dto));
    }
}