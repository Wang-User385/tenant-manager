package com.hand.hls.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.bill.mapper.hlsBillRequestMapper;
import com.hand.hls.bill.service.IhlsBillRequestService;
import com.hand.hls.bill.dto.hlsBillRequest;
import com.hand.hls.bp.dto.HlsScoreCalculation;
import com.hand.hls.bp.mapper.HlsScoreCalculationMapper;
import com.hand.hls.bp.service.IHlsScoreCalculationService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Component
@Transactional(rollbackFor = Exception.class)
public class HlsScoreCalculationSubmitServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    HlsScoreCalculationMapper hlsScoreCalculationMapper;
    @Autowired
    private IHlsScoreCalculationService hlsScoreCalculationService;
    public HlsScoreCalculationSubmitServiceTask() {
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String scoreId = delegateExecution.getProcessInstanceBusinessKey();

        HlsScoreCalculation hlsScoreCalculation = hlsScoreCalculationMapper.selectByPrimaryKey(scoreId);

        if ("APPROVED".equalsIgnoreCase(result)) {
            hlsScoreCalculation.setGradeStatus("APPROVED");
            //（评级生效时间）审批通过时间
            hlsScoreCalculation.setFromDate(new Date());
            //(到期时间)审批通过时间往后一年
            Calendar curr = Calendar.getInstance();
            curr.set(Calendar.YEAR,curr.get(Calendar.YEAR)+1);
            Date date=curr.getTime();
            hlsScoreCalculation.setToDate(date);
        } else if ("REJECTED".equalsIgnoreCase(result)) {
            hlsScoreCalculation.setGradeStatus("APPROVED_RETURN");
        }
        hlsScoreCalculationService.updateByPrimaryKeySelective(requestCtx, hlsScoreCalculation);
    }

}
