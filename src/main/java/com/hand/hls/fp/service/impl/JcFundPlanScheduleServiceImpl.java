package com.hand.hls.fp.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.excel.ExcelException;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fnd.dto.FndInterfaceLines;
import com.hand.hls.fnd.mapper.FndInterfaceLinesMapper;
import com.hand.hls.pam.dto.HlsCusLeaseItemList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.fp.dto.JcFundPlanSchedule;
import com.hand.hls.fp.service.JcFundPlanScheduleService;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class JcFundPlanScheduleServiceImpl extends BaseServiceImpl<JcFundPlanSchedule> implements JcFundPlanScheduleService{
    @Autowired
    private FndInterfaceLinesMapper fndInterfaceLinesMapper;

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

        List<FndInterfaceLines> fndInterfaceLinesList = getInterfaceData(hdId, 3L);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        for (FndInterfaceLines fndInterfaceLine : fndInterfaceLinesList) {
            JcFundPlanSchedule record = new JcFundPlanSchedule();
            //填报年度
            String fillYear = fndInterfaceLine.getAttributes_1();
            //季度
            String fillQuarter = fndInterfaceLine.getAttributes_2();
            // 填报月份
            String fillMon = fndInterfaceLine.getAttributes_3();
            // 填报周
            String fillWeek = fndInterfaceLine.getAttributes_4();
            // 填报周周开始日
            String startDate = fndInterfaceLine.getAttributes_5();
            // 填报周周结束日
            String endDate = fndInterfaceLine.getAttributes_6();
            // 本周第1天
            String firstDay = fndInterfaceLine.getAttributes_7();
            // 本周第2天
            String secondDay = fndInterfaceLine.getAttributes_8();
            // 本周第3天
            String thirdDay = fndInterfaceLine.getAttributes_9();
            // 本周第4天
            String fourthDay = fndInterfaceLine.getAttributes_10();
            // 本周第5天
            String fifthDay = fndInterfaceLine.getAttributes_11();
            // 本周第6天
            String sixthDay = fndInterfaceLine.getAttributes_12();
            // 本周第7天
            String seventhDay = fndInterfaceLine.getAttributes_13();
            // 本周第8天
            String eighthDay = fndInterfaceLine.getAttributes_14();
            // 本周第9天
            String ninthDay = fndInterfaceLine.getAttributes_15();
            // 本周第10天
            String tenthDay = fndInterfaceLine.getAttributes_16();
            // 本周第11天
            String eleventhDay = fndInterfaceLine.getAttributes_17();
            // 本周第12天
            String twelfthDay = fndInterfaceLine.getAttributes_18();
            // 本周第13天
            String thirteenthDay = fndInterfaceLine.getAttributes_19();

            //必填校验
            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:填报年度不能为空", fillYear);
            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:填报季度不能为空", fillQuarter);
            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:填报月份不能为空", fillMon);

            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:填报周不能为空", fillWeek);
            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:填报周周开始日不能为空", startDate);
            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:填报周周结束日不能为空", endDate);
//            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:本周第一天不能为空", firstDay);
//            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:本周第二天不能为空", secondDay);
//            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:本周第三天不能为空", thirdDay);


            //赋值 插入
            record.setFillYear(Long.valueOf(fillYear));
            record.setFillQuarter(Long.valueOf(fillQuarter));
            record.setFillMon(Long.valueOf(fillMon));
            record.setFillWeek(Long.valueOf(fillWeek));
            record.setStartDate(format.parse(startDate));
            record.setEndDate(format.parse(endDate));

            if(firstDay != null){
                record.setFirstDay(format.parse(firstDay));
            }
            if(secondDay != null){
                record.setSecondDay(format.parse(secondDay));
            }
            if(thirdDay != null){
                record.setThirdDay(format.parse(thirdDay));
            }
            if(fourthDay != null){
                record.setFourthDay(format.parse(fourthDay));
            }
            if(fifthDay != null){
                record.setFifthDay(format.parse(fifthDay));
            }
            if(sixthDay != null){
                record.setSixthDay(format.parse(sixthDay));
            }
            if(seventhDay != null){
                record.setSeventhDay(format.parse(seventhDay));
            }
            if(eighthDay != null){
                record.setEighthDay(format.parse(eighthDay));
            }
            if(ninthDay != null){
                record.setNinthDay(format.parse(ninthDay));
            }
            if(tenthDay != null){
                record.setTenthDay(format.parse(tenthDay));
            }
            if(eleventhDay != null){
                record.setEleventhDay(format.parse(eleventhDay));
            }
            if(twelfthDay != null){
                record.setTwelfthDay(format.parse(twelfthDay));
            }
            if(thirteenthDay != null){
                record.setThirteenthDay(format.parse(thirteenthDay));
            }

            //插入
            self().insertSelective(iRequest, record);
        }


    }
}