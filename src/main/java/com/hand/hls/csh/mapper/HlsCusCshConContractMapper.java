package com.hand.hls.csh.mapper;

import com.hand.hls.csh.dto.HlsCusCshConContract;

import java.util.List;
import java.util.Map;


public interface HlsCusCshConContractMapper extends CshConContractMapper<HlsCusCshConContract> {

    List<HlsCusCshConContract> contractHomeSecondQuery(Map<String, Object> map);

    List<HlsCusCshConContract> cshPaymentTodo(Map<String, Object> map);

    List<HlsCusCshConContract> cshPaymentDetailByCon(Map<String, Object> map);

    List<HlsCusCshConContract> contractHomeThirdQuery(Map<String, Object> map);

    List<HlsCusCshConContract> csh001PaymentTodo(Map<String, Object> map);

}
