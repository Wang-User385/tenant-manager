//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hap.mail.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.cache.impl.MessageTemplateCache;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.EmailException;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.mail.MessageTypeEnum;
import com.hand.hap.mail.PriorityLevelEnum;
import com.hand.hap.mail.SendTypeEnum;
import com.hand.hap.mail.dto.*;
import com.hand.hap.mail.mapper.MessageAttachmentMapper;
import com.hand.hap.mail.mapper.MessageEmailAccountMapper;
import com.hand.hap.mail.mapper.MessageMapper;
import com.hand.hap.mail.mapper.MessageReceiverMapper;
import com.hand.hap.mail.mapper.MessageTemplateMapper;
import com.hand.hap.mail.service.IEmailService;
import com.hand.hap.mail.service.IMessageService;
import com.hand.hap.message.IMessagePublisher;
import com.hand.hap.system.dto.BaseDTO;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.mapper.FndAttachmentMapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.File;
import java.io.StringWriter;
import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessageServiceImpl implements IMessageService {
    private static final String MSG_NO_MESSAGE_RECEIVER = "msg.warning.system.no_message_receiver";
    private static final String MSG_NO_MESSAGE_TEMPLATE = "msg.warning.system.no_message_template";
    private static final String MSG_MESSAGE_TYPE_EMPTY = "msg.warning.system.email_message_type_empty";
    private static final String MSG_MESSAGE_PRIORITY_EMPTY = "msg.warning.system.email_message_priority_empty";
    private static final String MSG_MESSAGE_SEND_TYPE_EMPTY = "msg.warning.system.message_send_type_empty";
    private static final String MSG_MESSAGE_ACCOUNT_EMPTY = "msg.warning.system.message_account_empty";
    private static final String MSG_MESSAGE_CONFIG_NONEXISTENT = "msg.warning.system.message_config_nonexistent";
    private static final String MSG_MESSAGE_CONFIG_DISABLE = "msg.warning.system.message_config_disable";
    private static final String PRIORITY_VIP = "vip";
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private MessageAttachmentMapper attachmentMapper;
    @Autowired
    private MessageReceiverMapper receiverMapper;
    @Autowired
    private MessageTemplateMapper templateMapper;
    @Autowired
    private MessageEmailAccountMapper emailAccountMapper;
    @Autowired
    private IMessagePublisher messagePublisher;
    @Autowired
    private IEmailService emailService;
    @Autowired
    private MessageTemplateCache templateCache;
    @Autowired
    private FndAttachmentMapper fndAttachmentMapper;

    public MessageServiceImpl() {
    }

    public Message sendMessage(IRequest iRequest, String templateCode, Map<String, Object> templateData, List<MessageReceiver> receivers, List<Long> attachmentIds) throws Exception {
        if (CollectionUtils.isEmpty(receivers)) {
            throw new EmailException("msg.warning.system.no_message_receiver");
        } else {
            MessageTemplate template = null;
            template = this.templateCache.getValue(templateCode);
            if (null == template) {
                template = this.templateMapper.selectByCode(templateCode);
            }

            System.out.println("template:" + template);
            this.validateMessageTemplate(template);
            MessageEmailAccount account = (MessageEmailAccount)this.emailAccountMapper.selectByPrimaryKey(template.getAccountId());
            String accountCode = null;
            if (account != null) {
                accountCode = account.getAccountCode();
            }

            String subject = this.translateData(template.getSubject(), templateData);
            String content = this.translateData(template.getContent(), templateData);
            String messageType = template.getTemplateType();
            String sendType = template.getSendType();
            this.sendMessage(iRequest, accountCode, subject, content, messageType, sendType, templateCode, receivers, attachmentIds);
            return null;
        }
    }

    public Message sendMessageWithFile(IRequest iRequest, String templateCode, Map<String, Object> templateData, List<MessageReceiver> receivers, List<Long> attachmentIds) throws Exception {
        if (CollectionUtils.isEmpty(receivers)) {
            throw new EmailException("msg.warning.system.no_message_receiver");
        } else {
            MessageTemplate template = null;
            template = this.templateCache.getValue(templateCode);
            if (null == template) {
                template = this.templateMapper.selectByCode(templateCode);
            }

            System.out.println("template:" + template);
            this.validateMessageTemplate(template);
            MessageEmailAccount account = (MessageEmailAccount)this.emailAccountMapper.selectByPrimaryKey(template.getAccountId());
            String accountCode = null;
            if (account != null) {
                accountCode = account.getAccountCode();
            }

            String subject = this.translateData(template.getSubject(), templateData);
            String content = this.translateData(template.getContent(), templateData);
            String messageType = template.getTemplateType();
            String sendType = template.getSendType();
            this.sendMessageWithFile(iRequest, accountCode, subject, content, messageType, sendType, templateCode, receivers, attachmentIds);
            return null;
        }
    }

    public Message sendMessage(IRequest iRequest, String accountCode, String subject, String content, String messageType, String sendType, String messageSource, List<MessageReceiver> receivers, List<Long> attachmentIds) throws Exception {
        this.validateMessageCustom(receivers, accountCode, messageType, sendType);
        if (MessageTypeEnum.EMAIL.getCode().equalsIgnoreCase(messageType)) {
            MessageEmailAccount emailAccount = new MessageEmailAccount();
            emailAccount.setAccountCode(accountCode);
            String messageEmailConfig = this.emailAccountMapper.selectMessageEmailConfig(emailAccount);
            if (StringUtils.isEmpty(messageEmailConfig)) {
                throw new EmailException("msg.warning.system.message_config_nonexistent");
            }

            if ("N".equals(messageEmailConfig)) {
                throw new EmailException("msg.warning.system.message_config_disable");
            }

            MessageEmailAccount account = this.emailAccountMapper.selectByAccountCode(accountCode);
            Message message = new Message();
            message.setMessageType(messageType);
            if(sendType.equals(SendTypeEnum.JOB.getCode())){
                if (SendTypeEnum.JOB.getCode().equalsIgnoreCase(sendType)) {
                    message.setSendFlag("P");
                }

                message.setSubject(subject);
                message.setContent(content);
                message.setMessageFrom(account.getAccountCode());
                message.setMessageSource(messageSource);
                ((IMessageService)this.self()).insertData(iRequest, message, receivers, attachmentIds);
                if (SendTypeEnum.JOB.getCode().equalsIgnoreCase(sendType)) {
                    this.emailService.sendSingleEmailMessage(message, (Map)null);
                }
            }else{
                if (SendTypeEnum.DIRECT.getCode().equalsIgnoreCase(sendType)) {
                    message.setSendFlag("P");
                }

                message.setSubject(subject);
                message.setContent(content);
                message.setMessageFrom(account.getAccountCode());
                message.setMessageSource(messageSource);
                ((IMessageService)this.self()).insertData(iRequest, message, receivers, attachmentIds);
                if (SendTypeEnum.DIRECT.getCode().equalsIgnoreCase(sendType)) {
                    this.emailService.sendSingleEmailMessage(message, (Map)null);
                }
            }

        }

        return null;
    }

    public Message sendMessageWithFile(IRequest iRequest, String accountCode, String subject, String content, String messageType, String sendType, String messageSource, List<MessageReceiver> receivers, List<Long> attachmentIds) throws Exception {
        this.validateMessageCustom(receivers, accountCode, messageType, sendType);
        if (MessageTypeEnum.EMAIL.getCode().equalsIgnoreCase(messageType)) {
            MessageEmailAccount emailAccount = new MessageEmailAccount();
            emailAccount.setAccountCode(accountCode);
            String messageEmailConfig = this.emailAccountMapper.selectMessageEmailConfig(emailAccount);
            if (StringUtils.isEmpty(messageEmailConfig)) {
                throw new EmailException("msg.warning.system.message_config_nonexistent");
            }

            if ("N".equals(messageEmailConfig)) {
                throw new EmailException("msg.warning.system.message_config_disable");
            }

            MessageEmailAccount account = this.emailAccountMapper.selectByAccountCode(accountCode);
            Message message = new Message();
            message.setMessageType(messageType);
            if(sendType.equals(SendTypeEnum.JOB.getCode())){
                if (SendTypeEnum.JOB.getCode().equalsIgnoreCase(sendType)) {
                    message.setSendFlag("P");
                }

                message.setSubject(subject);
                message.setContent(content);
                message.setMessageFrom(account.getAccountCode());
                message.setMessageSource(messageSource);
                ((IMessageService)this.self()).insertData(iRequest, message, receivers, attachmentIds);
                List<File> files=new ArrayList<>();
                for (Long attachmentId : attachmentIds) {
                    FndAttachment fndAttachment = fndAttachmentMapper.selectByPrimaryKey(attachmentId);
                    File file=new File(fndAttachment.getFilePath());
                    files.add(file);
                }
                if (SendTypeEnum.JOB.getCode().equalsIgnoreCase(sendType)) {
                    boolean flag = this.emailService.sendSingleEmailMessageWithFile(message, files);
                    if(flag){
                        System.out.println("发送成功");
                    }else {
                        System.out.println("发送失败");
                    }
                }
            }else{
                if (SendTypeEnum.DIRECT.getCode().equalsIgnoreCase(sendType)) {
                    message.setSendFlag("P");
                }

                message.setSubject(subject);
                message.setContent(content);
                message.setMessageFrom(account.getAccountCode());
                message.setMessageSource(messageSource);
                ((IMessageService)this.self()).insertData(iRequest, message, receivers, attachmentIds);
                List<File> files=new ArrayList<>();
                for (Long attachmentId : attachmentIds) {
                    FndAttachment fndAttachment = fndAttachmentMapper.selectByPrimaryKey(attachmentId);
                    File file=new File(fndAttachment.getFilePath());
                    files.add(file);
                }
                if (SendTypeEnum.DIRECT.getCode().equalsIgnoreCase(sendType)) {
                    boolean flag = this.emailService.sendSingleEmailMessageWithFile(message, files);
                    if(flag){
                        System.out.println("发送成功");
                    }else {
                        System.out.println("发送失败");
                    }
                }
            }

        }

        return null;
    }

    @Transactional(
            propagation = Propagation.REQUIRES_NEW,
            noRollbackFor = {Exception.class}
    )
    public void insertData(IRequest iRequest, Message message, List<MessageReceiver> receivers, List<Long> attachmentIds) {
        this.initStd(message, iRequest);
        this.messageMapper.insertSelective(message);
        Iterator var5;
        if (CollectionUtils.isNotEmpty(attachmentIds)) {
            var5 = attachmentIds.iterator();

            while(var5.hasNext()) {
                Long current = (Long)var5.next();
                MessageAttachment attachment = new MessageAttachment();
                attachment.setFileId(current);
                attachment.setMessageId(message.getMessageId());
                this.initStd(attachment, iRequest);
                this.attachmentMapper.insertSelective(attachment);
            }
        }

        var5 = receivers.iterator();

        while(var5.hasNext()) {
            MessageReceiver current = (MessageReceiver)var5.next();
            current.setMessageId(message.getMessageId());
            this.initStd(current, iRequest);
            this.receiverMapper.insertSelective(current);
        }

    }

    public List<Message> selectMessagesBySubject(IRequest requestContext, Message message, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return this.messageMapper.selectMessagesBySubject(message);
    }

    public List<Message> selectMessages(IRequest requestContext, Message message, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return this.messageMapper.selectMessages(message);
    }

    public Message selectMessageContent(IRequest requestContext, Message message) {
        return this.messageMapper.selectMessageContent(message);
    }

    public List<MessageReceiver> selectMessageAddressesByMessageId(IRequest requestContext, MessageReceiver messageReceiver, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return this.receiverMapper.selectMessageAddressesByMessageId(messageReceiver);
    }

    public Message addEmailMessage(Long userId, String accountCode, String templateCode, Map<String, Object> data, List<Long> attachmentIds, List<MessageReceiver> receivers) throws Exception {
        MessageTemplate template = this.templateMapper.selectByCode(templateCode);
        if (template == null) {
            throw new EmailException("msg.warning.system.no_message_template");
        } else if (template.getPriorityLevel() == null) {
            throw new EmailException("msg.warning.system.email_message_priority_empty");
        } else {
            Message message = ((IMessageService)this.self()).sendEmailMessage(userId, (Long)null, templateCode, accountCode, data, receivers, attachmentIds);
            if ("vip".equals(template.getPriorityLevel())) {
                this.messagePublisher.message("hap:queue:email:vip", message);
            } else {
                this.messagePublisher.message("hap:queue:email:normal", message);
            }

            return message;
        }
    }

    public Message addEmailMessage(Long userId, String accountCode, String subject, String content, PriorityLevelEnum priority, List<Long> attachmentIds, List<MessageReceiver> receivers) throws Exception {
        Message message = ((IMessageService)this.self()).sendEmailMessage(userId, (Long)null, accountCode, subject, content, priority, receivers, attachmentIds);
        if ("vip".equals(priority.getCode())) {
            this.messagePublisher.message("hap:queue:email:vip", message);
        } else {
            this.messagePublisher.message("hap:queue:email:normal", message);
        }

        return message;
    }

    @Transactional(
            rollbackFor = {Exception.class}
    )
    public Message sendEmailMessage(Long sender, Long marketId, String templateCode, String emailAccountCode, Map<String, Object> data, List<MessageReceiver> receivers, List<Long> attachmentIds) throws Exception {
        MessageTemplate template = this.check(templateCode, emailAccountCode, receivers);
        String subject = this.translateData(template.getSubject(), data);
        String content = this.translateData(template.getContent(), data);
        String priorityS = template.getPriorityLevel();
        PriorityLevelEnum priority = PriorityLevelEnum.valueOf(priorityS);
        Message sendSmsMessage = this.sendEmailMessage(sender, marketId, emailAccountCode, subject, content, priority, receivers, attachmentIds);
        return sendSmsMessage;
    }

    public static String from(Long marketId, String accountCode) {
        StringBuilder sb = new StringBuilder();
        sb.append("I");
        sb.append(marketId);
        sb.append("://");
        sb.append(accountCode);
        return sb.toString();
    }

    @Transactional(
            rollbackFor = {Exception.class}
    )
    public Message sendEmailMessage(Long sender, Long marketId, String emailAccountCode, String subject, String content, PriorityLevelEnum priority, List<MessageReceiver> receivers, List<Long> attachmentIds) throws Exception {
        this.check(emailAccountCode, priority, receivers);
        MessageEmailAccount emailAccount = new MessageEmailAccount();
        emailAccount.setAccountCode(emailAccountCode);
        String messageEmailConfig = this.emailAccountMapper.selectMessageEmailConfig(emailAccount);
        if (StringUtils.isEmpty(messageEmailConfig)) {
            throw new EmailException("msg.warning.system.message_config_nonexistent");
        } else if ("N".equals(messageEmailConfig)) {
            throw new EmailException("msg.warning.system.message_config_disable");
        } else {
            MessageEmailAccount account = this.emailAccountMapper.selectByAccountCode(emailAccountCode);
            Message message = new Message();
            message.setMessageType(MessageTypeEnum.EMAIL.getCode());
            message.setPriorityLevel(priority.getCode());
            message.setSubject(subject);
            message.setContent(content);
            message.setSendFlag("N");
            if (null == marketId) {
                message.setMessageFrom(account.getAccountCode());
            } else {
                message.setMessageFrom(from(marketId, account.getAccountCode()));
            }

            IRequest iRequest = RequestHelper.newEmptyRequest();
            iRequest.setUserId(sender);
            this.initStd(message, iRequest);
            this.messageMapper.insertSelective(message);
            Iterator var14;
            if (attachmentIds != null && attachmentIds.size() > 0) {
                var14 = attachmentIds.iterator();

                while(var14.hasNext()) {
                    Long current = (Long)var14.next();
                    MessageAttachment attachment = new MessageAttachment();
                    attachment.setMessageId(message.getMessageId());
                    attachment.setFileId(current);
                    this.initStd(attachment, iRequest);
                    this.attachmentMapper.insertSelective(attachment);
                }
            }

            var14 = receivers.iterator();

            while(var14.hasNext()) {
                MessageReceiver current = (MessageReceiver)var14.next();
                current.setMessageId(message.getMessageId());
                this.initStd(current, iRequest);
                this.receiverMapper.insertSelective(current);
            }

            return message;
        }
    }

    private void validateMessageTemplate(MessageTemplate template) throws EmailException {
        if (null == template) {
            throw new EmailException("msg.warning.system.no_message_template");
        } else if (StringUtils.isEmpty(template.getTemplateType())) {
            throw new EmailException("msg.warning.system.email_message_type_empty");
        } else {
            if (MessageTypeEnum.EMAIL.getCode().equalsIgnoreCase(template.getTemplateType())) {
                if (StringUtils.isEmpty(template.getSendType())) {
                    throw new EmailException("msg.warning.system.message_send_type_empty");
                }

                if (null == template.getAccountId()) {
                    throw new EmailException("msg.warning.system.message_account_empty");
                }
            }

        }
    }

    private void validateMessageCustom(List<MessageReceiver> receivers, String accountCode, String messageType, String sendType) throws EmailException {
        if (CollectionUtils.isEmpty(receivers)) {
            throw new EmailException("msg.warning.system.no_message_receiver");
        } else if (StringUtils.isEmpty(messageType)) {
            throw new EmailException("msg.warning.system.email_message_type_empty");
        } else {
            if (MessageTypeEnum.EMAIL.getCode().equalsIgnoreCase(messageType)) {
                if (StringUtils.isEmpty(sendType)) {
                    throw new EmailException("msg.warning.system.message_send_type_empty");
                }

                if (StringUtils.isEmpty(accountCode)) {
                    throw new EmailException("msg.warning.system.message_account_empty");
                }
            }

        }
    }

    private String translateData(String content, Map data) throws Exception {
        if (content == null) {
            return "";
        } else {
            StringWriter out = new StringWriter();
            Throwable var4 = null;

            String var7;
            try {
                Configuration config = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
                config.setDefaultEncoding("UTF-8");
                config.setClassicCompatible(true);
                Template template = new Template((String)null, content, config);
                template.process(data, out);
                var7 = out.toString();
            } catch (Throwable var16) {
                var4 = var16;
                throw var16;
            } finally {
                if (out != null) {
                    if (var4 != null) {
                        try {
                            out.close();
                        } catch (Throwable var15) {
                            var4.addSuppressed(var15);
                        }
                    } else {
                        out.close();
                    }
                }

            }

            return var7;
        }
    }

    private void initStd(BaseDTO dto, IRequest iRequest) {
        dto.setObjectVersionNumber(1L);
        dto.setCreationDate(new Date());
        dto.setLastUpdateDate(new Date());
        if (iRequest != null) {
            dto.setCreatedBy(iRequest.getUserId());
            dto.setLastUpdatedBy(iRequest.getUserId());
        } else {
            dto.setCreatedBy((Long)null);
            dto.setLastUpdatedBy((Long)null);
        }

    }

    private MessageTemplate check(String templateCode, String accountCode, List<MessageReceiver> receivers) throws EmailException {
        if (CollectionUtils.isEmpty(receivers)) {
            throw new EmailException("msg.warning.system.no_message_receiver");
        } else {
            MessageTemplate record = new MessageTemplate();
            record.setTemplateCode(templateCode);
            List<MessageTemplate> selectMessageTemplates = this.templateMapper.select(record);
            if (CollectionUtils.isEmpty(selectMessageTemplates)) {
                throw new EmailException("msg.warning.system.no_message_template");
            } else {
                MessageTemplate template = (MessageTemplate)selectMessageTemplates.get(0);
                if (template == null) {
                    throw new EmailException("msg.warning.system.no_message_template");
                } else if (template.getPriorityLevel() == null) {
                    throw new EmailException("msg.warning.system.email_message_priority_empty");
                } else if (accountCode == null) {
                    throw new EmailException("msg.warning.system.message_account_empty");
                } else {
                    return template;
                }
            }
        }
    }

    private void check(String accountCode, PriorityLevelEnum priority, List<MessageReceiver> receivers) throws EmailException {
        if (CollectionUtils.isEmpty(receivers)) {
            throw new EmailException("msg.warning.system.no_message_receiver");
        } else if (priority == null) {
            throw new EmailException("msg.warning.system.email_message_priority_empty");
        } else {
            Iterator var4 = receivers.iterator();

            String messageAddress;
            do {
                if (!var4.hasNext()) {
                    if (accountCode == null) {
                        throw new EmailException("msg.warning.system.message_account_empty");
                    }

                    return;
                }

                MessageReceiver messageReceiver = (MessageReceiver)var4.next();
                messageAddress = messageReceiver.getMessageAddress();
            } while(messageAddress != null && !"".equals(messageAddress.trim()));

            throw new EmailException("msg.warning.system.no_message_receiver");
        }
    }

    @Override
    public Message sendMessageWithFileJc(IRequest iRequest, String templateCode, Map<String, Object> templateData, List<MessageReceiver> receivers, List<Long> attachmentIds,List<File> fileList) throws Exception {
        if (CollectionUtils.isEmpty(receivers)) {
            throw new EmailException("msg.warning.system.no_message_receiver");
        } else {
            MessageTemplate template = null;
            template = this.templateCache.getValue(templateCode);
            if (null == template) {
                template = this.templateMapper.selectByCode(templateCode);
            }

            System.out.println("template:" + template);
            this.validateMessageTemplate(template);
            MessageEmailAccount account = (MessageEmailAccount)this.emailAccountMapper.selectByPrimaryKey(template.getAccountId());
            String accountCode = null;
            if (account != null) {
                accountCode = account.getAccountCode();
            }

            String subject = this.translateData(template.getSubject(), templateData);
            String content = this.translateData(template.getContent(), templateData);
            String messageType = template.getTemplateType();
            String sendType = template.getSendType();
            this.sendMessageWithFileJc(iRequest, accountCode, subject, content, messageType, sendType, templateCode, receivers, attachmentIds,fileList);
            return null;
        }
    }

    @Override
    public Message sendMessageWithFileJc(IRequest iRequest, String accountCode, String subject, String content, String messageType, String sendType, String messageSource, List<MessageReceiver> receivers, List<Long> attachmentIds,List<File> fileList) throws Exception {
        this.validateMessageCustom(receivers, accountCode, messageType, sendType);
        if (MessageTypeEnum.EMAIL.getCode().equalsIgnoreCase(messageType)) {
            MessageEmailAccount emailAccount = new MessageEmailAccount();
            emailAccount.setAccountCode(accountCode);
            String messageEmailConfig = this.emailAccountMapper.selectMessageEmailConfig(emailAccount);
            if (StringUtils.isEmpty(messageEmailConfig)) {
                throw new EmailException("msg.warning.system.message_config_nonexistent");
            }

            if ("N".equals(messageEmailConfig)) {
                throw new EmailException("msg.warning.system.message_config_disable");
            }

            MessageEmailAccount account = this.emailAccountMapper.selectByAccountCode(accountCode);
            Message message = new Message();
            message.setMessageType(messageType);
            if(sendType.equals(SendTypeEnum.JOB.getCode())){
                if (SendTypeEnum.JOB.getCode().equalsIgnoreCase(sendType)) {
                    message.setSendFlag("P");
                }

                message.setSubject(subject);
                message.setContent(content);
                message.setMessageFrom(account.getAccountCode());
                message.setMessageSource(messageSource);
                ((IMessageService)this.self()).insertData(iRequest, message, receivers, attachmentIds);
                if (SendTypeEnum.JOB.getCode().equalsIgnoreCase(sendType)) {
                    this.emailService.sendSingleEmailMessageWithFile(message, fileList);
                }
            }else{
                if (SendTypeEnum.DIRECT.getCode().equalsIgnoreCase(sendType)) {
                    message.setSendFlag("P");
                }

                message.setSubject(subject);
                message.setContent(content);
                message.setMessageFrom(account.getAccountCode());
                message.setMessageSource(messageSource);
                ((IMessageService)this.self()).insertData(iRequest, message, receivers, attachmentIds);
                if (SendTypeEnum.DIRECT.getCode().equalsIgnoreCase(sendType)) {
                    this.emailService.sendSingleEmailMessageWithFile(message, fileList);
                }
            }

        }

        return null;
    }
}
