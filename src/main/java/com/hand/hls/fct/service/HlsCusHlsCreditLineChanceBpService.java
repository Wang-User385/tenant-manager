package com.hand.hls.fct.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fct.dto.HlsCusHlsCreditChancePledge;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChanceBp;

import java.util.List;

public interface HlsCusHlsCreditLineChanceBpService extends IBaseService<HlsCusHlsCreditLineChanceBp>, ProxySelf<HlsCusHlsCreditLineChanceBpService> {

    /**
     * 根据头Id查询质押信息
     * @param requestContext
     * @param cusHlsCreditLineChanceBp
     * @param page
     * @param pageSize
     * @return 返回对象的集合
     */
    List<HlsCusHlsCreditLineChanceBp> selectByForeignKey(IRequest requestContext, HlsCusHlsCreditLineChanceBp cusHlsCreditLineChanceBp, Integer page, Integer pageSize);

    List<HlsCusHlsCreditLineChanceBp> selectBpByMarket(IRequest requestContext, HlsCusHlsCreditLineChanceBp cusHlsCreditLineChanceBp);

    void updateUsedAmountByBpId(HlsCusHlsCreditLineChanceBp hlsCusHlsCreditLineChanceBp);
}