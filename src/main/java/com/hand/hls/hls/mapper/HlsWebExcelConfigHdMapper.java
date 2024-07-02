package com.hand.hls.hls.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.hls.dto.HlsWebExcelConfigHd;

import java.util.List;

public interface HlsWebExcelConfigHdMapper extends Mapper<HlsWebExcelConfigHd>{

    List<HlsWebExcelConfigHd> getConfigHdInfoByExcel(HlsWebExcelConfigHd webExcelConfigHd);

}