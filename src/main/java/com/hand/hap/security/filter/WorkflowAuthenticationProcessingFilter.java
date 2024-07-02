package com.hand.hap.security.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.account.dto.User;
import com.hand.hap.account.service.IRole;
import com.hand.hap.account.service.IRoleService;
import com.hand.hap.account.service.IUserService;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.security.CustomUserDetails;
import com.hand.hls.interfacePlatform.utils.WorkflowUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.Asserts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * description
 *
 * @author shigure 2022/12/14 15:53
 */
@Slf4j
public class WorkflowAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {


    @Autowired
    private WorkflowUtils workflowUtils;

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoleService roleService;


    public WorkflowAuthenticationProcessingFilter(@Value("${workflow.authPattern:#}") String authPattern) {
        super(new AntPathRequestMatcher(authPattern, "GET"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        String loginUserInfo = request.getParameter("loginUserInfo");
        String decrypt = workflowUtils.decrypt(loginUserInfo);
        JSONObject jsonObject = JSON.parseObject(decrypt);
        String userName = jsonObject.getString("userName");
        Asserts.notNull(userName,"userName");
        log.info("获取到userName:{}",userName);

        User user = this.userService.selectByUserName(userName);
        Asserts.notNull(user,"user");

        List<IRole> leafRoles = this.roleService.selectAllocationRolesByUser(RequestHelper.createServiceRequest(request), user);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        if (leafRoles != null) {
            Iterator var8 = leafRoles.iterator();

            while(var8.hasNext()) {
                IRole role = (IRole)var8.next();
                authorities.add(new SimpleGrantedAuthority(role.getRoleCode()));
            }
        }
        return new UsernamePasswordAuthenticationToken(new CustomUserDetails(user.getUserId(), user.getUserName(), user.getPasswordEncrypted(), true, true, true, true, authorities), (Object)null, authorities);
    }


}
