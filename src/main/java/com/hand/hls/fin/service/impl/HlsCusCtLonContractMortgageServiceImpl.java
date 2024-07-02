package com.hand.hls.fin.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fin.dto.HlsCusCtLonContractMortgage;
import com.hand.hls.fin.service.HlsCusCtLonContractMortgageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor=Exception.class)
public class HlsCusCtLonContractMortgageServiceImpl extends BaseServiceImpl<HlsCusCtLonContractMortgage> implements HlsCusCtLonContractMortgageService {


}
