package com.hand.hls.fct.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChanceAttach;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChanceBp;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceBpMapper;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceAttachService;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceBpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusHlsCreditLineChanceAttachServiceImpl extends BaseServiceImpl<HlsCusHlsCreditLineChanceAttach> implements HlsCusHlsCreditLineChanceAttachService {


    @Autowired
    private HlsCusHlsCreditLineChanceBpMapper creditLineChanceBpMapper;

   /* @Override
    public List<HlsCusHlsCreditLineChanceAttach> selectByForeignKey(IRequest requestContext, HlsCusHlsCreditLineChanceAttach cusHlsCreditLineChanceAttach, Integer page, Integer pageSize) {
        return null;
    }*/
}