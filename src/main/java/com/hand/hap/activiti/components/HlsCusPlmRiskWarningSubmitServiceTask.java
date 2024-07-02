package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.plm.fc.mapper.HlsCusFiveClassificationContractMapper;
import com.hand.hls.plm.fc.mapper.HlsCusFiveClassificationMapper;
import com.hand.hls.plm.fc.service.HlsCusIFiveClassificationContractService;
import com.hand.hls.plm.fc.service.HlsCusIFiveClassificationService;
import com.hand.hls.rw.dto.HlsCusRiskWarning;
import com.hand.hls.rw.service.HlsCusIRiskWarningService;
import com.hand.hls.sys.mapper.HlsSystemNoticeMapper;
import com.hand.hls.user.service.LoginUserInfoService;
import hls.core.sys.event.service.SysEventService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description:风险预警新建工作流结束保存事件
 * @Author: wty
 * @Date: Created in 16:51 2018/06/01
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusPlmRiskWarningSubmitServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private HlsCusIRiskWarningService rwService;

    @Autowired
    private HlsCusIFiveClassificationService fiveClassificationService;

    @Autowired
    private HlsCusFiveClassificationMapper fiveClassificationMapper;

    @Autowired
    private SysEventService sysEventService;

    @Autowired
    private LoginUserInfoService loginUserInfoService;

    @Autowired
    private HlsCusFiveClassificationContractMapper fcContractMapper;

    @Autowired
    private HlsCusIFiveClassificationContractService fcContractService;

    @Autowired
    private HlsSystemNoticeMapper noticeMapper;
/*    @Autowired
    private HlsSystemEventService hlsSystemEventService;*/

    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest iRequest = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String employeeCode = (String) delegateExecution.getVariable("startUserName");
        String riskWarning = (String) delegateExecution.getVariable("riskWarning");
        HlsCusRiskWarning dto = JSON.parseObject(riskWarning, HlsCusRiskWarning.class);
        HlsCusRiskWarning newDto = new HlsCusRiskWarning();
        newDto.setRiskWarningId(dto.getRiskWarningId());

        if ("APPROVED".equalsIgnoreCase(result) ) {
            //flag = "APPROVED";
            newDto.setStatus("APPROVED");
            newDto.setSubmitStatus("Y");
        } else if ("APPROVED_RETURN".equalsIgnoreCase(result)) {
           // flag = "APPROVED_RETURN";
            newDto.setStatus("APPROVED_RETURN");
            newDto.setSubmitStatus("N");
        } else if ("REJECTED".equalsIgnoreCase(result)) {
            // flag = "APPROVED_RETURN";
            newDto.setStatus("REJECTED");
            newDto.setSubmitStatus("N");
        }
        rwService.updateByPrimaryKeySelective(iRequest, newDto);


    }


    /*
 *
 * @author Jeffery<曹金飞>
 * @date 2020年2月27日16点46分
 * @description 保存退回消息
 *
 * */
/*
    public void sendNotice(IRequest requestCtx, String result, Long userId, Long transferId) {
        HlsSystemEvent event = new HlsSystemEvent();
        event.setEventCode("RISK_WARNING");
        event.setSourceDocumentId(transferId);
        event.setEventUserId(userId);
        event.setSourceDocumentCategory("RISK_WARNING");
        event.setLastUpdatedBy(userId);
        event.setLastUpdateDate(new Date());
        event = hlsSystemEventService.insertSelective(requestCtx,event);
        HlsSystemNotice hlsSystemNotice = new HlsSystemNotice();
        hlsSystemNotice.setNotice_message(result);
        hlsSystemNotice.setNotice_title("资产管理审批退回");
        hlsSystemNotice.setNotice_datetime(new Date());
        hlsSystemNotice.setNotice_type("NOTICE");
        hlsSystemNotice.setNotice_level(Long.valueOf(2));
        hlsSystemNotice.setSource_module("RISK_WARNING");
        hlsSystemNotice.setSource_user_id(userId);
        hlsSystemNotice.setSource_event("RISK_WARNING");
        hlsSystemNotice.setLastUpdateDate(new Date());
        hlsSystemNotice.setSourceEventId(event.getEventId());
        noticeMapper.insertSelective(hlsSystemNotice);
        sysEventService.setNoticeCache(hlsSystemNotice);
    }
*/

}
