package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.fp.dto.JcFundFilling;
import com.hand.hls.fp.mapper.JcFundFillingLnMapper;
import com.hand.hls.fp.mapper.JcFundFillingMapper;
import com.hand.hls.fp.mapper.JcFundPlanScheduleMapper;
import com.hand.hls.fp.service.JcFundFillingService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : qzk
 * @date : 2020/4/11
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusJcFundFillingSummaryServiceTask implements JavaDelegate, IActivitiBean {

    public static final String APPROVED = "APPROVED";
    public static final String REJECTED = "REJECTED";
    public static final String APPROVING = "APPROVING";
    @Autowired
    JcFundFillingService service;
    @Autowired
    private JcFundFillingMapper mapper;
    @Autowired
    private JcFundPlanScheduleMapper scheduleMapper;
    @Autowired
    private JcFundFillingService fillingService;
    @Autowired
    private JcFundFillingLnMapper lnMapper;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final String SUMMARY_FLAG = "Y";


    @Override
    public void execute(DelegateExecution delegateExecution) {
        String flag;
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String jcFundFilling = (String) delegateExecution.getVariable("jcFundFilling");
        JcFundFilling fundFilling = JSON.parseObject(jcFundFilling, JcFundFilling.class);
        JcFundFilling filling = mapper.selectByPrimaryKey(fundFilling);

        if (APPROVING.equalsIgnoreCase(filling.getSummaryStatus())) {
            if (APPROVED.equalsIgnoreCase(result)) {
                flag = APPROVED;
            } else {
                flag = REJECTED;
            }
            filling.setSummaryStatus(flag);
            service.updateByPrimaryKeySelective(requestCtx, filling);
            if (APPROVED.equalsIgnoreCase(flag) && !"YEAR".equalsIgnoreCase(filling.getFillType())) {
                //需要生成 fp400 执行数据 季度 月 周
                fillingService.initExecutInit(requestCtx, filling,SUMMARY_FLAG);
            }
        }
    }
}
