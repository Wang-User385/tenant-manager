package com.hand.hls.layout.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.layout.dto.DocLayoutConfigLov;
import com.hand.hls.layout.service.IDocLayoutConfigLovService;
import hls.layout.service.IServerLayoutService;
import leaf.bean.LeafRequestData;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Eric Chen
 * @Email : qiang.chen04@hand-china.com
 * @Date: 2019/1/14 14:31
 */
@Controller
@RequestMapping("/hls/doc/layout/config/lov/validation")
public class DocLayoutConfigLovValidationController extends BaseController {

    @Autowired
    private IDocLayoutConfigLovService docLayoutConfigLovService;
    @Autowired
    private IServerLayoutService serverLayoutService;

    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(DocLayoutConfigLov dto,
                              @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,
                              HttpServletRequest request) throws SQLException {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        String config_id = request.getParameter("config_id");
        Map param = new HashMap();
        param.put("column_name", request.getParameter("column_name"));
        param.put("config_id", config_id);
        param.put("layout_code", request.getParameter("layout_code"));
        param.put("prompt", request.getParameter("prompt"));
        param.put("tab_code", request.getParameter("tab_code"));
        serverLayoutService.initDocLayoutConfigLovValidation(param);
        if (StringUtils.isBlank(config_id)) return new ResponseData(true);
        DocLayoutConfigLov layoutConfigLov = new DocLayoutConfigLov();
        layoutConfigLov.setConfigId(Long.valueOf(config_id));

        return new ResponseData(docLayoutConfigLovService.selectByConfigId(requestContext, layoutConfigLov, pagenum, pagesize));
    }

    @RequestMapping(value = "/batchUpdate")
    @ResponseBody
    public ResponseData batchUpdate(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        requestContext.setAttribute("springFlag", "Y");
        List parameterList = requestData.getParameterList();
        ResponseData responseData = new ResponseData(true);
        if (parameterList == null || parameterList.size() < 1) {
            return new ResponseData();
        }
        List<DocLayoutConfigLov> listToModify = new ArrayList<>();
        parameterList.forEach(o -> {
            if (o instanceof JSONObject) {
                JSONObject o1 = (JSONObject) o;
                DocLayoutConfigLov layoutConfigLov = o1.toJavaObject(DocLayoutConfigLov.class);
                listToModify.add(layoutConfigLov);
            }
        });
        return new ResponseData(docLayoutConfigLovService.batchUpdate(requestContext, listToModify));
    }
}


/**
 * BEGIN
 * hls_doc_layout_config_lov_pkg.insert_layout_config_lov(p_config_id      =>${@config_id},
 * p_col_name       =>${@lov_col_name},
 * p_query_column   =>${@lov_col_for_query},
 * p_dispaly_column =>${@lov_col_for_display},
 * p_data_type      =>${@lov_data_type},
 * p_width          =>${@lov_col_display_width},
 * p_lov_prompt    =>${@lov_col_prompt},
 * P_lov_sequence  =>${@lov_col_sequence},
 * p_user_id        =>${/session/@user_id});
 * END;
 */