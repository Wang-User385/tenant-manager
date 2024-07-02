//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.csh.service.impl;

import com.hand.hap.core.AppContextInitListener;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.components.CalculateUtil;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.csh.dto.CshPaymentReqDt;
import com.hand.hls.csh.dto.CshWriteOff;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqDt;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.csh.exception.BeyondAmountLimitException;
import com.hand.hls.csh.exception.WriteOffTypeNullException;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqDtMapper;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqLnMapper;
import com.hand.hls.csh.mapper.HlsCusCshTransactionMapper;
import com.hand.hls.csh.mapper.HlsCusCshWriteOffMapper;
import com.hand.hls.csh.service.CshTransactionCommon;
import com.hand.hls.csh.service.CshTransactionService;
import com.hand.hls.csh.service.IMainCshTransactionService;
import com.hand.hls.csh.service.IMainCshWriteOffService;
import com.hand.hls.fct.dto.HlsCusFctContractWithdrawCf;
import com.hand.hls.fct.mapper.HlsCusFctContractWithdrawCfMapper;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.utils.MathUtil;

import java.util.*;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(
        rollbackFor = {Exception.class}
)
public class MainCshWriteOffServiceImpl extends BaseServiceImpl<HlsCusCshWriteOff> implements IMainCshWriteOffService, AppContextInitListener {
    public static final String PROPERTY_DOCUMENT_TYPE_FCT = "FCT";
    @Autowired
    HlsCusCshWriteOffMapper cshWriteOffMapper;
    @Autowired
    CshTransactionService cshTransactionService;
    @Autowired
    HlsCusCshTransactionMapper cshTransactionMapper;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    private static final Map<String, CshTransactionCommon> writeOffRegistion = new HashMap();
    @Autowired
    private JeTrxCommonService jeTrxCommonService;
    @Autowired
    private IMainCshTransactionService mainCshTransactionService;
    @Autowired
    private HlsCusConContractCashflowMapper conContractCashflowMapper;
    @Autowired
    private HlsCusFctContractWithdrawCfMapper fctContractWithdrawCfMapper;
    @Autowired
    HlsCusCshPaymentReqLnMapper hlsCusCshPaymentReqLnMapper;
    @Autowired
    HlsCusCshPaymentReqDtMapper hlsCusCshPaymentReqDtMapper;
    private static final String WRITE_OFF_FLAG_FULL = "FULL";
    private static final String WRITE_OFF_FLAG_PARTIAL = "PARTIAL";

    public MainCshWriteOffServiceImpl() {
    }

    public void contextInitialized(ApplicationContext applicationContext) {
        Map<String, CshTransactionCommon> map = applicationContext.getBeansOfType(CshTransactionCommon.class);
        map.forEach((k, v) -> {
            writeOffRegistion.put(v.getWriteOffType(), v);
        });
    }

    public void createReverseWriteOffJe(IRequest iRequest, CshWriteOff cshWriteOff) {
        Map writeOffMap = new HashMap();
        JeTrxCommonService var10000 = this.jeTrxCommonService;
        AbstractJeTrxService writeOffJetrxService = (AbstractJeTrxService)JeTrxCommonService.map.get("CSH_WRITE_OFF");
        if (writeOffJetrxService != null) {
            writeOffMap.put("jeTrxId", cshWriteOff.getWriteOffId());
            writeOffMap.put("companyId", iRequest.getCompanyId());
            writeOffMap.put("contractId", cshWriteOff.getContractId());
            if (!"PAYMENT_DEBT".equals(cshWriteOff.getWriteOffType()) && !"RECEIPT_CREDIT".equalsIgnoreCase(cshWriteOff.getWriteOffType()) && !"RECEIPT_DEPOSIT".equalsIgnoreCase(cshWriteOff.getWriteOffType())) {
                if ("LON_PAYMENT_DEBT".equals(cshWriteOff.getWriteOffType())) {
                    writeOffMap.put("sourceDoc", "LON_CONTRACT");
                } else if (cshWriteOff.getWriteOffType().startsWith("FCT")) {
                    writeOffMap.put("sourceDoc", "FCT_CONTRACT");
                    writeOffMap.put("jeSourceId", cshWriteOff.getContractId());
                }
            } else {
                writeOffMap.put("sourceDoc", "CON_CONTRACT");
            }

            writeOffMap.put("reverseJeDate", cshWriteOff.getReversedDate());
            writeOffMap.put("reverseJeTrxId", cshWriteOff.getWriteOffId());
            writeOffJetrxService.process(iRequest, writeOffMap);
        }

    }

