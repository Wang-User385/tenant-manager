package com.hand.hls.fct.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fct.dto.HlsCusHlsCreditChancePledge;

import java.util.List;

public interface HlsCusHlsCreditChancePledgeService extends IBaseService<HlsCusHlsCreditChancePledge>, ProxySelf<HlsCusHlsCreditChancePledgeService> {
    /**
     * 根据头Id查询质押信息
     * @param requestContext
     * @param chancePledge
     * @param page
     * @param pageSize
     * @return 返回对象的集合
     */
    List<HlsCusHlsCreditChancePledge> selectByForeignKey(IRequest requestContext, HlsCusHlsCreditChancePledge chancePledge, Integer page, Integer pageSize);
}