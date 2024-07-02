package com.hand.hls.fin.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fin.dto.HlsCusLonContractPurpose;

import java.util.List;

public interface HlsCusLonContractPurposeService extends IBaseService<HlsCusLonContractPurpose>, ProxySelf<HlsCusLonContractPurposeService> {

    List<HlsCusLonContractPurpose> selectLonContractPur(IRequest request, HlsCusLonContractPurpose lonContractPurpose, int page, int pageSize);



    List<HlsCusLonContractPurpose> batchUpdatePurpose(IRequest request, List<HlsCusLonContractPurpose> purposeList)  throws HlsCusException;

    /**
     * 金额汇总
     * @param withdrawId
     * @return
     */
    Double  selectPurposeSum(Long withdrawId);
}