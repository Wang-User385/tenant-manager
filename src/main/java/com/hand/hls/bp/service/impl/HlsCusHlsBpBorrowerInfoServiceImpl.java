package com.hand.hls.bp.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.HlsCusHlsBpBorrowerInfo;
import com.hand.hls.bp.mapper.HlsCusHlsBpBorrowerInfoMapper;
import com.hand.hls.bp.service.HlsCusHlsBpBorrowerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusHlsBpBorrowerInfoServiceImpl extends BaseServiceImpl<HlsCusHlsBpBorrowerInfo> implements HlsCusHlsBpBorrowerInfoService {

    @Autowired
    HlsCusHlsBpBorrowerInfoMapper HlsCusHlsBpBorrowerInfoMapper ;

    @Override
    public List<HlsCusHlsBpBorrowerInfo> queryAll(HlsCusHlsBpBorrowerInfo dto, IRequest requestContext, int page, int pagesize){
        return HlsCusHlsBpBorrowerInfoMapper.queryAll(dto);
    }
}