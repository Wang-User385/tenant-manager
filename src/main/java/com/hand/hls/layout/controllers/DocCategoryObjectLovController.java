package com.hand.hls.layout.controllers;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.layout.dto.LovDocCategoryDbObjectDto;
import com.hand.hls.layout.mapper.DocCategoryDbObjectMapper;
import leaf.bean.LeafRequestData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Author: Eric Chen
 * @Email : qiang.chen04@hand-china.com
 * @Date: 2019/1/16 13:54
 */
@Controller
@RequestMapping(value = "/hls/doc/layout/category/db/object")
public class DocCategoryObjectLovController extends BaseController {


    @Autowired
    private DocCategoryDbObjectMapper docCategoryDbObjectMapper;

    @RequestMapping(value = "/lov/query")
    @ResponseBody
    public ResponseData queryForLov(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                    @RequestParam(value = "pagenum", defaultValue = DEFAULT_PAGE) final int page,
                                    @RequestParam(value = "pagesize", defaultValue = DEFAULT_PAGE_SIZE) final int pagesize,
                                    final HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        PageHelper.startPage(page, pagesize);
        LovDocCategoryDbObjectDto lovDocCategoryDto = new LovDocCategoryDbObjectDto();
        Map parameter = requestData.getParameter();
        if (parameter != null) {
            String documentCategory = request.getParameter("document_category");
            String objectType = request.getParameter("object_type");
            Object objectName = parameter.get("objectName");
            if (StringUtils.isNotEmpty(documentCategory)) {
                lovDocCategoryDto.setDocumentCategory(documentCategory);
            }
            if (objectName != null) {
                lovDocCategoryDto.setObjectName(String.valueOf(objectName));
            }
            if (StringUtils.isNotEmpty(objectType)) {
                lovDocCategoryDto.setObjectType(objectType);
            }
        }
        return new ResponseData(docCategoryDbObjectMapper.queryLov(lovDocCategoryDto));
    }

}