    public void createWriteOffJe(IRequest iRequest, CshWriteOff cshWriteOff) {
        Map writeOffMap = new HashMap();
        JeTrxCommonService var10000 = this.jeTrxCommonService;
        AbstractJeTrxService writeOffJetrxService = (AbstractJeTrxService)JeTrxCommonService.map.get("CSH_WRITE_OFF");
        if (writeOffJetrxService != null) {
            writeOffMap.put("jeTrxId", cshWriteOff.getWriteOffId());
            writeOffMap.put("companyId", iRequest.getCompanyId());
            writeOffMap.put("contractId", cshWriteOff.getContractId());
            if (!"PAYMENT_DEBT".equals(cshWriteOff.getWriteOffType()) && !"RECEIPT_CREDIT".equalsIgnoreCase(cshWriteOff.getWriteOffType()) && !"RECEIPT_DEPOSIT".equalsIgnoreCase(cshWriteOff.getWriteOffType())) {
                if ("LON_PAYMENT_DEBT".equals(cshWriteOff.getWriteOffType())) {
                    writeOffMap.put("sourceDoc", "LON_CONTRACT");
                } else if (cshWriteOff.getWriteOffType().startsWith("FCT")) {
                    writeOffMap.put("jeSourceDoc", "FCT_CONTRACT");
                }
            } else {
                writeOffMap.put("sourceDoc", "CON_CONTRACT");
            }

            writeOffJetrxService.process(iRequest, writeOffMap);
        }

    }

    public void updateOrigWriteOffAfterReverse(IRequest iRequest, Long writeOffId, Long reverseWriteOffId, Date reverseDate) throws Exception {
        HlsCusCshWriteOff cshWriteOff = new HlsCusCshWriteOff();
        cshWriteOff.setWriteOffId(writeOffId);
        cshWriteOff.setReversedWriteOffId(reverseWriteOffId);
        cshWriteOff.setReversedDate(reverseDate);
        cshWriteOff.setReversedFlag("W");
        this.cshWriteOffMapper.updateCshWriteOffbyId(cshWriteOff.getWriteOffId(), "W", reverseWriteOffId, reverseDate, iRequest.getUserId(), new Date());
    }

