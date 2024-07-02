package com.hand.hls.hls.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.hls.dto.HlsWebExcelCalcResult;

import java.util.List;

public interface HlsWebExcelCalcResultMapper extends Mapper<HlsWebExcelCalcResult>{

    List<HlsWebExcelCalcResult> selectHlsWebExcelCalcResultInfo(HlsWebExcelCalcResult calcResult);

}