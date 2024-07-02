package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.HlsCusPrjQuotationSubsection;

import java.util.List;

/**
 * @Author Robert8900
 * @Date: 2019/8/20 10:21
 * @Description:
 * @Purpose:
 **/
public interface HlsCusPrjQuotationSubsectionMapper extends Mapper<HlsCusPrjQuotationSubsection> {
    List<HlsCusPrjQuotationSubsection> selectPrjQuotationSubsectionDetail(HlsCusPrjQuotationSubsection hlsCusPrjQuotationSubsection);
}
