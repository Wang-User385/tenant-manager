package com.hand.hls.archive.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.archive.dto.HlsCusArchiveAttachment;

import java.util.List;
import java.util.Map;

public interface HlsCusArchiveAttachmentMapper extends Mapper<HlsCusArchiveAttachment>{

    //查询项目下所有附件
    List<HlsCusArchiveAttachment> queryAttachmentByProject(HlsCusArchiveAttachment hlsCusArchiveAttachment);
}