package com.hand.hls.partner.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

//风控审核原始数据

@Data
public class PreRiskAuditData extends BaseDTO {

    private String proline;//产品线

    private BasicCustomerInformation basicCustomerInformation;//承租人信息

    private BasicCustomerJobInformation basicCustomerJobInformation;//承租人职业信息

    private AssociatedPersonInformation associatedPersonInformation;//关联人信息

    private BasicDealerInformation basicDealerInformation;//经销商基本信息

    private CarInformation carInformation;//车辆信息
}
