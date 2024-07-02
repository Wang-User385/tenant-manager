package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.HlsCusProjectWflQuestion;

import java.util.List;

public interface HlsCusProjectWflQuestionMapper extends Mapper<HlsCusProjectWflQuestion>{

    List<HlsCusProjectWflQuestion> queryAll(HlsCusProjectWflQuestion hlsCusProjectWflQuestion);

}