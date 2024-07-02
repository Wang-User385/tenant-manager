package com.hand.hls.prj.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.prj.dto.HlsCusPrjProjectSupplement;
import com.hand.hls.prj.service.IHlsCusPrjProjectSupplementService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjProjectSupplementServiceImpl extends BaseServiceImpl<HlsCusPrjProjectSupplement> implements IHlsCusPrjProjectSupplementService{

}