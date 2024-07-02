package com.hand.hls.fnd.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fnd.dto.HlsCusImpData;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface HlsCusImpDataMapper extends Mapper<HlsCusImpData> {
    /**
     * 查询所有临时表数据
     * @param batchId
     * @return
     */
    List<Map<String,String>> getDataMap(@Param(value = "batchId") float batchId);

    void batchUpdateImpStatus(@Param(value = "batchId") float batchId,
                              @Param(value = "beforeStatus") String beforeStatus,
                              @Param(value = "afterStatus") String afterStatus);

}