package leaf.plugin.export.components;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.mybatis.util.StringUtil;
import com.hand.hap.system.dto.ResponseData;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ExcelExportUtil {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public void createExcel(HttpServletResponse response, JSONArray colunmConfig, Object result) {
//            开始生成文件
        int maxRowNumber = 1000000;
        try {

            JSONArray realColumns = parseColumns(colunmConfig);
//                初始化excel
            SXSSFWorkbook wb = new SXSSFWorkbook(50);
            CellStyle dateFormat = wb.createCellStyle();
//            dateFormat.setDataFormat(wb.createDataFormat().getFormat("yyyy-MM-DD HH:mm"));
            dateFormat.setDataFormat(wb.createDataFormat().getFormat("yyyy-MM-DD"));
            dateFormat.setAlignment(HorizontalAlignment.CENTER);

            DataFormat dataFormat = wb.createDataFormat();
            CellStyle textStyle = wb.createCellStyle();
            textStyle.setDataFormat(dataFormat.getFormat("@"));

            final AtomicInteger rowCount = new AtomicInteger(0);
            final AtomicInteger sheetIndex = new AtomicInteger(1);
            final SXSSFSheet[] sheet = {wb.createSheet()};

            createHeaderRow(colunmConfig, wb, sheet[0], rowCount);
            List resultList = new ArrayList();
            if (result instanceof List) {
                 resultList = (List) result;
            } else if (result instanceof ResponseData) {
                 resultList = ((ResponseData) result).getRows();
            }
            for (int i = 0; i < resultList.size(); i++) {
                Object resultItem = resultList.get(i);
                createSheet(wb, sheet[0], resultItem, rowCount,
                        sheetIndex, maxRowNumber, colunmConfig, realColumns,
                        dateFormat, textStyle);
            }
            response.addHeader("Content-Disposition", "attachment;filename=excel.xlsx");
            wb.write(response.getOutputStream());

        } catch (Exception e) {
            logger.error("Export excel file failed.", e);
        }
    }

    private JSONArray parseColumns(JSONArray originColumns) {
        JSONArray columns = new JSONArray();
        if (originColumns == null) {
            return columns;
        }
        for (int i = 0; i < originColumns.size(); i++) {
            JSONObject originColumn = originColumns.getJSONObject(i);
            boolean hasColumn = originColumn.containsKey("column");
            if (hasColumn) {
//            有嵌套
                JSONArray nestColumn = originColumn.getJSONArray("column");
                columns.addAll(parseColumns(nestColumn));
            } else {
//                直接加上名字
                columns.add(originColumn);
            }
        }

        return columns;
    }

    private SXSSFSheet createSheet(SXSSFWorkbook wb, SXSSFSheet sheet, Object object, AtomicInteger count,
                                   AtomicInteger rowIndex, int rowMaxNumber, JSONArray columnInfos, JSONArray realColumnInfos,
                                   CellStyle dateFormat, CellStyle textStyle) {
        if (count.get() % rowMaxNumber == 0) {
            sheet = wb.createSheet();
            count.set(0);
            createHeaderRow(columnInfos, wb, sheet, count);
        }
        SXSSFRow row = sheet.createRow(count.getAndIncrement());

        createRow(realColumnInfos, object, row, dateFormat, textStyle,wb);
        return sheet;
    }

    private void createRow(JSONArray columnInfos, Object object, SXSSFRow row, CellStyle dateFormat, CellStyle textStyle, SXSSFWorkbook wb) {
        CellStyle hssfCellStyleDouble = wb.createCellStyle();
        DataFormat dataFormat = wb.createDataFormat(); // 此处设置数据格式
        hssfCellStyleDouble.setDataFormat(dataFormat.getFormat("#,##0.00"));//保留1位小数点

        for (int columnIndex = 0; columnIndex < columnInfos.size(); columnIndex++) {
            Object fieldObject = null;
            JSONObject columnInfo = columnInfos.getJSONObject(columnIndex);
            String columnName = columnInfo.getString("name");
            try {
                if (object instanceof ResultSet) {
                    fieldObject = ((ResultSet) object).getObject(columnName);
                } else if (object instanceof Map) {
                    fieldObject = ((Map) object).get(columnName);
                } else {
                    fieldObject = PropertyUtils.getProperty(object, columnName);
                }

            } catch (Exception e) {
                logger.trace("Get value from object failed. Try use camelCase.", e);
                try {
                    fieldObject = PropertyUtils.getProperty(object, StringUtil.underlineToCamelhump(columnName));
                } catch (Exception e1) {
                    logger.trace("Get value from object by using camel case failed.", e);

                }
            }
//            String type = columnInfos.get(columnIndex).getType();
//            FIXME: modelOutput中提供的列配置中没有类型，暂时全部当做字符串
            String type = detectType(fieldObject);
            SXSSFCell cell = row.createCell(columnIndex);

            DecimalFormat df = new DecimalFormat("#,##0.00");

            if (null == fieldObject) {
                cell.setCellType(CellType.STRING);
                cell.setCellValue((String) null);
            } else {
                switch (type.toUpperCase(Locale.CHINA)) {
                    case "NUMBER":
                    case "FLOAT":
                    case "INT":
                    case "INTEGER":
                    case "LONG":
                    case "DOUBLE":
                        cell.setCellStyle(hssfCellStyleDouble);
                        cell.setCellType(CellType.NUMERIC);
                        cell.setCellValue(Double.valueOf(fieldObject.toString()));
                        break;
                    case "DATE":
                        cell.setCellStyle(dateFormat);
                        cell.setCellValue((Date) fieldObject);
                        break;
                    case "BOOLEAN":
                        cell.setCellType(CellType.BOOLEAN);
                        if (fieldObject instanceof Boolean) {
                            cell.setCellValue((Boolean) fieldObject);
                        } else {
                            cell.setCellValue(fieldObject.toString());
                        }
                        break;
                    case "CLOB":
                        cell.setCellType(CellType.STRING);
                        cell.setCellValue(clobToString((Clob) fieldObject));
                        break;
                    default:
                        cell.setCellType(CellType.STRING);
                        String value = fieldObject.toString();
                        if (value != null && value.contains("00:00:00.0")) {
                            value = value.replace("00:00:00.0", "");
                        }
                        cell.setCellValue(value);
                        break;
                }
            }
        }
    }

    private String detectType(Object fieldObject) {
        String type = "STRING";
        if (fieldObject == null) {
            return type;
        }
        if (fieldObject instanceof Long) {
            type = "LONG";
        } else if (fieldObject instanceof Date) {
            type = "DATE";
        } else if(fieldObject instanceof Double) {
            type = "DOUBLE";
        } else if(fieldObject instanceof Float) {
            type = "FLOAT";
        } else if(fieldObject instanceof BigDecimal){
            type = "DOUBLE";
        } else if(fieldObject instanceof Clob){
            type = "CLOB";
        }

        return type;
    }

    public static String clobToString(Clob clob) {
        if (clob == null)
            return null;

        StringBuffer sb = new StringBuffer(65535);// 64K
        Reader clobStream = null;
        try {
            clobStream = clob.getCharacterStream();
            char[] b = new char[60000];// 每次获取60K
            int i = 0;
            while ((i = clobStream.read(b)) != -1) {
                sb.append(b, 0, i);
            }
        } catch (Exception ex) {
            sb = null;
        } finally {
            try {
                if (clobStream != null) {
                    clobStream.close();
                }
            } catch (Exception e) {
            }
        }
        if (sb == null)
            return null;
        else
            return sb.toString();
    }

    private void createHeaderRow(JSONArray columnInfos, SXSSFWorkbook wb, SXSSFSheet sheet, AtomicInteger rowCount) {
        List<ColumnInfo> columns = columnInfos.toJavaList(ColumnInfo.class);
        int maxDepth = getMaxDepth(columns);
//        只有单行表头的
        if (maxDepth == 1) {
            createSingleHeaderRow(wb, sheet, columns, rowCount);
            return;
        }
        int rowIndex = rowCount.get();
        for (int i = 0; i < maxDepth; i++) {
            rowCount.getAndIncrement();
        }

        createSubHeaderRow(wb, sheet, columns, maxDepth, 1, rowIndex, 0);
    }

    private void createSubHeaderRow(SXSSFWorkbook wb, SXSSFSheet sheet, List<ColumnInfo> columns, int maxDepth, int currentDepth, int rowIndex, int cellIndex) {
        if (CollectionUtils.isEmpty(columns)) {
            return;
        }

        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        SXSSFRow row = getOrCreateRow(sheet, rowIndex);

        int currentCellIndex = cellIndex;

        for (int i = 0; i < columns.size(); i++) {
            ColumnInfo columnInfo = columns.get(i);
            int cellCount = columnInfo.getCellCount();
            SXSSFCell cell = row.createCell(currentCellIndex);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(columnInfo.getPrompt());

            if (cellCount == 1) {
//                单列,合并列单元格
                if (rowIndex != (rowIndex + maxDepth - currentDepth)) {
                    sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex + maxDepth - currentDepth, currentCellIndex, currentCellIndex));
                }
            } else {
//                多列的，必然有嵌套，合并行单元格
                if (currentCellIndex != currentCellIndex + cellCount - 1) {
                    sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, currentCellIndex, currentCellIndex + cellCount - 1));
                }
                createSubHeaderRow(wb, sheet, columnInfo.getColumn(), maxDepth, currentDepth + 1, rowIndex + 1, currentCellIndex);
            }
            currentCellIndex += cellCount;
        }

    }

    private SXSSFRow getOrCreateRow(SXSSFSheet sheet, int index) {
        SXSSFRow row = sheet.getRow(index);
        if (row == null) {
            row = sheet.createRow(index);
        }
        return row;
    }

    private void createSingleHeaderRow(SXSSFWorkbook wb, SXSSFSheet sheet, List<ColumnInfo> columns, AtomicInteger rowCount) {
        int rowIndex = rowCount.getAndIncrement();
        SXSSFRow firstRow = sheet.createRow(rowIndex);
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        for (int i = 0; i < columns.size(); i++) {
            ColumnInfo columnInfo = columns.get(i);
            SXSSFCell firstCell = firstRow.createCell(i);
            firstCell.setCellValue(columnInfo.getPrompt());
            // 设置列宽度
            Integer width = columnInfo.getWidth();
            if (width == null) {
                width = 150;
            }
            sheet.setColumnWidth(i, width * 16);
            firstCell.setCellStyle(cellStyle);
        }

    }

    private int getMaxDepth(List<ColumnInfo> columns) {
        if (CollectionUtils.isEmpty(columns)) {
            return 1;
        }
        return columns.stream().map(ColumnInfo::getDepth).sorted(Comparator.reverseOrder()).findFirst().get();
    }

}
