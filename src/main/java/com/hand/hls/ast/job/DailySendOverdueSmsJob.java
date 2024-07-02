package com.hand.hls.ast.job;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import com.hand.hap.core.IRequest;
import com.hand.hap.mail.dto.MessageRule;
import com.hand.hap.mail.mapper.MessageRuleMapper;
import com.hand.hls.ast.service.ISendMessageService;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.rpt.job.AbstractJobWithIRequest;
import jodd.util.StringUtil;
import org.json.JSONObject;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author shigure 2022/11/14 15:23
 */
@Component
public class DailySendOverdueSmsJob extends AbstractJobWithIRequest {
    public static final String TEMPLATE_CODE = "RENTAL_OVERDUE";

    public static final String LOGGER_STRING = "===================================================================================";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MessageRuleMapper messageRuleMapper;

    @Autowired
    private HlsCusConContractCashflowMapper cashflowMapper;

    @Autowired
    private ISendMessageService messageService;

    @Override
    public void safeExecuteWithIRequest(JobExecutionContext var1, IRequest var2) throws Exception {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            List<MessageRule> messageRules = messageRuleMapper.queryTemplateRule(TEMPLATE_CODE);
            for (MessageRule messageRule : messageRules) {
                List<HlsCusConContractCashflow> cashflowList = cashflowMapper.querySmsOverdueCashflowList(messageRule.getRuleV2(), messageRule.getRuleV1());
                for (HlsCusConContractCashflow cashflow : cashflowList) {
                    if (StringUtil.isNotEmpty(cashflow.getPhone())) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("bpName", cashflow.getBpName());
                        jsonObject.put("contractNumber", cashflow.getContractNumber());
                        String dueDate = sdf.format(cashflow.getDueDate());
                        jsonObject.put("dueDate", dueDate);
                        jsonObject.put("times", cashflow.getTimes());
                        jsonObject.put("dueAmount", cashflow.getDueAmount());
                        messageService.sendMessage(TEMPLATE_CODE, jsonObject.toString(), Collections.singletonList(cashflow.getPhone()));
                    }
                }
            }
            logger.info(LOGGER_STRING);
            logger.info("发送短信完成");
            logger.info(LOGGER_STRING);
        } catch (Exception e) {
            logger.info(LOGGER_STRING);
            logger.info("发送短信失败:{}", e.getMessage());
            logger.info(LOGGER_STRING);
            this.setExecutionSummary(e.getClass().getName() + ":" + e.getMessage());
        }
    }
}
