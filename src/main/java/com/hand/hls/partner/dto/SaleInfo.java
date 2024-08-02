package com.hand.hls.partner.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

//销售信息

@Data
public class SaleInfo extends BaseDTO {

    @NotNull(message = "销售方统一社会信用代码不能为空")
    @NotBlank(message = "销售方统一社会信用代码不能为空")
    private  String sellerCode;//销售方统一社会信用代码

    @NotNull(message = "销售方统一社会信用代码名称不能为空")
    @NotBlank(message = "销售方统一社会信用代码名称不能为空")
    private  String sellerName;//销售方统一社会信用代码名称

    @NotNull(message = "上牌主体社会代码不能为空")
    @NotBlank(message = "上牌主体社会代码不能为空")
    private  String licensePlateOwnerCode;//上牌主体社会代码

    @NotNull(message = "上牌主体名称不能为空")
    @NotBlank(message = "上牌主体名称不能为空")
    private  String licensePlateOwnerName;//上牌主体名称

    @NotNull(message = "抵押人社会代码不能为空")
    @NotBlank(message = "抵押人社会代码不能为空")
    private  String mortgagorCode;//抵押人社会代码

    @NotNull(message = "抵押人名称不能为空")
    @NotBlank(message = "抵押人名称不能为空")
    private  String mortgagorName;//抵押人名称

    @NotNull(message = "上牌城市Code（国标码）不能为空")
    @NotBlank(message = "上牌城市Code（国标码）不能为空")
    private  String licensePlateCityCode;//上牌城市Code（国标码）

    @NotNull(message = "上牌城市名称不能为空")
    @NotBlank(message = "上牌城市名称不能为空")
    private  String licensePlateCityName;//上牌城市名称

    @NotNull(message = "抵押城市 Code不能为空")
    @NotBlank(message = "抵押城市 Code不能为空")
    private  String mortgageCityCode;//抵押城市 Code

    @NotNull(message = "抵押城市名称不能为空")
    @NotBlank(message = "抵押城市名称不能为空")
    private  String mortgageCityName;//抵押城市名称

    @NotNull(message = "销售城市code不能为空")
    @NotBlank(message = "销售城市code不能为空")
    private  String salesCityCode;//销售城市code

    @NotNull(message = "销售城市名称不能为空")
    @NotBlank(message = "销售城市名称不能为空")
    private  String salesCityName;//销售城市名称
}
