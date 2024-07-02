package com.hand.hls.prj.service;

import cfca.sadk.algorithm.common.PKIException;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.exception.TaskActionException;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.prj.dto.*;
import com.hand.hls.wsdl.dto.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;


public interface IPrjProjectService extends IBaseService<HlsCusPrjProject>, ProxySelf<IPrjProjectService> {


    public static final String LINE = "-";
    public static final String ORDER_DESC = "第";
    public static final String ROW_DESC = "行";
    public static final String DEPOSIT_PROCESS_10 = "10";
    public static final String COL_DESC = "列";
    public static final String SELLER_NOT_EXISTS = "出卖人不存在";
    //不能为空
    public static final String NOT_NULL = "不能为空";
    public static final String UNDEFINED = "未定义";
    //承租人类别不能为空
    public static final String TENANT_TYPE_NOT_NULL = "承租人类别不能为空";

    public static final String NP_ID_CARD_EXCEPTION = "身份证号校验失败!";

    public static final String ID_CARD_EXCEPTION = "配偶身份证号校验失败!";

    public static final String NUMBER_EXCEPION = "请维护厂商的框架协议编号!";

    //证件类型不能为空
    public static final String CARD_TYPE_NOT_NULL = "证件类型不能为空";
    //性别不能为空
    public static final String GENDER_NOT_NULL = "性别不能为空";
    //签约方式不能为空
    public static final String SIGN_TYPE_NOT_NULL = "签约方式不能为空";
    //自然人
    public static final String NP_DESC = "自然人";

    //自然人
    public static final String NP = "NP";

    //自然人证件类型sys_code
    public static final String NP_CARD_SYS_CODE = "HLS211_ID_TYPE";

    //法人证件类型sys_code
    public static final String ZX_REGNOTYPE = "ZX_REGNOTYPE";

    //婚姻状况sys_code
    public static final String MARITAL_STATUS_SYS_CODE = "HLS211_MARITAL_STATUS";

    //已婚
    public static final String MARITALED = "已婚";

    public static final String REJECTED = "REJECTED";

    //学历sys_code
    public static final String ACADEMIC_BACKGROUND_SYS_CODE = "HLS211_ACADEMIC_BACKGROUND";

    //股东类型syscode
    public static final String SHAREHOLDER_TYPE_SYS_CODE = "ZX_STOCKHOLDERTYPE";

    //项目单据类别
    public static final String PROJECT_DOCUMENT_CATEGORY = "PRJ_PROJECT";

    //商业伙伴单据类别
    public static final String HLS_BP_DOCUMENT_CATEGORY = "HLS_BP_MASTER";

    //法人
    public static final String ORG_DESC = "法人";

    //法人
    public static final String ORG = "ORG";

    //承租人
    public static final String TENANT = "TENANT";

    //担保人
    public static final String GUARANTOR = "GUARANTOR";

    //保险类型sys_code
    public static final String INSURANCE_TYPE_SYS_CODE = "INSURANCE_TYPE";

    //营业执照号
    public static final String  BUSINESS_LICENSE_DESC= "营业执照号";

    //统一社会信用代码
    public static final String UNIFIED_SOCIAL_CREDIT  = "统一社会信用代码";

    //性别
    public static final String GENDER_SYS_CODE = "HLS211_GENDER";

    //人民币
    public static final String CNY = "CNY";

    public static final String PBOC = "PBOC";

    public static final String INT_RATE = "INT_RATE";

    public static final String GRACE_PERIOD = "GRACE_PERIOD";

    public static final String LEASE_CHARGE_RATIO = "LEASE_CHARGE_RATIO";

    //地址类型
    public static final String HLS211_ADDRESS_TYPE = "HLS211_ADDRESS_TYPE";

    //性别
    public static final String DATA_ECXEPTION = "数据异常";

    public static final String EMPLOYEE_EXCEPTION = "项目经理与当前用户不一致!";

    public static final String FACTORY_EXCEPTION = "主机厂与当前用户配置主机厂不一致!";

    public static final String DEALER_EXCEPTION = "经销商与当前用户配置经销商不一致!";

