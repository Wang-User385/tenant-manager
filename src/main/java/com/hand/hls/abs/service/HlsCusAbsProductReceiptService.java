package com.hand.hls.abs.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.abs.dto.HlsCusAbsProductReceipt;

import java.util.List;

public interface HlsCusAbsProductReceiptService extends IBaseService<HlsCusAbsProductReceipt>, ProxySelf<HlsCusAbsProductReceiptService> {


    /**
     * 查询
     * @param iRequest
     * @param productReceipt
     * @param page
     * @param pageSize
     * @return
     */
    List<HlsCusAbsProductReceipt> selectProductReceiptData(IRequest iRequest, HlsCusAbsProductReceipt productReceipt, int page, int pageSize);

}