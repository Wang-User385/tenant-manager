package com.hand.hls.bill.job;

import com.hand.hap.core.IRequest;
import com.hand.hap.job.AbstractJob;

import com.hand.hap.mail.dto.Message;
import com.hand.hap.mail.dto.MessageReceiver;
import com.hand.hap.mail.service.IEmailService;
import com.hand.hap.mail.service.IMessageService;
import com.hand.hls.bill.dto.hlsBillRequest;
import com.hand.hls.bill.mapper.hlsBillRequestMapper;
import com.hand.hls.sys.dto.HlsSystemNotice;
import com.hand.hls.sys.mapper.HlsSystemNoticeMapper;

import hls.core.sys.event.service.SysEventService;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description:贷后检查消息提醒JOB
 * @Author: wty
 * @Date: Created in 10:48 2018/6/19
 */
@Transactional(rollbackFor = Exception.class)
@Getter
public class hlsCusBillRequestJob extends AbstractJob {
    private static final Logger logger = LoggerFactory.getLogger(hlsBillRequest.class);

    @Autowired
    private SysEventService sysEventService;

    @Autowired
    private HlsSystemNoticeMapper noticeMapper;

    @Autowired
    private hlsBillRequestMapper hlsBillRequestMapper;

    @Autowired
    private IMessageService messageService;
    @Autowired
    private IEmailService emailService;

    @Override
    public void safeExecute(JobExecutionContext jobExecutionContext) throws Exception {
        logger.info("================== WTY SEND PLI NOTICE JOB ======================");
        //根据companyId来进行分别处理
        hlsBillRequest hlsBillRequest=new hlsBillRequest();
        List<hlsBillRequest> hlsEmployeeLists = hlsBillRequestMapper.queryHlsBillRequestJob(hlsBillRequest);
        if(CollectionUtils.isNotEmpty(hlsEmployeeLists)){
            for(hlsBillRequest hlsEmployeeList : hlsEmployeeLists) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                String content =  new StringBuilder().append("您好").append(hlsEmployeeList.getBillTypeN()).append("-").append(hlsEmployeeList.getBillApplyNumber()).append("-").append("将于").append(df.format(hlsEmployeeList.getExpireDate())).append("到期，请及时进行兑付操作及相关系统操作，谢谢！").toString();

                Message message = new Message();
                message.setMessageType("EMAIL");
                message.setSendFlag("P");
                message.setSubject("汇票兑付解付提醒");
                message.setContent(content);
                message.setMessageFrom("JC_EMAIL");
                message.setMessageSource("BILL");

                List<MessageReceiver> receiverList = new ArrayList<>();

                MessageReceiver receiver = new MessageReceiver();
                receiver.setMessageAddress(hlsEmployeeList.getEmail());
                receiver.setMessageType("NORMAL");
                receiverList.add(receiver);
                IRequest iRequest = null;
                messageService.insertData(iRequest, message, receiverList, null);
                try {
                    emailService.sendSingleEmailMessageWithFile(message, null);
                } catch (Exception e) {
                    logger.error("email send error, {}", e);


                }
            }
        }

        //根据companyId来进行分别处理  30天
        hlsBillRequest hlsBillRequest1=new hlsBillRequest();
        List<hlsBillRequest> hlsEmployeeLists1 = hlsBillRequestMapper.queryHlsBillRequestJob30(hlsBillRequest1);
        if(CollectionUtils.isNotEmpty(hlsEmployeeLists1)){
            for(hlsBillRequest hlsEmployeeList : hlsEmployeeLists1) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                String content =  new StringBuilder().append("您好").append(hlsEmployeeList.getBillTypeN()).append("-").append(hlsEmployeeList.getBillApplyNumber()).append("-").append("将于").append(df.format(hlsEmployeeList.getExpireDate())).append("到期，请及时进行兑付操作及相关系统操作，谢谢！").toString();

                Message message = new Message();
                message.setMessageType("EMAIL");
                message.setSendFlag("P");
                message.setSubject("汇票兑付解付提醒");
                message.setContent(content);
                message.setMessageFrom("JC_EMAIL");
                message.setMessageSource("BILL");

                List<MessageReceiver> receiverList = new ArrayList<>();

                MessageReceiver receiver = new MessageReceiver();
                receiver.setMessageAddress(hlsEmployeeList.getEmail());
                receiver.setMessageType("NORMAL");
                receiverList.add(receiver);
                IRequest iRequest = null;
                messageService.insertData(iRequest, message, receiverList, null);
                try {
                    emailService.sendSingleEmailMessageWithFile(message, null);
                } catch (Exception e) {
                    logger.error("email send error, {}", e);
                }
            }
        }
    }


  

}
