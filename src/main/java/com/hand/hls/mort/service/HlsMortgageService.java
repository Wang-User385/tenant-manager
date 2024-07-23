package com.hand.hls.mort.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.mort.dto.HlsMortgage;

import java.util.List;

public interface HlsMortgageService extends IBaseService<HlsMortgage>, ProxySelf<HlsMortgageService>{

    List<HlsMortgage> save(IRequest iRequest, HlsMortgage hlsMortgage);

    List<HlsMortgage> submitWfl(HlsMortgage dto, IRequest requestCtx);
}