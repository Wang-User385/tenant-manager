package com.hand.hls.inv.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.inv.dto.HlsCusFinanceRedeem;
import com.hand.hls.inv.dto.HlsCusInvFinanceDetail;
import com.hand.hls.inv.mapper.HlsCusFinanceRedeemMapper;
import com.hand.hls.inv.service.HlsCusIFinanceRedeemBakService;
import com.hand.hls.inv.service.HlsCusIFinanceRedeemService;
import com.hand.hls.inv.service.HlsCusInvFinanceDetailService;
import com.hand.hls.user.service.LoginUserInfoService;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.sys.event.service.SysEventService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @Description:赎回serviceImpl
 * @Author: wty
 * @Date: Created in 14:45 2018/4/17
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusFinanceRedeemServiceImpl extends BaseServiceImpl<HlsCusFinanceRedeem> implements HlsCusIFinanceRedeemService {
    @Autowired
    private HlsCusFinanceRedeemMapper mapper;

    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;

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
    private HlsCusIFinanceRedeemBakService redeemBakService;

    @Autowired
    private HlsCusInvFinanceDetailService detailService;



    private static final Logger logger = LoggerFactory.getLogger(HlsCusFinanceRedeemServiceImpl.class);

    @Override
    public List<HlsCusFinanceRedeem> redeemSave(IRequest iRequest, HlsCusFinanceRedeem dto) {
        if (dto.getFinanceRedeemId() == null || dto.getFinanceRedeemId() == 0) {
            HlsCusInvFinanceDetail financeDetail = new HlsCusInvFinanceDetail();
            BeanUtils.copyProperties(dto,financeDetail);
            financeDetail.setDocumentId(dto.getFinancePurchaseId());
            financeDetail.setDataClass("REDEEM");
            financeDetail.setFinancialDate(new Date());
            financeDetail.setDescription(dto.getRedemptionNote());
            detailService.insert(iRequest,financeDetail);
            dto.setPurchaseId(financeDetail.getInvFinanceDetailId());

            dto.setDocumentCategory("INV_FINANCE");
            dto.setDocumentType("INV_FINANCE");
            dto.setBusinessType("REDEEM");
            Map<String, String> params = new HashMap<>();
            dto.setRedemptionNumber(fndCodingRuleValuesService.getCodeRuleValue(iRequest, dto.getDocumentCategory(), dto.getDocumentType(), dto.getBusinessType(), params));
            dto.set__status("add");
            dto.setRedemptionStatus("NEW");
            dto.setInvalidBak("NORMAL");
            dto = self().insertSelective(iRequest, dto);

        } else {
            dto.set__status("update");
            dto = self().updateByPrimaryKeySelective(iRequest, dto);
        }
        List<HlsCusFinanceRedeem> list = new ArrayList<>();
        list.add(dto);
        return list;
    }

    @Override
    public List<HlsCusFinanceRedeem> queryAll(IRequest iRequest, HlsCusFinanceRedeem hlsCusFinanceRedeem, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return mapper.queryAll(hlsCusFinanceRedeem);
    }

    @Override
    public List<HlsCusFinanceRedeem> checkRedeemNewOrReturn(IRequest iRequest, HlsCusFinanceRedeem hlsCusFinanceRedeem, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return mapper.checkRedeemNewOrReturn(hlsCusFinanceRedeem);
    }

    /**
     * 赎回工作流提交
     *
     * @param iRequest
     * @param hlsCusFinanceRedeem
     * @return
     */
    @Override
    public List<HlsCusFinanceRedeem> redeemSubmitWfl(IRequest iRequest, HlsCusFinanceRedeem hlsCusFinanceRedeem) {
        List<HlsCusFinanceRedeem> list = self().redeemSave(iRequest, hlsCusFinanceRedeem);
        if (CollectionUtils.isNotEmpty(list)) {
            logger.debug("=============== start redeem activiti ==============");
            databaseLockProvider.lock(list.get(0));
            //获取申请人
            HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
            String employeeCode = employee.getEmployeeCode();
            iRequest.setEmployeeCode(employeeCode);
            //开始流程
            Map<String, Object> params = new HashMap<>();
            logger.debug("================ start redeem create activiti =================");
            params.put("workFlowType", "INV_REDEEM_WFL");
            activitiStartService.start(iRequest, list, params);
            list.get(0).setRedemptionStatus("APPROVING");
            list.get(0).set__status("update");
            for (HlsCusFinanceRedeem dt : list) {
                dt = self().updateByPrimaryKeySelective(iRequest, dt);
            }
            //消息发送
            Map<String, Object> paramsEvent = new HashMap<>();
            String userName = "";
            if (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode()).size() > 0) {
                userName = (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode())).get(0).getUserName();
            }
            String msg = userName + "创建了" + list.get(0).getPurchaseProductName() + "投资理财赎回新建" + list.get(0).getRedemptionNumber();
            paramsEvent.put("message", msg);
            paramsEvent.put("noticeTitle", "投资理财赎回新建");
            paramsEvent.put("noticeType", "NOTICE");
            paramsEvent.put("url", "");
            paramsEvent.put("level", 1L);
            sysEventService.eventSave(iRequest, list.get(0).getFinanceRedeemId(), list.get(0).getDocumentCategory(), list.get(0).getDocumentType(), "INV", "INV_FINANCE", "P2D", paramsEvent);

        }
        return list;
    }

    @Override
    public HlsCusFinanceRedeem redeemInvalidSubmit(IRequest iRequest, HlsCusFinanceRedeem hlsCusFinanceRedeem) {
        hlsCusFinanceRedeem.setRedemptionStatus("INVALID");
        HlsCusFinanceRedeem redeem = self().updateByPrimaryKeySelective(iRequest, hlsCusFinanceRedeem);
        return redeem;
    }

    @Override
    public HlsCusFinanceRedeem redeemChangesSubmit(IRequest iRequest, HlsCusFinanceRedeem hlsCusFinanceRedeem) {
        List<HlsCusFinanceRedeem> list = self().redeemSave(iRequest, hlsCusFinanceRedeem);
        if (CollectionUtils.isNotEmpty(list)) {
            hlsCusFinanceRedeem = list.get(0);
            //获取申请人
            HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
            String employeeCode = employee.getEmployeeCode();
            iRequest.setEmployeeCode(employeeCode);
            Map<String, Object> params = new HashMap<String, Object>();
            logger.debug("================ start redeem change activiti =================");
            params.put("workFlowType", "INV_REDEEM_CHANGE_WFL");
            activitiStartService.start(iRequest, list, params);
            hlsCusFinanceRedeem.setRedemptionStatus("PENDING");
            self().updateByPrimaryKeySelective(iRequest, hlsCusFinanceRedeem);

            //消息发送
            Map<String, Object> paramsEvent = new HashMap<String, Object>();
            String userName = "";
            if (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode()).size() > 0) {
                userName = (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode())).get(0).getUserName();
            }
            String msg = userName + "创建了" + hlsCusFinanceRedeem.getPurchaseProductName() + "投资理财赎回变更" + hlsCusFinanceRedeem.getRedemptionNumber();
            paramsEvent.put("message", msg);
            paramsEvent.put("noticeTitle", "投资理财赎回变更");
            paramsEvent.put("noticeType", "NOTICE");
            paramsEvent.put("url", "");
            paramsEvent.put("level", 1L);
            sysEventService.eventSave(iRequest, hlsCusFinanceRedeem.getFinanceRedeemId(), hlsCusFinanceRedeem.getDocumentCategory(), hlsCusFinanceRedeem.getDocumentType(), "INV", "INV_FINANCE", "P2D", paramsEvent);
        }

        return list.get(0);
    }

    @Override
    public List<HlsCusFinanceRedeem> queryRedeemDetail(IRequest iRequest, HlsCusFinanceRedeem hlsCusFinanceRedeem) {
        return mapper.queryRedeemDetail(hlsCusFinanceRedeem);
    }
}