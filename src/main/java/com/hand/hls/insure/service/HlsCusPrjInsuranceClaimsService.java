package com.hand.hls.insure.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.insure.dto.HlsCusPrjInsuranceClaims;

import java.util.List;

public interface HlsCusPrjInsuranceClaimsService extends IBaseService<HlsCusPrjInsuranceClaims>, ProxySelf<HlsCusPrjInsuranceClaimsService> {
    List<HlsCusPrjInsuranceClaims> queryAllFile(IRequest request, HlsCusPrjInsuranceClaims dto);
}