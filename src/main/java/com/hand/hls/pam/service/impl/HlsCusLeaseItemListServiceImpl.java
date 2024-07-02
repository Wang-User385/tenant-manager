package com.hand.hls.pam.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.excel.ExcelException;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.dto.HlsCusBpMasterBankAccount;
import com.hand.hls.csh.dto.HlsCusCshBankAccount;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.fnd.dto.FndInterfaceLines;
import com.hand.hls.fnd.dto.HLSCurrency;
import com.hand.hls.fnd.mapper.FndInterfaceLinesMapper;
import com.hand.hls.pam.dto.HlsCusLeaseItem;
import com.hand.hls.pam.dto.HlsCusLeaseItemList;
import com.hand.hls.pam.service.IHlsCusLeaseItemListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author kalvin
 * @version 1.0
 * @Date 2021/3/9 10:04
 * @Description
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusLeaseItemListServiceImpl extends BaseServiceImpl<HlsCusLeaseItemList> implements IHlsCusLeaseItemListService {
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
    public void receiptImport(IRequest iRequest, Long hdId,Long leaseItemId) throws ExcelException, SQLException, ParseException {

        List<FndInterfaceLines> fndInterfaceLinesList = getInterfaceData(hdId, 2L);


        for (FndInterfaceLines fndInterfaceLine : fndInterfaceLinesList) {
            HlsCusLeaseItemList record = new HlsCusLeaseItemList();
            //序号
            String seq = fndInterfaceLine.getAttributes_1();
            //租赁物名称
            String assetName = fndInterfaceLine.getAttributes_2();
            // 租赁物价值
            String valuation = fndInterfaceLine.getAttributes_3();
            // 数量
            String quantity = fndInterfaceLine.getAttributes_4();
            // 规格
            String specification = fndInterfaceLine.getAttributes_5();
            // 生产厂家
            String manufacturer = fndInterfaceLine.getAttributes_6();
            // 品类
            String leaseType = fndInterfaceLine.getAttributes_7();
            // 序列/识别号
            String assetNum = fndInterfaceLine.getAttributes_8();
            // 发票编号
            String invoiceNum = fndInterfaceLine.getAttributes_9();
            // 照片编号
            String imgNum = fndInterfaceLine.getAttributes_10();
            // 坐落位置
            String installationSite = fndInterfaceLine.getAttributes_11();
            // 备注
            String description = fndInterfaceLine.getAttributes_12();

            //必填校验
            validate("excel第" + fndInterfaceLine.getLineNumber() + "列:租赁物名称不能为空", assetName);
//            validate("excel第" + fndInterfaceLine.getLineNumber() + "列:单价不能为空", price);
//            validate("excel第" + fndInterfaceLine.getLineNumber() + "列:数量不能为空", quantity);

//            validate("excel第" + fndInterfaceLine.getLineNumber() + "列:规格不能为空", specification);
//            validate("excel第" + fndInterfaceLine.getLineNumber() + "列:生产厂商不能为空", manufacturer);
//            validate("excel第" + fndInterfaceLine.getLineNumber() + "列:序列/识别号不能为空", assetNum);
//            validate("excel第" + fndInterfaceLine.getLineNumber() + "列:坐落位置不能为空", installationSite);


            //赋值 插入
//            record.setSeq(Long.valueOf(seq));
            record.setLeaseItemId(leaseItemId);
            record.setAssetName(assetName);
//            if (price != null){ //原值
//                if (Double.valueOf(price) < 0){
//                    throw new RuntimeException("请输入正确的单价！");
//                }
//                record.setOriginalAssetValue(Double.valueOf(price));
//            }
            if(quantity != null){//数量
                if (Double.valueOf(quantity) < 0){
                    throw new RuntimeException("请输入正确的数量！");
                }
                record.setQuantity(Double.valueOf(quantity));
            }
            if (valuation != null) {//估值
                record.setPrice(Double.valueOf(valuation));
            }
            if (specification != null){
                record.setSpecification(specification);
            }
            if (manufacturer != null){
                record.setManufacturer(manufacturer);
            }
            if(assetNum != null){
                record.setAssetNum(assetNum);
            }
            if (invoiceNum != null){
                record.setInvoiceNum(invoiceNum);
            }
            if (imgNum != null){
                record.setImgNum(imgNum);
            }
            if (installationSite != null){
                record.setInstallationSite(installationSite);
            }
            if (description != null){
                record.setDescription(description);
            }
            if (leaseType != null){
                record.setLeaseType(leaseType);
            }
            //插入
            self().insertSelective(iRequest, record);
        }


    }



}
