package com.hand.hls.gld.components;

import com.hand.hap.core.IRequest;
import com.hand.hls.gld.dto.JeTrxDtl;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Description：
 * @Author：liangxian.chen@hand-china.com
 * @Date：2023/2/10 20:37
 * @Version：1.0
 */
@Component
public class HlsCusDepositRefundJeTrxService extends AbstractJeTrxService {
    @Override
    public String getJeTrx() {
        return "DEPOSIT_REFUND";
    }

    @Override
    protected boolean before(IRequest var1, Map var2) {
        return true;
    }

    @Override
    protected void after(IRequest var1, Map var2, JeTrxDtl var3) {

    }
}
