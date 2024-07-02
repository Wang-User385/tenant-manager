package com.hand.hls.abs.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.abs.dto.HlsCusAbsProductBuyback;
import com.hand.hls.eft.dto.HlsCusFundTransferList;
import com.hand.hls.exception.HlsCusException;

import java.util.List;

public interface HlsCusAbsProductBuybackService extends IBaseService<HlsCusAbsProductBuyback>, ProxySelf<HlsCusAbsProductBuybackService> {


    /**
     * 查询
     * @param iRequest
     * @param productBuyback
     * @param page
     * @param pageSize
     * @return
     */
    List<HlsCusAbsProductBuyback> selectProductBuybackData(IRequest iRequest, HlsCusAbsProductBuyback productBuyback, int page, int pageSize);


    /**
     * 回购收款确认
     * @param request
     * @param fundTransferList
     * @throws HlsCusException
     */
    void confirmReceiverProductBuyback(IRequest request, HlsCusFundTransferList fundTransferList) throws HlsCusException;
}