    public static final String DEALER_TEL_EXCEPTION = "担保人联系方式不得为空";

    public static final String QUOTATION_CALC_EXCEPTION = "报价计算失败,请联系管理员!";

    public static final String LEASE_ITEM_NOT_FOUND = "租赁物信息不能为空，请维护租赁物信息!";

    //是否下拉框
    public static final String  YES_AND_NO = "SYS.YES_NO";

    public static final String  GRACE_TYPE = "GRACE_TYPE";

    public static final String  EXEMPT_PENALTY_INT = "EXEMPT_PENALTY_INT";

    public static final String  DEPOSIT_PROCESS = "DEPOSIT_PROCESS";

    public static final String  GUARANTOR_TYPE = "GUARANTOR_TYPE";

    public static final String  ZX_MEMBERTYPE = "ZX_MEMBERTYPE";

    public static final String HLS500_ANNUAL_PAY_TIMES = "HLS500_ANNUAL_PAY_TIMES";
    //调息规则
    public static final String FLOATING_RANGE_METHOD = "CON.FLOATING_RANGE_METHOD";

    //保证金推算方式
    public static final String PAY_METHOD = "PAY_METHOD";

    //币种
    public static final String CHS_CURRENCY_TYPE = "CHS.CURRENCY_TYPE";

    //基准利率类型
    public static final String BASE_RATE_TYPE  = "FND.BASE_RATE_TYPE";

    //项目状态
    public static final String PROJECT_STATUS_NEW  = "NEW";
    public static final String CLOSED  = "CLOSED";

    //key
    public static final String KEY = "key";

    //product_para
    public static final String PRODUCT_PARA = "product_para";

    //承租人罚息率
    public static final String PENALTY_RATE = "PENALTY_RATE";

    //厂商宽限天
    public static final String MANUFACTURER_GRACE_DAY = "MANUFACTURER_GRACE_DAY";

    //厂商罚息率
    public static final String MANUFACTURER_PENALTY_RATE = "MANUFACTURER_PENALTY_RATE";

    //default_value
    public static final String DEFAULT_VALUE = "default_value";
    //value
    public static final String VALUE = "value";

    //合同文本
    public static final String PROJECT_DOCX = "PRJ_PROJECT_DOCX";

    //签约文本
    public static final String SIGN_DOCX = "SIGN_DOCX";

    public static final String HLS_LEASE_CHANNEL  ="HLS_LEASE_CHANNEL";

    //附件表名
    public static final String FND_ATM_ATTACHMENT_MULTI = "fnd_atm_attachment_multi";

    public static final String NOT_FOUND_CONTRACT_TEMPLATE  = "找不到对应的合同文本模板";

    public static final String CONTRACT_DOCX_DESCRIPTION = "合同文本";

    public static final String CONTENT_TYPE = "application/x-msdownload;";

    public static final String CONTENT_DISPOSITION = "Content-Disposition";

    public static final String ATTACHMENT_FILE = "attachment;filename=";

    public static final String ZIP = ".zip";

    public static final String ISO_8859_1 = "iso-8859-1";

    public static final String FILE_PATH = "file_path";

    public static final String DOCUMENT_NAME_DESC = "document_name";

    public static final String FILE_TYPE_CODE = "file_type_code";

    public static final String CONTENT_LENGTH = "Content-Length";

    public static final String TABLE_NAME = "table_name";

    public static final String SIGN_FIEL_NAME = "签约审批文本";

    public static final String APPROVE_RESULT = "approveResult";

    public static final String APPROVED = "APPROVED";

    public static final String APPROVE_RESULT_DESC  = "approveResultDesc";

    public static final String APPROVED_DESC = "同意";

    public static final String COMMENT = "comment";

    public static final String COMMENT_DESC = "批量审批";

    public static final String ACTION = "complete";

    public static final String MANUFACTURER_NAME = "manufacturerName";

    public static final String CREDIT_AVAILABLE_AMT = "credit_available_amt";

    public static final String CREDIT_EXPOSURE_AMT = "credit_exposure_amt";

