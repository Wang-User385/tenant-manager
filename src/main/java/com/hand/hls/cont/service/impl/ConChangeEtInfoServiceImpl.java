package com.hand.hls.cont.service.impl;


import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.dto.ConChangeEtInfo;
import com.hand.hls.cont.service.ConChangeEtInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class ConChangeEtInfoServiceImpl extends BaseServiceImpl<ConChangeEtInfo> implements ConChangeEtInfoService {


}
