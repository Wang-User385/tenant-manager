package com.hand.hls.partner.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.calc.exception.ChangeLimitException;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.csh.dto.CshAllocation;
import com.hand.hls.csh.dto.CshAllocationCredit;
import com.hand.hls.csh.dto.CshAllocationReceipt;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.csh.exception.BeyondAmountLimitException;
import com.hand.hls.csh.exception.WriteOffTypeNullException;
import com.hand.hls.csh.mapper.CshAllocationCreditMapper;
import com.hand.hls.csh.mapper.CshAllocationMapper;
import com.hand.hls.csh.mapper.CshAllocationReceiptMapper;
import com.hand.hls.csh.mapper.HlsCusCshTransactionMapper;
import com.hand.hls.csh.mapper.HlsCusCshWriteOffMapper;
import com.hand.hls.csh.service.CshWriteOffService;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.partner.dto.YLCshTransferPaymentDto;
import com.hand.hls.partner.mapper.YLCshTransferPaymentDtoMapper;
import com.hand.hls.partner.service.IYLCshTransferPaymentService;
import com.hand.hls.sys.utils.OracleUtils;
import com.hand.hls.utils.HlsCusMathUtil;
import com.hand.hls.utils.MathUtil;
import com.hand.hls.utils.ResMessageException;
import com.hand.hls.web.logs.dto.HlsWsRequests;
import com.hand.hls.web.logs.mapper.HlsWsRequestsMapper;
import hls.core.utils.exception.HlsCusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Transactional(rollbackFor = Exception.class)
public class YLCshTransferPaymentServiceImpl extends BaseServiceImpl<YLCshTransferPaymentDto> implements IYLCshTransferPaymentService {


    @Autowired
    private YLCshTransferPaymentDtoMapper ylCshTransferPaymentDtoMapper;

    @Autowired
    private HlsCusCshTransactionMapper hlsCusCshTransactionMapper;

    @Autowired
    private HlsCusConContractCashflowMapper hlsCusConContractCashflowMapper;

    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;

    @Autowired
    private HlsWsRequestsMapper hlsWsRequestsMapper;

    @Autowired
    private HlsCusCshWriteOffMapper hlsCusCshWriteOffMapper;

    @Autowired
    private CshAllocationMapper cshAllocationMapper;

    @Autowired
    private CshAllocationReceiptMapper cshAllocationReceiptMapper;

    @Autowired
    private CshAllocationCreditMapper cshAllocationCreditMapper;

    @Autowired
    private CshWriteOffService cshWriteOffService;


    public static final String DOCUMENT_CATEGORY = "CSH_TRANSACTION";

    public static final String DOCUMENT_TYPE = "RECEIPT";

    public static final String BUSINESS_TYPE = "RECEIPT";

    public static final String ALLOCATION_DOCUMENT_CATEGORY = "CSH_TRX";

    public static final String ALLOCATION_DOCUMENT_TYPE = "ALLOCATION";

    public static final String ALLOCATION_BUSINESS_TYPE = "ALLOCATION";

    @Override
    public List<YLCshTransferPaymentDto> updateTransferStatus(IRequest requestCtx, List<YLCshTransferPaymentDto> list) {
        List<YLCshTransferPaymentDto> res = new ArrayList<>();
        for (YLCshTransferPaymentDto ylCshTransferPaymentDto : list) {
            YLCshTransferPaymentDto ylCshTransferPaymentDto1 = new YLCshTransferPaymentDto();
            ylCshTransferPaymentDto1.setPaymentId(ylCshTransferPaymentDto.getPaymentId());
            ylCshTransferPaymentDto1 =
                    ylCshTransferPaymentDtoMapper.selectByPrimaryKey(ylCshTransferPaymentDto1);
            ylCshTransferPaymentDto1.setTransferPaymentStatus("CANCEL");
            ylCshTransferPaymentDtoMapper.updateByPrimaryKey(ylCshTransferPaymentDto1);
            res.add(ylCshTransferPaymentDto1);
        }
        return res;
    }

