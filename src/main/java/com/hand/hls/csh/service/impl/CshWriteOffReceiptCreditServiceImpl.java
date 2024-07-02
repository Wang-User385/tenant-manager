package com.hand.hls.csh.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hls.bp.components.CalculateUtil;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.csh.components.DocumentChangeCommon;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.csh.dto.HlsCusWriteOffMatch;
import com.hand.hls.csh.exception.BeyondAmountLimitException;
import com.hand.hls.csh.mapper.HlsCusCshTransactionMapper;
import com.hand.hls.csh.mapper.HlsCusCshWriteOffMapper;
import com.hand.hls.csh.service.CshTransactionCommon;
import com.hand.hls.csh.service.CshTransactionService;
import com.hand.hls.csh.service.CshWriteOffService;
import com.hand.hls.csh.service.HlsCusIWriteOffMatchService;
import com.hand.hls.fnd.mapper.HLSCurrencyMapper;
import com.hand.hls.fnd.mapper.HlsCfItemMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.interfacePlatform.utils.FinanceBaseUtils;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.mapper.SysUserMapper;
import com.hand.hls.utils.MathUtil;
import hls.core.sys.event.service.SysEventService;
import hls.core.sys.event.utils.SysEventCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

@Service
public class CshWriteOffReceiptCreditServiceImpl implements CshTransactionCommon {

    //租赁收款核销
    private static final String WriteOffType = "RECEIPT_CREDIT";

    @Autowired
    private JeTrxCommonService commonService;
    @Autowired
    HlsCusCshWriteOffMapper cshWriteOffMapper;
    @Autowired
    CshWriteOffService cshWriteOffService;
    @Autowired
    HlsCusCshTransactionMapper cshTransactionMapper;
    @Autowired
    CshTransactionService cshTransactionService;
    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;
    @Autowired
    HlsCusConContractCashflowMapper cashflowMapper;
    @Autowired
    HlsCusConContractMapper conContractMapper;
    @Autowired
    HlsCusConContractService conContractService;

    @Autowired
    private HLSCurrencyMapper hlsCurrencyMapper;
    @Autowired
    private SysEventService sysEventService;
    @Autowired
    HlsCfItemMapper hlsCfItemMapper;
    @Autowired
    HlsCusConContractCashflowMapper contractCashflowMapper;
    @Autowired
    HlsCusConContractCashflowService hlsCusConContractCashflowService;
    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;
    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private HlsCusIWriteOffMatchService hlsCusIWriteOffMatchService;
    @Autowired
    private FinanceBaseUtils financeBaseUtils;

    @Override
    public String getWriteOffType() {
        return WriteOffType;
    }

