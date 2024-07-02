package com.hand.hls.prj.components;

import org.springframework.stereotype.Component;

/**
 * @author: ximufeng
 * @version: v1.0
 * @description: 进件同步接口常量类
 * @date:2019/11/22
 */
@Component
public class SynPrjInfoConstants {

    /**
     * 基本信息
     */
    public static final String MANUFACTURER = "MANUFACTURER";
    public static final String TENANT = "TENANT";
    public static final String VENDER = "VENDER";
    public static final String CNY = "CNY";
    public static final String ORG = "ORG";
    public static final String NP = "NP";
    public static final String PARTNER = "PARTNER";
    public static final String BP_NAME = "bpName";
    public static final String AUTHORITY_RULE_FLAG = "authorityRuleFlag";
    public static final String PRJ_PROJECT_ATTACHMENT = "prj_project_attachment";
    public static final String PRJ_SIGN_ATTACHMENT = "prj_sign_attachment";
    public static final String CSH_PAYMENT_REQ_ATTACHMENT = "csh_payment_req_attachment";
    public static final String CSH_PAYMENT_REQ_LN = "csh_payment_req_ln";
    public static final String PRJ_PROJECT = "PRJ_PROJECT";
    public static final String SINGLE = "SINGLE";
    public static final String CAR_PROJECT = "CAR_PROJECT";
    public static final String PAYMENT_BP_TYPE_PARTNERS = "PARTNERS";
    public static final String DISTRIBUTOR = "DISTRIBUTOR";

    /**
     * 单据状态
     */

    public static final String APPROVED_RETURN = "APPROVED_RETURN";
    public static final String APPROVED = "APPROVED";
    public static final String FAILURE = "FAILURE";
    public static final String PAID = "PAID";
    public static final String PAYING = "PAYING";
    public static final String FAILED = "FAILED";

    /**
     * 字段名
     */
    public static final String PAYMENT_APPLY_STATUS = "paymentApplyStatus";
    public static final String PAYMENT_STATUS = "paymentStatus";

    /**
     * Key
     */
    public static final String DOWN_PAYMENT_METHOD_N = "downPaymentMethodN";
    public static final String DEPOSIT_METHOD_N = "depositMethodN";
    public static final String DEPOSIT_PROCESS_N = "depositProcessN";
    public static final String PRICE_TYPE_N = "priceTypeN";
    public static final String STEP_FLAG_N = "stepFlagN";

    /**
     * 返回字段
     */
    public static final String TYPE = "type";
    public static final String MESSAGE = "message";
    public static final String BUSINESS_KEY = "businessKey";
    public static final String PARTNERS_CONTRACT_NUMBER = "partnersContractNumber";
    public static final String PARTNERS = "partners";
    public static final String ATTACHMENT_ID = "attachmentId";
    public static final String FILE_NAME = "fileName";

    /**
     * 值列表
     */
    public static final String[] DOCUMENT_TYPE_LIST = {"PRJL", "PRJLB"};
    public static final String[] DOCUMENT_STATUS = {"CREATE", "CHANGE"};
    public static final String[] FILE_TYPES = {"DOCX","DOC","PDF","XLSX","PNG","JPG","XLS","JPEG","PPTX","PPT","TXT"};

