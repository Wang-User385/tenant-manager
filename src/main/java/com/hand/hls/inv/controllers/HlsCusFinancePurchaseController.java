package com.hand.hls.inv.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.inv.dto.HlsCusFinancePurchase;
import com.hand.hls.inv.service.HlsCusIFinancePurchaseService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description:申购controller
 * @Author: wty
 * @Date: Created in 14:45 2018/4/17
 */
@Controller
public class HlsCusFinancePurchaseController extends BaseController {

    @Autowired
    private HlsCusIFinancePurchaseService service;


    @RequestMapping(value = "/inv/finance/purchase/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusFinancePurchase dto = param.toJavaObject(HlsCusFinancePurchase.class);
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryAll(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/inv/finance/purchase/same/product/query")
    @ResponseBody
    public ResponseData querySameProductName(HlsCusFinancePurchase dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                             @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.querySameProductName(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/inv/finance/purchase/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusFinancePurchase dto = param.toJavaObject(HlsCusFinancePurchase.class);
        IRequest iRequest = createRequestContext(request);
        List<HlsCusFinancePurchase> list = new ArrayList<>();
        dto = service.invPurchaseSave(iRequest, dto);
        list.add(dto);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/inv/finance/purchase/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusFinancePurchase> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    /**
     * @Description:公司等基本信息的查询
     * @Author: Wty
     * @Date: Created om 16:26 2018/4/19
     */
    @RequestMapping(value = "/inv/finance/purchase/selectBaseInfo")
    @ResponseBody
    public ResponseData selectBaseInfo(HttpServletRequest request,
                                       HlsCusFinancePurchase dto,
                                       HttpSession session,
                                       @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                       @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {

        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.selectBaseInfo(requestCtx, dto, session, page, pageSize));
    }

    /**
     * @Description:首页第三块rollTable查询
     * @Author: Wty
     * @Date: Created om 16:28 2018/4/19
     */
    @RequestMapping(value = "/inv/finance/purchase/home/third/query")
    @ResponseBody
    public ResponseData homeThirdQuery(HttpServletRequest request,
                                       HlsCusFinancePurchase dto,
                                       HttpSession session,
                                       @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                       @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(service.queryInvPurchaseList(iRequest, dto, page, pageSize));
    }

    /**
     * @Description:赎回，明细等具体信息的展示
     * @Author: Wty
     * @Date: Created om 16:09 2018/4/24
     */
    @RequestMapping(value = "/inv/finance/purchase/detailQuery")
    @ResponseBody
    public ResponseData detailQuery(HttpServletRequest request,
                                    HlsCusFinancePurchase dto,
                                    HttpSession session,
                                    @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                    @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(service.detailPurchaseQuery(iRequest, dto, page, pageSize));
    }

    /**
     * @Description:校验是否存在新建或退回的追加
     * @Author: Wty
     * @Date: Created om 14:38 2018/4/25
     */
    @RequestMapping(value = "/inv/finance/purchase/check/newOrReturn")
    @ResponseBody
    public ResponseData purchaseCheckNewOrReturn(HttpServletRequest request,
                                                 HlsCusFinancePurchase dto,
                                                 HttpSession session,
                                                 @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(service.purchaseCheckNewOrReturn(iRequest, dto, page, pageSize));
    }

    /**
     * @Description:申购追加工作流
     * @Author: Wty
     * @Date: Created om 17:10 2018/4/26
     */
    @RequestMapping(value = "/inv/finance/purchase/submit/wfl")
    @ResponseBody
    public ResponseData invPurchaseSubmitWfl(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);

        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusFinancePurchase hlsCusFinancePurchase = param.toJavaObject(HlsCusFinancePurchase.class);
        return new ResponseData(service.purchaseSubmitWfl(iRequest, hlsCusFinancePurchase));
    }

    /**
     * @Description:历史申购信息查询
     * @Author: Wty
     * @Date: Created om 12:35 2018/4/27
     */
    @RequestMapping(value = "/inv/finance/purchase/history/search")
    @ResponseBody
    public ResponseData searchHistory(HttpServletRequest request,
                                      HlsCusFinancePurchase hlsCusFinancePurchase,
                                      @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(service.searchHistory(iRequest, hlsCusFinancePurchase, page, pageSize));
    }

    /**
     * @Description:首页第一块第一个tab页查询
     * @Author: Wty
     * @Date: Created om 14:08 2018/5/3
     */
    @RequestMapping(value = "/inv/finance/purchase/home/first/tab/search")
    @ResponseBody
    public ResponseData selectHomeFirstOneTab(HttpServletRequest request,
                                              HlsCusFinancePurchase hlsCusFinancePurchase,
                                              @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(service.selectHomeFirstOneTab(iRequest, hlsCusFinancePurchase));
    }

    /**
     * @Description:首页第一块第二个tab页查询
     * @Author: Wty
     * @Date: Created om 14:08 2018/5/3
     */
    @RequestMapping(value = "/inv/finance/purchase/home/second/tab/search")
    @ResponseBody
    public ResponseData selectHomeFirstTwoTab(HttpServletRequest request,
                                              HlsCusFinancePurchase hlsCusFinancePurchase,
                                              @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(service.selectHomeFirstTwoTab(iRequest, hlsCusFinancePurchase));
    }

    /**
     * @Description:首页第一块第三个tab页查询
     * @Author: Wty
     * @Date: Created om 14:08 2018/7/26
     */
    @RequestMapping(value = "/inv/finance/purchase/home/third/tab/search")
    @ResponseBody
    public ResponseData selectHomeThirdTwoTab(HttpServletRequest request,
                                              @RequestBody HlsCusFinancePurchase hlsCusFinancePurchase,
                                              @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(service.selectHomeFirstThreeTab(iRequest, hlsCusFinancePurchase));
    }

    /**
     * @Description:综合查询
     * @Author: Wty
     * @Date: Created om 14:41 2018/5/4
     */
    @RequestMapping(value = "/inv/finance/purchase/integratedQuery")
    @ResponseBody
    public ResponseData integratedQuery(HttpServletRequest request,
                                        HlsCusFinancePurchase dto,
                                        HttpSession session,
                                        @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(service.integratedQuery(iRequest, dto, page, pageSize));
    }
    /**
     * @Description:申购追加作废工作流
     * @Author: Wty
     * @Date: Created om 17:10 2018/4/26
     */
    @RequestMapping(value = "/inv/finance/purchase/invalid/submit")
    @ResponseBody
    public ResponseData invPurchaseInvalidSubmitWfl(HttpServletRequest request, @RequestBody HlsCusFinancePurchase hlsCusFinancePurchase) {
        IRequest iRequest = createRequestContext(request);
        List<HlsCusFinancePurchase> list = new ArrayList<>();
        list.add(service.purchaseInvalidSubmit(iRequest, hlsCusFinancePurchase));
        return new ResponseData(list);
    }

    /**
     * @Description:申购追加基本信息变更保存
     * @Author: Wty
     * @Date: Created om 1:36 2018/5/2
     */
    @RequestMapping(value = "/inv/finance/purchase/changes/submit")
    @ResponseBody
    public ResponseData purchaseChangesSubmit(HttpServletRequest request, @RequestBody HlsCusFinancePurchase dto) {
        IRequest iRequest = createRequestContext(request);
        List<HlsCusFinancePurchase> list = new ArrayList<>();
        list.add(service.purchaseChangesSubmit(iRequest, dto));
        return new ResponseData(list);
    }

    /**
     * @Description:首页第三个chart金额查询
     * @Author: Wty
     * @Date: Created om 下午2:25 2018/7/1
     */
    @RequestMapping(value = "/inv/finance/purchase/home/chart/third/amount/query")
    @ResponseBody
    public ResponseData homeChartThirdQueryAmount(HttpServletRequest request, @RequestBody Map map) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(service.homeChartThirdQueryAmount(iRequest,map));
    }

    /**
     * @Description:申购明细信息查询
     * @Author: xuju
     * @Date: Created om  2019/08/15
     */
    @RequestMapping(value = "/inv/finance/purchase/detail/query")
    @ResponseBody
    public ResponseData queryInvPurchaseDetail(HttpServletRequest request, HlsCusFinancePurchase dto) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(service.queryInvPurchaseDetail(iRequest, dto));
    }

    /**
     * @Description:财务投资理财查询
     * @Author: xuju
     * @Date: Created om  2019/08/21
     */
    @RequestMapping(value = "/inv/finance/do/detail/query")
    @ResponseBody
    public ResponseData queryInvDoFinance(HttpServletRequest request, HlsCusFinancePurchase dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                          @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(service.queryInvDoFinance(iRequest, dto, page, pageSize));
    }

    /**
     * @Description:投资理财产品信息
     * @Author: xuju
     * @Date: Created om  2019/08/21
     */
    @RequestMapping(value = "/inv/finance/products/detail/query")
    @ResponseBody
    public ResponseData queryProductsFinanceDetail(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusFinancePurchase dto = param.toJavaObject(HlsCusFinancePurchase.class);
        List<HlsCusFinancePurchase> list = service.queryProductsFinanceDetail(iRequest, dto);
        return new ResponseData(list);
    }

    /**
     * @Description:申购金额变更工作流
     * @Author: Wty
     * @Date: Created om 17:10 2018/4/26
     */
    @RequestMapping(value = "/inv/finance/purchase/amount/change/submit/wfl")
    @ResponseBody
    public ResponseData purchaseChangesSubmitWfl(HttpServletRequest request, @RequestBody HlsCusFinancePurchase hlsCusFinancePurchase) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(service.purchaseChangesSubmitWfl(iRequest, hlsCusFinancePurchase));
    }
    /**
     * @Description:投资理财产品信息
     * @Author: xuju
     * @Date: Created om  2019/08/21
     */
    @RequestMapping(value = "/inv/finance/products/detail/queryById")
    @ResponseBody
    public ResponseData queryById(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusFinancePurchase dto = param.toJavaObject(HlsCusFinancePurchase.class);
        List<HlsCusFinancePurchase> list = service.queryById(iRequest, dto);
        return new ResponseData(list);
    }



}