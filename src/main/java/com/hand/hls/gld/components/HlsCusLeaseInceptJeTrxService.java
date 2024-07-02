package com.hand.hls.gld.components;

import com.hand.hap.core.IRequest;
import com.hand.hls.gld.dto.JeTrxDtl;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by zhangyu on 2018/6/19.
 */
@Component
public class HlsCusLeaseInceptJeTrxService extends AbstractJeTrxService {
    @Override
    public String getJeTrx() {
        return "LEASE_INCEPT";
    }

    @Override
    protected boolean before(IRequest request, Map param) {
        return true;
    }

    @Override
    protected void after(IRequest request, Map param, JeTrxDtl result) {

    }
}
