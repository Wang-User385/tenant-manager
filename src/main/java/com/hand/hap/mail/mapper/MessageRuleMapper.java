package com.hand.hap.mail.mapper;

import java.util.List;
import com.hand.hap.mail.dto.MessageRule;
import com.hand.hap.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

public interface MessageRuleMapper extends Mapper<MessageRule>{
    /**
     * 短信的短信发送规则
     * @return
     */
    List<MessageRule> queryForSendMessage();

    /**
     * 根据模板找短信发送规则
     * @return
     */
    List<MessageRule> queryTemplateRule(@Param("tempalteCode") String templateCode);

    /**
     * 微信的短信发送规则
     * @return
     */
    List<MessageRule> queryForSendWechat();


}
