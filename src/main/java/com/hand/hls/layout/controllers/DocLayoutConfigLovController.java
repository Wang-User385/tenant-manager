package com.hand.hls.layout.controllers;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.layout.dto.DocLayoutConfigLov;
import com.hand.hls.layout.dto.LovDocLayoutConfigDto;
import com.hand.hls.layout.mapper.DocLayoutConfigLovMapper;
import com.hand.hls.layout.service.IDocLayoutConfigLovService;
import leaf.bean.LeafRequestData;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
public class DocLayoutConfigLovController extends BaseController {

    @Autowired
    private IDocLayoutConfigLovService service;

    @Autowired
    private DocLayoutConfigLovMapper docLayoutConfigLovMapper;


    @RequestMapping(value = "/hls/doc/layout/config/lov/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData data,
                              @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        String layoutCode = request.getParameter("layout_code");
        String tabCode = request.getParameter("tab_code");
        LovDocLayoutConfigDto dto = new LovDocLayoutConfigDto();
        if (StringUtils.isBlank(layoutCode) || StringUtils.isBlank(tabCode)) {
            return new ResponseData(false);
        }
        Map parameter = data.getParameter();
        Object column_name = parameter.get("columnName") == null ? parameter.get("column_name") : parameter.get("columnName");
        Object prompt = parameter.get("prompt");
        if (column_name != null) {
            dto.setColumnName(String.valueOf(column_name));
        }
        if (prompt != null) {
            dto.setPrompt(String.valueOf(prompt));
        }
        dto.setLayoutCode(layoutCode);
        dto.setTabCode(tabCode);
        PageHelper.startPage(pagenum, pagesize);
        List<LovDocLayoutConfigDto> list = docLayoutConfigLovMapper.queryLov(dto);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/hls/doc/layout/config/lov/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<DocLayoutConfigLov> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/hls/doc/layout/config/lov/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<DocLayoutConfigLov> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }
}