package com.hand.hls.layout.controllers;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.layout.dto.LovDocCategoryDto;
import com.hand.hls.layout.mapper.DocCategoryLovMapper;
import leaf.bean.LeafRequestData;
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
@RequestMapping(value = "/hls/doc/layout/category/lov")
public class DocCategoryLovController extends BaseController {


    @Autowired
    private DocCategoryLovMapper docCategoryLovMapper;

    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData queryForLov(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                    @RequestParam(value = "pagenum", defaultValue = DEFAULT_PAGE) final int page,
                                    @RequestParam(value = "pagesize", defaultValue = DEFAULT_PAGE_SIZE) final int pagesize,
                                    final HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        PageHelper.startPage(page, pagesize);
        LovDocCategoryDto lovDocCategoryDto = new LovDocCategoryDto();
        Map parameter = requestData.getParameter();
        if (parameter != null) {
            Object documentCategory = parameter.get("documentCategory");
            Object description = parameter.get("description");
            if (documentCategory != null) {
                lovDocCategoryDto.setDocumentCategory(String.valueOf(documentCategory));
            }
            if (description != null) {
                lovDocCategoryDto.setDescription(String.valueOf(description));
            }
        }
        return new ResponseData(docCategoryLovMapper.queryLov(lovDocCategoryDto));
    }

}