    public void updateConCashflowAfterReverse(HlsCusCshWriteOff cshWriteOff) throws Exception {
        if (cshWriteOff.getCashflowId() != null) {
            HlsCusConContractCashflow cashflow = this.conContractCashflowMapper.selectConContractCashflow(cshWriteOff.getCashflowId());
            Double receivedAmount = MathUtil.sub(cashflow.getReceivedAmount(), cshWriteOff.getWriteOffDueAmount());
            if (cashflow.getReceivedPrincipal() == null) {
                cashflow.setReceivedPrincipal(0.0D);
            }

            if (cashflow.getReceivedInterest() == null) {
                cashflow.setReceivedInterest(0.0D);
            }

            if (cshWriteOff.getWriteOffPrincipal() == null) {
                cshWriteOff.setWriteOffPrincipal(0.0D);
            }

            if (cshWriteOff.getWriteOffInterest() == null) {
                cshWriteOff.setWriteOffInterest(0.0D);
            }

            Double receivedPrincipal = MathUtil.sub(cashflow.getReceivedPrincipal(), cshWriteOff.getWriteOffPrincipal());
            Double receivedInterest = MathUtil.sub(cashflow.getReceivedInterest(), cshWriteOff.getWriteOffInterest());
            if (receivedAmount == 0.0D) {
                cashflow.setReceivedAmount(receivedAmount);
                cashflow.setReceivedPrincipal(receivedPrincipal);
                cashflow.setReceivedInterest(receivedInterest);
                cashflow.setWriteOffFlag("NOT");
                cashflow.setFullWriteOffDate((Date)null);
            } else {
                if (receivedAmount >= cashflow.getDueAmount()) {
                    throw new Exception("现金流已收金额超过应收金额，请核查数据!" + cashflow.getCashflowId());
                }

                cashflow.setReceivedAmount(receivedAmount);
                cashflow.setReceivedPrincipal(receivedPrincipal);
                cashflow.setReceivedInterest(receivedInterest);
                cashflow.setWriteOffFlag("PARTIAL");
                cashflow.setFullWriteOffDate((Date)null);
            }

            if (this.checkReceivedPrincipalAndInterest(cshWriteOff)) {
                cashflow.setReceivedPrincipal(0.0D);
                cashflow.setReceivedInterest(0.0D);
            }

            this.cshWriteOffMapper.updateContractCashFlow(cashflow);
        }

    }

    public void updateFctCashflowAfterReverse(HlsCusCshWriteOff cshWriteOff) throws BeyondAmountLimitException {
        Double cshWriteOffAmount = cshWriteOff.getCshWriteOffAmount();
        Double negativeCshWriteOffAmount = CalculateUtil.sub(0.0D, cshWriteOffAmount);
        HlsCusFctContractWithdrawCf fctContractWithdrawCf = (HlsCusFctContractWithdrawCf)this.fctContractWithdrawCfMapper.selectByPrimaryKey(cshWriteOff.getCashflowId());
        Double dueAmount = fctContractWithdrawCf.getDueAmount();
        Double writeOffAmount = fctContractWithdrawCf.getWriteOffAmount();
        Double resultAmount = CalculateUtil.add(negativeCshWriteOffAmount, writeOffAmount);
        if (resultAmount.compareTo(0.0D) == 0) {
            fctContractWithdrawCf.setWriteOffFlag("NOT");
        } else if (resultAmount.compareTo(dueAmount) == 0) {
            fctContractWithdrawCf.setWriteOffFlag("FULL");
        } else {
            if (resultAmount.compareTo(dueAmount) >= 0 || resultAmount.compareTo(0.0D) <= 0) {
                throw new BeyondAmountLimitException();
            }

            fctContractWithdrawCf.setWriteOffFlag("PARTIAL");
        }

        fctContractWithdrawCf.setWriteOffAmount(resultAmount);
        this.fctContractWithdrawCfMapper.updateByPrimaryKeySelective(fctContractWithdrawCf);
    }

