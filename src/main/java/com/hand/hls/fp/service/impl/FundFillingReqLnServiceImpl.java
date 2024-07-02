package com.hand.hls.fp.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.fp.dto.FundFillingReqLn;
import com.hand.hls.fp.service.FundFillingReqLnService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class FundFillingReqLnServiceImpl extends BaseServiceImpl<FundFillingReqLn> implements FundFillingReqLnService {

}