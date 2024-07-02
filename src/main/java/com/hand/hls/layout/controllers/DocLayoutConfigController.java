package com.hand.hls.layout.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.layout.dto.DocLayoutConfig;
import com.hand.hls.layout.service.IDocLayoutConfigService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DocLayoutConfigController extends BaseController {

    private static final String DEFAULT_COL_PAGE_SIZE = "30";

    @Autowired
    private IDocLayoutConfigService service;


    @RequestMapping(value = "/hls/doc/layout/config/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData data, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_COL_PAGE_SIZE) int colPageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        Map map = new HashMap();
        if (data.get("parameter") != null) {
            map = ((Map) data.get("parameter"));
        }
        return new ResponseData(service.selectDocLayoutConfig(map, page, colPageSize));
    }

    @RequestMapping(value = "/hls/doc/layout/config/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData data, BindingResult result, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        return service.updateConfig(data);
    }


    @RequestMapping(value = "/hls/doc/layout/config/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<DocLayoutConfig> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    @RequestMapping(value = "/hls/doc/layout/config/load")
    @ResponseBody
    public ResponseData reload(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData data, BindingResult result, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        Object layout_code = request.getAttribute("layout_code");
        Object config_id = request.getAttribute("config_id");
        if (layout_code == null || config_id == null) return new ResponseData(false);
        DocLayoutConfig config = new DocLayoutConfig();
        config.setLayoutCode(String.valueOf(layout_code));
        config.setConfigId((Long) config_id);
        return new ResponseData(service.configReload(config));
    }

    @RequestMapping(value = "/hls/doc/layout/config/field")
    @ResponseBody
    public ResponseData field(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData data, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        return service.resolveConfigField(data);
    }
}