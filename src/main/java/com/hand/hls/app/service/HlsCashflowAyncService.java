package com.hand.hls.app.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.app.dto.HlsCashflowAyncDto;
import com.hand.hls.app.entity.HlsCusItfcResponseData;

import java.util.List;
import java.util.Map;

public interface HlsCashflowAyncService extends IBaseService<HlsCashflowAyncDto>, ProxySelf<HlsCashflowAyncService> {

    HlsCusItfcResponseData getResult(IRequest iRequest, String fromDate, String toDate);

    void updateResult(IRequest iRequest,HlsCashflowAyncDto hlsCashflowAyncDto);
}