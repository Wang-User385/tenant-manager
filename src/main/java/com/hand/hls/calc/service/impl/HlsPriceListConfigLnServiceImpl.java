package com.hand.hls.calc.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.calc.dto.HlsPriceListConfigLn;
import com.hand.hls.calc.mapper.HlsPriceListConfigLnMapper;
import com.hand.hls.calc.service.HlsCalcExcelImportUtilService;
import com.hand.hls.calc.service.HlsPriceListConfigLnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class HlsPriceListConfigLnServiceImpl extends BaseServiceImpl<HlsPriceListConfigLn> implements HlsPriceListConfigLnService {

    @Autowired
    private HlsPriceListConfigLnMapper mapper;
    @Autowired
    private HlsCalcExcelImportUtilService hlsCalcExcelImportUtilService;

    /* (non-Javadoc)
     * @see com.hand.hls.calc.service.HlsPriceListConfigLnService#selectAllHlsPriceListConfigLn()
     */
    @Override
    public List<HlsPriceListConfigLn> selectAllHlsPriceListConfigLn(HlsPriceListConfigLn configLn) {
        return mapper.selectAllHlsPriceListConfigLn(configLn);
    }

    @Override
    public List<HlsPriceListConfigLn> selectHlsPriceListConfiglineByPriceList(HlsPriceListConfigLn configLn, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return mapper.selectHlsPriceListConfiglineByPriceList(configLn);
    }

    @Override
    public List<HlsPriceListConfigLn> selectHlsPriceListConfiglineByHdId(HlsPriceListConfigLn configLn, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return mapper.selectHlsPriceListConfiglineByHdId(configLn);
    }

    @Override
    public List<HlsPriceListConfigLn> selectHlsPriceListConfiglineHide(HlsPriceListConfigLn configLn) {
        List<HlsPriceListConfigLn> hlsPriceListConfigLnList = mapper.selectHlsPriceListConfiglineByPriceList(configLn);
        for (int i = 0; i < hlsPriceListConfigLnList.size(); i++) {
            String columnCode = hlsPriceListConfigLnList.get(i).getColumnCode() == null ? "" : hlsPriceListConfigLnList.get(i).getColumnCode().toUpperCase();
            String hideColumnFlag = hlsPriceListConfigLnList.get(i).getHideColumnFlag() == null ? "" : hlsPriceListConfigLnList.get(i).getHideColumnFlag().toUpperCase();
            if ("Y".equals(hideColumnFlag) && "Y".equals(configLn.getHideColumnFlag())) {
                int CellsIndex = hlsCalcExcelImportUtilService.excelColStrToNum(columnCode);
                hlsPriceListConfigLnList.get(i).setHideColumnIndex(CellsIndex - 1);
            }
            String hideRowFlag = hlsPriceListConfigLnList.get(i).getHideRowFlag() == null ? "" : hlsPriceListConfigLnList.get(i).getHideRowFlag().toUpperCase();
            if ("Y".equals(hideRowFlag) && "Y".equals(configLn.getHideRowFlag())) {
                int rowIndex = Integer.parseInt(hlsCalcExcelImportUtilService.getNumberFromExcelStr(columnCode, "N"));
                hlsPriceListConfigLnList.get(i).setHideRowIndex(rowIndex - 1);
            }
        }
        return hlsPriceListConfigLnList;
    }

    @Override
    public List<HlsPriceListConfigLn> selectTargetColumnCode(HlsPriceListConfigLn var1) {
        return this.mapper.targetColumnCode(var1);
    }
}
