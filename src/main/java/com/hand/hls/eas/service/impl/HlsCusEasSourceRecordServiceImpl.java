package com.hand.hls.eas.service.impl;


import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.eas.dto.HlsCusEasSourceRecord;
import com.hand.hls.eas.service.IHlsCusEasSourceRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusEasSourceRecordServiceImpl extends BaseServiceImpl<HlsCusEasSourceRecord> implements IHlsCusEasSourceRecordService {

}