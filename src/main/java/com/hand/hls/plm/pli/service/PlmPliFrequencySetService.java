package com.hand.hls.plm.pli.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.plm.pli.dto.PlmPliFrequencySet;

import java.util.List;

public interface PlmPliFrequencySetService extends IBaseService<PlmPliFrequencySet>, ProxySelf<PlmPliFrequencySetService> {

    ResponseData batchUpdate2(IRequest iRequest, List<PlmPliFrequencySet> dtos);

}
