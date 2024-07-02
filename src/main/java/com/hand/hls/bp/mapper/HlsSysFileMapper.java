package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsCusSysFile;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HlsSysFileMapper<T extends HlsCusSysFile> extends Mapper<HlsCusSysFile> {
    List<HlsCusSysFile> queryAll(HlsCusSysFile var1);

    HlsCusSysFile queryByAttachmentId(@Param("attachmentId") Long var1);

    List<HlsCusSysFile> selectAllContent(@Param("sourceKey") Long var1);

    List<HlsCusSysFile> selectAllConContent(@Param("sourceKey") Long var1);

    List<HlsCusSysFile> queryUka(@Param("sourceKey") Long var1);

    List<HlsCusSysFile> selectByAttachmentId(@Param("attachmentId") Long var1);
}
