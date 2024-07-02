package com.hand.hls.utils;

/**
 * 常量类
 * 不同用处的常量放在一个内部接口中
 */
public abstract class HlsConstantUtil {
    /**
     * 控制器中的静态常量
     */
    public interface BaseController {
        /**
         * 参数--key
         */
        String PARAMETER = "parameter";

        String Y = "Y";

        String KEY = "key";

        String VALUE = "value";


    }

    public interface PrjProject {
        String ONLY_QUOTATION = "请确认只有一个报价为启用状态!";
        String PRODUCT_PARA = "product_para";
        String DEFAULT_VALUE = "default_value";
        String VALUE_FROM = "value_from";
        String FLOATING_RANGE_METHOD = "floating_range_method";
        String LEASE_START_DATE = "lease_start_date";
        String FLOATING_RANGE_METHOD_N = "floating_range_method_n";
        String LEASE_TERM = "LEASE_TERM";
        String BASE_RATE = "base_rate";
        String GRACE_FLAG_N = "grace_flag_n";
        String REPAY_FLAG_N = "repay_flag_n";
        String SERVICE_FEE_REPAY_FLAG_N = "service_fee_repay_flag_n";
        String SIMPLE_COMPOUND_TYPE_N = "simple_compound_type_n";
        String FLOATING_RANGE_WAY_N = "floating_range_way_n";
        String SIGN_WFL_NOT_FOUND = "未找到提交的项目,请联系管理员!";
        String PROJECT_ID = "projectId";

        String LEASE_START_DATE_NOT_NULL = "起租日不能为空!";
        String SPECIAL_ATTACHMENT_FLAG_NOT_NULL = "是否存在特殊资料不能为空!";
        String PARTNERS_NUMBER_NOT_NULL = "合作方进件序号必填！合作方支付表编号必填!";
        String PARTNERS_PAYMENT_NUMBER_EXISTS = "合作方支付表编号已存在于：";

        String DOC_IS_NULL = "未打印合同文本!";

        String NOT_FOUND_CREDIT = "未找到额度信息!";
        String LEASE_START_DATE_NOT_EQUALS = "进件租赁开始日与报价租赁开始日不一样，请维护成一致!";
        String LEASE_ITEM_AMOUNT = "lease_item_amount";

        String STEP_QUOTATION_FLAG_N = "step_quotation_flag_n";
        String RESIDUAL_VALUE_RECEIVED_N = "residual_value_received_n";
        String REPAY_MODE_N = "repay_mode_n";

        String INT_RATE_CALC_FLAG = "int_rate_calc_flag";
        String LEASE_CHARGE_DISCOUNT_RULE = "lease_charge_discount_rule";
        String RENTAL_DISCOUNT_TIMES = "rental_discount_times";
        String RENTAL_DISCOUNT_TIMES_N = "rental_discount_times_n";
        String LEASE_CHARGE_DISCOUNT_RULE_N = "lease_charge_discount_rule_n";
        String MANAGEMENT_FEE_RATIO = "management_fee_ratio";
        String MANAGEMENT_FEE = "management_fee";
    }

    /**
     * 退款申请常量接口
     */
    public interface TransactionRefund {
        /**
         * 参数为空
         */
        String PARAMETER_IS_NULL = "参数为空，请检查";

        /**
         * 可退款现金事务为空
         */
        String CAN_REFUND_TRANSACTION_IS_NULL = "可退款现金事务为空!";
    }

    /**
     * 提交状态，通过提交状态判断单据保存是否需要提交
     */
    public interface SubmitStatus {
        /**
         * 提交
         */
        String SUBMIT = "SUBMIT";

        /**
         * 保存
         */
        String SAVE = "SAVE";

        /**
         * 取消
         */
        String CANCEL = "CANCEL";

        /**
         * 状态未知时返回信息
         */
        String STATUS_UNKOWN_ERROR_MESSAGE = "提交状态未知，请检查";


    }

    /**
     * 工作流审批状态
     */
    public interface WorkFlowStatus {
        /**
         * 新建
         */
        String NEW = "NEW";

        /**
         * 审批中
         */
        String APPROVING = "APPROVING";

        /**
         * 审批通过
         */
        String APPROVED = "APPROVED";

        /**
         * 审批拒绝
         */
        String REJECTED = "REJECTED";

        /**
         * 取消
         */
        String CANCEL = "CANCEL";
    }

    /**
     * 工作流参数名称
     */
    public interface WorkFlowParameterKey {
        /**
         * 工作流类型
         */
        String WORK_FLOW_TYPE = "workFlowType";

