package com.hand.hls.prj.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.prj.dto.HlsCusPrjProjectLease;
import com.hand.hls.prj.service.HlsCusPrjIProjectLeaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjProjectLeaseServiceImpl extends BaseServiceImpl<HlsCusPrjProjectLease> implements HlsCusPrjIProjectLeaseService {

}