package com.hand.hls.gld.components;

import com.hand.hap.core.IRequest;
import com.hand.hls.gld.dto.JeTrxDtl;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * description
 *
 * @author yuanyuan 2019/08/26 11:17 AM
 */
@Component
public class HlsCusEftFundTransferTrxService extends AbstractJeTrxService {

    @Override
    public String getJeTrx() {
        return "TRANSFER_OF_FINANCIAL_RESOURCES";
    }

    @Override
    protected boolean before(IRequest request, Map param) {
        return true;
    }

    @Override
    protected void after(IRequest request, Map param, JeTrxDtl result) {

    }
}
