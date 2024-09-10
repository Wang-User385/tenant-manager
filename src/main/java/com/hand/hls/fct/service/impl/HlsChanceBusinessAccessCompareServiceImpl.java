package com.hand.hls.fct.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fct.dto.HlsChanceBusinessAccessCompare;
import com.hand.hls.fct.service.IHlsChanceBusinessAccessCompareService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsChanceBusinessAccessCompareServiceImpl extends BaseServiceImpl<HlsChanceBusinessAccessCompare> implements IHlsChanceBusinessAccessCompareService{

    @Override
    public List<HlsChanceBusinessAccessCompare> queryChanceCompare(HlsChanceBusinessAccessCompare dto) {

        return null;
    }

    @Override
    public IHlsChanceBusinessAccessCompareService self() {
        return IHlsChanceBusinessAccessCompareService.super.self();
    }
}