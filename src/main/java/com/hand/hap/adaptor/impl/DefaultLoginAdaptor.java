//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hap.adaptor.impl;

import com.hand.hap.account.dto.User;
import com.hand.hap.account.exception.RoleException;
import com.hand.hap.account.exception.UserException;
import com.hand.hap.account.service.IRole;
import com.hand.hap.account.service.IRoleService;
import com.hand.hap.account.service.IUserService;
import com.hand.hap.adaptor.ILoginAdaptor;
import com.hand.hap.core.components.CaptchaConfig;
import com.hand.hap.core.components.SysConfigManager;
import com.hand.hap.core.exception.BaseException;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.core.util.TimeZoneUtil;
import com.hand.hap.fnd.service.ICompanyService;
import com.hand.hap.security.IUserSecurityStrategy;
import com.hand.hap.security.TokenUtils;
import com.hand.hap.security.captcha.ICaptchaManager;
import com.hand.hap.security.service.impl.UserSecurityStrategyManager;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fnd.dto.FndCompanyAttribute;
import com.hand.hls.fnd.mapper.FndCompanyAttributeMapper;
import com.hand.hls.sys.dto.SysUserAllocation;
import com.hand.hls.sys.dto.SysUserAuthorityRule;
import com.hand.hls.sys.service.SysUserAllocationService;
import com.hand.hls.sys.service.SysUserAuthorityRuleService;
import com.hand.hls.utils.IPUtils;
import com.hand.hls.utils.RSACoder;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import leaf.utils.ConfigUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.WebUtils;

/**
 * @Description：重写登录逻辑的部分功能
 * @Author：liangxian.chen@hand-china.com
 * @Date：2023/3/6 17:15
 * @Version：1.0
 */
public class DefaultLoginAdaptor implements ILoginAdaptor {
    private static final boolean VALIDATE_CAPTCHA = true;
    private static final String KEY_VERIFICODE = "verifiCode";
    private static final String VIEW_INDEX = "/leaf.lview";
    private static final String VIEW_LOGIN = "/login";
    private static final String VIEW_ROLE_SELECT = "/role";
    private static final String DEFAULT_HOME_PAGE = "redirect:/leaf.lview";
    @Autowired
    private ICaptchaManager captchaManager;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    @Qualifier("roleServiceImpl")
    private IRoleService roleService;
    @Autowired
    private IUserService userService;
    @Autowired
    private CaptchaConfig captchaConfig;
    @Autowired
    private SysConfigManager sysConfigManager;
    @Autowired
    UserSecurityStrategyManager userSecurityStrategyManager;
    @Autowired
    private ICompanyService companyService;
    @Autowired
    private SysUserAllocationService sysUserAllocationService;
    @Autowired
    private SysUserAuthorityRuleService sysUserAuthorityRuleService;
    @Autowired
    private FndCompanyAttributeMapper fndCompanyAttributeMapper;
    @Value("${sys.login.useHttps:false}")
    private boolean useHttps;

    public DefaultLoginAdaptor() {
    }

    public ModelAndView doLogin(User user, HttpServletRequest request, HttpServletResponse response) {
        ModelAndView view = new ModelAndView();
        Locale locale = RequestContextUtils.getLocale(request);
        view.setViewName(this.getLoginView(request));

        try {
            this.beforeLogin(view, user, request, response);
            this.checkCaptcha(view, user, request, response);
            user = this.userService.login(user);
            HttpSession session = request.getSession(true);
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("userName", user.getUserName());
            session.setAttribute("locale", locale.toString());
            this.setTimeZoneFromPreference(session, user.getUserId());
            this.generateSecurityKey(session);
            this.afterLogin(view, user, request, response);
        } catch (UserException var7) {
            view.addObject("msg", this.messageSource.getMessage(var7.getCode(), var7.getParameters(), locale));
            view.addObject("code", var7.getCode());
            this.processLoginException(view, user, var7, request, response);
        }

        return view;
    }

    private void setTimeZoneFromPreference(HttpSession session, Long accountId) {
        String tz = "GMT+0800";
        if (StringUtils.isBlank(tz)) {
            tz = TimeZoneUtil.toGMTFormat(TimeZone.getDefault());
        }

        session.setAttribute("timeZone", tz);
    }

    private String generateSecurityKey(HttpSession session) {
        return TokenUtils.setSecurityKey(session);
    }

    protected void beforeLogin(ModelAndView view, User account, HttpServletRequest request, HttpServletResponse response) throws UserException {
    }

