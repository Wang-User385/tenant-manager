package com.hand.hls.cont.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.cont.dto.ContentNumberHead;
import hls.core.utils.exception.HlsCusException;

public interface IContentNumberHeadService extends IBaseService<ContentNumberHead>, ProxySelf<IContentNumberHeadService>{

    void submitWfl(IRequest iRequest,ContentNumberHead contentNumberHead) throws HlsCusException;

}