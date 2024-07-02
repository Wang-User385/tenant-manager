package com.hand.hls.layout.controllers;

import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.layout.service.IDocLayoutValidationSqlService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class DocLayoutValidationSqlController extends BaseController {
    @Autowired
    private IDocLayoutValidationSqlService sqlService;

    @RequestMapping("/doc/layout/lov/common")
    public ResponseData queryLov(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, String sqlId, HttpServletRequest request) {
        try {
            return sqlService.queryLov(requestData, sqlId, request);
        } catch (Exception e) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(e.getMessage());
            return responseData;
        }
    }

    @RequestMapping("/doc/layout/comboBox/common")
    public ResponseData queryComboBox(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        try {
            return sqlService.queryBox(requestData, request);
        } catch (Exception e) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(e.getMessage());
            return responseData;
        }
    }
}
