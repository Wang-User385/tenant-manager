package com.hand.hls.excel.service;

import com.hand.hap.core.IRequest;
import com.hand.hls.excel.formbean.ExcelBean;

import java.util.List;
import java.util.Map;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2020/5/11
 * @description:
 */
public interface ExcelExportMonitorService <T>{
    String getExcelType();

    List<List<ExcelBean>> process(IRequest iRequest, List<T> list, Map params) throws Exception;
}
