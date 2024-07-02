package com.hand.hls.hn.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.hn.dto.PrjCheckQuestionSituation;
import com.hand.hls.hn.dto.PrjCheckSubjectChange;

import java.util.List;

public interface PrjCheckQuestionSituationMapper extends Mapper<PrjCheckQuestionSituation>{
    List<PrjCheckQuestionSituation> queryList(PrjCheckQuestionSituation prjCheckQuestionSituation);
}