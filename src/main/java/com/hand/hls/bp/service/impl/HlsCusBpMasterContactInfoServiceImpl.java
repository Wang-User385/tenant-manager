package com.hand.hls.bp.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.HlsCusBpMasterContactInfo;
import com.hand.hls.bp.mapper.HlsCusBpMasterContactInfoMapper;
import com.hand.hls.bp.service.HlsCusBpMasterContactInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusBpMasterContactInfoServiceImpl extends BaseServiceImpl<HlsCusBpMasterContactInfo> implements HlsCusBpMasterContactInfoService {

    @Autowired
    private HlsCusBpMasterContactInfoMapper mapper;

    @Override
    public List<HlsCusBpMasterContactInfo> queryAll(HlsCusBpMasterContactInfo dto, int page, int pagesize) {
        PageHelper.startPage(page,pagesize);
        return mapper.queryAll(dto);
    }

    @Override
    public List<HlsCusBpMasterContactInfo> queryAllAuthorize(HlsCusBpMasterContactInfo dto, int page, int pagesize) {
        PageHelper.startPage(page,pagesize);
        return mapper.queryAllAuthorize(dto);
    }

    @Override
    public List<HlsCusBpMasterContactInfo> queryAllBenifit(HlsCusBpMasterContactInfo dto, int page, int pagesize) {
        PageHelper.startPage(page,pagesize);
        return mapper.queryAllBenifit(dto);
    }
}
