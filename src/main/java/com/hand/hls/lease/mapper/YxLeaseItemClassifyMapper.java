package com.hand.hls.lease.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.lease.dto.YxLeaseItemClassify;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface YxLeaseItemClassifyMapper extends Mapper<YxLeaseItemClassify> {
    List<YxLeaseItemClassify> yxLeaseItemClassifyQuery(Map map);
    List<YxLeaseItemClassify> yxLeaseItemClassifyQueryForImport(Map map);
    List<YxLeaseItemClassify> yxLeaseItemClassifyTreeQuery(YxLeaseItemClassify yxLeaseItemClassify);
    List<YxLeaseItemClassify> yxLeaseItemClassifyForLov(YxLeaseItemClassify yxLeaseItemClassify);
    List<YxLeaseItemClassify> yxLeaseItemClassifyQueryByProductModel(@Param("attributeValue") String attributeValue,@Param("bpId") Long bpId);

    /**
     * 查询租赁物产品可比价格
     */
    List<Map> queryComparePrice(@Param("classifyId") Long classifyId);

    List<Map> querySysCodeByValue(Map map);

    Long queryRepeatData(Map map);

    void updateAdvancedEquipmentFlag(@Param("classifyId") Long classifyId, @Param("advancedEquipmentFlag") String advancedEquipmentFlag);

    List<YxLeaseItemClassify> classifyQueryYearsOrRateNull(@Param("attribute") String attribute);
}