package com.hand.hls.hls.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.hls.dto.HlsWebExcelConfigLn;

import java.util.List;

public interface HlsWebExcelConfigLnMapper extends Mapper<HlsWebExcelConfigLn>{

    List<HlsWebExcelConfigLn> selectHlsWebExcelConfiglineByHdId(HlsWebExcelConfigLn ln);
    List<HlsWebExcelConfigLn> selectHlsWebExcelConfiglineByExcelCode(HlsWebExcelConfigLn ln);

}