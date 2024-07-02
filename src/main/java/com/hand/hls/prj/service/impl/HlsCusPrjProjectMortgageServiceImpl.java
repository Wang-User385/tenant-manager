package com.hand.hls.prj.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.fnd.dto.FndInterfaceLines;
import com.hand.hls.fnd.mapper.FndInterfaceLinesMapper;
import com.hand.hls.prj.dto.HlsCusPrjProjectMortgage;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMortgageMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectMortgageService;
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
public class HlsCusPrjProjectMortgageServiceImpl extends BaseServiceImpl<HlsCusPrjProjectMortgage> implements HlsCusPrjProjectMortgageService {


    @Autowired
    private HlsCusPrjProjectMortgageMapper mapper;
    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;

    @Autowired
    private FndInterfaceLinesMapper fndInterfaceLinesMapper;

    private static final String[] fileds = new String[]{
            "seqNumber","mortgageName","mortgagePersionId","mortgagorImportantNumber",
            "mortgageLocation","mortgageLandUseType","mortgageLandUseTo","mortgageUseTimes",
            "landArea","mortgageLandArea","mortgagePreEvaluationValue","mortgageEvaluationValue",
            "mortgageValue","morgageRate","evaluationBpId","isinsure","description","housingStatus",
            "housingLandType","mortgageRegistrationLimit","mortgageRegistrationNumber"};

    private static Logger logger = LoggerFactory.getLogger(HlsCusPrjProjectMortgageServiceImpl.class);



    @Override
    public void excelImport(IRequest iRequest, Long headerId, Long projectId) {
        //先删除原来的数据
        mapper.deleteMortgageByProjectId(projectId);
        FndInterfaceLines interfaceLine = new FndInterfaceLines();
        interfaceLine.setHeaderId(headerId);
        List<FndInterfaceLines> fndInterfaceLinesList = fndInterfaceLinesMapper.select(interfaceLine);
        List<HlsCusPrjProjectMortgage> list = new ArrayList<>();
        List<Map> mortgageMaps = hlsCusBpMasterMapper.queryBpByBpTypeForExcel("MORTGAGOR");//权利人
        List<Map> evaluatorMaps = hlsCusBpMasterMapper.queryBpByBpTypeForExcel("EVALUATOR");//评估机构
        Map<String, List<Map>> map = new HashMap<>();
        try {
            HlsCusGridExcelImportUtil.excelImport(HlsCusPrjProjectMortgage.class, list, fndInterfaceLinesList, "projectId", projectId, map);
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            throw new IllegalArgumentException("导入失败,请检查模版数据");
        }
        if (CollectionUtils.isNotEmpty(list)) {
            String evaluationName="评估机构:";
            String mortgageName="权利人:";
            boolean dataRightEva=true;
            boolean dataRightMor=true;
            for(HlsCusPrjProjectMortgage dt:list){
                evaluatorMaps.forEach((m) -> {
                    String meaning = (String) m.get("meaning");
                    String value =  m.get("value").toString();
                    if (meaning.equals(dt.getEvaluationBpName())) {
                        dt.setEvaluationBpId(Long.parseLong(value));
                    }
                });
                mortgageMaps.forEach((m) -> {
                    String meaning = (String) m.get("meaning");
                    String value =  m.get("value").toString();
                    if (meaning.equals(dt.getMortgagePersionName())) {
                        dt.setMortgagePersionId(Long.parseLong(value));
                    }
                });
                if(dt.getEvaluationBpId()==null){
                    evaluationName=evaluationName+dt.getEvaluationBpName()+",";
                    dataRightEva=false;
                }
                if(dt.getMortgagePersionId()==null){
                    mortgageName=mortgageName+dt.getMortgagePersionName()+",";
                    dataRightMor=false;
                }
            }
            if((!dataRightEva)&&(!dataRightMor)){
                throw new IllegalArgumentException(evaluationName+","+mortgageName+"不存在");
            }else if((!dataRightEva)&&(dataRightMor)){
                throw new IllegalArgumentException(evaluationName+"不存在");
            }else if((dataRightEva)&&(!dataRightMor)){
                throw new IllegalArgumentException(mortgageName+"不存在");
            }else{
                self().batchUpdate(iRequest, list);
            }
        }
    }

}