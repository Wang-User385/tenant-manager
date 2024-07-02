//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hap.mail.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.cache.impl.MessageEmailConfigCache;
import com.hand.hap.mail.EmailStatusEnum;
import com.hand.hap.mail.EnvironmentEnum;
import com.hand.hap.mail.MailSender;
import com.hand.hap.mail.ReceiverTypeEnum;
import com.hand.hap.mail.dto.*;
import com.hand.hap.mail.mapper.MessageAttachmentMapper;
import com.hand.hap.mail.mapper.MessageEmailAccountMapper;
import com.hand.hap.mail.mapper.MessageEmailConfigMapper;
import com.hand.hap.mail.mapper.MessageEmailPropertyMapper;
import com.hand.hap.mail.mapper.MessageEmailWhiteListMapper;
import com.hand.hap.mail.mapper.MessageMapper;
import com.hand.hap.mail.mapper.MessageReceiverMapper;
import com.hand.hap.mail.mapper.MessageTransactionMapper;
import com.hand.hap.mail.service.IEmailService;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import com.hand.hls.prj.mapper.HlsCusPrjProjectAttachmentMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(
        rollbackFor = {Exception.class}
)
@Service
public class EmailServiceImpl implements IEmailService, BeanFactoryAware {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private static final int TWENTY = 20;
    private static final int FIFTY = 50;
    private static final String ATTACHMENT_FILE = "file";
    private static final String ATTACHMENT_SYSTEM = "system";
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private MessageAttachmentMapper attachmentMapper;
    @Autowired
    private MessageReceiverMapper receiverMapper;
    @Autowired
    private MessageTransactionMapper transactionMapper;
    @Autowired
    private MessageEmailAccountMapper emailAccountMapper;
    @Autowired
    private MessageEmailWhiteListMapper emailWhiteListMapper;
    @Autowired
    private MessageEmailConfigMapper emailConfigMapper;
    private BeanFactory beanFactory;
    @Autowired
    private MessageEmailPropertyMapper emailPropertyMapper;
    @Autowired
    private MessageEmailConfigCache configCache;
    @Autowired
    private HlsCusPrjProjectAttachmentMapper prjProjectAttachmentMapper;

    public EmailServiceImpl() {
    }

    public boolean sendMessages(Map<String, Object> params) throws Exception {
        Integer batch = (Integer)params.get("batch");
        boolean isVipQueue = (Boolean)params.get("isVipQueue");
        if (batch == null) {
            batch = 20;
        }

        if (batch == 0) {
            batch = 20;
        }

        PageHelper.startPage(1, batch);
        List userEmailToSend;
        if (isVipQueue) {
            userEmailToSend = this.messageMapper.selectVipEmailToSend();
        } else {
            userEmailToSend = this.messageMapper.selectEmailToSend();
        }

        return this.sendMessage(userEmailToSend, params);
    }

    public boolean reSendMessages(List<Message> messages, Map<String, Object> params) throws Exception {
        return this.sendMessage(messages, params);
    }

    public boolean sendMessageByReceiver(Message message, Map<String, Object> params) throws Exception {
        List<Message> messages = new ArrayList();
        messages.add(message);
        return this.sendMessage(messages, params);
    }

    @Transactional(
            propagation = Propagation.REQUIRES_NEW,
            rollbackFor = {Exception.class}
    )
    public boolean sendEmailMessage(List<Message> userEmailToSend, Map<String, Object> params) throws Exception {
        this.sendEmail(userEmailToSend, (List)null, params, "system");
        return true;
    }

    @Transactional(
            propagation = Propagation.REQUIRES_NEW,
            rollbackFor = {Exception.class}
    )
    public boolean sendEmailMessageWithFile(List<Message> userEmailToSend, List<File> fileList) throws Exception {
        this.sendEmail(userEmailToSend, fileList, (Map)null, "file");
        return true;
    }

    @Transactional(
            propagation = Propagation.REQUIRES_NEW,
            rollbackFor = {Exception.class}
    )
    public boolean sendSingleEmailMessage(Message currentMessage, Map<String, Object> params) throws Exception {
        return this.sendSingleEmail(currentMessage, params, (List)null, "system");
    }

    @Transactional(
            propagation = Propagation.REQUIRES_NEW,
            rollbackFor = {Exception.class}
    )
    public boolean sendSingleEmailMessageWithFile(Message message, List<File> fileList) throws Exception {
        return this.sendSingleEmail(message, (Map)null, fileList, "file");
    }

