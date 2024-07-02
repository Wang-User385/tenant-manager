package com.hand.hls.fnd.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.fnd.dto.AdjustmentCu;
import com.hand.hls.fnd.service.AdjustmentCuService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class AdjustmentCuServiceImpl extends BaseServiceImpl<AdjustmentCu> implements AdjustmentCuService{

}