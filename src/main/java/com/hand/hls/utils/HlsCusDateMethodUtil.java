package com.hand.hls.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * @Description:时间工具类
 * @Author: wutianyu
 * @Date: Created in 下午7:25 2018/6/27
 */
public class HlsCusDateMethodUtil {

    /**
     * @Description:网上copy得到2个日期的差值天数
     * @Author: Wty
     * @Date: Created om 19:18 2018/6/19
     * @param: [date1, date2]
     * @return: int
     */
    public static int dateReduction(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2) {
            //同一年
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {
                    //闰年
                    timeDistance += 366;
                } else {
                    //不是闰年
                    timeDistance += 365;
                }
            }
            return timeDistance + (day2 - day1);
        } else {
            //不同年
            return day2 - day1;
        }
    }
}
