package com.hand.hls.cont.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cont.dto.ConContract;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractBp;

import java.util.List;
import java.util.Map;

public interface HlsCusConContractBpMapper extends Mapper<HlsCusConContractBp> {

    List<Map> queryByContractId(HlsCusConContractBp bp);

    List<HlsCusConContractBp> queryContractBpLovModify(HlsCusConContractBp bp);

    /**
     * 二期功能：付款申请创建-付款对象银行信息Lov
     * @param bp
     * @return
     */
    List<HlsCusConContractBp> queryPaymentBpBankInfoLov(HlsCusConContractBp bp);

    List<HlsCusConContractBp> selectSignBpLov(HlsCusConContractBp bp);

    List<HlsCusConContractBp> queryContractTenant(HlsCusConContractBp bp);

    List<HlsCusConContractBp> queryContractGuarantor(HlsCusConContractBp bp);

    List<HlsCusConContractBp> queryContractBpLov(HlsCusConContractBp bp);


    List<ConContract> queryByContractNumber(ConContract bp);

    List<HlsCusConContractBp> queryContractBp(Map bp);

    List<HlsCusConContractBp> queryContractBpDetails(HlsCusConContractBp bp);

    List<HlsCusConContractBp> queryBpByContractId(HlsCusConContract bp);


    List<HlsCusConContractBp> queryContractBpLovNoGuarantor(HlsCusConContractBp bp);

    List<HlsCusConContractBp> queryDistinctBpLovNoGuarantor(HlsCusConContractBp bp);


    //保理合同放款明细客户信息
    List<HlsCusConContractBp> queryConFactoringLoanBpInfo(HlsCusConContractBp bp);
}