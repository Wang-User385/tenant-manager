package com.hand.hls.csh.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.fin.dto.HlsCusLonContractWithdraw;
import leaf.bean.LeafRequestData;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HlsCusConContractCashflowController extends BaseController {

    @Autowired
    private HlsCusConContractCashflowService service;
    @Autowired
    private HlsCusConContractCashflowMapper mapper;

    @RequestMapping(value = "/ct/csh/write/off/confirm")
    @ResponseBody
    public ResponseData confirmFullWriteOff(@RequestParam("contractId") Long contractId, @RequestParam("writeOffType") String writeOffType, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        List<String> list = new ArrayList<>(1);
        list.add(service.confirmFullWriteOff(requestContext, contractId, writeOffType));
        return new ResponseData(list);
    }

    /*租赁放款申请*/
    @RequestMapping(value = "/hls/cus/prj/quotation/and/cashflow/submit")
    @ResponseBody
    public ResponseData contractGenerate(Long projectId, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.contractGenerate(requestContext, projectId));
    }

    //还款计划现金流
    @RequestMapping(value = "/ct/con/contract/rep/cashflow/query")
    @ResponseBody
    public ResponseData queryContractRepCashflowByProjectId(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusConContractCashflow dto = param.toJavaObject(HlsCusConContractCashflow.class);
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryContractRepCashflowByContractId(dto, page, pageSize));
    }

    /**
     * 租赁合同现金流
     *
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/hls/cus/con/cash/flow/query")
    @ResponseBody
    public ResponseData prjQueryCashFlow(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                         @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusConContractCashflow dto = param.toJavaObject(HlsCusConContractCashflow.class);
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.conQueryCashFlow(dto, page, pageSize));
    }



    /**
     * 保理合同放款申请页面查询前期款收款信息
     *
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/con/contract/cashflow/queryConLoanRequest")
    @ResponseBody
    public ResponseData queryForLoanRequest(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusConContractCashflow dto = param.toJavaObject(HlsCusConContractCashflow.class);
        return new ResponseData(service.selectConLoanRequest(requestContext, dto, page, pageSize));
    }


    @RequestMapping(value = "/ct/csh/fine/info/query")
    @ResponseBody
    public ResponseData queryCshFineInfoList(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                             @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusConContractCashflow dto = param.toJavaObject(HlsCusConContractCashflow.class);
        return new ResponseData(service.queryCshFineInfoList(requestContext, dto, page, pageSize));
    }


    @RequestMapping(value = "/ct/csh/fine/info/attachment")
    @ResponseBody
    public ResponseData queryCshFineDetailInfo1(Long contractId, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        service.createCshFineAttachment(requestContext,contractId);
        return new ResponseData();
    }


    @RequestMapping(value = "/ct/csh/fine/info/submit/wfl")
    @ResponseBody
    public ResponseData fineCshSubmit(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusConContractCashflow>  dto = param.toJavaList(HlsCusConContractCashflow.class);
        return new ResponseData(service.fineCshSubmit(requestContext,dto));
    }

    @RequestMapping(value = "/prj/factoring/invoice/query")
    @ResponseBody
    public ResponseData queryFactoringInvoice(HttpServletRequest request, @RequestBody HlsCusConContractCashflow dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryFactoringInvoice(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/prj/factoring/invoice/query/by/user")
    @ResponseBody
    public ResponseData query4LonConWithdrawList(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,  @RequestParam(defaultValue = DEFAULT_PAGE) int pageNum,
                                                 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusConContractCashflow dto = param.toJavaObject(HlsCusConContractCashflow.class);
        String userId = requestContext.getAttributeMap().get("user_id").toString();
        dto.setUserId(Long.valueOf(userId));
        return new ResponseData(service.queryFactoringInvoice(requestContext, dto, pageNum, pageSize));
    }


    @RequestMapping(value = "/cashflow/info/by/contract/query")
    @ResponseBody
    public ResponseData selectCashflowInfoByContract(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusConContractCashflow dto = param.toJavaObject(HlsCusConContractCashflow.class);
        //需求全部查询 不分页
        return new ResponseData(mapper.selectCashflowInfoByContract(dto));
    }
    @RequestMapping(value = "/con/contract/cashflow/queryRpt")
    @ResponseBody
    public ResponseData queryRpt(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                 HttpServletRequest request, HttpServletResponse response,
                                 HlsCusConContractCashflow hlsCusConContractCashflow,
                                 @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusConContractCashflow metadataRelation = param.toJavaObject(HlsCusConContractCashflow.class);
        IRequest requestCtx = createRequestContext(request);

//        if (hlsCusCshTransaction.getTransactionId() != null) {
//            metadataRelation.setTransactionId(hlsCusCshTransaction.getTransactionId());
//        }
//        if (hlsCusCshTransaction.getSourceDocId() != null) {
//            metadataRelation.setSourceDocId(hlsCusCshTransaction.getSourceDocId());
//        }
//        if (hlsCusCshTransaction.getTransactionType() != null) {
//            metadataRelation.setTransactionType(hlsCusCshTransaction.getTransactionType());
//        }
//        if (hlsCusCshTransaction.getSourceDocCategory() != null) {
//            metadataRelation.setSourceDocCategory(hlsCusCshTransaction.getSourceDocCategory());
//        }
        List<HlsCusConContractCashflow> list = service.queryForRealIncomeReport(requestCtx, metadataRelation, pagenum, pagesize);
        return new ResponseData(list);
    }

    //关税现金流导入
    @RequestMapping(value = "/hls/cus/cont/tariff/cashflow/import", method = RequestMethod.POST)
    public Map<String, Object> lonContractRepaymentImport(HttpServletRequest request, Long headerId ) throws IOException {
        IRequest iRequest = createRequestContext(request);
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("success", false);
        try {
            service.contTariffCashflowImport(iRequest, headerId);
            response.put("message", "导入成功");
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "导入失败！" + e.getMessage());
        }
        return response;
    }


}
