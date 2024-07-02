package com.hand.hls.bp.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.mapper.QujianDataSourceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.bp.dto.QujianDataSource;
import com.hand.hls.bp.service.IQujianDataSourceService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class QujianDataSourceServiceImpl extends BaseServiceImpl<QujianDataSource> implements IQujianDataSourceService{
    @Autowired
    private QujianDataSourceMapper qujianDataSourceMapper;

    @Override
    public List<QujianDataSource> selectQujianDataSource(IRequest var1, QujianDataSource var2, int var3, int var4) {
        return qujianDataSourceMapper.selectQujianDataSource(var2);
    }
}