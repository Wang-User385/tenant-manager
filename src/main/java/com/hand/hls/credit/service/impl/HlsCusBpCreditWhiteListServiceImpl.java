package com.hand.hls.credit.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.credit.dto.HlsCusBpCreditWhiteList;
import com.hand.hls.credit.service.HlsCusBpCreditWhiteListService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusBpCreditWhiteListServiceImpl extends BaseServiceImpl<HlsCusBpCreditWhiteList> implements HlsCusBpCreditWhiteListService{

}