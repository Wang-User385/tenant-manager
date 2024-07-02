package com.hand.hls.inv.job;

import com.hand.hap.job.AbstractJob;
import com.hand.hls.inv.dto.HlsCusFinancePurchase;
import com.hand.hls.inv.mapper.HlsCusFinancePurchaseMapper;
import com.hand.hls.sys.dto.HlsSystemNotice;
import com.hand.hls.sys.mapper.HlsSystemNoticeMapper;
import hls.core.sys.event.service.SysEventService;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:投资理财消息提醒
 * @Author: wutianyu
 * @Date: Created in 下午9:26 2018/7/19
 */
@Transactional(rollbackFor = Exception.class)
public class HlsCusInvFinancingRemindJob extends AbstractJob {

    private static final Logger logger = LoggerFactory.getLogger(HlsCusInvFinancingRemindJob.class);

    @Autowired
    private HlsSystemNoticeMapper noticeMapper;

    @Autowired
    private SysEventService sysEventService;

    @Autowired
    private HlsCusFinancePurchaseMapper purchaseMapper;


    /**
     * 财务部门code为50
     */
    private static final long UNIT_CODE = 50;

    @Override
    public void safeExecute(JobExecutionContext jobExecutionContext) {
        HlsCusFinancePurchase fin = new HlsCusFinancePurchase();
        fin.setDays(3L);
        List<HlsCusFinancePurchase> list = purchaseMapper.selectExpiringPurchase(fin);
        Map map = new HashMap(16);
        map.put("unitCode", new Long(UNIT_CODE));
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        List<Long> userIds = purchaseMapper.selectOrgUnitUserIds(map);
        if (CollectionUtils.isNotEmpty(list)) {
            for (HlsCusFinancePurchase f : list) {
                String productName = f.getFinancialProductName();
                String s1 = "的预计到息日为";
                String dueDate = sd.format(f.getExpectedDueDate());
                String s2 = ",请及时进行赎回操作";
                StringBuffer message = new StringBuffer();
                message.append(productName);
                message.append(s1);
                message.append(dueDate);
                message.append(s2);
                sendNotice(userIds, message.toString());
            }
        }
    }

    /**
     * @Description:发送消息
     * @Author: Wty
     * @Date: Created om 20:09 2018/6/19
     * @param: [userIdList, message] userIdList:需要发送的userId,message:需要发送的消息
     * @return: void
     */
    public void sendNotice(List<Long> userIdList, String message) {
        logger.debug("============= SEND NOTICE START ==============");
        for (Long userId : userIdList) {
            HlsSystemNotice hlsSystemNotice = new HlsSystemNotice();
            hlsSystemNotice.setNoticeMessage(message);
            hlsSystemNotice.setNoticeTitle("投资理财到期提醒");
            hlsSystemNotice.setNoticeType("NOTICE");
            hlsSystemNotice.setNoticeLevel(String.valueOf(Long.valueOf(2)));
            hlsSystemNotice.setSourceModule("INV_EXP");
            hlsSystemNotice.setSourceUserId(userId);
//            hlsSystemNotice.setNotice_message(message);
//            hlsSystemNotice.setNotice_title("投资理财到期提醒");
//            hlsSystemNotice.setNotice_datetime(new Date());
//            hlsSystemNotice.setNotice_type("NOTICE");
//            hlsSystemNotice.setNotice_level(Long.valueOf(2));
//            hlsSystemNotice.setSource_module("INV_EXP");
//            hlsSystemNotice.setSource_user_id(userId);
            hlsSystemNotice.setLastUpdateDate(new Date());
            noticeMapper.insertSelective(hlsSystemNotice);
            sysEventService.setNoticeCache(hlsSystemNotice);
        }
    }
}