    public void updateConCashflowAfter(HlsCusCshWriteOff cshWriteOff) throws Exception {
        if (cshWriteOff.getCashflowId() != null) {
            HlsCusConContractCashflow cashflow = this.conContractCashflowMapper.selectConContractCashflow(cshWriteOff.getCashflowId());
            this.databaseLockProvider.lock(cashflow);
            Double receivedAmount = MathUtil.add(cashflow.getReceivedAmount(), cshWriteOff.getWriteOffDueAmount());
            if (cashflow.getReceivedPrincipal() == null) {
                cashflow.setReceivedPrincipal(0.0D);
            }

            if (cashflow.getReceivedInterest() == null) {
                cashflow.setReceivedInterest(0.0D);
            }

            if (cshWriteOff.getWriteOffPrincipal() == null) {
                cshWriteOff.setWriteOffPrincipal(0.0D);
            }

            if (cshWriteOff.getWriteOffInterest() == null) {
                cshWriteOff.setWriteOffInterest(0.0D);
            }

            Double receivedPrincipal = MathUtil.add(cashflow.getReceivedPrincipal(), cshWriteOff.getWriteOffPrincipal());
            Double receivedInterest = MathUtil.add(cashflow.getReceivedInterest(), cshWriteOff.getWriteOffInterest());
            if (receivedAmount == 0.0D) {
                cashflow.setReceivedAmount(receivedAmount);
                cashflow.setDueAmount(receivedAmount);
                cashflow.setReceivedPrincipal(receivedPrincipal);
                cashflow.setReceivedInterest(receivedInterest);
                cashflow.setWriteOffFlag("NOT");
                cashflow.setFullWriteOffDate((Date)null);
            } else if (receivedAmount < cashflow.getDueAmount()) {
                cashflow.setReceivedAmount(receivedAmount);
                cashflow.setReceivedPrincipal(receivedPrincipal);
                cashflow.setReceivedInterest(receivedInterest);
                cashflow.setDueAmount(receivedAmount);

                cashflow.setWriteOffFlag("PARTIAL");
                cashflow.setFullWriteOffDate((Date)null);
            } else if (receivedAmount.compareTo(cashflow.getDueAmount()) >= 0) {
                cashflow.setReceivedAmount(receivedAmount);
                cashflow.setReceivedPrincipal(receivedPrincipal);
                cashflow.setReceivedInterest(receivedInterest);
                cashflow.setWriteOffFlag("FULL");
                cashflow.setDueAmount(receivedAmount);

                cashflow.setFullWriteOffDate(cshWriteOff.getWriteOffDate());
            }

            if (this.checkReceivedPrincipalAndInterest(cshWriteOff)) {
                cashflow.setReceivedPrincipal(0.0D);
                cashflow.setReceivedInterest(0.0D);
            }

            this.cshWriteOffMapper.updateContractCashFlow(cashflow);
        }

    }

    public void updateFctCashflowAfter(HlsCusCshWriteOff cshWriteOff) throws BeyondAmountLimitException {
        if (cshWriteOff.getCashflowId() != null) {
            HlsCusFctContractWithdrawCf cashFlow = (HlsCusFctContractWithdrawCf)this.fctContractWithdrawCfMapper.selectByPrimaryKey(cshWriteOff.getCashflowId());
            this.databaseLockProvider.lock(cashFlow);
            Double cshWriteOffAmount = cshWriteOff.getWriteOffDueAmount();
            Double cfLeftAmount = CalculateUtil.sub(cashFlow.getDueAmount(), cashFlow.getWriteOffAmount());
            if (cshWriteOffAmount.compareTo(cfLeftAmount) < 0) {
                cashFlow.setWriteOffFlag("PARTIAL");
            } else {
                if (cshWriteOffAmount.compareTo(cfLeftAmount) != 0) {
                    throw new BeyondAmountLimitException();
                }

                cashFlow.setWriteOffFlag("FULL");
            }

            cashFlow.setWriteOffAmount(CalculateUtil.add(cshWriteOffAmount, cashFlow.getWriteOffAmount()));
            this.fctContractWithdrawCfMapper.updateByPrimaryKey(cashFlow);
        }

    }

