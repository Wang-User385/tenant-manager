package com.hand.hap.security;

import com.hand.hap.core.components.BlockUserConfig;
import com.hand.hap.core.components.CaptchaConfig;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.util.WebUtils;

/**
 * @Description：登录失败handler，新增验证密码错误锁定
 * @Author：liangxian.chen@hand-china.com
 * @Date：2023/3/30 15:14
 * @Version：1.0
 */
public class LoginFailureHandler implements AuthenticationFailureHandler {
    private static final Logger log = LoggerFactory.getLogger(LoginFailureHandler.class);

    private static final String  BAD_CREDENTIALS = "AbstractUserDetailsAuthenticationProvider.badCredentials";
    @Autowired
    private CaptchaConfig captchaConfig;
    @Autowired
    private BlockUserConfig blockUserConfig;

    public LoginFailureHandler() {
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if (log.isDebugEnabled()) {
            log.debug("login failed");
        }

        if (this.captchaConfig.getWrongTimes() > 0) {
            this.captchaConfig.updateLoginFailureInfo(WebUtils.getCookie(request, "loginKey"));
        }

        //20230330：是密码验证错误的异常才进入，验证码错误或其他错误不进入
        if (BAD_CREDENTIALS.equals(exception.getMessage())){
            String blockStr = blockUserConfig.updateBlockTimes(request);
            if(StringUtils.isNotEmpty(blockStr)){
                request.setAttribute("errorMsg", blockStr);
            }
        }

        request.setAttribute("error", true);
        request.setAttribute("code", "LOGIN_NOT_MATCH");
        request.setAttribute("exception", exception);
        request.getRequestDispatcher("/login.html").forward(request, response);
    }
}




