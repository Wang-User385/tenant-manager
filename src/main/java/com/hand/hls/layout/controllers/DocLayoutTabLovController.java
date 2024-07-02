package com.hand.hls.layout.controllers;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.layout.dto.DocLayoutTab;
import com.hand.hls.layout.mapper.DocLayoutTabLovMapper;
import leaf.bean.LeafRequestData;
import org.apache.commons.lang.StringUtils;
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
@RequestMapping(value = "/hls/doc/layout/tab/lov")
public class DocLayoutTabLovController extends BaseController {


    @Autowired
    private DocLayoutTabLovMapper docLayoutTabLovMapper;

    /**
     * hls.HLS030.hls_doc_layout_tab_lov?layout_code=${/parameter/@layout_code}
     * /hls/doc/layout/tab/lov?layout_code=${/parameter/@layout_code}~com.hand.hls.layout.dto.LovDocLayoutTabDto
     */
    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData queryForLov(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                    @RequestParam(value = "pagenum", defaultValue = DEFAULT_PAGE) final int page,
                                    @RequestParam(value = "pagesize", defaultValue = DEFAULT_PAGE_SIZE) final int pagesize,
                                    final HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        PageHelper.startPage(page, pagesize);
        DocLayoutTab lovDocLayoutTabDto = new DocLayoutTab();
        Map parameter = requestData.getParameter();
        if (parameter != null) {
            Object tabDesc = parameter.get("tabDesc");
            Object tabCode = parameter.get("tabCode");
            if (tabDesc != null) {
                lovDocLayoutTabDto.setTabDesc(String.valueOf(tabDesc));
            }
            if (tabCode != null) {
                lovDocLayoutTabDto.setTabCode(String.valueOf(tabCode));
            }
        }
        String layout_code = request.getParameter("layout_code");
        if (StringUtils.isNotBlank(layout_code)) {
            lovDocLayoutTabDto.setLayoutCode(layout_code);
        }
        String enabled_flag = request.getParameter("enabled_flag");
        if (StringUtils.isNotBlank(enabled_flag)) {
            lovDocLayoutTabDto.setEnabledFlag(enabled_flag);
        }
        String tab_type = request.getParameter("tab_type");
        if (StringUtils.isNotBlank(tab_type)) {
            lovDocLayoutTabDto.setTabType(tab_type);
        }
        return new ResponseData(docLayoutTabLovMapper.queryLov(lovDocLayoutTabDto));
    }

}
