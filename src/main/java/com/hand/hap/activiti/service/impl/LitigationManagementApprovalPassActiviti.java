package com.hand.hap.activiti.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.activiti.components.IHlsCusActivitiBean;
import com.hand.hls.activiti.dto.HlsCusProcess;
import com.hand.hls.avs.dto.LitigationManagement;
import com.hand.hls.avs.mapper.LitigationManagementMapper;
import com.hand.hls.avs.service.ILitigationManagementService;
import com.hand.hls.hn.dto.PrjCheck;
import com.hand.hls.hn.mapper.PrjCheckMapper;
import com.hand.hls.hn.service.IPrjCheckService;
import com.hand.hls.user.service.LoginUserInfoService;
import hls.core.sys.event.service.SysEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: quzongkai
 * @date: 2020/6/12
 * @description: 项目审查工作流一键操作
 */
@Component
public class LitigationManagementApprovalPassActiviti implements IHlsCusActivitiBean {

    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Autowired
    private LitigationManagementMapper litigationManagementMapper;
    @Autowired
    private ILitigationManagementService litigationManagementService;
    @Autowired
    private LoginUserInfoService loginUserInfoService;
    @Autowired
    private SysEventService sysEventService;
    private static final  String LITIGATIONMANAGEMENT = "LAWSUITS_MANAGEMENT_WFL";
    private static final  String wflName = "LAWSUITS_MANAGEMENT_WFL";
    private final static String PASS = "PASS";
    private final static String REJECT = "REJECT";

    @Override
    public void excute(IRequest iRequest, HlsCusProcess hlsCusProcess) {

        LitigationManagement litigationManagement = litigationManagementMapper.selectByPrimaryKey(hlsCusProcess.getBussinessKey());
        databaseLockProvider.lock(litigationManagement);

        if (PASS.equals(hlsCusProcess.getType())) {
            litigationManagement.setStatus("APPROVED");
            Map<String, Object> evenParams = new HashMap<>();

            //插入事件
            String userName = "";
            if (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode()).size() > 0) {
                userName = (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode())).get(0).getUserName();
            }
            //消息用于动态

            List<String> allocationIds = litigationManagementMapper.getAllocationIds();
            for(String allocationId:allocationIds){
                String msg = userName + "创建了诉讼审批" + litigationManagement.getContractNumber();
                evenParams.put("message", msg);
                evenParams.put("noticeTitle", "诉讼审批申请");
                evenParams.put("url", "");
                evenParams.put("level", 2L);
                evenParams.put("noticeType", "NOTICE");
                //evenParams.put("sourceUserId", requestCtx.getUserId());
                //allocation_id
                evenParams.put("allocation_id",allocationId);
                sysEventService.eventSave(iRequest, litigationManagement.getLitigationManagementId(),LITIGATIONMANAGEMENT, LITIGATIONMANAGEMENT, wflName, wflName, "P2D", evenParams);

            }
        } else if (REJECT.equals(hlsCusProcess.getType())) {
            litigationManagement.setStatus("REJECTED");

        }
        litigationManagementService.updateByPrimaryKeySelective(iRequest, litigationManagement);
    }
}
