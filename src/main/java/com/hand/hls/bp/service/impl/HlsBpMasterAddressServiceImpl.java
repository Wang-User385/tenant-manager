package com.hand.hls.bp.service.impl;

import java.util.List;

import com.hand.hls.bp.dto.HlsCusBpMasterAddress;
import com.hand.hls.bp.service.HlsBpMasterAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.mapper.HlsCusBpMasterAddressMapper;

@Service
@Transactional

public class HlsBpMasterAddressServiceImpl extends BaseServiceImpl<HlsCusBpMasterAddress> implements HlsBpMasterAddressService {

    @Autowired
    private HlsCusBpMasterAddressMapper mapper;
    @Override
    public List<HlsCusBpMasterAddress> selectAll(IRequest requestContext, HlsCusBpMasterAddress bpMasterAddress, int page,
                                                 int pagesize) {
        PageHelper.startPage(page, pagesize);
        return mapper.queryAll(bpMasterAddress);
    }

    @Override
    protected boolean useSelectiveUpdate() {
        return false;
    }
}
