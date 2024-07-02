package com.hand.hls.csh.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hls.bp.components.CalculateUtil;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.mapper.ConContractMapper;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.csh.components.DocumentChangeCommon;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.csh.exception.BeyondAmountLimitException;
import com.hand.hls.csh.mapper.HlsCusCshTransactionMapper;
import com.hand.hls.csh.mapper.HlsCusCshWriteOffMapper;
import com.hand.hls.csh.service.CshTransactionCommon;
import com.hand.hls.csh.service.CshTransactionService;
import com.hand.hls.csh.service.CshWriteOffService;
import com.hand.hls.fnd.dto.HLSCurrency;
import com.hand.hls.fnd.mapper.HLSCurrencyMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.mapper.SysUserMapper;
import hls.core.sys.event.service.SysEventService;
import hls.core.sys.event.utils.SysEventCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CshWriteOffReceiptAdvanceReceiptServiceImpl implements CshTransactionCommon {

    //预收款核销
    private static final String WriteOffType = "RECEIPT_ADVANCE_RECEIPT";

    @Autowired
    HlsCusCshWriteOffMapper cshWriteOffMapper;
    @Autowired
    CshWriteOffService cshWriteOffService;
    @Autowired
    HlsCusCshTransactionMapper cshTransactionMapper;
    @Autowired
    CshTransactionService cshTransactionService;
    @Autowired
    HlsCusConContractCashflowMapper cashflowMapper;
    @Autowired
    FndCodingRuleValuesService codingRuleValuesService;
    @Autowired
    private SysEventService sysEventService;
    @Autowired
    private ConContractMapper conContractMapper;
    @Autowired
    private HLSCurrencyMapper hlsCurrencyMapper;
    @Autowired
    private JeTrxCommonService jeTrxCommonService;
    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;

    Map<String, String> params = new HashMap<String, String>();

    @Override
    public String getWriteOffType() {
        return WriteOffType;
    }

    @Override
    public void process(IRequest iRequest, HlsCusCshWriteOff cshWriteOff) throws BeyondAmountLimitException {
        /**
         * 收款核销为预收款类型 1、核销表插入一条记录 2、更新现金事物表 3、插入一条新的现金事物记录
         * 4、更新后续事务Id
         */
        // 1、核销表插入一条记录
        HlsCusCshWriteOff writeOff = getReceiptCredit(cshWriteOff);
        cshWriteOffService.insertSelective(iRequest, writeOff);
        cshWriteOff.setWriteOffId(writeOff.getWriteOffId());

        // 2、更新现金事物表
        HlsCusCshTransaction cshTran = updateCshTrxAfterWriteoff(cshWriteOff);
        cshTransactionService.updateByPrimaryKeySelective(iRequest, cshTran);
        // 3、插入一条新的现金事物记录
        HlsCusCshTransaction cshTransaction = getCshTransaction(iRequest, cshWriteOff, "RECEIPT_ADVANCE_RECEIPT");
        cshTransaction.setSourceDocLineId(writeOff.getWriteOffId());
        cshTransactionService.insertSelective(iRequest, cshTransaction);
        // 4、更新后续事务Id
        cshWriteOffMapper.updateSubCshTrxId(writeOff.getWriteOffId(), cshTransaction.getTransactionId(),
                cshTransaction.getTransactionAmount());

        //添加凭证事物
     /*   AbstractJeTrxService JeWriteOffService = jeTrxCommonService.map.get("CSH_WRITE_OFF");
        Map map = new HashMap<>();
        map.put("jeTrxId", writeOff.getWriteOffId());
        map.put("companyId", cshWriteOff.getCompanyId());
        map.put("contractId", writeOff.getContractId());
        map.put("sourceDoc", "CON_CONTRACT");
        JeWriteOffService.process(iRequest, map);*/

        // 添加事件
  /*      Map<String, Object> params = new HashMap<>();

        HLSCurrency hlsCurrency = new HLSCurrency();
        hlsCurrency.setCurrencyCode(cshWriteOff.getCurrencyCode());
        hlsCurrency = hlsCurrencyMapper.selectOne(hlsCurrency);

        params.put("amount", cshWriteOff.getWriteOffDueAmount());
        params.put("currency", hlsCurrency.getCurrencySymbol());
        params.put("eventCode", SysEventCodeUtil.PROPERTY_CSH_WRITE_OFF_RECEIPT_ADVANCE_RECEIPT);
        params.put("level", 1L);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        DecimalFormat df = new DecimalFormat("###,##0.00");
        StringBuilder builder = new StringBuilder();
        builder.append("交易号").append(cshTran.getTransactionNum()).append("匹配预收款")
                .append("￥").append(df.format(cshWriteOff.getCshWriteOffAmount()))
                .append("，财务已核销成功，请前往收付管理功能查看或重新匹配应收项。");
        params.put("message", builder.toString());
        //运营岗
        List<String> positionCodeList = new ArrayList<>();
        positionCodeList.add("05530");
        positionCodeList.add("00320");
        List<SysUser> sysUsers = sysUserMapper.selectUserByPositionCode(iRequest.getCompanyId(), positionCodeList);
        IRequest request = RequestHelper.newEmptyRequest();
        for (SysUser user : sysUsers) {
            request.setUserId(user.getUserId());
            sysEventService.createEvent(request, cshWriteOff.getBpId(), "CSH_TRANSACTION", "", params);
        }*/
    }

    private HlsCusCshWriteOff getReceiptCredit(HlsCusCshWriteOff cshWriteOff) {
        HlsCusCshWriteOff wo = new HlsCusCshWriteOff();

        wo.setWriteOffType(cshWriteOff.getWriteOffType());
        wo.setWriteOffDate(cshWriteOff.getWriteOffDate());
        wo.setCshTransactionId(cshWriteOff.getCshTransactionId());
        wo.setCshWriteOffAmount(cshWriteOff.getWriteOffDueAmount());
        wo.setReversedFlag("N");
        wo.setDescription(cshWriteOff.getDescription());
        wo.setCashflowId(cshWriteOff.getCashflowId());
        wo.setContractId(cshWriteOff.getContractId());
        wo.setTimes(cshWriteOff.getTimes());
        wo.setCfItem(cshWriteOff.getCfItem());
        wo.setCfType(cshWriteOff.getCfType());
        wo.setWriteOffDueAmount(cshWriteOff.getWriteOffDueAmount());
        wo.setWriteOffPrincipal(cshWriteOff.getWriteOffPrincipal());
        wo.setWriteOffInterest(cshWriteOff.getWriteOffInterest());
        wo.setWriteOffDocCategory(DocumentChangeCommon.getTableName(HlsCusConContract.class));
        return wo;
    }

    private HlsCusCshTransaction getCshTransaction(IRequest iRequest, HlsCusCshWriteOff cshWriteOff, String writeOffType) {
        List<HlsCusCshTransaction> cshTransactions = cshTransactionMapper.queryDetailByIdList(cshWriteOff.getCshTransactionId());

        HlsCusCshTransaction cshTransaction = cshTransactions.get(0);

        cshTransaction.setTransactionId(null);
        cshTransaction.setTransactionCategory("CSH_TRANSACTION");
        if ("RECEIPT_ADVANCE_RECEIPT".equals(writeOffType)) {
            cshTransaction.setTransactionType("ADVANCE_RECEIPT");
            cshTransaction.setBusinessType("ADVANCE_RECEIPT");
        } else if ("RECEIPT_DEPOSIT".equals(writeOffType)) {

            cshTransaction.setTransactionType("DEPOSIT");
            cshTransaction.setBusinessType("DEPOSIT");
        } else if ("RECEIPT_DEPOSIT_POOL".equals(writeOffType)) {
            cshTransaction.setTransactionType("DEPOSIT_POOL");
            cshTransaction.setBusinessType("DEPOSIT");
        } else if ("REFUND".equals(writeOffType)) {
            cshTransaction.setTransactionType("REFUND");
            cshTransaction.setBusinessType("PAYMENT");
        }
        if (cshWriteOff.getContractId() != null) {
            cshTransaction.setContractId(cshWriteOff.getContractId());
        }
        cshTransaction.setTransactionAmount(cshWriteOff.getWriteOffDueAmount());
        cshTransaction.setReversedFlag("N");
        cshTransaction.setReversedTrxId(null);
        cshTransaction.setReversedDate(null);
        cshTransaction.setPostedFlag("Y");
        cshTransaction.setHandlingCharge(null);
        cshTransaction.setDescription(cshWriteOff.getDescription());
        cshTransaction.setWriteOffFlag("NOT");
        cshTransaction.setWriteOffAmount(null);
        cshTransaction.setFullWriteOffDate(null);
        cshTransaction.setSourceDocCategory("CSH_WRITE_OFF");
        cshTransaction.setTransactionNum(codingRuleValuesService.getCodeRuleValue(iRequest,
                cshTransaction.getTransactionCategory(), cshTransaction.getTransactionType(), cshTransaction.getBusinessType(), params));

        return cshTransaction;
    }

    private HlsCusCshTransaction updateCshTrxAfterWriteoff(HlsCusCshWriteOff cshWriteOff) throws BeyondAmountLimitException {
        HlsCusCshTransaction ct = new HlsCusCshTransaction();

        List<HlsCusCshTransaction> cshTransactions = cshTransactionMapper.queryDetailByIdList(cshWriteOff.getCshTransactionId());

        HlsCusCshTransaction cshTransaction = cshTransactions.get(0);

        if (cshTransaction.getWriteOffAmount() == null) {
            cshTransaction.setWriteOffAmount(0d);
        }
        if (cshWriteOff.getWriteOffDueAmount() == null) {
            cshWriteOff.setWriteOffDueAmount(0d);
        }

        Double sumAmount = CalculateUtil.add(cshTransaction.getWriteOffAmount(), cshWriteOff.getWriteOffDueAmount());

        if (new BigDecimal(sumAmount.toString()).compareTo(new BigDecimal(0)) == 0) {
            ct.setWriteOffFlag("NOT");
            ct.setWriteOffAmount(sumAmount);
        } else if (new BigDecimal(sumAmount.toString()).compareTo(new BigDecimal(cshTransaction.getTransactionAmount().toString())) < 0 && sumAmount > 0) {
            ct.setWriteOffFlag("PARTIAL");
            ct.setWriteOffAmount(sumAmount);
        } else if (new BigDecimal(sumAmount.toString()).compareTo(new BigDecimal(cshTransaction.getTransactionAmount().toString())) == 0) {
            ct.setWriteOffFlag("FULL");
            ct.setWriteOffAmount(sumAmount);
            ct.setFullWriteOffDate(cshWriteOff.getWriteOffDate());
        } else {
            throw new BeyondAmountLimitException();
        }

        ct.setTransactionId(cshWriteOff.getCshTransactionId());
        ct.setTransactionNum(cshTransaction.getTransactionNum());
        ct.setTransactionAmount(cshTransaction.getTransactionAmount());
        ct.setBpName(cshTransaction.getBpName());
        ct.setTransactionDate(cshTransaction.getTransactionDate());
        return ct;
    }

    @Override
    public void reversed(IRequest requestContext, HlsCusCshWriteOff cshWriteOff) throws BeyondAmountLimitException {
        // 1、核销表插入一条记录
        if (cshWriteOff.getWriteOffDueAmount() != null) {
            cshWriteOff.setCshWriteOffAmount(0 - cshWriteOff.getWriteOffDueAmount());
        }
        if (cshWriteOff.getWriteOffDueAmount() != null) {
            cshWriteOff.setWriteOffDueAmount(0 - cshWriteOff.getWriteOffDueAmount());
        }
        if (cshWriteOff.getWriteOffPrincipal() != null) {
            cshWriteOff.setWriteOffPrincipal(0 - cshWriteOff.getWriteOffPrincipal());
        }
        if (cshWriteOff.getWriteOffInterest() != null) {
            cshWriteOff.setWriteOffInterest(0 - cshWriteOff.getWriteOffInterest());
        }

        HlsCusCshWriteOff writeOff = getReceiptCredit(cshWriteOff);
        writeOff.setReversedFlag("R");
        writeOff.setReversedWriteOffId(cshWriteOff.getWriteOffId());
        writeOff.setReversedDate(cshWriteOff.getReversedDate());
        cshWriteOffService.insertSelective(requestContext, writeOff);
        // 2、更新现金事物表
        HlsCusCshTransaction cshTran = updateCshTrxAfterWriteoff(cshWriteOff);
        cshTransactionService.updateByPrimaryKeySelective(requestContext, cshTran);
        // 3、更新原核销表数据
        cshWriteOffMapper.updateCshWriteOffbyId(cshWriteOff.getWriteOffId(), "W", writeOff.getWriteOffId(),
                cshWriteOff.getReversedDate(), requestContext.getUserId(), new Date());

        //反冲核销此比预收款时候 生成的现金事务
        HlsCusCshTransaction transaction = new HlsCusCshTransaction();
        transaction.setTransactionType("ADVANCE_RECEIPT");
        transaction.setBusinessType("ADVANCE_RECEIPT");
        transaction.setSourceDocLineId(cshWriteOff.getWriteOffId());
        List<HlsCusCshTransaction> cusCshTransactions = cshTransactionService.select(requestContext, transaction, 1, 1);
        //controller已经做过判断 可直接反冲

        //插入一条反冲记录
        HlsCusCshTransaction cshTr = new HlsCusCshTransaction();
        Map<String, String> mapQuotationNormal = hlsBeanRefUtilService.getFieldValueMap(cusCshTransactions.get(0));
        hlsBeanRefUtilService.setFieldValue(cshTr, mapQuotationNormal);
        Double num = 0 - cshTr.getTransactionAmount();
        cshTr.setTransactionId(null);
        cshTr.setTransactionAmount(num);
        cshTr.setTransactionDate(writeOff.getReversedDate());
        cshTr.setPenaltyCalcDate(writeOff.getReversedDate());
        cshTr.setReversedFlag("R");
        cshTr.setDescription(writeOff.getDescription());
        cshTr.setReversedTrxId(cusCshTransactions.get(0).getTransactionId());
        cshTr.setReversedDate(writeOff.getReversedDate());
        cshTr.setPostedFlag("N");
        cshTr.setContractId(null);
        cshTr.setHandlingCharge(null);
        cshTr.setWriteOffFlag("NA");
        cshTr.setWriteOffAmount(null);
        cshTr.setFullWriteOffDate(null);
        cshTr.setSourceDocCategory(null);
        cshTr.setSourceDocId(null);
        cshTr.setSourceDocLineId(null);
        cshTr.setTransactionNum(codingRuleValuesService.getCodeRuleValue(requestContext, cshTr.getTransactionCategory(), cshTr.getTransactionType(), cshTr.getBusinessType(), params));
        cshTr = cshTransactionService.insertSelective(requestContext, cshTr);

        //更新现金事物表
        cusCshTransactions.get(0).setReversedFlag("W");
        cusCshTransactions.get(0).setReversedTrxId(cshTr.getTransactionId());
        cusCshTransactions.get(0).setReversedDate(cshTr.getReversedDate());
        cshTransactionService.updateCshTrByPrimaryKey(cusCshTransactions.get(0));


        //更新合同现金流

        //插入凭证事物流水表 by fjm 17.9.1
        AbstractJeTrxService transactionJeTrxService = jeTrxCommonService.map.get("CSH_TRANSACTION");
        Map transactionParams = new HashMap<>();
        transactionParams.put("jeTrxId", cshTr.getTransactionId());
        transactionParams.put("companyId", cshTr.getCompanyId());
        transactionParams.put("contractId", cshTr.getContractId());
        transactionParams.put("sourceDoc", "CON_CONTRACT");
        transactionParams.put("reverseJeDate", cusCshTransactions.get(0).getReversedDate());
        transactionParams.put("reverseJeTrxId", cusCshTransactions.get(0).getTransactionId());
        transactionJeTrxService.process(requestContext, transactionParams);


        // 4、插入一条现金事物表
        HlsCusCshTransaction cshTransaction = getCshTransaction(requestContext, cshWriteOff, "RECEIPT_ADVANCE_RECEIPT");
        cshTransaction.setTransactionAmount(cshWriteOff.getWriteOffDueAmount());
        cshTransaction.setReversedFlag("R");
        cshTransaction.setReversedTrxId(cshWriteOff.getSubsequentCshTrxId());
        cshTransaction.setReversedDate(cshWriteOff.getReversedDate());
        cshTransaction.setSourceDocLineId(writeOff.getWriteOffId());
        cshTransactionService.insertSelective(requestContext, cshTransaction);
        // 5、更新后续现金事物表
        HlsCusCshTransaction tcx = new HlsCusCshTransaction();
        tcx.setTransactionId(cshWriteOff.getSubsequentCshTrxId());
        tcx.setReversedFlag("W");
        tcx.setReversedTrxId(cshTransaction.getTransactionId());
        tcx.setReversedDate(cshTransaction.getReversedDate());
        cshTransactionMapper.updateCshTrByPrimaryKey(tcx);
        // 6、更新核销插入核销数据的关联事物Id
        cshWriteOffMapper.updateSubCshTrxId(writeOff.getWriteOffId(), cshTransaction.getTransactionId(),
                cshTransaction.getTransactionAmount());
    }


}
