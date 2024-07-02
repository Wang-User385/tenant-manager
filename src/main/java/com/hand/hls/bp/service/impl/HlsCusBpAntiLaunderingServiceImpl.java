package com.hand.hls.bp.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.BpAntiLaundering;
import com.hand.hls.bp.mapper.BpAntiLaunderingMapper;
import com.hand.hls.bp.service.HlsCusBpAntiLaunderingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 胡兴恒
 * @Time 2020-04-30 15:04
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusBpAntiLaunderingServiceImpl extends BaseServiceImpl<BpAntiLaundering> implements HlsCusBpAntiLaunderingService {

    @Autowired
    private BpAntiLaunderingMapper bpAntiLaunderingMapper;

    @Override
    public List<BpAntiLaundering> selectAntiLaunderingByBpId(BpAntiLaundering bpAntiLaundering, IRequest requestContext, int page, int pagesize) {
        return bpAntiLaunderingMapper.queryAll(bpAntiLaundering);
    }
}
