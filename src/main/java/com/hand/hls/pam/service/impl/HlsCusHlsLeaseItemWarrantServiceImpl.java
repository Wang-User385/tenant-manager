package com.hand.hls.pam.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.excel.ExcelException;
import com.hand.hap.system.dto.Code;
import com.hand.hap.system.dto.CodeValue;
import com.hand.hap.system.mapper.CodeMapper;
import com.hand.hap.system.mapper.CodeValueMapper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.dto.HlsCusBpMasterBankAccount;
import com.hand.hls.csh.dto.HlsCusCshBankAccount;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.fnd.dto.FndInterfaceLines;
import com.hand.hls.fnd.dto.HLSCurrency;
import com.hand.hls.fnd.mapper.FndInterfaceLinesMapper;
import com.hand.hls.pam.dto.HlsCusHlsLeaseItemWarrant;
import com.hand.hls.pam.mapper.HlsCusHlsLeaseItemWarrantMapper;
import com.hand.hls.pam.service.HlsCusHlsLeaseItemWarrantService;
import com.hand.hls.utils.ResMessageException;
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

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusHlsLeaseItemWarrantServiceImpl extends BaseServiceImpl<HlsCusHlsLeaseItemWarrant> implements HlsCusHlsLeaseItemWarrantService {


    @Autowired
    private FndInterfaceLinesMapper fndInterfaceLinesMapper;

    @Autowired
    private HlsCusHlsLeaseItemWarrantMapper hlsCusHlsLeaseItemWarrantMapper;
    @Autowired
    private CodeValueMapper codeValueMapper;
    @Autowired
    private CodeMapper codeMapper;

    @Override
    public void warrantImport(IRequest iRequest, Long hdId ,Long projectId) throws ExcelException, Exception, ParseException {
//        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        List<FndInterfaceLines> fndInterfaceLinesList = getInterfaceData(hdId, 1L);
        String warrantNameCompare = null;

        for (FndInterfaceLines fndInterfaceLine : fndInterfaceLinesList) {
            HlsCusHlsLeaseItemWarrant record = new HlsCusHlsLeaseItemWarrant();

            //客户名称
            String bpName = fndInterfaceLine.getAttributes_1();
            //业务角色
            String bpType = fndInterfaceLine.getAttributes_2();
            //权证名称
            String warrantName = fndInterfaceLine.getAttributes_3();
            //文件编号
            String fileNumber = fndInterfaceLine.getAttributes_4();
            //入库编号
            String warehouseNumber = fndInterfaceLine.getAttributes_5();
            //权证类型
            String warrantType= fndInterfaceLine.getAttributes_6();
            //档案类型
            String  fileType = fndInterfaceLine.getAttributes_7();
            //权证状态
            String warrantStatus = fndInterfaceLine.getAttributes_8();
            //正本/副本
            String documentType = fndInterfaceLine.getAttributes_9();
            //原件/复印件
            String documentClass = fndInterfaceLine.getAttributes_10();
            //文件数量
            String numberFiles = fndInterfaceLine.getAttributes_11();
            //权证说明
            String warrantExplanation = fndInterfaceLine.getAttributes_12();
            //备注
            String description= fndInterfaceLine.getAttributes_13();

            //校验权证名称不能重复
            if(warrantName.equalsIgnoreCase(warrantNameCompare) ){
                throw new RuntimeException("excel第" + fndInterfaceLine.getLineNumber() + "行:权证名称重复！");
            }else{
                warrantNameCompare = warrantName;
            }

            //校验 档案类型 是否定义
            HlsCusHlsLeaseItemWarrant warrantDoucument = new HlsCusHlsLeaseItemWarrant();
            warrantDoucument.setDocumentCategory("WARRANT_TYPE");
            //档案类型 ：代保管物 一类档案  其他
            warrantDoucument.setDocumentCategoryN(fileType);
            List<HlsCusHlsLeaseItemWarrant> categoryList =hlsCusHlsLeaseItemWarrantMapper.queryCategoryInfo(warrantDoucument);
            if (categoryList.isEmpty()) {
                throw new RuntimeException("excel第" + fndInterfaceLine.getLineNumber() + "行:档案类型没有在系统中维护！");
            }

            //校验 权证类型 是否定义
            warrantDoucument.setBusinessTypeN(warrantType);
            List<HlsCusHlsLeaseItemWarrant> businessList = hlsCusHlsLeaseItemWarrantMapper.queryDocumentInfo(warrantDoucument);
            if(businessList.isEmpty()){
                throw new RuntimeException("excel第" + fndInterfaceLine.getLineNumber() + "行:权证类型没有在系统中维护！");
            }

            //校验权证类型是否 与档案类型匹配
            warrantDoucument.setBusinessType(categoryList.get(0).getBusinessType());
            warrantDoucument.setBusinessDocumentType(businessList.get(0).getBusinessDocumentType());
            List<HlsCusHlsLeaseItemWarrant> businessDocumentList = hlsCusHlsLeaseItemWarrantMapper.queryBusinessDocumentInfo(warrantDoucument);
            if(businessDocumentList.isEmpty()){
                throw new RuntimeException("excel第" + fndInterfaceLine.getLineNumber() + "行:权证类型和档案类型 不匹配！");
            }

            Code code = new Code();
            code.setCode("BP.BP_MASTER_ROLE_CARD");
            List<Code> codes = codeMapper.select(code);
            if (codes.size() != 1) {
                throw new ResMessageException("请定义编码生成规则的系统代码！");
            }
            code = codes.get(0);
            CodeValue codeValue = new CodeValue();
            codeValue.setCodeId(code.getCodeId());
            codeValue.setMeaning(bpType);
            List<CodeValue> codeValues = codeValueMapper.select(codeValue);
            codeValues = codeValues.stream().filter(item -> bpType.equals(item.getMeaning())).collect(Collectors.toList());
            if(codeValues.size()!=1){
                throw new ResMessageException("没有该业务类型！");
            }else {
                codeValue = codeValues.get(0);
            }

            //赋值 插入
            record.setProjectId(String.valueOf(projectId));
            record.setBpName(bpName);
            record.setWarrantName(warrantName);
            record.setWarrantType(businessDocumentList.get(0).getBusinessDocumentTypeN());
            record.setFileType(categoryList.get(0).getBusinessType());
            record.setWarrantStatus(warrantStatus);
            record.setDocumentType(documentType);
            record.setDocumentClass(documentClass);
            record.setDescription(documentClass);
            record.setWarrantExplanation(warrantExplanation);
            record.setDescription(description);

            record.setBpType(codeValue.getValue());
            record.setFileNumber(fileNumber);
            record.setWarehouseNumber(warehouseNumber);
            record.setNumberFiles(Long.valueOf(numberFiles));
            //插入
            self().insertSelective(iRequest, record);
        }

    }


    //获取接口表数据
    public List<FndInterfaceLines> getInterfaceData(Long hdId, Long readLine) {

        FndInterfaceLines fndInterfaceLines = new FndInterfaceLines();
        fndInterfaceLines.setHeaderId(hdId);
        fndInterfaceLines.setReadLine(readLine);
        List<FndInterfaceLines> fndInterfaceLinesList = fndInterfaceLinesMapper.fndInterfaceLinesDetailQuery(fndInterfaceLines);
        return fndInterfaceLinesList;
    }
}