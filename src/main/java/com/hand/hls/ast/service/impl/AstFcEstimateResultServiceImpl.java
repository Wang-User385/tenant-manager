//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.ast.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hap.script.utils.LeafScriptUtils;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.ast.components.IAstFiveClassCalculator;
import com.hand.hls.ast.dto.AstFcEstimate;
import com.hand.hls.ast.dto.AstFcEstimateResult;
import com.hand.hls.ast.dto.AstFcEstimateResultDtl;
import com.hand.hls.ast.dto.FiveClassRunnerResult;
import com.hand.hls.ast.mapper.AstFcEstimateMapper;
import com.hand.hls.ast.mapper.AstFcEstimateResultDtlMapper;
import com.hand.hls.ast.mapper.AstFcEstimateResultMapper;
import com.hand.hls.ast.service.IAstFcEstimateResultService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import leaf.bean.LeafRequestData;
import leaf.bm.components.RecordHelper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uncertain.composite.CompositeMap;

@Service
@Transactional(
        rollbackFor = {Exception.class}
)
public class AstFcEstimateResultServiceImpl extends BaseServiceImpl<AstFcEstimateResult> implements IAstFcEstimateResultService {
    @Autowired
    private AstFcEstimateResultMapper astFcEstimateResultMapper;
    @Autowired
    private AstFcEstimateResultDtlMapper astFcEstimateResultDtlMapper;
    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;
    @Autowired
    private AstFcEstimateMapper astFcEstimateMapper;
    @Autowired
    private IAstFiveClassCalculator astFiveClassCalculator;

    public AstFcEstimateResultServiceImpl() {
    }

    public ResponseData queryByRequestData(IRequest requestContext, LeafRequestData requestData, int pagenum, int pagesize) {
        Map<String, Object> attributeMap = requestContext.getAttributeMap();
        CompositeMap compositeMap = new CompositeMap();
        Map parameter = requestData.getParameter();
        Object fc_estimate_id = parameter.get("fc_estimate_id");
        if (fc_estimate_id == null && attributeMap.get("fc_estimate_id") == null) {
            return new ResponseData(false, "请求参数有误，请联系管理员:fc_estimate_id不能为空");
        } else {
            compositeMap.putAll(attributeMap);
            compositeMap.putAll(parameter);
            PageHelper.startPage(pagenum, pagesize);
            List<CompositeMap> list = this.astFcEstimateResultMapper.queryByRequestData1(compositeMap);
            return new ResponseData(list);
        }
    }

    public ResponseData batchUpdate(IRequest requestContext, LeafRequestData data) {
        if (data.get("parameter") != null && ((List)data.get("parameter")).size() > 0) {
            List<Map> list = (List)data.get("parameter");

            for(int i = 0; i < list.size(); ++i) {
                CompositeMap compositeMap;
                if ("delete".equals(((Map)list.get(i)).get("_status"))) {
                    compositeMap = new CompositeMap("", (Map)list.get(i));
                    this.deleteByRequestData(requestContext, compositeMap);
                    RecordHelper.insert("hls_doc_layout_button", compositeMap);
                } else if ("update".equals(((Map)list.get(i)).get("_status"))) {
                    compositeMap = new CompositeMap("", (Map)list.get(i));
                    this.updateByRequestData(requestContext, compositeMap);
                }
            }
        }

        return new ResponseData((List)data.get("parameter"));
    }

