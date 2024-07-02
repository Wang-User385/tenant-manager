package com.hand.hls.inv.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.inv.dto.HlsCusFinanceRedeem;
import com.hand.hls.inv.service.HlsCusIFinanceRedeemService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:赎回controller
 * @Author: wty
 * @Date: Created in 14:45 2018/4/17
 */
@Controller
public class HlsCusFinanceRedeemController extends BaseController {

    @Autowired
    private HlsCusIFinanceRedeemService service;

    @RequestMapping(value = "/inv/finance/redeem/query")
    @ResponseBody
    public ResponseData query(HlsCusFinanceRedeem dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryAll(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/inv/finance/redeem/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusFinanceRedeem dto = param.toJavaObject(HlsCusFinanceRedeem.class);
        return new ResponseData(service.redeemSave(iRequest, dto));
    }

    @RequestMapping(value = "/inv/finance/redeem/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusFinanceRedeem> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    /**
     * @Description:查询赎回存在新建或审批退回的数据
     * @Author: Wty
     * @Date: Created om 15:25 2018/4/25
     */
    @RequestMapping(value = "/inv/finance/redeem/check/newOrReturn")
    @ResponseBody
    public ResponseData checkRedeemNewOrReturn(HttpServletRequest request,
                                               HlsCusFinanceRedeem dto,
                                               HttpSession session,
                                               @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                               @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(service.checkRedeemNewOrReturn(iRequest, dto, page, pageSize));
    }

    /**
     * @Description:赎回工作流
     * @Author: xuju
     * @Date: Created om 17:11 2018/4/26
     */
    @RequestMapping(value = "/inv/finance/redeem/submit/wfl")
    @ResponseBody
    public ResponseData invPurchaseSubmitWfl(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusFinanceRedeem dto = param.toJavaObject(HlsCusFinanceRedeem.class);
        return new ResponseData(service.redeemSubmitWfl(iRequest, dto));
    }

    /**
     * @Description:赎回作废工作流
     * @Author: Wty
     * @Date: Created om 17:11 2018/4/26
     */
    @RequestMapping(value = "/inv/finance/redeem/invalid/submit")
    @ResponseBody
    public ResponseData redeemInvalidSubmitWfl(HttpServletRequest request, @RequestBody HlsCusFinanceRedeem hlsCusFinanceRedeem) {
        IRequest iRequest = createRequestContext(request);
        List<HlsCusFinanceRedeem> list = new ArrayList<>();
        list.add(service.redeemInvalidSubmit(iRequest, hlsCusFinanceRedeem));
        return new ResponseData(list);
    }

    /**
     * @Description:赎回变更工基本信息保存
     * @Author: Wty
     * @Date: Created om 1:37 2018/5/2
     */
    @RequestMapping(value = "/inv/finance/redeem/changes/submit")
    @ResponseBody
    public ResponseData redeemChangesSubmit(HttpServletRequest request, @RequestBody HlsCusFinanceRedeem dto) {
        IRequest iRequest = createRequestContext(request);
        List<HlsCusFinanceRedeem> list = new ArrayList<>();
        list.add(service.redeemChangesSubmit(iRequest, dto));
        return new ResponseData(list);
    }


    /**
     * @Description:赎回明细信息查询
     * @Author: xuju
     * @Date: Created om 1:37 2019/8/30
     */
    @RequestMapping(value = "/inv/finance/redeem/detail/query")
    @ResponseBody
    public ResponseData queryRedeemDetail(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusFinanceRedeem dto = param.toJavaObject(HlsCusFinanceRedeem.class);
        return new ResponseData(service.queryRedeemDetail(iRequest, dto));
    }
}