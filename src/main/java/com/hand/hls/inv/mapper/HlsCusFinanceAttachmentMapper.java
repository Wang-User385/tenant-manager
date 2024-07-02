package com.hand.hls.inv.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.inv.dto.HlsCusFinanceAttachment;

import java.util.List;

public interface HlsCusFinanceAttachmentMapper extends Mapper<HlsCusFinanceAttachment> {
    List<HlsCusFinanceAttachment> queryAll(HlsCusFinanceAttachment hlsCusFinanceAttachment);

    List<HlsCusFinanceAttachment> invAttachmentDetailQuery(HlsCusFinanceAttachment hlsCusFinanceAttachment);
}