package com.hand.hls.hls.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.hls.dto.HlsWebExcel;
import com.hand.hls.hls.dto.HlsWebExcelConfigLn;

import java.util.List;

public interface IHlsWebExcelConfigLnService extends IBaseService<HlsWebExcelConfigLn>, ProxySelf<IHlsWebExcelConfigLnService>{

    List<HlsWebExcelConfigLn> selectHlsWebExcelConfiglineByHdId(IRequest iRequest,HlsWebExcelConfigLn ln,int pagenum, int pagesize);

}