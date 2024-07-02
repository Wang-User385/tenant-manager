package com.hand.hls.fct.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChanceBp;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceBpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

/**
 * @author Qian Yuanfeng
 * @date 2020/5/2 - 15:14
 */
@Controller
public class HlsCusHlsCreditLineChanceBpController  extends BaseController {

    @Autowired
    private HlsCusHlsCreditLineChanceBpService hlsCusHlsCreditLineChanceBpService;
    @RequestMapping(value = "/hls/credit/bp/market")
    @ResponseBody
    public ResponseData selectCreditLineC(HlsCusHlsCreditLineChanceBp hlsCusHlsCreditLineChanceBp, HttpServletRequest request) throws ParseException {
        IRequest requestCtx = createRequestContext(request);

        return new ResponseData(hlsCusHlsCreditLineChanceBpService.selectBpByMarket( requestCtx  ,  hlsCusHlsCreditLineChanceBp));
    }

}
