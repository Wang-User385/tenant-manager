package com.hand.hls.lease.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.lease.dto.LeaseItemInsurance;
import com.hand.hls.lease.service.ILeaseItemInsuranceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @Description：
 * @Author：liangxian.chen@hand-china.com
 * @Date：2022/12/20 16:49
 * @Version：1.0
 */
@Service
public class LeaseItemInsuranceServiceImpl extends BaseServiceImpl<LeaseItemInsurance> implements ILeaseItemInsuranceService {
    private Logger logger = LoggerFactory.getLogger(LeaseItemInsuranceServiceImpl.class);
}
