package com.hand.hls.csh.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.csh.dto.CshDepositDeductJobLogs;
import com.hand.hls.csh.dto.CshDepositDeductReqHd;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.mapper.CshDepositDeductJobLogsMapper;
import com.hand.hls.csh.mapper.HlsCusCshTransactionMapper;
import com.hand.hls.csh.service.ICshDepositDeductJobLogsService;
import com.hand.hls.exception.HlsCusException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;


/**
 * 保证金抵扣日志插入实现
 */

@Service
@Transactional(rollbackFor = Exception.class)
public class CshDepositDeductJobLogsServiceImpl extends BaseServiceImpl<CshDepositDeductJobLogs> implements ICshDepositDeductJobLogsService {

    /**
     * 抵扣日志来源
     */
    private String deductSource = "";

    /**
     * 请求人
     */
    private IRequest iRequest = null;

    @Override
    public void setIRequest(IRequest iRequest) {
        this.iRequest = iRequest;
    }


    @Override
    public void setDeductSource(String deductSource) {
        this.deductSource = deductSource;
    }


    @Autowired
    private ICshDepositDeductJobLogsService service;

    @Autowired
    private HlsCusCshTransactionMapper hlsCusCshTransactionMapper;

    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;

    private static final String REQUEST_ID = "requestId";
    private static final String ERROR = "ERROR";
    private static final String SUCCESS = "SUCCESS";
    private static final String FAIL = "FAIL";
    private static final String EXECUTING = "EXECUTING";


