package com.hand.hls.abs.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.abs.dto.HlsCusAbsProductPurpose;
import com.hand.hls.abs.mapper.HlsCusAbsProductPurposeMapper;
import com.hand.hls.abs.service.HlsCusAbsProductPurposeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusAbsProductPurposeServiceImpl extends BaseServiceImpl<HlsCusAbsProductPurpose> implements HlsCusAbsProductPurposeService {

    @Autowired
    private HlsCusAbsProductPurposeMapper absProductPurposeMapper;

    @Override
    public List<HlsCusAbsProductPurpose> selectAbsProductPurpose(IRequest iRequest, HlsCusAbsProductPurpose absProductPurpose, int page, int pageSize) {

        PageHelper.startPage(page,pageSize);

        return absProductPurposeMapper.selectAbsProductPurpose(absProductPurpose);
    }
}