package com.hand.hls.bp.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.mapper.HlsBpLitigationFinalMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.bp.dto.HlsBpLitigationFinal;
import com.hand.hls.bp.service.HlsBpLitigationFinalService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsBpLitigationFinalServiceImpl extends BaseServiceImpl<HlsBpLitigationFinal> implements HlsBpLitigationFinalService{

    @Autowired
    private HlsBpLitigationFinalMapper mapper;
    @Override
    public List<HlsBpLitigationFinal> selectAll(IRequest iRequest, HlsBpLitigationFinal hlsBpLitigationFinal, int page, int pageSize){
        PageHelper.startPage(page,pageSize);
        return mapper.queryAll(hlsBpLitigationFinal);
    }
}