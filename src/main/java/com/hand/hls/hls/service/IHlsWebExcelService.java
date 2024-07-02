package com.hand.hls.hls.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.hls.dto.HlsWebExcel;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public interface IHlsWebExcelService extends IBaseService<HlsWebExcel>, ProxySelf<IHlsWebExcelService>{


    List<Map> selectHlsWebExcelInfo(IRequest iRequest,HlsWebExcel excel,int pagenum, int pagesize);

    void updateSheets(Long excelId, String sheets,String compressSheets);

    List<Map> getSheetNames(HlsWebExcel hlsWebExcel) throws UnsupportedEncodingException;
}