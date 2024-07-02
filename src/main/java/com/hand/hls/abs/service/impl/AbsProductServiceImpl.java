package com.hand.hls.abs.service.impl;


import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.abs.dto.*;
import com.hand.hls.abs.mapper.*;
import com.hand.hls.abs.service.*;
import com.hand.hls.abs.util.HlsCusAbsConstant;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fin.service.LonContractWithdrawService;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.gld.dto.HlsCusGldLonContractFinCost;
import com.hand.hls.gld.service.GldLonContractFinCostService;
import com.hand.hls.gld.utils.CalculateUtil;
import com.hand.hls.gld.utils.IrrUtil;
import com.hand.hls.wfl.service.IActivitiCommonService;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.sys.event.service.SysEventService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import static com.hand.hls.abs.util.AbsDateUtils.format;

@SuppressWarnings("AliDeprecation")
@Service
@Transactional(rollbackFor = Exception.class)
public class AbsProductServiceImpl extends BaseServiceImpl<HlsCusAbsProduct> implements AbsProductService {
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
    private static final String WORKFLOW_ABS_PRODUCT_NAME = "FZ_PROJECT_DOS_WFL";
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
    private Calendar calendar = Calendar.getInstance();// 获取一个日历的实例
    @Autowired
    private HlsCusAbsProductMapper hlsCusAbsProductMapper;

    @Autowired
    private HlsCusAbsProjectMapper hlsCusAbsProjectMapper;
    @Autowired
    private AbsBankAccountService absBankAccountService;
    @Autowired
    private AbsProFeeInfoService absProFeeInfoService;
    @Autowired
    private AbsProductOrganizationService absProductOrganizationService;
    @Autowired
    private AbsProjectOrganizationService absProjectOrganizationService;
    @Autowired
    private AbsProductCollectionService absProductCollectionService;
    @Autowired
    private AbsProductCashDetailService absProductCashDetailService;
    @Autowired
    private AbsProductService absProductService;
    @Autowired
    private AbsProductStructureService absProductStructureService;
    @Autowired
    private AbsProjectService absProjectService;
    @Autowired
    private AbsAssetsPackService absAssetsPackService;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private HlsEmployeeMapper employeeMapper;
    @Autowired
    private IActivitiStartService activitiStartService;
    @Autowired
    private SysEventService sysEventService;
    @Autowired
    private HlsCusAbsProductCollectionService hlsCusAbsProductCollectionService;
    @Autowired
    private GldLonContractFinCostService gldLonContractFinCostService;
    @Autowired
    private LonContractWithdrawService lonContractWithdrawService;
    @Autowired
    private HlsCusAbsProductCashDetailMapper hlsCusAbsProductCashDetailMapper;
    @Autowired
    private HlsCusAbsProductCollectionMapper hlsCusAbsProductCollectionMapper;
    @Autowired
    private HlsCusWithdrawRepaymentMapper hlsCusWithdrawRepaymentMapper;

