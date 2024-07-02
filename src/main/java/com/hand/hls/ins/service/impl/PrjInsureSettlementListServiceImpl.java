package com.hand.hls.ins.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.ins.dto.PrjInsureSettlementList;
import com.hand.hls.ins.service.PrjInsureSettlementListService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class PrjInsureSettlementListServiceImpl extends BaseServiceImpl<PrjInsureSettlementList> implements PrjInsureSettlementListService{

}