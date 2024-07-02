package com.hand.hls.gld.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hls.gld.dto.HlsCusCtDocumentFinIncome;
import com.hand.hls.gld.dto.HlsCusFinIncomePkg;
import com.hand.hap.system.service.IBaseService;

/**
 * @author wujun
 * @version 1.0
 * @date 2020/2/7 18:59
 * @description
 */
public interface HlsCusCtDocumentFinIncomeService extends IBaseService<HlsCusCtDocumentFinIncome>, ProxySelf<HlsCusCtDocumentFinIncomeService> {
    /**
     * fetch data by rule id
     *
     * @param request request
     * @param hlsCusFinIncomePkg HlsCusFinIncomePkg
     * @return Result<update>
     */
    void insertCtFin(IRequest request, HlsCusFinIncomePkg hlsCusFinIncomePkg);
}
