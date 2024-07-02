package com.hand.hls.pam.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.pam.dto.AssetsDisposalDetail;
import com.hand.hls.pam.service.IAssetsDisposalDetailService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class AssetsDisposalDetailServiceImpl extends BaseServiceImpl<AssetsDisposalDetail> implements IAssetsDisposalDetailService{

}