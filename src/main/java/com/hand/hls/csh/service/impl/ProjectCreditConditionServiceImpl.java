package com.hand.hls.csh.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.csh.dto.ProjectCreditCondition;
import com.hand.hls.csh.mapper.ProjectCreditConditionMapper;
import com.hand.hls.csh.service.IProjectCreditConditionService;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.sys.service.SysUserAllocationService;
import com.hand.hls.wfl.service.IActivitiCommonService;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.utils.exception.HlsCusException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class ProjectCreditConditionServiceImpl extends BaseServiceImpl<ProjectCreditCondition> implements IProjectCreditConditionService {

    private static final String APPROVING = "APPROVING";
    private static final String WORK_FLOW_TYPE = "CSH_PAYMENT_CONDITION_WFL";

    @Autowired
    private IProjectCreditConditionService service;
    @Autowired
    private ProjectCreditConditionMapper mapper;
    @Autowired
    private IActivitiStartService activitiStartService;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;
    @Autowired
    private SysUserAllocationService sysUserAllocationService;

    private static final String BLACK_WORD = "、";
    private static final String BRACKETS = ")";
    private static final String NO_CONTENT = "\"无\"";
    private static final String FROM_WORD = "";
    private static final String TO_WORD = "变更为";
    private static final String ENTER = "\n";
    private static final String FRONT_STR = "\"";
    private static final String NO_CHANGE = "未变更";

    public void submitWfl(HttpSession session, IRequest iRequest, List<String> creditConditionIds) throws HlsCusException{
        Map params = new HashMap();
        String code;
        String documentCategory = "CON_PAY_CONDITION";
        String documentType = "CON_PAY_CONDITION";
        String businessType = "CON_PAY_CONDITION";
        synchronized (this) {
            code = fndCodingRuleValuesService.getCodeRuleValue(iRequest, documentCategory, documentType, businessType, params);
        }
        if (StringUtils.isEmpty(code)) {
            throw new HlsCusException("请求发生错误!");
        }

        List<ProjectCreditCondition> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        creditConditionIds.forEach(item -> {
            ProjectCreditCondition projectCreditCondition = mapper.selectByPrimaryKey(item);
            list.add(projectCreditCondition);
        });
        Long projectId = list.get(0).getProjectId();
        HlsCusPrjProject project = new HlsCusPrjProject();
        project.setProjectId(projectId);
        HlsCusPrjProject p = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, project);

        HlsCusPrjProject refHlsCusPrjProject = new HlsCusPrjProject();
        refHlsCusPrjProject.setProjectId(p.getRefProjectId());
        refHlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, refHlsCusPrjProject);

        String reAppointFlag = "N";
        Long allocationIdRisk = refHlsCusPrjProject.getAllocationIdRisk();
        if(allocationIdRisk != null){
            String allocationIdRiskName = sysUserAllocationService.selectUserNameByAllocationId(allocationIdRisk.toString());
            if(allocationIdRiskName == null || allocationIdRiskName == ""){
                reAppointFlag = "Y";
            }
        }else{
            reAppointFlag = "Y";
        }


        String idList = String.join(",", creditConditionIds);

        String unitIdStr = String.valueOf(session.getAttribute("unitId"));
        Long unitId = Long.valueOf(unitIdStr);
        String companyIdStr = String.valueOf(session.getAttribute("companyId"));
        Long companyId = Long.valueOf(companyIdStr);

        map.put("creditConditionIds", idList);
        map.put("documentCategory", WORK_FLOW_TYPE);
        map.put("projectId", projectId);
        map.put(IActivitiCommonService.WORK_FLOW_NAME, "CSH_PAYMENT_CONDITION_WFL");
        map.put(IActivitiCommonService.DEMO_NAME, "CSH_PAYMENT_CONDITION_WFL");
        map.put(IActivitiCommonService.BUSINESS_KEY, projectId);
        map.put("documentType", WORK_FLOW_TYPE);
        map.put("creditConditionNumber", code);
        map.put("workFlowType", WORK_FLOW_TYPE);
        map.put("documentName", p.getProjectName());
        map.put("unitId", unitId);
        map.put("companyId", companyId);
        map.put("documentNumber", code);
        map.put("allocationIdRisk", allocationIdRisk);
        map.put("reAppointFlag", reAppointFlag);
        activitiStartService.start(iRequest, list, map);

        creditConditionIds.forEach(item -> {
            ProjectCreditCondition projectCreditCondition = mapper.selectByPrimaryKey(item);
            projectCreditCondition.setApprovalFlag(APPROVING);
            projectCreditCondition.setCreditConditionNumber(code);
            service.updateByPrimaryKeySelective(iRequest, projectCreditCondition);
        });
    }

    @Override
    public String getProjectHistoryInfo(IRequest iRequest, Long currentProjectId,Long historyProjectId) {
        /*
        * 分为四个部分，
        * 第一个当前项目中没有，历史数据有的是数据，删除的数据
        * 第二个未变化数据
        * 第三个当前项目中有，历史数据有的数据，数据差异对比
        * 第四个当前项目中有，历史数据没有的数据，即新增的数据
        * */

        StringBuilder content = new StringBuilder();
        Long seq = 1L;
        ProjectCreditCondition condition = new ProjectCreditCondition();
        condition.setProjectId(currentProjectId);
        condition.setHisProjectId(historyProjectId);

        //删除
        List<ProjectCreditCondition> removeConditionList = mapper.selectChangeRemoveInfo(condition);
        for(int i = 0; i < removeConditionList.size();i++){
            content.append(seq.toString());
            content.append(BRACKETS);
//            content.append(BLACK_WORD);
            content.append(FROM_WORD);

            content.append(FRONT_STR);
            content.append(removeConditionList.get(i).getConditions());
            content.append(FRONT_STR);

            content.append(TO_WORD);
            content.append(NO_CONTENT);
            content.append(ENTER);
            seq++;
        }

        //未变化的数据
        List<ProjectCreditCondition> conditionList = mapper.selectNotChangeInfo(condition);
        for(int i = 0; i < conditionList.size();i++){
            content.append(seq.toString());
            content.append(BRACKETS);
//            content.append(BLACK_WORD);

            content.append(FRONT_STR);
            content.append(conditionList.get(i).getConditions());
            content.append(FRONT_STR);
            content.append(NO_CHANGE);

            content.append(ENTER);
            seq++;
        }

        //差异
        List<ProjectCreditCondition> diffConditionList = mapper.selectChangeDiffInfo(condition);
        for(int i = 0; i < diffConditionList.size();i++){
            content.append(seq.toString());
            content.append(BRACKETS);
//            content.append(BLACK_WORD);
            content.append(FROM_WORD);

            content.append(FRONT_STR);
            content.append(diffConditionList.get(i).getHisCondition());
            content.append(FRONT_STR);

            content.append(TO_WORD);

            content.append(FRONT_STR);
            content.append(diffConditionList.get(i).getConditions());
            content.append(FRONT_STR);

            content.append(ENTER);
            seq++;
        }

        //新增数据
        List<ProjectCreditCondition> addConditionList = mapper.selectChangeAddInfo(condition);
        for(int i = 0; i < addConditionList.size();i++){
            content.append(seq.toString());
            content.append(BRACKETS);
//            content.append(BLACK_WORD);
            content.append(FROM_WORD);
            content.append(NO_CONTENT);
            content.append(TO_WORD);

            content.append(FRONT_STR);
            content.append(addConditionList.get(i).getConditions());
            content.append(FRONT_STR);

            content.append(ENTER);
            seq++;
        }



        return content.toString();
    }

}