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
public class ContractHandler {

    private static String RECORD = "record";
    private static String PROJECT_TAB_CODE = "PROJECT_MODIFY_FINANCE_LEASE_F_BASIC_INFO_prj_project";
    private static String PROJECT_ID = "project_id";
    private static String QUOTATION_ID = "quotation_id";
    private static String PRJ_PROJECT = "PRJ_PROJECT";

    public ContractHandler(){

    }

    /**
     * 起租页面保存回调用】
     * @param qMap
     * @param factory
     * @param root
     */
    public void saveRent(CompositeMap qMap, IDatabaseServiceFactory factory, CompositeMap root){
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





}
