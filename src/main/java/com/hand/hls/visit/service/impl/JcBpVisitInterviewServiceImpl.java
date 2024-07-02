package com.hand.hls.visit.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.visit.dto.JcBpVisitInterview;
import com.hand.hls.visit.service.IJcBpVisitInterviewService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class JcBpVisitInterviewServiceImpl extends BaseServiceImpl<JcBpVisitInterview> implements IJcBpVisitInterviewService{

}