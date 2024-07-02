//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.ast.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hap.mybatis.entity.Example.Criteria;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.ast.dto.AstFcEstimate;
import com.hand.hls.ast.dto.AstFiveClassPlan;
import com.hand.hls.ast.mapper.AstFcEstimateMapper;
import com.hand.hls.ast.service.IAstFcEstimateService;
import com.hand.hls.ast.service.IAstFiveClassPlanService;
import com.hand.hls.utils.ResMessageException;
import com.hand.hls.wfl.service.IActivitiCommonService;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.utils.exception.HlsCusException;
import hls.layout.utils.ReplaceUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import leaf.bean.LeafRequestData;
import leaf.service.validation.ParameterNullException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uncertain.composite.CompositeMap;

@Service
@Transactional(
        rollbackFor = {Exception.class}
)
public class AstFcEstimateServiceImpl extends BaseServiceImpl<AstFcEstimate> implements IAstFcEstimateService {
    @Autowired
    private AstFcEstimateMapper astFcEstimateMapper;
    @Autowired
    private IActivitiStartService activitiStartService;
    @Autowired
    private IAstFiveClassPlanService astFiveClassPlanService;
    @Autowired
    private IAstFcEstimateService astFcEstimateService;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    private static final String APPROVED = "APPROVED";
    private static final String APPROVING = "APPROVING";
    public AstFcEstimateServiceImpl() {
    }

    public ResponseData queryByRequestData(IRequest requestContext, LeafRequestData requestData, int pagenum, int pagesize) {
        Object company_id = requestContext.getCompanyId();
        Map parameter = requestData.getParameter();
        Object status = parameter.get("status");
        Object estimate_num = parameter.get("estimate_num");
        Object internal_period_num = parameter.get("internal_period_num");
        Object fc_estimate_id = parameter.get("fc_estimate_id");
        PageHelper.startPage(pagenum, pagesize);
        Example example = new Example(AstFcEstimate.class);
        Criteria criteria = example.createCriteria().andEqualTo("companyId", company_id);
        if (status != null) {
            criteria.andEqualTo("status", status);
        }

        if (estimate_num != null) {
            criteria.andLike("estimateNum", "%" + estimate_num + "%");
        }

        if (internal_period_num != null) {
            criteria.andLike("internalPeriodNum", "%" + internal_period_num + "%");
        }

        if (fc_estimate_id != null) {
            criteria.andEqualTo("fcEstimateId", fc_estimate_id);
        }

        example.setOrderByClause("fc_estimate_id desc");
        List<AstFcEstimate> astFcEstimates = this.astFcEstimateMapper.selectByExample(example);
        return new ResponseData(astFcEstimates);
    }

    public ResponseData modelQuery(CompositeMap map, String whereSql) {
        CompositeMap param = new CompositeMap();
        Object parameter = map.get("parameter");
        if (parameter != null) {
            param = (CompositeMap)parameter;
        }

        CompositeMap parameter1 = (CompositeMap)map.get("parameter");
        String s = ReplaceUtils.repalceAll(whereSql, parameter1);
        List<CompositeMap> result = this.astFcEstimateMapper.modelQuery1(param, s);
        return new ResponseData(result);
    }

