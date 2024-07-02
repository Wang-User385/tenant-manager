package com.hand.hls.excel.service.impl;

import com.hand.hls.excel.formbean.ExcelBean;
import com.hand.hls.excel.formbean.ExcelExportBean;
import com.hand.hls.excel.service.ExcelExportService;
import com.hand.hls.office.service.IFndAtmAttachmentMultiService;
import com.hand.hls.office.service.IFndAtmAttachmentService;
import com.hand.hls.utils.HlsCusCheckNull;
import com.hand.hls.utils.ResMessageException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import leaf.utils.BrowserUtils;
import org.apache.poi.hssf.OldExcelFormatException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @version 2.0 添加合并行信息
 * @author: Eugene Song
 * @date: 2019/8/14
 * @description: excel 定制化导出
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ExcelExportServiceImpl implements ExcelExportService {

    private static String EXCEL_VERSION_2007_SUFFIX = ".xlsx";

    private static String EXCEL_VERSION_2003_SUFFIX = ".xls";

    private static String SHEET_NAME = "Sheet";

    private static String FILE_NAME = "模版.xlsx";

    private static String DOWN_FILE_NAME = "excel.xlsx";


    @Autowired
    private IFndAtmAttachmentService atmAttachmentService;
    @Autowired
    private IFndAtmAttachmentMultiService attachmentMultiService;


    private Logger logger = LoggerFactory.getLogger(getClass());

    private Configuration configuration = null;

    public ExcelExportServiceImpl() {
        configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        configuration.setDefaultEncoding("utf-8");
    }


    /**
     * @Title: freeMarkerExport
     * @Discription: 通过 freeMarker的方式生成excel
     * @Param: [params]
     * @Return: void
     */
    @Override
    public void freeMarkerExport(Map params) {
        configuration.setClassForTemplateLoading(this.getClass(), "/template");
        Template t = null;
        try {
            String template = (String) params.get("template");
            t = configuration.getTemplate(template, null, "utf-8");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        String path = (String) params.get("path");
        File outFile = new File(path);
        Writer out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "utf-8"));
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        try {
            t.process(params, out);
            out.close();
        } catch (TemplateException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

    }

    /**
     * @Title: createNewWorkbookIfNotExist
     * @Discription:创建一个不存在的excel文件
     * @Param: [fileName]
     * @Return: org.apache.poi.ss.usermodel.Workbook
     */
    private Workbook createNewWorkbookIfNotExist(String fileName) throws Exception {
        Workbook wb = null;
        if (fileName.endsWith(EXCEL_VERSION_2003_SUFFIX)) {
            wb = new HSSFWorkbook();
        } else if (fileName.endsWith(EXCEL_VERSION_2007_SUFFIX)) {
            wb = new XSSFWorkbook();
        } else {
            throw new ResMessageException("文件类型错误！既不是.xls也不是.xlsx");
        }
        logger.info(fileName + "文件创建成功！");
        return wb;
    }

    /**
     * @Title: createWorkbook
     * @Discription:创建一个新的或者已存在的Excel文档的Workbook
     * @Param: [fileName]
     * @Return: org.apache.poi.ss.usermodel.Workbook
     */
    public Workbook createWorkbook(String fileName) throws Exception {
        InputStream input = null;
        Workbook wb = null;
        try {
            //如果不存在
            if (!new File(fileName).exists()) {
                //创建新的
                wb = createNewWorkbookIfNotExist(fileName);
            } else {
                input = new FileInputStream(fileName);
                wb = WorkbookFactory.create(input);
            }
        } catch (OldExcelFormatException e) {
            logger.error("文件打开失败，原因：要打开的Excel文件版本过低！");
            throw new OldExcelFormatException("文件版本过低");
        } finally {
            if (input != null) {
                input.close();
            }
        }
        return wb;
    }

    /**
     * @Title: createSheet
     * @Discription: 创建sheet
     * @Param: [wb, sheetName]
     * @Return: org.apache.poi.ss.usermodel.Sheet
     */
    public Sheet createSheet(Workbook wb, String sheetName, String password) {
        Sheet sheet = wb.getSheet(sheetName);
        if (sheet == null) {
            sheet = wb.createSheet(sheetName);
        }
        //sheet 加密
        if (password != null) {
            sheet.protectSheet(password);
        }
        return sheet;
    }

    /**
     * @Title: createRow
     * @Discription: 创建行row
     * @Param: [sheet, rowNum]
     * @Return: org.apache.poi.ss.usermodel.Row
     */
    public Row createRow(Sheet sheet, int rowNum) {
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            row = sheet.createRow(rowNum);
        }
        return row;
    }

    /**
     * @Title: createCell
     * @Discription: 创建单元格cell
     * @Param: [row, cellNum]
     * @Return: org.apache.poi.ss.usermodel.Cell
     */
    public Cell createCell(Row row, int cellNum) {
        Cell cell = row.getCell(cellNum);
        if (cell == null) {
            cell = row.createCell(cellNum);
        }
        return cell;
    }


    /**
     * @Title: createFontStyle
     * @Discription: 设置字体样式
     * @Param: [workbook, data]
     * @Return: org.apache.poi.ss.usermodel.Font
     * @Exampl 例如：
     * 字体
     * excelBean.setFontName("宋体");
     * 字号
     * excelBean.setFontHeightInPotins(Short.parseShort("24"));
     * 颜色
     * excelBean.setColor(Font.COLOR_RED);
     * 下划线
     * excelBean.setUnderline(Font.U_SINGLE);
     * 上标下标
     * excelBean.setTypeOffset(Font.SS_SUPER);
     * 删除线
     * excelBean.setStrikeout(true);
     * 加粗
     * excelBean.setBold(true);
     */
    public Font createFontStyle(Workbook workbook, ExcelBean data) {
        Font font = workbook.createFont();

        /**
         * 设置字体名称
         */
        Optional.ofNullable(data.getFontName()).ifPresent(item -> font.setFontName(item));
        /**
         * 设置字号
         */
        Optional.ofNullable(data.getFontHeightInPotins()).ifPresent(item -> font.setFontHeightInPoints(item));
        /**
         * 设置字体颜色
         */
        Optional.ofNullable(data.getColor()).ifPresent(item -> font.setColor(item));

        /**
         * 设置下划线
         */
        Optional.ofNullable(data.getUnderline()).ifPresent(item -> font.setUnderline(item));

        /**
         * 设置上标下标
         */
        Optional.ofNullable(data.getTypeOffset()).ifPresent(item -> font.setTypeOffset(item));

        /**
         * 设置删除线
         */
        Optional.ofNullable(data.getStrikeout()).ifPresent(item -> font.setStrikeout(item));

        /**
         * 设置加粗
         */
        Optional.ofNullable(data.getBold()).ifPresent(item -> font.setBold(item));

        return font;
    }

    /**
     * @Title: createCellStyle
     * @Discription: 创建单元格样式
     * @Param: [workbook, cell, data]
     * @Return: org.apache.poi.ss.usermodel.Cell
     * @Example: 例如：
     * 左边框加粗
     * excelBean.setBorderLeft(BorderStyle.THICK);
     * 左边框蓝色
     * excelBean.setLeftBorderColor(IndexedColors.BLUE.getIndex());
     * 水平居中
     * excelBean.setAlignment(HorizontalAlignment.CENTER);
     * 垂直居中
     * excelBean.setVerticalAlignment(VerticalAlignment.CENTER);
     * 长宽自适应
     * excelBean.setShrinkToFit(true);
     * 单元格锁定
     * excelBean.setLockCell(true);
     */
    public Cell createCellStyle(Workbook workbook, Cell cell, ExcelBean data) {
        CellStyle cellStyle = workbook.createCellStyle();

        /**
         * 设置边框
         */
        Optional.ofNullable(data.getBorder()).ifPresent(item -> {
            cellStyle.setBorderTop(item);
            cellStyle.setBorderBottom(item);
            cellStyle.setBorderLeft(item);
            cellStyle.setBorderRight(item);
        });
        Optional.ofNullable(data.getBorderTop()).ifPresent(item -> cellStyle.setBorderTop(item));
        Optional.ofNullable(data.getBorderBottom()).ifPresent(item -> cellStyle.setBorderBottom(item));
        Optional.ofNullable(data.getBorderLeft()).ifPresent(item -> cellStyle.setBorderLeft(item));
        Optional.ofNullable(data.getBorderRight()).ifPresent(item -> cellStyle.setBorderRight(item));

        /**
         * 设置边框颜色
         */
        Optional.ofNullable(data.getBorderColor()).ifPresent(item -> {
            cellStyle.setTopBorderColor(item);
            cellStyle.setBottomBorderColor(item);
            cellStyle.setLeftBorderColor(item);
            cellStyle.setRightBorderColor(item);

        });
        Optional.ofNullable(data.getTopBorderColor()).ifPresent(item -> cellStyle.setTopBorderColor(item));
        Optional.ofNullable(data.getBottomBorderColor()).ifPresent(item -> cellStyle.setBottomBorderColor(item));
        Optional.ofNullable(data.getLeftBorderColor()).ifPresent(item -> cellStyle.setLeftBorderColor(item));
        Optional.ofNullable(data.getRightBorderColor()).ifPresent(item -> cellStyle.setRightBorderColor(item));

        /**
         * 设置对齐方式
         */
        Optional.ofNullable(data.getAlignment()).ifPresent(item -> cellStyle.setAlignment(item));
        /**
         * 设置垂直方式
         */
        Optional.ofNullable(data.getVerticalAlignment()).ifPresent(item -> cellStyle.setVerticalAlignment(item));
        /**
         * 设置长宽自适应
         */
        Optional.ofNullable(data.getShrinkToFit()).ifPresent(item -> cellStyle.setShrinkToFit(item));
        /**
         * 设置单元格锁定
         */
        Optional.ofNullable(data.getLockCell()).ifPresent(item -> cellStyle.setLocked(item));
        /**
         * 设置 字体
         */
        cellStyle.setFont(createFontStyle(workbook, data));

        cell.setCellStyle(cellStyle);
        return cell;
    }

    //行合并
    public void mergedRow(List<Long> mergedCols, List<ExcelBean> excelBeanList, Sheet sheet) {
        //找到需要合并的单元格
        for (Long column : mergedCols) {
            List<ExcelBean> mergedExcelBean = excelBeanList.stream().filter(item -> item.getCell().compareTo(Math.toIntExact(column)) == 0).collect(Collectors.toList());
            //按照行排序
            mergedExcelBean = mergedExcelBean.stream().sorted(Comparator.comparing(ExcelBean::getRow)).collect(Collectors.toList());
            //相邻的行记录 数据相同合并
            if (mergedExcelBean.size() > 1) {
                for (int i = 0; i < mergedExcelBean.size() - 1; i++) {
                    //相邻的行记录
                    if ((mergedExcelBean.get(i).getRow() + 1) == mergedExcelBean.get(i + 1).getRow()) {
                        if (!HlsCusCheckNull.isNull(mergedExcelBean.get(i).getValue())) {
                            if (mergedExcelBean.get(i).getValue().equals(mergedExcelBean.get(i + 1).getValue())) {
                                sheet.addMergedRegion(new CellRangeAddress(mergedExcelBean.get(i).getRow(), mergedExcelBean.get(i).getRow() + 1, 0, 0));
                            }
                        }
                        if (!HlsCusCheckNull.isNull(mergedExcelBean.get(i).getValueD())) {
                            if (mergedExcelBean.get(i).getValueD().equals(mergedExcelBean.get(i + 1).getValueD())) {
                                sheet.addMergedRegion(new CellRangeAddress(mergedExcelBean.get(i).getRow(), mergedExcelBean.get(i).getRow() + 1, 0, 0));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @Title: addExcel
     * @Discription: 追加到已有excel
     * @Param: [response, dataList, fileName, sheetNameList, password]
     * @Return: java.lang.String
     */
    public Workbook addExcel(List<List<ExcelBean>> dataList, String fileName, List<String> sheetNameList, String password) throws Exception {

        //获取文件模版路径
        URL url = ExcelExportServiceImpl.class.getClassLoader().getResource("template/" + fileName);
        String path = "";
        if (url != null) {
            path = URLDecoder.decode(url.getPath(), "UTF-8");
        } else {
            path = fileName;
        }

        Workbook workbook = createWorkbook(path);

        for (int i = 0; i < dataList.size(); i++) {
            //支持 合并的单元格
            List<Long> mergeRow = dataList.get(i).get(0).getMergeRow();
            //支持 多sheet 填充
            String sheetName = sheetNameList.size() < i + 1 ? SHEET_NAME + (i + 1) : sheetNameList.get(i);
            Sheet sheet = createSheet(workbook, sheetName, password);

            for (ExcelBean data : dataList.get(i)) {
                Row row = createRow(sheet, data.getRow());
                Cell cell = createCell(row, data.getCell());
                if (data.getValue() == null) {
                    cell.setCellValue(data.getValueD());
                } else {
                    cell.setCellValue(data.getValue());
                }
                createCellStyle(workbook, cell, data);
            }
            if (!HlsCusCheckNull.isNull(mergeRow)) {
                mergedRow(mergeRow, dataList.get(i), sheet);
            }
        }

        //excel公式自动计算
        /*HSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);*/
        workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
        return workbook;
    }


    public void IOWrite(HttpServletRequest request, HttpServletResponse response, Workbook workbook, String downFileName) throws IOException {
        String userAgent = request.getHeader("User-Agent");

        userAgent = userAgent.toLowerCase();
        if (BrowserUtils.isIE(userAgent)) {
            downFileName = URLEncoder.encode(downFileName, "UTF-8");
        } else {
            downFileName = new String(downFileName.getBytes("UTF-8"), "ISO-8859-1");
        }

        response.setContentType("application/x-download;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + downFileName + "\"");

        OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }


    /**
     * @Title: poiExport
     * @Discription: 入口函数
     * @Param: [request, response, dataList, excelExportBean]
     * @Return: void
     */
    @Override
    public void poiExport(HttpServletRequest request, HttpServletResponse response, List<List<ExcelBean>> dataList, ExcelExportBean excelExportBean) throws Exception {

        String fileName = HlsCusCheckNull.isNull(excelExportBean.getFileName()) ? FILE_NAME : excelExportBean.getFileName();
        String downFileName = HlsCusCheckNull.isNull(excelExportBean.getDownFileName()) ? DOWN_FILE_NAME : excelExportBean.getDownFileName();

        List<String> sheetNameList = new ArrayList<>();
        if (excelExportBean.getSheetNames() != null) {
            sheetNameList = Arrays.asList(excelExportBean.getSheetNames());
        }
        Workbook workbook = addExcel(dataList, fileName, sheetNameList, excelExportBean.getPassword());

        IOWrite(request, response, workbook, downFileName);
    }


}
