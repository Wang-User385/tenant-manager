package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.HlsCusPrjProjectMeeting;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface PrjProjectMeetingMapper<T extends HlsCusPrjProjectMeeting> extends Mapper<HlsCusPrjProjectMeeting> {
    List<HlsCusPrjProjectMeeting> queryPrjProjectMeetingByProjectId(HlsCusPrjProjectMeeting p);

    public HlsCusPrjProjectMeeting queryMeetingByMeetingId(HlsCusPrjProjectMeeting p);

    HlsCusPrjProjectMeeting selectTimes(Long projectMeeting);

    void updatePrjMeetingStatus(@Param("projectId") Long projectId,
                                @Param("status") String status, @Param("date") Date date,
                                @Param("userId") Long userId, @Param("result") String result);

    List<HlsCusPrjProjectMeeting> queryInfo(HlsCusPrjProjectMeeting p);

    List<HlsCusPrjProjectMeeting> queryInfoChange(HlsCusPrjProjectMeeting p);

    List<HlsCusPrjProjectMeeting> queryConfirmMeetingInfo(@Param("projectId") Long projectId, @Param("processInstanceId") Long processInstanceId);
}