    /**
     * 系统代码CODE
     */
    public static final String YES_NO_CODE= "SYS.YES_NO";
    public static final String FLOATING_RANGE_METHOD_CODE= "CON.FLOATING_RANGE_METHOD";
    public static final String BASE_RATE_TYPE_CODE= "FND.BASE_RATE_TYPE";
    public static final String PRICE_TYPE_CODE= "PRICE_TYPE";
    public static final String ZX_REGNOTYPE_CODE= "ZX_REGNOTYPE";
    public static final String HLS211_ID_TYPE = "HLS211_ID_TYPE";
    public static final String MAIN_COMPOSITION_POSITION = "MAIN_COMPOSITION_POSITION";
    public static final String HLS211_GENDER= "HLS211_GENDER";
    public static final String HLS211_MARITAL_STATUS= "HLS211_MARITAL_STATUS";
    public static final String GUARANTOR_TYPE= "GUARANTOR_TYPE";
    public static final String ZX_MEMBERTYPE= "ZX_MEMBERTYPE";
    public static final String HLS211_ADDRESS_TYPE= "HLS211_ADDRESS_TYPE";
    public static final String PAY_METHOD = "PAY_METHOD";
    public static final String GRACE_TYPE = "GRACE_TYPE";
    public static final String DEPOSIT_PROCESS = "DEPOSIT_PROCESS";
    public static final String PAYMENT_BP_TYPE = "PAYMENT_BP_TYPE";
    public static final String GRACE_TYPE_SPECIAL = "GRACE_TYPE_SPECIAL";
    public static final String CONFIRM_STATUS = "CONFIRM_STATUS";
    public static final String SIGN_METHOD = "SIGN_METHOD";
    public static final String PRJ_PAYMENT_METHOD = "PRJ.PAYMENT_METHOD";
    public static final String PRJ_PAYMENT_METHOD1 = "PRJ.PAYMENT_METHOD1";
    public static final String PRJ_ACCEPTANCE_TERM = "PRJ.ACCEPTANCE_TERM";
    public static final String HLS211_ACADEMIC_BACKGROUND = "HLS211_ACADEMIC_BACKGROUND";//学历
    public static final String ACADEMIC_DEGREE = "ACADEMIC_DEGREE";//学位
    public static final String EMPLOYMENT_STATUS = "EMPLOYMENT_STATUS";//就业状况
    public static final String BP_JOB_NATURE = "BP.JOB_NATURE";//单位性质
    public static final String INDUSTRY_ORG = "INDUSTRY_ORG";//行业
    public static final String PROFESSION = "PROFESSION";//职业
    public static final String POSITION = "POSITION";//职务
    public static final String JOB_TITLE = "JOB_TITLE";//职称
    public static final String BP_HOUSE_PROPERTY = "BP.HOUSE_PROPERTY";//居住状况
    //保险类型
    public static final String NEW_INSURANCE_TYPE = "NEW_INSURANCE_TYPE";


    /**
     * 返回信息
     */
    public static final String S = "S";
    public static final String E = "E";

    public static final String SUCCESS_MESSAGE = "同步成功!";

    public static final String NO_PARTNERS_ERROR_MSG = "合作方不能为空！";
    public static final String PARTNERS_MATCH_ERROR_MSG = "查找不到对应的合作方！";

    public static final String NO_PARTNERS_CONTRACT_NUMBER_ERROR_MSG = "进件序号不能为空！";
    public static final String MORE_PARTNERS_CONTRACT_NUMBER_ERROR_MSG = "进件序号重复！";

    public static final String NO_DOCUMENT_TYPE_ERROR_MSG = "单据类型不能为空！";
    public static final String DOCUMENT_TYPE_MATCH_ERROR_MSG = "单据类型匹配错误！";

    public static final String NO_LEASE_START_DATE_ERROR_MSG = "投放日不能为空！";

    public static final String NO_SECOND_HAND_FLAG_ERROR_MSG = "是否二手机不能为空！";
    public static final String SECOND_HAND_FLAG_MATCH_ERROR_MSG = "是否二手机匹配错误！";

    public static final String NO_VENDER_MODE_FLAG_ERROR_MSG = "是否经销商模式不能为空！";
    public static final String VENDER_MODE_FLAG_MATCH_ERROR_MSG = "是否经销商模式匹配错误！";

    public static final String FLOATING_RANGE_METHOD_MATCH_ERROR_MSG = "调息规则匹配错误！";
    public static final String NO_FLOATING_RANGE_METHOD_ERROR_MSG = "调息规则不能为空！";

    public static final String BASE_RATE_TYPE_MATCH_ERROR_MSG = "基准利率类型匹配错误！";

    public static final String NO_VENDER_NUMBER_ERROR_MSG = "经销商证件号码不能为空！";
    public static final String VENDER_NUMBER_MATCH_ERROR_MSG = "经销商匹配错误！";
    public static final String NO_VENDER_BANK_ERROR_MSG = "银行信息不齐全！";
    public static final String TSS_BANK_MATCH_ERROR_MSG = "TSS银行匹配错误！";
    public static final String VENDER_ROLE_ERROR_MSG = "经销商角色有误！";

    public static final String NO_PLAN_CODE_ERROR_MSG = "产品方案编号不能为空！";
    public static final String PLAN_CODE_MATCH_ERROR_MSG = "产品方案编号匹配有误！";

    public static final String NO_LEASE_TERM_ERROR_MSG = "租赁期限不能为空！";

