package com.hand.hls.app.event.service;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.dto.TaskActionRequestExt;
import com.hand.hap.core.IRequest;
import org.activiti.engine.task.Task;

import java.util.Map;

public interface AppWflTodoNoticeService {
    /**
     * 发送给企业微信待办
     * @param sourceId
     */
    void sendTodoNotice(Long sourceId, String openId, JSONObject mobiles);

    /**
     * 将企业微信待办置为已办
     * @param sourceId
     * @param openId
     */
    void dealTodoNotice(Long sourceId, String openId);

}
