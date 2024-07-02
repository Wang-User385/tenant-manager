//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.csh.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.bp.components.CalculateUtil;
import com.hand.hls.calc.exception.ChangeLimitException;
import com.hand.hls.csh.dto.*;
import com.hand.hls.csh.exception.BeyondAmountLimitException;
import com.hand.hls.csh.exception.WriteOffTypeNullException;
import com.hand.hls.csh.mapper.*;
import com.hand.hls.csh.service.*;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.utils.HlsCusMathUtil;
import com.hand.hls.utils.MathUtil;
import com.hand.hls.utils.ResMessageException;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class CshWriteOffController extends BaseController {
    @Autowired
    private CshWriteOffService cshWriteOffService;
    @Autowired
    private CshPaymentReqLnService iCshPaymentReqLnService;
    @Autowired
    private HlsCusCshPaymentReqDtMapper cshPaymentReqDtMapper;


    @Autowired
    private HlsCusCshTransactionMapper hlsCusCshTransactionMapper;

    @Autowired
    private HlsCusCshTransactionMapper transactionMapper;


    @Autowired
    private HlsCusWriteOffMatchMapper hlsCusWriteOffMatchMapper;

    @Autowired
    private CshTransactionService cshTransactionService;


    public CshWriteOffController() {
    }


    @RequestMapping(value = "/csh/write/off/paymentReversed/submit", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData paymentReversed(@ModelAttribute("_request_data") LeafRequestData requestData, HttpSession session, BindingResult result, HttpServletRequest request) throws ChangeLimitException {
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(this.getErrorMessage(result, request));
            return responseData;
        } else {
            JSONObject param = (JSONObject) requestData.get("parameter");
            HlsCusCshWriteOff cshWriteOff = param.toJavaObject(HlsCusCshWriteOff.class);
            IRequest requestContext = createRequestContext(request);
            cshWriteOffService.paymentWriteOffReversed(requestContext, cshWriteOff, session);
            return new ResponseData();
        }
    }

    @RequestMapping({"/csh/write/off/all/Details"})
    @ResponseBody
    public ResponseData selectAllWriteOff(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request, HttpSession session) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        Map map = param.toJavaObject(Map.class);
        IRequest iRequest = createRequestContext(request);
        List<Map> data = cshWriteOffService.selectAllWriteOff(map, session, iRequest);
        return new ResponseData(data);
    }

    @RequestMapping({"/csh/writeOff/CancelAfterVerification/query"})
    @ResponseBody
    public ResponseData CancelAfterVerification(@ModelAttribute("_request_data") LeafRequestData requestData) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusCshWriteOff cshWriteOff = param.toJavaObject(HlsCusCshWriteOff.class);
        List<HlsCusCshWriteOff> list = cshWriteOffService.selectAllCancelAfterVerificationDetail(cshWriteOff);
        return new ResponseData(list);
    }

    @RequestMapping({"/csh/write/off/payment/submit"})
    @ResponseBody
    public ResponseData submitPayment(@ModelAttribute("_request_data") LeafRequestData requestData, BindingResult result, HttpServletRequest request, HttpSession session) throws BeyondAmountLimitException {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusCshWriteOff hlsCusCshWriteOff = param.toJavaObject(HlsCusCshWriteOff.class);
        List<HlsCusCshWriteOff> cshTransactions = hlsCusCshWriteOff.getHlsCusCshWriteOffList();
        Double amout = 0.0D;
        int payFlag = 0;
        ResponseData responseData = null;
        if (result.hasErrors()) {
            responseData = new ResponseData(false);
            responseData.setMessage(this.getErrorMessage(result, request));
            return responseData;
        } else {
            HlsCusCshPaymentReqLn cshPaymentReqLn = new HlsCusCshPaymentReqLn();
            List<HlsCusCshPaymentReqLn> payLnList = new ArrayList();

            int i;
            for (i = 0; i < cshTransactions.size(); ++i) {
                Double OffAmount = ((HlsCusCshWriteOff) cshTransactions.get(i)).getCshWriteOffAmount();
                cshPaymentReqLn.setPaymentReqId(((HlsCusCshWriteOff) cshTransactions.get(i)).getPaymentReqId());
                List<HlsCusCshPaymentReqLn> cshPLnList = iCshPaymentReqLnService.queryByHnId(cshPaymentReqLn);
                Double deductAmount = 0.0D;
                if (cshPLnList.size() > 0) {
                    int j = 0;

                    while (true) {
                        if (j >= cshPLnList.size()) {
                            ((HlsCusCshWriteOff) cshTransactions.get(i)).setCshWriteOffAmount(CalculateUtil.sub(OffAmount, deductAmount));
                            break;
                        }

                        HlsCusCshPaymentReqLn hlsCusCshPaymentReqLn = (HlsCusCshPaymentReqLn) cshPLnList.get(j);
                        payLnList.add(hlsCusCshPaymentReqLn);
                        Long payment_req_ln_id = hlsCusCshPaymentReqLn.getPaymentReqLnId();
                        HlsCusCshPaymentReqDt cshPaymentReqDt = new HlsCusCshPaymentReqDt();
                        cshPaymentReqDt.setPaymentReqLnId(payment_req_ln_id);
                        List<HlsCusCshPaymentReqDt> cshPaymentReqDts = cshPaymentReqDtMapper.select(cshPaymentReqDt);

                        HlsCusCshPaymentReqDt item;
                        for (Iterator var19 = cshPaymentReqDts.iterator(); var19.hasNext(); deductAmount = CalculateUtil.add(deductAmount, item.getDeductAmount())) {
                            item = (HlsCusCshPaymentReqDt) var19.next();
                        }

                        ++j;
                    }
                }

                if (OffAmount != null) {
                    amout = CalculateUtil.add(OffAmount, amout);
                }
            }

            for (i = 0; i < payLnList.size(); ++i) {
                if ("FULL".equalsIgnoreCase(((HlsCusCshPaymentReqLn) payLnList.get(i)).getPaymentFlag())) {
                    ++payFlag;
                }
            }

            ResponseData rd = null;
        /*    if (payFlag == payLnList.size()) {
                rd = new ResponseData(false);
                rd.setMessage("该付款已被支付");
                return rd;
            } else {*/
//                List<HlsCusCshWriteOff> cshTransactionsDeduct=new ArrayList<>();
//                if(cshTransactions.size()>0){
//                    for (HlsCusCshWriteOff hlsCusCshWriteOff:cshTransactions){
//                        cshTransactionsDeduct.add(hlsCusCshWriteOff);
//                    }
//                }
            try {
                cshWriteOffService.transactionWriteOffDeduct(RequestHelper.getCurrentRequest(), cshTransactions);

                cshWriteOffService.updateWriteOff(RequestHelper.getCurrentRequest(), cshTransactions, session);

            } catch (Exception var21) {
                responseData = new ResponseData(false);
                responseData.setMessage(var21.getMessage());
                return responseData;
            }

            List<Double> list = new ArrayList();
            list.add(amout);
            return new ResponseData(list);
//            }
        }
    }


    @RequestMapping(value = "/csh/write/off/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                              HttpServletRequest request, HttpServletResponse response,
                              HlsCusCshWriteOff hlsCusCshWriteOff,
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


        HlsCusCshWriteOff metadataRelation = param.toJavaObject(HlsCusCshWriteOff.class);

        IRequest requestCtx = createRequestContext(request);

        if (hlsCusCshWriteOff.getCshTransactionId() != null) {
            metadataRelation.setCshTransactionId(hlsCusCshWriteOff.getCshTransactionId());
        }
        if (hlsCusCshWriteOff.getNonRefundFlag() != null) {
            metadataRelation.setNonRefundFlag(hlsCusCshWriteOff.getNonRefundFlag());
        }

        if (hlsCusCshWriteOff.getRefundFlag() != null) {
            metadataRelation.setRefundFlag(hlsCusCshWriteOff.getRefundFlag());
        }

        if (hlsCusCshWriteOff.getTransactionType() != null) {
            metadataRelation.setTransactionType(hlsCusCshWriteOff.getTransactionType());
        }


        List<HlsCusCshWriteOff> list = cshWriteOffService.queryWriteOff(requestCtx, metadataRelation, pagenum, pagesize, sortName, sortOrder);
        return new ResponseData(list);
    }


    @RequestMapping({"/csh/write/off/submit"})
    @ResponseBody
    public ResponseData submit(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request, HttpSession session) throws BeyondAmountLimitException, IllegalArgumentException, WriteOffTypeNullException, ChangeLimitException, ResMessageException {
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(this.getErrorMessage(result, request));
            return responseData;
        } else {
            JSONArray param = (JSONArray) requestData.get("parameter");
            List<HlsCusCshWriteOff> cshWriteOffList = param.toJavaList(HlsCusCshWriteOff.class);
            HlsCusCshTransaction cshTransaction = new HlsCusCshTransaction();
            cshTransaction.setTransactionId(cshWriteOffList.get(0).getCshTransactionId());
            List<HlsCusCshWriteOff> cshWriteOffs = this.getCshWriteOffs(cshTransaction);
            this.cshWriteOffService.updateWriteOff(RequestHelper.getCurrentRequest(), cshWriteOffs, session);
            return new ResponseData();
        }
    }

    //付款申请支付
    @RequestMapping(value = "/csh/write/off/payment")
    @ResponseBody
    public ResponseData payment(HttpServletRequest request,
                                @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpSession session) throws Exception {

//        JSONArray parameter = (JSONArray) requestData.get("parameter");
//        List<HlsCusCshPaymentReqLn> hlsCusCshPaymentReqLnList = parameter.toJavaList(HlsCusCshPaymentReqLn.class);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusCshPaymentTran cusCshPaymentTran = param.toJavaObject(HlsCusCshPaymentTran.class);

        cshWriteOffService.payment(createRequestContext(request), cusCshPaymentTran, session);
        return new ResponseData();

    }

    /**
     * 二期功能：退款申请支付
     * @param request
     * @param requestData
     * @param session
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/csh/write/off/refund/payment")
    @ResponseBody
    public ResponseData refundPayment(HttpServletRequest request,
                                @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpSession session) throws Exception {

        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusCshPaymentTran cusCshPaymentTran = param.toJavaObject(HlsCusCshPaymentTran.class);

        cshWriteOffService.refundPayment(createRequestContext(request), cusCshPaymentTran, session);
        return new ResponseData();

    }

    //核销入口 4.2
    @RequestMapping(value = "/csh/write/off/submit/new")
    @ResponseBody
    public ResponseData submit(HttpServletRequest request,
                               @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpSession session) throws BeyondAmountLimitException, IllegalArgumentException, WriteOffTypeNullException, ChangeLimitException, ResMessageException {

        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsCusCshWriteOff> hlsCusCshWriteOffs = parameter.toJavaList(HlsCusCshWriteOff.class);
        checkCshWriteOffs(hlsCusCshWriteOffs);
        cshWriteOffService.writeOff(createRequestContext(request), hlsCusCshWriteOffs, session);
        return new ResponseData();
    }


    public List<HlsCusCshWriteOff> checkCshWriteOffs(List<HlsCusCshWriteOff> cshWriteOffs) throws BeyondAmountLimitException {

        if (cshWriteOffs != null) {
            Double sumAmount = 0D;

            for (HlsCusCshWriteOff cshWriteOff : cshWriteOffs) {
                if (cshWriteOff.getSurplusAmount() != null && cshWriteOff.getWriteOffDueAmount() != null) {
                    /**
                     * 当核销金额大于预收款金额，抛出异常
                     */
                    if (cshWriteOff.getWriteOffDueAmount() > cshWriteOff.getSurplusAmount()) {
                        throw new BeyondAmountLimitException();
                    }

                    sumAmount = MathUtil.add(sumAmount, cshWriteOff.getWriteOffDueAmount());
                }

            }
            List<HlsCusCshTransaction> cshTransactions = hlsCusCshTransactionMapper.queryDetailByIdList(cshWriteOffs.get(0).getCshTransactionId());
            HlsCusCshTransaction transaction = cshTransactions.get(0);
            /**
             * 当核销金额总和大于收款金额时，抛出异常
             */
            if (transaction.getWriteOffAmount() == null) {
                transaction.setWriteOffAmount(0d);
            }

            Double sum = MathUtil.add(sumAmount, transaction.getWriteOffAmount());

            if (sum.compareTo(transaction.getTransactionAmount()) == 1) {
                throw new BeyondAmountLimitException();
            }
        }


        return cshWriteOffs;
    }

    /**
     * 核销改成从匹配取数
     *
     * @param cshTransaction
     * @return
     * @throws BeyondAmountLimitException
     */
    public List<HlsCusCshWriteOff> getCshWriteOffs(HlsCusCshTransaction cshTransaction) throws BeyondAmountLimitException {
        HlsCusWriteOffMatch hlsCusWriteOffMatch = new HlsCusWriteOffMatch();
        hlsCusWriteOffMatch.setCshTransactionId(cshTransaction.getTransactionId());
        cshTransaction = hlsCusCshTransactionMapper.selectByPrimaryKey(cshTransaction.getTransactionId());

        List<HlsCusWriteOffMatch> writeOffMatches = hlsCusWriteOffMatchMapper.selectWriteOffMatch(hlsCusWriteOffMatch);
        List<HlsCusCshWriteOff> cshWriteOffs = new ArrayList();
        if (cshTransaction != null) {
            Iterator var3;
            if (writeOffMatches != null && writeOffMatches.size() > 0) {
                var3 = writeOffMatches.iterator();
                while (var3.hasNext()) {
                    HlsCusWriteOffMatch match = (HlsCusWriteOffMatch) var3.next();
                    HlsCusCshWriteOff cshWriteOff = new HlsCusCshWriteOff();
                    cshWriteOff.setCfItem(match.getCfItem());
                    cshWriteOff.setCfType(match.getCfType());
                    cshWriteOff.setTimes(match.getTimes());
                    cshWriteOff.setCfItemDesc(match.getCfItemDesc());
                    cshWriteOff.setWriteOffDate(match.getWriteOffDate());
                    cshWriteOff.setWriteOffDocCategory(match.getWriteOffDocCategory());
                    cshWriteOff.setWriteOffPrincipal(match.getWriteOffPrincipal());
                    cshWriteOff.setWriteOffInterest(match.getWriteOffInterest());
                    cshWriteOff.setWriteOffDueAmount(match.getWriteOffDueAmount());
                    cshWriteOff.setCshWriteOffAmount(match.getCshWriteOffAmount());
                    cshWriteOff.setContractId(match.getContractId());
                    cshWriteOff.setContractNumber(match.getContractNumber());
                    cshWriteOff.setCashflowId(match.getCashflowId());
                    cshWriteOff.setWriteOffType(match.getWriteOffType());
                    cshWriteOff.setWriteOffMatchId(match.getWriteOffMatchId());
                    cshWriteOff.setBpId(cshTransaction.getBpId());
                    cshWriteOff.setBpName(cshTransaction.getBpName());
                    cshWriteOff.setCurrencyCode(cshTransaction.getCurrencyCode());
                    cshWriteOff.setCshTransactionId(cshTransaction.getTransactionId());
                    cshWriteOff.setTransactionId(cshTransaction.getTransactionId());
                    cshWriteOff.setWriteOffDocCategory(match.getWriteOffDocCategory());
                    cshWriteOff.setCompanyId(cshTransaction.getCompanyId());
                    cshWriteOff.setImportFlag(match.getImportFlag());
                    cshWriteOffs.add(cshWriteOff);
                }
            }
        }

        if (cshWriteOffs != null) {
            long sumAmount = 0L;
            Iterator var5 = cshWriteOffs.iterator();

            while (var5.hasNext()) {
                HlsCusCshWriteOff cshWriteOff = (HlsCusCshWriteOff) var5.next();
                if (cshWriteOff.getSurplusAmount() != null && cshWriteOff.getWriteOffDueAmount() != null) {
                    if (cshWriteOff.getWriteOffDueAmount() > cshWriteOff.getSurplusAmount()) {
                        throw new BeyondAmountLimitException();
                    }

                    sumAmount = (long) ((double) sumAmount + cshWriteOff.getWriteOffDueAmount());
                }

                cshWriteOff.setCshTransactionId(cshTransaction.getTransactionId());

            }

            List<HlsCusCshTransaction> cshTransactions = this.transactionMapper.queryDetailByIdList(cshTransaction.getTransactionId());
            HlsCusCshTransaction transaction = (HlsCusCshTransaction) cshTransactions.get(0);
            if (transaction.getWriteOffAmount() == null) {
                transaction.setWriteOffAmount(0.0D);
            }

            Double sum = (double) sumAmount + transaction.getWriteOffAmount();
            if (sum > transaction.getTransactionAmount()) {
                throw new BeyondAmountLimitException();
            }
        }

        return cshWriteOffs;
    }

    //兴业核销入口 分配确认 一阶段 没有 核销保证金池
    @RequestMapping({"/csh/write/off/allocation/confirm"})
    @ResponseBody
    public ResponseData allocationConfirm(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request, HttpSession session) throws Exception{

        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(this.getErrorMessage(result, request));
            return responseData;
        } else {

            IRequest iRequest = createRequestContext(request);
            JSONArray param = (JSONArray) requestData.get("parameter");
            List<HlsCusCshTransaction> cshTransactionList = param.toJavaList(HlsCusCshTransaction.class);
            cshWriteOffService.allocationConfirm(iRequest, cshTransactionList, session);
            return new ResponseData();
        }
    }

    @RequestMapping({"/csh/write/off/allocation/save"})
    @ResponseBody
    public ResponseData allocationSave(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request, HttpSession session) throws Exception{

        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(this.getErrorMessage(result, request));
            return responseData;
        } else {

            IRequest iRequest = createRequestContext(request);
            JSONArray param = (JSONArray) requestData.get("parameter");
            List<HlsCusCshTransaction> cshTransactionList = param.toJavaList(HlsCusCshTransaction.class);
            cshWriteOffService.allocationSave(iRequest, cshTransactionList, session);
            return new ResponseData();
        }
    }

    //核销反冲入口
    @RequestMapping(value = "/csh/write/off/reverse")
    @ResponseBody
    public ResponseData reverse(HttpServletRequest request,
                                @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpSession session) throws Exception {

        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsCusCshWriteOff> hlsCusCshWriteOffs = parameter.toJavaList(HlsCusCshWriteOff.class);
        checkCshWriteOffs(hlsCusCshWriteOffs);
        IRequest iRequest = createRequestContext(request);
        cshWriteOffService.writeOffReversed(iRequest, hlsCusCshWriteOffs, session);
        return new ResponseData();
    }

}
