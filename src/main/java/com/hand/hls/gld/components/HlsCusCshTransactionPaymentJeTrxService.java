package com.hand.hls.gld.components;

import com.hand.hap.core.IRequest;
import com.hand.hls.gld.dto.JeTrxDtl;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class HlsCusCshTransactionPaymentJeTrxService extends AbstractJeTrxService {
    @Override
    public String getJeTrx() {
        return "CSH_TRANSACTION_PAYMENT";
    }

    @Override
    protected boolean before(IRequest request, Map param) {
        return true;
    }

    @Override
    protected void after(IRequest request, Map param, JeTrxDtl result) {

    }
}
