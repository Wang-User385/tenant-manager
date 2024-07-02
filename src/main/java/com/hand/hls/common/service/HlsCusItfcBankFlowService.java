package com.hand.hls.common.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.common.dto.HlsCusItfcBankFlow;

import java.util.List;

public interface HlsCusItfcBankFlowService extends IBaseService<HlsCusItfcBankFlow>, ProxySelf<HlsCusItfcBankFlowService>{
    public void dealRivalAccount(IRequest iRequest, List<HlsCusItfcBankFlow> list);
}