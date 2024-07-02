package com.hand.hls.hls.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.hls.dto.HlsWebExcelConfigHd;

import java.util.List;

public interface IHlsWebExcelConfigHdService extends IBaseService<HlsWebExcelConfigHd>, ProxySelf<IHlsWebExcelConfigHdService>{

    List<HlsWebExcelConfigHd> saveWebExcelConfigHdInfo(IRequest iRequest, HlsWebExcelConfigHd configHd);

}