package com.hand.hls.cont.service.impl;


import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.dto.ConChangeRepaymentInfo;
import com.hand.hls.cont.service.ConChangeRepaymentInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class ConChangeRepaymentInfoServiceImpl extends BaseServiceImpl<ConChangeRepaymentInfo> implements ConChangeRepaymentInfoService {


}
