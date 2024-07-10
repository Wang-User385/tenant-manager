package com.hand.hls.partner.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;

//经销商基本信息

@Data
public class BasicDealerInformation extends BaseDTO {


    private  String dealerid;//经销商编号

    private  String dealername;//经销商名称

    private  String dealerriskl;//经销商风险等级

    private  String dealerprovince;//经销商所在省份

    private  String dealercity;//经销商所在城市

    private  String dealerqu;//经销商所属区县

    private  String dealerdaqu;//经销商所属大区(具体地址)

}
