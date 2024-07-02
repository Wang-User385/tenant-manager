package com.hand.hls.hls.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.hls.dto.FundingPlanAttachment;
import com.hand.hls.hls.service.IFundingPlanAttachmentService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class FundingPlanAttachmentServiceImpl extends BaseServiceImpl<FundingPlanAttachment> implements IFundingPlanAttachmentService{

}