package com.hand.hls.bill.service;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bill.mapper.hlsPaymentDischargeMapper;
import com.hand.hls.bill.dto.hlsPaymentDischarge;

import java.util.List;

public interface IhlsPaymentDischargeService extends IBaseService<hlsPaymentDischarge>, ProxySelf<IhlsPaymentDischargeService>{

    List<hlsPaymentDischarge> lonCreditBpLovQuery(hlsPaymentDischarge hlsPaymentDischarge, int page, int pagesize);
    void sendEmail(IRequest iRequest, hlsPaymentDischarge hlsPaymentDischarge);

}