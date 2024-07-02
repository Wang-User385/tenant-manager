package com.hand.hls.archive.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.archive.dto.HlsCusArchive;

import java.util.List;

public interface HlsCusArchiveMapper extends Mapper<HlsCusArchive>{

    /**
     * 查找没有分类的数据
     * @return
     */
    List<HlsCusArchive> selectNnusedArchive();
}