    public static final String AVAILABLE_AMT = "available_amt";

    public static final String SUM_FINANCE_AMOUNT = "sum_finance_amount";

    public static final String SUM_DOWN_PAYMENT  = "sum_down_payment";

    public static final String SUM_DEPOSIT  = "sum_deposit";

    public static final String SUM_LEASE_CHANGE = "sum_lease_change";

    public static final String SUM_LEASE_CHANGE_INTEREST = "sum_lease_change_interest";

    public static final String SUM_LEASE = "sum_lease";

    public static final String PRJL = "PRJL";

    public static final String SELECT_PRJL_FINANCE_AMOUNT = "select_prjl_finance_amount";

    public static final String SUM_PRJL_FINANCE_AMOUNT = "sum_prjl_finance_amount";

    public static final String SUM_NOT_FINANCE_AMOUNT = "sum_not_finance_amount";

    public static final String SUM_NOT_FINANCE_TOTAL_AMOUNT = "sum_not_finance_total_amount";

    public static final String CREDIT_EXPOSURE_TOTAL_AMT = "credit_exposure_total_amt";

    public static final String PRJL_RADIO = "prjl_radio";

    public static final String PRJLB = "PRJLB";

    public static final String SELECT_PRJLB_FINANCE_AMOUNT = "select_prjlb_finance_amount";

    public static final String SUM_PRJLB_FINANCE_AMOUNT = "sum_prjlb_finance_amount";

    public static final String PRJLB_RADIO = "prjlb_radio";

    public static final String VENDER_AMOUNT = "vender_amount";

    public static final String SUM_VENDER_SUM_AMOUNT = "sum_vender_sum_amount";

    public static final String VENDER_RADIO = "vender_radio";

    public static final String SENOND_AMOUNT = "senond_amount";

    public static final String DOWN_PAYMENT_TMP = "down_payment_tmp";

    public static final String DEPOSIT_TMP = "deposit_tmp";


    public static final String SUM_SENOND_AMOUNT = "sum_senond_amount";
    public static final String SECOND_RADIO = "second_radio";

    public static final String GRACE_FLAG_N = "grace_flag_n";
    public static final String GRACE_FLAG = "grace_flag";
    public static final String BUSINESS_TYPE = "business_type";
    public static final String BUSINESS_TYPE_N = "business_type_n";
    public static final String REPAY_FLAG = "repay_flag";
    public static final String REPAY_FLAG_N = "repay_flag_n";
    public static final String SERVICE_FEE_REPAY_FLAG = "service_fee_repay_flag";
    public static final String SERVICE_FEE_REPAY_FLAG_N = "service_fee_repay_flag_n";
    public static final String GRACE_FLAG_N_DESC = "逾期宽限类型";
    public static final String ANNUAL_PAY_TIMES_DESC = "支付频率";
    public static final String ANNUAL_PAY_TIMES = "annual_pay_times";
    public static final String ANNUAL_PAY_TIMES_N = "annual_pay_times_n";
    public static final String THREE_YEAR_AMOUNT = "three_year_amount";
    public static final String THREE_YEAR_AMOUNT_RATE = "three_year_amount_rate";
    public static final String FIVE_YEAR_AMOUNT = "five_year_amount";
    public static final String FIVE_YEAR_AMOUNT_RATE = "five_year_amount_rate";
    public static final String PARENT_CREDIT_EXCEPTION = "未查询到父额度!";

    public static final String GRACE_TYPE_MATCH_ERROR = "宽限类型匹配错误!";

    public static final String CREDIT_EXCEPTION = "未查询到额度!";

    public static final String AMOUNT_EXCEPTION = "租赁物总价款与设备价不一致!";

    public static final String NUMBER_DESC = "号";

    public static final String DEFINITION_NAME = "definition_name";

    public static final String PRICE_EXCEPTION = "主机厂对应的厂商下不存在该产品方案";

    public static final String MARRIED_WITH_CHILDREN = "已婚有子";

    public static final String MARRIED_WITHOUT_CHILDREN = "已婚无子";

    public static final String MARRIED = "已婚";