    @Override
    public List<YLCshTransferPaymentDto> updateAndVerification(IRequest requestCtx, HttpServletRequest request, List<YLCshTransferPaymentDto> ylCshTransferPaymentDtoList) {
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        ResponseData responseData = new ResponseData();
        ylCshTransferPaymentDtoList.forEach(ylCshTransferPaymentDto -> {
            commonLogHead(hlsWsRequests, "转付确认", ylCshTransferPaymentDto, request);
            //生产现金事务数据
            HlsCusCshTransaction hlsCusCshTransaction = getHlsCusCshTransaction(requestCtx, ylCshTransferPaymentDto);
            //插入现金事务数据
            hlsCusCshTransactionMapper.insertSelective(hlsCusCshTransaction);

            writeOffYL(requestCtx, request, hlsWsRequests, responseData, ylCshTransferPaymentDto, hlsCusCshTransaction);
            //更新转付确认表
            ylCshTransferPaymentDto.setConfirmDate(new Date());
            ylCshTransferPaymentDto.setTransferPaymentStatus("CONFIRMED");
            ylCshTransferPaymentDtoMapper.updateByPrimaryKey(ylCshTransferPaymentDto);
        });
        return ylCshTransferPaymentDtoList;
    }

    private void writeOffYL(IRequest requestCtx, HttpServletRequest request, HlsWsRequests hlsWsRequests, ResponseData responseData, YLCshTransferPaymentDto ylCshTransferPaymentDto, HlsCusCshTransaction hlsCusCshTransaction) {
        //核销
        //查出合同现金流数据
        HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
        hlsCusConContractCashflow.setCashflowId(ylCshTransferPaymentDto.getCashflowId());
        hlsCusConContractCashflow = hlsCusConContractCashflowMapper.selectByPrimaryKey(hlsCusConContractCashflow);
        Example example = new Example(HlsCusCshWriteOff.class);
        example.createCriteria().andEqualTo("cshTransactionId", hlsCusCshTransaction.getTransactionId());
        List<HlsCusCshWriteOff> res = hlsCusCshWriteOffMapper.selectByExample(example);
        try {
            //如果没有就加一个
            setHlsCusWriteOffList(ylCshTransferPaymentDto, hlsCusCshTransaction, hlsCusConContractCashflow, res);
            cshWriteOffService.writeOff(requestCtx, res, request.getSession());
        } catch (Exception e) {

            commonLog(responseData, "10001", "E", "核销报错", hlsWsRequests);
            throw new RuntimeException("转付确认异常");
        }
    }

    private void setHlsCusWriteOffList(YLCshTransferPaymentDto ylCshTransferPaymentDto, HlsCusCshTransaction hlsCusCshTransaction, HlsCusConContractCashflow hlsCusConContractCashflow, List<HlsCusCshWriteOff> res) {
        if (res.size() == 0){
            HlsCusCshWriteOff hlsCusCshWriteOff = new HlsCusCshWriteOff();
            hlsCusCshWriteOff.setContractId(ylCshTransferPaymentDto.getContractId());
            hlsCusCshWriteOff.setCshTransactionId(hlsCusCshTransaction.getTransactionId());
            //设置核销类型
            hlsCusCshWriteOff.setWriteOffType("RECEIPT_CREDIT");
            //设置核销时间
            hlsCusCshWriteOff.setWriteOffDate(new Date());
            //设置现金事务核销金额
            hlsCusCshWriteOff.setCshWriteOffAmount(hlsCusCshTransaction.getWriteOffAmount());
            //设置反冲标志
            hlsCusCshWriteOff.setReversedFlag("N");
            //设置现金流id
            hlsCusCshWriteOff.setCashflowId(hlsCusConContractCashflow.getCashflowId());
            //设置期次
            hlsCusCshWriteOff.setTimes(ylCshTransferPaymentDto.getTimes());
            //设置现金流项目
            hlsCusCshWriteOff.setCfItem(hlsCusConContractCashflow.getCfItem());
            //设置现金流类型
            hlsCusCshWriteOff.setCfType(hlsCusConContractCashflow.getCfType());
            //设置核销金额
            hlsCusCshWriteOff.setWriteOffDueAmount(ylCshTransferPaymentDto.getRepayAmount());
            //设置核销利息
            hlsCusCshWriteOff.setWriteOffInterest(ylCshTransferPaymentDto.getRepayInterest());
            //设置核销本金
            hlsCusCshWriteOff.setWriteOffPrincipal(ylCshTransferPaymentDto.getRepayPrincipal());
            //设置核销单据类别
            hlsCusCshWriteOff.setWriteOffDocCategory("CON_CONTRACT");
            //导入标识
            hlsCusCshWriteOff.setImportFlag("N");
            //首次支付设备款标识
            hlsCusCshWriteOff.setFirstLeasePayFlag("Y");
            //系统字段
            hlsCusCshWriteOff.setCreationDate(new Date());
            hlsCusCshWriteOff.setCreatedBy(hlsCusCshTransaction.getCreatedBy());
            hlsCusCshWriteOff.setLastUpdateDate(new Date());
            hlsCusCshWriteOff.setLastUpdatedBy(hlsCusCshTransaction.getLastUpdatedBy());
            res.add(hlsCusCshWriteOff);
        }
    }

