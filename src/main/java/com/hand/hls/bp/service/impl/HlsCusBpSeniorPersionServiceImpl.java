package com.hand.hls.bp.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.dto.HlsCusBpSeniorPersion;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.bp.mapper.HlsCusBpSeniorPersionMapper;
import com.hand.hls.bp.service.HlsCusBpSeniorPersionService;
import hls.core.utils.exception.HlsCusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusBpSeniorPersionServiceImpl extends BaseServiceImpl<HlsCusBpSeniorPersion> implements HlsCusBpSeniorPersionService {
    @Autowired
    HlsCusBpSeniorPersionMapper SeniorPersionMapper;

    @Autowired
    HlsCusBpMasterMapper bpMasterMapper;

    @Override
    public List<HlsCusBpSeniorPersion> querySeniorByBpId(HlsCusBpSeniorPersion hlsCusBpSeniorPersion, IRequest requestContext, int page, int pagesize){
        return SeniorPersionMapper.querySeniorByBpId(hlsCusBpSeniorPersion);
    }


    @Override
    public int delectSeniorByBpId(List<HlsCusBpSeniorPersion> dtos) throws HlsCusException {
        for(HlsCusBpSeniorPersion dto : dtos){
            if("5".equalsIgnoreCase(dto.getDocumentPersonType())){
                HlsCusBpMaster bpMaster = bpMasterMapper.selectByPrimaryKey(dto.getBpId());
                if ("Y".equalsIgnoreCase(bpMaster.getSaveFlag())){
                    throw new HlsCusException("法定代表人可以修改，不可以删除！");
                }
            }
        }
        return this.batchDelete(dtos);
    }
}