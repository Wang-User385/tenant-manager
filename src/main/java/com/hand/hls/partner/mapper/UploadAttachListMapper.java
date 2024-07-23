package com.hand.hls.partner.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.partner.dto.UploadAttachList;
import org.apache.ibatis.annotations.Param;

public interface UploadAttachListMapper extends Mapper<UploadAttachList>{

    UploadAttachList selectByFileId(@Param("fileId") String fileId);
}