    public HlsCusCshTransaction getHlsCusCshTransaction(IRequest requestCtx, YLCshTransferPaymentDto ylCshTransferPaymentDto) {
        HlsCusCshTransaction hlsCusCshTransaction = new HlsCusCshTransaction();
        Map<String, String> params = new HashMap<>();
        String transactionNum = fndCodingRuleValuesService.getCodeRuleValue(requestCtx, DOCUMENT_CATEGORY, DOCUMENT_TYPE, BUSINESS_TYPE, params);
        hlsCusCshTransaction.setTransactionCategory(DOCUMENT_CATEGORY);
        hlsCusCshTransaction.setTransactionType(DOCUMENT_TYPE);
        hlsCusCshTransaction.setBusinessType(BUSINESS_TYPE);
        hlsCusCshTransaction.setTransactionNum(transactionNum);
        hlsCusCshTransaction.setTransactionDate(new Date());
        hlsCusCshTransaction.setPenaltyCalcDate(hlsCusCshTransaction.getTransactionDate());
        hlsCusCshTransaction.setCompanyId(requestCtx.getCompanyId());
        hlsCusCshTransaction.setTransactionAmount(ylCshTransferPaymentDto.getRepayAmount());
        hlsCusCshTransaction.setContractId(ylCshTransferPaymentDto.getContractId());
        hlsCusCshTransaction.setWriteOffAmount(new BigDecimal("0").doubleValue());
        hlsCusCshTransaction.setReversedFlag("N");
        hlsCusCshTransaction.setPostedFlag("N");
        hlsCusCshTransaction.setCurrencyCode("CNY");
        //系统字段
        hlsCusCshTransaction.setCreationDate(new Date());
        hlsCusCshTransaction.setCreatedBy(requestCtx.getUserId());
        hlsCusCshTransaction.setLastUpdateDate(new Date());
        hlsCusCshTransaction.setLastUpdatedBy(requestCtx.getUserId());
        return hlsCusCshTransaction;
    }


    private void commonLog(ResponseData responseData, String code, String returnStatus, String parameter, HlsWsRequests hlsWsRequests) {
        responseData.setCode(code);
        responseData.setMessage(parameter);
        hlsWsRequests.setReturnStatus(returnStatus);
        hlsWsRequests.setResponseJson(JSON.toJSONString(responseData));
        hlsWsRequestsMapper.insert(hlsWsRequests);
    }

