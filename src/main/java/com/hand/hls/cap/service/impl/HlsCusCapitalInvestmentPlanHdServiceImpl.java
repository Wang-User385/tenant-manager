package com.hand.hls.cap.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cap.dto.HlsCusCapitalInvestmentPlanHd;
import com.hand.hls.cap.service.HlsCusCapitalInvestmentPlanHdService;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.wfl.service.IActivitiStartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusCapitalInvestmentPlanHdServiceImpl extends BaseServiceImpl<HlsCusCapitalInvestmentPlanHd> implements HlsCusCapitalInvestmentPlanHdService {
    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Autowired
    private HlsEmployeeMapper employeeMapper;

    @Autowired
    private IActivitiStartService activitiStartService;

    @Override
    public HlsCusCapitalInvestmentPlanHd capPlanSubmitWfl(IRequest request, List<HlsCusCapitalInvestmentPlanHd> hlsCusCapitalInvestmentPlanHds) {
        HlsCusCapitalInvestmentPlanHd hlsCusCapitalInvestmentPlanHd = hlsCusCapitalInvestmentPlanHds.get(0);
        //锁表
        databaseLockProvider.lock(hlsCusCapitalInvestmentPlanHd);
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(request.getUserId());
        String employeeCode = employee.getEmployeeCode();
        request.setEmployeeCode(employeeCode);
        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("workFlowType", "FUNDING_PLAN_WFL");
        activitiStartService.start(request, hlsCusCapitalInvestmentPlanHds, params);
        //修改状态为审批中
        hlsCusCapitalInvestmentPlanHd.setApproveStatus("APPROVING");
        hlsCusCapitalInvestmentPlanHd = self().updateByPrimaryKeySelective(request, hlsCusCapitalInvestmentPlanHd);

        return hlsCusCapitalInvestmentPlanHd;
    }
}