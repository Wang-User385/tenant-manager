package com.hand.hls.csh.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.components.CalculateUtil;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.csh.dto.HlsCusDepositDeduction;
import com.hand.hls.csh.dto.HlsCusDepositDeductionHd;
import com.hand.hls.csh.exception.BeyondAmountLimitException;
import com.hand.hls.csh.mapper.HlsCusCshDeductionMapper;
import com.hand.hls.csh.mapper.HlsCusCshWriteOffMapper;
import com.hand.hls.csh.mapper.HlsCusDepositDeductionHdMapper;
import com.hand.hls.csh.mapper.HlsCusDepositDeductionMapper;
import com.hand.hls.csh.service.CshTransactionService;
import com.hand.hls.csh.service.CshWriteOffService;
import com.hand.hls.csh.service.HlsCusDepositDeductionService;
import com.hand.hls.fct.dto.HlsCusFctQuotationCashflow;
import com.hand.hls.fct.service.HlsCusFctQuotationCashflowService;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.utils.exception.HlsCusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusDepositDeductionServiceImpl extends BaseServiceImpl<HlsCusDepositDeduction> implements HlsCusDepositDeductionService {


    @Autowired
    private HlsCusDepositDeductionMapper hlsCusDepositDeductionMapper;

    @Autowired
    private HlsCusConContractCashflowMapper cusConContractCashflowMapper;

    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;

    @Autowired
    private CshTransactionService cshTransactionService;

    @Autowired
    private HlsCusCshWriteOffMapper hlsCusCshWriteOffMapper;

    @Autowired
    private HlsCusConContractCashflowService conContractCashflowService;

    @Autowired
    private HlsCusFctQuotationCashflowService fctQuotationCashflowService;

    @Autowired
    private CshWriteOffService cshWriteOffService;

    @Autowired
    private HlsCusCshDeductionMapper hlsCusCshDeductionMapper;

    @Autowired
    private HlsCusDepositDeductionHdMapper hlsCusDepositDeductionHdMapper;

    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Autowired
    private HlsEmployeeMapper employeeMapper;

    @Autowired
    private IActivitiStartService activitiStartService;

    @Override
    public List<HlsCusDepositDeduction> selectDepositDedctionData(IRequest iRequest, HlsCusDepositDeduction hlsCusDepositDeduction, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return hlsCusDepositDeductionMapper.selectDepositDedctionData(hlsCusDepositDeduction);
    }


    @Override
    public List<HlsCusDepositDeduction> saveDepositDedctionData(IRequest iRequest, List<HlsCusDepositDeduction> hlsCusDepositDeductions) throws IllegalArgumentException, HlsCusException {

        List<HlsCusDepositDeduction> depositDeductionList=null;

        if(hlsCusDepositDeductions.size()>0) {
            HlsCusDepositDeduction hlsCusDepositDeduction=hlsCusDepositDeductions.get(0);

            HlsCusDepositDeductionHd depositDeductionHd =getDepositDeductionHd(iRequest,hlsCusDepositDeduction);
            //修改
            if(hlsCusDepositDeduction.getDepositDeductionHdId()!=null&&!new Long(0L).equals(hlsCusDepositDeduction.getDepositDeductionHdId())){
                hlsCusDepositDeductionHdMapper.updateByPrimaryKeySelective(depositDeductionHd);

            }else{
                //新增
                hlsCusDepositDeductionHdMapper.insertSelective(depositDeductionHd);
                for(HlsCusDepositDeduction deduction:hlsCusDepositDeductions){
                    deduction.setDepositDeductionHdId(depositDeductionHd.getDepositDeductionHdId());
                }
            }

            depositDeductionList = self().batchUpdate(iRequest, hlsCusDepositDeductions);

            int flowCount = hlsCusDepositDeductionMapper.selectCashFlowCount(hlsCusDepositDeduction);
            if(flowCount>0){
                throw new HlsCusException("请勿选择两条相同的现金流");
            }
            //抵扣生成
           // List<HlsCusDepositDeduction> depositDeductions = hlsCusDepositDeductionMapper.selectDepositDedctionData(hlsCusDepositDeduction);

           /* Map map=new HashMap();
            map.put("transactionId",hlsCusDepositDeduction.getTransactionId());
            List<Map> depositMgrs = hlsCusCshDeductionMapper.homePageDepositMgr(map);
            if(depositMgrs.size()!=1){
                throw new HlsCusException("请检查现金事务！");
            }*/
            /*Double dectionAmountSum=hlsCusDepositDeductionMapper.selectNewDectionAmountSum(hlsCusDepositDeduction);
            if(new BigDecimal(depositMgrs.get(0).get("leftAmount").toString()).compareTo(new BigDecimal(dectionAmountSum.toString()))<0){
                throw new HlsCusException("垫付金额超出限制,请检查!");
            }*/
        }
        return depositDeductionList;
    }


    @Override
    public void depositDeductionAdd(IRequest iRequest, HlsCusDepositDeduction depositDeduction ) throws BeyondAmountLimitException{
        //插入两条流水记录
        if ("CON_CONTRACT".equals(depositDeduction.getDeductionDocCategory())) {

            crateConContractFlowTran(iRequest,depositDeduction);
        }
        if ("FCT_CONTRACT".equals(depositDeduction.getDeductionDocCategory())) {

            crateFctContractFlowTran(iRequest,depositDeduction);
        }

    }


    //更新现金流
    public void updateConCsh(IRequest iRequest, HlsCusCshWriteOff cshWriteOff) throws BeyondAmountLimitException {
        if ("CON_CONTRACT".equals(cshWriteOff.getWriteOffDocCategory())) {
            HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
            conContractCashflow.setCashflowId(cshWriteOff.getCashflowId());
            conContractCashflow = conContractCashflowService.selectByPrimaryKey(iRequest, conContractCashflow);
            Double receivedAmount = conContractCashflow.getReceivedAmount();
            if(receivedAmount==null){
                receivedAmount=0D;
                conContractCashflow.setReceivedAmount(0D);
            }
            receivedAmount = CalculateUtil.add( receivedAmount , cshWriteOff.getCshWriteOffAmount());
            conContractCashflow.setReceivedAmount(receivedAmount);
            if (new BigDecimal(conContractCashflow.getDueAmount().toString()).compareTo(new BigDecimal(conContractCashflow.getReceivedAmount()))==0) {
                conContractCashflow.setFullWriteOffDate(cshWriteOff.getWriteOffDate());
                conContractCashflow.setWriteOffFlag("FULL");
            } else if (new BigDecimal(conContractCashflow.getDueAmount().toString()).compareTo(new BigDecimal(conContractCashflow.getReceivedAmount()))>0 && conContractCashflow.getReceivedAmount() >= 0) {
                conContractCashflow.setWriteOffFlag("PARTIAL");
            } else if (new BigDecimal(conContractCashflow.getDueAmount().toString()).compareTo(new BigDecimal(0))==0) {
                conContractCashflow.setWriteOffFlag("NOT");
            }else{
                throw new BeyondAmountLimitException();
            }
            conContractCashflow.setLastReceivedDate(cshWriteOff.getWriteOffDate());
            conContractCashflowService.updateByPrimaryKeySelective(iRequest, conContractCashflow);
        }


        if ("FCT_CONTRACT".equals(cshWriteOff.getWriteOffDocCategory())) {
            HlsCusFctQuotationCashflow fctQuotationCashflow = new HlsCusFctQuotationCashflow();
            fctQuotationCashflow.setQuotationCashflowId(cshWriteOff.getCashflowId());
            fctQuotationCashflow = fctQuotationCashflowService.selectByPrimaryKey(iRequest, fctQuotationCashflow);
            Double receivedAmount = fctQuotationCashflow.getReceivedAmount();
            if(receivedAmount==null){
                receivedAmount=0D;
                fctQuotationCashflow.setReceivedAmount(0D);
            }
            receivedAmount = CalculateUtil.add( receivedAmount , cshWriteOff.getCshWriteOffAmount());
            fctQuotationCashflow.setWriteOffAmount(receivedAmount);
            fctQuotationCashflow.setReceivedAmount(receivedAmount);
            if (new BigDecimal( fctQuotationCashflow.getDueAmount().toString()).compareTo(new BigDecimal(fctQuotationCashflow.getReceivedAmount().toString()))==0) {
                fctQuotationCashflow.setFullWriteOffDate(cshWriteOff.getWriteOffDate());
                fctQuotationCashflow.setWriteOffFlag("FULL");
            } else if (new BigDecimal( fctQuotationCashflow.getDueAmount().toString()) .compareTo(new BigDecimal( fctQuotationCashflow.getReceivedAmount().toString()))>0 && fctQuotationCashflow.getReceivedAmount() >= 0) {
                fctQuotationCashflow.setWriteOffFlag("PARTIAL");
            } else if (new BigDecimal(fctQuotationCashflow.getReceivedAmount().toString()).compareTo(new BigDecimal(0))== 0) {
                fctQuotationCashflow.setWriteOffFlag("NOT");
            }else{
                throw new BeyondAmountLimitException();
            }
            fctQuotationCashflow.setLastReceivedDate(cshWriteOff.getWriteOffDate());
            fctQuotationCashflowService.updateByPrimaryKeySelective(iRequest, fctQuotationCashflow);
        }

    }

    private void crateConContractFlowTran(IRequest iRequest, HlsCusDepositDeduction depositDeduction) throws BeyondAmountLimitException{

        //创建应收现金流
        HlsCusConContractCashflow cashflowIn = new HlsCusConContractCashflow();
        cashflowIn.setContractId(depositDeduction.getContractId());
        cashflowIn.setWriteOffFlag("NOT");
        cashflowIn.setQuotationId(0L);
        cashflowIn.setCfItem(14L);
        cashflowIn.setCfType(14L);
        cashflowIn.setCfDirection("INFLOW");
        cashflowIn.setCfStatus("RELEASE");
        cashflowIn.setTimes(depositDeduction.getTimes());
        cashflowIn.setDueAmount(depositDeduction.getDeductionAmount());
        cashflowIn.setPrincipal(depositDeduction.getDeductionPrincipal());
        cashflowIn.setInterest(depositDeduction.getDeductionInterest());
        cashflowIn.setDueDate(depositDeduction.getDeductionDate());
        cashflowIn.setCalcDate(depositDeduction.getDeductionDate());
        cashflowIn.setReceivedAmount(0D);
        cashflowIn.setCreatedBy(iRequest.getUserId());
        cashflowIn.setLastUpdatedBy(iRequest.getUserId());
        conContractCashflowService.insertSelective(iRequest,cashflowIn);


        //创建应付现金流
        HlsCusConContractCashflow cashflowOut = new HlsCusConContractCashflow();
        cashflowOut.setContractId(depositDeduction.getContractId());
        cashflowOut.setQuotationId(0L);
        cashflowOut.setWriteOffFlag("FULL");
        cashflowOut.setCfItem(13L);
        cashflowOut.setCfType(13L);
        cashflowOut.setCfDirection("OUTFLOW");
        cashflowOut.setCfStatus("RELEASE");
        cashflowOut.setTimes(depositDeduction.getTimes());
        cashflowOut.setDueAmount(depositDeduction.getDeductionAmount());
        cashflowOut.setPrincipal(depositDeduction.getDeductionPrincipal());
        cashflowOut.setInterest(depositDeduction.getDeductionInterest());
        cashflowOut.setDueDate(depositDeduction.getDeductionDate());
        cashflowOut.setCalcDate(depositDeduction.getDeductionDate());
        cashflowOut.setReceivedAmount(depositDeduction.getDeductionAmount());
        cashflowOut.setFullWriteOffDate(depositDeduction.getDeductionDate());
        cashflowOut.setLastReceivedDate(depositDeduction.getDeductionDate());
        cashflowOut.setCreatedBy(iRequest.getUserId());
        cashflowOut.setLastUpdatedBy(iRequest.getUserId());
        conContractCashflowService.insertSelective(iRequest,cashflowOut);


        HlsCusCshTransaction cshTransaction= new HlsCusCshTransaction();
        cshTransaction.setTransactionId(depositDeduction.getTransactionId());
        cshTransaction = cshTransactionService.selectByPrimaryKey(iRequest, cshTransaction);
        //创建现金事务
        HlsCusCshTransaction transaction = new HlsCusCshTransaction();
        transaction.setBankAccountId(cshTransaction.getBankAccountId());
        transaction.setBpId(cshTransaction.getBpId());
        transaction.setBpBankAccountId(cshTransaction.getBpBankAccountId());
        transaction.setBpBankAccountName(cshTransaction.getBpBankAccountName());
        transaction.setBpBankAccountNum(cshTransaction.getBpBankAccountNum());
        transaction.setBpBankBranchName(cshTransaction.getBpBankBranchName());
        transaction.setTransactionCategory("CSH_TRANSACTION");
        transaction.setBusinessType("DEPOSIT_ADD_CREDIT");
        transaction.setTransactionType("DEPOSIT_ADD_CREDIT");
        Map<String, String> params = new HashMap<String, String>();
        transaction.setTransactionNum(fndCodingRuleValuesService.getCodeRuleValue(iRequest, transaction.getTransactionCategory(), transaction.getTransactionType(), transaction.getBusinessType(), params));
        transaction.setTransactionDate(depositDeduction.getDeductionDate());
        transaction.setPenaltyCalcDate(depositDeduction.getDeductionDate());
        transaction.setCompanyId(cshTransaction.getCompanyId());
        transaction.setTransactionAmount(0D);
        transaction.setCurrencyCode("CNY");
        transaction.setPaymentMethod("T/T");
        transaction.setReversedFlag("N");
        transaction.setSourceDocCategory("CON_CONTRACT");
        transaction.setWriteOffFlag("FULL");
        transaction.setWriteOffAmount(0D);
        transaction.setFullWriteOffDate(depositDeduction.getDeductionDate());
        transaction.setCreatedBy(iRequest.getUserId());
        transaction.setLastUpdatedBy(iRequest.getUserId());
        cshTransactionService.insertSelective(iRequest,transaction);

        //创建核销记录

        //租金 已核销
        HlsCusCshWriteOff hlsCusCshWriteOff = new HlsCusCshWriteOff();
        hlsCusCshWriteOff.setCashflowId(depositDeduction.getCashflowId());
        hlsCusCshWriteOff.setContractId(depositDeduction.getContractId());
        hlsCusCshWriteOff.setCshWriteOffAmount(depositDeduction.getDeductionAmount());
        hlsCusCshWriteOff.setWriteOffDueAmount(depositDeduction.getDeductionAmount());
        hlsCusCshWriteOff.setWriteOffPrincipal(depositDeduction.getDeductionPrincipal());
        hlsCusCshWriteOff.setWriteOffInterest(depositDeduction.getDeductionInterest());
        hlsCusCshWriteOff.setCshTransactionId(transaction.getTransactionId());
        hlsCusCshWriteOff.setWriteOffDate(depositDeduction.getDeductionDate());
        hlsCusCshWriteOff.setReversedFlag("N");
        hlsCusCshWriteOff.setCfType(depositDeduction.getCfType());
        hlsCusCshWriteOff.setCfItem(depositDeduction.getCfItem());
        hlsCusCshWriteOff.setTimes(depositDeduction.getTimes());
        hlsCusCshWriteOff.setWriteOffType("DEPOSIT_ADD_CREDIT");
        hlsCusCshWriteOff.setWriteOffDocCategory("CON_CONTRACT");
        hlsCusCshWriteOff.setCreatedBy(iRequest.getUserId());
        hlsCusCshWriteOff.setLastUpdatedBy(iRequest.getUserId());
        hlsCusCshWriteOffMapper.insertSelective(hlsCusCshWriteOff);
        updateConCsh(iRequest,hlsCusCshWriteOff);


        //自动生成的 应付现金流已核销
        HlsCusCshWriteOff CshWriteOff = new HlsCusCshWriteOff();
        CshWriteOff.setCashflowId(cashflowOut.getCashflowId());
        CshWriteOff.setContractId(cashflowOut.getContractId());
        CshWriteOff.setCshWriteOffAmount(depositDeduction.getDeductionAmount());
        CshWriteOff.setWriteOffDueAmount(depositDeduction.getDeductionAmount());
        CshWriteOff.setWriteOffPrincipal(depositDeduction.getDeductionPrincipal());
        CshWriteOff.setWriteOffInterest(depositDeduction.getDeductionInterest());
        CshWriteOff.setCshTransactionId(transaction.getTransactionId());
        CshWriteOff.setWriteOffDate(depositDeduction.getDeductionDate());
        CshWriteOff.setReversedFlag("N");
        CshWriteOff.setCfType(cashflowOut.getCfType());
        CshWriteOff.setCfItem(cashflowOut.getCfItem());
        CshWriteOff.setTimes(cashflowOut.getTimes());
        CshWriteOff.setWriteOffType("DEPOSIT_ADD_CREDIT");
        CshWriteOff.setWriteOffDocCategory("CON_CONTRACT");
        CshWriteOff.setCreatedBy(iRequest.getUserId());
        CshWriteOff.setLastUpdatedBy(iRequest.getUserId());
        hlsCusCshWriteOffMapper.insertSelective(CshWriteOff);
    }


    private void crateFctContractFlowTran(IRequest iRequest, HlsCusDepositDeduction depositDeduction) throws BeyondAmountLimitException{
        //创建应收现金流
        HlsCusFctQuotationCashflow cashflowIn = new HlsCusFctQuotationCashflow();
        cashflowIn.setContractId(depositDeduction.getContractId());
        cashflowIn.setQuotationId(0L);
        cashflowIn.setWriteOffFlag("NOT");
        cashflowIn.setCfItem(14L);
        cashflowIn.setCfType(14L);
        cashflowIn.setCfDirection("INFLOW");
        cashflowIn.setCfStatus("RELEASE");
        cashflowIn.setTimes(depositDeduction.getTimes());
        cashflowIn.setDueAmount(depositDeduction.getDeductionAmount());
        cashflowIn.setPrincipal(depositDeduction.getDeductionPrincipal());
        cashflowIn.setInterest(depositDeduction.getDeductionInterest());
        cashflowIn.setDueDate(depositDeduction.getDeductionDate());
        cashflowIn.setCalcDate(depositDeduction.getDeductionDate());
        cashflowIn.setWriteOffAmount(0D);
        cashflowIn.setReceivedAmount(0D);
        cashflowIn.setGeneratedSource("CSH_DEPOSIT_DEDUCTION");
        cashflowIn.setGeneratedSourceDocId(depositDeduction.getDepositDeductionId());
        cashflowIn.setCreatedBy(iRequest.getUserId());
        cashflowIn.setLastUpdatedBy(iRequest.getUserId());
        fctQuotationCashflowService.insertSelective(iRequest,cashflowIn);


        //创建应付现金流
        HlsCusFctQuotationCashflow cashflowOut = new HlsCusFctQuotationCashflow();
        cashflowOut.setContractId(depositDeduction.getContractId());
        cashflowOut.setQuotationId(0L);
        cashflowOut.setWriteOffFlag("FULL");
        cashflowOut.setCfItem(13L);
        cashflowOut.setCfType(13L);
        cashflowOut.setCfDirection("OUTFLOW");
        cashflowOut.setCfStatus("RELEASE");
        cashflowOut.setTimes(depositDeduction.getTimes());
        cashflowOut.setDueAmount(depositDeduction.getDeductionAmount());
        cashflowOut.setPrincipal(depositDeduction.getDeductionPrincipal());
        cashflowOut.setInterest(depositDeduction.getDeductionInterest());
        cashflowOut.setDueDate(depositDeduction.getDeductionDate());
        cashflowOut.setCalcDate(depositDeduction.getDeductionDate());
        cashflowOut.setFullWriteOffDate(depositDeduction.getDeductionDate());
        cashflowOut.setWriteOffAmount(depositDeduction.getDeductionAmount());
        cashflowOut.setReceivedAmount(depositDeduction.getDeductionAmount());
        cashflowOut.setLastReceivedDate(depositDeduction.getDeductionDate());
        cashflowOut.setGeneratedSource("CSH_DEPOSIT_DEDUCTION");
        cashflowOut.setGeneratedSourceDocId(depositDeduction.getDepositDeductionId());
        cashflowOut.setCreatedBy(iRequest.getUserId());
        cashflowOut.setLastUpdatedBy(iRequest.getUserId());
        fctQuotationCashflowService.insertSelective(iRequest,cashflowOut);


        HlsCusCshTransaction cshTransaction= new HlsCusCshTransaction();
        cshTransaction.setTransactionId(depositDeduction.getTransactionId());
        cshTransaction = cshTransactionService.selectByPrimaryKey(iRequest, cshTransaction);
        //创建现金事务
        HlsCusCshTransaction transaction = new HlsCusCshTransaction();
        transaction.setBankAccountId(cshTransaction.getBankAccountId());
        transaction.setBpId(cshTransaction.getBpId());
        transaction.setBpBankAccountId(cshTransaction.getBpBankAccountId());
        transaction.setBpBankAccountName(cshTransaction.getBpBankAccountName());
        transaction.setBpBankAccountNum(cshTransaction.getBpBankAccountNum());
        transaction.setBpBankBranchName(cshTransaction.getBpBankBranchName());
        transaction.setTransactionCategory("CSH_TRANSACTION");
        transaction.setBusinessType("DEPOSIT_ADD");
        transaction.setTransactionType("DEPOSIT_ADD");
        Map<String, String> params = new HashMap<String, String>();
        transaction.setTransactionNum(fndCodingRuleValuesService.getCodeRuleValue(iRequest, transaction.getTransactionCategory(), transaction.getTransactionType(), transaction.getBusinessType(), params));
        transaction.setTransactionDate(depositDeduction.getDeductionDate());
        transaction.setPenaltyCalcDate(depositDeduction.getDeductionDate());
        transaction.setCompanyId(cshTransaction.getCompanyId());
        transaction.setTransactionAmount(0D);
        transaction.setCurrencyCode("CNY");
        transaction.setPaymentMethod("T/T");
        transaction.setReversedFlag("N");
        transaction.setSourceDocCategory("FCT_CONTRACT");
        transaction.setWriteOffFlag("FULL");
        transaction.setWriteOffAmount(0D);
        transaction.setFullWriteOffDate(depositDeduction.getDeductionDate());
        transaction.setCreatedBy(iRequest.getUserId());
        transaction.setLastUpdatedBy(iRequest.getUserId());
        cshTransactionService.insertSelective(iRequest,transaction);

        //创建核销记录

        //租金 已核销
        HlsCusCshWriteOff hlsCusCshWriteOff = new HlsCusCshWriteOff();
        hlsCusCshWriteOff.setCashflowId(depositDeduction.getCashflowId());
        hlsCusCshWriteOff.setContractId(depositDeduction.getContractId());
        hlsCusCshWriteOff.setCshWriteOffAmount(depositDeduction.getDeductionAmount());
        hlsCusCshWriteOff.setWriteOffDueAmount(depositDeduction.getDeductionAmount());
        hlsCusCshWriteOff.setWriteOffPrincipal(depositDeduction.getDeductionPrincipal());
        hlsCusCshWriteOff.setWriteOffInterest(depositDeduction.getDeductionInterest());
        hlsCusCshWriteOff.setCshTransactionId(transaction.getTransactionId());
        hlsCusCshWriteOff.setWriteOffDate(depositDeduction.getDeductionDate());
        hlsCusCshWriteOff.setReversedFlag("N");
        hlsCusCshWriteOff.setCfType(depositDeduction.getCfType());
        hlsCusCshWriteOff.setCfItem(depositDeduction.getCfItem());
        hlsCusCshWriteOff.setTimes(depositDeduction.getTimes());
        hlsCusCshWriteOff.setWriteOffDocCategory("FCT_CONTRACT");
        hlsCusCshWriteOff.setWriteOffType("DEPOSIT_ADD_CREDIT");
        hlsCusCshWriteOff.setCreatedBy(iRequest.getUserId());
        hlsCusCshWriteOff.setLastUpdatedBy(iRequest.getUserId());
        hlsCusCshWriteOffMapper.insertSelective(hlsCusCshWriteOff);
        updateConCsh(iRequest,hlsCusCshWriteOff);


        //自动生成的 应付现金流已核销
        HlsCusCshWriteOff CshWriteOff = new HlsCusCshWriteOff();
        CshWriteOff.setCashflowId(cashflowOut.getQuotationCashflowId());
        CshWriteOff.setContractId(cashflowOut.getContractId());
        CshWriteOff.setCshWriteOffAmount(depositDeduction.getDeductionAmount());
        CshWriteOff.setWriteOffDueAmount(depositDeduction.getDeductionAmount());
        CshWriteOff.setWriteOffPrincipal(depositDeduction.getDeductionPrincipal());
        CshWriteOff.setWriteOffInterest(depositDeduction.getDeductionInterest());
        CshWriteOff.setCshTransactionId(transaction.getTransactionId());
        CshWriteOff.setWriteOffDate(depositDeduction.getDeductionDate());
        CshWriteOff.setReversedFlag("N");
        CshWriteOff.setCfType(cashflowOut.getCfType());
        CshWriteOff.setCfItem(cashflowOut.getCfItem());
        CshWriteOff.setTimes(cashflowOut.getTimes());
        CshWriteOff.setWriteOffType("DEPOSIT_ADD_CREDIT");
        CshWriteOff.setWriteOffDocCategory("FCT_CONTRACT");
        CshWriteOff.setCreatedBy(iRequest.getUserId());
        CshWriteOff.setLastUpdatedBy(iRequest.getUserId());
        hlsCusCshWriteOffMapper.insertSelective(CshWriteOff);
    }


    @Override
    public Double selectNewDectionAmountSum(HlsCusDepositDeduction hlsCusDepositDeduction) {
        return hlsCusDepositDeductionMapper.selectNewDectionAmountSum(hlsCusDepositDeduction);
    }

    private HlsCusDepositDeductionHd getDepositDeductionHd(IRequest iRequest, HlsCusDepositDeduction hlsCusDepositDeduction){
        HlsCusDepositDeductionHd depositDeductionHd=new HlsCusDepositDeductionHd();
        depositDeductionHd.setContractId(hlsCusDepositDeduction.getContractId());
        depositDeductionHd.setBpId(hlsCusDepositDeduction.getBpId());
        depositDeductionHd.setDeductionDate(hlsCusDepositDeduction.getDeductionDate());
        depositDeductionHd.setDeductionDocCategory(hlsCusDepositDeduction.getDeductionDocCategory());
        depositDeductionHd.setDeductionType(hlsCusDepositDeduction.getDeductionType());
        depositDeductionHd.setDepositStatus(hlsCusDepositDeduction.getDepositStatus());
        depositDeductionHd.setTransactionId(hlsCusDepositDeduction.getTransactionId());
        depositDeductionHd.setDepositDeductionHdId(hlsCusDepositDeduction.getDepositDeductionHdId());
        depositDeductionHd.setCreatedBy(iRequest.getUserId());
        depositDeductionHd.setLastUpdatedBy(iRequest.getUserId());
        return depositDeductionHd;
    }

    @Override
    public void approvalDepositDeduction(IRequest iRequest, List<HlsCusDepositDeduction> hlsCusDepositDeductions) throws  IllegalArgumentException, HlsCusException {
        //保存
        self().saveDepositDedctionData(iRequest,hlsCusDepositDeductions);

        HlsCusDepositDeduction  depositDeduction= hlsCusDepositDeductions.get(0);

        HlsCusDepositDeductionHd depositDeductionHd =getDepositDeductionHd(iRequest,depositDeduction);

        databaseLockProvider.lock(depositDeductionHd);
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        if(ObjectUtils.isEmpty(employee)){
            throw new HlsCusException("获取提交人失败");
        }
        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);

        List<HlsCusDepositDeductionHd> list = new ArrayList<>();
        list.add(depositDeductionHd);
        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        Boolean backFlag=false;
        //保证金抵扣
        if ("DEPOSIT_DEDUCT_CREDIT".equalsIgnoreCase(depositDeductionHd.getDeductionType())) {
            params.put("workFlowType", "CM_DEPOSIT_DEDUCTION_WFL");
        } else if("DEPOSIT_ADD_CREDIT".equalsIgnoreCase(depositDeductionHd.getDeductionType())){
            //保证金垫付
            if("NEW".equals(depositDeductionHd.getDepositStatus())||"APPROVED_RETURN".equals(depositDeductionHd.getDepositStatus())||"REJECTED".equals(depositDeductionHd.getDepositStatus())){
                params.put("workFlowType", "CM_DEPOSIT_ADVANCE_PAYMENT_WFL");
            }else{
                backFlag=true;
                params.put("workFlowType", "CM_DEPOSIT_RUSH_BACK_WFL");
            }
        }
        activitiStartService.start(iRequest, list, params);

        //修改单据状态
        if(backFlag){
            depositDeductionHd.setDepositStatus("BACK_APPROVING");
        }else{
            depositDeductionHd.setDepositStatus("APPROVING");
        }
        hlsCusDepositDeductionHdMapper.updateByPrimaryKeySelective(depositDeductionHd);


    }
}