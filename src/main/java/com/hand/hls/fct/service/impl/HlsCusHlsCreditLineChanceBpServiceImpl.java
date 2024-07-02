package com.hand.hls.fct.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fct.dto.HlsCusHlsCreditChancePledge;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChanceBp;
import com.hand.hls.fct.mapper.HlsCusHlsCreditChancePledgeMapper;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceBpMapper;
import com.hand.hls.fct.service.HlsCusHlsCreditChancePledgeService;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceBpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusHlsCreditLineChanceBpServiceImpl extends BaseServiceImpl<HlsCusHlsCreditLineChanceBp> implements HlsCusHlsCreditLineChanceBpService {

//    @Autowired
//    private HlsCusHlsCreditChancePledgeMapper chancePledgeMapper;

    @Autowired
    private HlsCusHlsCreditLineChanceBpMapper creditLineChanceBpMapper;

    /**
     * 根据头Id查询质押信息
     *
     * @param requestContext
     * @param cusHlsCreditLineChanceBp
     * @param page
     * @param pageSize
     * @return 返回对象的集合
     */
    @Override
    public List<HlsCusHlsCreditLineChanceBp> selectByForeignKey(IRequest requestContext, HlsCusHlsCreditLineChanceBp cusHlsCreditLineChanceBp, Integer page, Integer pageSize) {
        if(page!=null && pageSize!=null){
            PageHelper.startPage(page,pageSize);
        }
        return creditLineChanceBpMapper.selectByForeignKey(cusHlsCreditLineChanceBp);
    }

    @Override
    public List<HlsCusHlsCreditLineChanceBp> selectBpByMarket(IRequest requestContext, HlsCusHlsCreditLineChanceBp cusHlsCreditLineChanceBp) {
        return  creditLineChanceBpMapper.selectBpByMarket(cusHlsCreditLineChanceBp);
    }

    @Override
    public void updateUsedAmountByBpId(HlsCusHlsCreditLineChanceBp ChanceBp) {
        if(ChanceBp.getChanceId()==null||ChanceBp.getCreditAmountUsed()==null){
            return;
        }
        List<HlsCusHlsCreditLineChanceBp> chanceBps = creditLineChanceBpMapper.selectBpInfoByChance(ChanceBp);
        if(chanceBps.size()==0||chanceBps==null){
            return;
        }
        creditLineChanceBpMapper.updateUsedAmountByBpId(ChanceBp);
    }


}