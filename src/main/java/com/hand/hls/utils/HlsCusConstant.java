package com.hand.hls.utils;

import java.text.SimpleDateFormat;

/**
 * @title:常量类
 * @description:用于本项目所有涉及到的常量
 * @notice:注意在每一次新增常量类的时候，先看看当前有没有，不要定义重复了，谢谢！--------------------------------这是一个很长的备注。
 * @author:tengfei
 * @date:2018/10/12
 * hls/core/utils/HlsCusConstant.java
 */

public class HlsCusConstant {
    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private HlsCusConstant() {
    }

    /**
     * -----授信立项模块--------
     */
    public static interface CREDIT_LINE_CHANCE {
        /**
         * 单据类型
         */
        String DOCUMENT_TYPE = "REVOLVING";
        /**
         * 单据类别
         */
        String DOCUMENT_CATEGORY = "HLS_CREDIT_LINE";
        /**
         * 业务模式
         */
        String BUSINESS_TYPE = "CREDIT_LINE";
    }

    /**
     * 授信编码的编码规则
     */
    public static final String CREDIT_LINE_NUMBER = "LON_CREDIT_CON_NUMBER";


    /**
     * 保理合同的合同编码规则
     */
    public static final String FCT_CONTRACT_CODE = "FCT_CONTRACT_CODE";

    /**
     * 订单的编码规则
     */
    public static final String ORDER_CODE = "ORDER_CODE";

    /**
     * 工作流状态
     */
    public static interface WORKFLOW_STATUS {
        /**
         * 新建
         */
        String NEW = "NEW";
        /**
         * 同意
         */
        String APPROVED = "APPROVED";
        /**
         * 不知道为啥写这个REJECT，先定义下再说
         */
        String REJECT = "REJECT";
        /**
         * 拒绝
         */
        String REJECTED = "REJECTED";
        /**
         * 审批退回
         */
        String APPROVED_RETURN = "APPROVED_RETURN";
        /**
         * 审批中
         */
        String APPROVING = "APPROVING";

        /**
         * 作废
         */
        String CANCEL = "CANCEL";

        /**
         * 确认
         */
        String CONFIRM = "CONFIRM";


        /**
         * 部分确认
         */
        String PART_CONFIRM = "PART_CONFIRM";


        /**
         * 暂挂
         */
        String PENDING = "PENDING";


        /**
         * 冻结
         */
        String BLOCK = "BLOCK";


        /**
         * 结束
         */
        String END = "END";
    }

    /**
     * 启动工作流使用的形参【公用的】
     */
    public interface WORKFLOW_PARAMS {
        String WORKFLOW_TYPE = "workFlowType";
        String PROCESS_DEFINITION_ID = "processDefinitionId";
        String START_USER_DESCRIPTION = "startUserDescription";
        String IREQUEST = "iRequest";
        String START_USER_NAME = "startUserName";
        String DOCUMENT_CATEGORY = "documentCategory";
        String DOCUMENT_TYPE = "documentType";
        String DOCUMENT_ID = "documentId";
        String PROJECT_DOCUMENT_CATEGORY = "projectDocumentCategory";
        String FCT_PROJECT = "FCT_PROJECT";
        String FCT_CONTRACT_CREATE="FCT_CONTRACT_CREATE";
        String BUSINESS_TYPE = "businessType";
        String FACTOR = "FACTOR";
        String EMPLOYEE_ASSISTANT_ASSIGNS_ID = "employeeAssistantAssignsId";
        String COMPANY_ID = "companyId";
        String ATT_TYPE = "attType";
        String P_NAME = "pName";
        String DOCUMENT_NAME = "documentName";
        String APPROVE_RESULT = "approveResult";
        String BUSINESS_KEY = "businessKey";
        String PROCESS_INSTANCE_ID = "processInstanceId";
    }

    /**
     * 右侧菜单栏权限控制【公用】
     */
    public interface MENU_ITEM {
        String TODO = "todo";
        String READ = "read";
        String PENDING = "pending";
        String UNDO = "undo";
        String HANDLE = "handle";
    }

