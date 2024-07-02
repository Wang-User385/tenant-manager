package com.hand.hls.abs.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.abs.dto.HlsCusAbsProductRelease;

import java.math.BigDecimal;
import java.util.List;

public interface HlsCusAbsProductReleaseMapper extends Mapper<HlsCusAbsProductRelease> {


    /**
     * 查询数据
     * @param productRelease
     * @return
     */
   List<HlsCusAbsProductRelease> selectProductReleaseData(HlsCusAbsProductRelease productRelease);


    BigDecimal selectRealseAmounSum(HlsCusAbsProductRelease productRelease);

}