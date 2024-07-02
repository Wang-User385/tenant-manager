package com.hand.hls.wsdl.dto;

import lombok.Data;

/**
 * 承租人预审结果查询返回实体类
 *
 * @author luo.biao
 * created by 2022/4/26
 */

@Data
public class FinanceTenantResultBody extends FinanceInterfaceBody {

    /**
     * 证件号码
     */
    private String idCardNo;

    /**
     * 承租人预审状态
     */
    private String result;

    /**
     * 拒绝原因
     */
    private String reason;
}
