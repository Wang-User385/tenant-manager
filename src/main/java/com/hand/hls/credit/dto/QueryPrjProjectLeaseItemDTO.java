package com.hand.hls.credit.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;

/**
 * <p>
 * description
 * </p>
 *
 * @author JINGZHOU.LI@hand-china.com 2024/7/12 14:45
 */
@Data
public class QueryPrjProjectLeaseItemDTO extends BaseDTO {
    /**
     * 车辆品牌
     */
    private String brandC;
    /**
     * 车系
     */
    private String seriesC;
    /**
     * 车辆准载（定员）
     */
    private String vehicleCapacity;
    /**
     * 上牌城市
     */
    private String cityCode;
    /**
     * 融资金额
     */
    private Double financeAmount;
    /**
     * 车辆厂商指导价格
     */
    private Double listPrice;

    /**
     *主键
     */
    private Long projectLeaseItemId;


}
