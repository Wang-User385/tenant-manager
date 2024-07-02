package com.hand.hls.hls.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.hls.dto.HlsDurationCompare;
import com.hand.hls.hls.dto.HlsDurationLn;

public interface HlsDurationCompareService extends IBaseService<HlsDurationCompare>, ProxySelf<HlsDurationCompareService>{
    void createCompare(IRequest iRequest, HlsDurationLn ln);
}