package com.hand.hls.bp.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.bp.dto.BpMasterVisitRecord;
import com.hand.hls.bp.service.IBpMasterVisitRecordService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class BpMasterVisitRecordServiceImpl extends BaseServiceImpl<BpMasterVisitRecord> implements IBpMasterVisitRecordService{

}