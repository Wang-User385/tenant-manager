package com.hand.hap.security.filter;

import com.hand.hap.account.dto.User;
import com.hand.hap.account.service.IUserService;
import com.hand.hap.security.LoginFailureHandler;
import com.hand.hls.sys.dto.IntranetSegmentDefine;
import com.hand.hls.sys.service.IIntranetSegmentDefineService;
import com.hand.hls.utils.IPUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Description：登录验证内网用户是否内网登录过滤器：在用户表中INTRANET_FLAG=Y的用户，客户端IP在所维护的内网网段中，才能登录系统
 * @Author：liangxian.chen@hand-china.com
 * @Date：2023/3/3 14:53
 * @Version：1.0
 */
public class IntranetIPVerifierFilter extends OncePerRequestFilter {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IUserService userService;

    @Autowired
    private IIntranetSegmentDefineService defineService;

    @Autowired
    private LoginFailureHandler loginFailureHandler;

    private RequestMatcher loginRequestMatcher;

    private final String loginUrl = "/login";

    private final String INTRANET_FLAG_Y = "Y";

    private final String METHOD_POST = "POST";

    public IntranetIPVerifierFilter() {
        this.setFilterProcessesUrl(this.loginUrl);
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String requestMethod = request.getMethod();
        boolean failFlag = false;
        if (loginUrl.equals(requestURI) && METHOD_POST.equals(requestMethod)) {
            try {
                validate(request, response);
            }catch (AuthenticationException e){
                loginFailureHandler.onAuthenticationFailure(request,response,e);
                failFlag = true;
            }
        }
        if(!failFlag){
            filterChain.doFilter(request, response);
        }

    }

    private void validate(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        if (username == null){
            throw new BadCredentialsException("error.login.name_password_not_match");
        }
        User user = this.userService.selectByUserName(username);
        if (user == null){
            throw new BadCredentialsException("error.login.user_not_exist");
        }else{
            if (StringUtils.isNotEmpty(user.getIntranetFlag()) && INTRANET_FLAG_Y.equals(user.getIntranetFlag())){

                String requestIpAddress = IPUtils.getRequestIpAddress(request);
                logger.info("-----IntranetIPVerifierFilter.requestIpAddress:{}",requestIpAddress);
                IntranetSegmentDefine define = new IntranetSegmentDefine();
                define.setEnableFlag("Y");
                List<IntranetSegmentDefine> defineList = defineService.getIntranetSegmentDefineInfo(define);
                if (CollectionUtils.isNotEmpty(defineList)){
                    int enableLine = 0;
                    for (IntranetSegmentDefine line : defineList) {
                        String ips = line.getIntranetFrom();
                        String ipe = line.getIntranetTo();
                        if (IPUtils.ipIsValid(ips,ipe,requestIpAddress)){
                            enableLine++;
                        }
                    }
                    if (enableLine == 0){
                        throw new BadCredentialsException("error.login.user_not_intranet");
                    }
                }
            }
        }
    }

    public void setFilterProcessesUrl(String filterProcessesUrl) {
        this.loginRequestMatcher = new AntPathRequestMatcher(filterProcessesUrl);
    }
}
