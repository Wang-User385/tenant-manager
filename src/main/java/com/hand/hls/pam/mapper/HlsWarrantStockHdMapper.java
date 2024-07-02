package com.hand.hls.pam.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.pam.dto.HlsWarrantStockHd;

import java.util.List;

public interface HlsWarrantStockHdMapper extends Mapper<HlsWarrantStockHd>{

    HlsWarrantStockHd queryApplicantInfo(HlsWarrantStockHd hlsWarrantStockHd);

    List<HlsWarrantStockHd> queryWarrantOutIn(HlsWarrantStockHd hlsWarrantStockHd);

    List<HlsWarrantStockHd> queryReturnDateNotice(HlsWarrantStockHd hlsWarrantStockHd);

}