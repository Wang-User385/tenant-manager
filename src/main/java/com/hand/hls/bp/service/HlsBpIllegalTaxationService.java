package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.HlsBpIllegalTaxation;

import java.util.List;

public interface HlsBpIllegalTaxationService extends IBaseService<HlsBpIllegalTaxation>, ProxySelf<HlsBpIllegalTaxationService>{

    List<HlsBpIllegalTaxation> selectAll(IRequest iRequest, HlsBpIllegalTaxation hlsBpIllegalTaxation, int page, int pageSize);
}