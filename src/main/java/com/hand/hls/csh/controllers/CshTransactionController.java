package com.hand.hls.csh.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.dto.HlsCusCshTransactionRefund;
import com.hand.hls.csh.exception.BeyondAmountLimitException;
import com.hand.hls.csh.mapper.HlsCusCshTransactionMapper;
import com.hand.hls.csh.mapper.HlsCusCshTransactionRefundMapper;
import com.hand.hls.csh.service.CshTransactionService;
import com.hand.hls.utils.HlsConstantUtil;
import com.hand.hls.utils.ResMessageException;
import leaf.bean.LeafRequestData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 现金事务控制器
 */
@Controller
public class CshTransactionController extends BaseController {

    @Autowired
    private CshTransactionService service;

    @Autowired
    private HlsCusCshTransactionMapper cshTransactionMapper;
    private static final String ADMIN = "ADMIN";

    @RequestMapping({"/csh/cshTransaction/collectionDetails"})
    @ResponseBody
    public ResponseData collectionDetails(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request, HttpServletResponse response, HlsCusCshTransaction hlsCusCshTransaction, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        IRequest requestCtx = this.createRequestContext(request);
        HlsCusCshTransaction metadataRelation = param.toJavaObject(HlsCusCshTransaction.class);
        return new ResponseData(this.service.collectionDetails(requestCtx, metadataRelation, pagenum, pagesize));
    }


    @RequestMapping({"/csh/cshTransaction/cashThingTransaction/queryDetail"})
    @ResponseBody
    public ResponseData CashThingTransactionQueryDetail(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        Map map = param.toJavaObject(Map.class);
        List<Map> list = this.service.CashThingTransactionQueryDetail(map);
        return new ResponseData(list);
    }


    @RequestMapping(value = "/csh/transaction/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                              HttpServletRequest request, HttpServletResponse response,
                              HlsCusCshTransaction hlsCusCshTransaction,
                              @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        String sortName = null;
        String sortOrder = null;
        if (param.get("sort_name") != null) {
            sortName = param.get("sort_name").toString();
        }
        if (param.get("sort_name") != null) {
            sortOrder = param.get("sort_order").toString();
        }

        HlsCusCshTransaction metadataRelation = param.toJavaObject(HlsCusCshTransaction.class);
        IRequest requestCtx = createRequestContext(request);

        if (hlsCusCshTransaction.getTransactionId() != null) {
            metadataRelation.setTransactionId(hlsCusCshTransaction.getTransactionId());
        }
        if (hlsCusCshTransaction.getSourceDocId() != null) {
            metadataRelation.setSourceDocId(hlsCusCshTransaction.getSourceDocId());
        }
        if (hlsCusCshTransaction.getTransactionType() != null) {
            metadataRelation.setTransactionType(hlsCusCshTransaction.getTransactionType());
        }
        if (hlsCusCshTransaction.getSourceDocCategory() != null) {
            metadataRelation.setSourceDocCategory(hlsCusCshTransaction.getSourceDocCategory());
        }
        List<HlsCusCshTransaction> list = service.queryCshTransaction(requestCtx, metadataRelation, pagenum, pagesize, sortName, sortOrder);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/csh/transaction/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusCshTransaction> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    //收款新增
    @RequestMapping(value = "/csh/transaction/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,
                               @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpSession session) throws ResMessageException {

        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsCusCshTransaction> hlsCusCshTransactions = parameter.toJavaList(HlsCusCshTransaction.class);
        IRequest requestCtx = createRequestContext(request);
        List<HlsCusCshTransaction> list = new ArrayList<>();

        HlsCusCshTransaction hlsCusCshTransaction = hlsCusCshTransactions.get(0);

        if ("Y".equals(hlsCusCshTransaction.getPostedFlag())) {
            service.createCshTransaction(requestCtx, hlsCusCshTransaction);
            list.add(hlsCusCshTransaction);
            list = service.postCshTransaction(requestCtx, list);
        } else {
            hlsCusCshTransaction = service.createCshTransaction(requestCtx, hlsCusCshTransaction);
            list.add(hlsCusCshTransaction);
        }
        return new ResponseData(list);
    }

    //收款删除
    @RequestMapping({"/csh/transaction/delete"})
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData, HttpSession session) {
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsCusCshTransaction> metadataRelations = parameter.toJavaList(HlsCusCshTransaction.class);
        IRequest requestCtx = this.createRequestContext(request);
        return new ResponseData(this.service.batchUpdate(requestCtx, metadataRelations));
    }