    /**
     * 合同状态【公用】
     */
    public interface CONTRACT_STATUS {
        /**
         * 合同新建
         */
        String NEW = "NEW";
        /**
         * 合同结束
         * (需求修改,由TERMINATION改成END)
         */
        String TERMINATION = "END";
        /**
         * 合同待签约
         */
        String SIGNING = "SIGNING";
        /**
         * 合同起贷
         */
        String INCEPT = "INCEPT";
        /**
         * 合同签约
         */
        String SIGN = "SIGN";
        /**
         * 合同挂起
         */
        String PENDING = "PENDING";
        /**
         * 合同取消
         */
        String CANCEL="CANCEL";
        /**
         * 合同放款
         */
        String LOAN ="LOAN";
        /**
         * 合同审查通过
         */
        String APPROVED = "APPROVED";
    }

    /**
     * @name:工作流名称：FCT_CONTRACT_CREATE_WFL【保理合同制作审批流程】
     * @desc:该模块定义的是保理合同制作流程工作流中涉及到的所有常量
     */
    public static interface FCT_CONTRACT_CREATE_WFL {
        /**
         * 工作流启动设置唯一标识,建议在启动工作流的时候设置workFlowType的时候使用工作流编码
         */
         String FCT_CONTRACT_CREATE_WFL="FCT_CONTRACT_CREATE_WFL";

        /**
         * 目标命名空间
         */
        String FCT_CONTRACT_CREATE="FCT_CONTRACT_CREATE";

        /**
         * 工作流启动的时候发送系统消息，定义的参数
         */
        String SOURCE_MODEL = "FCT_CONTRACT";
        String EVENTCODE = "FCT_CONTRACT_CREATE_WFL";
        String RELATION_TYPE = "P2D";
    }

    /**
     * 合同签约的工作流 【FCT_CONTRACT_SIGN_WFL】
     */
    public interface FCT_CONTRACT_SIGN_WFL{
        /**
         * 工作流启动设置唯一标识,建议在启动工作流的时候设置workFlowType的时候使用工作流编码
         */
        String FCT_CONTRACT_SIGN_WFL="FCT_CONTRACT_SIGN_WFL";
        /**
         * 目标命名空间
         */
        String FCT_CONTRACT_SIGN="FCT_CONTRACT_SIGN";
    }


    /**
     * 现金流类型
     */
    public static interface CASHFLOW_ITEM {
        /**
         * 保理利息
         */
        Long FACTOR_INTEREST_CF_ITEM = 55L;
        Long FACTOR_INTEREST_CF_TYPE = 50L;
        String INTEREST_DESC="保理利息";

        /**
         * 保理本金流入
         */
        Long FACTOR_PRINCIPAL_CF_ITEM = 54L;
        Long FACTOR_PRINCIPAL_CF_TYPE = 50L;
        String PRINCIPAL_DESC="保理本金";

        /**
         * 保理首付款/保理本金投放
         */
        Long FACTOR_PAYMENT_CF_ITEM = 40L;
        Long FACTOR_PAYMENT_CF_TYPE = 40L;
        String PAYMENT_DESC="保理本金投放";

        /**
         * 保理手续费，咨询服务费
         */
        Long FACTOR_SERVICE_CHARGE_CF_ITEM = 57L;
        Long FACTOR_SERVICE_CHARGE_CF_TYPE = 50L;
        String SERVICE_DESC="保理咨询服务费";


        /**
         * 保证金
         */
        Long DEPOSIT_CF_ITEM = 51L;
        Long DEPOSIT_CF_TYPE = 5L;
        String DEPOSIT_DESC="保证金";

        /**
         * 保证金退还
         */
        Long DEPOSIT_RETURN_CF_ITEM = 52L;
        Long DEPOSIT_RETURN_CF_TYPE = 52L;
        String DEPOSIT_RETURN_DESC="保证金退还";

        /**
         * 罚息
         */
        Long INTEREST_PENALTY_CF_ITEM = 45L;
        Long INTEREST_PENALTY_CF_TYPE = 45L;
        String INTEREST_PENALTY_DESC="逾期使用费(保理)";
        /**
         * 提前结清-保理首付款使用费
         */
        Long DOWN_PAYMENT_CF_ITEM = 53L;
        Long DOWN_PAYMENT_CF_TYPE = 50L;
        String DOWN_PAYMENT_DESC="提前结清-保理首付款使用费";
        /**
         * 提前结清-提前结清合同终止金(租赁)
         */
        Long CONTRACT_ET_CF_ITEM = 11L;
        Long CONTRACT_ET_CF_TYPE = 11L;
        String CONTRACT_ET_CF_ITEM_DESC = "提前结清-提前结清合同终止金";

