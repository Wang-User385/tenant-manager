package com.hand.hls.fct.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChanceAttach;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChanceBp;

import java.util.List;

public interface HlsCusHlsCreditLineChanceAttachService extends IBaseService<HlsCusHlsCreditLineChanceAttach>, ProxySelf<HlsCusHlsCreditLineChanceAttachService> {

    /**
     * 根据头Id查询质押信息
     * @param requestContext
     * @param cusHlsCreditLineChanceAttach
     * @param page
     * @param pageSize
     * @return 返回对象的集合
     */
//    List<HlsCusHlsCreditLineChanceAttach> selectByForeignKey(IRequest requestContext, HlsCusHlsCreditLineChanceAttach cusHlsCreditLineChanceAttach, Integer page, Integer pageSize);



}