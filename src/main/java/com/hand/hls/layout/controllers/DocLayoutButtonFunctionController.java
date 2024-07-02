package com.hand.hls.layout.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.function.dto.Function;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.layout.service.IDocLayoutButtonFunctionService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 *
 */
@Controller
public class DocLayoutButtonFunctionController extends BaseController {
    @Autowired
    IDocLayoutButtonFunctionService service;

    @RequestMapping(value = "/hls/doc/layout/button/function/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                              @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request, HttpSession session) {
        Map parameter = requestData.getParameter();
        Function dto = new Function();
        if (parameter != null) {
            JSONObject param = (JSONObject) parameter;
            dto = param.toJavaObject(Function.class);
        }
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectForFunctionInfo(requestContext, dto, pagenum, pagesize));
    }
}
