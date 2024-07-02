package com.hand.hls.fct.service;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fct.dto.HlsCusFctProject;

import java.util.List;

public interface HlsCusFctProjectService extends IBaseService<HlsCusFctProject>, ProxySelf<HlsCusFctProjectService> {

    List<HlsCusFctProject> selectManagerByUnitId(HlsCusFctProject fctProject);

}



