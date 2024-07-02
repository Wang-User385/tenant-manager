package com.hand.hls.cont.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.cont.dto.HlsCusConContractMortgage;

import java.util.List;

public interface HlsCusConContractMortgageService extends IBaseService<HlsCusConContractMortgage>, ProxySelf<HlsCusConContractMortgageService> {
    /**
     * 二期功能：复制项目上的抵押物到合同相关的表上
     * @param iRequest
     * @param contractId
     * @param projectId
     * @return
     */
    List<HlsCusConContractMortgage> saveMortgageFromPrj(IRequest iRequest, Long contractId, Long projectId);

}