package com.hand.hls.wsdl.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Optional;

/**
 * <p>接口通用常量
 *
 * @author ferry ferry_sy@163.com
 * created by 2019/11/28 09:43
 */

public interface InterfaceConstants {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    DecimalFormat df = new DecimalFormat("#0.00");
    DecimalFormat df_format = new DecimalFormat("#,##0.00");
    DecimalFormat df_rate = new DecimalFormat("#0.####");
    String EMPTY = "";
    Double ZORE = 0D;

    default String ifnull(Object o){
        return Optional.ofNullable(o).orElse(EMPTY).toString();
    }
}
