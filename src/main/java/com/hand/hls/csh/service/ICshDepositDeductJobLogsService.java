package com.hand.hls.csh.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.csh.dto.CshDepositDeductJobLogs;
import com.hand.hls.csh.dto.CshDepositDeductReqHd;
import com.hand.hls.exception.HlsCusException;

/**
 * 保证金抵扣日志插入接口
 */
public interface ICshDepositDeductJobLogsService extends IBaseService<CshDepositDeductJobLogs>, ProxySelf<ICshDepositDeductJobLogsService>{

    /**
     * 异常日志
     * @param throwable 异常
     * @param errorMsg 附加的报错信息，可不填
     */
    void error(Throwable throwable,String errorMsg);

    /**
     * 异常日志
     * @param throwable 异常
     * @param errorMsg  附加的报错信息，可不填
     * @param contractNumber 合同号
     * @param reqNumber      抵扣申请编号
     * @param transactionNum 现金事务编号
     */
    void error(Throwable throwable,String errorMsg,String contractNumber,String reqNumber,String transactionNum);

    /**
     * 成功日志
     * @param successMsg 成功信息
     */
    void success(String successMsg);


    /**
     * 成功日志
     * @param successMsg  成功信息
     * @param contractNumber 合同号
     * @param reqNumber      抵扣申请编号
     * @param transactionNum 现金事务编号
     */
    void success(String successMsg,String contractNumber,String reqNumber,String transactionNum);

    /**
     * 失败日志
     * @param failMsg 错误信息
     */
    void fail(String failMsg);

    /**
     * 失败日志
     * @param failMsg  错误信息
     * @param contractNumber 合同号
     * @param reqNumber      抵扣申请编号
     * @param transactionNum 现金事务编号
     */
    void fail(String failMsg,String contractNumber,String reqNumber,String transactionNum);


    /**
     * 在日志表中插入一条执行中的标志,这个是跑批使用的，完全不允许并发，不能有两个同时正在运行的跑批
     * @throws Exception 当存在不是本批次且在执行中的数据时，抛出异常
     */
    void lock() throws Exception;

    /**
     * 和上面的不同点是这个锁按照合同维度，锁汽车租赁确认的单据，同一个合同不能同时运行不同合同的可以
     * @param contractNumber 合同号
     * @throws Exception 当存在不是本批次且在执行中的数据时，抛出异常
     */
    void lockDeductConfirm(String contractNumber) throws Exception;

    /**
     * 将本次运行中的标志更新为SUCCESS，释放
     * @return 更新的条数
     */
    int unLock();

    /**
     * 更新执行中的单据的具体合同号
     * @param contractNumber 执行中的合同号
     * @return 更新条数
     */
    int updateExecuting(String contractNumber);

    /**
     * 设置抵扣来源
     * @param deductSource 抵扣来源
     */
    void setDeductSource(String deductSource);

    /**
     * 设置请求信息
     * @param iRequest 请求信息
     */
    public void setIRequest(IRequest iRequest);

    /**
     * 开启自治事务插入日志
     * @param cshDepositDeductJobLogs 要插入的数据
     */
    void insertLogWithNewTransaction(CshDepositDeductJobLogs cshDepositDeductJobLogs);


    /**
     * 查询当前合同有没有在运行中
     * @param cshDepositDeductReqHd
     * @throws HlsCusException
     */
    void checkOtherLock(CshDepositDeductReqHd cshDepositDeductReqHd) throws HlsCusException;

}