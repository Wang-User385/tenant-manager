package com.hand.hls.credit.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;

/**
 * <p>
 * description
 * </p>
 *
 * @author JINGZHOU.LI@hand-china.com 2024/7/12 15:34
 */
@Data
public class QueryProjectLeaseItemSalesDTO extends BaseDTO {
    //省国标码
    private Long provinceId;

    //市国标码
    private Long cityId;

}
