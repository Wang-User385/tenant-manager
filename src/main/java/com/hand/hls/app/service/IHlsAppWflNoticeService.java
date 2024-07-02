package com.hand.hls.app.service;

import com.hand.hap.core.ProxySelf;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;

/**
 * @author Qian Yuanfeng
 * @date 2020/10/14 - 10:21
 */
public interface IHlsAppWflNoticeService extends ProxySelf<IHlsAppWflNoticeService> {

    void wxinSendWflTodoMsg(Long userId, DelegateTask delegateTask);

    void wxinSendWflFinishMsg(Long userId, String noticeType, DelegateExecution delegateExecution);


}
