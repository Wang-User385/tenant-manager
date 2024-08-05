package com.hand.hls.csh.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.csh.dto.CshBaseDto;
import com.hand.hls.csh.dto.HlsCusCshTransactionRefund;
import com.hand.hls.csh.exception.BeyondAmountLimitException;
import com.hand.hls.utils.ResMessageException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2020/4/24
 * @description:
 */
public interface CshTransactionRefundService extends IBaseService<HlsCusCshTransactionRefund> , ProxySelf<CshTransactionRefundService> {
    /**
     * 二期功能：退款申请创建tab页查询
     * @param request
     * @param transactionRefund
     * @param page
     * @param pageSize
     * @return
     */
    List<HlsCusCshTransactionRefund> createRefundQuery(IRequest request, HlsCusCshTransactionRefund transactionRefund, int page, int pageSize);

    /**
     * 二期功能：退款申请维护tab页查询
     * @param iRequest
     * @param transactionRefund
     * @param page
     * @param pageSize
     * @return
     */
    List<HlsCusCshTransactionRefund> modifyHomeQuery(IRequest iRequest, HlsCusCshTransactionRefund transactionRefund, int page, int pageSize);

    /**
     * 二期功能：退款明细页面行信息查询
     * @param iRequest
     * @param transactionRefund
     * @param page
     * @param pageSize
     * @return
     */
    List<HlsCusCshTransactionRefund> refundInfoLnQuery(IRequest iRequest, HlsCusCshTransactionRefund transactionRefund, int page, int pageSize);

    /**
     * 二期功能：退款申请创建页面保存按钮逻辑
     * @param requestCtx
     * @param cshBaseDto
     * @return
     * @throws Exception
     */
    ResponseData createAndUpdate(IRequest requestCtx, CshBaseDto cshBaseDto) throws Exception;;

    /**
     * 二期功能：退款申请创建/维护页面  提交按钮  逻辑
     * @param iRequest
     * @param refundId
     * @return
     * @throws Exception
     */
    ResponseData refundSubmit(IRequest iRequest, Long refundId) throws Exception;

    /**
     * 二期功能：退款申请更新单据状态
     * @param iRequest
     * @param refundId
     * @param refundStatus
     */
    void updateRefundStatus(IRequest iRequest, long refundId, String refundStatus);

    /**
     * 二期功能：待支付清单-退款申请 首页查询
     * @param iRequest
     * @param transactionRefund
     * @param page
     * @param pageSize
     * @return
     */
    List<HlsCusCshTransactionRefund> refundPayHome(IRequest iRequest, HlsCusCshTransactionRefund transactionRefund, int page, int pageSize);

    /**
     * @Title: cshTransactionRefundQuery
     * @Discription: 明细查询
     * @Param: [hlsCusCshTransactionRefund, page, pagesize, request]
     * @Return: java.util.List<com.hand.hls.csh.dto.HlsCusCshTransactionRefund>
     */
    List<HlsCusCshTransactionRefund> cshTransactionRefundQuery(HlsCusCshTransactionRefund hlsCusCshTransactionRefund, int page, int pagesize,String sortName,String sortOrder);
    void refundCshTransactionSubmit (IRequest requestCtx, HttpSession session, List<HlsCusCshTransactionRefund> hlsCusCshTransactionRefund) throws BeyondAmountLimitException, ResMessageException;
    List<HlsCusCshTransactionRefund> saveLn(IRequest iRequest, List<HlsCusCshTransactionRefund> list) throws ResMessageException;
    List<HlsCusCshTransactionRefund> cshPaymentTransactionRefundQuery(HlsCusCshTransactionRefund hlsCusCshTransactionRefund, int page, int pagesize,String sortName,String sortOrder);


    /**
     * 退款申请提交按钮
     * @param iRequest
     * @param refundId
     * @return
     * @throws Exception
     */
    ResponseData refundSubmitNew(IRequest iRequest, Long refundId) throws Exception;
}
