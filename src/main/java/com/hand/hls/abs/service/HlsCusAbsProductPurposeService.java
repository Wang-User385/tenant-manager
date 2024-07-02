package com.hand.hls.abs.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.abs.dto.HlsCusAbsProductPurpose;

import java.util.List;

public interface HlsCusAbsProductPurposeService extends IBaseService<HlsCusAbsProductPurpose>, ProxySelf<HlsCusAbsProductPurposeService> {


    /**
     * 查询
     * @param iRequest
     * @param absProductPurpose
     * @param page
     * @param pageSize
     * @return
     */
    List<HlsCusAbsProductPurpose> selectAbsProductPurpose(IRequest iRequest, HlsCusAbsProductPurpose absProductPurpose, int page, int pageSize);
}