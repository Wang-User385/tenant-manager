package com.hand.hls.hls.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.calc.service.HlsCalcExcelImportUtilService;
import com.hand.hls.hls.dto.HlsWebExcel;
import com.hand.hls.hls.dto.HlsWebExcelConfigLn;
import com.hand.hls.hls.mapper.HlsWebExcelConfigHdMapper;
import com.hand.hls.hls.service.IHlsWebExcelConfigLnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.hls.dto.HlsWebExcelConfigHd;
import com.hand.hls.hls.service.IHlsWebExcelConfigHdService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsWebExcelConfigHdServiceImpl extends BaseServiceImpl<HlsWebExcelConfigHd> implements IHlsWebExcelConfigHdService{

    @Autowired
    private HlsWebExcelConfigHdMapper hlsWebExcelConfigHdMapper;

    @Autowired
    private IHlsWebExcelConfigLnService iHlsWebExcelConfigLnService;

    @Autowired
    private HlsCalcExcelImportUtilService hlsCalcExcelImportUtilService;

    @Override
    public List<HlsWebExcelConfigHd> saveWebExcelConfigHdInfo(IRequest iRequest, HlsWebExcelConfigHd calcConfigs) {
        List<HlsWebExcelConfigHd> lists = new ArrayList<>();
        List<HlsWebExcelConfigLn> HlsPriceListConfigLnList = new ArrayList<HlsWebExcelConfigLn>();

        if (calcConfigs.getConfigHdId() == null) {
            self().insert(iRequest, calcConfigs);
        } else {
            self().updateByPrimaryKeySelective(iRequest,calcConfigs);
        }


        if (calcConfigs.getHlsWebExcelConfigLnList() != null) {
            for (int i = 0; i < calcConfigs.getHlsWebExcelConfigLnList().size(); i++) {
                if (calcConfigs.getConfigHdId() != null && calcConfigs.getHlsWebExcelConfigLnList().get(i).getConfigHdId() == null) {
                    calcConfigs.getHlsWebExcelConfigLnList().get(i).setConfigHdId(calcConfigs.getConfigHdId());
                }

                //行表上的code必须是类似aaa这种 || a1
                checkLineCodeFormat(calcConfigs.getHlsWebExcelConfigLnList().get(i).getColumnCode());
                if ("update".equals(calcConfigs.getHlsWebExcelConfigLnList().get(i).get__status())) {
                    iHlsWebExcelConfigLnService.self().updateByPrimaryKey(iRequest, calcConfigs.getHlsWebExcelConfigLnList().get(i));
                } else if ("insert".equals(calcConfigs.getHlsWebExcelConfigLnList().get(i).get__status())) {
                    iHlsWebExcelConfigLnService.self().insertSelective(iRequest, calcConfigs.getHlsWebExcelConfigLnList().get(i));
                }

            }
            //hlsPriceListConfigLnService.batchUpdate(iRequest, calcConfigs.getHlsPriceListConfigLns());
        }
        lists.add(calcConfigs);
        return lists;
    }

    void checkLineCodeFormat(String str) {
        str = str.trim();
        String strNumber = "";
        String StrD = "";
        if (str != null && !"".equals(str)) {
            for (int i = 0; i < str.length(); i++) {
                if (!((str.charAt(i) >= 97 && str.charAt(i) <= 122) || (str.charAt(i) >= 65 && str.charAt(i) <= 90) || (str.charAt(i) >= 48 && str.charAt(i) <= 57))) {
                    throw new IllegalArgumentException("行表表达式" + str + "必须为字母！");
                }

            }
        }

    }
}