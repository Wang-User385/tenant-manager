package com.hand.hls.fnd.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fnd.dto.HlsCusImpSegment;

import java.util.List;

public interface HlsCusImpSegmentMapper extends Mapper<HlsCusImpSegment> {

    /**
     * 查询行信息通过模板ID
     * @param segment
     * @return
     */
    List<HlsCusImpSegment> selectSegmentByTempId(HlsCusImpSegment segment);

}