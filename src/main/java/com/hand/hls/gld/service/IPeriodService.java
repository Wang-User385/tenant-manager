package com.hand.hls.gld.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.gld.dto.Period;

import java.util.HashMap;
import java.util.List;

public interface IPeriodService extends IBaseService<Period>, ProxySelf<IPeriodService> {

    List<Period> periodCreate(IRequest request, HashMap param);

    List<Period> periodQuery4Lov(IRequest request, Period condition, int pageNum, int pageSize);

    List<Period> periodNameLov(IRequest request, Period condition, int pageNum, int pageSize);

}