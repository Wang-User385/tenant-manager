package com.hand.hls.vat.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.vat.dto.HlsCusAcrInvoiceRelationship;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AcrInvoiceRelationshipServiceImpl extends BaseServiceImpl<HlsCusAcrInvoiceRelationship> implements AcrInvoiceRelationshipService {
    public AcrInvoiceRelationshipServiceImpl() {
    }
}
