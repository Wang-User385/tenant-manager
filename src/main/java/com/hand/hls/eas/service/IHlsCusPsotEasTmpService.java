package com.hand.hls.eas.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.eas.dto.HlsCusPsotEasTmp;

public interface IHlsCusPsotEasTmpService extends IBaseService<HlsCusPsotEasTmp>, ProxySelf<IHlsCusPsotEasTmpService>{

    HlsCusPsotEasTmp gldPost(IRequest iRequest, HlsCusPsotEasTmp dto);

    HlsCusPsotEasTmp gldPostDelete(IRequest iRequest, HlsCusPsotEasTmp dto);

}