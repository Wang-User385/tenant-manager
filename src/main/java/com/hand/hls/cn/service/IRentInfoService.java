package com.hand.hls.cn.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.cn.dto.RentInfo;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fin.dto.HlsCusLonContractWithdraw;
import com.hand.hls.prj.dto.HlsCusPrjProjectAttachment;

import java.util.List;

public interface IRentInfoService extends IBaseService<RentInfo>, ProxySelf<IRentInfoService>{
    void sendEmail(IRequest request, List<HlsCusPrjProjectAttachment> attachments)  throws HlsCusException, hls.core.utils.exception.HlsCusException;
}