    public static final String NO_DOWN_PAYMENT_ERROR_MSG = "首付金额不能为空！";
    public static final String NO_DOWN_PAYMENT_RATIO_ERROR_MSG = "首付比例不能为空！";
    public static final String NO_DOWN_PAYMENT_METHOD_ERROR_MSG = "首付款推算方式不能为空！";
    public static final String NO_DOWN_PAYMENT_METHOD_MATCH_ERROR_MSG = "首付款推算方式匹配有误！";

    public static final String NO_DEPOSIT_ERROR_MSG = "保证金金额不能为空！";
    public static final String NO_DEPOSIT_RATIO_ERROR_MSG = "保证金比例不能为空！";
    public static final String NO_DEPOSIT_METHOD_ERROR_MSG = "保证金推算方式不能为空！";
    public static final String NO_DEPOSIT_METHOD_MATCH_ERROR_MSG = "保证金推算方式匹配有误！";

    public static final String NO_INT_RATE_ERROR_MSG = "年利率不能为空！";
    public static final String NO_LEASE_ITEM_AMOUNT_ERROR_MSG = "租赁物总价款不能为空！";
    public static final String NO_PRICE_TYPE_ERROR_MSG = "是否包牌价不能为空！";
    public static final String NO_PURCHASE_TAX_ERROR_MSG = "购置税不能为空！";
    public static final String NO_INSURANCE_PREMIUM_ERROR_MSG = "保险费不能为空！";

    public static final String LEASE_ITEM_MATCH_ERROR_MSG = "租赁物品牌相关信息不能为空！";
    public static final String NO_LEASE_ITEM_ID_ERROR_MSG = "租赁物ID不能为空！";

    public static final String BRAND_MATCH_ERROR_MSG = "租赁物品牌匹配有误！";
    public static final String SERIES_MATCH_ERROR_MSG = "租赁物车系匹配有误！";
    public static final String MODEL_MATCH_ERROR_MSG = "租赁物车型匹配有误！";

    public static final String NO_BP_CATEGORY_ERROR_MSG = "商业伙伴类别不能为空！";
    public static final String BP_CATEGORY_MATCH_ERROR_MSG = "商业伙伴类别匹配有误！";

    public static final String NO_BP_NAME_ERROR_MSG = "商业伙伴名称不能为空！";

    public static final String NO_BP_CLASS_ERROR_MSG = "商业伙伴分类不能为空！";
    public static final String BP_CLASS_MATCH_ERROR_MSG = "商业伙伴分类匹配有误！";

    public static final String NO_REGNOTYPE_ERROR_MSG = "登记注册号类型不能为空！";
    public static final String REGNOTYPE_MATCH_ERROR_MSG = "登记注册号类型匹配有误！";

    public static final String NO_REGNO_ERROR_MSG = "法人登记注册号码不能为空！";

    public static final String NO_ID_TYPE_ERROR_MSG = "证件类型不能为空！";
    public static final String ID_TYPE_MATCH_ERROR_MSG = "证件类型匹配有误！";
    public static final String NO_ID_CARD_NO_ERROR_MSG = "证件号码不能为空！";

    public static final String MARITAL_STATUS_MATCH_ERROR_MSG = "婚姻状况匹配有误！";

    public static final String GUARANTEE_TYPE_MATCH_ERROR_MSG = "担保类型匹配有误！";

    public static final String ZX_MEMBERTYPE_MATCH_ERROR_MSG = "与承租人关系匹配有误！";

    public static final String ID_TYPE_BP_MATCH_ERROR_MSG = "配偶证件类型匹配有误！";
    public static final String GENDER_MATCH_ERROR_MSG = "性别匹配有误！";
    public static final String GENDER_BP_MATCH_ERROR_MSG = "配偶性别匹配有误！";
    public static final String NO_GENDER_ERROR_MSG = "性别不能为空！";
    public static final String NO_SP_GENDER_ERROR_MSG = "性别不能为空！";

    public static final String NP_ID_CARD_ERROR_MSG = "身份证号格式有误!";
    public static final String ID_CARD_ERROR_MSG = "配偶身份证号格式有误!";

    public static final String NO_ADDRESS_TYPE_ERROR_MSG = "地址类型不能为空!";
    public static final String ADDRESS_TYPE_MATCH_ERROR_MSG = "地址类型匹配有误!";
    public static final String ADDRESS_TYPE_REPEATED = "地址类型重复";

