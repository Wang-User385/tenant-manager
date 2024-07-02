package com.hand.hls.check.components;

import com.hand.hls.fin.dto.HlsCusCreditContractLine;
import com.hand.hls.fin.mapper.HlsCusCreditContractLineMapper;
import com.hand.hls.fin.service.HlsCusCreditContractLineService;
import com.hand.hls.fin.service.impl.HlsCusCreditContractLineServiceImpl;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.utils.SpringContextHolder;
import leaf.database.service.IDatabaseServiceFactory;
import org.springframework.stereotype.Component;
import uncertain.composite.CompositeMap;

import java.util.List;

@Component
public class CreditHandler {

    private static String RECORD = "record";
    private static String PROJECT_TAB_CODE = "PROJECT_MODIFY_FINANCE_LEASE_F_BASIC_INFO_prj_project";
    private static String PROJECT_ID = "project_id";
    private static String QUOTATION_ID = "quotation_id";
    private static String PRJ_PROJECT = "PRJ_PROJECT";

    public CreditHandler(){
    }

    /**
     * 授信页面保存回调用】
     * @param qMap
     * @param factory
     * @param root
     */
    public void saveRent(CompositeMap qMap, IDatabaseServiceFactory factory, CompositeMap root){
        HlsCusCreditContractLineMapper hlsCusCreditContractLineMapper = SpringContextHolder.getBean(HlsCusCreditContractLineMapper.class);
        HlsCusCreditContractLine t = new HlsCusCreditContractLine();
        Long creditContractId = qMap.getLong("credit_contract_id");
        t.setCreditContractId(creditContractId);
        List<HlsCusCreditContractLine> list = hlsCusCreditContractLineMapper.select(t);
        System.out.println(list);
    }





}
