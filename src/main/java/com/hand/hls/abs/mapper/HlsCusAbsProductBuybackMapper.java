package com.hand.hls.abs.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.abs.dto.HlsCusAbsProductBuyback;

import java.util.List;

public interface HlsCusAbsProductBuybackMapper extends Mapper<HlsCusAbsProductBuyback> {


    /**
     * 查询
     * @param productBuyback
     * @return
     */
    List<HlsCusAbsProductBuyback> selectProductBuybackData(HlsCusAbsProductBuyback productBuyback);
}