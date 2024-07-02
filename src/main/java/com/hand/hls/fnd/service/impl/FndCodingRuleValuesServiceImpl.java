//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.hand.hls.fnd.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fnd.dto.DocumentType;
import com.hand.hls.fnd.dto.FndCodingRule;
import com.hand.hls.fnd.dto.FndCodingRuleDetails;
import com.hand.hls.fnd.dto.FndCodingRuleValues;
import com.hand.hls.fnd.mapper.DocumentTypeMapper;
import com.hand.hls.fnd.mapper.FndCodingRuleDetailsMapper;
import com.hand.hls.fnd.mapper.FndCodingRuleMapper;
import com.hand.hls.fnd.mapper.FndCodingRuleValuesMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FndCodingRuleValuesServiceImpl extends BaseServiceImpl<FndCodingRuleValues> implements FndCodingRuleValuesService {
    @Autowired
    FndCodingRuleMapper codingRuleMapper;
    @Autowired
    FndCodingRuleDetailsMapper codingRuleDetailsMapper;
    @Autowired
    FndCodingRuleValuesMapper codingRuleValuesMapper;
    @Autowired
    DocumentTypeMapper documentTypeMapper;

    public FndCodingRuleValuesServiceImpl() {
    }

    public String getCodeRuleValue(IRequest requestContext, String documentCategory, String documentType, String businessType, Map<String, String> params) {
        String value = null;
        DocumentType type = new DocumentType();
        type.setDocumentCategory(documentCategory);
        type.setDocumentType(documentType);
        type.setBusinessType(businessType);
        DocumentType document = (DocumentType)this.documentTypeMapper.selectByPrimaryKey(type);
        Long codingRuleId = document.getCodingRuleId();
        String segmentTypeValue = null;
        FndCodingRule codingRule = (FndCodingRule)this.codingRuleMapper.selectByPrimaryKey(codingRuleId);
        if (codingRule == null) {
            return null;
        } else {
            List<FndCodingRuleDetails> codingRuleDetails = this.codingRuleDetailsMapper.selectByCodingRuleId(codingRule.getCodingRuleId());
            Date date = new Date();
            Iterator var14 = codingRuleDetails.iterator();

            while(var14.hasNext()) {
                FndCodingRuleDetails codingRuleDetail = (FndCodingRuleDetails)var14.next();
                segmentTypeValue = this.getSegmentTypeValue(requestContext, documentCategory, documentType, businessType, date, codingRuleDetail.getSegmentType(), codingRule, params);
                if (value == null) {
                    value = segmentTypeValue;
                } else {
                    StringBuffer sb = new StringBuffer(value);
                    value = sb.append(segmentTypeValue).toString();
                }
            }

            return value;
        }
    }

    public String getCodeRuleValue(IRequest requestContext, String documentCategory, String documentType, String businessType, Date date, Map<String, String> params) {
        DocumentType type = new DocumentType();
        type.setDocumentCategory(documentCategory);
        type.setDocumentType(documentType);
        type.setBusinessType(businessType);
        DocumentType document = (DocumentType)this.documentTypeMapper.selectByPrimaryKey(type);
        Long codingRuleId = document.getCodingRuleId();
        String value = null;
        String segmentTypeValue = null;
        FndCodingRule codingRule = (FndCodingRule)this.codingRuleMapper.selectByPrimaryKey(codingRuleId);
        if (codingRule == null) {
            return null;
        } else {
            List<FndCodingRuleDetails> codingRuleDetails = this.codingRuleDetailsMapper.selectByCodingRuleId(codingRule.getCodingRuleId());
            Iterator var14 = codingRuleDetails.iterator();

            while(var14.hasNext()) {
                FndCodingRuleDetails codingRuleDetail = (FndCodingRuleDetails)var14.next();
                segmentTypeValue = this.getSegmentTypeValue(requestContext, documentCategory, documentType, businessType, date, codingRuleDetail.getSegmentType(), codingRule, params);
                if (value == null) {
                    value = segmentTypeValue;
                } else {
                    StringBuffer sb = new StringBuffer(value);
                    value = sb.append(segmentTypeValue).toString();
                }
            }

            return value;
        }
    }

    private String getSegmentTypeValue(IRequest requestContext, String documentCategory, String documentType, String businessType, Date date, String segmentType, FndCodingRule codingRule, Map<String, String> params) {
        FndCodingRuleDetails codingRuleDetails = null;
        if ("SYS_01".equals(segmentType)) {
            codingRuleDetails = this.codingRuleDetailsMapper.selectByUk(segmentType, codingRule.getCodingRuleId());
            return codingRuleDetails.getSegmentValue();
        } else if ("SYS_02".equals(segmentType)) {
            codingRuleDetails = this.codingRuleDetailsMapper.selectByUk(segmentType, codingRule.getCodingRuleId());
            Date currentTime = new Date();
            String format = codingRuleDetails.getDateFormat().replace("D", "d");
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            return formatter.format(currentTime);
        } else if ("SYS_03".equals(segmentType)) {
            String value = this.getSeqValue(requestContext, segmentType, codingRule, date);
            return value;
        } else if ("SYS_04".equals(segmentType)) {
            return documentCategory;
        } else if ("SYS_05".equals(segmentType)) {
            return documentType;
        } else {
            return "SYS_06".equals(segmentType) ? businessType : (String)params.get(segmentType);
        }
    }

    private String getSeqValue(IRequest requestContext, String segmentType, FndCodingRule codingRule, Date date) {
        FndCodingRuleValues codingRulevalue = null;
        Calendar cal = Calendar.getInstance();
        if (date != null) {
            cal.setTime(date);
        }

        int month = cal.get(2) + 1;
        int year = cal.get(1);
        FndCodingRuleDetails codingRuleDetails = this.codingRuleDetailsMapper.selectByUk(segmentType, codingRule.getCodingRuleId());
        if ("MONTH".equals(codingRule.getResetFrequence())) {
            codingRulevalue = this.codingRuleValuesMapper.selectByUyKey(codingRule.getCodingRuleId(), Integer.toString(month), new Long((long)year));
        } else if ("YEAR".equals(codingRule.getResetFrequence())) {
            codingRulevalue = this.codingRuleValuesMapper.selectByUyKey(codingRule.getCodingRuleId(), (String)null, new Long((long)year));
        } else {
            codingRulevalue = this.codingRuleValuesMapper.selectByUyKey(codingRule.getCodingRuleId(), (String)null, (Long)null);
        }

        Long seqValue;
        String value;
        if (codingRulevalue == null) {
            if(codingRule.getCodingRuleId() == 2){ // 重新定义合同编码生成规则
                codingRulevalue = this.codingRuleValuesMapper.selectByUyKey(codingRule.getCodingRuleId(), (String)null, new Long((long)year-1));
                seqValue =  codingRulevalue.getCurrentValue() + codingRuleDetails.getIncremental();
            }else{
                seqValue = codingRuleDetails.getStartValue() + codingRuleDetails.getIncremental();
            }
            for(value = seqValue.toString(); (long)value.length() < codingRuleDetails.getLength(); value = "0" + value) {
            }

            FndCodingRuleValues codingRuleValues = new FndCodingRuleValues();
            if ("MONTH".equals(codingRule.getResetFrequence())) {
                codingRuleValues.setCodingRuleId(codingRule.getCodingRuleId());
                codingRuleValues.setMonth(Integer.toString(month));
                codingRuleValues.setYear(new Long((long)year));
                codingRuleValues.setCurrentValue(seqValue);
            } else if ("YEAR".equals(codingRule.getResetFrequence())) {
                codingRuleValues.setCodingRuleId(codingRule.getCodingRuleId());
                codingRuleValues.setYear(new Long((long)year));
                codingRuleValues.setCurrentValue(seqValue);
            } else {
                codingRuleValues.setCodingRuleId(codingRule.getCodingRuleId());
                codingRuleValues.setCurrentValue(seqValue);
            }

            ((FndCodingRuleValuesService)this.self()).insertSelective(requestContext, codingRuleValues);
            return value;
        } else {
            seqValue = codingRulevalue.getCurrentValue() + codingRuleDetails.getIncremental();

            for(value = seqValue.toString(); (long)value.length() < codingRuleDetails.getLength(); value = "0" + value) {
            }

            if ("MONTH".equals(codingRule.getResetFrequence())) {
                this.codingRuleValuesMapper.updateByUyKey(codingRule.getCodingRuleId(), Integer.toString(month), new Long((long)year), seqValue);
            } else if ("YEAR".equals(codingRule.getResetFrequence())) {
                this.codingRuleValuesMapper.updateByUyKey(codingRule.getCodingRuleId(), (String)null, new Long((long)year), seqValue);
            } else {
                this.codingRuleValuesMapper.updateByUyKey(codingRule.getCodingRuleId(), (String)null, (Long)null, seqValue);
            }

            return value;
        }
    }
}
