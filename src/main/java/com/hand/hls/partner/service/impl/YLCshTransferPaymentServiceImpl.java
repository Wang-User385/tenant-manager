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
import org.springframework.util.CollectionUtils;

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
    private CshAllocationCreditMapper cshAllocationCreditMapper;

    @Autowired
    private CshAllocationReceiptMapper cshAllocationReceiptMapper;


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

            ylCshTransferPaymentDto =
                    ylCshTransferPaymentDtoMapper.selectByPrimaryKey(ylCshTransferPaymentDto);
            ylCshTransferPaymentDto.setTransferPaymentStatus("CANCEL");
            ylCshTransferPaymentDtoMapper.updateByPrimaryKey(ylCshTransferPaymentDto);
            HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
            //更新现金事务流转付状态
            hlsCusConContractCashflow.setCashflowId(ylCshTransferPaymentDto.getCashflowId());
            hlsCusConContractCashflow = hlsCusConContractCashflowMapper.selectByPrimaryKey(hlsCusConContractCashflow);
            hlsCusConContractCashflow.setTransferPaymentFlag("N");
            hlsCusConContractCashflowMapper.updateByPrimaryKey(hlsCusConContractCashflow);
            res.add(ylCshTransferPaymentDto);
        }
        return res;
    }

    @Override
    public List<YLCshTransferPaymentDto> updateAndVerification(IRequest requestCtx, HttpServletRequest request, List<YLCshTransferPaymentDto> ylCshTransferPaymentDtoList) {
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        ResponseData responseData = new ResponseData();
        ylCshTransferPaymentDtoList.forEach(ylCshTransferPaymentDto -> {
//            commonLogHead(hlsWsRequests, "转付确认", ylCshTransferPaymentDto, request);
            //生产现金事务数据
            HlsCusCshTransaction hlsCusCshTransaction = getHlsCusCshTransaction(requestCtx, ylCshTransferPaymentDto);
            //插入现金事务数据
            hlsCusCshTransactionMapper.insertSelective(hlsCusCshTransaction);

            writeOffYL(requestCtx, request, hlsWsRequests, responseData, ylCshTransferPaymentDto, hlsCusCshTransaction);
            //更新转付确认表
            ylCshTransferPaymentDto.setTransactionId(hlsCusCshTransaction.getTransactionId());
            ylCshTransferPaymentDto.setConfirmDate(new Date());
            ylCshTransferPaymentDto.setTransferPaymentStatus("CONFIRMED");
            ylCshTransferPaymentDtoMapper.updateByPrimaryKey(ylCshTransferPaymentDto);
        });
        return ylCshTransferPaymentDtoList;
    }

    private void writeOffYL(IRequest requestCtx, HttpServletRequest request, HlsWsRequests hlsWsRequests, ResponseData responseData, YLCshTransferPaymentDto ylCshTransferPaymentDto, HlsCusCshTransaction hlsCusCshTransaction) {
        //核销
        //查出合同现金流数据
        HlsCusConContractCashflow hlsCusConContractCashflow1 = new HlsCusConContractCashflow();
        Example example = new Example(HlsCusConContractCashflow.class);
        example.createCriteria().andEqualTo("cashflowId", ylCshTransferPaymentDto.getCashflowId());
        hlsCusConContractCashflow1 = hlsCusConContractCashflowMapper.selectByExample(example).get(0);

        List<HlsCusConContractCashflow> hlsCusConContractCashflowList = hlsCusConContractCashflowMapper.queryConContractCashflowList(ylCshTransferPaymentDto.getContractId(), hlsCusConContractCashflow1.getTimes());
        List<HlsCusCshWriteOff> ans = new ArrayList<>();
        try {
            //如果没有就加一个
            for (HlsCusConContractCashflow hlsCusConContractCashflow : hlsCusConContractCashflowList) {
                setHlsCusWriteOffList(ylCshTransferPaymentDto, hlsCusCshTransaction, hlsCusConContractCashflow, ans);
                updateWriteOffMatch(requestCtx, hlsCusCshTransaction, hlsCusConContractCashflow, ans.get(ans.size() - 1));
            }
            cshWriteOffService.writeOff(requestCtx, ans, request.getSession());
        } catch (Exception e) {
//            commonLog(responseData, "10001", "E", "核销报错", hlsWsRequests);
            throw new RuntimeException("转付确认异常");
        }
    }

    private void setHlsCusWriteOffList(YLCshTransferPaymentDto ylCshTransferPaymentDto, HlsCusCshTransaction hlsCusCshTransaction, HlsCusConContractCashflow hlsCusConContractCashflow, List<HlsCusCshWriteOff> res) {

        HlsCusCshWriteOff hlsCusCshWriteOff = new HlsCusCshWriteOff();
        hlsCusCshWriteOff.setContractId(ylCshTransferPaymentDto.getContractId());
        hlsCusCshWriteOff.setCshTransactionId(hlsCusCshTransaction.getTransactionId());
        //设置现金流id
        hlsCusCshWriteOff.setCashflowId(hlsCusConContractCashflow.getCashflowId());
        //设置核销类型
        hlsCusCshWriteOff.setWriteOffType("RECEIPT_CREDIT");
        //设置核销时间
        hlsCusCshWriteOff.setWriteOffDate(new Date());
        //设置现金事务核销金额
        hlsCusCshWriteOff.setCshWriteOffAmount(hlsCusCshTransaction.getWriteOffAmount());
        //设置反冲标志
        hlsCusCshWriteOff.setReversedFlag("N");
        //设置现金流项目
        hlsCusCshWriteOff.setCfItem(hlsCusConContractCashflow.getCfItem());
        //设置现金流类型
        hlsCusCshWriteOff.setCfType(hlsCusConContractCashflow.getCfType());
        if (ylCshTransferPaymentDto.getRepayPenalty() == null || HlsCusMathUtil.compare(ylCshTransferPaymentDto.getRepayPenalty(), (double) 0) == 0) {
            //设置核销金额
            hlsCusCshWriteOff.setWriteOffDueAmount(ylCshTransferPaymentDto.getRepayAmount());
            //设置核销利息
            hlsCusCshWriteOff.setWriteOffInterest(ylCshTransferPaymentDto.getRepayInterest());
            //设置核销本金
            hlsCusCshWriteOff.setWriteOffPrincipal(ylCshTransferPaymentDto.getRepayPrincipal());
        } else {
            //核销罚息
            if ((hlsCusCshWriteOff.getCfItem() != null && 9 == hlsCusCshWriteOff.getCfItem()) || (hlsCusCshWriteOff.getCfType() != null && 9 == hlsCusCshWriteOff.getCfType())) {
                hlsCusCshWriteOff.setWriteOffDueAmount(ylCshTransferPaymentDto.getRepayPenalty());
                //设置核销利息
                hlsCusCshWriteOff.setWriteOffInterest((double) 0);
                //设置核销本金
                hlsCusCshWriteOff.setWriteOffPrincipal((double) 0);
            } else {//核销本金加利息
                //设置核销金额
                hlsCusCshWriteOff.setWriteOffDueAmount(HlsCusMathUtil.add(ylCshTransferPaymentDto.getRepayInterest(), ylCshTransferPaymentDto.getRepayPrincipal()));
                //设置核销利息
                hlsCusCshWriteOff.setWriteOffInterest(ylCshTransferPaymentDto.getRepayInterest());
                //设置核销本金
                hlsCusCshWriteOff.setWriteOffPrincipal(ylCshTransferPaymentDto.getRepayPrincipal());
            }
        }
        //设置期次
        hlsCusCshWriteOff.setTimes(hlsCusConContractCashflow.getTimes());
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
        hlsCusCshTransaction.setWriteOffFlag("FULL");
        hlsCusCshTransaction.setBpBankName(ylCshTransferPaymentDto.getBpBankName());
        hlsCusCshTransaction.setBpBankBranchName(ylCshTransferPaymentDto.getBankBranchNameEx());
        hlsCusCshTransaction.setBpBankAccountNum(ylCshTransferPaymentDto.getBpBankAccountNum());
        hlsCusCshTransaction.setBpBankAccountName(ylCshTransferPaymentDto.getBpBankAccountName());
        hlsCusCshTransaction.setComments(ylCshTransferPaymentDto.getComments());
        hlsCusCshTransaction.setBankSlipNum(ylCshTransferPaymentDto.getExtraBankStatement());
        hlsCusCshTransaction.setBankAccountId(ylCshTransferPaymentDto.getBankAccountId());
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

    private void updateWriteOffMatch(IRequest requestCtx, HlsCusCshTransaction hlsCusCshTransaction, HlsCusConContractCashflow hlsCusConContractCashflow, HlsCusCshWriteOff hlsCusCshWriteOff) {
        CshAllocation cshAllocation = new CshAllocation();
        Map<String, String> params = new HashMap<>();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String allocationNumber = fndCodingRuleValuesService.getCodeRuleValue(requestCtx, ALLOCATION_DOCUMENT_CATEGORY, ALLOCATION_DOCUMENT_TYPE, ALLOCATION_BUSINESS_TYPE, params);
        cshAllocation.setAllocationNumber(allocationNumber);
        try {
            cshAllocation.setAllocationDate(df.parse(df.format(new Date())));
        } catch (ParseException e) {
            throw new RuntimeException("时间格式异常");
        }
        cshAllocation.setAllocationSource("AUTO");
        cshAllocation.setAllocationStatus("Y");
        //系统字段
        cshAllocation.setCreationDate(new Date());
        cshAllocation.setCreatedBy(requestCtx.getUserId());
        cshAllocation.setLastUpdateDate(new Date());
        cshAllocation.setLastUpdatedBy(requestCtx.getUserId());
        cshAllocationMapper.insertSelective(cshAllocation);
        //将现金事务表与核销匹配表关联
        Long allocationId = cshAllocation.getAllocationId();
        CshAllocationReceipt cshAllocationReceipt = new CshAllocationReceipt();
        cshAllocationReceipt.setAllocationId(allocationId);
        cshAllocationReceipt.setTransactionId(hlsCusCshTransaction.getTransactionId());
        cshAllocationReceipt.setAdvanceReceiptAmount(OracleUtils.nvl(hlsCusCshTransaction.getAdvanceReceiptAmount(), 0.0));
        cshAllocationReceiptMapper.insertSelective(cshAllocationReceipt);
        //将匹配表与现金事务流表关联
        CshAllocationCredit cshAllocationCredit = new CshAllocationCredit();
        cshAllocationCredit.setAllocationId(allocationId);
        cshAllocationCredit.setCashflowId(hlsCusConContractCashflow.getCashflowId());
        cshAllocationCredit.setDueAmount(hlsCusCshWriteOff.getWriteOffDueAmount());
        cshAllocationCredit.setPrincipal(hlsCusConContractCashflow.getPrincipal());
        cshAllocationCredit.setInterest(hlsCusConContractCashflow.getInterest());
        //插入核销匹配表
        cshAllocationCreditMapper.insertSelective(cshAllocationCredit);
    }


}