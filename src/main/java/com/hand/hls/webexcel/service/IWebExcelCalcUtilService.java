package com.hand.hls.webexcel.service;

import com.hand.hap.core.IRequest;

import java.util.List;
import java.util.Map;

public interface IWebExcelCalcUtilService {

    List<Map> getExcelSingleListByHdInfo(IRequest requestContext, String jsonStr, Long excelId, Map dtoMap,String tableType) throws Exception;

}
