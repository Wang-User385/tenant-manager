package com.hand.hls.partner.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.partner.dto.*;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface LeasingNoticeMapper extends Mapper<LeasingNotice> {

    /**
     * 查询审核结果通知
     *
     * @param projectId
     * @return
     */
    OrderAuditResultDto queryPerRiskResult(@Param("projectId") Long projectId);

    /**
     * 放款审核结果通知
     *
     * @param projectId
     * @return
     */
    OrderAuditResultDto queryLoanAuditResult(@Param("projectId") Long projectId);


    /**
     * 抵押材料审核结果通知
     *
     * @param contractId
     * @return
     */
    OrderAuditResultDto queryMortgageMaterialAuditResult(@Param("contractId") Long contractId);


    /**
     * 易靓放款结果通知
     *
     * @param projectId
     * @return
     */
    OrderLoanResultDto queryOrderLoanResult(@Param("projectId") Long projectId);

    /**
     * 关单结果通知
     */
    List<String> queryOrderClosedList();


    /**
     * 易靓需代偿通知
     *
     * @param
     * @return
     */
    List<AssetNeedSubstituteDto> queryAssetNeedSubstitute(AssetNeedSubstituteDto assetNeedSubstituteDto);

    /**
     * 易靓需回购通知
     *
     * @param
     * @return
     */
    List<AssetNeedBuybackDto> queryAssetNeedBuybackDto(AssetNeedBuybackDto assetNeedBuybackDto);

    /**
     * 易靓代扣签约结果通知
     *
     * @param projectId
     * @return
     */
    Map<String, Object> queryWithholdContractResult(@Param("projectId") Long projectId);

    /**
     *易靓期次代扣结果通知
     * @param projectId
     * @return
     */
    Map<String,Object> queryRepayPlanRepaidNotify(@Param("projectId") Long projectId);


    /**
     * 易靓期次代扣结果通知:具体期次查询
     *
     * @param projectId
     * @return
     */
    List<RepayPlanRepaidNotifyDto> queryTerm(@Param("projectId") Long projectId);

    /**
     * 信息通知记录
     * @return
     */
  List<LeasingNotice> qureyLeasingNotice();

    /**
     * 通过projectId查询出订单供应商是汉得测试还是其他供应商
     * @return
     */
  String queryBpCodeByProjectId(@Param("projectId") Long projectId);

}
