package com.hand.hls.partner.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.partner.dto.YLCshTransferPaymentDto;

import java.util.List;


public interface IYLCshTransferPaymentService extends IBaseService<YLCshTransferPaymentDto>, ProxySelf<IYLCshTransferPaymentService> {

    List<YLCshTransferPaymentDto> updateTransferStatus(IRequest requestCtx , List<YLCshTransferPaymentDto> list);

    List<YLCshTransferPaymentDto> updateAndVerification( IRequest requestCtx , List<YLCshTransferPaymentDto> ylCshTransferPaymentDtoList);

}