package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsCusSysAttachment;

import java.util.List;

public interface HlsSysAttachmentMapper<T extends HlsCusSysAttachment> extends Mapper<HlsCusSysAttachment> {
    List<HlsCusSysAttachment> sysAttachmentQuery(HlsCusSysAttachment var1);
}
