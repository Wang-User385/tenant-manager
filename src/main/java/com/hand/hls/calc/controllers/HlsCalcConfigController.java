package com.hand.hls.calc.controllers;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.TokenException;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.script.utils.DateUtils;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.calc.dto.HlsCalcConfig;
import com.hand.hls.calc.dto.HlsPriceListConfigBT;
import com.hand.hls.calc.dto.HlsPriceListConfigHd;
import com.hand.hls.calc.dto.HlsPriceListConfigLn;
import com.hand.hls.calc.exception.ChangeLimitException;
import com.hand.hls.calc.mapper.HlsPriceListConfigHdMapper;
import com.hand.hls.calc.service.HlsCalcConfigService;
import com.hand.hls.calc.service.HlsPriceListConfigHdService;
import com.hand.hls.calc.service.HlsPriceListConfigLnService;
import com.hand.hls.calc.service.IHlsPriceListExportService;
import com.hand.hls.common.utils.GzipUtil;
import leaf.bean.LeafRequestData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/hls/calcConfig")
public class HlsCalcConfigController extends BaseController {

    @Autowired
    private HlsCalcConfigService calcService;

    @Autowired
    private HlsPriceListConfigHdService hlsPriceListConfigHdService;

    @Autowired
    private HlsPriceListConfigLnService hlsPriceListConfigLnService;

    @Autowired
    private HlsPriceListConfigHdMapper hlsPriceListConfigHdMapper;

    @Autowired
    private IHlsPriceListExportService hlsPriceListExportService;

    @Autowired
    private RedisTemplate redisTemplate;