    public void fcEstimateApprove(IRequest iRequest, Long fcEstimateId) {
        AstFcEstimate astFcEstimate = new AstFcEstimate();
        astFcEstimate.setFcEstimateId(fcEstimateId);
        astFcEstimate = (AstFcEstimate)this.astFcEstimateMapper.selectByPrimaryKey(astFcEstimate);
        List<AstFcEstimate> list = new ArrayList();
        list.add(astFcEstimate);
        Map<String, Object> params = new HashMap();
        params.put("workFlowType", "FIVE_CLASS_ESITIMATE");
        params.put("DEMO", "CON");
        params.put("WORK_FLOW", "FIVE_CLASS_ESITIMATE_WORKFLOW");
        params.put("BUSINESS_KEY", astFcEstimate.getFcEstimateId());
        params.put("astFcEstimate", JSON.toJSONString(astFcEstimate));
        params.put("iRequest", iRequest);
        params.put("documentCategory", "AST_FIVE_CLASS");
        params.put("documentType", "");
        params.put("documentId", astFcEstimate.getFcEstimateId());
        AstFiveClassPlan astFiveClassPlan = new AstFiveClassPlan();
        astFiveClassPlan.setFiveClassPlan(astFcEstimate.getFiveClassPlan());
        List<AstFiveClassPlan> astFiveClassPlans = this.astFiveClassPlanService.selectSelective(iRequest, astFiveClassPlan);
        params.put("documentName", astFiveClassPlans == null ? "" : ((AstFiveClassPlan)astFiveClassPlans.get(0)).getDescription());
        params.put("documentNumber", astFcEstimate.getEstimateNum());
        params.put("businessKey", astFcEstimate.getFcEstimateId());
        params.put("estimateNum", astFcEstimate.getEstimateNum());
        params.put("fiveClassPlan", astFcEstimate.getFiveClassPlan());
        params.put("fcEstimateId", astFcEstimate.getFcEstimateId());
        params.put("List", list);
        this.activitiStartService.start(iRequest, list, params);
    }

    @Override
    public AstFcEstimate astFcEstimateCreate(IRequest requestCtx, AstFcEstimate dto) throws HlsCusException {
        AstFcEstimate astFcEstimate = new AstFcEstimate();
        //astFcEstimate.setCompanyId(248L);
        astFcEstimate.setCompanyId(dto.getCompanyId());
        astFcEstimate.setUnitId(dto.getUnitId());
        astFcEstimate.setFiveClassPlan(dto.getFiveClassPlan());
        astFcEstimate.setContractNumber(dto.getContractNumber());
        astFcEstimate.setEstimateNum(dto.getEstimateNum());
        astFcEstimate.setEstimateDate(dto.getEstimateDate());
        astFcEstimate.setEmployeeId(dto.getEmployeeId());
        astFcEstimate.setProjectId(dto.getProjectId());
        astFcEstimate.setRaiseStatus("NEW");
        astFcEstimate.setNowCountStatus("NEW");
        AstFcEstimate astFcEstimate1 = this.astFcEstimateService.insertSelective(requestCtx,astFcEstimate);



        return astFcEstimate1;
    }

    @Override
    public List<AstFcEstimate> queryAstFcEstimate(IRequest var1, AstFcEstimate var2, int var3, int var4) {
        return this.astFcEstimateMapper.queryAstFcEstimate(var2);
    }

    protected boolean useSelectiveUpdate() {
        return false;
    }

    private void approveWfl(IRequest iRequest, AstFcEstimate astFcEstimate) throws ResMessageException {
        astFcEstimate = this.astFcEstimateMapper.selectByPrimaryKey(astFcEstimate);
        if (APPROVED.equals(astFcEstimate.getRaiseStatus()) || APPROVING.equals(astFcEstimate.getRaiseStatus())) {
            throw new ResMessageException("当前单据状态不能提交申请！");
        }
        if (astFcEstimate.getStartScoreDesc() == null){
            throw new ResMessageException("定性指标值不能为空！");
        }
        databaseLockProvider.lock(astFcEstimate);

        List<AstFcEstimate> assetsDisposalArrayList = new ArrayList<>();
        assetsDisposalArrayList.add(astFcEstimate);

        Map<String, Object> params = new HashMap<>();
        params.put("workFlowType", "ASSETS_CLASSIFICATION_WFL");
        params.put("assetsDisposalId", astFcEstimate.getFcEstimateId());
        params.put(IActivitiCommonService.WORK_FLOW_NAME, "ASSETS_CLASSIFICATION_WFL");
        params.put(IActivitiCommonService.DEMO_NAME, "ASSETS_CLASSIFICATION_WFL");
        params.put(IActivitiCommonService.BUSINESS_KEY, astFcEstimate.getFcEstimateId());
        params.put("documentCategory", "ASSETS_CLASSIFICATION_WFL"); //
        params.put("documentName","资产分类评级审批流程"+astFcEstimate.getEstimateNum());
        params.put("documentNumber", astFcEstimate.getEstimateNum());
        params.put("astFcEstimate", JSON.toJSONString(astFcEstimate));
        params.put("startUserName", iRequest.getUserName());



        activitiStartService.start(iRequest, assetsDisposalArrayList, params);

        AstFcEstimate assetsDisposal1 = new AstFcEstimate();
        assetsDisposal1.setFcEstimateId(astFcEstimate.getFcEstimateId());
        assetsDisposal1.setEmployeeId(astFcEstimate.getEmployeeId());
        assetsDisposal1.setRaiseStatus(APPROVING);
        this.astFcEstimateService.updateByPrimaryKeySelective(iRequest, assetsDisposal1);

    }