    protected void processLoginException(ModelAndView view, User account, UserException e, HttpServletRequest request, HttpServletResponse response) {
    }

    private void checkCaptcha(ModelAndView view, User user, HttpServletRequest request, HttpServletResponse response) throws UserException {
        Cookie cookie = WebUtils.getCookie(request, this.captchaManager.getCaptchaKeyName());
        String captchaCode = request.getParameter("verifiCode");
        if (cookie == null || StringUtils.isEmpty(captchaCode) || !this.captchaManager.checkCaptcha(cookie.getValue(), captchaCode)) {
            throw new UserException("error.login.verification_code_error", "error.login.verification_code_error", (Object[])null);
        }
    }

    protected void afterLogin(ModelAndView view, User user, HttpServletRequest request, HttpServletResponse response) throws UserException {
        view.setViewName("redirect:" + this.getRoleView(request));
        Cookie cookie = new Cookie("userName", user.getUserName());
        cookie.setPath(StringUtils.defaultIfEmpty(request.getContextPath(), "/"));
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
    }

    public ModelAndView doSelectRole(IRole role, HttpServletRequest request, HttpServletResponse response) throws RoleException {
        ModelAndView result = new ModelAndView();
        HttpSession session = request.getSession(false);
        if (session != null) {
            Long userId = (Long)session.getAttribute("userId");
            Long allocationId = ((SysUserAllocation)role).getAllocationId();
            List<SysUserAllocation> list = this.sysUserAllocationService.editQuery(RequestHelper.createServiceRequest(request), userId, allocationId);
            SysUserAllocation sa = null;
            if (list != null && list.size() > 0) {
                sa = (SysUserAllocation)list.get(0);
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
                //2023-03新增内外网分离：登录成功后，把当前的登录客户端IP写入session中
                session.setAttribute("login_ip_address", IPUtils.getRequestIpAddress(request));
                result.setViewName("redirect:" + this.getIndexView(request));
            }
        } else {
            result.setViewName("redirect:" + this.getLoginView(request));
        }

        return result;
    }

    protected String getIndexView(HttpServletRequest request) {
        return "/leaf.lview";
    }

    protected String getLoginView(HttpServletRequest request) {
        return "/login";
    }

    protected String getRoleView(HttpServletRequest request) {
        return "/role";
    }

    public IUserService getUserService() {
        return this.userService;
    }

    public ModelAndView indexView(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        ModelAndView mav = this.indexModelAndView(request, response);
        if (session != null) {
            Long userId = (Long)session.getAttribute("userId");
            if (userId == null) {
                return new ModelAndView("redirect:" + this.getLoginView(request));
            }

            if (session.getAttribute("login_change_index") != null) {
                User user = new User();
                user.setUserId(userId);
                user = (User)this.userService.selectByPrimaryKey(RequestHelper.createServiceRequest(request), user);
                List<IUserSecurityStrategy> userSecurityStrategies = this.userSecurityStrategyManager.getUserSecurityStrategyList();
                Iterator var8 = userSecurityStrategies.iterator();

                while(var8.hasNext()) {
                    IUserSecurityStrategy userSecurityStrategy = (IUserSecurityStrategy)var8.next();
                    ModelAndView mv = userSecurityStrategy.loginVerifyStrategy(user, request);
                    if (mv != null) {
                        return mv;
                    }
                }

                session.removeAttribute("login_change_index");
            }

            if (!this.sysConfigManager.getRoleMergeFlag()) {
                Long roleId = (Long)session.getAttribute("roleId");
                if (roleId == null) {
                    return new ModelAndView("redirect:" + this.getRoleView(request));
                }

                User user = new User();
                user.setUserId(userId);
                List<IRole> roles = this.roleService.selectRolesByUser(RequestHelper.createServiceRequest(request), user);
                mav.addObject("SYS_USER_ROLES", roles);
                mav.addObject("CURRENT_USER_ROLE", roleId);
            }
        }

        String sysTitle = this.sysConfigManager.getSysTitle();
        mav.addObject("SYS_TITLE", sysTitle);
        if (mav.getViewName().equals("redirect:/leaf.lview")) {
            mav.getModelMap().clear();
        }

        return mav;
    }

    public ModelAndView indexModelAndView(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("redirect:/leaf.lview");
    }

