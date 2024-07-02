package com.hand.hls.prj.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.prj.dto.HlsCusPrjBpLiabilities;
import com.hand.hls.prj.service.HlsCusPrjBpLiabilitiesService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjBpLiabilitiesServiceImpl extends BaseServiceImpl<HlsCusPrjBpLiabilities> implements HlsCusPrjBpLiabilitiesService {

}