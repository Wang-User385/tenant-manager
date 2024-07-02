package com.hand.hap.activiti.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.activiti.components.IHlsCusActivitiBean;
import com.hand.hls.activiti.dto.HlsCusProcess;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.utils.BeanRefUtils;
import com.hand.hls.fct.dto.HlsCreditChanceLeaseItem;
import com.hand.hls.fct.dto.HlsCreditLineChanceMp;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChanceBp;
import com.hand.hls.fct.mapper.HlsCreditChanceLeaseItemMapper;
import com.hand.hls.fct.mapper.HlsCreditLineChanceMpMapper;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceBpMapper;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceMapper;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceService;
import com.hand.hls.fnd.dto.FndCompany;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.hls.dto.HlsDurationHd;
import com.hand.hls.hls.mapper.HlsDurationHdMapper;
import com.hand.hls.hls.mapper.HlsDurationLnMapper;
import com.hand.hls.hls.service.HlsDurationHdService;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationCashflowMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationDetailsMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.*;
import com.hand.hls.sys.dto.FndOrgUnit;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.mapper.FndCompanyMapper;
import com.hand.hls.sys.mapper.FndOrgUnitMapper;
import com.hand.hls.sys.mapper.SysUserMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: quzongkai
 * @date: 2020/6/12
 * @description: 项目审查工作流一键操作
 */
@Component
public class HlsDurationPassActiviti implements IHlsCusActivitiBean {

    private final static String PASS = "PASS";
    private final static String REJECT = "REJECT";
    private static final String REJECTED = "REJECTED";
    private static final String APPROVED = "APPROVED";
    private static final String APPROVING = "APPROVING";
    private static final String NORMAL = "NORMAL";
    private static final String TERMINATE = "TERMINATE";
    private static final String ET = "ET";
    private static final String CON_TERMINATION = "CON_TERMINATION";
    public static final String DURATION_APPROVED = "DURATION_APPROVED";
    public static final String PRESIDENT_APPROVED = "PRESIDENT_APPROVED";
    @Autowired
    private HlsDurationHdService service;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private HlsDurationHdService hdService;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private HlsCusConContractMapper contractMapper;
    @Autowired
    private HlsCusConContractService contractService;
    @Autowired
    private HlsDurationLnMapper hlsDurationLnMapper;
    @Autowired
    private HlsDurationHdMapper mapper;


    @Override
    public void excute(IRequest iRequest, HlsCusProcess hlsCusProcess) {
        String flag = "";
        HlsDurationHd hd = mapper.selectByPrimaryKey(hlsCusProcess.getBussinessKey());
        databaseLockProvider.lock(hd);
        if (PASS.equals(hlsCusProcess.getType())) {
            flag ="APPROVED";
            if("APPROVING".equalsIgnoreCase(hd.getDurationStatus())){
                //如果是所有涉及保证金退款的变更  在保证金管理功能执行
                //如果是正常结清 审批通过直接执行
                if (TERMINATE.equals(hd.getDurationType())) {
                    //解押&&回购
                    hdService.executeLeaseItem(iRequest, hd);
                    //更新合同状态
                    HlsCusConContract conContract = new HlsCusConContract();
                    conContract.setProjectId(hd.getProjectId());
                    conContract.setDataClass(NORMAL);
                    List<HlsCusConContract> contractList = contractMapper.select(conContract);
                    contractList.stream().forEach(item -> {
                        item.setContractStatus(TERMINATE);
                        item.set__status("update");
                    });
                    contractService.batchUpdate(iRequest, contractList);
                }
                //提前结清
                if (ET.equals(hd.getDurationType())) {
                    service.executeEt(iRequest,hd);
                }
                //合同终止
                if (CON_TERMINATION.equals(hd.getDurationType())) {
                    service.executeEnd(iRequest,hd);
                }
                //审批通过则需要将虚拟合同恢复签约状态
                HlsCusPrjProject project = new HlsCusPrjProject();
                project.setProjectId(hd.getProjectId());
                //合同终止则更新状态为END
                if (CON_TERMINATION.equals(hd.getDurationType())) {
                    project.setContractStatus("END");
                }else{
                    project.setContractStatus("SIGN");
                }
                hlsCusPrjProjectService.updateByPrimaryKeySelective(iRequest,project);
            }
        } else if (REJECT.equals(hlsCusProcess.getType())) {
            flag = "REJECTED";
        }
        hd.setDurationStatus(flag);
        hdService.updateByPrimaryKeySelective(iRequest, hd);
    }
}
