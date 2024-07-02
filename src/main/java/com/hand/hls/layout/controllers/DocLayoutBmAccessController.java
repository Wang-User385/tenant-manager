package com.hand.hls.layout.controllers;

import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.layout.dto.LovFunctionBmAccessDto;
import com.hand.hls.sys.service.ISysFunctionBmAccessService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Eric Chen
 * @Email : qiang.chen04@hand-china.com
 * @Date: 2019/1/21 13:56
 */
@Controller
@RequestMapping("/hls/doc/layout/bm/access")
public class DocLayoutBmAccessController extends BaseController {

    @Autowired
    private ISysFunctionBmAccessService sysFunctionBmAccessService;


    @RequestMapping(value = "/queryForLov")
    public ResponseData queryForLov(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                    @RequestParam(value = "pagenum", defaultValue = DEFAULT_PAGE) final int pageNum,
                                    @RequestParam(value = "pagesize", defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
                                    final HttpServletRequest request) {

        Map parameter = requestData.getParameter();
        Map param = new HashMap();
        if (parameter != null) {
            param.put("bm_name", parameter.get("bm_name"));
        }
        List<LovFunctionBmAccessDto> list = sysFunctionBmAccessService.selectForLov(param, pageNum, pageSize);
        return new ResponseData(list);
    }
}
