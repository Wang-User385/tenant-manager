package com.hand.hls.partner.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;

//承租人基本信息

@Data
public class BasicCustomerInformation extends BaseDTO {

    private  String cardno;//银行卡号

    private  String sex;//性别

    private  String nation;//民族

    private  String birthdate;//出生日期

    private  String age;//车型Code

    private  String modelName;//年龄

    private  String nationality;//国籍

    private  String domicileshen;//户籍所属省份

    private  String domicileshi;//户籍所属市

    private  String domicilequ;//户籍所属区

    private  String domicileaddress;//户籍地址

    private  String islocaldomicile;//是否本地户籍

    private  String issuegov;//签发机关

    private  String isenable;//是否长期有效

    private  String homeaddressprovince;//居住地址省

    private  String homeaddresspcity;//居住地址市

    private  String jzdzqx;//居住地址区县

    private  String homeaddress;//居住地址

    private  String housetype;//房产类型

    private  String marriage;//婚姻状况

    private  String childnum;//子女人数

    private  String isdriverlicence;//有无驾照

    private  String driverlicencetype;//驾照类型

    private  String driverstatus;//驾照状态

    private  String jzjzrq;//驾照截止日期

    private  String wzfs;//违章分数

    private  String wzfk;//违章罚款

    private  String diploma;//学历

}
