package com.hand.hls.hls.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.calc.dto.HlsPriceListConfigHd;
import com.hand.hls.calc.dto.HlsPriceListConfigLn;
import com.hand.hls.hls.dto.HlsDurationHd;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2020/6/16
 * @description: 报价计算工具 利用excel
 */
public interface HlsQuotationCalcService extends IBaseService<HlsCusPrjQuotation>, ProxySelf<HlsQuotationCalcService> {

    List<HlsPriceListConfigLn> getPriceListConfigLn(String priceList, String sheetName);

    HlsPriceListConfigHd getPriceListConfigLn(String priceList, String type, String sheetName);

    void readSheet(XSSFWorkbook wb, XSSFSheet sheet, JSONObject jsonObject);

    void readSheet(XSSFWorkbook wb, XSSFSheet sheet, JSONObject jsonObject, boolean valueOnly);

    void readSheets(XSSFWorkbook wb, JSONArray array);

    void readSheets(XSSFWorkbook wb, JSONArray array, boolean valueOnly);

    void setCellFormat(JSONArray transArray, List<HlsPriceListConfigLn> hlsPriceListConfigLns);

    void setCellFormat(JSONObject object, List<HlsPriceListConfigLn> hlsPriceListConfigLns);

    JSONArray extractHeadDataFromSheet(String sheets, String priceList, String sheetName);

    JSONArray extractLineDataFromSheet(String sheets, String priceList, String sheetName);

    void updateSheet(JSONArray cells, XSSFSheet sheet, String priceList);

    void updateSheetLines(JSONArray lines, XSSFSheet sheet, String priceList);

    void writeBack(XSSFWorkbook wb, JSONArray array, String priceList);

    String unzipSheet(String compressSheet) throws UnsupportedEncodingException;

    String getCompressSheets(String array) throws UnsupportedEncodingException;

    JSONArray extractHeadDataFromObject(Object object, String priceList, String sheetName);

    JSONArray extractLineDataFromObject(Object list, String priceList, String sheetName);

    void updateQuotationFromSheet(IRequest iRequest, Long quotationId, String priceList, JSONArray array) throws Exception;

    JSONArray extractHeadDataFromMap(List<Map> maps , String priceList, String sheetName);
}