    public HlsCusCshWriteOff reverseCshWriteOff(IRequest iRequest, Long writeOffId, Date reverseDate, String description) throws Exception {
        HlsCusCshWriteOff cshWriteOff = cshWriteOffMapper.selectByPrimaryKey(writeOffId);
        //this.databaseLockProvider.lock(cshWriteOff);
        String writeOffType = cshWriteOff.getWriteOffType();
        CshTransactionCommon trans = writeOffRegistion.get(writeOffType);
        if (trans == null) {
            throw new WriteOffTypeNullException();
        } else {
            cshWriteOff.setCompanyId(iRequest.getCompanyId());
            if (reverseDate == null && description == null) {
                trans.reversed(iRequest, cshWriteOff);
            } else {
                HlsCusCshWriteOff reverseWriteOff = new HlsCusCshWriteOff();
                PropertyUtils.copyProperties(reverseWriteOff, cshWriteOff);
                if (reverseWriteOff.getWriteOffDueAmount() != null) {
                    reverseWriteOff.setWriteOffDueAmount(MathUtil.mul(-1.0D, reverseWriteOff.getWriteOffDueAmount()));
                }

                if (reverseWriteOff.getWriteOffPrincipal() != null) {
                    reverseWriteOff.setWriteOffPrincipal(MathUtil.mul(-1.0D, reverseWriteOff.getWriteOffPrincipal()));
                }

                if (reverseWriteOff.getWriteOffInterest() != null) {
                    reverseWriteOff.setWriteOffInterest(MathUtil.mul(-1.0D, reverseWriteOff.getWriteOffInterest()));
                }

                reverseWriteOff.setReversedFlag("R");
                reverseWriteOff.setReversedWriteOffId(cshWriteOff.getWriteOffId());
                reverseWriteOff.setReversedDate(reverseDate);
                this.cshWriteOffMapper.insertSelective(reverseWriteOff);
                ((IMainCshWriteOffService)this.self()).updateOrigWriteOffAfterReverse(iRequest, cshWriteOff.getWriteOffId(), reverseWriteOff.getWriteOffId(), reverseDate);
                this.mainCshTransactionService.updateCshTrxAfterWriteOffReverse(cshWriteOff.getCshTransactionId(), cshWriteOff.getWriteOffId());
                if (writeOffType.startsWith("FCT")) {
                    ((IMainCshWriteOffService)this.self()).updateFctCashflowAfterReverse(cshWriteOff);
                } else {
                    ((IMainCshWriteOffService)this.self()).updateConCashflowAfterReverse(cshWriteOff);
                }

                trans.documentReverse(iRequest, cshWriteOff, reverseWriteOff);
            }

            cshWriteOff.setReversedDate(reverseDate);
          //  this.createReverseWriteOffJe(iRequest, cshWriteOff);
            return cshWriteOff;
        }
    }

    public HlsCusCshWriteOff reverseCshWriteOffNew(IRequest iRequest, Long writeOffId, Date reverseDate, String description, Long reverseTransactionId) throws Exception {
        HlsCusCshWriteOff cshWriteOff = (HlsCusCshWriteOff)this.cshWriteOffMapper.selectByPrimaryKey(writeOffId);
        this.databaseLockProvider.lock(cshWriteOff);
        String writeOffType = cshWriteOff.getWriteOffType();
        CshTransactionCommon trans = (CshTransactionCommon)writeOffRegistion.get(writeOffType);
        if (trans == null) {
            throw new WriteOffTypeNullException();
        } else {
            cshWriteOff.setCompanyId(iRequest.getCompanyId());
            if (reverseDate == null && description == null) {
                trans.reversed(iRequest, cshWriteOff);
            } else {
                HlsCusCshWriteOff reverseWriteOff = new HlsCusCshWriteOff();
                PropertyUtils.copyProperties(reverseWriteOff, cshWriteOff);
                if (reverseWriteOff.getWriteOffDueAmount() != null) {
                    reverseWriteOff.setWriteOffDueAmount(MathUtil.mul(-1.0D, reverseWriteOff.getWriteOffDueAmount()));
                }

                if (reverseWriteOff.getWriteOffPrincipal() != null) {
                    reverseWriteOff.setWriteOffPrincipal(MathUtil.mul(-1.0D, reverseWriteOff.getWriteOffPrincipal()));
                }

                if (reverseWriteOff.getWriteOffInterest() != null) {
                    reverseWriteOff.setWriteOffInterest(MathUtil.mul(-1.0D, reverseWriteOff.getWriteOffInterest()));
                }

                reverseWriteOff.setReversedFlag("R");
                reverseWriteOff.setReversedWriteOffId(cshWriteOff.getWriteOffId());
                reverseWriteOff.setReversedDate(reverseDate);
                reverseWriteOff.setCshTransactionId(reverseTransactionId);
                this.cshWriteOffMapper.insertSelective(reverseWriteOff);
                ((IMainCshWriteOffService)this.self()).updateOrigWriteOffAfterReverse(iRequest, cshWriteOff.getWriteOffId(), reverseWriteOff.getWriteOffId(), reverseDate);
                this.mainCshTransactionService.updateCshTrxAfterWriteOffReverse(cshWriteOff.getCshTransactionId(), cshWriteOff.getWriteOffId());
                if (writeOffType.startsWith("FCT")) {
                    ((IMainCshWriteOffService)this.self()).updateFctCashflowAfterReverse(cshWriteOff);
                } else {
                    ((IMainCshWriteOffService)this.self()).updateConCashflowAfterReverse(cshWriteOff);
                }

                trans.documentReverse(iRequest, cshWriteOff, reverseWriteOff);
            }

            this.createReverseWriteOffJe(iRequest, cshWriteOff);
            return cshWriteOff;
        }
    }

