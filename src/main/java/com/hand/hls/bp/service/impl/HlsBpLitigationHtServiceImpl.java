package com.hand.hls.bp.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.mapper.HlsBpLitigationHtMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.bp.dto.HlsBpLitigationHt;
import com.hand.hls.bp.service.HlsBpLitigationHtService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsBpLitigationHtServiceImpl extends BaseServiceImpl<HlsBpLitigationHt> implements HlsBpLitigationHtService{

    @Autowired
    private HlsBpLitigationHtMapper mapper;
    @Override
    public List<HlsBpLitigationHt> selectAll(IRequest iRequest, HlsBpLitigationHt hlsBpLitigationHt, int page, int pageSize){
        PageHelper.startPage(page,pageSize);
        return mapper.queryAll(hlsBpLitigationHt);
    }
}