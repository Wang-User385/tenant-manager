package com.hand.hls.webexcel.service;

import com.hand.hap.core.IRequest;
import com.hand.hls.hls.dto.HlsWebExcelCalcResult;
import hls.core.utils.exception.HlsCusException;

public interface IWebExcelCalcService {

    HlsWebExcelCalcResult calcExcel(IRequest iRequest,HlsWebExcelCalcResult calcResult) throws HlsCusException;

    String getSourceDocumentCategory();
}
