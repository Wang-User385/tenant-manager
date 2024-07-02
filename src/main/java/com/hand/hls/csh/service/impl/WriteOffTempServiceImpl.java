package com.hand.hls.csh.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.csh.dto.CshPaymentAttachment;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.csh.dto.ProjectCreditCondition;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectAttachment;
import com.hand.hls.prj.dto.PrjProjectApproval;
import com.hand.hls.prj.dto.ProjectApprovalCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.csh.dto.WriteOffTemp;
import com.hand.hls.csh.service.IWriteOffTempService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class WriteOffTempServiceImpl extends BaseServiceImpl<HlsCusCshPaymentReqHd> implements IWriteOffTempService{
    @Autowired
    FndCodingRuleValuesService codingRuleValuesService;


    public String getCodeValue(IRequest requestContext) {
        Map<String, String> params = new HashMap<String, String>();
        return codingRuleValuesService.getCodeRuleValue(requestContext, "CSH_PAYMENT_REQ", "PAYMENT_REQ", "PAYMENT_REQ", params);
    }

    @Override
    public HlsCusCshPaymentReqHd cshHdCreate(IRequest iRequest, HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd) {

        hlsCusCshPaymentReqHd.setTransferStatus("NEW");
        hlsCusCshPaymentReqHd.setDocumentCategory("CSH_PAYMENT_REQ");
        hlsCusCshPaymentReqHd.setDocumentType("PAYMENT_REQ");
        hlsCusCshPaymentReqHd.setCompanyId(hlsCusCshPaymentReqHd.getCompanyId());
        hlsCusCshPaymentReqHd.setBusinessType("PAYMENT_REQ");
        hlsCusCshPaymentReqHd.setPaymentType(hlsCusCshPaymentReqHd.getPaymentType());
        String value = getCodeValue(iRequest);
        hlsCusCshPaymentReqHd.setPaymentReqNumber(value);
        hlsCusCshPaymentReqHd.setPaymentReqStatus(hlsCusCshPaymentReqHd.getPaymentReqStatus());
        hlsCusCshPaymentReqHd.setPaymentApprovedStatus(hlsCusCshPaymentReqHd.getPaymentApprovedStatus());
        if(hlsCusCshPaymentReqHd.getUnitId() == null && iRequest.getAttribute("unitId") != null){
            hlsCusCshPaymentReqHd.setUnitId(Long.valueOf(iRequest.getAttribute("unitId")));
        }else{
            hlsCusCshPaymentReqHd.setUnitId(hlsCusCshPaymentReqHd.getUnitId());
        }
        hlsCusCshPaymentReqHd.setCurrency(hlsCusCshPaymentReqHd.getCurrency());
        hlsCusCshPaymentReqHd.setSourceDocId(hlsCusCshPaymentReqHd.getProjectId());
        hlsCusCshPaymentReqHd.setSourceContractId(hlsCusCshPaymentReqHd.getContractId());
        hlsCusCshPaymentReqHd.setSourceDocType("CON_CONTRACT");
        hlsCusCshPaymentReqHd.setCreatedBy(iRequest.getUserId());
        hlsCusCshPaymentReqHd.setCreationDate(new Date());
        hlsCusCshPaymentReqHd.setPaymentReqDate(new Date());
        hlsCusCshPaymentReqHd.setSendFlag("N");

        hlsCusCshPaymentReqHd.setFinanceAmount(hlsCusCshPaymentReqHd.getFinanceAmount());
        hlsCusCshPaymentReqHd.setProjectName(hlsCusCshPaymentReqHd.getProjectName());
        hlsCusCshPaymentReqHd.setProposedLaunchDate(hlsCusCshPaymentReqHd.getProposedLaunchDate());
        hlsCusCshPaymentReqHd.setSumToufangAmount(hlsCusCshPaymentReqHd.getSumToufangAmount());
        hlsCusCshPaymentReqHd.setContractBalance(hlsCusCshPaymentReqHd.getContractBalance());
        hlsCusCshPaymentReqHd.setContractCurrency(hlsCusCshPaymentReqHd.getContractCurrency());
        hlsCusCshPaymentReqHd.setLoanTotalAmount(hlsCusCshPaymentReqHd.getLoanTotalAmount());

        hlsCusCshPaymentReqHd.setEmployeeId(hlsCusCshPaymentReqHd.getEmployeeId());
        hlsCusCshPaymentReqHd.setAuthorityRuleString(hlsCusCshPaymentReqHd.getAuthorityRuleString());
        hlsCusCshPaymentReqHd = this.insertSelective(iRequest, hlsCusCshPaymentReqHd);


        return hlsCusCshPaymentReqHd;
    }

}