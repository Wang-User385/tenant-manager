package com.hand.hls.vat.controllers;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.vat.dto.HlsCusAcpInvoiceHd;
import com.hand.hls.vat.mapper.HlsCusAcpInvoiceHdMapper;
import com.hand.hls.vat.service.AcpInvoiceHdService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;



@Controller
public class AcpInvoiceHdController extends BaseController {
	@Autowired
	private AcpInvoiceHdService service;

	@Autowired
	private HlsCusAcpInvoiceHdMapper acpInvoiceHdMapper;

//	@RequestMapping(value = "/vat/acp/detail/query")
//	@ResponseBody
//	public ResponseData acpInvoiceQuery(HttpServletRequest request, @RequestBody Map<String,String> map,
//                                        @RequestParam(defaultValue = DEFAULT_PAGE) final int page,
//                                        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pagesize){
//		return new ResponseData(service.acpInvoiceQuery(map));
//	}
//
//
//	@RequestMapping(value = "/vat/acp/detail/scale/query")
//	@ResponseBody
//	public ResponseData acpInvoiceScaleQuery(HttpServletRequest request){
//		return new ResponseData(service.acpInvoiceScaleQuery());
//	}
//
//	@RequestMapping(value = "/vat/acp/invoiced/totle/query")
//	@ResponseBody
//	public ResponseData acpInvoicedTotleQuery(HttpServletRequest request){
//		return new ResponseData(service.acpInvoicedTotleQuery());
//	}
//
//	@RequestMapping(value = "/vat/acp/invoiced/group/query")
//	@ResponseBody
//	public ResponseData acpInvoicedGroupQuery(final HttpServletRequest request){
//		IRequest requestContext = createRequestContext(request);
//		List<HlsCusAcpInvoiceHd> list = service.acpInvoicedGroupQuery(requestContext);
//		return new ResponseData(list);
//	}


	// 发票查询
	@RequestMapping(value = "/vat/acp/invoice/detail/query")
	@ResponseBody
	public ResponseData queryVatAcpInvoiceDetail(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
												 @RequestParam(defaultValue = DEFAULT_PAGE) final int page,
                                        		 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pageSize) {
		PageHelper.startPage(page, pageSize);
		IRequest iRequest = RequestHelper.getCurrentRequest();
		JSONObject params = (JSONObject) requestData.get("parameter");
		Map<String, String> param = JSONObject.toJavaObject(params, Map.class);
		return new ResponseData(acpInvoiceHdMapper.queryVatAcpInvoiceDetail(param));
	}

}
