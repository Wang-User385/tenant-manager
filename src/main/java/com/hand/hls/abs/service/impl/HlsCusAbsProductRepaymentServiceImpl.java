package com.hand.hls.abs.service.impl;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.CodeValue;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.ICodeService;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.abs.dto.HlsCusAbsProductQuotation;
import com.hand.hls.abs.dto.HlsCusAbsProductRelease;
import com.hand.hls.abs.dto.HlsCusAbsProductRepayment;
import com.hand.hls.abs.mapper.HlsCusAbsProductRepaymentMapper;
import com.hand.hls.abs.service.HlsCusAbsProductQuotationService;
import com.hand.hls.abs.service.HlsCusAbsProductReleaseService;
import com.hand.hls.abs.service.HlsCusAbsProductRepaymentService;
import com.hand.hls.abs.service.HlsCusAbsProductService;
import com.hand.hls.bp.dto.HlsCusSysFile;
import com.hand.hls.bp.service.HlsSysFileService;
import com.hand.hls.eft.dto.HlsCusFundTransferList;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.service.HlsCusImpDataService;
import com.hand.hls.fnd.utils.HlsCusImportDataUtil;
import com.hand.hls.prj.dto.HlsCusPrjProjectAttachment;
import com.hand.hls.prj.service.HlsCusPrjProjectAttachmentService;
import com.hand.hls.utils.ExportExcelUtil;
import com.hand.hls.utils.HlsCusConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusAbsProductRepaymentServiceImpl extends BaseServiceImpl<HlsCusAbsProductRepayment> implements HlsCusAbsProductRepaymentService {

    @Autowired
    private HlsCusAbsProductRepaymentMapper productRepaymentMapper;

    @Autowired
    private ICodeService codeService;

    @Autowired
    private HlsCusAbsProductService absProductService;

    @Autowired
    private HlsCusAbsProductQuotationService productQuotationService;

    @Autowired
    private HlsCusImpDataService impDataService;

    @Autowired
    private HlsCusAbsProductReleaseService productReleaseService;

    @Autowired
    private HlsCusPrjProjectAttachmentService hlsCusPrjProjectAttachmentService;

    @Autowired
    private HlsSysFileService hlsSysFileService;

    @Override
    public List<HlsCusAbsProductRepayment> selectRepaymentPlanData(IRequest iRequest, HlsCusAbsProductRepayment productRepayment, int page, int pageSize) {

        PageHelper.startPage(page,pageSize);

        return productRepaymentMapper.selectRepaymentPlanData(productRepayment);
    }

    @Override
    public void batchDeleteRepayment(IRequest request, List<HlsCusAbsProductRepayment> productRepayments) throws HlsCusException {
        if (productRepayments.size() > 0) {
            for (HlsCusAbsProductRepayment repayment : productRepayments) {
                if ("Y".equalsIgnoreCase(repayment.getConfirmFlag())) {
                    throw new HlsCusException("已经确认的还款计划不可删除，请核对！");
                }
            }
            self().batchDelete(productRepayments);
        }
    }

    //sheet名称
    private final static String SHEET_NAME = "sheet1";
    //文件名称
    private final static String FILE_NAME_REPAY = "本息还款计划";
    private static final List<String> repayColNameList = Lists.newArrayList(
            "期数", "现金流项目", "还款日期", "本币还款金额");
    private static final List<String> repayColGetMethods = Lists.newArrayList("times", "cfItemDesc", "plannedDueDate",
            "cnyPlannedDueAmount");

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");

    @Override
    public void exportRepaymentReport(HttpServletRequest request, HttpServletResponse response, HlsCusAbsProductRepayment hlsCusLonContractRepayment) throws IOException, InvocationTargetException, IllegalAccessException {

        XSSFWorkbook xwork = new XSSFWorkbook();
        XSSFSheet sheet = xwork.createSheet(SHEET_NAME);
        List<HlsCusAbsProductRepayment> lonContractReps = productRepaymentMapper.selectRepaymentPlanData(hlsCusLonContractRepayment);

            int dataRowNum = ExportExcelUtil.createCommonExcelHead(xwork, sheet, null, repayColNameList);
            for (HlsCusAbsProductRepayment repayment : lonContractReps) {
                XSSFRow row = sheet.createRow(dataRowNum++);
                setData(xwork, sheet, row, repayment, repayColGetMethods);
            }
            ExportExcelUtil.IOWrite(xwork, null, request, response, FILE_NAME_REPAY);
    }

    /**
     * 把数据设置到Excel行中
     *
     * @param row
     * @param
     */
    private void setData(XSSFWorkbook workbook, XSSFSheet sheet, XSSFRow row, HlsCusAbsProductRepayment repayment, List<String> colGetMethods) throws IOException, InvocationTargetException, IllegalAccessException {
        DecimalFormat df = new DecimalFormat("###,##0.00");
        for (int i = 0; i < colGetMethods.size(); i++) {
            XSSFCell cell = row.createCell(i);
            Object value = ExportExcelUtil.getValue(repayment, colGetMethods.get(i));
            if (value instanceof Date) {
                value = simpleDateFormat.format(value);
            }
            if (value instanceof Double && !"exchangeRate".equals(colGetMethods.get(i))) {
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


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int importData(IRequest iRequest, List<Map<String, String>> dataMap, Map<String, String> descMap, String lang) {
        int errorCount = 0;
        try {
            //传递时间格式
            List<CodeValue> dateFormat = codeService.selectCodeValuesByCodeName(iRequest, "FND_IMP_DATE_FORMAT");
            List<String> formats = new ArrayList<>();
            for (CodeValue c : dateFormat) {
                formats.add(c.getValue());
            }
            List<Object> objects = HlsCusImportDataUtil.dataToDto(HlsCusAbsProductRepayment.class, dataMap, formats);


            Long productId = Long.parseLong(dataMap.get(0).get("tempKey"));
            //获取交易信息
            HlsCusAbsProductQuotation productQuotation = new HlsCusAbsProductQuotation();
            productQuotation.setProductId(productId);
            List<HlsCusAbsProductQuotation> hlsCusAbsProductQuotations = productQuotationService.select(iRequest, productQuotation, 1, 9999);
            //校验NPE
            if (CollectionUtils.isNotEmpty(hlsCusAbsProductQuotations)) {
                productQuotation = hlsCusAbsProductQuotations.get(0);
            }
            //如果是一次性还本，那么需要校验导入的还款-本金只有一条数据
            if (HlsCusConstant.LON_INTEREST_CALC_METHOD.ONCE_REPAYMENT.equalsIgnoreCase(productQuotation.getInterestCalcMethod())) {
                if (objects.size() > 1) {
                    for (Map<String, String> m : dataMap) {
                        impDataService.updateErrMessage(m, "当前计息方式为：一次性还本或者利随本清，只允许有一条还款-本金");
                    }
                    return 0;
                }
            }

            //在导入之前先将当前的现金流删除掉
            HlsCusAbsProductRepayment repayment=new HlsCusAbsProductRepayment();
            repayment.setProductId(productId);
            productRepaymentMapper.delete(repayment);
            for (int i = 0; i < objects.size(); i++) {
                StringBuilder message = new StringBuilder();
                HlsCusAbsProductRepayment lonContractRepayment = (HlsCusAbsProductRepayment) objects.get(i);
                //判断是融资-还款本金 or 融资还款-利息
                if(HlsCusConstant.LON_REPAYMENT.PRINCIPAL_CF_ITEM_DESC.equalsIgnoreCase(lonContractRepayment.getCfItemDesc())){
                    lonContractRepayment.setCfItem(301L);
                }else if(HlsCusConstant.LON_REPAYMENT.INTEREST_CF_ITEM_DESC.equalsIgnoreCase(lonContractRepayment.getCfItemDesc())){
                    //如果测算类型是系统，那么导入的现金流中不能包括 利息
                    if(HlsCusConstant.MEASUREMENT_TYPE.SYSTEM_ESTIMATION.equalsIgnoreCase(productQuotation.getMeasureType())){
                        message.append("系统测算类型不支持导入还款-利息");
                    }
                    lonContractRepayment.setCfItem(302L);
                }else{
                    message.append("不支持的现金流项目");
                }

                lonContractRepayment.setPlannedDueAmount((new BigDecimal(lonContractRepayment.getPlannedDueAmount().toString()).setScale(2, BigDecimal.ROUND_HALF_UP)).doubleValue());
                lonContractRepayment.setCnyDueAmount(0D);
                lonContractRepayment.setDueAmount(0D);
                lonContractRepayment.setWriteOffAmount(0D);
                lonContractRepayment.setCfType(70L);
                lonContractRepayment.setCfDirection("OUTFLOW");
                lonContractRepayment.setCfStatus("RELEASE");
                lonContractRepayment.setWriteOffFlag("NOT");
                lonContractRepayment.setConfirmFlag("N");
                lonContractRepayment.setProductId(productId);
                lonContractRepayment.setProjectId(productQuotation.getProjectId());
                lonContractRepayment.setPlannedDueDate(lonContractRepayment.getPlannedCalcDate());
                if (lonContractRepayment.getExchangeRate() == null) {
                    lonContractRepayment.setExchangeRate(1D);
                }
                lonContractRepayment.setCreatedBy(iRequest.getUserId());
                lonContractRepayment.setLastUpdatedBy(iRequest.getUserId());
                lonContractRepayment.setLastUpdateLogin(iRequest.getUserId());
                //后续操作
                productRepaymentMapper.insertSelective(lonContractRepayment);

                Double planAmountSum = productRepaymentMapper.selectRepaymentPlanAmountSum(productId, "301");


                if (planAmountSum > productQuotation.getDueAmount()) {
                    message.append("[还款计划总金额超出限制]");
                }
                //融资币种
                if ("CNY".equals(productQuotation.getProductCurrencyCode())) {
                    if (new BigDecimal(1).compareTo(new BigDecimal(lonContractRepayment.getExchangeRate())) != 0) {
                        message.append("[融资币种为人民币,汇率只能是1]");
                    }
                }

                if (org.apache.commons.lang3.StringUtils.isNotBlank(message.toString())) {
                    errorCount++;
                    impDataService.updateErrMessage(dataMap, i, message.toString());
                }

            }

            if (errorCount > 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return dataMap.size() - errorCount;
            }

            absProductService.calXirr(iRequest,productQuotation);
        }  catch (Exception e) {
            e.printStackTrace();
            for (Map<String, String> m : dataMap) {
                impDataService.updateErrMessage(m, e.getClass() + ":" + e.getMessage());
            }
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }

        return dataMap.size() - errorCount;
    }


    @Override
    public void confirmProductRepayment(IRequest iRequest, HlsCusFundTransferList fundTransferList) throws HlsCusException {
        HlsCusAbsProductRepayment productRepayment = productRepaymentMapper.selectByPrimaryKey(fundTransferList.getSourceDocLineId());
        if(productRepayment.getDueAmount()==null){
            productRepayment.setDueAmount(0D);
        }
        if(productRepayment.getCnyDueAmount()==null){
            productRepayment.setCnyDueAmount(0D);
        }
        productRepayment.setDueAmount(new BigDecimal(productRepayment.getDueAmount().toString()).add(fundTransferList.getActualPayAmount()).doubleValue());
        productRepayment.setCnyDueAmount(new BigDecimal(productRepayment.getCnyDueAmount().toString()).add(fundTransferList.getCnyActualPayAmount()).doubleValue());
        productRepayment.setWriteOffAmount(productRepayment.getDueAmount());
        if (Double.compare(productRepayment.getDueAmount(), productRepayment.getPlannedDueAmount()) < 0) {
            productRepayment.setConfirmFlag(HlsCusConstant.FLAG.Y);
            productRepayment.setWriteOffFlag(HlsCusConstant.FCT_WRITE_OFF_FLAG.PARTIAL);
        } else if (Double.compare(productRepayment.getDueAmount(),productRepayment.getPlannedDueAmount()) == 0) {
            productRepayment.setConfirmFlag(HlsCusConstant.FLAG.Y);
            productRepayment.setWriteOffFlag(HlsCusConstant.FCT_WRITE_OFF_FLAG.FULL);
        } else {
            throw new HlsCusException("还款金额不能大于计划还款金额");
        }
        productRepaymentMapper.updateByPrimaryKeySelective(productRepayment);
    }

    @Override
    public HlsCusAbsProductRepayment saveRepaymentRelease(IRequest request, HlsCusAbsProductRepayment hlsCusAbsProductRepayment) throws HlsCusException {
        if(CollectionUtils.isNotEmpty(hlsCusAbsProductRepayment.getHlsCusAbsProductReleases())){
            for (HlsCusAbsProductRelease productRelease : hlsCusAbsProductRepayment.getHlsCusAbsProductReleases()) {
                productRelease.setSourceDocId(hlsCusAbsProductRepayment.getRepaymentId());
                productRelease.setProductId(hlsCusAbsProductRepayment.getProductId());
                productRelease.setSourceDocCategory(HlsCusConstant.ABS_RELEASE_TYPE.REPAYMENT);
                if(productRelease.getReleaseId()==null) {
                    productRelease.set__status(DTOStatus.ADD);
                }else {
                    productRelease.set__status(DTOStatus.UPDATE);
                }
            }
            productReleaseService.batchUpdate(request, hlsCusAbsProductRepayment.getHlsCusAbsProductReleases());
        }

        HlsCusAbsProductRelease productRelease=new HlsCusAbsProductRelease();
        productRelease.setSourceDocId(hlsCusAbsProductRepayment.getRepaymentId());
        productRelease.setProductId(hlsCusAbsProductRepayment.getProductId());
        productRelease.setSourceDocCategory(HlsCusConstant.ABS_RELEASE_TYPE.REPAYMENT);
        BigDecimal realseAmounSum = productReleaseService.selectRealseAmounSum(productRelease);
        if(realseAmounSum.compareTo(new BigDecimal(hlsCusAbsProductRepayment.getPlannedDueAmount().toString()))>0){
            throw new HlsCusException("总释放额度不可大于应还款金额");
        }


        if (CollectionUtils.isNotEmpty(hlsCusAbsProductRepayment.getHlsCusPrjProjectAttachments())) {
            for (HlsCusPrjProjectAttachment attachment: hlsCusAbsProductRepayment.getHlsCusPrjProjectAttachments()){
                if(attachment.getProjectAttachmentId()!=null){
                    if(attachment.getDescription()==null){
                        attachment.setDescription("");
                    }
                    if(attachment.getAttachmentCode()==null){
                        attachment.setAttachmentCode("");
                    }
                    hlsCusPrjProjectAttachmentService.updateByPrimaryKeySelective(request,attachment);

                    HlsCusSysFile sysFile=new HlsCusSysFile();
                    sysFile.setFileId(Long.parseLong(attachment.getFileId()));
                    sysFile.setFileName(attachment.getFileName());
                    hlsSysFileService.updateByPrimaryKeySelective(request,sysFile);
                }
            }
        }

        return hlsCusAbsProductRepayment;

    }


    @Override
    public int selectNotFullRepaymentCount(Long productId) {
        return productRepaymentMapper.selectNotFullRepaymentCount(productId);
    }
}