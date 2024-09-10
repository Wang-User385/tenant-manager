package com.hand.hls.prj.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.prj.dto.HlsCusPrjBusinessAccessCompare;
import hls.core.utils.exception.HlsCusException;

import java.util.List;

public interface HlsCusPrjIBusinessAccessCompareService extends IBaseService<HlsCusPrjBusinessAccessCompare>, ProxySelf<HlsCusPrjBusinessAccessCompare>{


    List<HlsCusPrjBusinessAccessCompare> queryBusinessCompare(IRequest request, HlsCusPrjBusinessAccessCompare dto) throws HlsCusException;

}