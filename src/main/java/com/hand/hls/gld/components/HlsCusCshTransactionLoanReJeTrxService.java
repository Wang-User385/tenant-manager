package com.hand.hls.gld.components;

import com.hand.hap.core.IRequest;
import com.hand.hls.gld.dto.JeTrxDtl;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by zhangyu on 2018/6/13.
 */
@Component
public class HlsCusCshTransactionLoanReJeTrxService extends AbstractJeTrxService {
    @Override
    public String getJeTrx() {
        return "CSH_TRANSACTION_LOAN_RE";
    }

    @Override
    protected boolean before(IRequest request, Map param) {
        return true;
    }

    @Override
    protected void after(IRequest request, Map param, JeTrxDtl result) {

    }
}
