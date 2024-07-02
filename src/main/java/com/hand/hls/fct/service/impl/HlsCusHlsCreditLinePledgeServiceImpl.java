package com.hand.hls.fct.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fct.dto.HlsCusHlsCreditLinePledge;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLinePledgeMapper;
import com.hand.hls.fct.service.HlsCusHlsCreditLinePledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusHlsCreditLinePledgeServiceImpl extends BaseServiceImpl<HlsCusHlsCreditLinePledge> implements HlsCusHlsCreditLinePledgeService {

    @Autowired
    private HlsCusHlsCreditLinePledgeMapper mapper;

    /**
     * 查询质押信息
     *
     * @param iRequest
     * @param hlsCusHlsCreditLinePledge
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public List<HlsCusHlsCreditLinePledge> queryCreditLinePledge(IRequest iRequest, HlsCusHlsCreditLinePledge hlsCusHlsCreditLinePledge, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<HlsCusHlsCreditLinePledge> hlsCusHlsCreditLinePledgeList = mapper.queryCreditLinePledgeByCreditLineId(hlsCusHlsCreditLinePledge);
        return hlsCusHlsCreditLinePledgeList;
    }
}
