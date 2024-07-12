package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.credit.dto.QueryPrjQuotationDTO;
import com.hand.hls.fnd.dto.CalcPrice;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectParam;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import org.apache.ibatis.annotations.Param;
import uncertain.composite.CompositeMap;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface HlsCusPrjQuotationMapper extends Mapper<HlsCusPrjQuotation> {
    List<HlsCusPrjQuotation> prjQuotationDetailQuery(HlsCusPrjQuotation prjQuotation);

    List<HlsCusPrjQuotation> getPrjProjectQuotationSheets(HlsCusPrjQuotation prjQuotation);

    List<HlsCusPrjQuotation> queryPaymentTableInfo(HlsCusPrjQuotation prjQuotation);

    List<HlsCusPrjQuotation> queryPaymentTableInfoLov(HlsCusPrjQuotation prjQuotation);

    List<HlsCusPrjQuotation> queryCshFineInfo(HlsCusPrjQuotation prjQuotation);

    List<HlsCusPrjQuotation> queryPaymentTableInfoConfirm(HlsCusPrjQuotation prjQuotation);

    List<HlsCusPrjQuotation> queryPrjQuotationInfo(HlsCusPrjQuotation prjQuotation);
    /**获取当前最新报价信息*/
    List<HlsCusPrjQuotation> queryNowPrjQuotationInfo(HlsCusPrjQuotation prjQuotation);
    List<HlsCusPrjQuotation> queryPrjQuotationInfo1(HlsCusPrjQuotation prjQuotation);
    List<HlsCusPrjQuotation> queryPrjQuotationHistory(HlsCusPrjQuotation prjQuotation);

    List<HlsCusPrjQuotation> selectLeaseContractLeaseItemAmount(String contractId);

    List<HlsCusPrjQuotation> selectLeaseProjectLeaseItemAmount(String projectId);

    List<HlsCusPrjQuotation> selectLeaseChanceLeaseItemAmount(@Param("chanceId") String chanceId);

    List<HlsCusPrjQuotation> selectFctContractLeaseItemAmount(String contractId);

    List<HlsCusPrjQuotation> selectFctProjectLeaseItemAmount(String projectId);

    List<HlsCusPrjQuotation> queryQuotationInfo(HlsCusPrjQuotation prjQuotation);

    List<HlsCusPrjQuotation> queryQuotationInfoByQuotationId(HlsCusPrjQuotation quotation);

    List<HlsCusPrjQuotation>  queryQuotationInfoByQuotationIdMarketing(HlsCusPrjQuotation quotation);
    List<HlsCusPrjQuotation>  queryQuotationInfoByQuotationIdLoan(HlsCusPrjQuotation quotation);

    List<HlsCusPrjQuotation> queryQuotationInfoList(HlsCusPrjQuotation prjQuotation);

    List<HlsCusPrjQuotation> queryConQuotationInfo(HlsCusPrjQuotation prjQuotation);

    List<Map> selectQuotatonInfo();

    List<HlsCusPrjQuotation> queryConQuotationDelayRent(HlsCusPrjQuotation prjQuotation);

    HlsCusPrjQuotation selectQuotationByCalcPrice(CalcPrice calcPrice);

    List<HlsCusPrjQuotation> selectQuotationByProjectId(HlsCusPrjProject hlsCusPrjProject);

    List<HlsCusPrjQuotation> selectAllQuotationByProjectId(HlsCusPrjProject hlsCusPrjProject);

    List<HlsCusPrjQuotation> queryConQuotationPrjNotice(HlsCusPrjProject hlsCusPrjProject);

    List<HlsCusPrjQuotation> queryConQuotationNoticeDetail(HlsCusPrjQuotation hlsCusPrjQuotation);

    HlsCusPrjQuotation queryQuotationByConId(HlsCusPrjQuotation hlsCusPrjQuotation);

    List<Map> selectLprBaseRateByDate(HlsCusPrjQuotation hlsCusPrjQuotation);

    List<HlsCusPrjQuotation> selectQuotationFeeSum(HlsCusPrjQuotation quotation);

    List<Map> selectQuotationChangeInfo(HlsCusPrjQuotation quotation);

    List<HlsCusPrjQuotation> queryQuotationRateReq(HlsCusPrjQuotation quotation);
    List<HlsCusPrjQuotation> queryQuotationRateReqNew(HlsCusPrjQuotation quotation);
    List<HlsCusPrjQuotation> queryQuotationCalcReq(HlsCusPrjQuotation quotation);
    List<HlsCusPrjQuotation> queryQuotationCalcReqNew(HlsCusPrjQuotation quotation);
    List<HlsCusPrjQuotation> queryQuotationCalcReqWfl(HlsCusPrjQuotation quotation);
    List<HlsCusPrjQuotation> queryFloatingInterest(HlsCusPrjQuotation quotation);
    List<HlsCusPrjQuotation> queryTotalFloatingInterest(HlsCusPrjQuotation quotation);

    List<HlsCusPrjQuotation> queryQuotationCalcReqJob(HlsCusPrjQuotation quotation);
    List<HlsCusConContract> queryContractRateReq();


    List<HlsCusPrjQuotation> queryRateReqCalc(HlsCusPrjQuotation quotation);

    List<HlsCusPrjQuotation> selectHistoryQuotationList(HlsCusPrjQuotation quotation);

    List<HlsCusPrjQuotation> queryRefProjectQuotation(HlsCusPrjQuotation quotation);
    List<HlsCusPrjQuotation>  queryBaseRateNowReq(HlsCusPrjQuotation quotation);

    List<CompositeMap> queryBaseRateNow(CompositeMap var1, String var2);

    /**
     * 查询报价信息-投放计划制作
     * @param prjQuotation
     * @return
     */
    List<HlsCusPrjQuotation> queryPrjQuotationInfoForContractPlan(HlsCusPrjQuotation prjQuotation);

    /**
     * 查询报价信息-投放计划制作支付表编号
     * @return
     */
    String queryPrjQuotationPaymentNumber(@Param("projectId") Long projectId);

    List<Map> queryPrjQuotationAuditInfo(HlsCusPrjProjectParam prjQuotation);

    List<HlsCusPrjQuotation> selectQuotationInfo(HlsCusPrjQuotation quotation);

    Map selectExportVar(HlsCusPrjQuotation hlsCusPrjQuotation);

    /*
     * 查询租金/服务费是否返利描述
     */
    String queryYNFlag(@Param("valueCode") String valueCode);

    QueryPrjQuotationDTO getQueryPrjQuotationDTOByProjectId(@Param("projectId") Long projectId);
}