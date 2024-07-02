//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.prj.mapper;

import com.hand.hls.prj.dto.HlsCusPrjProjectMeetingJudge;

import java.util.List;

public interface HlsCusPrjProjectMeetingJudgeMapper extends PrjProjectMeetingJudgeMapper<HlsCusPrjProjectMeetingJudge> {

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

    void updatePrjProjectMeetingJudgeDetail(HlsCusPrjProjectMeetingJudge hlsCusPrjProjectMeetingJudge);

}