    @Override
    public void process(IRequest iRequest, HlsCusCshWriteOff cshWriteOff) throws BeyondAmountLimitException {
        /**
         * 收款核销债权类型 1、核销表插入一条记录 2、更新现金事物表 3、更新现金合同流表
         */
        // 1、核销表插入一条记录
        HlsCusCshWriteOff writeOff = getReceiptCredit(cshWriteOff);

        writeOff=cshWriteOffService.insertSelective(iRequest, writeOff);

        cshWriteOff.setWriteOffId(writeOff.getWriteOffId());

        //核销匹配表插入记录 通过导入方式的需要    非导入方式核销前会有匹配动作 已经插表
        if ("Y".equals(cshWriteOff.getImportFlag())) {
            HlsCusWriteOffMatch hlsCusWriteOffMatch = saveWriteOffMatch(cshWriteOff);
            hlsCusIWriteOffMatchService.insertSelective(iRequest, hlsCusWriteOffMatch);
            cshWriteOff.setWriteOffMatchId(hlsCusWriteOffMatch.getWriteOffMatchId());
        }

        //插入凭证事物流水
        Map map = new HashMap<>();
        map.put("jeTrxId", writeOff.getWriteOffId());
        map.put("companyId", writeOff.getCompanyId());
        map.put("contractId", writeOff.getContractId());
        map.put("sourceDoc", "CON_CONTRACT");
        AbstractJeTrxService writeOffJeTrx = commonService.map.get("CSH_WRITE_OFF");
        writeOffJeTrx.process(iRequest, map);
        //收款核销债权

        // 更新事件
        Map<String, Object> params = new HashMap<>();
        Long cashflowId = cshWriteOff.getCashflowId();
        HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
        conContractCashflow.setCashflowId(cashflowId);
        conContractCashflow = contractCashflowMapper.selectByPrimaryKey(conContractCashflow);

        //获取合同信息
        Double amount = conContractCashflow.getReceivedAmount();
        Long contract_id = conContractCashflow.getContractId();
        HlsCusConContract conContract = new HlsCusConContract();
        conContract.setContractId(contract_id);
        conContract = conContractMapper.selectByPrimaryKey(conContract);


        HlsCusCshTransaction transaction = cshTransactionMapper.selectByPrimaryKey(cshWriteOff.getCshTransactionId());
        //插入收款核销事件(消息)
        Map<String, Object> paramsEvent = new HashMap<String, Object>();
        DecimalFormat df = new DecimalFormat("###,##0.00");
        StringBuilder builder = new StringBuilder();
        if ("Y".equals(cshWriteOff.getImportFlag())) {
            builder.append("交易号").append(transaction.getTransactionNum()).append("自动匹配到合同")
                    .append(conContract.getContractNumber()).append("第").append(conContractCashflow.getTimes())
                    .append("期").append(cshWriteOff.getCfItemDesc())
                    .append("￥").append(df.format(cshWriteOff.getCshWriteOffAmount()))
                    .append("，已自动核销成功，请前往收付管理功能查看");
        } else {
            builder.append("交易号").append(transaction.getTransactionNum()).append("匹配到合同")
                    .append(conContract.getContractNumber()).append("第").append(conContractCashflow.getTimes())
                    .append("期").append(cshWriteOff.getCfItemDesc())
                    .append("￥").append(df.format(cshWriteOff.getCshWriteOffAmount()))
                    .append("，财务已核销成功，请前往收付管理功能查看");
        }
        paramsEvent.put("message", builder.toString());
        paramsEvent.put("noticeTitle", "租赁合同收款核销");
        paramsEvent.put("noticeType", "NOTICE");
        paramsEvent.put("url", "");
        paramsEvent.put("eventCode", SysEventCodeUtil.PROPERTY_CSH_WRITE_OFF_RECEIPT_CREDIT);
        paramsEvent.put("level", 1L);
        //运营岗
        List<String> positionCodeList = new ArrayList<>();
        positionCodeList.add("05530");
        positionCodeList.add("00320");
        List<SysUser> sysUsers = sysUserMapper.selectUserByPositionCode(writeOff.getCompanyId(), positionCodeList);
        IRequest request = RequestHelper.newEmptyRequest();
        for (SysUser user : sysUsers) {
            request.setUserId(user.getUserId());
            sysEventService.eventSave(request, contract_id, SysEventCodeUtil.PROPERTY_CSH_WRITE_OFF_RECEIPT_CREDIT, SysEventCodeUtil.PROPERTY_CSH_WRITE_OFF_RECEIPT_CREDIT, "BAC", SysEventCodeUtil.PROPERTY_CSH_WRITE_OFF_RECEIPT_CREDIT, "P2D", paramsEvent);
        }
//		//插入收款核销事件(事件)
//		cshWritiOffEventService.cshWritiOffEvent(iRequest, "CSH_WRITE_OFF.RECEIPT_CREDIT", writeOff.getWriteOffId());
//
        // 2、更新现金事物表
        HlsCusCshTransaction cshTran = updateCshTrxAfterWriteoff(cshWriteOff, "RECEIPT_CREDIT");
        cshTransactionService.updateByPrimaryKeySelective(iRequest, cshTran);
        // 3、更新现金合同流表
        HlsCusConContractCashflow conCashflow = updateConCashflowAfterWriteOff(iRequest, cshWriteOff);
        cashflowMapper.updateOne(conCashflow);

        //更新合同
        cshWriteOffService.updateContractReceivedStatus(conCashflow);

        // 4、粤海二期 零售业务 核销债权 核销保证金需要插入一条新的保证金现金事物记录
        HlsCusConContractCashflow cusConContractCashflow = contractCashflowMapper.selectByPrimaryKey(cshWriteOff.getCashflowId());
        HlsCusCshTransaction cshTransaction = null;
        if (cusConContractCashflow.getCfItem() == 51L && ("CONL".equals(conContract.getDocumentType()) || "CONLB".equals(conContract.getDocumentType()))) {
            cshTransaction = cshWriteOffService.getCshTransaction(iRequest, cshWriteOff, "RECEIPT_DEPOSIT");
            cshTransaction.setPostedFlag("Y");
            cshTransaction.setSourceDocLineId(writeOff.getWriteOffId());
            cshTransaction.setSourceDocId(writeOff.getCshTransactionId());

            cshTransactionService.insertSelective(iRequest, cshTransaction);
            // 5、更新后续事务Id
            cshWriteOffMapper.updateSubCshTrxId(writeOff.getWriteOffId(), cshTransaction.getTransactionId(),
                    cshTransaction.getTransactionAmount());
        }

        //调用YH007 同步核销流水 new_dev不启用

        if(1L == cshWriteOff.getCfItem() || 11L == cshWriteOff.getCfItem() || 13L == cshWriteOff.getCfItem()){
            if("FULL".equals(conCashflow.getWriteOffFlag())) {
                financeBaseUtils.mergeFlowItfc(iRequest, conCashflow);
                financeBaseUtils.writeOffFlowItfc(iRequest, conCashflow);
            }

            financeBaseUtils.postCashflowItfc(iRequest,cshWriteOff);
        }
    }

