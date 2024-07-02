package com.hand.hls.utils;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description：IP相关的工具类
 * @Author：liangxian.chen@hand-china.com
 * @Date：2023/3/2 09:56
 * @Version：1.0
 */
public class IPUtils {

    private static final String UNKNOWN = "unknown";
    private static final String X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String PROXY_CLIENT_IP = "Proxy-Client-IP";
    private static final String WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";
    private static final String HTTP_CLIENT_IP = "HTTP_CLIENT_IP";
    private static final String HTTP_X_FORWARDED_FOR = "HTTP_X_FORWARDED_FOR";
    private static final String LOCAL_ADDR = "0:0:0:0:0:0:0:1";
    private static final String LOCAL_IP = "127.0.0.1";



    private static final int INT_15 = 15;


    public IPUtils() {
    }

    /**
     * 获取请求中的IP
     * @param request
     * @return
     */
    public static String getRequestIpAddress(HttpServletRequest request) {
        String ip = request.getHeader(X_FORWARDED_FOR);
        if (ip != null && ip.length() != 0 && !UNKNOWN.equalsIgnoreCase(ip)) {
            if (ip.length() > INT_15) {
                String[] ips = ip.split(",");

                for(int index = 0; index < ips.length; ++index) {
                    String strIp = ips[index];
                    if (!UNKNOWN.equalsIgnoreCase(strIp)) {
                        ip = strIp;
                        break;
                    }
                }
            }
        } else {
            if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader(PROXY_CLIENT_IP);
            }

            if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader(WL_PROXY_CLIENT_IP);
            }

            if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader(HTTP_CLIENT_IP);
            }

            if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader(HTTP_X_FORWARDED_FOR);
            }

            if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        }
        if (LOCAL_ADDR.equals(ip)) {
            ip = LOCAL_IP;
        }
        return ip;
    }

    /**
     * IP地址转换为整数
     *
     * @param ip IP地址
     * @return 整数
     */
    public static long ip2Long(String ip) {
        String[] ipBytes;
        double num = 0;
        if (!StringUtils.isEmpty(ip)) {
            ipBytes = ip.split("\\.");
            for (int i = ipBytes.length - 1; i >= 0; i--) {
                num += ((Integer.parseInt(ipBytes[i]) % 256) * Math.pow(256, (3 - i)));
            }
        }
        return (long) num;
    }

    /**
     * 判断IP是否合法
     * @param ip
     * @return
     */
    public static boolean ipCheck(String ip) {
        if (ip != null && !ip.isEmpty()) {
            // 定义正则表达式
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            // 判断ip地址是否与正则表达式匹配
            return ip.matches(regex);
        }
        return false;
    }

    /**
     * 判断IP是否在指定范围
     *
     * @param ipStart
     * @param ipEnd
     * @param ip
     * @return
     */
    public static boolean ipIsValid(String ipStart, String ipEnd, String ip) {
        long start = ip2Long(ipStart);
        long end = ip2Long(ipEnd);
        long ipAddress = ip2Long(ip);
        return (ipAddress >= start && ipAddress <= end);
    }
}
