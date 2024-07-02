package com.hand.hls.csh.mapper;


import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.csh.dto.HlsCusCshContractCashflow;

import java.util.List;
import java.util.Map;

public interface HlsCusCshContractCashflowMapper extends Mapper<HlsCusCshContractCashflow> {
    List<Map> selectNotFullContractCashflowHome(HlsCusCshContractCashflow hlsCusCshContractCashflow);

    List<Map> selectAllContractCashflowHome(HlsCusCshContractCashflow hlsCusCshContractCashflow);

    List<Map> selectRecoveryRate(HlsCusCshContractCashflow hlsCusCshContractCashflow);

    List<Map> selectUnitRecoveryRate(HlsCusCshContractCashflow hlsCusCshContractCashflow);

    List<HlsCusCshContractCashflow> selectUnitLov(HlsCusCshContractCashflow hlsCusCshContractCashflow);

    List<Map> selectOverdueContarctCashFlow(HlsCusConContractCashflow hlsCusConContractCashflow);
    List<HlsCusConContractCashflow> selectOverdueContarctCashFlow1(HlsCusConContract hlsCusConContract);

    List<Map> queryOverdueContractList(HlsCusConContract dto);
    List<Map> queryOverdueContractList1(HlsCusConContract dto);

    List<HlsCusCshContractCashflow> queryContractCashflowJob(HlsCusCshContractCashflow hlsCusCshContractCashflow);

}
