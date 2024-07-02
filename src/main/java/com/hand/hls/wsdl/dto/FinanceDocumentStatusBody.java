package com.hand.hls.wsdl.dto;

import lombok.Data;

/**
 * <p>汽车租赁单据查询返回实体类
 *
 * @author mufeng.xi
 * created by 2020/03/17
 */

@Data
public class FinanceDocumentStatusBody extends FinanceInterfaceBody{

    /**
     * 越秀合同编号
     */
    private String contractNumber;

    /**
     * 越秀业务经理
     */
    private String employee;

    /**
     * 业务经理邮箱
     */
    private String eMail;

    /**
     * 投放审查状态
     */
    private String signStatus;

    /**
     * 投放款付款申请状态
     */
    private String paymentApplyStatus;

    /**
     * 投放款支付状态
     */
    private String paymentStatus;

    /**
     * 合同状态
     */
    private String contractStatus;

    /**
     * 合同文本状态
     */
    private String contractTextStatus;

    /**
     * 越秀最后放款日
     */
    private String paymentDate;

    /**
     * 越秀投放总额
     */
    private String paymentAmount;
    /**
     * 承租人预审状态
     */
    private String tenantStatus;
    /**
     * 撤单原因
     */
    private String reason;
    /**
     * 进件序号密文
     */
    private String partnersContractEncr;
}
