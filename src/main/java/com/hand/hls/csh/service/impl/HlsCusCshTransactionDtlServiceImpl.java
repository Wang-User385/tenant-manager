package com.hand.hls.csh.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.csh.dto.HlsCusCshTransactionDtl;
import com.hand.hls.csh.service.HlsCusCshTransactionDtlService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusCshTransactionDtlServiceImpl extends BaseServiceImpl<HlsCusCshTransactionDtl> implements HlsCusCshTransactionDtlService{

}