    //进件、合同设备类型
    public static final String PRJ_EQUIPMENT_TYPE = "PRJ_EQUIPMENT_TYPE";

    String CONL = "CONL";
    String CONLB = "CONLB";
    String UNCREATED = "UNCREATED";

    void projectWflSubmit(IRequest iRequest, HlsCusPrjProject project) throws HlsCusException;

    void signWflSubmit(IRequest iRequest,HlsCusPrjProject prjProject) throws Exception;

    /**
     * 投放批量提交审批
     */
    String signBatchWflSubmit(IRequest iRequest,List<HlsCusPrjProject> list);

    /**
     * 投放单个提交审批
     */
    String signSingleWflSubmit(IRequest iRequest,List<HlsCusPrjProject> list);

    HlsCusPrjProject selectForApprove(Long projectId);

    List<Map> queryProjectDetailById(Long projectId);

    String generateAuthorityString(IRequest iRequest, String division, String documentType, String businessType, String employeeCode);

    Boolean validateRequiredInfo(HlsCusPrjProject project);

    Boolean validateQuotation(HlsCusPrjProject project);

    void projectBpCreate(IRequest iRequest, Long projectId);

    List<Map> searchProjectQuery(IRequest iRequest, HlsCusPrjProject prjProject, int pagenum, int pagesize);

    //excel批量导入
    void  excelBatchImport(IRequest iRequest,Long headerId, String division) throws HlsCusException;
    //excel批量导入
    void  excelBatchImport2(IRequest iRequest,Long headerId, String division,Long projectId) throws HlsCusException;

    String createQuotationNumber(IRequest iRequest,HlsCusPrjProject hlsCusPrjProject);

    List<Map> getQuotationDefaultValue(IRequest iRequest, HlsCusPrjQuotation hlsCusPrjQuotation);

    List<FndAttachment> projectCreateDocx(IRequest iRequest, Long projectId, String templateType) throws Exception;

    /**
     * 重新生成合同文本，是否需要把最后标识置为N
     * @param iRequest
     * @param projectId
     * @param templateType
     * @param lastFlag 是否要把最后重签标志值为N
     * @return
     * @throws Exception
     */
    List<FndAttachment> projectCreateDocxWithLastFlag(IRequest iRequest, Long projectId, String templateType,Boolean lastFlag) throws Exception;

    List<FndAttachment> projectCreateDocx(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject) throws Exception;

    void projectDocxZipDownload(IRequest iRequest, HttpServletRequest request, HttpServletResponse response, HlsCusPrjProject hlsCusPrjProject,String documentCategory) throws IOException, HlsCusException;

    void prjSignReturn(IRequest iRequest,List<HlsCusPrjProject> list) throws Exception;

    void signCreateDocx(IRequest iRequest,HlsCusPrjProject hlsCusPrjProject) throws Exception;

    void batchCreateSignDocxAndDown(IRequest iRequest,HttpServletRequest request, HttpServletResponse response,List<HlsCusPrjProject> list) throws IOException;

    /**
     * 更新项目的签约申请日期
     */
    void updateApplyDate(IRequest iRequest,Long projectId, Date date);

    void wflBatchApprove(IRequest iRequest,List<Map> list,String comment) throws TaskActionException;

    List<Map> signBatchCalc(IRequest iRequest,HlsCusPrjProject prjProject) throws HlsCusException;

    String getProjectNumber(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject) throws HlsCusException;

    //前台自动计算报价，需设定某些默认值
    void calcQuotationFront(IRequest iRequest,Long projectId, Long quotationId) throws Exception;

    void wordToPdf(IRequest iRequest,List<FndAttachment> list);

    void sendWordToPdfRequest(Long attachmentId);

    /*进件查询*/
    List<Map> prjRpQueryAuto(IRequest iRequest, Map<String, Object> project, int pagenum, int pagesize);

    /**
     * 更新签约状态
     */
    void updateStatusForProjectReview(IRequest iRequest, Long projectId, String signStatus, Date signDate);

    /**
     * 助贷业务项目评审审批通过逻辑
     */
    boolean projectReviewSynchronization(IRequest iRequest, Long[] projectIds) throws HlsCusException ;