    public HlsCusCshWriteOff cshWriteOff(IRequest iRequest, HlsCusCshWriteOff cshWriteOff, Date writeDate) throws Exception {
        HlsCusCshTransaction cusCshTransaction = new HlsCusCshTransaction();
        cusCshTransaction.setTransactionId(cshWriteOff.getCshTransactionId());
        this.databaseLockProvider.lock(cusCshTransaction);
        String writeOffType = cshWriteOff.getWriteOffType();
        CshTransactionCommon trans = (CshTransactionCommon)writeOffRegistion.get(writeOffType);
        if (trans == null) {
            throw new WriteOffTypeNullException();
        } else {
            cshWriteOff.setCompanyId(iRequest.getCompanyId());
            HlsCusCshWriteOff writeOff = new HlsCusCshWriteOff();
            BeanUtils.copyProperties(writeOff, cshWriteOff);
            writeOff.setWriteOffDate(writeDate);
            writeOff.setReversedFlag("N");
            writeOff.setCshWriteOffAmount(writeOff.getWriteOffDueAmount());
            if ("PAYMENT_DEBT".equals(writeOff.getWriteOffType())) {
                HlsCusCshPaymentReqDt cshPaymentReqDt = new HlsCusCshPaymentReqDt();
                cshPaymentReqDt.setPaymentReqLnId(writeOff.getPaymentReqLineId());
                List<HlsCusCshPaymentReqDt> hlsCusCshPaymentReqDtList = this.hlsCusCshPaymentReqDtMapper.select(cshPaymentReqDt);
                Double deductionAmount = hlsCusCshPaymentReqDtList.stream().mapToDouble(CshPaymentReqDt::getDeductAmount).sum();
                Double cshWriteOffAmount = writeOff.getCshWriteOffAmount();
                cshWriteOffAmount = MathUtil.sub(cshWriteOffAmount, deductionAmount);
                writeOff.setCshWriteOffAmount(cshWriteOffAmount);
                writeOff.setWriteOffDueAmount(cshWriteOffAmount);
                cshWriteOff.setCshWriteOffAmount(cshWriteOffAmount);
                cshWriteOff.setWriteOffDueAmount(cshWriteOffAmount);
            }

            writeOff.setWriteOffDocCategory("CON_CONTRACT");
            if (trans.getWriteOffType().startsWith("FCT")) {
                writeOff.setWriteOffDocCategory("FCT_CONTRACT");
//                writeOff.setWithdrawId(writeOff.getQuotationId());
            }

            if ("FCT_PAYMENT_DEBT".equals(trans.getWriteOffType()) || !"FCT_RECEIPT_CREDIT".equals(trans.getWriteOffType()) && this.checkReceivedPrincipalAndInterest(writeOff)) {
                writeOff.setWriteOffPrincipal(0.0D);
                writeOff.setWriteOffInterest(0.0D);
            }

            ((IMainCshWriteOffService)this.self()).insertSelective(iRequest, writeOff);
            this.mainCshTransactionService.updateCshTrxAfterWriteOff(writeOff.getCshTransactionId(), writeOff);
            if (trans.getWriteOffType().startsWith("FCT")) {
                ((IMainCshWriteOffService)this.self()).updateFctCashflowAfter(writeOff);
            } else {
                ((IMainCshWriteOffService)this.self()).updateConCashflowAfter(writeOff);
            }

            trans.documentProcess(iRequest, writeOff, writeDate);
            this.createWriteOffJe(iRequest, writeOff);
            return writeOff;
        }
    }

