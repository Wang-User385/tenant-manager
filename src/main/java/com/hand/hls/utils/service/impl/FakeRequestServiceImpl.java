package com.hand.hls.utils.service.impl;

import com.hand.hap.account.dto.User;
import com.hand.hap.account.mapper.UserMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hls.sys.dto.FndEmployee;
import com.hand.hls.sys.dto.SysUserAllocation;
import com.hand.hls.sys.mapper.FndEmployeeMapper;
import com.hand.hls.sys.mapper.SysUserAllocationMapper;
import com.hand.hls.utils.ResMessageException;
import com.hand.hls.utils.service.FakeRequestService;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2019/10/9
 * @description: 构建iRequest 方法
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FakeRequestServiceImpl implements FakeRequestService {

    @Autowired
    private FndEmployeeMapper fndEmployeeMapper;
    @Autowired
    private SysUserAllocationMapper sysUserAllocationMapper;
    @Autowired
    private UserMapper userMapper;

    private static String LOCAL = "zh_CN";
    private static String ADMINISTRATOR = "admin";


    private final IRequest setMdc(IRequest iRequest) {
        Map<String, String> mdcMap = MDC.getCopyOfContextMap();
        if (mdcMap != null) {
            mdcMap.forEach((k, v) -> iRequest.setAttribute(IRequest.MDC_PREFIX.concat(k), v));
        }
        return iRequest;

    }

    private HttpServletRequest fakeRequest = new MockHttpServletRequest();

    /**
     * @Title: createFakeRequest
     * @Discription: 无参方法 默认管理员
     * @Param: []
     * @Return: com.hand.hap.core.IRequest
     */
    @Override
    public IRequest createFakeRequest(){
        User sysUser = userMapper.selectByUserName(ADMINISTRATOR);
        return createFakeRequest(sysUser.getUserId());
    }

    /**
     * @Title: createFakeRequest
     * @Discription: 根据userId 创建对应的irequest
     * @Param: [userId]
     * @Return: com.hand.hap.core.IRequest
     */
    @Override
    public IRequest createFakeRequest(Long userId) {
        User sysUser = userMapper.selectByPrimaryKey(userId);
        if (ObjectUtils.isEmpty(sysUser)) {
            throw new RuntimeException("未找到用户（userId：" + userId + "）!");
        }

        SysUserAllocation sysUserAllocation = new SysUserAllocation();
        sysUserAllocation.setUserId(sysUser.getUserId());
        List<SysUserAllocation> sysUserAllocationList = sysUserAllocationMapper.select(sysUserAllocation);
        if (sysUserAllocationList.size() <= 0) {
            throw new RuntimeException("用户" + sysUser.getUserName() + "没有进行用户分配!");
        }

        FndEmployee fndEmployee = fndEmployeeMapper.selectEmployeeInfoByAllocationId(sysUserAllocationList.get(0).getAllocationId().toString());
        if (ObjectUtils.isEmpty(fndEmployee)) {
            throw new RuntimeException("未找到员工(AllocationId：" + sysUserAllocationList.get(0).getAllocationId() + ")!");
        }

        HttpSession session = fakeRequest.getSession();

        session.setAttribute("userId", sysUser.getUserId());
        session.setAttribute("roleId", sysUserAllocationList.get(0).getRoleId());
        session.setAttribute("userName", sysUser.getUserName());
        session.setAttribute("companyId", sysUserAllocationList.get(0).getCompanyId());
        session.setAttribute("roleIds", null);
        session.setAttribute("allocationId", sysUserAllocationList.get(0).getAllocationId());
        session.setAttribute("employeeCode", fndEmployee.getEmployeeCode());


        IRequest iRequest = RequestHelper.createServiceRequest(fakeRequest);
        iRequest.setLocale(LOCAL);
        MDC.put("userId", sysUser.getUserId().toString());
        String uuid = UUID.randomUUID().toString().replace("-", "");
        MDC.put("requestId", uuid);
        MDC.put("sessionId", "");
        iRequest = setMdc(iRequest);

        iRequest.setAttribute("userId", sysUser.getUserId());
        iRequest.setAttribute("roleId", sysUserAllocationList.get(0).getRoleId());
        iRequest.setAttribute("userName", sysUser.getUserName());
        iRequest.setAttribute("companyId", sysUserAllocationList.get(0).getCompanyId());
        iRequest.setAttribute("roleIds", null);
        iRequest.setAttribute("allocationId", sysUserAllocationList.get(0).getAllocationId());
        iRequest.setAttribute("employeeCode", fndEmployee.getEmployeeCode());

        RequestHelper.setCurrentRequest(iRequest);
        ServletWebRequest servletWebRequest = new ServletWebRequest(fakeRequest);
        RequestContextHolder.setRequestAttributes(servletWebRequest);

        return iRequest;
    }
}
