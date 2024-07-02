package com.hand.hls.gld.service;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @Description:
 * @author: congweijing
 * @date: 2021/5/6 10:50
 */
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.gld.dto.GldContractCashflow;

public interface IGldContractCashflowService extends IBaseService<GldContractCashflow>, ProxySelf<IGldContractCashflowService> {
    /**
     * 租赁合同分摊
     * @param var1
     * @param contractId
     */
    void clacFinanceIncome(IRequest var1, Long contractId, Double vatRate, Double xirr);

    /**
     * 零售租赁合同分摊
     * @param var1
     * @param contractId
     */
    void clacFinanceIncomeRetail(IRequest var1, Long contractId, Double vatRate, Double xirr);
}
