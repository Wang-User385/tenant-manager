package com.hand.hls.abs.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.wfl.service.IActivitiStartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.abs.dto.HlsCusAbsInformationDisclosure;
import com.hand.hls.abs.service.HlsCusAbsInformationDisclosureService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusAbsInformationDisclosureServiceImpl extends BaseServiceImpl<HlsCusAbsInformationDisclosure> implements HlsCusAbsInformationDisclosureService{

    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;
    @Autowired
    private HlsEmployeeMapper employeeMapper;
    @Autowired
    private IActivitiStartService activitiStartService;
    @Override
    public HlsCusAbsInformationDisclosure ctAbsInformationDisclosureCreate(IRequest iRequest, HlsCusAbsInformationDisclosure dto) {
        HlsCusAbsInformationDisclosure newRecord = new HlsCusAbsInformationDisclosure();
        //获取编码规则
        Map<String, String> params = new HashMap<String, String>();
        newRecord.setUnitId(Long.valueOf(iRequest.getAttribute("unitId")));
        newRecord.setApprovalStatus("NEW");
        newRecord.setDisclosureNumber(fndCodingRuleValuesService.getCodeRuleValue(iRequest, "INFORMATION_DISCLOSURE", "INFORMATION_DISCLOSURE", "INFORMATION_DISCLOSURE", params));
        self().insert(iRequest,newRecord);
        return newRecord;
    }

    @Override
    public void submitInformationDisclosureWfl(IRequest iRequest,HlsCusAbsInformationDisclosure dto) throws HlsCusException{
        List<HlsCusAbsInformationDisclosure> absInformationDisclosures = new ArrayList<>();
        if(dto.getDisclosureId() != null){
            dto =this.selectByPrimaryKey(iRequest,dto);
            absInformationDisclosures.add(dto);
            //获取申请人
            HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
            if (ObjectUtils.isEmpty(employee)) {
                throw new HlsCusException("获取提交人失败");
            }
            String employeeCode = employee.getEmployeeCode();
            iRequest.setEmployeeCode(employeeCode);

            //开始流程
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("workFlowType", "INFORMATION_DISCLOSE_APPROVAL");

            activitiStartService.start(iRequest, absInformationDisclosures, params);

            //修改单据状态
            dto.setApprovalStatus("APPROVING");
            this.updateByPrimaryKeySelective(iRequest,dto);
        }
    }
}