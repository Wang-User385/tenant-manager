package com.hand.hls.csh.service;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.core.annotation.StdWho;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.csh.dto.CshDepositDeductReqHd;
import com.hand.hls.exception.HlsCusException;

import java.util.List;
import java.util.Map;

public interface ICshDepositDeductReqHdService extends IBaseService<CshDepositDeductReqHd>, ProxySelf<ICshDepositDeductReqHdService> {
    int SCALE = 2;
    /**
     * 查询保证金抵扣申请明细 初始化
     *
     * @param request
     * @param cshDepositDeductReqHd
     * @return 保证金抵扣申请明细
     */
    CshDepositDeductReqHd selectCshDepositDeductReqHdInit(IRequest request, CshDepositDeductReqHd cshDepositDeductReqHd);

    /**
     * 查询保证金抵扣申请明细
     *
     * @param request
     * @param cshDepositDeductReqHd
     * @return 保证金抵扣申请明细
     */
    CshDepositDeductReqHd selectCshDepositDeductReqHd(IRequest request, CshDepositDeductReqHd cshDepositDeductReqHd);

    /**
     * 查询保证金抵扣申请列表
     *
     * @param request
     * @param cshDepositDeductReqHd
     * @param page
     * @param pagesize
     * @return 保证金抵扣申请列表
     */
    List<CshDepositDeductReqHd> selectCshDepositDeductReqHdList(IRequest request, CshDepositDeductReqHd cshDepositDeductReqHd, int page, int pagesize);

    /**
     * 保证金抵扣，不开启自治事务，方便回滚
     * @param request 请求信息
     * @param cshDepositDeductReqHd 抵扣头行
     * @param createDeductMethod 抵扣来源
     * @return 抵扣头行
     * @throws HlsCusException 异常
     */
     CshDepositDeductReqHd saveDepositDeductReqWithoutNewTransaction(IRequest request, @StdWho CshDepositDeductReqHd cshDepositDeductReqHd,String createDeductMethod) throws HlsCusException;

    /**
     * 保存保证金抵扣申请
     *
     * @param request
     * @param cshDepositDeductReqHd
     * @param createDeductMethod 保证金抵扣申请创建来源
     * @return
     */
    CshDepositDeductReqHd saveDepositDeductReq(IRequest request, @StdWho CshDepositDeductReqHd cshDepositDeductReqHd,String createDeductMethod) throws HlsCusException;

    /**
     * 冻结现金流金额
     */
    void blockAmount(IRequest iRequest, Long deductReqId) throws HlsCusException;

    /**
     * 冻结现金流金额
     *      新事务
     */
    void blockAmountWithNewTransaction(IRequest iRequest, Long deductReqId) throws HlsCusException;

    /**
     * 提交保证金抵扣申请
     *
     * @param request
     * @param cshDepositDeductReqHd
     * @return
     */
    CshDepositDeductReqHd submitDepositDeductReq(IRequest request, @StdWho CshDepositDeductReqHd cshDepositDeductReqHd) throws HlsCusException;

    /**
     * 删除新建的保证金抵扣数据
     * @param requestContext 请求信息
     * @param param 头ID
     * @return 删除结果
     * @throws HlsCusException 异常
     */
    boolean deleteDepositDeductReq(IRequest requestContext, JSONObject param) throws HlsCusException;

    /**
     * 更新保证金抵扣状态
     */
    void updateReqStatus(IRequest iRequest,Long id, String status);

    /**
     * 抵扣前
     */
    void beforeDeduct(IRequest iRequest, CshDepositDeductReqHd cshDepositDeductReqHd, String status);

    void releaseAmount(IRequest iRequest, Long reqHdId);

    /**
     * 更新保证金抵扣状态
     */
    void updateDeductReqStatus(IRequest iRequest,Long id, String reqStatus, String deductStatus, String message);

    /**
     * 更新保证金抵扣sap状态
     */
    void updateDeductStatus(Long id, String status, String message);

    /**
     * 保证金抵扣申请审批通过执行抵扣处理
     * @param request 请求信息
     * @param cshDepositDeductReqHd 抵扣头行信息
     * @param changeType 发起来源
     * @return 成功标志
     */
    String execDepositDeductReqApproved(IRequest request, CshDepositDeductReqHd cshDepositDeductReqHd,String changeType);

    /**
     * 核销保证金
     */
    void writeOffDeposit(IRequest iRequest, CshDepositDeductReqHd cshDepositDeductReqHd) throws Exception;

    /**
     * 保证金抵扣手动释放冻结金额
     * @param iRequest 请求信息
     * @param cshDepositDeductReqHd 保证金抵扣头行
     * @return 返回信息
     * @throws HlsCusException 异常
     */
    ResponseData back(IRequest iRequest, CshDepositDeductReqHd cshDepositDeductReqHd) throws HlsCusException;


    /**
     * 获取发起工作流参数
     * @param request 请求信息
     * @param cshDepositDeductReqHd 保证金抵扣头行
     * @return 工作流参数
     * @throws HlsCusException 异常
     */
    Map<String, Object> getWflObjectMap(IRequest request, @StdWho CshDepositDeductReqHd cshDepositDeductReqHd) throws HlsCusException;


}
