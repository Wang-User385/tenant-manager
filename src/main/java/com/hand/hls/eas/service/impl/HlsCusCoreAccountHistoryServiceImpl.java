package com.hand.hls.eas.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.eas.mapper.HlsCusCoreAccountHistoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.eas.dto.HlsCusCoreAccountHistory;
import com.hand.hls.eas.service.IHlsCusCoreAccountHistoryService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusCoreAccountHistoryServiceImpl extends BaseServiceImpl<HlsCusCoreAccountHistory> implements IHlsCusCoreAccountHistoryService{


    @Autowired
    private HlsCusCoreAccountHistoryMapper hlsCusCoreAccountHistoryMapper;

    @Override
    public List<HlsCusCoreAccountHistory> selectDataByCheckDate(HlsCusCoreAccountHistory dto, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        List<HlsCusCoreAccountHistory> list = hlsCusCoreAccountHistoryMapper.selectDataByCheckDate(dto);
        return list;
    }


}