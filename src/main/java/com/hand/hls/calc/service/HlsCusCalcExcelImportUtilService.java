package com.hand.hls.calc.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.calc.dto.HlsCalcConfig;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface HlsCusCalcExcelImportUtilService extends IBaseService<HlsCalcConfig>, ProxySelf<HlsCusCalcExcelImportUtilService> {
      String[][][] parseExcelJsonToArray(IRequest requestContext, String jsonStr) throws IOException;

      Map parseExcelJsonToMap(IRequest requestContext, String jsonStr) throws IOException;

      String[][][] parseExcelJsonToArrayFormat(IRequest requestContext, String jsonStr) throws IOException;
      Map<String,String> getCalcHdMap(IRequest requestContext, String jsonStr, String priceList)throws  Exception;
      String getNumberFromExcelStr(String str, String type);
      Object  getExcelToCalcHdTable(IRequest requestContext, String jsonStr, String priceList, String models)throws  Exception;
      List<Object> getExcelToCalcLnTable(IRequest requestContext, String jsonStr, String priceList, String models, int leaseTimes, String sourceDocumentCategory)throws  Exception;
      Boolean excelFormatHasChange(IRequest requestContext, String priceList, String checkJsonStr) throws IOException;
      int excelColStrToNum(String colStr);

      List<Object> getExcelHdCashItemList(IRequest iRequest,String jsonStr,String priceList,String models);
}
