package com.hand.hls.hls.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.hls.dto.HlsWebExcelCalcResult;
import com.hand.hls.hls.dto.HlsWebExcelConfigHd;
import com.hand.hls.hls.dto.HlsWebExcelConfigLn;
import hls.core.utils.exception.HlsCusException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface IHlsWebExcelCalcResultService extends IBaseService<HlsWebExcelCalcResult>, ProxySelf<IHlsWebExcelCalcResultService>{

     String queryWebExcelCalcResult(IRequest iRequest,HlsWebExcelCalcResult result);

     HlsWebExcelCalcResult calcExcel(IRequest iRequest,HlsWebExcelCalcResult calcResult) throws HlsCusException;

     JSONArray extractLineDataFromObject(Object list, String sheetName,Long excelId);

     HlsWebExcelConfigHd getPriceListConfigLn(String type, String sheetName,Long excelId);

     void updateSheetLines(JSONArray lines, XSSFSheet sheet,Long excelId);

     void setCellFormat(JSONObject object, List<HlsWebExcelConfigLn> hlsWebExcelConfigLns);

     String unzipSheet(String compressSheet) throws UnsupportedEncodingException;

     void readSheets(XSSFWorkbook wb, JSONArray array);

     void readSheets(XSSFWorkbook wb, JSONArray array, boolean valueOnly);

     void writeBack(XSSFWorkbook wb, JSONArray array, Long excelId);

     String getCompressSheets(String array) throws UnsupportedEncodingException;



}