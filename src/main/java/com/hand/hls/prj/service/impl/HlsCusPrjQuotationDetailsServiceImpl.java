package com.hand.hls.prj.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotationDetails;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationDetailsMapper;
import com.hand.hls.prj.service.HlsCusPrjQuotationDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjQuotationDetailsServiceImpl extends BaseServiceImpl<HlsCusPrjQuotationDetails> implements HlsCusPrjQuotationDetailsService {

    @Autowired
    private HlsCusPrjQuotationDetailsMapper prjQuotationDetailsMapper;

    @Override
    public List<HlsCusPrjQuotationDetails> queryDetailsById(HlsCusPrjQuotationDetails prjQuotationDetails) {
        return prjQuotationDetailsMapper.queryDetailsById(prjQuotationDetails);
    }

    @Override
    public int deleteDetailsById(HlsCusPrjQuotation hlsCusPrjQuotation) {
        return prjQuotationDetailsMapper.deleteDetailsById(hlsCusPrjQuotation);
    }
}