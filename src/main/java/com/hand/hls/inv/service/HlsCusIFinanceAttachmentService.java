package com.hand.hls.inv.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.inv.dto.HlsCusFinanceAttachment;

import java.util.List;

public interface HlsCusIFinanceAttachmentService extends IBaseService<HlsCusFinanceAttachment>, ProxySelf<HlsCusIFinanceAttachmentService> {

    List<HlsCusFinanceAttachment> purchaseAttachmentSubmitWfl(IRequest iRequest, HlsCusFinanceAttachment hlsCusFinanceAttachment);

    List<HlsCusFinanceAttachment> invAttachmentDetailQuery(IRequest iRequest, HlsCusFinanceAttachment hlsCusFinanceAttachment, int page, int pageSize);


}