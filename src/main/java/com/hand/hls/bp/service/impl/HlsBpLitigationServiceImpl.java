package com.hand.hls.bp.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.mapper.HlsBpLitigationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.bp.dto.HlsBpLitigation;
import com.hand.hls.bp.service.HlsBpLitigationService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsBpLitigationServiceImpl extends BaseServiceImpl<HlsBpLitigation> implements HlsBpLitigationService{

    @Autowired
    private HlsBpLitigationMapper mapper;
    @Override
    public List<HlsBpLitigation> selectAll(IRequest iRequest, HlsBpLitigation hlsBpLitigation, int page, int pageSize){
        PageHelper.startPage(page,pageSize);
        return  mapper.queryAll(hlsBpLitigation);
    }
}