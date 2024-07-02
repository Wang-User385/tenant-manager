package com.hand.hls.eas.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.eas.dto.HlsCusPsotEasTmp;

import java.util.List;

public interface HlsCusPsotEasTmpMapper extends Mapper<HlsCusPsotEasTmp>{

List<HlsCusPsotEasTmp> selectCredentialsTmpData(HlsCusPsotEasTmp dto);

void deleteCurrentSessionIdTmpData(HlsCusPsotEasTmp dto);

    List<HlsCusPsotEasTmp> selectCredentialsLogData(HlsCusPsotEasTmp dto);

}