    /*
    * 查询sheet个数
    *
    * */
    @RequestMapping(value = "/get/sheets/by/pricelist")
    @ResponseBody
    public ResponseData queryExcelSheet(HttpServletRequest request,
                                        @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws UnsupportedEncodingException {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCalcConfig hlsCalcConfig = param.toJavaObject(HlsCalcConfig.class);

        List<Map> list = calcService.getSheetNames(hlsCalcConfig);
        return new ResponseData(list);
    }

    /**
     * 价目表定义查询
     *
     * @param request
     * @param requestData
     * @param pagenum
     * @param pagesize
     * @return
     */
    @RequestMapping(value = "/queryUnSheets")
    @ResponseBody
    public ResponseData queryUnSheets(HttpServletRequest request,
                                      @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                      @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCalcConfig hcc = param.toJavaObject(HlsCalcConfig.class);
        return new ResponseData(calcService.queryUnSheets(requestCtx, hcc, pagenum, pagesize));
    }

    /**
     * 价目表定义提交
     *
     * @param request
     * @param requestData
     * @return
     * @throws TokenException
     */
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData submitCalcConfig(HttpServletRequest request,
                                         @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData)
            throws TokenException {
        ResponseData rd = null;
        try {
            IRequest iRequest = createRequestContext(request);
            JSONArray param = (JSONArray) requestData.get("parameter");
            List<HlsCalcConfig> list = param.toJavaList(HlsCalcConfig.class);

            calcService.doSubmit(iRequest, list);

            rd = new ResponseData();
        } catch (Exception e) {
            rd = new ResponseData(false);
            rd.setMessage("价目表代码不可重复");
        }
        return rd;
    }

    @RequestMapping(value = "/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,
                               @RequestBody List<HlsCalcConfig> list) {
        calcService.batchDelete(list);
        return new ResponseData();
    }

    /**
     * 租金试算查询
     *
     * @param request
     * @param requestData
     * @param pagenum
     * @param pagesize
     * @return
     */
    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(HttpServletRequest request,
                              @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                              @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCalcConfig hcc = param.toJavaObject(HlsCalcConfig.class);
        return new ResponseData(calcService.queryAll(requestCtx, hcc, pagenum, pagesize));
    }

    @RequestMapping(value = "/getConfigHdInfoByPriceList")
    @ResponseBody
    public ResponseData getConfigHdInfoByPriceList(HttpServletRequest request,
                                                   @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                                   @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                                   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsPriceListConfigHd hcc = param.toJavaObject(HlsPriceListConfigHd.class);
        return new ResponseData(hlsPriceListConfigHdMapper.getConfigHdInfoByPriceList(hcc));
    }

    @RequestMapping(value = "/headConfig/submit")
    @ResponseBody
    public ResponseData submit(HttpServletRequest request,
                               @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData)
            throws ChangeLimitException {
        try {
            IRequest iRequest = createRequestContext(request);
            RequestHelper.setCurrentRequest(iRequest);
            JSONArray param = (JSONArray) requestData.get("parameter");
            HlsPriceListConfigHd calcConfigs = new HlsPriceListConfigHd();
            if (param.size() > 0) {
                JSONObject headRecord = param.getJSONObject(0);
                calcConfigs = headRecord.toJavaObject(HlsPriceListConfigHd.class);
                List<HlsPriceListConfigHd> lists = hlsPriceListConfigHdService.submit(iRequest, calcConfigs);
                return new ResponseData(lists);
            } else {
                return new ResponseData();
            }
        } catch (Exception e) {
            throw new ChangeLimitException(e.getMessage());
        }
    }

    /**
     * 行信息操作
     *
     * @param request
     * @param requestData
     * @param pagenum
     * @param pagesize
     * @return
     */
    @RequestMapping(value = "/lineConfig/query", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData queryLn(HttpServletRequest request,
                                @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsPriceListConfigLn calcConfig = param.toJavaObject(HlsPriceListConfigLn.class);
        List<HlsPriceListConfigLn> list = hlsPriceListConfigLnService.selectHlsPriceListConfiglineByHdId(calcConfig, pagenum, pagesize);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/lineConfig/delete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,
                               @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsPriceListConfigLn> calcConfigs = param.toJavaList(HlsPriceListConfigLn.class);
        hlsPriceListConfigLnService.batchDelete(calcConfigs);
        return new ResponseData(calcConfigs);
    }


    /**
     * excel租金计算
     *
     * @param request
     * @param priceList
     * @return
     */
    @RequestMapping(value = "/btConfig/query", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData calcBtConfigQuery(HttpServletRequest request, @RequestParam String priceList) {
        List<HlsPriceListConfigBT> btList = calcService.btConfigQuery(priceList);
        return new ResponseData(btList);
    }

    @RequestMapping(value = "/querySheet", produces = "application/json;charset=utf-8")
    @ResponseBody
    public JSONObject querySheet(HttpServletRequest request, HlsCalcConfig hcc) {
        IRequest requestCtx = createRequestContext(request);
        JSONObject sheetsData = new JSONObject();

        List<HlsPriceListConfigBT> btnList = calcService.btConfigQuery(hcc.getPriceList());
        sheetsData.put("btnList",btnList);


        HlsCalcConfig hlsCalcConfig = calcService.selectByPrimaryKey(requestCtx, hcc);
        sheetsData.put("sheets",hlsCalcConfig.getSheets());

        return sheetsData;
    }

    private void sheetsCache(String sheets,String priceList) {
        String redisKey = "spreadsheet-" + priceList + "-key";
        redisTemplate.opsForValue().set(redisKey, sheets);
    }

    @RequestMapping(value = "/submitSheet", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData submitSheet(HttpServletRequest request, String priceList, @RequestBody String sheets)
            throws TokenException {
        ResponseData rd = null;
        try {
            sheetsCache(sheets,priceList);
            String stringSheets = GzipUtil.atob(sheets);
            String unzipSheets = GzipUtil.unCompress(stringSheets.getBytes("iso-8859-1"));
            String jsonSheets = URLDecoder.decode(unzipSheets,"utf-8");
            calcService.updateSheets(priceList, jsonSheets,sheets);
            rd = new ResponseData();
        } catch (Exception e) {
            rd = new ResponseData(false);
            rd.setMessage(e.getMessage());
        }

        return rd;
    }

    @RequestMapping(value = "/column/query", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData queryColumn(HttpServletRequest request, @RequestBody HlsPriceListConfigLn calcConfig) {
        List<HlsPriceListConfigLn> list = this.hlsPriceListConfigLnService.selectTargetColumnCode(calcConfig);
        return new ResponseData(list);
    }

    /**
     * 导出
     */
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    @ResponseBody
    public void export(HttpServletRequest request, HttpServletResponse response, String[] idlist) throws IOException {
        IRequest requestCtx = createRequestContext(request);
        byte[] answer = "请求参数有误, idlist 不能为空".getBytes();
        List<String> idLists = Arrays.asList(idlist);
        if (CollectionUtils.isNotEmpty(idLists)) {
            answer = hlsPriceListExportService.export(requestCtx, idLists);
        }
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"calc_config_export_" + DateUtils.format(new Date(), DateUtils.DATETIME_PATTERN) + ".leaf\"");
        response.addHeader("Content-Length", "" + answer.length);
        response.setContentType("application/octet-stream; charset=" + "UTF-8");
        IOUtils.write(answer, response.getOutputStream());
    }


    /**
     * 导入
     */
    @PostMapping("/import")
    public ResponseData importFile(MultipartFile file, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        if (file == null) {
            return new ResponseData(false, "请求参数不能为空");
        }
        String name = file.getOriginalFilename();
        if (!name.contains(".leaf")) {
            return new ResponseData(false, "上传文件不支持,必须为 .leaf 文件");
        }
        IRequest currentRequest = RequestHelper.createServiceRequest(request);
        RequestHelper.setCurrentRequest(currentRequest);
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            return new ResponseData(false, "文件解析错误：无法获取输入流。");
        }
        return hlsPriceListExportService.importFile(requestCtx, inputStream);
    }

    @RequestMapping(value = "/adjust/queryLov")
    @ResponseBody
    public ResponseData queryTemplet(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                     HttpServletRequest request, HttpServletResponse response,
                                     HlsCalcConfig hlsCalcConfig,
                                     @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                     @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCalcConfig metadataRelation = param.toJavaObject(HlsCalcConfig.class);

        //用来接收setLovPara 参数
        if (hlsCalcConfig.getPriceList() != null) {
            metadataRelation.setPriceList(hlsCalcConfig.getPriceList());
        }
        if(hlsCalcConfig.getDescription() != null){
            metadataRelation.setDescription(hlsCalcConfig.getDescription());
        }

        IRequest requestCtx = createRequestContext(request);
        List<HlsCalcConfig> list = calcService.queryAll(requestCtx, metadataRelation, pagenum, pagesize);

        return new ResponseData(list);
    }


}
