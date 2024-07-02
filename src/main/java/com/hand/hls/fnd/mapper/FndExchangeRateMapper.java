package com.hand.hls.fnd.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fnd.dto.FndExchangeRate;

import java.util.List;

public interface FndExchangeRateMapper extends Mapper<FndExchangeRate>{

    List<FndExchangeRate> queryExchangeAll(FndExchangeRate fndExchangeRate);

    List<FndExchangeRate> queryCompanyCurrency(FndExchangeRate fndExchangeRate);

    List<FndExchangeRate> queryDailyRate(FndExchangeRate fndExchangeRate);




}