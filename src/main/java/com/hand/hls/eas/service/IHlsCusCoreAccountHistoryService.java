package com.hand.hls.eas.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.eas.dto.HlsCusCoreAccountHistory;

import java.util.List;

public interface IHlsCusCoreAccountHistoryService extends IBaseService<HlsCusCoreAccountHistory>, ProxySelf<IHlsCusCoreAccountHistoryService>{


    List<HlsCusCoreAccountHistory> selectDataByCheckDate(HlsCusCoreAccountHistory dto,int page,int pagesize);
}