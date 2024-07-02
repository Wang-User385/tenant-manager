package com.hand.hls.hls.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.common.utils.GzipUtil;
import com.hand.hls.hls.dto.HlsWebExcelConfigHd;
import com.hand.hls.hls.dto.HlsWebExcelConfigLn;
import com.hand.hls.hls.mapper.HlsWebExcelConfigHdMapper;
import com.hand.hls.hls.mapper.HlsWebExcelConfigLnMapper;
import com.hand.hls.hls.mapper.HlsWebExcelMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.hls.dto.HlsWebExcel;
import com.hand.hls.hls.service.IHlsWebExcelService;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsWebExcelServiceImpl extends BaseServiceImpl<HlsWebExcel> implements IHlsWebExcelService{

    @Autowired
    private HlsWebExcelMapper hlsWebExcelMapper;

    @Autowired
    private HlsWebExcelConfigHdMapper webExcelConfigHdMapper;

    @Autowired
    private HlsWebExcelConfigLnMapper webExcelConfigLnMapper;

    private final static String MULTI_LINE = "MULTI_LINE";

    @Override
    public List<Map> selectHlsWebExcelInfo(IRequest iRequest, HlsWebExcel excel,int pagenum,int pagesize) {
        PageHelper.startPage(pagenum,pagesize);
        return hlsWebExcelMapper.selectHlsWebExcelInfo(excel);
    }

    @Override
    public void updateSheets(Long excelId, String sheets,String compressSheets) {

        HlsWebExcel hlsWebExcel = new HlsWebExcel();
        hlsWebExcel.setExcelId(excelId);

        //保存sheets
        hlsWebExcel.setSheets(compressSheets);
        hlsWebExcelMapper.updateByPrimaryKeySelective(hlsWebExcel);

        //删除变化的sheet，先获取sheets里面sheet集合，再查询当前配置存在的sheet，如果查询的数据集不在当前更新的sheet集合中，则删除sheet的配置信息
        hlsWebExcel.setSheets(sheets);
        List<Map> sheetNames = getSheetNameMap(hlsWebExcel);

        HlsWebExcelConfigHd webExcelConfigHd = new HlsWebExcelConfigHd();
        webExcelConfigHd.setExcelId(excelId);
        List<HlsWebExcelConfigHd> configHdList = webExcelConfigHdMapper.select(webExcelConfigHd);
        if (configHdList.size() > 0) {
            for (HlsWebExcelConfigHd configHd :
                    configHdList) {
                HashMap<String, String> tempMap = new HashMap<>();
                tempMap.put("sheetName", configHd.getSheetName());
                if (sheetNames.size() > 0 && sheetNames.contains(tempMap) == false) {
                    webExcelConfigHdMapper.deleteByPrimaryKey(configHd);
                    HlsWebExcelConfigLn hlsPriceListConfigLn = new HlsWebExcelConfigLn();

                    hlsPriceListConfigLn.setConfigHdId(configHd.getConfigHdId());

                    webExcelConfigLnMapper.delete(hlsPriceListConfigLn);
                }
            }
        }
    }

    public List<Map> getSheetNameMap(HlsWebExcel hlsWebExcel) {
        final LinkedList<Map> results = new LinkedList<>();

        final String sheets = hlsWebExcel.getSheets();
        JSONArray array = JSONArray.parseArray(sheets);
        for (int i = 0; i < array.size(); i++) {
            final JSONObject keyPair = new JSONObject();
            String name = array.getJSONObject(i).getString("name");
            keyPair.put("sheetName", name);

            HlsWebExcelConfigHd webExcelConfigHd = new HlsWebExcelConfigHd();
            webExcelConfigHd.setExcelId(hlsWebExcel.getExcelId());
            webExcelConfigHd.setSheetName(name);
            webExcelConfigHd.setTableType(MULTI_LINE);

            webExcelConfigHd = webExcelConfigHdMapper.selectOne(webExcelConfigHd);

            results.add(keyPair);
        }
        return results;
    }

    @Override
    public List<Map> getSheetNames(HlsWebExcel hlsWebExcel) throws UnsupportedEncodingException {
        final LinkedList<Map> results = new LinkedList<>();

        hlsWebExcel = hlsWebExcelMapper.selectByPrimaryKey(hlsWebExcel);
        final String sheets = hlsWebExcel.getSheets();

        String stringSheets = GzipUtil.atob(sheets);
        String unzipSheets = GzipUtil.unCompress(stringSheets.getBytes("iso-8859-1"));
        String jsonSheets = URLDecoder.decode(unzipSheets,"utf-8");

        JSONArray array = JSONArray.parseArray(jsonSheets);
        for (int i = 0; i < array.size(); i++) {
            final JSONObject keyPair = new JSONObject();
            String name = array.getJSONObject(i).getString("name");
            keyPair.put("sheetName", name);

            HlsWebExcelConfigHd webExcelConfigHd = new HlsWebExcelConfigHd();
            webExcelConfigHd.setExcelId(hlsWebExcel.getExcelId());
            webExcelConfigHd.setSheetName(name);
            webExcelConfigHd.setTableType(MULTI_LINE);

            webExcelConfigHd = webExcelConfigHdMapper.selectOne(webExcelConfigHd);

            keyPair.put("code_value", name);
            keyPair.put("code_value_name", name);
            results.add(keyPair);
        }
        return results;
    }

}