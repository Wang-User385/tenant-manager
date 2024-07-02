package com.hand.hls.csh.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.csh.dto.HlsCusDepositRefund;
import hls.core.utils.exception.HlsCusException;

import java.util.List;

public interface HlsCusDepositRefundService extends IBaseService<HlsCusDepositRefund>, ProxySelf<HlsCusDepositRefundService> {


    List<HlsCusDepositRefund> selectDepositRefundData(IRequest iRequest, HlsCusDepositRefund hlsCusDepositRefund, int page, int pageSize);


    void approvalDepositRefund(IRequest iRequest, HlsCusDepositRefund hlsCusDepositRefund) throws HlsCusException;
}