    public HlsCusCshWriteOff cshWriteOffPay(IRequest iRequest, HlsCusCshWriteOff cshWriteOff, Date writeDate) throws Exception {
        HlsCusCshTransaction cusCshTransaction = new HlsCusCshTransaction();
        cusCshTransaction.setTransactionId(cshWriteOff.getCshTransactionId());
        //this.databaseLockProvider.lock(cusCshTransaction);
        String writeOffType = cshWriteOff.getWriteOffType();
        CshTransactionCommon trans = writeOffRegistion.get(writeOffType);
        if (trans == null) {
            throw new WriteOffTypeNullException();
        } else {
            cshWriteOff.setCompanyId(iRequest.getCompanyId());
            HlsCusCshWriteOff writeOff = new HlsCusCshWriteOff();
            BeanUtils.copyProperties(writeOff, cshWriteOff);
            writeOff.setWriteOffDate(writeDate);
            writeOff.setReversedFlag("N");
            writeOff.setCshWriteOffAmount(writeOff.getWriteOffDueAmount());
//            if ("PAYMENT_DEBT".equals(writeOff.getWriteOffType())) {
//                HlsCusCshPaymentReqDt cshPaymentReqDt = new HlsCusCshPaymentReqDt();
//                cshPaymentReqDt.setPaymentReqLnId(writeOff.getPaymentReqLineId());
//                List<HlsCusCshPaymentReqDt> hlsCusCshPaymentReqDtList = this.hlsCusCshPaymentReqDtMapper.select(cshPaymentReqDt);
//                Double deductionAmount = hlsCusCshPaymentReqDtList.stream().mapToDouble(CshPaymentReqDt::getDeductAmount).sum();
//                Double cshWriteOffAmount = writeOff.getCshWriteOffAmount();
//                cshWriteOffAmount = MathUtil.sub(cshWriteOffAmount, deductionAmount);
//                writeOff.setCshWriteOffAmount(cshWriteOffAmount);
//                writeOff.setWriteOffDueAmount(cshWriteOffAmount);
//                cshWriteOff.setCshWriteOffAmount(cshWriteOffAmount);
//                cshWriteOff.setWriteOffDueAmount(cshWriteOffAmount);
//            }

            writeOff.setWriteOffDocCategory("CON_CONTRACT");
            if (trans.getWriteOffType().startsWith("FCT")) {
                writeOff.setWriteOffDocCategory("FCT_CONTRACT");
//                writeOff.setWithdrawId(writeOff.getQuotationId());
            }

            if ("FCT_PAYMENT_DEBT".equals(trans.getWriteOffType()) || !"FCT_RECEIPT_CREDIT".equals(trans.getWriteOffType()) && this.checkReceivedPrincipalAndInterest(writeOff)) {
                writeOff.setWriteOffPrincipal(0.0D);
                writeOff.setWriteOffInterest(0.0D);
            }

             ((IMainCshWriteOffService)this.self()).insertSelective(iRequest, writeOff);
            this.mainCshTransactionService.updateCshTrxAfterWriteOff(writeOff.getCshTransactionId(), writeOff);
            if (trans.getWriteOffType().startsWith("FCT")) {
                ((IMainCshWriteOffService)this.self()).updateFctCashflowAfter(writeOff);
            } else {
/*
                ((IMainCshWriteOffService)this.self()).updateConCashflowAfter(writeOff);
*/
            }

            trans.documentProcess(iRequest, writeOff, writeDate);
//            this.createWriteOffJe(iRequest, writeOff);
            return writeOff;
        }
    }