    public ResponseData execute(IRequest requestContext, LeafRequestData requestData) {
        Map parameter = requestData.getParameter();
        Object resultId = parameter.get("result_id");
        Object five_class_plan = parameter.get("five_class_plan");
        Object contract_id = parameter.get("contract_id");
        Long userId = requestContext.getUserId();
        if (userId != null && !LeafScriptUtils.isValidLong(userId.toString())) {
            if (resultId != null && !LeafScriptUtils.isValidLong(resultId.toString())) {
                if (five_class_plan == null) {
                    return new ResponseData(false, "请求参数有误，请联系管理员:five_class_plan");
                } else {
                    return contract_id != null && !LeafScriptUtils.isValidLong(contract_id.toString()) ? this.astFcEstimateResultInit((Long)resultId, userId, (Long)contract_id, (String)five_class_plan) : new ResponseData(false, "请求参数有误，请联系管理员:contract_id");
                }
            } else {
                return new ResponseData(false, "请求参数有误，请联系管理员:resultId");
            }
        } else {
            return new ResponseData(false, "请求参数有误，请联系管理员:userId");
        }
    }

    public ResponseData deleteByRequestData(IRequest requestContext, CompositeMap compositeMap) {
        Object result_id = compositeMap.get("result_id");
        if (result_id == null) {
            return new ResponseData(false, "请求参数有误，请联系管理员:result_id");
        } else {
            this.astFcEstimateResultMapper.deleteByPrimaryKey(result_id);
            Example example = new Example(AstFcEstimateResultDtl.class);
            example.createCriteria().andEqualTo("resultId", result_id);
            this.astFcEstimateResultDtlMapper.deleteByExample(example);
            return new ResponseData(true);
        }
    }

    public ResponseData updateByRequestData(IRequest requestContext, CompositeMap compositeMap) {
        Object resultId = compositeMap.get("result_id");
        Object fiveClassCode = compositeMap.get("five_class_code");
        Object description = compositeMap.get("description");
        if (resultId == null) {
            return new ResponseData(false, "请求参数有误，请联系管理员:result_id");
        } else {
            AstFcEstimateResult result = new AstFcEstimateResult();
            result.setResultId(Long.valueOf(resultId.toString()));
            result.setFiveClassCode(fiveClassCode == null ? "" : (String)fiveClassCode);
            result.setDescription(description == null ? "" : (String)description);
            this.astFcEstimateResultMapper.updateByPrimaryKeySelective(result);
            return new ResponseData(true);
        }
    }

