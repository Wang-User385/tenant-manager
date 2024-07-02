package com.hand.hls.fct.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fct.dto.HlsCusCreditChanceFinStatement;

import java.util.List;

public interface HlsCusCreditChanceFinStatementService extends IBaseService<HlsCusCreditChanceFinStatement>, ProxySelf<HlsCusCreditChanceFinStatementService> {

    List<HlsCusCreditChanceFinStatement> selectByChanceId(IRequest requestContext, HlsCusCreditChanceFinStatement dto, int pagenum, int pagesize);
}
