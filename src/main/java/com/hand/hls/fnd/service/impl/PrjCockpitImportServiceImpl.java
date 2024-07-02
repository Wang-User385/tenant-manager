package com.hand.hls.fnd.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.excel.ExcelException;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fnd.dto.CockpitImport;
import com.hand.hls.fnd.dto.FndInterfaceLines;
import com.hand.hls.fnd.mapper.FndInterfaceLinesMapper;
import com.hand.hls.fnd.mapper.PrjCockpitImportMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.fnd.dto.PrjCockpitImport;
import com.hand.hls.fnd.service.PrjCockpitImportService;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class PrjCockpitImportServiceImpl extends BaseServiceImpl<PrjCockpitImport> implements PrjCockpitImportService{
    @Autowired
    private FndInterfaceLinesMapper fndInterfaceLinesMapper;
    @Autowired
    private PrjCockpitImportMapper cockpitImportMapper;

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
            PrjCockpitImport record = new PrjCockpitImport();
            //填报年度
            String year = fndInterfaceLine.getAttributes_1();
            //填报月份
            String mon = fndInterfaceLine.getAttributes_2();
            // 项目经理
            String hostProjectManagerN = fndInterfaceLine.getAttributes_3();
            PrjCockpitImport cockpitImportUser = new PrjCockpitImport();
            cockpitImportUser.setHostProjectManagerN(hostProjectManagerN);
            List<PrjCockpitImport> cockpitImportList = cockpitImportMapper.queryUser(cockpitImportUser);
            // 本年累计投放目标值
            String launchAmount = fndInterfaceLine.getAttributes_4();
            // 本年生息资产目标值
            String bearingAmount = fndInterfaceLine.getAttributes_5();
            // 本年收入目标值
            String incomeAmount = fndInterfaceLine.getAttributes_6();
            // 本年利润目标值
            String profitAmount = fndInterfaceLine.getAttributes_7();

            if(cockpitImportList.size()>0){
                record.setHostProjectManager(cockpitImportList.get(0).getHostProjectManager());
            }else{
                validate("excel第" + fndInterfaceLine.getLineNumber() + "行:项目经理不能为空或用户名不存在", hostProjectManagerN);
            }
            //必填校验
            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:填报年度不能为空", year);
            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:填报季度不能为空", mon);

            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:本年累计投放目标值不能为空", launchAmount);
            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:本年生息资产目标值不能为空", bearingAmount);
            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:本年收入目标值不能为空", incomeAmount);
            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:本年利润目标值不能为空", profitAmount);

            //赋值 插入
            record.setYear(Long.valueOf(year));
            record.setMon(Long.valueOf(mon));
            record.setLaunchAmount(Double.valueOf(launchAmount));
            record.setBearingAmount(Double.valueOf(bearingAmount));
            record.setIncomeAmount(Double.valueOf(incomeAmount));
            record.setProfitAmount(Double.valueOf(profitAmount));

            //插入
            self().insertSelective(iRequest, record);
        }


    }
}