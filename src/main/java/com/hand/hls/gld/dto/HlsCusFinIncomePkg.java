package com.hand.hls.gld.dto;

/**
 * Created by IntelliJ IDEA.
 * @author: WuJun
 * @Date: 2020/02/07
 * @Time: 11:48
 * @description copy from 光大幸福项目
 */

import java.util.List;

public class HlsCusFinIncomePkg {

    private List<HlsCusGldLonContractFinCost> hlsCusGldLonContractFinCostList;
    private List<HlsCusContractFinanceIncome> hlsCusContractFinanceIncomes;
    private List<HlsCusGldFctContractFinIncome> hlsCusGldFctContractFinIncomeList;
    private List<LonContractFinCost> lonContractFinCosts;
    private String finType;

    public List<HlsCusGldLonContractFinCost> getHlsCusGldLonContractFinCostList() {
        return hlsCusGldLonContractFinCostList;
    }

    public void setHlsCusGldLonContractFinCostList(List<HlsCusGldLonContractFinCost> hlsCusGldLonContractFinCostList) {
        this.hlsCusGldLonContractFinCostList = hlsCusGldLonContractFinCostList;
    }

    public List<HlsCusContractFinanceIncome> getHlsCusContractFinanceIncomeList() {
        return hlsCusContractFinanceIncomes;
    }

    public void setHlsCusContractFinanceIncomeList(List<HlsCusContractFinanceIncome> hlsCusContractFinanceIncomeList) {
        this.hlsCusContractFinanceIncomes = hlsCusContractFinanceIncomeList;
    }

    public List<HlsCusGldFctContractFinIncome> getHlsCusGldFctContractFinIncomeList() {
        return hlsCusGldFctContractFinIncomeList;
    }

    public void setHlsCusGldFctContractFinIncomeList(List<HlsCusGldFctContractFinIncome> hlsCusGldFctContractFinIncomeList) {
        this.hlsCusGldFctContractFinIncomeList = hlsCusGldFctContractFinIncomeList;
    }

    public List<LonContractFinCost> getLonContractFinCostList() {
        return lonContractFinCosts;
    }

    public void setLonContractFinCostList(List<LonContractFinCost> lonContractFinCostList) {
        this.lonContractFinCosts = lonContractFinCostList;
    }

    public String getFinType() {
        return finType;
    }

    public void setFinType(String finType) {
        this.finType = finType;
    }
}
