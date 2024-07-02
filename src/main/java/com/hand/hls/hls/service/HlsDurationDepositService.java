package com.hand.hls.hls.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.hls.dto.HlsDurationDeposit;
import com.hand.hls.hls.dto.HlsDurationHd;
import com.hand.hls.utils.ResMessageException;

import java.util.List;

public interface HlsDurationDepositService extends IBaseService<HlsDurationDeposit>, ProxySelf<HlsDurationDepositService>{
    List<HlsDurationDeposit> depositManagementQuery (HlsDurationDeposit hlsDurationDeposit,int page,int pagesize);
    List<HlsDurationDeposit> submitDepositWfl(IRequest iRequest, List<HlsDurationDeposit> depositList) throws ResMessageException;
    List<HlsDurationDeposit> executeDeposit(IRequest iRequest, List<HlsDurationDeposit> depositList);
}