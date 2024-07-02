package com.hand.hls.vat.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hand.hap.attachment.dto.AttachCategory;
import com.hand.hap.attachment.dto.Attachment;
import com.hand.hap.attachment.exception.AttachmentException;
import com.hand.hap.attachment.exception.FileReadIOException;
import com.hand.hap.attachment.mapper.AttachCategoryMapper;
import com.hand.hap.attachment.service.IAttachCategoryService;
import com.hand.hap.attachment.service.IAttachmentService;
import com.hand.hap.core.AppContextInitListener;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.TokenException;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.components.CalculateUtil;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.dto.HlsCusSysAttachment;
import com.hand.hls.bp.dto.HlsCusSysFile;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.bp.mapper.HlsCusSysAttachmentMapper;
import com.hand.hls.bp.mapper.HlsCusSysFileMapper;
import com.hand.hls.bp.service.HlsSysAttachmentService;
import com.hand.hls.bp.service.HlsSysFileService;
import com.hand.hls.cont.dto.DocFileTempletRule;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.DocFileTempletRuleMapper;
import com.hand.hls.cont.mapper.HlsCusConFloatingRateReqMapper;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.cont.service.HlsDocFileTempletService;
import com.hand.hls.cont.service.IConFloatingCalcService;
import com.hand.hls.docx4J.component.BookMarkReplaceComponent;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fct.dto.HlsCusFctProject;
import com.hand.hls.fct.dto.HlsCusFctQuotationCashflow;
import com.hand.hls.fct.service.HlsCusFctProjectService;
import com.hand.hls.fct.service.HlsCusFctQuotationCashflowService;
import com.hand.hls.fin.service.HlsCusFctContractService;
import com.hand.hls.fnd.components.Datasource2Json;
import com.hand.hls.fnd.dto.FndInterfaceLines;
import com.hand.hls.cont.dto.HlsDocFileTemplet;
import com.hand.hls.fnd.mapper.FndInterfaceLinesMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.prj.utils.HlsCusZipUtil;
import com.hand.hls.ruleengine.service.IHLSRuleEngineInitService;
import com.hand.hls.utils.ExportExcelUtil;
import com.hand.hls.vat.dto.*;
import com.hand.hls.vat.mapper.HlsCusAcrInvoiceHdMapper;
import com.hand.hls.vat.mapper.HlsCusAcrReceiptHdMapper;
import com.hand.hls.vat.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

