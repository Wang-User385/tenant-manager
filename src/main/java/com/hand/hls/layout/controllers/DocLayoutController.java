package com.hand.hls.layout.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.layout.dto.DocLayout;
import com.hand.hls.layout.service.IDocLayoutService;
import leaf.bean.LeafRequestData;
import leaf.bm.components.RecordHelper;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Controller
public class DocLayoutController extends BaseController {

    @Autowired
    private IDocLayoutService service;


    @RequestMapping(value = "/hls/doc/layout/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData data, DocLayout dto, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        Map map = new HashMap();
        if (data.get("parameter") != null) {
            map = ((Map) data.get("parameter"));
        }
        String layout_code = request.getParameter("layout_code");
        map.put("layout_code", layout_code);
        return new ResponseData(service.selectDocLayout(map, pagenum, pageSize));
    }

    @RequestMapping(value = "/hls/doc/layout/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData data, BindingResult result, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        if (data.get("parameter") != null && ((List) data.get("parameter")).size() > 0) {
            List<Map> list = ((List<Map>) data.get("parameter"));
            RecordHelper.batchUpdate("hls_doc_layout", list);
        }
        return new ResponseData();
    }

    @RequestMapping(value = "/hls/doc/layout/copy")
    @ResponseBody
    public ResponseData layoutCopy(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData data, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        Map parameter = data.getParameter();
        ResponseData responseData = new ResponseData(true);
        if (parameter == null || parameter.get("to") == null || parameter.get("from") == null) {
            return new ResponseData(false, "请求参数有误");
        }
        try {
            String to = parameter.get("to").toString();
            String from = parameter.get("from").toString();
            String desc = parameter.get("to_desc").toString();
            return service.layoutCopy(requestContext, from, to, desc);
        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage("复制失败，请联系管理员！");
        }
        return responseData;
    }

    @RequestMapping(value = "/hls/doc/layout/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<DocLayout> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    @RequestMapping(value = "/hls/doc/layout/export")
    @ResponseBody
    public ResponseEntity<Resource> export(HttpServletRequest request, DocLayout layout) {
        return service.exportLayout(layout);
    }

    @RequestMapping("/hls/doc/layout/import")
    @ResponseBody
    public ResponseData importLayout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        ResponseData responseData = new ResponseData(false);
        if (!multipartResolver.isMultipart(request)) {
            return responseData;
        }
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
        Iterator iter = multiRequest.getFileNames();
        try {
            while (iter.hasNext()) {
                MultipartFile file = multiRequest.getFile(iter.next().toString());
                if (file != null) {
                    String content = IOUtils.toString(file.getInputStream());
                    service.importLayout(content);
                }
            }
        } catch (Exception e) {
            responseData.setMessage(e.getMessage());
            return responseData;
        }
        responseData.setSuccess(true);
        return responseData;
    }


}