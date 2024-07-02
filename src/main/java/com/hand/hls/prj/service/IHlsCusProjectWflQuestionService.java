package com.hand.hls.prj.service;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.prj.dto.HlsCusProjectWflQuestion;

import java.util.List;

public interface IHlsCusProjectWflQuestionService extends IBaseService<HlsCusProjectWflQuestion>, ProxySelf<IHlsCusProjectWflQuestionService>{
    List<HlsCusProjectWflQuestion> queryAll(HlsCusProjectWflQuestion hlsCusProjectWflQuestion);
}