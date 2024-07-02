package com.hand.hls.fnd.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.fnd.dto.FndInterfaceLines;
import com.hand.hls.fnd.dto.FndWorkCalendar;
import com.hand.hls.fnd.service.FndInterfaceLinesService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2020/4/22
 * @description:
 */
@Controller
public class FndInterfaceLinesController extends BaseController {
    @Autowired
    FndInterfaceLinesService fndInterfaceLinesService;

    @RequestMapping(value = "/fnd/interface/lines/query")
    @ResponseBody
    public ResponseData query(
            FndInterfaceLines fndInterfaceLines,
            @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                              @RequestParam(defaultValue = DEFAULT_PAGE) int pageNum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize,
                              HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject parameter = (JSONObject) requestData.get("parameter");
        FndInterfaceLines dto = parameter.toJavaObject(FndInterfaceLines.class);
        dto = fndInterfaceLines;
        return new ResponseData(fndInterfaceLinesService.fndInterfaceLinesDetailQuery(requestContext,dto,pageNum,pageSize));
    }


}
