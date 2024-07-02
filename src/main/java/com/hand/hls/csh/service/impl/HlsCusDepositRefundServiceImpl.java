package com.hand.hls.csh.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.csh.dto.HlsCusDepositRefund;
import com.hand.hls.csh.mapper.HlsCusDepositRefundMapper;
import com.hand.hls.csh.service.HlsCusDepositRefundService;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.utils.exception.HlsCusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusDepositRefundServiceImpl extends BaseServiceImpl<HlsCusDepositRefund> implements HlsCusDepositRefundService {

    @Autowired
    private HlsCusDepositRefundMapper depositRefundMapper;

    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Autowired
    private HlsEmployeeMapper employeeMapper;

    @Autowired
    private IActivitiStartService activitiStartService;

    @Override
    public List<HlsCusDepositRefund> selectDepositRefundData(IRequest iRequest, HlsCusDepositRefund hlsCusDepositRefund, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return depositRefundMapper.selectDepositRefundData(hlsCusDepositRefund);
    }

    @Override
    public void approvalDepositRefund(IRequest iRequest, HlsCusDepositRefund hlsCusDepositRefund) throws HlsCusException {
        //保存
        depositRefundMapper.insertSelective(hlsCusDepositRefund);
        //
        databaseLockProvider.lock(hlsCusDepositRefund);

        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        if (ObjectUtils.isEmpty(employee)) {
            throw new HlsCusException("获取提交人失败");
        }
        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);

        List<HlsCusDepositRefund> list = new ArrayList<>();
        list.add(hlsCusDepositRefund);

        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("workFlowType", "CM_DEPOSIT_REFUND_WFL");
        activitiStartService.start(iRequest, list, params);

        hlsCusDepositRefund.setRefundStatus("APPROVING");
        depositRefundMapper.updateByPrimaryKeySelective(hlsCusDepositRefund);
    }
}