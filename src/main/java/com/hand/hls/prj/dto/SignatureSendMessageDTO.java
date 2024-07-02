package com.hand.hls.prj.dto;

import java.util.List;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 签章推送消息队列DTO
 * @author nian.liu@hand-china.com
 * @date 2022/8/24
 */
@Data
public class SignatureSendMessageDTO {


    @ApiModelProperty(value = "系统编码+系统唯一性标识流水号")
    private String uniqueIdentification;

    @ApiModelProperty(value = "业务系统名称")
    private String businessType;

    @ApiModelProperty(value = "说明业务应用场景和环节")
    private String businessScene;

    @ApiModelProperty(value = "签章结果状态")
    private String resultStatus;

    @ApiModelProperty(value = "签章结果描述")
    private String resultDesc;

    @ApiModelProperty(value = "签章合同列表")
    private List<ContractSendMessageDTO> contractList;

}
