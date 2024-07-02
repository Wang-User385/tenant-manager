package com.hand.hls.layout.controllers;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.layout.dto.DocLayoutTab;
import com.hand.hls.layout.dto.LovDocLayoutTabDto;
import com.hand.hls.layout.mapper.DocLayoutTabMapper;
import com.hand.hls.layout.service.IDocLayoutTabService;
import com.hand.hls.utils.DbUtils;
import leaf.bean.LeafRequestData;
import leaf.bm.components.RecordHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import uncertain.composite.CompositeMap;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/hls/doc/layout/tab")
public class DocLayoutTabController extends BaseController {

    @Autowired
    private IDocLayoutTabService service;
    @Autowired
    private DocLayoutTabMapper docLayoutTabMapper;


    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData data,
                              @RequestParam(name = "layout_code", required = false) String layoutCode,
                              @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize,
                              HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        Map map = new HashMap();
        if (data.get("parameter") != null) {
            map = ((Map) data.get("parameter"));
        }
        if (layoutCode != null) {
            map.put("layout_code", layoutCode);
        }
        return new ResponseData(service.selectDocLayoutTab(map, page, pageSize));
    }

    @RequestMapping(value = "/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData data, BindingResult result, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        if (data.get("parameter") != null && ((List) data.get("parameter")).size() > 0) {
            List<Map> list = ((List) data.get("parameter"));
            for (int i = 0; i < list.size(); i++) {
                CompositeMap compositeMap = new CompositeMap("", list.get(i));
                String o = String.valueOf(compositeMap.get("base_table"));
                if (StringUtils.isNotBlank(o)) {
                    compositeMap.put("base_table_pk", DbUtils.getPkFieldByTableName(o));
                }
                if ("insert".equals(list.get(i).get("_status"))) {
                    RecordHelper.insert("hls_doc_layout_tab", compositeMap);
                } else if ("update".equals(list.get(i).get("_status"))) {
                    RecordHelper.update("hls_doc_layout_tab", compositeMap);
                }
            }
        }
        return new ResponseData();
    }

    @RequestMapping(value = "/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<DocLayoutTab> dto) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        ResponseData result = new ResponseData(true);
        try {
            service.deleteDocLayoutTab(dto);
        } catch (Exception e) {
            result.setSuccess(false);
        }
        return result;
    }

    @RequestMapping(value = "/load")
    @ResponseBody
    public ResponseData load(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest requestContext = createRequestContext(request);
        ResponseData result = new ResponseData(true);
        Map parameter = requestData.getParameter();
        try {
            DocLayoutTab docLayoutTab = new DocLayoutTab();
            docLayoutTab.setLayoutCode(String.valueOf(parameter.get("layout_code")));
            docLayoutTab.setTabCode(String.valueOf(parameter.get("tab_code")));
            service.loadDocLayoutTab(requestContext, docLayoutTab);
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
        }
        return result;
    }

    @RequestMapping(value = "/batchTab")
    @ResponseBody
    public ResponseData queryBatchTab(@RequestParam(value = "pagenum", defaultValue = DEFAULT_PAGE) final int page,
                                      @RequestParam(value = "pagesize", defaultValue = DEFAULT_PAGE_SIZE) final int pagesize,
                                      final HttpServletRequest request, LovDocLayoutTabDto lovDocLayoutTabDto,
                                      @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        String layoutCode = request.getParameter("layout_code");
        PageHelper.startPage(page, pagesize);
        if (StringUtils.isNotBlank(layoutCode)) {
            lovDocLayoutTabDto.setLayoutCode(layoutCode);
        }
        List<LovDocLayoutTabDto> list = docLayoutTabMapper.queryBatchTab(lovDocLayoutTabDto);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/copy")
    @ResponseBody
    public ResponseData tabCopy(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData data, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        Map parameter = data.getParameter();
        ResponseData responseData = new ResponseData(true);
        if (parameter == null
                || parameter.get("to_layout_code") == null || parameter.get("from_layout_code") == null
                || parameter.get("from_tab_code") == null || parameter.get("to_tab_code") == null
                || parameter.get("to_tab_code_desc") == null || parameter.get("tab_only_flag") == null) {
            responseData.setSuccess(false);
            responseData.setMessage("请求参数有误！");
        }
        try {
            String toLayoutCode = parameter.get("to_layout_code").toString();
            String fromLayoutCode = parameter.get("from_layout_code").toString();
            String fromTabCode = parameter.get("from_tab_code").toString();
            String toTabCode = parameter.get("to_tab_code").toString();
            String toTabCodeDesc = parameter.get("to_tab_code_desc").toString();
            String tabOnlyFlag = parameter.get("tab_only_flag").toString();
            String parentTabCode = (String) parameter.get("parent_tab_code");
            return service.layoutTabCopy(requestContext, fromLayoutCode, toLayoutCode, fromTabCode, toTabCode, toTabCodeDesc, tabOnlyFlag, parentTabCode);
        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage("复制失败，请联系管理员！");
        }
        return responseData;
    }
}