    public void reverseCshWriteOffMain(IRequest iRequest, List<HlsCusCshWriteOff> cshWriteOffList, Date reverseDate, String description) throws Exception {
        for(int i = 0; i < cshWriteOffList.size(); ++i) {
            HlsCusCshWriteOff cshWriteOff = (HlsCusCshWriteOff)cshWriteOffList.get(i);
            this.reverseCshWriteOff(iRequest, cshWriteOff.getWriteOffId(), reverseDate, description);
        }

    }

    public void cshWriteOffMain(IRequest iRequest, List<HlsCusCshWriteOff> cshWriteOffList) throws Exception {
        this.checkCshWriteOffs(cshWriteOffList);

        for(int i = 0; i < cshWriteOffList.size(); ++i) {
            HlsCusCshWriteOff cshWriteOff = (HlsCusCshWriteOff)cshWriteOffList.get(i);
            this.cshWriteOff(iRequest, cshWriteOff, ((HlsCusCshWriteOff)cshWriteOffList.get(0)).getWriteOffDate());
        }

    }

    public List<HlsCusCshWriteOff> cshWriteOffMainPay(IRequest iRequest, List<HlsCusCshWriteOff> cshWriteOffList) throws Exception {
        this.checkCshWriteOffs(cshWriteOffList);
        List<HlsCusCshWriteOff> hlsCusCshWriteOffs = new ArrayList<>();
        for(int i = 0; i < cshWriteOffList.size(); ++i) {
            HlsCusCshWriteOff cshWriteOff = (HlsCusCshWriteOff)cshWriteOffList.get(i);
            HlsCusCshWriteOff cusCshWriteOff = new HlsCusCshWriteOff();
            cusCshWriteOff = this.cshWriteOffPay(iRequest, cshWriteOff, ((HlsCusCshWriteOff)cshWriteOffList.get(0)).getWriteOffDate());
            hlsCusCshWriteOffs.add(cusCshWriteOff);
        }
        return  hlsCusCshWriteOffs;

    }

    public void checkCshWriteOffs(List<HlsCusCshWriteOff> cshWriteOffs) throws BeyondAmountLimitException {
        if (cshWriteOffs != null && cshWriteOffs.size() > 0) {
            Double sumAmount = 0.0D;
            Iterator var3 = cshWriteOffs.iterator();

            while(var3.hasNext()) {
                HlsCusCshWriteOff cshWriteOff = (HlsCusCshWriteOff)var3.next();
                if (cshWriteOff.getSurplusAmount() != null && cshWriteOff.getWriteOffDueAmount() != null) {
                    if (cshWriteOff.getWriteOffDueAmount() > cshWriteOff.getSurplusAmount()) {
                        throw new BeyondAmountLimitException();
                    }

                    sumAmount = MathUtil.add(sumAmount, cshWriteOff.getWriteOffDueAmount());
                }
            }

            this.mainCshTransactionService.checkCshTransaction(((HlsCusCshWriteOff)cshWriteOffs.get(0)).getCshTransactionId(), sumAmount);
        }

    }

    boolean checkReceivedPrincipalAndInterest(HlsCusCshWriteOff cshWriteOff) {
        boolean flag = false;
        if (cshWriteOff.getCashflowId() != null) {
            HlsCusConContractCashflow cusConContractCashflow = new HlsCusConContractCashflow();
            cusConContractCashflow.setCashflowId(cshWriteOff.getCashflowId());
            cusConContractCashflow = (HlsCusConContractCashflow)this.conContractCashflowMapper.selectByPrimaryKey(cusConContractCashflow);
            List<Integer> cfItem = Arrays.asList(0, 2, 3, 5, 8, 9, 10);
            if (cfItem.contains(Integer.valueOf(cusConContractCashflow.getCfItem().toString()))) {
                flag = true;
            }
        }

        return flag;
    }
}