        /**
         * 工作流的business_key，单据主键
         */
        String BUSINESS_KEY = "BUSINESS_KEY";

        /**
         * 请求分装对象
         */
        String iRequest = "iRequest";

        /**
         *
         */
        String APPROVE_RESULT = "approveResult";
    }

    /**
     * 日期格式化
     */
    public interface DateFormatPattern {
        /**
         * 标准日期输出
         */
        String YEAR_MONTH_DAY = "yyyy-MM-dd";
    }

    /**
     * 付款申请常量
     */
    public interface HlsCusCshPaymentReqHd {
        /**
         * documentType
         */
        String DOCUMENT_TYPE = "PAYMENT_REQ";

        /**
         * documentCategory
         */
        String DOCUMENT_CATEGORY = "CSH_PAYMENT_REQ";

        /**
         * documentType
         */
        String BUSINESS_TYPE = "PAYMENT_REQ";

        /**
         * 付款撤回错误信息
         */
        String PAYMENT_BACK_MESSAGE = "未选择付款记录！";
    }

    /**
     * 支付状态
     */
    public interface SlipStatus {
        /**
         * 新建
         */
        String NEW = "NEW";

        /**
         * 支付中
         */
        String PAYING = "PAYING";

        /**
         * 撤回
         */
        String BACK = "BACK";

        /**
         * 已支付
         */
        String PAID = "PAID";

        /**
         * 行明细发送sap
         */
        String DETAIL_SEND = "DETAIL_SEND";

        /**
         * 支付失败
         */
        String FAILURE = "FAILURE";

        /**
         * 支付失败_内部错误
         */
        String INTERNAL_ERROR = "INTERNAL_ERROR";

        /**
         * 行支付失败
         */
        String DETAIL_ERROR = "DETAIL_ERROR";


    }

    /**
     * sap接口状态
     */
    public interface SapInterfaceStatus {
        /**
         * 发送中
         */
        String SENDING = "SENDING";

        /**
         * 响应成功
         */
        String SUCCESS = "SUCCESS";

        /**
         * 响应失败
         */
        String FAILURE = "FAILURE";


    }

    /**
     * 保证金抵扣sap状态
     */
    public interface DeductSapStatus {
        /**
         * 内部核销错误
         */
        String INNER_ERROR = "INNER_ERROR";

        /**
         * 响应成功
         */
        String SUCCESS = "SUCCESS";

        /**
         * 响应失败
         */
        String FAILURE = "FAILURE";


    }

    /**
     * sap接口类型
     */
    public interface InterfaceType {
        /**
         * 支付
         */
        String CSH_PAYMENT_REQ_SLIP = "CSH_PAYMENT_REQ_SLIP";

        /**
         * 收款核销
         */
        String CSH_WRITE_OFF = "CSH_WRITE_OFF";

        /**
         * 代扣查询结果
         */
        String CSH_WITHHOLD_RESULT = "CSH_WITHHOLD_RESULT";
    }

    /**
     * 收款核销类型
     */
    public interface TransactionType {
        /**
         * 收款核销为预收款
         */
        String RECEIPT_ADVANCE_RECEIPT = "RECEIPT_ADVANCE_RECEIPT";

        /**
         * 预收款核销债权
         */
        String ADVANCE_RECEIPT_CREDIT = "ADVANCE_RECEIPT_CREDIT";

        /**
         * 收款核销债权
         */
        String RECEIPT_CREDIT = "RECEIPT_CREDIT";

        /**
         * 收款核销保证金
         */
        String RECEIPT_DEPOSIT = "RECEIPT_DEPOSIT";

        /**
         * 预收款退款
         */
        String ADVANCE_REFUND = "ADVANCE_REFUND";

        /**
         * 预收款
         */
        String ADVANCE_RECEIPT = "ADVANCE_RECEIPT";

        /**
         * 保证金
         */
        String DEPOSIT = "DEPOSIT";

    }

    /**
     * 核销标志
     */
    public interface WriteOffFlag {
        /**
         * 完全核销
         */
        String FULL = "FULL";

        /**
         * 部分核销
         */
        String PARTIAL = "PARTIAL";

        /**
         * 未核销
         */
        String NOT = "NOT";
    }

    /**
     * 保证金抵扣申请类型常量
     */
    public interface CshDepositDeductReqHd {

        /**
         * documentCategory
         */
        String DOCUMENT_CATEGORY = "CSH_DEPOSIT_DEDUCT_REQ";

