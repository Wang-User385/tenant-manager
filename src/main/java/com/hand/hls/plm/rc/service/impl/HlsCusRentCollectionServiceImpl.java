package com.hand.hls.plm.rc.service.impl;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.plm.rc.dto.HlsCusRentCollection;
import com.hand.hls.plm.rc.mapper.HlsCusRentCollectionMapper;
import com.hand.hls.plm.rc.service.HlsCusIRentCollectionService;
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

/**
 * @Description:租金催收serviceImpl
 * @Author: Wty
 * @Date: Created om 21:38 2018/6/6
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusRentCollectionServiceImpl extends BaseServiceImpl<HlsCusRentCollection> implements HlsCusIRentCollectionService {

    @Autowired
    private HlsCusRentCollectionMapper mapper;

    /**
     * @Description:查询
     * @Author: Wty
     * @Date: Created om 15:43 2018/6/11
     */
    @Override
    public List<HlsCusRentCollection> queryAll(IRequest iRequest, HlsCusRentCollection rentCollection, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        rentCollection.setCompanyId(iRequest.getCompanyId());
        return mapper.queryAll(rentCollection);
    }

    @Override
    public int updateCollectionStatus(String procInstId, String collectionStatus) {
        return mapper.updateCollectionStatus(procInstId,collectionStatus);
    }



    //sheet名称
    private final static String SHEET_NAME = "sheet1";
    //文件名称
    private final static String FILE_NAME= "催收记录";
    private static final List<String> colNameList = Lists.newArrayList(
            "登记日期", "催收日期", "催收人员", "对方联系人","职务","联系方式","催收方式","催收进度","承诺下次还款日","承诺下次还款金额","后续措施");
    private static final List<String> colGetMethods = Lists.newArrayList("creationDate", "collectionTime", "collectionMember",
            "contactPerson","job","telephoneNumber","collectionMethodDesc","collectionResult","nextTimePayDate","nextTimePayAmount","collectionTake");
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void exportRentCollectionReport(HttpServletRequest request, HttpServletResponse response, HlsCusRentCollection rentCollection) throws IOException,InvocationTargetException, IllegalAccessException {
        XSSFWorkbook xwork = new XSSFWorkbook();
        XSSFSheet sheet = xwork.createSheet(SHEET_NAME);
        List<HlsCusRentCollection> rentCollectionList=mapper.queryAll(rentCollection);
        int dataRowNum = ExportExcelUtil.createCommonExcelHead(xwork, sheet, null, colNameList);
        for (HlsCusRentCollection collection : rentCollectionList) {
            XSSFRow row = sheet.createRow(dataRowNum++);
            setData(xwork, sheet, row, collection, colGetMethods);
        }
        ExportExcelUtil.IOWrite(xwork, null, request, response, FILE_NAME);

    }

    private void setData(XSSFWorkbook workbook, XSSFSheet sheet, XSSFRow row,HlsCusRentCollection collection, List<String> colGetMethods) throws InvocationTargetException, IllegalAccessException{
        DecimalFormat df = new DecimalFormat("###,##0.00");
        for (int i = 0; i < colGetMethods.size(); i++) {
            XSSFCell cell = row.createCell(i);
            Object value = ExportExcelUtil.getValue(collection, colGetMethods.get(i));
            if (value instanceof Date) {
                value = simpleDateFormat.format(value);
            }
            if (value instanceof Double) {
                XSSFCellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setAlignment(HorizontalAlignment.RIGHT);
                value = df.format(value);
                cell.setCellStyle(cellStyle);
            }
            if (Objects.nonNull(value)) {
                ExportExcelUtil.initColWidth(sheet, i, value.toString());
                cell.setCellValue(value.toString());
            }

        }
    }
}