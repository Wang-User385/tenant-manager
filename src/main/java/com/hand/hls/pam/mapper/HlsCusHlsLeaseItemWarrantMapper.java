package com.hand.hls.pam.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.pam.dto.HlsCusHlsLeaseItemWarrant;
import com.hand.hls.pam.dto.HlsWarrantStockHd;
import com.hand.hls.pam.dto.HlsWarrantStockLn;

import java.util.List;

public interface HlsCusHlsLeaseItemWarrantMapper extends HlsLeaseItemWarrantMapper<HlsCusHlsLeaseItemWarrant>{

    List<HlsCusHlsLeaseItemWarrant> queryMainContracrInfo(HlsCusHlsLeaseItemWarrant hlsCusHlsLeaseItemWarrant);

    List<HlsCusHlsLeaseItemWarrant> queryWarrantDetailInfo(HlsCusHlsLeaseItemWarrant hlsCusHlsLeaseItemWarrant);

    List<HlsCusHlsLeaseItemWarrant> queryCategoryInfo(HlsCusHlsLeaseItemWarrant hlsCusHlsLeaseItemWarrant);

    List<HlsCusHlsLeaseItemWarrant> queryDocumentInfo(HlsCusHlsLeaseItemWarrant hlsCusHlsLeaseItemWarrant);

    List<HlsCusHlsLeaseItemWarrant> queryBusinessDocumentInfo(HlsCusHlsLeaseItemWarrant hlsCusHlsLeaseItemWarrant);

    List<HlsCusHlsLeaseItemWarrant> queryWarrantByStock(HlsWarrantStockHd hlsWarrantStockHd);

}