//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.csh.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.common.components.HlsWordToPdfComponent;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.csh.dto.HlsCusCshTransactionRefund;
import com.hand.hls.csh.service.CshPaymentReqHdService;
import com.hand.hls.csh.service.CshTransactionRefundService;
import com.hand.hls.fin.exception.HlsCusAmountOverException;
import com.hand.hls.utils.ResMessageException;
import hls.core.utils.exception.HlsCusException;
import leaf.bean.LeafRequestData;
import leaf.service.validation.ParameterNullException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class CshPaymentReqHdController extends BaseController {
	@Autowired
	private CshPaymentReqHdService service;

	@Autowired
	private HlsWordToPdfComponent hlsWordToPdfComponent;

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private CshTransactionRefundService cshTransactionRefundService;

	public CshPaymentReqHdController() {
	}

	@RequestMapping({"/csh/payment/req/hd/query"})
	@ResponseBody
	public ResponseData query(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request, HttpServletResponse response, HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) {
		IRequest requestContext = this.createRequestContext(request);
		JSONObject param = (JSONObject)requestData.get("parameter");
		HlsCusCshPaymentReqHd metadataRelation = (HlsCusCshPaymentReqHd)param.toJavaObject(HlsCusCshPaymentReqHd.class);
		return new ResponseData(this.service.queryCshPaymentReqHd(requestContext, metadataRelation, pagenum, pagesize));
	}
	@RequestMapping(value = "/csh/payment/req/hd/save")
	@ResponseBody
	public ResponseData savePaymentReqHd(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) throws HlsCusAmountOverException, HlsCusException {
		IRequest requestContext = createRequestContext(request);

		JSONObject param = (JSONObject)requestData.get("parameter");
		HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd =param.toJavaObject(HlsCusCshPaymentReqHd.class);

		service.save(requestContext, hlsCusCshPaymentReqHd);
		List<HlsCusCshPaymentReqHd> hlsCusCshPaymentReqHds = new ArrayList<HlsCusCshPaymentReqHd>();
		hlsCusCshPaymentReqHds.add(hlsCusCshPaymentReqHd);
		return new ResponseData(hlsCusCshPaymentReqHds);
	}
	@RequestMapping(value = "/csh/payment/req/hd/create")
	@ResponseBody
	public ResponseData createPaymentReqHd(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) throws HlsCusAmountOverException, HlsCusException {
		IRequest requestContext = createRequestContext(request);

		JSONObject param = (JSONObject)requestData.get("parameter");
		HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd =param.toJavaObject(HlsCusCshPaymentReqHd.class);

		service.create(requestContext, hlsCusCshPaymentReqHd);
		List<HlsCusCshPaymentReqHd> hlsCusCshPaymentReqHds = new ArrayList<HlsCusCshPaymentReqHd>();
		hlsCusCshPaymentReqHds.add(hlsCusCshPaymentReqHd);
		return new ResponseData(hlsCusCshPaymentReqHds);
	}
	@RequestMapping(value = "/csh/payment/req/hd/saveEndTask")
	@ResponseBody
	public ResponseData saveEndTask(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) throws HlsCusAmountOverException, HlsCusException {
		IRequest requestContext = createRequestContext(request);

		JSONObject param = (JSONObject)requestData.get("parameter");
		HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd =param.toJavaObject(HlsCusCshPaymentReqHd.class);

		service.updateByPrimaryKeySelective(requestContext, hlsCusCshPaymentReqHd);
		List<HlsCusCshPaymentReqHd> hlsCusCshPaymentReqHds = new ArrayList<HlsCusCshPaymentReqHd>();
		hlsCusCshPaymentReqHds.add(hlsCusCshPaymentReqHd);
		return new ResponseData(hlsCusCshPaymentReqHds);
	}
	@RequestMapping(value = "/csh/payment/req/transfer/submit")
	@ResponseBody
	public ResponseData submitTransfer(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws ResMessageException, ParameterNullException {
		IRequest iRequest = createRequestContext(request);
		RequestHelper.setCurrentRequest(iRequest);

		JSONObject param = (JSONObject) requestData.get("parameter");
		HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd = param.toJavaObject(HlsCusCshPaymentReqHd.class);
		return new ResponseData(service.transferSubmit(iRequest, hlsCusCshPaymentReqHd));
	}
	@RequestMapping(value = "/csh/payment/req/transfer/checkSupplement")
	@ResponseBody
	public ResponseData checkSupplement(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws ResMessageException, ParameterNullException {
		IRequest iRequest = createRequestContext(request);
		RequestHelper.setCurrentRequest(iRequest);

		JSONObject param = (JSONObject) requestData.get("parameter");
		HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd = param.toJavaObject(HlsCusCshPaymentReqHd.class);
		service.checkSupplement(iRequest, hlsCusCshPaymentReqHd);
		return new ResponseData();
	}

	@RequestMapping(value = "/csh/payment/req/transfer/abandon")
	@ResponseBody
	public ResponseData abandonTransfer(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws ResMessageException, ParameterNullException {
		IRequest iRequest = createRequestContext(request);
		RequestHelper.setCurrentRequest(iRequest);

		JSONArray param = (JSONArray) requestData.get("parameter");
		List<HlsCusCshPaymentReqHd> hlsCusCshPaymentReqHds = param.toJavaList(HlsCusCshPaymentReqHd.class);
		return new ResponseData(service.abandonTransfer(iRequest, hlsCusCshPaymentReqHds));
	}

	@RequestMapping(value = "/csh/payment/req/transfer/saveEftTransfer")
	@ResponseBody
	public ResponseData saveEftTransfer(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws ResMessageException, ParameterNullException {
		IRequest iRequest = createRequestContext(request);
		RequestHelper.setCurrentRequest(iRequest);

		JSONObject param = (JSONObject) requestData.get("parameter");
		HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd = param.toJavaObject(HlsCusCshPaymentReqHd.class);
		service.saveEftTransfer(iRequest, hlsCusCshPaymentReqHd);
		return new ResponseData();
	}
	@RequestMapping(value = "/csh/payment/req/transfer/createEftTransferList")
	@ResponseBody
	public ResponseData createEftTransferList(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws ResMessageException, ParameterNullException {
		IRequest iRequest = createRequestContext(request);
		RequestHelper.setCurrentRequest(iRequest);

		JSONObject param = (JSONObject) requestData.get("parameter");
		HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd = param.toJavaObject(HlsCusCshPaymentReqHd.class);
		service.createEftTransferList(iRequest, hlsCusCshPaymentReqHd);
		return new ResponseData();
	}

	@RequestMapping(value = "/simple/doc/gen")
	@ResponseBody
	public ResponseData contentCreate(String code,String paymentId,final HttpServletRequest request,HttpServletResponse response) throws com.hand.hls.exception.HlsCusException {
		IRequest requestContext = createRequestContext(request);
		RequestHelper.setCurrentRequest(requestContext);
        if(StringUtils.isEmpty(code)) {
            throw new com.hand.hls.exception.HlsCusException("模板文件代码为空！");
        }
        if(StringUtils.isEmpty(paymentId)) {
            throw new com.hand.hls.exception.HlsCusException("放款单号为空！");
        }
		try {
 			List<FndAttachmentMulti> multiList = service.contextCreateMultiple(requestContext, code,paymentId,response);
			hlsWordToPdfComponent.wordToPdfAttachmentMuti(requestContext,multiList);
			return new ResponseData(multiList);
		} catch (Exception e) {
			logger.error("文件模版不存在!", e);
			return new ResponseData(false, "文件模版不存在!");
		}

		//return new ResponseData(true, "生成合同文本成功!");
	}

	@RequestMapping(value = "/csh/payment/req/hd/csh_hd_create")
	@ResponseBody
	public ResponseData PaymentReqHdCreate(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) throws HlsCusAmountOverException, HlsCusException {
		IRequest requestContext = createRequestContext(request);

		JSONObject param = (JSONObject)requestData.get("parameter");
		HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd =param.toJavaObject(HlsCusCshPaymentReqHd.class);

		service.cshHdCreate(requestContext, hlsCusCshPaymentReqHd);
		List<HlsCusCshPaymentReqHd> hlsCusCshPaymentReqHds = new ArrayList<HlsCusCshPaymentReqHd>();
		hlsCusCshPaymentReqHds.add(hlsCusCshPaymentReqHd);
		return new ResponseData(hlsCusCshPaymentReqHds);
	}
	//资金计划导入
	@RequestMapping(value = "/csh/payment/req/ln/import", method = RequestMethod.POST)
	public Map<String, Object> receiptImport(HttpServletRequest request, Long headerId , Long paymentReqId) throws IOException {
		IRequest iRequest = createRequestContext(request);
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("success", false);
		try {
			service.cshInImport(iRequest, headerId ,paymentReqId);
			response.put("message", "导入成功");
			response.put("success", true);
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "导入失败！" + e.getMessage());
		}
		return response;
	}

	@ResponseBody
	@RequestMapping("/csh/payment/dailyrate/query")

	public ResponseData dailyrate(HlsCusCshPaymentReqHd dto,
								  @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
								  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,
								  HttpServletRequest request) throws ParseException {
		Map<String, String> parameter = (Map) JSON.parseObject(request.getParameter("_request_data"), Map.class).get("parameter");
		if (StringUtils.isNotBlank(parameter.get("currency"))) {
			dto.setCurrency(parameter.get("currency"));
		}
		if (StringUtils.isNotBlank(parameter.get("actual_pay_date"))) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date actualPayDate = sdf.parse(parameter.get("actual_pay_date").toString());
			dto.setActualPayDate(actualPayDate);

		}
		IRequest requestCtx = createRequestContext(request);
		RequestHelper.setCurrentRequest(requestCtx);
		return new ResponseData(service.dailyrate(dto, pagenum, pagesize));
	}

	/**
	 * 关税付款申请创建
	 * @param requestData
	 * @param request
	 * @param session
	 * @return
	 * @throws HlsCusAmountOverException
	 * @throws HlsCusException
	 */
	@RequestMapping(value = "tariff/csh/payment/req/create")
	@ResponseBody
	public ResponseData tariffPaymentReqCreate(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) throws Exception {
		IRequest requestContext = createRequestContext(request);

		JSONArray param = (JSONArray) requestData.get("parameter");
		List<HlsCusConContractCashflow> list = param.toJavaList(HlsCusConContractCashflow.class);

		HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd = service.tariffPaymentReqCreate(requestContext, list);
		List<HlsCusCshPaymentReqHd> hlsCusCshPaymentReqHds = new ArrayList<HlsCusCshPaymentReqHd>();
		hlsCusCshPaymentReqHds.add(hlsCusCshPaymentReqHd);
		return new ResponseData(hlsCusCshPaymentReqHds);
	}

	/**
	 * 关税付款申请提交审批
	 * @param requestData
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "submit/tariff/csh/payment/req/wfl")
	@ResponseBody
	public ResponseData submitTariffPaymentReqWfl(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws Exception {
		IRequest requestContext = createRequestContext(request);
		JSONObject param = (JSONObject) requestData.get("parameter");
		HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd = param.toJavaObject(HlsCusCshPaymentReqHd.class);

		List<HlsCusCshPaymentReqHd> listR = service.submitTariffPaymentReqWfl(requestContext,hlsCusCshPaymentReqHd);

		return new ResponseData(listR);
	}

	/**
	 * 二期功能：零售业务付款支付首页查询
	 * @param requestData
	 * @param request
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/csh/payment/retail/home/query")
	@ResponseBody
	public ResponseData retailHomeQuery(@ModelAttribute("_request_data") LeafRequestData requestData,
										@RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
										@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,
										HttpServletRequest request,
										HttpSession session) {
		IRequest requestContext = this.createRequestContext(request);
		JSONObject param = (JSONObject)requestData.get("parameter");
		HlsCusCshPaymentReqHd metadataRelation = param.toJavaObject(HlsCusCshPaymentReqHd.class);
		return new ResponseData(service.retailPaymentHomeQuery(requestContext, metadataRelation, pagenum, pagesize));
	}

	@RequestMapping({"/csh/payment/queryActualPaymentAmount"})
	@ResponseBody
	public ResponseData queryActualPaymentAmount(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) {
		IRequest requestCtx = createRequestContext(request);

		JSONObject param = (JSONObject)requestData.get("parameter");
		HlsCusCshPaymentReqHd hlsCusLonContractWithdraw = param.toJavaObject(HlsCusCshPaymentReqHd.class);
		return new ResponseData(service.queryActualPaymentAmount(hlsCusLonContractWithdraw));
	}
	@RequestMapping({"/csh/payment/queryPaymentAmount"})
	@ResponseBody
	public ResponseData queryPaymentAmount(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) {
		IRequest requestCtx = createRequestContext(request);

		JSONObject param = (JSONObject)requestData.get("parameter");
		HlsCusCshPaymentReqHd hlsCusLonContractWithdraw = param.toJavaObject(HlsCusCshPaymentReqHd.class);
		return new ResponseData(service.queryPaymentAmount(hlsCusLonContractWithdraw));
	}

	@RequestMapping({"/csh/payment/req/hd/status"})
	@ResponseBody
	public ResponseData queryStatus(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request, HttpServletResponse response, HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) {
		IRequest requestContext = this.createRequestContext(request);
		JSONObject param = (JSONObject)requestData.get("parameter");
		HlsCusCshPaymentReqHd metadataRelation = (HlsCusCshPaymentReqHd)param.toJavaObject(HlsCusCshPaymentReqHd.class);
		List<HlsCusCshPaymentReqHd> result = new ArrayList<>();
		result.add(this.service.selectByPrimaryKey(requestContext, metadataRelation));
		return new ResponseData(result);
	}

	@RequestMapping(value = "/cah/refund/cancel")
	@ResponseBody
	public ResponseData updateRefundPayment(String refundId, String paymentRefundStatus, HttpServletRequest request,HttpServletResponse response) throws com.hand.hls.exception.HlsCusException {
		IRequest requestContext = createRequestContext(request);
		Map<String, Object> response1 = new HashMap<String, Object>();
		RequestHelper.setCurrentRequest(requestContext);
		HlsCusCshTransactionRefund hlsCusCshTransactionRefund = new HlsCusCshTransactionRefund();
		hlsCusCshTransactionRefund.setRefundId(Long.parseLong(refundId));
		hlsCusCshTransactionRefund.setPaymentRefundStatus(paymentRefundStatus);
		cshTransactionRefundService.updateByPrimaryKeySelective(requestContext,hlsCusCshTransactionRefund);
		return new ResponseData(true, "取消成功!");
	}


//


}
