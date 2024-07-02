package com.hand.hls.abs.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.abs.dto.*;
import com.hand.hls.abs.mapper.*;
import com.hand.hls.abs.service.*;
import com.hand.hls.abs.util.AbsDateUtils;
import com.hand.hls.abs.util.HlsCusAbsConstant;
import com.hand.hls.abs.utils.HlsCusExcelUtils;
import com.hand.hls.bp.components.CalculateUtil;
import com.hand.hls.bp.dto.HlsCusSysFile;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.bp.service.HlsSysFileService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fin.service.HlsCusLonContractWithdrawService;
import com.hand.hls.fnd.dto.FndInterfaceLines;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.FndInterfaceLinesMapper;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.gld.dto.HlsCusGldLonContractFinCost;
import com.hand.hls.gld.service.GldLonContractFinCostService;
import com.hand.hls.gld.utils.IrrUtil;
import com.hand.hls.prj.dto.HlsCusPrjProjectAttachment;
import com.hand.hls.prj.service.HlsCusPrjProjectAttachmentService;
import com.hand.hls.req.dto.HlsCusChangeReqInfo;
import com.hand.hls.req.service.HlsCusChangeReqInfoService;
import com.hand.hls.sys.dto.HlsSystemNotice;
import com.hand.hls.utils.DateUtil.HlsCusEndOfMonth;
import com.hand.hls.utils.HlsCusConstant;
import com.hand.hls.utils.HlsCusXirr;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.sys.event.service.SysEventService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import static com.hand.hls.utils.DateUtils.format;


