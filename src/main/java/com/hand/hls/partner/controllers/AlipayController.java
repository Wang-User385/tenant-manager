package com.hand.hls.partner.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hls.partner.service.IAlipayService;
import hls.core.utils.exception.HlsCusException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = {"/alipay"})
public class AlipayController extends BaseController {

    @Autowired
    private IAlipayService iAlipayService;

    @RequestMapping(value = "/sign")
    @ResponseBody
    public String sign(HttpServletRequest request, @RequestBody Long projectId) throws HlsCusException {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);

        String extInfo = "";//返回值，签约二维码字符串

        String penetrateId = iAlipayService.getPenetrateId(projectId);
        if(StringUtils.isNotEmpty(penetrateId)){
            extInfo = iAlipayService.sign(projectId);
        }

        return extInfo;
    }





}
