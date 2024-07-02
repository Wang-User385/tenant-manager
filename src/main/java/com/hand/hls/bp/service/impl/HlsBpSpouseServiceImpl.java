package com.hand.hls.bp.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.mapper.HlsBpSpouseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.bp.dto.HlsBpSpouse;
import com.hand.hls.bp.service.HlsBpSpouseService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsBpSpouseServiceImpl extends BaseServiceImpl<HlsBpSpouse> implements HlsBpSpouseService{

    @Autowired
    private HlsBpSpouseMapper mapper;
    @Override
    public List<HlsBpSpouse> selectAll(IRequest iRequest, HlsBpSpouse hlsBpSpouse, int page, int pageSize){
        PageHelper.startPage(page,pageSize);
        return mapper.queryAll(hlsBpSpouse);
    }
}