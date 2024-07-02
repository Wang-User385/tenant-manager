package com.hand.hls.bp.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.mapper.HlsBpSanctionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.bp.dto.HlsBpSanction;
import com.hand.hls.bp.service.HlsBpSanctionService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsBpSanctionServiceImpl extends BaseServiceImpl<HlsBpSanction> implements HlsBpSanctionService{

    @Autowired
    private HlsBpSanctionMapper mapper;
    @Override
    public List<HlsBpSanction> selectAll(IRequest iRequest,HlsBpSanction hlsBpSanction, int page, int pageSize){
        PageHelper.startPage(page,pageSize);
        return mapper.queryAll(hlsBpSanction);
    }

}