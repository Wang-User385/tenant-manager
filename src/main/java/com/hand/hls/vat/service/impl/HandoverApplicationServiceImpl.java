package com.hand.hls.vat.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.vat.dto.HlsCusAcrInvoiceHd;
import com.hand.hls.vat.mapper.HandoverApplicationMapper;
import com.hand.hls.vat.mapper.HlsCusAcrInvoiceHdMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.vat.dto.HandoverApplication;
import com.hand.hls.vat.service.IHandoverApplicationService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HandoverApplicationServiceImpl extends BaseServiceImpl<HandoverApplication> implements IHandoverApplicationService{
    @Autowired
    HlsCusAcrInvoiceHdMapper hlsCusAcrInvoiceHdMapper;

    @Autowired
    IHandoverApplicationService handoverapplicationService;
    @Autowired
    HandoverApplicationMapper handoverApplicationMapper;


    @Override
    public void newCreate(IRequest iRequest, HandoverApplication handoverapplication) {

        HlsCusAcrInvoiceHd hlscusacrinvoicehd = new HlsCusAcrInvoiceHd();


        List<HlsCusAcrInvoiceHd> hlscusacrinvoicehdLists=hlsCusAcrInvoiceHdMapper.queryHandOverInvoiceNew(hlscusacrinvoicehd);

            for (int i=0;i<hlscusacrinvoicehdLists.size();i++){
                HandoverApplication handoverapplications  =new HandoverApplication();
                handoverapplications.setDocumentId(hlscusacrinvoicehdLists.get(i).getSourceDocumentId());
                handoverapplications.setDocumentNumber(hlscusacrinvoicehdLists.get(i).getDocumentNumber());
                handoverapplications.setHandoverDate(new Date());
                handoverapplications.setHandoverPerson(hlscusacrinvoicehdLists.get(i).getHandoverPerson());
                handoverapplications.setBpName(hlscusacrinvoicehdLists.get(i).getBpName());
                handoverapplications.setHostPartment(hlscusacrinvoicehdLists.get(i).getHostPartment());
                handoverapplications.setHostManager(hlscusacrinvoicehdLists.get(i).getHostManager());
                handoverapplications.setContractNumber(hlscusacrinvoicehdLists.get(i).getContractNumber());
                handoverapplications.setInvoiceCode(hlscusacrinvoicehdLists.get(i).getInvoiceCode());
                handoverapplications.setInvoiceDate(hlscusacrinvoicehdLists.get(i).getInvoiceDate());
                handoverapplications.setInvoiceNumber(hlscusacrinvoicehdLists.get(i).getInvoiceNumber());
                handoverapplications.setTaxAmount(hlscusacrinvoicehdLists.get(i).getTaxAmount());
                handoverapplications.setTotalAmount(hlscusacrinvoicehdLists.get(i).getTotalAmount());
                handoverapplications.setTaxTypeRate(hlscusacrinvoicehdLists.get(i).getTaxTypeRate());
                handoverapplications.setHandoverStatus("NOT_TRANSFER");
                handoverapplications.setCreatedBy(hlscusacrinvoicehdLists.get(i).getCreatedBy());
                handoverapplications.setCreationDate(new Date());
                handoverapplicationService.insertSelective(iRequest, handoverapplications);

            }
    }


    @Override
    public List<HandoverApplication> updateHandoverStatus(IRequest iRequest, List<HandoverApplication> handoverapplicationlist) {
        List<HandoverApplication> handoverapplicationlist1 = new ArrayList<>();
        for(HandoverApplication handoverapplication :handoverapplicationlist){
            handoverApplicationMapper.updateHandoverStatus(handoverapplication);
        }
        return handoverapplicationlist1;

    }


    @Override
    public List<HandoverApplication> updateHandoverConfirmStatus(IRequest iRequest, List<HandoverApplication> handoverapplicationlist) {
        List<HandoverApplication> handoverapplicationlist1 = new ArrayList<>();
        for(HandoverApplication handoverapplication :handoverapplicationlist){
            handoverapplication.setReviewPerson(iRequest.getEmployeeName());
            handoverApplicationMapper.updateHandoverStatusReview(handoverapplication);
        }
        return handoverapplicationlist1;

    }

    @Override
    public List<HandoverApplication> updateHandoverRejectStatus(IRequest iRequest, List<HandoverApplication> handoverapplicationlist) {
        List<HandoverApplication> handoverapplicationlist1 = new ArrayList<>();
        for(HandoverApplication handoverapplication :handoverapplicationlist){
            handoverapplication.setReviewPerson(iRequest.getEmployeeName());
            handoverApplicationMapper.updateHandoverRejectStatusReview(handoverapplication);
        }
        return handoverapplicationlist1;

    }
}