    public boolean sendMessage(List<Message> userEmailToSend, Map<String, Object> params) throws Exception {
        Map<String, MailSender> senders = new HashMap(1);
        int success = 0;
        this.messageSetPending(userEmailToSend);

        try {
            Iterator var21 = userEmailToSend.iterator();

            while(var21.hasNext()) {
                Message currentMessage = (Message)var21.next();
                if (StringUtils.isAnyBlank(new CharSequence[]{currentMessage.getSubject(), currentMessage.getContent()})) {
                    this.error(currentMessage, "subject or content is null");
                } else {
                    List<MessageAttachment> attachments = this.attachmentMapper.selectByMessageId(currentMessage.getMessageId());
                    String messageFrom = currentMessage.getMessageFrom();
                    MessageAddress address = MessageAddress.toAddressObject(messageFrom);
                    messageFrom = address.getAddress();
                    MailSender mailSender = (MailSender)senders.get(messageFrom);
                    if (mailSender == null) {
                        mailSender = (MailSender)this.beanFactory.getBean("mailSender");
                        MessageEmailAccount record = new MessageEmailAccount();
                        record.setAccountCode(messageFrom);
                        List<MessageEmailAccount> selectMessageEmailAccounts = this.emailAccountMapper.selectMessageEmailAccounts(record);
                        if (CollectionUtils.isEmpty(selectMessageEmailAccounts)) {
                            this.error(currentMessage, "email account is no more exists:" + messageFrom);
                            continue;
                        }

                        MessageEmailAccount mailAccount = (MessageEmailAccount)selectMessageEmailAccounts.get(0);
                        if (mailAccount == null) {
                            this.error(currentMessage, "email account is no more exists:" + messageFrom);
                            continue;
                        }

                        MessageEmailConfig config = (MessageEmailConfig)this.emailConfigMapper.selectByPrimaryKey(mailAccount.getConfigId());
                        mailSender.setHost(config.getHost());
                        mailSender.setPort(Integer.parseInt(config.getPort()));
                        if (config.getTryTimes() != null) {
                            mailSender.setTryTimes(config.getTryTimes().intValue());
                        }

                        mailSender.setMessageAccount(mailAccount.getUserName());
                        this.setAuthUserNameAndPassword(mailSender, mailAccount, config);
                        if ("Y".equalsIgnoreCase(config.getUseWhiteList())) {
                            List<MessageEmailWhiteList> whitelist = this.emailWhiteListMapper.selectByConfigId(config.getConfigId());
                            List<String> stringList = new ArrayList();
                            Iterator var17 = whitelist.iterator();

                            while(var17.hasNext()) {
                                MessageEmailWhiteList current = (MessageEmailWhiteList)var17.next();
                                stringList.add(current.getAddress());
                            }

                            mailSender.setWhiteList(stringList);
                        }

                        senders.put(messageFrom, mailSender);
                    }

                    MimeMessage mimeMessage = this.generateMimeMessage(currentMessage, (List)null, attachments, mailSender);
                    if (ArrayUtils.isEmpty(mimeMessage.getAllRecipients())) {
                        this.error(currentMessage, "The recipient is empty!");
                    } else {
                        MessageTransaction obj = this.generateMessageTransaction(currentMessage);
                        int i = 0;

                        while(i < mailSender.getTryTimes()) {
                            try {
                                mailSender.send(mimeMessage);
                                ++success;
                                if (this.log.isDebugEnabled()) {
                                    this.log.debug("Send mail success, {}.", i);
                                }

                                obj.setTransactionStatus(EmailStatusEnum.SUCCESS.getCode());
                                currentMessage.setSendFlag("Y");
                                this.trySaveTransaction(currentMessage, obj);
                                break;
                            } catch (Exception var19) {
                                if (i == mailSender.getTryTimes() - 1) {
                                    this.saveFailMessageTransaction(currentMessage, obj, var19);
                                } else {
                                    Thread.sleep(50L);
                                }

                                ++i;
                            }
                        }
                    }
                }
            }
        } catch (Exception var20) {
            Exception e = var20;
            params.put("ERROR_MESSAGE", var20.getMessage());
            Iterator var6 = userEmailToSend.iterator();

            while(var6.hasNext()) {
                Message message = (Message)var6.next();
                this.error(message, e.getMessage());
            }

            if (this.log.isErrorEnabled()) {
                this.log.error(e.getMessage(), e);
            }

            throw e;
        }

        this.prepareSummary(userEmailToSend, params, success);
        return true;
    }

