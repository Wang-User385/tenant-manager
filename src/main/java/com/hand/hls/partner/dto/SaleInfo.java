package com.hand.hls.partner.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;

//销售信息

@Data
public class SaleInfo extends BaseDTO {

    private  String sellerCode;//销售方统一社会信用代码
    private  String sellerName;//销售方统一社会信用代码名称
    private  String licensePlateOwnerCode;//上牌主体社会代码
    private  String licensePlateOwnerName;//上牌主体名称
    private  String mortgagorCode;//抵押人社会代码
    private  String mortgagorName;//抵押人名称
    private  String licensePlateCityCode;//上牌城市Code（国标码）
    private  String licensePlateCityName;//上牌城市名称
    private  String mortgageCityCode;//抵押城市 Code
    private  String mortgageCityName;//抵押城市名称
    private  String salesCityCode;//销售城市code
    private  String salesCityName;//销售城市名称
}
