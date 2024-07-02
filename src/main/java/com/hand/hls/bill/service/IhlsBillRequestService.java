package com.hand.hls.bill.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bill.dto.hlsBillRequest;
import com.hand.hls.utils.ResMessageException;
import leaf.service.validation.ParameterNullException;

import java.util.List;

public interface IhlsBillRequestService extends IBaseService<hlsBillRequest>, ProxySelf<IhlsBillRequestService>{
    /**
     * 提交
     * @param iRequest
     * @param hlsBillRequest
     * @return
     * @throws
     */
    List<hlsBillRequest> insureSubmit(IRequest iRequest, hlsBillRequest hlsBillRequest) throws ResMessageException, ParameterNullException;

    void bpAssetsInit(IRequest iRequest, hlsBillRequest hlsCusPrjProjectBp);

}