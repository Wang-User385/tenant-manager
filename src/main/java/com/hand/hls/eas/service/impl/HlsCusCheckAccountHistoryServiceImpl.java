package com.hand.hls.eas.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.eas.mapper.HlsCusCheckAccountHistoryMapper;
import com.hand.hls.eas.service.IHlsCusEasLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.eas.dto.HlsCusCheckAccountHistory;
import com.hand.hls.eas.service.IHlsCusCheckAccountHistoryService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusCheckAccountHistoryServiceImpl extends BaseServiceImpl<HlsCusCheckAccountHistory> implements IHlsCusCheckAccountHistoryService{

    @Autowired
    private IHlsCusEasLoginService hlsCusEasLoginService;

    @Autowired
    private HlsCusCheckAccountHistoryMapper hlsCusCheckAccountHistoryMapper;


    @Override
    public HlsCusCheckAccountHistory  checkAccountDataPost(IRequest iRequest, HlsCusCheckAccountHistory dto){
        hlsCusEasLoginService.easCheckAccountSynNew(iRequest,dto.getCheckDate());

        dto= hlsCusCheckAccountHistoryMapper.isExistsNo(dto);
        return dto;
    }
}