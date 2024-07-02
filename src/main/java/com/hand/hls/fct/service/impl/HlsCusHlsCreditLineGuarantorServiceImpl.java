package com.hand.hls.fct.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineGuarantor;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineGuarantorMapper;
import com.hand.hls.fct.service.HlsCusHlsCreditLineGuarantorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusHlsCreditLineGuarantorServiceImpl extends BaseServiceImpl<HlsCusHlsCreditLineGuarantor> implements HlsCusHlsCreditLineGuarantorService {

    @Autowired
    private HlsCusHlsCreditLineGuarantorMapper mapper;

    /**
     * 查询保证信息
     *
     * @param iRequest
     * @param hlsCusHlsCreditLineGuarantor
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public List<HlsCusHlsCreditLineGuarantor> queryCreditLineGuarantor(IRequest iRequest, HlsCusHlsCreditLineGuarantor hlsCusHlsCreditLineGuarantor, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<HlsCusHlsCreditLineGuarantor> hlsCusHlsCreditLineGuarantorList = mapper.queryCreditLineGuarantor(hlsCusHlsCreditLineGuarantor);
        return hlsCusHlsCreditLineGuarantorList;
    }
}
