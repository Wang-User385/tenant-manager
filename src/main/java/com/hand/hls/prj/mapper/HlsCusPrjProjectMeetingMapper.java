package com.hand.hls.prj.mapper;


import com.hand.hls.prj.dto.HlsCusPrjProjectMeeting;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HlsCusPrjProjectMeetingMapper extends PrjProjectMeetingMapper<HlsCusPrjProjectMeeting> {

    HlsCusPrjProjectMeeting queryApproveResult(@Param("projectId") Long projectId);
    void updateMeetingStatus(@Param("projectId") Long projectId,@Param("dataClass") String dataClass);

}