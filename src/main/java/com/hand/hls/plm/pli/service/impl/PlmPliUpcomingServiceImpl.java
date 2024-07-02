package com.hand.hls.plm.pli.service.impl;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.plm.pli.dto.PlmPliUpcoming;
import com.hand.hls.plm.pli.mapper.PlmPliUpcomingMapper;
import com.hand.hls.plm.pli.service.HlsCusIPostloanInspectionService;
import com.hand.hls.plm.pli.service.PlmPliUpcomingService;
import com.hand.hls.utils.ExportExcelUtil;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(rollbackFor = Exception.class)
public class PlmPliUpcomingServiceImpl extends BaseServiceImpl<PlmPliUpcoming> implements PlmPliUpcomingService {

    @Autowired
    private PlmPliUpcomingMapper mapper;
    @Autowired
    private HlsCusIPostloanInspectionService hlsCusIPostloanInspectionService;

    private static final String FLAG_Y = "Y";
    private static final String FLAG_N = "N";

    @Override
    public List<PlmPliUpcoming> upcomingList(IRequest iRequest, PlmPliUpcoming dto, int page, int pageSize ) {
        PlmPliUpcoming plmPliUpcoming = new PlmPliUpcoming();
        plmPliUpcoming.setEnableFlag(FLAG_Y);
        plmPliUpcoming.setStatus(FLAG_Y);
        plmPliUpcoming.setIsPlaneCheck(dto.getIsPlaneCheck());
        PageHelper.startPage(page,pageSize);
        return mapper.selectUpcomingList(plmPliUpcoming);
    }


    private final static String SHEET_NAME = "sheet1";
    private final static String FILE_NAME = "待检查清单";
    private final static String DATE_FORMAT = "yyyy-MM-dd";
    private final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
    private static final  List<String> colNameList = Lists.newArrayList(
            "客户名称","检查性质","检查年份","检查月份","待检查时间");
    private static final List<String> colGetMethods = Lists.newArrayList(
            "bpName", "inspectionType","appointedYear","appointedMonth", "appointedDate");

    @Override
    public void upcomingListDownloadExcel(HttpServletRequest request, HttpServletResponse response) throws IOException, InvocationTargetException, IllegalAccessException {
        XSSFWorkbook xwork = new XSSFWorkbook();
        XSSFSheet sheet = xwork.createSheet(SHEET_NAME);
        int dataRowNum = ExportExcelUtil.createCommonExcelHead(xwork, sheet, null, colNameList);
        PlmPliUpcoming plmPliUpcoming = new PlmPliUpcoming();
        plmPliUpcoming.setEnableFlag(FLAG_Y);
        plmPliUpcoming.setStatus(FLAG_Y);
        List<PlmPliUpcoming> unitSelects = mapper.selectUpcomingList(plmPliUpcoming);

        for(PlmPliUpcoming dt : unitSelects){
            if(PlmPliFrequencySetUtilImpl.ON_SITE_INSPECT.equalsIgnoreCase(dt.getInspectionType())){
                dt.setInspectionType("现场检查");
            }
            if(PlmPliFrequencySetUtilImpl.OFF_SITE_INSPECT.equalsIgnoreCase(dt.getInspectionType())){
                dt.setInspectionType("非现场检查");
            }
            String appointedDatesplit[] = simpleDateFormat.format(dt.getAppointedDate()).split("-");
            dt.setAppointedYear(appointedDatesplit[0]+"年");
            dt.setAppointedMonth(Long.parseLong(appointedDatesplit[1])+"月");
            //创建列
            XSSFRow row = sheet.createRow(dataRowNum++);
            setData(xwork,sheet, row, dt);
        }
        ExportExcelUtil.IOWrite(xwork, null,request, response, FILE_NAME);
    }
    private void setData(XSSFWorkbook workbook , XSSFSheet sheet, XSSFRow row, PlmPliUpcoming creditContract) throws InvocationTargetException, IllegalAccessException {
        DecimalFormat df =new DecimalFormat("###,##0.00");
        for (int i = 0; i < colGetMethods.size(); i++) {
            XSSFCell cell = row.createCell(i);
            Object value = ExportExcelUtil.getValue(creditContract, colGetMethods.get(i));
            if(value instanceof Date){
                XSSFCellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setAlignment(HorizontalAlignment.CENTER);
                value = simpleDateFormat.format(value);
                cell.setCellStyle(cellStyle);
            }

            if(value instanceof Double){
                XSSFCellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setAlignment(HorizontalAlignment.RIGHT);
                value=df.format(value);
                cell.setCellStyle(cellStyle);
            }

            if (Objects.nonNull(value)) {
                ExportExcelUtil.initColWidth(sheet, i, value.toString());
                cell.setCellValue(value.toString());
            }

        }
    }

    @Override
    public List<PlmPliUpcoming> selectUpcomingListNotAnyCondition(IRequest iRequest,PlmPliUpcoming dto,int page,int pageSize) {
        PlmPliUpcoming plmPliUpcoming = new PlmPliUpcoming();
        plmPliUpcoming.setEnableFlag(FLAG_Y);
        plmPliUpcoming.setStatus(FLAG_Y);
        plmPliUpcoming.setIsPlaneCheck(dto.getIsPlaneCheck());
        PageHelper.startPage(page,pageSize);
        return mapper.selectUpcomingListNotAnyCondition(plmPliUpcoming);
    }

}

