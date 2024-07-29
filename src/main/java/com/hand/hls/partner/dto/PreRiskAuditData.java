package com.hand.hls.partner.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

//风控审核原始数据

@Data
public class PreRiskAuditData extends BaseDTO {

    private String proline;//产品线

    private  String cardno;//银行卡号

    @NotNull(message = "性别不能为空")
    private  String sex;//性别
    @NotNull(message = "民族不能为空")
    private  String nation;//民族
    @NotNull(message = "出生日期不能为空")
    private  String birthdate;//出生日期
    @NotNull(message = "年龄不能为空")
    private  String age;//年龄

    @NotNull(message = "国籍不能为空")
    private  String nationality;//国籍
    @NotNull(message = "户籍所属省份不能为空")
    private  String domicileshen;//户籍所属省份
    @NotNull(message = "户籍所属市不能为空")
    private  String domicileshi;//户籍所属市

    private  String domicilequ;//户籍所属区

    private  String domicileaddress;//户籍地址

    private  String islocaldomicile;//是否本地户籍

    private  String issuegov;//签发机关

    private  String isenable;//是否长期有效
    @NotNull(message = "居住地址省不能为空")
    private  String homeaddressprovince;//居住地址省
    @NotNull(message = "居住地址市不能为空")
    private  String homeaddresspcity;//居住地址市

    private  String jzdzqx;//居住地址区县
    @NotNull(message = "居住地址不能为空")
    private  String homeaddress;//居住地址

    private  String housetype;//房产类型

    private  String marriage;//婚姻状况

    private  String childnum;//子女人数
    @NotNull(message = "有无驾照不能为空")
    private  String isdriverlicence;//有无驾照
    @NotNull(message = "驾照类型不能为空")
    private  String driverlicencetype;//驾照类型

    private  String driverstatus;//驾照状态
    @NotNull(message = "驾照截止日期不能为空")
    private  String jzjzrq;//驾照截止日期

    private  String wzfs;//违章分数

    private  String wzfk;//违章罚款

    private  String diploma;//学历

    private  String company;//单位名称

    private  String industry;//所属行业

    private  String corpnprop;//单位性质

    private  String occu;//职业

    private  String position;//当前职位

    private  String salary;//个人月收入

    private  String companyphone;//单位电话
    @NotNull(message = "公司所属省份不能为空")
    private  String companyshen;//公司所属省份
    @NotNull(message = "公司所属市不能为空")
    private  String companyshi;//公司所属市

    private  String companyqu;//公司所属区

    private  String compaddr;//公司地址

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

    private  String dealerid;//经销商编号

    private  String dealername;//经销商名称

    private  String dealerriskl;//经销商风险等级
    @NotNull(message = "经销商所在省份不能为空")
    private  String dealerprovince;//经销商所在省份
    @NotNull(message = "经销商所在城市不能为空")
    private  String dealercity;//经销商所在城市

    private  String dealerqu;//经销商所属区县

    private  String dealerdaqu;//经销商所属大区(具体地址)

    private  String carfac;//制造商

    private  String vehicletype;//车辆类型（小型普通客车）
    @NotNull(message = "车辆品牌不能为空")
    private  String carbrand2;//车辆品牌

    private  String cartype;//车辆型号
    @NotNull(message = "车系不能为空")
    private  String chexi;//车系

    private  String carcolor;//车辆颜色

    private  String dangwei;//档位形式
    @NotNull(message = "车辆准载不能为空")
    private  String carzkcount;//车辆准载（定员）

    private  String sfjk;//是否进口

    private  String carno;//车架号

    private  String engineno;//发动机号码

    private  String rllx;//燃料类型

    private  String cardateofproduction;//车辆出厂日期

    private  String clpgjg;//车辆评估价格

    private  String scdjrq;//首次登记日期

    private  String transferencedate;//转让登记日期

    private  String chepaihao;//车牌号

    private  String carnatureofuse;//车辆使用性质
    @NotNull(message = "上牌城市不能为空")
    private  String registeredcity;//上牌城市

    private  String scspr;//首次上牌日

    private  String bxlc;//表显里程（公里数）

    private  String carlife;//车辆年限

    private  String caraffiliation;//车辆所有人

    private  String dycs;//抵押次数

    private  String ghjcs;//过户次数

    private  String jyndics;//近1年抵押次数

    private  String last1yearguohucount;//近1年过户次数

    private  String last2yearguohucount;//近2年过户次数

    private  String isregister;//是否有车辆登记证补领记录

    private  String isregisterhy;//近半年是否有车辆登记证补领记录

    private  String lastmortgagedate;//上一次抵押登记日期

    private  String lastdtecompressiondate;//最近一次解押日期

    private  String mortgagestatus;//抵押状态

    private  String jyts;//解押天数

    private  String sfyjqx;//是否有交强险

    private  String jqxdqrq;//交强险到期日期

    private  String sfycsx;//是否有车损险

    private  String csxdqrq;//车损险到期日期

    private  String sfyszx;//是否有第三者责任险

    private  String szxdqrq;//第三者责任险到期日期

    private  String ischeckyear;//是否年检

    private  String sfazgps;//是否安装GPS

    private  String cddcpmc;//车抵贷产品名称

    private  String usage;//贷款用途
    @NotNull(message = "融资金额不能为空")
    private  String financingamount;//融资金额

    private  String zpzlxmzj;//核批租赁项目总价

    private  String applyloanamount;//借款申请金额
    @NotNull(message = "申请期限不能为空")
    private  String shenqingqixain;//申请期限

    private  String sgzk;//事故状况

    private  String fdjdx;//发动机大修

    private  String sfsp;//是否水泡

    private  String sfyzf;//是否营转非

    private  String sfzdgzc;//是否重大改装车

    private  String sptype;//上牌类型

    private  String paizhaogs;//牌照归属

    private  String cljyjg;//车辆交易价

    private  String yuanchezzjlx;//原车主证件类型

    private  String yuanczxm;//原车主姓名

    private  String yuanchezzjhm;//原车主证件号

    private  String yuanchezhuhujishengfen;//原车主户籍所在省份

    private  String yuanchezhuhujishi;//原车主户籍所在市

    private  String carownersdomicilelast;//原车主户籍所在区县

    private  String bywxqk;//维修保养情况

    private  String jpjz;//精品加装

    private  String yfzj;//月付租金

    private  String nhll;//年化利率

    private  String sfje;//首付金额

    private  String clxsjg;//车辆销售价格
    @NotNull(message = "车辆厂商指导价格不能为空")
    private  String cfpp;//车辆厂商指导价格

    private  String rzll;//融资利率
    @NotNull(message = "首付比例不能为空")
    private  String paymentratio;//首付比例

    private  String wkbl;//尾款比例

    private  String fjpbl;//附加品比例

    private  String weikuan;//尾款

    private  String fjpje;//附加品金额

    private  String gouzhis;//购置税

    private  String qzbxje;//强制保险金额

    private  String shangyebx;//商业保险

    private  String ctac;//上牌发票金额

    private  String cheliangse;//车辆税额

    private  String fwhtf;//服务合同费

    private  String yanbaoje;//延保金额

    private  String zspje;//装饰品金额

    private  String gpsfy;//GPS费用

    private  String qtfy;//其他费用

    private  String yongjin;//佣金
    @NotNull(message = "月还款额不能为空")
    private  String yhke;//月还款额

    private  String clbxje;//车辆保险金额
}
