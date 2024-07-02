package com.hand.hls.bp.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.HlsCusBpMasterAddress;
import com.hand.hls.bp.mapper.HlsCusBpMasterAddressMapper;
import com.hand.hls.bp.service.HlsCusBpMasterAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 胡兴恒
 * @Time 2020-05-06 18:49
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusBpMasterAddressServiceImpl extends BaseServiceImpl<HlsCusBpMasterAddress> implements HlsCusBpMasterAddressService {

    @Autowired
    private HlsCusBpMasterAddressMapper hlsCusBpMasterAddressMapper;

    @Override
    public List<HlsCusBpMasterAddress> selectbpAddress(HlsCusBpMasterAddress hlsCusBpMasterAddress, IRequest requestContext, int page, int pagesize) {
        return hlsCusBpMasterAddressMapper.queryAll(hlsCusBpMasterAddress);
    }
}
