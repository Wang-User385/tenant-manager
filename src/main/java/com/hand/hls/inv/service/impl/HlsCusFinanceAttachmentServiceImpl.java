package com.hand.hls.inv.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.inv.dto.HlsCusFinanceAttachment;
import com.hand.hls.inv.dto.HlsCusFinancePurchase;
import com.hand.hls.inv.mapper.HlsCusFinanceAttachmentMapper;
import com.hand.hls.inv.service.HlsCusIFinanceAttachmentService;
import com.hand.hls.inv.service.HlsCusIFinancePurchaseService;
import com.hand.hls.user.service.LoginUserInfoService;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.sys.event.service.SysEventService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusFinanceAttachmentServiceImpl extends BaseServiceImpl<HlsCusFinanceAttachment> implements HlsCusIFinanceAttachmentService {

    @Autowired
    private HlsCusFinanceAttachmentMapper mapper;

    @Autowired
    private IActivitiStartService activitiStartService;

    @Autowired
    private HlsEmployeeMapper employeeMapper;

    @Autowired
    private HlsCusIFinancePurchaseService hlsCusFinancePurchaseService;

    @Autowired
    private LoginUserInfoService loginUserInfoService;

    @Autowired
    private SysEventService sysEventService;

    /**
     * @Description:附件补录工作流
     * @Author: Wty
     * @Date: Created om 14:00 2018/5/1
     */
    @Override
    public List<HlsCusFinanceAttachment> purchaseAttachmentSubmitWfl(IRequest iRequest, HlsCusFinanceAttachment hlsCusFinanceAttachment) {
        List<HlsCusFinanceAttachment> list = new ArrayList<>();
        list.add(hlsCusFinanceAttachment);
//        databaseLockProvider.lock(hlsCusFinanceAttachment);

        HlsCusFinancePurchase purchase = new HlsCusFinancePurchase();
        purchase.setFinancePurchaseId(hlsCusFinanceAttachment.getPurchaseId());
        purchase = hlsCusFinancePurchaseService.selectByPrimaryKey(iRequest, purchase);

        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);
        //开始流程
        Map<String, Object> params = new HashMap<>();
        params.put("workFlowType", "INV_ATTACHMENT_WFL");
        activitiStartService.start(iRequest, list, params);
        //查询新建补录的附件
        HlsCusFinanceAttachment attachment = new HlsCusFinanceAttachment();
        attachment.setPurchaseId(hlsCusFinanceAttachment.getPurchaseId());
        attachment.setMakeUp("RECORD");
        attachment.setStatus("NEW");
        List<HlsCusFinanceAttachment> queryList = mapper.queryAll(attachment);
        //更新申购的状态
        HlsCusFinancePurchase financePurchase = new HlsCusFinancePurchase();
        financePurchase.setPurchaseStatus("APPROVING");
        financePurchase.setFinancePurchaseId(hlsCusFinanceAttachment.getPurchaseId());
        hlsCusFinancePurchaseService.updateByPrimaryKeySelective(iRequest, financePurchase);

        if (CollectionUtils.isNotEmpty(queryList)) {
            for (int i = 0; i < queryList.size(); i++) {
                queryList.get(i).setStatus("APPROVING");
                queryList.get(i).set__status("update");
            }
        }
        //消息发送
        Map<String, Object> paramsEvent = new HashMap<String, Object>();
        String userName = "";
        if (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode()).size() > 0) {
            userName = (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode())).get(0).getUserName();
        }
        String msg = userName + "创建了" + purchase.getFinancialProductName() + "投资理财附件补录" + purchase.getPurchaseNumber();
        paramsEvent.put("message", msg);
        paramsEvent.put("noticeTitle", "投资理财附件补录");
        paramsEvent.put("noticeType", "NOTICE");
        paramsEvent.put("url", "");
        paramsEvent.put("level", 1L);
        sysEventService.eventSave(iRequest, purchase.getFinancePurchaseId(), purchase.getDocumentCategory(), purchase.getDocumentType(), "INV", "INV_FINANCE", "P2D", paramsEvent);


        return self().batchUpdate(iRequest, queryList);
    }

    @Override
    public List<HlsCusFinanceAttachment> invAttachmentDetailQuery(IRequest iRequest, HlsCusFinanceAttachment hlsCusFinanceAttachment, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return mapper.invAttachmentDetailQuery(hlsCusFinanceAttachment);
    }

}