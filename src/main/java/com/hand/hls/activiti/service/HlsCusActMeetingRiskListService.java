package com.hand.hls.activiti.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.activiti.dto.HlsCusActMeetingRiskList;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.fct.dto.HlsCusFctContract;

import java.util.List;

/**
 * @Author: qixiang.shao
 * @Description: 上会风险防范措施Service
 * @Date: Created in 17:04 2018/1/9
 * @Modified By:
 */
public interface HlsCusActMeetingRiskListService extends IBaseService<HlsCusActMeetingRiskList>, ProxySelf<HlsCusActMeetingRiskListService> {

    List<HlsCusActMeetingRiskList> queryRiskListByProcessInstanceId(IRequest iRequest, Long processInstanceId);// 根据工作流实例Id查询风险防范措施

    List<HlsCusActMeetingRiskList> queryRiskListSelectedByProcessInstanceId(IRequest iRequest, Long processInstanceId); // 根据工作流实例Id查询已风险

//    void insertFctPaymentPt(IRequest iRequest, HlsCusFctContract hlsCusFctContract);

    void insertConPaymentPt(IRequest iRequest, HlsCusConContract hlsCusConContract);
}
