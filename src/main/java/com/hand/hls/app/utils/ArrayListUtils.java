package com.hand.hls.app.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;

public class ArrayListUtils {
    private static final Logger logger = LoggerFactory.getLogger(ArrayListUtils.class);

    public static boolean ArrayListContains(Object[] array, Object[] containsArray) {
        for (Object object : containsArray) {
            if(ArrayUtils.contains(array, object)){
                return true;
            }
        }
        return false;
    }

    public static boolean ArrayListElementContains(Object[] array,  Object object) {
        return ArrayUtils.contains(array, object);
    }
}
