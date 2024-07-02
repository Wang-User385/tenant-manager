package com.hand.hls.inv.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.inv.dto.HlsCusFinancePurchase;
import com.hand.hls.inv.mapper.HlsCusFinanceAttachmentMapper;
import com.hand.hls.inv.mapper.HlsCusFinancePurchaseMapper;
import com.hand.hls.inv.mapper.HlsCusFinanceRedeemMapper;
import com.hand.hls.inv.service.HlsCusIFinanceAttachmentService;
import com.hand.hls.inv.service.HlsCusIFinancePurchaseBakService;
import com.hand.hls.inv.service.HlsCusIFinancePurchaseDetailIdBakService;
import com.hand.hls.inv.service.HlsCusIFinancePurchaseService;
import com.hand.hls.user.service.LoginUserInfoService;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.sys.event.service.SysEventService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:申购serviceImpl
 * @Author: wty
 * @Date: Created in 14:45 2018/4/17
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusFinancePurchaseServiceImpl extends BaseServiceImpl<HlsCusFinancePurchase> implements HlsCusIFinancePurchaseService {

    private final static Logger logger = LoggerFactory.getLogger(HlsCusFinancePurchaseServiceImpl.class);

    @Autowired
    private HlsCusFinancePurchaseMapper mapper;

    @Autowired
    private HlsCusFinanceRedeemMapper redeemMapper;

    @Autowired
    private HlsEmployeeMapper employeeMapper;

    @Autowired
    private IActivitiStartService activitiStartService;

    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;

    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Autowired
    private LoginUserInfoService loginUserInfoService;

    @Autowired
    private SysEventService sysEventService;

    @Autowired
    private HlsCusFinanceAttachmentMapper attachmentMapper;

    @Autowired
    private HlsCusIFinanceAttachmentService attachmentService;

    @Autowired
    private HlsCusIFinancePurchaseDetailIdBakService hlsCusIFinancePurchaseDetailIdBakService;

    @Autowired
    private HlsCusIFinancePurchaseBakService bakService;

    /**
     * @Description:申购保存
     * @Author: Wty
     * @Date: Created om 10:19 2018/4/26
     */
    @Override
    public HlsCusFinancePurchase invPurchaseSave(IRequest iRequest, HlsCusFinancePurchase dto) {
        if (dto.getFinancePurchaseId() == null || dto.getFinancePurchaseId() == 0) {
            dto.setDocumentCategory("INV_FINANCE");
            dto.setDocumentType("INV_FINANCE");
            dto.setBusinessType("PURCHASE");
            dto.setAllApproved("N");
            dto.setPurchaseStatus("NEW");
            dto.setInvalidBak("NORMAL");
            dto.setConfirmStatus("NOT");
            dto.setPurchasedAmount(0D);
            dto.setRedeemedAmount(0D);
            Map<String, String> params = new HashMap<String, String>();
            dto.setPurchaseNumber(fndCodingRuleValuesService.getCodeRuleValue(iRequest, dto.getDocumentCategory(), dto.getDocumentType(), dto.getBusinessType(), params));
            dto = self().insertSelective(iRequest, dto);
        } else {
            dto = self().updateByPrimaryKeySelective(iRequest, dto);
        }
        return dto;
    }

    @Override
    public List<HlsCusFinancePurchase> selectBaseInfo(IRequest request, HlsCusFinancePurchase dto, HttpSession session, int page, int pageSize) {

        PageHelper.startPage(page, pageSize);
        dto.setPurchaseApplicant(request.getUserId());
        dto.setCompanyId(request.getCompanyId());
        return mapper.selectBaseInfo(dto);
    }

    @Override
    public List<HlsCusFinancePurchase> queryAll(IRequest request, HlsCusFinancePurchase dto, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return mapper.queryAll(dto);
    }

    @Override
    public List<HlsCusFinancePurchase> querySameProductName(IRequest request, HlsCusFinancePurchase dto, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return mapper.querySameProductName(dto);
    }


    @Override
    public List<HlsCusFinancePurchase> homeThirdQuery(IRequest request, HlsCusFinancePurchase dto, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        PageHelper.orderBy("creation_date desc");
        dto.setCompanyId(request.getCompanyId());
        dto.setNewOrAdd("NEW");
        if (dto.getFinancialTypes() != null) {
            String[] financialTypeStatus = dto.getFinancialTypes().split(",");
            dto.setFinancialTypeStatus(financialTypeStatus);
        }
        if (dto.getRedeemTypes() != null) {
            String[] redeemTypeStatus = dto.getRedeemTypes().split(",");
            dto.setRedeemTypeStatus(redeemTypeStatus);
        }
        if (dto.getFinancialCategories() != null) {
            String[] financialCategoryStatus = dto.getFinancialCategories().split(",");
            dto.setFinancialCategoryStatus(financialCategoryStatus);
        }
        //查询所有新建的申购
        List<HlsCusFinancePurchase> list = mapper.detailPurchaseQuery(dto);
        //查询单据的具体状态 如新建申购，赎回审批完成等
        if (CollectionUtils.isNotEmpty(list)) {
            for (int i = 0; i < list.size(); i++) {
                PageHelper.orderBy("creation_date desc");
                HlsCusFinancePurchase maxDatePurchase = new HlsCusFinancePurchase();
                maxDatePurchase.setFinancePurchaseId(list.get(i).getFinancePurchaseId());
                List<HlsCusFinancePurchase> historyList = mapper.searchHistory(maxDatePurchase);
                if (CollectionUtils.isNotEmpty(historyList)) {
                    String documentDetailStatus = "";
                    maxDatePurchase = historyList.get(0);
                    if ("NEW".equals(maxDatePurchase.getNewOrAdd())) {
                        documentDetailStatus += "首次申购";
                    } else if ("ADD".equals(maxDatePurchase.getNewOrAdd())) {
                        documentDetailStatus += "追加";
                    } else {
                        documentDetailStatus += "赎回";
                    }

                    if ("APPROVED_RETURN".equals(maxDatePurchase.getPurchaseStatus())) {
                        documentDetailStatus += "审批退回";
                    } else if ("APPROVED".equals(maxDatePurchase.getPurchaseStatus())) {
                        documentDetailStatus += "审批完成";
                    } else if ("NEW".equals(maxDatePurchase.getPurchaseStatus())) {
                        documentDetailStatus += "新建";
                    } else if ("APPROVING".equals(maxDatePurchase.getPurchaseStatus())) {
                        documentDetailStatus += "审批中";
                    } else if ("CANCEL".equals(maxDatePurchase.getPurchaseStatus())) {
                        documentDetailStatus += "撤销";
                    } else if ("INVALID".equals(maxDatePurchase.getPurchaseStatus())) {
                        documentDetailStatus += "作废";
                    } else if ("PENDING".equals(maxDatePurchase.getPurchaseStatus())) {
                        documentDetailStatus = "暂挂";
                    }
                    list.get(i).setDocumentDetailStatus(documentDetailStatus);
                }

            }
        }
        return list;
    }

    /**
     * @Description:赎回明细等具体基本信息查询
     * @Author: Wty
     * @Date: Created om 16:02 2018/4/24
     */
    @Override
    public List<HlsCusFinancePurchase> detailPurchaseQuery(IRequest request, HlsCusFinancePurchase dto, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
//        dto.setCompanyId(request.getCompanyId());
        return mapper.detailPurchaseQuery(dto);
    }

    /**
     * @Description:校验是否存在新建或者退回的追加
     * @Author: Wty
     * @Date: Created om 14:34 2018/4/25
     */
    @Override
    public List<HlsCusFinancePurchase> purchaseCheckNewOrReturn(IRequest request, HlsCusFinancePurchase dto, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return mapper.purchaseCheckNewOrReturn(dto);
    }

    /**
     * @Description:新建申购提交工作流
     * @Author: Wty
     * @Date: Created om 9:37 2018/4/26
     */
    @Override
    public List<HlsCusFinancePurchase> purchaseSubmitWfl(IRequest iRequest, HlsCusFinancePurchase hlsCusFinancePurchase) {
        //进行保存
        List<HlsCusFinancePurchase> list = new ArrayList<>();
        hlsCusFinancePurchase = self().invPurchaseSave(iRequest, hlsCusFinancePurchase);
        list.add(hlsCusFinancePurchase);
        if (CollectionUtils.isNotEmpty(list)) {
            logger.debug("=============== start purchase activiti ==============");
            databaseLockProvider.lock(list.get(0));
            //获取申请人
            HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
            String employeeCode = employee.getEmployeeCode();
            iRequest.setEmployeeCode(employeeCode);
            //开始流程
            //消息参数
            Map<String, Object> paramsEvent = new HashMap<String, Object>();
            String userName = "";
            if (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode()).size() > 0) {
                userName = (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode())).get(0).getUserName();
            }

            Map<String, Object> params = new HashMap<String, Object>();
            if ("NEW".equals(hlsCusFinancePurchase.getNewOrAdd())) {
                logger.debug("================ start purchase create activiti =================");
                params.put("workFlowType", "INV_PURCHASE_WFL");
                String msg = userName + "新建了" + list.get(0).getFinancialProductsName() + "产品申购" + list.get(0).getPurchaseNumber();
                paramsEvent.put("message", msg);
                paramsEvent.put("noticeTitle", "产品新建申购审批");
            } else if ("ADD".equals(hlsCusFinancePurchase.getNewOrAdd())) {
                logger.debug("================ start purchase add activiti =================");
                params.put("workFlowType", "INV_PURCHASE_ADD_WFL");
                String msg = userName + "追加了" + list.get(0).getFinancialProductName() + "产品申购" + list.get(0).getPurchaseNumber();
                paramsEvent.put("message", msg);
                paramsEvent.put("noticeTitle", "产追加申购审批");
            } else {
                logger.error("not create or add purchase");
            }

            activitiStartService.start(iRequest, list, params);
            list.get(0).setPurchaseStatus("APPROVING");
            list.get(0).set__status("update");
            list = self().batchUpdate(iRequest, list);


            HlsCusFinancePurchase purchase = list.get(0);

            paramsEvent.put("noticeType", "NOTICE");
            paramsEvent.put("url", "");
            paramsEvent.put("level", 1L);
            sysEventService.eventSave(iRequest, purchase.getFinancePurchaseId(), purchase.getDocumentCategory(), purchase.getDocumentType(), "INV", "INV_FINANCE", "P2D", paramsEvent);

        }
        return list;
    }

    /**
     * @Description:追加审批完成后查询是否完全赎回
     * @Author: Wty
     * @Date: Created om 11:40 2018/4/27
     */
    @Override
    public List<HlsCusFinancePurchase> checkIsRedeemed(IRequest iRequest, HlsCusFinancePurchase hlsCusFinancePurchase) {
        return mapper.checkIsRedeemed(hlsCusFinancePurchase);
    }

    /**
     * @Description:查询单据历史信息
     * @Author: Wty
     * @Date: Created om 11:41 2018/4/27
     */
    @Override
    public List<HlsCusFinancePurchase> searchHistory(IRequest iRequest, HlsCusFinancePurchase hlsCusFinancePurchase, int page, int pageSize) {

        // PageHelper.orderBy("ifps.creation_date desc");
        List<HlsCusFinancePurchase> list = mapper.searchHistory(hlsCusFinancePurchase);
        return getIsChangeList(list);
    }

    //判断是否能进行变更
    private List<HlsCusFinancePurchase> getIsChangeList(List<HlsCusFinancePurchase> list) {
        logger.debug("============== " + list.size() + " ==============");
        int changeSite = -1;//用来记录是哪个可以变更
        int forSite = -1;//用来记录哪个节点循环结束的
        Boolean needSetChange = true;//判断是否已经设置change
        if (CollectionUtils.isNotEmpty(list)) {
            for (int i = 0; i < list.size(); i++) {
                String status = list.get(i).getPurchaseStatus();
                //如果存在新建审批中审批退回暂挂的单据 则不能进行变更
                if ("NEW".equals(status) || "APPROVING".equals(status) || "APPROVED_RETURN".equals(status) || "PENDING".equals(status)) {
                    forSite = i;
                    break;
                } else {
                    if (needSetChange) {
                        if ("APPROVED".equals(status)) {
                            needSetChange = false;
                            list.get(i).setWhetherChange("CHANGE");
                            changeSite = i;
                        } else if ("INVALID".equals(status)) {
                            continue;
                        }
                    }
                }
            }
        }
        if (forSite != -1 && forSite < list.size()) {
            if (!needSetChange) {
                list.get(changeSite).setWhetherChange(null);
            }
        }
        //只有最新的单据时同类型的才能做变更
        return list;
    }


    /**
     * @Description:首页第一块第一个tab查询
     * @Author: Wty
     * @Date: Created om 14:04 2018/5/3
     */
    @Override
    public List<HlsCusFinancePurchase> selectHomeFirstOneTab(IRequest iRequest, HlsCusFinancePurchase hlsCusFinancePurchase) {
        hlsCusFinancePurchase.setCompanyId(iRequest.getCompanyId());
        List<HlsCusFinancePurchase> list = mapper.selectHomeFirstOneTab(hlsCusFinancePurchase);
        Long currentTypePercent = 0L;
        Long currentAmountPercent = 0L;
        if (CollectionUtils.isNotEmpty(list)) {
            for (int i = 0; i < list.size() - 1; i++) {
                currentTypePercent += list.get(i).getTypeCountPercent();
                currentAmountPercent += list.get(i).getInverstmentAmountPercent();
            }
            list.get(list.size() - 1).setTypeCountPercent(100L - currentTypePercent);
            list.get(list.size() - 1).setInverstmentAmountPercent(100L - currentAmountPercent);
        }
        return list;
    }

      /**
     * @Description:首页第一块第二个tab查询
     * @Author: Wty
     * @Date: Created om 14:04 2018/5/3
     */
    @Override
    public List<HlsCusFinancePurchase> selectHomeFirstTwoTab(IRequest iRequest, HlsCusFinancePurchase hlsCusFinancePurchase) {
        hlsCusFinancePurchase.setCompanyId(iRequest.getCompanyId());
        return mapper.selectHomeFirstTwoTab(hlsCusFinancePurchase);
    }

    /**
     * @Description:首页第一块第三个个tab查询
     * @Author: Wty
     * @Date: Created om 14:04 2018/7/26
     */
    @Override
    public List<HlsCusFinancePurchase> selectHomeFirstThreeTab(IRequest iRequest, HlsCusFinancePurchase hlsCusFinancePurchase) {
        hlsCusFinancePurchase.setCompanyId(iRequest.getCompanyId());
        return mapper.selectHomeFirstThreeTab(hlsCusFinancePurchase);
    }


    /**
     * @Description:综合查询
     * @Author: Wty
     * @Date: Created om 13:34 2018/5/4
     */
    @Override
    public List<HlsCusFinancePurchase> integratedQuery(IRequest iRequest, HlsCusFinancePurchase hlsCusFinancePurchase, int page, int pageSize) {
        return self().homeThirdQuery(iRequest, hlsCusFinancePurchase, page, pageSize);
    }

    /**
     * @Description:作废工作流
     * @Author: Wty
     * @Date: Created om 13:44 2018/5/9
     */
    @Override
    public HlsCusFinancePurchase purchaseInvalidSubmit(IRequest iRequest, HlsCusFinancePurchase hlsCusFinancePurchase) {
        hlsCusFinancePurchase.setPurchaseStatus("INVALID");
        hlsCusFinancePurchase = self().updateByPrimaryKeySelective(iRequest, hlsCusFinancePurchase);
        return hlsCusFinancePurchase;
    }

    /**
     * @Description:变更基本信息保存
     * @Author: Wty
     */
    @Override
    public HlsCusFinancePurchase purchaseChangesSubmit(IRequest iRequest, HlsCusFinancePurchase hlsCusFinancePurchase) {
        bakService.submitBak(iRequest, hlsCusFinancePurchase.getHlsCusFinancePurchaseBak());
        return self().invPurchaseSave(iRequest, hlsCusFinancePurchase);
    }

    @Override
    public List<HlsCusFinancePurchase> purchaseChangesSubmitWfl(IRequest iRequest, HlsCusFinancePurchase hlsCusFinancePurchase) {
        //进行保存
        List<HlsCusFinancePurchase> list = new ArrayList<>();
        hlsCusFinancePurchase = self().invPurchaseSave(iRequest, hlsCusFinancePurchase);
        list.add(hlsCusFinancePurchase);
        if (CollectionUtils.isNotEmpty(list)) {
            logger.debug("=============== start purchase change activiti ==============");
            databaseLockProvider.lock(list.get(0));
            //获取申请人
            HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
            String employeeCode = employee.getEmployeeCode();
            iRequest.setEmployeeCode(employeeCode);
            //开始流程
            //消息参数
            Map<String, Object> paramsEvent = new HashMap<String, Object>();
            String userName = "";
            if (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode()).size() > 0) {
                userName = (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode())).get(0).getUserName();
            }

            Map<String, Object> params = new HashMap<String, Object>();
            if ("NEW".equals(hlsCusFinancePurchase.getNewOrAdd())) {
                logger.debug("================ start purchase change create activiti =================");
                params.put("workFlowType", "INV_PURCHASE_CHANGE_WFL");
                String msg = userName + "新建了" + list.get(0).getFinancialProductsName() + "产品申购的金额变更" + list.get(0).getPurchaseNumber();
                paramsEvent.put("message", msg);
                paramsEvent.put("noticeTitle", "产品新建申购金额变更审批");
            } else if ("ADD".equals(hlsCusFinancePurchase.getNewOrAdd())) {
                logger.debug("================ start purchase add activiti =================");
                params.put("workFlowType", "INV_PURCHASE_ADD_CHANGE_WFL");
                String msg = userName + "新建了" + list.get(0).getFinancialProductsName() + "产品追加申购的金额变更" + list.get(0).getPurchaseNumber();
                paramsEvent.put("message", msg);
                paramsEvent.put("noticeTitle", "产追加申购金额变更审批");
            } else {
                logger.error("not create or add purchase");
            }

            activitiStartService.start(iRequest, list, params);

            hlsCusFinancePurchase.setPurchaseStatus("PENDING");
            hlsCusFinancePurchase = self().updateByPrimaryKeySelective(iRequest, hlsCusFinancePurchase);


            HlsCusFinancePurchase purchase = list.get(0);

            paramsEvent.put("noticeType", "NOTICE");
            paramsEvent.put("url", "");
            paramsEvent.put("level", 1L);
            sysEventService.eventSave(iRequest, purchase.getFinancePurchaseId(), purchase.getDocumentCategory(), purchase.getDocumentType(), "INV", "INV_FINANCE", "P2D", paramsEvent);

        }
        return list;
    }

    /**
     * @Description:首页金额查询
     * @Author: Wty
     * @Date: Created om 下午12:24 2018/7/1
     */
    @Override
    public List<Map> homeChartThirdQueryAmount(IRequest iRequest, Map map) {
        map.put("companyId", iRequest.getCompanyId());
        if (map.get("productName") != null && map.get("productName") != "") {
            return mapper.homeChartThirdQueryAmount(map);
        } else {
            return mapper.homeChartThirdQueryAllAmount(map);
        }

    }

    @Override
    public List<HlsCusFinancePurchase> queryInvPurchaseDetail(IRequest iRequest, HlsCusFinancePurchase financePurchase) {
        return mapper.queryInvPurchaseDetail(financePurchase);
    }

    @Override
    public List<HlsCusFinancePurchase> queryInvPurchaseList(IRequest iRequest, HlsCusFinancePurchase dto, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        dto.setCompanyId(iRequest.getCompanyId());
        dto.setNewOrAdd("NEW");
        if (dto.getFinancialTypes() != null) {
            String[] financialTypeStatus = dto.getFinancialTypes().split(",");
            dto.setFinancialTypeStatus(financialTypeStatus);
        }
        if (dto.getRedeemTypes() != null) {
            String[] redeemTypeStatus = dto.getRedeemTypes().split(",");
            dto.setRedeemTypeStatus(redeemTypeStatus);
        }
        //查询所有新建的申购
        List<HlsCusFinancePurchase> list = mapper.queryInvPurchaseList(dto);
        //查询单据的具体状态 如新建申购，赎回审批完成等
        if (CollectionUtils.isNotEmpty(list)) {
            for (HlsCusFinancePurchase dt : list) {
                HlsCusFinancePurchase maxDatePurchase = new HlsCusFinancePurchase();
                maxDatePurchase.setFinancePurchaseId(dt.getFinancePurchaseId());
                List<HlsCusFinancePurchase> historyList = mapper.searchHistory(maxDatePurchase);
                maxDatePurchase = historyList.get(0);
                String documentDetailStatus = "";
                if ("NEW".equals(maxDatePurchase.getNewOrAdd())) {
                    documentDetailStatus += "首次申购";
                } else if ("ADD".equals(maxDatePurchase.getNewOrAdd())) {
                    documentDetailStatus += "追加";
                } else {
                    documentDetailStatus += "赎回";
                }

                if ("APPROVED_RETURN".equals(maxDatePurchase.getPurchaseStatus())) {
                    documentDetailStatus += "审批退回";
                } else if ("APPROVED".equals(maxDatePurchase.getPurchaseStatus())) {
                    documentDetailStatus += "审批完成";
                } else if ("NEW".equals(maxDatePurchase.getPurchaseStatus())) {
                    documentDetailStatus += "新建";
                } else if ("APPROVING".equals(maxDatePurchase.getPurchaseStatus())) {
                    documentDetailStatus += "审批中";
                } else if ("CANCEL".equals(maxDatePurchase.getPurchaseStatus())) {
                    documentDetailStatus += "撤销";
                } else if ("INVALID".equals(maxDatePurchase.getPurchaseStatus())) {
                    documentDetailStatus += "作废";
                } else if ("PENDING".equals(maxDatePurchase.getPurchaseStatus())) {
                    documentDetailStatus = "暂挂";
                }
                dt.setDocumentDetailStatus(documentDetailStatus);
            }

        }
        return list;
    }

    @Override
    public List<HlsCusFinancePurchase> queryInvDoFinance(IRequest iRequest, HlsCusFinancePurchase financePurchase, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return mapper.queryInvDoFinance(financePurchase);
    }

    @Override
    public List<HlsCusFinancePurchase> queryProductsFinanceDetail(IRequest iRequest, HlsCusFinancePurchase financePurchase) {
        return mapper.queryProductsFinanceDetail(financePurchase);
    }

    @Override
    public List<HlsCusFinancePurchase> queryById(IRequest iRequest, HlsCusFinancePurchase financePurchase) {
        return mapper.queryById(financePurchase);
    }

}