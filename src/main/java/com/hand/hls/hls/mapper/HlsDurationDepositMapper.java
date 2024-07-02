package com.hand.hls.hls.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.hls.dto.HlsDurationDeposit;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HlsDurationDepositMapper extends Mapper<HlsDurationDeposit> {
    List<HlsDurationDeposit> hlsDurationDepositDetailQuery(HlsDurationDeposit hlsDurationDeposit);

    List<HlsDurationDeposit> depositManagementQuery(HlsDurationDeposit hlsDurationDeposit);

    Double getDepositResidualAmountByProjectId(@Param("projectId")  Long id);
    Double getDepositResidualAmountByContractId(@Param("contractId") Long id);
}
