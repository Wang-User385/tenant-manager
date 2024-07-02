//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.ast.dto;

import com.hand.hls.ast.utils.FiveClassContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FiveClassRunner implements Comparable<FiveClassRunner> {
    private Logger logger = LoggerFactory.getLogger(FiveClassRunner.class);
    private String fiveClassPlan;
    private String fiveClassCode;
    private String description;
    private List<String> formula = new ArrayList();
    private int priority;

    public FiveClassRunnerResult run(Map map) {
        FiveClassRunnerResult fiveClassRunnerResult = new FiveClassRunnerResult();
        List<String> formulaList = this.getFormula();
        List<String> fiveClassTarget = (List)formulaList.stream().filter((l) -> {
            return l.startsWith("$");
        }).distinct().collect(Collectors.toList());
        Map<String, String> fiveClassTargetValue = new HashMap();
        Iterator var6 = fiveClassTarget.iterator();

        while(var6.hasNext()) {
            String target = (String)var6.next();
            String substring = target.substring(1);
            Object value = map.get(substring);
            //value = '7';
//            if (value == null) {
//                this.logger.error("执行五级分类计算器时出错，原因是找不到五级分类代码（FIVE_CLASS_TARGET）" + substring + "对应的值，请在com.hand.hls.ast.service.IAstFiveClassTargetValueAdapter.getFiveClassTargetValueMapByContract方法中返回");
//                fiveClassRunnerResult.setSuccess(false);
//                return fiveClassRunnerResult;
//            }

            fiveClassTargetValue.put(substring, value.toString());
        }

        fiveClassRunnerResult.setFiveClassTargetValue(fiveClassTargetValue);

        try {
            boolean execute = FiveClassContext.execute(formulaList, map);
            if (execute) {
                fiveClassRunnerResult.setSuccess(true);
                fiveClassRunnerResult.setFiveClassCodeBySystem(this.getFiveClassCode());
            } else {
                fiveClassRunnerResult.setSuccess(false);
            }

            return fiveClassRunnerResult;
        } catch (Exception var10) {
            fiveClassRunnerResult.setSuccess(false);
            return fiveClassRunnerResult;
        }
    }

    public static void main(String[] args) {
        Map map = new HashMap();
        map.put("GOVERNMENT_FUNDING", "Y");
        map.put("OVERDUE_DAYS", 10);
        map.put("OVERDUE_DAYS_TOTAL", 20);
        map.put("OVERDUE_MAX_DAYS", 30);
        map.put("RATE", 100);
        FiveClassRunner runner = new FiveClassRunner("test", "123");
        List<String> list = new ArrayList();
        list.add("(");
        list.add("10");
        list.add(">=");
        list.add("7");
        list.add("And");
        list.add("10");
        list.add("<=");
        list.add("30");
        list.add(")");
        list.add("Or");
        list.add("(");
        list.add("20");
        list.add(">=");
        list.add("30");
        list.add("And");
        list.add("20");
        list.add("<=");
        list.add("90");
        list.add(")");
        runner.setFormula(list);
        System.out.println("应该结果->true");
        runner.run(map);
    }

    public FiveClassRunner(String fiveClassPlan, String fiveClassCode) {
        this.fiveClassCode = fiveClassCode;
        this.fiveClassPlan = fiveClassPlan;
    }

    public int compareTo(FiveClassRunner o) {
        return this.priority - o.getPriority();
    }

    public int getPriority() {
        return this.priority;
    }

    public String getFiveClassPlan() {
        return this.fiveClassPlan;
    }

    public void setFiveClassPlan(String fiveClassPlan) {
        this.fiveClassPlan = fiveClassPlan;
    }

    public String getFiveClassCode() {
        return this.fiveClassCode;
    }

    public void setFiveClassCode(String fiveClassCode) {
        this.fiveClassCode = fiveClassCode;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getFormula() {
        return this.formula;
    }

    public void setFormula(List<String> formula) {
        this.formula = formula;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            FiveClassRunner runner = (FiveClassRunner)o;
            return this.fiveClassPlan.equals(runner.fiveClassPlan) && this.fiveClassCode.equals(runner.fiveClassCode);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.fiveClassPlan, this.fiveClassCode});
    }
}
