package com.hand.hls.ast.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.ast.dto.AssetClassLine;
import com.hand.hls.cont.dto.HlsCusConContract;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AssetClassLineMapper extends Mapper<AssetClassLine> {

    List<AssetClassLine> queryOverdueContractInfo();

    List<AssetClassLine> queryLineInfoByIdAndReviewLevel(AssetClassLine assetClassLine);

    List<AssetClassLine> queryLineInfoByIdAndInitLevel(AssetClassLine assetClassLine);

    List<AssetClassLine> queryOverdueContractInfoByContractIds(@Param("contracts") List<HlsCusConContract> contracts, @Param("manufacturerId") Long manufacturerId);

    /**
     * 根据合同编号和class_head_id查找资产分类行表数据

     */
    AssetClassLine queryByContractNumberAndClassHeadId(@Param("contractNumber") String contractNumber,@Param("classHeadId") Long classHeadId);

    void updateReviewLevel(AssetClassLine classLine);

    void updateApproveReviewLevel(AssetClassLine classLine);

    /**
     * 获取分类明细
     * @param assetClassLine 参数
     * @return List<AssetClassLine>
     */
    List<AssetClassLine> queryClassContractDetail(AssetClassLine assetClassLine);

    /**
     * 审批流程中获取分类明细
     * @param assetClassLine
     * @return List<AssetClassLine>
     */
    List<AssetClassLine> queryClassContractDetailApproval(AssetClassLine assetClassLine);
}