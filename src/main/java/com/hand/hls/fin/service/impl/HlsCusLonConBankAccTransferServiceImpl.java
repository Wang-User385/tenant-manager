package com.hand.hls.fin.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fin.dto.HlsCusLonConBankAccTransfer;
import com.hand.hls.fin.service.IHlsCusLonConBankAccTransferService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusLonConBankAccTransferServiceImpl extends BaseServiceImpl<HlsCusLonConBankAccTransfer> implements IHlsCusLonConBankAccTransferService {

}