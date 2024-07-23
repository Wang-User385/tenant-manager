package com.hand.hls.mort.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.mort.dto.HlsMortgage;
import com.hand.hls.mort.mapper.HlsMortgageMapper;
import com.hand.hls.mort.service.HlsMortgageService;
import com.hand.hls.prj.mapper.HlsCusPrjProjectLeaseItemMapper;
import com.hand.hls.wfl.service.IActivitiCommonService;
import com.hand.hls.wfl.service.IActivitiStartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsMortgageServiceImpl extends BaseServiceImpl<HlsMortgage> implements HlsMortgageService {
    @Autowired
    FndCodingRuleValuesService fndCodingRuleValuesService;

    @Autowired
    private IActivitiStartService activitiStartService;

    @Autowired
    private HlsMortgageMapper hlsMortgageMapper;

    //流程编码
    private final static String WORK_FLOW = "CAR_MORTGAGE";
    //流程分类
    private final static String DEMO_NAME = "CAR_MORTGAGE";

    @Override
    public List<HlsMortgage> save(IRequest iRequest, HlsMortgage hlsMortgage) {
        //后台生成编码规则
        Map<String, String> params = new HashMap<String, String>();
        String mortgageNumber = fndCodingRuleValuesService.getCodeRuleValue(iRequest, "HLS_MORTGAGE", "HLS_MORTGAGE", "HLS_MORTGAGE", params);
        //hlsMortgage.setMortgageNumber(mortgageNumber);
        self().insertSelective(iRequest,hlsMortgage);
        List<HlsMortgage> list = new ArrayList<>();
        list.add(hlsMortgage);
        return list;
    }

    @Override
    public List<HlsMortgage> submitWfl(HlsMortgage dto, IRequest requestCtx) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("workFlowType",WORK_FLOW);
        params.put(IActivitiCommonService.WORK_FLOW_NAME, WORK_FLOW);
        params.put(IActivitiCommonService.DEMO_NAME, DEMO_NAME);
        params.put(IActivitiCommonService.BUSINESS_KEY, dto.getContractId());
        params.put("contract_id",dto.getContractId());
        //单据名称
        //params.put("documentName", );
        //单据编号
        //params.put("documentNumber",);
        //查询
        List<HlsMortgage> res = new ArrayList<>();
        res.add(dto);
        activitiStartService.start(requestCtx, res, params);
        return res;
    }
}