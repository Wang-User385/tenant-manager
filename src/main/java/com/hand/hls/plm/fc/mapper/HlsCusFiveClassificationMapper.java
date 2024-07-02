package com.hand.hls.plm.fc.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.plm.fc.dto.HlsCusFiveClassification;


import java.util.List;

public interface HlsCusFiveClassificationMapper extends Mapper<HlsCusFiveClassification> {
    List<HlsCusFiveClassification> homeRollTableQuery(HlsCusFiveClassification cusFiveClassification);

    List<HlsCusFiveClassification> selectFcByBelongsToId(HlsCusFiveClassification cusFiveClassification);

    List<HlsCusFiveClassification> selectFiveClassficationByBelongsToId(HlsCusFiveClassification hlsCusFiveClassification);

    List<HlsCusFiveClassification> queryAll(HlsCusFiveClassification cusFiveClassification);
}