    //收款过账
    @RequestMapping(value = "/csh/transaction/post")
    @ResponseBody
    public ResponseData post(HttpServletRequest request,
                             @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpSession session) throws ResMessageException {

        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsCusCshTransaction> hlsCusCshTransactions = parameter.toJavaList(HlsCusCshTransaction.class);
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.postCshTransaction(requestCtx, hlsCusCshTransactions));
    }

    //收款反冲
    @RequestMapping(value = "/csh/transaction/reverse")
    @ResponseBody
    public ResponseData reverse(HttpServletRequest request,
                                @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpSession session) throws ResMessageException {

        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsCusCshTransaction> hlsCusCshTransactions = parameter.toJavaList(HlsCusCshTransaction.class);
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.reverseCshTransaction(requestCtx, hlsCusCshTransactions));
    }

    //收款退款
    @RequestMapping(value = "/csh/transaction/refund")
    @ResponseBody
    public ResponseData refund(HttpServletRequest request,
                               @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpSession session) throws BeyondAmountLimitException, ResMessageException {

        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsCusCshTransactionRefund> cshTransactionRefundList = parameter.toJavaList(HlsCusCshTransactionRefund.class);
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.refundCshTransaction(requestCtx, cshTransactionRefundList));
    }

    //收款退款申请
    @RequestMapping(value = "/csh/transaction/refund/submit")
    @ResponseBody
    public ResponseData refundSubmit(HttpServletRequest request,
                                     @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpSession session) throws BeyondAmountLimitException, ResMessageException {

        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsCusCshTransaction> hlsCusCshTransactions = parameter.toJavaList(HlsCusCshTransaction.class);
        IRequest requestCtx = createRequestContext(request);
        service.refundCshTransactionSubmit(requestCtx, session, hlsCusCshTransactions);
        return new ResponseData();
    }


    //兴业 收款 二开

    //收款明细查询
    @RequestMapping({"/csh/transaction/detail/query"})
    @ResponseBody
    public ResponseData detailQuery(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request, HttpServletResponse response, HlsCusCshTransaction hlsCusCshTransaction, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        IRequest requestCtx = this.createRequestContext(request);
        HlsCusCshTransaction metadataRelation = param.toJavaObject(HlsCusCshTransaction.class);

        String sortName = null;
        String sortOrder = null;
        if (param.get("sort_name") != null) {
            sortName = param.get("sort_name").toString();
        }
        if (param.get("sort_name") != null) {
            sortOrder = param.get("sort_order").toString();
        }


        String transactionIdStr = metadataRelation.getTransactionIdStr();

        if (transactionIdStr != null && !"".equals(transactionIdStr)) {
            List<Long> transactionIdS = new ArrayList<>();

            String[] str = transactionIdStr.split(",");

            for (int i = 0; i < str.length; i++) {
                if (!"undefined".equals(str[i])) {
                    transactionIdS.add(Long.parseLong(str[i]));
                }
            }
            if (transactionIdS.size() > 0) {
                metadataRelation.setTransactionIdS(transactionIdS);
            }
        }

        //用来接收setLovPara 参数
        String notTransactionIdStr = hlsCusCshTransaction.getNotTransactionIdStr();

        if (notTransactionIdStr != null && !"".equals(notTransactionIdStr)) {
            List<Long> notTransactionIdS = new ArrayList<>();

            String[] str = notTransactionIdStr.split(",");

            for (int i = 0; i < str.length; i++) {
                if (!"undefined".equals(str[i])) {
                    notTransactionIdS.add(Long.parseLong(str[i]));
                }
            }
            if (notTransactionIdS.size() > 0) {
                metadataRelation.setNotTransactionIdS(notTransactionIdS);
            }
        }


        return new ResponseData(this.service.detailQuery(requestCtx, metadataRelation, pagenum, pagesize, sortName, sortOrder));
    }
    //收款明细查询
    @RequestMapping({"/csh/transaction/detail/queryNew"})
    @ResponseBody
    public ResponseData detailQueryNew(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request, HttpServletResponse response, HlsCusCshTransaction hlsCusCshTransaction, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        IRequest requestCtx = this.createRequestContext(request);
        HlsCusCshTransaction metadataRelation = param.toJavaObject(HlsCusCshTransaction.class);

        String sortName = null;
        String sortOrder = null;
        if (param.get("sort_name") != null) {
            sortName = param.get("sort_name").toString();
        }
        if (param.get("sort_name") != null) {
            sortOrder = param.get("sort_order").toString();
        }


        String transactionIdStr = metadataRelation.getTransactionIdStr();

        if (transactionIdStr != null && !"".equals(transactionIdStr)) {
            List<Long> transactionIdS = new ArrayList<>();

            String[] str = transactionIdStr.split(",");

            for (int i = 0; i < str.length; i++) {
                if (!"undefined".equals(str[i])) {
                    transactionIdS.add(Long.parseLong(str[i]));
                }
            }
            if (transactionIdS.size() > 0) {
                metadataRelation.setTransactionIdS(transactionIdS);
            }
        }

        //用来接收setLovPara 参数
        String notTransactionIdStr = hlsCusCshTransaction.getNotTransactionIdStr();

        if (notTransactionIdStr != null && !"".equals(notTransactionIdStr)) {
            List<Long> notTransactionIdS = new ArrayList<>();

            String[] str = notTransactionIdStr.split(",");

            for (int i = 0; i < str.length; i++) {
                if (!"undefined".equals(str[i])) {
                    notTransactionIdS.add(Long.parseLong(str[i]));
                }
            }
            if (notTransactionIdS.size() > 0) {
                metadataRelation.setNotTransactionIdS(notTransactionIdS);
            }
        }


        return new ResponseData(this.service.detailQueryNew(requestCtx, metadataRelation, pagenum, pagesize, sortName, sortOrder));
    }


    //收款明细查询
    @RequestMapping({"/csh/transaction/detail/detailQueryNewBusiness"})
    @ResponseBody
    public ResponseData detailQueryNewBusiness(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request, HttpServletResponse response, HlsCusCshTransaction hlsCusCshTransaction, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        IRequest requestCtx = this.createRequestContext(request);
        HlsCusCshTransaction metadataRelation = param.toJavaObject(HlsCusCshTransaction.class);

        String sortName = null;
        String sortOrder = null;
        if (param.get("sort_name") != null) {
            sortName = param.get("sort_name").toString();
        }
        if (param.get("sort_name") != null) {
            sortOrder = param.get("sort_order").toString();
        }


        String transactionIdStr = metadataRelation.getTransactionIdStr();

        if (transactionIdStr != null && !"".equals(transactionIdStr)) {
            List<Long> transactionIdS = new ArrayList<>();

            String[] str = transactionIdStr.split(",");

            for (int i = 0; i < str.length; i++) {
                if (!"undefined".equals(str[i])) {
                    transactionIdS.add(Long.parseLong(str[i]));
                }
            }
            if (transactionIdS.size() > 0) {
                metadataRelation.setTransactionIdS(transactionIdS);
            }
        }

        //用来接收setLovPara 参数
        String notTransactionIdStr = hlsCusCshTransaction.getNotTransactionIdStr();

        if (notTransactionIdStr != null && !"".equals(notTransactionIdStr)) {
            List<Long> notTransactionIdS = new ArrayList<>();

            String[] str = notTransactionIdStr.split(",");

            for (int i = 0; i < str.length; i++) {
                if (!"undefined".equals(str[i])) {
                    notTransactionIdS.add(Long.parseLong(str[i]));
                }
            }
            if (notTransactionIdS.size() > 0) {
                metadataRelation.setNotTransactionIdS(notTransactionIdS);
            }
        }


        return new ResponseData(this.service.detailQueryNewBusiness(requestCtx, metadataRelation, pagenum, pagesize, sortName, sortOrder));
    }

    //收款明细查询
    @RequestMapping({"/csh/transaction/detail/detailQueryNewFinance"})
    @ResponseBody
    public ResponseData detailQueryNewFinance(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request, HttpServletResponse response, HlsCusCshTransaction hlsCusCshTransaction, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        IRequest requestCtx = this.createRequestContext(request);
        HlsCusCshTransaction metadataRelation = param.toJavaObject(HlsCusCshTransaction.class);

        String sortName = null;
        String sortOrder = null;
        if (param.get("sort_name") != null) {
            sortName = param.get("sort_name").toString();
        }
        if (param.get("sort_name") != null) {
            sortOrder = param.get("sort_order").toString();
        }


        String transactionIdStr = metadataRelation.getTransactionIdStr();

        if (transactionIdStr != null && !"".equals(transactionIdStr)) {
            List<Long> transactionIdS = new ArrayList<>();

            String[] str = transactionIdStr.split(",");

            for (int i = 0; i < str.length; i++) {
                if (!"undefined".equals(str[i])) {
                    transactionIdS.add(Long.parseLong(str[i]));
                }
            }
            if (transactionIdS.size() > 0) {
                metadataRelation.setTransactionIdS(transactionIdS);
            }
        }

        //用来接收setLovPara 参数
        String notTransactionIdStr = hlsCusCshTransaction.getNotTransactionIdStr();

        if (notTransactionIdStr != null && !"".equals(notTransactionIdStr)) {
            List<Long> notTransactionIdS = new ArrayList<>();

            String[] str = notTransactionIdStr.split(",");

            for (int i = 0; i < str.length; i++) {
                if (!"undefined".equals(str[i])) {
                    notTransactionIdS.add(Long.parseLong(str[i]));
                }
            }
            if (notTransactionIdS.size() > 0) {
                metadataRelation.setNotTransactionIdS(notTransactionIdS);
            }
        }


        return new ResponseData(this.service.detailQueryNewFinance(requestCtx, metadataRelation, pagenum, pagesize, sortName, sortOrder));
    }


    @RequestMapping({"/csh/transaction/detail/queryAdvance"})
    @ResponseBody
    public ResponseData detailQueryAdvance(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request, HttpServletResponse response, HlsCusCshTransaction hlsCusCshTransaction, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        IRequest requestCtx = this.createRequestContext(request);
        HlsCusCshTransaction metadataRelation = param.toJavaObject(HlsCusCshTransaction.class);

        String sortName = null;
        String sortOrder = null;
        if (param.get("sort_name") != null) {
            sortName = param.get("sort_name").toString();
        }
        if (param.get("sort_name") != null) {
            sortOrder = param.get("sort_order").toString();
        }


        String transactionIdStr = metadataRelation.getTransactionIdStr();

        if (transactionIdStr != null && !"".equals(transactionIdStr)) {
            List<Long> transactionIdS = new ArrayList<>();

            String[] str = transactionIdStr.split(",");

            for (int i = 0; i < str.length; i++) {
                if (!"undefined".equals(str[i])) {
                    transactionIdS.add(Long.parseLong(str[i]));
                }
            }
            if (transactionIdS.size() > 0) {
                metadataRelation.setTransactionIdS(transactionIdS);
            }
        }

        //用来接收setLovPara 参数
        String notTransactionIdStr = hlsCusCshTransaction.getNotTransactionIdStr();

        if (notTransactionIdStr != null && !"".equals(notTransactionIdStr)) {
            List<Long> notTransactionIdS = new ArrayList<>();

            String[] str = notTransactionIdStr.split(",");

            for (int i = 0; i < str.length; i++) {
                if (!"undefined".equals(str[i])) {
                    notTransactionIdS.add(Long.parseLong(str[i]));
                }
            }
            if (notTransactionIdS.size() > 0) {
                metadataRelation.setNotTransactionIdS(notTransactionIdS);
            }
        }


        return new ResponseData(this.service.detailQueryAdvance(requestCtx, metadataRelation, pagenum, pagesize, sortName, sortOrder));
    }
    // 收款新增导入
    @RequestMapping(value = "/csh/transaction/receipt/import", method = RequestMethod.POST)
    public Map<String, Object> receiptImport(HttpServletRequest request, Long headerId,String templateCode,Long readLine) throws IOException {
        IRequest iRequest = createRequestContext(request);
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("success", false);
        try {
            service.receiptImport(iRequest, headerId,templateCode,readLine);
            response.put("message", "导入成功");
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "导入失败！" + e.getMessage());
        }
        return response;
    }

    // 收款新增(兴业银行)导入
    @RequestMapping(value = "/csh/transaction/cashflow/import", method = RequestMethod.POST)
    public Map<String, Object> cashflowImport(HttpServletRequest request, Long headerId) throws IOException {
        IRequest iRequest = createRequestContext(request);
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("success", false);
        try {
            service.cashflowImport(iRequest, headerId);
            response.put("message", "导入成功");
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "导入失败！" + e.getMessage());
        }
        return response;
    }

    @RequestMapping(value = "/search/csh/home/query")
    @ResponseBody
    public ResponseData searchCshTransation(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                            HttpServletRequest request, HttpServletResponse response,
                                            HlsCusCshTransaction hlsCusCshTransaction,
                                            @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusCshTransaction metadataRelation = param.toJavaObject(HlsCusCshTransaction.class);
        IRequest requestCtx = createRequestContext(request);

        List<HlsCusCshTransaction> list = service.searchCshTransation(requestCtx, metadataRelation, pagenum, pagesize);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/csh/transaction/query/new")
    @ResponseBody
    public ResponseData refundQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                              HttpServletRequest request, HttpServletResponse response,
                              HlsCusCshTransaction hlsCusCshTransaction,
                              @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusCshTransaction metadataRelation = param.toJavaObject(HlsCusCshTransaction.class);
        IRequest requestCtx = createRequestContext(request);
        if (hlsCusCshTransaction.getTransactionId() != null) {
            metadataRelation.setTransactionId(hlsCusCshTransaction.getTransactionId());
        }
        List<HlsCusCshTransaction> list = service.queryCshTransactionNew(requestCtx, metadataRelation, pagenum, pagesize);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/csh/transaction/query/lov")
    @ResponseBody
    public ResponseData queryLov(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                 HttpServletRequest request, String notInTransactionIds, String transactionType,Long paymentBpId,
                                 @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) throws ParseException {
        JSONObject param = (JSONObject) requestData.get(HlsConstantUtil.BaseController.PARAMETER);
        HlsCusCshTransaction metadataRelation = param.toJavaObject(HlsCusCshTransaction.class);
        IRequest requestCtx = createRequestContext(request);
        requestCtx.setAttribute("authorityRuleFlag", "N");
        if (StringUtils.isNotEmpty(transactionType)) {
            metadataRelation.setTransactionTypes(transactionType.split(","));
        }
        if(null != paymentBpId){
            metadataRelation.setPaymentBpId(paymentBpId);
        }
        if (StringUtils.isNotEmpty(notInTransactionIds)) {
            String[] transactionIdsStr = notInTransactionIds.split(",");
            List<Long> transactionIds = new ArrayList<>(transactionIdsStr.length);
            for (int i = 0; i < transactionIdsStr.length; i++) {
                if (StringUtils.isNotEmpty(transactionIdsStr[i])) {
                    transactionIds.add(Long.valueOf(transactionIdsStr[i]));
                }
            }
            metadataRelation.setTransactionIdS(transactionIds);
        }
        List<HlsCusCshTransaction> list = service.queryLov(requestCtx, metadataRelation, pagenum, pagesize);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/csh/cashflow/query/Adlov")
    @ResponseBody
    public ResponseData queryCashflowAdLov(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                 HttpServletRequest request, String notInCashflowIds,
                                           String writeOffFlag,
                                           String bpBankAccountName,
                                           String manufacturerId,
                                           String transactionType,
                                 @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) throws ParseException {
        JSONObject param = (JSONObject) requestData.get(HlsConstantUtil.BaseController.PARAMETER);
        HlsCusCshTransactionRefund metadataRelation = param.toJavaObject(HlsCusCshTransactionRefund.class);
        IRequest requestCtx = createRequestContext(request);
        requestCtx.setAttribute("authorityRuleFlag", "N");
        if(StringUtils.isNotEmpty(writeOffFlag)){
            metadataRelation.setWriteOffFlag(writeOffFlag);
        }
        if(StringUtils.isNotEmpty(bpBankAccountName)){
            metadataRelation.setBpBankAccountName(bpBankAccountName);
        }
        if(StringUtils.isNotEmpty(manufacturerId)){
            metadataRelation.setManufacturerId(Long.valueOf(manufacturerId));
        }
        if (StringUtils.isNotEmpty(notInCashflowIds)) {
            String[] cashflowIdsStr = notInCashflowIds.split(",");
            List<Long> notInCashflowIdList = new ArrayList<>(cashflowIdsStr.length);
            for (int i = 0; i < cashflowIdsStr.length; i++) {
                if (StringUtils.isNotEmpty(cashflowIdsStr[i])) {
                    notInCashflowIdList.add(Long.valueOf(cashflowIdsStr[i]));
                }
            }
            metadataRelation.setCashflowIdS(notInCashflowIdList);
        }
        List<HlsCusCshTransactionRefund> list = service.queryCashflowRefundAdLov(requestCtx, metadataRelation, pagenum, pagesize);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/csh/transaction/deductQuery")
    @ResponseBody
    public ResponseData deductQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                    HttpServletRequest request, HttpServletResponse response,
                                    HlsCusCshTransaction hlsCusCshTransaction,
                                    @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                    @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusCshTransaction metadataRelation = param.toJavaObject(HlsCusCshTransaction.class);
        IRequest requestCtx = createRequestContext(request);

        requestCtx.setAttribute("wflRuleControlFlag", "Y");
        requestCtx.setAttribute("authorityRuleFlag", "N");

        if (hlsCusCshTransaction.getTransactionId() != null) {
            metadataRelation.setTransactionId(hlsCusCshTransaction.getTransactionId());
        }
        if (hlsCusCshTransaction.getSourceDocId() != null) {
            metadataRelation.setSourceDocId(hlsCusCshTransaction.getSourceDocId());
        }
        if (hlsCusCshTransaction.getTransactionType() != null) {
            metadataRelation.setTransactionType(hlsCusCshTransaction.getTransactionType());
        }
        if (hlsCusCshTransaction.getSourceDocCategory() != null) {
            metadataRelation.setSourceDocCategory(hlsCusCshTransaction.getSourceDocCategory());
        }
        List<HlsCusCshTransaction> list = cshTransactionMapper.queryDeductCshTransaction(metadataRelation);
        return new ResponseData(list);
    }

    /**
     * 保证金抵扣方式维护查询
     * @param requestData LeafRequestData
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param hlsCusCshTransaction HlsCusCshTransaction
     * @param pagenum int
     * @param pagesize int
     * @return
     */
    @RequestMapping(value = "/csh/deposit/deduct/method/query")
    @ResponseBody
    public ResponseData queryDepositDeductMethod(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                                 HttpServletRequest request, HttpServletResponse response,
                                                 HlsCusCshTransaction hlsCusCshTransaction,
                                                 @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                                 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusCshTransaction metadataRelation = param.toJavaObject(HlsCusCshTransaction.class);
        IRequest requestCtx = createRequestContext(request);
        requestCtx.setAttribute("authorityRuleFlag", "N");

        //判断当前用户是否为管理员
        /*if(contractService.isCompanyManageRole(requestCtx,ADMIN)){
            metadataRelation.setRoleCode(ADMIN);
        }*/
        if (hlsCusCshTransaction.getTransactionId() != null) {
            metadataRelation.setTransactionId(hlsCusCshTransaction.getTransactionId());
        }
        if (hlsCusCshTransaction.getSourceDocId() != null) {
            metadataRelation.setSourceDocId(hlsCusCshTransaction.getSourceDocId());
        }
        if (hlsCusCshTransaction.getTransactionType() != null) {
            metadataRelation.setTransactionType(hlsCusCshTransaction.getTransactionType());
        }
        if (hlsCusCshTransaction.getSourceDocCategory() != null) {
            metadataRelation.setSourceDocCategory(hlsCusCshTransaction.getSourceDocCategory());
        }
        List<HlsCusCshTransaction> list = service.queryDepositDeductMethod(requestCtx, metadataRelation, pagenum, pagesize);
        return new ResponseData(list);
    }

    /**
     * 更新抵扣方式
     */
    @RequestMapping(value = "/csh/deposit/deduct/method/save")
    @ResponseBody
    public ResponseData updateDepositDeductMethod(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                                  HttpServletRequest request, HttpServletResponse response) throws ResMessageException {
        JSONObject param = (JSONObject) requestData.get("parameter");
        JSONArray jsonArray = param.getJSONArray("transactionList");
        List<HlsCusCshTransaction> transactionList = jsonArray.toJavaList(HlsCusCshTransaction.class);
        IRequest requestCtx = createRequestContext(request);
        requestCtx.setAttribute("authorityRuleFlag", "N");
        service.updateDepositDeductMethod(requestCtx,transactionList);
        return new ResponseData(true);
    }


    //导入
    @RequestMapping(value = "/csh/transaction/import", method = RequestMethod.POST)
    public Map<String, Object> transactionImport(HttpServletRequest request, @RequestParam("headerId") Long headerId) throws IOException {
        IRequest iRequest = createRequestContext(request);
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("success", false);
        try {
            service.transactionImport(iRequest, headerId);
            response.put("message", "导入成功");
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "导入失败！" + e.getMessage());
        }
        return response;
    }

}
