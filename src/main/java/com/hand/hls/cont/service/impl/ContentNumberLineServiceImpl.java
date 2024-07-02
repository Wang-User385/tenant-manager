package com.hand.hls.cont.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.Code;
import com.hand.hap.system.dto.CodeValue;
import com.hand.hap.system.mapper.CodeMapper;
import com.hand.hap.system.mapper.CodeValueMapper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.components.CalculateUtil;
import com.hand.hls.cont.dto.ContentNumberHead;
import com.hand.hls.cont.mapper.ContentNumberHeadMapper;
import com.hand.hls.cont.mapper.ContentNumberLineMapper;
import com.hand.hls.fct.dto.FctProjectAttachment;
import com.hand.hls.fct.dto.HlsCusFctProjectAttachment;
import com.hand.hls.fct.service.HlsCusFctProjectAttachmentService;
import com.hand.hls.fnd.dto.FndSysCodes;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.utils.ResMessageException;
import hls.core.sys.mapper.SysCodeValueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.cont.dto.ContentNumberLine;
import com.hand.hls.cont.service.IContentNumberLineService;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class ContentNumberLineServiceImpl extends BaseServiceImpl<ContentNumberLine> implements IContentNumberLineService {

    private static final String COMPANY_NAME = "CIBFL";
    private static final String COMMON_STR = "-";

    private static final String CONTENT_NUMBER_REQ = "CONTENT_NUMBER_REQ";

    private static final String LON_CONTENT = "LON_CONTENT";
    private static final String DEPOSIT_CONTENT = "DEPOSIT_CONTENT";
    private static final String MORTGAGE_CONTRACT = "MORTGAGE_CONTRACT";
    private static final String CONTRACT_OF_PLEDGE = "CONTRACT_OF_PLEDGE";
    private static final String G_CONTENT = "G_CONTENT";

    private static final String LEASE = "LEASE";
    private static final String LEASEBACK = "LEASEBACK";

    private static final String ZZ = "ZZ";
    private static final String HZ = "HZ";

    private static final String BZJ = "BZJ";
    private static final String G = "G";
    private static final String ZY = "ZY";
    private static final String DY = "DY";

    @Autowired
    private ContentNumberLineMapper contentNumberLineMapper;

    @Autowired
    private ContentNumberHeadMapper contentNumberHeadMapper;

    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;

    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;
    @Autowired
    private CodeValueMapper codeValueMapper;
    @Autowired
    private CodeMapper codeMapper;
    @Autowired
    private HlsCusFctProjectAttachmentService fctProjectAttachmentService;

    private static final String LEASE_BUSINESS_TYPE = "LEASE";

    private static final String LEASEBACK_BUSINESS_TYPE = "LEASEBACK";

    @Override
    public List<ContentNumberLine> createDocumentNumber(IRequest iRequest, List<ContentNumberLine> lines) {
        //融资租赁合同
        //编码规则为‘固定字符CIBFL-当前年份-三位序列号-回租或直租缩写HZ/ZZ’，例如：CIBFL-2020-001-HZ、CIBFL-2019-053-ZZ
        //保证金合同
        //编码规则为‘固定字符CIBFL-当前年份-三位序列号-BZJ’，例如：CIBFL-2020-001-BZJ、CIBFL-2019-053-BZJ
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY");
        Map params = new HashMap();
        for (ContentNumberLine line : lines) {
            if (line.getDocumentNumber() == null) {


                ContentNumberHead contentNumberHead = new ContentNumberHead();
                HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
                String businessType;
                if (line.getProjectId() != null) {
                    hlsCusPrjProject.setProjectId(line.getProjectId());
                    hlsCusPrjProject = hlsCusPrjProjectMapper.selectByPrimaryKey(hlsCusPrjProject);
                    businessType = hlsCusPrjProject.getBusinessType();
                    if (LEASE_BUSINESS_TYPE.equalsIgnoreCase(businessType)) {
                        businessType = "ZZ";
                    } else if (LEASEBACK_BUSINESS_TYPE.equalsIgnoreCase(businessType)) {
                        businessType = "HZ";
                    } else {
                        businessType = "";
                    }
                } else {
                    contentNumberHead.setHeadId(line.getHeadId());
                    contentNumberHead = contentNumberHeadMapper.selectByPrimaryKey(contentNumberHead);
                    businessType = contentNumberHead.getBusinessType();
                    if (LEASE_BUSINESS_TYPE.equalsIgnoreCase(businessType)) {
                        businessType = "ZZ";
                    } else if (LEASEBACK_BUSINESS_TYPE.equalsIgnoreCase(businessType)) {
                        businessType = "HZ";
                    } else {
                        businessType = "";
                    }

                }

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(COMPANY_NAME);
                stringBuilder.append(COMMON_STR);
                stringBuilder.append(simpleDateFormat.format(new Date()));
                stringBuilder.append(COMMON_STR);

                switch (line.getContentType()) {
                    case LON_CONTENT:

                        String lonNum = fndCodingRuleValuesService.getCodeRuleValue(iRequest, CONTENT_NUMBER_REQ,
                                LON_CONTENT, LON_CONTENT, params);
                        stringBuilder.append(lonNum);

                        stringBuilder.append(COMMON_STR);

                        stringBuilder.append(businessType);

                        if (businessType == "" || businessType == null) {
                            stringBuilder = null;
                        }

                        break;
                    case DEPOSIT_CONTENT:

                        String depositNum = fndCodingRuleValuesService.getCodeRuleValue(iRequest, CONTENT_NUMBER_REQ,
                                DEPOSIT_CONTENT, DEPOSIT_CONTENT, params);
                        stringBuilder.append(depositNum);

                        stringBuilder.append(COMMON_STR);

                        stringBuilder.append(BZJ);

                        break;
                    case MORTGAGE_CONTRACT:

                        String gNum = fndCodingRuleValuesService.getCodeRuleValue(iRequest, CONTENT_NUMBER_REQ,
                                MORTGAGE_CONTRACT, MORTGAGE_CONTRACT, params);
                        stringBuilder.append(gNum);

                        stringBuilder.append(COMMON_STR);

                        stringBuilder.append(DY);
                        break;
                    case CONTRACT_OF_PLEDGE:

                        String pNum = fndCodingRuleValuesService.getCodeRuleValue(iRequest, CONTENT_NUMBER_REQ,
                                CONTRACT_OF_PLEDGE, CONTRACT_OF_PLEDGE, params);
                        stringBuilder.append(pNum);

                        stringBuilder.append(COMMON_STR);

                        stringBuilder.append(ZY);
                        break;
                }


                if (stringBuilder != null) {
                    line.setDocumentNumber(stringBuilder.toString());
                }

                contentNumberLineMapper.updateByPrimaryKeySelective(line);
            }
        }
        return lines;
    }


    @Override
    public List<HlsCusFctProjectAttachment> createDocumentNumberForCon(IRequest iRequest, List<HlsCusFctProjectAttachment> lines) throws ResMessageException {
        //融资租赁合同
        //编码规则为‘固定字符CIBFL-当前年份-三位序列号-回租或直租缩写HZ/ZZ’，例如：CIBFL-2020-001-HZ、CIBFL-2019-053-ZZ
        //保证金合同
        //编码规则为‘固定字符CIBFL-当前年份-三位序列号-BZJ’，例如：CIBFL-2020-001-BZJ、CIBFL-2019-053-BZJ
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY");
        Map params = new HashMap();
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(lines.get(0).getProjectId());
        hlsCusPrjProject = hlsCusPrjProjectMapper.selectByPrimaryKey(hlsCusPrjProject);


        for (HlsCusFctProjectAttachment line : lines) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(hlsCusPrjProject.getContractNumber());
            if (!LON_CONTENT.equals(line.getSourceType())) {

                CodeValue codeValue = getCodeValue("CONTRACT_CONTENT_NUM", line.getSourceType());

                stringBuilder.append(COMMON_STR);
                stringBuilder.append(codeValue.getMeaning());

                line.setDocumentNumber(stringBuilder.toString());

                fctProjectAttachmentService.updateByPrimaryKeySelective(iRequest, line);
            }
        }


        //抵押
        HlsCusFctProjectAttachment mortgageAttachment = new HlsCusFctProjectAttachment();
        mortgageAttachment.setProjectId(lines.get(0).getProjectId());
        mortgageAttachment.setSourceType("MORTGAGE_CONTRACT");
        List<HlsCusFctProjectAttachment> mortgageAttachmentList = fctProjectAttachmentService.selectSelective(iRequest, mortgageAttachment);
        updateDocumentNumber(iRequest, mortgageAttachmentList, "MORTGAGE_CONTRACT");

        //质押
        HlsCusFctProjectAttachment pledgeAttachment = new HlsCusFctProjectAttachment();
        pledgeAttachment.setProjectId(lines.get(0).getProjectId());
        pledgeAttachment.setSourceType("CONTRACT_OF_PLEDGE");
        List<HlsCusFctProjectAttachment> pledgeAttachmentList = fctProjectAttachmentService.selectSelective(iRequest, pledgeAttachment);
        updateDocumentNumber(iRequest, pledgeAttachmentList, "CONTRACT_OF_PLEDGE");

        //保证
        HlsCusFctProjectAttachment depositAttachment = new HlsCusFctProjectAttachment();
        depositAttachment.setProjectId(lines.get(0).getProjectId());
        depositAttachment.setSourceType("DEPOSIT_CONTENT");
        List<HlsCusFctProjectAttachment> depositAttachmentList = fctProjectAttachmentService.selectSelective(iRequest, depositAttachment);
        updateDocumentNumber(iRequest, depositAttachmentList, "DEPOSIT_CONTENT");

        return lines;
    }

    private void updateDocumentNumber(IRequest iRequest, List<HlsCusFctProjectAttachment> list, String sourceType) throws ResMessageException {
        if (list.size() > 1) {
            for (int i = 0; i < list.size(); i++) {
                String documentNumber = list.get(i).getDocumentNumber();

                CodeValue codeValue = getCodeValue("CONTRACT_CONTENT_NUM", sourceType);

                String[] spilt = documentNumber.split("-");
                if (spilt.length > 4) {
                    int index = documentNumber.lastIndexOf("-");
                    documentNumber = documentNumber.substring(0, index);
                }
                String str = String.format("%03d", i + 1);
                documentNumber = documentNumber + COMMON_STR + codeValue.getMeaning() + str;
                list.get(i).setDocumentNumber(documentNumber);
                fctProjectAttachmentService.updateByPrimaryKeySelective(iRequest, list.get(i));
            }
        }
    }

    private CodeValue getCodeValue(String valueCode, String valueName) throws ResMessageException {
        Code code = new Code();
        code.setCode(valueCode);
        List<Code> codes = codeMapper.select(code);
        if (codes.size() != 1) {
            throw new ResMessageException("请定义编码生成规则的系统代码！");
        }
        code = codes.get(0);
        CodeValue codeValue = new CodeValue();
        codeValue.setCodeId(code.getCodeId());
        codeValue.setValue(valueName);
        List<CodeValue> codeValues = codeValueMapper.select(codeValue);
        codeValue = codeValues.get(0);
        return codeValue;
    }
}