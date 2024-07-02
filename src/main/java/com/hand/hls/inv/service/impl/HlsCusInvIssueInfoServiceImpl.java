package com.hand.hls.inv.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.inv.dto.HlsCusInvIssueInfo;
import com.hand.hls.inv.mapper.HlsCusInvIssueInfoMapper;
import com.hand.hls.inv.service.HlsCusInvIssueInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusInvIssueInfoServiceImpl extends BaseServiceImpl<HlsCusInvIssueInfo> implements HlsCusInvIssueInfoService {

    @Autowired
    private HlsCusInvIssueInfoMapper hlsCusInvIssueInfoMapper;


    @Override
    public List<HlsCusInvIssueInfo> issueInfoSave(IRequest iRequest, List<HlsCusInvIssueInfo> hlsCusInvIssueInfos) {
        for(HlsCusInvIssueInfo dt:hlsCusInvIssueInfos){
            if(dt.getIssueInfoId()!=null && dt.getIssueInfoId()!=0){
                dt=self().updateByPrimaryKeySelective(iRequest,dt);
            }else{
                dt.set__status("add");
                dt=self().insertSelective(iRequest,dt);
            }
        }
        return hlsCusInvIssueInfos;
    }
}