    public ModelAndView loginView(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            return this.indexView(request, response);
        } else {
            ModelAndView view = new ModelAndView(this.getLoginView(request));
            Cookie cookie = WebUtils.getCookie(request, "loginKey");
            if (this.captchaConfig.getWrongTimes() > 0 && cookie == null) {
                String uuid = UUID.randomUUID().toString();
                cookie = new Cookie("loginKey", uuid);
                cookie.setPath(StringUtils.defaultIfEmpty(request.getContextPath(), "/"));
                cookie.setMaxAge(this.captchaConfig.getExpire());
                cookie.setHttpOnly(true);
                if (this.useHttps) {
                    cookie.setSecure(true);
                }

                response.addCookie(cookie);
                this.captchaConfig.updateLoginFailureInfo(cookie);
            }

            view.addObject("ENABLE_CAPTCHA", this.captchaConfig.isEnableCaptcha(cookie));
            view.addObject("SYS_TITLE", this.sysConfigManager.getSysTitle());
            Boolean error = (Boolean)request.getAttribute("error");

            Object exception;
            for(exception = (Exception)request.getAttribute("exception"); exception != null && ((Throwable)exception).getCause() != null; exception = ((Throwable)exception).getCause()) {
            }

            String code = "error.login.name_password_not_match";
            if (exception instanceof BaseException) {
                code = ((BaseException)exception).getDescriptionKey();
            }


            //2023-03新增内外网分离：验证不通过，返回错误信息到前端
            if (exception instanceof AuthenticationException) {
                code = ((AuthenticationException)exception).getMessage();
            }

            if (exception instanceof BadCredentialsException) {
                code = "error.login.name_password_not_match";
            }

            if (error != null && error) {
                String msg = null;
                if(request.getAttribute("errorMsg") != null){
                    msg = String.valueOf(request.getAttribute("errorMsg"));
                }
                if(StringUtils.isEmpty(msg)){
                    Locale locale = RequestContextUtils.getLocale(request);
                    msg = messageSource.getMessage(code, null, locale);
                }
                view.addObject("msg", msg);
            }

            if (ConfigUtils.getBooleanProp("password.encrypt")) {
                view.addObject("PUB_KEY", RSACoder.getPublicKey());
            }

            return view;
        }
    }

    public ModelAndView roleView(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new ModelAndView(this.getRoleView(request));
        HttpSession session = request.getSession(false);
        mv.addObject("SYS_TITLE", this.sysConfigManager.getSysTitle());
        if (session != null) {
            Long userId = (Long)session.getAttribute("userId");
            if (userId != null) {
                User user = new User();
                user.setUserId(userId);
                user.setUserName((String)session.getAttribute("userName"));
                session.setAttribute("userId", userId);
                this.addCookie("userId", userId.toString(), request, response);
                List<IRole> roles = this.roleService.selectAllocationRolesByUser(RequestHelper.createServiceRequest(request), user);
                mv.addObject("roles", roles);
            }
        }

        return mv;
    }

    protected void addCookie(String cookieName, String cookieValue, HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setPath(StringUtils.defaultIfEmpty(request.getContextPath(), "/"));
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
    }

    public ResponseData sessionExpiredLogin(User account, HttpServletRequest request, HttpServletResponse response) throws RoleException {
        ResponseData data = new ResponseData();
        ModelAndView view = this.doLogin(account, request, response);
        ModelMap mm = view.getModelMap();
        if (mm.containsAttribute("code")) {
            data.setSuccess(false);
            data.setCode((String)mm.get("code"));
            data.setMessage((String)mm.get("msg"));
        } else {
            Object userIdObj = request.getParameter("userId");
            Object roleIdObj = request.getParameter("roleId");
            if (userIdObj != null && roleIdObj != null) {
                Long userId = Long.valueOf(userIdObj.toString());
                Long roleId = Long.valueOf(roleIdObj.toString());
                this.roleService.checkUserRoleExists(userId, roleId);
                HttpSession session = request.getSession();
                session.setAttribute("userId", userId);
                session.setAttribute("roleId", roleId);
            }
        }

        return data;
    }

    public void createSessionContext(HttpSession session, SysUserAllocation sa) {
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
        if (attrList.size() > 0) {
            session.setAttribute("authorityRuleFlag", ((FndCompanyAttribute)attrList.get(0)).getAuthorityRuleFlag());
        }

        SysUserAuthorityRule sysUserAuthorityRule = new SysUserAuthorityRule();
        sysUserAuthorityRule.setAllocationId(sa.getAllocationId());
        List<SysUserAuthorityRule> ruleList = this.sysUserAuthorityRuleService.selectSelective(RequestHelper.getCurrentRequest(), sysUserAuthorityRule);
        if (ruleList.size() > 0) {
            session.setAttribute("userRules", ruleList);
        }

    }
}
