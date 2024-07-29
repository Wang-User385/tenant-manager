package com.hand.hls.partner.dto;

import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 回购试算、请求，，提前结清试算、请求计算结果数据
 */
@Data
public class CalculationResultsDto {
    private Double payableAmount; //应付金额

    private Double principal; //本金

    private Double interest; //利息

    private Double deductAmount; //抵扣金额

    private List<HlsCusConContractCashflow> writeOffList;  //需要自动核销为租金的代偿数据(回购请求使用)

    private List<Integer> termNos;  //期次信息(回购试算、提前结清试算使用)

    private List<Integer> deductNos; //期次信息(回购试算、提前结清试算使用)

    private Double penalty;  //罚息

    private Date dueDate; //回购、提前结清现金生成时应收日期

    private HlsCusConContractCashflow conContractCashflow; //现金流数据


}
