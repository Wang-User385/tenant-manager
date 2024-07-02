package com.hand.hls.pam.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.pam.dto.LeaseAssetLn;
import com.hand.hls.pam.service.ILeaseAssetLnService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class LeaseAssetLnServiceImpl extends BaseServiceImpl<LeaseAssetLn> implements ILeaseAssetLnService{


}