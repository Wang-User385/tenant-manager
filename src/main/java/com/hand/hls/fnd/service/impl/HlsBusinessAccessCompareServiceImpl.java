package com.hand.hls.fnd.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fnd.dto.HlsBusinessAccessCompare;
import org.springframework.stereotype.Service;
import com.hand.hls.fnd.dto.HlsBusinessAccessCompare;
import com.hand.hls.fnd.service.IHlsBusinessAccessCompareService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsBusinessAccessCompareServiceImpl extends BaseServiceImpl<HlsBusinessAccessCompare> implements IHlsBusinessAccessCompareService{

}