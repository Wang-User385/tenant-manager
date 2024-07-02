package com.hand.hls.csh.mapper;

import com.hand.hls.csh.dto.HlsCusCshPaymentReqLn;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface HlsCusCshPaymentReqLnMapper extends CshPaymentReqLnMapper<HlsCusCshPaymentReqLn> {

    List<HlsCusCshPaymentReqLn> selectCshPaymentReqLnDetailByLnID(List<String> ln_id);

    void updatePaymentFlag(HlsCusCshPaymentReqLn cshPaymentReqLn);

    void updateAmountPaid(HlsCusCshPaymentReqLn cshPaymentReqLn);

    List<HlsCusCshPaymentReqLn> queryByHnId(HlsCusCshPaymentReqLn cshPaymentReqLn);


    List<HlsCusCshPaymentReqLn> queryForLoanRequest(HlsCusCshPaymentReqLn cshPaymentReqLn);

    void updateSourceDocLineIdByReqPaymentId(@Param("paymentReqId") Long paymentReqId, @Param("cashflowId") Long cashflowId);

    Double selectCshPaymentReqLnAmountPaid(HlsCusCshPaymentReqLn cshPaymentReqLn);

    List<HlsCusCshPaymentReqLn> selectCshPaymentReqLnWithStartEndDate(HlsCusCshPaymentReqLn cshPaymentReqLn);


    void deleteByReqId(HlsCusCshPaymentReqLn  hlsCusCshPaymentReqLn);

    List<HlsCusCshPaymentReqLn> selectPaymentLnNew(HlsCusCshPaymentReqLn hlsCusCshPaymentReqLn);

    Integer selectApprovingPurchaseContractCount(Long purchaseContractId);

    /**
     * 二期功能：付款申请明细查询
     * @param hlsCusCshPaymentReqLn
     * @return
     */
    List<HlsCusCshPaymentReqLn> queryCshPaymentReqLn2(HlsCusCshPaymentReqLn hlsCusCshPaymentReqLn);

    /**
     * 二期功能：零售业务-付款支付明细行查询
     * @param hlsCusCshPaymentReqLn
     * @return
     */
    List<HlsCusCshPaymentReqLn> retailPaymentLineReqDetail(HlsCusCshPaymentReqLn hlsCusCshPaymentReqLn);


}
