package com.hand.hls.fin.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fin.dto.HlsCusLonContractBail;
import com.hand.hls.fin.service.HlsCusLonContractBailService;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusLonContractBailServiceImpl extends BaseServiceImpl<HlsCusLonContractBail> implements HlsCusLonContractBailService {

}