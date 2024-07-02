package com.hand.hls.fnd.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.excel.ExcelException;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fnd.dto.FndInterfaceLines;
import com.hand.hls.fnd.mapper.CockpitImportMapper;
import com.hand.hls.fnd.mapper.FndInterfaceLinesMapper;
import com.hand.hls.fp.dto.JcFundPlanSchedule;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.fnd.dto.CockpitImport;
import com.hand.hls.fnd.service.ICockpitImportService;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class CockpitImportServiceImpl extends BaseServiceImpl<CockpitImport> implements ICockpitImportService {
    @Autowired
    private FndInterfaceLinesMapper fndInterfaceLinesMapper;
    @Autowired
    private CockpitImportMapper cockpitImportMapper;

    @Override
    public List<CockpitImport> queryAdmin(IRequest iRequest, CockpitImport cockpitImport, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return cockpitImportMapper.queryAllReport(cockpitImport);
    }

    @Override
    public List<CockpitImport> queryProject(IRequest iRequest, CockpitImport cockpitImport, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return cockpitImportMapper.queryPrjAllReport(cockpitImport);
    }

    //获取接口表数据
    public List<FndInterfaceLines> getInterfaceData(Long hdId, Long readLine) {

        FndInterfaceLines fndInterfaceLines = new FndInterfaceLines();
        fndInterfaceLines.setHeaderId(hdId);
        fndInterfaceLines.setReadLine(readLine);
        List<FndInterfaceLines> fndInterfaceLinesList = fndInterfaceLinesMapper.fndInterfaceLinesDetailQuery(fndInterfaceLines);
        return fndInterfaceLinesList;
    }

    // 校验字段是否有值
    public static void validate(String message, Object... objects) {
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] == null || "".equals(objects[i]) || "null".equals(objects[i])) {
                throw new RuntimeException(message);
            }
        }
    }

    @Override
    public void receiptImport(IRequest iRequest, Long hdId) throws ExcelException, SQLException, ParseException {

        List<FndInterfaceLines> fndInterfaceLinesList = getInterfaceData(hdId, 2L);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        for (FndInterfaceLines fndInterfaceLine : fndInterfaceLinesList) {
            CockpitImport record = new CockpitImport();
            //填报年度
            String year = fndInterfaceLine.getAttributes_1();
            //填报月份
            String mon = fndInterfaceLine.getAttributes_2();
            // 填报项目
            String item = fndInterfaceLine.getAttributes_3();
            // 一部
            String one = fndInterfaceLine.getAttributes_4();
            // 二部
            String two = fndInterfaceLine.getAttributes_5();
            // 三部
            String three = fndInterfaceLine.getAttributes_6();
            // 战略部
            String td = fndInterfaceLine.getAttributes_7();
            //合计值
            String total = fndInterfaceLine.getAttributes_8();

            //必填校验
            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:填报年度不能为空", year);
            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:填报季度不能为空", mon);
            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:填报月份不能为空", item);

            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:填报周不能为空", one);
            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:填报周周开始日不能为空", two);
            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:填报周周结束日不能为空", three);
            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:填报周周结束日不能为空", td);
            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:填报周周结束日不能为空", total);

            //赋值 插入
            record.setYear(Long.valueOf(year));
            record.setMon(Long.valueOf(mon));
            record.setItem(item);
            record.setBusOneAmount(Double.valueOf(one));
            record.setBusTwoAmount(Double.valueOf(two));
            record.setBusThreeAmount(Double.valueOf(three));
            record.setBusTdAmount(Double.valueOf(td));
            record.setTotalAmount(Double.valueOf(total));


            if (year != null) {
                record.setYear(Long.valueOf(StringUtils.trim(year)));
            }
            if (mon != null) {
                record.setMon(Long.valueOf(StringUtils.trim(mon)));
            }
            if (item != null) {
                record.setItem(StringUtils.trim(item));
            }
            if (one != null) {
                record.setBusOneAmount(Double.valueOf(one));
            }
            if (two != null) {
                record.setBusTwoAmount(Double.valueOf(two));
            }
            if (three != null) {
                record.setBusThreeAmount(Double.valueOf(three));
            }
            if (td != null) {
                record.setBusTdAmount(Double.valueOf(td));
            }
            if (total != null) {
                record.setTotalAmount(Double.valueOf(total));
            }

            //插入
            self().insertSelective(iRequest, record);
        }


    }
}