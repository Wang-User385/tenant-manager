//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.hand.hap.security;

import com.hand.hap.account.dto.User;
import com.hand.hap.account.exception.RoleException;
import com.hand.hap.account.service.IRoleService;
import com.hand.hap.core.components.BlockUserConfig;
import com.hand.hap.core.components.SysConfigManager;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.message.components.DefaultRoleResourceListener;
import com.hand.hap.message.profile.SystemConfigListener;
import com.hand.hap.mybatis.util.StringUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.hand.hls.fnd.dto.FndCompanyAttribute;
import com.hand.hls.fnd.mapper.FndCompanyAttributeMapper;
import com.hand.hls.sys.dto.SysUserAllocation;
import com.hand.hls.sys.dto.SysUserAuthorityRule;
import com.hand.hls.sys.service.SysUserAllocationService;
import com.hand.hls.sys.service.SysUserAuthorityRuleService;
import com.hand.hls.utils.IPUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler implements SystemConfigListener {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private SysUserAllocationService sysUserAllocationService;
    @Autowired
    private SysUserAuthorityRuleService sysUserAuthorityRuleService;
    @Autowired
    private FndCompanyAttributeMapper fndCompanyAttributeMapper;

    @Autowired
    @Qualifier("roleServiceImpl")
    private IRoleService roleService;

    @Autowired
    private DefaultRoleResourceListener roleResourceListener;

    @Autowired
    private SysConfigManager sysConfigManager;

    @Autowired
    private BlockUserConfig blockUserConfig;

    @Value("${workflow.authPattern:#}")
    private String authPattern;


    private RequestCache requestCache = new HttpSessionRequestCache();
    private HttpSessionCsrfTokenRepository tokenRepository = new HttpSessionCsrfTokenRepository();
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Map<String, IAuthenticationSuccessListener> listeners;
    public static final String DEFAULT_TARGET_URL = "DEFAULT_TARGET_URL";
    private final String loginOauthUrl = "/login?oauth";
    private final String loginUrl = "/login";
    private final String indexUrl = "/index";
    private final String refererStr = "Referer";
    private final String loginCasUrl = "/login/cas";
    private final String functionCodeStr = "functionCode";
    private final String targetUrlParameter = "forwardUrl";

    public CustomAuthenticationSuccessHandler() {
        this.setDefaultTargetUrl("/");
        this.setTargetUrlParameter(targetUrlParameter);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (this.listeners == null) {
            this.listeners = this.applicationContext.getBeansOfType(IAuthenticationSuccessListener.class);
        }

        //2023-03-30清除锁定的用户
        blockUserConfig.clear(request);

        String referer = request.getHeader("Referer");
        if (referer != null && referer.endsWith("/login?oauth")) {
            super.onAuthenticationSuccess(request, response, authentication);
        } else {
            this.clearAuthenticationAttributes(request);
            List<IAuthenticationSuccessListener> list = new ArrayList();
            list.addAll(this.listeners.values());
            Collections.sort(list);
            IAuthenticationSuccessListener successListener = null;

            try {
                Iterator var7 = list.iterator();

                while(true) {
                    if (!var7.hasNext()) {
                        HttpSession session = request.getSession(false);
                        session.setAttribute("login_change_index", "CHANGE");
                        break;
                    }

                    IAuthenticationSuccessListener listener = (IAuthenticationSuccessListener)var7.next();
                    listener.onAuthenticationSuccess(request, response, authentication);
                }
            } catch (Exception var11) {
                this.logger.error("authentication success, but error occurred in " + successListener, var11);
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate();
                }

                request.setAttribute("error", true);
                request.setAttribute("exception", var11);
                request.getRequestDispatcher("/login").forward(request, response);
                return;
            }
            // 加一下判断redirectUrl，如果存在就设置defaultTargetUrl为redirectUrl
            String requestURI = request.getRequestURI();
            String forwardUrl = request.getParameter(targetUrlParameter);
            if(requestURI.endsWith(authPattern) && StringUtils.isNotBlank(forwardUrl)){
                try {
                    this.doSelectDefaultRole(request,response);
                } catch (RoleException e) {
                    throw new RuntimeException(e);
                }

            }

            boolean isCas = requestURI.endsWith("/login/cas");
            if (isCas) {
                SavedRequest savedRequest = this.requestCache.getRequest(request, response);
                if (savedRequest != null) {
                    String targetUrl = savedRequest.getRedirectUrl();
                    this.getRedirectStrategy().sendRedirect(request, response, targetUrl);
                    return;
                }
            }

            this.handle(request, response, authentication);
        }
    }

    public List<String> getAcceptedProfiles() {
        return Arrays.asList("DEFAULT_TARGET_URL");
    }

    public void updateProfile(String profileName, String profileValue) {
        if (StringUtil.isNotEmpty(profileValue)) {
            this.setDefaultTargetUrl(profileValue);
        }

    }

    /**
     * 进行默认的认证相关配置
     */
    private void doSelectDefaultRole(HttpServletRequest request, HttpServletResponse response) throws RoleException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Long userId = (Long)session.getAttribute("userId");
            if (userId != null) {
                User user = new User();
                user.setUserId(userId);
                user.setUserName((String)session.getAttribute("userName"));
                session.setAttribute("userId", userId);
                this.addCookie("userId", userId.toString(), request, response);

            }


            List<SysUserAllocation> list = this.sysUserAllocationService.editQuery(RequestHelper.createServiceRequest(request), userId, null);
            SysUserAllocation sa = null;
            if (CollectionUtils.isNotEmpty(list)) {
                sa = list.get(0);
            }

            if (sa != null && sa.getRoleId() != null) {
                this.roleService.checkUserRoleExists(userId, sa.getRoleId());
                if (!this.sysConfigManager.getRoleMergeFlag()) {
                    Long[] ids = new Long[]{sa.getRoleId()};
                    session.setAttribute("roleIds", ids);
                }

                this.createSessionContext(session, sa);
                session.setAttribute("subject", "HAP");
                session.setAttribute("roleId", sa.getRoleId());
                session.setAttribute("role_id", sa.getRoleId());
                //内外网分离：登录成功后，把当前的登录客户端IP写入session中
                session.setAttribute("login_ip_address", IPUtils.getRequestIpAddress(request));
            }

            List<Long> roleIds;
            if (!sysConfigManager.getRoleMergeFlag()) {
                roleIds = Arrays.asList(RequestHelper.createServiceRequest(request).getAllRoleId());
            } else {
                roleIds = (Collections.singletonList(RequestHelper.createServiceRequest(request).getRoleId()));
            }
            // 判断当前用户下的所有角色
            for (Long rid : roleIds) {
                roleResourceListener.getRoleResource(rid);
            }

            this.generateCsrfToken(request,response);
        }
    }

    private void generateCsrfToken(HttpServletRequest request, HttpServletResponse response){
        CsrfToken csrfToken = this.tokenRepository.loadToken(request);
        final boolean missingToken = csrfToken == null;
        if (missingToken) {
            csrfToken = this.tokenRepository.generateToken(request);
            this.tokenRepository.saveToken(csrfToken, request, response);
        }
    }

    private void createSessionContext(HttpSession session, SysUserAllocation sa) {
        session.setAttribute("allocationId", sa.getAllocationId());
        session.setAttribute("roleId", sa.getRoleId());
        session.setAttribute("roleName", sa.getRoleName());
        session.setAttribute("companyId", sa.getCompanyId());
        session.setAttribute("companyName", sa.getCompanyShortName());
        session.setAttribute("positionId", sa.getPositionId());
        session.setAttribute("unitId", sa.getUnitId());
        session.setAttribute("employeeAssignId", sa.getEmployeeAssignId());
        session.setAttribute("positionName", sa.getPositionName());
        session.setAttribute("positionCode", sa.getPositionCode());
        session.setAttribute("employeeName", sa.getName());
        session.setAttribute("unitCode", sa.getUnitCode());
        session.setAttribute("unitName", sa.getUnitName());
        session.setAttribute("email", sa.getEmail());
        session.setAttribute("sysDate", sa.getSysDate());
        session.setAttribute("employeeId", sa.getEmployeeId());
        session.setAttribute("employeeCode", sa.getEmployeeCode());
        FndCompanyAttribute fndCompanyAttribute = new FndCompanyAttribute();
        fndCompanyAttribute.setCompanyId((Long)session.getAttribute("companyId"));
        List<FndCompanyAttribute> attrList = this.fndCompanyAttributeMapper.select(fndCompanyAttribute);
        if (CollectionUtils.isNotEmpty(attrList)) {
            session.setAttribute("authorityRuleFlag", (attrList.get(0)).getAuthorityRuleFlag());
        }

        SysUserAuthorityRule sysUserAuthorityRule = new SysUserAuthorityRule();
        sysUserAuthorityRule.setAllocationId(sa.getAllocationId());
        List<SysUserAuthorityRule> ruleList = this.sysUserAuthorityRuleService.selectSelective(RequestHelper.getCurrentRequest(), sysUserAuthorityRule);
        if (CollectionUtils.isNotEmpty(ruleList)) {
            session.setAttribute("userRules", ruleList);
        }

    }

    protected void addCookie(String cookieName, String cookieValue, HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setPath(org.apache.commons.lang.StringUtils.defaultIfEmpty(request.getContextPath(), "/"));
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
    }
}
