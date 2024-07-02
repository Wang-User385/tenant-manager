package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.HlsCusProjectChangeMeetingJudge;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HlsCusProjectChangeMeetingJudgeMapper extends Mapper<HlsCusProjectChangeMeetingJudge> {
    /**
     * 获取项目上会评委主任
     *
     * @param var1 processInstaceId
     * @return java.util.list<String>
     */
    List<String> queryMeetingJudgeByProcessInstanceId(Long var1);

    /**
     * 获取项目上会评委
     *
     * @param var1 processInstaceId
     * @return java.util.list<String>
     */
    List<String> queryAllMeetingJudgeByProcessInstanceId(Long var1);


    List<HlsCusProjectChangeMeetingJudge> queryAllJudge(@Param("companyId") Long var1);

    void deleteByMeetId(Long var1);

    List<HlsCusProjectChangeMeetingJudge> queryPrjProjectMeetingJudgeByProjectMeetingId(HlsCusProjectChangeMeetingJudge var1);

    HlsCusProjectChangeMeetingJudge queryApprovedResult(@Param("projectId") Long var1);

    List<HlsCusProjectChangeMeetingJudge> queryDetail(@Param("projectId") Long var1);

    List<HlsCusProjectChangeMeetingJudge> queryUser(@Param("projectId") Long var1);

    List<HlsCusProjectChangeMeetingJudge> queryJudgeByJudgeId(HlsCusProjectChangeMeetingJudge var1);

    void updateMeetingResult(HlsCusProjectChangeMeetingJudge var1);

}