    /**
     * 助贷业务项目评审审批通过逻辑
     * @param partnersContractNumber 进件序号
     * @param partnersId 合作方
     * @param approvalStatus 审查状态 APPROVED/REJECTED
     * @param returnType 退回类型 ADD/RETURN/CANCEL/FAILED
     * @param returnReason 退回理由
     */
    boolean projectReviewSyncForProjectFailure(IRequest iRequest, String partnersContractNumber, Long partnersId,
                                               String approvalStatus, String returnType, String returnReason);

    /**
     * 进件同步接口
     */
    void checkFinanceProjectInfo(IRequest iRequest, PrjBaseDto prjBaseDto) throws Exception;

    void checkCorpFinanceProjectInfo(IRequest iRequest, PrjBaseDto prjBaseDto) throws Exception;

    /**
     * 三一自然人进件同步接口
     */
    void checkSyProjectInfo(IRequest iRequest, PrjBaseDto prjBaseDto) throws Exception;

    /**
     * 租赁物与保险信息校验接口
     */
    void checkFinanceProjectLeaseItem(IRequest iRequest, PrjBaseDto prjBaseDto) throws Exception;

    /**
     * 三一租赁物信息
     */
    void checkEquipmentCodingNotifyInfo(IRequest iRequest, SyFinanceInterface syFinanceInterface, SyEquipmentCodingNotifyInfo equipmentCodingNotifyInfo) throws Exception;

    /**
     * 三一租赁物信息保存接口
     */
    void saveEquipmentCodingNotifyInfo(IRequest iRequest, SyFinanceInterface syFinanceInterface,SyEquipmentCodingNotifyInfo equipmentCodingNotifyInfo) throws Exception;

    /**
     * 承租人预审信息校验接口
     */
    void checkPreFinanceBpmasterInfo(IRequest iRequest,List<BpMasterBaseDto> bpMasterBaseDtoList) throws Exception;

    /**
     *  进件附件同步
     */
    void synPrjProjectAttach(IRequest iRequest, MultipartFile file, FndAttachment fndAttachment) throws Exception;

    /**
     * 进件审批历史流程查询
     */
    List<Map> prjProcessInfoQuery(IRequest iRequest, Map<String, Object> project, int pagenum, int pagesize);

    /**
     * 根据进件编号、资料定义编码删除对应的附件
     */
    void deleteAttachByProjectNumAndAttachCode(IRequest iRequest,String projectNumber,String attachCode);

    /**
     * 根据进件编号查询主键
     */
    Long queryPrimaryKeyByPartnersContractNumber(String partnersContractNumber, String tableName, String primaryKey);

    /**
     * 根据进件ID删除进件相关信息
     * @param projectId
     */
    void deleteProjectInfo(Long projectId);

    /**
     * 汽车租赁单据状态查询
     * @param prjDocumentStatusDto
     * @return
     * @throws Exception
     */
    FinanceDocumentStatusBody financeQueryDocumentStatus(IRequest iRequest,PrjDocumentStatusDto prjDocumentStatusDto) throws Exception;

    void financeSynProjectInfo(IRequest iRequest,HlsCusPrjProject prjProject) throws Exception;

    void financeCorpSynProjectInfo(IRequest iRequest,HlsCusPrjProject prjProject) throws Exception;

    void financeSynQuotationInfo(IRequest iRequest,HlsCusPrjProject prjProject,HlsCusPrjQuotation prjQuotation,int leaseItemNum) throws Exception;

    void financeCorpSynQuotationInfo(IRequest iRequest,HlsCusPrjProject prjProject,HlsCusPrjQuotation prjQuotation,int leaseItemNum) throws Exception;

    void financeSynLeaseItemInfo(IRequest iRequest,HlsCusPrjProject prjProject,HlsCusPrjQuotation prjQuotation,List<HlsCusPrjProjectLeaseItem> prjProjectLeaseItemList) throws Exception;

    void financeSynLeaseItemTy(IRequest iRequest,HlsCusPrjProject prjProject,List<HlsCusPrjProjectLeaseItem> prjProjectLeaseItemList) throws Exception;

