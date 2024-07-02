package com.hand.hls.eas.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.eas.dto.HlsCusCityData;
import com.hand.hls.eas.service.IHlsCusCityDataService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusCityDataServiceImpl extends BaseServiceImpl<HlsCusCityData> implements IHlsCusCityDataService{

}