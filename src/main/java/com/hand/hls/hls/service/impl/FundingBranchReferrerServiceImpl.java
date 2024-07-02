package com.hand.hls.hls.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.hls.dto.FundingBranchReferrer;
import com.hand.hls.hls.service.IFundingBranchReferrerService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class FundingBranchReferrerServiceImpl extends BaseServiceImpl<FundingBranchReferrer> implements IFundingBranchReferrerService{

}