    @Override
    public List<HlsCusAbsProduct> createProduct(Long projectId, String productShortName, String productName,
                                                String productNumber, IRequest request) {
        HlsCusAbsProduct hlsCusAbsProduct = new HlsCusAbsProduct();
        if (projectId != null) {
            HlsCusAbsProject hlsCusAbsProject = hlsCusAbsProjectMapper.selectByPrimaryKey(projectId);
            hlsCusAbsProduct.setBusinessType(hlsCusAbsProject.getBusinessType());
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
     *
     * @param request
     * @param hlsCusAbsProduct
     * @return
     */
    private HlsCusAbsProduct calculateCollectionCash(IRequest request, HlsCusAbsProduct hlsCusAbsProduct)
                    throws HlsCusException {
        HlsCusAbsProductCollection hlsCusAbsProductCollection = new HlsCusAbsProductCollection();
        hlsCusAbsProductCollection.setProductId(hlsCusAbsProduct.getProductId());
        hlsCusAbsProductCollection.setDataClass(HlsCusAbsConstant.DATA_CLASS.NORMAL);
        // 删除之前计算过的数据
        absProductCashDetailService.deleteCashDetailByCollection(hlsCusAbsProductCollection);
        absProductCollectionService.deleteByCollection(hlsCusAbsProductCollection);
        // 起息日
        LocalDate dueDateBegin = format(hlsCusAbsProduct.getDueDateBegin());
        HlsCusAbsAssetsPack hlsCusAbsAssetsPack = new HlsCusAbsAssetsPack();
        hlsCusAbsAssetsPack.setPackId(hlsCusAbsProduct.getPackId());
        hlsCusAbsAssetsPack = absAssetsPackService.selectByPrimaryKey(request, hlsCusAbsAssetsPack);
        if (hlsCusAbsAssetsPack != null) {
            // 封包日
            LocalDate baseDate = format(hlsCusAbsAssetsPack.getBaseDate());
            // 第一个兑付日
            LocalDate firstCashDate = format(hlsCusAbsProduct.getFirstCashDate());
            // 第一个租金回收计算日
            LocalDate firstRentalBackDate = format(hlsCusAbsProduct.getFirstRentalBackDate());
            // 资产包最大日期
            Date packageEndDate = hlsCusAbsProductMapper.selectConPackageMaxDueDate(hlsCusAbsProduct.getProductId());
            hlsCusAbsProduct.setPackageEndDate(packageEndDate);

            List<HlsCusAbsProductCollection> list = new ArrayList<>();
            // 获取最后确认的一期 从这一期开始往下生成
            HlsCusAbsProductCollection productCollection =
                            absProductCollectionService.selectLastConfirmCollection(hlsCusAbsProduct.getProductId());
            if (productCollection == null) {
                HlsCusAbsProductCollection collection = new HlsCusAbsProductCollection();
                collection.setProductId(hlsCusAbsProduct.getProductId());
                collection.setTimes(1L);
                collection.setRentalBackDate(hlsCusAbsProduct.getFirstRentalBackDate());
                collection.setCashDate(hlsCusAbsProduct.getFirstCashDate());
                collection.setCollectionDate(
                                format(firstRentalBackDate.plusDays(hlsCusAbsProduct.getCollectionDateRule())));
                collection.setRemittanceDate(
                                format(firstRentalBackDate.plusDays(hlsCusAbsProduct.getRemittanceDateRule())));
                collection.setReportDate(format(firstRentalBackDate.plusDays(hlsCusAbsProduct.getReportDateRule())));
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
                collection = absProductCollectionService.insertSelective(request, collection);
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
            absProFeeInfoService.updateProductFeeAmount(hlsCusAbsProduct.getProductId());
        }


        return hlsCusAbsProduct;
    }

    /**
     * 计算各个兑付费用
     *
     * @param request
     * @param collection
     * @param hlsCusAbsProduct
     */
    private void calculateFee(IRequest request, HlsCusAbsProductCollection collection,
                              HlsCusAbsProduct hlsCusAbsProduct) {
        /**
         * 兑付总本金
         */
        BigDecimal cashPrincipalSum = absProductCashDetailService
                        .selectCashPrincipalSum(hlsCusAbsProduct.getProductId(), collection.getTimes());
        BigDecimal retainPrincipalSum = hlsCusAbsProduct.getIssueAmount().subtract(cashPrincipalSum);

        HlsCusAbsProFeeInfo proFeeInfo = new HlsCusAbsProFeeInfo();
        proFeeInfo.setSourceKey(collection.getProductId());
        proFeeInfo.setSourceType(HlsCusAbsConstant.ABS_PRO_TYPE.PRODUCT);
        proFeeInfo.setCalculateFlag(HlsCusAbsConstant.FLAG.Y);
        List<HlsCusAbsProFeeInfo> absProFeeInfos = absProFeeInfoService.selectSelective(request, proFeeInfo);
        if (CollectionUtils.isNotEmpty(absProFeeInfos)) {
            // 每种参与计算的费用类型只会存在一行
            for (HlsCusAbsProFeeInfo feeInfo : absProFeeInfos) {
                Double feeAmount = retainPrincipalSum.multiply(new BigDecimal(String.valueOf(feeInfo.getFeeRate())))
                                .multiply(new BigDecimal(collection.getInterestPeriodDays().toString()))
                                .divide(new BigDecimal(hlsCusAbsProduct.getInterestStandardDays()), 2,
                                                BigDecimal.ROUND_HALF_UP)
                                .doubleValue();
                switch (feeInfo.getFeeName()) {
                    // 增值税
                    case HlsCusAbsConstant.ABS_FEE_TYPE.VAT:
                        BigDecimal taxAmount = (new BigDecimal(String.valueOf(collection.getInterest()))
                                        .multiply(new BigDecimal(String.valueOf(feeInfo.getFeeRate())))
                                        .multiply(BigDecimal.ONE.add(new BigDecimal(String.valueOf(feeInfo.getAddTaxRate()))))).divide(
                                                        BigDecimal.ONE.add(new BigDecimal(String.valueOf(feeInfo.getFeeRate()))), 2,
                                                        BigDecimal.ROUND_HALF_UP);
                        collection.setTaxFee(BigDecimal.valueOf(taxAmount.doubleValue()));
                        break;
                    // 服务费
                    case HlsCusAbsConstant.ABS_FEE_TYPE.SERVICE_FEE:
                        collection.setServiceFee(BigDecimal.valueOf(feeAmount));
                        break;
                    // 托管费
                    case HlsCusAbsConstant.ABS_FEE_TYPE.HOSTING_FEE:
                        collection.setHostingFee(BigDecimal.valueOf(feeAmount));
                        break;
                    // 管理费
                    case HlsCusAbsConstant.ABS_FEE_TYPE.MANAGEMENT_FEE:
                        collection.setManagementFee(BigDecimal.valueOf(feeAmount));
                        break;
                    // 承销费
                    case HlsCusAbsConstant.ABS_FEE_TYPE.UNDERWRITING_FEE:
                        collection.setUnderwritingFee(BigDecimal.valueOf(feeAmount));
                        break;
                    // 评级费
                    case HlsCusAbsConstant.ABS_FEE_TYPE.RATING_FEE:
                        collection.setRatingFee(BigDecimal.valueOf(feeAmount));
                        break;
                    // 评估费
                    case HlsCusAbsConstant.ABS_FEE_TYPE.EVALUATION_FEE:
                        collection.setEvaluationFee(BigDecimal.valueOf(feeAmount));
                        break;
                    // 挂牌费
                    case HlsCusAbsConstant.ABS_FEE_TYPE.LISTING_FEE:
                        collection.setListingFee(BigDecimal.valueOf(feeAmount));
                        break;
                    // 律师费
                    case HlsCusAbsConstant.ABS_FEE_TYPE.LAWYER_FEE:
                        collection.setLawyerFee(BigDecimal.valueOf(feeAmount));
                        break;
                    // 会计师费
                    case HlsCusAbsConstant.ABS_FEE_TYPE.ACCOUNTANT_FEE:
                        collection.setAccountantFee(BigDecimal.valueOf(feeAmount));
                        break;
                    // 其他
                    case HlsCusAbsConstant.ABS_FEE_TYPE.OTHER:
                        collection.setOtherFee(BigDecimal.valueOf(feeAmount));
                        break;
                    default:
                        break;
                }
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
    private void calculateCashDeatil(IRequest request, HlsCusAbsProductCollection collection,
                                     HlsCusAbsProduct hlsCusAbsProduct) throws HlsCusException {
        HlsCusAbsProductStructure productStructure = new HlsCusAbsProductStructure();
        productStructure.setProductId(hlsCusAbsProduct.getProductId());
        List<HlsCusAbsProductStructure> productStructures =
                        absProductStructureService.selectProductStructureData(request, productStructure);
        if (CollectionUtils.isNotEmpty(productStructures)) {
            // 每个层次的利息总和
            BigDecimal cashInterestSum = absProductStructureService.selectStructureCashInterestSum(
                            hlsCusAbsProduct.getProductId(), collection.getInterestPeriodDays());
            // 当期兑付各个费用总和
            BigDecimal cashFeeSum = absProductCollectionService.selectCashFeeSum(collection.getCollectionId());
            // 当期可以兑付的金额
            BigDecimal cashDueAmount =
                            new BigDecimal(String.valueOf(collection.getDueAmount())).subtract(cashInterestSum).subtract(cashFeeSum);
            if (cashDueAmount.compareTo(new BigDecimal(0)) == -1) {
                throw new HlsCusException("第" + collection.getTimes() + "期归集租金不足");
            }
            for (HlsCusAbsProductStructure structure : productStructures) {
                HlsCusAbsProductCashDetail cashDetail = new HlsCusAbsProductCashDetail();
                cashDetail.setProductId(hlsCusAbsProduct.getProductId());
                cashDetail.setCollectionId(collection.getCollectionId());
                cashDetail.setStructureId(structure.getStructureId());
                if (structure.getSubscribeAmount().compareTo(structure.getPublishAmount()) == 1) {
                    throw new HlsCusException("认购金额超出分层发行金额");
                }
                BigDecimal interest = (new BigDecimal(String.valueOf(structure.getPublishAmount()))
                                .subtract(new BigDecimal(String.valueOf(structure.getCashPrincipal())))
                                .subtract(new BigDecimal(String.valueOf(structure.getSubscribeAmount()))))
                                                .multiply(new BigDecimal(String.valueOf(structure.getPredictRate())))
                                                .multiply(new BigDecimal(collection.getInterestPeriodDays().toString()))
                                                .divide(new BigDecimal(
                                                                hlsCusAbsProduct.getInterestStandardDays().toString()),
                                                                2, BigDecimal.ROUND_HALF_UP);
                cashDetail.setPlanCashInterest(BigDecimal.valueOf(interest.doubleValue()));
                HlsCusAbsProductStructure cashStructure = absProductStructureService.selectCashStructure(request,
                                structure.getStructureId(), "NORMAL", collection.getTimes());
                // 判断当期可以用来兑付的本金金额 是否大于 该层级的剩余本金金额
                if (cashDueAmount.compareTo(new BigDecimal(String.valueOf(structure.getPublishAmount()))
                                .subtract(new BigDecimal(String.valueOf(structure.getSubscribeAmount())))
                                .subtract(new BigDecimal(String.valueOf(cashStructure.getCashPrincipal())))) == 1) {
                    cashDetail.setPlanCashPrincipal(BigDecimal.valueOf(new BigDecimal(String.valueOf(structure.getPublishAmount()))
                                    .subtract(new BigDecimal(String.valueOf(structure.getSubscribeAmount())))
                                    .subtract(new BigDecimal(String.valueOf(cashStructure.getCashPrincipal()))).doubleValue()));
                } else {
                    cashDetail.setPlanCashPrincipal(BigDecimal.valueOf(cashDueAmount.doubleValue()));
                }
                cashDetail.setCreatedBy(request.getUserId());
                cashDetail.setLastUpdatedBy(request.getUserId());
                absProductCashDetailService.insertSelective(request, cashDetail);
                cashDueAmount = cashDueAmount.subtract(new BigDecimal(String.valueOf(cashDetail.getPlanCashPrincipal())));
            }
        }
    }

    /**
     * 计算每期归集信息
     */
    private List<HlsCusAbsProductCollection> calcuCollectionInfo(IRequest request, HlsCusAbsProduct hlsCusAbsProduct,
                                                                 long times, List<HlsCusAbsProductCollection> list) throws HlsCusException {
        Long startTimes = list.get(0).getTimes() + 1;
        int start = startTimes.intValue();
        Boolean flag = true;
        while (true) {
            HlsCusAbsProductCollection productCollection = new HlsCusAbsProductCollection();
            LocalDate nextMonth =
                            format(list.get(Integer.valueOf(String.valueOf(startTimes)) - start).getRentalBackDate())
                                            .plusMonths(times);
            LocalDate nextCashMonth =
                            format(list.get(Integer.valueOf(String.valueOf(startTimes)) - start).getCashDate())
                                            .plusMonths(times);
            LocalDate thisTimeRentalBackDate, beforeTimeRentalBackDate =
                            format(list.get(Integer.valueOf(String.valueOf(startTimes)) - start).getRentalBackDate());
            if (nextMonth.getMonthValue() == 2 && hlsCusAbsProduct.getRentalBackDateT() > 28) {
                thisTimeRentalBackDate = nextMonth.with(TemporalAdjusters.lastDayOfMonth());
            } else {
                thisTimeRentalBackDate = nextMonth
                                .withDayOfMonth(Integer.valueOf(String.valueOf(hlsCusAbsProduct.getRentalBackDateT())));
            }
            if (format(thisTimeRentalBackDate).compareTo(hlsCusAbsProduct.getPackageEndDate()) >= 0) {
                flag = false;
            }
            LocalDate thisTimecashDate;
            if (nextMonth.getMonthValue() == 2 && hlsCusAbsProduct.getCashDateT() > 28) {
                thisTimecashDate = nextCashMonth.with(TemporalAdjusters.lastDayOfMonth());
            } else {
                thisTimecashDate = nextCashMonth
                                .withDayOfMonth(Integer.valueOf(String.valueOf(hlsCusAbsProduct.getCashDateT())));
            }
            productCollection.setCollectionCfItem(HlsCusAbsConstant.CASHFLOW_ITEM.COLLECTION_CF_ITEM);
            productCollection.setCollectionCfType(HlsCusAbsConstant.CASHFLOW_ITEM.COLLECTION_CF_TYPE);
            productCollection.setRemittanceCfItem(HlsCusAbsConstant.CASHFLOW_ITEM.REMITTANCE_CF_ITEM);
            productCollection.setRemittanceCfType(HlsCusAbsConstant.CASHFLOW_ITEM.COLLECTION_CF_TYPE);
            productCollection.setProductId(hlsCusAbsProduct.getProductId());
            productCollection.setTimes(startTimes);
            productCollection.setCashDate(format(thisTimecashDate));
            productCollection.setCollectionDate(
                            format(thisTimeRentalBackDate.plusDays(hlsCusAbsProduct.getCollectionDateRule())));
            productCollection.setRemittanceDate(
                            format(thisTimeRentalBackDate.plusDays(hlsCusAbsProduct.getRemittanceDateRule())));
            productCollection.setReportDate(
                            format(thisTimeRentalBackDate.plusDays(hlsCusAbsProduct.getReportDateRule())));
            // 本金
            BigDecimal principal =
                            hlsCusAbsProductMapper.queryPrincipal(beforeTimeRentalBackDate.plusDays(1).toString(),
                                            thisTimeRentalBackDate.toString(), hlsCusAbsProduct.getProductId());
            // 利息
            BigDecimal interest = hlsCusAbsProductMapper.queryInterest2(beforeTimeRentalBackDate.plusDays(1).toString(),
                            thisTimeRentalBackDate.toString(), hlsCusAbsProduct.getProductId());
            // 提前结清
            BigDecimal finishAmount = hlsCusAbsProductMapper.selectOtherFinishAmount(
                            beforeTimeRentalBackDate.plusDays(1).toString(), thisTimeRentalBackDate.toString(),
                            hlsCusAbsProduct.getProductId());
            productCollection.setPrincipal(BigDecimal.valueOf(principal.doubleValue()));
            productCollection.setInterest(BigDecimal.valueOf(interest.doubleValue()));
            productCollection.setDueAmount(BigDecimal.valueOf(principal.add(interest).add(finishAmount).doubleValue()));
            productCollection.setCollectionAmount(productCollection.getDueAmount());
            productCollection.setCashAmount(productCollection.getDueAmount());
            productCollection.setRemittanceAmount(productCollection.getCollectionAmount());
            productCollection.setRentalBackDate(format(thisTimeRentalBackDate));
            productCollection.setDataClass(HlsCusAbsConstant.DATA_CLASS.NORMAL);
            productCollection.setInterestPeriodDays(thisTimecashDate.toEpochDay()
                            - format(list.get(Integer.valueOf(String.valueOf(startTimes)) - start).getCashDate())
                                            .toEpochDay());
            calculateFee(request, productCollection, hlsCusAbsProduct);
            startTimes++;
            list.add(productCollection);
            absProductCollectionService.insertSelective(request, productCollection);
            calculateCashDeatil(request, productCollection, hlsCusAbsProduct);
            if (!flag) {
                break;
            }
        }
        return list;
    }

    /**
     * 新增产品时复制行表信息
     */
    public void insertLineData(IRequest request, HlsCusAbsProduct hlsCusAbsProduct) {
        /**
         * 账户信息复制
         */
        HlsCusAbsBankAccount bankAccount = new HlsCusAbsBankAccount();
        bankAccount.setSourceKey(hlsCusAbsProduct.getProjectId());
        bankAccount.setSourceType(HlsCusAbsConstant.ABS_PRO_TYPE.PROJECT);
        List<HlsCusAbsBankAccount> bankAccountList = absBankAccountService.select(request, bankAccount, 1, 9999);
        for (HlsCusAbsBankAccount account : bankAccountList) {
            account.setAbsBankAccountId(null);
            account.setSourceType(HlsCusAbsConstant.ABS_PRO_TYPE.PRODUCT);
            account.setSourceKey(hlsCusAbsProduct.getProductId());
            account.setCreatedBy(request.getUserId());
            account.setLastUpdatedBy(request.getUserId());
            account.setCreationDate(new Date());
            account.setLastUpdateDate(new Date());
            absBankAccountService.insertSelective(request, account);
        }

        /**
         * 费用信息
         */
        HlsCusAbsProFeeInfo proFeeInfo = new HlsCusAbsProFeeInfo();
        proFeeInfo.setSourceKey(hlsCusAbsProduct.getProjectId());
        proFeeInfo.setSourceType(HlsCusAbsConstant.ABS_PRO_TYPE.PROJECT);
        List<HlsCusAbsProFeeInfo> proFeeInfoList = absProFeeInfoService.select(request, proFeeInfo, 1, 9999);
        for (HlsCusAbsProFeeInfo feeInfo : proFeeInfoList) {
            feeInfo.setSourceInfoId(feeInfo.getFeeInfoId());
            feeInfo.setFeeInfoId(null);
            feeInfo.setSourceType(HlsCusAbsConstant.ABS_PRO_TYPE.PRODUCT);
            feeInfo.setSourceKey(hlsCusAbsProduct.getProductId());
            feeInfo.setCreatedBy(request.getUserId());
            feeInfo.setLastUpdatedBy(request.getUserId());
            feeInfo.setCreationDate(new Date());
            feeInfo.setLastUpdateDate(new Date());
            absProFeeInfoService.insertSelective(request, feeInfo);
        }


        /**
         * 中介机构信息
         */
        HlsCusAbsProjectOrganization hlsCusAbsProjectOrganization = new HlsCusAbsProjectOrganization();
        hlsCusAbsProjectOrganization.setProjectId(hlsCusAbsProduct.getProjectId());
        List<HlsCusAbsProjectOrganization> projectOrganizationList =
                        absProjectOrganizationService.selectSelective(request, hlsCusAbsProjectOrganization);
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
                absProductOrganizationService.insertSelective(request, hlsCusAbsProductOrganization);
                absProFeeInfoService.updateFeeOrganizationId(hlsCusAbsProduct.getProductId(),
                                cusAbsProjectOrganization.getOrganizationId(),
                                hlsCusAbsProductOrganization.getOrganizationId());
            }
        }
    }

    // abs利息分摊，按天计息
    @Override
    public void absProductIncome(IRequest request, HlsCusAbsProduct hlsCusAbsProduct) throws HlsCusException {
        HlsCusAbsProduct absProduct = new HlsCusAbsProduct();
        absProduct.setProductId(hlsCusAbsProduct.getProductId());
        absProduct = self().selectByPrimaryKey(request, absProduct);
        Date calcStartDate = absProduct.getDueDateBegin();
        Date lastRepaymentDate = absProduct.getDueDateBegin();
        // 删除未确认分摊
        HlsCusGldLonContractFinCost hlsCusGldLonContractFinCostTemp = new HlsCusGldLonContractFinCost();
        hlsCusGldLonContractFinCostTemp.setProductId(absProduct.getProductId());
        hlsCusGldLonContractFinCostTemp.setPostFlag("N");
        List<HlsCusGldLonContractFinCost> list =
                        gldLonContractFinCostService.select(request, hlsCusGldLonContractFinCostTemp, 1, 9999999);
        gldLonContractFinCostService.batchDelete(list);
        HlsCusAbsProductCollection hlsCusAbsProductCollection = new HlsCusAbsProductCollection();
        hlsCusAbsProductCollection.setProductId(absProduct.getProductId());
        List<HlsCusAbsProductCollection> hlsCusAbsProductCollectionLists =
                        hlsCusAbsProductCollectionService.select(request, hlsCusAbsProductCollection, 1, 99999999);
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
                // 计算分摊的现金流金额,不含税金额
                HlsCusAbsProductCashDetail hlsCusAbsProductCashDetail = new HlsCusAbsProductCashDetail();
                hlsCusAbsProductCashDetail.setCollectionId(absProductCollection.getCollectionId());
                List<HlsCusAbsProductCashDetail> absProductCashDetailList =
                                absProductCashDetailService.select(request, hlsCusAbsProductCashDetail, 1, 99999999);
                if (absProductCashDetailList.size() == 0) {
                    continue;
                }
                for (HlsCusAbsProductCashDetail absProductCashDetail : absProductCashDetailList) {
                    cashInterest = CalculateUtil.add(cashInterest,
                                    absProductCashDetail.getPlanCashInterest().doubleValue());
                }
                netCashInterest = (double) Math.round(cashInterest / ABS_INCOMES_RATE * 100) / 100;
                cStart.setTime(calcStartDate);
                cEnd.setTime(absProductCollection.getCashDate());
                cEnd.add(Calendar.DAY_OF_MONTH, -1);
                month = (cEnd.get(Calendar.YEAR) - cStart.get(Calendar.YEAR)) * 12 + cEnd.get(Calendar.MONTH)
                                - cStart.get(Calendar.MONTH);
                Calendar calcStart = Calendar.getInstance();
                Calendar calcEnd = Calendar.getInstance();
                calcStart.setTime(calcStartDate);
                for (int i = 0; i <= month; i++) {
                    Calendar calcCal = Calendar.getInstance();
                    if ((i == 0 && month == 0) || (i == month && month > 0)) {
                        calcEnd.setTime(absProductCollection.getCashDate());
                        calcEnd.add(Calendar.DAY_OF_MONTH, -1);
                        calcDays = lonContractWithdrawService.getCalcDays(calcStart.getTime(), calcEnd.getTime());
                        sumFinIncome = getSumFinIncomeInclud(request, absProductCollection.getCollectionId(),
                                        absProduct.getProductId(), "N");
                        sumFinIncomeInclud = getSumFinIncomeInclud(request, absProductCollection.getCollectionId(),
                                        absProduct.getProductId(), "Y");
                        finIncome = netCashInterest - sumFinIncome;
                        finIncomeInclud = cashInterest - sumFinIncomeInclud;
                    } else {
                        calcEnd.setTime(lonContractWithdrawService.getMonthEndDate(calcStartDate));
                        calcDays = lonContractWithdrawService.getCalcDays(calcStart.getTime(), calcEnd.getTime());
                        calcCal.setTime(absProductCollection.getCashDate());
                        calcCal.add(Calendar.DAY_OF_MONTH, -1);
                        finIncome = (double) Math.round(netCashInterest * calcDays
                                        / lonContractWithdrawService.getCalcDays(lastRepaymentDate, calcCal.getTime())
                                        * 100) / 100;
                        finIncomeInclud = (double) Math.round(cashInterest * calcDays
                                        / lonContractWithdrawService.getCalcDays(lastRepaymentDate, calcCal.getTime())
                                        * 100) / 100;
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
                    List<HlsCusGldLonContractFinCost> hlsCusGldLonContractFinCostExistsList =
                                    gldLonContractFinCostService.select(request, hlsCusGldLonContractFinCostExists, 1,
                                                    999999);
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

    // 获取日期之间计提总和
    private Double getSumFinIncomeInclud(IRequest request, Long collectionId, Long productId, String vatFlag) {
        Double result = 0D;
        HlsCusGldLonContractFinCost hlsCusGldLonContractFinCostTemp = new HlsCusGldLonContractFinCost();
        hlsCusGldLonContractFinCostTemp.setProductId(productId);
        hlsCusGldLonContractFinCostTemp.setAbsFeeId(collectionId);
        hlsCusGldLonContractFinCostTemp.setSourceType(ABS_INTEREST_INCOMES_SOURCE_TYPE);
        List<HlsCusGldLonContractFinCost> list =
                        gldLonContractFinCostService.select(request, hlsCusGldLonContractFinCostTemp, 1, 9999999);
        if (list.size() > 0) {
            for (HlsCusGldLonContractFinCost hlsCusGldLonContractFinCost : list) {
                if ("Y".equals(vatFlag)) {
                    result = result + hlsCusGldLonContractFinCost.getFinanceIncomeInclud();
                } else {
                    result = result + hlsCusGldLonContractFinCost.getFinanceCost();
                }
            }
        }
        return result;
    }

    // abs其他费用按实率分摊
    public void calculateAbsOtherFeeIncomes(IRequest request, HlsCusAbsProduct hlsCusAbsProduct) {
        HlsCusAbsProductStructure hlsCusAbsProductStructure = new HlsCusAbsProductStructure();
        hlsCusAbsProductStructure.setProductId(hlsCusAbsProduct.getProductId());
        List<HlsCusAbsProductStructure> hlsCusAbsProductStructureList =
                        absProductStructureService.select(request, hlsCusAbsProductStructure, 1, 999999);
        Double realRate = getRealRate(request, hlsCusAbsProductStructureList);
        // 获取分摊费用
        HlsCusAbsProFeeInfo hlsCusAbsProFeeInfo = new HlsCusAbsProFeeInfo();
        hlsCusAbsProFeeInfo.setSourceKey(hlsCusAbsProduct.getProductId());
        hlsCusAbsProFeeInfo.setSourceType(ABS_TYPE);
        List<HlsCusAbsProFeeInfo> hlsCusAbsProFeeInfoList =
                        absProFeeInfoService.select(request, hlsCusAbsProFeeInfo, 1, 999999);

        for (int i = 0; i < hlsCusAbsProFeeInfoList.size(); i++) {
            HlsCusAbsProFeeInfo absProFeeInfo = hlsCusAbsProFeeInfoList.get(i);
            if (ABS_OTHER_INCOMES_ONE_TIME.equals(absProFeeInfo.getShareType())) {
                calculateAbsOneTimeIncomes(request, absProFeeInfo, hlsCusAbsProduct);
            } else {
                calculateAbsRealRateIncomes(request, absProFeeInfo, hlsCusAbsProduct, realRate);
            }
        }
    }

    public void calculateAbsRealRateIncomes(IRequest request, HlsCusAbsProFeeInfo absProFeeInfo,
                                            HlsCusAbsProduct hlsCusAbsProduct, Double realRate) {
        // 没有重新分摊(复用中间表，fee_info_id=withdraw_id,product_id=contract_id,用cf_item判断)
        HlsCusWithdrawRepayment hlsCusWithdrawRepaymentTemp = new HlsCusWithdrawRepayment();
        hlsCusWithdrawRepaymentTemp.setCfItem(absProFeeInfo.getCfItem());
        hlsCusWithdrawRepaymentTemp.setWithdrawId(absProFeeInfo.getFeeInfoId());
        hlsCusWithdrawRepaymentTemp.setContractId(hlsCusAbsProduct.getProductId());
        List<HlsCusWithdrawRepayment> hlsCusWithdrawRepaymentTempList =
                        hlsCusWithdrawRepaymentMapper.select(hlsCusWithdrawRepaymentTemp);
        if (hlsCusWithdrawRepaymentTempList.size() == 0) {
            Double productIncomeAmount =
                            hlsCusAbsProductMapper.queryProductIncomeAmount(hlsCusAbsProduct.getProductId());
            hlsCusAbsProduct.setCfItem(absProFeeInfo.getCfItem());
            Double incomeOtherFee = 0D;
            incomeOtherFee = hlsCusAbsProductMapper.queryProductFeeAmountbyCfItem(hlsCusAbsProduct).doubleValue();
            if (incomeOtherFee == 0) {
                return;
            }
            Calendar start = (Calendar) calendar.clone();
            Calendar end = (Calendar) calendar.clone();
            Double cashflowAmount = 0D;
            // 期初摊余成本
            Double amortizedCost = 0D;
            // 利息及融资费用
            Double interestFinancingFee = 0D;
            // 合同利息
            Double contractInterest = 0D;
            // 分摊费用
            Double financeIncome = ABS_INCOMES_DOUBLE_INIT;
            // 新建数组，保存计算IRR的数据
            List<Double> irrArray = new ArrayList<>();

            List<HlsCusAbsProductCollection> incomeAbsProductCollectionList =
                            hlsCusAbsProductCollectionMapper.selectProductCollectionInfoByCfItem(
                                            hlsCusAbsProduct.getProductId(), absProFeeInfo.getCfItem());
            // 获取分摊结束时间
            Date calcEndDate = incomeAbsProductCollectionList.get(0).getCashDate();
            // 分摊次数
            int incomeTimes = getMonthSpace(hlsCusAbsProduct.getDueDateBegin(), calcEndDate);
            for (int i = 0; i < incomeTimes; i++) {
                Date from;// 日期从
                // Date to;//日期到
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
                    // end.add(Calendar.DAY_OF_MONTH,-1);
                }
                if (end.getTime().getTime() >= calcEndDate.getTime() && i + 1 != incomeTimes) {
                    end.clear();
                    end.setTime(calcEndDate);
                    end.add(Calendar.DAY_OF_MONTH, -1);
                }
                // 期间的剩余本金
                Double remainingPrincipalTemp = 0D;
                for (HlsCusAbsProductCollection dto : incomeAbsProductCollectionList) {
                    if (start.getTime().compareTo(dto.getCashDate()) == 1) {
                        remainingPrincipalTemp = remainingPrincipalTemp + hlsCusAbsProductCashDetailMapper
                                        .queryRemainingPrincipalSum(hlsCusAbsProduct.getProductId(),
                                                        dto.getCollectionId())
                                        .doubleValue();
                    }
                }
                Double remainingPrincipal = CalculateUtil.sub(productIncomeAmount, remainingPrincipalTemp);
                Double chargePriAmount = 0D;
                for (HlsCusAbsProductCollection dto : incomeAbsProductCollectionList) {
                    if (start.getTime().compareTo(dto.getCashDate()) == -1
                                    && dto.getCashDate().compareTo(end.getTime()) == -1) {
                        chargePriAmount = chargePriAmount + dto.getIncomeFee()
                                        + hlsCusAbsProductCashDetailMapper
                                                        .queryRemainingPrincipalSum(hlsCusAbsProduct.getProductId(),
                                                                        dto.getCollectionId())
                                                        .doubleValue();
                    }
                }
                if (i == 0) {
                    contractInterest = ABS_INCOMES_DOUBLE_INIT;

                    cashflowAmount = CalculateUtil.sub(CalculateUtil.sub(productIncomeAmount, chargePriAmount),
                                    contractInterest);
                } else {
                    contractInterest = transforIncome(
                                    CalculateUtil.div(CalculateUtil.mul(remainingPrincipal, realRate), YEAR_MONTH));

                    cashflowAmount = CalculateUtil.sub(CalculateUtil.sub(ABS_INCOMES_DOUBLE_INIT, chargePriAmount),
                                    contractInterest);

                }
                irrArray.add(cashflowAmount);
                // 插入分摊临时表
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
            // 计算irr
            Double irr = IrrUtil.irr(irrArray) * YEAR_MONTH;// irr利率
            // 计算期初摊余成本&利息及融资费用
            HlsCusWithdrawRepayment repaymentDto = new HlsCusWithdrawRepayment();
            repaymentDto.setContractId(absProFeeInfo.getFeeInfoId());
            repaymentDto.setWithdrawId(hlsCusAbsProduct.getProductId());
            repaymentDto.setCfItem(absProFeeInfo.getCfItem());
            repaymentDto.setSortname("finIncomeDate");
            repaymentDto.setSortorder("asc");
            List<HlsCusWithdrawRepayment> hlsCusWithdrawRepaymentList =
                            hlsCusWithdrawRepaymentMapper.select(repaymentDto);
            for (int i = 0; i < hlsCusWithdrawRepaymentList.size(); i++) {
                Calendar tempDate = (Calendar) calendar.clone();

                HlsCusWithdrawRepayment withdrawRepayment = hlsCusWithdrawRepaymentList.get(i);
                tempDate.clear();
                tempDate.setTime(withdrawRepayment.getFinIncomeDate());
                if (i < hlsCusWithdrawRepaymentList.size() - 1) {
                    tempDate.add(Calendar.MONTH, 1);
                    tempDate.add(Calendar.DAY_OF_MONTH, -1);
                }
                if (tempDate.getTime().getTime() >= calcEndDate.getTime()
                                && i + 1 != hlsCusWithdrawRepaymentList.size()) {
                    tempDate.clear();
                    tempDate.setTime(calcEndDate);
                    tempDate.add(Calendar.DAY_OF_MONTH, -1);
                }
                Double chargePriAmount = 0D;
                // Double chargePriAmount =
                // hlsCusLonContractRepaymentMapper.selectAmountSum(hlsCusLonContractWithdraw.getWithdrawId(),
                // start.getTime(), end.getTime());
                for (HlsCusAbsProductCollection dto : incomeAbsProductCollectionList) {
                    if (start.getTime().compareTo(dto.getCashDate()) == -1
                                    && dto.getCashDate().compareTo(end.getTime()) == -1) {
                        chargePriAmount = chargePriAmount + dto.getIncomeFee()
                                        + hlsCusAbsProductCashDetailMapper
                                                        .queryRemainingPrincipalSum(hlsCusAbsProduct.getProductId(),
                                                                        dto.getCollectionId())
                                                        .doubleValue();
                    }
                }
                if (i == 0) {
                    amortizedCost = CalculateUtil.sub(productIncomeAmount, chargePriAmount);
                    interestFinancingFee = ABS_INCOMES_DOUBLE_INIT;
                } else {
                    interestFinancingFee = transforIncome(CalculateUtil.div(
                                    CalculateUtil.mul(hlsCusWithdrawRepaymentList.get(i - 1).getAmortizedCost(), irr),
                                    12D));
                    amortizedCost = CalculateUtil
                                    .sub(CalculateUtil.sub(
                                                    CalculateUtil.add(
                                                                    hlsCusWithdrawRepaymentList.get(i - 1)
                                                                                    .getAmortizedCost(),
                                                                    interestFinancingFee),
                                                    withdrawRepayment.getContractInterest()), chargePriAmount);
                }
                if (i + 1 == hlsCusWithdrawRepaymentList.size()) {
                    withdrawRepayment.setFinanceIncome(CalculateUtil.sub(incomeOtherFee, financeIncome));
                } else {
                    withdrawRepayment.setFinanceIncome(transforIncome(
                                    CalculateUtil.sub(interestFinancingFee, withdrawRepayment.getContractInterest())));
                }
                financeIncome = CalculateUtil.add(
                                CalculateUtil.sub(interestFinancingFee, withdrawRepayment.getContractInterest()),
                                financeIncome);
                // withdrawRepayment.setFinanceIncome(irr);
                withdrawRepayment.setAmortizedCost(amortizedCost);
                withdrawRepayment.setInterestFinancingFee(interestFinancingFee);
                withdrawRepayment.setIrr(irr);
                hlsCusWithdrawRepaymentMapper.updateByPrimaryKey(withdrawRepayment);
            }
            // 更新到分摊表
            HlsCusWithdrawRepayment hlsCusWithdrawRepaymentFinCost = new HlsCusWithdrawRepayment();
            hlsCusWithdrawRepaymentFinCost.setContractId(absProFeeInfo.getFeeInfoId());
            hlsCusWithdrawRepaymentFinCost.setWithdrawId(hlsCusAbsProduct.getProductId());
            hlsCusWithdrawRepaymentFinCost.setCfItem(absProFeeInfo.getCfItem());

            List<HlsCusWithdrawRepayment> hlsCusWithdrawRepaymentFinCostList =
                            hlsCusWithdrawRepaymentMapper.select(hlsCusWithdrawRepaymentFinCost);
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
                hlsCusGldLonContractFinCost
                                .setFinanceCost(hlsCusWithdrawRepaymentFinCostList.get(i).getFinanceIncome());
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
                    hlsCusGldLonContractFinCost
                                    .setStartDate(hlsCusWithdrawRepaymentFinCostList.get(i - 1).getFinIncomeDate());
                    hlsCusGldLonContractFinCost
                                    .setEndDate(hlsCusWithdrawRepaymentFinCostList.get(i).getFinIncomeDate());
                    Days = getCalcDays(hlsCusWithdrawRepaymentFinCostList.get(i - 1).getFinIncomeDate(),
                                    hlsCusWithdrawRepaymentFinCostList.get(i).getFinIncomeDate());
                    hlsCusGldLonContractFinCost.setDays(Days);
                } else {
                    finStart.clear();
                    finStart.setTime(hlsCusWithdrawRepaymentFinCostList.get(i - 1).getFinIncomeDate());
                    finStart.add(Calendar.DAY_OF_MONTH, 1);
                    hlsCusGldLonContractFinCost.setStartDate(finStart.getTime());
                    hlsCusGldLonContractFinCost
                                    .setEndDate(hlsCusWithdrawRepaymentFinCostList.get(i).getFinIncomeDate());
                    Days = getCalcDays(finStart.getTime(),
                                    hlsCusWithdrawRepaymentFinCostList.get(i).getFinIncomeDate());
                    hlsCusGldLonContractFinCost.setDays(Days);
                }
                gldLonContractFinCostService.insertSelective(request, hlsCusGldLonContractFinCost);

            }
        }
    }

    public void calculateAbsOneTimeIncomes(IRequest request, HlsCusAbsProFeeInfo absProFeeInfo,
                                           HlsCusAbsProduct hlsCusAbsProduct) {
        HlsCusGldLonContractFinCost hlsCusGldLonContractFinCost = new HlsCusGldLonContractFinCost();
        hlsCusGldLonContractFinCost.setSourceType(ABS_OTHER_INCOMES_SOURCE_TYPE);
        hlsCusGldLonContractFinCost.setCfItem(absProFeeInfo.getCfItem());
        hlsCusGldLonContractFinCost.setProductId(hlsCusAbsProduct.getProductId());
        hlsCusGldLonContractFinCost.setAbsFeeId(absProFeeInfo.getFeeInfoId());
        List<HlsCusGldLonContractFinCost> hlsCusGldLonContractFinCostLists =
                        gldLonContractFinCostService.select(request, hlsCusGldLonContractFinCost, 1, 99999);
        if (hlsCusGldLonContractFinCostLists.size() == 0) {
            hlsCusGldLonContractFinCost.setFinanceCostId(null);
            hlsCusGldLonContractFinCost.setCompanyId(hlsCusAbsProduct.getCompanyId());
            hlsCusGldLonContractFinCost.setContractId(ABS_INCOMES_LONG_INIT);
            hlsCusGldLonContractFinCost.setRepaymentId(ABS_INCOMES_LONG_INIT);
            hlsCusGldLonContractFinCost.setWithdrawId(ABS_INCOMES_LONG_INIT);
            // noinspection AliDeprecation,AliDeprecation
            hlsCusGldLonContractFinCost.setPeriodName(
                            absProFeeInfo.getPlanPayDate().getYear() + "-" + absProFeeInfo.getPlanPayDate().getMonth());
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

    public Double getRealRate(IRequest request, List<HlsCusAbsProductStructure> hlsCusAbsProductStructureList) {
        Double totalStructurePrecent = 0D;
        Double realRate = 0D;
        for (HlsCusAbsProductStructure absProductStructure : hlsCusAbsProductStructureList) {
            if (!"INFERIOR".equals(absProductStructure.getProjectStructure())) {
                totalStructurePrecent =
                                CalculateUtil.add(totalStructurePrecent, absProductStructure.getStructurePrecent().doubleValue());
            }
        }
        for (HlsCusAbsProductStructure absProductStructure : hlsCusAbsProductStructureList) {
            if (!"INFERIOR".equals(absProductStructure.getProjectStructure())) {
                realRate = realRate + (double) Math.round(absProductStructure.getPredictRate().doubleValue()
                                / totalStructurePrecent * absProductStructure.getStructurePrecent() * 100) / 100;
            }
        }
        return realRate;
    }

    @Override
    public void submitProduct(IRequest request, Long productId) throws HlsCusException {
        HlsCusAbsProduct hlsCusAbsProduct = new HlsCusAbsProduct();
        if (productId != null) {
            hlsCusAbsProduct.setProductId(productId);
            hlsCusAbsProduct = absProductService.selectByPrimaryKey(request, hlsCusAbsProduct);
            HlsCusAbsProject hlsCusAbsProject = hlsCusAbsProjectMapper.selectByPrimaryKey(hlsCusAbsProduct.getProjectId());

            if(hlsCusAbsProject == null || hlsCusAbsProject.getIssueAmount() == null){
                throw new HlsCusException("获取项目信息失败！");
            }else if(hlsCusAbsProduct.getIssueAmount()!=null && hlsCusAbsProduct.getIssueAmount().compareTo(hlsCusAbsProject.getIssueAmount()) == 1){
                throw new HlsCusException("发行金额不能超过注册信息维护的注册金额！");
            }
            if (hlsCusAbsProduct != null) {
                if (HlsCusAbsConstant.DATA_CLASS.NORMAL.equalsIgnoreCase(hlsCusAbsProduct.getDataClass())) {
                    hlsCusAbsProduct.setProductStatus(HlsCusAbsConstant.WORKFLOW_STATUS.APPROVING);
                }
                hlsCusAbsProductMapper.updateByPrimaryKeySelective(hlsCusAbsProduct);
                databaseLockProvider.lock(hlsCusAbsProduct);
                HlsEmployee employee = employeeMapper.getEmployeeCode(request.getUserId());
                if (ObjectUtils.isEmpty(employee)) {
                    throw new HlsCusException("获取提交人失败");
                }
                String employeeCode = employee.getEmployeeCode();
                request.setEmployeeCode(employeeCode);

                // 开始流程
                Map<String, Object> params = new HashMap<String, Object>();
                Map<String, Object> evenParams = new HashMap<>();
                //修改message
                evenParams.put("message", "abs发债产品" + hlsCusAbsProduct.getProductName() + "提交审批");
                evenParams.put("noticeTitle", NOTICE_TITLE_ABS_PRODUCT_SUBMIT);
                evenParams.put("url", "");
                evenParams.put("level", HlsCusAbsConstant.WORKFLOW_EVENT_PARAMS.EVENT_PARAMS_LEVEL);
                evenParams.put("noticeType", HlsCusAbsConstant.WORKFLOW_EVENT_PARAMS.NOTICE_TYPE_NOTICE);
                sysEventService.eventSave(request, hlsCusAbsProduct.getProductId(),
                                hlsCusAbsProduct.getDocumentCategory(), hlsCusAbsProduct.getDocumentType(), "ABS",
                                PROPERTY_ABS_PRODUCT_BONDING_SUBMIT, "P2D", evenParams);
                if (HlsCusAbsConstant.DATA_CLASS.NORMAL.equalsIgnoreCase(hlsCusAbsProduct.getDataClass())) {
                    if (HlsCusAbsConstant.ABS_BUSINESS_TYPE.ABS.equals(hlsCusAbsProduct.getBusinessType())
                                    || HlsCusAbsConstant.ABS_BUSINESS_TYPE.ABN
                                                    .equals(hlsCusAbsProduct.getBusinessType())) {
                        params.put(HlsCusAbsConstant.WORKFLOW_PARAMS.WORKFLOW_TYPE, WORKFLOW_ABS_PRODUCT_NAME);
                    } else {
                        params.put(HlsCusAbsConstant.WORKFLOW_PARAMS.WORKFLOW_TYPE,
                                        HlsCusAbsConstant.ABS_WFL.OTHER_DEBT_WFL);
                    }
                } else {
                    if (HlsCusAbsConstant.ABS_BUSINESS_TYPE.ABS.equals(hlsCusAbsProduct.getBusinessType())
                                    || HlsCusAbsConstant.ABS_BUSINESS_TYPE.ABN
                                                    .equals(hlsCusAbsProduct.getBusinessType())) {
                        params.put(HlsCusAbsConstant.WORKFLOW_PARAMS.WORKFLOW_TYPE,
                                        HlsCusAbsConstant.ABS_WFL.ABS_ABN_CHANGE_WFL);
                    } else {
                        params.put(HlsCusAbsConstant.WORKFLOW_PARAMS.WORKFLOW_TYPE,
                                        HlsCusAbsConstant.ABS_WFL.OTHER_DEBT_CHANGE_WFL);
                    }
                    // 变更
                    // HlsCusChangeReqInfo cusChangeReqInfo = new HlsCusChangeReqInfo();
                    // cusChangeReqInfo.setChangeReqId(absProduct.getChangeReqId());
                    // cusChangeReqInfo.setStatus(HlsCusAbsConstant.WORKFLOW_STATUS.APPROVING);
                    // hlsCusChangeReqInfoService.updateByPrimaryKeySelective(request, cusChangeReqInfo);
                }
                // params.put(HlsCusAbsConstant.WORKFLOW_PARAMS.WORKFLOW_TYPE, WORKFLOW_ABS_PRODUCT_NAME);
                params.put(HlsCusAbsConstant.WORKFLOW_PARAMS.DOCUMENT_NAME, hlsCusAbsProduct.getProductName());
                params.put(HlsCusAbsConstant.WORKFLOW_PARAMS.DOCUMENT_NUMBER, hlsCusAbsProduct.getProductNumber());
                params.put(HlsCusAbsConstant.WORKFLOW_PARAMS.PROJECT_ID, hlsCusAbsProduct.getProductId());
                params.put(HlsCusAbsConstant.WORKFLOW_PARAMS.DOCUMENT_CATEGORY, WORKFLOW_ABS_PRODUCT_NAME);
                params.put(IActivitiCommonService.WORK_FLOW_NAME, WORKFLOW_ABS_PRODUCT_NAME);
                params.put(IActivitiCommonService.DEMO_NAME, WORKFLOW_ABS_DEMO_NAME);
                params.put(IActivitiCommonService.BUSINESS_KEY, hlsCusAbsProduct.getProductId());
                List<HlsCusAbsProduct> productList = new ArrayList<>();
                productList.add(hlsCusAbsProduct);
                activitiStartService.start(request, productList, params);
            }
        }
    }

    @Override
    public HlsCusAbsProduct submitBuyBackData(IRequest request, Long productId) throws HlsCusException {
        HlsCusAbsProduct hlsCusAbsProduct = new HlsCusAbsProduct();
        if (productId != null) {
            hlsCusAbsProduct.setProductId(productId);
            hlsCusAbsProduct = absProductService.selectByPrimaryKey(request, hlsCusAbsProduct);
        }
        hlsCusAbsProduct.setBuybackStatus(HlsCusAbsConstant.WORKFLOW_STATUS.APPROVING);
        self().updateByPrimaryKeySelective(request, hlsCusAbsProduct);
        databaseLockProvider.lock(hlsCusAbsProduct);
        // 获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(request.getUserId());
        if (ObjectUtils.isEmpty(employee)) {
            throw new HlsCusException("获取提交人失败");
        }
        String employeeCode = employee.getEmployeeCode();
        request.setEmployeeCode(employeeCode);
        Map<String, Object> evenParams = new HashMap<>();
        evenParams.put("message", "abs清仓回购" + hlsCusAbsProduct.getProjectName() + "提交审批");
        evenParams.put("noticeTitle", NOTICE_TITLE_ABS_BUYBACK_SUBMIT);
        evenParams.put("url", "");
        evenParams.put("level", HlsCusAbsConstant.WORKFLOW_EVENT_PARAMS.EVENT_PARAMS_LEVEL);
        evenParams.put("noticeType", HlsCusAbsConstant.WORKFLOW_EVENT_PARAMS.NOTICE_TYPE_NOTICE);
        sysEventService.eventSave(request, hlsCusAbsProduct.getProductId(), hlsCusAbsProduct.getDocumentCategory(),
                        hlsCusAbsProduct.getDocumentType(), "ABS", PROPERTY_ABS_PRODUCT_BUYBACK_SUBMIT, "P2D",
                        evenParams);
        // 开始流程
        Map<String, Object> params = new HashMap<>();
        params.put(HlsCusAbsConstant.WORKFLOW_PARAMS.WORKFLOW_TYPE, HlsCusAbsConstant.ABS_WFL.BUYBACK_WFL);
        params.put(HlsCusAbsConstant.WORKFLOW_PARAMS.DOCUMENT_NAME, hlsCusAbsProduct.getProductName());
        params.put(HlsCusAbsConstant.WORKFLOW_PARAMS.DOCUMENT_NUMBER, hlsCusAbsProduct.getProjectNumber());
        params.put(HlsCusAbsConstant.WORKFLOW_PARAMS.PROJECT_ID, hlsCusAbsProduct.getProductId());
        params.put(HlsCusAbsConstant.WORKFLOW_PARAMS.DOCUMENT_CATEGORY, WORKFLOW_ABS_BUYBACK_NAME);
        params.put(IActivitiCommonService.WORK_FLOW_NAME, WORKFLOW_ABS_BUYBACK_NAME);
        params.put(IActivitiCommonService.DEMO_NAME, WORKFLOW_ABS_DEMO_NAME);
        params.put(IActivitiCommonService.BUSINESS_KEY, hlsCusAbsProduct.getProductId());
        List<HlsCusAbsProduct> productList = new ArrayList<>();
        productList.add(hlsCusAbsProduct);
        activitiStartService.start(request, productList, params);
        return hlsCusAbsProduct;
    }

    @Override
    public HlsCusAbsProduct reCalcCashProduct(IRequest request, HlsCusAbsProduct hlsCusAbsProduct, String dataClass) {
        HlsCusAbsProductCollection collection = new HlsCusAbsProductCollection();
        collection.setProductId(hlsCusAbsProduct.getProductId());
        collection.setDataClass(dataClass);
        List<HlsCusAbsProductCollection> productCollections =
                        absProductCollectionService.selectProductCollectionData(request, collection, 1, 999);

        for (HlsCusAbsProductCollection productCollection : productCollections) {
            // 归集各项费用更新 费用改为可编辑 所以注释不用系统计算
            // calculateFee(request,productCollection,hlsCusAbsProduct);
            absProductCollectionService.updateByPrimaryKeySelective(request, productCollection);

            HlsCusAbsProductCashDetail hlsCusAbsProductCashDetail = new HlsCusAbsProductCashDetail();
            hlsCusAbsProductCashDetail.setCollectionId(productCollection.getCollectionId());
            List<HlsCusAbsProductCashDetail> hlsCusAbsProductCashDetailList = absProductCashDetailService
                            .selectProductCashDetailData(request, hlsCusAbsProductCashDetail, 1, 999);

            // 利息更新
            for (HlsCusAbsProductCashDetail cashDetail : hlsCusAbsProductCashDetailList) {
                HlsCusAbsProductStructure productStructure = absProductStructureService.selectCashStructure(request,
                                cashDetail.getStructureId(), dataClass, productCollection.getTimes());
                BigDecimal interest = (productStructure.getPublishAmount()
                                .subtract(productStructure.getCashPrincipal())
                                                .multiply(productStructure.getPredictRate())
                                                .multiply(new BigDecimal(
                                                                productCollection.getInterestPeriodDays().toString()))
                                                .divide(new BigDecimal(
                                                                hlsCusAbsProduct.getInterestStandardDays().toString()),
                                                                2, BigDecimal.ROUND_HALF_UP));
                cashDetail.setPlanCashInterest(BigDecimal.valueOf(interest.doubleValue()));
                absProductCashDetailService.updateByPrimaryKeySelective(request, cashDetail);
            }
        }
        // 更新费用金额
        absProFeeInfoService.updateProductFeeAmount(hlsCusAbsProduct.getProductId());
        return hlsCusAbsProduct;
    }

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
        if (c2.get(Calendar.MONTH) < c1.get(Calendar.MONTH) || c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
                        && c2.get(Calendar.DAY_OF_MONTH) < c1.get(Calendar.DAY_OF_MONTH)) {
            yearInterval--;
        }
        int monthInterval = (c2.get(Calendar.MONTH) + 12) - c1.get(Calendar.MONTH);
        if (c2.get(Calendar.DAY_OF_MONTH) > c1.get(Calendar.DAY_OF_MONTH)) {
            monthInterval++;
        }
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
}
