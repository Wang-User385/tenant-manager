package com.hand.hls.partner.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;

import java.util.List;

//账单查询

@Data
public class QueryOrder extends BaseDTO {

    private String contractStatus;//合同状态
    private String orderNo;//订单编号
    private String status;//还款计划状态；NORMAL - 正常ENDED - 已结束
    private Integer termCount;//贷款总期数
    private List<RepayPlanTermInfoDTO> repayPlanTermInfoDTOList;
}
