package com.hand.hls.layout.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.layout.dto.LovDocLayoutButtonDto;
import com.hand.hls.layout.service.IDocLayoutButtonService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Eric Chen
 * @Email : qiang.chen04@hand-china.com
 * @Date: 2019/1/22 13:54
 */
@Controller
@RequestMapping(value = "/hls/doc/layout/button/lov")
public class DocLayoutButtonLovController extends BaseController {

    @Autowired
    private IDocLayoutButtonService docLayoutButtonService;


    /**
     * 页面布局定义按钮Lov查询
     */
    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData queryForLov(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                    @RequestParam(value = "pagenum", defaultValue = DEFAULT_PAGE) final int pageNum,
                                    @RequestParam(value = "pagesize", defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
                                    final HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        Map parameter = requestData.getParameter();
        if (parameter == null) {
            return new ResponseData(true);
        }
        Map param = new HashMap<String, Object>();
        parameter.forEach((k, v) -> param.put(k, v));
        List<LovDocLayoutButtonDto> list = docLayoutButtonService.queryForLov(param, pageNum, pageSize);
        return new ResponseData(list);
    }


}
