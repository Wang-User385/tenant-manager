package com.hand.hls.fct.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineMortgage;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineMortgageMapper;
import com.hand.hls.fct.service.HlsCusHlsCreditLineMortgageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusHlsCreditLineMortgageServiceImpl extends BaseServiceImpl<HlsCusHlsCreditLineMortgage> implements HlsCusHlsCreditLineMortgageService {
    @Autowired
    private HlsCusHlsCreditLineMortgageMapper mapper;

    /**
     * 查询抵押信息
     *
     * @param iRequest
     * @param hlsCusHlsCreditLineMortgage
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public List<HlsCusHlsCreditLineMortgage> queryCreditLineMortgage(IRequest iRequest, HlsCusHlsCreditLineMortgage hlsCusHlsCreditLineMortgage, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<HlsCusHlsCreditLineMortgage> hlsCusHlsCreditLineMortgageList = mapper.queryCreditLineMortgageByCreditLineId(hlsCusHlsCreditLineMortgage);
        return hlsCusHlsCreditLineMortgageList;
    }
}
