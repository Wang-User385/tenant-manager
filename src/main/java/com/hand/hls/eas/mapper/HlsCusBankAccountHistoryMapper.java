package com.hand.hls.eas.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.eas.dto.HlsCusBankAccountHistory;

import java.util.List;

public interface HlsCusBankAccountHistoryMapper extends Mapper<HlsCusBankAccountHistory>{


    HlsCusBankAccountHistory selectMaxHistoryVersion(HlsCusBankAccountHistory dto);

    HlsCusBankAccountHistory insertBnakAccountHistroryData(HlsCusBankAccountHistory dto);

    List<HlsCusBankAccountHistory> selectBankAccountData(HlsCusBankAccountHistory dto);
}