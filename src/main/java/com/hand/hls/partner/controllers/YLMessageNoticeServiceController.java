package com.hand.hls.partner.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.partner.dto.AssetNeedBuybackDto;
import com.hand.hls.partner.dto.AssetNeedSubstituteDto;
import com.hand.hls.partner.service.IYLMessageNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@Controller
@RequestMapping(value = {"/r/api"})
public class YLMessageNoticeServiceController extends BaseController {

    @Autowired
    private IYLMessageNoticeService iylMessageNoticeService;

    /**
     * 易靓审核结果通知
     *
     * @param map
     * @param request
     * @return
     */
    @RequestMapping(value = "/order/audit/result/notice", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseData orderAuditResult(@RequestBody Map<String, String> map, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        iylMessageNoticeService.orderAuditResult(Long.valueOf(String.valueOf(map.get("contractId"))), map.get("scene"), requestContext);
        return new ResponseData();
    }


    /**
     * 易靓放款结果通知
     *
     * @param map
     * @param request
     * @return
     */
    @RequestMapping(value = "/order/loan/result/notice", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseData orderLoanResult(@RequestBody Map<String, String> map, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        iylMessageNoticeService.orderLoanResult(Long.valueOf(String.valueOf(map.get("projectId"))), requestContext);
        return new ResponseData();
    }


    /**
     * 易靓关单结果通知
     *
     * @param map
     * @param request
     * @return
     */
    @RequestMapping(value = "/order/close/notify/notice", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseData orderClosedNotify(@RequestBody Map<String, String> map, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        iylMessageNoticeService.orderClosedNotify(Long.valueOf(String.valueOf(map.get("projectId"))), requestContext);
        return new ResponseData();
    }


    /**
     * 易靓还款计划生成通知
     *
     * @param map
     * @param request
     * @return
     */
    @RequestMapping(value = "/repay/plan/created/notify", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseData repayPlanCreatedNotify(@RequestBody Map<String, String> map, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        iylMessageNoticeService.repayPlanCreatedNotify(Long.valueOf(String.valueOf(map.get("projectId"))), requestContext);
        return new ResponseData();
    }


    /**
     * 易靓逾期算费完成通知
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/overdue/calculate/finished/notify", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseData overdueCalculateFinishedNotify(HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        iylMessageNoticeService.overdueCalculateFinishedNotify(requestContext);
        return new ResponseData();
    }


    /**
     * 易靓需代偿通知
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/asset/need/substitute", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseData assetNeedSubstitute(final AssetNeedSubstituteDto assetNeedSubstituteDto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        iylMessageNoticeService.assetNeedSubstitute(assetNeedSubstituteDto, requestContext);
        return new ResponseData();
    }


    /**
     * 易靓需回购通知
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/asset/need/buyback", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseData assetNeedBuyback(final AssetNeedBuybackDto assetNeedBuyback, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        iylMessageNoticeService.assetNeedBuyback(assetNeedBuyback, requestContext);
        return new ResponseData();
    }


    /**
     * 易靓代扣签约结果通知
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/withhold/contract/result", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseData withholdContractResult(@RequestBody Map<String, String> map, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        iylMessageNoticeService.withholdContractResult(Long.valueOf(String.valueOf(map.get("projectId"))), requestContext);
        return new ResponseData();
    }


    /**
     * 易靓期次代扣结果通知
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/repay/plan/repaid/notify", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseData repayPlanRepaidNotify(@RequestBody Map<String, String> map, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        iylMessageNoticeService.repayPlanRepaidNotify(Long.valueOf(String.valueOf(map.get("projectId"))), requestContext);
        return new ResponseData();
    }

}
