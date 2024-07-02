package com.hand.hls.app.utils.generalUtils;

/**
 * 公共常量
 * @author liao
 */
public interface AppConstantUtils {


    /**
     * 公共常量
     */
    public static class PublicMsg {
        public static final String SUCCESS = "成功";
        public static final String LOGIN_USER_ERROR = "用户名不存在！";
        public static final String LOGIN_PASSWORD_ERROR = "密码错误！";

    }

    public static class PublicConstans {

        public static final int DEFAULT_PAGE = 1;
        public static final int DEFAULT_PAGE_SIZE = 10;
        public static final String DEFAULT_TRUE = "TRUE";
        public static final String DEFAULT_FALSE = "FALSE";
    }
    public static class Interface {


        public static final String DATA_JSON = "JSON";
        public static final String DATA_XML = "XML";
        public static final Integer STATUS_CODE_200 = 200;
        public static final Integer STATUS_CODE_404 = 404;
    }
}
