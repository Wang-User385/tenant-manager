package com.hand.hls.pam.util;

/**
 * @author kalvin
 * @version 1.0
 * @Date 2021/3/17 19:46
 * @Description
 **/
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Date;

/**
 * 时间解析工具
 * 引用commons-lang3.jar
 *
 */
public class DateUtil {
    //可能的时间格式
    private static final String[] format = {" HH:mm:ss", "yyyy-MM-dd", "yyyyMMdd", "yyyy/MM/dd"};
    //时间匹配正则表达式
    private static final String[] dateRegex = {"^[\\d]{4}+[-]+[\\d]{1,2}+[-]+[\\d]{1,2}$", "\\d{8}", "^[\\d]{4}+[/]+[\\d]{1,2}+[/]+[\\d]{1,2}$"};
    //时间前后分割
    private static String space = "[A-Za-z\\s.]+"; //字母，空格，小数点

    private static String toTwo(String value) {
        return String.format("%02d", Integer.parseInt(value));
    }

    /**
     * 转换时间格式
     * [A-Za-z.]+ 排除其他异常输入格式
     */
    public static Date parseDate(String date) {
        Date value = null;
        if (date != null && !date.matches("[A-Za-z.]+")) {
            try {
                date = date.split("[A-Za-z.]+")[0].trim();
                String pre = date.split("[\\s]+")[0];
                if (pre.matches(dateRegex[0])) {
                    value = DateUtils.parseDate(date, format[1], format[1] + format[0]);
                } else if (pre.matches(dateRegex[1])) {
                    value = DateUtils.parseDate(date, format[2], format[2] + format[0]);
                } else if (pre.matches(dateRegex[2])) {
                    value = DateUtils.parseDate(date, format[3], format[3] + format[0]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return value;
    }


}