package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.HlsBpOutInvestment;

import java.util.List;

public interface HlsBpOutInvestmentService extends IBaseService<HlsBpOutInvestment>, ProxySelf<HlsBpOutInvestmentService>{

    List<HlsBpOutInvestment> selectAll(IRequest requestContext,HlsBpOutInvestment hlsBpOutInvestment,int page, int pagesize);

}