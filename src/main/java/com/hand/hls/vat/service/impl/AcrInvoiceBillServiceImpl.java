package com.hand.hls.vat.service.impl;


import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.vat.dto.AcrInvoiceBill;
import com.hand.hls.vat.dto.AcrInvoiceHdMid;
import com.hand.hls.vat.mapper.AcrInvoiceBillMapper;
import com.hand.hls.vat.service.AcrInvoiceBillService;
import com.hand.hls.vat.service.AcrInvoiceHdMidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class AcrInvoiceBillServiceImpl extends BaseServiceImpl<AcrInvoiceBill> implements AcrInvoiceBillService {

    @Autowired
    private AcrInvoiceBillMapper acrInvoiceBillMapper;

    @Override
    public List<AcrInvoiceBill> queryAcrInvoiceBillMaintenance() {
        return acrInvoiceBillMapper.queryAcrInvoiceBillMaintenance();
    }

    @Override
    public List<AcrInvoiceBill> queryAcrInvoiceBill() {
        return acrInvoiceBillMapper.queryAcrInvoiceBill();
    }

    //    @Override
    //    public int createAcrInvoiceBill(AcrInvoiceBill acrInvoiceBill) {
    //        return acrInvoiceBillMapper.createAcrInvoiceBill(acrInvoiceBill);
    //    }
    //
    //    @Override
    //    public int updateAcrInvoiceBill(AcrInvoiceBill acrInvoiceBill) {
    //        return acrInvoiceBillMapper.updateAcrInvoiceBill(acrInvoiceBill);
    //    }


}
