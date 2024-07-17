package com.hand.hls.credit.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.credit.dto.HlsCusConCreditWhiteList;
import com.hand.hls.credit.service.TongDunService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * description
 * </p>
 *
 * @author JINGZHOU.LI@hand-china.com 2024/7/16 9:21
 */

@Controller
public class TongDunTestController extends BaseController {

    @Resource
    private TongDunService tongDunService;


    @RequestMapping(value = "/tong/dun/test/{projectId}",method = {RequestMethod.GET})
    @ResponseBody
    public ResponseData test(@PathVariable(value = "projectId") Long projectId, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        System.out.println(tongDunService.preliminaryValid(projectId, request));
        return new ResponseData();
    }


    //正审
    @RequestMapping(value = "/tong/dun/test",method = {RequestMethod.GET})
    @ResponseBody
    public ResponseData test02(@RequestParam(value = "projectId") Long projectId, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        tongDunService.interlocutoryValid(projectId,request);
//        tongDunService.preliminaryValid(projectId,request);
        return new ResponseData();
    }

}
