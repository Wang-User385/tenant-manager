package com.hand.hls.pam.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.excel.ExcelException;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fnd.dto.FndInterfaceLines;
import com.hand.hls.fnd.mapper.FndInterfaceLinesMapper;
import com.hand.hls.pam.dto.HlsCusLeaseItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.pam.dto.HlsLeaseItemDetail;
import com.hand.hls.pam.service.IHlsLeaseItemDetailService;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsLeaseItemDetailServiceImpl extends BaseServiceImpl<HlsLeaseItemDetail> implements IHlsLeaseItemDetailService{

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
            if (objects[i] == null || "".equals(objects[i]) || "null".equals(objects[i]) || "".equals(objects[i].toString().trim())) {
                throw new RuntimeException(message);
            }
        }
    }

//    校验登记时间、到期日是否正确
    public static void validateTime(String message, Date start,Date end){
        if (start.after(end)){
            throw new RuntimeException(message);
        }
    }

    public static HashMap<String, String> classPage(){
        HashMap<String, String> map = new HashMap<>();
        map.put("动产抵押","MOVABLE_PROPERTY_PLEDGE");
        map.put("不动产抵押","NO_MOVABLE_PROPERTY_PLEDGE");
        map.put("应收账款质押","SHOUKUAN_PROPERTY_PLEDGE");
        map.put("股权质押","GUQUAN_PROPERTY_PLEDGE");
        map.put("收益权质押","SHOUYIQUAN_PROPERTY_PLEDGE");
        return map;
    }

    @Override
    public void receiptImportPledgeDc(IRequest iRequest, Long hdId, Long leaseItemId, String patternDet) throws ExcelException, SQLException, ParseException {
        List<FndInterfaceLines> fndInterfaceLinesList = getInterfaceData(hdId, 1L);

        HashMap<String, String> map = classPage();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        for (FndInterfaceLines fndInterfaceLine : fndInterfaceLinesList) {
            HlsLeaseItemDetail record = new HlsLeaseItemDetail();

            if(patternDet.equals("MOVABLE_PROPERTY_PLEDGE")){
                //序号
                String seq = fndInterfaceLine.getAttributes_1();
                //抵押物名称
                String fullName = fndInterfaceLine.getAttributes_2();
                // 抵押人
                String mortgagor = fndInterfaceLine.getAttributes_3();
                //抵质押分类
                String patternType = fndInterfaceLine.getAttributes_4();
                //抵质押状态
                String pledgeState = fndInterfaceLine.getAttributes_5();
                // 数量
                String quantity = fndInterfaceLine.getAttributes_6();
                // 计量单位
                String uom = fndInterfaceLine.getAttributes_7();
                // 单价
                String price = fndInterfaceLine.getAttributes_8();
                // 原购置价
                String originalAssetValue = fndInterfaceLine.getAttributes_9();
                // 市场价值/评估价值
                String assetValue = fndInterfaceLine.getAttributes_10();
                // 规格型号
                String specification = fndInterfaceLine.getAttributes_11();
                // 供应商
                String venderName = fndInterfaceLine.getAttributes_12();
                // 存放位置
                String installationSite = fndInterfaceLine.getAttributes_13();

                // 登记机关
                String registrationAgency = fndInterfaceLine.getAttributes_14();
                // 登记编号
                String internationalRegistrationMsn = fndInterfaceLine.getAttributes_15();


                // 登记时间
                Date mortgageDateFrom = null;
                if(fndInterfaceLine.getAttributes_16()!=null){
                    try {
                        mortgageDateFrom = df.parse(fndInterfaceLine.getAttributes_16());
                    }catch (Exception e){
                        throw new RuntimeException("登记时间格式错误！");
                    }
                }
                // 登记到期日
                Date mortgageDateTo = null;
                if(fndInterfaceLine.getAttributes_17()!=null){
                    // 登记到期日
                    try {
                        mortgageDateTo = df.parse(fndInterfaceLine.getAttributes_17());
                    }catch (Exception e){
                        throw new RuntimeException("登记到期日格式错误！");
                    }
                }

                // 备注
                String description = fndInterfaceLine.getAttributes_18();

                //必填校验
            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:抵押物名称不能为空", fullName);
            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:抵押人不能为空", mortgagor);
            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:抵质押分类不能为空", patternType);
            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:抵（质）押状态不能为空", pledgeState);
            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:原购置价不能为空", originalAssetValue);
            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:市场价值/评估价值不能为空", assetValue);
                if (fndInterfaceLine.getAttributes_16()!=null && fndInterfaceLine.getAttributes_17()!=null){
                    validateTime("excel第"+fndInterfaceLine.getLineNumber()+"行，登记时间不可小于登记到期日",mortgageDateFrom,mortgageDateTo);
                }

                if (pledgeState.equals("已办理")){
                    record.setPledgeState("MORTGAGE");
                } else {
                    record.setPledgeState("IN_PROCESS");
                }

                record.setLeaseItemId(leaseItemId);
                record.setPatternDet(patternDet);

                record.setPattern(map.get(patternType));
                record.setPatternN(patternType);
                record.setPledgeStateN(pledgeState);
                record.setLeaseFullName(fullName);
                record.setPrice(Double.valueOf(price));
                record.setQuantity(quantity);
                record.setSpecificationModel(specification);
                record.setLeaseMortgagor(mortgagor);
                record.setUom(uom);
                record.setPurchasePrice(Double.valueOf(originalAssetValue));
                record.setEvaluationValue(Double.valueOf(assetValue));
                record.setLocated(installationSite);
                record.setDescription(description);
                record.setVenderName(venderName);
                record.setRegistrationAgency(registrationAgency);
                record.setInternationalRegistrationMsn(internationalRegistrationMsn);
                record.setMortgageDateFrom(mortgageDateFrom);
                record.setMortgageDateTo(mortgageDateTo);

            }else if(patternDet.equals("NO_MOVABLE_PROPERTY_PLEDGE")){

                //序号
                String seq = fndInterfaceLine.getAttributes_1();
                //抵押物名称
                String fullName = fndInterfaceLine.getAttributes_2();
                // 抵押人
                String mortgagor = fndInterfaceLine.getAttributes_3();
                //抵质押分类
                String patternType = fndInterfaceLine.getAttributes_4();
                //抵（质）押状态
                String pledgeState = fndInterfaceLine.getAttributes_5();

                // 坐落位置
                String installationSite = fndInterfaceLine.getAttributes_6();
                // 面积（㎡）
                String floorArea = fndInterfaceLine.getAttributes_7();
                // 原购置价
                String originalAssetValue = fndInterfaceLine.getAttributes_8();
                // 市场价值/评估价值
                String assetValue = fndInterfaceLine.getAttributes_9();
                // 不动产权证号
                String equityNumber = fndInterfaceLine.getAttributes_10();


                // 登记机关
                String registrationAgency = fndInterfaceLine.getAttributes_11();
                // 登记编号
                String internationalRegistrationMsn = fndInterfaceLine.getAttributes_12();


                // 登记时间
                Date mortgageDateFrom = null;
                if(fndInterfaceLine.getAttributes_13()!=null){
                    try {
                        mortgageDateFrom = df.parse(fndInterfaceLine.getAttributes_13());
                    }catch (Exception e){
                        throw new RuntimeException("登记时间格式错误！");
                    }
                }
                // 登记到期日
                Date mortgageDateTo = null;
                if(fndInterfaceLine.getAttributes_14()!=null){
                    // 登记到期日
                    try {
                        mortgageDateTo = df.parse(fndInterfaceLine.getAttributes_14());
                    }catch (Exception e){
                        throw new RuntimeException("登记到期日格式错误！");
                    }
                }

                // 备注
                String description = fndInterfaceLine.getAttributes_15();

                //必填校验
                validate("excel第" + fndInterfaceLine.getLineNumber() + "行:抵押物名称不能为空", fullName);
                validate("excel第" + fndInterfaceLine.getLineNumber() + "行:抵押人不能为空", mortgagor);
                validate("excel第" + fndInterfaceLine.getLineNumber() + "行:抵质押分类不能为空", patternType);
                validate("excel第" + fndInterfaceLine.getLineNumber() + "行:抵（质）押状态不能为空", pledgeState);
                validate("excel第" + fndInterfaceLine.getLineNumber() + "行:原购置价不能为空", originalAssetValue);
                validate("excel第" + fndInterfaceLine.getLineNumber() + "行:市场价值/评估价值不能为空", assetValue);
                if (fndInterfaceLine.getAttributes_13()!=null && fndInterfaceLine.getAttributes_14()!=null){
                    validateTime("excel第"+fndInterfaceLine.getLineNumber()+"行，登记时间不可小于登记到期日",mortgageDateFrom,mortgageDateTo);
                }



                if (pledgeState.equals("已办理")){
                    record.setPledgeState("MORTGAGE");
                } else {
                    record.setPledgeState("IN_PROCESS");
                }

                record.setLeaseItemId(leaseItemId);
                record.setPatternDet(patternDet);

                record.setPattern(map.get(patternType));
                record.setPatternN(patternType);
                record.setPledgeStateN(pledgeState);
                record.setLeaseFullName(fullName);
                record.setLeaseMortgagor(mortgagor);
                record.setLocated(installationSite);
                if(floorArea!=null){
                    record.setArea(Double.valueOf(floorArea));
                }

                if(originalAssetValue!=null){
                    record.setPurchasePrice(Double.valueOf(originalAssetValue));
                }

                if(assetValue!=null){
                    record.setEvaluationValue(Double.valueOf((assetValue)));
                }



                record.setRealEstateNumber(equityNumber);
                record.setRegistrationAgency(registrationAgency);
                record.setInternationalRegistrationMsn(internationalRegistrationMsn);
                record.setMortgageDateFrom(mortgageDateFrom);
                record.setMortgageDateTo(mortgageDateTo);
                record.setDescription(description);

            }else if(patternDet.equals("DZ_PROPERTY_PLEDGE")){
//序号
                String seq = fndInterfaceLine.getAttributes_1();
                // 出质人
                String mortgagor = fndInterfaceLine.getAttributes_2();
                // 标的方
                String pledgeContractNum = fndInterfaceLine.getAttributes_3();
                // 质押财产价值
                String pledgeAssetValue = fndInterfaceLine.getAttributes_4();
                // 市场价值/评估价值
                String assetValue = fndInterfaceLine.getAttributes_5();
                // 质押财产描述
                String pledgeAssetDec = fndInterfaceLine.getAttributes_6();

                //抵质押分类
                String patternType = fndInterfaceLine.getAttributes_7();
                //抵（质）押状态
                String pledgeState = fndInterfaceLine.getAttributes_8();

                // 登记机关
                String registrationAgency = fndInterfaceLine.getAttributes_9();
                // 登记编号
                String internationalRegistrationMsn = fndInterfaceLine.getAttributes_10();


                // 登记时间
                Date mortgageDateFrom = null;
                if(fndInterfaceLine.getAttributes_11()!=null){
                    try {
                        mortgageDateFrom = df.parse(fndInterfaceLine.getAttributes_11());
                    }catch (Exception e){
                        throw new RuntimeException("登记时间格式错误！");
                    }
                }
                // 登记到期日
                Date mortgageDateTo = null;
                if(fndInterfaceLine.getAttributes_12()!=null){
                    // 登记到期日
                    try {
                        mortgageDateTo = df.parse(fndInterfaceLine.getAttributes_12());
                    }catch (Exception e){
                        throw new RuntimeException("登记到期日格式错误！");
                    }
                }

                // 备注
                String description = fndInterfaceLine.getAttributes_13();


                //必填校验
                validate("excel第" + fndInterfaceLine.getLineNumber() + "行:出质人不能为空", mortgagor);
                validate("excel第" + fndInterfaceLine.getLineNumber() + "行:质押合同编号不能为空", pledgeContractNum);
                validate("excel第" + fndInterfaceLine.getLineNumber() + "行:质押财产价值不能为空", pledgeAssetValue);
                validate("excel第" + fndInterfaceLine.getLineNumber() + "行:市场价值/评估价值不能为空", assetValue);
                validate("excel第" + fndInterfaceLine.getLineNumber() + "行:抵质押分类不能为空", patternType);
                validate("excel第" + fndInterfaceLine.getLineNumber() + "行:抵（质）押状态不能为空", pledgeState);
                if (fndInterfaceLine.getAttributes_11()!=null && fndInterfaceLine.getAttributes_12()!=null){
                    validateTime("excel第"+fndInterfaceLine.getLineNumber()+"行，登记时间不可小于登记到期日",mortgageDateFrom,mortgageDateTo);
                }

                record.setLeaseItemId(leaseItemId);
                record.setPatternDet(patternDet);

                record.setPledgeContractNum(pledgeContractNum);
                if(pledgeAssetValue!=null){
                    record.setPledgeAssetValue(Long.valueOf(pledgeAssetValue));
                }

                if(pledgeAssetDec!=null){
                    record.setPledgeAssetDec(pledgeAssetDec);
                }

                if(assetValue!=null){
                    record.setEvaluationValue(Double.valueOf(assetValue));
                }

                if (pledgeState.equals("已办理")){
                    record.setPledgeState("MORTGAGE");
                } else {
                    record.setPledgeState("IN_PROCESS");
                }

                record.setPattern(map.get(patternType));
                record.setPatternN(patternType);
                record.setPledgeStateN(pledgeState);

                record.setLeaseMortgagor(mortgagor);
                record.setRegistrationAgency(registrationAgency);
                record.setInternationalRegistrationMsn(internationalRegistrationMsn);
                record.setMortgageDateFrom(mortgageDateFrom);
                record.setMortgageDateTo(mortgageDateTo);
                record.setDescription(description);
            }




            //赋值 插入
            //record.setSeq(Long.valueOf(seq));

            //插入
            self().insertSelective(iRequest, record);
        }

    }


}