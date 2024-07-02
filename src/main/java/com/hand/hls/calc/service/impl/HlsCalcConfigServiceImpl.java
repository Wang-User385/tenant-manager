package com.hand.hls.calc.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hand.hap.account.dto.User;
import com.hand.hap.account.mapper.UserMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.calc.dto.HlsCalcConfig;
import com.hand.hls.calc.dto.HlsPriceListConfigBT;
import com.hand.hls.calc.dto.HlsPriceListConfigHd;
import com.hand.hls.calc.dto.HlsPriceListConfigLn;
import com.hand.hls.calc.mapper.HlsCalcConfigMapper;
import com.hand.hls.calc.mapper.HlsPriceListConfigBTMapper;
import com.hand.hls.calc.mapper.HlsPriceListConfigHdMapper;
import com.hand.hls.calc.mapper.HlsPriceListConfigLnMapper;
import com.hand.hls.calc.service.HlsCalcConfigService;
import com.hand.hls.calc.service.HlsPriceListConfigHdService;
import com.hand.hls.common.utils.GzipUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCalcConfigServiceImpl extends BaseServiceImpl<HlsCalcConfig> implements HlsCalcConfigService {

    @Autowired
    HlsCalcConfigMapper mapper;

    @Autowired
    HlsPriceListConfigBTMapper hlsPriceListConfigBTMapper;

    @Autowired
    private HlsPriceListConfigHdService hlsPriceListConfigHdService;

    @Autowired
    private HlsPriceListConfigHdMapper hlsPriceListConfigHdMapper;

    @Autowired
    private HlsPriceListConfigLnMapper hlsPriceListConfigLnMapper;

    @Autowired
    private UserMapper userMapper;

    private final static String MULTI_LINE = "MULTI_LINE";


    @Override
    public List<HlsCalcConfig> queryUnSheets(IRequest requestContext, HlsCalcConfig hcc, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return mapper.queryUnSheets(hcc);
    }

    @Override
    public List<HlsCalcConfig> queryAll(IRequest requestContext, HlsCalcConfig hcc, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        User user = userMapper.selectByUserId(requestContext.getUserId().toString());
        hcc.setIntranetFlag(user.getIntranetFlag());
        // 管理员手动标识为内部用户
        if (requestContext.getEmployeeCode().equals("ADMIN")){
            hcc.setIntranetFlag("Y");
        }
        return mapper.queryAll(hcc);
    }

    @Override
    public void updateSheets(String priceList, String sheets,String compressSheets) {

        HlsCalcConfig hlsCalcConfig = new HlsCalcConfig();
        hlsCalcConfig.setPriceList(priceList);
        hlsCalcConfig.setSheets(sheets);

        //保存sheets
        mapper.updateSheets(priceList, compressSheets);

        //删除变化的sheet，先获取sheets里面sheet集合，再查询当前配置存在的sheet，如果查询的数据集不在当前更新的sheet集合中，则删除sheet的配置信息
        List<Map> sheetNames = getSheetNameMap(hlsCalcConfig);

        HlsPriceListConfigHd hlsPriceListConfigHd = new HlsPriceListConfigHd();
        hlsPriceListConfigHd.setPriceList(priceList);
        List<HlsPriceListConfigHd> configHdList = hlsPriceListConfigHdMapper.select(hlsPriceListConfigHd);
        if (configHdList.size() > 0) {
            for (HlsPriceListConfigHd configHd :
                    configHdList) {
                HashMap<String, String> tempMap = new HashMap<>();
                tempMap.put("sheetName", configHd.getSheetName());
                if (sheetNames.size() > 0 && sheetNames.contains(tempMap) == false) {
                    hlsPriceListConfigHdMapper.deleteByPrimaryKey(configHd);
                    HlsPriceListConfigLn hlsPriceListConfigLn = new HlsPriceListConfigLn();

                    hlsPriceListConfigLn.setConfigHdId(configHd.getConfigHdId());

                    hlsPriceListConfigLnMapper.delete(hlsPriceListConfigLn);
                }
            }
        }
    }

    public List<Map> getSheetNameMap(HlsCalcConfig hlsCalcConfig) {
        final LinkedList<Map> results = new LinkedList<>();
        final String sheets = hlsCalcConfig.getSheets();
        JSONArray array = JSONArray.parseArray(sheets);
        for (int i = 0; i < array.size(); i++) {
            final JSONObject keyPair = new JSONObject();
            String name = array.getJSONObject(i).getString("name");
            keyPair.put("sheetName", name);

            HlsPriceListConfigHd hlsPriceListConfigHd = new HlsPriceListConfigHd();
            hlsPriceListConfigHd.setPriceList(hlsCalcConfig.getPriceList());
            hlsPriceListConfigHd.setSheetName(name);
            hlsPriceListConfigHd.setTableType(MULTI_LINE);

            hlsPriceListConfigHd = hlsPriceListConfigHdMapper.selectOne(hlsPriceListConfigHd);
            String sheetCode = name;
            if (hlsPriceListConfigHd != null && hlsPriceListConfigHd.getSheetCode() != null) {
                sheetCode = hlsPriceListConfigHd.getSheetCode();
            }
            results.add(keyPair);
        }
        return results;
    }

    @Override
    public List<Map> getSheetNames(HlsCalcConfig hlsCalcConfig) throws UnsupportedEncodingException {
        final LinkedList<Map> results = new LinkedList<>();
        final HlsCalcConfig hlsCalcConfig1 = mapper.selectByPrimaryKey(hlsCalcConfig);
        if (hlsCalcConfig1 == null || StringUtils.isBlank(hlsCalcConfig1.getSheets())) {
            return results;
        }
        final String sheets = hlsCalcConfig1.getSheets();
        String stringSheets = GzipUtil.atob(sheets);
        String unzipSheets = GzipUtil.unCompress(stringSheets.getBytes("iso-8859-1"));
        String jsonSheets = URLDecoder.decode(unzipSheets,"utf-8");

        JSONArray array = JSONArray.parseArray(jsonSheets);
        for (int i = 0; i < array.size(); i++) {
            final JSONObject keyPair = new JSONObject();
            String name = array.getJSONObject(i).getString("name");
            keyPair.put("sheetName", name);

            HlsPriceListConfigHd hlsPriceListConfigHd = new HlsPriceListConfigHd();
            hlsPriceListConfigHd.setPriceList(hlsCalcConfig1.getPriceList());
            hlsPriceListConfigHd.setSheetName(name);
            hlsPriceListConfigHd.setTableType(MULTI_LINE);

            hlsPriceListConfigHd = hlsPriceListConfigHdMapper.selectOne(hlsPriceListConfigHd);
            String sheetCode = name;
            if (hlsPriceListConfigHd != null && hlsPriceListConfigHd.getSheetCode() != null) {
                sheetCode = hlsPriceListConfigHd.getSheetCode();
            }
            keyPair.put("sheetCode", sheetCode);

            keyPair.put("code_value", name);
            keyPair.put("code_value_name", name);
            results.add(keyPair);
        }
        return results;
    }

    @Override
    public List<HlsPriceListConfigBT> btConfigQuery(String priceList) {
        HlsPriceListConfigBT hlsPriceListConfigBT = new HlsPriceListConfigBT();
        hlsPriceListConfigBT.setPriceList(priceList);
        List<HlsPriceListConfigBT> btList = hlsPriceListConfigBTMapper.select(hlsPriceListConfigBT);
        return btList;
    }

    @Override
    public void doSubmit(IRequest requestCtx, List<HlsCalcConfig> lists) {
        this.batchUpdate(requestCtx, lists);
        for (HlsCalcConfig hlsCalcConfig : lists) {
            if (hlsCalcConfig.get__status().equals("insert")) {
                HlsPriceListConfigHd hlsPriceListConfigHd = new HlsPriceListConfigHd();
                hlsPriceListConfigHd.setPriceList(hlsCalcConfig.getPriceList());
                hlsPriceListConfigHd.setTableType("SINGLE");

                HlsPriceListConfigHd hlsPriceListConfigHdMul = new HlsPriceListConfigHd();
                hlsPriceListConfigHdMul.setPriceList(hlsCalcConfig.getPriceList());
                hlsPriceListConfigHdMul.setTableType("MULTI_LINE");

                hlsPriceListConfigHdService.insertSelective(requestCtx, hlsPriceListConfigHd);
                hlsPriceListConfigHdService.insertSelective(requestCtx, hlsPriceListConfigHdMul);
            }
        }
    }
}
