package com.hand.hls.bp.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.mapper.BpShareholderInfoHtMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.bp.dto.BpShareholderInfoHt;
import com.hand.hls.bp.service.IBpShareholderInfoHtService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class BpShareholderInfoHtServiceImpl extends BaseServiceImpl<BpShareholderInfoHt> implements IBpShareholderInfoHtService{

    @Autowired
    private BpShareholderInfoHtMapper mapper;

    @Override
    public List<BpShareholderInfoHt> selectAll(IRequest requestContext, BpShareholderInfoHt bpShareholderInfoHt, int page, int pagesize){
        PageHelper.startPage(page, pagesize);
        return mapper.queryBpShareholderInfoHt(bpShareholderInfoHt);
    }

}