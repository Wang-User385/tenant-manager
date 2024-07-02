package com.hand.hls.plm.rc.job;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.job.AbstractJob;
import com.hand.hls.plm.rc.dto.HlsCusRentCollectionRule;
import com.hand.hls.plm.rc.service.HlsCusRentCollectionRuleService;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.mapper.SysUserMapper;
import hls.core.sys.event.service.SysEventService;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description 定时每天发送消息
 *
 * @author yuanyuan 2019/03/27 9:25 AM
 */
public class HlsCusRentCollectionNoticeJob extends AbstractJob {

    @Autowired
    private HlsCusRentCollectionRuleService rentCollectionRuleService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysEventService sysEventService;


    @Override
    public void safeExecute(JobExecutionContext jobExecutionContext) throws Exception {

        List<HlsCusRentCollectionRule> collectionContracts = rentCollectionRuleService.selectOverMonthContract();

        if (collectionContracts.size() > 0) {
            Map<String, Object> paramsEvent = new HashMap<>();
            paramsEvent.put("noticeType", "NOTICE");
            paramsEvent.put("url", "");
            paramsEvent.put("level", 1L);
            paramsEvent.put("noticeTitle", "五级分类调整建议");
            IRequest request = RequestHelper.newEmptyRequest();

            for (HlsCusRentCollectionRule contract : collectionContracts) {
                //发送系统消息
                StringBuffer notice = new StringBuffer();
                notice.append(contract.getContractNumber())
                        .append(contract.getContractName())
                        .append("逾期").append(contract.getCollectionDays()).append("天")
                        .append("建议五级分类调整为");
                if (contract.getCollectionDays().intValue() <= 180) {
                    notice.append("关注");
                } else if (contract.getCollectionDays().intValue() <= 365) {
                    notice.append("初级");
                } else if (contract.getCollectionDays().intValue() <= 730) {
                    notice.append("可疑");
                } else {
                    notice.append("损失");
                }

                paramsEvent.put("message", notice.toString());
                /*List<SysUser> userList = sysUserMapper.selectRentCollectionNoticeUser(contract.getCompanyId(), contract.getContractId(), contract.getContractDocCategory());
                for (SysUser user : userList) {
                    request.setUserId(user.getUserId());
                    if ("FCT_CONTRACT".equals(contract.getContractDocCategory())) {
                        sysEventService.eventSave(request, contract.getContractId(), "FCT_CONTRACT", "FCT_CONTRACT", "FCT", "FCT_CONTRACT.FIVE.ADJUST", "P2D", paramsEvent);
                    }
                    if ("CON_CONTRACT".equals(contract.getContractDocCategory())) {
                        sysEventService.eventSave(request, contract.getContractId(), "CON_CONTRACT", "CON_CONTRACT", "FCT", "CON_CONTRACT.FIVE.ADJUST", "P2D", paramsEvent);

                    }
                }*/
            }
        }

    }
}