import static com.hand.hls.utils.HlsCusDownloadDocxUtil.createDocx;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusAcrInvoiceHdServiceImpl extends BaseServiceImpl<HlsCusAcrInvoiceHd> implements HlsCusAcrInvoiceHdService, AppContextInitListener {
    /**
     * 发票单据类型
     */
    private static final String DOCUMENT_TYPE = "ACR";
    /**
     * 发票单据类别
     */
    private static final String DOCUMENT_CATEGORY = "AR_INVOICE";
    /**
     * 发票单据类别
     */
    private static final String BUSINESS_TYPE = "ACR";

    public static final String CON_CONTRACT = "con_contract";

    public static final String ACR_RECEIPT_ATTACHMENT = "ACR_RECEIPT_ATTACHMENT";

    @Autowired
    private HlsCusAcrInvoiceHdMapper hlsCusAcrInvoiceHdMapper;
    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;
    @Autowired
    private IAcrInvoiceLnService acrInvoiceLnService;
    @Autowired
    private AcrInvoiceRelationshipService acrInvoiceRelationshipService;
    @Autowired
    private HlsCusConContractCashflowService cashflowService;
    @Autowired
    private HlsCusConContractService hlsCusConContractService;
    @Autowired
    private HlsCusFctQuotationCashflowService hlsCusFctQuotationCashflowService;
    @Autowired
    private HlsCusFctContractService hlsCusFctContractService;
    @Autowired
    FndInterfaceLinesMapper fndInterfaceLinesMapper;
    @Autowired
    private IAttachmentService attachmentService;

    @Autowired
    private HlsSysFileService fileService;

    @Autowired
    private HlsCusSysFileMapper hlsCusSysFileMapper;

    @Autowired
    private BookMarkReplaceComponent bookMarkReplaceComponent;

    @Autowired
    private HlsSysAttachmentService hlsSysAttachmentService;
    @Autowired
    private IAttachCategoryService attachCategoryService;

    @Autowired
    private HlsCusConFloatingRateReqMapper hlsCusConFloatingRateReqMapper;

    @Autowired
    private HlsCusSysAttachmentMapper sysAttachmentMapper;
    @Autowired
    private AttachCategoryMapper attachCategoryMapper;

//    @Autowired
//    private IAcrInvoiceHistoryService acrInvoiceHistoryService;
    @Autowired
    private JeTrxCommonService jeTrxCommonService;

    @Autowired
    private HlsCusBpMasterMapper bpMasterMapper;


    @Autowired
    private AcpInvoiceHdService acpInvoiceHdService;
    @Autowired
    private IAcpInvoiceLnService acpInvoiceLnService;

    @Autowired
    private HlsCusAcrReceiptHdService receiptHdService;

    @Autowired
    private IAcrReceiptLnService receiptLnService;
    @Autowired
    private DocFileTempletRuleMapper docFileTempletRuleMapper;
    @Autowired
    private Datasource2Json datasource2Json;
    @Autowired
    private IHLSRuleEngineInitService hLSRuleEngineInitService;
    @Autowired
    private HlsDocFileTempletService hlsDocFileTempletService;
    @Autowired
    private HlsCusFctProjectService hlsCusFctProjectService;

    @Autowired
    private HlsCusAcrReceiptHdMapper hlsCusAcrReceiptHdMapper;




    private static Map<String, IConFloatingCalcService> conFloatingCalcServiceMap = new HashMap<>();


//    @Override
//    public List<Map<String, Object>> queryWriteOffDetail(IRequest request, HlsCusCshWriteOff hlsCusCshWriteOff) {
//        return hlsCusAcrInvoiceHdMapper.queryWriteOffDetail(hlsCusCshWriteOff);
//    }
//
//    @Override
//    public Map<String, Object> queryAcpInvoicePercent() {
//        return hlsCusAcrInvoiceHdMapper.queryAcpInvoicePercent();
//    }
//
//    @Override
//    public List<HlsCusAcrInvoiceHd> queryAcpInvoice(IRequest request, HlsCusAcrInvoiceHd hlsCusAcrInvoiceHd, int page, int pageSize) {
//        Date date = new Date();
//        if (StringUtils.equals("ONEWEEK", hlsCusAcrInvoiceHd.getInvoiceDateFlag())) {
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(date);
//            calendar.add(Calendar.DAY_OF_MONTH, 7);
//            date = calendar.getTime();
//        } else if (StringUtils.equals("ONEMONTH", hlsCusAcrInvoiceHd.getInvoiceDateFlag())) date = addDate(date, 1L);
//        else if (StringUtils.equals("THREEMONTH", hlsCusAcrInvoiceHd.getInvoiceDateFlag())) date = addDate(date, 3L);
//        else date = null;
//        hlsCusAcrInvoiceHd.setInvoiceDate(date);
//        PageHelper.startPage(page, pageSize);
//        return hlsCusAcrInvoiceHdMapper.queryAcpInvoice(hlsCusAcrInvoiceHd);
//    }
//
//    @Override
//    public List<HlsCusAcrInvoiceHd> queryReceiptPrepGuaranteeInfo(IRequest request, HlsCusAcrInvoiceHd acrInvoiceLn, int page, int pageSize) {
//        PageHelper.startPage(page, pageSize);
//        return hlsCusAcrInvoiceHdMapper.queryReceiptPrepGuaranteeInfo(acrInvoiceLn);
//    }

    @Override
    public List<HlsCusAcrInvoiceHd> queryReceiptPrepInfo(IRequest request, HlsCusAcrInvoiceHd acrInvoiceLn, int page, int pageSize) {
        //PageHelper.startPage(page, pageSize);
        return hlsCusAcrInvoiceHdMapper.queryReceiptPrepInfo(acrInvoiceLn);
    }

//
//    @Override
//    public List<Map<String, Object>> selectReceiptInfo(IRequest request, HlsCusAcrInvoiceHd hlsCusAcrInvoiceHd, int page, int pageSize) {
//        Date date = new Date();
//        if (null == hlsCusAcrInvoiceHd.getDateFlag()) date = null;
//        else if (StringUtils.equals("ONEWEEK", hlsCusAcrInvoiceHd.getDateFlag())) {
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(date);
//            calendar.add(Calendar.DAY_OF_MONTH, 7);
//            date = calendar.getTime();
//        } else if (StringUtils.equals("ONEMONTH", hlsCusAcrInvoiceHd.getDateFlag())) date = addDate(date, 1L);
//        else if (StringUtils.equals("THREEMONTH", hlsCusAcrInvoiceHd.getDateFlag())) date = addDate(date, 3L);
//        hlsCusAcrInvoiceHd.setInvoiceDate(date);
//        PageHelper.startPage(page, pageSize);
//        return hlsCusAcrInvoiceHdMapper.selectReceiptInfo(hlsCusAcrInvoiceHd);
//    }
//
//    @Override
//    public List<HlsCusAcrInvoiceHd> queryInvoiceByConditions(IRequest request, HlsCusAcrInvoiceHd hlsCusAcrInvoiceHd, int page, int pageSize) {
//        Date date = new Date();
//        if (StringUtils.equals("ONEWEEK", hlsCusAcrInvoiceHd.getInvoiceDateFlag())) {
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(date);
//            calendar.add(Calendar.DAY_OF_MONTH, 7);
//            date = calendar.getTime();
//        } else if (StringUtils.equals("ONEMONTH", hlsCusAcrInvoiceHd.getInvoiceDateFlag())) date = addDate(date, 1L);
//        else if (StringUtils.equals("THREEMONTH", hlsCusAcrInvoiceHd.getInvoiceDateFlag())) date = addDate(date, 3L);
//        else date = null;
//        hlsCusAcrInvoiceHd.setInvoiceDate(date);
//        PageHelper.startPage(page, pageSize);
//        return hlsCusAcrInvoiceHdMapper.queryInvoiceByConditions(hlsCusAcrInvoiceHd);
//    }
//
//    @Override
//    public boolean createFctInvoice(IRequest request, List<HlsCusAcrInvoiceHd> list) {
//        for (HlsCusAcrInvoiceHd invoiceHd : list) {
//            invoiceHd.setDocumentType("FCT_CONTRACT");
//            setInvoiceHdAndLnAndRelationShip(invoiceHd, request);
//            // 回写现金流数据
//            HlsCusFctQuotationCashflow cashflow = new HlsCusFctQuotationCashflow();
//            cashflow.setQuotationCashflowId(invoiceHd.getQuotationCashflowId());
//            cashflow = hlsCusFctQuotationCashflowService.selectByPrimaryKey(request, cashflow);
//            if (cashflow == null)
//                throw new IllegalArgumentException("找不到id为 " + invoiceHd.getQuotationCashflowId() + " 的现金流!");
//            if (cashflow.getBillingAmount() == null) cashflow.setBillingAmount(0D);
//            Double billingAmount = cashflow.getBillingAmount() + invoiceHd.getBillingAmount();
//            cashflow.setBillingStatus("FULL");
//            if (Double.compare(billingAmount, cashflow.getDueAmount()) < 0) cashflow.setBillingStatus("PARTIAL");
//            cashflow.setBillingAmount(billingAmount);
//            cashflow.set__status(DTOStatus.UPDATE);
//            hlsCusFctQuotationCashflowService.updateByPrimaryKeySelective(request, cashflow);
//        }
//        return true;
//    }

    @Override
    public boolean createInvoice(IRequest request, List<HlsCusAcrInvoiceHd> list) {
        for (HlsCusAcrInvoiceHd invoiceHd : list) {
            invoiceHd.setDocumentType("CON_CONTRACT");
            setInvoiceHdAndLnAndRelationShip(invoiceHd, request);
            // 回写现金流数据
            HlsCusConContractCashflow cashflow = new HlsCusConContractCashflow();
            cashflow.setCashflowId(invoiceHd.getCashflowId());
            cashflow = cashflowService.selectByPrimaryKey(request, cashflow);
            if (cashflow == null) throw new IllegalArgumentException("找不到id为 " + invoiceHd.getCashflowId() + " 的现金流!");
            HlsCusConContract hlsCusConContract = new HlsCusConContract();
            hlsCusConContract.setContractId(cashflow.getContractId());
            hlsCusConContract = hlsCusConContractService.selectByPrimaryKey(request, hlsCusConContract);
            if (hlsCusConContract == null)
                throw new IllegalArgumentException("找不到id为 " + cashflow.getContractId() + " 的合同!");

            Double billingAmount = cashflow.getBillingAmount() + invoiceHd.getBillingAmount();
            cashflow.setBillingStatus("FULL");
            if (Double.compare(billingAmount, cashflow.getDueAmount()) < 0) cashflow.setBillingStatus("PARTIAL");
            cashflow.setBillingAmount(billingAmount);
            if (StringUtils.equals("LEASEBACK", hlsCusConContract.getBusinessType()) && cashflow.getCfItem() == 1) {
                cashflow.setBillingInterest(cashflow.getBillingInterest() + invoiceHd.getBillingAmount());
            } else {
                cashflow.setBillingPrincipal(cashflow.getBillingPrincipal() + invoiceHd.getBillingAmount());
            }
            cashflow.set__status(DTOStatus.UPDATE);
            cashflowService.updateByPrimaryKeySelective(request, cashflow);
        }
        return true;
    }

    private void setInvoiceHdAndLnAndRelationShip(HlsCusAcrInvoiceHd invoiceHd, IRequest request) {
        // 插入发票头表
        HlsCusAcrInvoiceHd hlsCusAcrInvoiceHd = new HlsCusAcrInvoiceHd();
        hlsCusAcrInvoiceHd.setCompanyId(request.getCompanyId());
        hlsCusAcrInvoiceHd.setDocumentCategory(DOCUMENT_CATEGORY);
        hlsCusAcrInvoiceHd.setDocumentType(DOCUMENT_TYPE);
        hlsCusAcrInvoiceHd.setBusinessType(BUSINESS_TYPE);
        Map<String, String> params = new HashMap<>();
        hlsCusAcrInvoiceHd.setDocumentNumber(fndCodingRuleValuesService.getCodeRuleValue(request, hlsCusAcrInvoiceHd.getDocumentCategory(), hlsCusAcrInvoiceHd.getDocumentType(), hlsCusAcrInvoiceHd.getBusinessType(), params));
        hlsCusAcrInvoiceHd.setInvoiceKind(invoiceHd.getInvoiceKind());
        hlsCusAcrInvoiceHd.setBpId(invoiceHd.getBpId());
        hlsCusAcrInvoiceHd.setBpName(invoiceHd.getInvoiceTitle());
        hlsCusAcrInvoiceHd.setBpTaxRegistryNum(invoiceHd.getBpTaxRegistryNum());
        hlsCusAcrInvoiceHd.setBpAddressPhoneNum(invoiceHd.getBpAddressPhoneNum());
        hlsCusAcrInvoiceHd.setBpBankAccount(invoiceHd.getBpBankAccount());
        hlsCusAcrInvoiceHd.setTotalAmount(invoiceHd.getBillingAmount());
        hlsCusAcrInvoiceHd.setNetAmount(invoiceHd.getNetAmount());
        hlsCusAcrInvoiceHd.setTaxAmount(CalculateUtil.sub(invoiceHd.getBillingAmount(), invoiceHd.getNetAmount()));
        hlsCusAcrInvoiceHd.setCurrency("CNY");
        hlsCusAcrInvoiceHd.setInvoiceDate(new Date());
        hlsCusAcrInvoiceHd.setInvoiceStatus("NEW");
        hlsCusAcrInvoiceHd.setConfirmStatus("NEW");
        hlsCusAcrInvoiceHd.set__status(DTOStatus.ADD);
        hlsCusAcrInvoiceHd = self().insertSelective(request, hlsCusAcrInvoiceHd);
        // 插入发票行表
        HlsCusAcrInvoiceLn hlsCusAcrInvoiceLn = new HlsCusAcrInvoiceLn();
        hlsCusAcrInvoiceLn.setInvoiceHdId(hlsCusAcrInvoiceHd.getInvoiceHdId());
        hlsCusAcrInvoiceLn.setLineNumber(1L);
        hlsCusAcrInvoiceLn.setProductName(invoiceHd.getTaxableServiceName());
        hlsCusAcrInvoiceLn.setQuantity(1L);
        hlsCusAcrInvoiceLn.setPrice(invoiceHd.getBillingAmount());
        hlsCusAcrInvoiceLn.setNetPrice(invoiceHd.getNetAmount());
        hlsCusAcrInvoiceLn.setTaxTypeRate(invoiceHd.getTaxTypeRate());
        hlsCusAcrInvoiceLn.setTaxIncludedFlag("Y");
        hlsCusAcrInvoiceLn.setTaxAmount(hlsCusAcrInvoiceHd.getTaxAmount());
        hlsCusAcrInvoiceLn.setNetAmount(hlsCusAcrInvoiceHd.getNetAmount());
        hlsCusAcrInvoiceLn.setTotalAmount(hlsCusAcrInvoiceHd.getTotalAmount());
        hlsCusAcrInvoiceLn.set__status(DTOStatus.ADD);
        hlsCusAcrInvoiceLn = acrInvoiceLnService.insertSelective(request, hlsCusAcrInvoiceLn);
        // 插入发票关系表
        HlsCusAcrInvoiceRelationship relationship = new HlsCusAcrInvoiceRelationship();
        relationship.setInvoiceLnId(hlsCusAcrInvoiceLn.getInvoiceLnId());
        relationship.setSourceDocumentType(invoiceHd.getDocumentType());
        if ("CON_CONTRACT".equals(invoiceHd.getDocumentType())) {
            relationship.setSourceDocumentId(invoiceHd.getCashflowId());
        }
        if ("FCT_CONTRACT".equals(invoiceHd.getDocumentType())) {
            relationship.setSourceDocumentId(invoiceHd.getQuotationCashflowId());
        }
        relationship.setBillingAmount(invoiceHd.getBillingAmount());
        relationship.setTaxTypeRate(invoiceHd.getTaxTypeRate());
        relationship.setTaxIncludedFlag(hlsCusAcrInvoiceLn.getTaxIncludedFlag());
        relationship.setTaxAmount(hlsCusAcrInvoiceLn.getTaxAmount());
        relationship.setNetAmount(hlsCusAcrInvoiceLn.getNetAmount());
        relationship.setInvoiceKind(invoiceHd.getInvoiceKind());
        relationship.setBpId(invoiceHd.getBpId());
        relationship.set__status(DTOStatus.ADD);
        acrInvoiceRelationshipService.insertSelective(request, relationship);
    }

//    @Override
//    public List<HlsCusAcrInvoiceHd> queryByCashflowIds(IRequest request, HlsCusAcrInvoiceHd hlsCusAcrInvoiceHd) {
//        List<HlsCusAcrInvoiceHd> list = new ArrayList<>();
//        String cashflowIdStr = hlsCusAcrInvoiceHd.getCashflowIds();
//        String[] idArr = cashflowIdStr.split(",");
//        for (int i = 0; i < idArr.length; i++) {
//            Long cashflowId = Long.valueOf(idArr[i]);
//            HlsCusAcrInvoiceHd hd = new HlsCusAcrInvoiceHd();
//            hd.setCashflowId(cashflowId);
//            hd.setCompanyId(request.getCompanyId());
//            list.add(hlsCusAcrInvoiceHdMapper.queryInvoiceByCashflowId(hd));
//        }
//        return list;
//    }
//
//    @Override
//    public List<HlsCusAcrInvoiceHd> queryInvoiceHdInfo(IRequest request, HlsCusAcrInvoiceHd hlsCusAcrInvoiceHd, int page, int pageSize) {
//        PageHelper.startPage(page, pageSize);
//        return hlsCusAcrInvoiceHdMapper.queryInvoiceHdInfo(hlsCusAcrInvoiceHd);
//    }
//
//    @Override
//    public List<Map<String, Object>> queryFctInvoiceTitle(Long contractId, IRequest request) {
//        return hlsCusAcrInvoiceHdMapper.queryFctInvoiceTitle(contractId);
//    }
//
//    @Override
//    public List<HlsCusAcrInvoiceHd> queryInvoiceInfo(HlsCusFctQuotationCashflow hlsCusFctQuotationCashflow, IRequest request, int page, int pageSize) {
//        hlsCusFctQuotationCashflow.setCompanyId(request.getCompanyId());
//        PageHelper.startPage(page, pageSize);
//        return hlsCusAcrInvoiceHdMapper.queryInvoiceInfo(hlsCusFctQuotationCashflow);
//    }
//
//    @Override
//    public List<HlsCusAcrInvoiceHd> selectByKindTime(IRequest iRequest, HlsCusAcrInvoiceHd invoiceHd, int page, int pageSize) {
//        //调整到期日
//        if (invoiceHd.getTime() != null) {
//            String timeSpan = new String();
//            if ("7".equals(invoiceHd.getTime())) {
//                timeSpan = "7 0:0:0";  //一周
//            } else if ("30".equals(invoiceHd.getTime())) {
//                timeSpan = "30 0:0:0";  //一个月
//            } else if ("90".equals(invoiceHd.getTime())) {
//                timeSpan = "90 0:0:0";  //三个月
//            }
//
//            invoiceHd.setTime(timeSpan);
//        }
//        PageHelper.startPage(page, pageSize);
//        return hlsCusAcrInvoiceHdMapper.selectByKindTime(invoiceHd);
//
//    }
//
//    @Override
//    public List<HashMap> acrInvoiceDetail(HlsCusAcrInvoiceHd acrInvoiceHd, IRequest iRequest) {
//        return hlsCusAcrInvoiceHdMapper.acrInvoiceDetail(acrInvoiceHd);
//    }
//
//    /**
//     * @param date  要改变的时间
//     * @param month 增减的月数
//     * @return 改变后的日期
//     */
//    private Date addDate(Date date, Long month) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(date);
//        calendar.add(Calendar.MONTH, month.intValue());
//        return calendar.getTime();
//    }
//
//
//    @Override
//    public void updateInvoiceStatusAfterExport(HlsCusAcrInvoiceHd acrInvoiceHd, IRequest iRequest) {
//        List<HlsCusAcrInvoiceHd> hlsCusAcrInvoiceHdList = self().queryInvoiceByConditions(iRequest, acrInvoiceHd, 1, 1000);
//        for (HlsCusAcrInvoiceHd dt : hlsCusAcrInvoiceHdList) {
//            dt.setInvoiceStatus("EXPORTED");
//            dt = self().updateByPrimaryKeySelective(iRequest, dt);
//        }
//    }


    // 校验字段是否有值
    public static void validate(String message, Object... objects) {
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] == null || "".equals(objects[i]) || "null".equals(objects[i])) {
                throw new RuntimeException(message);
            }
        }
    }

    /**
     * 复制文件模板,将读取源文件的输入流复制文件存入一个备用的模板文件
     *
     * @param filePath
     * @param is
     * @throws IOException
     */
    private static synchronized void copyModel(String filePath, InputStream is) throws IOException {

        FileOutputStream fos = new FileOutputStream(filePath); //复制出一个模板
        int readData;
        byte[] b = new byte[1024];

        while ((readData = is.read(b)) != -1) {
            fos.write(b, 0, readData);
        }

        fos.flush();
        is.close();
        fos.close();
    }