        /**
         * 融资-还款本金
         */
        Long LON_PRINCIPAL_CF_ITEM = 301L;
        Long LON_PRINCIPAL_CF_TYPE = 70L;
        String LON_PRINCIPAL_DESC="融资-还款本金";


        /**
         * 融资-还款利息
         */
        Long LON_INTEREST_CF_ITEM = 302L;
        Long LON_INTEREST_CF_TYPE = 70L;
        String LON_INTEREST_DESC="融资-还款利息";


        /**
         * 融资-服务费
         */
        Long LON_SERVICE_CF_ITEM = 310L;
        Long LON_SERVICE_CF_TYPE = 70L;
        String LON_SERVICE_DESC="融资-服务费";


        /**
         * 归集
         */
        Long COLLECTION_CF_ITEM = 201L;
        Long COLLECTION_CF_TYPE = 20L;
        String COLLECTION_DESC="归集";


        /**
         * 转付
         */
        Long REMITTANCE_CF_ITEM = 202L;
        Long REMITTANCE_CF_TYPE = 20L;
        String REMITTANCE_DESC="转付";


        Long LEASE_DUE_AMOUNT_CF_ITEM = 1L;
        Long LEASE_DUE_AMOUNT_CF_TYPE = 1L;
        String LEASE_DUE_AMOUNT_DESC = "租金";


        Long LEASE_ITEM_CF_ITEM = 0L;
        Long LEASE_ITEM_CF_TYPE = 0L;
        String LEASE_ITEM_DESC = "设备款";

        Long LEASE_DOWN_PAYMENT_CF_ITEM = 2L;
        Long LEASE_DOWN_PAYMENT_CF_TYPE = 2L;
        String LEASE_DOWN_PAYMENT_DESC = "首付款";

        Long LEASE_LEASE_CHARGE_CF_ITEM = 3L;
        Long LEASE_LEASE_CHARGE_CF_TYPE = 3L;
        String LEASE_LEASE_CHARGE_DESC = "手续费";

        Long LEASE_DEPOSIT_CF_ITEM = 51L;
        Long LEASE_DEPOSIT_CF_TYPE = 5L;
        String LEASE_DEPOSIT_DESC = "保证金";

        Long LEASE_DEPOSIT_RETURN_CF_ITEM = 52L;
        Long LEASE_DEPOSIT_RETURN_CF_TYPE = 52L;
        String LEASE_DEPOSIT_RETURN_DESC = "保证金退还";


        Long LEASE_ADVICE_CF_ITEM = 4L;
        Long LEASE_ADVICE_CF_TYPE = 4L;
        String LEASE_ADVICE_DESC = "咨询服务费";


        Long LEASE_RESIDUAL_VALUE_CF_ITEM = 8L;
        Long LEASE_RESIDUAL_VALUE_CF_TYPE = 8L;
        String LEASE_RESIDUAL_VALUE__DESC = "留购价";
    }

    /**
     * 保理合同放款申请提交 工作流【INCEPT_ACTUAL_DELIVERY_WFL】
     */
    public interface INCEPT_ACTUAL_DELIVERY_WFL{
        /**
         * 流程标识
         */
        String GDXF_INCEPT_ACTUAL_DELIVERY_WFL="GDXF_INCEPT_ACTUAL_DELIVERY_WFL";
        /**
         * 命名空间
         */
        String GDXF_INCEPT_ACTUAL_DELIVERY="GDXF_INCEPT_ACTUAL_DELIVERY";
    }

    /**
     * 还款计划的现金流方向
     */
    public static final String INFLOW="INFLOW";
    public static final String INFLOW_DESC="流入";
    public static final String OUTFLOW="OUTFLOW";
    public static final String OUTFLOW_DESC="流出";

    public interface DATA_STATUS{
        String STATUS_ADD = "add";
        String STATUS_UPDATE = "update";
    }

    /**
     * 光大幸福公司
     */
    public interface COMPANY_TYPE {
        Long FACTOR = 264L;
        Long LEASE = 261L;
    }

