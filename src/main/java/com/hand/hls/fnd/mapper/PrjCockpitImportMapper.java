package com.hand.hls.fnd.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fnd.dto.PrjCockpitImport;

import java.util.List;

public interface PrjCockpitImportMapper extends Mapper<PrjCockpitImport> {
    List<PrjCockpitImport> queryUser(PrjCockpitImport cockpitImport);
}