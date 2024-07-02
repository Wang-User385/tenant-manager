package com.hand.hls.utils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.SheetUtil;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.BeanUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExportExcelUtil {
    public static final String TITLE_MAP = "titleMap";

    /**
     * Excel 版本
     */
    public static final String EXCEL_2003 = ".xls";
    public static final String EXCEL_2007 = ".xlsx";
    public static  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static  DecimalFormat df =new DecimalFormat("###,##0.00");
    /**
     * 格式化日期格式
     */
    private final static String DATE_FORMAT = "yyyy-MM-dd";

    public static final ThreadLocal<HashMap<Workbook, CellStyle>> BASE_CELL_STYLE = new ThreadLocal<>();

    public static void IOWrite(Workbook workbook, HSSFSheet sheet, HttpServletRequest request, HttpServletResponse response, String sheetName) throws IOException {

        ServletOutputStream outputStream = null;
        try {
            String fullFileName = null;
            if (workbook instanceof HSSFWorkbook) {
                fullFileName = sheetName + EXCEL_2003;
            } else if (workbook instanceof XSSFWorkbook) {
                fullFileName = sheetName + EXCEL_2007;
            } else {
                fullFileName = sheetName;
            }

            boolean isMSIE = AttachmentHttpUtils.isMSBrowser(request);
            if (isMSIE) {
                fullFileName = URLEncoder.encode(fullFileName, "UTF-8");
            } else {
                fullFileName = new String(fullFileName.getBytes("UTF-8"), "ISO-8859-1");
            }

            response.setContentType("application/x-download;charset=utf-8");
            response.addHeader("Content-Disposition", "attachment;filename="
                    + fullFileName);
            outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    /**
     * 用于多sheet导出
     *
     * @param workbook
     * @param response
     * @param excelName
     */
    public static void IOWriteForSheets(HttpServletRequest request, HSSFWorkbook workbook, HttpServletResponse response, String excelName) {

        try {
            String agent = request.getHeader("USER-AGENT");
            if (null != agent && -1 != agent.indexOf("MSIE") || null != agent
                    && -1 != agent.indexOf("Trident") || null != agent && -1 != agent.indexOf("Edge")) {// ie

                String fileName = URLEncoder.encode(excelName, "UTF8");
                response.addHeader("Content-Disposition", "attachment;filename="
                        + fileName + ".xls");
            } else if (null != agent && -1 != agent.indexOf("Mozilla")) {// 火狐,chrome等

                String fileName = new String(excelName.getBytes("utf-8"), "ISO8859-1");
                response.addHeader("Content-Disposition", "attachment;filename="
                        + fileName + ".xls");
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            response.setContentType("application/x-download;charset=utf-8");
            OutputStream os = response.getOutputStream();
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            byte[] b = new byte[1024];
            while ((bais.read(b)) > 0) {
                os.write(b);
            }
            bais.close();
            os.flush();
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建错误信息Excel 头
     *
     * @param workbook
     * @param sheet
     * @param headName
     */
    public static void writeExcelTitle(Workbook workbook, Sheet sheet, List<String> headName) {

        if (CollectionUtils.isEmpty(headName)) {
            return;
        }
        Row row = sheet.createRow(0);
        int i = 0;
        for (String colName : headName) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(getHeadStyle(workbook));
            cell.setCellValue(colName);
            i++;
        }
        // 错误信息
        Cell cell = row.createCell(i);
        cell.setCellStyle(getHeadStyle(workbook));
        cell.setCellValue("错误信息");

    }

    /**
     * 获取头
     *
     * @param book
     * @return
     */
    public static CellStyle getHeadStyle(Workbook book) {
        CellStyle headerStyle = createBackgroundStyle(book, IndexedColors.GREY_25_PERCENT.getIndex(), FillPatternType.SOLID_FOREGROUND);
        headerStyle.setFont(createFont(book, (short) 11, true, null));
        return headerStyle;
    }

    /**
     * 获取头
     *
     * @param book
     * @return
     */
    public static CellStyle getTextStyle(Workbook book) {
        CellStyle textStyle = creatBaseCellStyle(book);
        textStyle.setFont(createFont(book, (short) 11, false, null));
        return textStyle;
    }

    /**
     * 可以设置字体
     *
     * @param book
     * @param fontSize
     * @return
     */
    public static CellStyle getTextStyle(Workbook book, int fontSize) {
        CellStyle textStyle = creatBaseCellStyle(book);
        textStyle.setFont(createFont(book, (short) fontSize, false, null));
        // 清空
        BASE_CELL_STYLE.set(null);
        return textStyle;
    }

    /**
     * @param sheet    sheet页
     * @param title    标题，可不填。填的话居中
     * @param colNames
     * @return 返回填充数据开始的行数。
     */
    public static int createCommonExcelHead(XSSFWorkbook workbook, XSSFSheet sheet, String title, List<String> colNames) {

        short dataRowNum = 0;
        if (StringUtils.isNotBlank(title)) {
            XSSFRow titleRow = sheet.createRow(dataRowNum);
            XSSFCell cell = titleRow.createCell(0);
            CellStyle titleStyle = getTextStyle(workbook, 14);
            cell.setCellStyle(titleStyle);
            cell.setCellValue(title);
            titleRow.setHeight((short) (35 * 20));
            sheet.addMergedRegion(new CellRangeAddress(dataRowNum, dataRowNum, 0, colNames.size() - 1));
            dataRowNum++;
            titleStyle.setFont(null);
        }

        // 显示列名的行
        XSSFRow headRow = sheet.createRow(dataRowNum);
        headRow.setHeight((short) (25 * 20));
        CellStyle headStyle = getTitleStyle(workbook, 12);
        for (int i = 0; i < colNames.size(); i++) {
            String colName = colNames.get(i);
            XSSFCell cell = headRow.createCell(i);
            cell.setCellStyle(headStyle);
            cell.setCellValue(colName);
            // 列宽自动
            sheet.setColumnWidth(i, (int) ((colName.getBytes().length * 1.5) * 256));
        }
        // 行数+1
        return ++dataRowNum;
    }

    /**
     * 指定头标题的位置开始行
     *
     * @param workbook
     * @param sheet
     * @param title
     * @param colNames
     * @param dataRowNum
     * @return
     */
    public static int createCommonExcelHead(XSSFWorkbook workbook, XSSFSheet sheet, String title, List<String> colNames, int dataRowNum) {
        if (StringUtils.isNotBlank(title)) {
            XSSFRow titleRow = sheet.createRow(dataRowNum);
            XSSFCell cell = titleRow.createCell(0);
            CellStyle titleStyle = getTextStyle(workbook, 14);
            cell.setCellStyle(titleStyle);
            cell.setCellValue(title);
            titleRow.setHeight((short) (35 * 20));
            sheet.addMergedRegion(new CellRangeAddress(dataRowNum, dataRowNum, 0, colNames.size() - 1));
            dataRowNum++;
        }

        // 显示列名的行
        XSSFRow headRow = sheet.createRow(dataRowNum);
        headRow.setHeight((short) (25 * 20));
        CellStyle headStyle = getTitleStyle(workbook, 12);
        for (int i = 0; i < colNames.size(); i++) {
            String colName = colNames.get(i);
            XSSFCell cell = headRow.createCell(i);
            cell.setCellStyle(headStyle);
            cell.setCellValue(colName);
            // 列宽自动
            sheet.setColumnWidth(i, (int) ((colName.getBytes().length * 1.5) * 256));
        }
        // 行数+1
        return ++dataRowNum;
    }

    /**
     * 获取标题。默认加粗，居中
     *
     * @param book
     * @return
     */
    public static CellStyle getTitleStyle(Workbook book, int fontSize) {
        // 创建背景颜色的style
        CellStyle headerStyle = createBackgroundStyle(book, IndexedColors.GREY_25_PERCENT.getIndex(), FillPatternType.SOLID_FOREGROUND);
        // 设置字体
        headerStyle.setFont(createFont(book, (short) fontSize, true, null));
        return headerStyle;
    }


    /**
     * 创建字体，可以不写字体名称
     *
     * @param workbook
     * @param fontSize
     * @param bold
     * @param fontName
     * @return
     */
    public static Font createFont(Workbook workbook, short fontSize, boolean bold, String fontName) {
        Font font = workbook.createFont();
        font.setFontHeightInPoints(fontSize);
        if (StringUtils.isNotBlank(fontName)) {
            font.setFontName(fontName);
        }
        font.setBold(bold);
        return font;
    }

    /**
     * 创建最基础的cellStyle。包括上下居中.边框
     * 注意： 如果想在该样式基础上设置其他样式，只需要设置一遍即可。
     * @param workbook
     * @return
     */
    public static CellStyle creatBaseCellStyle(Workbook workbook) {
        if (BASE_CELL_STYLE.get() == null) {
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            HashMap<Workbook, CellStyle> wbCellStyleHashMap = new HashMap<>();
            wbCellStyleHashMap.put(workbook, cellStyle);
            BASE_CELL_STYLE.set(wbCellStyleHashMap);
        } else {
            HashMap<Workbook, CellStyle> hashMap = BASE_CELL_STYLE.get();
            CellStyle tempCellStyle = hashMap.get(workbook);
            if (tempCellStyle == null) {
                hashMap.clear();
                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setAlignment(HorizontalAlignment.CENTER);
                cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                cellStyle.setBorderBottom(BorderStyle.THIN);
                cellStyle.setBorderLeft(BorderStyle.THIN);
                cellStyle.setBorderTop(BorderStyle.THIN);
                cellStyle.setBorderRight(BorderStyle.THIN);
                hashMap.put(workbook, cellStyle);
            }
        }

        return BASE_CELL_STYLE.get().get(workbook);

    }


    /**
     * 创建有背景填充的cellStyle
     *
     * @param workbook
     * @param
     * @param fillPatternType
     * @return
     */
    public static CellStyle createBackgroundStyle(Workbook workbook, short colorIndex, FillPatternType fillPatternType) {
//        CellStyle cellStyle = creatBaseCellStyle(workbook);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        //设置背景色
        cellStyle.setFillForegroundColor(colorIndex);
        //填充图案
        cellStyle.setFillPattern(fillPatternType);
        return cellStyle;
    }

    /**
     * 对表格值进行赋值和样式
     *
     * @param workbook
     * @param row
     * @param index
     * @param val
     */
    public static synchronized void setBaseCellVal(XSSFWorkbook workbook, XSSFRow row, int index, String val) {
        XSSFCell cell = row.createCell(index);
        cell.setCellStyle(creatBaseCellStyle(workbook));
        cell.setCellValue(val);
    }

    /**
     * 自动设置列宽
     * @param sheet
     * @param i
     * @param value
     */
    public static void initColWidth(XSSFSheet sheet, int i, String value) {
        int columnWidth = sheet.getColumnWidth(i);
        if (columnWidth < (value.getBytes().length) * 256) {
            sheet.setColumnWidth(i, (value.getBytes().length) * 256 );
        }
    }

    /**
     *
     * @param sheet
     * @param startCol 开始的列号
     * @param endCol  结束的列号
     * @param useMergedCells 合并单元格的
     */
    public static void autoSizeColumn(XSSFSheet sheet, int startCol, int endCol, boolean useMergedCells){
        for(int i = startCol;i<= endCol;i++){
            autoSizeColumn(sheet,i,useMergedCells);
        }
    }

    /**
     *  自适应列宽。稍微兼容下有中文的列
     *  乘 1.5
     * @param sheet
     * @param column
     * @param useMergedCells
     */
    public static void autoSizeColumn(XSSFSheet sheet, int column, boolean useMergedCells){
        double width = SheetUtil.getColumnWidth(sheet, column, useMergedCells);

        if (width != -1) {
            width *= 256*1.7;
            // The maximum column width for an individual cell is 255 characters
            int maxColumnWidth = 255*256;
            if (width > maxColumnWidth) {
                width = maxColumnWidth;
            }
            sheet.setColumnWidth(column, (int)(width));
            sheet.getColumnHelper().setColBestFit(column, true);
        }
    }

    /**
     * 设置基础的Excel导出。没有任何样式
     *
     * @param workbook
     * @param sheet
     * @param nextRownum
     * @param cols
     * @param dataList
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static int setCommonData(XSSFWorkbook workbook, XSSFSheet sheet, int nextRownum, List<String> cols, List dataList) throws InvocationTargetException, IllegalAccessException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        PropertyDescriptor propertyDescriptor;
        Method readMethod;
        for (Object data : dataList) {
            XSSFRow row = sheet.createRow(nextRownum++);
            for (int i = 0; i < cols.size(); i++) {
//                XSSFCell cell = row.createCell(i);
                propertyDescriptor = BeanUtils.getPropertyDescriptor(data.getClass(), cols.get(i));
                readMethod = propertyDescriptor.getReadMethod();
                Object value = readMethod.invoke(data);
                if (Objects.nonNull(value)) {
                    if (value instanceof Date) {
                        value = simpleDateFormat.format(value);
                    }
                    setBaseCellVal(workbook, row, i, value.toString());
                } else {
                    setBaseCellVal(workbook, row, i, null);
                }

            }
        }

        return nextRownum;
    }

    /**
     * 导出file文件
     * @param response
     * @param file
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void writeFileToResp(HttpServletResponse response, File file) throws FileNotFoundException, IOException {
        byte[] buf = new byte[4096];
        InputStream inStream = new FileInputStream(file);
        Throwable var5 = null;

        try {
            ServletOutputStream outputStream = response.getOutputStream();
            Throwable var7 = null;

            try {
                int readLength;
                while((readLength = inStream.read(buf)) != -1) {
                    outputStream.write(buf, 0, readLength);
                }

                outputStream.flush();
            } catch (Throwable var30) {
                var7 = var30;
                throw var30;
            } finally {
                if (outputStream != null) {
                    if (var7 != null) {
                        try {
                            outputStream.close();
                        } catch (Throwable var29) {
                            var7.addSuppressed(var29);
                        }
                    } else {
                        outputStream.close();
                    }
                }

            }
        } catch (Throwable var32) {
            var5 = var32;
            throw var32;
        } finally {
            if (inStream != null) {
                if (var5 != null) {
                    try {
                        inStream.close();
                    } catch (Throwable var28) {
                        var5.addSuppressed(var28);
                    }
                } else {
                    inStream.close();
                }
            }

        }

    }

    /**
     * 反射获取值
     *
     * @param object
     * @param fieldName
     * @return
     */
    public static Object getValue(Object object, String fieldName) throws InvocationTargetException, IllegalAccessException {
        if("".equals(fieldName)) {
            return "";
        }
        PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(object.getClass(), fieldName);
        Method readMethod = propertyDescriptor.getReadMethod();
        Object value = readMethod.invoke(object);
        return value;
    }

    /**
     * 把dto中的数据设置到Excel行中
     *
     * @param row
     * @param object
     */
    public static void setData(XSSFWorkbook workbook , XSSFSheet sheet, XSSFRow row, Object object , List<String> colGetMethods) throws InvocationTargetException, IllegalAccessException {
        for (int i = 0; i < colGetMethods.size(); i++) {
            XSSFCell cell = row.createCell(i);
            Object value = getValue(object, colGetMethods.get(i));
            if(value instanceof Date){
                value = simpleDateFormat.format(value);
            }
            if(value instanceof Double || value instanceof BigDecimal){
                XSSFCellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setAlignment(HorizontalAlignment.RIGHT);
                value=df.format(value);
                cell.setCellStyle(cellStyle);
            }
            if (Objects.nonNull(value)) {
                initColWidth(sheet, i, value.toString());
                cell.setCellValue(value.toString());
            }

        }
    }
    public static void setData(XSSFWorkbook workbook , XSSFSheet sheet, XSSFRow row, Map<String,String> rowData , List<String> attrList){
        for (int i = 0; i < attrList.size(); i++) {
            XSSFCell cell = row.createCell(i);
            String value=rowData.get(attrList.get(i));
            if (Objects.nonNull(value)) {
                initColWidth(sheet, i, value.toString());
                cell.setCellValue(value);
            }
        }
    }
}
