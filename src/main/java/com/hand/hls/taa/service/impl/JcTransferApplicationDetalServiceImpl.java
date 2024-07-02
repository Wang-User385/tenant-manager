package com.hand.hls.taa.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.taa.service.IJcTransferApplicationDetalService;
import org.springframework.stereotype.Service;
import com.hand.hls.taa.dto.JcTransferApplicationDetal;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class JcTransferApplicationDetalServiceImpl extends BaseServiceImpl<JcTransferApplicationDetal> implements IJcTransferApplicationDetalService {

}