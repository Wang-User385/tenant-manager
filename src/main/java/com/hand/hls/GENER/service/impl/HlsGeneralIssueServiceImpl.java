package com.hand.hls.GENER.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.GENER.mapper.HlsGeneralIssueMapper;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.utils.ResMessageException;
import com.hand.hls.wfl.service.IActivitiStartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.GENER.dto.HlsGeneralIssue;
import com.hand.hls.GENER.service.IHlsGeneralIssueService;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsGeneralIssueServiceImpl extends BaseServiceImpl<HlsGeneralIssue> implements IHlsGeneralIssueService{
    @Autowired
    private IHlsGeneralIssueService hlsgeneralissueservice;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private HlsEmployeeMapper employeeMapper;
    @Autowired
    private HlsGeneralIssueMapper hlsgeneralissuemapper;

    @Autowired
    private IActivitiStartService activitiStartService;
    @Override
    public void SubmitWfl(IRequest iRequest, HlsGeneralIssue hlsgeneralissue) throws ResMessageException  {
        List<HlsGeneralIssue> hlsCusPrjProjectList = new ArrayList<>();
        hlsgeneralissue = hlsgeneralissueservice.selectByPrimaryKey(iRequest, hlsgeneralissue);
        hlsCusPrjProjectList.add(hlsgeneralissue);

        databaseLockProvider.lock(hlsgeneralissue);
        Long unitId = hlsgeneralissuemapper.queryGeneralUnitId(hlsgeneralissue.getNextApprovePerson());
         long positionId=hlsgeneralissuemapper.queryGeneralPositionId(unitId);
         String employeeCode=hlsgeneralissuemapper.queryGeneralEmployeeCode(positionId);
        //获取申请人
/*
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
*/

      //  String employeeCode = employee.getEmployeeCode();


/*
       String employeeCode="JIAOYU";
*/
        if (employeeCode.equals("null")) {
            throw new ResMessageException("获取提交人失败");
        }
        iRequest.setEmployeeCode(employeeCode);
        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        //FCT_CONTRACT_SIGN_WORK_FLOW
        params.put("workFlowType", "GENERAL_MATTERS_WFL");
        activitiStartService.start(iRequest, hlsCusPrjProjectList, params);

  hlsgeneralissue.setStatus("APPROVING");

        self().updateByPrimaryKeySelective(iRequest, hlsgeneralissue);
    }
}