package com.hand.hls.ast.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.service.IHapInterfaceHeaderService;
import com.hand.hap.mail.dto.Message;
import com.hand.hap.mail.dto.MessageReceiver;
import com.hand.hap.mail.dto.MessageTemplate;
import com.hand.hap.mail.dto.MessageTransaction;
import com.hand.hap.mail.mapper.MessageMapper;
import com.hand.hap.mail.mapper.MessageReceiverMapper;
import com.hand.hap.mail.mapper.MessageTemplateMapper;
import com.hand.hap.mail.mapper.MessageTransactionMapper;
import com.hand.hls.ast.constants.MessageTransactionStatus;
import com.hand.hls.ast.service.ISendMessageService;
import com.hand.hls.interfacePlatform.utils.InterfacePlatformUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 发送微信提醒 和短信提醒接口
 *
 * @author Edward
 */
@Service
public class SendMessageServiceImpl implements ISendMessageService {
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private MessageTemplateMapper messageTemplateMapper;
    @Autowired
    private MessageReceiverMapper messageReceiverMapper;
    @Autowired
    private MessageTransactionMapper messageTransactionMapper;
    @Autowired
    private InterfacePlatformUtils interfacePlatformUtils;

    private final Logger logger = LoggerFactory.getLogger(SendMessageServiceImpl.class);

    private static final String HMSINTERFACEHEADER = "return HmsInterfaceHeader:{}";

    private static final String DATA_NOT_FOUND = "根据sysName和apiName没有找到数据";

    private static final String RAW = "raw";

    private static final String FORMAT_ERROR = "不支持的请求形式";

    private static final String INTERFACE_ERROR = "不支持的接口类型";

    public static final String MESSAGE_TYPE = "SMS";

    public static final String SUCCESS_RETURN_STATUS = "S";

    public static final String SEND_INTERFACE_URL = "/hitf/v2p/rest/invoke/R0RIQ0c6SFpFUk8uU01TLlBPUlRBTDpKS1BUMDIw";

    @Override
    public boolean resendMessage(IRequest requestContext, JSONObject param) {
        Long messageId = Long.valueOf(param.getString("message_id"));
        Message message = messageMapper.selectByPrimaryKey(messageId);
        this.sendMessage(message);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Message createMessage(String templateCode, String args, List<String> receiverAddressList) {
        MessageTemplate messageTemplate = messageTemplateMapper.selectByCode(templateCode);
        Message message = new Message();
        message.setMessageType(MESSAGE_TYPE);
        message.setSubject(messageTemplate.getSubject());

        // 转换模板内容
        String content = messageTemplate.getContent();
        JSONObject jsonObject = JSON.parseObject(args);
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            String reg = "\\$\\{" + entry.getKey() + "\\}";
            content = content.replaceAll(reg, entry.getValue().toString());
        }
        message.setContent(content);

        message.setSendFlag("0");
        message.setTemplateCode(templateCode);
        message.setSendArgs(args);
        messageMapper.insertSelective(message);

        for (String receiver : receiverAddressList) {
            MessageReceiver mr = new MessageReceiver();
            mr.setMessageId(message.getMessageId());
            mr.setMessageAddress(receiver);
            mr.setMessageType(MESSAGE_TYPE);
            messageReceiverMapper.insertSelective(mr);
        }

        MessageTransaction messageTransaction = new MessageTransaction();
        messageTransaction.setMessageId(message.getMessageId());
        // 初始状态就绪
        messageTransaction.setTransactionStatus(MessageTransactionStatus.P.getValue());
        messageTransaction.setTransactionMessage(message.getContent());
        messageTransactionMapper.insertSelective(messageTransaction);
        return message;
    }

    @Override
    public String sendMessage(Message message) {
        MessageTransaction transaction = messageTransactionMapper.selectByMessageId(message.getMessageId());
        try {
            JSONObject jsonObject = new JSONObject();
            List<MessageReceiver> receivers = messageReceiverMapper.selectByMessageId(message.getMessageId());
            String address = receivers.stream().map(MessageReceiver::getMessageAddress).collect(Collectors.joining(","));
            jsonObject.put("appNo", address);
            jsonObject.put("templateCode", message.getTemplateCode());
            jsonObject.put("args", JSON.parseObject(message.getSendArgs()));
            transaction.setTransactionStatus(MessageTransactionStatus.S.getValue());
            message.setSendFlag("1");
            return invokeMessage(jsonObject);
        } catch (Exception e) {
            transaction.setTransactionStatus(MessageTransactionStatus.F.getValue());
            transaction.setTransactionMessage(e.getMessage());
            message.setSendFlag("0");
            throw e;
        } finally {
            messageTransactionMapper.updateByPrimaryKeySelective(transaction);
            messageMapper.updateByPrimaryKeySelective(message);
        }
    }

    @Override
    public String sendMessage(String templateCode, String args, List<String> receiverAddressList) {
        Message message = this.createMessage(templateCode, args, receiverAddressList);
        return this.sendMessage(message);
    }

    @Override
    public String getToken() {
        String token = interfacePlatformUtils.getAccessTokenFromRedis();
        return token;
    }

    public String invokeMessage(JSONObject jsonObject) {
        // 返回的结果
        JSONObject result = null;
        logger.info("短信发送内容:{}", jsonObject);
        result = interfacePlatformUtils.getInterfaceRequest(jsonObject,
                SEND_INTERFACE_URL+"?access_token=",
                "短信发送");
        String returnStatus = result.getString("returnStatus");
        String returnMsg = result.getString("returnMsg");
        if(!SUCCESS_RETURN_STATUS.equals(returnStatus)){
            throw new RuntimeException(returnMsg);
        }
        return result.toString();
    }
}
