package com.hand.hls.bp.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.mapper.HlsBpEventOthersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.bp.dto.HlsBpEventOthers;
import com.hand.hls.bp.service.HlsBpEventOthersService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsBpEventOthersServiceImpl extends BaseServiceImpl<HlsBpEventOthers> implements HlsBpEventOthersService{

    @Autowired
    private HlsBpEventOthersMapper mapper;

    @Override
    public List<HlsBpEventOthers> selectAll(IRequest iRequest, HlsBpEventOthers hlsBpEventOthers, int page, int pageSize){
        PageHelper.startPage(page,pageSize);
        return mapper.queryAll(hlsBpEventOthers);
    }

}