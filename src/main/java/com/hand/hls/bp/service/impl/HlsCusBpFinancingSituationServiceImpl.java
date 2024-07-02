package com.hand.hls.bp.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.HlsCusBpFinancingSituation;
import com.hand.hls.bp.mapper.HlsCusBpFinancingSituationMapper;
import com.hand.hls.bp.mapper.HlsCusBpLiabilitiesMapper;
import com.hand.hls.bp.service.HlsCusIBpFinancingSituationService;
import com.hand.hls.fnd.dto.FndInterfaceLines;
import com.hand.hls.fnd.mapper.FndInterfaceLinesMapper;
import com.hand.hls.utils.HlsCusGridExcelImportUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusBpFinancingSituationServiceImpl extends BaseServiceImpl<HlsCusBpFinancingSituation> implements HlsCusIBpFinancingSituationService {

    @Autowired
    private HlsCusBpFinancingSituationMapper mapper;

    @Autowired
    private HlsCusBpLiabilitiesMapper liabilitiesMapper;

    @Autowired
    private FndInterfaceLinesMapper fndInterfaceLinesMapper;

    private static final String[] fileds = new String[]{
            "financingAgency", "variety",
            "exposureAmount", "interestRate", "timeLimit",
            "timeUnit", "expiryDate", "guaranteeMethod", "marginRatio", "note"};

    private static Logger logger = LoggerFactory.getLogger(HlsCusBpFinancingSituationServiceImpl.class);

    @Override
    public List<HlsCusBpFinancingSituation> selectAll(IRequest requestContext, HlsCusBpFinancingSituation bpFinancingSituation, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return mapper.queryAll(bpFinancingSituation);
    }

    @Override
    public void excelImport(IRequest iRequest, Long headerId, Long bpId) {
        //先删除原来的数据
        mapper.deleteFinancingByBpId(bpId);
        FndInterfaceLines interfaceLine = new FndInterfaceLines();
        interfaceLine.setHeaderId(headerId);
        logger.debug("select interfacr list");
        List<FndInterfaceLines> fndInterfaceLinesList = fndInterfaceLinesMapper.select(interfaceLine);
        logger.debug("interface list size " + fndInterfaceLinesList.size());
        List<HlsCusBpFinancingSituation> list = new ArrayList<>();
        List<Map> varietyMaps = liabilitiesMapper.selectSysCodeValue("HLS_BP_MASTER_VARIETY");//syscode的返回map 的key为 value:code对应的value,meaning:code对应的meaning lov同理
        List<Map> limitUnitMaps = liabilitiesMapper.selectSysCodeValue("LIMIT_UNIT");
        List<Map> guaranteeTypeMaps = liabilitiesMapper.selectSysCodeValue("CON.GUARANTEE_TYPE");
        Map<String, List<Map>> map = new HashMap<>();
        map.put("variety", varietyMaps);//map的第一个字段为 对应的属性，第二个字段为这个属性对应的syscode
        map.put("timeUnit", limitUnitMaps);
        map.put("guaranteeMethod", guaranteeTypeMaps);
        try {
            logger.debug("start interface list to object by excel");
            HlsCusGridExcelImportUtil.excelImport(HlsCusBpFinancingSituation.class, list, fndInterfaceLinesList, "bpId", bpId, map);
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            throw new IllegalArgumentException("导入失败,请检查模版数据");
        }
        if (CollectionUtils.isNotEmpty(list)) {
            self().batchUpdate(iRequest, list);
        }
    }
}