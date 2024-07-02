package com.hand.hls.pam.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.pam.dto.AssetsDisposalAttch;
import com.hand.hls.pam.service.IAssetsDisposalAttchService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class AssetsDisposalAttchServiceImpl extends BaseServiceImpl<AssetsDisposalAttch> implements IAssetsDisposalAttchService{

}