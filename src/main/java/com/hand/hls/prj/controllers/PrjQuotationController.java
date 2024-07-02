package com.hand.hls.prj.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.TokenException;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.calc.dto.HlsCalcConfig;
import com.hand.hls.calc.dto.HlsPriceListConfigBT;
import com.hand.hls.calc.mapper.HlsCalcConfigMapper;
import com.hand.hls.calc.service.HlsCalcConfigService;
import com.hand.hls.calc.service.HlsCalcExcelImportUtilService;
import com.hand.hls.calc.service.HlsCalcSaveService;
import com.hand.hls.common.utils.GzipUtil;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.fin.service.HlsCusLonContractQuotationService;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotationDetails;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationDetailsMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.HlsCusPrjQuotationDetailsService;
import com.hand.hls.prj.service.HlsCusPrjQuotationService;
import com.hand.hls.prj.service.IPrjQuotationService;
import com.hand.hls.utils.JsonUtils;
import leaf.bean.LeafRequestData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Controller
public class PrjQuotationController extends BaseController {

    private static final String PRJ_PROJECT = "PRJ_PROJECT";

    @Autowired
    private IPrjQuotationService service;

    @Autowired
    private HlsCusPrjQuotationService hlsCusPrjQuotationService;

    @Autowired
    private HlsCalcExcelImportUtilService hlsCalcExcelImportUtilService;

    @Autowired
    private HlsCusPrjQuotationMapper prjQuotationMapper;

    @Autowired
    private HlsCalcSaveService hlsCalcSaveService;

    @Autowired
    private HlsCusPrjQuotationDetailsMapper prjQuotationDetailsMapper;

    @Autowired
    private HlsCusPrjQuotationDetailsService hlsCusPrjQuotationDetailsService;

    @Autowired
    private HlsCalcConfigMapper hlsCalcConfigMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private HlsCalcConfigService calcService;

    @Autowired
    private HlsCusLonContractQuotationService hlsCusLonQuotationService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/prj/quotation/query")
    @ResponseBody
    public ResponseData query(HlsCusPrjQuotation dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/prj/quotation/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request, @RequestBody List<HlsCusPrjQuotation> dto) {
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/prj/quotation/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusPrjQuotation> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }


