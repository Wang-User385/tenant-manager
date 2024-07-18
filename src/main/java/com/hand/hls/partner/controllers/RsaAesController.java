package com.hand.hls.partner.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hls.partner.service.IRsaAesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = {"/r/api"})
public class RsaAesController extends BaseController {

    @Autowired
    private IRsaAesService iRsaAesService;

    @RequestMapping(
            value = {"/encryptedData"},
            method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public JSONObject encryptedData(@RequestBody JSONObject jsonObject, HttpServletRequest request) throws Exception {
        return iRsaAesService.encryptedData(jsonObject);
    }

    @RequestMapping(
            value = {"/decryptedData"},
            method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String decryptedData(@RequestBody JSONObject jsonObject, HttpServletRequest request) throws Exception {
        return iRsaAesService.decryptedData(jsonObject);
    }


}