    private HlsCusCshWriteOff getReceiptCredit(HlsCusCshWriteOff cshWriteOff) {
        HlsCusCshWriteOff wo = new HlsCusCshWriteOff();

        wo.setWriteOffType(cshWriteOff.getWriteOffType());
        wo.setWriteOffDate(cshWriteOff.getWriteOffDate());
        wo.setCshTransactionId(cshWriteOff.getCshTransactionId());
        wo.setCshWriteOffAmount(cshWriteOff.getWriteOffDueAmount());
        wo.setReversedFlag("N");
        wo.setImportFlag(cshWriteOff.getImportFlag());
        wo.setDescription(cshWriteOff.getDescription());
        wo.setCashflowId(cshWriteOff.getCashflowId());
        wo.setContractId(cshWriteOff.getContractId());
        wo.setTimes(cshWriteOff.getTimes());
        wo.setCfItem(cshWriteOff.getCfItem());
        wo.setCfType(cshWriteOff.getCfType());
        wo.setWriteOffDueAmount(cshWriteOff.getWriteOffDueAmount());
        wo.setWriteOffPrincipal(cshWriteOff.getWriteOffPrincipal());
        wo.setWriteOffInterest(cshWriteOff.getWriteOffInterest());
        wo.setCompanyId(cshWriteOff.getCompanyId());
        wo.setDealDate(cshWriteOff.getDealDate());
        wo.setWriteOffDocCategory(DocumentChangeCommon.getTableName(HlsCusConContract.class));
        // dealDate为应收日期，查询现金流的应收日期
        HlsCusConContractCashflow hlsCusConContractCashflow = contractCashflowMapper.selectByPrimaryKey(cshWriteOff.getCashflowId());
        wo.setDealDate(hlsCusConContractCashflow.getDueDate());

        return wo;
    }

    private HlsCusCshTransaction updateCshTrxAfterWriteoff(HlsCusCshWriteOff cshWriteOff, String writeOffType) throws BeyondAmountLimitException {
        HlsCusCshTransaction ct = new HlsCusCshTransaction();

        List<HlsCusCshTransaction> cshTransactions = cshTransactionMapper.queryDetailByIdList(cshWriteOff.getCshTransactionId());

        HlsCusCshTransaction cshTransaction = cshTransactions.get(0);

        if (cshTransaction.getWriteOffAmount() == null) {
            cshTransaction.setWriteOffAmount(0d);
        }
        if (cshWriteOff.getWriteOffDueAmount() == null) {
            cshWriteOff.setWriteOffDueAmount(0d);
        }

        Double sumAmount = 0D;
        if ("RECEIPT_CREDIT".equalsIgnoreCase(writeOffType)) {
            sumAmount = CalculateUtil.add(cshTransaction.getWriteOffAmount(), Math.abs(cshWriteOff.getWriteOffDueAmount()));
        } else if ("RESERVE".equalsIgnoreCase(writeOffType)) {
            sumAmount = CalculateUtil.sub(cshTransaction.getWriteOffAmount(), Math.abs(cshWriteOff.getWriteOffDueAmount()));
        }


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

        return ct;
    }

