package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.HlsCusBpShareholderInformation;

import java.util.List;

public interface HlsCusIBpShareholderInformationService extends IBaseService<HlsCusBpShareholderInformation>, ProxySelf<HlsCusIBpShareholderInformationService> {

    List<HlsCusBpShareholderInformation> selectAll(IRequest requestContext, HlsCusBpShareholderInformation bpShareholderInformation, int page, int pagesize);

}