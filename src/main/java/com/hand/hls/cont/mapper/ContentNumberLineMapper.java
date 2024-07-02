package com.hand.hls.cont.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cont.dto.ContentNumberLine;

import java.util.List;
import java.util.Map;

public interface ContentNumberLineMapper extends Mapper<ContentNumberLine>{

    List<ContentNumberLine> selectContentLineList(ContentNumberLine line);

}