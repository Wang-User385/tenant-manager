package com.hand.hls.plm.fc.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.plm.fc.dto.PlmFiveClassifyMeet;

import java.util.List;

public interface PlmFiveClassifyMeetMapper extends Mapper<PlmFiveClassifyMeet> {
    List<PlmFiveClassifyMeet> queryMeetAllByKey(PlmFiveClassifyMeet dto);
}