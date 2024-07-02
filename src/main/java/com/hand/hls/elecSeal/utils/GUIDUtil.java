//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.elecSeal.utils;

import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

public class GUIDUtil {
    private static SecureRandom random = new SecureRandom();

    public GUIDUtil() {
    }

    public static synchronized String generateId() {
        Date date = Calendar.getInstance().getTime();
        String timeString = DateFormatUtils.format(date, "yyyyMMddHHmmssSSS");
        return timeString + getRandomNumber(6) + getRandomNumber(7);
    }

    public static synchronized String generateId(int length) {
        if (length < 17) {
            throw new IllegalArgumentException("Requested random string length " + length + " is less than 17");
        } else {
            Date date = Calendar.getInstance().getTime();
            String timeString = DateFormatUtils.format(date, "yyyyMMddHHmmssSSS");
            RandomStringUtils.randomNumeric(4);
            return timeString + getRandomNumber(length - 17);
        }
    }

    public static String getRandomNumber(int length) {
        StringBuffer stringBuffer = new StringBuffer();
        String randomLongStr = getRandomLongStr();
        stringBuffer.append(randomLongStr);

        while(randomLongStr.length() < length) {
            String tempRandomLongStr = getRandomLongStr();
            stringBuffer.append(tempRandomLongStr);
        }

        return stringBuffer.toString().substring(0, length);
    }

    private static String getRandomLongStr() {
        long s1 = random.nextLong();
        long s2 = s1 >> 1;
        long s3 = 4611686018427387903L;
        long s4 = s2 + s3;
        return String.valueOf(s4);
    }
}
