package com.hand.hls.fp.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.fp.dto.FundFillingReqDetail;
import com.hand.hls.fp.service.FundFillingReqDetailService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class FundFillingReqDetailServiceImpl extends BaseServiceImpl<FundFillingReqDetail> implements FundFillingReqDetailService{

}