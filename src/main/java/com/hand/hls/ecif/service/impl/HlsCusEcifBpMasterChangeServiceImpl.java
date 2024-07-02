package com.hand.hls.ecif.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.ecif.mapper.HlsCusEcifBpMasterChangeMapper;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.wfl.service.IActivitiStartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.ecif.dto.HlsCusEcifBpMasterChange;
import com.hand.hls.ecif.service.HlsCusEcifBpMasterChangeService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusEcifBpMasterChangeServiceImpl extends BaseServiceImpl<HlsCusEcifBpMasterChange> implements HlsCusEcifBpMasterChangeService{

    @Autowired
    private HlsCusEcifBpMasterChangeMapper hlsCusEcifBpMasterChangeMapper;

    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Autowired
    private HlsEmployeeMapper employeeMapper;


    @Autowired
    private IActivitiStartService activitiStartService;


    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;

    @Override
    public HlsCusEcifBpMasterChange ecifBpMasterChange(IRequest iRequest, HlsCusEcifBpMasterChange dto) {

        dto.setWflStatus("APPROVED_RETURN");
        HlsCusEcifBpMasterChange hlsCusEcifBpMasterChangeApprovedReturn=  hlsCusEcifBpMasterChangeMapper.selectChangeApprovedReturn(dto);
        if(hlsCusEcifBpMasterChangeApprovedReturn!=null){
            dto=hlsCusEcifBpMasterChangeApprovedReturn;
        }
        else {
            HlsCusEcifBpMasterChange hlsCusEcifBpMasterChangeData = hlsCusEcifBpMasterChangeMapper.selectChangeInfoByBpId(dto);
            hlsCusEcifBpMasterChangeData.setWflStatus("NEW");
            Map<String, String> params = new HashMap<String, String>();
            hlsCusEcifBpMasterChangeData.setChangeNumber(fndCodingRuleValuesService.getCodeRuleValue(iRequest, "BP_ECIF_CHANGE", "BP_ECIF_CHANGE", "BP_ECIF_CHANGE", params));

            dto = this.insertSelective(iRequest, hlsCusEcifBpMasterChangeData);
        }
        return dto;
    }



    @Override
    public HlsCusEcifBpMasterChange ecifBpMasterSubmitWfl(IRequest iRequest, HlsCusEcifBpMasterChange dto) {
        dto.setWflStatus("APPROVING");
        dto.setSubmitUserId(iRequest.getUserId());
        dto=this.updateByPrimaryKeySelective(iRequest,dto);
        databaseLockProvider.lock(dto);

        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        if(employee!=null){
            String employeeCode = employee.getEmployeeCode();
            iRequest.setEmployeeCode(employeeCode);
        }

        //开始流程
        Map<String, Object> params = new HashMap<>();
        List<HlsCusEcifBpMasterChange> list = new ArrayList<>();
        list.add(dto);
        params.put("workFlowType", "BP_ECIF_CHANGE_WORK_FLOW");
        activitiStartService.start(iRequest, list, params);

        return dto;
    }



    @Override
    public HlsCusEcifBpMasterChange ecifBpMasterChangeCancel(IRequest iRequest, HlsCusEcifBpMasterChange dto) {
        dto.setWflStatus("CANCEL");
        dto=this.updateByPrimaryKeySelective(iRequest,dto);
        return dto;

    }



    @Override
    public List<HlsCusEcifBpMasterChange> ecifHistoryQuery(HlsCusEcifBpMasterChange dto, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        List<HlsCusEcifBpMasterChange> list = hlsCusEcifBpMasterChangeMapper.selectByEcifHistory(dto);
        return list;
    }

}