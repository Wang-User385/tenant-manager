package com.hand.hls.hls.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.hls.dto.HlsDurationAttachment;

import java.util.List;

public interface HlsDurationAttachmentMapper extends Mapper<HlsDurationAttachment>{
    List<HlsDurationAttachment> selectDurationAttachmentInfo(HlsDurationAttachment hlsDurationAttachment);
}