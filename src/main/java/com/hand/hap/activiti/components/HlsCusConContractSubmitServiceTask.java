package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.eas.dto.HlsCusEasBasicData;
import com.hand.hls.eas.dto.HlsCusEasLogin;
import com.hand.hls.eas.dto.HlsCusEasSourceRecord;
import com.hand.hls.eas.mapper.HlsCusEasSourceRecordMapper;
import com.hand.hls.eas.service.IHlsCusEasLoginService;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.hls.dto.HlsDurationHd;
import com.hand.hls.hls.mapper.HlsDurationHdMapper;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.sys.service.SysUserService;
import hls.core.sys.event.service.SysEventService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Yenick
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusConContractSubmitServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private SysUserService userService;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private SysEventService sysEventService;
    @Autowired
    private HlsCusConContractService hlsCusConContractService;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;

    @Autowired
    private HlsCusEasSourceRecordMapper hlsCusEasSourceRecordMapper;

    @Autowired
    private IHlsCusEasLoginService hlsCusEasLoginService;

    @Autowired
    private HlsDurationHdMapper hlsDurationHdMapper;

    public HlsCusConContractSubmitServiceTask(){}
    @Override
    public void execute(DelegateExecution delegateExecution) {
        String businessKey = delegateExecution.getProcessInstanceBusinessKey();

        String flag = null;
        IRequest requestCtx = (IRequest)delegateExecution.getVariable("iRequest");
        String result = (String)delegateExecution.getVariable("approveResult");
        String employeeCode = (String)delegateExecution.getVariable("startUserName");
        String hlsCusPrjProjectPrams = (String) delegateExecution.getVariable("hlsCusPrjProject");
        HlsCusPrjProject resultHlsCusPrjProject = new HlsCusPrjProject();
        //根据状态修改合同信息
        resultHlsCusPrjProject.setProjectId(Long.parseLong(businessKey));
        resultHlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(requestCtx, resultHlsCusPrjProject);
    /*    HlsCusConContract hlsCusConContract = new HlsCusConContract();
        hlsCusConContract.setProjectId(resultHlsCusPrjProject.getProjectId());
        hlsCusConContract.setDataClass("NORMAL");
        List<HlsCusConContract> hlsCusConContractList = hlsCusConContractService.select(requestCtx, hlsCusConContract, 1, 999999);*/
        databaseLockProvider.lock(resultHlsCusPrjProject);
        if("APPROVED".equalsIgnoreCase(result)){
            flag = "SIGN";
            HlsDurationHd hlsDurationHd = new HlsDurationHd();
            hlsDurationHd.setProjectId(resultHlsCusPrjProject.getProjectId());
            List<HlsDurationHd> hlsDurationHds = hlsDurationHdMapper.hlsDurationHdEtDetailQueryNew(hlsDurationHd);
            if (hlsDurationHds.size() > 0) {
                resultHlsCusPrjProject.setConApplicationChangeStatus(result);
            }else{
                resultHlsCusPrjProject.setConApplicationStatus(result);
            }
            resultHlsCusPrjProject.setContractStatus(flag);
/*
            resultHlsCusPrjProject.setConApplicationStatus(result);
*/
            hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, resultHlsCusPrjProject);
/*
            hlsCusConContractList.get(0).setContractStatus(flag);
*/
/*
            hlsCusConContractService.updateByPrimaryKeySelective(requestCtx, hlsCusConContractList.get(0));
*/


            //金蝶基础资料同步接口
           // easDataSyn(requestCtx,hlsCusConContractList.get(0).getContractId());


        }else if("APPROVED_RETURN".equalsIgnoreCase(result)){
            flag = "APPROVED_RETURN";
/*
            resultHlsCusPrjProject.setContractStatus(flag);
*/
            resultHlsCusPrjProject.setConApplicationStatus(flag);

            hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, resultHlsCusPrjProject);
           /* hlsCusConContractList.get(0).setContractStatus(flag);
            hlsCusConContractService.updateByPrimaryKeySelective(requestCtx, hlsCusConContractList.get(0));*/
        }
        else if("REJECTED".equalsIgnoreCase(result)){
            flag = "REJECTED";
/*
            resultHlsCusPrjProject.setContractStatus(flag);
*/

            HlsDurationHd hlsDurationHd = new HlsDurationHd();
            hlsDurationHd.setProjectId(resultHlsCusPrjProject.getProjectId());
            List<HlsDurationHd> hlsDurationHds = hlsDurationHdMapper.hlsDurationHdEtDetailQueryNew(hlsDurationHd);
            if (hlsDurationHds.size() > 0) {
                resultHlsCusPrjProject.setConApplicationChangeStatus(result);
            }else{
                resultHlsCusPrjProject.setConApplicationStatus(result);
            }
/*
            resultHlsCusPrjProject.setConApplicationStatus(flag);
*/

            hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, resultHlsCusPrjProject);
           /* hlsCusConContractList.get(0).setContractStatus(flag);
            hlsCusConContractService.updateByPrimaryKeySelective(requestCtx, hlsCusConContractList.get(0));*/
        }
    }


    public void easDataSyn(IRequest iRequest, Long contractId) {

        HlsCusEasSourceRecord hlsCusEasSourceRecord = new HlsCusEasSourceRecord();
        hlsCusEasSourceRecord.setContractId(contractId);
        //客户资料
        List<HlsCusEasSourceRecord> hlsCusEasSourceRecordList01 = hlsCusEasSourceRecordMapper.selectEasType01(hlsCusEasSourceRecord);
        //供应商资料
        List<HlsCusEasSourceRecord> hlsCusEasSourceRecordList04 = hlsCusEasSourceRecordMapper.selectEasType04(hlsCusEasSourceRecord);
        //租赁合同资料
        List<HlsCusEasSourceRecord> hlsCusEasSourceRecordList05 = hlsCusEasSourceRecordMapper.selectEasType05(hlsCusEasSourceRecord);
        //部门资料
        List<HlsCusEasSourceRecord> hlsCusEasSourceRecordListbm = hlsCusEasSourceRecordMapper.selectEasTypebm(hlsCusEasSourceRecord);

        //往来单位
        List<HlsCusEasSourceRecord> hlsCusEasSourceRecordList11 = hlsCusEasSourceRecordMapper.selectEasType11(hlsCusEasSourceRecord);


        //首先调用金蝶登录接口
        HlsCusEasLogin hlsCusEasLogin = new HlsCusEasLogin();
        hlsCusEasLogin = hlsCusEasLoginService.easDoLogin(iRequest, hlsCusEasLogin);

        if (hlsCusEasLogin != null) {
            if (hlsCusEasLogin.getSessionId() != null) {//说明登录成功
                //客户资料
                for (HlsCusEasSourceRecord item : hlsCusEasSourceRecordList01) {
                    HlsCusEasBasicData hlsCusEasBasicData = new HlsCusEasBasicData();
                    hlsCusEasBasicData.setTypeNumber(item.getTypeNumber());
                    hlsCusEasBasicData.setNumber(item.getSourceNumber());
                    hlsCusEasBasicData.setName(item.getSourceName());
                    hlsCusEasBasicData.setDescription(item.getDescription());

                    hlsCusEasLoginService.easBasicSyn(iRequest, hlsCusEasBasicData, item.getSourceTable(), item.getSourceId());
                }
                //供应商资料
                for (HlsCusEasSourceRecord item : hlsCusEasSourceRecordList04) {
                    HlsCusEasBasicData hlsCusEasBasicData = new HlsCusEasBasicData();
                    hlsCusEasBasicData.setTypeNumber(item.getTypeNumber());
                    hlsCusEasBasicData.setNumber(item.getSourceNumber());
                    hlsCusEasBasicData.setName(item.getSourceName());
                    hlsCusEasBasicData.setDescription(item.getDescription());
                    hlsCusEasLoginService.easBasicSyn(iRequest, hlsCusEasBasicData, item.getSourceTable(), item.getSourceId());
                }

                //租赁合同资料
                for (HlsCusEasSourceRecord item : hlsCusEasSourceRecordList05) {
                    HlsCusEasBasicData hlsCusEasBasicData = new HlsCusEasBasicData();
                    hlsCusEasBasicData.setTypeNumber(item.getTypeNumber());
                    hlsCusEasBasicData.setNumber(item.getSourceNumber());
                    hlsCusEasBasicData.setName(item.getSourceName());
                    hlsCusEasBasicData.setDescription(item.getDescription());
                    hlsCusEasLoginService.easBasicSyn(iRequest, hlsCusEasBasicData, item.getSourceTable(), item.getSourceId());
                }

                //部门资料
                for (HlsCusEasSourceRecord item : hlsCusEasSourceRecordListbm) {
                    HlsCusEasBasicData hlsCusEasBasicData = new HlsCusEasBasicData();
                    hlsCusEasBasicData.setTypeNumber(item.getTypeNumber());
                    hlsCusEasBasicData.setNumber(item.getSourceNumber());
                    hlsCusEasBasicData.setName(item.getSourceName());
                    hlsCusEasBasicData.setDescription(item.getDescription());
                    hlsCusEasLoginService.easBasicSyn(iRequest, hlsCusEasBasicData, item.getSourceTable(), item.getSourceId());
                }

                //往来单位资料
                for (HlsCusEasSourceRecord item : hlsCusEasSourceRecordList11) {
                    HlsCusEasBasicData hlsCusEasBasicData = new HlsCusEasBasicData();
                    hlsCusEasBasicData.setTypeNumber(item.getTypeNumber());
                    hlsCusEasBasicData.setNumber(item.getSourceNumber());
                    hlsCusEasBasicData.setName(item.getSourceName());
                    hlsCusEasBasicData.setDescription(item.getDescription());
                    hlsCusEasLoginService.easBasicSyn(iRequest, hlsCusEasBasicData, item.getSourceTable(), item.getSourceId());
                }

            }
        }
    }

}
