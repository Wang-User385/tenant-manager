package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.HlsCusBpFinancingSituation;

import java.util.List;

public interface HlsCusIBpFinancingSituationService extends IBaseService<HlsCusBpFinancingSituation>, ProxySelf<HlsCusIBpFinancingSituationService> {

    List<HlsCusBpFinancingSituation> selectAll(IRequest requestContext, HlsCusBpFinancingSituation bpFinancingSituation, int page, int pagesize);

    void excelImport(IRequest iRequest, Long headerId, Long bpId);
}