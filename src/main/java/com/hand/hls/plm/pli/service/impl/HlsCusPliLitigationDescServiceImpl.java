package com.hand.hls.plm.pli.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.plm.pli.dto.HlsCusPliLitigationDesc;
import com.hand.hls.plm.pli.service.HlsCusPliLitigationDescService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPliLitigationDescServiceImpl extends BaseServiceImpl<HlsCusPliLitigationDesc> implements HlsCusPliLitigationDescService {

}