package com.hand.hls.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.mybatis.util.StringUtil;
import com.hand.hls.activiti.mapper.HlsCusActVarMapper;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.ecif.dto.HlsCusBpMasterRequestRecords;
import com.hand.hls.ecif.service.HlsCusBpMasterRequestRecordsService;
import com.hand.hls.fct.dto.HlsCusHlsCreditLine;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineMapper;
import com.hand.hls.fct.service.HlsCreditLineService;
import com.hand.hls.fnd.dto.HlsCusEmployee;
import com.hand.hls.fnd.mapper.HlsCusEmployeeMapper;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectSupplement;
import com.hand.hls.prj.mapper.HlsCusPrjProjectSupplementMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import hls.core.sys.event.service.SysEventService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by xuju on 2018/04/19.
 * modify by xuju on 2018/05/22
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjProjectSuppleSubmitServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private HlsCreditLineService hlsCreditLineService;
    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;
    @Autowired
    private HlsCusHlsCreditLineMapper hlsCusHlsCreditLineMapper;

    @Autowired
    private SysEventService sysEventService;

    @Autowired
    private IActivitiService activitiService;

    @Autowired
    private HlsCusEmployeeMapper employeeMapper;

    @Autowired
    private HlsCusActVarMapper hlsCusActVarMapper;

    @Autowired
    private HlsCusBpMasterRequestRecordsService hlsCusBpMasterRequestRecordsService;

    @Autowired
    private HlsCusPrjProjectSupplementMapper hlsCusPrjProjectSupplementMapper;



    public HlsCusPrjProjectSuppleSubmitServiceTask(){}
    @Override
    public void execute(DelegateExecution delegateExecution) {
        String flag = null;
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        IRequest requestCtx = (IRequest)delegateExecution.getVariable("iRequest");
        String result = (String)delegateExecution.getVariable("approveResult");
        String employeeCode = (String)delegateExecution.getVariable("startUserName");
        String hlsCusPrjProjectPrams = (String) delegateExecution.getVariable("hlsCusPrjProject");
        HlsCusPrjProject hlsCusPrjProject = JSON.parseObject(hlsCusPrjProjectPrams, HlsCusPrjProject.class);

        String  prjSupple = String.valueOf(delegateExecution.getVariable("prjSuppleId"));
        Long prjSuppleId = Long.valueOf(prjSupple);

        HlsCusPrjProject resultHlsCusPrjProject = new HlsCusPrjProject();
        String unitId = String.valueOf(delegateExecution.getVariable("unitId"));
        String companyId = String.valueOf(delegateExecution.getVariable("companyId"));

        resultHlsCusPrjProject.setProjectId(hlsCusPrjProject.getProjectId());
        resultHlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(requestCtx, resultHlsCusPrjProject);
        databaseLockProvider.lock(resultHlsCusPrjProject);


        HlsCusPrjProjectSupplement hlsCusPrjProjectSupplement = new HlsCusPrjProjectSupplement();
        HlsCusPrjProjectSupplement cusPrjProjectSupplement = new HlsCusPrjProjectSupplement();
        cusPrjProjectSupplement.setPrjSuppleId(prjSuppleId);
        hlsCusPrjProjectSupplement = hlsCusPrjProjectSupplementMapper.selectByPrimaryKey(cusPrjProjectSupplement);


        if("APPROVED".equalsIgnoreCase(result)){
            flag = "APPROVED";
            hlsCusPrjProjectSupplement.setSuppleStatus(flag);
            hlsCusPrjProjectSupplement.setApproveDate(new Date());
            hlsCusPrjProjectSupplementMapper.updateByPrimaryKeySelective(hlsCusPrjProjectSupplement);


        }else if("APPROVED_RETURN".equalsIgnoreCase(result)){
            flag = "APPROVED_RETURN";
            hlsCusPrjProjectSupplement.setSuppleStatus(flag);
            hlsCusPrjProjectSupplement.setApproveDate(new Date());
            hlsCusPrjProjectSupplementMapper.updateByPrimaryKeySelective(hlsCusPrjProjectSupplement);


        }
        else if("REJECTED".equalsIgnoreCase(result)){
            flag = "REJECT";
            hlsCusPrjProjectSupplement.setSuppleStatus(flag);
            hlsCusPrjProjectSupplement.setApproveDate(new Date());
            hlsCusPrjProjectSupplementMapper.updateByPrimaryKeySelective(hlsCusPrjProjectSupplement);


        }else if("SUSPEND".equalsIgnoreCase(result)){
            flag = "SUSPEND";
            hlsCusPrjProjectSupplement.setSuppleStatus(flag);
            hlsCusPrjProjectSupplement.setApproveDate(new Date());
            hlsCusPrjProjectSupplementMapper.updateByPrimaryKeySelective(hlsCusPrjProjectSupplement);


        }


    }

}