    void financeCorpSynLeaseItemInfo(IRequest iRequest,HlsCusPrjProject prjProject,HlsCusPrjQuotation prjQuotation,List<HlsCusPrjProjectLeaseItem> prjProjectLeaseItemList) throws Exception;

    void financeSynBpMasterInfo(IRequest iRequest,HlsCusPrjProject prjProject,List<BpMasterBaseDto> bpMasterBaseDtoList,List<HlsCusPrjProjectBp> projectBpList) throws Exception;

    void financePreSynBpMasterInfo(IRequest iRequest,List<BpMasterBaseDto> bpMasterBaseDtoList) throws Exception;

    FinanceTenantResultBody financeTenantResult(IRequest iRequest, List<FinanceTenantResultBody> financeInterface) throws Exception;

    void financeSynBpMasterAddressInfo(HlsBpMaster hlsBpMaster,List<HlsBpMasterAddress> hlsBpMasterAddressList) throws Exception;

    void financeSynBpMasterBankAccountInfo(IRequest iRequest,HlsBpMaster hlsBpMaster,List<HlsBpMasterBankAccount> hlsBpMasterBankAccountList) throws Exception;

    void financeSynBpMainMembersInfo(IRequest iRequest,HlsBpMaster hlsBpMaster,List<HlsBpMasterMainMembers> hlsBpMasterMainMembersList) throws Exception;

    void financeSynPrjBankInfo(IRequest iRequest, HlsCusPrjProject prjProject, Long bpId)throws Exception;

    /**
     * 报价计算和保存进件相关信息
     * @param iRequest
     * @param prjBaseDto
     * @throws Exception
     */
    void excecuteAndSavePrjInfo(IRequest iRequest, PrjBaseDto prjBaseDto) throws Exception;

    void savePrjLeaseItem(IRequest iRequest, PrjBaseDto prjBaseDto) throws Exception;

    /**
     * 汽车租赁重新生成合同文本
     */
    void retryGenerateDocx(IRequest iRequest, Long projectId);

    List<Map> repaymentBatchCalc(IRequest iRequest, Long cshPaymentReqId) throws HlsCusException;

    /**
     * 进件审批提交前校验
     */
    void projectSubmitValidate(HlsCusPrjProject project) throws HlsCusException;

    /**
     * 租后信息同步
     * @param iRequest
     * @param rentInfo
     * @throws HlsCusException
     */
    void syncRentInfo(IRequest iRequest, RentInfo rentInfo) throws HlsCusException;

    /**
     * 进件批量提交审批
     */
    String projectBatchSubmit(IRequest iRequest,String[] projectIdList);

    /**
     * 风险引擎预审结果查看
     */
    List prjRiskTrailQuery(IRequest iRequest, String[] projectIdList);

    /**
     * 重签伙伴信息查询
     */
    List bpInfoByProjectId(IRequest iRequest, String[] projectIdList);

    /*
     * 经营租赁正审自动拒绝结果查看
     */
    List queryOlAutoReject(IRequest iRequest, String requestHeadId);

    /**
     * 付款申请退回
     *          新事务
     */
    boolean returnPayment(IRequest iRequest, Long projectId, String returnReason, String returnDescription)
            throws HlsCusException;

    /**
     * 放款信息同步
     */
    void syncPaymentReq(IRequest iRequest, PaymentReq paymentReq) throws HlsCusException;

    /**
     * 更新放款信息的租赁物信息
     * 提取出来该部分逻辑并放入一个新事物中
     */
    void updateLeaseItem(IRequest iRequest, Long projectId, PaymentReq paymentReq) throws HlsCusException;

    /**
     * 历史流程报表查询
     * @param taskInfo
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<Map> queryReportHistoryList(Map<String, Object> taskInfo, int pageNum, int pageSize);

    /**
     *
     * @return
     */
    List<Map> queryProcessName();

    /**
     * 报表历史查询指定发起人LOV
     * @return
     */
    List<ReportStartUserLov> queryReportStartUserLov(IRequest iRequest, ReportStartUserLov user, int page, int pagesize);

