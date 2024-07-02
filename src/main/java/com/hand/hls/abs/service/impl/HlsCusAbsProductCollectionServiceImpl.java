package com.hand.hls.abs.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.excel.dto.ColumnInfo;
import com.hand.hap.excel.dto.ExportConfig;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.dto.CodeValue;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.ICodeService;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.abs.dto.HlsCusAbsProduct;
import com.hand.hls.abs.dto.HlsCusAbsProductCashDetail;
import com.hand.hls.abs.dto.HlsCusAbsProductCollection;
import com.hand.hls.abs.dto.HlsCusAbsProductRelease;
import com.hand.hls.abs.mapper.HlsCusAbsProductCashDetailMapper;
import com.hand.hls.abs.mapper.HlsCusAbsProductCollectionMapper;
import com.hand.hls.abs.service.HlsCusAbsProductCashDetailService;
import com.hand.hls.abs.service.HlsCusAbsProductCollectionService;
import com.hand.hls.abs.service.HlsCusAbsProductReleaseService;
import com.hand.hls.abs.service.HlsCusAbsProductService;
import com.hand.hls.bp.dto.HlsCusSysFile;
import com.hand.hls.bp.service.HlsSysFileService;
import com.hand.hls.eft.dto.HlsCusFundTransferList;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.fnd.service.HlsCusImpDataService;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.prj.dto.HlsCusPrjProjectAttachment;
import com.hand.hls.prj.service.HlsCusPrjProjectAttachmentService;
import com.hand.hls.user.service.LoginUserInfoService;
import com.hand.hls.utils.BeanRefUtil;
import com.hand.hls.utils.ExportExcelUtil;
import com.hand.hls.utils.HlsCusConstant;
import com.hand.hls.utils.HlsCusImportDataUtil;
import com.hand.hls.wfl.service.IActivitiStartService;
import com.hand.hls.abs.service.*;
import hls.core.sys.event.service.SysEventService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.Asserts;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusAbsProductCollectionServiceImpl extends BaseServiceImpl<HlsCusAbsProductCollection> implements HlsCusAbsProductCollectionService {

    @Autowired
    private HlsCusAbsProductCollectionMapper hlsCusAbsProductCollectionMapper;
    @Autowired
    private HlsCusAbsProductService hlsCusAbsProductService;
    @Autowired
    private ICodeService codeService;
    @Autowired
    private HlsCusImpDataService impDataService;

    @Autowired
    private HlsCusAbsProductCashDetailService productCashDetailService;

    @Autowired
    private HlsCusAbsProductReleaseService productReleaseService;

    @Autowired
    private HlsCusPrjProjectAttachmentService hlsCusPrjProjectAttachmentService;

    @Autowired
    private HlsSysFileService hlsSysFileService;
    @Autowired
    private HlsCusAbsProductCashDetailMapper hlsCusAbsProductCashDetailMapper;
    @Override
    public List<HlsCusAbsProductCollection> selectProductCollectionData(IRequest request, HlsCusAbsProductCollection hlsCusAbsProductCollection, int page, int pageSize) {

        PageHelper.startPage(page,pageSize);

        return hlsCusAbsProductCollectionMapper.selectProductCollectionData(hlsCusAbsProductCollection);
    }

    @Override
    public void deleteNotConfirmByProductId(Long productId ,String dataClass) {
        hlsCusAbsProductCollectionMapper.deleteNotConfirmByProductId(productId,dataClass);
    }

    @Override
    public HlsCusAbsProductCollection queryCollection(IRequest request, HlsCusAbsProductCollection hlsCusAbsProductCollection) {
        return hlsCusAbsProductCollectionMapper.queryCollection(hlsCusAbsProductCollection);
    }

    @Override
    public HlsCusAbsProductCollection queryCashDeatil(IRequest request, HlsCusAbsProductCollection hlsCusAbsProductCollection) {
        return hlsCusAbsProductCollectionMapper.queryCashDeatil(hlsCusAbsProductCollection);
    }

    @Override
    public HlsCusAbsProductCollection queryRemittance(IRequest request, HlsCusAbsProductCollection hlsCusAbsProductCollection) {
        return hlsCusAbsProductCollectionMapper.queryRemittance(hlsCusAbsProductCollection);
    }

    @SuppressWarnings("ALL")
    @Override
    public HlsCusAbsProductCollection save(IRequest request, HlsCusAbsProductCollection hlsCusAbsProductCollection) {
        HlsCusAbsProduct hlsCusAbsProduct = new HlsCusAbsProduct();
        hlsCusAbsProduct.setProductId(hlsCusAbsProductCollection.getProductId());
        hlsCusAbsProduct = hlsCusAbsProductService.selectByPrimaryKey(request, hlsCusAbsProduct);
        //增加校验: 若前一期归集未确认则不能保存
        HlsCusAbsProductCollection collection = new HlsCusAbsProductCollection();
        collection.setProductId(hlsCusAbsProductCollection.getProductId());
        List<HlsCusAbsProductCollection> list = this.select(request, collection, 1, 99999);
        list.sort(Comparator.comparing(HlsCusAbsProductCollection::getTimes));
        String status = "";
        for (int i = 0; i < list.size(); i++) {
            if (Long.compare(list.get(i).getTimes(), hlsCusAbsProductCollection.getTimes()) == 0 && Long.compare(list.get(i).getTimes(), 1L) != 0) {
                status = list.get(i - 1).getCollectionStatus();
                break;
            }
        }
        if (StringUtils.equalsIgnoreCase("NEW", status)) {
            throw new IllegalArgumentException("请先归集上一期金额!");
        }
        if (hlsCusAbsProductCollection.getOldCollectionId() == null && StringUtils.isEmpty(hlsCusAbsProductCollection.getCollectionNumber())) {
            hlsCusAbsProductCollection.setCollectionNumber(createNumber("collectionNumber", hlsCusAbsProduct.getProjectId()));
        } else if (hlsCusAbsProductCollection.getOldCollectionId() == null && !StringUtils.isEmpty(hlsCusAbsProductCollection.getCollectionNumber())) {

        } else if (Long.compare(hlsCusAbsProductCollection.getOldCollectionId(), hlsCusAbsProductCollection.getCollectionId()) == 0 &&
                StringUtils.isEmpty(hlsCusAbsProductCollection.getCollectionNumber())) {
            hlsCusAbsProductCollection.setCollectionNumber(createNumber("collectionNumber", hlsCusAbsProduct.getProjectId()));
        } else if (Long.compare(hlsCusAbsProductCollection.getOldCollectionId(), hlsCusAbsProductCollection.getCollectionId()) != 0) {
            hlsCusAbsProductCollectionMapper.resetCollection(hlsCusAbsProductCollection.getOldCollectionId());
            hlsCusAbsProductCollection.setCollectionNumber(createNumber("collectionNumber", hlsCusAbsProduct.getProjectId()));
        }
        hlsCusAbsProductCollection.setCollectionApplyDate(new Date());
        hlsCusAbsProductCollection = self().updateByPrimaryKeySelective(request, hlsCusAbsProductCollection);
        return hlsCusAbsProductCollection;
    }

    @SuppressWarnings("ALL")
    @Override
    public HlsCusAbsProductCollection remittanceSave(IRequest request, HlsCusAbsProductCollection hlsCusAbsProductCollection) {
        HlsCusAbsProduct hlsCusAbsProduct = new HlsCusAbsProduct();
        hlsCusAbsProduct.setProductId(hlsCusAbsProductCollection.getProductId());
        hlsCusAbsProduct = hlsCusAbsProductService.selectByPrimaryKey(request, hlsCusAbsProduct);
        //增加校验: 若前一期转付未确认则不能保存
        HlsCusAbsProductCollection collection = new HlsCusAbsProductCollection();
        collection.setProductId(hlsCusAbsProductCollection.getProductId());
        List<HlsCusAbsProductCollection> list = this.select(request, collection, 1, 99999);
        list.sort(Comparator.comparing(HlsCusAbsProductCollection::getTimes));
        String status = "";
        for (int i = 0; i < list.size(); i++) {
            if (Long.compare(list.get(i).getTimes(), hlsCusAbsProductCollection.getTimes()) == 0 && Long.compare(list.get(i).getTimes(), 1L) != 0) {
                status = list.get(i - 1).getRemittanceStatus();
                break;
            }
        }
        if (StringUtils.equalsIgnoreCase("NEW", status)) {
            throw new IllegalArgumentException("请先转付上一期金额!");
        }
        if (hlsCusAbsProductCollection.getOldCollectionId() == null) {
            if (StringUtils.isEmpty(hlsCusAbsProductCollection.getRemittanceNumber())) {  // 新建转付,第一次选择期数保存
                hlsCusAbsProductCollection.setRemittanceNumber(createNumber("remittanceNumber", hlsCusAbsProduct.getProjectId()));
            } else {  // 点击明细进入页面但是没有更改期数

            }
        } else {
            if (Long.compare(hlsCusAbsProductCollection.getOldCollectionId(), hlsCusAbsProductCollection.getCollectionId()) != 0) {
                hlsCusAbsProductCollectionMapper.resetRemittance(hlsCusAbsProductCollection.getOldCollectionId());
                hlsCusAbsProductCollection.setRemittanceNumber(createNumber("remittanceNumber", hlsCusAbsProduct.getProjectId()));
            } else {
                if (StringUtils.isEmpty(hlsCusAbsProductCollection.getRemittanceNumber())) {  // 选择期数后未保存再次选择期数
                    hlsCusAbsProductCollection.setRemittanceNumber(createNumber("remittanceNumber", hlsCusAbsProduct.getProjectId()));
                }
            }
        }
        hlsCusAbsProductCollection.setRemittanceApplyDate(new Date());
        hlsCusAbsProductCollection = self().updateByPrimaryKeySelective(request, hlsCusAbsProductCollection);
        return hlsCusAbsProductCollection;
    }

    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private HlsEmployeeMapper employeeMapper;
    @Autowired
    private IActivitiStartService activitiStartService;
    @Autowired
    private LoginUserInfoService loginUserInfoService;
    @Autowired
    private SysEventService sysEventService;

    @Override
    public HlsCusAbsProductCollection remittanceSubmit(IRequest request, HlsCusAbsProductCollection hlsCusAbsProductCollection) {
        hlsCusAbsProductCollection.setRemittanceStatus("APPROVING");
        hlsCusAbsProductCollection = remittanceSave(request, hlsCusAbsProductCollection);
        HlsCusAbsProductCollection collection = new HlsCusAbsProductCollection();
        collection.setProductId(hlsCusAbsProductCollection.getProductId());
        List<HlsCusAbsProductCollection> list = hlsCusAbsProductCollectionMapper.select(collection);
        boolean flag = false;
        if (CollectionUtils.isNotEmpty(list)) {
            for (HlsCusAbsProductCollection cusAbsProductCollection : list) {
                if (StringUtils.equals("NEW", cusAbsProductCollection.getRemittanceStatus()) || StringUtils.equals("APPROVED_RETURN", cusAbsProductCollection.getRemittanceStatus())) {
                    flag = true;
                }
            }
        }
        if (!flag) {
            HlsCusAbsProduct hlsCusAbsProduct = new HlsCusAbsProduct();
            hlsCusAbsProduct.setProductId(hlsCusAbsProductCollection.getProductId());
            hlsCusAbsProduct.setRemittanceStatus("APPROVING");
            hlsCusAbsProductService.updateByPrimaryKeySelective(request, hlsCusAbsProduct);
        }

        List<HlsCusAbsProductCollection> collectionList = new ArrayList<>();
        collectionList.add(hlsCusAbsProductCollection);
        databaseLockProvider.lock(hlsCusAbsProductCollection);
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(request.getUserId());
        String employeeCode = employee.getEmployeeCode();
        request.setEmployeeCode(employeeCode);
        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("workFlowType", "ABS_PRODUCT_REMITTANCE");
        activitiStartService.start(request, collectionList, params);

        /*发消息*/
        Map<String, Object> paramsEvent = new HashMap<String, Object>();
        String userName = "";
        if (loginUserInfoService.queryUserInfo(request.getEmployeeCode()).size() > 0) {
            userName = (loginUserInfoService.queryUserInfo(request.getEmployeeCode())).get(0).getUserName();
        }
        String msg = userName + "提交了" + hlsCusAbsProductCollection.getRemittanceNumber() + "ABS转付审核";
        paramsEvent.put("message", msg);
        paramsEvent.put("noticeTitle", "ANS转付审批");
        paramsEvent.put("noticeType", "NOTICE");
        paramsEvent.put("url", "/abs/ABS112A/abs_product_remittance.view?productId=" + hlsCusAbsProductCollection.getProductId() + "&collectionId=" + hlsCusAbsProductCollection.getCollectionId());
        paramsEvent.put("level", 1L);
        sysEventService.eventSave(request, hlsCusAbsProductCollection.getCollectionId(), "ABS_PRODUCT_REMITTANCE", "ABS_PRODUCT_REMITTANCE", "ABS", "ABS_PRODUCT_REMITTANCE", "P2D", paramsEvent);

        return hlsCusAbsProductCollection;
    }

    @Autowired
    private JeTrxCommonService jeTrxCommonService;

    @Override
    public HlsCusAbsProductCollection colConfirm(IRequest request, HlsCusAbsProductCollection hlsCusAbsProductCollection) {
        hlsCusAbsProductCollection.setCollectionStatus("CONFIRM");
        hlsCusAbsProductCollection = save(request, hlsCusAbsProductCollection);
        HlsCusAbsProductCollection collection = new HlsCusAbsProductCollection();
        collection.setProductId(hlsCusAbsProductCollection.getProductId());
        List<HlsCusAbsProductCollection> list = hlsCusAbsProductCollectionMapper.select(collection);
        boolean flag = false;
        if (CollectionUtils.isNotEmpty(list)) {
            for (HlsCusAbsProductCollection cusAbsProductCollection : list) {
                if (StringUtils.equals("NEW", cusAbsProductCollection.getCollectionStatus())) {
                    flag = true;
                }
            }
        }

        HlsCusAbsProduct hlsCusAbsProduct = new HlsCusAbsProduct();
        hlsCusAbsProduct.setProductId(hlsCusAbsProductCollection.getProductId());
        hlsCusAbsProduct = hlsCusAbsProductService.selectByPrimaryKey(request, hlsCusAbsProduct);
        if (!flag) {

            hlsCusAbsProduct.setCollectionStatus("CONFIRM");
            hlsCusAbsProductService.updateByPrimaryKeySelective(request, hlsCusAbsProduct);
        }

        //插入凭证事物流水表
        AbstractJeTrxService abstractJeTrxService = jeTrxCommonService.map.get("ABS_PRODUCT_COLLECTION");
        Map params = new HashMap<>();
        params.put("jeTrxId", hlsCusAbsProductCollection.getCollectionId());
        params.put("companyId", hlsCusAbsProduct.getCompanyId());
        params.put("jeSourceId", hlsCusAbsProduct.getProductId());
        params.put("jeSourceDoc", "ABS_PRODUCT_COLLECTION");
        abstractJeTrxService.process(request, params);
        return hlsCusAbsProductCollection;
    }

    private String createNumber(String msg, Long projectId) {
        List<HlsCusAbsProductCollection> list = hlsCusAbsProductCollectionMapper.queryAllNumberFromProject(msg, projectId);
        if (CollectionUtils.isEmpty(list)) {
            switch (msg) {
                case "collectionNumber":
                    return "COL" + LocalDate.now().toString().replace("-", "") + "001";
                case "remittanceNumber":
                    return "TRA" + LocalDate.now().toString().replace("-", "") + "001";
                default:
                    break;
            }

        }
        String[] numbers = new String[list.size()];
        switch (msg) {
            case "collectionNumber":
                for (int i = 0; i < list.size(); i++) {
                    numbers[i] = list.get(i).getCollectionNumber();
                }
                break;
            case "remittanceNumber":
                for (int i = 0; i < list.size(); i++) {
                    numbers[i] = list.get(i).getRemittanceNumber();
                }
                break;
            default:
                break;
        }
        numbers = Arrays.stream(numbers).sorted(String::compareTo).toArray(String[]::new);
        String number = numbers[numbers.length - 1];
        String first = number.substring(0, number.length() - 3);
        Integer seq = Integer.valueOf(number.substring(number.length() - 3));
        if (seq == 999) {
            seq = 0;
        }
        if (++seq < 10) {
            return first + "00" + seq;
        } else if (seq < 100) {
            return first + "0" + seq;
        } else {
            return first + seq;
        }
    }

    @Override
    public int deleteProductNotCollection(Long productId) {
        return hlsCusAbsProductCollectionMapper.deleteProductNotCollection(productId);
    }

    @Override
    public Date selectMaxRentalBackDate(Long productId) {
        return hlsCusAbsProductCollectionMapper.selectMaxRentalBackDate(productId);
    }

    @Override
    public Long selectMaxCollectionTimes(Long productId) {
        return hlsCusAbsProductCollectionMapper.selectMaxCollectionTimes(productId);
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
            List<Object> objects = HlsCusImportDataUtil.dataToDto(HlsCusAbsProductCollection.class, dataMap, formats);
            Long productId = Long.parseLong(dataMap.get(0).get("tempKey"));
            for (int i = 0; i < objects.size(); i++) {
                HlsCusAbsProductCollection productCollection = (HlsCusAbsProductCollection) objects.get(i);
                productCollection.setProductId(productId);
                productCollection.setLastUpdatedBy(iRequest.getUserId());
                productCollection.setCreatedBy(iRequest.getUserId());
                hlsCusAbsProductCollectionMapper.insertSelective(productCollection);
            }
        } catch (Exception e) {
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
    public BigDecimal selectCashFeeSum(Long collectionId) {
        return hlsCusAbsProductCollectionMapper.selectCashFeeSum(collectionId);
    }

    @Override
    public HlsCusAbsProductCollection saveCashDeatil(IRequest request, HlsCusAbsProductCollection hlsCusAbsProductCollection) {


        if(CollectionUtils.isNotEmpty(hlsCusAbsProductCollection.getHlsCusAbsProductCashDetails())){
            for (HlsCusAbsProductCashDetail productCashDetail : hlsCusAbsProductCollection.getHlsCusAbsProductCashDetails()) {
                productCashDetail.set__status(DTOStatus.UPDATE);
            }
            productCashDetailService.batchUpdate(request, hlsCusAbsProductCollection.getHlsCusAbsProductCashDetails());
        }

        if(CollectionUtils.isNotEmpty(hlsCusAbsProductCollection.getHlsCusAbsProductReleases())){
            for (HlsCusAbsProductRelease productRelease : hlsCusAbsProductCollection.getHlsCusAbsProductReleases()) {
                productRelease.setSourceDocId(hlsCusAbsProductCollection.getCollectionId());
                productRelease.setProductId(hlsCusAbsProductCollection.getProductId());
                productRelease.setSourceDocCategory(HlsCusConstant.ABS_RELEASE_TYPE.COLLECTION);
                if(productRelease.getReleaseId()==null) {
                    productRelease.set__status(DTOStatus.ADD);
                }else {
                    productRelease.set__status(DTOStatus.UPDATE);
                }
            }
            productReleaseService.batchUpdate(request, hlsCusAbsProductCollection.getHlsCusAbsProductReleases());
        }

        if (CollectionUtils.isNotEmpty(hlsCusAbsProductCollection.getHlsCusPrjProjectAttachments())) {
            for (HlsCusPrjProjectAttachment attachment: hlsCusAbsProductCollection.getHlsCusPrjProjectAttachments()){
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

        return hlsCusAbsProductCollection;
    }

    @Override
    public HlsCusAbsProductCollection confirmCashDeatil(IRequest request, HlsCusAbsProductCollection hlsCusAbsProductCollection) {
        //保存
        self().saveCashDeatil(request,hlsCusAbsProductCollection);

        hlsCusAbsProductCollection.setCashStatus(HlsCusConstant.WORKFLOW_STATUS.CONFIRM);
        hlsCusAbsProductCollectionMapper.updateByPrimaryKeySelective(hlsCusAbsProductCollection);
        return hlsCusAbsProductCollection;
    }

    @Override
    public HlsCusAbsProductCollection calculateCashDetail(IRequest request, HlsCusAbsProductCollection hlsCusAbsProductCollection) {


        for (HlsCusAbsProductCashDetail productCashDetail : hlsCusAbsProductCollection.getHlsCusAbsProductCashDetails()) {
            productCashDetail.set__status(DTOStatus.UPDATE);
            productCashDetail.setPlanCashPrincipal(productCashDetail.getCashPrincipal());
        }
        productCashDetailService.batchUpdate(request, hlsCusAbsProductCollection.getHlsCusAbsProductCashDetails());


        HlsCusAbsProduct hlsCusAbsProduct=new HlsCusAbsProduct();
        hlsCusAbsProduct.setProductId(hlsCusAbsProductCollection.getProductId());
        hlsCusAbsProduct=hlsCusAbsProductService.selectByPrimaryKey(request,hlsCusAbsProduct);
        hlsCusAbsProductService.reCalcCashProduct(request,hlsCusAbsProduct,hlsCusAbsProductCollection.getDataClass());
        return hlsCusAbsProductCollection;
    }


    @Override
    public void confirmProductCollection(IRequest request, HlsCusFundTransferList fundTransferList) throws HlsCusException {
        HlsCusAbsProductCollection productCollection = hlsCusAbsProductCollectionMapper.selectByPrimaryKey(fundTransferList.getSourceDocLineId());
        if(HlsCusConstant.WORKFLOW_STATUS.BLOCK.equals(productCollection.getCollectionStatus())){
            throw new HlsCusException("由于清仓回购，归集现金流已经被冻结！请检查");
        }
        productCollection.setCollectionAccountId(fundTransferList.getInBankAccountId());
        productCollection.setCollectionBankAccountName(fundTransferList.getInBankAccountName());
        productCollection.setCollectionBankAccountNum(fundTransferList.getInBankAccountNum());
        productCollection.setCollectionBankBranchName(fundTransferList.getInBankBranchName());
        productCollection.setCollectionOrganizationId(fundTransferList.getBpId());
        productCollection.setCollectionOrganizationName(fundTransferList.getOrganizationName());
        if(productCollection.getCollectionReceiverAmount()==null){
            productCollection.setCollectionReceiverAmount(BigDecimal.ZERO);
        }
        productCollection.setCollectionReceiverAmount(productCollection.getCollectionReceiverAmount().add(fundTransferList.getActualPayAmount()));
        if(productCollection.getCollectionReceiverAmount().compareTo(productCollection.getCollectionAmount())==0){
            productCollection.setCollectionStatus(HlsCusConstant.WORKFLOW_STATUS.CONFIRM);
        }else if(productCollection.getCollectionReceiverAmount().compareTo(productCollection.getCollectionAmount())<0){
            productCollection.setCollectionStatus(HlsCusConstant.WORKFLOW_STATUS.PART_CONFIRM);
        }else {
            throw new HlsCusException("已归集大于应归集金额，请检查！");
        }
        productCollection.setConfirmFlag(HlsCusConstant.FLAG.Y);
        hlsCusAbsProductCollectionMapper.updateByPrimaryKeySelective(productCollection);

        hlsCusAbsProductCollectionMapper.updateConfirmTempCollection(productCollection.getProductId(),productCollection.getTimes());
    }

    @Override
    public void confirmProductRemittance(IRequest request, HlsCusFundTransferList fundTransferList) throws HlsCusException {
        HlsCusAbsProductCollection productCollection = hlsCusAbsProductCollectionMapper.selectByPrimaryKey(fundTransferList.getSourceDocLineId());
        if(HlsCusConstant.WORKFLOW_STATUS.BLOCK.equals(productCollection.getRemittanceStatus())){
            throw new HlsCusException("由于清仓回购，兑付现金流已经被冻结！请检查");
        }

        productCollection.setRemittanceAccountId(fundTransferList.getInBankAccountId());
        productCollection.setRemittanceBankAccountName(fundTransferList.getInBankAccountName());
        productCollection.setRemittanceBankAccountNum(fundTransferList.getInBankAccountNum());
        productCollection.setRemittanceBankBranchName(fundTransferList.getInBankBranchName());
        productCollection.setRemittanceOrganizationName(fundTransferList.getOrganizationName());
        if(productCollection.getRemittanceReceiverAmount()==null){
            productCollection.setRemittanceReceiverAmount(BigDecimal.ZERO);
        }
        productCollection.setRemittanceReceiverAmount(productCollection.getRemittanceReceiverAmount().add(fundTransferList.getActualPayAmount()));
        if(productCollection.getRemittanceReceiverAmount().compareTo(productCollection.getRemittanceAmount())==0){
            productCollection.setRemittanceStatus(HlsCusConstant.WORKFLOW_STATUS.CONFIRM);
        }else if(productCollection.getRemittanceReceiverAmount().compareTo(productCollection.getRemittanceAmount())<0){
            productCollection.setRemittanceStatus(HlsCusConstant.WORKFLOW_STATUS.PART_CONFIRM);
        }else {
            throw new HlsCusException("已归集大于应归集金额，请检查！");
        }
        productCollection.setConfirmFlag(HlsCusConstant.FLAG.Y);
        hlsCusAbsProductCollectionMapper.updateByPrimaryKeySelective(productCollection);

        hlsCusAbsProductCollectionMapper.updateConfirmTempCollection(productCollection.getProductId(),productCollection.getTimes());
    }

    @Override
    public HlsCusAbsProductCollection selectLastConfirmCollection(Long productId) {
        return hlsCusAbsProductCollectionMapper.selectLastConfirmCollection(productId);
    }

    @Override
    public int selectNotConfirmCollectionCount(Long productId) {
        return hlsCusAbsProductCollectionMapper.selectNotConfirmCollectionCount(productId);
    }

    /**
     * 自定义导出
     * @param exportConfig 前端导出的字段和名称
     * @param request
     * @param httpServletResponse
     * @param requestContext
     */
    @Override
    public void exportAndDownloadExcel(ExportConfig<HlsCusAbsProductCollection, ColumnInfo> exportConfig, HttpServletRequest request, HttpServletResponse httpServletResponse, IRequest requestContext)
            throws InvocationTargetException, IllegalAccessException, IOException, NoSuchMethodException {
        Asserts.notNull(exportConfig,"参数不能为空");
        DecimalFormat df =new DecimalFormat("###,##0.00");
        XSSFWorkbook xwork = new XSSFWorkbook();
        XSSFSheet sheet = xwork.createSheet("sheet1");
//        Map<String,String> map = new HashMap<>();
        List<ColumnInfo> columnsInfo = exportConfig.getColumnsInfo();
        List<String> titles=new LinkedList<>();
        List<String> names=new LinkedList<>();
        if (CollectionUtils.isNotEmpty(columnsInfo)){
            for (ColumnInfo columnInfo : columnsInfo) {
                names.add(columnInfo.getName());
                titles.add(columnInfo.getTitle());
//                map.put(columnInfo.getName(),columnInfo.getTitle());
            }
            HlsCusAbsProductCollection param=exportConfig.getParam();
            Asserts.notNull(param,"传参错误");
            Asserts.notNull(param.getProductId(),"传参错误");
            Asserts.notNull(param.getDataClass(),"传参错误");
            //查询数据
            List<HlsCusAbsProductCollection> list= hlsCusAbsProductCollectionMapper.selectProductCollectionData(param);
            List<Map> datas=new LinkedList<>();
            List<String> attributes1=new LinkedList<>();
            List<String> attributes2=new LinkedList<>();
            boolean flag=false;
            for (HlsCusAbsProductCollection collect : list) {
                HlsCusAbsProductCashDetail detail = new HlsCusAbsProductCashDetail();
                detail.setCollectionId(collect.getCollectionId());
                List<HlsCusAbsProductCashDetail> details = hlsCusAbsProductCashDetailMapper.selectProductCashDetailData(detail);
                Map<String,String> beanMap= BeanRefUtil.getFieldValueMap(collect);
                if (!details.isEmpty()) {
                    //是否需要修改模板
                    if (!flag) {
                        for (HlsCusAbsProductCashDetail hdetail : details) {
                            //本金
                            String priciAttribute=UUID.randomUUID().toString();
                            //利息
                            String intersetAttribute=UUID.randomUUID().toString();
                            names.add(priciAttribute);
                            titles.add(hdetail.getProjectStructure() + "兑付本金(元)");
                            names.add(intersetAttribute);
                            titles.add(hdetail.getProjectStructure() + "兑付利息(元)");
//                            map.put(priciAttribute,hdetail.getProjectStructure() + "兑付本金(元)");
//                            map.put(intersetAttribute,hdetail.getProjectStructure() + "兑付利息(元)");
                            attributes1.add(priciAttribute);
                            attributes2.add(intersetAttribute);
                        }
                        flag = true;
                    }
                    //获取数据
                    int index=0;
                    for (HlsCusAbsProductCashDetail hdetail : details) {
                        if (null!=hdetail.getCashPrincipal()){
                            //兑付本金
                            beanMap.put(attributes1.get(index),df.format(hdetail.getCashPrincipal()));
                        }
                        if (null!=hdetail.getCashInterest())
                        {
                            //兑付利息
                            beanMap.put(attributes2.get(index),df.format(hdetail.getCashInterest()));
                        }
                        ++index;
                    }
                    datas.add(beanMap);
                }
            }
//            拼接数据和列名
            int dataRowNum = ExportExcelUtil.createCommonExcelHead(xwork, sheet, null,titles);
            for (Map data : datas) {
                //创建列
                XSSFRow row = sheet.createRow(dataRowNum++);
                ExportExcelUtil.setData(xwork,sheet,row,data,names);
            }
            ExportExcelUtil.IOWrite(xwork, null,request,httpServletResponse, "兑付情况");
        }
    }

    @Override
    public void deleteByCollection(HlsCusAbsProductCollection hlsCusAbsProductCollection) {
        hlsCusAbsProductCollectionMapper.deleteByCollection(hlsCusAbsProductCollection);
    }
}

