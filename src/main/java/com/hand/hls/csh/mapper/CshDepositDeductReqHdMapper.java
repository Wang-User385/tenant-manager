package com.hand.hls.csh.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.csh.dto.CshDepositDeductReqHd;

import java.util.List;

public interface CshDepositDeductReqHdMapper extends Mapper<CshDepositDeductReqHd> {

    CshDepositDeductReqHd selectCshDepositDeductReqHdInit(CshDepositDeductReqHd cshDepositDeductReqHd);

    CshDepositDeductReqHd selectCshDepositDeductReqHdInitWithBlockAmt(CshDepositDeductReqHd cshDepositDeductReqHd);

    CshDepositDeductReqHd selectCshDepositDeductReqHd(CshDepositDeductReqHd cshDepositDeductReqHd);

    List<CshDepositDeductReqHd> selectCshDepositDeductReqHdList(CshDepositDeductReqHd cshDepositDeductReqHd);
}