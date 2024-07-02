package com.hand.hls.eas.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.eas.dto.HlsCusAccountHistory;

import java.util.List;

public interface HlsCusAccountHistoryMapper extends Mapper<HlsCusAccountHistory>{

    List<HlsCusAccountHistory> selectCheckAccountData(HlsCusAccountHistory dto);

    List<HlsCusAccountHistory> selectCheckContractData(HlsCusAccountHistory dto);

    List<HlsCusAccountHistory> selectCheckGldAccountData(HlsCusAccountHistory dto);

    List<HlsCusAccountHistory> selectCheckCurrencyData(HlsCusAccountHistory dto);

    List<HlsCusAccountHistory> selectCheckCompanyData(HlsCusAccountHistory dto);
}