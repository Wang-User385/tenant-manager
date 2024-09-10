package com.hand.hls.fct.service;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fct.dto.HlsChanceBusinessAccessCompare;

import java.util.List;

public interface IHlsChanceBusinessAccessCompareService extends IBaseService<HlsChanceBusinessAccessCompare>, ProxySelf<IHlsChanceBusinessAccessCompareService>{


    List<HlsChanceBusinessAccessCompare> queryChanceCompare(HlsChanceBusinessAccessCompare dto);

}