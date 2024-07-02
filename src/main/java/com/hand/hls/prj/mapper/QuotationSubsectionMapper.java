package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.QuotationSubsection;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface QuotationSubsectionMapper extends Mapper<QuotationSubsection> {
    Double quotationSubsectionMaxTimes(@Param("quotationId") Long quotationId);

    List<QuotationSubsection> quotationSubsectionByFloatingRateStartTimes(QuotationSubsection quotationSubsection);
}