    /**
     * ***************************************************************************定义快码code***************************
     */
    public static interface SYS_CODE{
        /**
         * 项目评审附件的资料清单
         */
        String PRJ_LIST_INFORMATION="PRJ_LIST_INFORMATION";
    }

    /**
     * 核销状态
     */
    public static interface FCT_WRITE_OFF_FLAG{
        /**
         * NA 不需核销  	FULL 完全核销  PARTIAL 部分核销  NOT  未核销
         */

        String NA="NA";
        String FULL="FULL";
        String PARTIAL="PARTIAL";
        String NOT="NOT";
    }
    /**
     * 保证金付款方式
     */
    public static interface PRJ_MARGIN_PAYMENT_METHOD{
        /**
         * PERIOD_FINAL_RETURN  	不抵扣
         *  PERIOD_FINAL_DEDUCTIBLE  	抵扣
         */

        String PERIOD_FINAL_RETURN="PERIOD_FINAL_RETURN";
        String PERIOD_FINAL_DEDUCTIBLE="PERIOD_FINAL_DEDUCTIBLE";
    }
    /**
     * 测算类型
     */
    public interface MEASUREMENT_TYPE{
         String HAND_ESTIMATION="HAND_ESTIMATION";
         String SYSTEM_ESTIMATION="SYSTEM_ESTIMATION";
    }

    /**
     * 融资提款-计息方式 code[LON.INTEREST_CALC_METHOD]
     */
    public interface LON_INTEREST_CALC_METHOD {
        /**
         * 当期还本还息
         */
        String REPAYMENT_INTERES_CURRENT_PERIOD="REPAYMENT_INTERES_CURRENT_PERIOD";
        /**
         * 利随本清
         */
        String ONCE_CLEAR="ONCE_CLEAR";
        /**
         * 剩余本金法
         */
        String RESIDUAL_OF_PRINCIPAL="RESIDUAL_OF_PRINCIPAL";
        /**
         * 一次性还本
         */
        String ONCE_REPAYMENT="ONCE_REPAYMENT";
    }
    /************************************************************定义快码结束********************************************/

    public static interface LON_REPAYMENT{
        /**
         * 融资-还款本金
         */
        Long PRINCIPAL_CF_ITEM=301L;
        String PRINCIPAL_CF_ITEM_DESC="融资-还款本金";

        /**
         * 融资-还款利息
         */
        Long INTEREST_CF_ITEM=302L;
        String INTEREST_CF_ITEM_DESC="融资-还款利息";
    }


    /**
     * 项目附件表  prj_project_attachment的PROJECT_ATTACHMENT_CATEGORY字段维护，每一个模块的编码都是不一样的。
     */

    public static interface PROJECT_ATTACHMENT_CATEGORY{
        /**
         * 项目评审的附件类型
         */
        String PROJECT_REVIEW="FCT_PROJECT_REVIEW";
    }

    /**
     * 放款申请行相关常量
     */
    public static interface CSH_PAYMENT_REQ_LN{
        /**
         * SOURCE_DOC_CATEGORY  付款来源单据类别
         */
        String SOURCE_DOC_CATEGORY="FCT_CONTRACT";
    }

    /**
     * 放款申请头相关常量
     */
    public static interface CSH_PAYMENT_REQ_HD{
        /**
         * DOCUMENT_CATEGORY
         */
        String CONFIRM_DOCUMENT_CATEGORY="CSH_PAYMENT_CONFIRM_REQ";
    }

    /**
     * 还款计划的常量
     */
    public interface CASHFLOW{
        /**
         * 保理本金
         */
        String PRINCIPLE_FLAG ="principleFlag";
        /**
         * 保理利息
         */
        String INTEREST_FLAG ="interestFlag";
        /**
         * 咨询服务费
         */
        String SERVICE_FLAG ="serviceFlag";

    }

    /**
     * 合同文本清单的编码
     * FCT_F 正向保理前缀
     * FCT_RF 反向保理前缀
     */
    public interface contractText{
        /**
         * 公司前缀
         * 光大幸福租赁 EBFIL   261
         * 光大幸福保理 EBFIF   264
         */

        String EBFIL = "EBFIL";

        String EBFIF = "EBFIF";