    @RequestMapping(value = "/prj/quotation/submitSheettTest", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData submitSheettTest(HttpServletRequest request, String priceList, @RequestBody String sheets)
            throws TokenException {
        ResponseData rd = null;
        IRequest requestCtx = createRequestContext(request);
        try {
            hlsCalcExcelImportUtilService.parseExcelJsonToArray(requestCtx, sheets);
        } catch (Exception e) {
            // TODO: handle exception
            rd = new ResponseData(false);
            rd.setMessage(e.getMessage());
        }

        return rd;
    }

    private void sheetsCache(String sheets, Long quotationId, String priceList) {
        String redisKey = "spreadsheet-" + priceList + "-" + quotationId.toString() + "-key";
        redisTemplate.opsForValue().set(redisKey, sheets);
    }

    @RequestMapping(value = "/prj/quotation/querySheetByquotation", produces = "application/json;charset=utf-8")
    @ResponseBody
    public JSONObject querySheet(HttpServletRequest request, HlsCusPrjQuotation hlsCusPrjQuotation) {
        IRequest requestCtx = createRequestContext(request);
        HlsCusPrjQuotationDetails hlsCusPrjQuotationDetails = new HlsCusPrjQuotationDetails();
        String sheets = null;
        JSONObject sheetsData = new JSONObject();

        hlsCusPrjQuotationDetails.setQuotationId(hlsCusPrjQuotation.getQuotationId());
        hlsCusPrjQuotation = prjQuotationMapper.selectByPrimaryKey(hlsCusPrjQuotation);

        List<HlsPriceListConfigBT> btList = calcService.btConfigQuery(hlsCusPrjQuotation.getPriceList());
        sheetsData.put("btnList",btList);

        List<HlsCusPrjQuotationDetails> quotationList = prjQuotationDetailsMapper.select(hlsCusPrjQuotationDetails);
        if(quotationList.size()==0){
            HlsCalcConfig hlsCalcConfig = new HlsCalcConfig();
            hlsCalcConfig.setPriceList(hlsCusPrjQuotation.getPriceList());
            List<HlsCalcConfig> priceList = hlsCalcConfigMapper.select(hlsCalcConfig);
            if(priceList.size()>0){
                sheets = priceList.get(0).getSheets();
            }
        }else{
            sheets = quotationList.get(0).getSheets();
        }
        sheetsData.put("sheets",sheets);
        return sheetsData;
    }

    @RequestMapping(value = "/prj/quotation/quotationSubmit", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData quotationSumbit(HttpServletRequest request, Long quotationId, String priceList,Long sourceId, @RequestBody String sheets)
            throws Exception {
        ResponseData rd = new ResponseData();
        IRequest requestCtx = createRequestContext(request);
        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        sheetsCache(sheets,quotationId,priceList);
        hlsCusPrjQuotation.setQuotationId(quotationId);
        hlsCusPrjQuotation = service.selectByPrimaryKey(requestCtx, hlsCusPrjQuotation);
        if (hlsCusPrjQuotation != null) {
            /*String stringSheets = GzipUtil.atob(sheets);
            String unzipSheets = GzipUtil.unCompress(stringSheets.getBytes("iso-8859-1"));
            String jsonSheets = URLDecoder.decode(unzipSheets,"utf-8");
            hlsCusPrjQuotation.setSheets(jsonSheets);
            hlsCusPrjQuotation.setCompressSheets(sheets);*/
            hlsCusPrjQuotation.setSourceId(sourceId);
            hlsCusPrjQuotationService.saveCalcFront(requestCtx,hlsCusPrjQuotation,sheets);
            /*HlsCusPrjQuotation prjQuotationR = hlsCalcSaveService.savePrjQuotation(requestCtx, hlsCusPrjQuotation);*/
            rd.setSuccess(true);
            rd.setMessage("计算成功!");
        } else {
            rd.setSuccess(false);
            rd.setMessage("未找到quotationId!");
        }
        return rd;

    }

    /**
     * 融资提款报价提交
     */
    @RequestMapping(value = {"/lon/quotation/quotationSumbit"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseData lonQuotationSumbit(IRequest iRequest,HttpServletRequest request, Long quotationId, String priceList, @RequestBody String sheets) throws Exception {
        ResponseData rd = new ResponseData();
        IRequest requestCtx = this.createRequestContext(request);
        List<HlsCusPrjQuotation> prjQuotationList = new ArrayList();
        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        hlsCusPrjQuotation.setQuotationId(quotationId);
        hlsCusPrjQuotation = this.service.selectByPrimaryKey(requestCtx, hlsCusPrjQuotation);
        if (hlsCusPrjQuotation != null) {
            hlsCusPrjQuotation.setSheets(sheets);
            hlsCusPrjQuotation.setPriceList(priceList);
            hlsCusLonQuotationService.saveCalcFront(requestCtx,hlsCusPrjQuotation,sheets);
            rd.setSuccess(true);
            rd.setMessage("计算成功!");
        } else {
            rd.setSuccess(false);
            rd.setMessage("未找到quotationId!");
        }

        return rd;
    }


    @RequestMapping(value = "/prj/quotation/quotationSave", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData quotationSave(HttpServletRequest request, Long quotationId, String priceList, @RequestBody String sheets)
            throws Exception {
        ResponseData rd = new ResponseData();
        IRequest requestCtx = createRequestContext(request);
        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();

        hlsCusPrjQuotation.setQuotationId(quotationId);
        hlsCusPrjQuotation = service.selectByPrimaryKey(requestCtx, hlsCusPrjQuotation);
        if (hlsCusPrjQuotation != null) {
            String stringSheets = GzipUtil.atob(sheets);
            String unzipSheets = GzipUtil.unCompress(stringSheets.getBytes("iso-8859-1"));
            String jsonSheets = URLDecoder.decode(unzipSheets,"utf-8");
            hlsCusPrjQuotation.setSheets(jsonSheets);
            hlsCusPrjQuotation.setCompressSheets(sheets);

            if(hlsCusPrjQuotation.getQuotationId() != null) {
                HlsCusPrjQuotationDetails quotationDetails = new HlsCusPrjQuotationDetails();
                quotationDetails.setQuotationId(hlsCusPrjQuotation.getQuotationId());
                quotationDetails = prjQuotationDetailsMapper.selectOne(quotationDetails);
                if (quotationDetails != null) {
                    quotationDetails.setSheets(sheets);
                    hlsCusPrjQuotationDetailsService.updateByPrimaryKeySelective(requestCtx, quotationDetails);
                }else{
                    HlsCusPrjQuotationDetails details = new HlsCusPrjQuotationDetails();
                    details.setQuotationId(hlsCusPrjQuotation.getQuotationId());
                    details.setSheets(sheets);
                    hlsCusPrjQuotationDetailsService.insertSelective(requestCtx,details);
                }
            }

            rd.setSuccess(true);
            rd.setMessage("保存成功!");
        } else {
            rd.setSuccess(false);
            rd.setMessage("未找到quotationId!");
        }
        return rd;

    }

    /**
     * 获取审批项目报价信息
     *
     * @param dto
     * @param requestData
     * @param pagenum
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/prj/quotation/queryByProjectId")
    @ResponseBody
    public ResponseData queryForApprove(HlsCusPrjQuotation dto,
                                        @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                        @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        Map param = (Map) requestData.get("parameter");
        if (param == null) {
            return new ResponseData(false, "请求发生错误");
        }
        if (param.get("project_id") == null) {
            return new ResponseData(false, "请求发生错误");
        }
        dto.setSourceDocumentId(Long.parseLong(param.get("project_id").toString()));
        return new ResponseData(service.getApproveInfo(dto.getSourceDocumentId(), pagenum, pageSize));
    }


    @RequestMapping(value = "/prj/quotation/contract/detail")
    @ResponseBody
    public ResponseData queryPrjQuotationDetail(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        Map map = (Map) requestData.get("parameter");
        map = (Map) JSON.parse(JsonUtils.toCamelJsonString(map));
        return new ResponseData(service.queryPrjQuotationDetail(map));
    }

    @RequestMapping(value = "/prj/quotation/spinoff")
    @ResponseBody
    public ResponseData spinOff(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        Map map = (Map) requestData.get("parameter");
        HlsCusConContract contract = JSONObject.parseObject(JsonUtils.toCamelJsonString(map), HlsCusConContract.class);
        try {
            service.spinOff(iRequest, contract);
        } catch (Exception e) {
            logger.error("保存失败：", e);
            return new ResponseData(false, e.getMessage());
        }
        return new ResponseData();
    }

    @RequestMapping(value = "/prj/quotation/contract/all")
    @ResponseBody
    public ResponseData querySpinQuotationDetail(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        Map map = (Map) requestData.get("parameter");
        map = (Map) JSON.parse(JsonUtils.toCamelJsonString(map));
        return new ResponseData(service.querySpinQuotationDetail(map));
    }
    /**
     * @description 查询审批通过的拆分报价
     * @param requestData
     * @param request
     * @return com.hand.hap.system.dto.ResponseData
     */
    @RequestMapping(value = "/prj/quotation/contract/approved")
    @ResponseBody
    public ResponseData querySpinQuotationApproved(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        Map map = (Map) requestData.get("parameter");
        map = (Map) JSON.parse(JsonUtils.toCamelJsonString(map));
        return new ResponseData(service.querySpinQuotationApproved(map));
    }

    /**
     * 查询审批中的拆分报价
     *
     * @param contractId
     * @param processInstanceId
     * @param request
     * @return
     */
    @RequestMapping(value = "/prj/quotation/contract/approvedSpinQuotation")
    @ResponseBody
    public ResponseData queryApprovedSpinQuotation(@RequestParam Long contractId,
                                                   @RequestParam Long processInstanceId,
                                                   HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        return new ResponseData(service.queryApprovingSpinQuotation(contractId, processInstanceId));
    }

    /**
     * 寄送管理查询合同报价
     *
     * @param requestData
     * @param request
     * @param pagenum
     * @param pagesize
     * @return
     */
    @RequestMapping(value = "/prj/quotation/contract/query")
    @ResponseBody
    public ResponseData queryContractQuotationList(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                                   HttpServletRequest request,
                                                   @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                                   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        HlsCusPrjQuotation dto = JSON.parseObject(requestData.get("parameter").toString(), HlsCusPrjQuotation.class);
        return new ResponseData(service.queryContractQuotationList(dto, pagenum, pagesize));
    }

}