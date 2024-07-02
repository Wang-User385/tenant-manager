package com.hand.hls.pam.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.pam.dto.HlsLeaseItemDetail;
import com.hand.hls.pam.dto.HlsLeaseItemTransfer;

import java.util.List;

public interface HlsLeaseItemTransferMapper extends Mapper<HlsLeaseItemTransfer>{


    List<HlsLeaseItemTransfer> selectLeaseTransferForeignKey(HlsLeaseItemTransfer hlsLeaseItemTransfer);
}