        /**
         * 正向保理类型
         */
        String FACTORING = "FACTORING";
        /**
         * 反向保理类型
         */
        String REVERSE_FACTORING = "REVERSE_FACTORING";

        /**
         * 保理合同：EBFIF-年-三位流水号-BL-01，按照系统生成合同的顺序显示流水号；
         * B 后缀为批量合同
         */
        String FCT_F_CONTRACT = "FCT_F_CONTRACT";
        String FCT_RF_CONTRACT = "FCT_RF_CONTRACT";
        String FCT_F_CONTRACT_B = "FCT_F_CONTRACT_B";
        String FCT_RF_CONTRACT_B = "FCT_RF_CONTRACT_B";

        /**
         * 保证合同：EBFIF-年-主合同流水 -BZ-01，光大保理前缀为EBFIF，光大幸福前缀为EBFIL
         * S  原债权人  |  B    债务人
         * NP 自然人    |  ORG  法人
         * N  无配偶    |  C    配偶不提供担保仅确认  |  G  夫妻双方提供担保
         */
        String FCT_F_GUARANTEE_CON_S_ORG = "FCT_F_GUARANTEE_CON_S_ORG";
        String FCT_F_GUARANTEE_CON_S_NP_N = "FCT_F_GUARANTEE_CON_S_NP_N";
        String FCT_F_GUARANTEE_CON_S_NP_C = "FCT_F_GUARANTEE_CON_S_NP_C";
        String FCT_F_GUARANTEE_CON_S_NP_G = "FCT_F_GUARANTEE_CON_S_NP_G";
        String FCT_F_GUARANTEE_CON_B_ORG = "FCT_F_GUARANTEE_CON_B_ORG";
        String FCT_F_GUARANTEE_CON_B_NP_N = "FCT_F_GUARANTEE_CON_B_NP_N";
        String FCT_F_GUARANTEE_CON_B_NP_C = "FCT_F_GUARANTEE_CON_B_NP_C";
        String FCT_F_GUARANTEE_CON_B_NP_G = "FCT_F_GUARANTEE_CON_B_NP_G";
        String FCT_RF_GUARANTEE_CON_S_ORG = "FCT_RF_GUARANTEE_CON_S_ORG";
        String FCT_RF_GUARANTEE_CON_S_NP_N = "FCT_RF_GUARANTEE_CON_S_NP_N";
        String FCT_RF_GUARANTEE_CON_S_NP_C = "FCT_RF_GUARANTEE_CON_S_NP_C";
        String FCT_RF_GUARANTEE_CON_S_NP_G = "FCT_RF_GUARANTEE_CON_S_NP_G";
        String FCT_RF_GUARANTEE_CON_B_ORG = "FCT_RF_GUARANTEE_CON_B_ORG";
        String FCT_RF_GUARANTEE_CON_B_NP_N = "FCT_RF_GUARANTEE_CON_B_NP_N";
        String FCT_RF_GUARANTEE_CON_B_NP_C = "FCT_RF_GUARANTEE_CON_B_NP_C";
        String FCT_RF_GUARANTEE_CON_B_NP_G = "FCT_RF_GUARANTEE_CON_B_NP_G";

        /**
         * 应收账款转让申请书（样张）：同主合同编号；
         */


        /**
         * 债权转让通知书（样张）：同主合同编号；
         */
        /**
         * 应收账款回款计划表：同主合同编号；
         */

        /**
         * 应收账款质押登记协议（人行登记用，应收账款质押相关合同）
         */
        String FCT_RECEIVABLE_CHANGE = "FCT_RECEIVABLE_CHANGE";

        /**
         * 应收账款质押合同：EBFIF-年-主合同流水 -YSZY-01；
         */
        String FCT_PLEDGE_CONTRACT = "FCT_PLEDGE_CONTRACT";

        /**
         * 咨询服务合同：EBFIF-年-主合同流水 -ZX-01；
         */
        String FCT_SERVICE_CONTRACT = "FCT_SERVICE_CONTRACT";

        /**
         * 应收账款转让登记协议（人行 登记用，商业保理相关合同）：EBFIF-年-主合同流水 -YSZR-01；
         */
        String FCT_ACCOUNT_RECEIVABLE = "FCT_ACCOUNT_RECEIVABLE";

