package com.hand.hap.security;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.hand.hap.account.service.IRole;
import com.hand.hap.account.service.IRoleService;
import com.hand.hap.core.BaseConstants;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.components.SysConfigManager;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.core.util.RequestUtil;
import com.hand.hap.function.dto.Resource;
import com.hand.hap.message.components.DefaultRoleResourceListener;
import com.hand.hls.utils.ResponseUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.FilterInvocation;

/**
 * @author hailor
 * @date 16/6/12.
 */
public class PermissionVoter implements AccessDecisionVoter<FilterInvocation> {
    private static final Logger logger = LoggerFactory.getLogger(PermissionVoter.class);

    private static final List<String> whiteList = Arrays.asList(
            "modules/sys/SYS1050/sys_cache_data_reload.lsc",
            "modules/sys/SYS1050/sys_query_cache_data.lsc",
            "server/parameters/lov/query",
            "server/parameters/lov/comboBox/query",
            "role",
            "leafRole",
            "logout",
            "atm_upload.lsc;"
    );

    @Autowired
    @Qualifier("roleServiceImpl")
    private IRoleService roleService;


    @Autowired
    private DefaultRoleResourceListener roleResourceListener;

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return false;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Autowired
    private SysConfigManager sysConfigManager;

    @Override
    public int vote(Authentication authentication, FilterInvocation fi, Collection<ConfigAttribute> attributes) {
        int result = ACCESS_ABSTAIN;
        assert authentication != null;
        assert fi != null;
        assert attributes != null;
        // 已经 permitAll 的 url 不再过滤(主要是一些资源类 url,通用 url)
        for (ConfigAttribute attribute : attributes) {
            if ("permitAll".equals(attribute.toString())) {
                return result;
            }
        }

        HttpServletRequest request = fi.getRequest();
        HttpServletResponse response = fi.getResponse();
        String uri = StringUtils.substringAfter(request.getRequestURI(), request.getContextPath());

        if (uri.startsWith("/")) {
            uri = uri.substring(1);
        }
        if ("".equals(uri)) {
            return ACCESS_ABSTAIN;
        }

        if(uri.endsWith(".js") || uri.endsWith(".css") || uri.endsWith(".png") || uri.endsWith(".svg") || uri.endsWith(".jpg")){
            return ACCESS_GRANTED;
        }

        //首先判断是否白名单并且已登陆，直接放行
        if ((whiteList.contains(uri) || uri.indexOf("websocket") != -1) && islogined(request, response)) {
            return ACCESS_GRANTED;
        }
        Resource resource = ResourceAccessor.CURRENT_RESOURCE.get();
        ResourceAccessor.CURRENT_RESOURCE.remove();
        if (resource == null) {
            logger.warn("uri: {}, not registered", uri);

            if (RequestUtil.isAjaxRequest(request)) {
                ResponseUtil.responseErrorMsg(response, "页面或URL未注册，请联系管理员。" + uri);
            }
            return ACCESS_DENIED; //没有注册
        }
        if (authentication instanceof OAuth2Authentication) {
            OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication;
            Set<String> authrotities = AuthorityUtils.authorityListToSet(oAuth2Authentication.getAuthorities());
            IRole role = null;
            for (String roleCode : authrotities) {
                role = roleService.selectRoleByCode(roleCode);
                if (role != null) {
                    List<Long> ids = roleResourceListener.getRoleResource(role.getRoleId());
                    if (ids != null && ids.contains(resource.getResourceId())) {
                        return ACCESS_ABSTAIN;
                    }
                }
            }
            return ACCESS_DENIED;
        }
        HttpSession session = request.getSession(false);
        if (session == null) {
            return ACCESS_DENIED;
        }
        Long roleId = (Long) session.getAttribute(IRequest.FIELD_ROLE_ID);
        if (roleId == null&&!("password/reset".equals(uri))) {
            ResponseUtil.sendRedirect(response, "/role");
        }
        if (!BaseConstants.YES.equalsIgnoreCase(resource.getAccessCheck())) {
            if (logger.isDebugEnabled()) {
                logger.debug("url :'{}' need no access control.", uri);
            }
            return ACCESS_ABSTAIN;
        }

        List<Long> roleIds;
        if (!sysConfigManager.getRoleMergeFlag()) {
            roleIds = Arrays.asList(RequestHelper.createServiceRequest(request).getAllRoleId());
        } else {
            roleIds = (Collections.singletonList(RequestHelper.createServiceRequest(request).getRoleId()));
        }
        // 判断当前用户下的所有角色

        for (Long rid : roleIds) {
            List<Long> ids = roleResourceListener.getRoleResource(rid);
            if (ids != null && ids.contains(resource.getResourceId())) {
                return ACCESS_ABSTAIN;
            }
        }
        logger.debug("access to uri :'{}' denied.", uri);
        if (RequestUtil.isAjaxRequest(request)) {
            ResponseUtil.responseErrorMsg(response, "您没有权限访问，请联系管理员。" + resource.getUrl());
        }
        result = ACCESS_DENIED;

        return result;
    }


    /**
     * 判断是否登录
     * 1. session是否失效
     * 2. roleId是否存在
     *
     * @return
     */
    private boolean islogined(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        Long userId = (Long) session.getAttribute(IRequest.FIELD_USER_ID);
        return userId != null;
    }

}