    public static final String NO_COUNTRY_CODE_ERROR_MSG = "地址信息国家不能为空!";
    public static final String COUNTRY_CODE_MATCH_ERROR_MSG = "地址信息国家匹配有误!";

    public static final String NO_PROVINCE_CODE_ERROR_MSG = "地址信息省份不能为空!";
    public static final String PROVINCE_CODE_MATCH_ERROR_MSG = "地址信息省份匹配有误!";

    public static final String NO_CITY_CODE_ERROR_MSG = "地址信息市不能为空!";
    public static final String CITY_CODE_MATCH_ERROR_MSG = "地址信息市匹配有误!";
//
//    public static final String NO_DISTRICT_CODE_ERROR_MSG = "安装地址信息区不能为空!";
//    public static final String DISTRICT_CODE_MATCH_ERROR_MSG = "安装地址信息区匹配有误!";

    public static final String NO_ADDRESS_ERROR_MSG = "地址信息详细地址不能为空!";

    public static final String PARTNER_USER_MATCH_ERROR_MSG = "系统无合作方关联用户，请联系管理员!";

    public static final String BANK_ACCOUNT_MATCH_MORE_THAN_ONE_ERROR_MSG = "经销商银行信息匹配有误，请联系管理员!";

    public static final String APPLICATION_ERROR_MSG = "程序异常，请联系管理员！";

    public static final String MORE_TENANT_ERROR_MSG = "只能存在一个承租人！";
    public static final String ONE_TENANT_ERROR_MSG = "必须维护一个承租人！";

    public static final String MORE_PARTNER_MATCH_ERROR_MSG = "合作方匹配到多个用户！";
    public static final String NO_PARTNER_MATCH_ERROR_MSG = "合作方未定义相关用户！";

    public static final String MORE_LEASE_ITEM_ID_ERROR_MSG = "租赁物ID重复！";

    public static final String NO_ATTACHMENT_ERROR_MSG = "附件列表不能为空！";

    public static final String LEASE_START_DATE_ERROR_MSG = "投放日日期格式有误！";
    public static final String FOUNDED_DATE_ERROR_MSG = "注册时间日期格式有误！";

