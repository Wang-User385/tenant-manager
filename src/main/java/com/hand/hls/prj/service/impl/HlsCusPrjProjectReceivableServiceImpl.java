package com.hand.hls.prj.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.prj.dto.HlsCusPrjProjectReceivable;
import com.hand.hls.prj.service.HlsCusPrjIProjectReceivableService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjProjectReceivableServiceImpl extends BaseServiceImpl<HlsCusPrjProjectReceivable> implements HlsCusPrjIProjectReceivableService {

}