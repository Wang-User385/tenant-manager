package com.hand.hls.csh.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.csh.dto.CshBaseDto;
import com.hand.hls.csh.dto.CshPaymentReqLnBankAccount;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqLn;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.utils.ResMessageException;

import java.util.List;


public interface CshPaymentReqLnService extends IBaseService<HlsCusCshPaymentReqLn>, ProxySelf<CshPaymentReqLnService> {

    List<HlsCusCshPaymentReqLn> selectCshPaymentReqLnDetailByLnID(HlsCusCshPaymentReqLn cshPaymentReqLn);

    List<HlsCusCshPaymentReqLn> selectCshPaymentReqLnDetailByLnID(List<String> ln_id);

    List<HlsCusCshPaymentReqLn> queryByHnId(HlsCusCshPaymentReqLn cshPaymentReqLn);

    List<HlsCusCshPaymentReqLn> queryForLoanRequest(HlsCusCshPaymentReqLn cshPaymentReqLn,int page,int pageSize);

    List<CshPaymentReqLnBankAccount> queryAccount(HlsCusCshPaymentReqLn cshPaymentReqLn);

    /**
     * 二期功能：付款申请明细查询
     * @param iRequest
     * @param hlsCusCshPaymentReqLn
     * @param pagenum
     * @param pagesize
     * @return
     */
    List<HlsCusCshPaymentReqLn> queryCshPaymentReqLn(IRequest iRequest, HlsCusCshPaymentReqLn hlsCusCshPaymentReqLn, int pagenum, int pagesize);

    /**
     * 二期功能：付款申请创建保存提交
     * @param iRequest
     * @param cshBaseDto
     * @return
     * @throws ResMessageException
     * @throws HlsCusException
     */
    ResponseData cshPaymentReqCreateAndSubmit(IRequest iRequest, CshBaseDto cshBaseDto) throws ResMessageException, HlsCusException;

    /**
     * 二期功能：付款申请撤回
     * @param iRequest
     * @param paymentReqId
     * @throws HlsCusException
     */
    void paymentBack(IRequest iRequest, Long paymentReqId) throws HlsCusException;

    /**
     * 二期功能：付款申请行删除
     * @param hlsCusCshPaymentReqLnList
     */
    void batchDeleteReqLn(List<HlsCusCshPaymentReqLn> hlsCusCshPaymentReqLnList);

    /**
     * 二期功能：付款申请--合同取消
     * @param iRequest
     * @param hlsCusConContract
     * @throws HlsCusException
     */
    void cancelContract(IRequest iRequest, HlsCusConContract hlsCusConContract) throws HlsCusException;

    /**
     * 更新付款状态
     * @param iRequest 请求封装对象
     * @param paymentReqId 主键
     * @param paymentStatus 状态
     */
    void updatePaymentStatus(IRequest iRequest, long paymentReqId, String paymentStatus);

    /**
     * 设置权限字段
     */
    String getAuthorityRuleString(IRequest iRequest);

}
