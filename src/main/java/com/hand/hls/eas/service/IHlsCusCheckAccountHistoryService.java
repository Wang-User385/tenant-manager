package com.hand.hls.eas.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.eas.dto.HlsCusCheckAccountHistory;

public interface IHlsCusCheckAccountHistoryService extends IBaseService<HlsCusCheckAccountHistory>, ProxySelf<IHlsCusCheckAccountHistoryService>{

    HlsCusCheckAccountHistory checkAccountDataPost(IRequest iRequest,HlsCusCheckAccountHistory dto);
}