package com.hand.hls.eft.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.hand.hap.attachment.dto.Attachment;
import com.hand.hap.attachment.exception.AttachmentException;
import com.hand.hap.attachment.exception.FileReadIOException;
import com.hand.hap.attachment.service.IAttachmentService;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.TokenException;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.ICodeService;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.service.IFndAttachmentMultiService;
import com.hand.hls.atm.service.IFndAttachmentService;
import com.hand.hls.bp.dto.HlsCusSysFile;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.bp.service.HlsSysFileService;
import com.hand.hls.cont.dto.HlsDocFileTemplet;
import com.hand.hls.cont.service.HlsDocFileTempletService;
import com.hand.hls.csh.dto.HlsCusCshBankAccount;
import com.hand.hls.csh.mapper.HlsCusCshBankAccountMapper;
import com.hand.hls.eft.dto.HlsCusFundTransfer;
import com.hand.hls.eft.dto.HlsCusFundTransferList;
import com.hand.hls.eft.mapper.HlsCusFundTransferListMapper;
import com.hand.hls.eft.service.HlsCusFundTransferListService;
import com.hand.hls.eft.service.HlsCusFundTransferService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.prj.utils.HlsCusZipUtil;
import com.hand.hls.utils.ExportExcelUtil;
import com.hand.hls.utils.HlsCusConstant;
import com.hand.hls.utils.HlsCusDownloadDocxUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;
import java.util.zip.ZipOutputStream;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusFundTransferListServiceImpl extends BaseServiceImpl<HlsCusFundTransferList> implements HlsCusFundTransferListService {

    @Autowired
    private HlsCusFundTransferListMapper fundTransferListMapper;

    @Autowired
    private HlsCusFundTransferService fundTransferService;

    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;

    @Autowired
    private FndCodingRuleValuesService codingRuleValuesService;

    @Autowired
    private ICodeService codeService;

    @Autowired
    private IAttachmentService attachmentService;

    @Autowired
    private HlsSysFileService fileService;

    @Autowired
    private HlsDocFileTempletService hlsDocFileTempletService;


    @Autowired
    private HlsCusCshBankAccountMapper cshBankAccountMapper;


    @Override
    public List<HlsCusFundTransferList> selectFundTransferList(IRequest iRequest, HlsCusFundTransferList fundTransferList, int page, int pageSize) {

        PageHelper.startPage(page,pageSize);

        return fundTransferListMapper.selectFundTransferList(fundTransferList);
    }


    @Override
    public List<HlsCusFundTransferList> selectFundTransferListDeatil(IRequest iRequest, HlsCusFundTransferList fundTransferList, int page, int pageSize) {

        PageHelper.startPage(page,pageSize);

        return fundTransferListMapper.selectFundTransferListDeatil(fundTransferList);
    }

    @Override
    public List<HlsCusFundTransferList> selectFundTransferFinanceList(IRequest iRequest, HlsCusFundTransferList fundTransferList, int page, int pageSize) {

        PageHelper.startPage(page,pageSize);

        return fundTransferListMapper.selectFundTransferFinanceList(fundTransferList);
    }

    @Override
    public List<HlsCusFundTransferList> selectTransferTaskList(IRequest iRequest, HlsCusFundTransferList fundTransferList, int page, int pageSize) {

        PageHelper.startPage(page,pageSize);

        return fundTransferListMapper.selectTransferTaskList(fundTransferList);
    }


    @Override
    public List<HlsCusFundTransferList> selectCopyFundTransferList(IRequest iRequest, HlsCusFundTransferList fundTransferList, int page, int pageSize) {

        PageHelper.startPage(page,pageSize);

        return fundTransferListMapper.selectCopyFundTransferList(fundTransferList);
    }


    @Override
    public List<HlsCusFundTransferList> selectApproveTransferList(IRequest iRequest, HlsCusFundTransferList fundTransferList, int page, int pageSize) {

        PageHelper.startPage(page,pageSize);

        return fundTransferListMapper.selectApproveTransferList(fundTransferList);
    }

    @Override
    public List<HlsCusFundTransferList> batchUpdateTransferList(IRequest iRequest, List<HlsCusFundTransferList> hlsCusFundTransferLists) {
        HlsCusFundTransfer transfer=new HlsCusFundTransfer();
        transfer.setTransferId(hlsCusFundTransferLists.get(0).getTransferId());
        transfer=fundTransferService.selectByPrimaryKey(iRequest,transfer);

        for(HlsCusFundTransferList fundTransferList:hlsCusFundTransferLists){
            if(DTOStatus.ADD.equals(fundTransferList.get__status())) {
                fundTransferList.setTransferNumber(transfer.getApplyNumber() + fundTransferListMapper.selectTransferNumberMax(transfer.getTransferId()));
            }
        }
        return self().batchUpdate(iRequest,hlsCusFundTransferLists);
    }

    @Override
    public List<HlsCusFundTransferList> createTransferListFund(IRequest iRequest, List<HlsCusFundTransferList> hlsCusFundTransferLists) throws HlsCusException {

        //新增资金调拨
        final HlsCusFundTransfer fundTransfer=new HlsCusFundTransfer();
        String value = codingRuleValuesService.getCodeRuleValue(iRequest, HlsCusConstant.TRANSFER_BUSINESS_TYPE.FUND_TRANSFER, HlsCusConstant.TRANSFER_BUSINESS_TYPE.FUND_TRANSFER,HlsCusConstant.TRANSFER_BUSINESS_TYPE.FUND_TRANSFER, new HashMap<>());
        fundTransfer.setApplyNumber(value);
        fundTransfer.setApplyDate(new Date());
        fundTransfer.setApplyStatus(HlsCusConstant.WORKFLOW_STATUS.NEW);
        fundTransfer.setCompanyId(iRequest.getCompanyId());
        fundTransfer.setUnitId(Long.parseLong(iRequest.getAttribute(HlsCusFundTransfer.FIELD_UNIT_ID)));
        fundTransfer.setDataClass(HlsCusConstant.DATA_CLASS.NORMAL);
        fundTransfer.setBusinessType(HlsCusConstant.TRANSFER_BUSINESS_TYPE.FUND_TYPE);
        fundTransfer.setCreatedBy(iRequest.getUserId());
        fundTransfer.setLastUpdatedBy(iRequest.getUserId());
        fundTransferService.insertSelective(iRequest,fundTransfer);
       //
        for(HlsCusFundTransferList fundTransferList:hlsCusFundTransferLists){
            fundTransferList.setTransferId(fundTransfer.getTransferId());
            fundTransferList.setDataClass(HlsCusConstant.DATA_CLASS.NORMAL);
            fundTransferList.setTransferStatus(HlsCusConstant.WORKFLOW_STATUS.NEW);
            fundTransferList.setIsOnceWriteOff(HlsCusConstant.FLAG.Y);
            fundTransferList.setApplyPayAmount(fundTransferList.getPlannedDueAmount());
            fundTransferList.setApplyPayDate(fundTransferList.getPlannedDueDate());
            fundTransferList.setCreatedBy(iRequest.getUserId());
            fundTransferList.setLastUpdatedBy(iRequest.getUserId());
            fundTransferList.setLastUpdateLogin(iRequest.getUserId());
            //银行借款
            if(HlsCusConstant.TRANSFER_LIST_CATEGORY.LON_CONTRACT_REPAYMENT.equals(fundTransferList.getSourceDocCategory())){
//                if(fundTransferList.getOutBankAccountId()==null){
//                    throw new HlsCusException("请维护租金回款户："+fundTransferList.getSourceDocNumber());
//                }
                if(HlsCusConstant.CASHFLOW_ITEM.LON_PRINCIPAL_CF_ITEM.equals(fundTransferList.getCfItem())){
                    if(fundTransferList.getInBankAccountNum()==null) {
                        throw new HlsCusException("请维护融资还本户：" + fundTransferList.getSourceDocNumber());
                    }
                }
                if(HlsCusConstant.CASHFLOW_ITEM.LON_INTEREST_CF_ITEM.equals(fundTransferList.getCfItem())){
                    if(fundTransferList.getInBankAccountNum()==null) {
                        throw new HlsCusException("请维护融资还息户：" + fundTransferList.getSourceDocNumber());
                    }
                }
//                String accountSource=getAccountSource(fundTransferList);
                //调拨
                if(null ==fundTransferList.getOutBankAccountId()){
                    throw new HlsCusException("请维护租金回款户："+fundTransferList.getSourceDocNumber());
                }
                if((HlsCusConstant.ACCOUNT_SOURCE.OUR_ACCOUNT.equalsIgnoreCase(fundTransferList.getAccountSource()))&&(!fundTransferList.getOutBankAccountId().equals(fundTransferList.getInBankAccountId()))){
                    final HlsCusFundTransferList transferList=new HlsCusFundTransferList();
                    Map<String, String> transferMap = hlsBeanRefUtilService.getFieldValueMap(fundTransferList);
                    hlsBeanRefUtilService.setFieldValue(transferList, transferMap);
                    transferList.setExchangeRate(fundTransferList.getExchangeRate());
                    transferList.setPlannedDueAmount(fundTransferList.getPlannedDueAmount());
                    transferList.setApplyPayAmount(fundTransferList.getPlannedDueAmount());
                    transferList.setTransferType(HlsCusConstant.TRANSFER_TYPE.TRANSFER);
                    transferList.setApplyPurpose(HlsCusConstant.TRANSFER_PURPOSE.TRANSFER);
                    transferList.setCreatedBy(iRequest.getUserId());
                    transferList.setLastUpdatedBy(iRequest.getUserId());
                    transferList.setLastUpdateLogin(iRequest.getUserId());
                    transferList.setTransferNumber(fundTransfer.getApplyNumber()+fundTransferListMapper.selectTransferNumberMax(fundTransfer.getTransferId()));
                    fundTransferListMapper.insertSelective(transferList);
                }

                //对方账户
                if(HlsCusConstant.ACCOUNT_SOURCE.RECIPROCAL_ACCOUNT.equals(fundTransferList.getAccountSource())){
                    //默认基本户
                    HlsCusCshBankAccount bankAccount=new HlsCusCshBankAccount();
                    bankAccount.setBankAccountType("BASIC");
                    bankAccount.setCompanyId(iRequest.getCompanyId());
                    bankAccount = cshBankAccountMapper.selectOneData(bankAccount);
                    if(null!=bankAccount) {
                        fundTransferList.setOutBankAccountId(bankAccount.getBankAccountId());
                        fundTransferList.setOutBankAccountName(bankAccount.getBankAccountName());
                        fundTransferList.setOutBankBranchName(bankAccount.getBankBranchName());
                        fundTransferList.setOutBankAccountNum(bankAccount.getBankAccountNum());
                    }
                }else{
                    fundTransferList.setOutBankAccountId(fundTransferList.getInBankAccountId());
                    fundTransferList.setOutBankAccountName(fundTransferList.getInBankAccountName());
                    fundTransferList.setOutBankBranchName(fundTransferList.getInBankBranchName());
                    fundTransferList.setOutBankAccountNum(fundTransferList.getInBankAccountNum());
                    fundTransferList.setInBankAccountId(null);
                    fundTransferList.setInBankAccountName(null);
                    fundTransferList.setInBankBranchName(null);
                    fundTransferList.setInBankAccountNum(null);
                }

            }
            fundTransferList.setTransferType(HlsCusConstant.TRANSFER_TYPE.PAY);
            fundTransferList.setTransferNumber(fundTransfer.getApplyNumber()+fundTransferListMapper.selectTransferNumberMax(fundTransfer.getTransferId()));
            fundTransferListMapper.insertSelective(fundTransferList);
        }
        return hlsCusFundTransferLists;
    }

    private String getAccountSource(HlsCusFundTransferList fundTransferList) {
        Assert.notNull(fundTransferList.getSourceDocId(),"参数错误,不能为空");
        Long cfItemL=fundTransferList.getCfItem();
        String cfItem = "";
        if (cfItemL==310) {
            cfItem = "FINANCING_EXPENSE_ACCOUNT";
        } else if (cfItemL==320) {
            cfItem = "FINANCING_INTEREST_ACCOUNT";
        } else if (cfItemL==301) {
            cfItem = "FINANCING_ACCOUNT";
        }
        if (StringUtils.isNotEmpty(cfItem)){
            return fundTransferListMapper.selectAccountSource(cfItem,fundTransferList.getSourceDocId());
        }
        return "";
    }

    @Override
    public void exportTransferList(HttpServletRequest request, HttpServletResponse response, HlsCusFundTransferList fundTransferList) throws IOException, InvocationTargetException, IllegalAccessException {

      final List<String> colNameList = Lists.newArrayList(
                "调拨单编号", "申请币种", "申请支付金额(元)","汇率", "申请支付日期","实际支付金额(元)","实际支付日期","拨出账户","拨出账号",
                           "拨出账户开户行","拨入账户","拨入账号","拨入账户开户行","申请支付用途","用途说明","理财产品名称","提款/产品编号","主合同编号",
      "合同名称/产品简称","机构名称","期数","现金流类型","应还日期","原币币种","原币应还款金额（元）","本币应还款金额（元）","状态","备注");
      final List<String> colGetMethods = Lists.newArrayList(
              "transferNumber", "applyCurrencyCode", "applyPayAmount","exchangeRate","applyPayDate","actualPayAmount","actualPayDate", "outBankAccountName","outBankAccountNum",
              "outBankBranchName","inBankAccountName","inBankAccountNum","inBankBranchName","applyPurpose","purposeDescription", "financialCode","sourceDocNumber","majorContractNumber",
              "sourceDocName","organizationName","times","cfItemDesc","plannedDueDate","plannedCurrencyCode","plannedDueAmount",
              "cnyPlannedDueAmount","transferStatus","description");
        XSSFWorkbook xwork = new XSSFWorkbook();
        XSSFSheet sheet = xwork.createSheet("sheet1");
        int dataRowNum = ExportExcelUtil.createCommonExcelHead(xwork, sheet, null, colNameList);
        if(HlsCusConstant.TRANSFER_BUSINESS_TYPE.FINANCE_TYPE.equals(fundTransferList.getBusinessType())){
            fundTransferList.setFinTransferId(fundTransferList.getTransferId());
            fundTransferList.setTransferId(null);
        }else{
            fundTransferList.setFinTransferId(null);
        }
        List<HlsCusFundTransferList> fundTransferLists = fundTransferListMapper.selectFundTransferList(fundTransferList);
        for (HlsCusFundTransferList transferList : fundTransferLists) {
            XSSFRow row = sheet.createRow(dataRowNum++);
            transferList.setApplyPurpose(codeService.getCodeMeaningByValue(RequestHelper.getCurrentRequest(),"FT.PAYMENT_PURPOSE",transferList.getApplyPurpose()));
            transferList.setApplyCurrencyCode(codeService.getCodeMeaningByValue(RequestHelper.getCurrentRequest(),"FCT.ACCOUNT_CURRENCY",transferList.getApplyCurrencyCode()));
            transferList.setPlannedCurrencyCode(codeService.getCodeMeaningByValue(RequestHelper.getCurrentRequest(),"FCT.ACCOUNT_CURRENCY",transferList.getPlannedCurrencyCode()));
            transferList.setTransferStatus(codeService.getCodeMeaningByValue(RequestHelper.getCurrentRequest(),"HLS_EFT_CONFIRM_STATUS",transferList.getTransferStatus()));
            ExportExcelUtil.setData(xwork, sheet, row, transferList, colGetMethods);
        }
        ExportExcelUtil.IOWrite(xwork, null, request, response, "资金调拨单");
    }

    @Override
    public HlsCusFundTransfer createTransferListFinance(IRequest iRequest, List<HlsCusFundTransferList> hlsCusFundTransferLists) throws HlsCusException {
        //新增资金调拨
        final HlsCusFundTransfer fundTransfer=new HlsCusFundTransfer();
        String value = codingRuleValuesService.getCodeRuleValue(iRequest, HlsCusConstant.TRANSFER_BUSINESS_TYPE.FUND_TRANSFER, HlsCusConstant.TRANSFER_BUSINESS_TYPE.FUND_TRANSFER,HlsCusConstant.TRANSFER_BUSINESS_TYPE.FUND_TRANSFER, new HashMap<>());
        fundTransfer.setApplyNumber(value);
        fundTransfer.setApplyDate(new Date());
        fundTransfer.setApplyStatus(HlsCusConstant.WORKFLOW_STATUS.NEW);
        fundTransfer.setCompanyId(iRequest.getCompanyId());
        fundTransfer.setUnitId(Long.parseLong(iRequest.getAttribute(HlsCusFundTransfer.FIELD_UNIT_ID)));
        fundTransfer.setDataClass(HlsCusConstant.DATA_CLASS.NORMAL);
        fundTransfer.setBusinessType(HlsCusConstant.TRANSFER_BUSINESS_TYPE.FINANCE_TYPE);
        fundTransfer.setCreatedBy(iRequest.getUserId());
        fundTransfer.setLastUpdatedBy(iRequest.getUserId());
        fundTransferService.insertSelective(iRequest,fundTransfer);

        for(HlsCusFundTransferList fundTransferList:hlsCusFundTransferLists){
            fundTransferList.setFinTransferId(fundTransfer.getTransferId());
            fundTransferList.setActualPayAmount(fundTransferList.getApplyPayAmount());
            fundTransferList.setActualPayDate(fundTransferList.getApplyPayDate());
            fundTransferListMapper.updateByPrimaryKeySelective(fundTransferList);
        }
        return fundTransfer;
    }


    @Override
    public void cancelTransferListFinance(IRequest iRequest, List<HlsCusFundTransferList> hlsCusFundTransferLists) throws HlsCusException{
        for(HlsCusFundTransferList transferList:hlsCusFundTransferLists){
            HlsCusFundTransferList beforeTransfer = fundTransferListMapper.selectByPrimaryKey(transferList.getTransferListId());
            if(!HlsCusConstant.WORKFLOW_STATUS.NEW.equals(beforeTransfer.getTransferStatus())){
                throw new HlsCusException("该单据不可以作废，请刷新！");
            }
            if(beforeTransfer.getFinTransferId()!=null&&!new Long(0L).equals(beforeTransfer.getFinTransferId())){
                throw new HlsCusException("该单据财务已经创建了调拨单，请检查！");
            }
            transferList.setTransferStatus(HlsCusConstant.WORKFLOW_STATUS.CANCEL);
            transferList.set__status(DTOStatus.UPDATE);
        }
        self().batchUpdate(iRequest,hlsCusFundTransferLists);
    }

    @Override
    public List<HlsCusFundTransferList> selectAccountBalanceData(HlsCusFundTransferList fundTransferList) {
        return fundTransferListMapper.selectAccountBalanceData(fundTransferList);
    }

    @Override
    public List<HlsCusFundTransferList> createTransferListGap(IRequest iRequest, List<HlsCusFundTransferList> hlsCusFundTransferLists) throws HlsCusException {
        //创建头
        HlsCusFundTransfer fundTransfer = fundTransferService.createFundTransferGap(iRequest, new HlsCusFundTransfer());
        for(HlsCusFundTransferList fundTransferList:hlsCusFundTransferLists){
            fundTransferList.setTransferId(fundTransfer.getTransferId());
            fundTransferList.setDataClass(HlsCusConstant.DATA_CLASS.NORMAL);
            fundTransferList.setTransferStatus(HlsCusConstant.WORKFLOW_STATUS.NEW);
            fundTransferList.setTransferType(HlsCusConstant.TRANSFER_TYPE.TRANSFER);
            fundTransferList.setApplyPurpose(HlsCusConstant.TRANSFER_PURPOSE.TRANSFER);
            fundTransferList.setTransferNumber(fundTransfer.getApplyNumber()+fundTransferListMapper.selectTransferNumberMax(fundTransfer.getTransferId()));
            fundTransferList.setApplyPayAmount(fundTransferList.getApplyPayAmount().abs());
            //fundTransferList.setPlannedCurrencyCode(fundTransferList.getApplyCurrencyCode());
            fundTransferList.setIsOnceWriteOff(HlsCusConstant.FLAG.Y);
            fundTransferListMapper.insertSelective(fundTransferList);
        }
        return hlsCusFundTransferLists;
    }


    @Override
    public List<HlsCusFundTransferList> selectAccountBalanceDetail(IRequest iRequest, HlsCusFundTransferList fundTransferList, int page, int pageSize) {

        PageHelper.startPage(page,pageSize);

        return fundTransferListMapper.selectAccountBalanceDetail(fundTransferList);
    }


    @Override
    public List<HlsCusFundTransferList> selectBankAccountData(IRequest iRequest, HlsCusFundTransferList fundTransferList) {
        return fundTransferListMapper.selectBankAccountData(fundTransferList);
    }


    @Override
    public void batchDeleteTransferList(IRequest iRequest, List<HlsCusFundTransferList> hlsCusFundTransferLists) {
        for(HlsCusFundTransferList fundTransferList:hlsCusFundTransferLists){
            //财务界面的删除 只是删除引用
            if(HlsCusConstant.TRANSFER_BUSINESS_TYPE.FINANCE_TYPE.equals(fundTransferList.getBusinessType())){
                fundTransferList.setFinTransferId(0L);
                fundTransferListMapper.updateByPrimaryKeySelective(fundTransferList);
            }else{

                fundTransferListMapper.deleteByPrimaryKey(fundTransferList);
            }
        }
    }


    @Override
    public void exportBalanceDetail(HttpServletRequest request, HttpServletResponse response, HlsCusFundTransferList fundTransferList) throws IOException, InvocationTargetException, IllegalAccessException {
        final List<String> colNameList = Lists.newArrayList(
                "时间", "类型", "收款金额(元)", "付款金额(元)","合同/产品/调拨单编号","合同名称/产品简称");
        final List<String> colGetMethods = Lists.newArrayList(
                "applyPayDate", "cfItemDesc", "applyPayAmount","plannedDueAmount","sourceDocNumber","sourceDocName");
        XSSFWorkbook xwork = new XSSFWorkbook();
        XSSFSheet sheet = xwork.createSheet("sheet1");
        int dataRowNum = ExportExcelUtil.createCommonExcelHead(xwork, sheet, null, colNameList);
        List<HlsCusFundTransferList> fundTransferLists = fundTransferListMapper.selectAccountBalanceDetail(fundTransferList);
        for (HlsCusFundTransferList transferList : fundTransferLists) {
            XSSFRow row = sheet.createRow(dataRowNum++);
            if(transferList.getApplyPayAmount().compareTo(BigDecimal.ZERO)==1){
                transferList.setPlannedDueAmount(BigDecimal.ZERO);
            }else{
                transferList.setPlannedDueAmount(transferList.getApplyPayAmount().abs());
                transferList.setApplyPayAmount(BigDecimal.ZERO);
            }
           ExportExcelUtil.setData(xwork, sheet, row, transferList, colGetMethods);
        }
        ExportExcelUtil.IOWrite(xwork, null, request, response, "账户收付明细");
    }


    @Override
    public BigDecimal selectActualPaySurplusAmount(IRequest iRequest, String sourceDocCategory, Long sourceDocLineId) {
        return fundTransferListMapper.selectActualPaySurplusAmount(sourceDocCategory,sourceDocLineId);
    }

    @Autowired
    private IFndAttachmentService fndAttachmentService;

    @Autowired
    private IFndAttachmentMultiService fndAttachmentMultiService;


    @Override
    public void downloadTransferList(List<HlsCusFundTransferList> fundTransferLists , HttpServletRequest request, HttpServletResponse response) throws Exception{
        IRequest iRequest = RequestHelper.getCurrentRequest();
        HlsDocFileTemplet templet=new HlsDocFileTemplet();
        templet.setTempletCode("FUND_TRANSFER_APPLICATION");
        List<HlsDocFileTemplet> docFileTemplets = hlsDocFileTempletService.select(iRequest, templet, 1, 1);
        if(docFileTemplets.size()!=1){
            throw new HlsCusException("不存在模板,请配置");
        }
        Long templetId=docFileTemplets.get(0).getTempletId();
        FndAttachmentMulti fileParam = new FndAttachmentMulti();
        fileParam.setTableName("hls_doc_file_templet");
        fileParam.setTablePkValue(templetId.toString());
        FndAttachmentMulti sysFileMulti = fndAttachmentMultiService.selectSelective(iRequest, fileParam).get(0);
        FndAttachment templateFileParam = new FndAttachment();
        templateFileParam.setSourceTypeCode("fnd_atm_attachment_multi");
        templateFileParam.setSourcePkValue(sysFileMulti.getRecordId().toString());
        List<FndAttachment> fndAttachments = fndAttachmentService.selectSelective(iRequest, templateFileParam);
        Validate.notEmpty(fndAttachments, "文件模版不存在");
        FndAttachment sysFile = fndAttachments.get(0);
        if (sysFile == null || StringUtils.isBlank(sysFile.getFilePath())) {
            throw new HlsCusException("文件模版不存在");
        }
        File file = new File(sysFile.getFilePath());
        if (!file.exists()) {
            throw new HlsCusException("文件模版不存在");
        }
        List<HlsCusSysFile> hlsCusSysFiles=new ArrayList<>(fundTransferLists.size());
        for(HlsCusFundTransferList fundTransferList:fundTransferLists){
            InputStream inStream = new FileInputStream(file);
            //定义复制模板的filepath,使用uuid拼接于文件末尾，避免备份文件重名覆盖
            String copyPath = sysFile.getFilePath().concat("_back_").concat(fundTransferList.getTransferListId().toString());
            //复制模板
            HlsCusDownloadDocxUtil.copyModel(copyPath, inStream);
            //用输入流读取复制后的模板
            InputStream modelIs = new FileInputStream(copyPath);
            Map<String, Object> params = new HashMap<>();
            params.put("templetId", templetId);
            params.put("transferId", fundTransferList.getTransferId());
            params.put("finTransferId", fundTransferList.getFinTransferId());
            params.put("transferListId",fundTransferList.getTransferListId());
            //生成合同文本
            HlsCusDownloadDocxUtil.createDocx(iRequest, modelIs, new File(copyPath), params);
            HlsCusSysFile hlsCusSysFile=new HlsCusSysFile();
            hlsCusSysFile.setFilePath(copyPath);
            hlsCusSysFile.setFileName(fundTransferList.getTransferNumber().concat(".docx"));
            hlsCusSysFiles.add(hlsCusSysFile);
        }

        if(hlsCusSysFiles.size()>0) {
            String userName = iRequest.getUserName();
            File zipFilePath = new File(savePath);
            if (!zipFilePath.exists()) {
                zipFilePath.mkdirs();
            }
            //拼接文件名,用户名+系统时间,避免出现重复
            String zipFile = zipFilePath + File.separator + userName + "-" + System.currentTimeMillis() + ".zip";
            FileOutputStream outStream = new FileOutputStream(zipFile);
            ZipOutputStream toClient = new ZipOutputStream(outStream);
//                toClient.setEncoding("GBK");
            //打包转换为zip文件
            HlsCusZipUtil.zipFile(hlsCusSysFiles, toClient);
            toClient.close();
            outStream.close();
            //下载zip文件
            HlsCusZipUtil.downloadZip(new File(zipFile), response);

        }
    }

    @Value("${file.upload.dir:.}")
    private String savePath = ".";

    @Override
    public int selectSourceDocumentCount(String sourceDocCategory,Long sourceDocId, Long sourceDocLineId) {
        return fundTransferListMapper.selectSourceDocumentCount(sourceDocCategory,sourceDocId,sourceDocLineId);
    }

    @Override
    public List<HlsCusFundTransferList> selectWriteOffDeatil(IRequest iRequest, HlsCusFundTransferList fundTransferList, int page, int pageSize) {

        PageHelper.startPage(page,pageSize);

        return fundTransferListMapper.selectWriteOffDeatil(fundTransferList);
    }

    @Override
    public String selectTransferNumberMax(Long transferId) {
        return fundTransferListMapper.selectTransferNumberMax(transferId);
    }


    @Override
    public void cancelTaskListCancel(IRequest iRequest, List<HlsCusFundTransfer> hlsCusFundTransfer) {
        for(HlsCusFundTransfer transfer : hlsCusFundTransfer){
            String bussinessType = transfer.getBusinessType();
            HlsCusFundTransferList hlsCusFundTransferList = new HlsCusFundTransferList();
            if("FINANCE".equals(bussinessType)){
                hlsCusFundTransferList.setFinTransferId(transfer.getTransferId());
            }else{
                hlsCusFundTransferList.setTransferId(transfer.getTransferId());
            }
            List<HlsCusFundTransferList> transferLists = fundTransferListMapper.queryTransferListById(hlsCusFundTransferList);
            List<HlsCusFundTransferList> saveList = new ArrayList<>();
            for(HlsCusFundTransferList transferList : transferLists){
                transferList.setTransferStatus(HlsCusConstant.WORKFLOW_STATUS.CANCEL);
                transferList.set__status(DTOStatus.UPDATE);
                saveList.add(transferList);
            }
            self().batchUpdate(iRequest,saveList);
        }
        fundTransferService.batchDelete(hlsCusFundTransfer);
    }
}
