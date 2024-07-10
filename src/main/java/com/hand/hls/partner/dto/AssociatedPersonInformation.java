package com.hand.hls.partner.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;

@Data
public class AssociatedPersonInformation extends BaseDTO {


    private  String sjjsrysqrgx;//实际驾驶人与申请人关系

    private  String issureor;//有无担保人

    private  String dbryczrgx;//担保人与承租人关系

    private  String surename;//担保人姓名

    private  String surecertype;//担保人证件类型

    private  String sureid;//担保人身份证

    private  String suremobi;//担保人手机

    private  String iscop;//有无共同承租人

    private  String corelation;//共同借款人社会关系

    private  String coname;//共同借款人姓名

    private  String cocerttype;//共同承租人证件类型

    private  String coid;//共同借款人身份证

    private  String comobile;//共同借款人手机

    private  String coaddr;//共同承租人居住地址

    private  String cocompany;//共同借款人工作单位

    private  String cocomtel;//共同承租人公司电话

    private  String cocomaddr;//共同借款人公司地址

    private  String zxqsgx;//直系亲属关系

    private  String spousename;//配偶姓名

    private  String spouseidcard;//配偶身份证

    private  String spousephone;//配偶联系电话

    private  String spousesex;//配偶性别

    private  String spousebir;//配偶出生日期

    private  String spousecomp;//配偶公司名称

    private  String spousecompind;//配偶公司所属行业

    private  String spousecomptype;//配偶公司性质

    private  String spousezylx;//配偶职业类型

    private  String spousezw;//配偶职位

    private  String spoucecompaddr;//配偶单位地址

    private  String contnum;//联系人数量

    private  String contreleship;//联系人与承租人关系

    private  String contname;//联系人姓名

    private  String contaddr;//联系人当前居住地址

    private  String partymobile;//联系人移动电话

}
