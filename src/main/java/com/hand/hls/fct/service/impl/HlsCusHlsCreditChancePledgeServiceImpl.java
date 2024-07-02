package com.hand.hls.fct.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fct.dto.HlsCusHlsCreditChancePledge;
import com.hand.hls.fct.mapper.HlsCusHlsCreditChancePledgeMapper;
import com.hand.hls.fct.service.HlsCusHlsCreditChancePledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusHlsCreditChancePledgeServiceImpl extends BaseServiceImpl<HlsCusHlsCreditChancePledge> implements HlsCusHlsCreditChancePledgeService {

    @Autowired
    private HlsCusHlsCreditChancePledgeMapper chancePledgeMapper;
    /**
     * 根据头Id查询质押信息
     *
     * @param requestContext
     * @param chancePledge
     * @param page
     * @param pageSize
     * @return 返回对象的集合
     */
    @Override
    public List<HlsCusHlsCreditChancePledge> selectByForeignKey(IRequest requestContext, HlsCusHlsCreditChancePledge chancePledge, Integer page, Integer pageSize) {
        if(page!=null && pageSize!=null){
            PageHelper.startPage(page,pageSize);
        }
        return chancePledgeMapper.selectByForeignKey(chancePledge);
    }
}