package com.hand.hls.prj.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.prj.dto.HlsCusPrjProjectMailInfo;
import com.hand.hls.prj.service.IHlsCusPrjProjectMailInfoService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjProjectMailInfoServiceImpl extends BaseServiceImpl<HlsCusPrjProjectMailInfo> implements IHlsCusPrjProjectMailInfoService{

}