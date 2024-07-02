package com.hand.hls.utils;

import java.io.IOException;
import java.util.Properties;

/**
 * @version: 1.0
 * @name: 配置文件工具类
 * @description: (描述此类的功能)
 * @date: 2017-08-07 16:21
 */

public class HlsCusPropertiesUtil {

    /**
     * 配置文件路径
     */
    private static final String filePath = "/config.properties";

    private static final Properties properties = new Properties();

    static
    {
        loadProperties();
    }

    /**
     * 初始化
     */
    private static void loadProperties()
    {
        try
        {
            System.out.println("==="+ HlsCusPropertiesUtil.class.getResourceAsStream(filePath));
            properties.load(HlsCusPropertiesUtil.class.getResourceAsStream(filePath));
        }
        catch (final IOException e)
        {

        }
    }

    /**
     * 通过key获得value
     * @param key
     * @return
     */
    public static String getValue(final String key)
    {
        if (properties.isEmpty())
        {
            loadProperties();
        }
        return properties.getProperty(key);
    }

}