package com.hand.hls.bp.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.HlsCusBpMasterVisitRecord;
import com.hand.hls.bp.mapper.HlsCusBpMasterVisitRecordMapper;
import com.hand.hls.bp.service.HlsCusIBpMasterVisitRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusBpMasterVisitRecordServiceImpl extends BaseServiceImpl<HlsCusBpMasterVisitRecord> implements HlsCusIBpMasterVisitRecordService{

    @Autowired
    private HlsCusBpMasterVisitRecordMapper mapper;

    @Override
    public List<HlsCusBpMasterVisitRecord> selectByBpid(IRequest iRequest, HlsCusBpMasterVisitRecord dto, int page, int pageSize) {
        PageHelper.startPage(page,pageSize);
        return mapper.selectByBpid(dto);
    }

    @Override
    protected boolean useSelectiveUpdate() {
        return false;
    }
}