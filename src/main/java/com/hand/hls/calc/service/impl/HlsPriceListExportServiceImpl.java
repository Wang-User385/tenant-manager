package com.hand.hls.calc.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.script.dto.LeafExportScript;
import com.hand.hap.script.utils.ExportUtils;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.calc.dto.HlsCalcConfig;
import com.hand.hls.calc.dto.HlsPriceListConfigBT;
import com.hand.hls.calc.dto.HlsPriceListConfigHd;
import com.hand.hls.calc.dto.HlsPriceListConfigLn;
import com.hand.hls.calc.dto.export.HlsPriceListConfigBTRow;
import com.hand.hls.calc.dto.export.HlsPriceListConfigHdRow;
import com.hand.hls.calc.dto.export.HlsPriceListConfigLnRow;
import com.hand.hls.calc.dto.export.HlsPriceListRow;
import com.hand.hls.calc.mapper.*;
import com.hand.hls.calc.service.IHlsPriceListExportService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 价目表定义导出
 * </p>
 *
 * @author qiang.chen04@hand-china.com 2019/06/21 13:50
 */
@Service
@Transactional
public class HlsPriceListExportServiceImpl implements IHlsPriceListExportService {

    private static final Logger logger = LoggerFactory.getLogger(HlsPriceListExportServiceImpl.class);

    @Autowired
    private HlsPriceListExportMapper hlsPriceListExportMapper;

    @Autowired
    private HlsCalcConfigMapper hlsCalcConfigMapper;

    @Autowired
    private HlsPriceListConfigBTMapper hlsPriceListConfigBTMapper;

    @Autowired
    private HlsPriceListConfigHdMapper hlsPriceListConfigHdMapper;

    @Autowired
    private HlsPriceListConfigLnMapper hlsPriceListConfigLnMapper;

