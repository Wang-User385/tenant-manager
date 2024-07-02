package com.hand.hls.ecif.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.ecif.dto.HlsCusEcifBpMasterChange;

import java.util.List;

public interface HlsCusEcifBpMasterChangeService extends IBaseService<HlsCusEcifBpMasterChange>, ProxySelf<HlsCusEcifBpMasterChangeService>{

    HlsCusEcifBpMasterChange ecifBpMasterChange(IRequest iRequest, HlsCusEcifBpMasterChange dto);



    HlsCusEcifBpMasterChange ecifBpMasterSubmitWfl(IRequest iRequest, HlsCusEcifBpMasterChange dto);

    HlsCusEcifBpMasterChange ecifBpMasterChangeCancel(IRequest iRequest, HlsCusEcifBpMasterChange dto);

    List<HlsCusEcifBpMasterChange> ecifHistoryQuery(HlsCusEcifBpMasterChange dto, int page, int pagesize);
}