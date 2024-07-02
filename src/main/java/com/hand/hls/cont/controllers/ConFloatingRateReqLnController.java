package com.hand.hls.cont.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.mybatis.util.StringUtil;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.cont.dto.FloatingRateReqDetail;
import com.hand.hls.cont.dto.HlsCusConFloatingRateReq;
import com.hand.hls.cont.dto.HlsCusConFloatingRateReqLn;
import com.hand.hls.cont.service.ConFloatingRateReqLnService;
import com.hand.hls.cont.service.IFloatingRateReqDetailService;
import com.hand.hls.csh.dto.HlsCusDepositRefund;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.service.HlsCusPrjQuotationService;
import leaf.bean.LeafRequestData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.rmi.NoSuchObjectException;
import java.util.*;

/**
 * @author Qian Yuanfeng
 * @date 2020/6/16 - 14:17
 */

@Controller
public class ConFloatingRateReqLnController extends BaseController {
    @Autowired
    private ConFloatingRateReqLnService service;
    @Autowired
    private IFloatingRateReqDetailService floatingRateReqDetailService;
    public static final String DOCUMENT_CATEGORY = "CON_FLOATING_RATE_REQ";

    public ConFloatingRateReqLnController() {
    }

    @Autowired
    private HlsCusPrjQuotationService hlsCusPrjQuotationService;

    @RequestMapping({"/con/floating/ln/query"})
    @ResponseBody
    public ResponseData query(HlsCusConFloatingRateReqLn dto, @ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        IRequest requestContext = this.createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusConFloatingRateReqLn metadataRelation = (HlsCusConFloatingRateReqLn) param.toJavaObject(HlsCusConFloatingRateReqLn.class);
        Map parameter = requestData.getParameter();
        if ("Y".equals(parameter.get("wfl_flag"))) {
            metadataRelation.setStatus("CALCULATED");
        }

        return new ResponseData(this.service.queryConFloatingRateReqLn(requestContext, metadataRelation, pagenum, pagesize));

    }

    @RequestMapping({"/con/floating/query/calcdetail"})
    @ResponseBody
    public ResponseData queryDetailCalc(HlsCusConFloatingRateReqLn dto, @ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        IRequest requestContext = this.createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusConFloatingRateReqLn metadataRelation = (HlsCusConFloatingRateReqLn) param.toJavaObject(HlsCusConFloatingRateReqLn.class);

        return new ResponseData(this.service.queryFloatlnCalcDetail(requestContext, metadataRelation, pagenum, pagesize));

    }


    @RequestMapping({"/con/floating/ln/compare/query"})
    @ResponseBody
    public ResponseData compareQuery(@ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        IRequest requestContext = this.createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        FloatingRateReqDetail floatingRateReqDetail = (FloatingRateReqDetail) param.toJavaObject(FloatingRateReqDetail.class);
        return floatingRateReqDetail != null && floatingRateReqDetail.getFltReqLnId() != null ? new ResponseData(this.floatingRateReqDetailService.select(requestContext, floatingRateReqDetail, pagenum, pagesize)) : new ResponseData(false, "请求参数有误");
    }

    @RequestMapping({"/con/floating/ln/history/query"})
    @ResponseBody
    public ResponseData historyQuery(@ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize, HttpServletRequest request) throws NoSuchObjectException {
        IRequest requestContext = this.createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusConFloatingRateReq metadataRelation = (HlsCusConFloatingRateReq) param.toJavaObject(HlsCusConFloatingRateReq.class);
        return metadataRelation != null && metadataRelation.getQuotationId() != null ? new ResponseData(this.service.queryHistory(requestContext, metadataRelation, page, pageSize)) : new ResponseData(false, "请求参数有误");
    }

    @RequestMapping({"/con/floating/ln/create/rateChange"})
    @ResponseBody
    public ResponseData createRateChange(HttpServletRequest request, HlsCusConFloatingRateReq floatingRateReq) {
        IRequest requestContext = this.createRequestContext(request);
        Long companyId = requestContext.getCompanyId();
        floatingRateReq.setCompanyId(companyId);
        String description = (String) requestContext.getAttribute("description");
        if (StringUtils.isBlank(description)) {
            return new ResponseData(false, "请输入调息说明");
        } else {
            ResponseData responseData = new ResponseData(true);

            try {
                HlsCusConFloatingRateReq floatingRateChange = this.service.createFloatingRateChangeList(requestContext);
                responseData.setRows(Collections.singletonList(floatingRateChange));
            } catch (Exception var9) {
                responseData.setSuccess(false);
                responseData.setMessage(var9.getMessage());
            }

            return responseData;
        }
    }

    @RequestMapping({"/con/floating/ln/create/contract"})
    @ResponseBody
    public ResponseData createRateLnContract(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData ,HlsCusConFloatingRateReq floatingRateReq,@RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize)throws Exception {
        IRequest requestContext = this.createRequestContext(request);
        Long companyId = requestContext.getCompanyId();
        floatingRateReq.setCompanyId(companyId);

        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsCusPrjQuotation> prjQuotationList = parameter.toJavaList(HlsCusPrjQuotation.class);

        HlsCusPrjQuotation metadataRelation = prjQuotationList.get(0);
        ResponseData responseData = new ResponseData(true);
        try {
            // 判断是否存在新建状态的调息
            List<HlsCusPrjQuotation> hlsCusPrjQuotations = hlsCusPrjQuotationService.queryQuotationRateReqNew(requestContext, metadataRelation, pagenum, pagesize);
            if("NEW".equals(hlsCusPrjQuotations.get(0).getFltStatus())){
                throw new IllegalArgumentException("已经是新建状态的调息单只需进行计算，无需再次创建调息！");
            }
            List<HlsCusConFloatingRateReqLn> cusConFloatingRateReqLnList = this.service.createFloatingRateQuotation( requestContext , prjQuotationList);
            responseData.setRows(Collections.singletonList(cusConFloatingRateReqLnList));
        } catch (Exception var9) {
            responseData.setSuccess(false);
            responseData.setMessage(var9.getMessage());
        }
        return responseData;

    }

    @RequestMapping({"/con/floating/ln/cal/rateChange"})
    @ResponseBody
    public Map<String, Object> calRateChange(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request) throws Exception {
        String fltReqId = request.getParameter("fltReqId");
        List<Long> quotationIds = new ArrayList();
        Map<String, Object> response = new HashMap<String, Object>();
        if (StringUtil.isEmpty(fltReqId)) {
            response.put("false" , "false");
            response.put("false" , "请求参数有误,请联系管理员。[fltReqId] is null");
//            return new ResponseData(false, "请求参数有误,请联系管理员。[fltReqId] is null");
        } else {
            List parameterList = requestData.getParameterList();
            if (CollectionUtils.isNotEmpty(parameterList)) {
                for (Object contractId : parameterList) {
                    quotationIds.add(Long.valueOf(contractId.toString()));
                }
            }

            IRequest requestContext = this.createRequestContext(request);

            response = service.calRateChange(requestContext, Long.valueOf(fltReqId), quotationIds);
        }
        return response;
    }

    @RequestMapping({"/con/floating/ln/cancel/rateChange"})
    @ResponseBody
    public ResponseData cancelRateChange(HttpServletRequest request, HlsCusConFloatingRateReq floatingRateReq) {
        IRequest requestContext = this.createRequestContext(request);
        ResponseData responseData = new ResponseData(true);

        try {
            this.service.cancelFloatingRateReq(requestContext, floatingRateReq);
        } catch (Exception var6) {
            responseData.setSuccess(false);
            responseData.setMessage(var6.getMessage());
        }

        return responseData;
    }
}
