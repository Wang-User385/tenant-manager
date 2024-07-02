package com.hand.hls.inv.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.inv.dto.HlsCusFinanceRedeem;
import com.hand.hls.inv.dto.HlsCusFinanceRedeemBak;
import com.hand.hls.inv.mapper.HlsCusFinanceRedeemBakMapper;
import com.hand.hls.inv.service.HlsCusIFinanceRedeemBakService;
import com.hand.hls.inv.service.HlsCusIFinanceRedeemService;
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
public class HlsCusFinanceRedeemBakServiceImpl extends BaseServiceImpl<HlsCusFinanceRedeemBak> implements HlsCusIFinanceRedeemBakService {

    @Autowired
    private HlsCusFinanceRedeemBakMapper mapper;

    @Autowired
    private IActivitiStartService activitiStartService;

    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Autowired
    private HlsEmployeeMapper employeeMapper;

    @Autowired
    private HlsCusIFinanceRedeemService redeemService;

    @Autowired
    private LoginUserInfoService loginUserInfoService;

    @Autowired
    private SysEventService sysEventService;

    private static final Logger logger = LoggerFactory.getLogger(HlsCusFinanceRedeemBakServiceImpl.class);


    @Override
    public HlsCusFinanceRedeemBak submitBak(IRequest iRequest, HlsCusFinanceRedeemBak hlsCusFinanceRedeemBak) {
        hlsCusFinanceRedeemBak.set__status("ADD");
        if ("BASE".equals(hlsCusFinanceRedeemBak.getUpdateMethod()) || "INFO".equals(hlsCusFinanceRedeemBak.getUpdateMethod())) {
            hlsCusFinanceRedeemBak.setStatus("APPROVED");
        }else {
            hlsCusFinanceRedeemBak.setStatus("NEW");
        }
        return self().insertSelective(iRequest,hlsCusFinanceRedeemBak);
    }

    @Override
    public List<HlsCusFinanceRedeemBak> queryAll(IRequest iRequest, HlsCusFinanceRedeemBak hlsCusFinanceRedeemBak, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return mapper.queryAll(hlsCusFinanceRedeemBak);
    }

    @Override
    public List<HlsCusFinanceRedeemBak> submitWfl(IRequest iRequest, HlsCusFinanceRedeemBak hlsCusFinanceRedeemBak) {
        HlsCusFinanceRedeemBak redeemBak = self().submitBak(iRequest, hlsCusFinanceRedeemBak);
        databaseLockProvider.lock(redeemBak);

        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);
        Map<String, Object> params = new HashMap<String, Object>();
        logger.debug("================ start redeem change activiti =================");
        params.put("workFlowType", "INV_REDEEM_CHANGE_WFL");
        List<HlsCusFinanceRedeemBak> list = new ArrayList<>();
        list.add(redeemBak);
        activitiStartService.start(iRequest, list, params);

        list.get(0).set__status("update");
        list.get(0).setStatus("PENDING");
        HlsCusFinanceRedeem financeRedeem = new HlsCusFinanceRedeem();
        financeRedeem.setFinanceRedeemId(redeemBak.getFinanceRedeemId());
        financeRedeem.setRedemptionStatus("PENDING");
        redeemService.updateByPrimaryKeySelective(iRequest, financeRedeem);

        //消息发送
        Map<String, Object> paramsEvent = new HashMap<String, Object>();
        String userName = "";
        if (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode()).size() > 0) {
            userName = (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode())).get(0).getUserName();
        }
        String msg = userName + "创建了" + redeemBak.getPurchaseProductName() + "投资理财赎回变更" + redeemBak.getRedemptionNumber();
        paramsEvent.put("message", msg);
        paramsEvent.put("noticeTitle", "投资理财赎回变更");
        paramsEvent.put("noticeType", "NOTICE");
        paramsEvent.put("url", "");
        paramsEvent.put("level", 1L);
        sysEventService.eventSave(iRequest, redeemBak.getFinanceRedeemId(), redeemBak.getDocumentCategory(), redeemBak.getDocumentType(), "INV", "INV_FINANCE", "P2D", paramsEvent);


        return self().batchUpdate(iRequest, list);
    }
}