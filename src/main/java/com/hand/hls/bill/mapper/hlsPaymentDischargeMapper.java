package com.hand.hls.bill.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bill.dto.hlsPaymentDischarge;

import java.util.List;

public interface hlsPaymentDischargeMapper extends Mapper<hlsPaymentDischarge>{
    List<hlsPaymentDischarge>lonCreditBpLovQuery(hlsPaymentDischarge hlsCusBpMaster);


}