    public ResponseData astFcEstimateResultInit(Long fcEstimateId, IRequest request) {
        AstFcEstimate astFcEstimate = (AstFcEstimate)this.astFcEstimateMapper.selectByPrimaryKey(fcEstimateId);
        if (astFcEstimate == null) {
            return new ResponseData(false, "请求失败，查询不到指定的五级分类");
        } else {
            Example example = new Example(HlsCusConContract.class);
            example.createCriteria().andEqualTo("dataClass", "NORMAL").andEqualTo("contractStatus", "INCEPT");
            List<HlsCusConContract> contractList = this.hlsCusConContractMapper.selectByExample(example);
            if (CollectionUtils.isEmpty(contractList)) {
                return new ResponseData(true);
            } else {
                String fiveClassPlan = astFcEstimate.getFiveClassPlan();

                try {
                    this.astFiveClassCalculator.load(fiveClassPlan);
                } catch (IllegalArgumentException var19) {
                    return new ResponseData(false, "根据指定的五级分类计划无法找到确切的五级分类，fiveClassCode :" + fiveClassPlan);
                }

                Iterator var7 = contractList.iterator();

                while(true) {
                    HlsCusConContract cusConContract;
                    Long contractId;
                    List astFcEstimateResults;
                    do {
                        do {
                            if (!var7.hasNext()) {
                                return new ResponseData(true);
                            }

                            cusConContract = (HlsCusConContract)var7.next();
                            contractId = cusConContract.getContractId();
                        } while(contractId == null);

                        Example fcEstimateResultExample = new Example(AstFcEstimateResult.class);
                        fcEstimateResultExample.createCriteria().andEqualTo("fcEstimateId", fcEstimateId).andEqualTo("contractId", cusConContract.getContractId());
                        astFcEstimateResults = this.astFcEstimateResultMapper.selectByExample(fcEstimateResultExample);
                    } while(astFcEstimateResults != null && astFcEstimateResults.size() > 0);

                    FiveClassRunnerResult result = this.astFiveClassCalculator.execute(cusConContract);
                    Example fcEstimateResultExample2 = new Example(AstFcEstimateResult.class);
                    fcEstimateResultExample2.createCriteria().andEqualTo("contractId", cusConContract.getContractId());
                    fcEstimateResultExample2.orderBy("fcEstimateId").desc();
                    List<AstFcEstimateResult> history = this.astFcEstimateResultMapper.selectByExample(fcEstimateResultExample2);
                    String orginal = "";
                    if (history != null && history.size() > 0) {
                        AstFcEstimateResult result1 = (AstFcEstimateResult)history.get(0);
                        orginal = result1.getFiveClassCode() != null ? result1.getFiveClassCode() : result1.getFiveClassCodeBySystem();
                    }

                    String fiveClassCodeBySystem = result.getFiveClassCodeBySystem();
                    AstFcEstimateResult insert = new AstFcEstimateResult();
                    insert.setFcEstimateId(fcEstimateId);
                    insert.setContractId(contractId);
                    if (result.isSuccess()) {
                        insert.setFiveClassCodeBySystem(fiveClassCodeBySystem);
                    } else {
                        insert.setFiveClassCodeBySystem("");
                    }

                    insert.setOriginalFiveClassCode(orginal);
                    insert.setEstimateDate(new Date());
                    insert.setStatus("NEW");
                    insert.setCreatedBy(request.getUserId());
                    insert.setCreationDate(new Date());
                    insert.setLastUpdatedBy(request.getUserId());
                    insert.setLastUpdateDate(new Date());
                    this.astFcEstimateResultMapper.insertSelective(insert);
                    Map<String, String> fiveClassTargetValue = result.getFiveClassTargetValue();
                    fiveClassTargetValue.forEach((k, v) -> {
                        AstFcEstimateResultDtl insertDtl = new AstFcEstimateResultDtl();
                        insertDtl.setResultId(insert.getResultId());
                        insertDtl.setFiveClassTarget(k);
                        insertDtl.setTargetValue(v);
                        insertDtl.setCreatedBy(request.getUserId());
                        insertDtl.setCreationDate(new Date());
                        insertDtl.setLastUpdatedBy(request.getUserId());
                        insertDtl.setLastUpdateDate(new Date());
                        System.out.println(insertDtl.toString());
                        this.astFcEstimateResultDtlMapper.insertSelective(insertDtl);
                    });
                }
            }
        }
    }

    public ResponseData astFcEstimateResultInit(Long result_id, Long userId, Long contractId, String fiveClassPlan) {
        return new ResponseData(true);
    }

