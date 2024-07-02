package com.hand.hls.prj.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.prj.dto.HlsCusProjectWflQuestion;
import com.hand.hls.prj.mapper.HlsCusProjectWflQuestionMapper;
import com.hand.hls.prj.service.IHlsCusProjectWflQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusProjectWflQuestionServiceImpl extends BaseServiceImpl<HlsCusProjectWflQuestion> implements IHlsCusProjectWflQuestionService{

    @Autowired
    private HlsCusProjectWflQuestionMapper hlsCusProjectWflQuestionMapper;

    @Override
    public List<HlsCusProjectWflQuestion> queryAll(HlsCusProjectWflQuestion hlsCusProjectWflQuestion) {
        return hlsCusProjectWflQuestionMapper.queryAll(hlsCusProjectWflQuestion);
    }
}