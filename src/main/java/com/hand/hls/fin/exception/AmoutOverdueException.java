package com.hand.hls.fin.exception;

import com.hand.hap.core.exception.BaseException;

/**
 * Created by wangyan on 2017/11/22.
 */
public class AmoutOverdueException extends BaseException {

    public static final String BEYOND_THE_AMOUNT_LIMIT = "融资金额超出授信额度限制";  //"msg.error.system.csh.beyond_amount_error";

    public AmoutOverdueException() {
        super(BEYOND_THE_AMOUNT_LIMIT, BEYOND_THE_AMOUNT_LIMIT,new Object[0]);
    }

    public AmoutOverdueException(String message) {
        super(message, message,new Object[0]);
    }
}
