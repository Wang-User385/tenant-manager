package com.hand.hls.csh.mapper;

import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface HlsCusCshWriteOffMapper extends CshWriteOffMapper<HlsCusCshWriteOff> {

    /**
     * 未被反冲数
     *
     * @param cshTransactionId
     * @return
     */
    int selectNoReverseCount(@Param("cshTransactionId") Long cshTransactionId);

    List<Map> selectAllWriteOff(Map map);

    List<HlsCusCshWriteOff> selectAllCancelAfterVerificationDetail(HlsCusCshWriteOff hlsCusCshWriteOff);

    List<HlsCusCshWriteOff> selectPenaltyWriteOffDay(HlsCusCshWriteOff writeOff);


    HlsCusCshWriteOff queryByCfItem(HlsCusCshWriteOff writeOff);

    List<HlsCusCshWriteOff> queryWriteOffByFinance(HlsCusCshWriteOff cusCshWriteOff);

    List<HlsCusCshWriteOff> writeOffDetailQueryReport(HlsCusCshWriteOff cusCshWriteOff);

    List<HlsCusCshWriteOff> selectAdvancedWriteOff(HlsCusCshWriteOff cusCshWriteOff);

    List<HlsCusCshWriteOff> selectCashflowTrans(HlsCusCshWriteOff cusCshWriteOff);
}
