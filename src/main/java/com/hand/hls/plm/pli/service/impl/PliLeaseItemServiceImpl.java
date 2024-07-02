package com.hand.hls.plm.pli.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.plm.pli.dto.PliLeaseItem;
import com.hand.hls.plm.pli.mapper.PliLeaseItemMapper;
import com.hand.hls.plm.pli.service.IPliLeaseItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class PliLeaseItemServiceImpl extends BaseServiceImpl<PliLeaseItem> implements IPliLeaseItemService {

    @Autowired
    PliLeaseItemMapper mapper;
    @Override
    public List<PliLeaseItem> queryPliLeaseItemInfo(PliLeaseItem dto, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return this.mapper.queryPliLeaseItemInfo(dto);
    }
}