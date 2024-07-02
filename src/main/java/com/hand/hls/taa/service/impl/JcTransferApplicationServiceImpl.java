package com.hand.hls.taa.service.impl;

import com.alibaba.fastjson.JSON;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.sys.mapper.FndCompanyMapper;
import com.hand.hls.taa.dto.JcTransferApplicationDetal;
import com.hand.hls.taa.mapper.JcTransferApplicationMapper;

import com.hand.hls.taa.service.IJcTransferApplicationDetalService;
import com.hand.hls.utils.ResMessageException;
import com.hand.hls.wfl.service.IActivitiCommonService;
import com.hand.hls.wfl.service.IActivitiStartService;
import leaf.service.validation.ParameterNullException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.taa.dto.JcTransferApplication;
import com.hand.hls.taa.service.IJcTransferApplicationService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class JcTransferApplicationServiceImpl extends BaseServiceImpl<JcTransferApplication> implements IJcTransferApplicationService{
    private static final String APPROVED = "APPROVED";
    private static final String APPROVING = "APPROVING";

    @Autowired
    private IActivitiStartService activitiStartService;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private JcTransferApplicationMapper jcTransferApplicationMapper;
    @Autowired
    private IJcTransferApplicationService jcTransferApplicationService;

    @Autowired
    private IJcTransferApplicationDetalService jcTransferApplicationDetalService;


    @Autowired
    FndCompanyMapper fndCompanyMapper;

    private void approveWfl(IRequest iRequest, JcTransferApplication jcTransferApplication) throws ResMessageException {
        jcTransferApplication = jcTransferApplicationMapper.selectByPrimaryKey(jcTransferApplication);
        if (APPROVED.equals(jcTransferApplication.getStatus()) || APPROVING.equals(jcTransferApplication.getStatus())) {
            throw new ResMessageException("当前单据状态不能提交申请！");
        }
        databaseLockProvider.lock(jcTransferApplication);

        List<JcTransferApplication> jcTransferApplicationList = new ArrayList<>();
        jcTransferApplication = jcTransferApplicationService.selectByPrimaryKey(iRequest, jcTransferApplication);
        jcTransferApplicationList.add(jcTransferApplication);

        Map<String, Object> params = new HashMap<>();
        params.put("workFlowType", "TRANSFER_APPLICATION_WFL");
        params.put("transferApplicationId", jcTransferApplication.getTransferApplicationId());
        params.put("documentName", "转账申请审批流程"+jcTransferApplication.getTransferApplicationNumber());
        params.put("documentNumber", jcTransferApplication.getTransferApplicationNumber());

        params.put(IActivitiCommonService.WORK_FLOW_NAME, "TRANSFER_APPLICATION_WFL");
        params.put(IActivitiCommonService.DEMO_NAME, "TRANSFER_APPLICATION_WFL");
        params.put(IActivitiCommonService.BUSINESS_KEY, jcTransferApplication.getTransferApplicationId());
        params.put("documentCategory", "TRANSFER_APPLICATION_WFL"); //
        params.put("jcTransferApplication", JSON.toJSONString(jcTransferApplication));
        params.put("startUserName", iRequest.getUserName());

        JcTransferApplicationDetal jcTransferApplicationDetal = new JcTransferApplicationDetal();
        jcTransferApplicationDetal.setTransferApplicationId(jcTransferApplication.getTransferApplicationId());
        //统计转账金额
        Double totalAmount = 0.0;
        List<JcTransferApplicationDetal> jcTransferApplicationDetalList = jcTransferApplicationDetalService.selectSelective(iRequest,jcTransferApplicationDetal);
        for(JcTransferApplicationDetal jcTransferApplicationDetal1:jcTransferApplicationDetalList){
            totalAmount += jcTransferApplicationDetal1.getTransferAccounts();
        }
        params.put("totalAmount", totalAmount);
        activitiStartService.start(iRequest, jcTransferApplicationList, params);

        JcTransferApplication jcTransferApplication1 = new JcTransferApplication();
        jcTransferApplication1.setTransferApplicationId(jcTransferApplication.getTransferApplicationId());
        jcTransferApplication1.setStatus(APPROVING);
        jcTransferApplicationService.updateByPrimaryKeySelective(iRequest, jcTransferApplication1);

    }

    @Override
    public List<JcTransferApplication> conInceptSubmit(IRequest iRequest, JcTransferApplication jcTransferApplication) throws ResMessageException, ParameterNullException {


        //启动工作流
        approveWfl(iRequest, jcTransferApplication);

        List<JcTransferApplication> contractInsure = new ArrayList<>();
        contractInsure.add(jcTransferApplication);
        return contractInsure;
    }
}