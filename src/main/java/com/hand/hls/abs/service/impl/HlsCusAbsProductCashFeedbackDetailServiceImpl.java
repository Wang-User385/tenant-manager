package com.hand.hls.abs.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.abs.dto.HlsCusAbsProductCashFeedbackDetail;
import com.hand.hls.abs.service.HlsCusAbsProductCashFeedbackDetailService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusAbsProductCashFeedbackDetailServiceImpl extends BaseServiceImpl<HlsCusAbsProductCashFeedbackDetail> implements HlsCusAbsProductCashFeedbackDetailService{

}