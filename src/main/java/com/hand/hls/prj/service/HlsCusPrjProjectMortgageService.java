package com.hand.hls.prj.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.prj.dto.HlsCusPrjProjectMortgage;

public interface HlsCusPrjProjectMortgageService extends IBaseService<HlsCusPrjProjectMortgage>, ProxySelf<HlsCusPrjProjectMortgageService> {

    void excelImport(IRequest iRequest, Long headerId, Long projectId);


}