package com.hand.hls.lease.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.BaseConstants;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.service.IInterfaceErrorMsgService;
import com.hand.hls.lease.dto.YxLeaseItemClassify;
import com.hand.hls.lease.service.YxLeaseItemService;
import leaf.bean.LeafRequestData;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Controller
public class YxLeaseItemController extends BaseController {

    @Autowired
    private YxLeaseItemService yxLeaseItemService;

    @Autowired
    private IInterfaceErrorMsgService interfaceErrorMsgService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String AUTHORITY_RULE_FLAG = "authorityRuleFlag";
    private static final String N = "N";


    @RequestMapping(value = "/lease/item/classify/tree/query")
    @ResponseBody
    public ResponseData selectScoreResultDtl(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONObject param = (JSONObject)requestData.get("parameter");
        YxLeaseItemClassify dto = (YxLeaseItemClassify)param.toJavaObject(YxLeaseItemClassify.class);
        List<YxLeaseItemClassify> yxLeaseItemClassifies = yxLeaseItemService.yxLeaseItemClassifyTreeQuery(dto);
        return new ResponseData(yxLeaseItemClassifies);
    }

    @RequestMapping(value = "/lease/item/classify/query/classifyId/lov")
    @ResponseBody
    public ResponseData query(@ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request, HttpSession session) {
        JSONObject param = (JSONObject)requestData.get("parameter");
        YxLeaseItemClassify dto = (YxLeaseItemClassify)param.toJavaObject(YxLeaseItemClassify.class);
        IRequest iRequest = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        return new ResponseData(yxLeaseItemService.yxLeaseItemClassifyForLov(dto));
    }

    /**
     * 查询租赁物产品可比价格
     * @param request
     * @return
     */
    @RequestMapping(value = "/lease/item/compare/price/query")
    @ResponseBody
    public ResponseData queryComparePrice(HttpServletRequest request, @RequestParam HashMap params) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(yxLeaseItemService.queryComparePrice(iRequest,params));
    }

    /**
     * 租赁物产品库批量导入
     * @param request
     * @return
     */
    @RequestMapping("/yx/lease/item/batch/excel/import")
    @Transactional(rollbackFor = Exception.class)
    public ResponseData leaseItemBatchExcelImport(HttpServletRequest request,Long headerId) throws HlsCusException {
        logger.info("租赁物产品库批量导入进入controller：{}",headerId);
        ResponseData responseData = new ResponseData(true);
        IRequest iRequest = createRequestContext(request);
        iRequest.setAttribute(AUTHORITY_RULE_FLAG,N);
        yxLeaseItemService.leaseItemExcelBatchImport(iRequest,headerId);

        //判断导入是否有异常信息
        String message = interfaceErrorMsgService.checkImportErrorMessageExist(headerId,"YX_LEASE_ITEM_CLASSIFY");
        responseData.setMessage(message);
        //存在异常信息手动回滚事务
        if(StringUtils.equals(message, BaseConstants.NO)){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return responseData;
    }

    /**
     * 生产厂商批量导入
     * @param request
     * @return
     */
    @RequestMapping("/yx/manufacturer/batch/excel/import")
    @Transactional(rollbackFor = Exception.class)
    public ResponseData manufacturerBatchExcelImport(HttpServletRequest request,Long headerId) throws HlsCusException {
        logger.info("生产厂商批量导入进入controller：{}",headerId);
        ResponseData responseData = new ResponseData(true);
        IRequest iRequest = createRequestContext(request);
        iRequest.setAttribute(AUTHORITY_RULE_FLAG,N);
        yxLeaseItemService.manufacturerExcelBatchImport(iRequest,headerId);

        //判断导入是否有异常信息
        String message = interfaceErrorMsgService.checkImportErrorMessageExist(headerId,"YX_LEASE_ITEM_MANUFACTURER");
        responseData.setMessage(message);
        //存在异常信息手动回滚事务
        if(StringUtils.equals(message, BaseConstants.NO)){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return responseData;
    }

    /**
     * 保存产品库相关功能操作日志
     * @param request
     * @return
     */
    @RequestMapping(value = "/yx/lease/item/log/create")
    @ResponseBody
    public ResponseData yxLeaseItemLogCreate(HttpServletRequest request, @RequestParam HashMap params) {
        IRequest iRequest = createRequestContext(request);
        yxLeaseItemService.leaseItemLogCreate(iRequest,params);
        return new ResponseData(true);
    }

    /**
     * 保存前数据重复校验
     * @param request
     * @return
     */
    @RequestMapping(value = "/yx/lease/item/check/before/submit")
    @ResponseBody
    public ResponseData yxLeaseItemCheckBeforeSubmit(HttpServletRequest request, @RequestParam HashMap params) {
        IRequest iRequest = createRequestContext(request);
        String code = yxLeaseItemService.leaseItemCheckBeforeSubmit(iRequest,params);
        return new ResponseData(Arrays.asList(code));
    }

    /**
     * 更新高端装备标志，子集一并更新
     */
    @RequestMapping(value = "/yx/lease/item/update/advanced/equipment/flag")
    @ResponseBody
    public ResponseData updateAdvancedEquipmentFlag(HttpServletRequest request, @RequestParam HashMap params) {
        JSONObject paramJson = JSONObject.parseObject(params.get("_request_data").toString()).getJSONObject("parameter");
        Long classifyId = paramJson.getLong("classifyId");
        String advancedEquipmentFlag = paramJson.getString("advancedEquipmentFlag");
        IRequest iRequest = createRequestContext(request);
        yxLeaseItemService.updateAdvancedEquipmentFlag(classifyId,advancedEquipmentFlag);
        return new ResponseData(true,"更新成功！");
    }

    @RequestMapping(value = "yx/lease/item/update/permission/check")
    @ResponseBody
    public ResponseData updateLeaseItemClassifyCheck(HttpServletRequest request) throws HlsCusException {
        IRequest iRequest = createRequestContext(request);
        boolean result = yxLeaseItemService.updateLeaseItemCheck(iRequest);
        String message = "";
        if (!result) {
            message = "没有权限";
        }
        return new ResponseData(true, message);
    }

}