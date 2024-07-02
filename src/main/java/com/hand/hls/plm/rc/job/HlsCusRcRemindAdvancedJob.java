package com.hand.hls.plm.rc.job;

import com.hand.hap.job.AbstractJob;
import com.hand.hls.plm.rc.mapper.HlsCusRentCollectionMapper;
import com.hand.hls.sys.dto.HlsSystemNotice;
import com.hand.hls.sys.mapper.HlsSystemNoticeMapper;
import hls.core.sys.event.service.SysEventService;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: wutianyu
 * @Date: Created in 下午7:47 2018/7/23
 */
public class HlsCusRcRemindAdvancedJob extends AbstractJob {
    private static final Logger logger = LoggerFactory.getLogger(HlsCusRcRemindAdvancedJob.class);

    @Autowired
    private SysEventService sysEventService;

    @Autowired
    private HlsSystemNoticeMapper noticeMapper;

    @Autowired
    HlsCusRentCollectionMapper rentCollectionMapper;

    @Override
    public void safeExecute(JobExecutionContext jobExecutionContext) throws Exception {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        List<Map> list = rentCollectionMapper.getAdvancedRemindList();
        for (Map m : list) {
            StringBuilder sb = new StringBuilder();
            sb.append(m.get("contractName").toString() + " ");
            sb.append(m.get("cfItemDesc").toString() + " ");
            sb.append("第");
            sb.append(m.get("times").toString());
            sb.append("期  ");
            sb.append("金额 ");
            sb.append(new BigDecimal(m.get("dueAmount").toString()).toPlainString());
            sb.append("的应收日期为");
            sb.append(sd.format((Date)m.get("dueDate")));
            sb.append(",请及时收取。");
            String message = sb.toString();
            sendNotice((Long) m.get("userId"), message);
        }
    }

    /**
     * @Description:发送消息
     * @Author: Wty
     * @Date: Created om 20:09 2018/6/19
     * @param: [userIdList, message] userIdList:需要发送的userId,message:需要发送的消息
     * @return: void
     */
    public void sendNotice(Long userId, String message) {
        logger.debug("============= SEND NOTICE START ==============");
            HlsSystemNotice hlsSystemNotice = new HlsSystemNotice();
            /*hlsSystemNotice.setNotice_message(message);
            hlsSystemNotice.setNotice_title("贷后检查提醒");
            hlsSystemNotice.setNotice_datetime(new Date());
            hlsSystemNotice.setNotice_type("NOTICE");
            hlsSystemNotice.setNotice_level(Long.valueOf(2));
            hlsSystemNotice.setSource_module("PLI_RC_ADVANCE");
            hlsSystemNotice.setSource_user_id(userId);*/
            hlsSystemNotice.setLastUpdateDate(new Date());
            noticeMapper.insertSelective(hlsSystemNotice);
            sysEventService.setNoticeCache(hlsSystemNotice);
    }
}
