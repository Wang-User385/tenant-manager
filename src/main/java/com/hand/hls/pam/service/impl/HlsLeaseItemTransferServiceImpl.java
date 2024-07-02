package com.hand.hls.pam.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.pam.dto.HlsLeaseItemTransfer;
import com.hand.hls.pam.service.IHlsLeaseItemTransferService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsLeaseItemTransferServiceImpl extends BaseServiceImpl<HlsLeaseItemTransfer> implements IHlsLeaseItemTransferService{

}