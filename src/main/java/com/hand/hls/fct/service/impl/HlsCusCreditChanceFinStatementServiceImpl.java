package com.hand.hls.fct.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fct.dto.HlsCusCreditChanceFinStatement;
import com.hand.hls.fct.service.HlsCusCreditChanceFinStatementService;
import com.hand.hls.fct.mapper.HlsCusCreditChanceFinStatementMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusCreditChanceFinStatementServiceImpl extends BaseServiceImpl<HlsCusCreditChanceFinStatement> implements HlsCusCreditChanceFinStatementService {

    @Autowired
    private HlsCusCreditChanceFinStatementMapper mapper;
    @Override
    public List<HlsCusCreditChanceFinStatement> selectByChanceId(IRequest requestContext, HlsCusCreditChanceFinStatement dto, int pagenum, int pagesize) {
        List<HlsCusCreditChanceFinStatement> list = mapper.selectByChanceId(dto);
        HlsCusCreditChanceFinStatement hlsCusCreditChanceFinStatement = new HlsCusCreditChanceFinStatement();
        hlsCusCreditChanceFinStatement.setBpId(dto.getBpId());
        if (CollectionUtils.isEmpty(list)) {
            list.add(hlsCusCreditChanceFinStatement);
        }
        return list;
    }
}
