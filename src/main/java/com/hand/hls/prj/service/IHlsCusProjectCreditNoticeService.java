package com.hand.hls.prj.service;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.prj.dto.HlsCusProjectCreditNotice;

import java.util.List;

public interface IHlsCusProjectCreditNoticeService extends IBaseService<HlsCusProjectCreditNotice>, ProxySelf<IHlsCusProjectCreditNoticeService>{
    List<HlsCusProjectCreditNotice> QueryAllByInstanceId(HlsCusProjectCreditNotice hlsCusProjectCreditNotice);
}