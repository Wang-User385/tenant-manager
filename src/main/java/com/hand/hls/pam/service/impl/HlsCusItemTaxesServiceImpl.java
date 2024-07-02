package com.hand.hls.pam.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.pam.dto.HlsCusLeaseItemTaxes;
import com.hand.hls.pam.service.HlsCusItemTaxesService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Qian Yuanfeng
 * @date 2020/4/16 - 10:49
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusItemTaxesServiceImpl extends BaseServiceImpl<HlsCusLeaseItemTaxes> implements HlsCusItemTaxesService {

}
