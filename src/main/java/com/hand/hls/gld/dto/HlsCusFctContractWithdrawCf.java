package com.hand.hls.gld.dto;

import javax.persistence.Table;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;

/**
 * @author wujun
 * @version 1.0
 * @date 2020/2/7 15:23
 * @description
 */
@ExtensionAttribute(
        disable = true
)
@Table(
        name = "fct_contract_withdraw_cf"
)
public class HlsCusFctContractWithdrawCf extends FctContractWithdrawCf {
    public HlsCusFctContractWithdrawCf() {
    }
}
