package com.hand.hap.activiti.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.activiti.components.IHlsCusActivitiBean;
import com.hand.hls.activiti.dto.HlsCusProcess;
import com.hand.hls.hn.dto.PrjCheck;
import com.hand.hls.hn.mapper.PrjCheckMapper;
import com.hand.hls.hn.service.IPrjCheckService;
import com.hand.hls.pam.dto.AssetsDisposal;
import com.hand.hls.pam.dto.AssetsDisposalDetail;
import com.hand.hls.pam.mapper.AssetsDisposalDetailMapper;
import com.hand.hls.pam.mapper.AssetsDisposalMapper;
import com.hand.hls.pam.service.IAssetsDisposalDetailService;
import com.hand.hls.pam.service.IAssetsDisposalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: quzongkai
 * @date: 2020/6/12
 * @description: 项目审查工作流一键操作
 */
@Component
public class AssetsDisposalApprovalPassActiviti implements IHlsCusActivitiBean {

    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Autowired
    private AssetsDisposalMapper assetsDisposalMapper;
    @Autowired
    private AssetsDisposalDetailMapper assetsDisposalDetailMapper;
    @Autowired
    private IAssetsDisposalService assetsDisposalService;
    @Autowired
    private IAssetsDisposalDetailService assetsDisposalDetailService;

    private final static String PASS = "PASS";
    private final static String REJECT = "REJECT";

    @Override
    public void excute(IRequest iRequest, HlsCusProcess hlsCusProcess) {

        AssetsDisposal assetsDisposal = assetsDisposalMapper.selectByPrimaryKey(hlsCusProcess.getBussinessKey());
        databaseLockProvider.lock(assetsDisposal);

        if (PASS.equals(hlsCusProcess.getType())) {
            assetsDisposal.setApprovalStatus("APPROVED");
            AssetsDisposalDetail assetsDisposalDetail = new AssetsDisposalDetail();
            assetsDisposalDetail.setAssetsDisposalId(assetsDisposal.getAssetsDisposalId());
            List<AssetsDisposalDetail> assetsDisposalDetailList = assetsDisposalDetailMapper.select(assetsDisposalDetail);
            for(AssetsDisposalDetail assetsDisposalDetail1:assetsDisposalDetailList){
                //ASSETS_DISPOSAL_STAUTS
                assetsDisposalDetail1.setAssetsDisposalStauts("DISPOSED");
                assetsDisposalDetailService.updateByPrimaryKeySelective(iRequest,assetsDisposalDetail1);
            }
        } else if (REJECT.equals(hlsCusProcess.getType())) {
            assetsDisposal.setApprovalStatus("REJECTED");

        }
        assetsDisposalService.updateByPrimaryKeySelective(iRequest, assetsDisposal);
    }
}
