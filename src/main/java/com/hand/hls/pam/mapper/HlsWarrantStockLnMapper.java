package com.hand.hls.pam.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.pam.dto.HlsWarrantStockLn;

public interface HlsWarrantStockLnMapper extends Mapper<HlsWarrantStockLn>{

    HlsWarrantStockLn warrantStockCheck (HlsWarrantStockLn hlsWarrantStockLn);

}