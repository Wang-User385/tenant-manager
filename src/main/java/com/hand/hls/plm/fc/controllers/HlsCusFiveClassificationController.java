package com.hand.hls.plm.fc.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.plm.fc.dto.HlsCusFiveClassification;
import com.hand.hls.plm.fc.service.HlsCusIFiveClassificationService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class HlsCusFiveClassificationController extends BaseController {

    @Autowired
    private HlsCusIFiveClassificationService service;


    @RequestMapping(value = "/plm/five/classification/query")
    @ResponseBody
    public ResponseData query(HlsCusFiveClassification dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryAll(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/plm/five/classification/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusFiveClassification dto = param.toJavaObject(HlsCusFiveClassification.class);
        List<HlsCusFiveClassification> list = new ArrayList<>();
        list.add(service.save(requestCtx, dto));
        return new ResponseData(list);
    }



    @RequestMapping(value = "/plm/five/classification/contract/save")
    @ResponseBody
    public ResponseData contractSave(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusFiveClassification dto = param.toJavaObject(HlsCusFiveClassification.class);
        List<HlsCusFiveClassification> list = new ArrayList<>();
        list.add(service.contractSave(requestCtx, dto));
        return new ResponseData(list);
    }

    @RequestMapping(value = "/plm/five/classification/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusFiveClassification> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    /**
     * @Description:五级分类工作流提交
     * @Author: Wty
     * @Date: Created om 10:12 2018/5/22
     */
    @RequestMapping(value = "/plm/five/classification/submit/wfl")
    @ResponseBody
    public ResponseData submitWfl(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusFiveClassification dto = param.toJavaObject(HlsCusFiveClassification.class);
        HlsCusFiveClassification hlsCusFiveClassification = service.submitWfl(requestCtx, dto);
        List<HlsCusFiveClassification> list = new ArrayList<>();
        if (hlsCusFiveClassification != null) {
            list.add(hlsCusFiveClassification);
        }
        return new ResponseData(list);
    }

    /**
     * @Description: 五级分类调整清单
     * @Author: Wty
     * @Date: Created om 13:09 2018/5/22
     */
    @RequestMapping(value = "/plm/fc/home/rollTable/query")
    @ResponseBody
    public ResponseData homeRollTableQuery(HlsCusFiveClassification dto, BindingResult result,
                                           HttpServletRequest request,
                                           @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                           @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        IRequest requestCtx = createRequestContext(request);
        dto.setCompanyId(requestCtx.getCompanyId());
        return new ResponseData(service.homeRollTableQuery(requestCtx, dto, page, pageSize));
    }

    /**
     * @Description:五级分类查询合同和附件是否变更过
     * @Author: Wty
     * @Date: Created om 10:51 2018/5/23
     */
    @RequestMapping(value = "/plm/five/classification/isChange/query")
    @ResponseBody
    public ResponseData contractOrAttachmentIsChange(HlsCusFiveClassification dto, BindingResult result,
                                                     HttpServletRequest request,
                                                     @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                     @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        IRequest requestCtx = createRequestContext(request);
        dto.setCompanyId(requestCtx.getCompanyId());
        Map map = service.contractOrAttachmentIsChange(requestCtx, dto);
        List<Map> list = new ArrayList<>();
        list.add(map);
        return new ResponseData(list);
    }

    /**
     * @Description:五级分类变更提交工作流
     * @Author: Wty
     * @Date: Created om 13:21 2018/5/23
     */
    @RequestMapping(value = "/plm/five/classification/submit/change/wfl")
    @ResponseBody
    public ResponseData submitChangeWfl(@RequestBody HlsCusFiveClassification dto, BindingResult result,
                                        HttpServletRequest request,
                                        @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        IRequest requestCtx = createRequestContext(request);
        HlsCusFiveClassification hlsCusFiveClassification = service.submitChangeWfl(requestCtx, dto);
        List<HlsCusFiveClassification> list = new ArrayList<>();
        if (hlsCusFiveClassification != null) {
            list.add(hlsCusFiveClassification);
        }
        return new ResponseData(list);
    }

    /**
     * @Description:五级分类copy备份数据
     * @Author: Wty
     * @Date: Created om 11:21 2018/6/13
     */
    @RequestMapping(value = "/plm/five/classification/copy/change")
    @ResponseBody
    public ResponseData copyChangeData(HlsCusFiveClassification dto, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        service.copyChangeData(iRequest, dto);
        return new ResponseData();
    }

    @RequestMapping(value = "/plm/five/classification/meet/wfl")
    @ResponseBody
    public ResponseData meetwfl(@RequestBody HlsCusFiveClassification dto, BindingResult result,
                                HttpServletRequest request,
                                @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(service.meetwfl(iRequest, dto));
    }

}