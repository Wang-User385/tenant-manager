package com.hand.hls.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.avs.dto.LitigationManagement;
import com.hand.hls.avs.mapper.LitigationManagementMapper;
import com.hand.hls.avs.service.ILitigationManagementService;
import com.hand.hls.pam.dto.AssetsDisposal;
import com.hand.hls.pam.dto.AssetsDisposalDetail;
import com.hand.hls.pam.dto.HlsWarrantStockHd;
import com.hand.hls.pam.mapper.AssetsDisposalDetailMapper;
import com.hand.hls.pam.mapper.AssetsDisposalMapper;
import com.hand.hls.pam.service.IAssetsDisposalDetailService;
import com.hand.hls.pam.service.IAssetsDisposalService;
import com.hand.hls.user.service.LoginUserInfoService;
import hls.core.sys.event.service.SysEventService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Transactional(rollbackFor = Exception.class)
public class LitigationManagementSubmitServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private LitigationManagementMapper litigationManagementMapper;
    @Autowired
    private ILitigationManagementService litigationManagementService;
    @Autowired
    private LoginUserInfoService loginUserInfoService;

    @Autowired
    private SysEventService sysEventService;
    public LitigationManagementSubmitServiceTask() {
    }
    private static final  String LITIGATIONMANAGEMENT = "LAWSUITS_MANAGEMENT_WFL";
    private static final  String wflName = "LAWSUITS_MANAGEMENT_WFL";
    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String litigationManagementId = delegateExecution.getProcessInstanceBusinessKey();

        LitigationManagement litigationManagement = litigationManagementMapper.selectByPrimaryKey(litigationManagementId);

        if ("APPROVED".equalsIgnoreCase(result)) {
            litigationManagement.setStatus("APPROVED");
            Map<String, Object> evenParams = new HashMap<>();

            //插入事件
            String userName = "";
            if (loginUserInfoService.queryUserInfo(requestCtx.getEmployeeCode()).size() > 0) {
                userName = (loginUserInfoService.queryUserInfo(requestCtx.getEmployeeCode())).get(0).getUserName();
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
                sysEventService.eventSave(requestCtx, litigationManagement.getLitigationManagementId(),LITIGATIONMANAGEMENT, LITIGATIONMANAGEMENT, wflName, wflName, "P2D", evenParams);

            }

        } else if ("REJECTED".equalsIgnoreCase(result)) {
            litigationManagement.setStatus("REJECTED");
        }
        litigationManagementService.updateByPrimaryKeySelective(requestCtx, litigationManagement);
    }

}
