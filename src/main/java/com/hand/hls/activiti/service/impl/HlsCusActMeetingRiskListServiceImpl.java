package com.hand.hls.activiti.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.activiti.dto.HlsCusActMeetingRiskList;
import com.hand.hls.activiti.mapper.HlsCusActMeetingRiskListMapper;
import com.hand.hls.activiti.service.HlsCusActMeetingRiskListService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractPaymentPt;
import com.hand.hls.cont.service.HlsCusConContractPaymentPtService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * @Author: qixiang.shao
 * @Description: 上会风险防范措施Service实现类
 * @Date: Created in 17:05 2018/1/9
 * @Modified By:
 */
@Service
@Transactional
public class HlsCusActMeetingRiskListServiceImpl extends BaseServiceImpl<HlsCusActMeetingRiskList> implements HlsCusActMeetingRiskListService {
    @Autowired
    private HlsCusActMeetingRiskListMapper hlsCusActMeetingRiskListMapper;
//    @Autowired
//    private HlsCusFctContractPaymentPtService hlsCusFctContractPaymentPtService;
//    @Autowired
//    private HlsCusFctProjectService hlsCusFctProjectService;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private HlsCusConContractPaymentPtService hlsCusConContractPaymentPtService;

    @Override
    public List<HlsCusActMeetingRiskList> queryRiskListByProcessInstanceId(IRequest iRequest, Long processInstanceId) {
        return hlsCusActMeetingRiskListMapper.queryRiskListByProcessInstanceId(processInstanceId);
    }

    @Override
    public List<HlsCusActMeetingRiskList> queryRiskListSelectedByProcessInstanceId(IRequest iRequest, Long processInstanceId) {
        return hlsCusActMeetingRiskListMapper.queryRiskListSelectedByProcessInstanceId(processInstanceId);
    }

//    @Override
//    public void insertFctPaymentPt(IRequest iRequest, HlsCusFctContract hlsCusFctContract) {
//        HlsCusFctProject hlsCusFctProject = new HlsCusFctProject();
//        hlsCusFctProject.setProjectId(hlsCusFctContract.getProjectId());
//        hlsCusFctProject = hlsCusFctProjectService.selectByPrimaryKey(iRequest, hlsCusFctProject);
//        List<HlsCusActMeetingRiskList> hlsCusActMeetingRiskListList = hlsCusActMeetingRiskListMapper.queryPaymentPt(hlsCusFctProject.getRefProjectId());
//        for (HlsCusActMeetingRiskList dt : hlsCusActMeetingRiskListList) {
//            HlsCusFctContractPaymentPt fctContractPaymentPt = new HlsCusFctContractPaymentPt();
//            fctContractPaymentPt.setContractId(hlsCusFctContract.getContractId());
//            fctContractPaymentPt.setPaymentName(dt.getMeetingRiskDescription());
//            fctContractPaymentPt.setSatisfyFlag("N");
//            fctContractPaymentPt.setMeetFlag("Y");
//            hlsCusFctContractPaymentPtService.insertSelective(iRequest, fctContractPaymentPt);
//        }
//    }

    @Override
    public void insertConPaymentPt(IRequest iRequest, HlsCusConContract hlsCusConContract) {
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(hlsCusConContract.getProjectId());
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProject);

        List<HlsCusActMeetingRiskList> hlsCusActMeetingRiskListList = hlsCusActMeetingRiskListMapper.queryConPaymentPt(hlsCusPrjProject.getRefProjectId());
        HlsCusConContractPaymentPt conContractPaymentPtTemp = new HlsCusConContractPaymentPt();
        conContractPaymentPtTemp.setContractId(hlsCusConContract.getContractId());
        List<HlsCusConContractPaymentPt> conContractPaymentPtLists = hlsCusConContractPaymentPtService.select(iRequest, conContractPaymentPtTemp, 1, 999999);
        for (HlsCusActMeetingRiskList dt : hlsCusActMeetingRiskListList) {
            HlsCusConContractPaymentPt conContractPaymentPt = new HlsCusConContractPaymentPt();
            conContractPaymentPt.setContractId(hlsCusConContract.getContractId());
            conContractPaymentPt.setPaymentName(dt.getMeetingRiskDescription());
            conContractPaymentPt.setSatisfyFlag("N");
            conContractPaymentPt.setMeetFlag("Y");
            if (conContractPaymentPtLists.size() == 0) {
                hlsCusConContractPaymentPtService.insertSelective(iRequest, conContractPaymentPt);
            }
        }
    }
}
