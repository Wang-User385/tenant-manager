package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.dto.HlsCusBpMasterRelation;
import com.hand.hls.bp.mapper.HlsCusBpMasterRelationMapper;
import com.hand.hls.bp.service.HlsCusBpMasterService;
import com.hand.hls.prj.dto.BpMasterChangeReq;
import com.hand.hls.prj.dto.HlsBpMaster;
import com.hand.hls.prj.service.HlsBpMasterService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BpServiceTask implements JavaDelegate, IActivitiBean {

    private static final String APPROVED = "APPROVED";
    private static final String YES_ENABLE_FLAG = "Y";
    private static final String REJECTED = "REJECTED";
    private static final String APPROVED_RETURN = "APPROVED_RETURN";
    private static final String FAILURE = "FAILURE";
    public static final String DOCUMENT_CATEGORY_BP_CHANGE = "HLS_BP_MASTER_CHANGE";
    public static final String BP_ADMIT_WORK_FLOW = "BP_ADMIT_WORK_FLOW";
    public static final String BP_DISTRU_ADMIT_WORK_FLOW = "BP_DISTRU_ADMIT_WORK_FLOW";

    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private HlsCusBpMasterService bpCusMasterService;
//    @Autowired
//    private HlsBpMasterService bpMasterService;//    @Autowired
//    private IBpMasterChangeReqService bpMasterChangeReqService;
    @Autowired
    private HlsCusBpMasterRelationMapper bpCusMasterRelationMapper;
//    @Autowired
//    private LegalIndividualLaunchService legalIndividualLaunchService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final String NO_FLAG = "N";

    /**
     * 主机厂/经销商审批流执行过程
     *
     * @param delegateExecution
     */
    public void execute(DelegateExecution delegateExecution) {
        String flag = "";
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
          String result = (String) delegateExecution.getVariable("approveResult");
        Long bpId = (Long) delegateExecution.getVariable("bpId");
        HlsCusBpMaster hlsBpMaster=new HlsCusBpMaster();
        hlsBpMaster.setBpId(bpId);
        hlsBpMaster=bpCusMasterService.selectByPrimaryKey(requestCtx,hlsBpMaster);
        if (APPROVED.equalsIgnoreCase(result)) {
            flag = "APPROVED";
        } else if (REJECTED.equalsIgnoreCase(result)) {
            flag = "REJECTED";
        }
        hlsBpMaster.setBpApproveStatus(flag);
        bpCusMasterService.updateByPrimaryKeySelective(requestCtx,hlsBpMaster);

//        if (StringUtils.equals(delegateExecution.getVariable("documentCategory").toString(), DOCUMENT_CATEGORY_BP_CHANGE)) {
//            String flag;
//            IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
//            String result = (String) delegateExecution.getVariable("approveResult");
//            String bpChangeReq = (String) delegateExecution.getVariable("bpChangeReq");
//            String BpMaster = (String) delegateExecution.getVariable("hlsBpMaster");
//            BpMasterChangeReq changeReq = JSON.parseObject(bpChangeReq, BpMasterChangeReq.class);
//            HlsCusBpMaster hlsBpMaster = JSON.parseObject(BpMaster, HlsCusBpMaster.class);
//
//            // lock con_contract
//            databaseLockProvider.lock(hlsBpMaster);
//            if ("APPROVED".equalsIgnoreCase(result)) {
//                flag = "APPROVED";
//                try {
//                   // bpMasterChangeReqService.leaveHistory(requestCtx, changeReq, Long.parseLong(delegateExecution.getProcessInstanceId()));
//                    //更新SAP接口传输标志
//                    hlsBpMaster = bpCusMasterService.selectByPrimaryKey(requestCtx, hlsBpMaster);
//                    hlsBpMaster.setSapSendVenderFlag("");
//                    hlsBpMaster.setSapSendBpFlag("");
//                    bpCusMasterService.updateByPrimaryKey(requestCtx, hlsBpMaster);
//                } catch (Exception e) {
//                    logger.error("leave history error:", e);
//                }
//            } else if ("CANCEL".equalsIgnoreCase(result)) {
//                flag = "CANCEL";
//            } else {
//                flag = "REJECTED";
//            }
//
//            BpMasterChangeReq bpMasterChangeReq = new BpMasterChangeReq();
//            bpMasterChangeReq.setChangeReqId(changeReq.getChangeReqId());
//            bpMasterChangeReq.setProcessInstanceId(Long.parseLong(delegateExecution.getProcessInstanceId()));
//            bpMasterChangeReq.setChangeReqDate(new Date());
//            bpMasterChangeReq.setStatus(flag);
//          //  bpMasterChangeReqService.updateByPrimaryKeySelective(requestCtx, bpMasterChangeReq);
//        } else {
//            String str = (String) delegateExecution.getVariable("bpMaster");
//            HlsCusBpMaster hlsBpMaster = JSON.parseObject(str, HlsCusBpMaster.class);
//            IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
//            String result = (String) delegateExecution.getVariable("approveResult");
//            String userId = String.valueOf(delegateExecution.getVariable("startUserId"));
//            Long bpRelationId = (Long) delegateExecution.getVariable("bpRelationId");
//            String documentCategory = delegateExecution.getVariable("documentCategory").toString();
//            String workFlowType = delegateExecution.getVariable("workFlowType").toString();
//            HlsCusBpMasterRelation bpMasterRelation = bpCusMasterRelationMapper.queryRelationByBpRelationId(bpRelationId).get(0);
//            requestCtx.setUserId(Long.valueOf(userId));
//            if (StringUtils.equalsIgnoreCase(result, APPROVED)) {
//                hlsBpMaster.setBpApproveStatus(APPROVED);
//                hlsBpMaster.setEnabledFlag(YES_ENABLE_FLAG);
//                bpMasterRelation.setAdmitStatus(APPROVED);
//                bpMasterRelation.setEnabledFlag(YES_ENABLE_FLAG);
//                bpCusMasterRelationMapper.updateByPrimaryKeySelective(bpMasterRelation);
//
//                //新的一条审批通过则当前相同‘关系类型-相关伙伴’下准入状态为‘审批通过’的记录更新为‘失效’状态，且不启用
//                Example example = new Example(HlsCusBpMasterRelation.class);
//                example.createCriteria().andEqualTo("bpId",bpMasterRelation.getBpId()).andEqualTo("relatedBpId",bpMasterRelation.getRelatedBpId())
//                        .andEqualTo("relationType",bpMasterRelation.getRelationType()).andEqualTo("enabledFlag","Y");
//                List<HlsCusBpMasterRelation> unuserRelationList = bpCusMasterRelationMapper.selectByExample(example);
//                if (unuserRelationList.size() > 1) {
//                    unuserRelationList = unuserRelationList.stream().filter(o -> bpRelationId.compareTo(o.getBpRelationId()) != 0).collect(Collectors.toList());
//                    for (HlsCusBpMasterRelation unRelation : unuserRelationList) {
//                        unRelation.setAdmitStatus(FAILURE);
//                        unRelation.setEnabledFlag(NO_FLAG);
//                        bpCusMasterRelationMapper.updateByPrimaryKeySelective(unRelation);
//                    }
//                }
////                if (BP_ADMIT_WORK_FLOW.equals(workFlowType)) {
////                    legalIndividualLaunchService.queryBpResult(bpRelationId, "1", BP_ADMIT_WORK_FLOW, null, null);
////                } else {
////                    legalIndividualLaunchService.queryBpResult(bpRelationId, "1", BP_DISTRU_ADMIT_WORK_FLOW, null, null);
////                }
//            } else if (StringUtils.equalsIgnoreCase(result, REJECTED)) {
//                bpMasterRelation.setAdmitStatus(REJECTED);
//                bpCusMasterRelationMapper.updateByPrimaryKeySelective(bpMasterRelation);
//                HlsCusBpMaster master = new HlsCusBpMaster();
//                master = bpCusMasterService.selectByPrimaryKey(null,hlsBpMaster);
//                //主机厂/经销商/分销商准入必须选一条伙伴关系提交，流程的同意拒绝撤回等操作会改变伙伴关系的准入状态。BpApproveStatus第一次变为审批通过后，后续的流程操作将不再改变
//                if (!"APPROVED".equals(master.getBpApproveStatus())) {
//                    hlsBpMaster.setBpApproveStatus(REJECTED);
//                }
////                String rejectMsg = String.valueOf(redisTemplate.opsForValue().get(delegateExecution.getProcessInstanceId() + "rejectMsg"));
////                String codeValue = taskNewMapper.queryCodeValue(rejectMsg);
////
////                String nodeName = taskNewMapper.queryNodeName(delegateExecution.getProcessInstanceId());
////                if (!"业务部负责人审核".equals(nodeName) && BP_ADMIT_WORK_FLOW.equals(workFlowType)) {
////                    legalIndividualLaunchService.queryBpResult(bpRelationId, "2", BP_ADMIT_WORK_FLOW, codeValue, rejectMsg);
////                } else if (BP_DISTRU_ADMIT_WORK_FLOW.equals(workFlowType)) {
////                    legalIndividualLaunchService.queryBpResult(bpRelationId, "2", BP_DISTRU_ADMIT_WORK_FLOW, codeValue, rejectMsg);
////                }
//            }
//            this.bpCusMasterService.updateByPrimaryKeySelective(requestCtx, hlsBpMaster);
//        }


    }
}
