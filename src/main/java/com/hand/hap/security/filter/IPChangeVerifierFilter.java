package com.hand.hap.security.filter;


import com.hand.hap.account.dto.User;
import com.hand.hap.account.service.IUserService;
import com.hand.hap.account.service.impl.UserServiceImpl;
import com.hand.hap.security.LoginFailureHandler;
import com.hand.hls.sys.dto.IntranetSegmentDefine;
import com.hand.hls.sys.service.IIntranetSegmentDefineService;
import com.hand.hls.sys.service.impl.IntranetSegmentDefineServiceImpl;
import com.hand.hls.utils.IPUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @Description：客户端IP是否变化过滤器：当登录后的用户，切换内外网网络是，对当前用户进行判断，在用户表中INTRANET_FLAG=Y的用户，如果
 *               新的客户端IP不在系统已维护的内网网段中，则直接把session失效，强制用户登出系统重新登录
 * @Author：liangxian.chen@hand-china.com
 * @Date：2023/3/7 16:44
 * @Version：1.0
 */
@Component
public class IPChangeVerifierFilter extends OncePerRequestFilter {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final String LOGIN_IP_ADDR = "login_ip_address";

    private final String INTRANET_FLAG_Y = "Y";

    private final String loginUrl = "/login";

    private static final List<String> whiteList = Arrays.asList(
            "/login.html",
            "/login",
            "/role",
            "/logout",
            "/sessionExpiredLogin",
            "/timeout"
    );


    @Autowired
    private IUserService userService;
    @Autowired
    private IIntranetSegmentDefineService defineService;
    @Autowired
    private LoginFailureHandler loginFailureHandler;

    public IPChangeVerifierFilter(){
    }



    @Override
    protected void initFilterBean() throws ServletException {
        ServletContext context = getServletContext();
        ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(context);
        userService  =  ctx.getBean(UserServiceImpl.class);
        defineService  =  ctx.getBean(IntranetSegmentDefineServiceImpl.class);
        loginFailureHandler  =  ctx.getBean(LoginFailureHandler.class);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String requestURI = request.getRequestURI();
        if (session != null && session.getAttribute(LOGIN_IP_ADDR) != null && !whiteList.contains(requestURI)){
            if (validate(request, response, session)){
                //如果内网用户切换了内网到外网，则直接失效session
                session.invalidate();
                return;
            }
        }

        filterChain.doFilter(request,response);
    }

    private boolean validate(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, HttpSession session)throws AuthenticationException {

        String requestIpAddress = IPUtils.getRequestIpAddress(httpServletRequest);
        logger.info("-----IPChangeVerifierFilter.requestIpAddress:{}",requestIpAddress);
        String loginIpAddress =  (String)session.getAttribute(LOGIN_IP_ADDR);
        logger.info("-----IPChangeVerifierFilter.loginIpAddress:{}",loginIpAddress);
        if (!loginIpAddress.equals(requestIpAddress)){
            Long userId = (Long)session.getAttribute("userId");
            //User user = this.userService.selectUserById(userId);
            //hotfix-20230314:调用selectUserById()方法只能获取到description，其他属性均为空，因此改用selectUserNameByUserId()
            User user = this.userService.selectUserNameByUserId(String.valueOf(userId));
            if (user != null && StringUtils.isNotEmpty(user.getIntranetFlag()) && INTRANET_FLAG_Y.equals(user.getIntranetFlag())){
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
                    logger.info("-----IPChangeVerifierFilter.enableLine:{}",enableLine);
                    return enableLine == 0;
                }

            }
        }
        return false;
    }

    @Override
    public void destroy() {

    }
}
