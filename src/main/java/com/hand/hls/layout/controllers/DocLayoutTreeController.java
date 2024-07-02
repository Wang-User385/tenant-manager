package com.hand.hls.layout.controllers;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.layout.dto.DocLayoutTree;
import com.hand.hls.layout.dto.LovDocLayoutTreeDto;
import com.hand.hls.layout.mapper.DocLayoutTreeMapper;
import com.hand.hls.layout.service.IDocLayoutTreeService;
import leaf.bean.LeafRequestData;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DocLayoutTreeController extends BaseController {

    @Autowired
    private IDocLayoutTreeService service;

    @Autowired
    private DocLayoutTreeMapper docLayoutTreeMapper;


    @RequestMapping(value = "/hls/doc/layout/tree/query")
    @ResponseBody
    public ResponseData query(DocLayoutTree dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/hls/doc/layout/tree/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<DocLayoutTree> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/hls/doc/layout/tree/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<DocLayoutTree> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }


    @RequestMapping(value = "/hls/doc/layout/tree/code/flag/query")
    @ResponseBody
    public Map queryFlag(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                         final HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        Map parameter = requestData.getParameter();
        if (parameter == null || parameter.get("layout_code") == null) return null;
        DocLayoutTree layoutTree = new DocLayoutTree();
        layoutTree.setLayoutCode((String) parameter.get("layout_code"));
        Map<String, Object> answer = new HashMap<>();
        Map<String, Object> result = new HashMap<>();
        Map map = new HashMap();
        try {
            map = service.queryFlag(layoutTree);
            answer.put("success", true);
        } catch (Exception e) {
            answer.put("success", false);
        }
        result.put("record", map);
        answer.put("result", result);
        return answer;
    }

    @RequestMapping(value = "/hls/doc/layout/tree/lov/query")
    @ResponseBody
    public ResponseData queryForLov(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                    @RequestParam(value = "pagenum", defaultValue = DEFAULT_PAGE) final int page,
                                    @RequestParam(value = "pagesize", defaultValue = DEFAULT_PAGE_SIZE) final int pagesize,
                                    final HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        LovDocLayoutTreeDto lovDocLayoutTreeDto = new LovDocLayoutTreeDto();
        Map parameter = requestData.getParameter();
        if (parameter != null) {
            Object treeCode = parameter.get("treeCode");
            Object parentTreeCode = parameter.get("parentTreeCode");
            Object tabCode = parameter.get("enabledFlag");
            Object treeDesc = parameter.get("treeDesc");

        }
        String layout_code = request.getParameter("layout_code");
        if (StringUtils.isNotBlank(layout_code)) {
            lovDocLayoutTreeDto.setLayoutCode(layout_code);
        }
        String enabled_flag = request.getParameter("enabled_flag");
        lovDocLayoutTreeDto.setEnabledFlag(enabled_flag);

        PageHelper.startPage(page, pagesize);
        return new ResponseData(docLayoutTreeMapper.queryLov(lovDocLayoutTreeDto));
    }
}