package com.hand.hls.fnd.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fnd.dto.CalcPrice;

import java.util.List;
import java.util.Map;

public interface CalcPriceMapper extends Mapper<CalcPrice>{

    List<Map> selectCalcPrice();

    List<Map> selectPrjCalcPrice();

    List<Map> selectQuotationAllInfo();
}