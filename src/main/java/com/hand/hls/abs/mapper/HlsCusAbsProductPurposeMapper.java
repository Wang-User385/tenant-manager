package com.hand.hls.abs.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.abs.dto.HlsCusAbsProductPurpose;

import java.util.List;

public interface HlsCusAbsProductPurposeMapper extends Mapper<HlsCusAbsProductPurpose> {


    /**
     * 查询
     * @param absProductPurpose
     * @return
     */
    List<HlsCusAbsProductPurpose> selectAbsProductPurpose(HlsCusAbsProductPurpose absProductPurpose);

}