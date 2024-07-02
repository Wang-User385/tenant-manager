package com.hand.hls.cap.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cap.dto.HlsCusCapFinancingPlan;
import com.hand.hls.cap.dto.HlsCusCapFinancingPlanLn;
import com.hand.hls.cap.mapper.HlsCusCapFinancingPlanLnMapper;
import com.hand.hls.cap.mapper.HlsCusCapFinancingPlanMapper;
import com.hand.hls.cap.service.HlsCusCapFinancingPlanLnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusCapFinancingPlanLnServiceImpl extends BaseServiceImpl<HlsCusCapFinancingPlanLn> implements HlsCusCapFinancingPlanLnService {
    public static final String CANCEL="CANCEL";
    @Autowired
    private HlsCusCapFinancingPlanLnMapper capFinancingPlanLnMapper;
    @Autowired
    private HlsCusCapFinancingPlanMapper planMapper;
    @Override
    public int updateFinancePlanStatus(Long planId, Long financePlanLineId) {
        return capFinancingPlanLnMapper.updateFinancePlanStatus(planId,financePlanLineId);
    }

    /**
     * @param request 请求,暂时不用
     * @param dto     需要判断的IDS
     * @return 每个ID是否存在
     */
    @Override
    public Map<Long, Integer> haveRelevanced(IRequest request, List<HlsCusCapFinancingPlanLn> dto) {
        Map<Long,Integer> map=new HashMap<>(dto.size());
        dto.forEach(planLn-> map.put(planLn.getFinancePlanLineId(),capFinancingPlanLnMapper.getCountNo(planLn)));
        return map;
    }

    /**
     * 作废单子
     *
     * @param dto
     * @param request
     * @return
     */
    @Override
    public ResponseData cancelPlan(HlsCusCapFinancingPlan dto, HttpServletRequest request) {
        ResponseData res=new ResponseData();
        Assert.notNull(dto,"参数错误 ,不能为空");
        Assert.notNull(dto.getPlanId(),"参数错误 ,不能为空");
        dto.setConfirmStatus(CANCEL);
        planMapper.updateByPrimaryKeySelective(dto);
        return res;
    }
}
