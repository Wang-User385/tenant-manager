package com.hand.hls.csh.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.csh.dto.HlsCusConDebtExemptionReq;
import com.hand.hls.csh.dto.HlsCusConDebtExemptionReqCf;
import com.hand.hls.csh.mapper.HlsCusConDebtExemptionReqMapper;
import com.hand.hls.csh.service.IConDebtExemptionReqCfService;
import com.hand.hls.csh.service.IConDebtExemptionReqService;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsCusEmployeeMapper;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.user.service.LoginUserInfoService;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.sys.event.service.SysEventService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class ConDebtExemptionReqImpl extends BaseServiceImpl<HlsCusConDebtExemptionReq> implements IConDebtExemptionReqService {


    @Autowired
    private IConDebtExemptionReqCfService iConDebtExemptionReqCfService;

    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;

    @Autowired
    private HlsCusConDebtExemptionReqMapper hlsCusConDebtExemptionReqMapper;

    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;

    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Autowired
    private HlsCusEmployeeMapper employeeMapper;

    @Autowired
    private IActivitiStartService activitiStartService;

    @Autowired
    private LoginUserInfoService loginUserInfoService;

    @Autowired
    private SysEventService sysEventService;

    @Autowired
    private HlsCusConContractService hlsCusConContractService;

    @Override
    public void hlsCusConDebtExemptionReqSave(HttpSession session, IRequest request, HlsCusConDebtExemptionReq hlsCusConDebtExemptionReq) {
//        hlsCusConDebtExemptionReq.setDocumentId(5l);
        List<HlsCusConDebtExemptionReqCf> hlsCusConDebtExemptionReqCfList = hlsCusConDebtExemptionReq.getHlsCusConDebtExemptionReqCfList();
        //获取编码规则
        Map<String, String> params = new HashMap<String, String>();
        hlsCusConDebtExemptionReq.setChangeReqNumber(fndCodingRuleValuesService.getCodeRuleValue(request, hlsCusConDebtExemptionReq.getDocumentCategory(), hlsCusConDebtExemptionReq.getDocumentType(), hlsCusConDebtExemptionReq.getBusinessType(), params));
        hlsCusConDebtExemptionReq = self().insertSelective(request,hlsCusConDebtExemptionReq);
//        List<Long> contractId = hlsCusConDebtExemptionReqCfList.stream().map(m -> m.getContractId()).collect(Collectors.toList());
        for(HlsCusConDebtExemptionReqCf dt : hlsCusConDebtExemptionReqCfList){
            dt.setChangeReqId(hlsCusConDebtExemptionReq.getChangeReqId());
            dt = iConDebtExemptionReqCfService.insertSelective(request,dt);
        }
        conDebtSubmitWfl(session,request,hlsCusConDebtExemptionReq);
    }

    @Override
    public void hlsCusConDebtExemptionReqSave2(HttpSession session, IRequest request, HlsCusConDebtExemptionReq hlsCusConDebtExemptionReq) {
//        hlsCusConDebtExemptionReq.setDocumentId(5l);
        List<HlsCusConDebtExemptionReqCf> hlsCusConDebtExemptionReqCfList = hlsCusConDebtExemptionReq.getHlsCusConDebtExemptionReqCfList();
        //获取编码规则
        Map<String, String> params = new HashMap<String, String>();
        hlsCusConDebtExemptionReq.setChangeReqNumber(fndCodingRuleValuesService.getCodeRuleValue(request, hlsCusConDebtExemptionReq.getDocumentCategory(), hlsCusConDebtExemptionReq.getDocumentType(), hlsCusConDebtExemptionReq.getBusinessType(), params));
        hlsCusConDebtExemptionReq = self().insertSelective(request,hlsCusConDebtExemptionReq);
        List<Long> contractId = hlsCusConDebtExemptionReqCfList.stream().map(m -> m.getContractId()).collect(Collectors.toList());
        for(HlsCusConDebtExemptionReqCf dt : hlsCusConDebtExemptionReqCfList){
            dt.setChangeReqId(hlsCusConDebtExemptionReq.getChangeReqId());
            dt = iConDebtExemptionReqCfService.insertSelective(request,dt);
        }
        conDebtSubmitWfl2(session,request,hlsCusConDebtExemptionReq,contractId);
    }


    @Override
    public HlsCusConDebtExemptionReq conDebtSubmitWfl(HttpSession session, IRequest iRequest, HlsCusConDebtExemptionReq hlsCusConDebtExemptionReq) {
        HlsCusConDebtExemptionReq conDebtExemptionReq = new HlsCusConDebtExemptionReq();
        conDebtExemptionReq.setChangeReqId(hlsCusConDebtExemptionReq.getChangeReqId());
        conDebtExemptionReq = hlsCusConDebtExemptionReqMapper.selectByPrimaryKey(conDebtExemptionReq);
        List<HlsCusConDebtExemptionReq> hlsCusConDebtExemptionReqList = new ArrayList<>();
        hlsCusConDebtExemptionReqList.add(conDebtExemptionReq);
        databaseLockProvider.lock(conDebtExemptionReq);
        HlsCusConContract hlsCusConContract = new HlsCusConContract();
        hlsCusConContract.setContractId(conDebtExemptionReq.getDocumentId());
        hlsCusConContract = hlsCusConContractService.selectByPrimaryKey(iRequest,hlsCusConContract);
        //获取申请人
        Long userId=iRequest.getUserId();
        HlsEmployee employee1=employeeMapper.getEmployeeCode(userId);
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);

        //获取部门与公司
        String unitIdStr = String.valueOf(session.getAttribute("unitId"));
        Long unitId = Long.valueOf(unitIdStr);
        String companyIdStr = String.valueOf(session.getAttribute("companyId"));
        Long companyId = Long.valueOf(companyIdStr);

        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("workFlowType", "CON_CONTRACT_CSH_WFL");
        params.put("unitId", unitId);
        params.put("companyId", companyId);
        activitiStartService.start(iRequest, hlsCusConDebtExemptionReqList, params);

//        for(Long cid : contractId){
//            HlsCusConContract hlsCusConContract2=new HlsCusConContract();
//            hlsCusConContract2=hlsCusConContractMapper.selectByPrimaryKey(cid);
//            hlsCusConContract2.setFineReduceStatus("APPROVING");
//            hlsCusConContractService.updateByPrimaryKeySelective(iRequest,hlsCusConContract2);
//        }


        hlsCusConContract.setFineReduceStatus("APPROVING");
        hlsCusConContract = hlsCusConContractService.updateByPrimaryKeySelective(iRequest,hlsCusConContract);

        //修改罚息减免状态为审批中
        conDebtExemptionReq.setReqStatus("APPROVING");
        HlsCusConDebtExemptionReq getDebt = new HlsCusConDebtExemptionReq();
        getDebt.setChangeReqId(conDebtExemptionReq.getChangeReqId());
        conDebtExemptionReq.setObjectVersionNumber(self().selectByPrimaryKey(iRequest, getDebt).getObjectVersionNumber());
        conDebtExemptionReq = self().updateByPrimaryKeySelective(iRequest, conDebtExemptionReq);
        Map<String, Object> paramsEvent = new HashMap<String, Object>();
        String userName = "";
        if (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode()).size() > 0) {
            userName = (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode())).get(0).getUserName();
        }

        String msg = userName + "创建了" + hlsCusConContract.getContractName() + conDebtExemptionReq.getChangeReqNumber();
        paramsEvent.put("message", msg);
        paramsEvent.put("noticeTitle", "合同罚息减免审批");
        paramsEvent.put("noticeType", "NOTICE");
        paramsEvent.put("url", "");
        paramsEvent.put("level", 1L);
        sysEventService.eventSave(iRequest, conDebtExemptionReq.getChangeReqId(), conDebtExemptionReq.getDocumentCategory(), conDebtExemptionReq.getDocumentType(), "CON", "CON_PENALTY_REDUCE", "P2D", paramsEvent);

        return conDebtExemptionReq;
    }

    @Override
    public HlsCusConDebtExemptionReq conDebtSubmitWfl2(HttpSession session, IRequest iRequest, HlsCusConDebtExemptionReq hlsCusConDebtExemptionReq,List<Long> contractId) {
        HlsCusConDebtExemptionReq conDebtExemptionReq = new HlsCusConDebtExemptionReq();
        conDebtExemptionReq.setChangeReqId(hlsCusConDebtExemptionReq.getChangeReqId());
        conDebtExemptionReq = hlsCusConDebtExemptionReqMapper.selectByPrimaryKey(conDebtExemptionReq);
        List<HlsCusConDebtExemptionReq> hlsCusConDebtExemptionReqList = new ArrayList<>();
        hlsCusConDebtExemptionReqList.add(conDebtExemptionReq);
        databaseLockProvider.lock(conDebtExemptionReq);
        HlsCusConContract hlsCusConContract = new HlsCusConContract();
        hlsCusConContract.setContractId(conDebtExemptionReq.getDocumentId());
        hlsCusConContract = hlsCusConContractService.selectByPrimaryKey(iRequest,hlsCusConContract);
        //获取申请人
        Long userId=iRequest.getUserId();
        HlsEmployee employee1=employeeMapper.getEmployeeCode(userId);
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);

        //获取部门与公司
        String unitIdStr = String.valueOf(session.getAttribute("unitId"));
        Long unitId = Long.valueOf(unitIdStr);
        String companyIdStr = String.valueOf(session.getAttribute("companyId"));
        Long companyId = Long.valueOf(companyIdStr);

        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("workFlowType", "CON_CONTRACT_CSH_WFL_LS");
        params.put("unitId", unitId);
        params.put("companyId", companyId);
        List<HlsCusConDebtExemptionReq> reqs = hlsCusConDebtExemptionReqMapper.selectReqByChangeReqId(hlsCusConDebtExemptionReq);
        params.put("documentName","零售罚息减免");
        if(CollectionUtils.isNotEmpty(reqs)){
            String man = Optional.of(reqs.get(0)).map(HlsCusConDebtExemptionReq::getManufacturerName).orElse("");
            log.info("零售罚息减免主机厂:{}",man);
            params.put("documentName",man+"-"+"零售罚息减免");
        }
        params.put("documentNumber", hlsCusConDebtExemptionReq.getChangeReqNumber());
        params.put("businessKey", hlsCusConDebtExemptionReq.getChangeReqId());
        log.info("params:{}",params);
        activitiStartService.start(iRequest, hlsCusConDebtExemptionReqList, params);

        for(Long cid : contractId){
            HlsCusConContract hlsCusConContract2=new HlsCusConContract();
            hlsCusConContract2=hlsCusConContractMapper.selectByPrimaryKey(cid);
            hlsCusConContract2.setFineReduceStatus("APPROVING");
            hlsCusConContractService.updateByPrimaryKeySelective(iRequest,hlsCusConContract2);
        }