    private void commonLogHead(HlsWsRequests hlsWsRequests, String functionName, Object param, HttpServletRequest request) {
        //获取请求路径
        String requestURI = request.getRequestURI();
        hlsWsRequests.setRequestWsdlUrl(requestURI);
        //请求日期
        hlsWsRequests.setRequestDate(new Date());
        //功能名称
        hlsWsRequests.setFunctionName(functionName);
        //状态变更日期
        hlsWsRequests.setStatusDate(new Date());
        // user_id
        String userId = request.getParameter("user_id");
        if (userId != null) {
            hlsWsRequests.setUserId(Long.valueOf(userId));
        }
        //请求状态
        hlsWsRequests.setStatusCode("200");
        //参数类型
        hlsWsRequests.setParameterType("JSON");
        // 请求体
        String s = JSONObject.toJSONString(param);
        hlsWsRequests.setRequestJson(s);
    }





//    public List<HlsCusCshWriteOff> checkCshWriteOffs(List<HlsCusCshWriteOff> cshWriteOffs) throws BeyondAmountLimitException {
//
//        if (cshWriteOffs != null) {
//            Double sumAmount = 0D;
//
//            for (HlsCusCshWriteOff cshWriteOff : cshWriteOffs) {
//                if (cshWriteOff.getSurplusAmount() != null && cshWriteOff.getWriteOffDueAmount() != null) {
//                    /**
//                     * 当核销金额大于预收款金额，抛出异常
//                     */
//                    if (cshWriteOff.getWriteOffDueAmount() > cshWriteOff.getSurplusAmount()) {
//                        throw new BeyondAmountLimitException();
//                    }
//
//                    sumAmount = MathUtil.add(sumAmount, cshWriteOff.getWriteOffDueAmount());
//                }
//
//            }
//            List<HlsCusCshTransaction> cshTransactions = hlsCusCshTransactionMapper.queryDetailByIdList(cshWriteOffs.get(0).getCshTransactionId());
//            HlsCusCshTransaction transaction = cshTransactions.get(0);
//            /**
//             * 当核销金额总和大于收款金额时，抛出异常
//             */
//            if (transaction.getWriteOffAmount() == null) {
//                transaction.setWriteOffAmount(0d);
//            }
//
//            Double sum = MathUtil.add(sumAmount, transaction.getWriteOffAmount());
//
//            if (sum.compareTo(transaction.getTransactionAmount()) == 1) {
//                throw new BeyondAmountLimitException();
//            }
//        }
//
//
//        return cshWriteOffs;
//    }
//
//    public void updateComContractCashflowAndInsertCshTransaction(IRequest requestCtx, HlsWsRequests hlsWsRequests, ResponseData responseData, YLCshTransferPaymentDto ylCshTransferPaymentDto, HlsCusCshTransaction hlsCusCshTransaction) {
//        //查出合同现金流数据
//        HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
//        hlsCusConContractCashflow.setCashflowId(ylCshTransferPaymentDto.getCashflowId());
//        hlsCusConContractCashflow = hlsCusConContractCashflowMapper.selectByPrimaryKey(hlsCusConContractCashflow);
//        //设置为是转付
//        hlsCusConContractCashflow.setTransferPaymentFlag("Y");
//        //应收金额
//        Double dueAmount = hlsCusConContractCashflow.getDueAmount();
//        //已收金额
//        Double receivedAmount = hlsCusConContractCashflow.getReceivedAmount();
//        //不存在应收小于已收 逾期和罚息是新的现金流
//        //已收加还款总金额
//        Double amount = HlsCusMathUtil.add(receivedAmount, ylCshTransferPaymentDto.getRepayAmount());
//        //应收  == 已收加还款
//        if (HlsCusMathUtil.compare(dueAmount, amount) == -1) {
//            commonLog(responseData, "10001", "E", "已收金额大于应收", hlsWsRequests);
//            throw new RuntimeException("已收金额大于应收");
//        }
//        //设置核销字段
//        if (HlsCusMathUtil.compare(dueAmount, amount) == 0) {
//            //完全核销
//            hlsCusCshTransaction.setWriteOffFlag("FULL");
//            hlsCusConContractCashflow.setWriteOffFlag("FULL");
//        } else {
//            //部分核销
//            hlsCusCshTransaction.setWriteOffFlag("PARTIAL");
//            hlsCusConContractCashflow.setWriteOffFlag("PARTIAL");
//        }
//        //设置核销金额
//        hlsCusCshTransaction.setWriteOffAmount(HlsCusMathUtil.add(hlsCusCshTransaction.getUnWriteOffAmount(), ylCshTransferPaymentDto.getRepayAmount()));
//        //设置还款后的已收金额
//        hlsCusConContractCashflow.setReceivedAmount(HlsCusMathUtil.add(hlsCusConContractCashflow.getReceivedAmount(), ylCshTransferPaymentDto.getRepayPrincipal()));
//        //设置还款后的已收金额
//        hlsCusConContractCashflow.setReceivedAmount(amount);
//        //设置还款后已收利息
//        hlsCusConContractCashflow.setReceivedInterest(HlsCusMathUtil.add(hlsCusConContractCashflow.getReceivedInterest(), ylCshTransferPaymentDto.getRepayInterest()));
//        //更新核销表信息
//        HlsCusCshWriteOff hlsCusCshWriteOff = updateHlsCusWriteOff(ylCshTransferPaymentDto, hlsCusCshTransaction, hlsCusConContractCashflow);
//        //更新核销匹配表信息
//        updateWriteOffMatch(requestCtx, hlsCusCshTransaction, hlsCusConContractCashflow, hlsCusCshWriteOff);
//        //插入现金事务数据
//        hlsCusCshTransactionMapper.insertSelective(hlsCusCshTransaction);
//        //更新现金事务流数据
//        hlsCusConContractCashflowMapper.updateByPrimaryKey(hlsCusConContractCashflow);
//    }
//
//    private void updateWriteOffMatch(IRequest requestCtx, HlsCusCshTransaction hlsCusCshTransaction, HlsCusConContractCashflow hlsCusConContractCashflow, HlsCusCshWriteOff hlsCusCshWriteOff) {
//        CshAllocation cshAllocation = new CshAllocation();
//        Map<String, String> params = new HashMap<>();
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//        String allocationNumber = fndCodingRuleValuesService.getCodeRuleValue(requestCtx, ALLOCATION_DOCUMENT_CATEGORY, ALLOCATION_DOCUMENT_TYPE, ALLOCATION_BUSINESS_TYPE, params);
//        cshAllocation.setAllocationNumber(allocationNumber);
//        try {
//            cshAllocation.setAllocationDate(df.parse(df.format(new Date())));
//        } catch (ParseException e) {
//            throw new RuntimeException("时间格式异常");
//        }
//        cshAllocation.setAllocationSource("AUTO");
//        cshAllocation.setAllocationStatus("N");
//        //系统字段
//        cshAllocation.setCreationDate(new Date());
//        cshAllocation.setCreatedBy(requestCtx.getUserId());
//        cshAllocation.setLastUpdateDate(new Date());
//        cshAllocation.setLastUpdatedBy(requestCtx.getUserId());
//        //将现金事务表与核销匹配表关联
//        Long allocationId = cshAllocation.getAllocationId();
//        CshAllocationReceipt cshAllocationReceipt = new CshAllocationReceipt();
//        cshAllocationReceipt.setAllocationId(allocationId);
//        cshAllocationReceipt.setTransactionId(hlsCusCshTransaction.getTransactionId());
//        cshAllocationReceipt.setAdvanceReceiptAmount(OracleUtils.nvl(hlsCusCshTransaction.getAdvanceReceiptAmount(), 0.0));
//        //将匹配表与现金事务流表关联
//        CshAllocationCredit cshAllocationCredit = new CshAllocationCredit();
//        cshAllocationCredit.setAllocationId(allocationId);
//        cshAllocationCredit.setCashflowId(hlsCusConContractCashflow.getCashflowId());
//        cshAllocationCredit.setDueAmount(hlsCusCshWriteOff.getWriteOffDueAmount());
//        cshAllocationCredit.setPrincipal(hlsCusConContractCashflow.getPrincipal());
//        cshAllocationCredit.setInterest(hlsCusConContractCashflow.getInterest());
//        //插入核销匹配表
//        cshAllocationMapper.insertSelective(cshAllocation);
//        cshAllocationReceiptMapper.insertSelective(cshAllocationReceipt);
//        cshAllocationCreditMapper.insertSelective(cshAllocationCredit);
//    }
//
//    private HlsCusCshWriteOff updateHlsCusWriteOff(YLCshTransferPaymentDto ylCshTransferPaymentDto, HlsCusCshTransaction hlsCusCshTransaction, HlsCusConContractCashflow hlsCusConContractCashflow) {
//        HlsCusCshWriteOff hlsCusCshWriteOff = hlsCusCshWriteOffMapper.
//                getHlsCusCshWriteOffByContractIdAndTransactionId
//                        (ylCshTransferPaymentDto.getContractId(), hlsCusCshTransaction.getTransactionId());
//        if (hlsCusCshWriteOff == null) {
//            hlsCusCshWriteOff = new HlsCusCshWriteOff();
//            hlsCusCshWriteOff.setContractId(ylCshTransferPaymentDto.getContractId());
//            hlsCusCshWriteOff.setTransactionId(hlsCusCshTransaction.getTransactionId());
//            //设置核销类型
//            hlsCusCshWriteOff.setWriteOffType("RECEIPT_CREDIT");
//            //设置核销时间
//            hlsCusCshWriteOff.setWriteOffDate(new Date());
//            //设置现金事务核销金额
//            hlsCusCshWriteOff.setCshWriteOffAmount(hlsCusCshTransaction.getWriteOffAmount());
//            //设置反冲标志
//            hlsCusCshWriteOff.setReversedFlag("N");
//            //设置现金流id
//            hlsCusCshWriteOff.setCashflowId(hlsCusConContractCashflow.getCashflowId());
//            //设置期次
//            hlsCusCshWriteOff.setTimes(ylCshTransferPaymentDto.getTimes());
//            //设置现金流项目
//            hlsCusCshWriteOff.setCfItem(hlsCusConContractCashflow.getCfItem());
//            //设置现金流类型
//            hlsCusCshWriteOff.setCfType(hlsCusConContractCashflow.getCfType());
//            //设置核销金额
//            hlsCusCshWriteOff.setWriteOffDueAmount(ylCshTransferPaymentDto.getRepayAmount());
//            //设置还款利息
//            hlsCusCshWriteOff.setWriteOffDueAmount(ylCshTransferPaymentDto.getRepayInterest());
//            //设置还款本金
//            hlsCusCshWriteOff.setWriteOffPrincipal(ylCshTransferPaymentDto.getRepayPrincipal());
//            //设置核销单据类别
//            hlsCusCshWriteOff.setWriteOffDocCategory("CON_CONTRACT");
//            //导入标识
//            hlsCusCshWriteOff.setImportFlag("N");
//            //首次支付设备款标识
//            hlsCusCshWriteOff.setFirstLeasePayFlag("Y");
//            //系统字段
//            hlsCusCshWriteOff.setCreationDate(new Date());
//            hlsCusCshWriteOff.setCreatedBy(hlsCusCshTransaction.getCreatedBy());
//            hlsCusCshWriteOff.setLastUpdateDate(new Date());
//            hlsCusCshWriteOff.setLastUpdatedBy(hlsCusCshTransaction.getLastUpdatedBy());
//            hlsCusCshWriteOffMapper.insertSelective(hlsCusCshWriteOff);
//        } else {
//            //设置核销时间
//            hlsCusCshWriteOff.setWriteOffDate(new Date());
//            //设置现金事务核销金额
//            hlsCusCshWriteOff.setCshWriteOffAmount(hlsCusCshTransaction.getWriteOffAmount());
//            //设置核销金额
//            hlsCusCshWriteOff.setWriteOffDueAmount(HlsCusMathUtil.add(ylCshTransferPaymentDto.getRepayAmount(), hlsCusCshWriteOff.getCshWriteOffAmount()));
//            //设置还款利息
//            hlsCusCshWriteOff.setWriteOffInterest(HlsCusMathUtil.add(ylCshTransferPaymentDto.getRepayInterest(), hlsCusCshWriteOff.getWriteOffInterest()));
//            //设置还款本金
//            hlsCusCshWriteOff.setWriteOffPrincipal(HlsCusMathUtil.add(ylCshTransferPaymentDto.getRepayPrincipal(), hlsCusCshWriteOff.getWriteOffPrincipal()));
//            //首次支付设备款标识
//            hlsCusCshWriteOff.setFirstLeasePayFlag("N");
//            //系统字段
//            hlsCusCshWriteOff.setLastUpdateDate(new Date());
//            hlsCusCshWriteOff.setLastUpdatedBy(hlsCusCshTransaction.getLastUpdatedBy());
//            hlsCusCshWriteOffMapper.updateByPrimaryKey(hlsCusCshWriteOff);
//        }
//
//        return hlsCusCshWriteOff;
//    }



}