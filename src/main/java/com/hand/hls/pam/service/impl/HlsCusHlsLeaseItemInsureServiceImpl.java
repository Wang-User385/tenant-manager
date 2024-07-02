package com.hand.hls.pam.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.pam.dto.HlsCusHlsLeaseItemInsure;
import org.springframework.stereotype.Service;
import com.hand.hls.pam.dto.HlsLeaseItemInsure;
import com.hand.hls.pam.service.HlsCusHlsLeaseItemInsureService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusHlsLeaseItemInsureServiceImpl extends BaseServiceImpl<HlsCusHlsLeaseItemInsure> implements HlsCusHlsLeaseItemInsureService {

}