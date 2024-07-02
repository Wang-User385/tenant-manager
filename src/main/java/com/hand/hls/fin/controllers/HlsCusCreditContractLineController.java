package com.hand.hls.fin.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fin.dto.HlsCusCreditContractLine;
import com.hand.hls.fin.service.HlsCusCreditContractLineService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class HlsCusCreditContractLineController extends BaseController {

    @Autowired
    private HlsCusCreditContractLineService service;


    @RequestMapping(value = "/lon/credit/contract/line/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusCreditContractLine dto = param.toJavaObject(HlsCusCreditContractLine.class);
        IRequest requestContext = createRequestContext(request);

        if ("Y".equals(dto.getIsCarryFlag())) {
            if (dto.getCreditContractId() != null && (dto.getCreditContractId().equals((long) -1) || dto.getCreditContractId().equals(0L))) {
                dto.setCreditContractId(null);
            }
            return new ResponseData(service.selectCreditLineCarry(requestContext, dto, page, pageSize));
        }
        return new ResponseData(service.selectCreditContractLine(requestContext, dto, page, pageSize));
    }


}