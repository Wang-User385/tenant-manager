package com.hand.hls.partner.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
public class DataAcquisitionDTO extends BaseDTO {

        @NotNull(message = "订单编号不能为空")
        @NotBlank(message = "订单编号不能为空")
        private String orderNo;//订单编号

        @NotNull(message = "销售信息不能为空")
        private SaleInfo saleInfo;//销售信息

        @NotNull(message = "租赁物相关信息不能为空")
        private CarInfo carInfo;//租赁物相关信息

        @NotNull(message = "融资方案相关信息不能为空")
        private FinanceInfo financeInfo;//融资方案相关信息

        private String riskInfo;//风控审核相关数据

}
