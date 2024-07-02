package com.hand.hls.fct.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fct.dto.HlsCusCreditChanceFinStatement;
import com.hand.hls.fct.service.HlsCusCreditChanceFinStatementService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class HlsCusCreditChanceFinStatementController extends BaseController {

    @Autowired
    private HlsCusCreditChanceFinStatementService service;


    @RequestMapping(value = "/credit/chance/fin/statement/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request, HttpServletResponse response, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) {
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusCreditChanceFinStatement hlsCusCreditChanceFinStatement = param.toJavaObject(HlsCusCreditChanceFinStatement.class);
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectByChanceId(requestContext, hlsCusCreditChanceFinStatement, pagenum, pagesize));
    }
}
