package com.hand.hls.partner.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;

@Data
public class BasicCustomerJobInformation extends BaseDTO {

    private  String company;//单位名称

    private  String industry;//所属行业

    private  String corpnprop;//单位性质

    private  String occu;//职业

    private  String position;//当前职位

    private  String salary;//个人月收入

    private  String companyphone;//单位电话

    private  String companyshen;//公司所属省份

    private  String companyshi;//公司所属市

    private  String companyqu;//公司所属区

    private  String compaddr;//公司地址

}
