package com.hand.hls.fct.controllers;

import com.alibaba.fastjson.JSONArray;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChanceBp;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceBpService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.List;

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

    @RequestMapping({ "/get/factoring/bp/info"})
    @ResponseBody
    public ResponseData getFactoringBPInfo(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusHlsCreditLineChanceBp> hlsCusBpMasters = param.toJavaList(HlsCusHlsCreditLineChanceBp.class);
        return new ResponseData(hlsCusHlsCreditLineChanceBpService.getFactoringBPInfo(hlsCusBpMasters));
    }

}
