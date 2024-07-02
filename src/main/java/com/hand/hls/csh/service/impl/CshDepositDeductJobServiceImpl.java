package com.hand.hls.csh.service.impl;

import com.alibaba.fastjson.JSON;
import com.hand.hap.core.IRequest;
import com.hand.hap.mail.service.SysMessageEmailRuleService;
import com.hand.hls.bp.components.CalculateUtil;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.service.IConContractChangeReqService;
import com.hand.hls.csh.dto.CshDepositDeductReqHd;
import com.hand.hls.csh.dto.CshDepositDeductReqLn;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.mapper.CshDepositDeductJobMapper;
import com.hand.hls.csh.mapper.CshDepositDeductReqHdMapper;
import com.hand.hls.csh.service.ICshDepositDeductJobLogsService;
import com.hand.hls.csh.service.ICshDepositDeductJobService;
import com.hand.hls.csh.service.ICshDepositDeductReqHdService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.utils.HlsConstantUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 保证金抵扣逻辑实现
 *
 * @author wuyicheng
 * @date 2020/05/17
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CshDepositDeductJobServiceImpl implements ICshDepositDeductJobService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String EXCEPTION_MAIL = "AUTOMATIC_DEPOSIT_DEDUCTION";

    private static final String DETAIL_MAIL = "AUTOMATIC_DEPOSIT_DEDUCTION_DETAIL";

    private static final String BR = "<br />";

    private static final String REQUEST_ID = "requestId";
    private static final String MAIL_HEAD = "合同编号:";

    @Autowired
    private ICshDepositDeductJobLogsService logService;

    @Autowired
    private CshDepositDeductReqHdMapper cshDepositDeductReqHdMapper;

    @Autowired
    private CshDepositDeductJobMapper mapper;

    @Autowired
    private ICshDepositDeductJobService service;

    @Autowired
    private HlsCusConContractCashflowMapper contractCashflowMapper;

    @Autowired
    private IConContractChangeReqService conContractChangeReqService;

    @Autowired
    private ICshDepositDeductReqHdService iCshDepositDeductReqHdService;

    private List<String> errorList;

    @Override
    public void start(IRequest iRequest,String contractNumber){
        logService.setDeductSource("AUTO");
        logService.setIRequest(iRequest);
        errorList = new ArrayList<>(4);
        try {
            logService.success("任务开始 CshDepositDeductJobServiceImpl start",contractNumber,null,null);
            //加上正在运行的标志，同时校验有没有并发
            logService.lock();
            //保证金抵扣逻辑
            depositDeductMain(iRequest,contractNumber);
        } catch (HlsCusException e) {
            logService.error(e,null);
            errorList.add(e.getMessage());
        } catch (Exception e) {
            logService.error(e,"最外层异常 CshDepositDeductJobServiceImpl error");
            errorList.add("保证金自动抵扣程序最外层发生异常");
        }finally {
            //释放本次跑批运行中这个状态
            int count = logService.unLock();
        }
        logService.success("任务结束 CshDepositDeductJobServiceImpl end",contractNumber,null,null);
    }

    /**
     * 保证金抵扣主要逻辑
     * @param iRequest 请求信息
     * @param contractNumber 单独运行时的合同编号
     */
    private void depositDeductMain(IRequest iRequest,String contractNumber) {
        //首先查询出来本次要跑的所有合同
        List<HlsCusConContract> contractList = mapper.selectAllContract(contractNumber);
        logService.success("本次执行合同数" + contractList.size());
        for(HlsCusConContract conContract : contractList){
            //以合同为维度跑批
            List<CshDepositDeductReqHd> cshDepositDeductReqHdList = new ArrayList<>(4);
            //生成抵扣申请单
            service.contractDepositDeduct(iRequest,conContract,cshDepositDeductReqHdList);
            //推送SAP
            service.contractDepositDeductToSap(iRequest,conContract,cshDepositDeductReqHdList,"AUTO");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void contractDepositDeduct(IRequest iRequest, HlsCusConContract conContract,List<CshDepositDeductReqHd> cshDepositDeductReqHdList){
        try {
            //更新当前正在运行的是哪个合同
            logger.info("updateExecuting start : {}",logService.updateExecuting(conContract.getContractNumber()));
            List<HlsCusCshTransaction> transactionList = mapper.selectTransactionBelongContract(conContract.getContractId());
            if(CollectionUtils.isEmpty(transactionList)){
                logService.fail("现金事务状态或金额被更新，抵扣失败",conContract.getContractNumber(),null,null);
                //邮件信息
                errorList.add("合同" + conContract.getContractNumber() + "现金事务状态或金额被更新，抵扣失败");
            }
            checkAllCashflow(conContract.getContractId(), conContract.getContractNumber());

            for(HlsCusCshTransaction  transaction :transactionList){
                logger.info("contract_number:{}  transaction_num{} ",conContract.getContractNumber(),transaction.getTransactionNum());
                transactionDepositDeduct(iRequest,transaction,conContract.getContractNumber(),cshDepositDeductReqHdList);
            }
        } catch (HlsCusException e) {
            logService.error(e,null);
            errorList.add(e.getMessage());
        } catch (Exception e) {
            logService.error(e,"合同维度抵扣发生异常",conContract.getContractNumber(),null,null);
            errorList.add("合同" + conContract.getContractNumber() + "抵扣出现异常，请联系管理员检查日志信息");
        }finally {
            logger.info("updateExecuting end : {}",logService.updateExecuting("released"));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void contractDepositDeductToSap(IRequest iRequest, HlsCusConContract conContract,List<CshDepositDeductReqHd> cshDepositDeductReqHdList,String changeType){
        for(CshDepositDeductReqHd cshDepositDeductReqHd : cshDepositDeductReqHdList){
            try {
                //String status = iCshDepositDeductReqHdService.execDepositDeductReqApproved(iRequest, cshDepositDeductReqHd,changeType);
                //计划任务需要走审批流
                iCshDepositDeductReqHdService.submitDepositDeductReq(iRequest,cshDepositDeductReqHd);
            } catch (HlsCusException e) {
                logService.error(e,null);
                errorList.add(e.getMessage());
            } catch (Exception e) {
                logService.error(e,"合同抵扣发生异常",conContract.getContractNumber(),null,null);
                errorList.add(MAIL_HEAD + conContract.getContractNumber() + "下抵扣申请编号" + cshDepositDeductReqHd.getReqNumber() + "抵扣异常，请联系管理员检查日志信息");
            }
        }
    }

    private void checkAllCashflow(Long contractId,String contractNumber) throws HlsCusException{
        HlsCusConContractCashflow cashflow = new HlsCusConContractCashflow();
        cashflow.setContractId(contractId);
        List<HlsCusConContractCashflow> cashflowList = contractCashflowMapper.queryAutoDepositCashflowOrderWithoutBlockAmt(cashflow);
        StringBuilder sb = new StringBuilder("|");
        for(HlsCusConContractCashflow contractCashflow : cashflowList){
            sb.append(contractCashflow.getCashflowId());
            sb.append("|");
            if(contractCashflow.getBlockAmount() != null && contractCashflow.getBlockAmount().compareTo(0D) != 0){
                logService.fail("现金流存在冻结金额，本次不自动抵扣 现金流ID：" + contractCashflow.getCashflowId(),contractNumber,null,null);
                throw new HlsCusException(MAIL_HEAD +  contractNumber + "下现金流" + "存在冻结金额，本次不自动抵扣");
            }
        }
        logService.success("需要抵扣的现金流条数:" + cashflowList.size() + "现金流ID" + sb.toString(),contractNumber,null,null);
    }

    private void transactionDepositDeduct(IRequest iRequest,HlsCusCshTransaction transaction,String contractNumber,List<CshDepositDeductReqHd> cshDepositDeductReqHdList) throws HlsCusException{
        String errorMsgStr = MAIL_HEAD +  contractNumber + "下现金事务" + transaction.getTransactionNum();
        CshDepositDeductReqHd cshDepositDeductReqHd = new CshDepositDeductReqHd();
        cshDepositDeductReqHd.setTransactionId(transaction.getTransactionId());
        cshDepositDeductReqHd = cshDepositDeductReqHdMapper.selectCshDepositDeductReqHdInit(cshDepositDeductReqHd);
        cshDepositDeductReqHd.setDeductDate(new Date());
        if(cshDepositDeductReqHd.getTransactionId() == null){
            logService.fail("未查询到保证金信息",contractNumber,null,transaction.getTransactionNum());
            throw new HlsCusException(errorMsgStr + "未查询到保证金信息");
        }
        if(cshDepositDeductReqHd.getBlockAmount() != null && cshDepositDeductReqHd.getBlockAmount() != 0){
            logService.fail("现金事务上存在冻结金额，本次不自动抵扣",contractNumber,null,cshDepositDeductReqHd.getTransactionNum());
            throw new HlsCusException(errorMsgStr + "存在冻结金额，本次不自动抵扣");
        }
        Double canReturnAmount = cshDepositDeductReqHd.getCanReturnAmount();
        if (canReturnAmount == null) {
            canReturnAmount = 0D;
        }
        List<CshDepositDeductReqLn> lnList = new ArrayList<>();

        HlsCusConContractCashflow cashflow = new HlsCusConContractCashflow();
        cashflow.setContractId(transaction.getContractId());
        List<HlsCusConContractCashflow> cashflowList = contractCashflowMapper.queryAutoDepositCashflowOrderWithoutBlockAmt(cashflow);
        logService.success("需要抵扣的现金流条数：" + cashflowList.size(),contractNumber,null,cshDepositDeductReqHd.getTransactionNum());
        for (HlsCusConContractCashflow contractCashflow : cashflowList) {
            Double surplusAmount = contractCashflow.getSurplusAmount();
            logService.success("抵扣行开始生成，现金流ID：" + contractCashflow.getCashflowId()
                    + "  surplusAmount：" + surplusAmount + "  canReturnAmount:" + canReturnAmount,contractNumber,null,cshDepositDeductReqHd.getTransactionNum());
            if (canReturnAmount.compareTo(surplusAmount) > 0) {
                conContractChangeReqService.calcFullCashflow(contractCashflow, lnList);
                canReturnAmount = CalculateUtil.sub(canReturnAmount, surplusAmount);
            } else {
                conContractChangeReqService.calcCashflow(contractCashflow, lnList, canReturnAmount);
                canReturnAmount = 0D;
                break;
            }
        }
        if(CollectionUtils.isEmpty(lnList)){
            logger.info("CollectionUtils.isEmpty(lnList)");
            return;
        }
        cshDepositDeductReqHd.setCanReturnAmount(canReturnAmount);
        cshDepositDeductReqHd.setCshDepositDeductReqLnList(lnList);
        cshDepositDeductReqHd.setReqStatus(HlsConstantUtil.WorkFlowStatus.NEW);
        cshDepositDeductReqHd = iCshDepositDeductReqHdService.saveDepositDeductReq(iRequest, cshDepositDeductReqHd,"AUTO");
        /*logger.info("blockAmountWithNewTransaction start");
        iCshDepositDeductReqHdService.blockAmount(iRequest, cshDepositDeductReqHd.getReqHdId());*/
        String status = HlsConstantUtil.DeductSapStatus.FAILURE;
        CshDepositDeductReqHd cshDepositDeductReqHd1 = new CshDepositDeductReqHd();
        cshDepositDeductReqHd1.setReqHdId(cshDepositDeductReqHd.getReqHdId());
        cshDepositDeductReqHd1.setDeductStatus(status);
        cshDepositDeductReqHdMapper.updateByPrimaryKeySelective(cshDepositDeductReqHd1);
        logService.success("抵扣头生成完成",contractNumber,cshDepositDeductReqHd.getReqNumber(),cshDepositDeductReqHd.getTransactionNum());
        cshDepositDeductReqHdList.add(cshDepositDeductReqHd);
        logger.info("cshDepositDeductReqHdList  size:{}",cshDepositDeductReqHdList.size());
    }
}
