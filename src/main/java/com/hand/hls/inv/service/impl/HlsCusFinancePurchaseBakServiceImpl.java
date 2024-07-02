package com.hand.hls.inv.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.inv.dto.HlsCusFinancePurchase;
import com.hand.hls.inv.dto.HlsCusFinancePurchaseBak;
import com.hand.hls.inv.mapper.HlsCusFinancePurchaseBakMapper;
import com.hand.hls.inv.service.HlsCusIFinancePurchaseBakService;
import com.hand.hls.inv.service.HlsCusIFinancePurchaseService;
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
public class HlsCusFinancePurchaseBakServiceImpl extends BaseServiceImpl<HlsCusFinancePurchaseBak> implements HlsCusIFinancePurchaseBakService {

    @Autowired
    private HlsCusFinancePurchaseBakMapper mapper;

    @Autowired
    private IActivitiStartService activitiStartService;

    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Autowired
    private HlsEmployeeMapper employeeMapper;

    @Autowired
    private HlsCusIFinancePurchaseService purchaseService;

    @Autowired
    private LoginUserInfoService loginUserInfoService;

    @Autowired
    private SysEventService sysEventService;

    private static final Logger logger = LoggerFactory.getLogger(HlsCusFinancePurchaseBakServiceImpl.class);

    @Override
    public HlsCusFinancePurchaseBak submitBak(IRequest iRequest, HlsCusFinancePurchaseBak dto) {
        if ("BASE".equals(dto.getUpdateMethod()) || "INFO".equals(dto.getUpdateMethod())) {
            dto.setStatus("APPROVED");
        }else {
            dto.setStatus("NEW");
        }
        return self().insertSelective(iRequest, dto);
    }

    @Override
    public List<HlsCusFinancePurchaseBak> queryAll(IRequest iRequest, HlsCusFinancePurchaseBak hlsCusFinancePurchaseBak, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return mapper.queryAll(hlsCusFinancePurchaseBak);
    }

    @Override
    public List<HlsCusFinancePurchaseBak> submitWfl(IRequest iRequest, HlsCusFinancePurchaseBak hlsCusFinancePurchaseBak) {
        HlsCusFinancePurchaseBak purchaseBak = self().submitBak(iRequest, hlsCusFinancePurchaseBak);
        databaseLockProvider.lock(purchaseBak);

        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);
        Map<String, Object> params = new HashMap<String, Object>();

        //消息发送参数
        Map<String, Object> paramsEvent = new HashMap<String, Object>();
        String userName = "";
        if (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode()).size() > 0) {
            userName = (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode())).get(0).getUserName();
        }

        if ("NEW".equals(purchaseBak.getNewOrAdd())) {
            logger.debug("================ start purchase change activiti =================");
            params.put("workFlowType", "INV_PURCHASE_CHANGE_WFL");
            String msg = userName + "新建了" +purchaseBak.getFinancialProductName() + "产品申购金额变更" + purchaseBak.getPurchaseNumber();
            paramsEvent.put("message", msg);
            paramsEvent.put("noticeTitle", "产品新建申购金额变更审批");
        }else {
            logger.debug("================ start purchase add change activiti =================");
            params.put("workFlowType", "INV_PURCHASE_ADD_CHANGE_WFL");
            String msg = userName + "新建了" +purchaseBak.getFinancialProductName() + "产品追加金额变更" + purchaseBak.getPurchaseNumber();
            paramsEvent.put("message", msg);
            paramsEvent.put("noticeTitle", "产品追加金额变更审批");
        }
        List<HlsCusFinancePurchaseBak> list = new ArrayList<>();
        list.add(purchaseBak);
        activitiStartService.start(iRequest, list, params);
        list.get(0).set__status("update");
        list.get(0).setStatus("PENDING");

        HlsCusFinancePurchase purchase = new HlsCusFinancePurchase();
        purchase.setPurchaseStatus("PENDING");
        purchase.setFinancePurchaseId(purchaseBak.getFinancePurchaseId());
        purchaseService.updateByPrimaryKeySelective(iRequest, purchase);

        paramsEvent.put("noticeType", "NOTICE");
        paramsEvent.put("url", "");
        paramsEvent.put("level", 1L);
        sysEventService.eventSave(iRequest, purchaseBak.getFinancePurchaseId(), purchaseBak.getDocumentCategory(), purchaseBak.getDocumentType(), "INV", "INV_FINANCE", "P2D", paramsEvent);


        return self().batchUpdate(iRequest,list);
    }
}