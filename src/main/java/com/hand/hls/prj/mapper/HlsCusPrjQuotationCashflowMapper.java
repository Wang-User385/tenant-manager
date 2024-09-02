package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotationCashflow;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface HlsCusPrjQuotationCashflowMapper extends Mapper<HlsCusPrjQuotationCashflow> {
    List<HlsCusPrjQuotationCashflow> queryPrjQuotationCashflowByQuotationId(HlsCusPrjQuotationCashflow prjQuotationCashflow);

    void deleteRentByQuotationId(HlsCusPrjQuotation quotation);

    void deleteByQuotationId(HlsCusPrjQuotation quotation);

    List<HlsCusPrjQuotationCashflow> queryPrjQuotationCashflowByProjectId(HlsCusPrjQuotationCashflow prjQuotationCashflow);

    List<HlsCusPrjQuotationCashflow> queryCshFineInfo(HlsCusPrjQuotationCashflow prjQuotationCashflow);

    List<HlsCusPrjQuotationCashflow> queryCshFineInfo2(HlsCusPrjQuotationCashflow prjQuotationCashflow);

    List<HlsCusPrjQuotationCashflow> queryFineCshFlowInfo(HlsCusPrjQuotationCashflow prjQuotationCashflow);

    List<HlsCusPrjQuotationCashflow> queryPrjQuotationOutCashflowByProjectId(HlsCusPrjQuotationCashflow prjQuotationCashflow);

    List<HlsCusPrjQuotationCashflow> prjQueryCashFlow(HlsCusPrjQuotationCashflow prjQuotationCashflow);

    List<HlsCusPrjQuotationCashflow> capQueryConCashFlow(HlsCusPrjQuotationCashflow prjQuotationCashflow);

    List<HlsCusPrjQuotationCashflow> capQueryCtCashFlow(HlsCusPrjQuotationCashflow prjQuotationCashflow);

    HlsCusPrjQuotationCashflow selectPrjQuotationCashflowFirstTimeRental(HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow);
    HlsCusPrjQuotationCashflow quotationCheck(HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow);

    List<HlsCusPrjQuotationCashflow> selectPrjQuotationCashflowAfterFirstTimeRental(HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow);

    List<HlsCusPrjQuotationCashflow> selectMaxDueDateByQuotationId(HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow);

    List<HlsCusPrjQuotationCashflow> selectIrrQuotation(HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow);
    List<HlsCusPrjQuotationCashflow> selectIrrQuotationJC(HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow);

    /**
     * 考虑手续费现金流
     * @param hlsCusPrjQuotationCashflow
     * @return
     */
    List<HlsCusPrjQuotationCashflow> selectIrrQuotationJCForApp(HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow);

    List<HlsCusPrjQuotationCashflow> selectPaynoteIrrQuotation(HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow);

    List<HlsCusPrjQuotationCashflow> prjQuotationCashflowExport(HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow);

    HlsCusPrjQuotationCashflow selectPrjQuotationCashflowMaxTimeAndDate(HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow);

    List<Map> selectProjectOtherCashflow();

    List<Map> selectQuotationCashflow();

    List<HlsCusPrjQuotationCashflow> queryQuotationCashFlowById(HlsCusPrjQuotation hlsCusPrjQuotation);

    int deleteQuotationCashFlowById(HlsCusPrjQuotation hlsCusPrjQuotation);

    int deleteResidualCashflow(HlsCusPrjQuotation hlsCusPrjQuotation);

    List<Map> selectCalcQuotationCashflowInfoQuery(Map map);

    int deleteDepositReturnCashflow(HlsCusPrjQuotation hlsCusPrjQuotation);

    int deleteRiskReturnCashflow(HlsCusPrjQuotation quotation);

    List<Map> selectCalcQuotationPaymentCashflowInfoQuery(Map map);
    List<Map> selectCalcPaynoteCashflow(Map map);
    List<Map> selectConFinancialHeadQuery(Map map);

    List<HlsCusPrjQuotationCashflow> selectPaynoteLeaseChageCashflow(HlsCusPrjQuotationCashflow cashflow);

    List<HlsCusPrjQuotationCashflow> selectLeaseItemPaynoteCashflow(HlsCusPrjQuotationCashflow cashflow);

    void updatePrjQuotationCashflowDuedate(HlsCusPrjQuotationCashflow cashflow);

    List<HlsCusPrjQuotationCashflow> queryPrjCashflowByDate(HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow);

    List<HlsCusPrjQuotationCashflow> queryPrjCalcBaseCashflow(HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow);

    List<HlsCusPrjQuotationCashflow> queryCashByQuotation(HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow);

    List<HlsCusPrjQuotationCashflow> selectQuotationFeeCashflow(HlsCusPrjQuotationCashflow cashflow);
    void updateQuotationAmortizationMethod(HlsCusConContractCashflow cashflow);

    void updatePrjQuotationCashflowReturnDuedate(HlsCusPrjQuotationCashflow cashflow);

    /**
     * 查询现金流应收日根据期次
     * @param times 期次
     * @return
     */
    HlsCusPrjQuotationCashflow queryDueDateByTimes(@Param("quotationId") Long quotationId, @Param("times") Long times);

    HlsCusPrjQuotationCashflow queryBeforeEtDateCashflow(@Param("quotationId") Long quotationId, @Param("dueDate") Date dueDate);
    HlsCusPrjQuotationCashflow queryAfterChangeDateCashflow(@Param("quotationId") Long quotationId, @Param("dueDate") Date dueDate);
    int deleteChengeTimeAfterCashflow(@Param("quotationId") Long quotationId, @Param("times") Long times);


    List<Map> selectQuotationCompareInfo(@Param("quotationId") Long quotationId);
    List<HlsCusPrjQuotationCashflow> queryPrjQuotationCashflowDetailByProjectId1(@Param("projectId") Long projectId);



    /**
     * 代扣管理查询
     */
    List<HlsCusCshPaymentReqHd> queryWithholding(HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd);

    List<HlsCusPrjQuotationCashflow> selectCashflowForXirr(@Param("quotationId") Long quotationId);

    /**
     * 代扣状态查询
     */
    Map selectWithholdingStateByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 是否暂停代扣状态更新
     */
    void updateStopWithholdFlagByOrderNoAndTermNo(@Param("orderNo") String orderNo, @Param("stopWithholdFlag") String stopWithholdFlag, @Param("termNo") Integer termNo);

    /***
     * 保理现金流信息
     * @param hlsCusPrjQuotationCashflow
     * @return
     */

    List<HlsCusPrjQuotationCashflow> findFactoringInfo(HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow);

    /***
     * 保理项目审批现金流信息
     * @param hlsCusPrjQuotationCashflow
     * @return
     */
    List<HlsCusPrjQuotationCashflow> findFactoringApprovalInfo(HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow);

}