package com.hand.hls.prj.service;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjQuotationHistory;

public interface IHlsCusPrjQuotationHistoryService extends IBaseService<HlsCusPrjQuotationHistory>, ProxySelf<IHlsCusPrjQuotationHistoryService>{

    Long selectVersionCount(HlsCusPrjProject hlsCusPrjProject);

    Long selectVersionCountCon(HlsCusConContract hlsCusConContract);

}