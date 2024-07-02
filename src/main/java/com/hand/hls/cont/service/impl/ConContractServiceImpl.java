package com.hand.hls.cont.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.account.dto.Role;
import com.hand.hap.account.mapper.RoleMapper;
import com.hand.hap.core.BaseConstants;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.ICodeService;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.cont.dto.*;
import com.hand.hls.cont.mapper.ConfirmBatchMapper;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsCusConContractChangeReqMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.service.*;
import com.hand.hls.cont.utils.BeanRefUtils;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.csh.exception.BeyondAmountLimitException;
import com.hand.hls.csh.service.impl.CshWriteOffServiceImpl;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.dto.DocumentType;
import com.hand.hls.fnd.dto.FndCompany;
import com.hand.hls.fnd.dto.FndInterfaceLines;
import com.hand.hls.fnd.dto.HlsCashflowItem;
import com.hand.hls.fnd.mapper.DocumentTypeMapper;
import com.hand.hls.fnd.mapper.FndInterfaceLinesMapper;
import com.hand.hls.fnd.mapper.HlsCashflowItemMapper;
import com.hand.hls.fnd.mapper.HlsCusFndCompanyMapper;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.prj.dto.HlsBpMaster;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotationCashflow;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationCashflowMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.HlsBpMasterService;
import com.hand.hls.prj.service.HlsCusPrjProjectAttachmentService;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.prj.service.IPrjProjectService;
import com.hand.hls.sys.dto.FndEmployee;
import com.hand.hls.sys.dto.SysDocumentHistory;
import com.hand.hls.sys.dto.SysDocumentHistoryDetail;
import com.hand.hls.sys.mapper.FndEmployeeMapper;
import com.hand.hls.sys.mapper.SysDocumentHistoryDetailMapper;
import com.hand.hls.sys.mapper.SysDocumentHistoryMapper;
import com.hand.hls.sys.service.ISysDocumentHistoryService;
import com.hand.hls.utils.DateUtils;
import com.hand.hls.utils.HlsConstantUtil;
import com.hand.hls.utils.MathUtil;
import com.sun.istack.NotNull;
import hls.core.sys.mapper.SysCodeValueMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


@Service
@Transactional(rollbackFor = Exception.class)
public class ConContractServiceImpl extends BaseServiceImpl<HlsCusConContract> implements IConContractService {
    @Autowired
    private HlsCusConContractMapper conContractMapper;
    @Autowired
    private ConfirmBatchMapper confirmBatchMapper;
    @Autowired
    private IBusinessConfirmService businessConfirmService;

    @Autowired
    private HlsCusPrjProjectService prjProjectService;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;
    @Autowired
    private HlsCusConContractService hlsCusConContractService;
    @Autowired
    private HlsCusConContractCashflowService hlsCusConContractCashflowService;
    @Autowired
    private HlsCusConContractBpService hlsCusConContractBpService;
    @Autowired
    private IConContractLeaseItemService conContractLeaseItemService;
    @Autowired
    private HlsCusConContractMortgageService cusConContractMortgageService;
    @Autowired
    private HlsCusPrjProjectAttachmentService hlsCusPrjProjectAttachmentService;
    @Autowired
    private ISysDocumentHistoryService sysDocumentHistoryService;
    @Autowired
    private ConContractChangeReqServiceImpl conContractChangeReqService;

    @Autowired
    private HlsCusPrjQuotationMapper prjQuotationMapper;
    @Autowired
    private DocumentTypeMapper documentTypeMapper;
    @Autowired
    private FndEmployeeMapper fndEmployeeMapper;
    @Autowired
    private HlsCusFndCompanyMapper hlsCusFndCompanyMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private FndInterfaceLinesMapper fndInterfaceLinesMapper;
    @Autowired
    private HlsCashflowItemMapper hlsCashflowItemMapper;
    @Autowired
    private HlsCusConContractChangeReqMapper hlsCusConContractChangeReqMapper;
    @Autowired
    private SysDocumentHistoryMapper sysDocumentHistoryMapper;
    @Autowired
    private SysDocumentHistoryDetailMapper sysDocumentHistoryDetailMapper;
    @Autowired
    private HlsCusConContractCashflowMapper hlsCusConContractCashflowMapper;
    @Autowired
    private SysCodeValueMapper sysCodeValueMapper;
    @Autowired
    private HlsCusPrjQuotationCashflowMapper hlsCusPrjQuotationCashflowMapper;


    private Logger logger = LoggerFactory.getLogger(ConContractServiceImpl.class);

    // 确认函批次未取回
    private static final String UNREFRESH="UNREFRESH";

    private final static String PROJECT_NOT_FOUND = "当前项目不存在";
    private final static String NOT_FOUND_DOCUMENT_TYPE = "未找到可匹配的单据类型！";
    private final static String PRJ_PROJECT = "PRJ_PROJECT";
    private final static String CON_CONTRACT = "CON_CONTRACT";
    private final static String PRJ_QUOTATION = "PRJ_QUOTATION";
    private final static String CON_QUOTATION = "CON_QUOTATION";

    private final static String CONTRACT_STATUS_SIGN = "SIGN";
    private final static String UNCREATED = "UNCREATED";
    private final static String NOT = "NOT";
    private final static String CONTRACT_DATA_CLASS_NORMAL = "NORMAL";
    private final static String CONTRACT_TEXT_STATUS_AUDITED = "AUDITED";

    private static final String CANCEL_CN = "-取消";





    @Override
    public List<Long> createBusinessConfirm(IRequest iRequest, List<Long> list) throws Exception {
        List<Long> result = new ArrayList<>();
        //获取合同
        List<HlsCusConContract> conContracts = conContractMapper.queryByContractIds(list);
        //过滤掉未维护主机厂的合同
//        if (CollectionUtils.isNotEmpty(conContracts)) {
//            conContracts = conContracts.stream().filter(t -> Objects.nonNull(t.getFactoryId())).collect(Collectors.toList());
//        }
        //根据主机厂进行分组
        if (CollectionUtils.isNotEmpty(conContracts)) {
            Date currentTime = new Date();
            DateFormat format = new SimpleDateFormat("yyyyMMdd");
            ConfirmBatch confirmBatch = new ConfirmBatch();
            Map<Long, List<HlsCusConContract>> contractGroupMap = conContracts.stream().collect(Collectors.groupingBy(HlsCusConContract::getFactoryId));
            long maxBatchCode = 1L;
            ConfirmBatch maxBatch = confirmBatchMapper.queryMaxCountByDay();
            if (Objects.nonNull(maxBatch)) {
                maxBatchCode = maxBatch.getCountByDay()+1;
            }
            AtomicReference<Long> finalMaxBatchCode = new AtomicReference<>(maxBatchCode);
            contractGroupMap.forEach((key, value) -> {
                confirmBatch.setBatchCode("QRH" + format.format(currentTime) + String.format("%04d", finalMaxBatchCode.get()));
                confirmBatch.setRefreshStatus(UNREFRESH);
                confirmBatch.setCreatedBy(iRequest.getUserId());
                confirmBatch.setCreationDate(new Date());
                confirmBatch.setLastUpdatedBy(iRequest.getUserId());
                confirmBatch.setLastUpdateDate(new Date());
                confirmBatchMapper.insertSelective(confirmBatch);
                for (HlsCusConContract contract : value) {
                    BusinessConfirm businessConfirm = new BusinessConfirm();
                    businessConfirm.setBatchId(confirmBatch.getBatchId());
                    businessConfirm.setContractId(contract.getContractId());
                    businessConfirm.setDealerStatus("UNCREATED");
                    businessConfirmService.insertSelective(iRequest, businessConfirm);
                }
                result.add(confirmBatch.getBatchId());
                finalMaxBatchCode.getAndSet(finalMaxBatchCode.get() + 1);
            });
        }
        return result;
    }

