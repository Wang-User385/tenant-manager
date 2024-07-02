package com.hand.hls.bp.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.mapper.FndScoreResultMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.bp.dto.FndScoreResult;
import com.hand.hls.bp.service.IFndScoreResultService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class FndScoreResultServiceImpl extends BaseServiceImpl<FndScoreResult> implements IFndScoreResultService{

    @Autowired
    FndScoreResultMapper mapper;

    public FndScoreResultServiceImpl() {
    }

    public List<FndScoreResult> queryFndScoreResult(Long Id) {
        return this.mapper.queryFndScoreResult1(Id);
    }

}