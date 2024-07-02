package com.hand.hls.archive.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.wfl.service.IActivitiStartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.archive.dto.HlsCusArchiveApproval;
import com.hand.hls.archive.service.HlsCusArchiveApprovalService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusArchiveApprovalServiceImpl extends BaseServiceImpl<HlsCusArchiveApproval> implements HlsCusArchiveApprovalService{
    @Autowired
    private HlsEmployeeMapper employeeMapper;
    @Autowired
    private IActivitiStartService activitiStartService;
    @Override
    public void approvalSubmit(IRequest iRequest, List<HlsCusArchiveApproval> hlsCusArchiveApprovals) throws HlsCusException{
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        if (ObjectUtils.isEmpty(employee)) {
            throw new HlsCusException("获取提交人失败");
        }
        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);

        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("workFlowType", "JC_ARCHIVES_MANAGE_WFL");

        activitiStartService.start(iRequest, hlsCusArchiveApprovals, params);


    }
}