//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.ast.components.impl;

import com.hand.hap.mybatis.entity.Example;
import com.hand.hap.mybatis.util.StringUtil;
import com.hand.hls.ast.components.IAstFiveClassCalculator;
import com.hand.hls.ast.dto.*;
import com.hand.hls.ast.mapper.AstFiveClassCodeMapper;
import com.hand.hls.ast.mapper.AstFiveClassRuleDetailMapper;
import com.hand.hls.ast.mapper.AstFiveClassRuleMapper;
import com.hand.hls.ast.service.IAstFiveClassTargetValueAdapter;
import com.hand.hls.ast.service.impl.DefaultFiveClassTargetValueAdapter;
import com.hand.hls.cont.dto.ConContract;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class AstFiveClassCalculator implements IAstFiveClassCalculator, InitializingBean {
    private List<AstFiveClassCode> astFiveClassCodeList;
    @Autowired
    private AstFiveClassCodeMapper astFiveClassCodeMapper;
    @Autowired
    private AstFiveClassRuleMapper astFiveClassRuleMapper;
    @Autowired
    private AstFiveClassRuleDetailMapper astFiveClassRuleDetailMapper;
    private List<FiveClassRunner> fiveClassRunners = new ArrayList();
    @Autowired(
            required = false
    )
    private IAstFiveClassTargetValueAdapter astFiveClassTargetValueAdapter;
    @Autowired
    private ApplicationContext applicationContext;

    public AstFiveClassCalculator() {
    }

    public void load(String fiveClassPlan) {
        if (StringUtils.isEmpty(fiveClassPlan)) {
            throw new IllegalArgumentException("AstFiveClassCalculator 初始化失败，原因：load时找不到 fiveClassPlan");
        } else {
            AstFiveClassCode code = new AstFiveClassCode();
            code.setFiveClassPlan(fiveClassPlan);
            code.setEnabledFlag("Y");
            this.astFiveClassCodeList = this.astFiveClassCodeMapper.select(code);
            this.fiveClassRunners.clear();
            Iterator var3 = this.astFiveClassCodeList.iterator();

            while(var3.hasNext()) {
                AstFiveClassCode astFiveClassCode = (AstFiveClassCode)var3.next();
                AstFiveClassRule rule = new AstFiveClassRule();
                rule.setFiveClassRuleId(astFiveClassCode.getFiveClassRuleId());
                rule.setEnabledFlag("Y");
                List<AstFiveClassRule> fiveClassRules = this.astFiveClassRuleMapper.select(rule);
                if (fiveClassRules != null && fiveClassRules.size() > 0) {
                    AstFiveClassRule fiveClassRule = (AstFiveClassRule)fiveClassRules.get(0);
                    Example example = new Example(AstFiveClassRuleDetail.class);
                    example.createCriteria().andEqualTo("fiveClassRuleId", fiveClassRule.getFiveClassRuleId());
                    example.orderBy("sequenceNo");
                    List<AstFiveClassRuleDetail> ruleDetails = this.astFiveClassRuleDetailMapper.selectByExample(example);
                    if (ruleDetails != null && ruleDetails.size() > 0) {
                        this.generateFiveClassRunner(fiveClassPlan, astFiveClassCode, fiveClassRule, ruleDetails);
                    }
                }
            }

        }
    }

    public FiveClassRunnerResult execute(HlsCusConContract contract) {
        Map<String, Object> map = this.astFiveClassTargetValueAdapter.getFiveClassTargetValueMapByContract(contract);
        Collections.sort(this.fiveClassRunners);
        Iterator var3 = this.fiveClassRunners.iterator();

        FiveClassRunnerResult run;
        do {
            if (!var3.hasNext()) {
                return FiveClassRunnerResult.noMatch();
            }

            FiveClassRunner runner = (FiveClassRunner)var3.next();
            run = runner.run(map);
        } while(!run.isSuccess());

        run.setContractId(contract.getContractId());
        return run;
    }


    @Override
    public FiveClassRunnerResult executeNew(HlsCusConContractCashflow cashFlow, AstFcEstimate astFcEstimate) {
        Map<String, Object> map = astFiveClassTargetValueAdapter.getFiveClassTargetValueMapByContractNew(cashFlow,astFcEstimate);
        Collections.sort(fiveClassRunners);
        Iterator var3 = fiveClassRunners.iterator();

        FiveClassRunnerResult run;
        do {
            if (!var3.hasNext()) {
                return FiveClassRunnerResult.noMatch();
            }

            FiveClassRunner runner = (FiveClassRunner)var3.next();
            run = runner.run(map);
        } while(!run.isSuccess());

        run.setContractId(cashFlow.getContractId());
        return run;
    }



    private void generateFiveClassRunner(String fiveClassPlan, AstFiveClassCode code, AstFiveClassRule fiveClassRule, List<AstFiveClassRuleDetail> ruleDetails) {
        FiveClassRunner runner = new FiveClassRunner(fiveClassPlan, code.getFiveClassCode());
        List<String> formula = new ArrayList();
        Iterator var7 = ruleDetails.iterator();

        while(var7.hasNext()) {
            AstFiveClassRuleDetail detail = (AstFiveClassRuleDetail)var7.next();
            String leftBracket = detail.getLeftBracket();
            if (StringUtils.isNotEmpty(leftBracket)) {
                formula.add(leftBracket);
            }

            String fiveClassTarget = detail.getFiveClassTarget();
            if (StringUtils.isNotEmpty(fiveClassTarget)) {
                formula.add("$" + fiveClassTarget);
            }

            String calculateSymbol = detail.getCalculateSymbol();
            if (StringUtil.isNotEmpty(calculateSymbol)) {
                formula.add(calculateSymbol);
            }

            String calculateValue = detail.getCalculateValue();
            if (StringUtils.isNotEmpty(calculateValue)) {
                formula.add(calculateValue);
            }

            String rightBracket = detail.getRightBracket();
            if (StringUtils.isNotEmpty(rightBracket)) {
                formula.add(rightBracket);
            }

            String logicCalcSymbol = detail.getLogicCalcSymbol();
            if (StringUtils.isNotEmpty(logicCalcSymbol)) {
                formula.add(logicCalcSymbol);
            }
        }

        runner.setFormula(formula);
        Long priority = code.getPriority();
        if (priority != null) {
            runner.setPriority(priority.intValue());
        }
        String description = code.getDescription();
        if(description!=null){
            runner.setDescription(description);
        }

        this.fiveClassRunners.add(runner);
    }

    public void afterPropertiesSet() throws Exception {
        if (this.astFiveClassTargetValueAdapter == null) {
            this.astFiveClassTargetValueAdapter = new DefaultFiveClassTargetValueAdapter();
            this.applicationContext.getAutowireCapableBeanFactory().autowireBean(this.astFiveClassTargetValueAdapter);
        }

    }
}
