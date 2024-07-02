package com.hand.hap.security.filter;

import com.hand.hap.account.dto.User;
import com.hand.hap.account.service.IRole;
import com.hand.hap.account.service.IRoleService;
import com.hand.hap.account.service.IUserService;
import com.hand.hap.core.components.BlockUserConfig;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.security.CustomUserDetails;
import com.hand.hap.security.RSAUtils;
import com.hand.hap.security.components.PasswordManager;
import com.hand.hls.sys.mapper.UserExtensionMapper;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @Description：登录过滤器，新增验证密码错误锁定功能
 * @Author：liangxian.chen@hand-china.com
 * @Date：2023/3/30 15:14
 * @Version：1.0
 */

public class UsernamePasswordAuthenticationExtendFilter extends UsernamePasswordAuthenticationFilter {
    private boolean postOnly = true;
    @Value("${ssoLoginFlag:false}")
    private Boolean ssoLoginFlag;
    @Autowired
    private IUserService userService;
    @Autowired
    private IRoleService roleService;
    @Autowired
    private UserExtensionMapper userExtensionMapper;
    @Autowired
    private PasswordManager passwordManager;

    @Autowired
    private BlockUserConfig blockUserConfig;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String REDIS_BLOCK_KEY = "hap:cache:BLOCK:";

    private RSAPrivateKey privateKey = RSAUtils.getPrivateKey();

    public UsernamePasswordAuthenticationExtendFilter() {
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else {
            String username = this.obtainUsername(request);
            String password = this.obtainPassword(request);
            if (username == null) {
                username = "";
            }

            if (password == null) {
                password = "";
            }

            username = username.trim();
            if (this.loginFromPortal(request)) {
                username = this.decrypt(username);
                password = this.decrypt(password);
                User user = this.userService.selectByUserName(username);
                if (user != null && this.passwordManager.matches(password, user.getPasswordEncrypted())) {
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

                password = "";
            }else if(blockUserConfig.checkBlock(request)){
                //20230330:用户是否锁定
                //单点登录不控制
                throw new AuthenticationServiceException("用户已锁定，请"  + (redisTemplate.opsForValue().getOperations().getExpire(REDIS_BLOCK_KEY+username, TimeUnit.MINUTES) + 1) +  "分钟后重试。");
            }

            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
            this.setDetails(request, authRequest);
            return this.getAuthenticationManager().authenticate(authRequest);
        }
    }

    private String decrypt(String encodedValue) {
        if (this.privateKey == null) {
            this.logger.warn("No private key. Can not decrypt values");
            return "";
        } else {
            String s = RSAUtils.decryptByPrivate(encodedValue, this.privateKey);
            return s == null ? "" : s;
        }
    }

    private boolean loginFromPortal(HttpServletRequest request) {
        return request != null && "Y".equals(request.getParameter("__portal_login__"));
    }
}
