package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.HlsCusPrjSurviveRequire;

import java.util.List;

public interface HlsCusPrjSurviveRequireMapper extends Mapper<HlsCusPrjSurviveRequire>{

    void deleteSurviveRequireByProjectId(HlsCusPrjSurviveRequire require);

    List<HlsCusPrjSurviveRequire> selectChangeAddInfo(HlsCusPrjSurviveRequire require);

    List<HlsCusPrjSurviveRequire> selectChangeRemoveInfo(HlsCusPrjSurviveRequire require);

    List<HlsCusPrjSurviveRequire> selectChangeDiffInfo(HlsCusPrjSurviveRequire require);

    List<HlsCusPrjSurviveRequire> selectNotChangeInfo(HlsCusPrjSurviveRequire require);
}