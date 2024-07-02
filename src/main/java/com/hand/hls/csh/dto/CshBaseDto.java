package com.hand.hls.csh.dto;

import lombok.Data;

import java.util.List;

/**
 * @Description：收付管理模块头行保存实体类
 * @Author：liangxian.chen@hand-china.com
 * @Date：2022/11/29 16:27
 * @Version：1.0
 */
@Data
public class CshBaseDto {
    /**
     * 退款
     */
    private HlsCusCshTransactionRefund transactionRefund;

    /**
     * 退款行集合
     */
    private List<CshTransactionRefundLn> transactionRefundLnList;

    /**
     * 付款头
     */
    private HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd;

    /**
     * 付款行
     */
    private List<HlsCusCshPaymentReqLn> hlsCusCshPaymentReqLnList;

    /**
     * 现金事务
     */
    private HlsCusCshTransaction hlsCusCshTransaction;

    /**
     * 核销集合
     */
    private List<HlsCusCshWriteOff> hlsCusCshWriteOffList;

    /**
     * 支付单
     */
    private CshPaymentReqSlip cshPaymentReqSlip;

    /**
     * 支付单行数据
     */
    private List<CshPaymentReqSlipLn> cshPaymentReqSlipLnList;

    /**
     * 核销暂存数据
     */
    private CshWriteOffSlip cshWriteOffSlip;

    /**
     * 核销暂存数据
     */
    private List<CshWriteOffSlipLn> cshWriteOffSlipLnList;

    /**
     * 保证金抵扣头表
     */
    private HlsCusDepositDeductionHd cusDepositDeductionHd;

    /**
     * 保证金抵扣行数据
     */
    private List<HlsCusDepositDeduction> cusDepositDeductionList;

    /**
     * 保存状态
     */
    private String submitStatus;
}
