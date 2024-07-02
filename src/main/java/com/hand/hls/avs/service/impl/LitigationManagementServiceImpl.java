package com.hand.hls.avs.service.impl;

import com.alibaba.fastjson.JSON;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.ast.dto.AstFcEstimate;
import com.hand.hls.avs.mapper.LitigationManagementMapper;
import com.hand.hls.utils.ResMessageException;
import com.hand.hls.wfl.service.IActivitiCommonService;
import com.hand.hls.wfl.service.IActivitiStartService;
import leaf.service.validation.ParameterNullException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.avs.dto.LitigationManagement;
import com.hand.hls.avs.service.ILitigationManagementService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class LitigationManagementServiceImpl extends BaseServiceImpl<LitigationManagement> implements ILitigationManagementService{
    @Autowired
    private LitigationManagementMapper litigationManagementMapper;
    @Autowired
    private ILitigationManagementService litigationManagementService;
    @Autowired
    private IActivitiStartService activitiStartService;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    private static final String APPROVED = "APPROVED";
    private static final String APPROVING = "APPROVING";
    private void approveWfl(IRequest iRequest, LitigationManagement litigationManagement) throws ResMessageException {
        litigationManagement = this.litigationManagementMapper.selectByPrimaryKey(litigationManagement);
        if (APPROVED.equals(litigationManagement.getStatus()) || APPROVING.equals(litigationManagement.getStatus())) {
            throw new ResMessageException("当前单据状态不能提交申请！");
        }
        databaseLockProvider.lock(litigationManagement);

        List<LitigationManagement> litigationManagementArrayList = new ArrayList<>();
        litigationManagementArrayList.add(litigationManagement);

        Map<String, Object> params = new HashMap<>();
        params.put("workFlowType", "LAWSUITS_MANAGEMENT_WFL");
        params.put("litigationManagementId", litigationManagement.getLitigationManagementId());
        params.put(IActivitiCommonService.WORK_FLOW_NAME, "LAWSUITS_MANAGEMENT_WFL");
        params.put(IActivitiCommonService.DEMO_NAME, "LAWSUITS_MANAGEMENT_WFL");
        params.put(IActivitiCommonService.BUSINESS_KEY, litigationManagement.getLitigationManagementId());
        params.put("documentCategory", "LAWSUITS_MANAGEMENT_WFL"); //
        params.put("documentName","诉讼管理评级审批流程"+litigationManagement.getContractNumber());
        params.put("documentNumber", litigationManagement.getContractNumber());
        params.put("litigationManagement", JSON.toJSONString(litigationManagement));
        params.put("startUserName", iRequest.getUserName());



        activitiStartService.start(iRequest, litigationManagementArrayList, params);

        LitigationManagement litigationManagement1 = new LitigationManagement();
        litigationManagement1.setLitigationManagementId(litigationManagement.getLitigationManagementId());
        litigationManagement1.setStatus(APPROVING);
        this.litigationManagementService.updateByPrimaryKeySelective(iRequest, litigationManagement1);

    }

    @Override
    public List<LitigationManagement> conInceptSubmit(IRequest iRequest, LitigationManagement litigationManagement) throws ResMessageException, ParameterNullException {
        //启动工作流
        approveWfl(iRequest, litigationManagement);

        List<LitigationManagement> contractInsure = new ArrayList<>();
        contractInsure.add(litigationManagement);
        return contractInsure;
    }
}