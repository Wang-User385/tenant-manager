package com.hand.hls.fct.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fct.dto.HlsCusHlsCreditChanceMortgage;
import com.hand.hls.fct.mapper.HlsCusHlsCreditChanceMortgageMapper;
import com.hand.hls.fct.service.HlsCusHlsCreditChanceMortgageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusHlsCreditChanceMortgageServiceImpl extends BaseServiceImpl<HlsCusHlsCreditChanceMortgage> implements HlsCusHlsCreditChanceMortgageService {

    @Autowired
    private HlsCusHlsCreditChanceMortgageMapper chanceMortgageMapper;
    /**
     * @param requestContext
     * @param chanceMortgage
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public List<HlsCusHlsCreditChanceMortgage> selectByForeignKey(IRequest requestContext, HlsCusHlsCreditChanceMortgage chanceMortgage, Integer page, Integer pageSize) {
        if(page!=null && pageSize!=null){
            PageHelper.startPage(page,pageSize);
        }
        return chanceMortgageMapper.selectByForeignKey(chanceMortgage);
    }
}