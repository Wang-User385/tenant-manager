package com.hand.hls.pam.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.pam.dto.AssetsDisposalCost;
import com.hand.hls.pam.service.IAssetsDisposalCostService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class AssetsDisposalCostServiceImpl extends BaseServiceImpl<AssetsDisposalCost> implements IAssetsDisposalCostService{

}