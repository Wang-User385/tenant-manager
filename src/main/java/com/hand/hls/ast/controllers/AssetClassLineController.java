package com.hand.hls.ast.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.BaseConstants;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.ast.dto.AssetClassLine;
import com.hand.hls.ast.service.IAssetClassLineService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.service.IInterfaceErrorMsgService;
import leaf.bean.LeafRequestData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
public class AssetClassLineController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(AssetClassLineController.class);

    private static final String PARAMETER = "parameter";
    private static final String AUTHORITY_RULE_FLAG = "authorityRuleFlag";
    private static final String N = "N";

    @Autowired
    private IAssetClassLineService service;
    @Autowired
    private IInterfaceErrorMsgService interfaceErrorMsgService;


    @RequestMapping(value = "/hls/asset/class/line/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        AssetClassLine dto = param.toJavaObject(AssetClassLine.class);
        return new ResponseData(service.select(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/hls/asset/class/line/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<AssetClassLine> list = param.toJavaList(AssetClassLine.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/hls/asset/class/line/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<AssetClassLine> dto = parameter.toJavaList(AssetClassLine.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }

    @RequestMapping(value = "/hls/asset/class/line/update/level")
    @ResponseBody
    public ResponseData updateInitLevel(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONObject param = (JSONObject) requestData.get(PARAMETER);
        List<AssetClassLine> assetClassLines = param.getJSONArray("assetClassLineList").toJavaList(AssetClassLine.class);
        return service.updateInitLevel(iRequest, assetClassLines);
    }

    @RequestMapping(value = "/hls/asset/class/line/update/approveReviewLevel")
    @ResponseBody
    public ResponseData updateApproveReviewLevel(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONObject param = (JSONObject) requestData.get(PARAMETER);
        List<AssetClassLine> assetClassLines = param.getJSONArray("assetClassLineList").toJavaList(AssetClassLine.class);
        return service.updateApproveReviewLevel(iRequest, assetClassLines);
    }

    @RequestMapping(value = "/hls/asset/class/line/create")
    @ResponseBody
    public ResponseData transforByContracts(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws Exception {
        IRequest iRequest = createRequestContext(request);
        iRequest.setAttribute("authorityRuleFlag","N");
        RequestHelper.setCurrentRequest(iRequest);
        Map map = (Map) requestData.get("parameter");
        Long classHeadId= Long.valueOf(map.get("classHeadId").toString());
        List<HlsCusConContract> contracts = JSONObject.parseArray(map.get("contractList").toString(), HlsCusConContract.class);

        if(CollectionUtils.size(contracts) > 1000){
            throw new HlsCusException("一次性最多只能提交1000行数据！");
        }
        service.transforByContracts(iRequest,contracts,classHeadId);
        return new ResponseData(true);
    }

//    @RequestMapping("/review/level/excel/import")
//    @Transactional(rollbackFor = Exception.class)
//    public ResponseData prjBatchExcelImport(HttpServletRequest request,Long headerId,Long classHeadId,String layoutCode) throws HlsCusException {
//        logger.info("资产分类复核级别导入headerId：{},classHeadId:{},layoutCode:{}",headerId,classHeadId,layoutCode);
//        ResponseData responseData = new ResponseData(true);
//        IRequest iRequest = createRequestContext(request);
//        iRequest.setAttribute(AUTHORITY_RULE_FLAG,N);
//        service.excelBatchImport(iRequest,headerId,classHeadId,layoutCode);
//
//        //判断导入是否有异常信息
//        String message = interfaceErrorMsgService.checkImportErrorMessageExist(headerId,"PRJ_PROJECT");
//        responseData.setMessage(message);
//        //存在异常信息手动回滚事务
//        if(StringUtils.equals(message, BaseConstants.NO)){
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//        }
//
//        return responseData;
//    }
}