    @Override
    public byte[] export(IRequest requestCtx, List<String> idlist) {
        LeafExportScript leafExportScript = new LeafExportScript(LeafExportScript.SCRIPT_HLS_PRICE_LIST);
        List<HlsPriceListRow> rows = new ArrayList<>();
        for (String priceList : idlist) {
            HlsCalcConfig hlsCalcConfig = hlsCalcConfigMapper.selectByPrimaryKey(priceList);
            if (hlsCalcConfig == null) {
                continue;
            }
            HlsPriceListRow hlsPriceListRow = ExportUtils.copyObj(hlsCalcConfig, HlsPriceListRow.class);
            hlsPriceListRow.setHlsPriceListConfigBTRows(assembleHlsPriceListConfigBTRows(requestCtx, hlsPriceListRow.getPriceList()));
            hlsPriceListRow.setHlsPriceListConfigHdRows(assembleHlsPriceListConfigHdRows(requestCtx, hlsPriceListRow.getPriceList()));
            rows.add(hlsPriceListRow);
        }
        leafExportScript.setRows(rows);
        try {
            return JSON.toJSONString(leafExportScript).getBytes(StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }
        return null;
    }


    public List<HlsPriceListConfigBTRow> assembleHlsPriceListConfigBTRows(IRequest requestCtx, String priceList) {
        if (StringUtils.isEmpty(priceList)) {
            logger.warn("attribute [priceList] is null when assembling HlsPriceListConfigBTRow, ignore and return null ");
            return null;
        }

        HlsPriceListConfigBT hlsPriceListConfigBT = new HlsPriceListConfigBT();
        hlsPriceListConfigBT.setPriceList(priceList);
        List<HlsPriceListConfigBT> select = hlsPriceListConfigBTMapper.select(hlsPriceListConfigBT);
        return ExportUtils.copyList(select, HlsPriceListConfigBTRow.class);
    }

    @Override
    public List<HlsPriceListConfigHdRow> assembleHlsPriceListConfigHdRows(IRequest requestCtx, String priceList) {
        if (StringUtils.isEmpty(priceList)) {
            logger.warn("attribute [priceList] is null when assembling HlsPriceListConfigHdRow, ignore and return null ");
            return null;
        }
        HlsPriceListConfigHd param = new HlsPriceListConfigHd();
        param.setPriceList(priceList);
        List<HlsPriceListConfigHd> configHds = hlsPriceListConfigHdMapper.select(param);
        List<HlsPriceListConfigHdRow> hlsPriceListConfigHdRows = ExportUtils.copyList(configHds, HlsPriceListConfigHdRow.class);
        if (CollectionUtils.isEmpty(hlsPriceListConfigHdRows)) {
            return hlsPriceListConfigHdRows;
        }
        for (HlsPriceListConfigHdRow hlsPriceListConfigHdRow : hlsPriceListConfigHdRows) {
            if (hlsPriceListConfigHdRow != null && hlsPriceListConfigHdRow.getConfigHdId() != null) {
                hlsPriceListConfigHdRow.setHlsPriceListConfigLnRows(assembleHlsPriceListConfigLnRows(requestCtx, hlsPriceListConfigHdRow));
            }
        }
        return hlsPriceListConfigHdRows;
    }

    @Override
    public List<HlsPriceListConfigLnRow> assembleHlsPriceListConfigLnRows(IRequest requestCtx, HlsPriceListConfigHdRow hlsPriceListConfigHdRow) {
        if (hlsPriceListConfigHdRow == null || hlsPriceListConfigHdRow.getConfigHdId() == null) {
            return null;
        }
        Long configHdId = hlsPriceListConfigHdRow.getConfigHdId();
        HlsPriceListConfigLn configLnParam = new HlsPriceListConfigLn();
        configLnParam.setConfigHdId(configHdId);
        List<HlsPriceListConfigLn> configLns = hlsPriceListConfigLnMapper.select(configLnParam);
        return ExportUtils.copyList(configLns, HlsPriceListConfigLnRow.class);
    }


    public ResponseData importFile(IRequest requestCtx, String json) {
        ResponseData responseData = new ResponseData(true);
        LeafExportScript leafExportScript = JSON.parseObject(json, LeafExportScript.class);
        if (leafExportScript == null
                || !LeafExportScript.SCRIPT_LEAF_EXPORT_SCRIPT.equals(leafExportScript.getType())
                || !LeafExportScript.SCRIPT_HLS_PRICE_LIST.equals(leafExportScript.getName())
                || CollectionUtils.isEmpty(leafExportScript.getRows())) {
            responseData.setMessage("文件不能为空,或者导入的数据与功能不匹配");
            responseData.setSuccess(false);
            return responseData;
        }
        List<?> rows = leafExportScript.getRows();
        for (Object row : rows) {
            JSONObject jsonObject = (JSONObject) row;
            HlsPriceListRow hlsPriceListRow = jsonObject.toJavaObject(HlsPriceListRow.class);
            if (hlsPriceListRow == null) {
                continue;
            }
            HlsCalcConfig exsitParam = new HlsCalcConfig();
            exsitParam.setPriceList(hlsPriceListRow.getPriceList());
            HlsCalcConfig exist = hlsCalcConfigMapper.selectByPrimaryKey(exsitParam);
            HlsCalcConfig hlsCalcConfig = ExportUtils.copyObj(hlsPriceListRow, HlsCalcConfig.class);
            if (hlsCalcConfig == null) {
                continue;
            }
            if (exist == null) {
                hlsCalcConfigMapper.insert(hlsCalcConfig);
            } else {
                hlsCalcConfigMapper.updateByPrimaryKeySelective(hlsCalcConfig);
            }
            insertHlsPriceListConfigBTRows(requestCtx, hlsPriceListRow);
            insertHlsPriceListConfigHdRows(requestCtx, hlsPriceListRow);
        }

        return responseData;
    }

    private void insertHlsPriceListConfigHdRows(IRequest requestCtx, HlsPriceListRow hlsPriceListRow) {
        if (hlsPriceListRow == null || hlsPriceListRow.getHlsPriceListConfigHdRows() == null) {
            return;
        }
        List<HlsPriceListConfigHdRow> hlsPriceListConfigHdRows = hlsPriceListRow.getHlsPriceListConfigHdRows();
        insertHlsPriceListConfigHdRows(requestCtx, hlsPriceListConfigHdRows, hlsPriceListRow.getPriceList());

    }

    public void insertHlsPriceListConfigHdRows(IRequest requestCtx, List<HlsPriceListConfigHdRow> priceListConfigHdRows, String priceList) {
        if (CollectionUtils.isEmpty(priceListConfigHdRows)) {
            return;
        }

        //存在更新,不存在插入
        HlsPriceListConfigHd param = new HlsPriceListConfigHd();
        param.setPriceList(priceList);
        List<HlsPriceListConfigHd> configHds = hlsPriceListConfigHdMapper.select(param);

        if (CollectionUtils.isNotEmpty(configHds)) {
            for (HlsPriceListConfigHd configHd : configHds) {
                //删除行
                HlsPriceListConfigLn delParam = new HlsPriceListConfigLn();
                delParam.setConfigHdId(configHd.getConfigHdId());
                hlsPriceListConfigLnMapper.delete(delParam);
                //删除头
                hlsPriceListConfigHdMapper.deleteByPrimaryKey(configHd);
            }
        }

        for (HlsPriceListConfigHdRow row : priceListConfigHdRows) {
            HlsPriceListConfigHd insert = ExportUtils.copyObj(row, HlsPriceListConfigHd.class);
            if (insert == null) {
                continue;
            }
            insert.setPriceList(priceList);
            insert.setConfigHdId(null);
            hlsPriceListConfigHdMapper.insert(insert);
            //插入行表
            List<HlsPriceListConfigLnRow> hlsPriceListConfigLnRows = row.getHlsPriceListConfigLnRows();
            if (CollectionUtils.isEmpty(hlsPriceListConfigLnRows)) {
                continue;
            }
            List<HlsPriceListConfigLn> configLnList = ExportUtils.copyList(hlsPriceListConfigLnRows, HlsPriceListConfigLn.class);
            if (CollectionUtils.isEmpty(configLnList)) {
                continue;
            }
            for (HlsPriceListConfigLn configLnRow : configLnList) {
                configLnRow.setConfigHdId(insert.getConfigHdId());
                configLnRow.setConfigLnId(null);
                hlsPriceListConfigLnMapper.insert(configLnRow);

            }

        }
    }


    private void insertHlsPriceListConfigBTRows(IRequest requestCtx, HlsPriceListRow hlsPriceListRow) {
        if (hlsPriceListRow == null || hlsPriceListRow.getHlsPriceListConfigBTRows() == null) {
            return;
        }
        List<HlsPriceListConfigBTRow> hlsPriceListConfigBTRows = hlsPriceListRow.getHlsPriceListConfigBTRows();
        for (HlsPriceListConfigBTRow hlsPriceListConfigBTRow : hlsPriceListConfigBTRows) {
            hlsPriceListConfigBTRow.setPriceList(hlsPriceListRow.getPriceList());
        }
        insertHlsPriceListConfigBTRows(requestCtx, hlsPriceListConfigBTRows);
    }


    public List<HlsPriceListConfigBTRow> insertHlsPriceListConfigBTRows(IRequest requestCtx, List<HlsPriceListConfigBTRow> hlsPriceListConfigBTRow) {
        if (CollectionUtils.isEmpty(hlsPriceListConfigBTRow)) {
            return null;
        }
        for (HlsPriceListConfigBTRow priceListConfigBTRow : hlsPriceListConfigBTRow) {
            HlsPriceListConfigBT hlsPriceListConfigBT = ExportUtils.copyObj(priceListConfigBTRow, HlsPriceListConfigBT.class, "configBtId");
            hlsPriceListConfigBT.setConfigBtId(null);
            hlsPriceListConfigBTMapper.insert(hlsPriceListConfigBT);
        }
        return hlsPriceListConfigBTRow;
    }


//    private void insertConfigHd(HlsCalcConfig hlsCalcConfig, List<HlsPriceListConfigHd> configHdRows) {
//        List<HlsPriceListConfigHd> insertList = new ArrayList<>();
//        for (HlsPriceListConfigHd source : configHdRows) {
//            HlsPriceListConfigHd insert = new HlsPriceListConfigHd();
//
//            List<HlsPriceListConfigLn> configLnsSource = source.getHlsPriceListConfigLns();
//            BeanUtils.copyProperties(source, insert, "configHdId");
//            hlsPriceListConfigHdMapper.insert(insert);
//
//            for (HlsPriceListConfigLn hlsPriceListConfigLn : configLnsSource) {
//                hlsPriceListConfigLn.setConfigLnId(null);
//                hlsPriceListConfigLn.setConfigHdId(insert.getConfigHdId());
//                hlsPriceListConfigLnMapper.insert(hlsPriceListConfigLn);
//            }
//
//        }
//    }
//
//    private void insertConfigBt(HlsCalcConfig hlsCalcConfig, List<HlsPriceListConfigBT> configBtRows) {
//        for (HlsPriceListConfigBT source : configBtRows) {
//            HlsPriceListConfigBT insert = new HlsPriceListConfigBT();
//            BeanUtils.copyProperties(source, insert, "configBtId");
//            hlsPriceListConfigBTMapper.insert(insert);
//        }
//    }


//    /**
//     * 组装 ConfigBT
//     */
//    public List<HlsPriceListConfigBTRow> assembleConfigBtRow(String priceList) {
//        HlsPriceListConfigBT hlsPriceListConfigBT = new HlsPriceListConfigBT();
//        hlsPriceListConfigBT.setPriceList(priceList);
//        List<HlsPriceListConfigBT> select = hlsPriceListConfigBTMapper.select(hlsPriceListConfigBT);
//        return ExportUtils.copyList(select, HlsPriceListConfigBTRow.class);
//    }
//
//    /**
//     * 组装 ConfigHd
//     */
//    private List<HlsPriceListConfigHd> assembleConfigHdRow(String priceList) {
//        HlsPriceListConfigHd param = new HlsPriceListConfigHd();
//        param.setPriceList(priceList);
//        List<HlsPriceListConfigHd> configHds = hlsPriceListConfigHdMapper.select(param);
//        configHds.forEach(configHd -> {
//            HlsPriceListConfigLn configLn = new HlsPriceListConfigLn();
//            List<HlsPriceListConfigLn> configLns = hlsPriceListConfigLnMapper.select(configLn);
//            configHd.setHlsPriceListConfigLns(configLns);
//        });
//        return configHds;
//    }


}
