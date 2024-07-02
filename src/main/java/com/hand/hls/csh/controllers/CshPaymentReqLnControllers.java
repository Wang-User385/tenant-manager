package com.hand.hls.csh.controllers;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.csh.dto.CshBaseDto;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqLn;
import com.hand.hls.csh.service.CshPaymentReqLnService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.utils.HlsConstantUtil;
import com.hand.hls.utils.ResMessageException;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class CshPaymentReqLnControllers extends BaseController {

	@Autowired
	private CshPaymentReqLnService cshPaymentReqLnService;
	
	@RequestMapping(value = "/csh/CshPaymentReqLnDetail/query", method = RequestMethod.POST)
	@ResponseBody
	public ResponseData select(@ModelAttribute("_request_data") LeafRequestData requestData) {
		JSONObject param = (JSONObject) requestData.get("parameter");
		HlsCusCshPaymentReqLn cshPaymentReqLn = param.toJavaObject(HlsCusCshPaymentReqLn.class);
		return new ResponseData(cshPaymentReqLnService.selectCshPaymentReqLnDetailByLnID(cshPaymentReqLn));
	}


	/**
	 * 保理合同放款页面查询支付结算信息
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/csh/CshPaymentReqLn/queryForLoanRequest")
	@ResponseBody
	public ResponseData queryForLoanRequest(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
											@RequestParam(defaultValue = DEFAULT_PAGE) final int page,
											@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pagesize) {
		JSONObject param = (JSONObject) requestData.get("parameter");
		HlsCusCshPaymentReqLn cshPaymentReqLn = param.toJavaObject(HlsCusCshPaymentReqLn.class);
		List<HlsCusCshPaymentReqLn> list = cshPaymentReqLnService.queryForLoanRequest(cshPaymentReqLn,page,pagesize);
		return new ResponseData(list);
	}

	@RequestMapping(value = "/csh/payment/req/ln/account")
	@ResponseBody
	public ResponseData queryAccount(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) {
		IRequest requestCtx = createRequestContext(request);

		JSONObject param = (JSONObject)requestData.get("parameter");
		HlsCusCshPaymentReqLn hlsCusCshPaymentReqLn = param.toJavaObject(HlsCusCshPaymentReqLn.class);
		return new ResponseData(cshPaymentReqLnService.queryAccount(hlsCusCshPaymentReqLn));
	}

	/**
	 * 二期功能：付款申请明细查询
	 * @param requestData
	 * @param request
	 * @param hlsCusCshPaymentReqLn
	 * @param pagenum
	 * @param pagesize
	 * @return
	 */
	@RequestMapping(value = "/csh/payments/req/ln/query")
	@ResponseBody
	public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
							  HttpServletRequest request,
							  HlsCusCshPaymentReqLn hlsCusCshPaymentReqLn,
							  @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
							  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {

		IRequest requestContext = createRequestContext(request);
		requestContext.setAttribute("authorityRuleFlag", "N");
		JSONObject param = (JSONObject) requestData.get(HlsConstantUtil.BaseController.PARAMETER);
		HlsCusCshPaymentReqLn metadataRelation = param.toJavaObject(HlsCusCshPaymentReqLn.class);
		if(hlsCusCshPaymentReqLn.getPaymentReqId()!=null){
			metadataRelation.setPaymentReqId(hlsCusCshPaymentReqLn.getPaymentReqId());
		}
		if(hlsCusCshPaymentReqLn.getPaymentReqLnId()!=null){
			metadataRelation.setPaymentReqLnId(hlsCusCshPaymentReqLn.getPaymentReqLnId());
		}
		if(hlsCusCshPaymentReqLn.getProcessInstanceId()!=null){
			metadataRelation.setProcessInstanceId(hlsCusCshPaymentReqLn.getProcessInstanceId());
		}
		return new ResponseData(cshPaymentReqLnService.queryCshPaymentReqLn(requestContext, metadataRelation, pagenum, pagesize));
	}


	/**
	 * 二期功能：付款申请创建保存提交
	 * @param request
	 * @param requestData
	 * @return
	 * @throws ResMessageException
	 * @throws HlsCusException
	 */
	@RequestMapping(value = "/csh/payments/req/ln/save")
	@ResponseBody
	public ResponseData saveData(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws ResMessageException, HlsCusException {

		IRequest requestCtx = createRequestContext(request);
		requestCtx.setAttribute("authorityRuleFlag", "N");
		RequestHelper.setCurrentRequest(requestCtx);
		JSONObject param = (JSONObject) requestData.get(HlsConstantUtil.BaseController.PARAMETER);
		CshBaseDto cshBaseDto = param.toJavaObject(CshBaseDto.class);
		return cshPaymentReqLnService.cshPaymentReqCreateAndSubmit(requestCtx, cshBaseDto);
	}

	/**
	 * 二期功能：付款申请撤回
	 * @param request
	 * @param paymentReqId
	 * @return
	 * @throws HlsCusException
	 */
	@RequestMapping(value = "/csh/payments/req/create/back")
	@ResponseBody
	public ResponseData paymentBack(HttpServletRequest request,
									@RequestParam("paymentReqId")Long paymentReqId) throws HlsCusException {
		IRequest requestContext = createRequestContext(request);
		cshPaymentReqLnService.paymentBack(requestContext, paymentReqId);
		return new ResponseData();
	}

	/**
	 * 二期功能：付款申请行删除
	 * @param request
	 * @param requestData
	 * @return
	 */
	@RequestMapping(value = "/csh/payments/req/ln/remove")
	@ResponseBody
	public ResponseData deleteBatch(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
		JSONArray parameter = (JSONArray) requestData.get(HlsConstantUtil.BaseController.PARAMETER);
		List<HlsCusCshPaymentReqLn> hlsCusCshPaymentReqLnList = parameter.toJavaList(HlsCusCshPaymentReqLn.class);
		IRequest requestCtx = createRequestContext(request);
		RequestHelper.setCurrentRequest(requestCtx);
		cshPaymentReqLnService.batchDeleteReqLn(hlsCusCshPaymentReqLnList);
		return new ResponseData(hlsCusCshPaymentReqLnList);
	}

	/**
	 * 二期功能：付款申请退回--取消合同
	 */
	@RequestMapping(value = "/csh/payments/return")
	@ResponseBody
	public ResponseData returnPayment(HttpServletRequest request,
									  @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws HlsCusException {
		IRequest requestContext = createRequestContext(request);
		JSONObject param = (JSONObject) requestData.get(HlsConstantUtil.BaseController.PARAMETER);
		HlsCusConContract metadataRelation = param.getObject("hlsCusConContract", HlsCusConContract.class);
//        service.returnPayment(requestContext, metadataRelation);
		cshPaymentReqLnService.cancelContract(requestContext, metadataRelation);
		return new ResponseData();
	}
}
