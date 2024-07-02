package com.hand.hls.eas.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.eas.dto.HlsCusCredentialsData;
import com.hand.hls.eas.dto.HlsCusEasLogin;
import com.hand.hls.eas.dto.HlsCusPsotEasTmp;

import java.util.List;

public interface HlsCusEasLoginMapper extends Mapper<HlsCusEasLogin>{


  List<HlsCusCredentialsData> selectCredentialsData(HlsCusPsotEasTmp dto);

}