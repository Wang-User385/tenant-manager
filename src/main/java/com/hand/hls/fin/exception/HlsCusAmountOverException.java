package com.hand.hls.fin.exception;

import com.hand.hap.core.exception.BaseException;

/**
 * @ClassName HlsCusAmountOverException
 * @Description //TODO
 * @Author yuan.yuan01@hand-china.com
 * @Date 2018/11/15 10:09 PM
 * @Version 1.0
 **/
public class HlsCusAmountOverException extends BaseException {

    public static final String BEYOND_AMOUNT_LIMIT = "还款计划总金额超出实际提款金额";

    public HlsCusAmountOverException() {
        super(BEYOND_AMOUNT_LIMIT, BEYOND_AMOUNT_LIMIT,new Object[0]);
    }

    public HlsCusAmountOverException(String message) {
        super(message, message,new Object[0]);
    }
}
