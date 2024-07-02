package com.hand.hls.csh.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.csh.dto.HlsCusCshTransactionRefund;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2020/4/24
 * @description:
 */
public interface HlsCusCshTransactionRefundMapper extends CshTransactionRefundMapper<HlsCusCshTransactionRefund> {
    /**
     * 二期功能：退款申请创建tab页查询
     * @param transactionRefund
     * @return
     */
    List<HlsCusCshTransactionRefund> createRefundQuery(HlsCusCshTransactionRefund transactionRefund);

    /**
     * 二期功能：退款申请维护tab页查询
     * @param transactionRefund
     * @return
     */
    List<HlsCusCshTransactionRefund> modifyHomeQuery(HlsCusCshTransactionRefund transactionRefund);
    /**
     * 二期功能：退款明细页面行信息查询
     * @param transactionRefund
     * @return
     */
    List<HlsCusCshTransactionRefund> refundInfoLnQuery(HlsCusCshTransactionRefund transactionRefund);

    /**
     * 二期功能：待支付清单-退款申请 首页查询
     * @param transactionRefund
     * @return
     */
    List<HlsCusCshTransactionRefund> refundPayHome(HlsCusCshTransactionRefund transactionRefund);


    /**
     * 二期功能：待支付清单-退款申请 支付页面头信息查询
     * @param transactionRefund
     * @return
     */
    List<HlsCusCshTransactionRefund> refundPayHeadDetail(HlsCusCshTransactionRefund transactionRefund);

    /**
     * 二期功能：当前退款申请付款明细现金事务ID
     * @param refundId
     * @return
     */
    List<Map> queryRefundTransactionId(@Param("refundId") String refundId);

    /**
     * @Title: cshTransactionRefundQuery
     * @Discription: 收款退款明细查询
     * @Param: [cshTransactionRefund]
     * @Return: java.util.List<com.hand.hls.csh.dto.HlsCusCshTransactionRefund>
     */
    List<HlsCusCshTransactionRefund> cshTransactionRefundQuery(HlsCusCshTransactionRefund cshTransactionRefund);

    List<HlsCusCshTransactionRefund> cshPaymentTransactionRefundQuery(HlsCusCshTransactionRefund cshTransactionRefund);

}
