package com.hand.hls.cont.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.dto.HlsCusConFloatingRateReq;
import com.hand.hls.cont.mapper.HlsCusConFloatingRateReqMapper;
import com.hand.hls.cont.service.HlsCusConFloatingRateReqService;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.user.service.LoginUserInfoService;
import com.hand.hls.wfl.service.IActivitiCommonService;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.sys.event.service.SysEventService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusConFloatingRateReqServiceImpl extends BaseServiceImpl<HlsCusConFloatingRateReq> implements HlsCusConFloatingRateReqService {
    @Autowired
    private HlsCusConFloatingRateReqMapper mapper;

    @Autowired
    private final static Logger logger = LoggerFactory.getLogger(HlsCusConFloatingRateReqService.class);

    @Autowired
    private IActivitiStartService activitiStartService;

    @Autowired
    private LoginUserInfoService loginUserInfoService;

    @Autowired
    private SysEventService sysEventService;

    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Autowired
    private HlsEmployeeMapper employeeMapper;

    /*调息头查询方法*/
    /*public List<HlsCusConFloatingRateReq> queryHeadList(IRequest iRequest, HlsCusConFloatingRateReq dto, int page, int pageSize){
        PageHelper.startPage(page, pageSize);
        Map<String,Object> map = new HashedMap();
        map.put("companyId",dto.getCompanyId());
        map.put("fltReqId",dto.getFltReqId());
        String[]  paramStatus = null;
        if(dto.getStatusList()!=null){
            paramStatus=dto.getStatusList().split("、");
        }
        map.put("statusList",paramStatus);
       return mapper.queryHeadList(map);
    }*/

    /**
     * 调息工作流入口
     *
     * @param request
     * @param cusConFloatingRateReq
     */
    @Override
    public void floatingRateWorkFlowStart(IRequest request, HlsCusConFloatingRateReq cusConFloatingRateReq) {
        cusConFloatingRateReq = self().selectByPrimaryKey(request, cusConFloatingRateReq);

        List<HlsCusConFloatingRateReq> list = new ArrayList<>();
        list.add(cusConFloatingRateReq);

        if (CollectionUtils.isNotEmpty(list)) {
            logger.debug("=============== start ctFloatingRateReq activiti ==============");
            databaseLockProvider.lock(list.get(0));
            //获取申请人
            HlsEmployee employee = employeeMapper.getEmployeeCode(request.getUserId());
            String employeeCode = employee.getEmployeeCode();
            request.setEmployeeCode(employeeCode);

            //开始流程
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("workFlowType", "CON_CONTRACT_INTEREST_ADJUSTMENT");
            params.put(IActivitiCommonService.DEMO_NAME, "LON_CONTRACT");
            activitiStartService.start(request, list, params);

            //修改单据状态
            cusConFloatingRateReq.setStatus("APPROVING");
            self().updateByPrimaryKeySelective(request, cusConFloatingRateReq);

            //发送系统消息
            Map<String, Object> paramsEvent = new HashMap<String, Object>();
            String userName = "";
            if (loginUserInfoService.queryUserInfo(request.getEmployeeCode()).size() > 0) {
                userName = (loginUserInfoService.queryUserInfo(request.getEmployeeCode())).get(0).getUserName();
            }
            //发送系统消息
            String msg;
            msg = userName + "提交了" + cusConFloatingRateReq.getDocumentTypeDesc() + "的批量调息申请，编号为" + cusConFloatingRateReq.getFltReqNumber();
            paramsEvent.put("noticeTitle", "批量调息申请");


            paramsEvent.put("message", msg);
            paramsEvent.put("noticeType", "NOTICE");
            paramsEvent.put("url", "");
            paramsEvent.put("level", 1L);
            sysEventService.eventSave(request, cusConFloatingRateReq.getFltReqId(), cusConFloatingRateReq.getDocumentCategory(), cusConFloatingRateReq.getDocumentType(), "CON_FLOATING_RATE_REQ", "CT_FLOATING_RATE_REQ_WFL", "P2D", paramsEvent);
        }
    }
}