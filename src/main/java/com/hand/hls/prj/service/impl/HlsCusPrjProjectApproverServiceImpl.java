package com.hand.hls.prj.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.prj.dto.HlsCusPrjProjectApprover;
import com.hand.hls.prj.service.HlsCusPrjIProjectApproverService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjProjectApproverServiceImpl extends BaseServiceImpl<HlsCusPrjProjectApprover> implements HlsCusPrjIProjectApproverService {

}