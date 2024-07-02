package com.hand.hls.check.components;

import com.hand.hls.prj.dto.HlsCusPrjProjectBp;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.mapper.HlsCusPrjProjectBpMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectBpService;
import com.hand.hls.prj.service.HlsCusPrjQuotationCashflowService;
import com.hand.hls.prj.service.HlsCusPrjQuotationService;
import com.hand.hls.utils.SpringContextHolder;
import leaf.database.service.IDatabaseServiceFactory;
import org.springframework.stereotype.Component;
import uncertain.composite.CompositeMap;

import java.util.List;

@Component
public class QuotationCheck {

    private static String RECORD = "record";
    private static String PROJECT_TAB_CODE = "PROJECT_MODIFY_FINANCE_LEASE_F_BASIC_INFO_prj_project";
    private static String PROJECT_ID = "project_id";
    private static String QUOTATION_ID = "quotation_id";
    private static String PRJ_PROJECT = "PRJ_PROJECT";

    public QuotationCheck(){

    }

    public void saveQuotationDocument(CompositeMap qMap, IDatabaseServiceFactory factory, CompositeMap root){
        if(root != null && root.getChild(RECORD) != null && root.getChild(RECORD).getChild(PROJECT_TAB_CODE) != null){
            CompositeMap map = root.getChild(RECORD).getChild(PROJECT_TAB_CODE);
            if(map.getChilds() != null && map.getChilds().size() > 0){
                if(map.getChilds().get(0).get(PROJECT_ID) != null){
                    Long projectId = Long.valueOf(map.getChilds().get(0).get(PROJECT_ID).toString());

                    //更新项目id到项目表
                    Long quotationId = Long.valueOf(qMap.get(QUOTATION_ID).toString());

                    HlsCusPrjQuotationMapper hlsCusPrjQuotationMapper = SpringContextHolder.getBean(HlsCusPrjQuotationMapper.class);
                    HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
                    hlsCusPrjQuotation.setQuotationId(quotationId);
                    hlsCusPrjQuotation = hlsCusPrjQuotationMapper.selectByPrimaryKey(hlsCusPrjQuotation);

                    if(hlsCusPrjQuotation.getSourceDocumentId() == null) {
                        hlsCusPrjQuotation.setSourceDocumentId(projectId);
                        hlsCusPrjQuotation.setSourceDocumentCategory(PRJ_PROJECT);
                        hlsCusPrjQuotationMapper.updateByPrimaryKeySelective(hlsCusPrjQuotation);
                    }
                }
            }
        }
    }

    public void afterSave(CompositeMap qMap, IDatabaseServiceFactory factory, CompositeMap root){
        if(root != null && root.getChild(RECORD) != null && root.getChild(RECORD).getChild("CONT301F1_F_BASIC_INFO_prj_project") != null){
            CompositeMap map = root.getChild(RECORD).getChild("CONT301F1_F_BASIC_INFO_prj_project");
            if(map.getChilds() != null && map.getChilds().size() > 0){
                if(map.getChilds().get(0).get(PROJECT_ID) != null){
                    Long quotationId = Long.valueOf(qMap.get(QUOTATION_ID).toString());
                    HlsCusPrjQuotation prjQuotation = new HlsCusPrjQuotation();
                    prjQuotation.setQuotationId(quotationId);
                    HlsCusPrjQuotationService hlsCusPrjQuotationService = SpringContextHolder.getBean(HlsCusPrjQuotationService.class);
                    HlsCusPrjQuotationCashflowService hlsCusPrjQuotationCashflowService = SpringContextHolder.getBean(HlsCusPrjQuotationCashflowService.class);
                    HlsCusPrjProjectBpMapper hlsCusPrjProjectBpMapper = SpringContextHolder.getBean(HlsCusPrjProjectBpMapper.class);
                    HlsCusPrjProjectBpService hlsCusPrjProjectBpService = SpringContextHolder.getBean(HlsCusPrjProjectBpService.class);
                    
                    
                    prjQuotation = hlsCusPrjQuotationService.selectByPrimaryKey(null, prjQuotation);
                    //现金流拆税
                    hlsCusPrjQuotationCashflowService.taxCashflowDemolition(null, prjQuotation);
                    Long projectId = prjQuotation.getSourceDocumentId();
                    //生成流水号
                    HlsCusPrjProjectBp t1 = new HlsCusPrjProjectBp();
                    t1.setProjectId(projectId);
                    t1.setBpRoleType("WARRANTOR");
                    List<HlsCusPrjProjectBp> hlsCusPrjProjectGuaranteeList = hlsCusPrjProjectBpMapper.prjProjectBpQuery(t1);
                    hlsCusPrjProjectBpService.createSerialNumber(null, projectId, hlsCusPrjProjectGuaranteeList, "WARRANTOR");
                    for (HlsCusPrjProjectBp hlsCusPrjProjectBp : hlsCusPrjProjectGuaranteeList) {
                        hlsCusPrjProjectBpService.updateByPrimaryKey(null, hlsCusPrjProjectBp);
                    }
                    t1.setBpRoleType("MORTGAGOR");
                    List<HlsCusPrjProjectBp> hlsCusPrjProjectGuaranteeList2 = hlsCusPrjProjectBpMapper.prjProjectBpQuery(t1);
                    hlsCusPrjProjectBpService.createSerialNumber(null, projectId, hlsCusPrjProjectGuaranteeList2, "MORTGAGOR");
                    for (HlsCusPrjProjectBp hlsCusPrjProjectBp : hlsCusPrjProjectGuaranteeList2) {
                        hlsCusPrjProjectBpService.updateByPrimaryKey(null, hlsCusPrjProjectBp);
                    }
                    t1.setBpRoleType("PLEDGOR");
                    List<HlsCusPrjProjectBp> hlsCusPrjProjectGuaranteeList3 = hlsCusPrjProjectBpMapper.prjProjectBpQuery(t1);
                    hlsCusPrjProjectBpService.createSerialNumber(null, projectId, hlsCusPrjProjectGuaranteeList3, "PLEDGOR");
                    for (HlsCusPrjProjectBp hlsCusPrjProjectBp : hlsCusPrjProjectGuaranteeList3) {
                        hlsCusPrjProjectBpService.updateByPrimaryKey(null, hlsCusPrjProjectBp);
                    }
                }
            }
        }
    }



}
