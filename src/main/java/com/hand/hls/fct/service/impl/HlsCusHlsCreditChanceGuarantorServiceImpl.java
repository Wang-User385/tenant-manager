package com.hand.hls.fct.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fct.dto.HlsCusHlsCreditChanceGuarantor;
import com.hand.hls.fct.mapper.HlsCusHlsCreditChanceGuarantorMapper;
import com.hand.hls.fct.service.HlsCusHlsCreditChanceGuarantorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusHlsCreditChanceGuarantorServiceImpl extends BaseServiceImpl<HlsCusHlsCreditChanceGuarantor> implements HlsCusHlsCreditChanceGuarantorService {

    @Autowired
    private HlsCusHlsCreditChanceGuarantorMapper chanceGuarantorMapper;
    /**
     * 根据授信立项主键【chanceId】查询所有的保证信息
     *
     * @param iRequest
     * @param chanceGuarantor
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public List<HlsCusHlsCreditChanceGuarantor> selectByForeignKey(IRequest iRequest, HlsCusHlsCreditChanceGuarantor chanceGuarantor, Integer page, Integer pageSize) {
        if(page!=null && pageSize!=null){
            PageHelper.startPage(page,pageSize);
        }
        return chanceGuarantorMapper.selectByForeignKey(chanceGuarantor);
    }
}