package com.hand.hap.security;

import com.hand.hap.core.components.CaptchaConfig;
import com.hand.hap.security.captcha.ICaptchaManager;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description：重写验证码过滤器实现。原有的逻辑是不起作用的，开启验证码后，输入任何东西都可以验证
 * @Author：liangxian.chen@hand-china.com
 * @Date：2023/3/6 11:57
 * @Version：1.0
 */
public class CaptchaVerifierFilter extends OncePerRequestFilter {
    @Autowired
    private ICaptchaManager captchaManager;
    @Autowired
    private LoginFailureHandler loginFailureHandler;
    @Autowired
    private CaptchaConfig captchaConfig;
    private RequestMatcher loginRequestMatcher;
    private String captchaField = "captcha";
    private String loginUrl = "/login";

    public CaptchaVerifierFilter() {
        this.setFilterProcessesUrl(this.loginUrl);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        if (this.captchaConfig.isEnableCaptcha(WebUtils.getCookie(httpServletRequest, "loginKey")) && this.requiresValidateCaptcha(httpServletRequest, httpServletResponse)) {
            try {
                validate(httpServletRequest);
            }catch (AuthenticationException e){
                loginFailureHandler.onAuthenticationFailure(httpServletRequest,httpServletResponse,e);
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private void validate(HttpServletRequest httpServletRequest) {
        Cookie cookie = WebUtils.getCookie(httpServletRequest, this.captchaManager.getCaptchaKeyName());
        String captchaCode = httpServletRequest.getParameter(this.getCaptchaField());
        if (cookie == null || StringUtils.isEmpty(captchaCode) || !this.captchaManager.checkCaptcha(cookie.getValue(), captchaCode)) {

            throw new BadCredentialsException("error.login.verification_code_error");

        }
    }

    public String getCaptchaField() {
        return this.captchaField;
    }

    public void setCaptchaField(String captchaField) {
        this.captchaField = captchaField;
    }

    public void setFilterProcessesUrl(String filterProcessesUrl) {
        this.loginRequestMatcher = new AntPathRequestMatcher(filterProcessesUrl);
    }

    protected boolean requiresValidateCaptcha(HttpServletRequest request, HttpServletResponse response) {
        return this.loginRequestMatcher.matches(request) && "POST".equalsIgnoreCase(request.getMethod());
    }


}
