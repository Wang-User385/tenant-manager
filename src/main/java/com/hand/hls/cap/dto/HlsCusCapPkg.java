package com.hand.hls.cap.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HlsCusCapPkg {
    private HlsCusCapitalInvestmentPlanHd hlsCusCapitalInvestmentPlanHd;
    private List<HlsCusCapitalInvestmentPlanLn> hlsCusCapitalInvestmentPlanLnList;
    private HlsCusCapFinancingPlan hlsCusCapFinancingPlan;
    private HlsCusCapAccountMonthlyBalance hlsCusCapAccountMonthlyBalance;
    private List<HlsCusCapAccountMonthlyBalance> hlsCusCapAccountMonthlyBalanceList;
    private List<HlsCusCapFinancingPlanLn> hlsCusCapFinancingPlanLnList;
    private Boolean success;
    private String message;
}
