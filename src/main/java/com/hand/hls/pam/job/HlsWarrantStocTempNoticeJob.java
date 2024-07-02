package com.hand.hls.pam.job;

import com.hand.hap.job.AbstractJob;
import com.hand.hls.pam.dto.HlsWarrantStockHd;
import com.hand.hls.pam.dto.HlsWarrantStockLn;
import com.hand.hls.pam.mapper.HlsWarrantStockHdMapper;
import com.hand.hls.sys.dto.HlsSystemNotice;
import com.hand.hls.sys.mapper.HlsSystemNoticeMapper;
import hls.core.sys.event.service.SysEventService;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @author Qian Yuanfeng
 * @date 2020/7/30 - 16:05
 */
public class HlsWarrantStocTempNoticeJob extends AbstractJob {

    private static final Logger logger = LoggerFactory.getLogger(HlsWarrantStocTempNoticeJob.class);

    @Autowired
    private SysEventService sysEventService;

    @Autowired
    private HlsSystemNoticeMapper noticeMapper;

    @Autowired
    private HlsWarrantStockHdMapper hlsWarrantStockHdMapper;

    public static final int THREE_DAYS = 3;

    /**
     * 系统会在权证临时出库后每隔3天，提醒主办项目经理进行归还权证的操作，直到权证归还。
     *
     * @param context
     * @throws Exception
     */
    @Override
    public void safeExecute(JobExecutionContext context) throws Exception {
        logger.info("================== WARRANT JOB ======================");
        //查询出权证信息的权证状态为 临时出库 且 权证头表的出入库性质为 临时出库（TEMP_OUT_STOCK） 的权证 ， 取头表的归还日期，头表的申请人
        //每隔3天，进行通知提醒
        HlsWarrantStockHd hlsWarrantStockHd = new HlsWarrantStockHd();
        List<HlsWarrantStockHd> hlsWarrantStockHdList = hlsWarrantStockHdMapper.queryReturnDateNotice(hlsWarrantStockHd);
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = dateFormat.format(date);

        for (HlsWarrantStockHd warrantStockHd : hlsWarrantStockHdList) {

            if (warrantStockHd.getReturnDate() != null) {
                Date returnDate = warrantStockHd.getReturnDate();
                String returnDa = dateFormat.format(returnDate);
                int compareResult = currentDate.compareTo(returnDa);
                if (compareResult == 0 || compareResult > 0) {

                    Long datsBetween = getDays(warrantStockHd.getReturnDate(), new Date());
                    if (datsBetween % THREE_DAYS == 0) {
                        List<Long> userIds = new ArrayList<>();
                        String contractName = null;
                        String warrantName = null;
                        if (warrantStockHd.getContractName() != null) {
                            contractName = warrantStockHd.getContractName();
                        }
                        if (warrantStockHd.getWarrantName() != null) {
                            warrantName = warrantStockHd.getWarrantName();
                        }
                        StringBuilder warrantMessage = new StringBuilder();
                        warrantMessage.append("请对合同名称为：");
                        warrantMessage.append(contractName);
                        warrantMessage.append(" ,权证名称为:");
                        warrantMessage.append(warrantName);
                        warrantMessage.append(" 的权证进行回库操作！");
                        String message = warrantMessage.toString();

                        if (warrantStockHd.getApplicantId() != null) {
                            Long userId = warrantStockHd.getApplicantId();
                            if (!userIds.contains(userId)) {
                                userIds.add(userId);
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


    /**
     * 发送消息
     *
     * @param userIdList
     * @param message
     */
    public void sendNotice(List<Long> userIdList, String message) {
        logger.debug("============= SEND WARRANT NOTICE START ==============");
        for (Long userId : userIdList) {
            HlsSystemNotice hlsSystemNotice = new HlsSystemNotice();
            hlsSystemNotice.setNoticeMessage(message);
            hlsSystemNotice.setNoticeTitle("权证回库提醒");
            hlsSystemNotice.setNoticeDatetime(new Date());
            hlsSystemNotice.setNoticeType("NOTICE");
            hlsSystemNotice.setNoticeLevel("3");
            hlsSystemNotice.setSourceModule("PLI");
            hlsSystemNotice.setSourceUserId(userId);
            hlsSystemNotice.setLastUpdateDate(new Date());
            noticeMapper.insertSelective(hlsSystemNotice);
            sysEventService.setNoticeCache(hlsSystemNotice);
        }
    }


    public Long getDays(Date startDate, Date endDate) {
        return ChronoUnit.DAYS.between(startDate.toInstant(), endDate.toInstant());
    }

}
