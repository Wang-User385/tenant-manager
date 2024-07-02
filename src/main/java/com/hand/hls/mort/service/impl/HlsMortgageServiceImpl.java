package com.hand.hls.mort.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.mort.dto.HlsMortgage;
import com.hand.hls.mort.service.HlsMortgageService;
import com.hand.hls.prj.mapper.HlsCusPrjProjectLeaseItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsMortgageServiceImpl extends BaseServiceImpl<HlsMortgage> implements HlsMortgageService {
    @Autowired
    FndCodingRuleValuesService fndCodingRuleValuesService;

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
}