    @Transactional(
            propagation = Propagation.REQUIRES_NEW,
            noRollbackFor = {Exception.class}
    )
    public void saveTransaction(Message message, MessageTransaction obj) {
        if (message != null) {
            this.messageMapper.updateByPrimaryKeySelective(message);
        }

        if (obj != null) {
            this.transactionMapper.insertSelective(obj);
        }

    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    private void error(Message message, String msg) {
        MessageTransaction obj = this.generateMessageTransaction(message);
        obj.setTransactionStatus(EmailStatusEnum.ERROR.getCode());
        obj.setTransactionMessage(msg);
        message.setSendFlag("F");
        this.trySaveTransaction(message, obj);
    }

    private void messageSetPending(List<Message> messages) {
        Iterator var2 = messages.iterator();

        while(var2.hasNext()) {
            Message current = (Message)var2.next();
            current.setSendFlag("P");
            this.trySaveTransaction(current, (MessageTransaction)null);
        }

    }

    private void setAuthUserNameAndPassword(MailSender mailSender, MessageEmailAccount messageEmailAccount, MessageEmailConfig config) {
        if (StringUtils.isEmpty(messageEmailAccount.getPassWord())) {
            mailSender.setUsername(config.getUserName());
            mailSender.setPassword(config.getPassword());
        } else {
            mailSender.setUsername(messageEmailAccount.getUserName());
            mailSender.setPassword(messageEmailAccount.getPassWord());
        }

    }

    private boolean sendSingleEmail(Message message, Map<String, Object> params, List<File> fileList, String type) throws MessagingException, UnsupportedEncodingException {
        if (StringUtils.isAnyBlank(new CharSequence[]{message.getSubject(), message.getContent()})) {
            this.error(message, "subject or content is null");
            return false;
        } else {
            MailSender mailSender = this.generateMailSender(message);
            mailSender.setEnvironment("");
            MimeMessage mimeMessage;
            try {
                mimeMessage = this.generateMimeMessage(message, fileList, this.getMessageAttachments(message, type), mailSender);
                if (ArrayUtils.isEmpty(mimeMessage.getAllRecipients())) {
                    this.error(message, "The recipient is empty!");
                    return false;
                }
            } catch (Exception var10) {
                if (params != null) {
                    params.put("ERROR_MESSAGE", var10.getMessage());
                }

                this.error(message, var10.getMessage());
                if (this.log.isErrorEnabled()) {
                    this.log.error(var10.getMessage(), var10);
                }

                throw var10;
            }

            MessageTransaction obj = this.generateMessageTransaction(message);

            try {
                mailSender.send(mimeMessage);
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Send mail success.");
                }

                obj.setTransactionStatus(EmailStatusEnum.SUCCESS.getCode());
                message.setSendFlag("Y");
                this.trySaveTransaction(message, obj);
            } catch (Exception var9) {
                this.saveFailMessageTransaction(message, obj, var9);
                throw var9;
            }

            if (params != null) {
                List<Message> messageList = new ArrayList();
                messageList.add(message);
                this.prepareSummary(messageList, params, 1);
            }

            return true;
        }
    }

