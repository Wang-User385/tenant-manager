package com.hand.hls.layout.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.layout.dto.DocLayoutButton;
import com.hand.hls.layout.service.IDocLayoutButtonService;
import com.hand.hls.layout.service.IDocLayoutService;
import leaf.bean.LeafRequestData;
import leaf.bm.components.RecordHelper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import uncertain.composite.CompositeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Marshal
 * @date 2019-01-12 10:56
 * @description
 */
@Controller
public class DocLayoutButtonController extends BaseController {

    @Autowired
    IDocLayoutButtonService docLayoutButtonService;
    @Autowired
    private IDocLayoutService docLayoutService;

    private static final String BTN_DEFAULT_PAGESIZE = "30";

    /**
     * 页面布局定义按钮查询
     *
     * @param data
     * @param pagenum
     * @param btnPageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/hls/doc/layout/button/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData data, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = BTN_DEFAULT_PAGESIZE) int btnPageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        DocLayoutButton dto = new DocLayoutButton();
        if (data != null) {
            JSONObject param = (JSONObject) data.get("parameter");
            dto = param.toJavaObject(DocLayoutButton.class);
        }
        return new ResponseData(docLayoutButtonService.select(requestContext, dto, pagenum, btnPageSize));
    }

    /**
     * 页面布局定义按钮保存
     *
     * @param data
     * @param request
     * @return
     */
    @RequestMapping(value = "/hls/doc/layout/button/submit")
    @ResponseBody
    public ResponseData saveLayoutUserButton(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData data, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        if (data.get("parameter") != null && ((List) data.get("parameter")).size() > 0) {
            List<Map> list = ((List) data.get("parameter"));
            for (int i = 0; i < list.size(); i++) {
                if ("insert".equals(list.get(i).get("_status"))) {
                    CompositeMap compositeMap = new CompositeMap("", list.get(i));
                    RecordHelper.insert("hls_doc_layout_button", compositeMap);
                } else if ("update".equals(list.get(i).get("_status"))) {
                    CompositeMap compositeMap = new CompositeMap("", list.get(i));
                    RecordHelper.update("hls_doc_layout_button", compositeMap);
                }
            }
        }
        return new ResponseData();
    }

    /**
     * 页面布局按钮定义删除
     *
     * @param request
     * @param requestData
     * @return
     */
    @RequestMapping(value = "/hls/doc/layout/button/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,
                               @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        List<DocLayoutButton> dto = new ArrayList<>();
        if (requestData != null) {
            JSONArray param = (JSONArray) requestData.get("parameter");
            dto = param.toJavaList(DocLayoutButton.class);
        }
        docLayoutButtonService.batchDelete(dto);
        return new ResponseData(dto);
    }


    /**
     * 加载默认按钮
     *
     * @param request
     * @param functionCode
     * @return
     */
    @RequestMapping("/hls/doc/layout/button/load")
    @ResponseBody
    public ResponseData load(HttpServletRequest request,
                             @RequestParam("functionCode") String functionCode) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        if (StringUtils.isNotEmpty(functionCode)) {
            docLayoutButtonService.load(functionCode);
        }
        return new ResponseData();
    }

    /**
     * 重载按钮
     */
    @RequestMapping("/hls/doc/layout/button/reload")
    @ResponseBody
    public ResponseData reload(HttpServletRequest request,
                               @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData data) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        ResponseData responseData = new ResponseData(true);
        if (data == null || data.getParameterList() == null || data.getParameterList().size() < 1) {
            return new ResponseData(false, "请求参数有误");
        }
        List<DocLayoutButton> listToModify = new ArrayList<>();
        List parameterList = data.getParameterList();
        parameterList.forEach(o -> {
            if (o instanceof JSONObject) {
                JSONObject o1 = (JSONObject) o;
                DocLayoutButton button = o1.toJavaObject(DocLayoutButton.class);
                listToModify.add(button);
            }
        });

        try {
            if (listToModify.size() > 1) {
                docLayoutButtonService.reload(requestContext, listToModify);
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage("操作失败");
        }
        return responseData;
    }

    @RequestMapping(value = "/hls/doc/layout/button/export")
    @ResponseBody
    public ResponseEntity<Resource> export(HttpServletRequest request, @RequestParam("functionCode") String functionCode) {
        return docLayoutService.exportLayoutButton(functionCode);
    }

    @RequestMapping(value = "/hls/doc/layout/button/import")
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
                    docLayoutService.importLayoutButton(content);
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
