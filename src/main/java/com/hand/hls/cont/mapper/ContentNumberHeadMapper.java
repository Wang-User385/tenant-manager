package com.hand.hls.cont.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cont.dto.ContentNumberHead;

import java.util.List;
import java.util.Map;

public interface ContentNumberHeadMapper extends Mapper<ContentNumberHead>{

    List<Map> selectHeadList();

}