        /**
         * 自定义合同文本ID和编码
         */
        String CUSTOM_FCT_CONTRACT = "CUSTOM_FCT_CONTRACT";
        Long CUSTOM_CONTRACT_ID = 148L;

        /**
         * 租赁合同文本编码
         * 融资租赁合同（售后回租） CON_CONTRACT_LEASEBACK--HZ
         * 融资租赁合同（直租） CON_CONTRACT_LEASE --ZZ
         * 经营性租赁合同  CON_OPERATIONAL_LEASE--JY
         * 购买合同  CON_CONTRACT_PURCHASE--ZZM
         * 保证合同  GUARANTEE_CON--BZ
         * 动产抵押合同  CON_CONTRACT_MORTGAGE--DY
         * 咨询服务合同  CON_SERVICE_CONTRACT--ZX
         * 应收账款质押登记协议  CON_RECEIVABLE_CHANGE--ZY
         * 土地使用权抵押合同  CON_LAND_MORTGAGE--DY
         * 保证合同（自然人单人担保） GUARANTEE_NATURAL_PERSON--BZ
         * 保证合同（自然人单人担保+配偶确认）GUARANTEE_NATURAL_PERSON_GD--BZ
         * 保证合同（自然人夫妇提供担保）    GUARANTEE_NATURAL_COUPLES--BZ
         */
        String CON_CONTRACT_LEASEBACK = "CON_CONTRACT_LEASEBACK";
        String CON_CONTRACT_LEASE = "CON_CONTRACT_LEASE";
        String CON_OPERATIONAL_LEASE = "CON_OPERATIONAL_LEASE";
        String CON_CONTRACT_PURCHASE = "CON_CONTRACT_PURCHASE";
        String GUARANTEE_CON = "GUARANTEE_CON";
        String CON_CONTRACT_MORTGAGE = "CON_CONTRACT_MORTGAGE";
        String CON_SERVICE_CONTRACT = "CON_SERVICE_CONTRACT";
        String CON_RECEIVABLE_CHANGE = "CON_RECEIVABLE_CHANGE";
        String CON_LAND_MORTGAGE = "CON_LAND_MORTGAGE";
        String CON_RECEIVABLE_PIEDGE = "CON_RECEIVABLE_PIEDGE";
        String GUARANTEE_NATURAL_PERSON = "GUARANTEE_NATURAL_PERSON";
        String GUARANTEE_NATURAL_PERSON_GD = "GUARANTEE_NATURAL_PERSON_GD";
        String GUARANTEE_NATURAL_COUPLES = "GUARANTEE_NATURAL_COUPLES";
    }

    /**
     * 商业伙伴类型
     */
    public interface BP_ROLE_TYPE{
        /**
         * 保证人
         */
        String WARRANTOR="WARRANTOR";
        /**
         * 抵押人
         */
        String MORTGAGOR="MORTGAGOR";
        /**
         * 质押人
         */
        String PLEDGOR="PLEDGOR";
        /**
         * 买方
         */
        String BUYER="BUYER";
        /**
         * 卖方
         */
        String SELLER="SELLER";
    }

    /**
     * 项目附件类型
     */
    public interface projectSourceType{
        /**
         * 风险评估报告
         */
        String FCT_PROJECT_RISK_REPORT = "FCT_PROJECT_RISK_REPORT";
    }

    /**
     * 项目附件清单类型
     */
    public interface projectText{
        /**
         * 审批通知书(保理/租赁)
         */
        String FCT_APPROVAL_NOTICE_REPORT = "FCT_APPROVAL_NOTICE_REPORT";
        String PRJ_APPROVAL_NOTICE_REPORT = "PRJ_APPROVAL_NOTICE_REPORT";

    }

    /**
     * 财报excel文本的sheet页名称
     */
    public interface FinancialReportSheet{
        /**
         * 审批通知书
         */
        String BASIC_FINANCE_INFO = "客户财报基本信息";
        String BALANCE_SHEET = "资产负债表";
        String PROFIT_STATEMENT = "利润表";
        String CASH_FLOW_STATEMENT = "现金流量表";
        String FINANCIAL_INDEX = "补充资料表";
        String FINANCIAL_INDICATOR = "财务指标";

    }

