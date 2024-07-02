package com.hand.hls.abs.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.abs.dto.HlsCusAbsProduct;
import com.hand.hls.exception.HlsCusException;

import java.util.List;

public interface AbsProductService extends IBaseService<HlsCusAbsProduct>, ProxySelf<AbsProductService> {
    List<HlsCusAbsProduct> createProduct(Long projectId, String productShortName, String productName, String productNumber, IRequest request);

    void calcProduct(IRequest request, Long productId) throws HlsCusException;

    /**
     * abs 分摊
     *
     * @param request
     * @param hlsCusAbsProduct
     * @return
     */
    void absProductIncome(IRequest request, HlsCusAbsProduct hlsCusAbsProduct) throws HlsCusException;

    /**
     * ABS产品确认
     */
    void submitProduct(IRequest request, Long productId) throws HlsCusException;

    /**
     * 清仓回购提交
     */
    HlsCusAbsProduct submitBuyBackData(IRequest request, Long productId) throws HlsCusException;

    /**
     * ABS产品归集行重算
     */
    HlsCusAbsProduct reCalcCashProduct(IRequest request, HlsCusAbsProduct hlsCusAbsProduct, String dataClass);
}
