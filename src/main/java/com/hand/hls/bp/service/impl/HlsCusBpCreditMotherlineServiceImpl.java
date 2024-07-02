package com.hand.hls.bp.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.BpCreditMotherline;
import com.hand.hls.bp.mapper.BpCreditMotherlineMapper;
import com.hand.hls.bp.service.HlsCusBpCreditMotherlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 胡兴恒
 * @Time 2020-04-30 14:44
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusBpCreditMotherlineServiceImpl extends BaseServiceImpl<BpCreditMotherline> implements HlsCusBpCreditMotherlineService {

    @Autowired
    private BpCreditMotherlineMapper bpCreditMotherlineMapper;

    @Override
    public List<BpCreditMotherline> selectCreditMotherlineByBpId(BpCreditMotherline bpCreditMotherline, IRequest requestContext, int page, int pagesize) {
        return bpCreditMotherlineMapper.queryAll(bpCreditMotherline);
    }
}
