package com.hand.hls.ast.service;

import java.util.List;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.mail.dto.Message;

public interface ISendMessageService extends ProxySelf<ISendMessageService> {

    /**
     * 创建消息
     * @param templateCode 模板代码
     * @param args 模板参数
     * @param receiverAddressList 接收人手机号list
     */
    Message createMessage(String templateCode, String args, List<String> receiverAddressList);


    /**
     * 发送短信
     * @param message 消息
     */
    String sendMessage(Message message);


    /**
     * 创建并发送消息
     * @param templateCode 模板代码
     * @param args 模板参数
     * @param receiverAddressList 接收人手机号list
     * @return
     */
    String sendMessage(String templateCode, String args, List<String> receiverAddressList);

    /**
     * 重新发送短信
     * @param param
     * @return
     */
    boolean resendMessage(IRequest requestContext, JSONObject param);

    String getToken();
}
