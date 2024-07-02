package com.hand.hls.fct.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fct.dto.HlsCusHlsCreditChanceGuarantor;

import java.util.List;

public interface HlsCusHlsCreditChanceGuarantorService extends IBaseService<HlsCusHlsCreditChanceGuarantor>, ProxySelf<HlsCusHlsCreditChanceGuarantorService> {
    /**
     * 根据授信立项主键【chanceId】查询所有的保证信息
     * @param iRequest
     * @param dto
     * @param page
     * @param pageSize
     * @return
     */
    List<HlsCusHlsCreditChanceGuarantor> selectByForeignKey(IRequest iRequest, HlsCusHlsCreditChanceGuarantor dto, Integer page, Integer pageSize);
}