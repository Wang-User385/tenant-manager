package com.hand.hls.plm.pli.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.plm.pli.dto.HlsCusPostloanInspection;
import com.hand.hls.plm.pli.service.HlsCusIPostloanInspectionService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:贷后检查controller
 * @Author: Wty
 * @Date: Created om 16:26 2018/5/24
 */
@Controller
public class HlsCusPostloanInspectionController extends BaseController {

    @Autowired
    private HlsCusIPostloanInspectionService service;


    @RequestMapping(value = "/plm/postloan/inspection/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        List<HlsCusPostloanInspection> list = new ArrayList<>();

        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPostloanInspection dto = param.toJavaObject(HlsCusPostloanInspection.class);

        list.add(service.selectPostloadInspection(requestContext, dto));
        return new ResponseData(list);
    }

    @RequestMapping(value = "/plm/postloan/inspection/submit")
    @ResponseBody
    public ResponseData update(@RequestBody HlsCusPostloanInspection dto, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        List<HlsCusPostloanInspection> list = new ArrayList<>();
        list.add(service.savePostLoan(requestCtx, dto));
        return new ResponseData(list);
    }

    @RequestMapping(value = "/plm/postloan/inspection/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusPostloanInspection> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    /**
     * @Description:首页rolltable查询
     * @Author: Wty
     * @Date: Created om 15:19 2018/5/28
     */
    @RequestMapping(value = "/plm/postloan/inspection/home/rollTable/query")
    @ResponseBody
    public ResponseData homeRollTableQuery(HttpServletRequest request,
                                           HlsCusPostloanInspection dto,
                                           @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                           @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.homeRollTableQuery(requestCtx, dto, page, pageSize));
    }

    /**
     * @Description:查找对应的贷后检查频率
     * @Author: Wty
     * @Date: Created om 18:19 2018/5/28
     */
    @RequestMapping(value = "/plm/postloan/inspection/selectContractFrequency")
    @ResponseBody
    public ResponseData selectContractFrequency(HttpServletRequest request,
                                                HlsCusPostloanInspection dto) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(service.selectContractFrequency(iRequest, dto));
    }

    /**
     * @Description:贷后检查工作流提交
     * @Author: Wty
     * @Date: Created om 10:12 2018/5/22
     */
    @RequestMapping(value = "/plm/postloan/inspection/submit/wfl")
    @ResponseBody
    public ResponseData submitWfl(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);

        JSONObject param  =(JSONObject) requestData.get("parameter");
        HlsCusPostloanInspection dto = param.toJavaObject(HlsCusPostloanInspection.class);

        HlsCusPostloanInspection hlsCusPostloanInspection = service.submitWfl(requestCtx, dto);
        List<HlsCusPostloanInspection> list = new ArrayList<>();
        if (hlsCusPostloanInspection != null) {
            list.add(hlsCusPostloanInspection);
        }
        return new ResponseData(list);
    }

    /**
     * 贷后检查创建
     * @param dto
     * @param result
     * @param request
     * @return
     */
    @RequestMapping(value = "/plm/postloan/inspection/create")
    @ResponseBody
    public ResponseData create(@RequestBody HlsCusPostloanInspection dto, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        ResponseData responseData  = new ResponseData(false);
        List<HlsCusPostloanInspection> list = service.createPostloanInspection(requestCtx, dto);
        if (list!=null&&list.size()>0&&list.get(0).getPostloanInspectionId()!=null&&list.get(0).getPostloanInspectionId()>0) {
           responseData.setSuccess(true);
           responseData.setRows(list);
        }else {
            responseData.setMessage("未获取创建的贷后检查的主键");
        }
        return responseData;
    }
}
