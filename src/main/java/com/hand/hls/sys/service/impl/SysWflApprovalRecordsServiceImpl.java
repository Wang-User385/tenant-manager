package com.hand.hls.sys.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.sys.dto.SysWflApprovalRecords;
import com.hand.hls.sys.service.SysWflApprovalRecordsService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class SysWflApprovalRecordsServiceImpl extends BaseServiceImpl<SysWflApprovalRecords> implements SysWflApprovalRecordsService{

}