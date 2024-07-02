package com.hand.hls.plm.pli.job;

import com.hand.hap.job.AbstractJob;
import com.hand.hls.plm.pli.dto.HlsCusPostloanInspection;
import com.hand.hls.plm.pli.mapper.HlsCusPostloanInspectionMapper;
import com.hand.hls.plm.pli.service.HlsCusIPostloanInspectionService;
import com.hand.hls.sys.dto.HlsSystemNotice;
import com.hand.hls.sys.mapper.HlsSystemNoticeMapper;
import com.hand.hls.utils.HlsCusDateMethodUtil;
import hls.core.sys.event.service.SysEventService;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @Description:贷后检查消息提醒JOB
 * @Author: wty
 * @Date: Created in 10:48 2018/6/19
 */
@Transactional(rollbackFor = Exception.class)
@Getter
public class HlsCusPLINoticeRemind extends AbstractJob {
    private static final Logger logger = LoggerFactory.getLogger(HlsCusPLINoticeRemind.class);

    @Autowired
    private SysEventService sysEventService;

    @Autowired
    private HlsSystemNoticeMapper noticeMapper;

    @Autowired
    private HlsCusPostloanInspectionMapper postloanInspectionMapper;

    @Autowired
    private HlsCusIPostloanInspectionService postloanInspectionService;

    public static final int WEEK_DAYS = 7;

    @Override
    public void safeExecute(JobExecutionContext jobExecutionContext) throws Exception {
        String message;
        int minusDays;
        logger.info("================== WTY SEND PLI NOTICE JOB ======================");
        //根据companyId来进行分别处理
        List<Map<String, Object>> companyList = postloanInspectionMapper.selectCompanyInfo(new HashMap<>());
        if (CollectionUtils.isNotEmpty(companyList)) {
            for (Map m : companyList) {
                Long companyId = Long.parseLong(m.get("companyId").toString());
                String MODEL = "JOB";
                List<HlsCusPostloanInspection> list = postloanInspectionService.selectCheckList(companyId,MODEL);
                if (CollectionUtils.isNotEmpty(list)) {
                    for (HlsCusPostloanInspection p : list) {
                        minusDays = HlsCusDateMethodUtil.dateReduction(p.getRemindDate(), new Date());
                        //如果能被7整除，则说明满足要求
                        if (minusDays % WEEK_DAYS == 0) {
                            List<Long> userIds = new ArrayList<>();
                            message = p.getCheckContain();
                            Map<String, Object> map = new HashMap<>();
                            map.put("bpId", p.getBpId());
                            map.put("companyId", companyId);
                            List<Map<String, Object>> mapList = postloanInspectionMapper.selectInceptContractsCreatedByBpId(map);
                            if (CollectionUtils.isNotEmpty(mapList)) {
                                for (Map u : mapList) {
                                    Long userId = Long.parseLong(u.get("userId").toString());
                                    if (!userIds.contains(userId)) {
                                        userIds.add(userId);
                                    }
                                }
                            }
                            if (CollectionUtils.isNotEmpty(userIds)) {
                                //发送消息
                                sendNotice(userIds, message);
                            }
                        }
                    }
                }
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
            hlsSystemNotice.setNoticeTitle("贷后检查提醒");
            hlsSystemNotice.setNoticeDatetime(new Date());
            hlsSystemNotice.setNoticeType("NOTICE");
            hlsSystemNotice.setNoticeLevel("2");
            hlsSystemNotice.setSourceModule("PLI");
            hlsSystemNotice.setSourceUserId(userId);
            hlsSystemNotice.setLastUpdateDate(new Date());
            noticeMapper.insertSelective(hlsSystemNotice);
            sysEventService.setNoticeCache(hlsSystemNotice);
        }
    }

}
