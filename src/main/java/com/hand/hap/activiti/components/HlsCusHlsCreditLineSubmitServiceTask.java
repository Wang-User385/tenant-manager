package com.hand.hap.activiti.components;


import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.fct.dto.HlsCusHlsCreditLine;
import com.hand.hls.fct.service.HlsCreditLineService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusHlsCreditLineSubmitServiceTask implements JavaDelegate, IActivitiBean {

    @Autowired
    private HlsCreditLineService hlsCreditLineService;
    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;
    private Logger logger = LoggerFactory.getLogger(HlsCusHlsCreditLineSubmitServiceTask.class);

    public HlsCusHlsCreditLineSubmitServiceTask() {

    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        String flag = null;
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String meetingResult = (String) delegateExecution.getVariable("hasMeetingAgree");
        String hlsCusHlsCreditLinePrams = (String) delegateExecution.getVariable("hlsCusHlsCreditLine");
        HlsCusHlsCreditLine hlsCusHlsCreditLine = JSON.parseObject(hlsCusHlsCreditLinePrams, HlsCusHlsCreditLine.class);
        HlsCusHlsCreditLine resultHlsCusHlsCreditline = new HlsCusHlsCreditLine();
        //根据状态修改立项信息
        resultHlsCusHlsCreditline.setCreditLineId(hlsCusHlsCreditLine.getCreditLineId());
        resultHlsCusHlsCreditline = hlsCreditLineService.selectByPrimaryKey(requestCtx, resultHlsCusHlsCreditline);
        if ("false".equalsIgnoreCase(meetingResult)) {
            result = "REJECTED";
        }
        if ("APPROVED".equalsIgnoreCase(result)) {
            flag = "APPROVED";
            resultHlsCusHlsCreditline.setCreditLineStatus(flag);
            resultHlsCusHlsCreditline.setValidFrom(new Date());
            if (resultHlsCusHlsCreditline.getTerm() != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, resultHlsCusHlsCreditline.getTerm().intValue());
                resultHlsCusHlsCreditline.setValidTo(calendar.getTime());
            }
            hlsCreditLineService.updateByPrimaryKeySelective(requestCtx, resultHlsCusHlsCreditline);
            // 授信开始日、结束日、币种、额度、额度类型更新到HlsCusBpMsater
            //if (!resultHlsCusHlsCreditline.getCreditLineStatus().equals("APPROVED")) {

                HlsCusBpMaster hlsCusBpMaster = new HlsCusBpMaster();
                hlsCusBpMaster.setBpId(resultHlsCusHlsCreditline.getBpId());
                hlsCusBpMaster = hlsCusBpMasterMapper.selectByPrimaryKey(hlsCusBpMaster);
                hlsCusBpMaster.setValidFrom(new Date());
                hlsCusBpMaster.setCreditLineAmount(resultHlsCusHlsCreditline.getCreditLineAmt());
                hlsCusBpMaster.setCreditType(resultHlsCusHlsCreditline.getDocumentType());
                hlsCusBpMaster.setCreditCurrency(resultHlsCusHlsCreditline.getCurrencyCode());
                cal.setTime(date);
                cal.add(Calendar.MONTH, resultHlsCusHlsCreditline.getTerm().intValue());
                hlsCusBpMaster.setValidTo(cal.getTime());
                hlsCusBpMasterMapper.updateByPrimaryKey(hlsCusBpMaster);
            //}
        } else if ("APPROVED_RETURN".equalsIgnoreCase(result)) {
            flag = "APPROVED_RETURN";
            resultHlsCusHlsCreditline.setCreditLineStatus(flag);
            hlsCreditLineService.updateByPrimaryKeySelective(requestCtx, resultHlsCusHlsCreditline);
        } else if ("REJECTED".equalsIgnoreCase(result)) {
            flag = "REJECTED";
            resultHlsCusHlsCreditline.setCreditLineStatus(flag);
            hlsCreditLineService.updateByPrimaryKeySelective(requestCtx, resultHlsCusHlsCreditline);
        }

    }
}
