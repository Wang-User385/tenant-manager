package com.hand.hls.gld.components;

import com.hand.hap.core.IRequest;
import com.hand.hls.gld.dto.JeTrxDtl;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Qian Yuanfeng
 * @date 2020/6/1 - 19:35
 */
@Component
public class HlsCusCshDeductionTrxService extends AbstractJeTrxService {
    @Override
    public String getJeTrx() {
        return "CSH_DEDUCTION";
    }

    @Override
    protected boolean before(IRequest request, Map param) {
        return true;
    }

    @Override
    protected void after(IRequest request, Map param, JeTrxDtl result) {

    }
}