    public interface POSITION_NAME {
        String PROJECT_MANAGER = "projectManager";
        String PROJECT_MANAGER_DESC = "项目经理岗";
        //Capital duty
        String CAPITAL_DUTY = "capitalDuty";
        String CAPITAL_DUTY_DESC = "资金岗";
    }

    /**
     * 是或否的静态标记
     */
    public interface FLAG {
        String Y = "Y";
        String N = "N";
        String ALL = "ALL";
        String NULL = "NULL";
    }


    /**
     * 拦截变量名称
     */
    public interface INTER_VAR_NAME {
        String BP_MASTER_CHANGE="bpMaterChangeFlag";
        String RULE_CONTROLLER="wflRuleControlFlag";
    }


    public interface DATA_CLASS {

        String NORMAL="NORMAL";

        String CHANGE="CHANGE";

        String CHANGE_REQ="CHANGE_REQ";

        String HISTORY="HISTORY";
    }

    public interface TRANSFER_BUSINESS_TYPE {
        String FUND_TYPE="FUND";
        String FUND_TRANSFER="FUND_TRANSFER";

        String FINANCE_TYPE="FINANCE";

        String GAP_TYPE="GAP";
    }

    public interface TRANSFER_LIST_CATEGORY {
        String LON_CONTRACT_REPAYMENT="LON_CONTRACT_REPAYMENT";
        String CT_ABS_PRODUCT_REPAYMENT="CT_ABS_PRODUCT_REPAYMENT";
        String CT_ABS_PRODUCT_FEE="CT_ABS_PRODUCT_FEE";
        String CT_ABS_PRODUCT_COLLECTION="CT_ABS_PRODUCT_COLLECTION";
        String CT_ABS_PRODUCT_REMITTANCE="CT_ABS_PRODUCT_REMITTANCE";
        String CT_ABS_PROJECT_FEE="CT_ABS_PROJECT_FEE";
        String CT_ABS_PRODUCT_BUYBACK="CT_ABS_PRODUCT_BUYBACK";
    }

    public interface TRANSFER_TYPE {
        /**
         * 还款
         */
        String PAY="PAY";
        /**
         * 调拨
         */
        String TRANSFER="TRANSFER";
    }

    //账户来源
    public interface ACCOUNT_SOURCE {
        /**
         * 对方账户
         */
        String RECIPROCAL_ACCOUNT="RECIPROCAL_ACCOUNT";

        /**
         * 本方账户
         */
        String OUR_ACCOUNT="OUR_ACCOUNT";

    }

    public interface TRANSFER_PURPOSE {
        String TRANSFER ="INTERNAL_FUND_ACCOUNT_TRANSFER";

    }


    public interface TRANSFER_WFL{

        /**
         * 目标命名空间
         */
        String FUND_TRANSFER="FUND_TRANSFER";


        String FUND_TRANSFER_CHANGE="FUND_TRANSFER_CHANGE";


        /**
         * 资金发起调拨工作流
         */
        String FUND_TRANSFER_WFL="TREASURY_DEPARTMENT_FUND_TRANSFER_WFL";


        /**
         * 财务发起调拨工作流
         */
        String FINANCE_TRANSFER_WFL="FINANCE_DEPARTMENT_FUND_TRANSFER_WFL";



        /**
         * 资金发起调拨变更
         */
        String FUND_TRANSFER_CHANGE_WFL="FINANCE_DEPARTMENT_FUND_TRANSFER_CHANGE_WFL";
    }


    public interface ABS_WFL{

        String PROJECT_WFL="ABS_WFL";


        String BUYBACK_WFL="BOND_ISSUE_CLEARANCE_REPURCHASE_WFL";

        /**
         * 资产变更
         */
        String ASSEET_CHANGE_WFL="BOND_ISSUE_ASSET_CHANGE_WFL";

        /**
         * ABS ABN
         */
        String ABS_ABN_WFL="BOND_ISSUE_PRODUCT_ESTABLISHMENT_A_WFL";

        /**
         * 其他发债
         */
        String OTHER_DEBT_WFL="BOND_ISSUE_PRODUCT_ESTABLISHMENT_B_WFL";

        /**
         * ABS ABN变更
         */
        String ABS_ABN_CHANGE_WFL="BOND_ISSUE_PRODUCT_CHANGE_WFL";