    /**
     * 二期功能：投放审查申请工作流审批通过后，自动生成合同数据
     *
     * @param iRequest
     * @param prjProjectParameter
     */
    @Override
    public void saveConContractFromPrjProjectSign(IRequest iRequest, HlsCusPrjProject prjProjectParameter) {
        HlsCusPrjProject prjProject = prjProjectService.selectByPrimaryKey(iRequest, prjProjectParameter);

        if (prjProject == null) {
            throw new IllegalArgumentException(PROJECT_NOT_FOUND);
        }

        //databaseLockProvider.lock(prjProject);

        HlsCusConContract conContract = new HlsCusConContract();
        BeanRefUtils.beanToBean(prjProject, conContract, hlsBeanRefUtilService);

        //把报价上的字段也复制到合同表上
        HlsCusPrjQuotation prjQuotationParameter = new HlsCusPrjQuotation();
        prjQuotationParameter.setSourceDocumentId(prjProject.getProjectId());
        prjQuotationParameter.setSourceDocumentCategory(PRJ_PROJECT);
        prjQuotationParameter.setEnabledFlag(BaseConstants.YES);
        HlsCusPrjQuotation prjQuotation = prjQuotationMapper.selectOne(prjQuotationParameter);

        BeanRefUtils.beanToBean(prjQuotation, conContract, hlsBeanRefUtilService);
        conContract.setDocumentCategory(CON_CONTRACT);

        //获取单据类型
        if ("LEASE".equals(prjProject.getBusinessType())){
            conContract.setDocumentType("CONL");
        }else {
            conContract.setDocumentType(getDocumentType(CON_CONTRACT, prjProject.getBusinessType()));
        }
        conContract.setBusinessType(prjProject.getBusinessType());
        conContract.setContractStatus(CONTRACT_STATUS_SIGN);
        conContract.setBusinessConfirmStatus(UNCREATED);
        conContract.setLegalContractNumber(prjProject.getContractNum());
        //暂时用项目名称赋值给合同名称
        conContract.setContractName(prjProject.getProjectName());

        //TODO 获取编码规则

        conContract.setContractNumber(prjProject.getProjectNumber());

        //授权规则字符串设置
        FndEmployee employee = new FndEmployee();
        employee.setEmployeeId(prjProject.getEmployeeId());
        employee = fndEmployeeMapper.selectByPrimaryKey(employee);
        String empCode = employee.getEmployeeCode();
        FndCompany fndCompany = new FndCompany();
        fndCompany.setCompanyId(conContract.getCompanyId());
        fndCompany = hlsCusFndCompanyMapper.selectByPrimaryKey(fndCompany);
        String division = conContract.getDivision() == null ? "" : conContract.getDivision();
        if (fndCompany != null) {
            conContract.setAuthorityRuleString('"' + fndCompany.getCompanyCode() + '"' + "." + '"'
                    + prjProject.getLeaseOrganization() + '"' + "." + '"' + '"' + "." + '"'
                    + conContract.getDocumentType() + '"' + "." + '"' + conContract.getBusinessType()
                    + '"' + "." + '"' + division + '"' + "." + '"' + empCode + '"');
        }

        conContract.setOverdueStatus("N");
        conContract.setBillingStatus(NOT);//目前先定为开票，状态为NOT
        conContract.setDataClass(CONTRACT_DATA_CLASS_NORMAL);
        conContract.setPrintStatus(NOT);
        conContract.setBillingStatus(NOT);
        conContract.setReceivedStatus(NOT);
        conContract.setCurrency("CNY");
        conContract.setDownloadVersion(1L);
        //零售合同也要计算罚息
        conContract.setPenaltyProfile("STD");
        //电子签约-合同表上的contract_text_status合同签约状态 合同投放审查总经理节点通过后状态变为已审核
        conContract.setContractTextStatus(CONTRACT_TEXT_STATUS_AUDITED);


        conContract.setCreationDate(new Date());

        //插入合同表
        conContract = self().insertSelective(iRequest, conContract);

        Long contractId = conContract.getContractId();
        Long projectId = prjProject.getProjectId();
        Long quotationId = prjQuotation.getQuotationId();

        //首先复制报价
        HlsCusPrjQuotation hlsCusPrjQuotation = hlsCusConContractService.copyQuotationRelated(iRequest, quotationId, contractId, CON_CONTRACT, CON_QUOTATION);

        //复制现金流
        hlsCusConContractCashflowService.saveCashflowFromPrjCashflow(iRequest, contractId, quotationId, hlsCusPrjQuotation.getQuotationId());

        //复制项目bp
        hlsCusConContractBpService.saveConContractBpFromPrj(iRequest, contractId, projectId);

        //复制项目租赁物
        conContractLeaseItemService.saveLeaseItemFromPrj(iRequest, contractId, projectId);

        //复制抵制押物
        cusConContractMortgageService.saveMortgageFromPrj(iRequest, contractId, projectId);

        //复制附件
        hlsCusPrjProjectAttachmentService.saveAttachmentFromPrj(iRequest, contractId, projectId);
    }

