package com.hand.hls.inv.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.inv.dto.HlsCusFinancePurchase;
import com.hand.hls.inv.dto.HlsCusFinanceRedeem;
import com.hand.hls.inv.dto.HlsCusInvFinanceDetail;
import com.hand.hls.inv.mapper.HlsCusInvFinanceDetailMapper;
import com.hand.hls.inv.service.HlsCusInvFinanceDetailService;
import com.hand.hls.user.service.LoginUserInfoService;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.sys.event.service.SysEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusInvFinanceDetailServiceImpl extends BaseServiceImpl<HlsCusInvFinanceDetail> implements HlsCusInvFinanceDetailService {

    private final static Logger logger = LoggerFactory.getLogger(HlsCusFinancePurchaseServiceImpl.class);

    @Autowired
    private HlsCusInvFinanceDetailMapper hlsCusInvFinanceDetailMapper;

    @Autowired
    private HlsEmployeeMapper employeeMapper;

    @Autowired
    private IActivitiStartService activitiStartService;

    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Autowired
    private LoginUserInfoService loginUserInfoService;

    @Autowired
    private SysEventService sysEventService;

    @Autowired
    private HlsCusInvFinanceDetailService hlsCusInvFinanceDetailService;



    public List<HlsCusInvFinanceDetail> queryInvDoneFinanceList(IRequest iRequest, HlsCusInvFinanceDetail hlsCusInvFinanceDetail, int page, int pageSize) {
        return hlsCusInvFinanceDetailMapper.queryInvDoneFinanceList(hlsCusInvFinanceDetail);
    }


    public HlsCusFinancePurchase invFinanceDetailSubmit(IRequest iRequest, HlsCusFinancePurchase hlsCusFinancePurchase) {
        //进行保存
        List<HlsCusFinancePurchase> list = new ArrayList<>();
        logger.debug("=============== start purchase activiti ==============");
        databaseLockProvider.lock(hlsCusFinancePurchase);
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
            params.put("workFlowType", "INV_ACTUAL_PURCHASE_WFL");
            String msg = userName + "新建了" + hlsCusFinancePurchase.getFinancialProductsName() + "产品实际申购" + hlsCusFinancePurchase.getPurchaseNumber();
            paramsEvent.put("message", msg);
            paramsEvent.put("noticeTitle", "产品实际申购审批");
        } else if ("ADD".equals(hlsCusFinancePurchase.getNewOrAdd())) {
            logger.debug("================ start purchase add activiti =================");
            params.put("workFlowType", "INV_ACTUAL_ADDTIONAL_PURCHASE_WFL");
            String msg = userName + "新建了" + hlsCusFinancePurchase.getFinancialProductsName() + "产品追加申购" + hlsCusFinancePurchase.getPurchaseNumber();
            paramsEvent.put("message", msg);
            paramsEvent.put("noticeTitle", "产品实际追加申购审批");
        } else {
            logger.error("not create or add purchase");
        }

        List<HlsCusInvFinanceDetail> hlsCusInvFinanceDetailList = hlsCusFinancePurchase.getHlsCusInvFinanceDetailList();
        for (HlsCusInvFinanceDetail dt : hlsCusInvFinanceDetailList) {
            dt.setApprovedStatus("APPROVING");
            dt = hlsCusInvFinanceDetailService.updateByPrimaryKeySelective(iRequest, dt);
        }
        hlsCusFinancePurchase.setHlsCusInvFinanceDetailList(hlsCusInvFinanceDetailList);
        list.add(hlsCusFinancePurchase);
        activitiStartService.start(iRequest, list, params);

        //消息

        HlsCusFinancePurchase purchase = list.get(0);

        paramsEvent.put("noticeType", "NOTICE");
        paramsEvent.put("url", "");
        paramsEvent.put("level", 1L);
        sysEventService.eventSave(iRequest, purchase.getFinancePurchaseId(), purchase.getDocumentCategory(), purchase.getDocumentType(), "INV", "INV_FINANCE", "P2D", paramsEvent);
        return hlsCusFinancePurchase;
    }


    public HlsCusFinanceRedeem invFinanceDetailSubmit(IRequest iRequest, HlsCusFinanceRedeem hlsCusFinanceRedeem) {
        //进行保存
        List<HlsCusFinanceRedeem> list = new ArrayList<>();
        logger.debug("=============== start purchase activiti ==============");
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
        logger.debug("================ start purchase create activiti =================");
        params.put("workFlowType", "INV_ACTUAL_REDEEM_WFL");
        String msg = userName + "新建了" + hlsCusFinanceRedeem.getPurchaseProductName() + "产品实际赎回" + hlsCusFinanceRedeem.getRedemptionNumber();
        paramsEvent.put("message", msg);
        paramsEvent.put("noticeTitle", "产品实际赎回审批");


        List<HlsCusInvFinanceDetail> hlsCusInvFinanceDetailList = hlsCusFinanceRedeem.getHlsCusInvFinanceDetailList();
        for (HlsCusInvFinanceDetail dt : hlsCusInvFinanceDetailList) {
            dt.setApprovedStatus("APPROVING");
            dt = hlsCusInvFinanceDetailService.updateByPrimaryKeySelective(iRequest, dt);
        }
        hlsCusFinanceRedeem.setHlsCusInvFinanceDetailList(hlsCusInvFinanceDetailList);
        list.add(hlsCusFinanceRedeem);
        activitiStartService.start(iRequest, list, params);

        //消息


        paramsEvent.put("noticeType", "NOTICE");
        paramsEvent.put("url", "");
        paramsEvent.put("level", 1L);
        sysEventService.eventSave(iRequest, hlsCusFinanceRedeem.getFinanceRedeemId(), hlsCusFinanceRedeem.getDocumentCategory(), hlsCusFinanceRedeem.getDocumentType(), "INV", "INV_FINANCE", "P2D", paramsEvent);
        return hlsCusFinanceRedeem;
    }

    @Override
    public List<HlsCusInvFinanceDetail> queryInvFinanceDetal(IRequest iRequest, HlsCusInvFinanceDetail hlsCusInvFinanceDetail, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return hlsCusInvFinanceDetailMapper.queryInvFinanceDetal(hlsCusInvFinanceDetail);
    }
}