        /**
         * 其他发债变更
         */
        String OTHER_DEBT_CHANGE_WFL="BOND_ISSUE_FUND_PLAN_CHANGE_WFL";
    }

    public interface ABS_PRO_TYPE{

        String PROJECT="ABS_PROJECT";

        String PRODUCT="ABS_PRODUCT";

    }

    public interface ABS_CHANGE_TYPE{

        /**
         * 产品变更
         */
        String PRODUCT="PRODUCT";

        String ASSET="ASSET";

        String PREVFINISH="PREVFINISH";

        String CONTRACT="CONTRACT";
    }


    public interface ABS_BUSINESS_TYPE{

        String ABS="ABS";

        String ABN="ABN";
    }

    public interface ABS_FEE_TYPE{

        String VAT="VAT";

        /**
         * 服务费
         */
        String SERVICE_FEE="SERVICE_FEE";

        /**
         *托管费
         */
        String HOSTING_FEE="HOSTING_FEE";

        /**
         * 管理费
         */
        String MANAGEMENT_FEE="MANAGEMENT_FEE";

        /**
         * 承销费
         */
        String UNDERWRITING_FEE="UNDERWRITING_FEE";

        /**
         * 评级费
         */
        String RATING_FEE="RATING_FEE";

        /**
         * 评估费
         */
        String EVALUATION_FEE="EVALUATION_FEE";

        /**
         * 挂牌费
         */
        String LISTING_FEE="LISTING_FEE";

        /**
         * 律师费
         */
        String LAWYER_FEE="LAWYER_FEE";

        /**
         * 会计师费
         */
        String ACCOUNTANT_FEE="ACCOUNTANT_FEE";

        /**
         * 其他
         */
        String OTHER="OTHER";

    }


    public interface CON_ATTACHMENT {

        /**
         * 罚息
         */
        String FINE="CON_CONTRACT_CSH_ATT";
    }

    public interface ABS_RELEASE_TYPE{

        String REPAYMENT="CT_ABS_PRODUCT_REPAYMENT";

        String COLLECTION="CT_ABS_PRODUCT_COLLECTION";
    }


    public interface ABS_ASSET_TYPE{

        /**
         * 初始资产
         */
        String INITIAL_ASSET="INITIAL_ASSET";

        /**
         * 原资产调整
         */
        String ASSET_ADJUSTMENT="ORIGINAL_ASSET_ADJUSTMENT";

        /**
         * 新增资产
         */
        String NEW_ASSET="NEW_ASSET";

        /**
         * 替换资产
         */
        String REPLACEMENT_ASSET="REPLACEMENT_ASSET";
    }

    public static interface YES_NO {
        String Y = "Y";
        String N = "N";
        String YES = "YES";
        String NO = "NO";
        String NOT = "NOT";
        String ONE = "1";
        String ZERO = "0";
    }

    public static interface WFL {
        String NEW = "NEW";
        String APPROVING = "APPROVING";
        String APPROVED = "APPROVED";
        String REJECTED = "REJECTED";
        String REJECT = "REJECT";
        String RETURN = "RETURN";
        String CANCEL = "CANCEL";
        String PENDING = "PENDING";
        String NEW_CN = "新建";
        String APPROVING_CN = "审批中";
        String APPROVED_CN = "同意";
        String REJECTED_CN = "拒绝";
        String REJECT_CN = "拒绝";
        String RETURN_CN = "退回";
        String CANCEL_CN = "取消";
        String PENDING_CN = "暂挂";
    }

    /**
     * DC51 应付报账接口回传 code
     */
    public interface DC51_RETURN_CODE{
        String ERROR_JSON="ERROR_JSON";
        String ERROR_USER="ERROR_USER";
        String ERROR_TYPE="ERROR_TYPE";
        String ERROR_COMPANY="ERROR_COMPANY";
        String ERROR_ACCESS="ERROR_ACCESS";
        String ERROR_VAL="ERROR_VAL";
        String OK_VAL="OK_VAL";
        String OK_IMPORT="OK_IMPORT";
    }

    /**
     * YH007 核销后流水匹配 code
     */
    public interface YH007_RETURN_CODE{
        String success="0";
    }
}
