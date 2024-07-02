package com.hand.hls.vat.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.vat.dto.HandoverApplication;

import java.util.List;

public interface IHandoverApplicationService extends IBaseService<HandoverApplication>, ProxySelf<IHandoverApplicationService>{
    void newCreate(IRequest iRequest, HandoverApplication handoverapplication);
    List<HandoverApplication> updateHandoverStatus(IRequest iRequest , List<HandoverApplication> handoverapplicationlist  );
    List<HandoverApplication> updateHandoverConfirmStatus(IRequest iRequest , List<HandoverApplication> handoverapplicationlist  );
    List<HandoverApplication> updateHandoverRejectStatus(IRequest iRequest , List<HandoverApplication> handoverapplicationlist  );

}