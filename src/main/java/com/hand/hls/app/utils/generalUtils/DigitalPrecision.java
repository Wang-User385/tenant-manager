package com.hand.hls.app.utils.generalUtils;

import java.math.BigDecimal;

/**
 * 数字精度转换
 * @author liao
 */
public class DigitalPrecision {
    /**
     *
     * 四舍五入保留 n 为小数
     * */
    public static Double rounding(double data , int n){
        BigDecimal bigDecimal =  new BigDecimal(data );
        return bigDecimal.setScale(n, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