        /**
         * documentType
         */
        String DOCUMENT_TYPE = "CSH_DEPOSIT_DEDUCT_REQ";

        /**
         * documentType
         */
        String BUSINESS_TYPE = "CSH_DEPOSIT_DEDUCT_REQ";

        /**
         * workFlowType
         */
        String WORK_FLOW_TYPE = "CSH_DEPOSIT_DEDUCT_REQ";


    }

    /**
     * 产品申请类型常量
     */
    public interface HlsProductDefinition {

        /**
         * documentCategory
         */
        String DOCUMENT_CATEGORY = "PRODUCT_DEFINITION";

        /**
         * documentType
         */
        String DOCUMENT_TYPE = "PRODUCT_DEFINITION";

        /**
         * documentType
         */
        String BUSINESS_TYPE = "PRODUCT_DEFINITION";

        /**
         * workFlowType
         */
        String WORK_FLOW_TYPE = "PRODUCT_DEFINITION";

        /**
         * 经营性租赁工作流
         * workFlowType
         */
        String OPERATING_LEASE_WORK_FLOW_TYPE = "OPERATING_LEASE_PRODUCT_DEFINITION";

        /**
         * exemptPenaltyInt
         */
        public interface ExemptPenaltyInt {
            /**
             * 宽限至次月
             */
            String NEXT_MONTH = "NEXT_MONTH";

            /**
             * 按天宽限
             */
            String BY_DAY = "BY_DAY";
        }

        /**
         * productPara  暂定部分参数，需要使用时添加
         */
        public interface ProductPara {
            /**
             * 承租人宽限天
             */
            String GRACE_PERIOD = "GRACE_PERIOD";

            /**
             * 承租人罚息率
             */
            String PENALTY_RATE = "PENALTY_RATE";
            /**
             * 厂商宽限天
             */
            String MANUFACTURER_GRACE_DAY = "MANUFACTURER_GRACE_DAY";
            /*
            * 产品编号
            * */
            String PRODUC_NUMBER = "product_number";
            /**
             * 厂商罚息率
             */
            String MANUFACTURER_PENALTY_RATE = "MANUFACTURER_PENALTY_RATE";

            /**
             * 租息率
             */
            String INT_RATE = "INT_RATE";

            /**
             * 服务费比例
             */
            String LEASE_CHARGE_RATIO = "LEASE_CHARGE_RATIO";

        }

        /**
         * RepayFlag
         */
        String REPAY_FLAG_YES = "Y";
        String REPAY_FLAG_NO = "N";
        String REPAY_FLAG = "repay_flag";

        String INT_RATE_REPLY_TMP = "int_rate_reply_tmp";

        String LEASE_CHARGE_RATIO_REPLY_TMP = "lease_charge_ratio_reply_tmp";
    }

    /**
     * 收款参数
     */
    public interface CshTransactionParameter {
        String transaction_num = "transaction_num";
    }

    /**
     * 认领类型
     */
    public interface SlipType {
        /**
         * 收款认领
         */
        String RECEIVE = "RECEIVE";
        /**
         * 认领撤回
         */
        String REVERSED = "REVERSED";
    }

    /**
     * 合同上的 sapStatus
     */
    public interface SapStatus {
        /**
         * 已发送
         */
        String SUCCESS = "SUCCESS";

        /**
         * 发送失败
         */
        String FAILURE = "FAILURE";

        /**
         * 创建借据
         */
        String NEW_CONTRACT = "NEW_CONTRACT";

        /**
         * 变更租赁支付计划表
         */
        String CHANGE_RENTAL = "CHANGE_RENTAL";

        /**
         * 变更手续费支付计划表
         */
        String CHANGE_CHANGE_FEE = "CHANGE_CHANGE_FEE";

        /**
         * 厂商租赁项目回购
         */
        String CHANGE_BACK_BUY = "CHANGE_BACK_BUY";

        /**
         * 收到名义货价
         */
        String CHANGE_HIRE_PURCHASE = "CHANGE_HIRE_PURCHASE";

        /**
         * 合同全部提前结清
         */
        String CHANGE_EARLY_TERMINATE = "CHANGE_EARLY_TERMINATE";
    }

    /**
     * 厂商起租规则类型
     */
    public interface HlsBpMasterInceptRule {
        /**
         * 固定日
         */
        String FIXED_DAY = "FIXED_DAY";

        /**
         * 投放即起租
         */
        String LOAN_DATE = "LOAN_DATE";
    }

