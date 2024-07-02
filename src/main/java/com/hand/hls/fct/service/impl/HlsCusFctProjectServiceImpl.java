package com.hand.hls.fct.service.impl;

import com.hand.hls.fct.dto.HlsCusFctProject;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fct.mapper.HlsCusFctProjectMapper;
import com.hand.hls.fct.service.HlsCusFctProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusFctProjectServiceImpl extends BaseServiceImpl<HlsCusFctProject> implements HlsCusFctProjectService {

    @Autowired
    private HlsCusFctProjectMapper mapper;

    @Override
    public List<HlsCusFctProject> selectManagerByUnitId(HlsCusFctProject fctProject) {
        return mapper.selectManagerByUnitId(fctProject);
    }
}