    private void sendEmail(List<Message> userEmailToSend, List<File> fileList, Map<String, Object> params, String type) throws MessagingException, UnsupportedEncodingException {
        int success = 0;
        HashMap senders = new HashMap(1);

        try {
            Iterator var17 = userEmailToSend.iterator();

            while(var17.hasNext()) {
                Message currentMessage = (Message)var17.next();
                if (StringUtils.isAnyBlank(new CharSequence[]{currentMessage.getSubject(), currentMessage.getContent()})) {
                    this.error(currentMessage, "subject or content is null");
                } else {
                    String messageFrom = currentMessage.getMessageFrom();
                    MessageAddress address = MessageAddress.toAddressObject(messageFrom);
                    messageFrom = address.getAddress();
                    MailSender mailSender = (MailSender)senders.get(messageFrom);
                    if (mailSender == null) {
                        mailSender = this.generateMailSender(currentMessage);
                        senders.put(messageFrom, mailSender);
                    }

                    MimeMessage mimeMessage = this.generateMimeMessage(currentMessage, fileList, this.getMessageAttachments(currentMessage, type), mailSender);
                    if (ArrayUtils.isEmpty(mimeMessage.getAllRecipients())) {
                        this.error(currentMessage, "The recipient is empty!");
                    } else {
                        MessageTransaction obj = this.generateMessageTransaction(currentMessage);

                        try {
                            mailSender.send(mimeMessage);
                            ++success;
                            if (this.log.isDebugEnabled()) {
                                this.log.debug("Send mail success.");
                            }

                            obj.setTransactionStatus(EmailStatusEnum.SUCCESS.getCode());
                            currentMessage.setSendFlag("Y");
                            this.trySaveTransaction(currentMessage, obj);
                        } catch (Exception var15) {
                            this.saveFailMessageTransaction(currentMessage, obj, var15);
                        }
                    }
                }
            }
        } catch (Exception var16) {
            Exception e = var16;
            if (params != null) {
                params.put("ERROR_MESSAGE", var16.getMessage());
            }

            Iterator var8 = userEmailToSend.iterator();

            while(var8.hasNext()) {
                Message message = (Message)var8.next();
                this.error(message, e.getMessage());
            }

            if (this.log.isErrorEnabled()) {
                this.log.error(e.getMessage(), e);
            }

            try {
                throw e;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (params != null) {
            this.prepareSummary(userEmailToSend, params, success);
        }

    }

    private MailSender generateMailSender(Message message) {
        MailSender mailSender = (MailSender)this.beanFactory.getBean("mailSender");
        MessageEmailAccount messageEmailAccount = this.emailAccountMapper.selectByAccountCode(message.getMessageFrom());
        MessageEmailConfig config = this.configCache.getValue(messageEmailAccount.getConfigId().toString());
        if (null == config) {
            config = (MessageEmailConfig)this.emailConfigMapper.selectByPrimaryKey(messageEmailAccount.getConfigId());
            config.setWhiteLists(this.emailWhiteListMapper.selectByConfigId(config.getConfigId()));
            config.setPropertyLists(this.emailPropertyMapper.selectByConfigId(config.getConfigId()));
        }

        mailSender.setHost(config.getHost());
        mailSender.setPort(Integer.parseInt(config.getPort()));
        mailSender.setMessageAccount(messageEmailAccount.getUserName());
        this.setAuthUserNameAndPassword(mailSender, messageEmailAccount, config);
        List propertyLists;
        Iterator var7;
        if ("Y".equalsIgnoreCase(config.getUseWhiteList())) {
            propertyLists = config.getWhiteLists();
            List<String> stringList = new ArrayList();
            var7 = propertyLists.iterator();

            while(var7.hasNext()) {
                MessageEmailWhiteList current = (MessageEmailWhiteList)var7.next();
                stringList.add(current.getAddress());
            }

            mailSender.setWhiteList(stringList);
        }

        if (config.getPropertyLists() != null) {
            propertyLists = config.getPropertyLists();
            Properties props = new Properties();
            var7 = propertyLists.iterator();

            while(var7.hasNext()) {
                MessageEmailProperty emailProperty = (MessageEmailProperty)var7.next();
                props.put(emailProperty.getPropertyName(), emailProperty.getPropertyCode());
            }

            mailSender.setJavaMailProperties(props);
        }

        return mailSender;
    }

    private MimeMessage generateMimeMessage(Message currentMessage, List<File> fileList, List<MessageAttachment> attachments, MailSender mailSender) throws MessagingException, UnsupportedEncodingException {
        List<MessageReceiver> receivers = this.receiverMapper.selectByMessageId(currentMessage.getMessageId());
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setFrom(mailSender.getMessageAccount());
        helper.setSubject(this.setEmailSubject(mailSender, currentMessage.getSubject()));
        if (CollectionUtils.isNotEmpty(fileList)) {
            BodyPart messageBodyPart = new MimeBodyPart();
//            messageBodyPart.setText(currentMessage.getContent());
            Multipart multipart = new MimeMultipart();
//            multipart.addBodyPart(messageBodyPart);


            MimeBodyPart htmlPart  = new MimeBodyPart();
            htmlPart.setContent(currentMessage.getContent(),"text/html;charset=utf-8");//html代码部分
            multipart.addBodyPart(htmlPart);

            Iterator var11 = fileList.iterator();

            while(var11.hasNext()) {
                File file = (File)var11.next();
                messageBodyPart = new MimeBodyPart();
                FileDataSource fileDataSource = new FileDataSource(file);
                messageBodyPart.setDataHandler(new DataHandler(fileDataSource));
                messageBodyPart.setFileName(MimeUtility.encodeText(file.getName()));
                String fileName = prjProjectAttachmentMapper.queryNameByPath(file.getPath());
                if(StringUtils.isEmpty(fileName)){
                    fileName = "未知文件.docx";
                }
                messageBodyPart.setFileName(MimeUtility.encodeText(fileName));
                multipart.addBodyPart(messageBodyPart);
            }

            mimeMessage.setContent(multipart);
        } else {
            helper.setText(currentMessage.getContent(), true);
        }

        Iterator var13 = receivers.iterator();

        while(true) {
            MessageReceiver receiver;
            do {
                do {
                    if (!var13.hasNext()) {
                        return mimeMessage;
                    }

                    receiver = (MessageReceiver)var13.next();
                } while(receiver.getMessageAddress() == null);
            } while(CollectionUtils.isNotEmpty(mailSender.getWhiteList()) && !mailSender.getWhiteList().contains(receiver.getMessageAddress()));

            if (ReceiverTypeEnum.NORMAL.getCode().equalsIgnoreCase(receiver.getMessageType())) {
                helper.addTo(receiver.getMessageAddress());
            } else if (ReceiverTypeEnum.CC.getCode().equalsIgnoreCase(receiver.getMessageType())) {
                helper.addCc(receiver.getMessageAddress());
            } else if (ReceiverTypeEnum.BCC.getCode().equalsIgnoreCase(receiver.getMessageType())) {
                helper.addBcc(receiver.getMessageAddress());
            }
        }
    }

    private List<MessageAttachment> getMessageAttachments(Message message, String type) {
        List<MessageAttachment> attachments = null;
        if ("system".equals(type)) {
            attachments = this.attachmentMapper.selectByMessageId(message.getMessageId());
        }

        return attachments;
    }

    private MessageTransaction generateMessageTransaction(Message message) {
        MessageTransaction obj = new MessageTransaction();
        Date time = new Date();
        obj.setCreatedBy(-1L);
        obj.setLastUpdatedBy(-1L);
        obj.setCreationDate(time);
        obj.setLastUpdateDate(time);
        obj.setMessageId(message.getMessageId());
        obj.setObjectVersionNumber(0L);
        return obj;
    }

    private void saveFailMessageTransaction(Message currentMessage, MessageTransaction obj, Exception e) {
        obj.setTransactionMessage(this.getExceptionStack(e));
        obj.setTransactionStatus(EmailStatusEnum.ERROR.getCode());
        currentMessage.setSendFlag("F");
        this.trySaveTransaction(currentMessage, obj);
        if (this.log.isErrorEnabled()) {
            this.log.error("Send mail failed.", e);
        }

    }

    private void prepareSummary(List<Message> messages, Map<String, Object> param, int success) {
        StringBuilder sb = new StringBuilder();
        if (messages.isEmpty()) {
            sb.append("No Email To Send.");
        } else {
            sb.append("Send ").append(messages.size()).append(" Emails. ");
            sb.append("  Success : ").append(success);
            Object object = param.get("ERROR_MESSAGE");
            if (object != null) {
                sb.append("  Error :  ").append(object);
            }
        }

        param.put("summary", sb.toString());
    }

    private void trySaveTransaction(Message message, MessageTransaction obj) {
        int dataBaseTryTime = 3;
        int i = 0;

        while(i < dataBaseTryTime) {
            try {
                ((IEmailService)this.self()).saveTransaction(message, obj);
                return;
            } catch (Exception var6) {
                if (i == dataBaseTryTime - 1 && this.log.isErrorEnabled()) {
                    this.log.error("save transaction failed.", var6);
                }

                ++i;
            }
        }

    }

    private String setEmailSubject(MailSender mailSender, String subject) {
        return !EnvironmentEnum.SIT.getCode().equals(mailSender.getEnvironment()) && !EnvironmentEnum.UAT.getCode().equals(mailSender.getEnvironment()) ? subject : "[" + mailSender.getEnvironment() + "] " + subject;
    }

    private String getExceptionStack(Throwable th) {
        return ExceptionUtils.getStackTrace(th);
    }
}
