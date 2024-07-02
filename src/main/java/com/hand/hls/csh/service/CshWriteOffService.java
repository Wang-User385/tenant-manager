package com.hand.hls.csh.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.calc.exception.ChangeLimitException;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.csh.dto.*;
import com.hand.hls.csh.exception.BeyondAmountLimitException;
import com.hand.hls.csh.exception.WriteOffTypeNullException;
import com.hand.hls.utils.ResMessageException;
import com.hand.hls.exception.HlsCusException;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface CshWriteOffService extends IBaseService<HlsCusCshWriteOff>, ProxySelf<CshWriteOffService> {

    void paymentWriteOffReversed(IRequest iRequest, HlsCusCshWriteOff cshWriteOff, HttpSession session) throws ChangeLimitException;

    List<Map> selectAllWriteOff(Map map, HttpSession session, IRequest iRequest);

    /**
     * @param iRequest
     * @param cshWriteOffs
     * @param session
     * @throws BeyondAmountLimitException
     * @throws IllegalArgumentException
     * @throws WriteOffTypeNullException
     * @throws ChangeLimitException
     */
    void updateWriteOff(IRequest iRequest, List<HlsCusCshWriteOff> cshWriteOffs, HttpSession session)
            throws BeyondAmountLimitException, IllegalArgumentException, WriteOffTypeNullException, ChangeLimitException, ResMessageException;

    void updateWriteOff(IRequest iRequest, List<HlsCusCshWriteOff> cshWriteOffs, Long companyId)
            throws BeyondAmountLimitException, IllegalArgumentException, WriteOffTypeNullException, ChangeLimitException, ResMessageException;

    /**
     * @param cshWriteOff
     * @return
     */
    List<HlsCusCshWriteOff> selectAllCancelAfterVerificationDetail(HlsCusCshWriteOff cshWriteOff);

    //处理抵扣
    void paymentDeduct(IRequest iRequest, HlsCusCshPaymentReqHd cshPaymentReqHd);

    void transactionWriteOffDeduct(IRequest iRequest, List<HlsCusCshWriteOff> cshTransactions) throws Exception;

    List<HlsCusCshWriteOff> queryWriteOff (IRequest requestCtx, HlsCusCshWriteOff hlsCusCshWriteOff,int pagenum,int pagesize,String sortName,String sortOrder);

    public void updateContractReceivedStatus(HlsCusConContractCashflow conCashflow);

    void writeOff(IRequest iRequest, List<HlsCusCshWriteOff> cshWriteOffs, HttpSession session)
            throws BeyondAmountLimitException, IllegalArgumentException, WriteOffTypeNullException, ChangeLimitException, ResMessageException;

    HlsCusCshWriteOff getReceiptCredit(HlsCusCshWriteOff cshWriteOff);

    HlsCusCshTransaction updateCshTrxAfterWriteoff(HlsCusCshWriteOff cshWriteOff) throws BeyondAmountLimitException;

    HlsCusConContractCashflow updateConCashflowAfterWriteOff(HlsCusCshWriteOff cshWriteOff);

    HlsCusCshTransaction getCshTransaction(IRequest iRequest, HlsCusCshWriteOff cshWriteOff, String writeOffType);

    void writeOffReversed(IRequest iRequest, List<HlsCusCshWriteOff> cshWriteOffs,HttpSession session) throws Exception;

    void allocationConfirm(IRequest iRequest ,List<HlsCusCshTransaction> cshTransactionList,HttpSession session) throws Exception;
    void allocationSave(IRequest iRequest ,List<HlsCusCshTransaction> cshTransactionList,HttpSession session) throws Exception;

    void payment(IRequest iRequest, HlsCusCshPaymentTran hlsCusCshPaymentTran, HttpSession session) throws Exception;

    /**
     * 二期功能：退款申请支付
     * @param iRequest
     * @param hlsCusCshPaymentTran
     * @param session
     * @throws Exception
     */
    void refundPayment(IRequest iRequest, HlsCusCshPaymentTran hlsCusCshPaymentTran, HttpSession session) throws Exception;
    /**
     * 核销发送sap接口
     * @param iRequest
     * @param cshWriteOffs
     * @param session
     * @param insertFlag 是否插入承租人还款信息表，只有手动核销的要插入
     * @throws Exception
     */
    void cshWriteOffSendSap(IRequest iRequest, List<HlsCusCshWriteOff> cshWriteOffs, HttpSession session,boolean insertFlag) throws Exception;
    /**
     * 核销发送前保存
     */
    CshWriteOffSlip writeOffSendBefore(IRequest iRequest, HlsCusCshTransaction hlsCusCshTransaction, List<HlsCusCshWriteOff> cshWriteOffs) throws BeyondAmountLimitException;
    /**
     * 认领操作冻结金额
     */
    void blockAmountForWriteOffSLip(IRequest iRequest, Long transactionId, List<HlsCusCshWriteOff> cshWriteOffs) throws BeyondAmountLimitException;

    /**
     * 释放额度
     */
    void releaseCredit(IRequest iRequest, List<HlsCusCshWriteOff> hlsCusCshWriteOffList) throws HlsCusException;

    /**
     * 释放额度
     */
    void releaseCredit(IRequest iRequest, Long cfItem, Long contractId, Double writeOffPrincipal);
}