    @Override
    public List<AstFcEstimate> conInceptSubmit(IRequest iRequest, AstFcEstimate astFcEstimate) throws ResMessageException, ParameterNullException {
        //启动工作流
        approveWfl(iRequest, astFcEstimate);

        List<AstFcEstimate> contractInsure = new ArrayList<>();
        contractInsure.add(astFcEstimate);
        return contractInsure;
    }


       /**
       * @author kalvin
       * @Description 
       * @Date 11:22 2021/4/30
       * @Param [iRequest, astFcEstimate]
       * @return void
       **/
    private void approveWfl2(IRequest iRequest, AstFcEstimate astFcEstimate) throws ResMessageException {
        astFcEstimate = this.astFcEstimateMapper.selectByPrimaryKey(astFcEstimate);
        if (APPROVED.equals(astFcEstimate.getNowCountStatus()) || APPROVING.equals(astFcEstimate.getNowCountStatus())) {
            throw new ResMessageException("当前单据状态不能提交申请！");
        }
        databaseLockProvider.lock(astFcEstimate);

        List<AstFcEstimate> assetsDisposalArrayList = new ArrayList<>();
        assetsDisposalArrayList.add(astFcEstimate);

        Map<String, Object> params = new HashMap<>();
        params.put("workFlowType", "PROVISION_SHALL_BE_MADE_WFL");
        params.put("assetsDisposalId", astFcEstimate.getFcEstimateId());
        params.put(IActivitiCommonService.WORK_FLOW_NAME, "PROVISION_SHALL_BE_MADE_WFL");
        params.put(IActivitiCommonService.DEMO_NAME, "PROVISION_SHALL_BE_MADE_WFL");
        params.put(IActivitiCommonService.BUSINESS_KEY, astFcEstimate.getFcEstimateId());
        params.put("documentCategory", "PROVISION_SHALL_BE_MADE_WFL"); //
        params.put("documentName","资产分类应计提拔备评估审批流程"+astFcEstimate.getEstimateNum());
        params.put("documentNumber", astFcEstimate.getEstimateNum());
        params.put("astFcEstimate", JSON.toJSONString(astFcEstimate));
        params.put("startUserName", iRequest.getUserName());



        activitiStartService.start(iRequest, assetsDisposalArrayList, params);

        AstFcEstimate assetsDisposal1 = new AstFcEstimate();
        assetsDisposal1.setFcEstimateId(astFcEstimate.getFcEstimateId());
        assetsDisposal1.setEmployeeId(astFcEstimate.getEmployeeId());
        assetsDisposal1.setNowCountStatus(APPROVING);
        this.astFcEstimateService.updateByPrimaryKeySelective(iRequest, assetsDisposal1);

    }

    @Override
    public List<AstFcEstimate> conInceptSubmit2(IRequest iRequest, AstFcEstimate astFcEstimate) throws ResMessageException, ParameterNullException {
        //启动工作流
        approveWfl2(iRequest, astFcEstimate);

        List<AstFcEstimate> contractInsure = new ArrayList<>();
        contractInsure.add(astFcEstimate);
        return contractInsure;
    }

}