    /**
     * 报表历史查询指定发起人部门LOV
     * @return
     */
    List<ReportStartUserDepartmentLov> ReportStartUserDepartmentLov(IRequest iRequest, ReportStartUserDepartmentLov dept, int page, int pagesize);

    /**
     * 厂商 重新生成合同文本
     * @param iRequest
     * @param projectId
     */
    void sigRetryGenerateDocx(IRequest iRequest, Long projectId) throws HlsCusException;

    /**
     * 承租人&担保人 重新生成合同文本
     * @param iRequest
     * @param projectId
     * @param params
     */
    void sigBpRetryGenerateDocx(IRequest iRequest, Long projectId, Map params) throws HlsCusException, PKIException;

    /**
     * 自动评分
     * @param iRequest
     * @param projectId
     */
    void autoScore(IRequest iRequest, Long projectId,String objectType,String reCalFlag);

    /**
     * 根据进件id查找进件信息
     * @param projectId
     * @return
     */
    HlsCusPrjProject queryProjectByProjectId(Long projectId);

    /**
     * 业务确认函打印
     */
    void downloadBusinessConfirmPdf(IRequest iRequest,HttpServletResponse response, List<HlsCusPrjProject> list) throws Exception;
    /**
     * 生成业务确认函
     */
    List<Map> generateBusinessConfirm(IRequest iRequest,List<HlsCusPrjProject> list) throws Exception;

    /**
     *当前登录用户信息获取
     * @return
     */
    List<Map> queryUserInfo(IRequest iRequest, Map params);

    /**
     * 检查租赁物编号是否重复
     * @param itemNumber 车架号或者整机编号
     * @param division 业务线
     * @param contractNumber 合同编号，不与自身校验
     * @param classifyId 规格型号
     * @return 重复校验结果，空为通过
     */
    String allLeaseItemCheck(String itemNumber,String division, String contractNumber,String classifyId);

    /**
     * 进件审批自治事务提交任务
     * @param iRequest 请求
     * @param project 进件信息
     * @param itemMap 租赁物编号map
     * @throws HlsCusException 校验报错
     */
    String submitTask(IRequest iRequest, HlsCusPrjProject project,Map<String,String> itemMap);

    /**
     * 投放审查自治事务提交任务
     * @param iRequest 请求
     * @param project 进件信息
     * @throws HlsCusException 校验报错
     */
    String submitSignTask(IRequest iRequest, HlsCusPrjProject project);

    /**
     * 获取项目和合同的产品线
     * @param contractNumber 合同编或项目编号
     * @return 产品线
     */
    String getDivision(String contractNumber, Long contractId, Long projectId);

    void downloadConfirmPdf(IRequest iRequest, HttpServletResponse response, List<FndAttachment> list) throws Exception;

    /**
     *
     * @param iRequest IRequest
     * @param list projectId
     * @return List
     */
    List<Long> generateProjectBusinessConfirm(IRequest iRequest, List<HlsCusPrjProject> list) throws Exception;

    List<Long> checkDownloadConfirmPdf(IRequest iRequest, List<HlsCusPrjProject> list) throws Exception;

    /**
     *用户决策报告查看权限查询
     * @return
     */
    List<Map> reportAuthQuery(IRequest iRequest);

    /**
     * 获取项目和合同的厂商code
     * @param contractNumber 合同编或项目编号
     * @return 厂商code
     */
    String getManufacturerCode(String contractNumber, Long contractId, Long projectId);

    String getLeaseChannel(String contractNumber, Long contractId, Long projectId);

    void createOrgTenantSignRequest(IRequest iRequest, HlsCusPrjProject project);
    /**
     * 报错的行数超过十行省略
     * @param errorMessage
     * @param project
     * @param errorStr
     */
    public void substrErrorMsg(StringBuilder errorMessage, HlsCusPrjProject project, String errorStr);

    void createOrgGuarantorSignRequest(IRequest iRequest, HlsCusPrjProject project, HlsCusPrjProjectBp prjProjectBp);
}
