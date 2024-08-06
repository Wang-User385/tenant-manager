package com.hand.hls.partner.controllers;
import cfca.paperless.base.util.StringUtil;
import com.alibaba.fastjson.JSONObject;
import com.hand.hls.partner.dto.YLCshTransferPaymentDto;
import com.hand.hls.partner.service.IYLCshTransferPaymentService;
import hls.core.utils.exception.HlsCusException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.alibaba.fastjson.JSONArray;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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
        JSONObject param = (JSONObject) requestData.get("parameter");
        YLCshTransferPaymentDto ylCshTransferPaymentDto = param.toJavaObject(YLCshTransferPaymentDto.class);
        String paymentIdStr = ylCshTransferPaymentDto.getPaymentIdStr();
        if (paymentIdStr != null && !"".equals(paymentIdStr)) {
            List<Long> paymentIds = new ArrayList<>();
            String[] str = paymentIdStr.split(",");
            for (int i = 0; i < str.length; i++) {
                if (!"undefined".equals(str[i])) {
                    paymentIds.add(Long.parseLong(str[i]));
                }
            }
            if (paymentIds.size() > 0) {
                ylCshTransferPaymentDto.setPaymentIdS(paymentIds);
            }
        }
        List<Long> paymentIdS = ylCshTransferPaymentDto.getPaymentIdS();
        List<YLCshTransferPaymentDto> res = new ArrayList<>();
        paymentIdS.forEach(paymentId->{
            YLCshTransferPaymentDto dto= new YLCshTransferPaymentDto();
            dto.setPaymentId(paymentId);
            dto = service.selectByPrimaryKey(requestCtx,dto);
            if (!StringUtils.isEmpty(ylCshTransferPaymentDto.getExtraBankStatement())){
                dto.setBankStatement(ylCshTransferPaymentDto.getExtraBankStatement());
            }
            res.add(dto);
        });
        service.updateAndVerification(requestCtx,request,res);
        return new ResponseData();
    }


}