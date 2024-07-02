package com.hand.hls.plm.pli.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.plm.pli.dto.PlmPliUpcoming;

import java.util.List;

public interface PlmPliUpcomingMapper extends Mapper<PlmPliUpcoming> {

    List<PlmPliUpcoming> selectUpcomingList(PlmPliUpcoming plmPliUpcoming);

    List<PlmPliUpcoming> selectUpcomingListNotAnyCondition(PlmPliUpcoming plmPliUpcoming);

}
