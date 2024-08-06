package com.hand.hls.wfl.service.impl;

import com.github.pagehelper.StringUtil;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hls.fnd.dto.HlsPosition;
import com.hand.hls.fnd.mapper.HlsPositionMapper;
import com.hand.hls.prj.dto.HlsBpMaster;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsBpMasterMapper;
import com.hand.hls.prj.service.IPrjProjectService;
import com.hand.hls.wfl.components.WflGetProcessInstanceComponents;
import com.hand.hls.wfl.service.IActivitiCommonService;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceResponse;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PrjProjectFormalActivitiStartServiceImpl implements IActivitiCommonService {
    //获取实现类
    private static final String WORK_FLOW_TYPE = "PRJ";

    //对应工作流页面配置的唯一标志
    private static final String WORK_FLOW_KEY = "FORMAL_APPROVAL_WORK_FLOW";
    //对应工作流页面配置的分类
    private static final String DEMO = "PRJ";

    private static final String BUSINESS_KEY = "BUSINESS_KEY";

    private static final String POSITION_CODE = "positionCode";

    private static final String VENDER_WFL_FLAG = "venderWflFlag";

    private static final String N = "N";

    private static final String business_key = "businessKey";

    @Autowired
    private IActivitiService activitiService;
    @Autowired
    WflGetProcessInstanceComponents wflGetProcessInstanceComponents;

    @Autowired
    IPrjProjectService prjProjectService;
    @Autowired
    HlsPositionMapper hlsPositionMapper;
    @Autowired
    HlsBpMasterMapper hlsBpMasterMapper;

    @Override
    public String getWorkFlowType() {
        return WORK_FLOW_TYPE;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        //设置主键
        params.put(BUSINESS_KEY, ((HlsCusPrjProject) list.get(0)).getProjectId());
        params.put(WORK_FLOW_NAME, WORK_FLOW_KEY);
        params.put(DEMO_NAME, DEMO);
        //判断提交人是否需要主机厂审批
        HlsPosition fndOrgPosition = new HlsPosition();
        fndOrgPosition.setPositionCode(iRequest.getAttribute(POSITION_CODE));
        List<HlsPosition> positionList = hlsPositionMapper.select(fndOrgPosition);
        if(CollectionUtils.isNotEmpty(positionList) && positionList.size() == 1){
//            String venderWflFlag = positionList.get(0).getVenderWflFlag();
            String venderWflFlag="";
            if(StringUtil.isEmpty(venderWflFlag)){
                venderWflFlag = N;
            }
            params.put(VENDER_WFL_FLAG,venderWflFlag);
        }

        // 进件的所属厂商为山推工程机械股份有限公司，经销商提交进件审批后，系统自动审批通过，流程结束
        HlsBpMaster hlsBpMaster = hlsBpMasterMapper.queryByBpId(((HlsCusPrjProject) list.get(0)).getManufacturerId());
        if ("C000010315".equals(hlsBpMaster.getBpCode())) {
            params.put(VENDER_WFL_FLAG, "N");
        }

        ProcessInstanceCreateRequest processInstanceCreateRequest = wflGetProcessInstanceComponents.getProcessInstance(iRequest, params);

        ProcessInstanceResponse processInstanceResponse = activitiService.startProcess(iRequest, processInstanceCreateRequest);
        // 回写工作流实例ID到项目prj_project表
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setProcessInstanceId(Long.valueOf(processInstanceResponse.getId()));
        prjProject.setProjectId(((HlsCusPrjProject) list.get(0)).getProjectId());
        prjProjectService.updateByPrimaryKeySelective(iRequest,prjProject);
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get(business_key);
        long id = Long.parseLong(businessKey);
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setProjectId(id);
        prjProject.setProjectStatus(CANCEL_STATUS);
        prjProjectService.updateByPrimaryKeySelective(iRequest, prjProject);
    }
}
