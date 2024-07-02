package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.dto.HlsCusBpMasterRelation;
import com.hand.hls.bp.mapper.HlsCusBpMasterRelationMapper;
import com.hand.hls.bp.service.impl.HlsCusBpMasterServiceImpl;
import com.hand.hls.prj.dto.BpMasterChangeReq;
import com.hand.hls.wfl.components.WflGetProcessInstanceComponents;
import com.hand.hls.wfl.service.IActivitiCommonService;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class HlsBpMasterActivitiStartServiceImpl implements IActivitiCommonService {

    private static final String WORK_FLOW_TYPE = "BP_ADMIT_WORK_FLOW";
    private static final String DEMO = "DEMO";
    private static final String BUSINESS_KEY = "BUSINESS_KEY";
    private static final String WORK_FLOW = "WORK_FLOW";
    private static final String BP = "BP";
    public static final String DOCUMENT_CATEGORY_BP_CHANGE = "HLS_BP_MASTER_CHANGE";
    private static final String CANCEL = "CANCEL";

    @Autowired
    private IActivitiService activitiService;
    @Autowired
    WflGetProcessInstanceComponents wflGetProcessInstanceComponents;
    @Autowired
    HlsCusBpMasterServiceImpl bpCusMasterService;
//    @Autowired
//    IBpMasterChangeReqService bpMasterChangeReqService;
    @Autowired
    private HlsCusBpMasterRelationMapper bpCusMasterRelationMapper;
//    @Autowired
//    private LegalIndividualLaunchService legalIndividualLaunchService;

    public HlsBpMasterActivitiStartServiceImpl() {

    }

    public String getWorkFlowType() {
        return WORK_FLOW_TYPE;
    }

    //主机厂/经销商工作流启动
    public void process(IRequest iRequest, List list, Map params) {
//        if (StringUtils.equals(params.get("documentCategory").toString(), DOCUMENT_CATEGORY_BP_CHANGE)) {
//            params.put(BUSINESS_KEY, Long.parseLong(params.get(IActivitiCommonService.BUSINESS_KEY).toString()));
//        } else {
//            params.put(BUSINESS_KEY, ((HlsCusBpMasterRelation) list.get(0)).getBpId());
//        }
//        params.put(WORK_FLOW, WORK_FLOW_TYPE);
//        params.put(DEMO, BP);
        ProcessInstanceCreateRequest processInstanceCreateRequest = this.wflGetProcessInstanceComponents.getProcessInstance(iRequest, params);
        this.activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }

    //主机厂/经销商工作流
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        long id = Long.parseLong(businessKey);
        if (StringUtils.equals(params.get("documentCategory").toString(), DOCUMENT_CATEGORY_BP_CHANGE)) {
            BpMasterChangeReq bpMasterChangeReq = new BpMasterChangeReq();
            bpMasterChangeReq.setChangeReqId(id);
            bpMasterChangeReq.setStatus(CANCEL);
            //this.bpMasterChangeReqService.updateByPrimaryKeySelective(iRequest, bpMasterChangeReq);
        } else {
            Long bpRelationId = Long.valueOf(String.valueOf(params.get("bpRelationId")));
            HlsCusBpMaster bpMaster = new HlsCusBpMaster();
            bpMaster.setBpId(id);
            bpMaster = bpCusMasterService.selectByPrimaryKey(iRequest, bpMaster);
            //主机厂/经销商/分销商准入必须选一条伙伴关系提交，流程的同意拒绝撤回等操作会改变伙伴关系的准入状态。BpApproveStatus第一次变为审批通过后，后续的流程操作将不再改变
            if (!"APPROVED".equals(bpMaster.getBpApproveStatus())) {
                bpMaster.setBpApproveStatus(CANCEL);
                this.bpCusMasterService.updateByPrimaryKeySelective(iRequest, bpMaster);
            }
            HlsCusBpMasterRelation bpMasterRelation = new HlsCusBpMasterRelation();
            bpMasterRelation.setBpRelationId(bpRelationId);
            bpMasterRelation.setAdmitStatus(CANCEL);
            bpCusMasterRelationMapper.updateByPrimaryKeySelective(bpMasterRelation);
            //legalIndividualLaunchService.queryBpResult(bpRelationId, "3", WORK_FLOW_TYPE, null, null);
        }


    }

}