    @Autowired
    private CshDepositDeductJobLogsMapper cshDepositDeductJobLogsMapper;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public void error(Throwable throwable,String errorMsg) {
        this.error(throwable,errorMsg,null,null,null);
    }


    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public void error(Throwable throwable,String errorMsg,String contractNumber,String reqNumber,String transactionNum) {
        logger.error(errorMsg,throwable);
        CshDepositDeductJobLogs cshDepositDeductJobLogs = new CshDepositDeductJobLogs();
        cshDepositDeductJobLogs.setContractNumber(contractNumber);
        cshDepositDeductJobLogs.setReqNumber(reqNumber);
        cshDepositDeductJobLogs.setTransactionNum(transactionNum);
        cshDepositDeductJobLogs.setStatus(ERROR);
        cshDepositDeductJobLogs.setErrorMsgPreview(errorMsg);
        cshDepositDeductJobLogs.setErrorMsg(getStackTrace(throwable));
        this.insertLog(cshDepositDeductJobLogs);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public void success(String successMsg) {
        this.success(successMsg,null,null,null);
    }


    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public void success(String successMsg,String contractNumber,String reqNumber,String transactionNum) {
        this.insertInfo(SUCCESS,successMsg,contractNumber,reqNumber,transactionNum);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public void fail(String failMsg) {
        this.fail(failMsg,null,null,null);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public void fail(String failMsg,String contractNumber,String reqNumber,String transactionNum) {
        this.insertInfo(FAIL,failMsg,contractNumber,reqNumber,transactionNum);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public void lock() throws Exception {
        //如果已经有在运行中的任务，本次任务不执行
        logger.info("lock start");
        check();
        CshDepositDeductJobLogs cshDepositDeductJobLogs = new CshDepositDeductJobLogs();
        cshDepositDeductJobLogs.setStatus(EXECUTING);
        cshDepositDeductJobLogs.setErrorMsg("此条数据用于记录正在运行中的记录");
        service.insertLogWithNewTransaction(cshDepositDeductJobLogs);
        //插入运行中的标记后再次查询
        checkSleep();
        logger.info("lock end");
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public void lockDeductConfirm(String contractNumber) throws Exception {
        //如果已经有在运行中的任务，本次任务不执行
        logger.info("lockDeductConfirm start");
        checkDeductConfirm(contractNumber);
        CshDepositDeductJobLogs cshDepositDeductJobLogs = new CshDepositDeductJobLogs();
        cshDepositDeductJobLogs.setStatus(EXECUTING);
        cshDepositDeductJobLogs.setContractNumber(contractNumber);
        cshDepositDeductJobLogs.setErrorMsg("此条数据用于记录正在运行中的记录");
        service.insertLogWithNewTransaction(cshDepositDeductJobLogs);
        //插入运行中的标记后再次查询
        checkSleepDeductConfirm(contractNumber);
        logger.info("lockDeductConfirm end");
    }



    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public int unLock() {
        logger.info("unLock start");
        return cshDepositDeductJobLogsMapper.updateExecutedByBatchId(MDC.get(REQUEST_ID));
    }


    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public int updateExecuting(String contractNumber) {
        logger.info("update start:{}",contractNumber);
        CshDepositDeductJobLogs cshDepositDeductJobLogsQuery = new CshDepositDeductJobLogs();
        cshDepositDeductJobLogsQuery.setBatchId(MDC.get(REQUEST_ID));
        cshDepositDeductJobLogsQuery.setStatus(EXECUTING);
        cshDepositDeductJobLogsQuery.setLastUpdateDate(new Date());
        List<CshDepositDeductJobLogs> cshDepositDeductJobLogsList  =  cshDepositDeductJobLogsMapper.select(cshDepositDeductJobLogsQuery);
        if(CollectionUtils.isEmpty(cshDepositDeductJobLogsList)){
            logger.warn("can't find BatchId!");
            return 0;
        }
        CshDepositDeductJobLogs cshDepositDeductJobLogs = new CshDepositDeductJobLogs();
        cshDepositDeductJobLogs.setLogId(cshDepositDeductJobLogsList.get(0).getLogId());
        cshDepositDeductJobLogs.setContractNumber(contractNumber);
        return cshDepositDeductJobLogsMapper.updateByPrimaryKeySelective(cshDepositDeductJobLogs);
    }

    private void insertInfo(String status,String errorMsg,String contractNumber,String reqNumber,String transactionNum){
        logger.info(errorMsg);
        CshDepositDeductJobLogs cshDepositDeductJobLogs = new CshDepositDeductJobLogs();
        cshDepositDeductJobLogs.setContractNumber(contractNumber);
        cshDepositDeductJobLogs.setReqNumber(reqNumber);
        cshDepositDeductJobLogs.setTransactionNum(transactionNum);
        cshDepositDeductJobLogs.setStatus(status);
        cshDepositDeductJobLogs.setErrorMsg(errorMsg);
        this.insertLog(cshDepositDeductJobLogs);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public void insertLogWithNewTransaction(CshDepositDeductJobLogs cshDepositDeductJobLogs){
        this.insertLog(cshDepositDeductJobLogs);
    }

    private void insertLog(CshDepositDeductJobLogs cshDepositDeductJobLogs){
        cshDepositDeductJobLogs.setBatchId(MDC.get(REQUEST_ID));
        cshDepositDeductJobLogs.setPushDate(new Date());
        cshDepositDeductJobLogs.setDeductSource(deductSource);
        String errorMsg = cshDepositDeductJobLogs.getErrorMsg();
        if(StringUtils.isNotEmpty(errorMsg)){
            //截取1000位做预览使用，方便前台查询
            String substring = errorMsg.substring(0, Math.min(errorMsg.length(), 500));
            if(StringUtils.isNotEmpty(cshDepositDeductJobLogs.getErrorMsgPreview())){
                cshDepositDeductJobLogs.setErrorMsgPreview(cshDepositDeductJobLogs.getErrorMsgPreview() + substring);
            }else{
                cshDepositDeductJobLogs.setErrorMsgPreview(substring);
            }
        }
        if(iRequest != null && iRequest.getUserId() != null){
            cshDepositDeductJobLogs.setCreatedBy(iRequest.getUserId());
            cshDepositDeductJobLogs.setLastUpdatedBy(iRequest.getUserId());
        }
        cshDepositDeductJobLogsMapper.insertSelective(cshDepositDeductJobLogs);
    }

    private String getStackTrace(Throwable throwable){
        try {
            try (StringWriter stringWriter = new StringWriter(); PrintWriter printWriter = new PrintWriter(stringWriter)) {
                throwable.printStackTrace(printWriter);
                return stringWriter.toString();
            }
        } catch (IOException e) {
            return e.getMessage();
        }
    }


    private void check() throws Exception{
        CshDepositDeductJobLogs cshDepositDeductJobLogs = new CshDepositDeductJobLogs();
        cshDepositDeductJobLogs.setBatchId(MDC.get(REQUEST_ID));
        String batchId = cshDepositDeductJobLogsMapper.selectExecutingBatchId(cshDepositDeductJobLogs);
        if(StringUtils.isNotEmpty(batchId)){
            throw new HlsCusException("批次号为：" + batchId + "的任务仍在运行中，本次运行取消");
        }
    }

    private void checkSleep() throws Exception{
        Thread.sleep(500);
        CshDepositDeductJobLogs cshDepositDeductJobLogsAfter = new CshDepositDeductJobLogs();
        cshDepositDeductJobLogsAfter.setBatchId(MDC.get(REQUEST_ID));
        String batchId = cshDepositDeductJobLogsMapper.selectExecutingBatchIdNew(cshDepositDeductJobLogsAfter);
        if(StringUtils.isNotEmpty(batchId)){
            throw new HlsCusException("批次号为：" + batchId + "的任务仍在运行中，本次运行取消");
        }
    }

    private void checkDeductConfirm(String contractNumber) throws Exception{
        CshDepositDeductJobLogs cshDepositDeductJobLogs = new CshDepositDeductJobLogs();
        cshDepositDeductJobLogs.setBatchId(MDC.get(REQUEST_ID));
        cshDepositDeductJobLogs.setContractNumber(contractNumber);
        String batchId = cshDepositDeductJobLogsMapper.selectDeductConfirmExecutingBatchId(cshDepositDeductJobLogs);
        if(StringUtils.isNotEmpty(batchId)){
            throw new HlsCusException("合同号：" + contractNumber + "正在确认中，请稍后再确认");
        }
    }

    private void checkSleepDeductConfirm(String contractNumber) throws Exception{
        Thread.sleep(500);
        CshDepositDeductJobLogs cshDepositDeductJobLogsAfter = new CshDepositDeductJobLogs();
        cshDepositDeductJobLogsAfter.setContractNumber(contractNumber);
        cshDepositDeductJobLogsAfter.setBatchId(MDC.get(REQUEST_ID));
        String batchId = cshDepositDeductJobLogsMapper.selectDeductConfirmExecutingBatchIdNew(cshDepositDeductJobLogsAfter);
        if(StringUtils.isNotEmpty(batchId)){
            throw new HlsCusException("合同号：" + contractNumber + "正在确认中，请稍后再确认");
        }
    }


    @Override
    public void checkOtherLock(CshDepositDeductReqHd cshDepositDeductReqHd) throws HlsCusException{
        HlsCusCshTransaction hlsCusCshTransaction = new HlsCusCshTransaction();
        HlsCusConContract hlsCusConContract = new HlsCusConContract();
        try {
            hlsCusCshTransaction =  hlsCusCshTransactionMapper.selectByPrimaryKey(cshDepositDeductReqHd.getTransactionId());
            hlsCusConContract = hlsCusConContractMapper.selectByPrimaryKey(hlsCusCshTransaction.getContractId());
        } catch (Exception e) {
            logger.error("query error",e);
        }
        if(StringUtils.isNotEmpty(hlsCusConContract.getContractNumber()) &&  StringUtils.isNotEmpty(MDC.get(REQUEST_ID)) ){
            CshDepositDeductJobLogs cshDepositDeductJobLogs = new CshDepositDeductJobLogs();
            cshDepositDeductJobLogs.setBatchId(MDC.get(REQUEST_ID));
            cshDepositDeductJobLogs.setContractNumber(hlsCusConContract.getContractNumber());
            String batchId = cshDepositDeductJobLogsMapper.selectExecutingAllBatchId(cshDepositDeductJobLogs);
            if(StringUtils.isNotEmpty(batchId)){
                logger.info("{}下保证金自动抵扣正在运行中，运行编号:{}" ,hlsCusConContract.getContractNumber(), batchId);
                throw new HlsCusException(hlsCusConContract.getContractNumber() + "下保证金自动抵扣正在运行中，运行编号:" + batchId);
            }
        }
    }



}