//        hlsCusConContract.setFineReduceStatus("APPROVING");
//        hlsCusConContract = hlsCusConContractService.updateByPrimaryKeySelective(iRequest,hlsCusConContract);

        //修改罚息减免状态为审批中
        conDebtExemptionReq.setReqStatus("APPROVING");
        HlsCusConDebtExemptionReq getDebt = new HlsCusConDebtExemptionReq();
        getDebt.setChangeReqId(conDebtExemptionReq.getChangeReqId());
        conDebtExemptionReq.setObjectVersionNumber(self().selectByPrimaryKey(iRequest, getDebt).getObjectVersionNumber());
        conDebtExemptionReq = self().updateByPrimaryKeySelective(iRequest, conDebtExemptionReq);
        Map<String, Object> paramsEvent = new HashMap<String, Object>();
        String userName = "";
        if (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode()).size() > 0) {
            userName = (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode())).get(0).getUserName();
        }

        String msg = userName + "创建了" + hlsCusConContract.getContractName() + conDebtExemptionReq.getChangeReqNumber();
        paramsEvent.put("message", msg);
        paramsEvent.put("noticeTitle", "合同罚息减免审批");
        paramsEvent.put("noticeType", "NOTICE");
        paramsEvent.put("url", "");
        paramsEvent.put("level", 1L);
        sysEventService.eventSave(iRequest, conDebtExemptionReq.getChangeReqId(), conDebtExemptionReq.getDocumentCategory(), conDebtExemptionReq.getDocumentType(), "CON", "CON_PENALTY_REDUCE", "P2D", paramsEvent);

        return conDebtExemptionReq;
    }


}