    private HlsCusConContractCashflow updateConCashflowAfterWriteOff(IRequest iRequest, HlsCusCshWriteOff cshWriteOff) throws BeyondAmountLimitException {
        HlsCusConContractCashflow cf = cashflowMapper.selectByPrimaryKey(cshWriteOff.getCashflowId());

        if (cf.getCfItem() == 9) {
            //罚息现金流核销，核销金额累加为已收核销
            Double receivedAmount = cf.getReceivedAmount();
            if (null == receivedAmount) {
                receivedAmount = 0D;
            }
        }


        if (cf.getReceivedAmount() == null) {
            cf.setReceivedAmount(0d);
        }

        if (cf.getReceivedPrincipal() == null) {
            cf.setReceivedPrincipal(cshWriteOff.getWriteOffPrincipal());
        } else {
            cf.setReceivedPrincipal(CalculateUtil.add(cf.getReceivedPrincipal(), cshWriteOff.getWriteOffPrincipal()));
        }
        if(cf.getPrincipal() < cf.getReceivedPrincipal()){
            throw new BeyondAmountLimitException("核销本金超过应收本金");
        }

        if (cf.getReceivedInterest() == null) {
            cf.setReceivedInterest(cshWriteOff.getWriteOffInterest());
        } else {
            cf.setReceivedInterest(CalculateUtil.add(cf.getReceivedInterest(), cshWriteOff.getWriteOffInterest()));
        }
        if(cf.getInterest() < cshWriteOff.getWriteOffInterest()){
            throw new BeyondAmountLimitException("核销利息超过应收利息");
        }

        Double receivedAmount = CalculateUtil.add(cf.getReceivedAmount(), cshWriteOff.getWriteOffDueAmount());
        cf.setReceivedAmount(receivedAmount);
        if (new BigDecimal(receivedAmount.toString()).compareTo(new BigDecimal(0)) == 0) {
            cf.setReceivedAmount(receivedAmount);
            cf.setWriteOffFlag("NOT");
        } else if (new BigDecimal(receivedAmount.toString()).compareTo(new BigDecimal(cf.getDueAmount().toString())) == -1 && receivedAmount > 0) {
            cf.setReceivedAmount(receivedAmount);
            cf.setWriteOffFlag("PARTIAL");
        } else if (new BigDecimal(receivedAmount.toString()).compareTo(new BigDecimal(cf.getDueAmount().toString())) == 0) {
            cf.setReceivedAmount(receivedAmount);
            cf.setWriteOffFlag("FULL");
            cf.setFullWriteOffDate(cshWriteOff.getDealDate());
        } else {
            throw new BeyondAmountLimitException();
        }

        cf.setLastReceivedDate(cshWriteOff.getDealDate());

        //判断如果保证金完全核销，插入保证金退回的现金流
        HlsCusConContract cusConContract = new HlsCusConContract();
        cusConContract.setContractId(cf.getContractId());
        cusConContract = conContractService.selectByPrimaryKey(iRequest, cusConContract);

        /*//保证金类型 完全核销 生成保证金退还现金流
        if (51 == cf.getCfItem() && "FULL".equalsIgnoreCase(cf.getWriteOffFlag())) {
            HlsCusConContractCashflow conCash = new HlsCusConContractCashflow();
            conCash.setCfType(52L);
            conCash.setCfItem(52L);
            conCash.setWriteOffFlag("NOT");
            conCash.setCfDirection("OUTFLOW");
            conCash.setContractId(cusConContract.getContractId());
            List<HlsCusConContractCashflow> conContractCashflows = hlsCusConContractCashflowService.select(iRequest, conCash, 1, 10);

            if (conContractCashflows.size() == 0) {
                throw new BeyondAmountLimitException("无保证金退还现金流");
            } else if (conContractCashflows.size() > 1) {
                throw new BeyondAmountLimitException("查到多条保证金退还现金流");
            }

//			if(list.size()!=0){
//				conContractCashflows.get(0).setDueDate(list.get(0).getDueDate());
//				hlsCusConContractCashflowService.updateByPrimaryKeySelective(iRequest,conContractCashflows.get(0));
//			}

            //插入现金事物
            //cshTransactionService
            HlsCusCshTransaction cshDeposit = new HlsCusCshTransaction();
            cshDeposit.setTransactionCategory("CSH_TRANSACTION");
            cshDeposit.setBusinessType("DEPOSIT");
            cshDeposit.setTransactionType("DEPOSIT");
            Map<String, String> params = new HashMap<String, String>();
            cshDeposit.setTransactionNum(fndCodingRuleValuesService.getCodeRuleValue(iRequest, cshDeposit.getTransactionCategory(), cshDeposit.getTransactionType(), cshDeposit.getBusinessType(), params));
            cshDeposit.setTransactionDate(new Date());
            cshDeposit.setPenaltyCalcDate(new Date());
            cshDeposit.setCompanyId(iRequest.getCompanyId());
            cshDeposit.setTransactionAmount(conContractCashflows.get(0).getDueAmount());
            cshDeposit.setCurrencyCode("CNY");
            cshDeposit.setPaymentMethod("T/T");
            cshDeposit.setReversedFlag("N");
            cshDeposit.setContractId(conContractCashflows.get(0).getContractId());
            cshDeposit.setSourceDocId(conContractCashflows.get(0).getContractId());
            cshDeposit.setSourceDocLineId(conContractCashflows.get(0).getCashflowId());
            cshDeposit.setSourceDocCategory("CON_CONTRACT");
            cshDeposit.setWriteOffFlag("NOT");
            cshDeposit.setWriteOffAmount(0D);
            cshDeposit.setBpId(cusConContract.getTenantId());
            cshDeposit = cshTransactionService.insertSelective(iRequest, cshDeposit);

        }*/
        return cf;
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
        HlsCusCshTransaction cshTran = updateCshTrxAfterWriteoff(cshWriteOff, "RESERVE");
        cshTransactionService.updateByPrimaryKeySelective(requestContext, cshTran);
        // 3、更新原核销表数据
        cshWriteOffMapper.updateCshWriteOffbyId(cshWriteOff.getWriteOffId(), "W", writeOff.getWriteOffId(),
                cshWriteOff.getReversedDate(), requestContext.getUserId(), new Date());
        // 4、更新现金流表数据
        HlsCusConContractCashflow conCashflow = updateConCashflowAfterWriteOff(requestContext, cshWriteOff);
        cashflowMapper.updateOne(conCashflow);

        //更新合同
        cshWriteOffService.updateContractReceivedStatus(conCashflow);

        //插入凭证事物流水表
        AbstractJeTrxService writeOffService = commonService.map.get("CSH_WRITE_OFF");
        Map params = new HashMap<>();
        params.put("jeTrxId", writeOff.getWriteOffId());
        params.put("companyId", requestContext.getCompanyId());
        params.put("contractId", cshWriteOff.getContractId());
        params.put("sourceDoc", "CON_CONTRACT");
        params.put("reverseJeDate", cshWriteOff.getReversedDate());
        params.put("reverseJeTrxId", cshWriteOff.getWriteOffId());
        writeOffService.process(requestContext, params);

    }

