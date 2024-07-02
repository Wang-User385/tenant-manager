package com.hand.hls.abs.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.excel.dto.ColumnInfo;
import com.hand.hap.excel.dto.ExportConfig;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.abs.dto.HlsCusAbsAssetsPack;
import com.hand.hls.abs.dto.HlsCusAbsAssetsPackage;
import com.hand.hls.abs.service.HlsCusAbsAssetsPackService;
import com.hand.hls.abs.service.HlsCusAbsAssetsPackageService;
import leaf.bean.LeafRequestData;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HlsCusAbsAssetsPackageController extends BaseController {

    @Autowired
    private HlsCusAbsAssetsPackageService service;

    @Autowired
    private HlsCusAbsAssetsPackService assetsPackService;

    @Autowired
    ObjectMapper objectMapper;


    @RequestMapping(value = "/ct/abs/assets/package/query")
    @ResponseBody
    public ResponseData query(HlsCusAbsAssetsPackage dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectAssetsPackage(requestContext, dto, page, pageSize));
    }

    /**
     * 判断列表中的数据期数的日期是否在封包日之后
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping(value = "/ct/abs/assets/package/check")
    @ResponseBody
    public ResponseData check(@RequestBody List<HlsCusAbsAssetsPackage> dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return service.checkAssetsPackage(requestContext, dto);
    }

    //@RequestBody List<HlsCusAbsAssetsPackage> dto,
    @RequestMapping(value = "/ct/abs/assets/package/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) throws Exception {

        JSONArray param = (JSONArray) requestData.get("parameter");

        List<HlsCusAbsAssetsPackage> dto= param.toJavaList(HlsCusAbsAssetsPackage.class);
        dto.stream().forEach(index->{
            index.set__status("insert");
        });
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        for(HlsCusAbsAssetsPackage index:dto){
            //增加校验
            service.checkAssetsPackagePackDateNew(requestCtx,index);
        }
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/ct/abs/assets/package/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusAbsAssetsPackage> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }


    @RequestMapping(value = "/ct/abs/assets/package/queryContract")
    @ResponseBody
    public ResponseData queryContarct(HlsCusAbsAssetsPackage dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectPackageContract(requestContext, dto, page, pageSize));
    }


    @RequestMapping(value = "/ct/abs/assets/package/queryCashflow")
    @ResponseBody
    public ResponseData queryCashflow(HlsCusAbsAssetsPackage dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectPackageConCashFlow(requestContext, dto, page, pageSize));
    }


    @RequestMapping(value = "/ct/abs/assets/package/timesSumData")
    @ResponseBody
    public ResponseData querytimesSumData(@RequestBody HlsCusAbsAssetsPackage dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectBetweenTimesCashflow(requestContext, dto));
    }

    @RequestMapping(value = "/ct/abs/assets/package/queryChange")
    @ResponseBody
    public ResponseData queryChangeBefore(HlsCusAbsAssetsPackage dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                          @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        HlsCusAbsAssetsPack assetsPack=new HlsCusAbsAssetsPack();
        assetsPack.setPackId(dto.getPackId());
        assetsPack=assetsPackService.selectByPrimaryKey(requestContext,assetsPack);

        if(assetsPack!=null) {
            HlsCusAbsAssetsPack historyPack = new HlsCusAbsAssetsPack();
            historyPack.setChangeReqId(assetsPack.getChangeReqId());
            historyPack.setRefPackId(assetsPack.getPackId());
            historyPack.setDataClass("HISTORY");
            List<HlsCusAbsAssetsPack> hlsCusAbsAssetsPacks = assetsPackService.select(requestContext, historyPack, 1, 999);
            if (CollectionUtils.isEmpty(hlsCusAbsAssetsPacks)) {
                dto.setPackId(assetsPack.getRefPackId());
            } else {
                dto.setPackId(hlsCusAbsAssetsPacks.get(0).getPackId());
            }
        }
        return new ResponseData(service.selectAssetsPackage(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/ct/abs/assets/package/cashFlowBetweenDate")
    @ResponseBody
    public ResponseData queryCashFlowBetweenDate(HlsCusAbsAssetsPackage dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectPackContarctCashFlow(requestContext, dto,page,pageSize));
    }


    @RequestMapping(value = "/ct/abs/assets/package/exportCashflow")
    public void exportCashflow(HttpServletRequest request, @RequestParam String config, HttpServletResponse httpServletResponse) throws IOException,InvocationTargetException, IllegalAccessException{
        IRequest requestContext = createRequestContext(request);
        try {
            JavaType type = objectMapper.getTypeFactory().constructParametrizedType(ExportConfig.class,
                    ExportConfig.class, HlsCusAbsAssetsPackage.class, ColumnInfo.class);
            ExportConfig<HlsCusAbsAssetsPackage, ColumnInfo> exportConfig = objectMapper.readValue(config, type);

            //由于金额千分位的原因 自己重新写一个导出
            service.exportCashflow(request,httpServletResponse,exportConfig.getParam());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @RequestMapping(value = "/ct/abs/assets/package/export-asset-details")
    public void exportAssetDetails(HttpServletRequest request, @RequestParam String config, HttpServletResponse httpServletResponse) throws InvocationTargetException, IllegalAccessException, IOException {
        JavaType type = objectMapper.getTypeFactory().constructParametrizedType(ExportConfig.class,
                ExportConfig.class, HlsCusAbsAssetsPackage.class, ColumnInfo.class);
        ExportConfig<HlsCusAbsAssetsPackage, ColumnInfo> exportConfig = objectMapper.readValue(config, type);
        //由于金额千分位的原因 自己重新写一个导出
        service.exportAssetDetails(request,httpServletResponse,exportConfig.getParam());
    }



    @RequestMapping(value = "/ct/abs/assets/package/exportCashFlowBetweenDate")
    public void exportCashFlowBetweenDate(HttpServletRequest request, @RequestParam String config, HttpServletResponse httpServletResponse) throws IOException,InvocationTargetException, IllegalAccessException{
        IRequest requestContext = createRequestContext(request);
        try {
            JavaType type = objectMapper.getTypeFactory().constructParametrizedType(ExportConfig.class,
                    ExportConfig.class, HlsCusAbsAssetsPackage.class, ColumnInfo.class);
            ExportConfig<HlsCusAbsAssetsPackage, ColumnInfo> exportConfig = objectMapper.readValue(config, type);

            //由于金额千分位的原因 自己重新写一个导出
            service.exportCashFlowBetweenDate(request,httpServletResponse,exportConfig.getParam());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 校验打包日期从/至
     * @return
     */
    @RequestMapping(value = "/ct/abs/assets/package/pack/date/check")
    @ResponseBody
    public ResponseData checkPackDate(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                      HttpServletRequest request)  {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusAbsAssetsPackage dto = param.toJavaObject(HlsCusAbsAssetsPackage.class);
        List<HlsCusAbsAssetsPackage> list = new ArrayList<>();
        list.add(dto);
        return service.checkAssetsPackagePackDate(requestContext, list);
    }

}
