package com.hand.hls.fnd.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fnd.dto.CockpitImport;

import java.util.List;

public interface CockpitImportMapper extends Mapper<CockpitImport> {
    /**
     * 管理驾驶舱
     *
     * @param cockpitImport
     * @return
     */
    List<CockpitImport> queryAllReport(CockpitImport cockpitImport);

    List<CockpitImport> queryBpAllReport(CockpitImport cockpitImport);

    List<CockpitImport> queryLonAllReport(CockpitImport cockpitImport);

    List<CockpitImport> queryPrjAllReport(CockpitImport cockpitImport);

    List<CockpitImport> queryPrjAuthorization(CockpitImport cockpitImport);
}