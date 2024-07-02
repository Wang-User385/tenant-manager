package com.hand.hls.gld.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.gld.dto.GldCostAccountAdjust;
import com.hand.hls.gld.service.GldCostAccountAdjustService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class GldCostAccountAdjustServiceImpl extends BaseServiceImpl<GldCostAccountAdjust> implements GldCostAccountAdjustService{

}