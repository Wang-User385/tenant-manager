package com.hand.hls.abs.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.abs.dto.HlsCusAbsProductStructure;

import java.math.BigDecimal;
import java.util.List;

public interface HlsCusAbsProductStructureService extends IBaseService<HlsCusAbsProductStructure>, ProxySelf<HlsCusAbsProductStructureService> {


    /**
     * 查询分层机构
     * @param iRequest
     * @param productStructure
     * @param page
     * @param pageSize
     * @return
     */
    List<HlsCusAbsProductStructure> selectProductStructureData(IRequest iRequest, HlsCusAbsProductStructure productStructure, int page, int pageSize);


    /**
     * 兑付数据
     * @param structureId
     * @return
     */
    HlsCusAbsProductStructure selectCashStructure(IRequest iRequest, Long structureId, String dataClass, Long times);


    /**
     * 兑付利息总和
     * @param productId
     * @param interestPeriodDays
     * @return
     */
    BigDecimal selectStructureCashInterestSum(Long productId, Long interestPeriodDays);


    /**
     * 引用数量
     * @param productStructure
     * @return
     */
    int selectStructureQuoteCount(HlsCusAbsProductStructure productStructure);

}