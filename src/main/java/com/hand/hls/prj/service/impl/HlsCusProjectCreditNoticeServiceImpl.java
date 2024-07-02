package com.hand.hls.prj.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.prj.dto.HlsCusProjectCreditNotice;
import com.hand.hls.prj.mapper.HlsCusProjectCreditNoticeMapper;
import com.hand.hls.prj.service.IHlsCusProjectCreditNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusProjectCreditNoticeServiceImpl extends BaseServiceImpl<HlsCusProjectCreditNotice> implements IHlsCusProjectCreditNoticeService{

    @Autowired
    private HlsCusProjectCreditNoticeMapper hlsCusProjectCreditNoticeMapper;

    @Override
    public List<HlsCusProjectCreditNotice> QueryAllByInstanceId(HlsCusProjectCreditNotice hlsCusProjectCreditNotice) {
        return hlsCusProjectCreditNoticeMapper.QueryAllByInstanceId(hlsCusProjectCreditNotice);
    }
}