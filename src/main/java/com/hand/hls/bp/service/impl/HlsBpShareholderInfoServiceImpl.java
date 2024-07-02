package com.hand.hls.bp.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.mapper.HlsBpShareholderInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.bp.dto.HlsBpShareholderInfo;
import com.hand.hls.bp.service.HlsBpShareholderInfoService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsBpShareholderInfoServiceImpl extends BaseServiceImpl<HlsBpShareholderInfo> implements HlsBpShareholderInfoService{

    @Autowired
    private HlsBpShareholderInfoMapper mapper;

    @Override
    public List<HlsBpShareholderInfo> selectAll(IRequest requestContext, HlsBpShareholderInfo hlsBpShareholderInfo, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return mapper.queryHlsBpShareholderInfo(hlsBpShareholderInfo);
    }

}
