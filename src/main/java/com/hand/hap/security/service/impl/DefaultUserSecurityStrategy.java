//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.hand.hap.security.service.impl;

import com.hand.hap.account.dto.User;
import com.hand.hap.account.exception.UserException;
import com.hand.hap.core.components.SysConfigManager;
import com.hand.hap.mybatis.util.StringUtil;
import com.hand.hap.security.IUserSecurityStrategy;
import com.hand.hap.security.components.PasswordManager;
import java.util.Date;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

@Component
public class DefaultUserSecurityStrategy implements IUserSecurityStrategy {
    private static final Pattern PASSWORD_PATTERN_DIGITS_AND_LETTERS = Pattern.compile("(?!^\\d+$)(?!^[a-zA-Z]+$)[0-9a-zA-Z]+");
    private static final Pattern PASSWORD_PATTERN_DIGITS_AND_CASE_LETTERS = Pattern.compile("(?!^\\d+$)(?!^[a-z]+$)(?!^[A-Z]+$)(?!^[\\dA-Z]+$)(?!^[\\da-z]+$)(?!^[a-zA-Z]+$)[0-9a-zA-Z]+");
//    private static final Pattern STRONG_PASSWORD = Pattern.compile("^(?![a-zA-z]+$)(?!\\d+$)(?![!@#$%^&*]+$)(?![a-zA-z\\d]+$)(?![a-zA-z!@#$%^&*]+$)(?![\\d!@#$%^&*]+$)[a-zA-Z\\d!@#$%^&*]+$");
    public static final String PASSWORD_UPDATE_REASON = "PASSWORD_UPDATE_REASON";
    public static final String PASSWORD_UPDATE_REASON_EXPIRED = "EXPIRED";
    public static final String PASSWORD_UPDATE_REASON_RESET = "RESET";
    @Autowired
    private PasswordManager passwordManager;
    @Autowired
    SysConfigManager sysConfigManager;

    public DefaultUserSecurityStrategy() {
    }

    public ModelAndView loginVerifyStrategy(User user, HttpServletRequest request) {
        String reason = null;
        if (this.passwordManager.getPasswordInvalidTime() > 0 && user.getLastPasswordUpdateDate() != null && this.daysBetween(user.getLastPasswordUpdateDate(), new Date()) >= this.passwordManager.getPasswordInvalidTime()) {
            reason = "EXPIRED";
        }

        if (this.sysConfigManager.getResetPwFlag() && StringUtil.isNotEmpty(user.getFirstLogin()) && "Y".equalsIgnoreCase(user.getFirstLogin())) {
            reason = "RESET";
        }

        if (StringUtil.isNotEmpty(reason)) {
            HttpSession session = request.getSession(false);
            session.setAttribute("PASSWORD_UPDATE_REASON", reason);
            return new ModelAndView("redirect:/password/reset");
        } else {
            return null;
        }
    }

    public void validatePassword(String newPwd, String newPwdAgain) throws UserException {
        if (!StringUtils.isEmpty(newPwd) && !StringUtils.isEmpty(newPwdAgain)) {
            if (newPwd != null && !newPwd.equals(newPwdAgain)) {
                throw new UserException("error.user.two_password_not_match", (Object[])null);
            } else {
                Integer length = this.passwordManager.getPasswordMinLength();
                if (newPwd.length() < length) {
                    throw new UserException("密码长度必须大于等于"+length, null);
                } else {
                    String complexity = this.passwordManager.getPasswordComplexity();
                    if (!"NO_LIMIT".equalsIgnoreCase(complexity)) {
                        if ("DIGITS_AND_LETTERS".equalsIgnoreCase(complexity) && !PASSWORD_PATTERN_DIGITS_AND_LETTERS.matcher(newPwd).matches()) {
                            throw new UserException("error.user.password_format_error_digits_and_letters", (Object[])null);
                        }

                        if ("DIGITS_AND_CASE_LETTERS".equalsIgnoreCase(complexity) && !PASSWORD_PATTERN_DIGITS_AND_CASE_LETTERS.matcher(newPwd).matches()) {
                            throw new UserException("error.user.password_format_error_digits_and_case_letters", (Object[])null);
                        }
                        if ("STRONG_PASSWORD".equalsIgnoreCase(complexity) && !checkPasswordStrong(newPwd)) {
                            throw new UserException("密码需是大小写字母数字特殊字符的组合", (Object[])null);
                        }

                    }

                }
            }
        } else {
            throw new UserException("error.user.password_not_empty", (Object[])null);
        }
    }
    private  boolean checkPasswordStrong(String password){
        int i = 0;
        if (password.matches(".*\\d+.*")) i++;
        if (password.matches(".*[A-Z]+.*"))i++;
        if (password.matches(".*[a-z]+.*")) i++;
        if (password.matches(".*[~!@#$%^&*()_+|<>,.?/:;'\\[\\]{}\"]+.*")) i++;
        if (i  < 4 )  return false;
        return true;
    }
    public int getOrder() {
        return 9999;
    }

    private int daysBetween(Date dateStart, Date dateEnd) {
        return (int)((dateEnd.getTime() - dateStart.getTime()) / 86400000L);
    }
}