    public static final String NO_BP_NAME_SP_ERROR_MSG = "已婚，配偶姓名不能为空！";
    public static final String NO_ID_TYPE_SP_ERROR_MSG = "已婚，配偶证件类型不能为空！";
    public static final String NO_ID_CARD_SP_ERROR_MSG = "已婚，配偶证件号不能为空！";
    public static final String BP_INFO_SP_ERROR_MSG = "已录入配偶信息，请核实基本信息中婚姻状况录入是否准确！";
    public static final String NO_ACADEMIC_BACKGROUND_ERROR_MSG = "学历不能为空！";
    public static final String ACADEMIC_BACKGROUND_ERROR_MSG = "学历匹配有误!";
    public static final String NO_ACADEMIC_DEGREE_ERROR_MSG = "学位不能为空！";
    public static final String ACADEMIC_DEGREE_ERROR_MSG = "学位匹配有误!";
    public static final String NO_EMPLOYMENT_STATUS_ERROR_MSG = "就业状况不能为空！";
    public static final String EMPLOYMENT_STATUS_ERROR_MSG = "就业状况匹配有误!";
    public static final String NO_WORKING_PLACE_ERROR_MSG = "工作单位不能为空！";
    public static final String NO_JOB_NATURE_ERROR_MSG = "单位性质不能为空！";
    public static final String JOB_NATURE_ERROR_MSG = "单位性质匹配有误!";
    public static final String NO_INDUSTRY_ERROR_MSG = "行业不能为空！";
    public static final String INDUSTRY_ERROR_MSG = "行业匹配有误!";
    public static final String NO_PROFESSION_ERROR_MSG = "职业不能为空！";
    public static final String PROFESSION_ERROR_MSG = "职业匹配有误!";
    public static final String NO_POSITION_ERROR_MSG = "职务不能为空！";
    public static final String POSITION_ERROR_MSG = "职务匹配有误!";
    public static final String NO_JOB_TITLE_ERROR_MSG = "职称不能为空！";
    public static final String JOB_TITLE_ERROR_MSG = "职称匹配有误!";
    public static final String NO_HOUSE_PROPERTY_ERROR_MSG = "居住状况不能为空！";
    public static final String HOUSE_PROPERTY_ERROR_MSG = "居住状况匹配有误!";
    public static final String NO_NATIONALITY_ERROR_MSG = "国籍不能为空!";
    public static final String NATIONALITY_ERROR_MSG = "国籍匹配有误!";
    public static final String BP_CATEGORY_NOT_TENANT_ERROR_MSG = "商业伙伴类别只能为承租人！";
    public static final String BP_CLASS_NOT_NP_ERROR_MSG = "承租人类型只能为自然人！";
    public static final String NO_CELL_PHONE_ERROR_MSG = "手机号不能为空！";
    public static final String CELL_PHONE_STYLE_ERROR_MSG = "手机号格式错误！";
    public static final String BP_CELL_PHONE_STYLE_ERROR_MSG = "承租人手机号格式错误！";
    public static final String NO_ADDRESS_CELL_PHONE_ERROR_MSG = "地址信息手机号不能为空！";
    public static final String NO_ADDRESS_PHONE_ERROR_MSG = "地址信息电话不能为空！";
    public static final String ADDRESS_CELL_PHONE_STYLE_ERROR_MSG = "地址信息手机号格式错误！";
    public static final String NO_AGE_ERROR_MSG = "承租人年龄不能为空！";
    public static final String BP_AGE_20_60_ERROR_MSG = "承租人年龄必须在20（包含）到60（包含）之间！";
    public static final String BP_AGE_20_57_ERROR_MSG = "承租人年龄必须在20（包含）到57（包含）之间！";
    public static final String BP_AGE_18_60_ERROR_MSG = "承租人年龄必须在18（包含）到60（包含）之间！";
    public static final String BP_COUNTRY_NO_CHINA_ERROR_MSG = "承租人地址国家只能为中国！";
    public static final String NO_NECESSARY_ADDRESS_ERROR_MSG = "身份证地址和租赁物地址必传其一";
    public static final String NO_LEASEITEM_ADDRESS_ERROR_MSG = "租赁物地址必传";
    public static final String PROJECT_ACCOUNT_TSS_BANK_NUMBER_ERROR_MSG = "账号信息联行号[%s]，越秀无此联行号，请联系越秀管理员进行添加";
    public static final String NO_PROJECT_ACCOUNT_TYPE_ERROR_MSG = "进件银行信息，账号类型不能为空";
    public static final String NO_PROJECT_ACCOUNT_NUMBER_ERROR_MSG = "进件银行信息，账号不能为空";
    public static final String NO_PROJECT_ACCOUNT_NAME_ERROR_MSG = "进件银行信息，账号名称不能为空";
    public static final String BP_NAME_SPECIAL_CHARACTERS_ERROR_MSG = "商业伙伴名称不能包含特殊字符！";
    public static final String NO_SPOUSE_CELL_PHONE_ERROR_MSG = "配偶联系方式不能为空！";
    public static final String SPOUSE_CELL_PHONE_STYLE_ERROR_MSG = "配偶联系方式格式错误！";

    /**
     * 经销商同步返回信息
     */



    /**
     * 常量
     */
    public static final Long NUMBER_ONE = 1L;
    public static final Long NUMBER_ZERO = 0L;
    public static final Long NUMBER_FOURTY_SIX = 46L;
    public static final String NUMBER_TWENTY = "20";
    public static final String NUMBER_TEN = "10";
    public static final String HLS_BP_MASTER = "HLS_BP_MASTER";
    public static final String NEW = "NEW";
    public static final String ID_CARD = "ID_CARD";
    public static final String YES_CODE = "Y";
    public static final String NO_CODE = "N";
    public static final String NOT = "NOT";
    public static final String DEALER = "DEALER";
    public static final String NUMBER_FORTY = "40";

    /**
     * 狮桥
     */
    public static final String SQ = "YX-SQ";
    /**
     *天合
     */
    public static final String TH = "YX-TH";
    /**
     *碳银
     */
    public static final String TY = "YX-TY";
    /**
     *三一
     */
    public static final String SY = "YX-SY";
    /**
     *晴天
     */
    public static final String QT = "YX-QT";
    /*
    * 广东省编码
    */
    public static final String GD_CODE = "440000";

}
