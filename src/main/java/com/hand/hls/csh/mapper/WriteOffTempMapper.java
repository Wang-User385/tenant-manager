package com.hand.hls.csh.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.csh.dto.WriteOffTemp;

import java.util.List;

public interface WriteOffTempMapper extends Mapper<WriteOffTemp>{
    List<WriteOffTemp> selectDepositDeductionInfo(WriteOffTemp writeOffTemp);
    List<WriteOffTemp> selectDepositInfo(WriteOffTemp writeOffTemp);
    List<WriteOffTemp> selectDepositRefundInfo(WriteOffTemp writeOffTemp);
}