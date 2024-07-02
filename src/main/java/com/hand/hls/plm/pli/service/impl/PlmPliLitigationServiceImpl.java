package com.hand.hls.plm.pli.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.plm.pli.dto.PlmPliLitigation;
import com.hand.hls.plm.pli.service.PlmPliLitigationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class PlmPliLitigationServiceImpl extends BaseServiceImpl<PlmPliLitigation> implements PlmPliLitigationService {

}