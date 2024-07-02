package com.hand.hls.fin.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fin.dto.HlsCusLonContract;
import com.hand.hls.fin.dto.HlsCusLonContractGuarantor;

import java.util.List;

public interface IHlsCusLonContractGuarantorService extends IBaseService<HlsCusLonContractGuarantor>, ProxySelf<IHlsCusLonContractGuarantorService>{

    List<HlsCusLonContract> selectCompanyByBpId(IRequest requestContext, HlsCusLonContractGuarantor lonContractGuarantor);
}