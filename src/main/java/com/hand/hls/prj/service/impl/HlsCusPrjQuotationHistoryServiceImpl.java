package com.hand.hls.prj.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjQuotationHistory;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationHistoryMapper;
import com.hand.hls.prj.service.IHlsCusPrjQuotationHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjQuotationHistoryServiceImpl extends BaseServiceImpl<HlsCusPrjQuotationHistory> implements IHlsCusPrjQuotationHistoryService{

    @Autowired
    private HlsCusPrjQuotationHistoryMapper hlsCusPrjQuotationHistoryMapper;

    @Override
    public Long selectVersionCount(HlsCusPrjProject hlsCusPrjProject) {
        return hlsCusPrjQuotationHistoryMapper.selectVersionCount(hlsCusPrjProject);
    }

    @Override
    public Long selectVersionCountCon(HlsCusConContract hlsCusConContract) {
        return hlsCusPrjQuotationHistoryMapper.selectVersionCountCon(hlsCusConContract);
    }
}