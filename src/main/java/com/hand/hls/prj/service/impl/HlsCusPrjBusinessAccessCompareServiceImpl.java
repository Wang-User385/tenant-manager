package com.hand.hls.prj.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.prj.dto.HlsCusPrjBusinessAccessCompare;
import com.hand.hls.prj.service.HlsCusPrjIBusinessAccessCompareService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjBusinessAccessCompareServiceImpl extends BaseServiceImpl<HlsCusPrjBusinessAccessCompare> implements HlsCusPrjIBusinessAccessCompareService {

}