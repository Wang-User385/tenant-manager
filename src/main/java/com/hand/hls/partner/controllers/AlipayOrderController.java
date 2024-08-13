package com.hand.hls.partner.controllers;

import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.partner.service.IAlipayService;
import com.hand.hls.utils.ResMessageException;
import hls.core.utils.exception.HlsCusException;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.partner.dto.AlipayOrderDTO;
import com.hand.hls.partner.service.IAlipayOrderService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

    @Controller
    public class AlipayOrderController extends BaseController{
    @Autowired
    private IAlipayService alipayService;

    @Autowired
    private IAlipayOrderService service;

        /**
         *代扣查寻
         * @param requestData
         * @param pagenum
         * @param pagesize
         * @param request
         * @return
         */

    @RequestMapping(value = "/gt/alipay/order/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,BindingResult result, HttpServletRequest request) throws HlsCusException, ResMessageException {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONArray param = (JSONArray)  requestData.get("parameter");
        List<HlsCusConContractCashflow> list = param.toJavaList(HlsCusConContractCashflow.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        ArrayList<AlipayOrderDTO> orderDTOS = new ArrayList<>();

        for (HlsCusConContractCashflow cashflow : list) {
            // 将数据插入中间表
            List<AlipayOrderDTO> alipayOrderDTOS = service.selectState(requestContext, cashflow);

            // 确保查询结果非空
            if (alipayOrderDTOS == null || alipayOrderDTOS.isEmpty()) {
                continue; // 如果查询结果为空，跳过当前循环
            }

            // 将查询结果的最后一个元素添加到 orderDTOS
            AlipayOrderDTO lastAlipayOrderDTO = alipayOrderDTOS.get(alipayOrderDTOS.size() - 1);
            orderDTOS.add(lastAlipayOrderDTO);

            // 代扣查询
            alipayService.withholdQuery(lastAlipayOrderDTO.getOrderId());
        }

          // 返回最终收集的 AlipayOrderDTO 列表
        return new ResponseData(orderDTOS);
    }

        /**
         * 发起代扣
         * @param requestData
         * @param result
         * @param request
         * @return
         */

    @RequestMapping(value = "/gt/alipay/order/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) throws HlsCusException{
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusConContractCashflow> list = param.toJavaList(HlsCusConContractCashflow.class);
        getValidator().validate(list, result);
        ArrayList<AlipayOrderDTO> alipayOrderDTOS = new ArrayList<>();
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }

        for (HlsCusConContractCashflow cashflow : list) {
            //将数据插入中间表，防止重复操作
            AlipayOrderDTO orderDTO = service.batchAdd(requestCtx, cashflow);

                //发起代扣
                alipayService.withhold(orderDTO.getOrderId());
                alipayOrderDTOS.add(orderDTO);

        }
      return new ResponseData(alipayOrderDTOS);
    }

    @RequestMapping(value = "/gt/alipay/order/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<AlipayOrderDTO> dto = parameter.toJavaList(AlipayOrderDTO.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }
}