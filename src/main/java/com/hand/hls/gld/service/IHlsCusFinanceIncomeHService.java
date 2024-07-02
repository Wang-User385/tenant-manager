package com.hand.hls.gld.service;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.gld.dto.HlsCusFinanceIncomeH;

public interface IHlsCusFinanceIncomeHService extends IBaseService<HlsCusFinanceIncomeH>, ProxySelf<IHlsCusFinanceIncomeHService>{

    Long selectVersionCount(HlsCusFinanceIncomeH hlsCusFinanceIncomeH);

    HlsCusFinanceIncomeH selectRecordByVersionId(HlsCusFinanceIncomeH hlsCusFinanceIncomeH);
}