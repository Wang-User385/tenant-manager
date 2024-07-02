package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceBpService;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceService;
import com.hand.hls.prj.mapper.HlsCusPrjProjectBpMapper;
import com.hand.hls.prj.service.HlsCreditPlanService;
import com.hand.hls.prj.service.HlsCusPrjProjectBpService;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.req.dto.HlsCusChangeReqInfo;
import com.hand.hls.req.service.HlsCusChangeReqInfoService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Created by wangyan on 2017/11/13.
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjChanceChangeSubmitServiceTask implements JavaDelegate, IActivitiBean {

    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;
    @Autowired
    private HlsCusChangeReqInfoService hlsCusChangeReqInfoService;
    @Autowired
    private HlsCusHlsCreditLineChanceService chanceService;
    @Autowired
    HlsCreditPlanService hlsCreditPlanService;
    @Autowired
    HlsCusHlsCreditLineChanceBpService hlsCusHlsCreditLineChanceBpService;
    @Autowired
    private HlsCusPrjProjectBpService hlsCusPrjProjectBpService;
    @Autowired
    HlsCusPrjProjectBpMapper hlsCusPrjProjectBpMapper;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public HlsCusPrjChanceChangeSubmitServiceTask() {
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String employeeCode = (String) delegateExecution.getVariable("startUserName");
        String processDefinitionId =  delegateExecution.getProcessDefinitionId().split(":")[0];
        String hlsCusPrjProjectPrams = (String) delegateExecution.getVariable("hlsCusPrjProjectOld");
        HlsCusHlsCreditLineChance hlsCusPrjChanceOld = JSON.parseObject(hlsCusPrjProjectPrams, HlsCusHlsCreditLineChance.class);
        HlsCusHlsCreditLineChance resultHlsCusPrjChanceOld = new HlsCusHlsCreditLineChance();

        //解决跳转时结束监听器多次被调用问题
        if(((ExecutionEntityImpl) delegateExecution).getStartTime()==null){
            return;
        }

        String hlsCusPrjProjectPramsNew = (String) delegateExecution.getVariable("hlsCusPrjProjectNew");
        HlsCusHlsCreditLineChance hlsCusPrjProjectNew = JSON.parseObject(hlsCusPrjProjectPramsNew, HlsCusHlsCreditLineChance.class);
        HlsCusHlsCreditLineChance resultHlsCusPrjProjectNew = new HlsCusHlsCreditLineChance();

        //根据状态修改项目信息
        resultHlsCusPrjChanceOld.setChanceId(hlsCusPrjChanceOld.getChanceId());
        resultHlsCusPrjChanceOld = chanceService.selectByPrimaryKey(requestCtx, resultHlsCusPrjChanceOld);

        resultHlsCusPrjProjectNew.setChanceId(hlsCusPrjProjectNew.getChanceId());
        resultHlsCusPrjProjectNew = chanceService.selectByPrimaryKey(requestCtx, resultHlsCusPrjProjectNew);

        Long chanceIdOld = hlsCusPrjChanceOld.getChanceId();
        Long chanceIdNew = hlsCusPrjProjectNew.getChanceId();


        /*更新审批信息表*/
        HlsCusChangeReqInfo hlsCusChangeReqInfo = new HlsCusChangeReqInfo();
        hlsCusChangeReqInfo.setChangeReqId(resultHlsCusPrjProjectNew.getChangeReqId());
        hlsCusChangeReqInfo = hlsCusChangeReqInfoService.selectByPrimaryKey(requestCtx, hlsCusChangeReqInfo);

        Boolean swapParojectFlag = false;

        if("APPROVED".equals(result)){
            swapParojectFlag = true;
            hlsCusChangeReqInfo.setStatus("APPROVED");
        }else if("REJECTED".equals(result)||"APPROVED_RETURN".equals(result)){
            //更新项目状态
            hlsCusPrjChanceOld.setCreditLineStatus("APPROVED");
            chanceService.updateByPrimaryKeySelective(requestCtx,hlsCusPrjChanceOld);

            resultHlsCusPrjProjectNew.setDataType("HISTORY");
            chanceService.updateByPrimaryKeySelective(requestCtx,resultHlsCusPrjProjectNew);

            hlsCusChangeReqInfo.setStatus(result);
        }

        hlsCusChangeReqInfo.setInstanceEndFlag("Y");


        if(swapParojectFlag){
            /*备份history*/
           /* HlsCusHlsCreditLineChance prjChance = new HlsCusHlsCreditLineChance();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(resultHlsCusPrjChanceOld);
            hlsBeanRefUtilService.setFieldValue(prjChance, map);
            prjChance.setChanceId(null);
            prjChance.setDataType("HISTORY");
            prjChance.setRefChanceId(chanceIdOld);
            prjChance.setChangeReqId(resultHlsCusPrjProjectNew.getChangeReqId());
            prjChance = chanceService.insertSelective(requestCtx, prjChance);*/

            /*备份从表history数据*/
           /* try {
                chanceService.chanceBackUp(requestCtx, prjChance);
            } catch (HlsCusException e) {
                logger.info("项目变更备份数据失败!");
                logger.info(e.getMessage());
            }*/

            /*删除旧数据，将新数据回写*/
           /* requestCtx.setAttribute("processDefinitionId", processDefinitionId);

            chanceService.deleteOld(chanceIdOld, requestCtx);
            try {
                chanceService.updateOld(requestCtx,chanceIdOld, chanceIdNew, requestCtx);
            } catch (HlsCusException e) {
                logger.info("项目变更更新数据失败!");
                logger.info(e.getMessage());
            }*/

            /*更新normal*/
           /* HlsCusHlsCreditLineChance prjchanceNew = new HlsCusHlsCreditLineChance();
            Map<String, String> map1 = hlsBeanRefUtilService.getFieldValueMap(resultHlsCusPrjProjectNew);
            hlsBeanRefUtilService.setFieldValue(prjchanceNew, map1);
            prjchanceNew.setCreditLineStatus("APPROVED");
            //prjchanceNew.setDataClass("NORMAL");
            prjchanceNew.setDataType("NORMAL");
            prjchanceNew.setRefChanceId(null);
            prjchanceNew.setChangeReqId(null);*/

            //更新原始项目
            /*prjchanceNew.setChanceId(chanceIdOld);

            chanceService.updateByPrimaryKeySelective(requestCtx, prjchanceNew);*/


            //删除原单据
            chanceService.deleteOld(chanceIdOld, requestCtx);

            //复制变更单据, 使用原始单据id
            HlsCusHlsCreditLineChance prjChance = new HlsCusHlsCreditLineChance();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(resultHlsCusPrjProjectNew);
            hlsBeanRefUtilService.setFieldValue(prjChance, map);
            prjChance.setChanceId(chanceIdOld);
            prjChance.setDataType("NORMAL");
            prjChance.setRefChanceId(null);
            prjChance.setChangeReqId(null);
            prjChance.setCreditLineStatus("APPROVED");
            prjChance.setChangeReqId(resultHlsCusPrjProjectNew.getChangeReqId());
            //prjChance = chanceService.insertSelective(requestCtx, prjChance);
            chanceService.updateByPrimaryKeySelective(requestCtx, prjChance);

            //从表复制
            try {
                //设置RefChanceId去复制变更单据的从表
                prjChance.setRefChanceId(resultHlsCusPrjProjectNew.getChanceId());
                chanceService.chanceBackUp(requestCtx, prjChance);
            } catch (HlsCusException e) {
                logger.info("项目变更更新数据失败!");
                logger.info(e.getMessage());
            }

            //变更历史单据状态从CHANGE_REQ_HISTORY改为HISTORY
            HlsCusHlsCreditLineChance prjChanceH = new HlsCusHlsCreditLineChance();
            prjChanceH.setChangeReqId(hlsCusChangeReqInfo.getChangeReqId());
            prjChanceH.setDataType("CHANGE_REQ_HISTORY");
            prjChanceH = chanceService.select(requestCtx, prjChanceH, 1, 999).get(0);
            prjChanceH.setDataType("HISTORY");
            chanceService.updateByPrimaryKeySelective(requestCtx, prjChanceH);


            // 同步更新尽调的授信方案与客户信息 如果是授信项目并且是授信变更,把授信方案与客户信息同步到立项
           /* if("Y".equals(resultHlsCusPrjProjectNew.getCreditFlag()) && !"CHANCE_ALL_CHANGE".equals(hlsCusChangeReqInfo.getChangeType())){
                Long chanceId = chanceIdOld;
                //获取尽调
                HlsCusPrjProject prjProject = new HlsCusPrjProject();
                prjProject.setChanceId(chanceId);
                prjProject = hlsCusPrjProjectService.select(requestCtx, prjProject, 1, 999).get(0);
                Long projectId = prjProject.getProjectId();

                //获取授信立项的授信方案
                HlsCreditPlan hcpChance = new HlsCreditPlan();
                hcpChance.setSourceDocumentId(chanceId);
                hcpChance.setSourceDocumentCategory("HLS_CREDIT_LINE_CHANCE");
                List<HlsCreditPlan> hcpChanceSelect = hlsCreditPlanService.select(requestCtx, hcpChance, 1, 999);
                hcpChance = hcpChanceSelect.get(0);
                //同步到尽调项授信方案(update)
                HlsCreditPlan hcpPrj = new HlsCreditPlan();
                hcpPrj.setSourceDocumentId(projectId);
                hcpPrj.setSourceDocumentCategory("PRJ_PROJECT");
                List<HlsCreditPlan> hcpPrjSelect = hlsCreditPlanService.select(requestCtx, hcpPrj, 1, 999);
                hcpPrj = hcpPrjSelect.get(0);
                hcpChance.setCreditPlanId(hcpPrj.getCreditPlanId());
                hcpChance.setSourceDocumentId(projectId);
                hcpChance.setSourceDocumentCategory("PRJ_PROJECT");
                hlsCreditPlanService.updateByPrimaryKeySelective(requestCtx, hcpChance);


                //获取立项客户信息
                HlsCusHlsCreditLineChanceBp hclcb = new HlsCusHlsCreditLineChanceBp();
                hclcb.setChanceId(chanceId);
                List<HlsCusHlsCreditLineChanceBp> hclcbSelect = hlsCusHlsCreditLineChanceBpService.select(requestCtx, hclcb, 1, 999);
                //同步到尽调客户信息(删除后新增)
                HlsCusPrjProjectBp hcppb = new HlsCusPrjProjectBp();
                hcppb.setProjectId(projectId);
                hlsCusPrjProjectBpMapper.delete(hcppb);
                for (HlsCusHlsCreditLineChanceBp hlsCusHlsCreditLineChanceBp : hclcbSelect) {
                    HlsCusPrjProjectBp record = new HlsCusPrjProjectBp();
                    Map<String, String> map2 = hlsBeanRefUtilService.getFieldValueMap(hlsCusHlsCreditLineChanceBp);
                    hlsBeanRefUtilService.setFieldValue(record , map2);
                    record.setProjectId(projectId);
                    record.setChanceId(chanceId);
                    hlsCusPrjProjectBpService.insertSelective(requestCtx, record);
                }
            }*/

        }

        hlsCusChangeReqInfoService.updateByPrimaryKey(requestCtx, hlsCusChangeReqInfo);

    }

}
