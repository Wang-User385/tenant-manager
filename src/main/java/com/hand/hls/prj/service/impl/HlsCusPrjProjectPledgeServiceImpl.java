package com.hand.hls.prj.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.prj.dto.HlsCusPrjProjectPledge;
import com.hand.hls.prj.service.HlsCusPrjProjectPledgeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjProjectPledgeServiceImpl extends BaseServiceImpl<HlsCusPrjProjectPledge> implements HlsCusPrjProjectPledgeService {

}