package com.hand.hls.layout.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.layout.dto.DocLayoutTabButton;
import com.hand.hls.layout.service.IDocLayoutTabButtonService;
import leaf.bean.LeafRequestData;
import leaf.bm.components.RecordHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uncertain.composite.CompositeMap;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/hls/doc/layout/tab/button")
public class DocLayoutTabButtonController extends BaseController {

    @Autowired
    private IDocLayoutTabButtonService service;


    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData data,
                              @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        Map map = new HashMap();
        if (data.get("parameter") != null) {
            map = ((Map) data.get("parameter"));
        }
        return new ResponseData(service.selectDocLayoutTabButton(map, page, pageSize));

    }

    @RequestMapping(value = "/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData data, BindingResult result, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        if (data.get("parameter") != null && ((List) data.get("parameter")).size() > 0) {
            List<Map> list = ((List) data.get("parameter"));
            for (int i = 0; i < list.size(); i++) {
                if ("insert".equals(list.get(i).get("_status"))) {
                    CompositeMap compositeMap = new CompositeMap("", list.get(i));
                    RecordHelper.insert("hls_doc_layout_tab_button", compositeMap);
                } else if ("update".equals(list.get(i).get("_status"))) {
                    CompositeMap compositeMap = new CompositeMap("", list.get(i));
                    RecordHelper.update("hls_doc_layout_tab_button", compositeMap);
                }
            }
        }
        return new ResponseData();
    }

    @RequestMapping(value = "/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,
                               @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData data) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        List<DocLayoutTabButton> dto = new ArrayList<>();
        if (data != null) {
            JSONArray param = (JSONArray) data.get("parameter");
            dto = param.toJavaList(DocLayoutTabButton.class);
        }
        service.batchDelete(dto);
        return new ResponseData(dto);
    }

    @RequestMapping(value = "/execute")
    @ResponseBody
    public ResponseData execute(HttpServletRequest request,
                                @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData data) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        DocLayoutTabButton docLayoutTabButton = new DocLayoutTabButton();
        Map parameter = data.getParameter();
        Object functionCode = parameter.get("function_code");
        Object layoutCode = parameter.get("layout_code");
        Object tabCode = parameter.get("tab_code");
        ResponseData responseData = new ResponseData();
        if (functionCode == null || layoutCode == null || tabCode == null) {
            responseData.setSuccess(false);
            responseData.setMessage("请求参数有误");
            return responseData;
        }
        docLayoutTabButton.setLayoutCode(String.valueOf(layoutCode));
        docLayoutTabButton.setTabCode(String.valueOf(tabCode));
        docLayoutTabButton.setFunctionCode(String.valueOf(functionCode));
        return service.tabButtonConfigLoad(docLayoutTabButton);
    }

    @RequestMapping(value = "/reload")
    @ResponseBody
    public ResponseData reload(HttpServletRequest request,
                               @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData data) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        DocLayoutTabButton docLayoutTabButton = new DocLayoutTabButton();
        List parameterList = data.getParameterList();
        List<DocLayoutTabButton> listToReload = new ArrayList<>();

        parameterList.forEach(o -> {
            if (o instanceof JSONObject) {
                JSONObject o1 = (JSONObject) o;
                DocLayoutTabButton button = o1.toJavaObject(DocLayoutTabButton.class);
                listToReload.add(button);
            }
        });

        return service.tabButtonConfigReload(listToReload);
    }
}