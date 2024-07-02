package com.hand.hls.hls.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.hls.dto.HlsWebExcel;

import java.util.List;
import java.util.Map;

public interface HlsWebExcelMapper extends Mapper<HlsWebExcel>{

    List<Map> selectHlsWebExcelInfo(HlsWebExcel excel);

}