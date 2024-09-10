package com.hand.hls.prj.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fct.dto.HlsChanceBusinessAccessCompare;
import com.hand.hls.fct.dto.HlsCreditLineAttach;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.fct.mapper.HlsChanceBusinessAccessCompareMapper;
import com.hand.hls.prj.dto.HlsCusPrjBusinessAccessCompare;
import com.hand.hls.prj.service.HlsCusPrjIBusinessAccessCompareService;
import hls.core.utils.exception.HlsCusException;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjBusinessAccessCompareServiceImpl extends BaseServiceImpl<HlsCusPrjBusinessAccessCompare> implements HlsCusPrjIBusinessAccessCompareService {
@Autowired
private HlsChanceBusinessAccessCompareMapper hlsChanceBusinessAccessCompareMapper;
    @Override
    public List<HlsCusPrjBusinessAccessCompare> queryBusinessCompare(IRequest request,HlsCusPrjBusinessAccessCompare dto) throws HlsCusException {

        HlsCusHlsCreditLineChance lineChance = new HlsCusHlsCreditLineChance();
        lineChance.setChanceId(dto.getProgramId());
        List<HlsChanceBusinessAccessCompare> accessCompares = hlsChanceBusinessAccessCompareMapper.queryChanceCompareByProjectChanceId(lineChance);
        if (CollectionUtils.isNotEmpty(accessCompares)) {
            for (HlsChanceBusinessAccessCompare accessCompare : accessCompares) {
                copyPublicFields(request,dto, accessCompare);
            }
        }
        return null;
    }
    @Autowired
     private HlsCusPrjIBusinessAccessCompareService service;
    @Autowired
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(HlsCusPrjBusinessAccessCompareServiceImpl.class);

    private void copyPublicFields(IRequest request, HlsCusPrjBusinessAccessCompare dto, HlsChanceBusinessAccessCompare accessCompare) throws HlsCusException {
        try {
            dto.setProgramId(null);
            // 复制公共属性
            BeanUtils.copyProperties(accessCompare,dto);
            // 保存新创建的对象到数据库
            service.insertSelective(request,dto);
            //self().ins(request,dto);
        } catch (Exception e) {
            logger.error("Failed to copy and save project", e);
            throw new HlsCusException(e.getMessage());
        }
    }

    @Override
    public HlsCusPrjBusinessAccessCompare self() {
        return HlsCusPrjIBusinessAccessCompareService.super.self();
    }
}