package com.hand.hls.excel.service;

import com.hand.hls.excel.formbean.ExcelBean;
import com.hand.hls.excel.formbean.ExcelExportBean;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2019/8/14
 * @description:
 */
@Service
public interface ExcelExportService {
    /**
     * @param params
     */
    void freeMarkerExport(Map<String, String> params);

    void poiExport(HttpServletRequest request, HttpServletResponse response, List<List<ExcelBean>> dataList, ExcelExportBean excelExportBean) throws Exception;
}
