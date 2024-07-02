package com.hand.hls.bp.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.mapper.HlsBpAssetsListMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.bp.dto.HlsBpAssetsList;
import com.hand.hls.bp.service.HlsBpAssetsListService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsBpAssetsListServiceImpl extends BaseServiceImpl<HlsBpAssetsList> implements HlsBpAssetsListService{

    @Autowired
    private HlsBpAssetsListMapper mapper;
    @Override
    public List<HlsBpAssetsList> selectAll(IRequest iRequest, HlsBpAssetsList hlsBpAssetsList, int page, int pageSize){
        PageHelper.startPage(page,pageSize);
        return mapper.queryAll(hlsBpAssetsList);
    }
}