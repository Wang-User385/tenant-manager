package com.hand.hls.prj.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.HlsCusBpFinancingSituation;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.bp.service.HlsCusIBpFinancingSituationService;
import com.hand.hls.prj.dto.HlsCusPrjBpFinancingSituation;
import com.hand.hls.prj.mapper.HlsCusPrjBpFinancingSituationMapper;
import com.hand.hls.prj.service.HlsCusPrjBpFinancingSituationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjBpFinancingSituationServiceImpl extends BaseServiceImpl<HlsCusPrjBpFinancingSituation> implements HlsCusPrjBpFinancingSituationService {

    @Autowired
    private HlsCusPrjBpFinancingSituationMapper hlsCusPrjBpFinancingSituationMapper;
    @Autowired
    private HlsCusIBpFinancingSituationService hlsCusIBpFinancingSituationService;
    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;


    @Override
    public List<HlsCusPrjBpFinancingSituation> prjBpFinancingSituationInfoQuery(IRequest iRequest, HlsCusPrjBpFinancingSituation hlsCusPrjBpFinancingSituation, int page, int pageSize){
        PageHelper.startPage(page, pageSize);
        List<HlsCusPrjBpFinancingSituation> hlsCusPrjBpFinancingSituationList=new ArrayList<>();
        hlsCusPrjBpFinancingSituationList=hlsCusPrjBpFinancingSituationMapper.prjBpFinancingSituationInfoQuery(hlsCusPrjBpFinancingSituation);
        return hlsCusPrjBpFinancingSituationList;
    }

    @Override
    public List<HlsCusPrjBpFinancingSituation> queryBpFinancingSituationCopy(IRequest iRequest, HlsCusPrjBpFinancingSituation hlsCusPrjBpFinancingSituation, int page, int pageSize){
       pageSize=1000;
       List<HlsCusBpFinancingSituation> hlsCusBpFinancingSituationList=new ArrayList<>();
       List<HlsCusPrjBpFinancingSituation> hlsCusPrjBpFinancingSituationList=new ArrayList<>();
       HlsCusBpFinancingSituation hlsCusBpFinancingSituation=new HlsCusBpFinancingSituation();
        hlsCusBpFinancingSituation.setBpId(hlsCusPrjBpFinancingSituation.getBpId());
       hlsCusBpFinancingSituationList=hlsCusIBpFinancingSituationService.select(iRequest,hlsCusBpFinancingSituation,page,pageSize);
       for(HlsCusBpFinancingSituation dt:hlsCusBpFinancingSituationList) {
           HlsCusPrjBpFinancingSituation hlsCusPrjBpFinancingSituationNew = new HlsCusPrjBpFinancingSituation();
           Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
           hlsBeanRefUtilService.setFieldValue(hlsCusPrjBpFinancingSituationNew, map);
           hlsCusPrjBpFinancingSituationNew.set__status("add");
           hlsCusPrjBpFinancingSituationNew.setBpId(hlsCusPrjBpFinancingSituation.getBpId());
           hlsCusPrjBpFinancingSituationNew.setProjectId(hlsCusPrjBpFinancingSituation.getProjectId());
           hlsCusPrjBpFinancingSituationList.add(hlsCusPrjBpFinancingSituationNew);
       }
       hlsCusPrjBpFinancingSituationList=self().batchUpdate(iRequest,hlsCusPrjBpFinancingSituationList);
       return hlsCusPrjBpFinancingSituationList;
    }


}