@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusAbsProductServiceImpl extends BaseServiceImpl<HlsCusAbsProduct> implements HlsCusAbsProductService {
    private static Logger logger = LoggerFactory.getLogger(HlsCusAbsProductServiceImpl.class);

    @Autowired
    private AbsProductService absProductService;

    /**
     * 兑付周期
     */
    private static final String YEAR = "YEAR";
    private static final String HALF_A_YEAR = "HALF_A_YEAR";
    private static final String QUARTER = "QUARTER";
    private static final String MONTH = "MONTH";
    private static final String IRREGULAR_FLAG_N = "N";
    private static final String SUBSCRIBE_FLAG_N = "N";
    private static final String BUYBACKSTATUS_NEW = "N";
    private static final String NOTICE_TITLE_ABS_PRODUCT_SUBMIT = "abs发债产品提交审批";
    private static final String NOTICE_TITLE_ABS_BUYBACK_SUBMIT = "abs发债产品提交审批";
    private static final String WORKFLOW_ABS_PRODUCT_NAME = "ABS_PRODUCT_ESTABLISHMENT_A_WFL";
    private static final String WORKFLOW_ABS_BUYBACK_NAME = "ABS_REPURCHASE_WFL";
    private static final String WORKFLOW_ABS_DEMO_NAME = "ABS";
    private static final String PROPERTY_ABS_PRODUCT_BONDING_SUBMIT = "ABS_PRODUCT_BONDING.SUBMIT";
    private static final String PROPERTY_ABS_PRODUCT_BUYBACK_SUBMIT = "ABS_PRODUCT_BUYBACK.SUBMIT";
    /**
     * 分摊类型,税率,初始化数据
     */
    private static final String ABS_INTEREST_INCOMES_SOURCE_TYPE = "ABS_INTEREST";
    private static final String ABS_OTHER_INCOMES_SOURCE_TYPE = "ABS_OTHER_FEE";
    private static final Double ABS_INCOMES_RATE = 1.06D;
    private static final Long ABS_INCOMES_LONG_INIT = 0L;
    private static final Double ABS_INCOMES_DOUBLE_INIT = 0D;
    private static final Double YEAR_MONTH = 12D;
    /**
     * 分摊方式：实率法分摊,一次性分摊
     */
    private static final String ABS_OTHER_INCOMES_REAL_RATE = "REAL_RATE_METHOD";
    private static final String ABS_OTHER_INCOMES_ONE_TIME = "ONE_TIME_CONFIRMATION";



    /**
     * abs 类型
     */
    private static final String ABS_TYPE = "ABS_PRODUCT";
    private Calendar calendar = Calendar.getInstance();//获取一个日历的实例
    @Autowired
    private HlsCusAbsProductMapper hlsCusAbsProductMapper;
    @Autowired
    private HlsCusAbsProjectMapper hlsCusAbsProjectMapper;

    @Autowired
    private HlsCusAbsProjectService hlsCusAbsProjectService;
    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;
    @Autowired
    private SysEventService sysEventService;
    @Autowired
    private HlsCusAbsProjectOrganizationService hlsCusAbsProjectOrganizationService;
    @Autowired
    private HlsCusAbsProductOrganizationService hlsCusAbsProductOrganizationService;
    @Autowired
    private HlsCusAbsProductCollectionService hlsCusAbsProductCollectionService;
    @Autowired
    private FndInterfaceLinesMapper linesMapper;
    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;
    @Autowired
    private HlsCusChangeReqInfoService hlsCusChangeReqInfoService;

    @Autowired
    private HlsCusAbsBankAccountService hlsCusAbsBankAccountService;

    @Autowired
    private HlsCusAbsProFeeInfoService hlsCusAbsProFeeInfoService;

    @Autowired
    private HlsCusAbsProductPurposeService hlsCusAbsProductPurposeService;

    @Autowired
    private HlsCusAbsProductStructureService hlsCusAbsProductStructureService;

    @Autowired
    private HlsCusPrjProjectAttachmentService hlsCusPrjProjectAttachmentService;

    @Autowired
    private HlsSysFileService hlsSysFileService;

    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Autowired
    private HlsEmployeeMapper employeeMapper;

    @Autowired
    private IActivitiStartService activitiStartService;

    @Autowired
    private HlsCusAbsProductQuotationService productQuotationService;

    @Autowired
    private HlsCusAbsProductRepaymentMapper productRepaymentMapper;

    @Autowired
    private HlsCusAbsProductRepaymentService productRepaymentService;

//    @Autowired
//    private HlsCusSysAttachmentMapper hlsCusSysAttachmentMapper;

    @Autowired
    private HlsCusAbsAssetsPackService hlsCusAbsAssetsPackService;


    @Autowired
    private HlsCusAbsAssetsPackageService hlsCusAbsAssetsPackageService;

    @Autowired
    private HlsCusAbsAssetsPackageMapper absAssetsPackageMapper;

    @Autowired
    private HlsCusAbsContractFinishMapper absContractFinishMapper;

    @Autowired
    private HlsCusAbsProductReceiptService hlsCusAbsProductReceiptService;

    @Autowired
    private HlsCusAbsProductCashDetailService hlsCusAbsProductCashDetailService;

    @Autowired
    private HlsCusAbsProductBuybackService hlsCusAbsProductBuybackService;
    @Autowired
    private GldLonContractFinCostService gldLonContractFinCostService;
    @Autowired
    private HlsCusLonContractWithdrawService hlsCusLonContractWithdrawService;
    @Autowired
    private HlsCusWithdrawRepaymentMapper hlsCusWithdrawRepaymentMapper;
    @Autowired
    private HlsCusAbsProductCollectionMapper hlsCusAbsProductCollectionMapper;
    @Autowired
    private HlsCusAbsProductCashDetailMapper hlsCusAbsProductCashDetailMapper;

    @Autowired
    private HlsCusAbsProductSubscribeService hlsCusAbsProductSubscribeService;



    @Override
    public List<HlsCusAbsProduct> createProduct(Long projectId, String productShortName, String productName,
                                                String productNumber, IRequest request) {
        HlsCusAbsProduct hlsCusAbsProduct = new HlsCusAbsProduct();
        if (projectId != null) {
            HlsCusAbsProject hlsCusAbsProject = hlsCusAbsProjectMapper.selectByPrimaryKey(projectId);
            hlsCusAbsProduct.setBusinessType(hlsCusAbsProject.getBusinessType());
            hlsCusAbsProduct.setDocumentCategory(ABS_TYPE);
            hlsCusAbsProduct.setDocumentType(ABS_TYPE);
            hlsCusAbsProduct.setProductDate(hlsCusAbsProject.getProjectDate());
            hlsCusAbsProduct.setProductName(productName);
            hlsCusAbsProduct.setProductShortName(productShortName);
            hlsCusAbsProduct.setProductDate(hlsCusAbsProject.getProjectDate());
            hlsCusAbsProduct.setPayInterestFrequency(hlsCusAbsProject.getPayInterestFrequency());
            hlsCusAbsProduct.setBaseDate(hlsCusAbsProject.getBaseDate());
            hlsCusAbsProduct.setProductNumber(productNumber);
            hlsCusAbsProduct.setProductCurrencyCode(hlsCusAbsProject.getCurrencyCode());
            hlsCusAbsProduct.setPackId(hlsCusAbsProject.getPackId());
            hlsCusAbsProduct.setCompanyId(request.getCompanyId());
            hlsCusAbsProduct.setProjectId(projectId);
            hlsCusAbsProduct.setUnitId(Long.valueOf(request.getAttribute("unitId")));
            hlsCusAbsProduct.setUserId(request.getUserId());
            hlsCusAbsProduct.setIsIrregular(IRREGULAR_FLAG_N);
            hlsCusAbsProduct.setBuybackStatus(BUYBACKSTATUS_NEW);
            hlsCusAbsProduct.setDataClass(HlsCusConstant.DATA_CLASS.NORMAL);
            hlsCusAbsProduct.setProductStatus(HlsCusConstant.WORKFLOW_STATUS.NEW);
            if (!HlsCusAbsConstant.ABS_BUSINESS_TYPE.ABS.equals(hlsCusAbsProduct.getBusinessType())
                    && !HlsCusAbsConstant.ABS_BUSINESS_TYPE.ABN.equals(hlsCusAbsProduct.getBusinessType())) {
                hlsCusAbsProduct.setSubscribeFlag(SUBSCRIBE_FLAG_N);
            }
            hlsCusAbsProductMapper.insertSelective(hlsCusAbsProduct);
            // 复制行表数据
            insertLineData(request, hlsCusAbsProduct);
        }
        Example procuctExample = new Example(HlsCusAbsProduct.class);
        procuctExample.createCriteria().andEqualTo(AbsProduct.FIELD_PRODUCT_NUMBER, productNumber);
        List<HlsCusAbsProduct> hlsCusAbsProductList = hlsCusAbsProductMapper.selectByExample(procuctExample);
        return hlsCusAbsProductList;
    }


    @Override
    public List<HlsCusAbsProduct> queryChangeHome(IRequest request, HlsCusAbsProduct hlsCusAbsProduct, int page, int pageSize) {
        if (StringUtils.isNotEmpty(hlsCusAbsProduct.getDateStr())) {
            LocalDate localDate = LocalDate.now();
            switch (hlsCusAbsProduct.getDateStr()) {
                case "week":
                    localDate = localDate.plusWeeks(-1L);
                    break;
                case "month":
                    localDate = localDate.plusMonths(-1L);
                    break;
                case "three":
                    localDate = localDate.plusMonths(-3L);
                    break;
                default:
                    logger.error("日期参数不正确!");
                    break;
            }
            hlsCusAbsProduct.setDateStr(localDate.toString());
        }
        PageHelper.startPage(page, pageSize);
        return hlsCusAbsProductMapper.queryChangeHome(hlsCusAbsProduct);
    }

    @Override
    public Map<String, Object> queryProductChangeChart() {
        return hlsCusAbsProductMapper.queryProductChangeChart();
    }

   /* @Override
    public HlsCusAbsProduct createChangeProduct(IRequest request, HlsCusAbsProduct hlsCusAbsProduct) {
        if (null == hlsCusAbsProduct || hlsCusAbsProduct.getProductId() == null) {
            logger.error("没有变更信息,请查看页面提交方式");
            return null;
        }
        HlsCusAbsProduct normalProduct = new HlsCusAbsProduct();
        normalProduct.setProductId(hlsCusAbsProduct.getProductId());
        normalProduct = self().selectByPrimaryKey(request, normalProduct);

        HlsCusChangeReqInfo hlsCusChangeReqInfo = new HlsCusChangeReqInfo();
        hlsCusChangeReqInfo.setDocumentVersionId(1L);
        hlsCusChangeReqInfo.setStatus(HlsCusConstant.WORKFLOW_STATUS.NEW);
        hlsCusChangeReqInfo.setDocumentId(normalProduct.getProductId());
        hlsCusChangeReqInfo.setDocumentCategory(normalProduct.getDocumentCategory());
        hlsCusChangeReqInfo.setDocumentType(normalProduct.getDocumentType());
        hlsCusChangeReqInfo.setChangeReqDate(hlsCusAbsProduct.getChangeReqDate());
        hlsCusChangeReqInfo.setChangeReqUserId(request.getUserId());
        hlsCusChangeReqInfo.setChangeDescription(hlsCusAbsProduct.getChangeDescription());
        hlsCusChangeReqInfo.setBusinessType(hlsCusAbsProduct.getBusinessType());
        hlsCusChangeReqInfo = hlsCusChangeReqInfoService.insertSelective(request, hlsCusChangeReqInfo);

        HlsCusAbsProduct changeProduct = new HlsCusAbsProduct();
        Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(normalProduct);
        hlsBeanRefUtilService.setFieldValue(changeProduct, map);
        changeProduct.setProductId(null);
        changeProduct.setAssetChangeType(hlsCusAbsProduct.getAssetChangeType());
        changeProduct.setIssueAmount(normalProduct.getIssueAmount());
        changeProduct.setChangeAmount(normalProduct.getChangeAmount());
        changeProduct.setMakeupAmount(normalProduct.getMakeupAmount());
        changeProduct.setRefProductId(normalProduct.getProductId());
        changeProduct.setDataClass(HlsCusConstant.DATA_CLASS.CHANGE_REQ);
        changeProduct.setChangeReqId(hlsCusChangeReqInfo.getChangeReqId());
        changeProduct.setCreationDate(null);
        changeProduct.setLastUpdateDate(null);
        changeProduct.setCreatedBy(request.getUserId());
        changeProduct.setLastUpdatedBy(request.getUserId());
        changeProduct = self().insertSelective(request, changeProduct);
        normalProduct.setProductStatus(HlsCusConstant.WORKFLOW_STATUS.PENDING);
        self().updateByPrimaryKeySelective(request, normalProduct);

        //变更
        cloneLineData(request,hlsCusChangeReqInfo.getBusinessType(), normalProduct, changeProduct.getProductId());
        if(normalProduct.getPackId()!=null&&HlsCusConstant.ABS_CHANGE_TYPE.CONTRACT.equals(hlsCusAbsProduct.getAssetChangeType())) {
            //资产变更
            HlsCusAbsAssetsPack normalPack = new HlsCusAbsAssetsPack();
            normalPack.setPackId(normalProduct.getPackId());
            normalPack = hlsCusAbsAssetsPackService.selectByPrimaryKey(request, normalPack);

            HlsCusAbsAssetsPack changePack = new HlsCusAbsAssetsPack();
            Map<String, String> mapPack = hlsBeanRefUtilService.getFieldValueMap(normalPack);
            hlsBeanRefUtilService.setFieldValue(changePack, mapPack);
            changePack.setPackId(null);
            changePack.setDataClass(HlsCusConstant.DATA_CLASS.CHANGE_REQ);
            changePack.setChangeReqId(hlsCusChangeReqInfo.getChangeReqId());
            changePack.setRefPackId(normalPack.getPackId());
            changePack.setCreationDate(null);
            changePack.setLastUpdateDate(null);
            changePack.setCreatedBy(request.getUserId());
            changePack.setLastUpdatedBy(request.getUserId());
            hlsCusAbsAssetsPackService.insertSelective(request, changePack);

            clonePackLineData(request, normalPack.getPackId(), changePack.getPackId());

            changeProduct.setPackId(changePack.getPackId());
            self().updateByPrimaryKeySelective(request, changeProduct);
        }
        changeProduct.setBusinessType(hlsCusChangeReqInfo.getBusinessType());
        return changeProduct;
    }
*/

    private void clonePackLineData(IRequest iRequest, Long sourcePackId, Long targetPackId) {
        HlsCusAbsAssetsPackage absAssetsPackage = new HlsCusAbsAssetsPackage();
        absAssetsPackage.setPackId(sourcePackId);
        List<HlsCusAbsAssetsPackage> absAssetsPackages = hlsCusAbsAssetsPackageService.select(iRequest, absAssetsPackage, 1, 999);
        if (CollectionUtils.isNotEmpty(absAssetsPackages)) {
            for (HlsCusAbsAssetsPackage assetsPackage : absAssetsPackages) {
                assetsPackage.setPackageId(null);
                assetsPackage.setPackId(targetPackId);
                assetsPackage.set__status(DTOStatus.ADD);
                assetsPackage.setCreationDate(null);
                assetsPackage.setLastUpdateDate(null);
                assetsPackage.setCreatedBy(iRequest.getUserId());
                assetsPackage.setLastUpdatedBy(iRequest.getUserId());
            }
            hlsCusAbsAssetsPackageService.batchUpdate(iRequest, absAssetsPackages);
        }

    }

    @Override
    public HlsCusAbsProduct queryChangeDetail(IRequest request, HlsCusAbsProduct hlsCusAbsProduct) {
        return hlsCusAbsProductMapper.queryChangeDetail(hlsCusAbsProduct);
    }

    @Override
    public HlsCusAbsProduct saveProductChange(IRequest request, HlsCusAbsProduct hlsCusAbsProduct) {
        return self().updateByPrimaryKeySelective(request, hlsCusAbsProduct);
    }

    @Autowired
    private HlsCusAbsContractFinishService hlsCusAbsContractFinishService;

    @Override
    public HlsCusAbsPkg saveAssetChange(IRequest request, HlsCusAbsPkg hlsCusAbsPkg) {
        HlsCusChangeReqInfo changeReqInfo=new HlsCusChangeReqInfo();
        changeReqInfo.setChangeReqId(hlsCusAbsPkg.getHlsCusAbsProduct().getChangeReqId());
        changeReqInfo.setChangeReqDate(hlsCusAbsPkg.getHlsCusAbsProduct().getChangeReqDate());
        changeReqInfo.setChangeDescription(hlsCusAbsPkg.getHlsCusAbsProduct().getChangeDescription());
        hlsCusChangeReqInfoService.updateByPrimaryKeySelective(request,changeReqInfo);
        HlsCusAbsProduct hlsCusAbsProduct = self().updateByPrimaryKeySelective(request, hlsCusAbsPkg.getHlsCusAbsProduct());
        hlsCusAbsProduct = self().selectByPrimaryKey(request, hlsCusAbsProduct);
        if (StringUtils.equals(HlsCusConstant.ABS_CHANGE_TYPE.PREVFINISH, hlsCusAbsProduct.getAssetChangeType())) {
            List<HlsCusAbsContractFinish> finishList = hlsCusAbsPkg.getHlsCusAbsContractFinishes();
            if (CollectionUtils.isNotEmpty(finishList)) {
                for (HlsCusAbsContractFinish finish : finishList) {
                    finish.setProductId(hlsCusAbsProduct.getProductId());
                    if (finish.getFinishId() == null) {
                        finish.set__status(DTOStatus.ADD);
                    } else {
                        finish.set__status(DTOStatus.UPDATE);
                    }
                }
                hlsCusAbsContractFinishService.batchUpdate(request, finishList);
            }
        } else if (StringUtils.equals(HlsCusConstant.ABS_CHANGE_TYPE.CONTRACT, hlsCusAbsProduct.getAssetChangeType())) {
            List<HlsCusAbsAssetsPackage> absAssetsPackages = hlsCusAbsPkg.getHlsCusAbsAssetsPackages();
            if (CollectionUtils.isNotEmpty(absAssetsPackages)) {
                for (HlsCusAbsAssetsPackage assetsPackage : absAssetsPackages) {
                    if (assetsPackage.getPackId() == null) {
                        assetsPackage.setPackId(hlsCusAbsProduct.getPackId());
                        assetsPackage.set__status(DTOStatus.ADD);
                    } else {
                        assetsPackage.set__status(DTOStatus.UPDATE);
                    }
                }
                hlsCusAbsAssetsPackageService.batchUpdate(request, absAssetsPackages);
            }
        }
        if(CollectionUtils.isNotEmpty(hlsCusAbsPkg.getHlsCusAbsProductCollections())){
            for (HlsCusAbsProductCollection productCollection : hlsCusAbsPkg.getHlsCusAbsProductCollections()) {
                productCollection.setProductId(hlsCusAbsProduct.getProductId());
                if(productCollection.getCollectionId() == null){
                    productCollection.set__status(DTOStatus.ADD);
                }else{
                    productCollection.set__status(DTOStatus.UPDATE);
                }
            }
            hlsCusAbsProductCollectionService.batchUpdate(request, hlsCusAbsPkg.getHlsCusAbsProductCollections());
        }
        updateAbsAttachemntFile(request, hlsCusAbsPkg);
        return hlsCusAbsPkg;
    }

   /* @Override
    public HlsCusAbsPkg calcAssetChange(IRequest request, HlsCusAbsPkg hlsCusAbsPkg) throws HlsCusException {
        self().saveAssetChange(request,hlsCusAbsPkg);

        HlsCusAbsProduct hlsCusAbsProduct=new HlsCusAbsProduct();
        hlsCusAbsProduct.setProductId(hlsCusAbsPkg.getHlsCusAbsProduct().getProductId());
        hlsCusAbsProduct=hlsCusAbsProductMapper.queryProductData(hlsCusAbsProduct).get(0);
        calculateCollectionCash(request,hlsCusAbsProduct);
        return hlsCusAbsPkg;
    }
*/
    /*@Override
    public HlsCusAbsProduct confirmAssetChange(IRequest request, HlsCusAbsProduct changeProduct) {
        changeProduct = self().selectByPrimaryKey(request, changeProduct);
        HlsCusAbsProduct normalProduct = new HlsCusAbsProduct();
        normalProduct.setProductId(changeProduct.getRefProductId());
        normalProduct = self().selectByPrimaryKey(request, normalProduct);

        HlsCusAbsAssetsPack historyPack = new HlsCusAbsAssetsPack();
        if (normalProduct.getPackId() != null && HlsCusConstant.ABS_CHANGE_TYPE.CONTRACT.equals(changeProduct.getAssetChangeType())) {
            HlsCusAbsAssetsPack normalPack = new HlsCusAbsAssetsPack();
            normalPack.setPackId(normalProduct.getPackId());
            normalPack = hlsCusAbsAssetsPackService.selectByPrimaryKey(request, normalPack);

            Map<String, String> mapPack = hlsBeanRefUtilService.getFieldValueMap(normalPack);
            hlsBeanRefUtilService.setFieldValue(historyPack, mapPack);
            historyPack.setDataClass(HlsCusConstant.DATA_CLASS.HISTORY);
            historyPack.setChangeReqId(changeProduct.getChangeReqId());
            historyPack.setRefPackId(normalPack.getPackId());
            historyPack.setPackId(null);
            hlsCusAbsAssetsPackService.insertSelective(request, historyPack);

            //复制资产包 noraml-->history
            clonePackLineData(request, normalPack.getPackId(), historyPack.getPackId());
            //删除normal未结清
            absAssetsPackageMapper.deleteAssetPageageNotFinish(normalPack.getPackId());
            //复制未结清的changeReq--> normal
            List<HlsCusAbsAssetsPackage> absAssetsPackages = absAssetsPackageMapper.selectAssetPageageNotFinish(changeProduct.getPackId());
            for (HlsCusAbsAssetsPackage absAssetsPackage : absAssetsPackages) {
                absAssetsPackage.setPackageId(null);
                absAssetsPackage.setRefPackageId(null);
                absAssetsPackage.setPackId(normalPack.getPackId());
                absAssetsPackage.setCreatedBy(request.getUserId());
                absAssetsPackage.setLastUpdatedBy(request.getUserId());
                absAssetsPackage.setCreationDate(null);
                absAssetsPackage.setLastUpdateDate(null);
                hlsCusAbsAssetsPackageService.insertSelective(request, absAssetsPackage);
            }
        }

        HlsCusAbsProduct historyProduct = new HlsCusAbsProduct();
        Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(normalProduct);
        hlsBeanRefUtilService.setFieldValue(historyProduct, map);
        historyProduct.setIssueAmount(normalProduct.getIssueAmount());
        historyProduct.setChangeAmount(normalProduct.getChangeAmount());
        historyProduct.setMakeupAmount(normalProduct.getMakeupAmount());
        historyProduct.setDataClass(HlsCusConstant.DATA_CLASS.HISTORY);
        historyProduct.setChangeReqId(changeProduct.getChangeReqId());
        historyProduct.setRefProductId(normalProduct.getProductId());
        historyProduct.setProductStatus(changeProduct.getProductStatus());
        historyProduct.setProductId(null);
        historyProduct.setPackId(historyPack.getPackId());
        self().insertSelective(request, historyProduct);

        normalProduct.setProductStatus(changeProduct.getProductStatus());
        self().updateByPrimaryKeySelective(request, normalProduct);

        HlsCusChangeReqInfo hlsCusChangeReqInfo = new HlsCusChangeReqInfo();
        hlsCusChangeReqInfo.setChangeReqId(changeProduct.getChangeReqId());
        hlsCusChangeReqInfo.setStatus(HlsCusConstant.WORKFLOW_STATUS.APPROVED);
        hlsCusChangeReqInfoService.updateByPrimaryKeySelective(request, hlsCusChangeReqInfo);

        cloneLineData(request, HlsCusConstant.ABS_CHANGE_TYPE.ASSET,normalProduct, historyProduct.getProductId());
        deleteLineData(request,HlsCusConstant.ABS_CHANGE_TYPE.ASSET,normalProduct.getBusinessType(),normalProduct.getProductId());
        changeLineData(request,HlsCusConstant.ABS_CHANGE_TYPE.ASSET, normalProduct.getBusinessType(),changeProduct.getProductId(), normalProduct.getProductId());
        return changeProduct;
    }
*/

    @Override
    public HlsCusAbsProduct submitAssetChange(IRequest request, HlsCusAbsPkg hlsCusAbsPkg) throws HlsCusException{
       // hlsCusAbsPkg.getHlsCusAbsProduct().setProductStatus(HlsCusConstant.WORKFLOW_STATUS.APPROVING);
        hlsCusAbsPkg = saveAssetChange(request, hlsCusAbsPkg);
        HlsCusAbsProduct absProduct=hlsCusAbsProductMapper.selectByPrimaryKey(hlsCusAbsPkg.getHlsCusAbsProduct());
        databaseLockProvider.lock(absProduct);
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(request.getUserId());
        if (ObjectUtils.isEmpty(employee)) {
            throw new HlsCusException("获取提交人失败");
        }
        String employeeCode = employee.getEmployeeCode();
        request.setEmployeeCode(employeeCode);

        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(HlsCusConstant.WORKFLOW_PARAMS.WORKFLOW_TYPE, HlsCusConstant.ABS_WFL.ASSEET_CHANGE_WFL);
        //变更
        HlsCusChangeReqInfo cusChangeReqInfo = new HlsCusChangeReqInfo();
        cusChangeReqInfo.setChangeReqId(absProduct.getChangeReqId());
        cusChangeReqInfo.setStatus(HlsCusConstant.WORKFLOW_STATUS.APPROVING);
        hlsCusChangeReqInfoService.updateByPrimaryKeySelective(request, cusChangeReqInfo);
        List<HlsCusAbsProduct> productList = new ArrayList<>();
        productList.add(absProduct);
        activitiStartService.start(request, productList, params);

        return absProduct;
    }
    @Override
    public HlsCusAbsProduct confirmProductChange(IRequest request, HlsCusAbsProduct hlsCusAbsProduct) {
        //hlsCusAbsProduct = saveProductChange(request, hlsCusAbsProduct);
        HlsCusAbsProduct changeProduct = new HlsCusAbsProduct();
        changeProduct.setProductId(hlsCusAbsProduct.getProductId());
        changeProduct = self().selectByPrimaryKey(request, changeProduct);

        HlsCusAbsProduct normalProduct = new HlsCusAbsProduct();
        normalProduct.setProductId(changeProduct.getRefProductId());
        normalProduct = self().selectByPrimaryKey(request, normalProduct);
        HlsCusAbsProduct historyProduct = new HlsCusAbsProduct();
        Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(normalProduct);
        hlsBeanRefUtilService.setFieldValue(historyProduct, map);
        historyProduct.setIssueAmount(normalProduct.getIssueAmount());
        historyProduct.setChangeAmount(normalProduct.getChangeAmount());
        historyProduct.setMakeupAmount(normalProduct.getMakeupAmount());
        historyProduct.setDataClass(HlsCusConstant.DATA_CLASS.HISTORY);
        historyProduct.setChangeReqId(changeProduct.getChangeReqId());
        historyProduct.setRefProductId(normalProduct.getProductId());
        historyProduct.setProductStatus(changeProduct.getProductStatus());
        historyProduct.setProductId(null);
        self().insertSelective(request, historyProduct);

        normalProduct.setIssueAmount(changeProduct.getIssueAmount());
        normalProduct.setChangeAmount(changeProduct.getChangeAmount());
        normalProduct.setMakeupAmount(changeProduct.getMakeupAmount());
        normalProduct.setInterestRate(changeProduct.getChangeRate());
        normalProduct.setProductLevel(changeProduct.getProductLevel());
        normalProduct.setProductStatus(changeProduct.getProductStatus());
        self().updateByPrimaryKeySelective(request, normalProduct);

        HlsCusChangeReqInfo hlsCusChangeReqInfo = new HlsCusChangeReqInfo();
        hlsCusChangeReqInfo.setChangeReqId(changeProduct.getChangeReqId());
        hlsCusChangeReqInfo.setStatus(HlsCusConstant.WORKFLOW_STATUS.APPROVED);
        hlsCusChangeReqInfoService.updateByPrimaryKeySelective(request, hlsCusChangeReqInfo);

        cloneLineData(request, HlsCusConstant.ABS_CHANGE_TYPE.PRODUCT,normalProduct, historyProduct.getProductId());
        deleteLineData(request,HlsCusConstant.ABS_CHANGE_TYPE.PRODUCT,normalProduct.getBusinessType(),normalProduct.getProductId());
        changeLineData(request,HlsCusConstant.ABS_CHANGE_TYPE.PRODUCT, normalProduct.getBusinessType(),changeProduct.getProductId(), normalProduct.getProductId());
        return changeProduct;
    }
    @Override
    public List<HlsCusAbsProduct> queryProductHome(IRequest request, HlsCusAbsProduct hlsCusAbsProduct, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return hlsCusAbsProductMapper.queryProductHome(hlsCusAbsProduct);
    }

    @Override
    public List<HlsCusAbsProduct> queryProductData(IRequest request, HlsCusAbsProduct hlsCusAbsProduct, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return hlsCusAbsProductMapper.queryProductData(hlsCusAbsProduct);
    }

    @Override
    public List<Map<String, Object>> queryProductChart(IRequest request, HlsCusAbsProduct hlsCusAbsProduct) {
        List<Map<String, Object>> list = hlsCusAbsProductMapper.queryProductChart(hlsCusAbsProduct);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>(0);
        }
        else {
            BigDecimal issueAmountAll=new BigDecimal("0");
            BigDecimal surplusAmountAll=new BigDecimal("0");
            for (Map<String, Object> m : list) {
                issueAmountAll= issueAmountAll.add((BigDecimal)m.getOrDefault("issueAmountSum",new BigDecimal("0")));
                surplusAmountAll=surplusAmountAll.add((BigDecimal)m.getOrDefault("surplusAmount",new BigDecimal("0")));
                //设置数据万元
                formatNum(m, new String[]{"issueAmountSum", "surplusAmount"});
            }
            Map<String,Object> all=new HashMap<>(8);
            all.put("issueAmountSum",issueAmountAll);
            all.put("surplusAmount",surplusAmountAll);
            all.put("businessType","ALL");
            formatNum(all, new String[]{"issueAmountSum", "surplusAmount"});
            list.add(all);
        }
       /* for (Map<String, Object> map : list) {
            map.put("productPercent", (((Integer)map.get("productCount")/(Integer)map.get("productTotalCount"))*100)+"%");
        }*/

        return list;
    }
    private void formatNum(Map<String, Object> map, String[] keys)
    {
        DecimalFormat df = new DecimalFormat("#,##0.000");
        for (String key : keys) {
            map.put(key,df.format((((BigDecimal)map.get(key)).divide(new BigDecimal("10000"),3, BigDecimal.ROUND_HALF_UP))));
        }
    }

  /*  @Override
    public List<HlsSystemNotice> queryProductNotice(IRequest request, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<HlsSystemNotice> list = hlsCusAbsProductMapper.queryProductNotice();
        for (int i = 0; i < list.size(); ++i) {
            try {
                Date date = ((HlsSystemNotice) list.get(i)).getNotice_datetime();
                Date now = new Date();
                Long differ = (now.getTime() - date.getTime()) / 3600L;
                if (differ >= 0L && differ <= 60L) {
                    ((HlsSystemNotice) list.get(i)).setNotice_datetime_differ(differ.toString());
                } else if (differ < 0L) {
                    ((HlsSystemNotice) list.get(i)).setNotice_datetime_differ("1");
                } else {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String dateString = formatter.format(date);
                    ((HlsSystemNotice) list.get(i)).setNotice_datetime_differ(dateString);
                }
            } catch (Exception var12) {
                ;
            }
        }
        return list;
    }
*/
    @Override
    public HlsCusAbsProduct updateProduct(IRequest request, HlsCusAbsPkg pkg) {
        if (pkg.getHlsCusAbsProduct().getProductId() == null) {
            return insertProduct(request, pkg.getHlsCusAbsProduct());
        }
        String businessType = pkg.getHlsCusAbsProduct().getBusinessType();

        if (!HlsCusConstant.ABS_BUSINESS_TYPE.ABS.equals(businessType) && !HlsCusConstant.ABS_BUSINESS_TYPE.ABN.equals(businessType)) {
            pkg.getHlsCusAbsProduct().setProductTerm(pkg.getHlsCusAbsProduct().getLoanTerm());
            if (null!=pkg.getHlsCusAbsProduct().getDueAmount())
            {
                pkg.getHlsCusAbsProduct().setIssueAmount(new BigDecimal(pkg.getHlsCusAbsProduct().getDueAmount().toString()));
            }
            pkg.getHlsCusAbsProduct().setPlanDateEnd(pkg.getHlsCusAbsProduct().getWithdrawEndDate());
            pkg.getHlsCusAbsProduct().setDueDateBegin(pkg.getHlsCusAbsProduct().getDueDate());
        }
        HlsCusAbsProduct hlsCusAbsProduct = self().updateByPrimaryKey(request, pkg.getHlsCusAbsProduct());
        if(HlsCusConstant.WORKFLOW_STATUS.NEW.equals(hlsCusAbsProduct.getProductStatus()) || HlsCusConstant.WORKFLOW_STATUS.APPROVED_RETURN.equals(hlsCusAbsProduct.getProductStatus())) {
            hlsCusAbsProductMapper.updateProductReportName(hlsCusAbsProduct.getProductId());
        }

        HlsCusAbsProductQuotation productQuotation = null;
        //报价数据
        if (!HlsCusConstant.ABS_BUSINESS_TYPE.ABS.equals(businessType) && !HlsCusConstant.ABS_BUSINESS_TYPE.ABN.equals(businessType)) {
            productQuotation = getProductQuotation(request, pkg.getHlsCusAbsProduct());
            if (pkg.getHlsCusAbsProduct().getQuotationId() == null) {
                productQuotationService.insertSelective(request, productQuotation);
            }else{
                productQuotationService.updateByPrimaryKey(request, productQuotation);
            }
            pkg.getHlsCusAbsProduct().setQuotationId(productQuotation.getQuotationId());
            hlsCusAbsProduct.setQuotationId(productQuotation.getQuotationId());
        }

        if (CollectionUtils.isNotEmpty(pkg.getHlsCusAbsProBankAccounts())) {
            for (HlsCusAbsBankAccount bankAccount : pkg.getHlsCusAbsProBankAccounts()) {
                bankAccount.setSourceKey(hlsCusAbsProduct.getProductId());
                bankAccount.setSourceType(HlsCusConstant.ABS_PRO_TYPE.PRODUCT);
                if (bankAccount.getAbsBankAccountId() == null) {
                    bankAccount.set__status(DTOStatus.ADD);
                } else {
                    bankAccount.set__status(DTOStatus.UPDATE);
                }
            }
            hlsCusAbsBankAccountService.batchUpdate(request, pkg.getHlsCusAbsProBankAccounts());
        }
        if (CollectionUtils.isNotEmpty(pkg.getHlsCusAbsProFeeInfos())) {
            for (HlsCusAbsProFeeInfo feeInfo : pkg.getHlsCusAbsProFeeInfos()) {
                feeInfo.setSourceKey(hlsCusAbsProduct.getProductId());
                feeInfo.setSourceType(HlsCusConstant.ABS_PRO_TYPE.PRODUCT);
                if (feeInfo.getFeeInfoId() == null) {
                    feeInfo.set__status(DTOStatus.ADD);
                } else {
                    feeInfo.set__status(DTOStatus.UPDATE);
                }
            }
            hlsCusAbsProFeeInfoService.batchUpdate(request, pkg.getHlsCusAbsProFeeInfos());
        }
        if (CollectionUtils.isNotEmpty(pkg.getHlsCusAbsProductOrganizations())) {
            for (HlsCusAbsProductOrganization productOrganization : pkg.getHlsCusAbsProductOrganizations()) {
                productOrganization.setProductId(hlsCusAbsProduct.getProductId());
                if (productOrganization.getOrganizationId() == null) {
                    productOrganization.set__status(DTOStatus.ADD);
                } else {
                    productOrganization.set__status(DTOStatus.UPDATE);
                }
            }
            hlsCusAbsProductOrganizationService.batchUpdate(request, pkg.getHlsCusAbsProductOrganizations());
        }

        if (CollectionUtils.isNotEmpty(pkg.getHlsCusAbsProductPurposes())) {
            for (HlsCusAbsProductPurpose productPurpose : pkg.getHlsCusAbsProductPurposes()) {
                productPurpose.setProductId(hlsCusAbsProduct.getProductId());
                if (productPurpose.getPurposeId() == null) {
                    productPurpose.set__status(DTOStatus.ADD);
                } else {
                    productPurpose.set__status(DTOStatus.UPDATE);
                }
            }
            hlsCusAbsProductPurposeService.batchUpdate(request, pkg.getHlsCusAbsProductPurposes());
        }

        if (CollectionUtils.isNotEmpty(pkg.getHlsCusAbsProductStructures())) {
            for (HlsCusAbsProductStructure productStructure : pkg.getHlsCusAbsProductStructures()) {
                productStructure.setProductId(hlsCusAbsProduct.getProductId());
                if (productStructure.getStructureId() == null) {
                    productStructure.set__status(DTOStatus.ADD);
                } else {
                    productStructure.set__status(DTOStatus.UPDATE);
                }
            }
            hlsCusAbsProductStructureService.batchUpdate(request, pkg.getHlsCusAbsProductStructures());
        }


        if (CollectionUtils.isNotEmpty(pkg.getHlsCusAbsProductReceipts())) {
            for (HlsCusAbsProductReceipt productReceipt : pkg.getHlsCusAbsProductReceipts()) {
                productReceipt.setProductId(hlsCusAbsProduct.getProductId());
                if (productReceipt.getReceiptId() == null) {
                    productReceipt.set__status(DTOStatus.ADD);
                } else {
                    productReceipt.set__status(DTOStatus.UPDATE);
                }
            }
            hlsCusAbsProductReceiptService.batchUpdate(request, pkg.getHlsCusAbsProductReceipts());
        }

        if (CollectionUtils.isNotEmpty(pkg.getHlsCusAbsProductCollections())) {
            for (HlsCusAbsProductCollection productCollection : pkg.getHlsCusAbsProductCollections()) {
                productCollection.setProductId(hlsCusAbsProduct.getProductId());
                productCollection.set__status(DTOStatus.UPDATE);
            }
            hlsCusAbsProductCollectionService.batchUpdate(request, pkg.getHlsCusAbsProductCollections());
        }

        if (CollectionUtils.isNotEmpty(pkg.getHlsCusAbsProductRepayments())) {
            for (HlsCusAbsProductRepayment productRepayment : pkg.getHlsCusAbsProductRepayments()) {
                productRepayment.setProductId(hlsCusAbsProduct.getProductId());
                productRepayment.setProjectId(hlsCusAbsProduct.getProjectId());
                productRepayment.setPlannedCalcDate(productRepayment.getPlannedDueDate());
                if(productRepayment.getRepaymentId() == null){
                    productRepayment.set__status(DTOStatus.ADD);
                } else {
                    productRepayment.set__status(DTOStatus.UPDATE);
                }
            }
            productRepaymentService.batchUpdate(request, pkg.getHlsCusAbsProductRepayments());
        }

        if(CollectionUtils.isNotEmpty(pkg.getHlsCusAbsProductCollections())){
            for (HlsCusAbsProductCollection productCollection : pkg.getHlsCusAbsProductCollections()) {
                productCollection.setProductId(hlsCusAbsProduct.getProductId());
                if(productCollection.getCollectionId() == null){
                    productCollection.set__status(DTOStatus.ADD);
                }else{
                    productCollection.set__status(DTOStatus.UPDATE);
                }
            }
            hlsCusAbsProductCollectionService.batchUpdate(request, pkg.getHlsCusAbsProductCollections());
        }

        updateAbsAttachemntFile(request, pkg);

        if (!HlsCusConstant.ABS_BUSINESS_TYPE.ABS.equals(businessType) && !HlsCusConstant.ABS_BUSINESS_TYPE.ABN.equals(businessType)) {
            calXirr(request, productQuotation);
        }

        return hlsCusAbsProduct;
    }

    private void updateAbsAttachemntFile(IRequest request, HlsCusAbsPkg pkg) {
        //附件
        if (pkg.getHlsCusAbsProAttachments() != null) {
            for (HlsCusPrjProjectAttachment attachment : pkg.getHlsCusAbsProAttachments()) {
                if (attachment.getProjectAttachmentId() != null) {
                    if (attachment.getDescription() == null) {
                        attachment.setDescription("");
                    }
                    if (attachment.getAttachmentCode() == null) {
                        attachment.setAttachmentCode("");
                    }
                    hlsCusPrjProjectAttachmentService.updateByPrimaryKeySelective(request, attachment);

                    HlsCusSysFile sysFile = new HlsCusSysFile();
                    sysFile.setFileId(Long.parseLong(attachment.getFileId()));
                    sysFile.setFileName(attachment.getFileName());
                    hlsSysFileService.updateByPrimaryKeySelective(request, sysFile);
                }
            }

        }
    }


    private HlsCusAbsProductQuotation getProductQuotation(IRequest request, HlsCusAbsProduct hlsCusAbsProduct) {
        HlsCusAbsProductQuotation productQuotation = new HlsCusAbsProductQuotation();
        productQuotation.setQuotationId(hlsCusAbsProduct.getQuotationId());
        productQuotation.setProductId(hlsCusAbsProduct.getProductId());
        productQuotation.setProjectId(hlsCusAbsProduct.getProjectId());
        productQuotation.setProductCurrencyCode(hlsCusAbsProduct.getProductCurrencyCode());
        productQuotation.setBaseRate(hlsCusAbsProduct.getBaseRate());
        productQuotation.setBaseRateType(hlsCusAbsProduct.getBaseRateType());
        productQuotation.setCalcInterestYearDays(hlsCusAbsProduct.getCalcInterestYearDays());
        productQuotation.setComperhensiveFinancingCost(hlsCusAbsProduct.getComperhensiveFinancingCost());
        productQuotation.setConvertBeginPrincipalAmount(hlsCusAbsProduct.getConvertBeginPrincipalAmount());
        productQuotation.setConvertCurrency(hlsCusAbsProduct.getConvertCurrency());
        productQuotation.setDueAmount(hlsCusAbsProduct.getDueAmount());
        productQuotation.setDueDate(hlsCusAbsProduct.getDueDate());
        productQuotation.setExchangeRate(hlsCusAbsProduct.getExchangeRate());
        productQuotation.setFirstRate(hlsCusAbsProduct.getFirstRate());
        productQuotation.setFloatingRangeMethod(hlsCusAbsProduct.getFloatingRangeMethod());
        productQuotation.setFloatingRangeMethodRemark(hlsCusAbsProduct.getFloatingRangeMethodRemark());
        productQuotation.setFloatingWay(hlsCusAbsProduct.getFloatingWay());
        productQuotation.setFloatingWayRange(hlsCusAbsProduct.getFloatingWayRange());
        productQuotation.setInterestCalcDate(hlsCusAbsProduct.getInterestCalcDate());
        productQuotation.setInterestCalcMethod(hlsCusAbsProduct.getInterestCalcMethod());
        productQuotation.setInterestMonth(hlsCusAbsProduct.getInterestMonth());
        productQuotation.setInterestPaymentDate(hlsCusAbsProduct.getInterestPaymentDate());
        productQuotation.setIntRate(hlsCusAbsProduct.getIntRate());
        productQuotation.setIntRateType(hlsCusAbsProduct.getIntRateType());
        productQuotation.setInterestCycle(hlsCusAbsProduct.getInterestCycle());
        productQuotation.setLoanTimes(hlsCusAbsProduct.getLoanTimes());
        productQuotation.setLoanTerm(hlsCusAbsProduct.getLoanTerm());
        productQuotation.setMeasureType(hlsCusAbsProduct.getMeasureType());
        productQuotation.setTaxRate(hlsCusAbsProduct.getTaxRate());
        productQuotation.setWithdrawEndDate(hlsCusAbsProduct.getWithdrawEndDate());
        productQuotation.setXirr(hlsCusAbsProduct.getXirr());
        productQuotation.setRateChangeAfter(hlsCusAbsProduct.getRateChangeAfter());
        productQuotation.setRateChangeDate(hlsCusAbsProduct.getRateChangeDate());
        productQuotation.setCreatedBy(request.getUserId());
        productQuotation.setLastUpdatedBy(request.getUserId());
        productQuotation.setPublishDate(hlsCusAbsProduct.getPublishDate());
        return productQuotation;

    }

    @Override
    public HlsCusAbsProduct calcProduct(IRequest request, HlsCusAbsPkg pkg) throws HlsCusException {
        HlsCusAbsProduct hlsCusAbsProduct = self().updateProduct(request, pkg);
        return calculateCollectionCash(request,hlsCusAbsProduct);
    }

    @Override
    public void calcProduct(IRequest request, Long productId) throws HlsCusException {
        if (productId != null) {
            HlsCusAbsProduct hlsCusAbsProduct = new HlsCusAbsProduct();
            hlsCusAbsProduct.setProductId(productId);
            hlsCusAbsProduct = absProductService.selectByPrimaryKey(request, hlsCusAbsProduct);
            calculateCollectionCash(request, hlsCusAbsProduct);
        }
    }

    /**
     * 计算归集
     * @param request
     * @param hlsCusAbsProduct
     * @return
     * @throws HlsCusException
     */
    private HlsCusAbsProduct calculateCollectionCash(IRequest request, HlsCusAbsProduct hlsCusAbsProduct) throws HlsCusException{

        HlsCusAbsProductCollection hlsCusAbsProductCollection = new HlsCusAbsProductCollection();
        hlsCusAbsProductCollection.setProductId(hlsCusAbsProduct.getProductId());
        hlsCusAbsProductCollection.setDataClass(HlsCusAbsConstant.DATA_CLASS.NORMAL);
        // 删除之前计算过的数据
        hlsCusAbsProductCashDetailService.deleteCashDetailByCollection(hlsCusAbsProductCollection);
        hlsCusAbsProductCollectionService.deleteByCollection(hlsCusAbsProductCollection);
        // 起息日
        LocalDate dueDateBegin = AbsDateUtils.format(hlsCusAbsProduct.getDueDateBegin());
        HlsCusAbsAssetsPack hlsCusAbsAssetsPack = new HlsCusAbsAssetsPack();
        hlsCusAbsAssetsPack.setPackId(hlsCusAbsProduct.getPackId());
        hlsCusAbsAssetsPack = hlsCusAbsAssetsPackService.selectByPrimaryKey(request, hlsCusAbsAssetsPack);
        if (hlsCusAbsAssetsPack != null) {
            // 封包日
            LocalDate baseDate = AbsDateUtils.format(hlsCusAbsAssetsPack.getBaseDate());
            // 第一个兑付日
            LocalDate firstCashDate = AbsDateUtils.format(hlsCusAbsProduct.getFirstCashDate());
            // 第一个租金回收计算日
            LocalDate firstRentalBackDate = AbsDateUtils.format(hlsCusAbsProduct.getFirstRentalBackDate());
            // 资产包最大日期
            Date packageEndDate = hlsCusAbsProductMapper.selectConPackageMaxDueDate(hlsCusAbsProduct.getProductId());
            hlsCusAbsProduct.setPackageEndDate(packageEndDate);

            List<HlsCusAbsProductCollection> list = new ArrayList<>();
            // 获取最后确认的一期 从这一期开始往下生成
            HlsCusAbsProductCollection productCollection =
                    hlsCusAbsProductCollectionService.selectLastConfirmCollection(hlsCusAbsProduct.getProductId());
            if (productCollection == null) {
                HlsCusAbsProductCollection collection = new HlsCusAbsProductCollection();
                collection.setProductId(hlsCusAbsProduct.getProductId());
                collection.setTimes(1L);
                collection.setRentalBackDate(hlsCusAbsProduct.getFirstRentalBackDate());
                collection.setCashDate(hlsCusAbsProduct.getFirstCashDate());
                collection.setCollectionDate(
                        AbsDateUtils.format(firstRentalBackDate.plusDays(hlsCusAbsProduct.getCollectionDateRule())));
                collection.setRemittanceDate(
                        AbsDateUtils.format(firstRentalBackDate.plusDays(hlsCusAbsProduct.getRemittanceDateRule())));
                collection.setReportDate(AbsDateUtils.format(firstRentalBackDate.plusDays(hlsCusAbsProduct.getReportDateRule())));
                Double principal = hlsCusAbsProductMapper.queryPrincipal(baseDate.toString(),
                        firstRentalBackDate.toString(), hlsCusAbsProduct.getProductId()).doubleValue();
                Double interest = hlsCusAbsProductMapper.queryInterest2(baseDate.toString(),
                        firstRentalBackDate.toString(), hlsCusAbsProduct.getProductId()).doubleValue();
                Double finishAmount = hlsCusAbsProductMapper.selectOtherFinishAmount(baseDate.toString(),
                        firstRentalBackDate.toString(), hlsCusAbsProduct.getProductId()).doubleValue();
                collection.setPrincipal(BigDecimal.valueOf(principal));
                collection.setInterest(BigDecimal.valueOf(interest));
                collection.setDueAmount(BigDecimal.valueOf(principal + interest + finishAmount));
                collection.setCollectionAmount(collection.getDueAmount());
                collection.setCashAmount(collection.getDueAmount());
                collection.setRemittanceAmount(collection.getDueAmount());
                collection.setDataClass(HlsCusAbsConstant.DATA_CLASS.NORMAL);
                collection.setInterestPeriodDays(firstCashDate.toEpochDay() - dueDateBegin.toEpochDay());
                if (collection.getInterestPeriodDays().compareTo(new Long(0L)) < 0) {
                    throw new HlsCusException("起息日不可小于第一个兑付日！请检查");
                }
                collection.setCollectionCfItem(HlsCusAbsConstant.CASHFLOW_ITEM.COLLECTION_CF_ITEM);
                collection.setCollectionCfType(HlsCusAbsConstant.CASHFLOW_ITEM.COLLECTION_CF_TYPE);
                collection.setRemittanceCfItem(HlsCusAbsConstant.CASHFLOW_ITEM.REMITTANCE_CF_ITEM);
                collection.setRemittanceCfType(HlsCusAbsConstant.CASHFLOW_ITEM.COLLECTION_CF_TYPE);

                calculateFee(request, collection, hlsCusAbsProduct);
                collection = hlsCusAbsProductCollectionService.insertSelective(request, collection);
                calculateCashDeatil(request, collection, hlsCusAbsProduct);
                list.add(collection);
            } else {
                list.add(productCollection);
            }
            if (packageEndDate.compareTo(hlsCusAbsProduct.getFirstRentalBackDate()) == 1) {
                switch (hlsCusAbsProduct.getCashPeriod()) {
                    case MONTH:
                        list = calcuCollectionInfo(request, hlsCusAbsProduct, 1, list);
                        break;
                    case QUARTER:
                        list = calcuCollectionInfo(request, hlsCusAbsProduct, 3, list);
                        break;
                    case HALF_A_YEAR:
                        list = calcuCollectionInfo(request, hlsCusAbsProduct, 6, list);
                        break;
                    case YEAR:
                        list = calcuCollectionInfo(request, hlsCusAbsProduct, 12, list);
                        break;
                    default:
                        // logger.error("兑付周期 {} 不合法", hlsCusAbsProduct.getCashPeriod());
                        throw new HlsCusException("兑付周期不合法");
                }
            }
            hlsCusAbsProFeeInfoService.updateProductFeeAmount(hlsCusAbsProduct.getProductId());
        }


        return hlsCusAbsProduct;


    /*    hlsCusAbsProductCollectionService.deleteNotConfirmByProductId(hlsCusAbsProduct.getProductId(),HlsCusConstant.DATA_CLASS.NORMAL);

        hlsCusAbsProductCashDetailService.deleteCashDetailByProduct(hlsCusAbsProduct.getProductId(),HlsCusConstant.DATA_CLASS.NORMAL);

        //起息日
        LocalDate dueDateBegin = format(hlsCusAbsProduct.getDueDateBegin());

        //封包日
        LocalDate baseDate = format(hlsCusAbsProduct.getBaseDate());

        //第一个兑付日
        LocalDate firstCashDate = format(hlsCusAbsProduct.getFirstCashDate());
        //第一个租金回收计算日
        LocalDate firstRentalBackDate = format(hlsCusAbsProduct.getFirstRentalBackDate());


        //资产包最大日期
        Date packageEndDate = hlsCusAbsProductMapper.selectConPackageMaxDueDate(hlsCusAbsProduct.getProductId());
        if (packageEndDate == null) {
            throw new HlsCusException("无资产包记录，请勿计算");
        }
        hlsCusAbsProduct.setPackageEndDate(packageEndDate);

        List<HlsCusAbsProductCollection> list = new ArrayList<>();

        //获取最后确认的一期 从这一期开始往下生成
        HlsCusAbsProductCollection productCollection = hlsCusAbsProductCollectionService.selectLastConfirmCollection(hlsCusAbsProduct.getProductId());
        if(productCollection==null) {
            HlsCusAbsProductCollection collection = new HlsCusAbsProductCollection();
            collection.setProductId(hlsCusAbsProduct.getProductId());
            collection.setTimes(1L);
            collection.setRentalBackDate(hlsCusAbsProduct.getFirstRentalBackDate());
            collection.setCashDate(hlsCusAbsProduct.getFirstCashDate());
            collection.setCollectionDate(format(firstRentalBackDate.plusDays(hlsCusAbsProduct.getCollectionDateRule())));
            collection.setRemittanceDate(format(firstRentalBackDate.plusDays(hlsCusAbsProduct.getRemittanceDateRule())));
            collection.setReportDate(format(firstRentalBackDate.plusDays(hlsCusAbsProduct.getReportDateRule())));
            BigDecimal principal = hlsCusAbsProductMapper.queryPrincipal(baseDate.toString(), firstRentalBackDate.toString(), hlsCusAbsProduct.getProductId());
            BigDecimal interest = hlsCusAbsProductMapper.queryInterest2(baseDate.toString(), firstRentalBackDate.toString(), hlsCusAbsProduct.getProductId());
            BigDecimal finishAmount = hlsCusAbsProductMapper. selectOtherFinishAmount(baseDate.toString(), firstRentalBackDate.toString(), hlsCusAbsProduct.getProductId());
            collection.setPrincipal(principal);
            collection.setInterest(interest);
            collection.setDueAmount(principal.add(interest).add(finishAmount));
            collection.setCollectionAmount(collection.getDueAmount());
            collection.setCashAmount(collection.getDueAmount());
            collection.setRemittanceAmount(collection.getDueAmount());
            collection.set__status(DTOStatus.ADD);
            collection.setDataClass(HlsCusConstant.DATA_CLASS.NORMAL);
            collection.setInterestPeriodDays(firstCashDate.toEpochDay() - dueDateBegin.toEpochDay());
            if (collection.getInterestPeriodDays().compareTo(new Long(0L)) < 0) {
                throw new HlsCusException("起息日不可小于第一个兑付日！请检查");
            }
            collection.setCollectionCfItem(HlsCusConstant.CASHFLOW_ITEM.COLLECTION_CF_ITEM);
            collection.setCollectionCfType(HlsCusConstant.CASHFLOW_ITEM.COLLECTION_CF_TYPE);
            collection.setRemittanceCfItem(HlsCusConstant.CASHFLOW_ITEM.REMITTANCE_CF_ITEM);
            collection.setRemittanceCfType(HlsCusConstant.CASHFLOW_ITEM.COLLECTION_CF_TYPE);

            calculateFee(request, collection, hlsCusAbsProduct);
            hlsCusAbsProductCollectionService.insertSelective(request, collection);
            calculateCashDeatil(request, collection, hlsCusAbsProduct);
            list.add(collection);
        }else{
            list.add(productCollection);
        }

        if(packageEndDate.compareTo(hlsCusAbsProduct.getFirstRentalBackDate())==1) {
            switch (hlsCusAbsProduct.getCashPeriod()) {
                case MONTH:
                    list = calcuCollectionInfo(request, hlsCusAbsProduct, 1, list);
                    break;
                case QUARTER:
                    list = calcuCollectionInfo(request, hlsCusAbsProduct, 3, list);
                    break;
                case HALF_A_YEAR:
                    list = calcuCollectionInfo(request, hlsCusAbsProduct, 6, list);
                    break;
                case YEAR:
                    list = calcuCollectionInfo(request, hlsCusAbsProduct, 12, list);
                    break;
                default:
                    logger.error("兑付周期 {} 不合法", hlsCusAbsProduct.getCashPeriod());
                    throw new HlsCusException("兑付周期不合法");
            }
        }
        hlsCusAbsProFeeInfoService.updateProductFeeAmount(hlsCusAbsProduct.getProductId());

       return  hlsCusAbsProduct;*/
    }
    /**
     * 计算每期归集信息
     */
    private List<HlsCusAbsProductCollection> calcuCollectionInfo(IRequest request, HlsCusAbsProduct hlsCusAbsProduct, long times, List<HlsCusAbsProductCollection> list) throws HlsCusException{


        Long startTimes=list.get(0).getTimes()+1;
        int start=startTimes.intValue();
        Boolean flag=true;
        while(true) {
            HlsCusAbsProductCollection productCollection = new HlsCusAbsProductCollection();
            LocalDate nextMonth = format(list.get(Integer.valueOf(String.valueOf(startTimes)) - start).getRentalBackDate()).plusMonths(times);
            LocalDate nextCashMonth = format(list.get(Integer.valueOf(String.valueOf(startTimes)) - start).getCashDate()).plusMonths(times);
            LocalDate thisTimeRentalBackDate, beforeTimeRentalBackDate = format(list.get(Integer.valueOf(String.valueOf(startTimes)) - start).getRentalBackDate());
            if (nextMonth.getMonthValue() == 2 && hlsCusAbsProduct.getRentalBackDateT() > 28) {
                thisTimeRentalBackDate = nextMonth.with(TemporalAdjusters.lastDayOfMonth());
            } else {
                thisTimeRentalBackDate = nextMonth.withDayOfMonth(Integer.valueOf(String.valueOf(hlsCusAbsProduct.getRentalBackDateT())));
            }
            if(format(thisTimeRentalBackDate).compareTo(hlsCusAbsProduct.getPackageEndDate())>=0){
                flag=false;
            }
            LocalDate thisTimecashDate;
            if (nextMonth.getMonthValue() == 2 && hlsCusAbsProduct.getCashDateT() > 28) {
                thisTimecashDate = nextCashMonth.with(TemporalAdjusters.lastDayOfMonth());
            } else {
                thisTimecashDate = nextCashMonth.withDayOfMonth(Integer.valueOf(String.valueOf(hlsCusAbsProduct.getCashDateT())));
            }
            productCollection.setCollectionCfItem(HlsCusConstant.CASHFLOW_ITEM.COLLECTION_CF_ITEM);
            productCollection.setCollectionCfType(HlsCusConstant.CASHFLOW_ITEM.COLLECTION_CF_TYPE);
            productCollection.setRemittanceCfItem(HlsCusConstant.CASHFLOW_ITEM.REMITTANCE_CF_ITEM);
            productCollection.setRemittanceCfType(HlsCusConstant.CASHFLOW_ITEM.COLLECTION_CF_TYPE);
            productCollection.setProductId(hlsCusAbsProduct.getProductId());
            productCollection.setTimes(startTimes);
            productCollection.setCashDate(format(thisTimecashDate));
            productCollection.setCollectionDate(format(thisTimeRentalBackDate.plusDays(hlsCusAbsProduct.getCollectionDateRule())));
            productCollection.setRemittanceDate(format(thisTimeRentalBackDate.plusDays(hlsCusAbsProduct.getRemittanceDateRule())));
            productCollection.setReportDate(format(thisTimeRentalBackDate.plusDays(hlsCusAbsProduct.getReportDateRule())));
            // 本金
            BigDecimal principal = hlsCusAbsProductMapper.queryPrincipal(beforeTimeRentalBackDate.plusDays(1).toString(), thisTimeRentalBackDate.toString(), hlsCusAbsProduct.getProductId());
            // 利息
            BigDecimal interest = hlsCusAbsProductMapper.queryInterest2(beforeTimeRentalBackDate.plusDays(1).toString(), thisTimeRentalBackDate.toString(), hlsCusAbsProduct.getProductId());
            //提前结清
            BigDecimal finishAmount = hlsCusAbsProductMapper. selectOtherFinishAmount(beforeTimeRentalBackDate.plusDays(1).toString(), thisTimeRentalBackDate.toString(), hlsCusAbsProduct.getProductId());

            productCollection.setPrincipal(principal);
            productCollection.setInterest(interest);
            productCollection.setDueAmount(principal.add(interest).add(finishAmount));
            productCollection.setCollectionAmount(productCollection.getDueAmount());
            productCollection.setCashAmount(productCollection.getDueAmount());
            productCollection.setRemittanceAmount(productCollection.getCollectionAmount());
            productCollection.setRentalBackDate(format(thisTimeRentalBackDate));
            productCollection.set__status(DTOStatus.ADD);
            productCollection.setDataClass(HlsCusConstant.DATA_CLASS.NORMAL);
            productCollection.setInterestPeriodDays(thisTimecashDate.toEpochDay()
                    -format(list.get(Integer.valueOf(String.valueOf(startTimes)) - start).getCashDate()).toEpochDay());
            calculateFee(request,productCollection,hlsCusAbsProduct);
            startTimes++;
            list.add(productCollection);
            hlsCusAbsProductCollectionService.insertSelective(request, productCollection);
            calculateCashDeatil(request, productCollection, hlsCusAbsProduct);
            if (!flag) {
                break;
            }
        }
        return list;
    }


    /**
     * 计算各个兑付费用
     *
     * @param request
     * @param collection
     * @param hlsCusAbsProduct
     */
    private void calculateFee(IRequest request, HlsCusAbsProductCollection collection, HlsCusAbsProduct hlsCusAbsProduct) {
        /**
         * 兑付总本金
         */
        BigDecimal cashPrincipalSum = hlsCusAbsProductCashDetailService.selectCashPrincipalSum(hlsCusAbsProduct.getProductId(), collection.getTimes());

        BigDecimal retainPrincipalSum = hlsCusAbsProduct.getIssueAmount().subtract(cashPrincipalSum);

        HlsCusAbsProFeeInfo proFeeInfo = new HlsCusAbsProFeeInfo();
        proFeeInfo.setSourceKey(collection.getProductId());
        proFeeInfo.setSourceType(HlsCusConstant.ABS_PRO_TYPE.PRODUCT);
        proFeeInfo.setCalculateFlag(HlsCusConstant.FLAG.Y);
        List<HlsCusAbsProFeeInfo> absProFeeInfos = hlsCusAbsProFeeInfoService.selectProjectFeeInfo(request, proFeeInfo, 1, 999);
        //每种参与计算的费用类型只会存在一行
        for (HlsCusAbsProFeeInfo feeInfo : absProFeeInfos) {
            BigDecimal feeAmount = retainPrincipalSum.multiply(feeInfo.getFeeRate())
                    .multiply(new BigDecimal(collection.getInterestPeriodDays().toString()))
                    .divide(new BigDecimal(hlsCusAbsProduct.getInterestStandardDays()), 2, BigDecimal.ROUND_HALF_UP);
            switch (feeInfo.getFeeName()){
                //增值税
                case HlsCusConstant.ABS_FEE_TYPE.VAT:
                    BigDecimal taxAmount= (collection.getInterest().multiply(feeInfo.getFeeRate()).multiply(BigDecimal.ONE.add(feeInfo.getAddTaxRate()))).
                            divide(BigDecimal.ONE.add(feeInfo.getFeeRate()),2,BigDecimal.ROUND_HALF_UP);
                    collection.setTaxFee(taxAmount);
                    break;
                //服务费
                case HlsCusConstant.ABS_FEE_TYPE.SERVICE_FEE:
                    collection.setServiceFee(feeAmount);
                    break;
                //托管费
                case HlsCusConstant.ABS_FEE_TYPE.HOSTING_FEE:
                    collection.setHostingFee(feeAmount);
                    break;
                //管理费
                case HlsCusConstant.ABS_FEE_TYPE.MANAGEMENT_FEE:
                    collection.setManagementFee(feeAmount);
                    break;
                //承销费
                case HlsCusConstant.ABS_FEE_TYPE.UNDERWRITING_FEE:
                    collection.setUnderwritingFee(feeAmount);
                    break;
                //评级费
                case HlsCusConstant.ABS_FEE_TYPE.RATING_FEE:
                    collection.setRatingFee(feeAmount);
                    break;
                //评估费
                case HlsCusConstant.ABS_FEE_TYPE.EVALUATION_FEE:
                    collection.setEvaluationFee(feeAmount);
                    break;
                //挂牌费
                case HlsCusConstant.ABS_FEE_TYPE.LISTING_FEE:
                    collection.setListingFee(feeAmount);
                    break;
                //律师费
                case HlsCusConstant.ABS_FEE_TYPE.LAWYER_FEE:
                    collection.setLawyerFee(feeAmount);
                    break;
                //会计师费
                case HlsCusConstant.ABS_FEE_TYPE.ACCOUNTANT_FEE:
                    collection.setAccountantFee(feeAmount);
                    break;
                //其他
                case HlsCusConstant.ABS_FEE_TYPE.OTHER:
                    collection.setOtherFee(feeAmount);
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * 计算兑付本金利息详情
     *
     * @param request
     * @param collection
     * @param hlsCusAbsProduct
     */
    private void calculateCashDeatil(IRequest request, HlsCusAbsProductCollection collection, HlsCusAbsProduct hlsCusAbsProduct) throws HlsCusException {

        HlsCusAbsProductStructure productStructure = new HlsCusAbsProductStructure();
        productStructure.setProductId(hlsCusAbsProduct.getProductId());
        List<HlsCusAbsProductStructure> productStructures = hlsCusAbsProductStructureService.selectProductStructureData(request, productStructure, 1, 999);
        //每个层次的利息总和
        BigDecimal cashInterestSum = hlsCusAbsProductStructureService.selectStructureCashInterestSum(hlsCusAbsProduct.getProductId(), collection.getInterestPeriodDays());
        //当期兑付各个费用总和
        BigDecimal cashFeeSum = hlsCusAbsProductCollectionService.selectCashFeeSum(collection.getCollectionId());
        //当期可以兑付的金额
        BigDecimal cashDueAmount = collection.getDueAmount().subtract(cashInterestSum).subtract(cashFeeSum);

        if (cashDueAmount.compareTo(new BigDecimal(0)) == -1) {
            throw new HlsCusException("第" + collection.getTimes() + "期归集租金不足");
        }

        for (HlsCusAbsProductStructure structure : productStructures) {
            HlsCusAbsProductCashDetail cashDetail = new HlsCusAbsProductCashDetail();
            cashDetail.setProductId(hlsCusAbsProduct.getProductId());
            cashDetail.setCollectionId(collection.getCollectionId());
            cashDetail.setStructureId(structure.getStructureId());
            if(structure.getSubscribeAmount().compareTo(structure.getPublishAmount())==1){
                throw new HlsCusException("认购金额超出分层发行金额");
            }
            BigDecimal interest=(structure.getPublishAmount().subtract(structure.getCashPrincipal()).subtract(structure.getSubscribeAmount())).multiply(structure.getPredictRate())
                    .multiply(new BigDecimal(collection.getInterestPeriodDays().toString()))
                    .divide(new BigDecimal(hlsCusAbsProduct.getInterestStandardDays().toString()), 2, BigDecimal.ROUND_HALF_UP);
            cashDetail.setPlanCashInterest(interest);
            HlsCusAbsProductStructure cashStructure = hlsCusAbsProductStructureService.selectCashStructure(request, structure.getStructureId(), "NORMAL", collection.getTimes());
            //判断当期可以用来兑付的本金金额 是否大于 该层级的剩余本金金额
            if(cashDueAmount.compareTo(structure.getPublishAmount().subtract(structure.getSubscribeAmount()).subtract(cashStructure.getCashPrincipal()))==1){
                cashDetail.setPlanCashPrincipal(structure.getPublishAmount().subtract(structure.getSubscribeAmount()).subtract(cashStructure.getCashPrincipal()));
            }else{
                cashDetail.setPlanCashPrincipal(cashDueAmount);
            }
            cashDetail.setCreatedBy(request.getUserId());
            cashDetail.setLastUpdatedBy(request.getUserId());
            hlsCusAbsProductCashDetailService.insertSelective(request, cashDetail);
            cashDueAmount = cashDueAmount.subtract(cashDetail.getPlanCashPrincipal());
        }
    }

    /*@Override
    public HlsCusAbsProduct calcCashProduct(IRequest request, HlsCusAbsPkg pkg) throws HlsCusException {
        HlsCusAbsProduct hlsCusAbsProduct = self().updateProduct(request, pkg);
        reCalcCashProduct(request, hlsCusAbsProduct, HlsCusConstant.DATA_CLASS.NORMAL);
        return hlsCusAbsProduct;
    }*/

    @Override
    public HlsCusAbsProduct reCalcCashProduct(IRequest request, HlsCusAbsProduct hlsCusAbsProduct, String dataClass) {

        HlsCusAbsProductCollection collection = new HlsCusAbsProductCollection();
        collection.setProductId(hlsCusAbsProduct.getProductId());
        collection.setDataClass(dataClass);
        List<HlsCusAbsProductCollection> productCollections = hlsCusAbsProductCollectionService.selectProductCollectionData(request, collection, 1, 999);

        for(HlsCusAbsProductCollection productCollection:productCollections){
            //归集各项费用更新 费用改为可编辑 所以注释不用系统计算
            //calculateFee(request,productCollection,hlsCusAbsProduct);
            hlsCusAbsProductCollectionService.updateByPrimaryKeySelective(request,productCollection);

            HlsCusAbsProductCashDetail absProductCashDetail = new HlsCusAbsProductCashDetail();
            absProductCashDetail.setCollectionId(productCollection.getCollectionId());
            List<HlsCusAbsProductCashDetail> productCashDetails = hlsCusAbsProductCashDetailService.selectProductCashDetailData(request, absProductCashDetail, 1, 999);

            //利息更新
            for (HlsCusAbsProductCashDetail cashDetail : productCashDetails) {
                HlsCusAbsProductStructure productStructure = hlsCusAbsProductStructureService.selectCashStructure(request, cashDetail.getStructureId(), dataClass, productCollection.getTimes());
                BigDecimal interest = (productStructure.getPublishAmount().subtract(productStructure.getCashPrincipal())).multiply(productStructure.getPredictRate())
                        .multiply(new BigDecimal(productCollection.getInterestPeriodDays().toString()))
                        .divide(new BigDecimal(hlsCusAbsProduct.getInterestStandardDays().toString()), 2, BigDecimal.ROUND_HALF_UP);
                cashDetail.setPlanCashInterest(interest);
                hlsCusAbsProductCashDetailService.updateByPrimaryKeySelective(request, cashDetail);
            }
        }
        //更新费用金额
        hlsCusAbsProFeeInfoService.updateProductFeeAmount(hlsCusAbsProduct.getProductId());
        return hlsCusAbsProduct;
    }

    @Override
    public HlsCusAbsProduct submitProduct(IRequest request, HlsCusAbsPkg pkg)  throws HlsCusException{
        if (HlsCusConstant.DATA_CLASS.NORMAL.equalsIgnoreCase(pkg.getHlsCusAbsProduct().getDataClass())) {
            pkg.getHlsCusAbsProduct().setProductStatus(HlsCusConstant.WORKFLOW_STATUS.APPROVING);
        }
        HlsCusAbsProduct absProduct=self().updateProduct(request, pkg);
        databaseLockProvider.lock(absProduct);
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(request.getUserId());
        if (ObjectUtils.isEmpty(employee)) {
            throw new HlsCusException("获取提交人失败");
        }
        String employeeCode = employee.getEmployeeCode();
        request.setEmployeeCode(employeeCode);

        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        if (HlsCusConstant.DATA_CLASS.NORMAL.equalsIgnoreCase(absProduct.getDataClass())) {
            if (HlsCusConstant.ABS_BUSINESS_TYPE.ABS.equals(absProduct.getBusinessType()) || HlsCusConstant.ABS_BUSINESS_TYPE.ABN.equals(absProduct.getBusinessType())) {
                params.put(HlsCusConstant.WORKFLOW_PARAMS.WORKFLOW_TYPE, HlsCusConstant.ABS_WFL.ABS_ABN_WFL);
            } else {
                params.put(HlsCusConstant.WORKFLOW_PARAMS.WORKFLOW_TYPE, HlsCusConstant.ABS_WFL.OTHER_DEBT_WFL);
            }
        } else {
            if (HlsCusConstant.ABS_BUSINESS_TYPE.ABS.equals(absProduct.getBusinessType()) || HlsCusConstant.ABS_BUSINESS_TYPE.ABN.equals(absProduct.getBusinessType())) {
                params.put(HlsCusConstant.WORKFLOW_PARAMS.WORKFLOW_TYPE, HlsCusConstant.ABS_WFL.ABS_ABN_CHANGE_WFL);
            } else {
                params.put(HlsCusConstant.WORKFLOW_PARAMS.WORKFLOW_TYPE, HlsCusConstant.ABS_WFL.OTHER_DEBT_CHANGE_WFL);
            }
            //变更
            HlsCusChangeReqInfo cusChangeReqInfo = new HlsCusChangeReqInfo();
            cusChangeReqInfo.setChangeReqId(absProduct.getChangeReqId());
            cusChangeReqInfo.setStatus(HlsCusConstant.WORKFLOW_STATUS.APPROVING);
            hlsCusChangeReqInfoService.updateByPrimaryKeySelective(request, cusChangeReqInfo);
        }
        List<HlsCusAbsProduct> productList = new ArrayList<>();
        productList.add(absProduct);
        activitiStartService.start(request, productList, params);

        return absProduct;
    }

    /**
     * 新增产品
     */
    private HlsCusAbsProduct insertProduct(IRequest request, HlsCusAbsProduct hlsCusAbsProduct) {
        if (hlsCusAbsProduct.getProjectId() == null) {
            logger.error("创建ABS需要关联ABS立项,未找到 project_id.");
            return null;
        }
        HlsCusAbsProject hlsCusAbsProject = new HlsCusAbsProject();
        hlsCusAbsProject.setProjectId(hlsCusAbsProduct.getProjectId());
        if (null == (hlsCusAbsProject = hlsCusAbsProjectService.selectByPrimaryKey(request, hlsCusAbsProject))) {
            logger.error("未找到 project_id 为 {} 的ABS立项.", hlsCusAbsProject.getProjectId());
            return null;
        }
        hlsCusAbsProduct.setBusinessType(hlsCusAbsProject.getBusinessType());
        hlsCusAbsProduct.setProductDate(hlsCusAbsProject.getProjectDate());
        hlsCusAbsProduct.setPayInterestFrequency(hlsCusAbsProject.getPayInterestFrequency());
        hlsCusAbsProduct.setCompanyId(request.getCompanyId());
        hlsCusAbsProduct.setBaseDate(hlsCusAbsProject.getBaseDate());
        hlsCusAbsProduct.setUserId(request.getUserId());
        hlsCusAbsProduct.setUnitId(Long.valueOf(request.getAttribute("unitId")));
        //Map<String, String> params = new HashMap<>();
        //hlsCusAbsProduct.setProductNumber(fndCodingRuleValuesService.getCodeRuleValue(request, HlsCusConstant.ABS_PRO_TYPE.PRODUCT, HlsCusConstant.ABS_PRO_TYPE.PRODUCT, HlsCusConstant.ABS_PRO_TYPE.PRODUCT, params));
        hlsCusAbsProduct.setProductNumber(hlsCusAbsProject.getProjectNumber()+hlsCusAbsProductMapper.selectProductNumberMax(hlsCusAbsProject.getProjectId()));
        hlsCusAbsProduct.setPackId(hlsCusAbsProject.getPackId());
        //hlsCusAbsProduct.setTableFlag(HlsCusConstant.FLAG.N);
        hlsCusAbsProduct.setIsIrregular(HlsCusConstant.FLAG.N);
        hlsCusAbsProduct.setBuybackStatus(HlsCusConstant.WORKFLOW_STATUS.NEW);
        if(!HlsCusConstant.ABS_BUSINESS_TYPE.ABS.equals(hlsCusAbsProduct.getBusinessType())&&!HlsCusConstant.ABS_BUSINESS_TYPE.ABN.equals(hlsCusAbsProduct.getBusinessType())) {
            hlsCusAbsProduct.setSubscribeFlag(HlsCusConstant.FLAG.N);
        }
        hlsCusAbsProduct =  self().insertSelective(request, hlsCusAbsProduct);

        // 复制行表数据
        insertLineData(request, hlsCusAbsProduct);

        // 消息通知
        String url = "/abs/ABS110A/abs_product_detail.view?productId=" + hlsCusAbsProduct.getProductId();
        Map<String, Object> paramsEvent = new HashMap<>();
        paramsEvent.put("message", "发债产品创建");
        paramsEvent.put("noticeTitle", "发债产品创建");
        paramsEvent.put("noticeType", "NOTICE");
        paramsEvent.put("url", url);
        paramsEvent.put("level", 1L);
        sysEventService.eventSave(request, hlsCusAbsProduct.getProductId(), HlsCusConstant.ABS_PRO_TYPE.PRODUCT, HlsCusConstant.ABS_PRO_TYPE.PRODUCT,
                "ABS", HlsCusConstant.ABS_PRO_TYPE.PRODUCT, "P2D", paramsEvent);

        return hlsCusAbsProduct;
    }

    /**
     * 新增产品时复制行表信息
     */
    private void insertLineData(IRequest request, HlsCusAbsProduct hlsCusAbsProduct) {
        /**
         * 账户信息复制
         */
        HlsCusAbsBankAccount bankAccount = new HlsCusAbsBankAccount();
        bankAccount.setSourceKey(hlsCusAbsProduct.getProjectId());
        bankAccount.setSourceType(HlsCusConstant.ABS_PRO_TYPE.PROJECT);
        List<HlsCusAbsBankAccount> bankAccountList = hlsCusAbsBankAccountService.select(request, bankAccount, 1, 9999);
        for (HlsCusAbsBankAccount account : bankAccountList) {
            account.setAbsBankAccountId(null);
            account.setSourceType(HlsCusConstant.ABS_PRO_TYPE.PRODUCT);
            account.setSourceKey(hlsCusAbsProduct.getProductId());
            account.setCreatedBy(request.getUserId());
            account.setLastUpdatedBy(request.getUserId());
            account.setCreationDate(new Date());
            account.setLastUpdateDate(new Date());
            hlsCusAbsBankAccountService.insertSelective(request, account);
        }

        /**
         * 费用信息
         */
        HlsCusAbsProFeeInfo proFeeInfo = new HlsCusAbsProFeeInfo();
        proFeeInfo.setSourceKey(hlsCusAbsProduct.getProjectId());
        proFeeInfo.setSourceType(HlsCusConstant.ABS_PRO_TYPE.PROJECT);
        List<HlsCusAbsProFeeInfo> proFeeInfoList = hlsCusAbsProFeeInfoService.select(request, proFeeInfo, 1, 9999);
        for (HlsCusAbsProFeeInfo feeInfo : proFeeInfoList) {
            feeInfo.setSourceInfoId(feeInfo.getFeeInfoId());
            feeInfo.setFeeInfoId(null);
            feeInfo.setSourceType(HlsCusConstant.ABS_PRO_TYPE.PRODUCT);
            feeInfo.setSourceKey(hlsCusAbsProduct.getProductId());
            feeInfo.setCreatedBy(request.getUserId());
            feeInfo.setLastUpdatedBy(request.getUserId());
            feeInfo.setCreationDate(new Date());
            feeInfo.setLastUpdateDate(new Date());
            hlsCusAbsProFeeInfoService.insertSelective(request, feeInfo);
        }
        /**
         * 中介机构信息
         */
        HlsCusAbsProjectOrganization hlsCusAbsProjectOrganization = new HlsCusAbsProjectOrganization();
        hlsCusAbsProjectOrganization.setProjectId(hlsCusAbsProduct.getProjectId());
        List<HlsCusAbsProjectOrganization> projectOrganizationList = hlsCusAbsProjectOrganizationService.select(request, hlsCusAbsProjectOrganization, 1, 9999);
        if (CollectionUtils.isNotEmpty(projectOrganizationList)) {
            for (HlsCusAbsProjectOrganization cusAbsProjectOrganization : projectOrganizationList) {
                HlsCusAbsProductOrganization hlsCusAbsProductOrganization = new HlsCusAbsProductOrganization();
                hlsCusAbsProductOrganization.setProductId(hlsCusAbsProduct.getProductId());
                hlsCusAbsProductOrganization.setOrganizationType(cusAbsProjectOrganization.getOrganizationType());
                hlsCusAbsProductOrganization.setBpId(cusAbsProjectOrganization.getBpId());
                hlsCusAbsProductOrganization.setTakeLead(cusAbsProjectOrganization.getTakeLead());
                hlsCusAbsProductOrganization.setBankAccountId(cusAbsProjectOrganization.getBankAccountId());
                hlsCusAbsProductOrganization.setBankAccountType(cusAbsProjectOrganization.getBankAccountType());
                hlsCusAbsProductOrganization.setContactPerson(cusAbsProjectOrganization.getContactPerson());
                hlsCusAbsProductOrganization.setContactPhone(cusAbsProjectOrganization.getContactPhone());
                hlsCusAbsProductOrganization.setReceiverFlag(cusAbsProjectOrganization.getReceiverFlag());
                hlsCusAbsProductOrganization.setOrganizationName(cusAbsProjectOrganization.getOrganizationName());
                hlsCusAbsProductOrganization.setBankAccountName(cusAbsProjectOrganization.getBankAccountName());
                hlsCusAbsProductOrganization.setBankAccountNum(cusAbsProjectOrganization.getBankAccountNum());
                hlsCusAbsProductOrganization.setBankBranchName(cusAbsProjectOrganization.getBankBranchName());
                hlsCusAbsProductOrganization.setDescription(cusAbsProjectOrganization.getDescription());
                hlsCusAbsProductOrganization.set__status(DTOStatus.ADD);
                hlsCusAbsProductOrganizationService.insertSelective(request, hlsCusAbsProductOrganization);
                hlsCusAbsProFeeInfoService.updateFeeOrganizationId(hlsCusAbsProduct.getProductId(), cusAbsProjectOrganization.getOrganizationId(), hlsCusAbsProductOrganization.getOrganizationId());
            }
        }
    }

    @Override
    public void excelImport(IRequest request, Long headerId, final Long key) {
        FndInterfaceLines lines = new FndInterfaceLines();
        lines.setHeaderId(headerId);
        List<FndInterfaceLines> linesList = linesMapper.select(lines);
        Map<String, String> map = new HashMap<>();
        map.put("times", "期数");
        map.put("rentalBackDate", "租金回收计算日");
        map.put("cashDate", "兑付日");
        map.put("collectionDate", "归集日");
        map.put("remittanceDate", "转付日");
        map.put("reportDate", "报告日");
        map.put("dueAmount", "应归集金额");
        List<HlsCusAbsProductCollection> collectionList = HlsCusExcelUtils.excelImportFromInterface(linesList, HlsCusAbsProductCollection.class, map);
        if (CollectionUtils.isNotEmpty(collectionList)) {
            collectionList.forEach(collection -> {
                collection.setProductId(key);
                collection.set__status(DTOStatus.ADD);
            });
            hlsCusAbsProductCollectionService.batchUpdate(request, collectionList);
        }

    }



    private void deleteLineData(IRequest request, String changeType, String businessType, Long productId){
        if(!HlsCusConstant.ABS_BUSINESS_TYPE.ABS.equals(businessType)&&!HlsCusConstant.ABS_BUSINESS_TYPE.ABN.equals(businessType)) {
            HlsCusAbsProductQuotation absProductQuotation = new HlsCusAbsProductQuotation();
            absProductQuotation.setProductId(productId);
            List<HlsCusAbsProductQuotation> absProductQuotations = productQuotationService.select(request, absProductQuotation, 1, 1);
            if (CollectionUtils.isNotEmpty(absProductQuotations)) {
                productQuotationService.batchDelete(absProductQuotations);
            }

            HlsCusAbsProductRepayment hlsCusAbsProductRepayment = new HlsCusAbsProductRepayment();
            hlsCusAbsProductRepayment.setProductId(productId);
            hlsCusAbsProductRepayment.setConfirmFlag(HlsCusConstant.FLAG.N);
            hlsCusAbsProductRepayment.setWriteOffFlag(HlsCusConstant.FCT_WRITE_OFF_FLAG.NOT);
            productRepaymentMapper.delete(hlsCusAbsProductRepayment);
        } else {


            //归集转付
            HlsCusAbsProductCollection productCollection = new HlsCusAbsProductCollection();
            productCollection.setProductId(productId);
            productCollection.setConfirmFlag(HlsCusConstant.FLAG.N);
            productCollection.setDataClass(HlsCusConstant.DATA_CLASS.NORMAL);
            List<HlsCusAbsProductCollection> productCollections = hlsCusAbsProductCollectionService.select(request, productCollection, 1, 999);
            if (CollectionUtils.isNotEmpty(productCollections)) {
                hlsCusAbsProductCollectionService.batchDelete(productCollections);
            }

            if(HlsCusConstant.ABS_CHANGE_TYPE.PRODUCT.equals(changeType)) {
                // 资金用途
                HlsCusAbsProductPurpose absProductPurpose = new HlsCusAbsProductPurpose();
                absProductPurpose.setProductId(productId);
                List<HlsCusAbsProductPurpose> absProductPurposes = hlsCusAbsProductPurposeService.select(request, absProductPurpose, 1, 999);
                if (CollectionUtils.isNotEmpty(absProductPurposes)) {
                    hlsCusAbsProductPurposeService.batchDelete(absProductPurposes);
                }


                // 附件
                HlsCusPrjProjectAttachment prjProjectAttachment = new HlsCusPrjProjectAttachment();
                prjProjectAttachment.setProjectId(productId);
                prjProjectAttachment.setProjectAttachmentCategory(HlsCusConstant.ABS_PRO_TYPE.PRODUCT);
                List<HlsCusPrjProjectAttachment> prjProjectAttachments = hlsCusPrjProjectAttachmentService.select(request, prjProjectAttachment, 1, 999);
                if (CollectionUtils.isNotEmpty(prjProjectAttachments)) {
                    hlsCusPrjProjectAttachmentService.batchDelete(prjProjectAttachments);
                }
            }
        }
    }


    /**
     * 复制sourceProductId的所有的行信息至targetProductId
     *
     * @param request
     * @param changeType
     * @param product
     * @param targetProductId
     */
    private void cloneLineData(IRequest request, String changeType, HlsCusAbsProduct product, Long targetProductId) {

        Long sourceProductId=product.getProductId();

        // 复制中介机构信息
        HlsCusAbsProductOrganization absProductOrganization = new HlsCusAbsProductOrganization();
        absProductOrganization.setProductId(sourceProductId);
        List<HlsCusAbsProductOrganization> absProductOrganizations = hlsCusAbsProductOrganizationService.select(request, absProductOrganization, 1, 999);
        if (CollectionUtils.isNotEmpty(absProductOrganizations)) {
            for (HlsCusAbsProductOrganization organization : absProductOrganizations) {
                Long normalOrganizationId=organization.getOrganizationId();
                organization.setRefOrganizationId(normalOrganizationId);
                organization.setOrganizationId(null);
                organization.setProductId(targetProductId);
                organization.set__status(DTOStatus.ADD);
                organization.setCreationDate(null);
                organization.setLastUpdateDate(null);
                organization.setCreatedBy(request.getUserId());
                organization.setLastUpdatedBy(request.getUserId());
                hlsCusAbsProductOrganizationService.insertSelective(request,organization);

                // 复制费用信息
                HlsCusAbsProFeeInfo absProFeeInfo = new HlsCusAbsProFeeInfo();
                absProFeeInfo.setSourceKey(sourceProductId);
                absProFeeInfo.setSourceType("ABS_PRODUCT");
                absProFeeInfo.setOrganizationId(normalOrganizationId);
                List<HlsCusAbsProFeeInfo> absProFeeInfos = hlsCusAbsProFeeInfoService.select(request, absProFeeInfo, 1, 999);
                if (CollectionUtils.isNotEmpty(absProFeeInfos)) {
                    for (HlsCusAbsProFeeInfo proFeeInfo : absProFeeInfos) {
                        proFeeInfo.setRefFeeInfoId(proFeeInfo.getFeeInfoId());
                        proFeeInfo.setOrganizationId(organization.getOrganizationId());
                        proFeeInfo.setFeeInfoId(null);
                        proFeeInfo.setSourceKey(targetProductId);
                        proFeeInfo.set__status(DTOStatus.ADD);
                        proFeeInfo.setCreationDate(null);
                        proFeeInfo.setLastUpdateDate(null);
                        proFeeInfo.setCreatedBy(request.getUserId());
                        proFeeInfo.setLastUpdatedBy(request.getUserId());
                    }
                    hlsCusAbsProFeeInfoService.batchUpdate(request, absProFeeInfos);
                }
            }
        }


        if(!HlsCusConstant.ABS_BUSINESS_TYPE.ABS.equals(product.getBusinessType())&&!HlsCusConstant.ABS_BUSINESS_TYPE.ABN.equals(product.getBusinessType())) {
            // 复制报价
            HlsCusAbsProductQuotation absProductQuotation = new HlsCusAbsProductQuotation();
            absProductQuotation.setProductId(sourceProductId);
            List<HlsCusAbsProductQuotation> absProductQuotations = productQuotationService.select(request, absProductQuotation, 1, 1);
            if (CollectionUtils.isNotEmpty(absProductQuotations)) {
                for (HlsCusAbsProductQuotation quotation : absProductQuotations) {
                    quotation.setQuotationId(null);
                    quotation.setProductId(targetProductId);
                    quotation.set__status(DTOStatus.ADD);
                    quotation.setCreationDate(null);
                    quotation.setLastUpdateDate(null);
                    quotation.setCreatedBy(request.getUserId());
                    quotation.setLastUpdatedBy(request.getUserId());
                }
                productQuotationService.batchUpdate(request, absProductQuotations);
            }

            HlsCusAbsProductRepayment hlsCusAbsProductRepayment = new HlsCusAbsProductRepayment();
            hlsCusAbsProductRepayment.setProductId(sourceProductId);
            List<HlsCusAbsProductRepayment> absProductRepayments = productRepaymentService.select(request, hlsCusAbsProductRepayment, 1, 999);
            if (CollectionUtils.isNotEmpty(absProductRepayments)) {
                for (HlsCusAbsProductRepayment repayment : absProductRepayments) {
                    repayment.setRepaymentId(null);
                    repayment.setProductId(targetProductId);
                    repayment.set__status(DTOStatus.ADD);
                    repayment.setCreationDate(null);
                    repayment.setLastUpdateDate(null);
                    repayment.setCreatedBy(request.getUserId());
                    repayment.setLastUpdatedBy(request.getUserId());
                }
                productRepaymentService.batchUpdate(request, absProductRepayments);
            }


        } else {

            // 复制账户
            HlsCusAbsBankAccount absBankAccount = new HlsCusAbsBankAccount();
            absBankAccount.setSourceKey(sourceProductId);
            absBankAccount.setSourceType("ABS_PRODUCT");
            List<HlsCusAbsBankAccount> absBankAccounts = hlsCusAbsBankAccountService.select(request, absBankAccount, 1, 999);
            if (CollectionUtils.isNotEmpty(absBankAccounts)) {
                for (HlsCusAbsBankAccount account : absBankAccounts) {
                    account.setRefAbsBankAccountId(account.getAbsBankAccountId());
                    account.setAbsBankAccountId(null);
                    account.setSourceKey(targetProductId);
                    account.set__status(DTOStatus.ADD);
                    account.setCreationDate(null);
                    account.setLastUpdateDate(null);
                    account.setCreatedBy(request.getUserId());
                    account.setLastUpdatedBy(request.getUserId());
                }
                hlsCusAbsBankAccountService.batchUpdate(request, absBankAccounts);
            }

            // 复制分层机构
            HlsCusAbsProductStructure absProductStructure = new HlsCusAbsProductStructure();
            absProductStructure.setProductId(sourceProductId);
            List<HlsCusAbsProductStructure> absProductStructures = hlsCusAbsProductStructureService.select(request, absProductStructure, 1, 999);
            if (CollectionUtils.isNotEmpty(absProductStructures)) {
                for (HlsCusAbsProductStructure productStructure : absProductStructures) {
                    productStructure.setRefStructureId(productStructure.getStructureId());
                    productStructure.setStructureId(null);
                    productStructure.setProductId(targetProductId);
                    productStructure.set__status(DTOStatus.ADD);
                    productStructure.setCreationDate(null);
                    productStructure.setLastUpdateDate(null);
                    productStructure.setCreatedBy(request.getUserId());
                    productStructure.setLastUpdatedBy(request.getUserId());
                }
                hlsCusAbsProductStructureService.batchUpdate(request, absProductStructures);

            }


            // 复制归集兑付计划
            HlsCusAbsProductCollection productCollection = new HlsCusAbsProductCollection();
            productCollection.setProductId(sourceProductId);
            productCollection.setDataClass(HlsCusConstant.DATA_CLASS.NORMAL);
            List<HlsCusAbsProductCollection> productCollections = hlsCusAbsProductCollectionService.select(request, productCollection, 1, 999);
            if (CollectionUtils.isNotEmpty(productCollections)) {
                for (HlsCusAbsProductCollection collection : productCollections) {
                    Long normalCollectionId=collection.getCollectionId();
                    collection.setCollectionId(null);
                    collection.setProductId(targetProductId);
                    collection.set__status(DTOStatus.ADD);
                    collection.setCreationDate(null);
                    collection.setLastUpdateDate(null);
                    collection.setCreatedBy(request.getUserId());
                    collection.setLastUpdatedBy(request.getUserId());
                    hlsCusAbsProductCollectionService.insertSelective(request,collection);

                    HlsCusAbsProductCashDetail cashDetail = new HlsCusAbsProductCashDetail();
                    cashDetail.setProductId(sourceProductId);
                    cashDetail.setCollectionId(normalCollectionId);
                    List<HlsCusAbsProductCashDetail> cashDetails = hlsCusAbsProductCashDetailService.select(request, cashDetail, 1, 999);
                    for (HlsCusAbsProductCashDetail detail : cashDetails) {
                        detail.setCashDetailId(null);
                        detail.setCollectionId(collection.getCollectionId());
                        detail.setProductId(collection.getProductId());

                        HlsCusAbsProductStructure structure = new HlsCusAbsProductStructure();
                        structure.setProductId(targetProductId);
                        structure.setRefStructureId(detail.getStructureId());
                        detail.setStructureId(hlsCusAbsProductStructureService.select(request, structure, 1, 1).get(0).getStructureId());

                        detail.setCreationDate(null);
                        detail.setLastUpdateDate(null);
                        detail.setCreatedBy(request.getUserId());
                        detail.setLastUpdatedBy(request.getUserId());

                        hlsCusAbsProductCashDetailService.insertSelective(request,detail);
                    }

                }
            }

            if(HlsCusConstant.ABS_CHANGE_TYPE.PRODUCT.equals(changeType)) {
                cloneChangeLineData(request, sourceProductId, targetProductId);
            }
        }
    }

    /**
     * 某些行信息 后续可能会引用ID 在变更的时候不可删除(界面做了控制)
     * 变更完成后 更新原始数据
     * @param request
     * @param businessType
     * @param sourceProductId
     * @param targetProductId
     */
    private void changeLineData(IRequest request, String changeType, String businessType, Long sourceProductId, Long targetProductId) {

        // 复制中介机构信息
        HlsCusAbsProductOrganization absProductOrganization = new HlsCusAbsProductOrganization();
        absProductOrganization.setProductId(sourceProductId);
        List<HlsCusAbsProductOrganization> absProductOrganizations = hlsCusAbsProductOrganizationService.select(request, absProductOrganization, 1, 999);
        if (CollectionUtils.isNotEmpty(absProductOrganizations)) {
            for (HlsCusAbsProductOrganization organization : absProductOrganizations) {
                Long organizationId=organization.getOrganizationId();
                organization.setProductId(targetProductId);
                if(organization.getRefOrganizationId()!=null) {
                    organization.setOrganizationId(organization.getRefOrganizationId());
                    organization.setRefOrganizationId(null);
                    organization.setObjectVersionNumber(null);
                    organization.set__status(DTOStatus.UPDATE);
                    hlsCusAbsProductOrganizationService.updateByPrimaryKey(request,organization);
                }else{
                    organization.setOrganizationId(null);
                    organization.set__status(DTOStatus.ADD);
                    organization.setObjectVersionNumber(1L);
                    organization.setCreationDate(null);
                    organization.setLastUpdateDate(null);
                    organization.setCreatedBy(request.getUserId());
                    organization.setLastUpdatedBy(request.getUserId());
                    hlsCusAbsProductOrganizationService.insertSelective(request,organization);
                }

                // 复制费用信息
                HlsCusAbsProFeeInfo absProFeeInfo = new HlsCusAbsProFeeInfo();
                absProFeeInfo.setSourceKey(sourceProductId);
                absProFeeInfo.setSourceType(HlsCusConstant.ABS_PRO_TYPE.PRODUCT);
                absProFeeInfo.setOrganizationId(organizationId);
                List<HlsCusAbsProFeeInfo> absProFeeInfos = hlsCusAbsProFeeInfoService.select(request, absProFeeInfo, 1, 999);
                if (CollectionUtils.isNotEmpty(absProFeeInfos)) {
                    for (HlsCusAbsProFeeInfo proFeeInfo : absProFeeInfos) {
                        proFeeInfo.setSourceKey(targetProductId);
                        proFeeInfo.setOrganizationId(organization.getOrganizationId());
                        if(proFeeInfo.getRefFeeInfoId()!=null) {
                            proFeeInfo.setFeeInfoId(proFeeInfo.getRefFeeInfoId());
                            proFeeInfo.setRefFeeInfoId(null);
                            proFeeInfo.setObjectVersionNumber(null);
                            proFeeInfo.set__status(DTOStatus.UPDATE);
                        }else {
                            proFeeInfo.setFeeInfoId(null);
                            proFeeInfo.set__status(DTOStatus.ADD);
                            proFeeInfo.setObjectVersionNumber(1L);
                            proFeeInfo.setCreationDate(null);
                            proFeeInfo.setLastUpdateDate(null);
                            proFeeInfo.setCreatedBy(request.getUserId());
                            proFeeInfo.setLastUpdatedBy(request.getUserId());
                        }
                    }
                    hlsCusAbsProFeeInfoService.batchUpdate(request, absProFeeInfos);
                }
            }
        }



        if (!HlsCusConstant.ABS_BUSINESS_TYPE.ABS.equals(businessType) && !HlsCusConstant.ABS_BUSINESS_TYPE.ABN.equals(businessType)) {
            // 复制报价
            HlsCusAbsProductQuotation absProductQuotation = new HlsCusAbsProductQuotation();
            absProductQuotation.setProductId(sourceProductId);
            List<HlsCusAbsProductQuotation> absProductQuotations = productQuotationService.select(request, absProductQuotation, 1, 1);
            if (CollectionUtils.isNotEmpty(absProductQuotations)) {
                for (HlsCusAbsProductQuotation quotation : absProductQuotations) {
                    quotation.setQuotationId(null);
                    quotation.setProductId(targetProductId);
                    quotation.set__status(DTOStatus.ADD);
                    quotation.setCreationDate(null);
                    quotation.setLastUpdateDate(null);
                    quotation.setCreatedBy(request.getUserId());
                    quotation.setLastUpdatedBy(request.getUserId());
                }
                productQuotationService.batchUpdate(request, absProductQuotations);
            }

            HlsCusAbsProductRepayment hlsCusAbsProductRepayment = new HlsCusAbsProductRepayment();
            hlsCusAbsProductRepayment.setProductId(sourceProductId);
            hlsCusAbsProductRepayment.setConfirmFlag("N");
            List<HlsCusAbsProductRepayment> absProductRepayments = productRepaymentService.select(request, hlsCusAbsProductRepayment, 1, 999);
            if (CollectionUtils.isNotEmpty(absProductRepayments)) {
                for (HlsCusAbsProductRepayment repayment : absProductRepayments) {
                    repayment.setRepaymentId(null);
                    repayment.setProductId(targetProductId);
                    repayment.set__status(DTOStatus.ADD);
                    repayment.setCreationDate(null);
                    repayment.setLastUpdateDate(null);
                    repayment.setCreatedBy(request.getUserId());
                    repayment.setLastUpdatedBy(request.getUserId());
                }
                productRepaymentService.batchUpdate(request, absProductRepayments);
            }


        } else {

            // 复制账户
            HlsCusAbsBankAccount absBankAccount = new HlsCusAbsBankAccount();
            absBankAccount.setSourceKey(sourceProductId);
            absBankAccount.setSourceType(HlsCusConstant.ABS_PRO_TYPE.PRODUCT);
            List<HlsCusAbsBankAccount> absBankAccounts = hlsCusAbsBankAccountService.select(request, absBankAccount, 1, 999);
            if (CollectionUtils.isNotEmpty(absBankAccounts)) {
                for (HlsCusAbsBankAccount account : absBankAccounts) {
                    account.setSourceKey(targetProductId);
                    if (account.getRefAbsBankAccountId() != null) {
                        account.setAbsBankAccountId(account.getRefAbsBankAccountId());
                        account.setRefAbsBankAccountId(null);
                        account.setObjectVersionNumber(null);
                        account.set__status(DTOStatus.UPDATE);
                    } else {
                        account.setAbsBankAccountId(null);
                        account.set__status(DTOStatus.ADD);
                        account.setObjectVersionNumber(1L);
                        account.setCreationDate(null);
                        account.setLastUpdateDate(null);
                        account.setCreatedBy(request.getUserId());
                        account.setLastUpdatedBy(request.getUserId());
                    }
                }
                hlsCusAbsBankAccountService.batchUpdate(request, absBankAccounts);
            }

            // 复制分层机构
            HlsCusAbsProductStructure absProductStructure = new HlsCusAbsProductStructure();
            absProductStructure.setProductId(sourceProductId);
            List<HlsCusAbsProductStructure> absProductStructures = hlsCusAbsProductStructureService.select(request, absProductStructure, 1, 999);
            if (CollectionUtils.isNotEmpty(absProductStructures)) {
                for (HlsCusAbsProductStructure productStructure : absProductStructures) {
                    productStructure.setProductId(targetProductId);
                    if(productStructure.getRefStructureId()!=null){
                        Long structureId=productStructure.getStructureId();
                        productStructure.setStructureId(productStructure.getRefStructureId());
                        productStructure.setRefStructureId(structureId);
                        productStructure.setObjectVersionNumber(null);
                        productStructure.set__status(DTOStatus.UPDATE);
                    }else{
                        productStructure.setRefStructureId(productStructure.getStructureId());
                        productStructure.setStructureId(null);
                        productStructure.set__status(DTOStatus.ADD);
                        productStructure.setObjectVersionNumber(1L);
                        productStructure.setCreationDate(null);
                        productStructure.setLastUpdateDate(null);
                        productStructure.setCreatedBy(request.getUserId());
                        productStructure.setLastUpdatedBy(request.getUserId());
                    }
                }
                hlsCusAbsProductStructureService.batchUpdate(request, absProductStructures);
            }


            // 复制归集兑付计划
            HlsCusAbsProductCollection productCollection=new HlsCusAbsProductCollection();
            productCollection.setProductId(sourceProductId);
            productCollection.setConfirmFlag(HlsCusConstant.FLAG.N);
            productCollection.setDataClass(HlsCusConstant.DATA_CLASS.NORMAL);
            List<HlsCusAbsProductCollection> productCollections = hlsCusAbsProductCollectionService.select(request, productCollection, 1, 999);
            if (CollectionUtils.isNotEmpty(productCollections)) {
                for (HlsCusAbsProductCollection collection : productCollections) {
                    Long normalCollectionId = collection.getCollectionId();
                    collection.setCollectionId(null);
                    collection.setProductId(targetProductId);
                    collection.set__status(DTOStatus.ADD);
                    collection.setCreationDate(null);
                    collection.setLastUpdateDate(null);
                    collection.setCreatedBy(request.getUserId());
                    collection.setLastUpdatedBy(request.getUserId());
                    hlsCusAbsProductCollectionService.insertSelective(request,collection);

                    HlsCusAbsProductCashDetail cashDetail = new HlsCusAbsProductCashDetail();
                    cashDetail.setProductId(sourceProductId);
                    cashDetail.setCollectionId(normalCollectionId);
                    List<HlsCusAbsProductCashDetail> cashDetails = hlsCusAbsProductCashDetailService.select(request, cashDetail, 1, 999);
                    for (HlsCusAbsProductCashDetail detail : cashDetails) {
                        detail.setCashDetailId(null);
                        detail.setCollectionId(collection.getCollectionId());
                        detail.setProductId(collection.getProductId());

                        HlsCusAbsProductStructure structure = new HlsCusAbsProductStructure();
                        structure.setProductId(targetProductId);
                        structure.setRefStructureId(detail.getStructureId());
                        detail.setStructureId(hlsCusAbsProductStructureService.select(request, structure, 1, 1).get(0).getStructureId());

                        detail.setCreationDate(null);
                        detail.setLastUpdateDate(null);
                        detail.setCreatedBy(request.getUserId());
                        detail.setLastUpdatedBy(request.getUserId());

                        hlsCusAbsProductCashDetailService.insertSelective(request,detail);
                    }
                }
            }

            if(HlsCusConstant.ABS_CHANGE_TYPE.PRODUCT.equals(changeType)) {
                cloneChangeLineData(request, sourceProductId, targetProductId);
            }
        }
    }

    /**
     * changeLineData方法的其他行
     * 后续业务不会引用 变更的时候是可以删的
     * 所以这部分行数据是 直接删除原始normal行 插入变更的
     *
     * @param request
     * @param sourceProductId
     * @param targetProductId
     */
    private void cloneChangeLineData(IRequest request, Long sourceProductId, Long targetProductId) {
        // 复制资金用途
        HlsCusAbsProductPurpose absProductPurpose = new HlsCusAbsProductPurpose();
        absProductPurpose.setProductId(sourceProductId);
        List<HlsCusAbsProductPurpose> absProductPurposes = hlsCusAbsProductPurposeService.select(request, absProductPurpose, 1, 999);
        if (CollectionUtils.isNotEmpty(absProductPurposes)) {
            for (HlsCusAbsProductPurpose productPurpose : absProductPurposes) {
                productPurpose.setPurposeId(null);
                productPurpose.setProductId(targetProductId);
                productPurpose.set__status(DTOStatus.ADD);
                productPurpose.setCreationDate(null);
                productPurpose.setLastUpdateDate(null);
                productPurpose.setCreatedBy(request.getUserId());
                productPurpose.setLastUpdatedBy(request.getUserId());
            }
            hlsCusAbsProductPurposeService.batchUpdate(request, absProductPurposes);
        }

        // 复制附件
        HlsCusPrjProjectAttachment prjProjectAttachment = new HlsCusPrjProjectAttachment();
        prjProjectAttachment.setProjectId(sourceProductId);
        prjProjectAttachment.setProjectAttachmentCategory(HlsCusConstant.ABS_PRO_TYPE.PRODUCT);
        List<HlsCusPrjProjectAttachment> prjProjectAttachments = hlsCusPrjProjectAttachmentService.select(request, prjProjectAttachment, 1, 999);
        if (CollectionUtils.isNotEmpty(prjProjectAttachments)) {
            for (HlsCusPrjProjectAttachment attachment : prjProjectAttachments) {
                attachment.setProjectAttachmentId(null);
                attachment.setProjectId(targetProductId);
                attachment.set__status(DTOStatus.ADD);
                attachment.setCreationDate(null);
                attachment.setLastUpdateDate(null);
                attachment.setCreatedBy(request.getUserId());
                attachment.setLastUpdatedBy(request.getUserId());
            }
            hlsCusPrjProjectAttachmentService.batchUpdate(request, prjProjectAttachments);

       /*     HlsCusSysAttachment sysAttachment = new HlsCusSysAttachment();
            sysAttachment.setSourceType(HlsCusConstant.ABS_PRO_TYPE.PRODUCT);
            sysAttachment.setSourceKey(targetProductId.toString());
            List<HlsCusSysAttachment> sourceAttachments = hlsCusSysAttachmentMapper.select(sysAttachment);
            if (sourceAttachments.size() == 0) {
                sysAttachment.setSourceKey(sourceProductId.toString());
                List<HlsCusSysAttachment> sysAttachments = hlsCusSysAttachmentMapper.select(sysAttachment);
                if (CollectionUtils.isNotEmpty(sysAttachments)) {
                    for (HlsCusSysAttachment attachment : sysAttachments) {
                        attachment.setAttachmentId(null);
                        attachment.setSourceKey(targetProductId.toString());
                        attachment.set__status(DTOStatus.ADD);
                        attachment.setCreationDate(null);
                        attachment.setLastUpdateDate(null);
                        attachment.setCreatedBy(request.getUserId());
                        attachment.setLastUpdatedBy(request.getUserId());
                        hlsCusSysAttachmentMapper.insertSelective(attachment);
                    }

                }
            }*/
        }
    }
    @Override
    public List<HlsCusAbsProduct> queryForProject(IRequest request, HlsCusAbsProduct hlsCusAbsProduct, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return hlsCusAbsProductMapper.queryForProject(hlsCusAbsProduct);
    }

    @Override
    public void cancelProductChange(IRequest request, HlsCusAbsProduct hlsCusAbsProduct) {
        HlsCusAbsProduct changeProduct = self().selectByPrimaryKey(request, hlsCusAbsProduct);
        HlsCusChangeReqInfo hlsCusChangeReqInfo = new HlsCusChangeReqInfo();
        hlsCusChangeReqInfo.setChangeReqId(changeProduct.getChangeReqId());
        hlsCusChangeReqInfo.setStatus(HlsCusConstant.WORKFLOW_STATUS.CANCEL);
        hlsCusChangeReqInfoService.updateByPrimaryKeySelective(request, hlsCusChangeReqInfo);

        HlsCusAbsProduct normalProduct = new HlsCusAbsProduct();
        normalProduct.setProductId(changeProduct.getRefProductId());
        normalProduct.setProductStatus(changeProduct.getProductStatus());
        self().updateByPrimaryKeySelective(request, normalProduct);
    }


    /**
     * 从融资提款报价计算搬过来的代码(有问题请参考HlsCusLonContractWithdrawServiceImpl.lonContractCalcRepayment)
     * @param request
     * @param pkg
     * @return
     * @throws Exception
     */
    @Override
    public HlsCusAbsProduct calculateIssueProduct(IRequest request, HlsCusAbsPkg pkg) throws Exception {
        HlsCusAbsProduct hlsCusAbsProduct = self().updateProduct(request, pkg);

        HlsCusAbsProductQuotation productQuotation = new HlsCusAbsProductQuotation();
        productQuotation.setQuotationId(hlsCusAbsProduct.getQuotationId());
        productQuotation = productQuotationService.selectByPrimaryKey(request, productQuotation);
        productQuotation.setProjectId(hlsCusAbsProduct.getProjectId());

        //获取所有还款计划
        HlsCusAbsProductRepayment repaymentTemp = new HlsCusAbsProductRepayment();
        repaymentTemp.setProductId(hlsCusAbsProduct.getProductId());
        repaymentTemp.setCfItem(HlsCusConstant.LON_REPAYMENT.PRINCIPAL_CF_ITEM);
        List<HlsCusAbsProductRepayment> hlsCusAbsProductRepayments = productRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(repaymentTemp);

        int principalCount = 0;
        Long principalCfItem = HlsCusConstant.LON_REPAYMENT.PRINCIPAL_CF_ITEM;
        Date lastRepaymentPlanDate = productQuotation.getWithdrawEndDate();
        //本金
        Double principalSum = 0D;

        //判断该笔提款计划项下是否存在还本计划
        for (HlsCusAbsProductRepayment hlsCusAbsProductRepayment : hlsCusAbsProductRepayments) {
            if (principalCfItem.equals(hlsCusAbsProductRepayment.getCfItem())) {
                principalCount = principalCount + 1;
                lastRepaymentPlanDate = hlsCusAbsProductRepayment.getPlannedDueDate();
                principalSum = principalSum + hlsCusAbsProductRepayment.getPlannedDueAmount();
            }
        }


        //如果为剩余本金计算法，则按照还本计划计算，使用计息方式为剩余本金计息
        //无还本计划则在末期插入
        if ("ONCE_REPAYMENT".equalsIgnoreCase(productQuotation.getInterestCalcMethod())) {
            //如果为一次性还本，则删除所有本金计划，重新在末期插入还本计划
            HlsCusAbsProductRepayment deleteRepaymentTemp = new HlsCusAbsProductRepayment();
            deleteRepaymentTemp.setProductId(hlsCusAbsProduct.getProductId());
            deleteRepaymentTemp.setCfItem(HlsCusConstant.LON_REPAYMENT.PRINCIPAL_CF_ITEM);
            deleteRepaymentTemp.setConfirmFlag(HlsCusConstant.FLAG.N);
            productRepaymentMapper.delete(deleteRepaymentTemp);
        } else {

            if (principalCount > 0) {
                //格式化设置
                DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
                //由于double类型的精度问题，因此不能直接去对比，先对这两个数据进行格式化，在进行对比
                if (decimalFormat.format(productQuotation.getDueAmount()).compareTo(decimalFormat.format(principalSum)) != 0) {
                    throw new HlsCusException("还款-本金之和为" + decimalFormat.format(principalSum) + "，发行金额为"
                            + decimalFormat.format(productQuotation.getDueAmount()) + "两者不相等");
                }
            }
        }

        //取得所有还本日期
        Date[] repaymentPrincipalDate = new Date[principalCount];
        int j = 0;
        for (HlsCusAbsProductRepayment hlsCusAbsProductRepayment : hlsCusAbsProductRepayments) {
            if (principalCfItem.equals(hlsCusAbsProductRepayment.getCfItem())) {
                repaymentPrincipalDate[j] = hlsCusAbsProductRepayment.getPlannedDueDate();
                j = j + 1;
            }
        }


        //计算还本付息计划
        outStdPrincipalCalcRepayment(request, productQuotation, lastRepaymentPlanDate, principalCount, repaymentPrincipalDate);

        //刷新PLANNED_DUE_AMOUNT位数,保留2位
        HlsCusAbsProductRepayment refreshRepaymentTemp = new HlsCusAbsProductRepayment();
        refreshRepaymentTemp.setProductId(productQuotation.getProductId());
        List<HlsCusAbsProductRepayment> refreshRepaymentList = productRepaymentMapper.select(refreshRepaymentTemp);
        for (HlsCusAbsProductRepayment dt : refreshRepaymentList) {
            if (dt.getPlannedDueAmount() != null && dt.getPlannedDueAmount() != 0) {
                BigDecimal amountNew = new BigDecimal(dt.getPlannedDueAmount());
                dt.setPlannedDueAmount(amountNew.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            }
            productRepaymentMapper.updateByPrimaryKeySelective(dt);
        }
        return hlsCusAbsProduct;
    }


    /**
     * @Description:剩余本金计算还本付息计划，同时判断合同是否为一次性还本的还款方式
     * @Author: zhangyu
     * @Date: Created on 2018/5/14
     */
    public void outStdPrincipalCalcRepayment(IRequest iRequest, HlsCusAbsProductQuotation productQuotation, Date lastRepaymentPlanDate, int principalCount, Date[] repaymentPrincipalDate) throws Exception {
        //获取第1期的计算日期
        Date firstInterestCalcDate = null;

        Long times = 1L;
        Long calcInterestDays;//计息天数
        Double repaymentAmount;
        //提款金额
        Double outStd = productQuotation.getDueAmount();
        //实际提款日
        Date repaymentDate = productQuotation.getDueDate();

        Date repaymentCalcDate = productQuotation.getDueDate();
        Date nextRepaymentCalcDate;
        Date nextRepaymentDate;
        HlsCusAbsProductRepayment hlsCusLonContractRepayment = new HlsCusAbsProductRepayment();
        HlsCusAbsProductRepayment hlsCusLonContractPriRepayment = new HlsCusAbsProductRepayment();

        //1.删除所有未确认还息计划
        HlsCusAbsProductRepayment deleteRepaymentTemp = new HlsCusAbsProductRepayment();
        deleteRepaymentTemp.setProductId(productQuotation.getProductId());
        deleteRepaymentTemp.setCfItem(HlsCusConstant.LON_REPAYMENT.INTEREST_CF_ITEM);
        deleteRepaymentTemp.setConfirmFlag(HlsCusConstant.FLAG.N);
        productRepaymentMapper.delete(deleteRepaymentTemp);

        //获取该笔提款计划项下所有还款计划
        HlsCusAbsProductRepayment hlsCusAbsProductRepaymenttTemp = new HlsCusAbsProductRepayment();
        hlsCusAbsProductRepaymenttTemp.setProductId(productQuotation.getProductId());
        hlsCusAbsProductRepaymenttTemp.setCfItem(HlsCusConstant.LON_REPAYMENT.PRINCIPAL_CF_ITEM);
        List<HlsCusAbsProductRepayment> hlsCusLonContractRepayments = productRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(hlsCusAbsProductRepaymenttTemp);

        //2.如果存在还本计划，则更新每一期还本计划的剩余本金
        if (principalCount > 0) {
            updateAllPrincipalOutStd(iRequest, productQuotation, "N", null);
        }
        /**
         * add by tengfei,添加利随本清
         */
        if (!ObjectUtils.isEmpty(productQuotation)) {

            //3.在规定还款期数内计算还款计划，在提款期数范围内循环插入
            String flag = "N";

            for (int i = 1; i <= (productQuotation.getLoanTimes()==null?0:productQuotation.getLoanTimes()); i++) {
                calcInterestDays = 0L;
                if (i == 1) {
                    flag = "Y";
                }
                nextRepaymentCalcDate = getNextRepaymentDateTmp(repaymentCalcDate, productQuotation.getInterestCycle(), flag, productQuotation.getInterestMonth());
                nextRepaymentDate = getNextRepaymentDateTmp(repaymentDate, productQuotation.getInterestCycle(), flag, productQuotation.getInterestMonth());
                //如果firstInterestCalcDate第一期的计算日期已经存在，那么就按照第一期的计算日期为基准日期进行月末矫正
                if (!ObjectUtils.isEmpty(firstInterestCalcDate)) {
                    nextRepaymentCalcDate = HlsCusEndOfMonth.calcEndOfMonth(firstInterestCalcDate, nextRepaymentCalcDate);
                }
                //计息开始日
                Calendar cStart = Calendar.getInstance();
                //计息结束日
                Calendar cEnd = Calendar.getInstance();
                cStart.setTime(repaymentCalcDate);
                cEnd.setTime(nextRepaymentCalcDate);
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                if (cStart.getTime() == cEnd.getTime() && times == 1) {
                    i = i - 1;
                    repaymentDate = nextRepaymentDate;
                    repaymentCalcDate = nextRepaymentCalcDate;
                    continue;
                }

                //第一期
                if (times == 1) {
                    int RepaymentMonth = cStart.get(Calendar.MONTH) + 1;
                    int RepaymentYear = cStart.get(Calendar.YEAR);
                    int RepaymentDay = cStart.get(Calendar.DATE);
                    int j = 1;
                    int monthCount = 0;
                    if (YEAR.equalsIgnoreCase(productQuotation.getInterestCycle())) {
                        monthCount = 12;
                    } else if (HALF_A_YEAR.equalsIgnoreCase(productQuotation.getInterestCycle())) {
                        monthCount = 6;
                    } else if (QUARTER.equalsIgnoreCase(productQuotation.getInterestCycle())) {
                        monthCount = 3;
                    } else if (MONTH.equalsIgnoreCase(productQuotation.getInterestCycle())) {
                        monthCount = 1;
                    }
                    for (int k = 0; k < monthCount; k++) {
                        //按季,如果提款起息日为2018-12-30 如果按季度，结息月中包括12月份的话，那么就直接用当前月份
                        if (QUARTER.equalsIgnoreCase(productQuotation.getInterestCycle())) {
                            //如果提款起息日小于结息日期
                            if (k == 0 && RepaymentDay <= productQuotation.getInterestCalcDate() &&
                                    (calcMonth(RepaymentMonth, Integer.parseInt(productQuotation.getInterestMonth()), 0)
                                            || calcMonth(RepaymentMonth, Integer.parseInt(productQuotation.getInterestMonth()), monthCount)
                                            || calcMonth(RepaymentMonth, Integer.parseInt(productQuotation.getInterestMonth()), monthCount * 2)
                                            || calcMonth(RepaymentMonth, Integer.parseInt(productQuotation.getInterestMonth()), monthCount * 3)
                                    )) {
                                //就使用当前月  RepaymentMonth
                                break;
                            } else {

                                if (productQuotation.getInterestMonth().equals("1")) {
                                    if (cStart.get(Calendar.MONTH) + 1 + j <= 12) {
                                        if ((cStart.get(Calendar.MONTH) + 1 + j) == 1 || (cStart.get(Calendar.MONTH) + 1 + j) == 4 || (cStart.get(Calendar.MONTH) + 1 + j) == 7 || (cStart.get(Calendar.MONTH) + 1 + j) == 11) {
                                            RepaymentMonth = cStart.get(Calendar.MONTH) + 1 + j;
                                        }
                                    } else {
                                        if ((cStart.get(Calendar.MONTH) + 1 + j - 12) == 1 || (cStart.get(Calendar.MONTH) + 1 + j - 12) == 4 || (cStart.get(Calendar.MONTH) + 1 + j - 12) == 7 || (cStart.get(Calendar.MONTH) + 1 + j - 12) == 11) {
                                            RepaymentMonth = cStart.get(Calendar.MONTH) + 1 + j;
                                        }
                                    }
                                } else if (productQuotation.getInterestMonth().equals("2")) {
                                    if (cStart.get(Calendar.MONTH) + 1 + j <= 12) {
                                        if ((cStart.get(Calendar.MONTH) + 1 + j) == 2 || (cStart.get(Calendar.MONTH) + 1 + j) == 5 || (cStart.get(Calendar.MONTH) + 1 + j) == 8 || (cStart.get(Calendar.MONTH) + 1 + j) == 11) {
                                            RepaymentMonth = cStart.get(Calendar.MONTH) + 1 + j;
                                        }
                                    } else {
                                        if ((cStart.get(Calendar.MONTH) + 1 + j - 12) == 2 || (cStart.get(Calendar.MONTH) + 1 + j - 12) == 5 || (cStart.get(Calendar.MONTH) + 1 + j - 12) == 8 || (cStart.get(Calendar.MONTH) + 1 + j - 12) == 11) {
                                            RepaymentMonth = cStart.get(Calendar.MONTH) + 1 + j;
                                        }
                                    }
                                } else if (productQuotation.getInterestMonth().equals("3")) {
                                    if (cStart.get(Calendar.MONTH) + 1 + j <= 12) {
                                        if ((cStart.get(Calendar.MONTH) + 1 + j) == 3 || (cStart.get(Calendar.MONTH) + 1 + j) == 6 || (cStart.get(Calendar.MONTH) + 1 + j) == 9 || (cStart.get(Calendar.MONTH) + 1 + j) == 12) {
                                            RepaymentMonth = cStart.get(Calendar.MONTH) + 1 + j;
                                        }
                                    } else {
                                        if ((cStart.get(Calendar.MONTH) + 1 + j - 12) == 3 || (cStart.get(Calendar.MONTH) + 1 + j - 12) == 6 || (cStart.get(Calendar.MONTH) + 1 + j - 12) == 9 || (cStart.get(Calendar.MONTH) + 1 + j - 12) == 12) {
                                            RepaymentMonth = cStart.get(Calendar.MONTH) + 1 + j;
                                        }
                                    }
                                }
                            }
                            //按半年
                        } else if (HALF_A_YEAR.equalsIgnoreCase(productQuotation.getInterestCycle())) {
                            if (k == 0 && RepaymentDay <= productQuotation.getInterestCalcDate() &&
                                    (calcMonth(RepaymentMonth, Integer.parseInt(productQuotation.getInterestMonth()), 0)
                                            || calcMonth(RepaymentMonth, Integer.parseInt(productQuotation.getInterestMonth()), monthCount)
                                            || calcMonth(RepaymentMonth, Integer.parseInt(productQuotation.getInterestMonth()), monthCount * 2)
                                    )
                                    ) {
                                //就使用当前月  RepaymentMonth
                                break;
                            } else {


                                if (productQuotation.getInterestMonth().equals("1")) {
                                    if (cStart.get(Calendar.MONTH) + 1 + j <= 12) {
                                        if ((cStart.get(Calendar.MONTH) + 1 + j) == 1 || (cStart.get(Calendar.MONTH) + 1 + j) == 7) {
                                            RepaymentMonth = cStart.get(Calendar.MONTH) + 1 + j;
                                        }
                                    } else {
                                        if ((cStart.get(Calendar.MONTH) + 1 + j - 12) == 1 || (cStart.get(Calendar.MONTH) + 1 + j - 12) == 7) {
                                            RepaymentMonth = cStart.get(Calendar.MONTH) + 1 + j;
                                        }
                                    }
                                } else if (productQuotation.getInterestMonth().equals("2")) {
                                    if (cStart.get(Calendar.MONTH) + 1 + j <= 12) {
                                        if ((cStart.get(Calendar.MONTH) + 1 + j) == 2 || (cStart.get(Calendar.MONTH) + 1 + j) == 8) {
                                            RepaymentMonth = cStart.get(Calendar.MONTH) + 1 + j;
                                        }
                                    } else {
                                        if ((cStart.get(Calendar.MONTH) + 1 + j - 12) == 2 || (cStart.get(Calendar.MONTH) + 1 + j - 12) == 8) {
                                            RepaymentMonth = cStart.get(Calendar.MONTH) + 1 + j;
                                        }
                                    }
                                } else if (productQuotation.getInterestMonth().equals("3")) {
                                    if (cStart.get(Calendar.MONTH) + 1 + j <= 12) {
                                        if ((cStart.get(Calendar.MONTH) + 1 + j) == 3 || (cStart.get(Calendar.MONTH) + 1 + j) == 9) {
                                            RepaymentMonth = cStart.get(Calendar.MONTH) + 1 + j;
                                        }
                                    } else {
                                        if ((cStart.get(Calendar.MONTH) + 1 + j - 12) == 3 || (cStart.get(Calendar.MONTH) + 1 + j - 12) == 9) {
                                            RepaymentMonth = cStart.get(Calendar.MONTH) + 1 + j;
                                        }
                                    }
                                } else if (productQuotation.getInterestMonth().equals("4")) {
                                    if (cStart.get(Calendar.MONTH) + 1 + j <= 12) {
                                        if ((cStart.get(Calendar.MONTH) + 1 + j) == 4 || (cStart.get(Calendar.MONTH) + 1 + j) == 10) {
                                            RepaymentMonth = cStart.get(Calendar.MONTH) + 1 + j;
                                        }
                                    } else {
                                        if ((cStart.get(Calendar.MONTH) + 1 + j - 12) == 4 || (cStart.get(Calendar.MONTH) + 1 + j - 12) == 10) {
                                            RepaymentMonth = cStart.get(Calendar.MONTH) + 1 + j;
                                        }
                                    }
                                } else if (productQuotation.getInterestMonth().equals("5")) {
                                    if (cStart.get(Calendar.MONTH) + 1 + j <= 12) {
                                        if ((cStart.get(Calendar.MONTH) + 1 + j) == 5 || (cStart.get(Calendar.MONTH) + 1 + j) == 11) {
                                            RepaymentMonth = cStart.get(Calendar.MONTH) + 1 + j;
                                        }
                                    } else {
                                        if ((cStart.get(Calendar.MONTH) + 1 + j - 12) == 5 || (cStart.get(Calendar.MONTH) + 1 + j - 12) == 11) {
                                            RepaymentMonth = cStart.get(Calendar.MONTH) + 1 + j;
                                        }
                                    }
                                } else if (productQuotation.getInterestMonth().equals("6")) {
                                    if (cStart.get(Calendar.MONTH) + 1 + j <= 12) {
                                        if ((cStart.get(Calendar.MONTH) + 1 + j) == 6 || (cStart.get(Calendar.MONTH) + 1 + j) == 12) {
                                            RepaymentMonth = cStart.get(Calendar.MONTH) + 1 + j;
                                        }
                                    } else {
                                        if ((cStart.get(Calendar.MONTH) + 1 + j - 12) == 6 || (cStart.get(Calendar.MONTH) + 1 + j - 12) == 12) {
                                            RepaymentMonth = cStart.get(Calendar.MONTH) + 1 + j;
                                        }
                                    }
                                }
                            }
                            //按年
                        } else if (YEAR.equalsIgnoreCase(productQuotation.getInterestCycle())) {
//
                            if (k == 0) {
                                if (RepaymentMonth > Integer.valueOf(productQuotation.getInterestMonth()).intValue() || (RepaymentMonth == Integer.valueOf(productQuotation.getInterestMonth()).intValue() && RepaymentDay > productQuotation.getInterestCalcDate())) {
                                    RepaymentYear = RepaymentYear + 1;
                                }
                            }

                            RepaymentMonth = Integer.valueOf(productQuotation.getInterestMonth()).intValue();


                            //按月
                        } else if (MONTH.equalsIgnoreCase(productQuotation.getInterestCycle())) {

                            if (k == 0 && RepaymentDay <= productQuotation.getInterestCalcDate()) {
                                //就使用当前月  RepaymentMonth

                                break;
                            } else {
                                RepaymentMonth = RepaymentMonth + 1;
                            }

                        }
                        j = j + 1;
                    }

                    if (RepaymentMonth > 12) {
                        //如果月份大于12，那么就让这个月份对12取模
                        int count = RepaymentMonth / 12;
                        RepaymentYear = RepaymentYear + count;
                        RepaymentMonth = RepaymentMonth - count * 12;
                    }
                    Long var1 = 0L;

                    if (productQuotation.getInterestCalcDate() != null) {
                        var1 = productQuotation.getInterestCalcDate() + 1;
                    } else {
                        var1 = productQuotation.getInterestCalcDate();
                    }
                    String firstRepaymentCalcDate = RepaymentYear + "-" + RepaymentMonth + "-" + var1;

                    String firstRepaymentDate = RepaymentYear + "-" + RepaymentMonth + "-" + var1;
                    try {
                        //矫正日期格式
                        nextRepaymentCalcDate = df.parse(HlsCusEndOfMonth.correctDate(firstRepaymentCalcDate));
                        nextRepaymentDate = df.parse(HlsCusEndOfMonth.correctDate(firstRepaymentDate));
                        cEnd.setTime(nextRepaymentCalcDate);
                        //用来记录第1期计算-利息的日期
                        firstInterestCalcDate = nextRepaymentCalcDate;
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }

                //最后一期
                if (Objects.equals(times, productQuotation.getLoanTimes())) {
                    nextRepaymentCalcDate = productQuotation.getWithdrawEndDate();
                    nextRepaymentDate = nextRepaymentCalcDate;
                    cEnd.setTime(nextRepaymentCalcDate);

                }

                //计息天数
                calcInterestDays = calcInterestDays + (cEnd.getTimeInMillis() - cStart.getTimeInMillis()) / (1000 * 3600 * 24);
                calcInterestDays = calcInterestDays + daysAjust(productQuotation.getDueDate(), productQuotation.getWithdrawEndDate(),
                        repaymentPrincipalDate, repaymentCalcDate, nextRepaymentCalcDate, productQuotation.getProductId());
                //考虑到一个临界值，如果第一期的结息日期和起息日期一致，那么上述计算得到的值为0，那么给当前值+1，然后在总体计算完成之后，
                // 不需要去给第一期+1天
                if (times == 1 && calcInterestDays == 0) {
                    calcInterestDays = calcInterestDays + 1;
                }

                //还款利息，当期剩余本金
                if (productQuotation.getRateChangeDate() != null && productQuotation.getRateChangeAfter() != null
                        && productQuotation.getCalcInterestYearDaysAfter() != null) {
                    //分段计算
                    if (repaymentCalcDate.getTime() <= productQuotation.getRateChangeDate().getTime() && productQuotation.getRateChangeDate().getTime() < nextRepaymentCalcDate.getTime()) {
                        Calendar cChangeStart = Calendar.getInstance();//计息开始日
                        Calendar cChangeEnd = Calendar.getInstance();//计息结束日
                        cChangeStart.setTime(repaymentCalcDate);
                        cChangeEnd.setTime(productQuotation.getRateChangeDate());
                        if (1 == (cChangeEnd.getTimeInMillis() - cChangeStart.getTimeInMillis()) / (1000 * 3600 * 24)) {
                            //变更日正好是该期的第一天，算整期
                            repaymentAmount = getRepaymentAmount(iRequest, repaymentCalcDate, nextRepaymentCalcDate,
                                    productQuotation, productQuotation.getDueAmount(),
                                    productQuotation.getRateChangeAfter() * 100,
                                    productQuotation.getCalcInterestYearDaysAfter(), repaymentPrincipalDate);
                        } else {
                            //变更日期不是第一天，相减算头不算为，故变更日期往前调整一天在计算天数
                            cChangeEnd.add(Calendar.DAY_OF_YEAR, -1);
                            repaymentAmount = getRepaymentAmount(iRequest, repaymentCalcDate, cChangeEnd.getTime(), productQuotation, productQuotation.getDueAmount(), productQuotation.getIntRate(), productQuotation.getCalcInterestYearDays(), repaymentPrincipalDate);
                            repaymentAmount = repaymentAmount + getRepaymentAmount(iRequest, cChangeEnd.getTime(), nextRepaymentCalcDate, productQuotation, productQuotation.getDueAmount(), productQuotation.getRateChangeAfter() * 100, productQuotation.getCalcInterestYearDaysAfter(), repaymentPrincipalDate);
                        }
                    } else if (repaymentCalcDate.getTime() >= productQuotation.getRateChangeDate().getTime()) {
                        repaymentAmount = getRepaymentAmount(iRequest, repaymentCalcDate, nextRepaymentCalcDate, productQuotation, productQuotation.getDueAmount(), productQuotation.getRateChangeAfter() * 100, productQuotation.getCalcInterestYearDaysAfter(), repaymentPrincipalDate);
                    } else {
                        repaymentAmount = getRepaymentAmount(iRequest, repaymentCalcDate, nextRepaymentCalcDate, productQuotation, productQuotation.getDueAmount(), productQuotation.getIntRate(), productQuotation.getCalcInterestYearDays(), repaymentPrincipalDate);
                    }
                } else {
                    repaymentAmount = getRepaymentAmount(iRequest, repaymentCalcDate, nextRepaymentCalcDate, productQuotation, productQuotation.getDueAmount(), productQuotation.getIntRate(), productQuotation.getCalcInterestYearDays(), repaymentPrincipalDate);
                }
                outStd = getOutStd(nextRepaymentCalcDate, productQuotation, productQuotation.getDueAmount());

                if (times == productQuotation.getLoanTimes()) {
                    outStd = 0D;
                }

                hlsCusLonContractRepayment.setProductId(productQuotation.getProductId());
                hlsCusLonContractRepayment.setTimes(times);
                hlsCusLonContractRepayment.setCfType(70L);
                hlsCusLonContractRepayment.setCfItem(HlsCusConstant.LON_REPAYMENT.INTEREST_CF_ITEM);
                hlsCusLonContractRepayment.setCfDirection(HlsCusConstant.OUTFLOW);
                hlsCusLonContractRepayment.setCfStatus("RELEASE");
                //还款计算日
                hlsCusLonContractRepayment.setPlannedCalcDate(nextRepaymentCalcDate);

                //如果是最后一期
                if (Objects.equals(times, productQuotation.getLoanTimes())) {
                    //如果是期初一次性
                    if ("BEGINNING_OF_PERIOD".equalsIgnoreCase(productQuotation.getInterestCycle())) {
                        nextRepaymentCalcDate = productQuotation.getDueDate();
                        hlsCusLonContractRepayment.setPlannedDueDate(nextRepaymentCalcDate);
                        //如果是期末一次性
                    } else if ("EXPIRE_OF_PERIOD".equalsIgnoreCase(productQuotation.getInterestCycle())) {
                        hlsCusLonContractRepayment.setPlannedDueDate(nextRepaymentDate);
                    } else {

                        hlsCusLonContractRepayment.setPlannedDueDate(nextRepaymentDate);
                    }
                } else {
                    //如果是还款-利息中间的期数，那么更新lon_contract_repayment的还款日为lon_contract_withdraw的付息日
                    Calendar c = Calendar.getInstance();
                    c.setTime(hlsCusLonContractRepayment.getPlannedCalcDate());
                    int month = c.get(Calendar.MONTH) + 1;
                    int year = c.get(Calendar.YEAR);
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    day = Integer.parseInt(productQuotation.getInterestPaymentDate().toString());
                    String date = year + "-" + month + "-" + day;
                    //修改结束
                    hlsCusLonContractRepayment.setPlannedDueDate(df.parse(HlsCusEndOfMonth.correctDate(date)));
                }


                hlsCusLonContractRepayment.setInterestPeriodDays(calcInterestDays);
                hlsCusLonContractRepayment.setProjectId(productQuotation.getProjectId());
                hlsCusLonContractRepayment.setPlannedDueAmount(repaymentAmount);
                hlsCusLonContractRepayment.setInterestAccrualBalance(outStd);
                hlsCusLonContractRepayment.setWriteOffFlag("NOT");
                //QQ  hlsCusLonContractRepayment.setReceivedBpName(hlsCusBpMaster.getBpName());
                hlsCusLonContractRepayment.setExchangeRate(1D);
                hlsCusLonContractRepayment.setIsSystemFlag("Y");
                //QQ
//                if (ctLonBankAccountList.size() > 0) {
//                    hlsCusLonContractRepayment.setReceivedBankAccountName(ctLonBankAccountList.get(0).getBankAccountName());
//                    hlsCusLonContractRepayment.setReceivedBankAccountNum(ctLonBankAccountList.get(0).getBankAccountNum());
//                }

                HlsCusAbsProductRepayment lonContractRepayment = new HlsCusAbsProductRepayment();
                lonContractRepayment.setProductId(productQuotation.getProductId());
                lonContractRepayment.setCfItem(HlsCusConstant.LON_REPAYMENT.INTEREST_CF_ITEM);
                lonContractRepayment.setTimes(times);

                List<HlsCusAbsProductRepayment> lonContractRepaymentList = productRepaymentMapper.select(lonContractRepayment);
                if (lonContractRepaymentList.size() == 0 && (hlsCusLonContractRepayment.getPlannedDueDate().getTime() <= productQuotation.getWithdrawEndDate().getTime()) && (hlsCusLonContractRepayment.getPlannedCalcDate().getTime() <= productQuotation.getWithdrawEndDate().getTime())) {
                    productRepaymentService.insertSelective(iRequest, hlsCusLonContractRepayment);
                }
                times = times + 1;
                repaymentDate = nextRepaymentDate;
                repaymentCalcDate = nextRepaymentCalcDate;

            }
        }

        //4.如果没有还本计划，则在末期插入还本计划
        if (principalCount == 0 || "ONCE_REPAYMENT".equalsIgnoreCase(productQuotation.getInterestCalcMethod())) {
            hlsCusLonContractRepayment.setProductId(productQuotation.getProductId());
            hlsCusLonContractRepayment.setTimes(0L);
            hlsCusLonContractRepayment.setCfType(70L);
            hlsCusLonContractRepayment.setCfItem(HlsCusConstant.LON_REPAYMENT.PRINCIPAL_CF_ITEM);
            hlsCusLonContractRepayment.setCfDirection("OUTFLOW");
            hlsCusLonContractRepayment.setCfStatus("RELEASE");

            hlsCusLonContractRepayment.setPlannedDueDate(productQuotation.getWithdrawEndDate());
            hlsCusLonContractRepayment.setPlannedCalcDate(productQuotation.getWithdrawEndDate());
            hlsCusLonContractRepayment.setInterestPeriodDays(0L);
            hlsCusLonContractRepayment.setProjectId(productQuotation.getProjectId());
            hlsCusLonContractRepayment.setPlannedDueAmount(productQuotation.getDueAmount());
            hlsCusLonContractRepayment.setInterestAccrualBalance(0D);
            hlsCusLonContractRepayment.setWriteOffFlag("NOT");
            //  QQ
//            hlsCusLonContractRepayment.setReceivedBpName(hlsCusBpMaster.getBpName());
            hlsCusLonContractRepayment.setExchangeRate(1D);
            //QQ
//            if (ctLonBankAccountList.size() > 0) {
//                hlsCusLonContractRepayment.setReceivedBankAccountName(ctLonBankAccountList.get(0).getBankAccountName());
//                hlsCusLonContractRepayment.setReceivedBankAccountNum(ctLonBankAccountList.get(0).getBankAccountNum());
//            }
            productRepaymentService.insertSelective(iRequest, hlsCusLonContractRepayment);

        }

        //还本计划处理剩余本金,同时刷新还款计划期数
        updateAllPrincipalOutStd(iRequest, productQuotation, "Y", repaymentPrincipalDate);

        //计算XIRR
        calXirr(iRequest, productQuotation);


        //合并重复还款-利息的金额为一期
        HlsCusAbsProductRepayment hlsCusLonContractRepayment3 = new HlsCusAbsProductRepayment();
        hlsCusLonContractRepayment3.setProductId(productQuotation.getProductId());
        hlsCusLonContractRepayment3.setCfItem(HlsCusConstant.LON_REPAYMENT.INTEREST_CF_ITEM);
        List<HlsCusAbsProductRepayment> repaymentList = productRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(hlsCusLonContractRepayment3);
        for (int i = 0; i < repaymentList.size() - 1; i++) {
            for (int k = i + 1; k < repaymentList.size(); k++) {
                if (repaymentList.get(i).getPlannedDueDate().getTime() == repaymentList.get(k).getPlannedDueDate().getTime()) {
                    if (repaymentList.get(i).getPlannedCalcDate().getTime() > repaymentList.get(k).getPlannedCalcDate().getTime()) {
                        repaymentList.get(i).setInterestPeriodDays(repaymentList.get(k).getInterestPeriodDays());
                        productRepaymentService.updateByPrimaryKeySelective(iRequest, repaymentList.get(i));
                        productRepaymentService.deleteByPrimaryKey(repaymentList.get(k));
                    } else {
                        repaymentList.get(k).setInterestPeriodDays(repaymentList.get(k).getInterestPeriodDays());
                        productRepaymentService.updateByPrimaryKeySelective(iRequest, repaymentList.get(k));
                        productRepaymentService.deleteByPrimaryKey(repaymentList.get(i));
                    }
                }

            }
            if (repaymentList.get(i).getPlannedDueAmount() <= 0D) {
                productRepaymentService.deleteByPrimaryKey(repaymentList.get(i));
            }
        }

        HlsCusAbsProductRepayment t1 = new HlsCusAbsProductRepayment();
        t1.setProductId(productQuotation.getProductId());
        t1.setCfItem(HlsCusConstant.LON_REPAYMENT.INTEREST_CF_ITEM);
        List<HlsCusAbsProductRepayment> dd = productRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(t1);

//处理数据
        HlsCusAbsProductRepayment hlsCusLonContractRepaymentDeal1 = new HlsCusAbsProductRepayment();
        hlsCusLonContractRepaymentDeal1.setProductId(productQuotation.getProductId());
        hlsCusLonContractRepaymentDeal1.setCfItem(HlsCusConstant.LON_REPAYMENT.INTEREST_CF_ITEM);
        hlsCusLonContractRepaymentDeal1.setIsSystemFlag("Y");
        List<HlsCusAbsProductRepayment> repaymentListDeal1 = productRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(hlsCusLonContractRepaymentDeal1);

        for (int i = 0; i < repaymentListDeal1.size(); i++) {
            HlsCusAbsProductRepayment hlsCusLonContractRepaymentDeal2 = new HlsCusAbsProductRepayment();
            hlsCusLonContractRepaymentDeal2.setProductId(productQuotation.getProductId());
            hlsCusLonContractRepaymentDeal2.setCfItem(HlsCusConstant.LON_REPAYMENT.INTEREST_CF_ITEM);
            hlsCusLonContractRepaymentDeal2.setIsSystemFlag("N");

            Calendar dealStartDate = Calendar.getInstance();
            Calendar dealEndDate = Calendar.getInstance();
            if (i == 0) {
                dealStartDate.setTime(productQuotation.getDueDate());
                dealEndDate.setTime(repaymentListDeal1.get(i).getPlannedDueDate());
            } else if (i == repaymentListDeal1.size() - 1) {
                dealStartDate.setTime(repaymentListDeal1.get(i - 1).getPlannedDueDate());
                dealStartDate.add(Calendar.DAY_OF_MONTH, 1);
                dealEndDate.setTime(repaymentListDeal1.get(i).getPlannedDueDate());
                dealEndDate.add(Calendar.DAY_OF_MONTH, -1);
            } else {
                dealStartDate.setTime(repaymentListDeal1.get(i - 1).getPlannedDueDate());
                dealStartDate.add(Calendar.DAY_OF_MONTH, 1);
                dealEndDate.setTime(repaymentListDeal1.get(i).getPlannedDueDate());
            }
            hlsCusLonContractRepaymentDeal2.setStartDay(dealStartDate.getTime());
            hlsCusLonContractRepaymentDeal2.setEndDay(dealEndDate.getTime());
            Double intereSum = productRepaymentMapper.selectInterestSum(hlsCusLonContractRepaymentDeal2);
            repaymentListDeal1.get(i).setPlannedDueAmount(CalculateUtil.sub(repaymentListDeal1.get(i).getPlannedDueAmount(), intereSum));
            productRepaymentService.updateByPrimaryKeySelective(iRequest, repaymentListDeal1.get(i));

        }

        //将期数排序
        sortContractRepayment(iRequest, productQuotation);

    }


    //还本计划处理剩余本金,同时刷新还款计划期数
    public void updateAllPrincipalOutStd(IRequest iRequest, HlsCusAbsProductQuotation productQuotation, String flag, Date[] repaymentPrincipalDate) {
        Double outStd;
        //按照还款日顺序获取该笔提款项下所有还本计划
        HlsCusAbsProductRepayment hlsCusLonContractRepaymentTemp = new HlsCusAbsProductRepayment();
        hlsCusLonContractRepaymentTemp.setProductId(productQuotation.getProductId());
        hlsCusLonContractRepaymentTemp.setCfItem(HlsCusConstant.LON_REPAYMENT.PRINCIPAL_CF_ITEM);
        List<HlsCusAbsProductRepayment> hlsCusLonContractRepayments = productRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(hlsCusLonContractRepaymentTemp);

        for (HlsCusAbsProductRepayment hlsCusLonContractRepayment : hlsCusLonContractRepayments) {
            //- hlsCusLonContractRepayment.getPlannedDueAmount()
            outStd = getOutStd(hlsCusLonContractRepayment.getPlannedDueDate(), productQuotation, productQuotation.getDueAmount());
            hlsCusLonContractRepayment.setInterestAccrualBalance(outStd);
            productRepaymentService.updateByPrimaryKeySelective(iRequest, hlsCusLonContractRepayment);
        }
        //302L:融资-还款利息
        HlsCusAbsProductRepayment hlsCusLonContractRepaymentTemp1 = new HlsCusAbsProductRepayment();
        hlsCusLonContractRepaymentTemp1.setProductId(productQuotation.getProductId());
        hlsCusLonContractRepaymentTemp1.setCfItem(HlsCusConstant.LON_REPAYMENT.INTEREST_CF_ITEM);
        List<HlsCusAbsProductRepayment> lonContractRepaymentList1 = productRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(hlsCusLonContractRepaymentTemp1);

        //301L:融资-还款本金
        hlsCusLonContractRepaymentTemp.setCfItem(HlsCusConstant.LON_REPAYMENT.PRINCIPAL_CF_ITEM);
        List<HlsCusAbsProductRepayment> lonContractRepaymentList = productRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(hlsCusLonContractRepaymentTemp);

        Long times = 0L;
        Long times1 = 1L;
        Date lastRepaymentDate = productQuotation.getDueDate();
        //对还款-本金进行排序
        for (HlsCusAbsProductRepayment lonContractRepayment : lonContractRepaymentList) {
            if (lonContractRepayment.getPlannedDueDate().getTime() > lastRepaymentDate.getTime()) {
                times = times + 1;
            }
            lonContractRepayment.set__status(DTOStatus.UPDATE);
            lonContractRepayment.setTimes(times);
            productRepaymentService.updateByPrimaryKeySelective(iRequest, lonContractRepayment);
            lastRepaymentDate = lonContractRepayment.getPlannedDueDate();
        }
        //按照日期升序排序
        if (CollectionUtils.isNotEmpty(lonContractRepaymentList1)) {
            Collections.sort(lonContractRepaymentList1, new Comparator<HlsCusAbsProductRepayment>() {
                @Override
                public int compare(HlsCusAbsProductRepayment o1, HlsCusAbsProductRepayment o2) {
                    if (o1.getPlannedDueDate().after(o2.getPlannedDueDate())) {
                        return 1;
                    }
                    if (o1.getPlannedDueDate() == o2.getPlannedDueDate()) {
                        return 0;
                    }
                    return -1;
                }
            });

        }
        for (HlsCusAbsProductRepayment lonContractRepayment : lonContractRepaymentList1) {
            if (lonContractRepayment.getPlannedDueDate().getTime() > lastRepaymentDate.getTime()) {
                times1 = times1 + 1;
            }
            lonContractRepayment.set__status(DTOStatus.UPDATE);
            lonContractRepayment.setTimes(times1);
            productRepaymentService.updateByPrimaryKeySelective(iRequest, lonContractRepayment);
            lastRepaymentDate = lonContractRepayment.getPlannedDueDate();
        }
        //当期还本还息
        if (("REPAYMENT_INTERES_CURRENT_PERIOD".equalsIgnoreCase(productQuotation.getInterestCalcMethod())
                || HlsCusConstant.LON_INTEREST_CALC_METHOD.ONCE_CLEAR.equalsIgnoreCase(productQuotation.getInterestCalcMethod()))
                && flag.equals("Y")) {

            Long totalDays = Long.valueOf(compareDays(productQuotation.getDueDate(), productQuotation.getWithdrawEndDate()));

            HlsCusAbsProductRepayment hlsCusLonContractRepayment = new HlsCusAbsProductRepayment();
            hlsCusLonContractRepayment.setProductId(productQuotation.getProductId());
            hlsCusLonContractRepayment.setCfItem(HlsCusConstant.LON_REPAYMENT.INTEREST_CF_ITEM);
            //查询利息
            List<HlsCusAbsProductRepayment> tempIntList = productRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(hlsCusLonContractRepayment);

            String sysflag;
            for (int i = 1; i < tempIntList.size() + 1; i++) {
                HlsCusAbsProductRepayment interestRepayment = tempIntList.get(i - 1);
                Long betweenDay = 0L;
                Date StartDate;
                if (i == 1) {
                    hlsCusLonContractRepayment.setStartDay(productQuotation.getDueDate());
                    hlsCusLonContractRepayment.setEndDay(tempIntList.get(i - 1).getPlannedCalcDate());

                } else {
                    hlsCusLonContractRepayment.setStartDay(tempIntList.get(i - 2).getPlannedCalcDate());
                    hlsCusLonContractRepayment.setEndDay(tempIntList.get(i - 1).getPlannedCalcDate());
                }

                //查询两个日期之间的本金
                hlsCusLonContractRepayment.setCfItem(HlsCusConstant.LON_REPAYMENT.PRINCIPAL_CF_ITEM);
                List<HlsCusAbsProductRepayment> lonContractRepaymentLists = productRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(hlsCusLonContractRepayment);
                if (CollectionUtils.isNotEmpty(lonContractRepaymentLists)) {


                    //用来获取startDay和endDay中间的最后一期的还款-本金
                    HlsCusAbsProductRepayment lastPrincipal = lonContractRepaymentLists.get(lonContractRepaymentLists.size() - 1);
                    for (int j = 0; j < lonContractRepaymentLists.size(); j++) {
                        double amount = 0L;
                        HlsCusAbsProductRepayment var1 = new HlsCusAbsProductRepayment();
                        if (i == 1) {
                            StartDate = productQuotation.getDueDate();
                        } else {
                            StartDate = tempIntList.get(i - 2).getPlannedCalcDate();
                        }
                        betweenDay = Long.valueOf(compareDays(StartDate, lonContractRepaymentLists.get(j).getPlannedCalcDate()));
                        betweenDay = betweenDay + daysAjust(productQuotation.getDueDate(), productQuotation.getWithdrawEndDate(),
                                repaymentPrincipalDate, StartDate, lonContractRepaymentLists.get(j).getPlannedCalcDate(), productQuotation.getProductId());
                        amount = getOutStd(lonContractRepaymentLists.get(j).getPlannedCalcDate(), productQuotation, productQuotation.getDueAmount());
                        var1.setProductId(productQuotation.getProductId());
                        var1.setTimes(lonContractRepaymentLists.get(j).getTimes());
                        var1.setCfType(70L);
                        var1.setCfItem(HlsCusConstant.LON_REPAYMENT.INTEREST_CF_ITEM);
                        var1.setCfDirection(HlsCusConstant.OUTFLOW);
                        var1.setCfStatus("RELEASE");
                        var1.setPlannedDueDate(lonContractRepaymentLists.get(j).getPlannedDueDate());
                        var1.setPlannedCalcDate(lonContractRepaymentLists.get(j).getPlannedCalcDate());
                        var1.setInterestPeriodDays(betweenDay);
                        var1.setProjectId(productQuotation.getProjectId());
                        var1.setPlannedDueAmount(transfor(lonContractRepaymentLists.get(j).getPlannedDueAmount() * betweenDay / new Long(String.valueOf(productQuotation.getCalcInterestYearDays())) * productQuotation.getIntRate()));
                        var1.setInterestAccrualBalance(transfor(amount));
                        var1.setWriteOffFlag(HlsCusConstant.FCT_WRITE_OFF_FLAG.NOT);
                        var1.setExchangeRate(1D);
                        //QQ
//                        var1.setReceivedBpName(hlsCusBpMaster.getBpName());
//                        if (ctLonBankAccountList.size() > 0) {
//                            var1.setReceivedBankAccountName(ctLonBankAccountList.get(0).getBankAccountName());
//                            var1.setReceivedBankAccountNum(ctLonBankAccountList.get(0).getBankAccountNum());
//                        }
                        if ((transfor(lonContractRepaymentLists.get(j).getPlannedDueAmount() * betweenDay / new Long(String.valueOf(productQuotation.getCalcInterestYearDays())) * productQuotation.getIntRate())) > 0) {
                            var1 = productRepaymentService.insertSelective(iRequest, var1);

                        }
                    }

                }
            }
        }

    }


    public void sortContractRepayment(IRequest iRequest, HlsCusAbsProductQuotation productQuotation) {
        //302L:融资-还款利息
        HlsCusAbsProductRepayment hlsCusLonContractRepaymentTemp1 = new HlsCusAbsProductRepayment();
        hlsCusLonContractRepaymentTemp1.setProductId(productQuotation.getProductId());
        hlsCusLonContractRepaymentTemp1.setCfItem(HlsCusConstant.LON_REPAYMENT.INTEREST_CF_ITEM);
        List<HlsCusAbsProductRepayment> lonContractRepaymentList1 = productRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(hlsCusLonContractRepaymentTemp1);

        //301L:融资-还款本金
        hlsCusLonContractRepaymentTemp1.setCfItem(HlsCusConstant.LON_REPAYMENT.PRINCIPAL_CF_ITEM);
        List<HlsCusAbsProductRepayment> lonContractRepaymentList = productRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(hlsCusLonContractRepaymentTemp1);

        Long times = 0L;
        Long times1 = 0L;
        Date lastRepaymentDate = productQuotation.getDueDate();
        for (HlsCusAbsProductRepayment lonContractRepayment : lonContractRepaymentList) {
            if (lonContractRepayment.getPlannedDueDate().getTime() > lastRepaymentDate.getTime()) {
                times = times + 1;
            }
            lonContractRepayment.set__status(DTOStatus.UPDATE);
            lonContractRepayment.setTimes(times);
            productRepaymentService.updateByPrimaryKeySelective(iRequest, lonContractRepayment);
            lastRepaymentDate = lonContractRepayment.getPlannedDueDate();
        }
        //按照日期升序排序
        if (CollectionUtils.isNotEmpty(lonContractRepaymentList1)) {
            Collections.sort(lonContractRepaymentList1, new Comparator<HlsCusAbsProductRepayment>() {
                @Override
                public int compare(HlsCusAbsProductRepayment o1, HlsCusAbsProductRepayment o2) {
                    if (o1.getPlannedDueDate().after(o2.getPlannedDueDate())) {
                        return 1;
                    }
                    if (o1.getPlannedDueDate() == o2.getPlannedDueDate()) {
                        return 0;
                    }
                    return -1;
                }
            });

        }
        for (HlsCusAbsProductRepayment lonContractRepayment : lonContractRepaymentList1) {
            times1 = times1 + 1;
            lonContractRepayment.set__status(DTOStatus.UPDATE);
            lonContractRepayment.setTimes(times1);
            productRepaymentService.updateByPrimaryKeySelective(iRequest, lonContractRepayment);
            lastRepaymentDate = lonContractRepayment.getPlannedDueDate();
        }
    }


    //计算当期剩余本金
    private Double getOutStd(Date repaymentCalcDateTo, HlsCusAbsProductQuotation productQuotation, Double withdrawAmount) {
        IRequest iRequest = RequestHelper.getCurrentRequest();
        Double resultAmount;
        Double sumRepaymentPrincipal = 0D;

        HlsCusAbsProductRepayment hlsCusLonContractRepaymentTemp = new HlsCusAbsProductRepayment();
        hlsCusLonContractRepaymentTemp.setProductId(productQuotation.getProductId());
        hlsCusLonContractRepaymentTemp.setCfItem(HlsCusConstant.LON_REPAYMENT.PRINCIPAL_CF_ITEM);
        List<HlsCusAbsProductRepayment> hlsCusLonContractRepayments = productRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(hlsCusLonContractRepaymentTemp);

        Date lastDate = productQuotation.getWithdrawEndDate();
        for (HlsCusAbsProductRepayment hlsCusLonContractRepayment : hlsCusLonContractRepayments) {

            //如果不是最后一期
            if (hlsCusLonContractRepayment.getPlannedDueDate().getTime() < lastDate.getTime()) {
                if (hlsCusLonContractRepayment.getPlannedDueDate().getTime() <= repaymentCalcDateTo.getTime()) {
                    sumRepaymentPrincipal = sumRepaymentPrincipal + hlsCusLonContractRepayment.getPlannedDueAmount();
                }
            } else {
                if (hlsCusLonContractRepayment.getPlannedDueDate().getTime() <= repaymentCalcDateTo.getTime()) {
                    sumRepaymentPrincipal = sumRepaymentPrincipal + hlsCusLonContractRepayment.getPlannedDueAmount();
                }

            }
        }
        resultAmount = withdrawAmount - sumRepaymentPrincipal;
        return resultAmount;
    }

    //计算下一期还款日或者还款计算日
    private Date getNextRepaymentDateTmp(Date dateFrom, String interestCycle, String flag, String interestMonth) {
        Date resultDate;
        Date temp;
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateFrom);//设置起时间
        int RepaymentMonth = cal.get(Calendar.MONTH) + 1;

        if (YEAR.equalsIgnoreCase(interestCycle)) {
            cal.add(Calendar.YEAR, 1);

            int interestMonthTemp = Integer.valueOf(interestMonth).intValue();
            if (RepaymentMonth > interestMonthTemp) {
                cal.add(Calendar.YEAR, 1);
            } else {
                cal.add(Calendar.YEAR, 0);
            }
            if (flag.equals("N")) {
                cal.add(Calendar.YEAR, 1);
            }


        } else if (HALF_A_YEAR.equalsIgnoreCase(interestCycle)) {
            cal.add(Calendar.MONTH, 6);
        } else if (QUARTER.equalsIgnoreCase(interestCycle)) {
            cal.add(Calendar.MONTH, 3);
        } else if (MONTH.equalsIgnoreCase(interestCycle)) {
            cal.add(Calendar.MONTH, 1);
        }
        resultDate = cal.getTime();

        return resultDate;
    }

    /**
     * @param currentMonth
     * @param InterestMonth
     * @param count
     * @return
     */
    private Boolean calcMonth(int currentMonth, int InterestMonth, int count) {
        int month = InterestMonth + count;
        month = month == 12 ? 12 : month % 12;
        if (currentMonth == month) {
            return true;
        } else {
            return false;
        }

    }


    //2018/9/27:第一期：算头算尾+1   还本日还息：不算头不算尾-1   还本下一期：算头算尾+1   合同结束日：不算头不算尾-1
    private Long daysAjust(Date withdrawPlanDate, Date endActiveDate, Date[] repaymentPrincipalDate,
                           Date repaymentCalcDateFrom, Date repaymentCalcDateTo, Long productId) {

        Long adjustDays = 0L;
        //上一个日期是否为系统
        boolean isSysFlagPre;
        //下一个日期是否为系统
        boolean isSysFlagNext;
        //上一期的日期是否存在系统计算的利息，同时上一期的日期存在对应本金
        boolean existPrincipalInterestPre = false;
        //下一期的日期是否存在系统计算的利息，同时下一期的日期存在对应本金
        boolean existPrincipalInterestNext = false;

        HlsCusAbsProductRepayment repayment = new HlsCusAbsProductRepayment();
        repayment.setProductId(productId);
        repayment.setCfItem(HlsCusConstant.LON_REPAYMENT.PRINCIPAL_CF_ITEM);
        List<HlsCusAbsProductRepayment> hlsCusLonContractRepayments =
                productRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(repayment);
        //获取所有的利息
        repayment.setCfItem(HlsCusConstant.LON_REPAYMENT.INTEREST_CF_ITEM);
        List<HlsCusAbsProductRepayment> hlsCusLonContractRepayments1 =
                productRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(repayment);


        //获取系统计算的利息
        repayment.setIsSystemFlag(HlsCusConstant.FLAG.Y);
        List<HlsCusAbsProductRepayment> hlsCusLonContractRepayments2 =
                productRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(repayment);

        if (repaymentCalcDateFrom.getTime() == withdrawPlanDate.getTime()) {
            isSysFlagPre = false;
            for (HlsCusAbsProductRepayment t : hlsCusLonContractRepayments1) {
                if (t.getPlannedCalcDate().getTime() == repaymentCalcDateFrom.getTime()) {
                    isSysFlagPre = true;
                    break;
                }
            }

        } else {
            isSysFlagPre = true;
            for (HlsCusAbsProductRepayment contractRepayment : hlsCusLonContractRepayments) {
                if (contractRepayment.getPlannedCalcDate().getTime() == repaymentCalcDateFrom.getTime()) {
                    isSysFlagPre = false;
                    //如果当前手工插入本金的日期和当前系统计算的利息重合 则为true
                    for (HlsCusAbsProductRepayment hlsCusLonContractRepayment : hlsCusLonContractRepayments2) {
                        if (hlsCusLonContractRepayment.getPlannedCalcDate().getTime() == repaymentCalcDateFrom.getTime()) {
                            existPrincipalInterestPre = true;
                            break;
                        }
                    }

                }
            }
            if (existPrincipalInterestPre) {
                isSysFlagPre = true;
            }

        }

        if (repaymentCalcDateTo.getTime() == endActiveDate.getTime()) {
            isSysFlagNext = false;
        } else {
            isSysFlagNext = true;
            for (HlsCusAbsProductRepayment contractRepayment : hlsCusLonContractRepayments) {
                if (contractRepayment.getPlannedCalcDate().getTime() == repaymentCalcDateTo.getTime()) {
                    isSysFlagNext = false;
                    //如果当前手工插入本金的日期和当前系统计算的利息重合 则为true
                    for (HlsCusAbsProductRepayment hlsCusLonContractRepayment : hlsCusLonContractRepayments2) {
                        if (hlsCusLonContractRepayment.getPlannedCalcDate().getTime() == repaymentCalcDateTo.getTime()) {
                            existPrincipalInterestNext = true;
                            break;
                        }
                    }
                }
            }

            if (existPrincipalInterestNext) {
                isSysFlagNext = true;
            }

        }
        return adjustDays;
    }


    /**
     * 剩余本金计算利息
     *
     * @param iRequest
     * @param repaymentCalcDateFrom
     * @param repaymentCalcDateTo
     * @param productQuotation
     * @param withdrawAmount
     * @param intRate
     * @param calcInterestYearDays
     * @return
     */
    //计算当期还款利息
    public Double getRepaymentAmount(IRequest iRequest, Date repaymentCalcDateFrom, Date repaymentCalcDateTo,
                                     HlsCusAbsProductQuotation productQuotation, Double withdrawAmount, Double intRate, String calcInterestYearDays,
                                     Date[] repaymentPrincipalDate) {
        Double resultAmount = 0D;
        Double sumRepaymentPrincipal = 0D;
        Date calcDateStart = repaymentCalcDateFrom;
        Long calcInterestDays = 0L;
        Calendar cStart = Calendar.getInstance();
        Calendar cEnd = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Long adjDays = 0L;


        HlsCusAbsProductRepayment hlsCusLonContractRepaymentTemp = new HlsCusAbsProductRepayment();
        hlsCusLonContractRepaymentTemp.setProductId(productQuotation.getProductId());
        hlsCusLonContractRepaymentTemp.setCfItem(HlsCusConstant.LON_REPAYMENT.PRINCIPAL_CF_ITEM);
        List<HlsCusAbsProductRepayment> hlsCusLonContractRepayments = productRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(hlsCusLonContractRepaymentTemp);

        for (int i = 0; i < hlsCusLonContractRepayments.size(); i++) {
            HlsCusAbsProductRepayment hlsCusLonContractRepayment = hlsCusLonContractRepayments.get(i);
            calcInterestDays = 0L;
            if (hlsCusLonContractRepayment.getPlannedDueDate().getTime() < repaymentCalcDateFrom.getTime()) {
                sumRepaymentPrincipal = sumRepaymentPrincipal + hlsCusLonContractRepayment.getPlannedDueAmount();
            }
            if (hlsCusLonContractRepayment.getPlannedDueDate().getTime() >= repaymentCalcDateFrom.getTime()
                    && hlsCusLonContractRepayment.getPlannedDueDate().getTime() <= repaymentCalcDateTo.getTime()) {
                cStart.setTime(calcDateStart);
                cEnd.setTime(hlsCusLonContractRepayment.getPlannedDueDate());
                calcInterestDays = calcInterestDays + (cEnd.getTimeInMillis() - cStart.getTimeInMillis()) / (1000 * 3600 * 24) + adjDays;
                if (repaymentPrincipalDate != null) {
                    calcInterestDays = calcInterestDays + daysAjust(productQuotation.getDueDate(), productQuotation.getWithdrawEndDate(),
                            repaymentPrincipalDate, cStart.getTime(), cEnd.getTime(), productQuotation.getProductId());
                }

                resultAmount = resultAmount + ((withdrawAmount - sumRepaymentPrincipal) * calcInterestDays * intRate / Integer.valueOf(calcInterestYearDays).intValue());
                sumRepaymentPrincipal = sumRepaymentPrincipal + hlsCusLonContractRepayment.getPlannedDueAmount();
                calcDateStart = hlsCusLonContractRepayment.getPlannedDueDate();
            }
            if (calcDateStart.getTime() >= repaymentCalcDateFrom.getTime()
                    && calcDateStart.getTime() < repaymentCalcDateTo.getTime()
                    && hlsCusLonContractRepayment.getPlannedDueDate().getTime() > repaymentCalcDateTo.getTime()) {
                cStart.setTime(calcDateStart);
                cEnd.setTime(repaymentCalcDateTo);
                calcInterestDays = calcInterestDays + (cEnd.getTimeInMillis() - cStart.getTimeInMillis()) / (1000 * 3600 * 24);
                if (repaymentPrincipalDate != null) {
                    calcInterestDays = calcInterestDays + daysAjust(productQuotation.getDueDate(), productQuotation.getWithdrawEndDate(),
                            repaymentPrincipalDate, cStart.getTime(), cEnd.getTime(), productQuotation.getProductId());
                }
                resultAmount = resultAmount + ((withdrawAmount - sumRepaymentPrincipal) * calcInterestDays * intRate / Integer.valueOf(calcInterestYearDays).intValue());

                calcDateStart = hlsCusLonContractRepayment.getPlannedDueDate();
            }
            adjDays = 0L;
        }
        for (int k = 0; k < repaymentPrincipalDate.length; k++) {
            if (repaymentPrincipalDate[k].getTime() == repaymentCalcDateTo.getTime()) {
                adjDays = 1L;
            }
        }
        if (resultAmount == 0) {
            cStart.setTime(repaymentCalcDateFrom);
            cEnd.setTime(repaymentCalcDateTo);
            calcInterestDays = calcInterestDays + (cEnd.getTimeInMillis() - cStart.getTimeInMillis()) / (1000 * 3600 * 24) + adjDays;
            calcInterestDays = calcInterestDays + daysAjust(productQuotation.getDueDate(), productQuotation.getWithdrawEndDate(),
                    repaymentPrincipalDate, repaymentCalcDateFrom, repaymentCalcDateTo, productQuotation.getProductId());
            resultAmount = (withdrawAmount - sumRepaymentPrincipal) * calcInterestDays * intRate / Integer.valueOf(calcInterestYearDays).intValue();
            adjDays = 0L;
        }

        return (double) (resultAmount);
    }

    /**
     * 计算XIRR
     *
     * @param iRequest
     * @param
     */
    @Override
    public void calXirr(IRequest iRequest, HlsCusAbsProductQuotation productQuotation) {

        HlsCusAbsProductRepayment contractRepayment = new HlsCusAbsProductRepayment();
        contractRepayment.setProductId(productQuotation.getProductId());
        List<HlsCusAbsProductRepayment> lonContractReps = productRepaymentMapper.selectRepaymentPlanOrderByRepaymentDate(contractRepayment);

        HlsCusAbsProFeeInfo feeInfo = new HlsCusAbsProFeeInfo();
        feeInfo.setSourceKey(productQuotation.getProductId());
        feeInfo.setSourceType(HlsCusConstant.ABS_PRO_TYPE.PRODUCT);
        List<HlsCusAbsProFeeInfo> absProFeeInfos = hlsCusAbsProFeeInfoService.selectProjectFeeInfo(iRequest, feeInfo, 1, 9999);

        double[] payments = new double[lonContractReps.size() + absProFeeInfos.size() + 1];

        Date[] dates = new Date[lonContractReps.size() + absProFeeInfos.size() + 1];
        for (int i = 0; i < lonContractReps.size(); i++) {
            if (HlsCusConstant.OUTFLOW.equalsIgnoreCase(lonContractReps.get(i).getCfDirection())) {
                payments[i] = -lonContractReps.get(i).getPlannedDueAmount();
                dates[i] = lonContractReps.get(i).getPlannedDueDate();
            } else {
                payments[i] = lonContractReps.get(i).getPlannedDueAmount();
                dates[i] = lonContractReps.get(i).getPlannedDueDate();
            }
        }
        for (int i = 0; i < absProFeeInfos.size(); i++) {
            payments[i + lonContractReps.size()] = -absProFeeInfos.get(i).getFeeAmount().doubleValue();
            dates[i + lonContractReps.size()] = absProFeeInfos.get(i).getPlanPayDate();
        }
        if (productQuotation.getDueAmount()!=null)
        {
            //防止精度问题
            payments[lonContractReps.size() + absProFeeInfos.size()] = productQuotation.getDueAmount();
            dates[lonContractReps.size() + absProFeeInfos.size()] = productQuotation.getDueDate();
            Double xirr = HlsCusXirr.Newtons_method(0.1, payments, dates);
            if (!xirr.isInfinite() && !xirr.isNaN()) {
                productQuotation.setXirr((double) Math.round(xirr * 1000000) / 1000000);
            } else {
                productQuotation.setXirr(0D);
            }
            productQuotationService.updateByPrimaryKeySelective(iRequest, productQuotation);

        }
    }


    /**
     * 比较日期相差的天数
     *
     * @param start
     * @param from
     * @return
     */
    private static int compareDays(Date start, Date from) {
        if (null == start || null == from) {
            return -1;
        }
        long intervalMilli = Math.abs(start.getTime() - from.getTime());//取绝对值
        return (int) (intervalMilli / (24 * 60 * 60 * 1000));
    }

    public Double transfor(Double amount) {
        BigDecimal bg = new BigDecimal(amount);
        double num = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return num;
    }


    @Override
    public HlsCusAbsProduct saveBuyBackData(IRequest request, HlsCusAbsPkg pkg) {
        if (CollectionUtils.isNotEmpty(pkg.getHlsCusAbsProductBuybacks())) {
            for (HlsCusAbsProductBuyback productBuyback : pkg.getHlsCusAbsProductBuybacks()) {
                productBuyback.setProductId(pkg.getHlsCusAbsProduct().getProductId());
                if (productBuyback.getBuybackId() == null) {
                    productBuyback.set__status(DTOStatus.ADD);
                } else {
                    productBuyback.set__status(DTOStatus.UPDATE);
                }
            }
            hlsCusAbsProductBuybackService.batchUpdate(request, pkg.getHlsCusAbsProductBuybacks());
        }

        updateAbsAttachemntFile(request, pkg);

        return pkg.getHlsCusAbsProduct();
    }


    @Override
    public HlsCusAbsProduct submitBuyBackData(IRequest request, HlsCusAbsPkg pkg) throws HlsCusException {
        HlsCusAbsProduct absProduct = self().saveBuyBackData(request, pkg);
        absProduct.setBuybackStatus(HlsCusConstant.WORKFLOW_STATUS.APPROVING);
        self().updateByPrimaryKeySelective(request,absProduct);
        databaseLockProvider.lock(absProduct);
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(request.getUserId());
        if (ObjectUtils.isEmpty(employee)) {
            throw new HlsCusException("获取提交人失败");
        }
        String employeeCode = employee.getEmployeeCode();
        request.setEmployeeCode(employeeCode);

        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(HlsCusConstant.WORKFLOW_PARAMS.WORKFLOW_TYPE, HlsCusConstant.ABS_WFL.BUYBACK_WFL);
        List<HlsCusAbsProduct> productList = new ArrayList<>();
        productList.add(absProduct);
        activitiStartService.start(request, productList, params);

        return absProduct;
    }

    //abs利息分摊，按天计息
    @Override
    public void absProductIncome(IRequest request, HlsCusAbsProduct hlsCusAbsProduct) throws HlsCusException {
        HlsCusAbsProduct absProduct = new HlsCusAbsProduct();
        absProduct.setProductId(hlsCusAbsProduct.getProductId());
        absProduct = self().selectByPrimaryKey(request, absProduct);
        Date calcStartDate = absProduct.getDueDateBegin();
        Date lastRepaymentDate = absProduct.getDueDateBegin();
        //删除未确认分摊
        HlsCusGldLonContractFinCost hlsCusGldLonContractFinCostTemp = new HlsCusGldLonContractFinCost();
        hlsCusGldLonContractFinCostTemp.setProductId(absProduct.getProductId());
        hlsCusGldLonContractFinCostTemp.setPostFlag("N");
        List<HlsCusGldLonContractFinCost> list = gldLonContractFinCostService.select(request, hlsCusGldLonContractFinCostTemp, 1, 9999999);
        gldLonContractFinCostService.batchDelete(list);
        HlsCusAbsProductCollection hlsCusAbsProductCollection = new HlsCusAbsProductCollection();
        hlsCusAbsProductCollection.setProductId(absProduct.getProductId());
        List<HlsCusAbsProductCollection> hlsCusAbsProductCollectionLists = hlsCusAbsProductCollectionService.select(request, hlsCusAbsProductCollection, 1, 99999999);
        if (hlsCusAbsProductCollectionLists.size() > 0 && calcStartDate != null) {
            int month;
            Calendar cStart = Calendar.getInstance();
            Calendar cEnd = Calendar.getInstance();
            HlsCusGldLonContractFinCost hlsCusGldLonContractFinCost = new HlsCusGldLonContractFinCost();
            for (HlsCusAbsProductCollection absProductCollection : hlsCusAbsProductCollectionLists) {
                Double sumFinIncome = 0D;
                Double sumFinIncomeInclud = 0D;
                Double finIncome = 0D;
                Double finIncomeInclud = 0D;
                Double cashInterest = 0D;
                Double netCashInterest = 0D;
                Long calcDays;
                String periodName;
                //计算分摊的现金流金额,不含税金额
                HlsCusAbsProductCashDetail hlsCusAbsProductCashDetail = new HlsCusAbsProductCashDetail();
                hlsCusAbsProductCashDetail.setCollectionId(absProductCollection.getCollectionId());
                List<HlsCusAbsProductCashDetail> absProductCashDetailList = hlsCusAbsProductCashDetailService.select(request, hlsCusAbsProductCashDetail, 1, 99999999);
                if (absProductCashDetailList.size() == 0) {
                    continue;
                }
                for (HlsCusAbsProductCashDetail absProductCashDetail : absProductCashDetailList) {
                    cashInterest = CalculateUtil.add(cashInterest, absProductCashDetail.getPlanCashInterest().doubleValue());
                }
                netCashInterest = (double) Math.round(cashInterest / ABS_INCOMES_RATE * 100) / 100;
                cStart.setTime(calcStartDate);
                cEnd.setTime(absProductCollection.getCashDate());
                cEnd.add(Calendar.DAY_OF_MONTH, -1);
                month = (cEnd.get(Calendar.YEAR) - cStart.get(Calendar.YEAR)) * 12 + cEnd.get(Calendar.MONTH) - cStart.get(Calendar.MONTH);
                Calendar calcStart = Calendar.getInstance();
                Calendar calcEnd = Calendar.getInstance();
                calcStart.setTime(calcStartDate);
                for (int i = 0; i <= month; i++) {
                    Calendar calcCal = Calendar.getInstance();
                    if (i == 0 && month == 0) {
                        calcEnd.setTime(absProductCollection.getCashDate());
                        calcEnd.add(Calendar.DAY_OF_MONTH, -1);
                        calcDays = hlsCusLonContractWithdrawService.getCalcDays(calcStart.getTime(), calcEnd.getTime());
                        sumFinIncome = getSumFinIncomeInclud(request, absProductCollection.getCollectionId(), absProduct.getProductId(), "N");
                        sumFinIncomeInclud = getSumFinIncomeInclud(request, absProductCollection.getCollectionId(), absProduct.getProductId(), "Y");
                        finIncome = netCashInterest - sumFinIncome;
                        finIncomeInclud = cashInterest - sumFinIncomeInclud;
                    } else if (i == 0 && month != 0) {
                        calcEnd.setTime(hlsCusLonContractWithdrawService.getMonthEndDate(calcStartDate));
                        calcDays = hlsCusLonContractWithdrawService.getCalcDays(calcStart.getTime(), calcEnd.getTime());
                        calcCal.setTime(absProductCollection.getCashDate());
                        calcCal.add(Calendar.DAY_OF_MONTH, -1);
                        finIncome = (double) Math.round(netCashInterest * calcDays / hlsCusLonContractWithdrawService.getCalcDays(lastRepaymentDate, calcCal.getTime()) * 100) / 100;
                        finIncomeInclud = (double) Math.round(cashInterest * calcDays / hlsCusLonContractWithdrawService.getCalcDays(lastRepaymentDate, calcCal.getTime()) * 100) / 100;
                    } else if (i == month && month > 0) {
                        calcEnd.setTime(absProductCollection.getCashDate());
                        calcEnd.add(Calendar.DAY_OF_MONTH, -1);
                        calcDays = hlsCusLonContractWithdrawService.getCalcDays(calcStart.getTime(), calcEnd.getTime());
                        sumFinIncome = getSumFinIncomeInclud(request, absProductCollection.getCollectionId(), absProduct.getProductId(), "N");
                        sumFinIncomeInclud = getSumFinIncomeInclud(request, absProductCollection.getCollectionId(), absProduct.getProductId(), "Y");
                        finIncome = netCashInterest - sumFinIncome;
                        finIncomeInclud = cashInterest - sumFinIncomeInclud;
                    } else {
                        calcEnd.setTime(hlsCusLonContractWithdrawService.getMonthEndDate(calcStartDate));
                        calcDays = hlsCusLonContractWithdrawService.getCalcDays(calcStart.getTime(), calcEnd.getTime());
                        calcCal.setTime(absProductCollection.getCashDate());
                        calcCal.add(Calendar.DAY_OF_MONTH, -1);
                        finIncome = (double) Math.round(netCashInterest * calcDays / hlsCusLonContractWithdrawService.getCalcDays(lastRepaymentDate, calcCal.getTime()) * 100) / 100;
                        finIncomeInclud = (double) Math.round(cashInterest * calcDays / hlsCusLonContractWithdrawService.getCalcDays(lastRepaymentDate, calcCal.getTime()) * 100) / 100;
                    }
                    if (cEnd.get(Calendar.MONTH) + 1 < 10) {
                        periodName = cEnd.get(Calendar.YEAR) + "-0" + (cEnd.get(Calendar.MONTH) + 1);

                    } else {
                        periodName = cEnd.get(Calendar.YEAR) + "-" + (cEnd.get(Calendar.MONTH) + 1);
                    }
                    hlsCusGldLonContractFinCost.setFinanceCostId(null);
                    hlsCusGldLonContractFinCost.setCompanyId(absProduct.getCompanyId());
                    hlsCusGldLonContractFinCost.setContractId(ABS_INCOMES_LONG_INIT);
                    hlsCusGldLonContractFinCost.setRepaymentId(ABS_INCOMES_LONG_INIT);
                    hlsCusGldLonContractFinCost.setWithdrawId(ABS_INCOMES_LONG_INIT);
                    hlsCusGldLonContractFinCost.setPeriodName(periodName);
                    hlsCusGldLonContractFinCost.setStartDate(cStart.getTime());
                    hlsCusGldLonContractFinCost.setEndDate(cEnd.getTime());
                    hlsCusGldLonContractFinCost.setDays(calcDays);
                    hlsCusGldLonContractFinCost.setFinanceCost(finIncome);
                    hlsCusGldLonContractFinCost.setSourceType(ABS_INTEREST_INCOMES_SOURCE_TYPE);
                    hlsCusGldLonContractFinCost.setSourceId(absProduct.getProductId());
                    hlsCusGldLonContractFinCost.setPostFlag("N");
                    hlsCusGldLonContractFinCost.setFinanceIncomeInclud(finIncomeInclud);
                    hlsCusGldLonContractFinCost.setFinanceIncomeVat(CalculateUtil.sub(finIncomeInclud, finIncome));
                    hlsCusGldLonContractFinCost.setProductId(absProduct.getProductId());
                    hlsCusGldLonContractFinCost.setAbsFeeId(absProductCollection.getCollectionId());
                    HlsCusGldLonContractFinCost hlsCusGldLonContractFinCostExists = new HlsCusGldLonContractFinCost();
                    hlsCusGldLonContractFinCostExists.setAbsFeeId(absProductCollection.getCollectionId());
                    hlsCusGldLonContractFinCostExists.setProductId(absProductCollection.getProductId());
                    hlsCusGldLonContractFinCostExists.setStartDate(cStart.getTime());
                    hlsCusGldLonContractFinCostExists.setEndDate(cEnd.getTime());
                    hlsCusGldLonContractFinCostExists.setSourceType(ABS_INTEREST_INCOMES_SOURCE_TYPE);
                    List<HlsCusGldLonContractFinCost> hlsCusGldLonContractFinCostExistsList = gldLonContractFinCostService.select(request, hlsCusGldLonContractFinCostExists, 1, 999999);
                    if (hlsCusGldLonContractFinCostExistsList.size() == 0) {
                        gldLonContractFinCostService.insertSelective(request, hlsCusGldLonContractFinCost);
                    }
                    calcEnd.add(Calendar.DAY_OF_MONTH, 1);
                    calcStartDate = calcEnd.getTime();
                    calcStart.setTime(calcStartDate);
                }
                cEnd.add(Calendar.DAY_OF_MONTH, 1);
                calcStartDate = cEnd.getTime();
                lastRepaymentDate = absProductCollection.getCashDate();
            }
        }
        calculateAbsOtherFeeIncomes(request, hlsCusAbsProduct);
    }
    //获取日期之间计提总和
    private Double getSumFinIncomeInclud(IRequest request, Long collectionId, Long produectId, String vatFlag) {
        Double result = 0D;
        HlsCusGldLonContractFinCost hlsCusGldLonContractFinCostTemp = new HlsCusGldLonContractFinCost();
        hlsCusGldLonContractFinCostTemp.setProductId(produectId);
        hlsCusGldLonContractFinCostTemp.setAbsFeeId(collectionId);
        hlsCusGldLonContractFinCostTemp.setSourceType(ABS_INTEREST_INCOMES_SOURCE_TYPE);
        List<HlsCusGldLonContractFinCost> list = gldLonContractFinCostService.select(request, hlsCusGldLonContractFinCostTemp, 1, 9999999);
        if (list.size() > 0) {
            for (HlsCusGldLonContractFinCost hlsCusGldLonContractFinCost : list
                    ) {
                if (vatFlag.equals("Y")) {
                    result = result + hlsCusGldLonContractFinCost.getFinanceIncomeInclud();
                } else {
                    result = result + hlsCusGldLonContractFinCost.getFinanceCost();
                }
            }
        }
        return result;
    }
    //abs其他费用按实率分摊
    public void calculateAbsOtherFeeIncomes(IRequest request, HlsCusAbsProduct hlsCusAbsProduct) {
        HlsCusAbsProductStructure hlsCusAbsProductStructure = new HlsCusAbsProductStructure();
        hlsCusAbsProductStructure.setProductId(hlsCusAbsProduct.getProductId());
        List<HlsCusAbsProductStructure> hlsCusAbsProductStructureList = hlsCusAbsProductStructureService.select(request, hlsCusAbsProductStructure, 1, 999999);
        Double realRate = getRealRate(request, hlsCusAbsProductStructureList);
        //获取分摊费用
        HlsCusAbsProFeeInfo hlsCusAbsProFeeInfo = new HlsCusAbsProFeeInfo();
        hlsCusAbsProFeeInfo.setSourceKey(hlsCusAbsProduct.getProductId());
        hlsCusAbsProFeeInfo.setSourceType(ABS_TYPE);
        List<HlsCusAbsProFeeInfo> hlsCusAbsProFeeInfoList = hlsCusAbsProFeeInfoService.select(request, hlsCusAbsProFeeInfo, 1, 999999);

        for (int i = 0; i < hlsCusAbsProFeeInfoList.size(); i++) {
            HlsCusAbsProFeeInfo absProFeeInfo = hlsCusAbsProFeeInfoList.get(i);
            if (absProFeeInfo.getShareType().equals(ABS_OTHER_INCOMES_ONE_TIME)) {
                calculateAbsOneTimeIncomes(request, absProFeeInfo, hlsCusAbsProduct);
            } else {
                calculateAbsRealRateIncomes(request, absProFeeInfo, hlsCusAbsProduct, realRate);

            }
        }
    }
    public Double getRealRate(IRequest request, List<HlsCusAbsProductStructure> hlsCusAbsProductStructureList) {
        Double totalPredictRate = 0D;
        Double realRate = 0D;
        for (HlsCusAbsProductStructure absProductStructure : hlsCusAbsProductStructureList) {
            if (!absProductStructure.getProjectStructure().equals("INFERIOR")) {
                totalPredictRate = CalculateUtil.add(totalPredictRate, absProductStructure.getPredictRate().doubleValue());
            }
        }
        for (HlsCusAbsProductStructure absProductStructure : hlsCusAbsProductStructureList) {
            if (!absProductStructure.getProjectStructure().equals("INFERIOR")) {
                realRate = realRate + (double) Math.round(absProductStructure.getPredictRate().doubleValue() / totalPredictRate * absProductStructure.getStructurePrecent() * 100) / 100;
            }
        }
        return realRate;
    }

    public void calculateAbsOneTimeIncomes(IRequest request, HlsCusAbsProFeeInfo absProFeeInfo, HlsCusAbsProduct hlsCusAbsProduct) {
        HlsCusGldLonContractFinCost hlsCusGldLonContractFinCost = new HlsCusGldLonContractFinCost();
        hlsCusGldLonContractFinCost.setSourceType(ABS_OTHER_INCOMES_SOURCE_TYPE);
        hlsCusGldLonContractFinCost.setCfItem(absProFeeInfo.getCfItem());
        hlsCusGldLonContractFinCost.setProductId(hlsCusAbsProduct.getProductId());
        hlsCusGldLonContractFinCost.setAbsFeeId(absProFeeInfo.getFeeInfoId());
        List<HlsCusGldLonContractFinCost> hlsCusGldLonContractFinCostLists = gldLonContractFinCostService.select(request, hlsCusGldLonContractFinCost, 1, 99999);
        if (hlsCusGldLonContractFinCostLists.size() == 0) {
            hlsCusGldLonContractFinCost.setFinanceCostId(null);
            hlsCusGldLonContractFinCost.setCompanyId(hlsCusAbsProduct.getCompanyId());
            hlsCusGldLonContractFinCost.setContractId(ABS_INCOMES_LONG_INIT);
            hlsCusGldLonContractFinCost.setRepaymentId(ABS_INCOMES_LONG_INIT);
            hlsCusGldLonContractFinCost.setWithdrawId(ABS_INCOMES_LONG_INIT);
            hlsCusGldLonContractFinCost.setPeriodName(absProFeeInfo.getPlanPayDate().getYear() + "-" + absProFeeInfo.getPlanPayDate().getMonth());
            hlsCusGldLonContractFinCost.setStartDate(absProFeeInfo.getPlanPayDate());
            hlsCusGldLonContractFinCost.setEndDate(absProFeeInfo.getPlanPayDate());
            hlsCusGldLonContractFinCost.setDays(1L);
            hlsCusGldLonContractFinCost.setFinanceCost(absProFeeInfo.getFeeAmount().doubleValue());
            hlsCusGldLonContractFinCost.setSourceType(ABS_OTHER_INCOMES_SOURCE_TYPE);
            hlsCusGldLonContractFinCost.setSourceId(hlsCusAbsProduct.getProductId());
            hlsCusGldLonContractFinCost.setPostFlag("N");
            hlsCusGldLonContractFinCost.setFinanceIncomeInclud(absProFeeInfo.getFeeAmount().doubleValue());
            hlsCusGldLonContractFinCost.setFinanceIncomeVat(ABS_INCOMES_DOUBLE_INIT);
            hlsCusGldLonContractFinCost.setProductId(hlsCusAbsProduct.getProductId());
            hlsCusGldLonContractFinCost.setAbsFeeId(absProFeeInfo.getFeeInfoId());
            gldLonContractFinCostService.insertSelective(request, hlsCusGldLonContractFinCost);
        }

    }
    public void calculateAbsRealRateIncomes(IRequest request, HlsCusAbsProFeeInfo absProFeeInfo, HlsCusAbsProduct hlsCusAbsProduct, Double realRate) {
        //没有重新分摊(复用中间表，fee_info_id=withdraw_id,product_id=contract_id,用cf_item判断)
        HlsCusWithdrawRepayment hlsCusWithdrawRepaymentTemp = new HlsCusWithdrawRepayment();
        hlsCusWithdrawRepaymentTemp.setCfItem(absProFeeInfo.getCfItem());
        hlsCusWithdrawRepaymentTemp.setWithdrawId(absProFeeInfo.getFeeInfoId());
        hlsCusWithdrawRepaymentTemp.setContractId(hlsCusAbsProduct.getProductId());

        List<HlsCusWithdrawRepayment> hlsCusWithdrawRepaymentTempList = hlsCusWithdrawRepaymentMapper.select(hlsCusWithdrawRepaymentTemp);
        if (hlsCusWithdrawRepaymentTempList.size() == 0) {

            Double productIncomeAmount = hlsCusAbsProductMapper.queryProductIncomeAmount(hlsCusAbsProduct.getProductId());
            hlsCusAbsProduct.setCfItem(absProFeeInfo.getCfItem());
            Double incomeOtherFee = 0D;
            incomeOtherFee = hlsCusAbsProductMapper.queryProductFeeAmountbyCfItem(hlsCusAbsProduct).doubleValue();
            if (incomeOtherFee == 0) {
                return;
            }
            Calendar start = (Calendar) calendar.clone();
            Calendar end = (Calendar) calendar.clone();
            Double cashflowAmount = 0D;
            //期初摊余成本
            Double amortizedCost = 0D;
            //利息及融资费用
            Double interestFinancingFee = 0D;
            //合同利息
            Double contractInterest = 0D;
            //分摊费用
            Double financeIncome = ABS_INCOMES_DOUBLE_INIT;
            //新建数组，保存计算IRR的数据
            List<Double> irrArray = new ArrayList<>();

            List<HlsCusAbsProductCollection> incomeAbsProductCollectionList = hlsCusAbsProductCollectionMapper.selectProductCollectionInfoByCfItem(hlsCusAbsProduct.getProductId(), absProFeeInfo.getCfItem());
            //获取分摊结束时间
            Date calcEndDate = incomeAbsProductCollectionList.get(0).getCashDate();
            //分摊次数
            int incomeTimes = getMonthSpace(hlsCusAbsProduct.getDueDateBegin(), calcEndDate);
            for (int i = 0; i < incomeTimes; i++) {
                Date from;//日期从
                //    Date to;//日期到
                start.clear();
                end.clear();
                from = hlsCusAbsProduct.getDueDateBegin();
                start.setTime(from);
                start.add(Calendar.MONTH, i);
                end.setTime(start.getTime());
                end.add(Calendar.MONTH, 1);
                end.add(Calendar.DAY_OF_MONTH, -1);
                if (i + 1 == incomeTimes) {
                    start.clear();
                    end.clear();
                    from = calcEndDate;
                    start.setTime(from);
                    end.setTime(from);
                    //end.add(Calendar.DAY_OF_MONTH,-1);
                }
                if (end.getTime().getTime() >= calcEndDate.getTime() && i + 1 != incomeTimes) {
                    end.clear();
                    end.setTime(calcEndDate);
                    end.add(Calendar.DAY_OF_MONTH, -1);
                }
                //期间的剩余本金
                Double remainingPrincipalTemp = 0D;
                for (HlsCusAbsProductCollection dto : incomeAbsProductCollectionList) {
                    if (start.getTime().compareTo(dto.getCashDate()) == 1) {
                        remainingPrincipalTemp = remainingPrincipalTemp + hlsCusAbsProductCashDetailMapper.queryRemainingPrincipalSum(hlsCusAbsProduct.getProductId(), dto.getCollectionId()).doubleValue();
                    }
                }
                Double remainingPrincipal = CalculateUtil.sub(productIncomeAmount, remainingPrincipalTemp);
                Double chargePriAmount = 0D;
                //  Double chargePriAmount = hlsCusLonContractRepaymentMapper.selectAmountSum(hlsCusLonContractWithdraw.getWithdrawId(), start.getTime(), end.getTime());
                for (HlsCusAbsProductCollection dto : incomeAbsProductCollectionList) {
                    if (start.getTime().compareTo(dto.getCashDate()) == -1 && dto.getCashDate().compareTo(end.getTime()) == -1) {
                        chargePriAmount = chargePriAmount + dto.getIncomeFee() + hlsCusAbsProductCashDetailMapper.queryRemainingPrincipalSum(hlsCusAbsProduct.getProductId(), dto.getCollectionId()).doubleValue();
                    }
                }
                if (i == 0) {
                    contractInterest = ABS_INCOMES_DOUBLE_INIT;

                    cashflowAmount = CalculateUtil.sub(CalculateUtil.sub(productIncomeAmount, chargePriAmount), contractInterest);
                } else {
                    contractInterest = transforIncome(CalculateUtil.div(CalculateUtil.mul(remainingPrincipal, realRate), YEAR_MONTH));

                    cashflowAmount = CalculateUtil.sub(CalculateUtil.sub(ABS_INCOMES_DOUBLE_INIT, chargePriAmount), contractInterest);

                }
                irrArray.add(cashflowAmount);
                //插入分摊临时表
                HlsCusWithdrawRepayment hlsCusWithdrawRepayment = new HlsCusWithdrawRepayment();
                hlsCusWithdrawRepayment.setWithdrawId(absProFeeInfo.getFeeInfoId());
                hlsCusWithdrawRepayment.setCfItem(absProFeeInfo.getCfItem());
                hlsCusWithdrawRepayment.setFinIncomeDate(start.getTime());
                hlsCusWithdrawRepayment.setCashflowAmount(cashflowAmount);
                hlsCusWithdrawRepayment.setRemainingPrincipal(remainingPrincipal);
                hlsCusWithdrawRepayment.setContractInterest(contractInterest);
                hlsCusWithdrawRepayment.setContractId(hlsCusAbsProduct.getProductId());
                hlsCusWithdrawRepaymentMapper.insert(hlsCusWithdrawRepayment);
            }
            //计算irr
            Double irr = IrrUtil.irr(irrArray) * YEAR_MONTH;//irr利率
            //计算期初摊余成本&利息及融资费用
            HlsCusWithdrawRepayment repaymentDto = new HlsCusWithdrawRepayment();
            repaymentDto.setContractId(absProFeeInfo.getFeeInfoId());
            repaymentDto.setWithdrawId(hlsCusAbsProduct.getProductId());
            repaymentDto.setCfItem(absProFeeInfo.getCfItem());
            repaymentDto.setSortname("finIncomeDate");
            repaymentDto.setSortorder("asc");
            List<HlsCusWithdrawRepayment> hlsCusWithdrawRepaymentList = hlsCusWithdrawRepaymentMapper.select(repaymentDto);
            for (int i = 0; i < hlsCusWithdrawRepaymentList.size(); i++) {
                Calendar tempDate = (Calendar) calendar.clone();

                HlsCusWithdrawRepayment withdrawRepayment = hlsCusWithdrawRepaymentList.get(i);
                tempDate.clear();
                tempDate.setTime(withdrawRepayment.getFinIncomeDate());
                if (i < hlsCusWithdrawRepaymentList.size() - 1) {
                    tempDate.add(Calendar.MONTH, 1);
                    tempDate.add(Calendar.DAY_OF_MONTH, -1);
                }
                if (tempDate.getTime().getTime() >= calcEndDate.getTime() && i + 1 != hlsCusWithdrawRepaymentList.size()) {
                    tempDate.clear();
                    tempDate.setTime(calcEndDate);
                    tempDate.add(Calendar.DAY_OF_MONTH, -1);
                }
                Double chargePriAmount = 0D;
                //  Double chargePriAmount = hlsCusLonContractRepaymentMapper.selectAmountSum(hlsCusLonContractWithdraw.getWithdrawId(), start.getTime(), end.getTime());
                for (HlsCusAbsProductCollection dto : incomeAbsProductCollectionList) {
                    if (start.getTime().compareTo(dto.getCashDate()) == -1 && dto.getCashDate().compareTo(end.getTime()) == -1) {
                        chargePriAmount = chargePriAmount + dto.getIncomeFee() + hlsCusAbsProductCashDetailMapper.queryRemainingPrincipalSum(hlsCusAbsProduct.getProductId(), dto.getCollectionId()).doubleValue();
                    }
                }
                if (i == 0) {
                    amortizedCost = CalculateUtil.sub(productIncomeAmount, chargePriAmount);
                    interestFinancingFee = ABS_INCOMES_DOUBLE_INIT;
                } else {
                    interestFinancingFee = transforIncome(CalculateUtil.div(CalculateUtil.mul(hlsCusWithdrawRepaymentList.get(i - 1).getAmortizedCost(), irr), 12D));
                    amortizedCost = CalculateUtil.sub(CalculateUtil.sub(CalculateUtil.add(hlsCusWithdrawRepaymentList.get(i - 1).getAmortizedCost(), interestFinancingFee), withdrawRepayment.getContractInterest()), chargePriAmount);
                }
                if (i + 1 == hlsCusWithdrawRepaymentList.size()) {
                    withdrawRepayment.setFinanceIncome(CalculateUtil.sub(incomeOtherFee, financeIncome));
                } else {
                    withdrawRepayment.setFinanceIncome(transforIncome(CalculateUtil.sub(interestFinancingFee, withdrawRepayment.getContractInterest())));
                }
                financeIncome = CalculateUtil.add(CalculateUtil.sub(interestFinancingFee, withdrawRepayment.getContractInterest()), financeIncome);
                //   withdrawRepayment.setFinanceIncome(irr);
                withdrawRepayment.setAmortizedCost(amortizedCost);
                withdrawRepayment.setInterestFinancingFee(interestFinancingFee);
                withdrawRepayment.setIrr(irr);
                hlsCusWithdrawRepaymentMapper.updateByPrimaryKey(withdrawRepayment);
            }
            //更新到分摊表
            HlsCusWithdrawRepayment hlsCusWithdrawRepaymentFinCost = new HlsCusWithdrawRepayment();
            hlsCusWithdrawRepaymentFinCost.setContractId(absProFeeInfo.getFeeInfoId());
            hlsCusWithdrawRepaymentFinCost.setWithdrawId(hlsCusAbsProduct.getProductId());
            hlsCusWithdrawRepaymentFinCost.setCfItem(absProFeeInfo.getCfItem());

            List<HlsCusWithdrawRepayment> hlsCusWithdrawRepaymentFinCostList = hlsCusWithdrawRepaymentMapper.select(hlsCusWithdrawRepaymentFinCost);
            Calendar finStart = (Calendar) calendar.clone();
            Calendar finEnd = (Calendar) calendar.clone();
            String periodName;
            for (int i = 1; i < hlsCusWithdrawRepaymentFinCostList.size(); i++) {
                Long Days = 0L;
                finEnd.clear();
                finEnd.setTime(hlsCusWithdrawRepaymentFinCostList.get(i).getFinIncomeDate());
                HlsCusGldLonContractFinCost hlsCusGldLonContractFinCost = new HlsCusGldLonContractFinCost();
                hlsCusGldLonContractFinCost.setWithdrawId(ABS_INCOMES_LONG_INIT);
                hlsCusGldLonContractFinCost.setContractId(ABS_INCOMES_LONG_INIT);
                hlsCusGldLonContractFinCost.setProductId(hlsCusAbsProduct.getProductId());
                hlsCusGldLonContractFinCost.setAbsFeeId(absProFeeInfo.getFeeInfoId());
                hlsCusGldLonContractFinCost.setFinanceCost(hlsCusWithdrawRepaymentFinCostList.get(i).getFinanceIncome());
                hlsCusGldLonContractFinCost.setPostFlag("N");
                hlsCusGldLonContractFinCost.setCfItem(absProFeeInfo.getCfItem());
                hlsCusGldLonContractFinCost.setSourceType(ABS_OTHER_INCOMES_REAL_RATE);
                hlsCusGldLonContractFinCost.setCompanyId(hlsCusAbsProduct.getCompanyId());
                if (finEnd.get(Calendar.MONTH) + 1 < 10) {
                    periodName = finEnd.get(Calendar.YEAR) + "-0" + (finEnd.get(Calendar.MONTH) + 1);

                } else {
                    periodName = finEnd.get(Calendar.YEAR) + "-" + (finEnd.get(Calendar.MONTH) + 1);
                }
                hlsCusGldLonContractFinCost.setPeriodName(periodName);
                if (i == 1) {
                    hlsCusGldLonContractFinCost.setStartDate(hlsCusWithdrawRepaymentFinCostList.get(i - 1).getFinIncomeDate());
                    hlsCusGldLonContractFinCost.setEndDate(hlsCusWithdrawRepaymentFinCostList.get(i).getFinIncomeDate());
                    Days = getCalcDays(hlsCusWithdrawRepaymentFinCostList.get(i - 1).getFinIncomeDate(), hlsCusWithdrawRepaymentFinCostList.get(i).getFinIncomeDate());
                    hlsCusGldLonContractFinCost.setDays(Days);
                } else {
                    finStart.clear();
                    finStart.setTime(hlsCusWithdrawRepaymentFinCostList.get(i - 1).getFinIncomeDate());
                    finStart.add(Calendar.DAY_OF_MONTH, 1);
                    hlsCusGldLonContractFinCost.setStartDate(finStart.getTime());
                    hlsCusGldLonContractFinCost.setEndDate(hlsCusWithdrawRepaymentFinCostList.get(i).getFinIncomeDate());
                    Days = getCalcDays(finStart.getTime(), hlsCusWithdrawRepaymentFinCostList.get(i).getFinIncomeDate());
                    hlsCusGldLonContractFinCost.setDays(Days);
                }
                gldLonContractFinCostService.insertSelective(request, hlsCusGldLonContractFinCost);

            }
        }
    }

//计算分摊次数

    /**
     * @param date1 <Date>
     * @param date2 <Date>
     * @return int
     */
    public int getMonthSpace(Date date1, Date date2) {

        int result = 0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();

        c1.setTime(date1);
        c2.setTime(date2);

        int yearInterval = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
        if (c2.get(Calendar.MONTH) < c1.get(Calendar.MONTH) || c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) && c2.get(Calendar.DAY_OF_MONTH) < c1.get(Calendar.DAY_OF_MONTH))
            yearInterval--;
        int monthInterval = (c2.get(Calendar.MONTH) + 12) - c1.get(Calendar.MONTH);
        if (c2.get(Calendar.DAY_OF_MONTH) > c1.get(Calendar.DAY_OF_MONTH)) monthInterval++;
        monthInterval %= 12;
        return yearInterval * 12 + monthInterval + 1;
    }

    public Double transforIncome(Double amount) {
        BigDecimal bg = new BigDecimal(amount);
        double num = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return num;
    }

    /**
     * 获取两个日期之间相差天数
     *
     * @param calcStartDate
     * @param calcEndDate
     * @author zhangyu
     */
    public Long getCalcDays(Date calcStartDate, Date calcEndDate) {
        Long result;
        Calendar calcStart = Calendar.getInstance();
        Calendar calcEnd = Calendar.getInstance();
        calcStart.setTime(calcStartDate);
        calcEnd.setTime(calcEndDate);

        result = (calcEnd.getTimeInMillis() - calcStart.getTimeInMillis()) / (1000 * 3600 * 24) + 1;

        return result;
    }



    @Override
    public void confirmCollectionTemp(IRequest request, HlsCusAbsProduct hlsCusAbsProduct) {

        hlsCusAbsProductCollectionService.deleteNotConfirmByProductId(hlsCusAbsProduct.getProductId(),"TEMP");

        hlsCusAbsProductCashDetailService.deleteCashDetailByProduct(hlsCusAbsProduct.getProductId(),"TEMP");

        HlsCusAbsProductCollection productCollection = new HlsCusAbsProductCollection();
        productCollection.setProductId(hlsCusAbsProduct.getProductId());
        productCollection.setDataClass(HlsCusConstant.DATA_CLASS.NORMAL);
        productCollection.setConfirmFlag(HlsCusConstant.FLAG.N);
        List<HlsCusAbsProductCollection> productCollections = hlsCusAbsProductCollectionService.select(request, productCollection, 1, 999);
        if (CollectionUtils.isNotEmpty(productCollections)) {
            for (HlsCusAbsProductCollection collection : productCollections) {
                Long normalCollectionId = collection.getCollectionId();
                collection.setCollectionId(null);
                productCollection.setDataClass("TEMP");
                collection.set__status(DTOStatus.ADD);
                collection.setCreationDate(null);
                collection.setLastUpdateDate(null);
                collection.setCreatedBy(request.getUserId());
                collection.setLastUpdatedBy(request.getUserId());
                hlsCusAbsProductCollectionService.insertSelective(request,collection);

                HlsCusAbsProductCashDetail cashDetail = new HlsCusAbsProductCashDetail();
                cashDetail.setCollectionId(normalCollectionId);
                List<HlsCusAbsProductCashDetail> cashDetails = hlsCusAbsProductCashDetailService.select(request, cashDetail, 1, 999);
                for (HlsCusAbsProductCashDetail detail : cashDetails) {
                    detail.setCashDetailId(null);
                    detail.setCollectionId(collection.getCollectionId());
                    detail.setProductId(collection.getProductId());
                    detail.setCreationDate(null);
                    detail.setLastUpdateDate(null);
                    detail.setCreatedBy(request.getUserId());
                    detail.setLastUpdatedBy(request.getUserId());

                    hlsCusAbsProductCashDetailService.insertSelective(request,detail);
                }
            }
        }

    }

    @Override
    public void endAbsProduct(IRequest request, HlsCusAbsProduct hlsCusAbsProduct) throws HlsCusException {

        int notCollectionCount = hlsCusAbsProductCollectionService.selectNotConfirmCollectionCount(hlsCusAbsProduct.getProductId());

        if(notCollectionCount>0){

            throw new HlsCusException("归集转付或兑付未全部确认！请检查");
        }

        int notRepaymentCount = productRepaymentService.selectNotFullRepaymentCount(hlsCusAbsProduct.getProductId());


        if(notRepaymentCount>0){

            throw new HlsCusException("本息还款计划未全部核销！请检查");
        }

        int notReleaseCount = hlsCusAbsProductSubscribeService.selectSubscribeNotReleaseCount(hlsCusAbsProduct.getProductId());

        if(notReleaseCount>0){
            throw new HlsCusException("认购关联的额度为全部释放！请检查");
        }

        hlsCusAbsProduct.setProductStatus(HlsCusConstant.WORKFLOW_STATUS.END);
        self().updateByPrimaryKeySelective(request,hlsCusAbsProduct);
    }
}

