package com.hand.hls.cont.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cont.dto.HlsCusConContractRentPaymentConfirm;

import java.util.List;

public interface HlsCusConContractRentPaymentConfirmMapper extends Mapper<HlsCusConContractRentPaymentConfirm>{
    /**
     * 查询租金支付详情
     * @param var
     * @return
     */
    List<HlsCusConContractRentPaymentConfirm> queryConContractRentPaymentConfirm(HlsCusConContractRentPaymentConfirm var);
    List<HlsCusConContractRentPaymentConfirm> queryCheckConfirm(HlsCusConContractRentPaymentConfirm var);
}