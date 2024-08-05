package com.hand.hls.partner.controllers;
import com.hand.hls.partner.dto.YLCshTransferPaymentDto;
import com.hand.hls.partner.service.IYLCshTransferPaymentService;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.alibaba.fastjson.JSONArray;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

@Controller
public class YLCshTransferPaymentController extends BaseController {

    @Resource
    private IYLCshTransferPaymentService service;


    @RequestMapping(value = "/yl/csh/update/transfer/status")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<YLCshTransferPaymentDto> ylCshTransferPaymentDtoList = param.toJavaList(YLCshTransferPaymentDto.class);
        return new ResponseData(service.updateTransferStatus(requestCtx,ylCshTransferPaymentDtoList));
    }


    @RequestMapping(value = "/yl/csh/confirm")
    @ResponseBody
    public ResponseData confirm(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<YLCshTransferPaymentDto> ylCshTransferPaymentDtoList = param.toJavaList(YLCshTransferPaymentDto.class);
        return new ResponseData(service.updateAndVerification(ylCshTransferPaymentDtoList));
    }


}