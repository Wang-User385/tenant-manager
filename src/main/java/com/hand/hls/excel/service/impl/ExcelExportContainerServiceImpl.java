package com.hand.hls.excel.service.impl;

import com.hand.hap.core.AppContextInitListener;
import com.hand.hap.core.IRequest;
import com.hand.hls.excel.formbean.ExcelBean;
import com.hand.hls.excel.service.ExcelExportContainerService;
import com.hand.hls.excel.service.ExcelExportMonitorService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
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
@Service
public class ExcelExportContainerServiceImpl implements AppContextInitListener, ExcelExportContainerService {

    private Map<String, ExcelExportMonitorService> commonService = new HashMap<String, ExcelExportMonitorService>();

    @Override
    public void contextInitialized(ApplicationContext applicationContext) {
        Map<String, ExcelExportMonitorService> map = applicationContext.getBeansOfType(ExcelExportMonitorService.class);
        map.forEach((k, v) -> {
            commonService.put(v.getExcelType(), v);
        });
    }

    @Override
    public List<List<ExcelBean>> start(IRequest iRequest, List list, Map params) throws Exception {
        String type = (String) params.get("excelType");
        List<List<ExcelBean>> excelList = new ArrayList<>();
        ExcelExportMonitorService service = commonService.get(type);
        if (service != null) {
            try {
                excelList = service.process(iRequest, list, params);
            } catch (Exception e) {
                throw e;
            }
        }
        return excelList;
    }
}
