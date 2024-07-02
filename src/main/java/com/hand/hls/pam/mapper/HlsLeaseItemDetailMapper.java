package com.hand.hls.pam.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.pam.dto.HlsCusLeaseItem;
import com.hand.hls.pam.dto.HlsCusLeaseItemTaxes;
import com.hand.hls.pam.dto.HlsLeaseItemDetail;

import java.util.List;

public interface HlsLeaseItemDetailMapper extends Mapper<HlsLeaseItemDetail>{

    List<HlsLeaseItemDetail> selectByLeaseForeignKey(HlsLeaseItemDetail hlsLeaseItemDetail);
    void deleteLeaseItemDetailByLeaseItemId(HlsLeaseItemDetail hlsBpFinancialHeader);

    List<HlsLeaseItemDetail> selectByLeaseSerialNumber(HlsLeaseItemDetail hlsLeaseItemDetail);

    List<HlsLeaseItemDetail> selectLeaseMovables();

    List<HlsCusLeaseItem> selectLeaseNoMovables();

    List<HlsCusLeaseItem> selectLeaseMovablesDz();


}