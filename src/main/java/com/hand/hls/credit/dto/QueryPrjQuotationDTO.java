package com.hand.hls.credit.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;

/**
 * <p>
 * description
 * </p>
 *
 * @author JINGZHOU.LI@hand-china.com 2024/7/12 15:04
 */
@Data
public class QueryPrjQuotationDTO extends BaseDTO {

    /**
     * 主键
     */
    private Long quotationId;

    /**
     * 首付比例
     */
    private Double downPaymentRatio;
    /**
     * 月还款额
     */
    private Double pmt;
    /**
     * 申请期限
     */
    private Long leaseTimes;

}
