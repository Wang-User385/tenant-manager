package com.hand.hap.mail.mapper;

import com.hand.hap.mail.dto.MessageTransaction;
import com.hand.hap.mybatis.common.Mapper;

/**
 * description
 *
 * @author 许泽文 2022/11/07 16:20
 */
public interface MessageTransactionMapper extends Mapper<MessageTransaction> {
    int deleteByMessageId(Long messageId);

    MessageTransaction selectByMessageId(Long messageId);

    long selectSuccessCountByMessageId(Long messageId);
}
