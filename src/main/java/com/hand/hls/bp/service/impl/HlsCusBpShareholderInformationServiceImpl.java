package com.hand.hls.bp.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.HlsCusBpShareholderInformation;
import com.hand.hls.bp.mapper.HlsCusBpShareholderInformationMapper;
import com.hand.hls.bp.service.HlsCusIBpShareholderInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusBpShareholderInformationServiceImpl extends BaseServiceImpl<HlsCusBpShareholderInformation> implements HlsCusIBpShareholderInformationService {

    @Autowired
    private HlsCusBpShareholderInformationMapper mapper;

    @Override
    public List<HlsCusBpShareholderInformation> selectAll(IRequest requestContext, HlsCusBpShareholderInformation bpShareholderInformation, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return mapper.queryAll(bpShareholderInformation);
    }
}