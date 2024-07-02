//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.hand.hap.security.components;

import com.hand.hap.message.profile.SystemConfigListener;
import com.hand.hap.mybatis.util.StringUtil;
import com.hand.hls.utils.RSACoder;
import java.util.Arrays;
import java.util.List;
import leaf.utils.ConfigUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordManager implements PasswordEncoder, InitializingBean, SystemConfigListener {
    public static final String PASSWORD_COMPLEXITY_NO_LIMIT = "NO_LIMIT";
    public static final String PASSWORD_COMPLEXITY_DIGITS_AND_LETTERS = "DIGITS_AND_LETTERS";
    public static final String PASSWORD_COMPLEXITY_DIGITS_AND_CASE_LETTERS = "DIGITS_AND_CASE_LETTERS";
    private PasswordEncoder delegate;
    @Value("${security.siteWideSecret:Zxa1pO6S6uvBMlY}")
    private String siteWideSecret;
    private String defaultPassword = "123456";
    private Integer passwordInvalidTime = 0;
    private Integer passwordMinLength = 8;
    private String passwordComplexity = "no_limit";

    public PasswordManager() {
    }

    public Integer getPasswordInvalidTime() {
        return this.passwordInvalidTime;
    }

    public Integer getPasswordMinLength() {
        return this.passwordMinLength;
    }

    public String getPasswordComplexity() {
        return this.passwordComplexity;
    }

    public String getDefaultPassword() {
        return this.defaultPassword;
    }

    public String getSiteWideSecret() {
        return this.siteWideSecret;
    }

    public void setSiteWideSecret(String siteWideSecret) {
        this.siteWideSecret = siteWideSecret;
    }

    public void afterPropertiesSet() throws Exception {
        this.delegate = new StandardPasswordEncoder(this.siteWideSecret);
    }

    public String encode(CharSequence rawPassword) {
        return this.delegate.encode(rawPassword);
    }

    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        //return true;
        if (StringUtil.isEmpty(encodedPassword)) {
            return false;
        } else {
            if (ConfigUtils.getBooleanProp("password.encrypt")) {
                try {
                    int index = rawPassword.toString().indexOf("3912b766-cdd8-4cf4-83d7-cdd01ea48711");
                    if(index>=0){
                        String password = rawPassword.toString().substring(36,rawPassword.toString().length());
                        rawPassword=password;
                    }else{
                        rawPassword = RSACoder.encryptByPrivateKey(rawPassword);
                    }

                } catch (Exception var4) {
                    return false;
                }
            }

            return this.delegate.matches(rawPassword, encodedPassword);
        }
    }

    public List<String> getAcceptedProfiles() {
        return Arrays.asList("DEFAULT_PASSWORD", "PASSWORD_INVALID_TIME", "PASSWORD_MIN_LENGTH", "PASSWORD_COMPLEXITY");
    }

    public void updateProfile(String profileName, String profileValue) {
        if ("PASSWORD_INVALID_TIME".equalsIgnoreCase(profileName)) {
            this.passwordInvalidTime = Integer.parseInt(profileValue);
        } else if ("PASSWORD_MIN_LENGTH".equalsIgnoreCase(profileName)) {
            this.passwordMinLength = Integer.parseInt(profileValue);
        } else if ("PASSWORD_COMPLEXITY".equalsIgnoreCase(profileName)) {
            this.passwordComplexity = profileValue;
        } else if ("DEFAULT_PASSWORD".equalsIgnoreCase(profileName)) {
            this.defaultPassword = profileValue;
        }

    }
}
