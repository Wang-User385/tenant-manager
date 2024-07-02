package com.hand.hls.wfl.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.wfl.dto.HlsCusWflAppointApprover;


public interface WflAppointApproverService extends IBaseService<HlsCusWflAppointApprover>, ProxySelf<WflAppointApproverService> {

    void wxInsertAppint(IRequest iRequest, String processInstanceId, String designatedAllocationId , String currentSidCode, String businessKey, Long  userId);
}