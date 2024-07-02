//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hap.mail.mapper;

import java.util.List;
import com.hand.hap.mail.dto.MessageReceiver;
import com.hand.hap.mybatis.common.Mapper;

public interface MessageReceiverMapper extends Mapper<MessageReceiver> {
    int deleteByMessageId(Long var1);

    List<MessageReceiver> selectByMessageId(Long var1);

    List<MessageReceiver> selectMessageAddressesByMessageId(MessageReceiver var1);
}