//    public void createDocx(IRequest requestContext, InputStream is, File file, Map<String, Object> params) throws Exception {
//
//        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(is);//通过输入流构建WordprocessingMLPackage对象
//
//        wordMLPackage = bookMarkReplaceComponent.docxCreateBookMarkReplaceWithText(wordMLPackage, requestContext, params);//将构建的wordMLPackage对象传入方法中
//
//        wordMLPackage.save(file);//将替换后的合同文本保存到服务器上作为备份
//
//    }
//
//
    @Override
    public HlsCusSysFile docxCreateMethod(IRequest requestContext, Map<String, Object> params) throws TokenException, FileReadIOException, Docx4JException {

        Long templetId = Long.parseLong(params.get("templetId").toString());
        Long receiptLnId = Long.parseLong(params.get("receiptLnId").toString());
        String SourceType = "HLS_DOC_FILE_TEMPLET";

        Long SourceKey = templetId;//模板的ID为对应的SourceKey

        Attachment attachment = new Attachment();
        HlsCusSysFile returnSysFile = null;

        try {
            attachment = attachmentService.selectAttachByCodeAndKey(requestContext, SourceType, SourceKey.toString());//根据sys_attachment表上的记录获取附件的信息
            HlsCusSysFile sysFile = fileService.queryByAttachmentId(attachment.getAttachmentId());
            if (sysFile != null && org.apache.commons.lang.StringUtils.isNotBlank(sysFile.getFilePath())) {
                File file = new File(sysFile.getFilePath());
                if (file.exists()) {
                    int fileLength = (int) file.length();//先获取模板文件的大小

                    int fileBackLength = 0;//定义备份文件的大小

                    if (fileLength > 0) {

                        InputStream inStream = new FileInputStream(file);//用inputStram输入流读取本地的docx文件

                        String copyPath = sysFile.getFilePath().concat("_back_").concat(UUID.randomUUID().toString());//定义复制模板的filepath,使用uuid拼接于文件末尾，避免备份文件重名覆盖

                        this.copyModel(copyPath, inStream);//复制模板

                        InputStream modelIs = new FileInputStream(copyPath);//用输入流读取复制后的模板

                        createDocx(requestContext, modelIs, new File(copyPath), params);//生成合同文本

                        fileBackLength = (int) new File(copyPath).length();


                        AttachCategory attachCategory = new AttachCategory();
                        attachCategory.setSourceType(ACR_RECEIPT_ATTACHMENT);
                        List<AttachCategory> lists = attachCategoryService.selectCategories(requestContext, attachCategory);

                        HlsCusSysAttachment hsa = new HlsCusSysAttachment();
                        hsa.setSourceKey(receiptLnId.toString());
                        hsa.setCategoryId(lists.get(0).getCategoryId());
                        hsa.setSourceType(ACR_RECEIPT_ATTACHMENT);
                        hsa.setName(ACR_RECEIPT_ATTACHMENT);
                        hsa.setStatus("1");
                        hsa = hlsSysAttachmentService.insertSelective(requestContext, hsa);

                        HlsCusAcrInvoiceHd invoiceHd = new HlsCusAcrInvoiceHd();
                        invoiceHd.setReceiptLnId(receiptLnId);
                        List<Map<String, Object>> receiptInfo = hlsCusAcrInvoiceHdMapper.selectReceiptInfo(invoiceHd);
                        //按照合同编号去找合同批复号
                        HlsCusFctProject hlsCusFctProject=new HlsCusFctProject();
                        hlsCusFctProject.setContractNumber(receiptInfo.get(0).get("contractNumber").toString());
                        List<HlsCusFctProject> projects=hlsCusFctProjectService.select(requestContext,hlsCusFctProject,1,10);
                        String approvalNumber="";
                        if(CollectionUtils.isNotEmpty(projects)){
                            approvalNumber=projects.get(0).getApprovalNumber();
                        }
                        StringBuilder builder = new StringBuilder("");
                        if (receiptInfo.size() > 0) {
                            builder.append(approvalNumber).append(receiptInfo.get(0).get("bpName")).append("第")
                                    .append(receiptInfo.get(0).get("times")).append("期").append(receiptInfo.get(0).get("cfItemDesc"))
                                    .append("收据");
                        }

                        HlsCusSysFile backSysFile = new HlsCusSysFile();
                        //String fileBak = sysFile.getFileName().substring(0, sysFile.getFileName().length() - 5);
                        backSysFile.setFileName(builder.toString().concat(".docx"));
                        backSysFile.setAttachmentId(hsa.getAttachmentId());
                        backSysFile.setFilePath(copyPath);
                        backSysFile.setFileSize(new BigDecimal(fileBackLength));
                        backSysFile.setFileType(sysFile.getFileType());
                        backSysFile.setUploadDate(new Date());
                        returnSysFile = fileService.insertSelective(requestContext, backSysFile);

                    }
                }
            }
        } catch (IOException e) {
            throw new FileReadIOException();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnSysFile;
    }

    @Override
    public void batchDownloadWithZip(List<HlsCusAcrInvoiceHd> acrInvoiceHdList, HttpServletRequest request, HttpServletResponse response) throws Docx4JException, FileReadIOException, TokenException {

        IRequest currentRequest = RequestHelper.getCurrentRequest();
        String templetType = "ACR_RECEPIT";
        DocFileTempletRule docFileTempletRule = new DocFileTempletRule();
        docFileTempletRule.setCompanyId(currentRequest.getCompanyId());
        docFileTempletRule.setTempletType(templetType);
        docFileTempletRule = docFileTempletRuleMapper.selectOne(docFileTempletRule);//匹配模板
        String json = null;

        String [] cashflowIdArr = new String[acrInvoiceHdList.size()];

        for (int i = 0; i < acrInvoiceHdList.size(); i++) {
            HlsCusAcrInvoiceHd acrInvoiceHd = acrInvoiceHdList.get(i);
            try {
                cashflowIdArr[i] = String.valueOf(acrInvoiceHd.getReceiptLnId());
                Map<String, Object> pMap = new HashMap<>();
                pMap.put("receiptLnId", acrInvoiceHd.getReceiptLnId());
                json = datasource2Json.executeSQL4Json(docFileTempletRule.getDataSourceId(), pMap);//从数据源获取匹配的结果
            } catch (IOException e) {
                e.printStackTrace();
            }

            JSONObject jsonObject0 = JSON.parseObject(json);
            JSONObject jsonObject1 = JSON.parseObject(JSON.toJSONString(jsonObject0.get(docFileTempletRule.getDataSourceId().toString())));
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(jsonObject1.get("default")));
            jsonObject.put("ruleEngineId", docFileTempletRule.getRuleEngineId());//获得规则引擎id

            //获取匹配的模板
            String[] arr = hLSRuleEngineInitService.ruleEngineInit(currentRequest, jsonObject,null);

            //删除sys_file sys_attatchment 中数据
            deleteSysFile(currentRequest, acrInvoiceHd.getCashflowId());
            for (String item : arr) {
                HlsDocFileTemplet hlsDocFileTemplet = new HlsDocFileTemplet();
                hlsDocFileTemplet.setTempletId(Long.parseLong(item));
                hlsDocFileTemplet = hlsDocFileTempletService.selectByPrimaryKey(currentRequest, hlsDocFileTemplet);
                Long templetId = hlsDocFileTemplet.getTempletId();
                Map<String, Object> params = new HashMap<>();
                params.put("templetId", templetId);
                params.put("receiptLnId", acrInvoiceHd.getReceiptLnId());
                self().docxCreateMethod(currentRequest, params);
            }
        }


        List<HlsCusSysFile> hlsCusSysFiles = hlsCusSysFileMapper.selectFileList(ACR_RECEIPT_ATTACHMENT, cashflowIdArr);
        HlsCusSysFile file = hlsCusSysFiles.stream().findAny().get();
        Long attachmentId = file.getAttachmentId();
        HlsCusSysAttachment hlsCusSysAttachment = sysAttachmentMapper.selectByPrimaryKey(attachmentId);
        Long categoryId = hlsCusSysAttachment.getCategoryId();
        AttachCategory attachCategory = attachCategoryMapper.selectByPrimaryKey(categoryId);
        String categoryPath = attachCategory.getCategoryPath();//拿到系统中已有的目录(避免目录权限问题)
        String userName = currentRequest.getUserName();//获取用户名
        File zipFilePath = new File(categoryPath);
        if (!zipFilePath.exists()) {
            zipFilePath.mkdirs();
        }
        String zipFile = zipFilePath + File.separator + userName + "-" + System.currentTimeMillis() + ".zip";//拼接文件名,用户名+系统时间,避免出现重复
        try {
            FileOutputStream outStream = new FileOutputStream(zipFile);
            ZipOutputStream toClient = new ZipOutputStream(outStream);
//            toClient.setEncoding("GBK");//设置编码,避免出现乱码
            HlsCusZipUtil.zipFile(hlsCusSysFiles, toClient);//打包转换为zip文件
            toClient.close();//关闭流
            outStream.close();//关闭流
            HlsCusZipUtil.downloadZip(new File(zipFile), response);//下载zip文件
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AttachmentException e) {
            e.printStackTrace();
        }
    }

    // 发票关闭
    @Override
    public void closeVatInvoice(List<HlsCusAcrInvoiceHd> acrInvoiceHdList) {
//        Boolean isCanClose = acrInvoiceHdList.stream().allMatch(acrInvoiceHd -> "NEW".equals(acrInvoiceHd.getInvoiceStatus()));
        // 1. 将发票状态改为 CLOSE
        // 2. 释放现金流
        IRequest requestCtx = RequestHelper.getCurrentRequest();
        acrInvoiceHdList.forEach(item -> {
            HlsCusAcrInvoiceHd acrInvoiceHd = hlsCusAcrInvoiceHdMapper.selectByPrimaryKey(item.getInvoiceHdId());
            if (!"NEW".equals(acrInvoiceHd.getInvoiceStatus()) && !"CANCELLATION".equals(acrInvoiceHd.getInvoiceStatus())) {
                throw new RuntimeException("存在不是新建或者作废的单据，不能关闭");
            }
            acrInvoiceHd.setInvoiceStatus("CLOSED");
            acrInvoiceHd.setReversedFlag("W");
            self().updateByPrimaryKey(requestCtx, acrInvoiceHd);

            insertHistory(requestCtx, acrInvoiceHd.getInvoiceHdId(), "CLOSED", "发票关闭");

            HlsCusAcrInvoiceHd acrInvoiceHdParam = new HlsCusAcrInvoiceHd();
            acrInvoiceHdParam.setInvoiceHdId(acrInvoiceHd.getInvoiceHdId());
            HlsCusAcrInvoiceHd acrInvoiceHdDetail = hlsCusAcrInvoiceHdMapper.queryInvoiceDetail(acrInvoiceHdParam).get(0);
            //插入负数三表
            insertReverseData(requestCtx, acrInvoiceHdDetail);
            // 释放现金流
            releaseCashflowByInvoiceHdId(requestCtx, acrInvoiceHdDetail);
        });
    }

    // 发票导出
    @Override
    public void exportVatAcrInvoiceExcel(HttpServletRequest request,HttpServletResponse response, String invoiceHdIdStr) throws IOException, InvocationTargetException, IllegalAccessException, HlsCusException {
        IRequest requestCtx = RequestHelper.getCurrentRequest();
        XSSFWorkbook xwork = new XSSFWorkbook();
        XSSFSheet sheet = xwork.createSheet("发票信息");
        List<String> colNameList = Arrays.asList("发票代码", "发票号码", "购方企业名称", "购方税号", "银行账号", "地址电话", "开票日期", "商品编码版本号", "单据号", "商品名称", "规格", "单位", "数量", "单价", "金额", "税率", "税额", "税收分类编码");
        int dataRowNum = ExportExcelUtil.createCommonExcelHead(xwork, sheet,"兴业金融租赁有限责任公司发票数据", colNameList);


        HlsCusAcrInvoiceHd acrInvoiceHdParam = new HlsCusAcrInvoiceHd();
        acrInvoiceHdParam.setInvoiceHdIdArr(invoiceHdIdStr.split("-"));
        List<HlsCusAcrInvoiceHd> acrInvoiceHds = hlsCusAcrInvoiceHdMapper.queryInvoiceDetail(acrInvoiceHdParam);

        for (HlsCusAcrInvoiceHd acrInvoiceHd : acrInvoiceHds) {
            XSSFRow row = sheet.createRow(dataRowNum++);
            setData(xwork, sheet, row, acrInvoiceHd);
            HlsCusAcrInvoiceHd acrInvoiceHdsUpdate = new HlsCusAcrInvoiceHd();
            acrInvoiceHdsUpdate.setInvoiceHdId(acrInvoiceHd.getInvoiceHdId());
            acrInvoiceHdsUpdate.setNone1("12");
            acrInvoiceHdsUpdate.setInvoiceStatus("EXPORTED");
            self().updateByPrimaryKeySelective(requestCtx, acrInvoiceHdsUpdate);
            // 插入历史表
//            insertHistory(requestCtx, acrInvoiceHd.getInvoiceHdId(), "EXPORTED", "发票导出");
        }
        ExportExcelUtil.IOWrite(xwork, null, request, response, "发票信息");
    }

    // 发票红冲
    @Override
    public void reverseVatAcrInvoice(List<HlsCusAcrInvoiceHd> acrInvoiceHdList) {
        IRequest requestCtx = RequestHelper.getCurrentRequest();
        if (acrInvoiceHdList.size() > 1) {
            throw new RuntimeException("只能对勾选一条单据进行红冲");
        }
        HlsCusAcrInvoiceHd param = acrInvoiceHdList.get(0);
        if (StringUtils.isEmpty(param.getRedInvoiceCode()) || StringUtils.isEmpty(param.getRedInvoiceNumber())) {
            throw new RuntimeException("请输入红冲发票代码和发票编号");
        }

        HlsCusAcrInvoiceHd acrInvoiceHd = hlsCusAcrInvoiceHdMapper.selectByPrimaryKey(param.getInvoiceHdId());
        if (!"IMPORTED".equals(acrInvoiceHd.getInvoiceStatus())) {
            throw new RuntimeException("存在不是已导入的单据，不能红冲");
        }
        acrInvoiceHd.setInvoiceStatus("REVERSE");
        acrInvoiceHd.setReversedFlag("W");
        self().updateByPrimaryKey(requestCtx, acrInvoiceHd);

        HlsCusAcrInvoiceHd acrInvoiceHdParam = new HlsCusAcrInvoiceHd();
        acrInvoiceHdParam.setInvoiceHdId(acrInvoiceHd.getInvoiceHdId());
        acrInvoiceHd = hlsCusAcrInvoiceHdMapper.queryInvoiceDetail(acrInvoiceHdParam).get(0);
        //插入负数三表
        insertReverseData(requestCtx, acrInvoiceHd, param.getRedInvoiceCode(), param.getRedInvoiceNumber());

        // 释放现金流
        releaseCashflowByInvoiceHdId(requestCtx, acrInvoiceHd);

        // 插入历史表
        insertHistory(requestCtx, acrInvoiceHd.getInvoiceHdId(), "REVERSE", "发票红冲");
    }

    // 发票导入
    @Override
    public void acrInvoiceExcelImport(IRequest iRequest, Long hdId) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        //获取接口表数据
        FndInterfaceLines acrInvoiceHd = new FndInterfaceLines();
        acrInvoiceHd.setHeaderId(hdId);
        acrInvoiceHd.setReadLine(1L);
        List<FndInterfaceLines> fndInterfaceLines = fndInterfaceLinesMapper.fndInterfaceLinesDetailQuery(acrInvoiceHd);

        HlsCusAcrInvoiceHd allInvoiceHdParam = new HlsCusAcrInvoiceHd();
        allInvoiceHdParam.setReversedFlag("N");
        List<HlsCusAcrInvoiceHd> allAcrInvoiceHd = hlsCusAcrInvoiceHdMapper.select(allInvoiceHdParam);
        String  i=null;
        for (FndInterfaceLines fndInterfaceLine : fndInterfaceLines) {

            String documentNumber = fndInterfaceLine.getAttributes_9();
            System.out.println(documentNumber);
          /*  String isCancel = fndInterfaceLine.getAttributes_1();*/
            String isCancel ="否";//模板无该字段，所以暂定为更新，不删除
            String invoiceCode = fndInterfaceLine.getAttributes_2();
            String invoiceNumber = fndInterfaceLine.getAttributes_1();
            String taxInvoiceDateStr = fndInterfaceLine.getAttributes_7();
            validate("excel第" + fndInterfaceLine.getLineNumber() + "行:单据编号不能为空", documentNumber);


            List<HlsCusAcrInvoiceHd> acrInvoiceHdFilter = allAcrInvoiceHd.stream().filter(item -> item.getDocumentNumber().equals(documentNumber.trim())).collect(Collectors.toList());

                if (acrInvoiceHdFilter != null && acrInvoiceHdFilter.size() > 0) {
                    HlsCusAcrInvoiceHd opRecord = acrInvoiceHdFilter.get(0);
                    if (!"EXPORTED".equals(opRecord.getInvoiceStatus())) {
                        throw new RuntimeException("excel第" + fndInterfaceLine.getLineNumber() + "行:单据在系统里面不是已导出状态，不能导入");
                    }
                    if ("否".equals(isCancel.trim())) {
                   /* validate("excel第" + fndInterfaceLine.getLineNumber() + "行:发票代码不能为空", invoiceCode);
                    validate("excel第" + fndInterfaceLine.getLineNumber() + "行:发票编号不能为空", invoiceNumber);*/
                        opRecord.setInvoiceCode(invoiceCode);
                        opRecord.setInvoiceNumber(invoiceNumber);

                        try {
                            opRecord.setTaxInvoiceDate(df.parse(taxInvoiceDateStr));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        opRecord.set__status("update");
                        opRecord.setIsCancelFlag("N");




                    } else if ("是".equals(isCancel.trim())) {
                        opRecord.setInvoiceCode("");
                        opRecord.setInvoiceNumber("");
                        opRecord.setInvoiceStatus("CANCELLATION");
                        try {
                            opRecord.setTaxInvoiceDate(df.parse(taxInvoiceDateStr));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        opRecord.set__status("update");
                        opRecord.setIsCancelFlag("Y");
                    }

                } else {
                    throw new RuntimeException("excel第" + fndInterfaceLine.getLineNumber() + "行:发票编号" + documentNumber + "在系统中找不到");
                }
        }

        List<HlsCusAcrInvoiceHd> needUpdateDoc = allAcrInvoiceHd.stream().filter(item -> "update".equals(item.get__status())).collect(Collectors.toList());

        needUpdateDoc.forEach(item -> {
           /* if ("N".equals(item.getIsCancelFlag())) { // 不是作废的导入才插凭证
                HlsCusAcrInvoiceHd acrInvoiceHdParam = new HlsCusAcrInvoiceHd();
                acrInvoiceHdParam.setInvoiceHdId(item.getInvoiceHdId());
                HlsCusAcrInvoiceHd acrInvoiceHdDetail = hlsCusAcrInvoiceHdMapper.queryInvoiceDetail(acrInvoiceHdParam).get(0);
                saveInvoiceJeTrx(iRequest, acrInvoiceHdDetail);
            }*/
            item.setInvoiceStatus("IMPORTED");
            self().updateByPrimaryKeySelective(iRequest, item);
            insertHistory(iRequest, item.getInvoiceHdId(), item.getInvoiceStatus(), "IMPORTED".equals(item.getInvoiceStatus()) ? "发票导入" : "发票作废导入");

        });
    }
    @Autowired
    private FndCodingRuleValuesService codingRuleValuesService;
    public String getCodeValue(IRequest requestContext) {
        Map<String, String> params = new HashMap<String, String>();
        return codingRuleValuesService.getCodeRuleValue(requestContext, "AP_INVOICE", "ACP", "ACP", params);
    }

    @Override
    public void importVatAcpInvoice(IRequest requestCtx, Long headerId) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        //获取接口表数据
        Example example = new Example(FndInterfaceLines.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("headerId", headerId).andGreaterThan("lineNumber", 2);
        List<FndInterfaceLines> fndInterfaceLines = fndInterfaceLinesMapper.selectByExample(example);

        // 直接将记录存到acp头行表
        for (FndInterfaceLines fndInterfaceLine : fndInterfaceLines) {
            HlsCusAcpInvoiceHd acpInvoiceHd = new HlsCusAcpInvoiceHd();
            acpInvoiceHd.setCompanyId(requestCtx.getCompanyId());
            acpInvoiceHd.setDocumentNumber(fndCodingRuleValuesService.getCodeRuleValue(requestCtx, "AP_INVOICE", "ACP", "ACP", new HashMap()));
            acpInvoiceHd.setDocumentType("ACP");
            acpInvoiceHd.setDocumentCategory("AP_INVOICE");
            acpInvoiceHd.setBusinessType("ACP");
            acpInvoiceHd.setBillingMethod("EXCEL");
            acpInvoiceHd.setInvoiceKind(fndInterfaceLine.getAttributes_11());

            HlsCusBpMaster bpMasterParam = new HlsCusBpMaster();
            bpMasterParam.setBpName(fndInterfaceLine.getAttributes_6());
            List<HlsCusBpMaster> bpMasters = bpMasterMapper.select(bpMasterParam);
            if (bpMasters.size() > 0) {
                acpInvoiceHd.setBpId(bpMasters.get(0).getBpId());
                acpInvoiceHd.setBpAddressPhoneNum(bpMasters.get(0).getInvoiceBpAddressPhoneNum());
                acpInvoiceHd.setBpBankAccount(bpMasters.get(0).getInvoiceBpBankAccount());
            }
            acpInvoiceHd.setBpName(fndInterfaceLine.getAttributes_6());
            acpInvoiceHd.setBpTaxRegistryNum(fndInterfaceLine.getAttributes_5());
            acpInvoiceHd.setTotalAmount(Double.parseDouble(fndInterfaceLine.getAttributes_7()));
            acpInvoiceHd.setTaxAmount(Double.parseDouble(fndInterfaceLine.getAttributes_8()));
            acpInvoiceHd.setCurrency("CNY");
            acpInvoiceHd.setInvoiceCode(fndInterfaceLine.getAttributes_2());
            acpInvoiceHd.setInvoiceNumber(fndInterfaceLine.getAttributes_3());
            acpInvoiceHd.setInvoiceStatus(fndInterfaceLine.getAttributes_12());
            try {
                acpInvoiceHd.setInvoiceDate(df.parse(fndInterfaceLine.getAttributes_4()));
                acpInvoiceHd.setPostedDate(df.parse(fndInterfaceLine.getAttributes_10()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            acpInvoiceHd.setPostedType(fndInterfaceLine.getAttributes_9());
            acpInvoiceHdService.insert(requestCtx, acpInvoiceHd);

            HlsCusAcpInvoiceLn acpInvoiceLn = new HlsCusAcpInvoiceLn();
            acpInvoiceLn.setInvoiceHdId(acpInvoiceHd.getInvoiceHdId());
            try {
                acpInvoiceLn.setLineNumber(Long.parseLong(fndInterfaceLine.getAttributes_1().trim()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            acpInvoiceLn.setContractNumber(fndInterfaceLine.getAttributes_13());
            acpInvoiceLn.setContractName(fndInterfaceLine.getAttributes_14());
            acpInvoiceLn.setTotalAmount(Double.parseDouble(fndInterfaceLine.getAttributes_7()));
            acpInvoiceLn.setTaxAmount(Double.parseDouble(fndInterfaceLine.getAttributes_8()));

            if (acpInvoiceLn.getDocumentNumber() == null) {
                String value = getCodeValue(requestCtx);

               acpInvoiceLn.setDocumentNumber(value);

           }
            acpInvoiceLnService.insert(requestCtx, acpInvoiceLn);
        }
    }

    @Override
    public void saveDocAndDownload(List<HlsCusAcrInvoiceHd> dto, HttpServletRequest request, HttpServletResponse response) throws TokenException, FileReadIOException, Docx4JException {
        List<HlsCusAcrInvoiceHd> acrInvoiceHdList = dto;
        IRequest requestCtx = RequestHelper.getCurrentRequest();

        if (acrInvoiceHdList == null || acrInvoiceHdList.size() == 0) {
            throw new IllegalArgumentException("至少选择一条单据生成");
        }

        this.saveReceiptToData(requestCtx, acrInvoiceHdList);

        this.batchDownloadWithZip(acrInvoiceHdList, request, response);

    }

    @Override
    public List<HlsCusAcrInvoiceHd> updateHandoverStatus(IRequest iRequest, List<HlsCusAcrInvoiceHd> hlsCusAcrInvoiceHdList) {
        List<HlsCusAcrInvoiceHd> hlsCusAcrInvoiceHdList1 = new ArrayList<>();
        for(HlsCusAcrInvoiceHd hlsCusAcrInvoiceHd :hlsCusAcrInvoiceHdList){
            hlsCusAcrInvoiceHdMapper.updateHandoverStatus(hlsCusAcrInvoiceHd);
        }
        return hlsCusAcrInvoiceHdList1;

    }
    @Override
    public List<HlsCusAcrInvoiceHd> updateHandoverConfirmStatus(IRequest iRequest, List<HlsCusAcrInvoiceHd> hlsCusAcrInvoiceHdList) {
        List<HlsCusAcrInvoiceHd> hlsCusAcrInvoiceHdList1 = new ArrayList<>();
        for(HlsCusAcrInvoiceHd hlsCusAcrInvoiceHd :hlsCusAcrInvoiceHdList){
            hlsCusAcrInvoiceHd.setReviewPerson(iRequest.getEmployeeName());
            hlsCusAcrInvoiceHdMapper.updateHandoverStatusReview(hlsCusAcrInvoiceHd);
        }
        return hlsCusAcrInvoiceHdList1;

    }

    private void saveReceiptToData(IRequest requestCtx, List<HlsCusAcrInvoiceHd> acrInvoiceHdList) {
        for (HlsCusAcrInvoiceHd params : acrInvoiceHdList) {
            if (params.getReceiptLnId() != null) {
                continue;
            }
            // 收据头表
            HlsCusAcrReceiptHd acrReceiptHd = new HlsCusAcrReceiptHd();
            acrReceiptHd.setCompanyId(requestCtx.getCompanyId());
            acrReceiptHd.setBpId(params.getBpId());
            acrReceiptHd.setCurrency("CNY");
            acrReceiptHd.setDocumentType("RECEIPT");
            acrReceiptHd.setDocumentCategory("AR_RECEIPT");
            acrReceiptHd.setBusinessType("RECEIPT");
            acrReceiptHd.setDocumentNumber(fndCodingRuleValuesService.getCodeRuleValue(requestCtx, acrReceiptHd.getDocumentCategory(), acrReceiptHd.getDocumentType(), acrReceiptHd.getBusinessType(), new HashMap<>()));
            acrReceiptHd.setReceiptDate(new Date());
            acrReceiptHd.setReceiptStatus("PRINT");
            acrReceiptHd.setTotalAmount(params.getDueAmount());
            hlsCusAcrReceiptHdMapper.insertSelective(acrReceiptHd);
            HlsCusAcrReceiptLn acrReceiptLn = new HlsCusAcrReceiptLn();
            acrReceiptLn.setReceiptHdId(acrReceiptHd.getReceiptHdId());
            acrReceiptLn.setSourceType(params.getDocumentCategory());
            acrReceiptLn.setSourceId(params.getCashflowId());
            acrReceiptLn.setAmount(params.getDueAmount());
            receiptLnService.insertSelective(requestCtx, acrReceiptLn);

            params.setReceiptLnId(acrReceiptLn.getReceiptLnId());
        }
    }


    private void deleteSysFile(IRequest currentRequest, Long fltReqLnId) {
        //生成之前先删掉所有的之前生成的附件
        HlsCusSysAttachment sysAttachment = new HlsCusSysAttachment();
        sysAttachment.setSourceType("ACR_RECEIPT_ATTACHMENT");
        sysAttachment.setSourceKey(fltReqLnId.toString());
        List<HlsCusSysAttachment> attachmentList = hlsSysAttachmentService.select(currentRequest, sysAttachment, 1, 999999);
        hlsSysAttachmentService.batchDelete(attachmentList);
    }


    private void insertHistory(IRequest requestCtx, Long invoiceHdId, String action, String description) {
//        AcrInvoiceHistory acrInvoiceHistory = new AcrInvoiceHistory();
//        acrInvoiceHistory.setInvoiceHdId(invoiceHdId);
//        acrInvoiceHistory.setAction(action);
//        acrInvoiceHistory.setDescription(description);
//        acrInvoiceHistoryService.insert(requestCtx, acrInvoiceHistory);
    }

    private void insertReverseData(IRequest requestCtx, HlsCusAcrInvoiceHd acrInvoiceHdDetail) {
        HlsCusAcrInvoiceHd acrInvoiceHdReverse = hlsCusAcrInvoiceHdMapper.selectByPrimaryKey(acrInvoiceHdDetail.getInvoiceHdId());
        acrInvoiceHdReverse.setTotalAmount(-acrInvoiceHdReverse.getTotalAmount());
        acrInvoiceHdReverse.setTaxAmount(-acrInvoiceHdReverse.getTaxAmount());
        acrInvoiceHdReverse.setNetAmount(-acrInvoiceHdReverse.getNetAmount());
        acrInvoiceHdReverse.setReversedFlag("R");
        acrInvoiceHdReverse.setInvoiceStatus("CLOSE");
        acrInvoiceHdReverse.setInvoiceHdId(null);
        self().insert(requestCtx, acrInvoiceHdReverse);


        HlsCusAcrInvoiceLn acrInvoiceLnReverse = new HlsCusAcrInvoiceLn();
        acrInvoiceLnReverse.setInvoiceLnId(acrInvoiceHdDetail.getInvoiceLnId());
        acrInvoiceLnReverse = acrInvoiceLnService.selectByPrimaryKey(requestCtx, acrInvoiceLnReverse);
        acrInvoiceLnReverse.setTotalAmount(-acrInvoiceLnReverse.getTotalAmount());
        acrInvoiceLnReverse.setTaxAmount(-acrInvoiceLnReverse.getTaxAmount());
        acrInvoiceLnReverse.setNetAmount(-acrInvoiceLnReverse.getNetAmount());
        acrInvoiceLnReverse.setInvoiceHdId(acrInvoiceHdReverse.getInvoiceHdId());
        acrInvoiceLnReverse.setInvoiceLnId(null);
        acrInvoiceLnService.insert(requestCtx, acrInvoiceLnReverse);


        HlsCusAcrInvoiceRelationship acrInvoiceRelationshipReverse = new HlsCusAcrInvoiceRelationship();
        acrInvoiceRelationshipReverse.setInvoiceRelationId(acrInvoiceHdDetail.getInvoiceRelationId());
        acrInvoiceRelationshipReverse = acrInvoiceRelationshipService.selectByPrimaryKey(requestCtx, acrInvoiceRelationshipReverse);
        acrInvoiceRelationshipReverse.setBillingAmount(-acrInvoiceRelationshipReverse.getBillingAmount());
        acrInvoiceRelationshipReverse.setTaxAmount(-acrInvoiceRelationshipReverse.getTaxAmount());
        acrInvoiceRelationshipReverse.setNetAmount(-acrInvoiceRelationshipReverse.getNetAmount());
        acrInvoiceRelationshipReverse.setInvoiceLnId(acrInvoiceLnReverse.getInvoiceLnId());
        acrInvoiceRelationshipReverse.setInvoiceRelationId(null);
        acrInvoiceRelationshipService.insert(requestCtx, acrInvoiceRelationshipReverse);

        // 如果是关闭作废的发票就要插反冲凭证
        if ("CANCELLATION".equals(acrInvoiceHdDetail.getInvoiceStatus())) {
            acrInvoiceHdDetail.setInvoiceRelationId(acrInvoiceRelationshipReverse.getInvoiceRelationId());
            saveInvoiceJeTrx(requestCtx, acrInvoiceHdDetail);
        }
    }

    private void insertReverseData(IRequest requestCtx, HlsCusAcrInvoiceHd acrInvoiceHd, String redInvoiceCode, String redInvoiceNumber) {
        HlsCusAcrInvoiceHd acrInvoiceHdReverse = hlsCusAcrInvoiceHdMapper.selectByPrimaryKey(acrInvoiceHd.getInvoiceHdId());
        acrInvoiceHdReverse.setTotalAmount(-acrInvoiceHdReverse.getTotalAmount());
        acrInvoiceHdReverse.setTaxAmount(-acrInvoiceHdReverse.getTaxAmount());
        acrInvoiceHdReverse.setNetAmount(-acrInvoiceHdReverse.getNetAmount());
        acrInvoiceHdReverse.setReversedFlag("R");
        acrInvoiceHdReverse.setInvoiceHdId(null);
        acrInvoiceHdReverse.setInvoiceCode(redInvoiceCode);
        acrInvoiceHdReverse.setInvoiceNumber(redInvoiceNumber);
        acrInvoiceHdReverse.setReverseDate(new Date());
        acrInvoiceHdReverse.setSourceInvoiceHeaderId(acrInvoiceHd.getInvoiceHdId());
        self().insert(requestCtx, acrInvoiceHdReverse);


        HlsCusAcrInvoiceLn acrInvoiceLnReverse = new HlsCusAcrInvoiceLn();
        acrInvoiceLnReverse.setInvoiceLnId(acrInvoiceHd.getInvoiceLnId());
        acrInvoiceLnReverse = acrInvoiceLnService.selectByPrimaryKey(requestCtx, acrInvoiceLnReverse);
        acrInvoiceLnReverse.setTotalAmount(-acrInvoiceLnReverse.getTotalAmount());
        acrInvoiceLnReverse.setTaxAmount(-acrInvoiceLnReverse.getTaxAmount());
        acrInvoiceLnReverse.setNetAmount(-acrInvoiceLnReverse.getNetAmount());
        acrInvoiceLnReverse.setPrice(-acrInvoiceLnReverse.getPrice());
        acrInvoiceLnReverse.setInvoiceHdId(acrInvoiceHdReverse.getInvoiceHdId());
        acrInvoiceLnReverse.setInvoiceLnId(null);
        acrInvoiceLnService.insert(requestCtx, acrInvoiceLnReverse);


        HlsCusAcrInvoiceRelationship acrInvoiceRelationshipReverse = new HlsCusAcrInvoiceRelationship();
        acrInvoiceRelationshipReverse.setInvoiceRelationId(acrInvoiceHd.getInvoiceRelationId());
        acrInvoiceRelationshipReverse = acrInvoiceRelationshipService.selectByPrimaryKey(requestCtx, acrInvoiceRelationshipReverse);
        acrInvoiceRelationshipReverse.setBillingAmount(-acrInvoiceRelationshipReverse.getTaxAmount());
        acrInvoiceRelationshipReverse.setTaxAmount(-acrInvoiceRelationshipReverse.getTaxAmount());
        acrInvoiceRelationshipReverse.setNetAmount(-acrInvoiceRelationshipReverse.getNetAmount());
        acrInvoiceRelationshipReverse.setInvoiceLnId(acrInvoiceLnReverse.getInvoiceLnId());
        acrInvoiceRelationshipReverse.setInvoiceRelationId(null);
        acrInvoiceRelationshipService.insert(requestCtx, acrInvoiceRelationshipReverse);

        // 凭证
        acrInvoiceHd.setInvoiceRelationId(acrInvoiceRelationshipReverse.getInvoiceRelationId());
        saveInvoiceJeTrx(requestCtx, acrInvoiceHd);
    }

    private void releaseCashflowByInvoiceHdId(IRequest requestCtx, HlsCusAcrInvoiceHd acrInvoiceHd) {

        String sourceDocumentType = acrInvoiceHd.getSourceDocumentType();
        Long cashflowId = acrInvoiceHd.getSourceDocumentId();

        if ("FCT_CONTRACT".equals(sourceDocumentType)) { // 保理
            HlsCusFctQuotationCashflow fctQuotationCashflow = new HlsCusFctQuotationCashflow();
            fctQuotationCashflow.setQuotationCashflowId(cashflowId);

            fctQuotationCashflow.setBillingAmount(CalculateUtil.sub(fctQuotationCashflow.getBillingAmount(), acrInvoiceHd.getTotalAmount()));
            if (fctQuotationCashflow.getBillingAmount().compareTo(0D) == 0) {
                fctQuotationCashflow.setBillingStatus("NOT");
            } else {
                fctQuotationCashflow.setBillingStatus("PARTIAL");
            }
            hlsCusFctQuotationCashflowService.updateByPrimaryKeySelective(requestCtx, fctQuotationCashflow);
        } else if ("CON_CONTRACT".equals(sourceDocumentType)) { // 租赁
            HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
            conContractCashflow.setCashflowId(cashflowId);
            conContractCashflow.setBillingAmount(CalculateUtil.sub(conContractCashflow.getBillingAmount(), acrInvoiceHd.getTotalAmount()));
//            conContractCashflow.setBillingPrincipal(0D);
//            conContractCashflow.setBillingInterest(0D); 租赁还没开始开发
            if (conContractCashflow.getBillingAmount().compareTo(0D) == 0) {
                conContractCashflow.setBillingStatus("NOT");
            } else {
                conContractCashflow.setBillingStatus("PARTIAL");
            }

            cashflowService.updateByPrimaryKeySelective(requestCtx, conContractCashflow);
        }

    }

    private void setData(XSSFWorkbook workbook, XSSFSheet sheet, XSSFRow row, HlsCusAcrInvoiceHd acrInvoiceHd) throws InvocationTargetException, IllegalAccessException, HlsCusException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DecimalFormat df = new DecimalFormat("###,##0.00");
        List<String> colGetMethods = Arrays.asList("invoiceNumber","invoiceCode" , "bpName", "bpTaxRegistryNum", "bpBankAccount", "bpAddressPhoneNum", "invoiceDate", "", "documentNumber", "productName", "specification", "uom", "quantity", "price", "totalAmount", "taxTypeRate", "taxAmount", "taxTypeNum");
        for (int i = 0; i < colGetMethods.size(); i++) {
            XSSFCell cell = row.createCell(i);
            Object value;
            Long invoiceHdId=acrInvoiceHd.getInvoiceHdId();
            Map map=new HashMap();
            map.put("invoiceHdId",invoiceHdId);
            List<Map> mapList= hlsCusAcrInvoiceHdMapper.queryVatAcrInvoiceDetail(map);

            //如果是备注，那么就显示业务合同编号
            if("description".equalsIgnoreCase(colGetMethods.get(i))){

                if(mapList.size()>1){
                    throw new HlsCusException("单个发票头查出多行");
                }
                value=((HlsCusAcrInvoiceHd) mapList.get(0)).getContractContentNumber();
                //如果是发票种类
            } else if ("invoiceKindDesc".equalsIgnoreCase(colGetMethods.get(i))) {
                value = ExportExcelUtil.getValue(acrInvoiceHd, colGetMethods.get(i));
                if ("增值税专用发票".equalsIgnoreCase((String)value)) {
                    value="增票";
                }else{
                    value="普票";
                }

            }else if("taxTypeRate".equalsIgnoreCase(colGetMethods.get(i))){
                value = ExportExcelUtil.getValue(acrInvoiceHd, colGetMethods.get(i));
                value=Double.parseDouble(value.toString())*100+"%";
            } else {
                System.out.println(colGetMethods.get(i));
                value = ExportExcelUtil.getValue(acrInvoiceHd, colGetMethods.get(i));
            }

            if (value instanceof Date) {
                value = simpleDateFormat.format(value);
            }
            if (value instanceof Double) {
                XSSFCellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setAlignment(HorizontalAlignment.RIGHT);
                value = df.format(value);
                cell.setCellStyle(cellStyle);
            }
            if (Objects.nonNull(value)) {
                ExportExcelUtil.initColWidth(sheet, i, value.toString());
                cell.setCellValue(value.toString());
            }

        }
    }

    private void saveInvoiceJeTrx(IRequest requestCtx, HlsCusAcrInvoiceHd acrInvoiceHd) {
        Map param = new HashMap<>();
        param.put("jeTrxId", acrInvoiceHd.getInvoiceRelationId());
        param.put("companyId", requestCtx.getCompanyId());
        param.put("sourceDoc", acrInvoiceHd.getSourceDocumentType());
        param.put("contractId", acrInvoiceHd.getContractId());
        AbstractJeTrxService acrInvoiceConfirmJeTrxService = JeTrxCommonService.map.get("ACR_INVOICE_CONFIRM");
        acrInvoiceConfirmJeTrxService.process(requestCtx, param);
    }

    @Override
    public void contextInitialized(ApplicationContext applicationContext) {
        Map<String, IConFloatingCalcService> calcMap = applicationContext.getBeansOfType(IConFloatingCalcService.class);
        calcMap.forEach((k, v) -> {
            conFloatingCalcServiceMap.put(v.getFloatingCalcMethod(), v);
        });
    }
}
