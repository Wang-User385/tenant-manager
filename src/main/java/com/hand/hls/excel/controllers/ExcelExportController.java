package com.hand.hls.excel.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hls.excel.formbean.ExcelBean;
import com.hand.hls.excel.formbean.ExcelExportBean;
import com.hand.hls.excel.service.ExcelExportContainerService;
import com.hand.hls.excel.service.ExcelExportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2020/5/9
 * @description:
 */
@Controller
public class ExcelExportController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ExcelExportService excelExportService;
    @Autowired
    private ExcelExportContainerService excelExportContainerService;


    @RequestMapping(value = "/excel/export")
    @ResponseBody
    public void export(HttpServletRequest request,
                       HttpServletResponse response, ExcelExportBean excelExportBean) {

        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        Map params = new HashMap(1);

        Optional.ofNullable(excelExportBean.getExcelType()).ifPresent(item -> {
            params.put("excelType", excelExportBean.getExcelType());

            List<Long> idList = new ArrayList<>();

            if (excelExportBean.getIds() != null) {
                idList = Arrays.asList(excelExportBean.getIds());
            }

            try {
                List<List<ExcelBean>> allList = excelExportContainerService.start(iRequest, idList, params);
                excelExportService.poiExport(request, response, allList, excelExportBean);
            } catch (Exception e) {
                logger.error("excel export error", e);
            }

        });
    }
}
