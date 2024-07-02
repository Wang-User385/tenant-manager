package com.hand.hls.wsdl.dto;

import lombok.Data;

/**
 * 三一订单补充合同要素入参
 * @author luobiao
 * @Date 2022/4/15
 */
@Data
public class SyEquipmentCodingNotifyInfo {

    private String assetNo;

    private String certificateNo;

    private String engineNo;

    private String vehicleNumber;

    private String chassisNumber;
}