    @Override
    public AstFcEstimate assessment(IRequest requestContext, AstFcEstimate astFcEstimate, AstFcEstimate conContractAst) {
        if (astFcEstimate != null && conContractAst != null) {

            Long userId = requestContext.getUserId();
                //if ("insert".equals(conContractAst.get__status())) {
                    Example fcEstimateResultExample = new Example(AstFcEstimateResult.class);
                    fcEstimateResultExample.createCriteria()
                            .andEqualTo(AstFcEstimateResult.FIELD_FC_ESTIMATE_ID, astFcEstimate.getFcEstimateId())
                            .andEqualTo(AstFcEstimateResult.FIELD_CONTRACT_ID, conContractAst.getProjectId());
                    //理论上这里应该只有一个，每个合同ID和fcEstimateId 对应一个结果
                    List<AstFcEstimateResult> astFcEstimateResults = astFcEstimateResultMapper.selectByExample(fcEstimateResultExample);

                    if (astFcEstimateResults != null && astFcEstimateResults.size() > 0) {

                        //存在即跳过2019年4月3日12:37:58

                        //将原来的评估结果进行保存,存储的结构为key:fcEstimateId,ContractId ,value 是本
//                orginal = result.getFiveClassCode() != null ? result.getFiveClassCode() : result.getFiveClassCodeBySystem();

                        //测试打开 开始 删除上一次跑的结果---->
//                                        AstFcEstimateResult result = astFcEstimateResults.get(0);
//                                        AstFcEstimateResultDtl delete = new AstFcEstimateResultDtl();
//                                        delete.setResultId(result.getResultId());
//                                        astFcEstimateResultDtlMapper.delete(delete);
//                                        astFcEstimateResultMapper.deleteByPrimaryKey(result);
                        //测试打开 结束---->
                    }else{
                        //计算逻辑,获取指标
                        HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
                        //hlsCusConContractCashflow.setContractType(conContractAst.getContractType());
                        hlsCusConContractCashflow.setContractId(conContractAst.getProjectId());
                        //hlsCusConContractCashflow.setContractId(7407L);
                        astFiveClassCalculator.load(astFcEstimate.getFiveClassPlan());
                        FiveClassRunnerResult result = astFiveClassCalculator.executeNew(hlsCusConContractCashflow,astFcEstimate);
                        //查询上一次的结果
                        Example fcEstimateResultExample2 = new Example(AstFcEstimateResult.class);
                        fcEstimateResultExample2.createCriteria()
                                .andEqualTo(AstFcEstimateResult.FIELD_CONTRACT_ID, conContractAst.getProjectId());
                        fcEstimateResultExample2.orderBy(AstFcEstimateResult.FIELD_FC_ESTIMATE_ID).desc();
                        //理论上这里应该只有一个，每个合同ID和fcEstimateId 对应一个结果
                        List<AstFcEstimateResult> history = astFcEstimateResultMapper.selectByExample(fcEstimateResultExample2);
                        String orginal = "";
                        if (history != null && history.size() > 0) {
                            AstFcEstimateResult result1 = history.get(0);
                            orginal = result1.getFiveClassCode() != null ? result1.getFiveClassCode() : result1.getFiveClassCodeBySystem();
                        }
                        String fiveClassCodeBySystem = result.getFiveClassCodeBySystem();
                        AstFcEstimateResult insert = new AstFcEstimateResult();
                        insert.setFcEstimateId(astFcEstimate.getFcEstimateId());
                        insert.setContractId(conContractAst.getProjectId());
                        if (result.isSuccess()) {
                            insert.setFiveClassCodeBySystem(fiveClassCodeBySystem);
                        } else {
                            insert.setFiveClassCodeBySystem("");
                        }
                        insert.setOriginalFiveClassCode(orginal);
                        insert.setEstimateDate(new Date());
                        insert.setStatus("NEW");
                        insert.setCreatedBy(userId);
                        insert.setCreationDate(new Date());
                        insert.setLastUpdatedBy(userId);
                        insert.setLastUpdateDate(new Date());
                        astFcEstimateResultMapper.insertSelective(insert);
                        Map<String, String> fiveClassTargetValue = result.getFiveClassTargetValue();
                        fiveClassTargetValue.forEach((k, v) -> {
                            AstFcEstimateResultDtl insertDtl = new AstFcEstimateResultDtl();
                            insertDtl.setResultId(insert.getResultId());
                            insertDtl.setFiveClassTarget(k);
                            insertDtl.setTargetValue(v);
                            insertDtl.setCreatedBy(userId);
                            insertDtl.setCreationDate(new Date());
                            insertDtl.setLastUpdatedBy(userId);
                            insertDtl.setLastUpdateDate(new Date());
                            System.out.println(insertDtl.toString());
                            astFcEstimateResultDtlMapper.insertSelective(insertDtl);
                        });
                    }



               /* } else if ("delete".equals(conContractAst.get__status())) {
                    // this.functionResourceMapper.deleteFunctionResource(function.getFunctionId(), resource.getResourceId());
                }*/


            // this.notifyCache((Long)null);
        }

        return astFcEstimate;
    }
}
