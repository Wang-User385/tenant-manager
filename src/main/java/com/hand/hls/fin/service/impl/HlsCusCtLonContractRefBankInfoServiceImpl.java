package com.hand.hls.fin.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fin.dto.HlsCusCtLonContractRefBankInfo;

import com.hand.hls.fin.service.HlsCusCtLonContractRefBankInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor=Exception.class)
public class HlsCusCtLonContractRefBankInfoServiceImpl extends BaseServiceImpl<HlsCusCtLonContractRefBankInfo> implements HlsCusCtLonContractRefBankInfoService {

}
