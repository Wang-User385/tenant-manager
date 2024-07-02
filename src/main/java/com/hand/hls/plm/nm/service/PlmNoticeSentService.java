package com.hand.hls.plm.nm.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.core.annotation.StdWho;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.plm.nm.dto.PlmNoticeSent;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public interface PlmNoticeSentService extends IBaseService<PlmNoticeSent>, ProxySelf<PlmNoticeSentService> {
    List<PlmNoticeSent> selectNoticeSentAll(IRequest iRequest, PlmNoticeSent dto, int page, int pageSize);

    List<PlmNoticeSent> creditSave(IRequest iRequest, @StdWho PlmNoticeSent dto);

//    List<PlmNoticeSent> creditNotice(IRequest iRequest, PlmNoticeSent dto);
    /**
     * 通知书打印提醒定时任务
     */
    //void noticePrintReminder(CountDownLatch latch);

    /**
     * 催收通知书打印提醒定时任务
     */
    //void noticePrintCollection(CountDownLatch latch);

    /**
     * 关注回款情况提醒定时任务
     */
    //void paymentSituation(CountDownLatch latch);

}
