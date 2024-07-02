package com.hand.hls.cont.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.dto.HlsCusConQuotation;
import com.hand.hls.cont.mapper.HlsCusConQuotationMapper;
import com.hand.hls.cont.service.HlsCusConQuotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusConQuotationServiceImpl extends BaseServiceImpl<HlsCusConQuotation> implements HlsCusConQuotationService {

    @Autowired
    private HlsCusConQuotationMapper hlsCusConQuotationMapper;

    @Override
    public HlsCusConQuotation conQuotationSave(IRequest iRequest, HlsCusConQuotation hlsCusConQuotation){
        HlsCusConQuotation conQuotation=new HlsCusConQuotation();
        if(hlsCusConQuotation.getQuotationId()!=null&&hlsCusConQuotation.getQuotationId()!=0){
            conQuotation.setQuotationId(hlsCusConQuotation.getQuotationId());
            conQuotation=self().selectByPrimaryKey(iRequest,conQuotation);
            hlsCusConQuotation.setObjectVersionNumber(conQuotation.getObjectVersionNumber());
            conQuotation=self().updateByPrimaryKey(iRequest,hlsCusConQuotation);
        }else{
            hlsCusConQuotation.set__status("add");
            hlsCusConQuotation.setSourceDocumentCategory("CON_CONTRACT");
            hlsCusConQuotation.setStatus("NEW");
            if(hlsCusConQuotation.getPriceList().equalsIgnoreCase("LEVEL_RATE_TAX_INC_T")){
                hlsCusConQuotation.setSourceDocumentCategory("CON_CONTRACT");
                hlsCusConQuotation.setFloatingWay("INCREASE");
                hlsCusConQuotation.setIntRateType("FLOATING");
                hlsCusConQuotation.setRentingFrequency("MONTH");
                hlsCusConQuotation.setRentingMethod("PERIOD_FINAL");
                hlsCusConQuotation.setDepositReturnMethod("PERIOD_FINAL_DEDUCTIBLE");
            }
            conQuotation=self().insertSelective(iRequest,hlsCusConQuotation);
        }
        return conQuotation;
    }




}