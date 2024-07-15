package com.hand.hls.credit.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;

/**
 * <p>
 * description
 * </p>
 *
 * @author JINGZHOU.LI@hand-china.com 2024/7/15 9:21
 */
@Data
public class QueryLateInfo extends BaseDTO {

    /**
     * 逾期4-30天次数
     */
    private Long fourToThirtyDaysOverdueCount;
    /**
     * 逾期31-60天
     */
    private Long ThirtyOneToSixtyDaysOverdueCount;
    /**
     * 一年内有多少个起租日
     */
    private Long leaseStartDateCount;

}
