package com.hand.hls.prj.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.prj.dto.HlsCusPrjQuotationElements;

public interface IHlsCusQuatationElementsService extends IBaseService<HlsCusPrjQuotationElements>, ProxySelf<IHlsCusQuatationElementsService>{

     String getQuatationElementChangeInfo(IRequest iRequest, Long currentProjectId, Long historyProjectId);

     String getRentalPlanChangeInfo(IRequest iRequest , Long currentProjectId, Long historyProjectId );


}