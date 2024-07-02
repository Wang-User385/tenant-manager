package com.hand.hls.pam.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.pam.dto.HlsCapitalInvestmentInfo;

import java.util.List;

public interface HlsCapitalInvestmentInfoMapper extends Mapper<HlsCapitalInvestmentInfo>{
    List<HlsCapitalInvestmentInfo> selectCapitalInvestmentInfo();
}