    /**
     * 二期功能：付款申请-取消合同按钮更新合同/项目表字段
     *
     * @param iRequest
     * @param contractIds
     * @param returnDate
     * @param returnReason
     * @param returnDescription
     */
    @Override
    public void updatePaymentReturn(IRequest iRequest, Long[] contractIds, Date returnDate, String returnReason, String returnDescription) {
        for (int i = 0; i < contractIds.length; i++) {
            HlsCusConContract hlsCusConContract = new HlsCusConContract();
            hlsCusConContract.setContractId(contractIds[i]);
            hlsCusConContract.setContractStatus("CANCEL");
            self().updateByPrimaryKeySelective(iRequest, hlsCusConContract);

            Long projectId = conContractMapper.queryProjectIdByContractId(contractIds[i]);
            HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
            hlsCusPrjProject.setProjectId(projectId);
            hlsCusPrjProject.setRejectedDate(returnDate);
            hlsCusPrjProject.setReturnType("CANCEL");
            hlsCusPrjProject.setRejectedDescription(returnDescription);
            hlsCusPrjProject.setReturnStage("PAYMENT");


            hlsCusPrjProject.setProjectStatus(IPrjProjectService.CLOSED);
            prjProjectService.updateByPrimaryKeySelective(iRequest, hlsCusPrjProject);
        }
    }

