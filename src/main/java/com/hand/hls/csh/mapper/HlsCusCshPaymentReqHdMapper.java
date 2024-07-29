package com.hand.hls.csh.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqDt;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


public interface HlsCusCshPaymentReqHdMapper extends Mapper<HlsCusCshPaymentReqHd> {
    List<HlsCusCshPaymentReqHd> queryAll(Map<String, Object> var1);

    HlsCusCshPaymentReqHd queryCshPaymentHdById(@Param("payment_req_id") Long var1);
    HlsCusCshPaymentReqHd queryCshPaymentCheckHdById(@Param("payment_req_id") Long var1);
    HlsCusCshPaymentReqHd queryConComCostRate(@Param("payment_req_id") Long var1);

    List<HlsCusCshPaymentReqHd> queryApply(@Param("contractId") Long var1);

    List<HlsCusCshPaymentReqHd> fctPaymentReqHistorySelect(HlsCusCshPaymentReqHd var1);

    List<HlsCusCshPaymentReqHd> searchCshPaymentHomeInfo(HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd);
    List<HlsCusCshPaymentReqHd> queryCshPaymentReqHd(HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd);
    List<HlsCusCshPaymentReqHd> queryForTransfer(HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd);
    List<HlsCusCshPaymentReqHd> queryForPaymentSubmit(HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd);

    List<HlsCusCshPaymentReqHd> queryContractIdByFundPlan(HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd);
    List<HlsCusCshPaymentReqHd>dailyrate(HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd);
    List<HlsCusCshPaymentReqHd>queryEmployeeEmail(HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd);
    List<HlsCusCshPaymentReqHd>queryEmployeeEmailwfl(HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd);
    List<HlsCusCshPaymentReqHd>conContractCshReqDetail(HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd);
    Double queryActualPaymentAmount(@Param("paymentReqId") Long paymentReqId);
    Double queryPaymentAmount(@Param("paymentReqId") Long paymentReqId);
    String queryUserName(@Param("paymentReqId") Long paymentReqId);

    /**
     * 二期功能：零售业务-查询实际支付金额
     * @param paymentReqId
     * @return
     */
    Double queryRetailActualPaymentAmount(@Param("paymentReqId") Long paymentReqId);

    /**
     * 二期功能：零售业务-查询支付金额
     * @param paymentReqId
     * @return
     */
    Double queryRetailPaymentAmount(@Param("paymentReqId") Long paymentReqId);

    /**
     * 二期功能：零售业务付款支付首页查询
     * @param hlsCusCshPaymentReqHd
     * @return
     */
    List<HlsCusCshPaymentReqHd> retailPaymentHomeQuery(HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd);

    List<HlsCusCshPaymentReqHd> retailPaymentQuery(HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd);

    /**
     * 二期功能：零售业务付款支付详细页头信息查询
     * @param hlsCusCshPaymentReqHd
     * @return
     */
    List<HlsCusCshPaymentReqHd> retailPaymentHeadReqDetail(HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd);



    /**
     * 查询合同投放信息
     * @param contractId 合同id
     * @return
     */
    HlsCusCshPaymentReqHd queryContractPaymentInfo(@Param("contractId") Long contractId);
    List<Map> queryTariffCshPaymentReqHd(HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd);

    List<HlsCusCshPaymentReqHd>queryPaymentOther(HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd);




}
