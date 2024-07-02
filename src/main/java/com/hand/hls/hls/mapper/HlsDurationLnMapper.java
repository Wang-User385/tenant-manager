package com.hand.hls.hls.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.hls.dto.HlsDurationLn;

import java.util.Date;
import java.util.List;

public interface HlsDurationLnMapper extends Mapper<HlsDurationLn>{
    List<HlsDurationLn> hlsDurationLnDetailQuery(HlsDurationLn ln);
    List<HlsDurationLn> hlsDurationLnProjectQuery(HlsDurationLn ln);
    List<HlsDurationLn> hlsDurationLnDepositQuery(HlsDurationLn ln);
    List<HlsDurationLn> hlsDurationLnPledgorMortgagorQuery(HlsDurationLn ln);
    List<HlsDurationLn> hlsDurationLnPledgorMortgagorQueryNew(HlsDurationLn ln);
    List<HlsDurationLn> hlsDurationLnItemDetailQuery(HlsDurationLn ln);
    List<HlsDurationLn> hlsDurationLnItemDetailQueryLease(HlsDurationLn ln);
    List<HlsDurationLn> hlsDurationLnWarrantDetailQuery(HlsDurationLn ln);
    List<HlsDurationLn> hlsDurationLnLeaseItemQuery(HlsDurationLn ln);
    List<HlsDurationLn> hlsDurationLnLeaseItemQueryNew(HlsDurationLn ln);
    void deleteHlsDurationLn(HlsDurationLn dto);
    List<HlsDurationLn> hlsDurationLnEtCalc(HlsDurationLn ln);
    List<HlsDurationLn> hlsDurationLnPrepaymentCalc(HlsDurationLn ln);
    List<HlsDurationLn> hlsDurationLnChangeCalc(HlsDurationLn ln);
    List<HlsDurationLn> hlsDurationLnChangeCalcNew(HlsDurationLn ln);
    List<HlsDurationLn> hlsDurationLnBpInfoQuery(HlsDurationLn ln);

    List<HlsDurationLn> hlsDurationLnEtDetailQueryNew(HlsDurationLn ln);
    List<HlsDurationLn> selectWriteOff(HlsDurationLn ln);
    List<HlsDurationLn> CountEt(HlsDurationLn ln);
    List<HlsDurationLn> hlsDurationLnChangeDetailQuery(HlsDurationLn ln);
    List<HlsDurationLn> hlsDurationLnChangeDateQuery(HlsDurationLn ln);
}
