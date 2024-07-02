package com.hand.hls.credit.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.credit.dto.HlsCusConCreditWhiteList;
import com.hand.hls.credit.service.HlsCusConCreditWhiteListService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusConCreditWhiteListServiceImpl extends BaseServiceImpl<HlsCusConCreditWhiteList> implements HlsCusConCreditWhiteListService{

}