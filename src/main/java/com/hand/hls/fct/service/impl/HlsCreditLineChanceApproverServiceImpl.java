package com.hand.hls.fct.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fct.mapper.HlsCreditLineChanceApproverMapper;
import com.hand.hls.fnd.dto.PrjMeetingJudge;
import com.hand.hls.fnd.mapper.PrjMeetingJudgeMapper;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.mapper.SysUserMapper;
import com.hand.hls.utils.ResMessageException;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.fct.dto.HlsCreditLineChanceApprover;
import com.hand.hls.fct.service.HlsICreditLineChanceApproverService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCreditLineChanceApproverServiceImpl extends BaseServiceImpl<HlsCreditLineChanceApprover> implements HlsICreditLineChanceApproverService {
    @Autowired
    private HlsCreditLineChanceApproverMapper hlsCreditLineChanceApproverMapper;

    @Autowired
    private PrjMeetingJudgeMapper prjMeetingJudgeMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Override
    public void saveVote(IRequest requestCtx, HlsCreditLineChanceApprover approver) throws ResMessageException {
        PrjMeetingJudge prjMeetingJudge = new PrjMeetingJudge();
        prjMeetingJudge.setEnabledFlag("Y");
        List<PrjMeetingJudge> judgeList = prjMeetingJudgeMapper.select(prjMeetingJudge);
        List<SysUser> users = sysUserMapper.findAllocationIdByUserID(requestCtx.getUserId());
        List<HlsCreditLineChanceApprover> voteInfo = hlsCreditLineChanceApproverMapper.findVoteInfo(approver);
        if (!CollectionUtils.isEmpty(users)) {
            approver.setAllocationId(users.get(0).getAllocationId());
        }
        //防止重复提交
        for (HlsCreditLineChanceApprover chanceApprover : voteInfo) {
            if (Objects.equals(chanceApprover.getAllocationId(), approver.getAllocationId())) {
                throw new ResMessageException("提示", "请不要重复提交");
            }
        }
        if (!CollectionUtils.isEmpty(judgeList)) {
            //当所有人已经投票但是还是走了这函数说明退回重新开始投票
            if (judgeList.size() == voteInfo.size()) {
                batchDelete(voteInfo);
            }
            self().insert(requestCtx, approver);
        }
    }
}