package com.hand.hls.layout.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.layout.dto.DocLayoutTabButton;
import com.hand.hls.layout.service.IDocLayoutTabButtonService;
import leaf.bean.LeafRequestData;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/hls/doc/layout/tab/button/function")
public class DocLayoutTabButtonFunctionController extends BaseController {

    @Autowired
    private IDocLayoutTabButtonService service;


    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData data,
                              @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        DocLayoutTabButton param = new DocLayoutTabButton();
        String layoutCode = request.getParameter("layout_code");
        if (StringUtils.isEmpty(layoutCode)) {
            return new ResponseData(false);
        }
        param.setLayoutCode(layoutCode);
        Map parameter = data.getParameter();
        if (parameter != null) {
            Object functionCode = parameter.get("function_code");
            Object functionName = parameter.get("function_name");
            Object tabCode = parameter.get("tab_code");
            Object tabDesc = parameter.get("tab_desc");
            if (functionCode != null) {
                param.setFunctionCode(String.valueOf(functionCode));
            }
            if (functionName != null) {
                param.setFunctionName(String.valueOf(functionName));
            }
            if (tabCode != null) {
                param.setTabCode(String.valueOf(tabCode));
            }
            if (tabDesc != null) {
                param.setTabDesc(String.valueOf(tabDesc));
            }
        }
        List<DocLayoutTabButton> list = service.queryDocLayoutTabButton(param, pagenum, pagesize);
        return new ResponseData(list);
    }


}