    private HlsCusWriteOffMatch saveWriteOffMatch(HlsCusCshWriteOff cshWriteOff) {
        HlsCusWriteOffMatch wo = new HlsCusWriteOffMatch();

        wo.setWriteOffType(cshWriteOff.getWriteOffType());
        wo.setWriteOffDate(cshWriteOff.getWriteOffDate());
        wo.setCshTransactionId(cshWriteOff.getCshTransactionId());
        wo.setCshWriteOffAmount(cshWriteOff.getWriteOffDueAmount());
        wo.setReversedFlag("N");
        wo.setDescription(cshWriteOff.getDescription());
        wo.setCashflowId(cshWriteOff.getCashflowId());
        wo.setContractId(cshWriteOff.getContractId());
        wo.setTimes(cshWriteOff.getTimes());
        wo.setWriteFlag("Y");
        wo.setImportFlag(cshWriteOff.getImportFlag());
        wo.setCfItem(cshWriteOff.getCfItem());
        wo.setCfType(cshWriteOff.getCfType());
        wo.setWriteOffDueAmount(cshWriteOff.getWriteOffDueAmount());
        wo.setWriteOffPrincipal(cshWriteOff.getWriteOffPrincipal());
        wo.setWriteOffInterest(cshWriteOff.getWriteOffInterest());
        wo.setCompanyId(cshWriteOff.getCompanyId());
        wo.setWriteOffDocCategory("CON_CONTRACT");

        return wo;
    }

}
