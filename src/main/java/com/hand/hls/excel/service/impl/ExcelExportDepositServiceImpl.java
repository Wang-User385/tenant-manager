package com.hand.hls.excel.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.excel.formbean.ExcelBean;
import com.hand.hls.excel.service.ExcelExportMonitorService;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.ws.soap.Addressing;
import java.util.ArrayList;
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
@Transactional(rollbackFor = Exception.class)
public class ExcelExportDepositServiceImpl implements ExcelExportMonitorService {
    @Autowired
    private HlsCusConContractMapper contractMapper;

    public static final String EXCEL_TYPE = "DEPOSIT";

    @Override
    public String getExcelType() {
        return EXCEL_TYPE;
    }

    @Override
    public List<List<ExcelBean>> process(IRequest iRequest, List list, Map params) throws Exception {
        List<HlsCusConContract> conContractList = contractMapper.selectAll();
        List<ExcelBean> excelBeanList = new ArrayList<>();
        List<List<ExcelBean>> allList=new ArrayList<>();
        for (int i =0 ; i< conContractList.size() ;i++) {
            ExcelBean excelBean = new ExcelBean();
            excelBean.setRow(i + 1);
            excelBean.setCell(1);
            excelBean.setBorder(BorderStyle.THICK);
            excelBean.setLeftBorderColor(IndexedColors.BLUE.getIndex());
            excelBean.setValue(conContractList.get(i).getContractNumber());
            excelBean.setAlignment(HorizontalAlignment.CENTER);
            excelBean.setVerticalAlignment(VerticalAlignment.CENTER);
            excelBean.setShrinkToFit(true);
            excelBean.setLockCell(false);
            excelBean.setFontName("宋体");
            excelBean.setFontHeightInPotins(Short.parseShort("24"));
            excelBean.setColor(Font.COLOR_RED);
            excelBean.setUnderline(Font.U_SINGLE);
            excelBean.setTypeOffset(Font.SS_SUPER);
            excelBean.setStrikeout(true);
            excelBean.setBold(true);
            excelBeanList.add(excelBean);
        }
        allList.add(excelBeanList);
        return allList;
    }
}
