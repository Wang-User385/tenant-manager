package com.hand.hls.prj.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.prj.dto.HlsCusPrjBpFinancingSituation;

import java.util.List;

public interface HlsCusPrjBpFinancingSituationService extends IBaseService<HlsCusPrjBpFinancingSituation>, ProxySelf<HlsCusPrjBpFinancingSituationService> {

    List<HlsCusPrjBpFinancingSituation> prjBpFinancingSituationInfoQuery(IRequest iRequest, HlsCusPrjBpFinancingSituation hlsCusPrjBpFinancingSituation, int page, int pageSize);

    List<HlsCusPrjBpFinancingSituation> queryBpFinancingSituationCopy(IRequest iRequest, HlsCusPrjBpFinancingSituation hlsCusPrjBpFinancingSituation, int page, int pageSize);
}