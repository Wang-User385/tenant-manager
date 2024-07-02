
package com.hand.hap.security.provider;

import com.hand.hap.account.dto.User;
import com.hand.hap.account.service.IUserService;
import com.hand.hap.adaptor.dto.LdapConfig;
import com.hand.hls.utils.RSACoder;
import leaf.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.PlaintextPasswordEncoder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.Assert;

import javax.naming.Context;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

@Slf4j
public class MyAuthenticationProvider extends DaoAuthenticationProvider {
	private static final String USER_NOT_FOUND_PASSWORD = "userNotFoundPassword";
	private final String INTRANET_FLAG_Y = "Y";
	private PasswordEncoder passwordEncoder;
	private String userNotFoundEncodedPassword;
	private SaltSource saltSource;
	private UserDetailsService userDetailsService;
	private static final String USER_ADMIN = "admin";
	private static final List<String> whiteUserList = Arrays.asList(
			"admin",
			"shzn01",
			"shzn02",
			"lgjt01",
			"lgjt02"
	);

	@Autowired
	private IUserService userService;

	@Autowired
	LdapConfig ldapConfig;

	public MyAuthenticationProvider() {
		this.setPasswordEncoder((PasswordEncoder)(new PlaintextPasswordEncoder()));
	}

	protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

		if (authentication.getCredentials() == null) {
			log.debug("Authentication failed: no credentials provided");
			throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
		} else {
			String user = authentication.getPrincipal().toString();
			String password = authentication.getCredentials().toString();

			if(ldapConfig.getLoginSwitch()){

				//若开启了密码加密,则先将原密码解密出来
				if (ConfigUtils.getBooleanProp("password.encrypt")) {
					try {
						password = String.valueOf(RSACoder.encryptByPrivateKey(password));
					} catch (Exception var4) {
						throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
					}
				}

				User userInfo = this.userService.selectByUserName(user);

				//使用ldap登录,登录成功跳过密码, 否则抛出异常
				log.debug("MyAuthenticationProvider user:{}", user);
				/*if( (!whiteUserList.contains(user)) && ldapLogin(user, password) == null){
					throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
				}else if(whiteUserList.contains(user)){
					//启用ldap登录验证时，针对admin账号使用默认的登录验证方式
					authenticationChecksDefault(userDetails, authentication);
				}*/

				if (USER_ADMIN.equals(user)){
					//启用ldap登录验证时，admin使用默认的登录验证方式
					authenticationChecksDefault(userDetails, authentication);
				}else if((INTRANET_FLAG_Y.equals(userInfo.getIntranetFlag())) && ldapLogin(user, password) == null){
					throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
				}else if(!INTRANET_FLAG_Y.equals(userInfo.getIntranetFlag())){
					//启用ldap登录验证时，针对非内网用户使用默认的登录验证方式
					authenticationChecksDefault(userDetails, authentication);
				}

			}else{
				authenticationChecksDefault(userDetails, authentication);
			}

		}
	}

	/**
	 * 不使用ldap登录时的校验
	 * @param userDetails
	 * @param authentication
	 */
	private void authenticationChecksDefault(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) {
		Object salt = null;
		if (this.saltSource != null) {
			salt = this.saltSource.getSalt(userDetails);
		}

		if (authentication.getCredentials() == null) {
			this.logger.debug("Authentication failed: no credentials provided");
			throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
		} else {
			String presentedPassword = authentication.getCredentials().toString();
			if (!this.passwordEncoder.isPasswordValid(userDetails.getPassword(), presentedPassword, salt)) {
				this.logger.debug("Authentication failed: password does not match stored value");
				throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
			}
		}
	}

	protected void doAfterPropertiesSet() throws Exception {
		Assert.notNull(this.userDetailsService, "A UserDetailsService must be set");
	}


	public void setPasswordEncoder(Object passwordEncoder) {
		Assert.notNull(passwordEncoder, "passwordEncoder cannot be null");
		if (passwordEncoder instanceof PasswordEncoder) {
			this.setPasswordEncoder((PasswordEncoder)passwordEncoder);
		} else if (passwordEncoder instanceof org.springframework.security.crypto.password.PasswordEncoder) {
			final org.springframework.security.crypto.password.PasswordEncoder delegate = (org.springframework.security.crypto.password.PasswordEncoder)passwordEncoder;
			this.setPasswordEncoder(new PasswordEncoder() {
				public String encodePassword(String rawPass, Object salt) {
					this.checkSalt(salt);
					return delegate.encode(rawPass);
				}

				public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
					this.checkSalt(salt);
					return delegate.matches(rawPass, encPass);
				}

				private void checkSalt(Object salt) {
					Assert.isNull(salt, "Salt value must be null when used with crypto module PasswordEncoder");
				}
			});
		} else {
			throw new IllegalArgumentException("passwordEncoder must be a PasswordEncoder instance");
		}
	}

	private void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		Assert.notNull(passwordEncoder, "passwordEncoder cannot be null");
		this.userNotFoundEncodedPassword = passwordEncoder.encodePassword("userNotFoundPassword", (Object)null);
		this.passwordEncoder = passwordEncoder;
	}

	protected PasswordEncoder getPasswordEncoder() {
		return this.passwordEncoder;
	}

	public void setSaltSource(SaltSource saltSource) {
		this.saltSource = saltSource;
	}

	protected SaltSource getSaltSource() {
		return this.saltSource;
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	protected UserDetailsService getUserDetailsService() {
		return this.userDetailsService;
	}

	private LdapContext ldapLogin(String username, String password){
		LdapContext ldapContext = null;

		for (String ldapUrl : ldapConfig.getLoginUrls()) {
			ldapContext = connetLDAP(username + "@gdh.local", password, ldapUrl);
			if (ldapContext != null) {
				break;
			}
		}

		return ldapContext;
	}

	private LdapContext connetLDAP(String username, String password, String ldapUrl) {

		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		// url 格式：协议://ip:端口/组,域 可直接连接到域或者组上面
		env.put(Context.PROVIDER_URL, ldapUrl);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		// 用户名称，CN,OU,DC 分别表示：用户，组，域
		env.put(Context.SECURITY_PRINCIPAL, username);
		env.put(Context.SECURITY_CREDENTIALS, password);
		env.put("com.sun.jndi.ldap.connect.timeout", "10000");
		// 初始化上下文
		LdapContext ldapContext = null;
		try {
			ldapContext = new InitialLdapContext(env, null);
			return ldapContext;
		} catch (javax.naming.AuthenticationException e) {
			log.error("身份验证失败! ldapUrl={} username={}", ldapUrl, username, e);
			return null;
		} catch (javax.naming.CommunicationException e) {
			log.error("AD域连接失败! ldapUrl={} username={}", ldapUrl, username, e);
			return null;
		} catch (Exception e) {
			log.error("身份验证未知异常! ldapUrl={} username={}", ldapUrl, username, e);
			return null;
		}
	}
}
