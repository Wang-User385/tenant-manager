package com.hand.hls.plm.fc.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.plm.fc.dto.PlmFiveClassifyMeet;
import com.hand.hls.plm.fc.service.PlmFiveClassifyMeetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class PlmFiveClassifyMeetServiceImpl extends BaseServiceImpl<PlmFiveClassifyMeet> implements PlmFiveClassifyMeetService {

}