package com.hand.hls.abs.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.abs.dto.HlsCusAbsAssetsPackage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HlsCusAbsAssetsPackageMapper extends Mapper<HlsCusAbsAssetsPackage> {


    /**
     * 资产包查询
     * @param absAssetsPackage
     * @return
     */
    List<HlsCusAbsAssetsPackage> selectAssetsPackage(HlsCusAbsAssetsPackage absAssetsPackage);


    /**
     * 资产包新增合同查询
     * @param absAssetsPackage
     * @return
     */
    List<HlsCusAbsAssetsPackage> selectPackageContract(HlsCusAbsAssetsPackage absAssetsPackage);


    /**
     * 资产包现金流查询
     * @param absAssetsPackage
     * @return
     */
    List<HlsCusAbsAssetsPackage> selectPackageConCashFlow(HlsCusAbsAssetsPackage absAssetsPackage);


    /**
     * 资产包选择期数后 现金流数据
     * @param absAssetsPackage
     * @return
     */
    List<HlsCusAbsAssetsPackage> selectBetweenTimesCashflow(HlsCusAbsAssetsPackage absAssetsPackage);


    /**
     * 占用单据
     * @param packId
     * @return
     */
    List<HlsCusAbsAssetsPackage> selectPackageOccupyData(@Param("packId") Long packId);


    /**
     * 日期区间内的现金流
     * @param absAssetsPackage
     * @return
     */
    List<HlsCusAbsAssetsPackage> selectPackContarctCashFlow(HlsCusAbsAssetsPackage absAssetsPackage);


    /**
     * 删除未结清的资产包
     * @param packId
     * @return
     */
    int deleteAssetPageageNotFinish(@Param("packId") Long packId);


    /**
     * 查找出为结清的资产包
     * @param packId
     * @return
     */
    List<HlsCusAbsAssetsPackage>  selectAssetPageageNotFinish(@Param("packId") Long packId);

    int selectTimesDateCount(@Param("pack") HlsCusAbsAssetsPackage pack, @Param("tableName") String tableName, @Param("dueDateStr") String dueDateStr);

    int selectPackDateCount(HlsCusAbsAssetsPackage absAssetsPackage);

    HlsCusAbsAssetsPackage selectCashflowInfo(HlsCusAbsAssetsPackage absAssetsPackage);

}
