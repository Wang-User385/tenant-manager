package com.hand.hls.hls.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.hls.dto.HlsDurationHd;
import com.hand.hls.hls.dto.HlsDurationLn;
import com.hand.hls.utils.ResMessageException;

import java.util.Date;
import java.util.List;

public interface HlsDurationLnService extends IBaseService<HlsDurationLn>, ProxySelf<HlsDurationLnService>{

    List<HlsDurationLn> hlsDurationLnItemDetailQuery(HlsDurationLn ln);

    List<HlsDurationLn> hlsDurationLnWarrantDetailQuery(HlsDurationLn ln);
    //根据变更日期回写其他参数
    List<HlsDurationLn> hlsLnChangeDateUpdate(IRequest iRequest, HlsDurationLn ln) throws ResMessageException;
    void executeCheck(Long contractId, Date ChangeDate) throws ResMessageException;

}