package com.hand.hls.prj.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.prj.dto.HlsCusPrjSurviveRequire;

public interface HlsCusPrjSurviveRequireService extends IBaseService<HlsCusPrjSurviveRequire>, ProxySelf<HlsCusPrjSurviveRequireService>{

    String getPrjSurviveRequireChangeInfo(IRequest iRequest,Long currentProjectId,Long historyProjectId);

}