    /**
     * 合同状态
     */
    public interface ConContractStatus {
        /**
         * 起租
         */
        String INCEPT = "INCEPT";

        /**
         * 签约
         */
        String SIGN = "SIGN";

        /**
         * 取消
         */
        String CANCEL = "CANCEL";

        /**
         * 结束
         */
        String TERMINATE = "TERMINATE";

        /**
         * 暂挂
         */
        String PENDING = "PENDING";
    }

    /**
     * 价目表
     */
    public interface PriceList{
        /**
         * 等额本金后付
         */
        String YX_EQUAL_PRINCIPAL = "YX_EQUAL_PRINCIPAL";

        /**
         * 等额租金后付
         */
        String YX_EQUAL_RENTAL = "YX_EQUAL_RENTAL";

        /**
         * 等额本金后付（复利）
         */
        String YX_EQUAL_PRINCIPAL_COMPOUND = "YX_EQUAL_PRINCIPAL_COMPOUND";

        /**
         * 等额租金后付（复利）
         */
        String YX_EQUAL_RENTAL_COMPOUND = "YX_EQUAL_RENTAL_COMPOUND";

        /**
         * 缩期展期-等额租金
         */
        String EXTENSION = "EXTENSION";

        /**
         * 汽车租赁-等额本息
         */
        String ZD_EQUAL_RENTAL_COMPOUND = "ZD_EQUAL_RENTAL_COMPOUND";

        /**
         * 缩期展期-等额租金（复利）
         */
        String EXTENSION_COMPOUND = "EXTENSION_COMPOUND";

        /**
         * 缩期展期-等额本金
         */
        String EXTENSION_EQUAL_PRINCIPAL = "EXTENSION_EQUAL_PRINCIPAL";

        /**
         * 缩期展期-等额本金(复利)
         */
        String EXTENSION_EQUAL_PRINCIPAL_COMPOUND = "EXTENSION_EQUAL_PRINCIPAL_COMPOUND";

        /**
         * 调息_等额本金后付
         */
        String INTEREST_PRINCIPAL = "INTEREST_PRINCIPAL";

        /**
         * 调息_等额本息后付
         */
        String INTEREST_RENTAL = "INTEREST_RENTAL";

        /**
         * 调息_等额本金后付(复利)
         */
        String INTEREST_PRINCIPAL_COMPOUND = "INTEREST_PRINCIPAL_COMPOUND";

        /**
         * 调息_等额本息后付(复利)
         */
        String INTEREST_RENTAL_COMPOUND = "INTEREST_RENTAL_COMPOUND";

        /**
         * 不规则还款
         */
        String YX_FREE = "YX_FREE";
    }

    /**
     * 浮动类型
     */
    public interface FloatingRangeMethod{
        /**
         * 固定利率
         */
        String FIXED_AMOUNT = "FIXED_AMOUNT";

        /**
         * 次月1日
         */
        String NEXT_MONTH_1_DAYS = "NEXT_MONTH_1_DAYS";

        /**
         * 次期
         */
        String NEXT_TIMES = "NEXT_TIMES";

        /**
         * 次年1月1日
         */
        String NEXT_YEAR_NUARY_1ST = "NEXT_YEAR_NUARY_1ST";
    }

    /**
     * 评分单据类型
     */
    public interface ScoreObjectType {
        /**
         * HLS_BP_MASTER
         */
        String HLS_BP_MASTER = "HLS_BP_MASTER";

        /**
         * PRJ_PROJECT
         */
        String PRJ_PROJECT = "PRJ_PROJECT";

        /**
         * CON_CONTRACT
         */
        String CON_CONTRACT = "CON_CONTRACT";

        /**
         * ...
         */
    }

    /**
     * 认领来源
     */
    public interface WriteOffSlipSourceType{
        /**
         * 认领，非代收模式，代收模式下的cf_type非1，9，8的现金流
         */
        String WRITE_OFF = "WRITE_OFF";

        /**
         * 认领，银企直联
         */
        String WITHHOLD_WRITE_OFF = "WITHHOLD_WRITE_OFF";

        /**
         * 认领，代收模式
         */
        String PARTNER_WRITE_OFF = "PARTNER_WRITE_OFF";
    }

    /**
     * 重试状态
     */
    public interface RetryStatus{
        /**
         * 重试中
         */
        String RETRYING = "RETRYING";

        /**
         * 成功
         */
        String SUCCESS = "SUCCESS";

        /**
         * 失败
         */
        String FAILURE = "FAILURE";
    }
}