    /**
     * 二期功能：修改合同or项目编号后缀加上 _取消
     * @param iRequest
     * @param documentCategory
     * @param documentId
     * @throws HlsCusException
     */
    @Override
    public void documentNumberCancel(IRequest iRequest, String documentCategory, Long documentId) throws HlsCusException {
        String documentNumber;
        //查询出当前单据号
        HlsCusPrjProject hlsCusPrjProjectLock = new HlsCusPrjProject();
        HlsCusConContract hlsCusConContractLock = new HlsCusConContract();
        if (PRJ_PROJECT.equals(documentCategory)) {
            hlsCusPrjProjectLock.setProjectId(documentId);
            hlsCusPrjProjectLock = prjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProjectLock);

            documentNumber = hlsCusPrjProjectLock.getProjectNumber();
            String documentNumberReplace = documentNumber + CANCEL_CN;
            hlsCusPrjProjectLock.setProjectNumber(documentNumberReplace);
            prjProjectService.updateByPrimaryKeySelective(iRequest, hlsCusPrjProjectLock);

        } else if (CON_CONTRACT.equals(documentCategory)) {
            hlsCusConContractLock.setContractId(documentId);
            hlsCusConContractLock = self().selectByPrimaryKey(iRequest, hlsCusConContractLock);

            documentNumber = hlsCusConContractLock.getContractNumber();
            String documentNumberReplace = documentNumber + CANCEL_CN;

            hlsCusConContractLock.setContractNumber(documentNumberReplace);
            self().updateByPrimaryKeySelective(iRequest, hlsCusConContractLock);

            //合同编号和项目编号需要一起修改
            hlsCusPrjProjectLock.setProjectNumber(documentNumber);
            hlsCusPrjProjectLock = prjProjectService.selectSelective(iRequest, hlsCusPrjProjectLock).get(0);

            hlsCusPrjProjectLock.setProjectNumber(documentNumberReplace);
            prjProjectService.updateByPrimaryKeySelective(iRequest, hlsCusPrjProjectLock);

        } else {
            throw new HlsCusException("单据取消类型未配置");
        }
    }

    /**
     * 获取单据类型
     * @param documentCategory
     * @param businessType
     * @return
     */
    private String getDocumentType(String documentCategory, String businessType) {
        DocumentType documentType = new DocumentType();
        documentType.setDocumentCategory(documentCategory);
        documentType.setBusinessType(businessType);
        documentType.setEnabledFlag(BaseConstants.YES);
        List<DocumentType> documentTypeList = documentTypeMapper.selectDocumentTypeByBusinessType(documentType);
        if (documentTypeList.size() != 1) {
            throw new IllegalArgumentException(NOT_FOUND_DOCUMENT_TYPE);
        }

        return documentTypeList.get(0).getDocumentType();
    }

    @Override
    public List<Long> createDealerBusinessConfirm(IRequest iRequest, List<Long> list) throws Exception {
        List<Long> result = new ArrayList<>();
        //获取合同
        List<HlsCusConContract> conContracts = conContractMapper.queryByContractIds(list);
        //过滤掉未维护经销商的合同
        if (CollectionUtils.isNotEmpty(conContracts)) {
            conContracts = conContracts.stream().filter(t -> Objects.nonNull(t.getBpIdVender())).collect(Collectors.toList());
        }
        //根据经销商进行分组
        if (CollectionUtils.isNotEmpty(conContracts)) {
            Date currentTime = new Date();
            DateFormat format = new SimpleDateFormat("yyyyMMdd");
            ConfirmBatch confirmBatch = new ConfirmBatch();
            Map<Long, List<HlsCusConContract>> contractGroupMap = conContracts.stream().collect(Collectors.groupingBy(HlsCusConContract::getBpIdVender));
            long maxBatchCode = 1L;
            ConfirmBatch maxBatch = confirmBatchMapper.queryDealerMaxCountByDay();
            if (Objects.nonNull(maxBatch)) {
                maxBatchCode = maxBatch.getCountByDay()+1;
            }
            AtomicReference<Long> finalMaxBatchCode = new AtomicReference<>(maxBatchCode);
            contractGroupMap.forEach((key, value) -> {
                confirmBatch.setBatchCode("QRH-J-" + format.format(currentTime) + String.format("%04d", finalMaxBatchCode.get()));
                confirmBatch.setRefreshStatus(UNREFRESH);
                confirmBatch.setCreatedBy(iRequest.getUserId());
                confirmBatch.setCreationDate(new Date());
                confirmBatch.setLastUpdatedBy(iRequest.getUserId());
                confirmBatch.setLastUpdateDate(new Date());
                confirmBatchMapper.insertSelective(confirmBatch);
                for (HlsCusConContract contract : value) {
                    BusinessConfirm businessConfirm = new BusinessConfirm();
                    businessConfirm.setBatchId(confirmBatch.getBatchId());
                    businessConfirm.setContractId(contract.getContractId());
                    businessConfirm.setDealerStatus("UNCREATED");
                    businessConfirmService.insertSelective(iRequest, businessConfirm);
                }
                result.add(confirmBatch.getBatchId());
                finalMaxBatchCode.getAndSet(finalMaxBatchCode.get() + 1);
            });
        }
        return result;
    }

    /**
     * 判断是否为对应的角色
     *
     * @param iRequest
     * @param roleCode
     * @return
     */
    @Override
    public Boolean isCompanyManageRole(IRequest iRequest, @NotNull String roleCode) {
        if (!Objects.isNull(iRequest)) {
            if (null != iRequest.getRoleId() && -1 != iRequest.getRoleId()) {
                Role role = new Role();
                role.setRoleId(iRequest.getRoleId());
                role = roleMapper.selectByPrimaryKey(role);
                if (StringUtils.equals(role.getRoleCode(), roleCode)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public void terminate(IRequest iRequest, HlsCusConContract hlsCusConContract) throws HlsCusException {
        hlsCusConContract = conContractMapper.selectByPrimaryKey(hlsCusConContract.getContractId());
        if (StringUtils.equals(HlsConstantUtil.ConContractStatus.TERMINATE, hlsCusConContract.getContractStatus())) {
            throw new HlsCusException("该合同已经结束，不能结束!");
        }
        if (StringUtils.equals(HlsConstantUtil.ConContractStatus.CANCEL, hlsCusConContract.getContractStatus())) {
            throw new HlsCusException("该合同已经取消，不能结束!");
        }
        if (StringUtils.equals(HlsConstantUtil.ConContractStatus.PENDING, hlsCusConContract.getContractStatus())) {
            throw new HlsCusException("该合同已经暂挂，不能结束!");
        }
        HlsCusConContractCashflow cashflow = new HlsCusConContractCashflow();
        cashflow.setContractId(hlsCusConContract.getContractId());
        List<HlsCusConContractCashflow> cashflowList = hlsCusConContractCashflowService.select(iRequest, cashflow, 0, 0);
        if (CollectionUtils.isEmpty(cashflowList)) {
            throw new HlsCusException("未找到合同下的现金流!");
        }
        for (HlsCusConContractCashflow hlsCusConContractCashflow : cashflowList) {
            if ("INFLOW".equals(hlsCusConContractCashflow.getCfDirection()) && !StringUtils.equals(HlsConstantUtil.WriteOffFlag.FULL, hlsCusConContractCashflow.getWriteOffFlag())) {
                throw new HlsCusException("第" + hlsCusConContractCashflow.getTimes() + "期金额未完全核销，不能结束此合同!");
            }
        }
        HlsCusConContract contract = new HlsCusConContract();
        contract.setContractId(hlsCusConContract.getContractId());
        contract.setContractStatus("TERMINATE");
        contract.setTerminationDate(new Date());
        self().updateByPrimaryKeySelective(iRequest, contract);
    }

    @Override
    public ResponseData conContractChangePrepaymentCashflowExcelImport(IRequest iRequest, Long headerId,Long changeReqId) throws Exception {
        ResponseData responseData = new ResponseData(false);
        FndInterfaceLines fndInterfaceLines = new FndInterfaceLines();
        fndInterfaceLines.setHeaderId(headerId);
        List<FndInterfaceLines> list = fndInterfaceLinesMapper.select(fndInterfaceLines);
        if (CollectionUtils.isEmpty(list)) {
            logger.error("\n\n\n未找到现金流导入的数据! headerId为 {} \n\n\n", headerId);
            responseData.setMessage("数据导入失败");
            return responseData;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(HlsConstantUtil.DateFormatPattern.YEAR_MONTH_DAY);
        Date now = new Date();
        StringBuilder msg = new StringBuilder();
        boolean flag = true;

        //先给每一行一个唯一的编号
        int i = 1;
        for(FndInterfaceLines interfaceLines: list){
            interfaceLines.setAttributes_100(String.valueOf(i));
            i++;
        }

        //筛选出数据行
        List<FndInterfaceLines> cashLines = list.stream().filter(item -> Integer.parseInt(item.getAttributes_100()) > 3).collect(Collectors.toList());

        //数据校验
        Double outstandingPrincipal = 0D;
        Long maxTimes = 0L;
        for(FndInterfaceLines interfaceLines:cashLines){
            //校验期数
            if (StringUtils.isEmpty(interfaceLines.getAttributes_1()) || StringUtils.isEmpty(interfaceLines.getAttributes_1().trim())) {
                logger.error("\n\n\n期数不能为空! lineId为 {} \n\n\n", interfaceLines.getLineId());
                flag = false;
                if (interfaceLines.getAttributes_100() != null) {
                    msg.append("序号: " + interfaceLines.getAttributes_100());
                }
                msg.append("\t期数不能为空!<br/>");
                continue;
            }
            Long times = 0L;
            try {
                times = Long.valueOf(interfaceLines.getAttributes_1().trim());
            } catch (NumberFormatException e) {
                logger.error("\n\n\n期数不是一个数字! lineId为 {}, 错误信息 {} \n\n\n", interfaceLines.getLineId(), e);
                flag = false;
                if (interfaceLines.getAttributes_100() != null) {
                    msg.append("序号: " + interfaceLines.getAttributes_100());
                }
                msg.append("\t期数不是一个数字!<br/>");
                continue;
            }
            if (times.compareTo(0L) < 0) {
                logger.error("\n\n\n期数不能小于0! lineId为 {} \n\n\n", interfaceLines.getLineId());
                flag = false;
                if (interfaceLines.getAttributes_100() != null) {
                    msg.append("序号: " + interfaceLines.getAttributes_100());
                }
                msg.append("\t期数不能小于0!<br/>");
                continue;
            }

            //校验现金流日期
            if (StringUtils.isEmpty(interfaceLines.getAttributes_2()) || StringUtils.isEmpty(interfaceLines.getAttributes_2().trim())) {
                logger.error("\n\n\n现金流日期不能为空! lineId为 {} \n\n\n", interfaceLines.getLineId());
                flag = false;
                if (interfaceLines.getAttributes_100() != null) {
                    msg.append("序号: " + interfaceLines.getAttributes_100());
                }
                msg.append("\t现金流日期不能为空!<br/>");
                continue;
            }
            try {
                Date dueDate = sdf.parse(interfaceLines.getAttributes_2().trim());
                interfaceLines.setAttributes_2(DateUtil.format(dueDate, "yyyy-MM-dd"));
            } catch (ParseException e) {
                logger.error("\n\n\n现金流日期格式错误! lineId为 {}, 错误信息 {} \n\n\n", interfaceLines.getLineId(), e);
                flag = false;
                if (interfaceLines.getAttributes_100() != null) {
                    msg.append("序号: " + interfaceLines.getAttributes_100());
                }
                msg.append("\t现金流日期格式错误: " + interfaceLines.getAttributes_2().trim() + "!<br/>");
                continue;
            }

            //校验现金流类型
            if (StringUtils.isEmpty(interfaceLines.getAttributes_3()) || StringUtils.isEmpty(interfaceLines.getAttributes_3().trim())) {
                logger.error("\n\n\n现金流类型不能为空! lineId为 {} \n\n\n", interfaceLines.getLineId());
                flag = false;
                if (interfaceLines.getAttributes_100() != null) {
                    msg.append("序号: " + interfaceLines.getAttributes_100());
                }
                msg.append("\t现金流类型不能为空!<br/>");
                continue;
            }
            HlsCashflowItem hlsCashflowItem = new HlsCashflowItem();
            hlsCashflowItem.setDescription(interfaceLines.getAttributes_3().trim());
            List<HlsCashflowItem> hlsCashflowItemList = hlsCashflowItemMapper.select(hlsCashflowItem);
            if(hlsCashflowItemList.size() == 0){
                logger.error("\n\n\n现金流类型非法! lineId为 {}, 类型 {}\n\n\n", interfaceLines.getLineId(), interfaceLines.getAttributes_3().trim());
                flag = false;
                if (interfaceLines.getAttributes_100() != null) {
                    msg.append("序号: " + interfaceLines.getAttributes_100());
                }
                msg.append("\t现金流类型 " + interfaceLines.getAttributes_3().trim() + " 非法!<br/>");
                continue;
            }

            //校验现金流金额
            if (StringUtils.isEmpty(interfaceLines.getAttributes_4()) || StringUtils.isEmpty(interfaceLines.getAttributes_4().trim())) {
                logger.error("\n\n\n现金流金额不能为空! lineId为 {} \n\n\n", interfaceLines.getLineId());
                flag = false;
                if (interfaceLines.getAttributes_100() != null) {
                    msg.append("序号: " + interfaceLines.getAttributes_100());
                }
                msg.append("\t现金流金额不能为空!<br/>");
                continue;
            }
            Double dueAmount = 0D;
            try {
                dueAmount = MathUtil.round(Double.valueOf(interfaceLines.getAttributes_4().trim()),2);
            } catch (NumberFormatException e) {
                logger.error("\n\n\n现金流金额不是一个数字! lineId为 {}, 错误信息 {} \n\n\n", interfaceLines.getLineId(), e);
                flag = false;
                if (interfaceLines.getAttributes_100() != null) {
                    msg.append("序号: " + interfaceLines.getAttributes_100());
                }
                msg.append("\t现金流金额不是一个数字!<br/>");
                continue;
            }
            if (dueAmount.compareTo(0D) <= 0) {
                logger.error("\n\n\n现金流金额不能小于等于0! lineId为 {} \n\n\n", interfaceLines.getLineId());
                flag = false;
                if (interfaceLines.getAttributes_100() != null) {
                    msg.append("序号: " + interfaceLines.getAttributes_100());
                }
                msg.append("\t现金流金额不能小于等于0!<br/>");
                continue;
            }

            //校验本金金额
            if (StringUtils.isEmpty(interfaceLines.getAttributes_5()) || StringUtils.isEmpty(interfaceLines.getAttributes_5().trim())) {
                logger.error("\n\n\n本金不能为空! lineId为 {} \n\n\n", interfaceLines.getLineId());
                flag = false;
                if (interfaceLines.getAttributes_100() != null) {
                    msg.append("序号: " + interfaceLines.getAttributes_100());
                }
                msg.append("\t本金不能为空!<br/>");
                continue;
            }
            Double principal = 0D;
            try {
                principal = MathUtil.round(Double.valueOf(interfaceLines.getAttributes_5().trim()),2);
            } catch (NumberFormatException e) {
                logger.error("\n\n\n本金不是一个数字! lineId为 {}, 错误信息 {} \n\n\n", interfaceLines.getLineId(), e);
                flag = false;
                if (interfaceLines.getAttributes_100() != null) {
                    msg.append("序号: " + interfaceLines.getAttributes_100());
                }
                msg.append("\t本金不是一个数字!<br/>");
                continue;
            }
            if (principal.compareTo(0D) < 0) {
                logger.error("\n\n\n本金不能小于0! lineId为 {} \n\n\n", interfaceLines.getLineId());
                flag = false;
                if (interfaceLines.getAttributes_100() != null) {
                    msg.append("序号: " + interfaceLines.getAttributes_100());
                }
                msg.append("\t本金不能小于0!<br/>");
                continue;
            }
            outstandingPrincipal = MathUtil.add(outstandingPrincipal,principal);

            //校验利息金额
            if (StringUtils.isEmpty(interfaceLines.getAttributes_6()) || StringUtils.isEmpty(interfaceLines.getAttributes_6().trim())) {
                logger.error("\n\n\n利息不能为空! lineId为 {} \n\n\n", interfaceLines.getLineId());
                flag = false;
                if (interfaceLines.getAttributes_100() != null) {
                    msg.append("序号: " + interfaceLines.getAttributes_100());
                }
                msg.append("\t利息不能为空!<br/>");
                continue;
            }
            Double interest = 0D;
            try {
                interest = MathUtil.round(Double.valueOf(interfaceLines.getAttributes_6().trim()),2);
            } catch (NumberFormatException e) {
                logger.error("\n\n\n利息不是一个数字! lineId为 {}, 错误信息 {} \n\n\n", interfaceLines.getLineId(), e);
                flag = false;
                if (interfaceLines.getAttributes_100() != null) {
                    msg.append("序号: " + interfaceLines.getAttributes_100());
                }
                msg.append("\t利息不是一个数字!<br/>");
                continue;
            }
            if (interest.compareTo(0D) < 0) {
                logger.error("\n\n\n利息不能小于0! lineId为 {} \n\n\n", interfaceLines.getLineId());
                flag = false;
                if (interfaceLines.getAttributes_100() != null) {
                    msg.append("序号: " + interfaceLines.getAttributes_100());
                }
                msg.append("\t利息不能小于0!<br/>");
                continue;
            }

            //如果是租金，校验本金+利息=现金流金额
            if(hlsCashflowItemList.get(0).getCfItem().equals("1")){
                Double totalAmount = MathUtil.add(principal,interest);
                if(totalAmount.compareTo(dueAmount) != 0){
                    logger.error("\n\n\n本金利息之和不等于租金! lineId为 {} \n\n\n", interfaceLines.getLineId());
                    flag = false;
                    if (interfaceLines.getAttributes_100() != null) {
                        msg.append("序号: " + interfaceLines.getAttributes_100());
                    }
                    msg.append("\t本金利息之和不等于租金!<br/>");
                }else{
                    maxTimes = Math.max(maxTimes,times);
                }
            }
        }

        SysDocumentHistory sysDocumentHistory = new SysDocumentHistory();
        sysDocumentHistory.setDocumentId(changeReqId);
        sysDocumentHistory.setDocumentCategory("CONTRACT_CHANGE");
        sysDocumentHistory = sysDocumentHistoryMapper.selectOne(sysDocumentHistory);

        SysDocumentHistoryDetail sysDocumentHistoryDetailQuery = new SysDocumentHistoryDetail();
        sysDocumentHistoryDetailQuery.setHistoryId(sysDocumentHistory.getHistoryId());
        List<SysDocumentHistoryDetail> sysDocumentHistoryDetailList = sysDocumentHistoryDetailMapper.select(sysDocumentHistoryDetailQuery);

        SysDocumentHistoryDetail changeReqDetail = sysDocumentHistoryDetailList.stream().filter(item -> item.getTableName().equals("con_contract_change_req")).collect(Collectors.toList()).get(0);
        SysDocumentHistoryDetail contractDetail = sysDocumentHistoryDetailList.stream().filter(item -> item.getTableName().equals("con_contract")).collect(Collectors.toList()).get(0);
        SysDocumentHistoryDetail quotationDetail = sysDocumentHistoryDetailList.stream().filter(item -> item.getTableName().equals("prj_quotation")).collect(Collectors.toList()).get(0);

        List<SysDocumentHistoryDetail> cashflowDetailList = sysDocumentHistoryDetailList.stream().filter(item -> item.getTableName().equals("con_contract_cashflow")).collect(Collectors.toList());

        HlsCusConContract hlsCusConContract = conContractMapper.selectByPrimaryKey(Long.valueOf(contractDetail.getTablePkValue()));

        JSONObject changeReqJson = JSONObject.parseObject(changeReqDetail.getHistoryData());
        Long ccrStartTimes = changeReqJson.getLong("ccr_start_times");
        JSONArray cashflowArray = new JSONArray();
        for(SysDocumentHistoryDetail sysDocumentHistoryDetail:cashflowDetailList){
            JSONObject cashflowJson = JSONObject.parseObject(sysDocumentHistoryDetail.getHistoryData());
            cashflowArray.add(cashflowJson);
        }
        //获取变更起始期上一期的剩余本金，与导入现金流的本金对比
        Double outstandingPrincipalLast = ((JSONObject)cashflowArray.stream().filter(item -> ((JSONObject)item).getLong("times") == ccrStartTimes - 1&&((JSONObject)item).getLong("cf_item") == 1L).collect(Collectors.toList()).get(0)).getDouble("outstanding_principal");
        if(outstandingPrincipal.compareTo(outstandingPrincipalLast) != 0){
            logger.error("\n\n\n变更现金流本金之和不等于剩余本金! headId为 {} \n\n\n", fndInterfaceLines.getHeaderId());
            flag = false;
            msg.append("导入变更现金流本金之和: " + outstandingPrincipal);
            msg.append("\t正确剩余本金:" + outstandingPrincipalLast + "<br/>");
        }


        //校验不通过不执行导入操作
        if(!flag){
            responseData.setSuccess(false);
            responseData.setMessage(msg.toString());
            return responseData;
        }

        //删除历史表当前变更后数据
        for(SysDocumentHistoryDetail sysDocumentHistoryDetail:cashflowDetailList){
            JSONObject cashJson = JSONObject.parseObject(sysDocumentHistoryDetail.getHistoryData());
            Long times = cashJson.getLong("times");
            if(times >= ccrStartTimes){
                if("insert".equals(cashJson.getString("_status"))){
                    sysDocumentHistoryDetailMapper.deleteByPrimaryKey(sysDocumentHistoryDetail);
                }else if(!"delete".equals(cashJson.getString("_status"))){
                    cashJson.put("_status","delete");
                    sysDocumentHistoryDetail.setHistoryData(cashJson.toJSONString());
                    sysDocumentHistoryDetailMapper.updateByPrimaryKeySelective(sysDocumentHistoryDetail);
                }
            }
        }

        //税率
        Double vatRate = JSONObject.parseObject(quotationDetail.getHistoryData()).getDouble("vat_rate");
        //单据类型
        String businessType = JSONObject.parseObject(quotationDetail.getHistoryData()).getString("business_type");
        List<Map> cfDirections = sysCodeValueMapper.queryCodeDetails("HLS.CASHFLOW_DIRECTION");
        List<Map> cfStatus = sysCodeValueMapper.queryCodeDetails("CON.CF_STATUS");
        List<Map> writeOffFlags = sysCodeValueMapper.queryCodeDetails("CON.CASHFLOW_WRITE_OFF_FLAG");

        String depositDeduction = hlsCusConContract.getDepositDeduction();
        Double residualValue = hlsCusConContract.getResidualValue();
        Double netResidualValue = MathUtil.div(residualValue,MathUtil.add(1D,vatRate,2),2);
        Double vatResidualValue = MathUtil.sub(residualValue,netResidualValue,2);
        List<Object> depositList = cashflowArray.stream().filter(item -> ((JSONObject)item).getLong("times") == 0L&&((JSONObject)item).getLong("cf_item") == 51L).collect(Collectors.toList());
        Double depositAmount = 0D;
        if(depositList.size() > 0L){
            depositAmount = ((JSONObject)depositList.get(0)).getDouble("due_amount");
        }

        //保证金均摊
        List<Map> depositMaps = new ArrayList<>();
        if(Double.compare(depositAmount,0D) > 0&&"PERIOD_FINAL_DEDUCTIBLE".equals(depositDeduction)){
            Double currentDeposit = depositAmount;
            for(int k = 0;k < maxTimes;k++){
                final Long currentTimes = maxTimes - k;
                Double dueAmount = Double.valueOf(((FndInterfaceLines)cashLines.stream().filter(item -> Long.valueOf(item.getAttributes_1().trim()) == currentTimes&&"租金".equals(item.getAttributes_3().trim())).collect(Collectors.toList()).get(0)).getAttributes_4().trim());
                if(Double.compare(currentDeposit,dueAmount) > 0D){
                    Map map = new HashMap();
                    map.put("times",currentTimes);
                    map.put("depositAmount",dueAmount);
                    depositMaps.add(map);
                    currentDeposit = MathUtil.sub(currentDeposit,dueAmount);
                }else{
                    Map map = new HashMap();
                    map.put("times",currentTimes);
                    map.put("depositAmount",currentDeposit);
                    depositMaps.add(map);
                    break;
                }
            }
        }

        //保证金
        //导入数据插入历史表
        for(FndInterfaceLines interfaceLines:cashLines){
            //期数
            Long times = Long.valueOf(interfaceLines.getAttributes_1().trim());
            if(times >= ccrStartTimes){
                //现金流日期
                String dueDate = interfaceLines.getAttributes_2().trim();

                //现金流类型
                HlsCashflowItem hlsCashflowItem = new HlsCashflowItem();
                hlsCashflowItem.setDescription(interfaceLines.getAttributes_3().trim());
                List<HlsCashflowItem> hlsCashflowItemList = hlsCashflowItemMapper.select(hlsCashflowItem);
                Long cfItem = Long.valueOf(hlsCashflowItemList.get(0).getCfItem());
                Long cfType = Long.valueOf(hlsCashflowItemList.get(0).getCfType());
                String cfItemN = hlsCashflowItemList.get(0).getDescription();
                String cfDirection = hlsCashflowItemList.get(0).getCfDirection();

                //现金流金额，本金，利息
                Double dueAmount = new BigDecimal(interfaceLines.getAttributes_4().trim()).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
                Double principal = new BigDecimal(interfaceLines.getAttributes_5().trim()).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
                Double interest = new BigDecimal(interfaceLines.getAttributes_6().trim()).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
                Double vatPrincipal = 0D;
                Double netPrincipal = 0D;
                Double vatInterest = 0D;
                Double netInterest = 0D;
                Double vatDueAmount = 0D;
                Double netDueAmount = 0D;

                //本金(不含税)
                if("LEASE".equals(businessType)){
                    netPrincipal = MathUtil.div(principal,MathUtil.add(1D,vatRate,2),2);
                }else{
                    netPrincipal = principal;
                }
                //本金(税额)
                vatPrincipal = MathUtil.sub(principal,netPrincipal,2);

                //利息（不含税）
                netInterest = MathUtil.div(interest,MathUtil.add(1D,vatRate,2),2);
                //利息（税额）
                vatInterest = MathUtil.sub(interest,netInterest,2);
                //租金(不含税)
                netDueAmount = MathUtil.add(netPrincipal,netInterest,2);
                //租金(税额)
                vatDueAmount = MathUtil.add(vatPrincipal,vatInterest, 2);

                JSONObject newCashflowObject = new JSONObject();
                newCashflowObject.put("contract_id",Long.valueOf(contractDetail.getTablePkValue()));
                newCashflowObject.put("quotation_id",Long.valueOf(quotationDetail.getTablePkValue()));
                newCashflowObject.put("cf_item",cfItem);
                newCashflowObject.put("cf_item_n",cfItemN);
                newCashflowObject.put("cf_type",cfType);
                newCashflowObject.put("cf_direction",cfDirection);
                cfDirections.forEach(item -> {
                    if (item.get("code_value").equals(newCashflowObject.get("cf_direction").toString())) {
                        newCashflowObject.put("cf_direction_n", item.get("meaning"));
                    }
                });
                newCashflowObject.put("cf_status","RELEASE");
                cfStatus.forEach(item -> {
                    if (item.get("code_value").equals(newCashflowObject.get("cf_status").toString())) {
                        newCashflowObject.put("cf_status_n", item.get("meaning"));
                    }
                });
                newCashflowObject.put("times",times);
                newCashflowObject.put("due_date",dueDate);
                newCashflowObject.put("calc_date",dueDate);
                newCashflowObject.put("fin_income_date",dueDate);
                newCashflowObject.put("due_amount",dueAmount);
                newCashflowObject.put("net_due_amount",netDueAmount);
                newCashflowObject.put("vat_due_amount",vatDueAmount);
                newCashflowObject.put("principal",principal);
                newCashflowObject.put("net_principal",netPrincipal);
                newCashflowObject.put("vat_principal",vatPrincipal);
                newCashflowObject.put("interest",interest);
                newCashflowObject.put("net_interest",netInterest);
                newCashflowObject.put("vat_interest",vatInterest);
                newCashflowObject.put("write_off_flag","NOT");
                writeOffFlags.forEach(item -> {
                    if (item.get("code_value").equals(newCashflowObject.get("write_off_flag").toString())) {
                        newCashflowObject.put("write_off_flag_n", item.get("meaning"));
                    }
                });
                newCashflowObject.put("received_amount",0D);
                newCashflowObject.put("received_principal",0D);
                newCashflowObject.put("received_interest",0D);
                outstandingPrincipal = MathUtil.sub(outstandingPrincipal,principal);
                newCashflowObject.put("outstanding_principal",outstandingPrincipal);
                newCashflowObject.put("generated_source",ConContractCashflow.SOURCE_PRJ_QUOTATION);
                newCashflowObject.put("generated_source_doc_id",Long.valueOf(quotationDetail.getTablePkValue()));
                HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflowQuery = new HlsCusPrjQuotationCashflow();
                hlsCusPrjQuotationCashflowQuery.setQuotationId(Long.valueOf(quotationDetail.getTablePkValue()));
                hlsCusPrjQuotationCashflowQuery.setTimes(times);
                hlsCusPrjQuotationCashflowQuery.setCfItem(1L);
                List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationCashflowList = hlsCusPrjQuotationCashflowMapper.select(hlsCusPrjQuotationCashflowQuery);
                if(hlsCusPrjQuotationCashflowList.size() > 0){
                    newCashflowObject.put("generated_source_doc_line_id",hlsCusPrjQuotationCashflowList.get(0).getQuotationCashflowId());
                }
                newCashflowObject.put("change_flag","Y");
                newCashflowObject.put("overdue_status","N");
                newCashflowObject.put("penalty_process_status","N");
                newCashflowObject.put("billing_status","NOT");
                newCashflowObject.put("program_id",-1L);
                newCashflowObject.put("request_id",-1);
                newCashflowObject.put("_tls",new JSONObject());
                newCashflowObject.put("inner_map",new JSONObject());
                newCashflowObject.put("_status", "insert");

                SysDocumentHistoryDetail documentHistoryDetail = new SysDocumentHistoryDetail();
                documentHistoryDetail.setParentTableName("prj_quotation");
                documentHistoryDetail.setParentPkValue(quotationDetail.getTablePkValue());
                documentHistoryDetail.setTableName("con_contract_cashflow");
                documentHistoryDetail.setTablePkValue(UUID.randomUUID().toString());
                documentHistoryDetail.setHistoryData(newCashflowObject.toJSONString());
                documentHistoryDetail.setHistoryId(sysDocumentHistory.getHistoryId());
                sysDocumentHistoryDetailMapper.insertSelective(documentHistoryDetail);

                if(Double.compare(residualValue,0D) > 0&&times == maxTimes&&cfItem == 1L){
                    JSONObject newResidualCashflowObject = newCashflowObject;

                    HlsCashflowItem cashflowItem = new HlsCashflowItem();
                    cashflowItem.setCfItem("8");
                    cashflowItem = hlsCashflowItemMapper.selectOne(cashflowItem);

                    newResidualCashflowObject.put("cf_item",Long.valueOf(cashflowItem.getCfItem()));
                    newResidualCashflowObject.put("cf_item_n",cashflowItem.getDescription());
                    newResidualCashflowObject.put("cf_type",Long.valueOf(cashflowItem.getCfType()));
                    newResidualCashflowObject.put("cf_direction",cashflowItem.getCfDirection());
                    cfDirections.forEach(item -> {
                        if (item.get("code_value").equals(newResidualCashflowObject.get("cf_direction").toString())) {
                            newResidualCashflowObject.put("cf_direction_n", item.get("meaning"));
                        }
                    });
                    newResidualCashflowObject.put("due_amount",residualValue);
                    newResidualCashflowObject.put("net_due_amount",netResidualValue);
                    newResidualCashflowObject.put("vat_due_amount",vatResidualValue);
                    newResidualCashflowObject.put("principal",0D);
                    newResidualCashflowObject.put("net_principal",0D);
                    newResidualCashflowObject.put("vat_principal",0D);
                    newResidualCashflowObject.put("interest",0D);
                    newResidualCashflowObject.put("net_interest",0D);
                    newResidualCashflowObject.put("vat_interest",0D);

                    SysDocumentHistoryDetail residualDocumentHistoryDetail = new SysDocumentHistoryDetail();
                    residualDocumentHistoryDetail.setParentTableName("prj_quotation");
                    residualDocumentHistoryDetail.setParentPkValue(quotationDetail.getTablePkValue());
                    residualDocumentHistoryDetail.setTableName("con_contract_cashflow");
                    residualDocumentHistoryDetail.setTablePkValue(UUID.randomUUID().toString());
                    residualDocumentHistoryDetail.setHistoryData(newResidualCashflowObject.toJSONString());
                    residualDocumentHistoryDetail.setHistoryId(sysDocumentHistory.getHistoryId());
                    sysDocumentHistoryDetailMapper.insertSelective(residualDocumentHistoryDetail);
                }

                if(Double.compare(depositAmount,0D) > 0){
                    if("PERIOD_FINAL_RETURN".equals(depositDeduction)&&times == maxTimes&&cfItem == 1L){
                        JSONObject newDepositCashflowObject = newCashflowObject;

                        HlsCashflowItem cashflowItem = new HlsCashflowItem();
                        cashflowItem.setCfItem("52");
                        cashflowItem = hlsCashflowItemMapper.selectOne(cashflowItem);

                        newDepositCashflowObject.put("cf_item",Long.valueOf(cashflowItem.getCfItem()));
                        newDepositCashflowObject.put("cf_item_n",cashflowItem.getDescription());
                        newDepositCashflowObject.put("cf_type",Long.valueOf(cashflowItem.getCfType()));
                        newDepositCashflowObject.put("cf_direction",cashflowItem.getCfDirection());
                        cfDirections.forEach(item -> {
                            if (item.get("code_value").equals(newDepositCashflowObject.get("cf_direction").toString())) {
                                newDepositCashflowObject.put("cf_direction_n", item.get("meaning"));
                            }
                        });
                        newDepositCashflowObject.put("due_amount",depositAmount);
                        newDepositCashflowObject.put("net_due_amount",0D);
                        newDepositCashflowObject.put("vat_due_amount",0D);
                        newDepositCashflowObject.put("principal",0D);
                        newDepositCashflowObject.put("net_principal",0D);
                        newDepositCashflowObject.put("vat_principal",0D);
                        newDepositCashflowObject.put("interest",0D);
                        newDepositCashflowObject.put("net_interest",0D);
                        newDepositCashflowObject.put("vat_interest",0D);

                        SysDocumentHistoryDetail depositDocumentHistoryDetail = new SysDocumentHistoryDetail();
                        depositDocumentHistoryDetail.setParentTableName("prj_quotation");
                        depositDocumentHistoryDetail.setParentPkValue(quotationDetail.getTablePkValue());
                        depositDocumentHistoryDetail.setTableName("con_contract_cashflow");
                        depositDocumentHistoryDetail.setTablePkValue(UUID.randomUUID().toString());
                        depositDocumentHistoryDetail.setHistoryData(newDepositCashflowObject.toJSONString());
                        depositDocumentHistoryDetail.setHistoryId(sysDocumentHistory.getHistoryId());
                        sysDocumentHistoryDetailMapper.insertSelective(depositDocumentHistoryDetail);
                    }else if("PERIOD_FINAL_DEDUCTIBLE".equals(depositDeduction)&&cfItem == 1L){
                        List<Map> maps = depositMaps.stream().filter(item -> Long.valueOf(item.get("times").toString()) == times).collect(Collectors.toList());
                        if(maps.size() > 0){
                            JSONObject newDepositCashflowObject = newCashflowObject;

                            HlsCashflowItem cashflowItem = new HlsCashflowItem();
                            cashflowItem.setCfItem("52");
                            cashflowItem = hlsCashflowItemMapper.selectOne(cashflowItem);

                            newDepositCashflowObject.put("cf_item",Long.valueOf(cashflowItem.getCfItem()));
                            newDepositCashflowObject.put("cf_item_n",cashflowItem.getDescription());
                            newDepositCashflowObject.put("cf_type",Long.valueOf(cashflowItem.getCfType()));
                            newDepositCashflowObject.put("cf_direction",cashflowItem.getCfDirection());
                            cfDirections.forEach(item -> {
                                if (item.get("code_value").equals(newDepositCashflowObject.get("cf_direction").toString())) {
                                    newDepositCashflowObject.put("cf_direction_n", item.get("meaning"));
                                }
                            });
                            newDepositCashflowObject.put("due_amount",Double.valueOf(maps.get(0).get("depositAmount").toString()));
                            newDepositCashflowObject.put("net_due_amount",0D);
                            newDepositCashflowObject.put("vat_due_amount",0D);
                            newDepositCashflowObject.put("principal",0D);
                            newDepositCashflowObject.put("net_principal",0D);
                            newDepositCashflowObject.put("vat_principal",0D);
                            newDepositCashflowObject.put("interest",0D);
                            newDepositCashflowObject.put("net_interest",0D);
                            newDepositCashflowObject.put("vat_interest",0D);

                            SysDocumentHistoryDetail depositDocumentHistoryDetail = new SysDocumentHistoryDetail();
                            depositDocumentHistoryDetail.setParentTableName("prj_quotation");
                            depositDocumentHistoryDetail.setParentPkValue(quotationDetail.getTablePkValue());
                            depositDocumentHistoryDetail.setTableName("con_contract_cashflow");
                            depositDocumentHistoryDetail.setTablePkValue(UUID.randomUUID().toString());
                            depositDocumentHistoryDetail.setHistoryData(newDepositCashflowObject.toJSONString());
                            depositDocumentHistoryDetail.setHistoryId(sysDocumentHistory.getHistoryId());
                            sysDocumentHistoryDetailMapper.insertSelective(depositDocumentHistoryDetail);
                        }
                    }
                }
            }
        }

        JSONArray configArray = new JSONArray();
        JSONObject configJson = new JSONObject();
        configJson.put("field","lease_charge");
        configJson.put("value",changeReqJson.getDouble("lease_charge"));
        configArray.add(configJson);
        //更新变更后Xirr
        conContractChangeReqService.updateDocumentHistoryChangeReqPartialPrepayment(iRequest,changeReqId,"CONTRACT_CHANGE",Long.valueOf(quotationDetail.getTablePkValue()),changeReqId,configArray);
        msg.append("导入成功");
        responseData.setSuccess(flag);
        responseData.setMessage(msg.toString());
        return responseData;
    }
}
