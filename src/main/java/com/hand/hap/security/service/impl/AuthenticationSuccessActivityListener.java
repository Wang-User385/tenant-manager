//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.hand.hap.security.service.impl;

import com.hand.hap.account.dto.User;
import com.hand.hap.account.service.IUserService;
import com.hand.hap.activiti.listeners.TaskCreateNotificationListener;
import com.hand.hap.security.IAuthenticationSuccessListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessActivityListener implements IAuthenticationSuccessListener {
    @Autowired
    private TaskCreateNotificationListener taskCreateNotificationListener;
    @Autowired
    private IUserService userService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        HttpSession session = request.getSession(true);
        //类型转换（Long -> String fail）报错修复
        Long employeeId = (Long)session.getAttribute("employeeId");
        if (employeeId != null) {
            String userName = (String)session.getAttribute("userName");
            Object codes = this.redisTemplate.opsForHash().get("hap:cache:badge:", userName);
            if (codes == null) {
                String employeeCode = (String)session.getAttribute("employeeCode");
                String employeeName = (String)session.getAttribute("employeeName");
                User user = new User();
                user.setEmployeeCode(employeeCode);
                user.setEmployeeName(employeeName);
                user.setUserName(userName);
                this.taskCreateNotificationListener.sendMessage(user);
                this.taskCreateNotificationListener.sendMessageForCC(user);
            }
        }

    }
}
