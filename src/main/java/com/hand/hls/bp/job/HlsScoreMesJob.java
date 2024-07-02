package com.hand.hls.bp.job;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.job.AbstractJob;
import com.hand.hap.mail.controllers.MessageController;
import com.hand.hap.mail.mapper.MessageReceiverMapper;
import com.hand.hap.mail.mapper.MessageTransactionMapper;
import com.hand.hap.mail.service.IMessageService;
import com.hand.hls.bp.dto.HlsScoreCalculation;
import com.hand.hls.bp.mapper.HlsScoreCalculationMapper;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import hls.core.sys.event.service.SysEventService;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HlsScoreMesJob extends AbstractJob {
    @Autowired
    private HlsScoreCalculationMapper mapper;
    @Autowired
    private SysEventService sysEventService;

    /*
     * 商业伙伴评级提醒
     * 有效时间到期后，系统生成提醒提醒对应人员进行客户评级
     * */
    @Override
    public void safeExecute(JobExecutionContext jobExecutionContext) throws Exception {
        //1。到期日 to_date
        Date toDate = new Date();
        IRequest iRequest = RequestHelper.getCurrentRequest(true);
        List<HlsScoreCalculation> calculationList = mapper.selectScoreCountM(toDate);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        if (calculationList.size() > 0) {
            for (HlsScoreCalculation calculation : calculationList) {
                String maxDate = df.format(calculation.getMaxToDate());
                String nowDate = df.format(toDate);
                //若果当前日期等于最大到期日 则进行下面判断
                if (maxDate.equalsIgnoreCase(nowDate)) {
                    //到期日所在日期，若果该商业伙伴未做评级则进行提醒
                    List<HlsScoreCalculation> list = mapper.selectScoreMes(calculation.getBpId(), calculation.getMaxToDate());
                    //查询当前年份该商业伙伴是否已做评级 并审批通过
                    List<HlsScoreCalculation> calculationListF = mapper.selectScoreCountF(calculation.getBpId(), calculation.getMaxToDate());
                    if (list.size() <= 0 && calculationListF.size() <= 0) {
                        Map<String, Object> evenParams = new HashMap();
                        String str = "您好：【" + calculation.getBpName() + "】的信用评级已到期，请进行再次评级，谢谢！";
                        evenParams.put("message", str);
                        evenParams.put("noticeTitle", "客户评级");
                        evenParams.put("url", "");
                        evenParams.put("level", 1L);
                        evenParams.put("noticeType", "NOTICE");
                        evenParams.put("allocation_id", calculation.getAllocationId());
                        evenParams.put("eventUserId", calculation.getScoreUserId());
                        sysEventService.eventSave(iRequest, calculation.getScoreId(), "HLS_SCORE_CALCULATION", "HLS_SCORE_CALCULATION", "BP", "HLS_SCORE_CALCULATION", "P2D", evenParams);
                    }
                }
            }
        }
    }
}
