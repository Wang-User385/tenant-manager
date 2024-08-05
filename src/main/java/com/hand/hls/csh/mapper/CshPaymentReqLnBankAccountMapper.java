package com.hand.hls.csh.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.csh.dto.CshPaymentReqLnBankAccount;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CshPaymentReqLnBankAccountMapper extends Mapper<CshPaymentReqLnBankAccount>{


    List<CshPaymentReqLnBankAccount> selectCshPaymentReqLnBankAccount(CshPaymentReqLnBankAccount cshPaymentReqLnBankAccount);
    Double queryActualPaymentAmount(@Param("paymentReqId") Long paymentReqId);

    List<CshPaymentReqLnBankAccount> queryContractCshBank(Long contractId);

    /**
     * 二期功能：零售业务付款支付-核销信息查询
     * @param cshPaymentReqLnBankAccount
     * @return
     */
    List<CshPaymentReqLnBankAccount> selectRetailCshPaymentReqLnBankAccount(CshPaymentReqLnBankAccount cshPaymentReqLnBankAccount);

    /**
     * 二期功能：零售业务付款支付-实际支付信息查询
     * @param cshPaymentReqLnBankAccount
     * @return
     */
    List<CshPaymentReqLnBankAccount> retailPaymentBankAccountDetail(CshPaymentReqLnBankAccount cshPaymentReqLnBankAccount);
    /**
     * 二期功能：零售业务付款支付-实际支付信息查询（线上支付TAB页查询）
     * @param cshPaymentReqLnBankAccount
     * @return
     */
    List<CshPaymentReqLnBankAccount> retailPaymentBankAccountDetailTT(CshPaymentReqLnBankAccount cshPaymentReqLnBankAccount);
    /**
     * 二期功能：零售业务付款支付-实际支付信息查询（线下支付TAB页查询）
     * @param cshPaymentReqLnBankAccount
     * @return
     */
    List<CshPaymentReqLnBankAccount> retailPaymentBankAccountDetailOther(CshPaymentReqLnBankAccount cshPaymentReqLnBankAccount);

    /**
     * 二期功能：付款支付-实际支付信息查询（线上支付TAB页查询）
     * @param cshPaymentReqLnBankAccount
     * @return
     */
    List<CshPaymentReqLnBankAccount> selectCshPaymentReqLnBankAccountTT(CshPaymentReqLnBankAccount cshPaymentReqLnBankAccount);

    /**
     * 二期功能：付款支付-实际支付信息查询（线下支付TAB页查询）
     * @param cshPaymentReqLnBankAccount
     * @return
     */
    List<CshPaymentReqLnBankAccount> selectCshPaymentReqLnBankAccountOther(CshPaymentReqLnBankAccount cshPaymentReqLnBankAccount);

    /**
     * 二期功能：退款申请付款支付-实际支付信息查询（线上支付TAB页查询）
     * @param cshPaymentReqLnBankAccount
     * @return
     */
    List<CshPaymentReqLnBankAccount> refundPaymentBankAccountDetailTT(CshPaymentReqLnBankAccount cshPaymentReqLnBankAccount);
    /**
     * 二期功能：退款申请付款支付-实际支付信息查询（线下支付TAB页查询）
     * @param cshPaymentReqLnBankAccount
     * @return
     */
    List<CshPaymentReqLnBankAccount> refundPaymentBankAccountDetailOther(CshPaymentReqLnBankAccount cshPaymentReqLnBankAccount);

    /**
     * 二期功能：退款申请付款支付-获取退款申请实际支付汇总金额
     * @param refundId
     * @return
     */
    Double queryRefundPaymentAmount(@Param("refundId") Long refundId);

    /**
     * 二期功能：退款申请付款支付-核销信息查询
     * @param cshPaymentReqLnBankAccount
     * @return
     */
    List<CshPaymentReqLnBankAccount>  selectRefundCshPaymentReqLnBankAccount(CshPaymentReqLnBankAccount cshPaymentReqLnBankAccount);


    void updateCshPaymentReqLnBankAccountByLn(CshPaymentReqLnBankAccount cshPaymentReqLnBankAccount);
}