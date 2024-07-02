//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.ast.service.impl;

import com.hand.hls.ast.dto.AstFcEstimate;
import com.hand.hls.ast.dto.AstFcEstimateResultDtl;
import com.hand.hls.ast.dto.AstFiveClassRuleDetail;
import com.hand.hls.ast.mapper.AstFcEstimateMapper;
import com.hand.hls.ast.service.IAstFiveClassTargetValueAdapter;
import com.hand.hls.cont.dto.ConContract;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.sys.utils.OracleUtils;

import java.util.*;
import java.util.stream.Collectors;

import org.opensaml.xmlsec.signature.P;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author liao
 */
public class DefaultFiveClassTargetValueAdapter implements IAstFiveClassTargetValueAdapter {
    @Autowired
    private HlsCusConContractCashflowMapper hlsCusConContractCashflowMapper;
    @Autowired
    private AstFcEstimateMapper astFcEstimateMapper;

    public DefaultFiveClassTargetValueAdapter() {
    }

    @Override
    public Map<String, Object> getFiveClassTargetValueMapByContract(HlsCusConContract contract) {
        Map map = new HashMap(5);
        map.put("GOVERNMENT_FUNDING", "Y");
        map.put("OVERDUE_DAYS", 10);
        HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
        hlsCusConContractCashflow.setContractId(contract.getContractId());

        List<HlsCusConContractCashflow> list = this.hlsCusConContractCashflowMapper.queryFiveClassInfo(hlsCusConContractCashflow);
        long sum = ((IntSummaryStatistics)list.stream().collect(Collectors.summarizingInt((cashflow) -> {
            return cashflow.getOverdueMaxDays() != null && cashflow.getOverdueMaxDays() > 0L ? cashflow.getOverdueMaxDays().intValue() : 0;
        }))).getSum();
        map.put("OVERDUE_DAYS_TOTAL", sum);
        Long overdueMaxDays = contract.getOverdueMaxDays();
        map.put("OVERDUE_MAX_DAYS", OracleUtils.nvl(overdueMaxDays, 0L));
        map.put("RATE", 100);
        return map;
    }
    @Override
    public Map<String, Object> getFiveClassTargetValueMapByContractNew(HlsCusConContractCashflow cashFlow, AstFcEstimate astFcEstimate) {
        Map map = new HashMap(5);
        map.put("GOVERNMENT_FUNDING", "Y");
        map.put("OVERDUE_DAYS", 10);


        List<HlsCusConContractCashflow> list = hlsCusConContractCashflowMapper.queryFiveClassInfo(cashFlow);
        long sum = (list.stream().collect(Collectors.summarizingInt((e) -> {
            return e.getOverdueMaxDays() != null && e.getOverdueMaxDays() > 0L ? e.getOverdueMaxDays().intValue() : 0;
        }))).getSum();
        Optional<HlsCusConContractCashflow> max1 = list.stream().filter(e -> e.getOverdueMaxDays() != null)
                .max((e, y) -> e.getOverdueMaxDays().compareTo(y.getOverdueMaxDays()));
        Long overdueMaxDays ;
        if(max1.isPresent()){
            if(max1.get().getOverdueMaxDays() != null){
                overdueMaxDays =max1.get().getOverdueMaxDays();
            }else {
                overdueMaxDays = 0L;
            }
        }else {
            overdueMaxDays = 0L;
        }
        map.put("OVERDUE_DAYS_TOTAL", sum);
        map.put("OVERDUE_MAX_DAYS", OracleUtils.nvl(overdueMaxDays, 0L));
        map.put("RATE", 100);
        AstFiveClassRuleDetail astFiveClassRuleDetail =  astFcEstimateMapper.queryFiveClassTarget(astFcEstimate);
       // map.put("1",OracleUtils.nvl(overdueMaxDays, 0L));
        map.put(astFiveClassRuleDetail.getFiveClassTarget(),OracleUtils.nvl(overdueMaxDays, 0L));
        return map;
    }
}
