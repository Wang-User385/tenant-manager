package com.hand.hls.cont.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.calc.service.HlsCalcSaveService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractChangeReq;
import com.hand.hls.cont.mapper.HlsCusConContractChangeReqMapper;
import com.hand.hls.cont.service.IConContractChangeReqService;
import com.hand.hls.fnd.dto.HlsCusDocumentList;
import com.hand.hls.prj.dto.HlsBpMaster;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.service.IPrjQuotationService;
import com.hand.hls.sys.dto.DocumentHistoryData;
import com.hand.hls.utils.HlsConstantUtil;
import com.hand.hls.utils.JsonUtils;
import com.hand.hls.utils.ResMessageException;
import java.rmi.NoSuchObjectException;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import leaf.bean.LeafRequestData;
import leaf.service.validation.ParameterNullException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ConContractChangeReqController extends BaseController {
    @Autowired
    private IConContractChangeReqService service;
    @Autowired
    private HlsCalcSaveService hlsCalcSaveService;
    @Autowired
    private IPrjQuotationService prjQuotationService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public ConContractChangeReqController() {
    }

    @RequestMapping({"/con/contract/change/req/quotation/query"})
    @ResponseBody
    public ResponseData queryQuotation(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest iRequest = this.createRequestContext(request);
        Map map = (Map)requestData.get("parameter");
        HlsCusConContract contract = (HlsCusConContract)JSONObject.parseObject(JsonUtils.toCamelJsonString(map), HlsCusConContract.class);
        return new ResponseData(this.service.getIfQuotationPendding(iRequest, contract));
    }

    @RequestMapping({"/con/contract/change/req/backup"})
    @ResponseBody
    public ResponseData contractBackup(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam String tablePkValue, @RequestParam Long documentId) {
        IRequest iRequest = this.createRequestContext(request);
        DocumentHistoryData documentHistoryData = new DocumentHistoryData();
        documentHistoryData.setTableName("CON_CONTRACT");
        documentHistoryData.setDocumentId(documentId);
        documentHistoryData.setDocumentCategory("CONTRACT_CHANGE");
        documentHistoryData.setTablePkValue(tablePkValue);

        try {
            return this.service.backUp(iRequest, documentHistoryData, requestData, tablePkValue);
        } catch (ResMessageException var8) {
            return new ResponseData(false, var8.getMessage());
        }
    }

    @RequestMapping({"/con/contract/change/request/submit"})
    @ResponseBody
    public ResponseData submitRequest(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        JSONArray param = (JSONArray)requestData.get("parameter");
        if (CollectionUtils.isEmpty(param)) {
            return new ResponseData();
        } else {
            List changeReqs = param.toJavaList(HlsCusConContractChangeReq.class);

            try {
                return new ResponseData(this.service.submit(this.createRequestContext(request), (HlsCusConContractChangeReq)changeReqs.get(0)));
            } catch (ParameterNullException var6) {
                return new ResponseData(false, var6.getMessage());
            }
        }
    }

    /**
     * 大单提前部分还本 - 保存/下一步 按钮逻辑
     * @param request
     * @param requestData
     * @return
     */
    @RequestMapping({"/con/contract/change/request/partial/prepayment/submit"})
    @ResponseBody
    public ResponseData submitPartialPrepaymentRequest(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        JSONArray param = (JSONArray)requestData.get("parameter");
        if (CollectionUtils.isEmpty(param)) {
            return new ResponseData();
        } else {
            List changeReqs = param.toJavaList(HlsCusConContractChangeReq.class);

            try {
                return new ResponseData(this.service.submitPartialPrepayment(this.createRequestContext(request), (HlsCusConContractChangeReq)changeReqs.get(0)));
            } catch (ParameterNullException var6) {
                return new ResponseData(false, var6.getMessage());
            }
        }
    }

    /**
     * 大单提前部分还本 - 计算 按钮逻辑
     * @param request
     * @param requestData
     * @param documentId
     * @param quotationId
     * @return
     */
    @RequestMapping({"/con/contract/change/req/partial/prepayment/recalculate"})
    @ResponseBody
    public ResponseData recalculatePartialPrepayment(HttpServletRequest request,
                                                     @ModelAttribute("_request_data") LeafRequestData requestData,
                                                     @RequestParam Long documentId,
                                                     @RequestParam Long quotationId) {
        IRequest iRequest = this.createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");

        try {
            return this.service.recalculatePartialPrepayment(iRequest, documentId, quotationId, param);
        } catch (Exception e) {
            this.logger.error("recalculate partial prepayment error:", e);
            return new ResponseData(false, e.getMessage());
        }
    }

    /**
     * 大单提前部分还本 - 取消申请 按钮逻辑
     * @param request
     * @param leafRequestData
     * @return
     */
    @RequestMapping({"/con/contract/change/req/partial/prepayment/cancel"})
    @ResponseBody
    public ResponseData cancelPartialPrepayment(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData leafRequestData) {
        Long changeReqId = ((JSONObject)leafRequestData.get("parameter")).getLong("change_req_id");

        try {
            this.service.cancelChangeReqPartialPrepayment(this.createRequestContext(request), changeReqId);
        } catch (NoSuchObjectException var5) {
            this.logger.error("can not get contract record", var5);
            return new ResponseData(false, var5.getMessage());
        }

        return new ResponseData();
    }

    /**
     * 大单提前部分还本 - 提交审批 按钮逻辑
     * @param request
     * @param requestData
     * @return
     * @throws Exception
     */
    @RequestMapping({"/con/contract/change/req/partial/prepayment/approve"})
    @ResponseBody
    public ResponseData approvePartialPrepayment(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws Exception {
        HlsCusConContractChangeReq changeReq = (HlsCusConContractChangeReq)JSONObject.parseObject(requestData.get("parameter").toString(), HlsCusConContractChangeReq.class);

        try {
            this.service.approvePartialPrepaymentWfl(this.createRequestContext(request), changeReq);
        } catch (ResMessageException var5) {
            this.logger.error("提交失败:", var5);
            return new ResponseData(false, var5.getMessage());
        }

        return new ResponseData();
    }

    /**
     * 大单提前部分还本 - 填写提前还本日，带出变更起始期数
     * @param requestData
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/con/contract/change/req/partial/prepayment/check/times")
    public ResponseData checkTimesPartialPrepayment(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws Exception {
        IRequest iRequest = createRequestContext(request);
        iRequest.setAttribute("authorityRuleFlag","N");
        HlsCusConContractChangeReq changeReq = (HlsCusConContractChangeReq)JSONObject.parseObject(requestData.get("parameter").toString(), HlsCusConContractChangeReq.class);
        List<Map> list = new ArrayList<>();
        Map map = new HashMap<String,Object>();

        Long times = 0L;
        try {
            times = this.service.checkTimesPartialPrepayment(iRequest, changeReq);
            map.put("ccr_start_times",times);
            list.add(map);
            return new ResponseData(list);
        }catch (ResMessageException e){
            return new ResponseData(false, e.getMessage());
        }
    }

    /**
     * 大单提前部分还本 - 修改变更起始期数，检验提前还本日是否在本期数与上一期日期之间
     * @param requestData
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/con/contract/change/req/partial/prepayment/check/date")
    public ResponseData checkDatePartialPrepayment(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws Exception {
        IRequest iRequest = createRequestContext(request);
        iRequest.setAttribute("authorityRuleFlag","N");
        HlsCusConContractChangeReq changeReq = (HlsCusConContractChangeReq)JSONObject.parseObject(requestData.get("parameter").toString(), HlsCusConContractChangeReq.class);
        List result = new ArrayList();
        result.add(this.service.checkDatePartialPrepayment(iRequest, changeReq));
        ResponseData responseData = new ResponseData();
        responseData.setRows(result);
        return responseData;
    }

    /**
     * 大单提前部分还本 - 变更后的租金总和与该合同保证金比较大小：保证金 > 变更后的租金总和 返回 Y ；保证金 <= 变更后的租金总和 返回 N
     * @param requestData
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/con/contract/change/req/partial/prepayment/check/deposit")
    public ResponseData checkDepositPartialPrepayment(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws Exception {
        IRequest iRequest = createRequestContext(request);
        iRequest.setAttribute("authorityRuleFlag","N");
        HlsCusConContractChangeReq changeReq = (HlsCusConContractChangeReq)JSONObject.parseObject(requestData.get("parameter").toString(), HlsCusConContractChangeReq.class);
        List<Map> list = new ArrayList<>();
        Map map = new HashMap<String,Object>();

        String  flag  = this.service.checkDepositPartialPrepayment(iRequest, changeReq);
        map.put("flag",flag);
        list.add(map);
        return new ResponseData(list);
    }

    @RequestMapping({"/con/contract/change/req/recalculate"})
    @ResponseBody
    public ResponseData recalculate(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam Long documentId, @RequestParam Long quotationId) {
        IRequest iRequest = this.createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");

        try {
            return this.service.recalculate(iRequest, documentId, quotationId, param);
        } catch (Exception var8) {
            this.logger.error("recalculate error:", var8);
            return new ResponseData(false, var8.getMessage());
        }
    }

    @RequestMapping({"/con/contract/change/req/cancel"})
    @ResponseBody
    public ResponseData cancel(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData leafRequestData) {
        Long changeReqId = ((JSONObject)leafRequestData.get("parameter")).getLong("change_req_id");

        try {
            this.service.cancelChangeReq(this.createRequestContext(request), changeReqId);
        } catch (NoSuchObjectException var5) {
            this.logger.error("can not get contract record", var5);
            return new ResponseData(false, var5.getMessage());
        }

        return new ResponseData();
    }

    @RequestMapping({"/con/contract/change/req/approve"})
    @ResponseBody
    public ResponseData approve(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws Exception {
        HlsCusConContractChangeReq changeReq = (HlsCusConContractChangeReq)JSONObject.parseObject(requestData.get("parameter").toString(), HlsCusConContractChangeReq.class);

        try {
            this.service.approveWfl(this.createRequestContext(request), changeReq);
        } catch (ResMessageException var5) {
            this.logger.error("提交失败:", var5);
            return new ResponseData(false, var5.getMessage());
        }

        return new ResponseData();
    }

    @RequestMapping({"/con/contract/change/req/quotationSubmit"})
    @ResponseBody
    public ResponseData quotationSubmit(HttpServletRequest request, Long quotationId, String documentCategory, Long documentId, @RequestBody String sheets) throws Exception {
        ResponseData rd = new ResponseData();
        IRequest requestCtx = this.createRequestContext(request);
        List<HlsCusPrjQuotation> prjQuotationList = new ArrayList();
        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        hlsCusPrjQuotation.setQuotationId(quotationId);
        hlsCusPrjQuotation = (HlsCusPrjQuotation)this.prjQuotationService.selectByPrimaryKey(requestCtx, hlsCusPrjQuotation);
        if (hlsCusPrjQuotation != null) {
            hlsCusPrjQuotation.setSheets(sheets);
            HlsCusPrjQuotation prjQuotationR = this.hlsCalcSaveService.savePrjQuotation(requestCtx, hlsCusPrjQuotation, documentId, documentCategory);
            prjQuotationList.add(prjQuotationR);
            rd.setSuccess(true);
            rd.setMessage("计算成功!");
        } else {
            rd.setSuccess(false);
            rd.setMessage("未找到quotationId!");
        }

        return new ResponseData(prjQuotationList);
    }

    @RequestMapping({"/con/contract/change/req/quotationQuery"})
    @ResponseBody
    public String quotationQuery(HttpServletRequest request, Long quotationId, String documentCategory, Long documentId) {
        IRequest iRequest = this.createRequestContext(request);

        try {
            return this.service.queryDocumentSheets(iRequest, documentId, documentCategory, quotationId);
        } catch (Exception var7) {
            this.logger.error("get sheets error: ", var7);
            return JSON.toJSONString(new ResponseData(false, var7.getMessage()));
        }
    }

    @RequestMapping(
            value = {"/con/contract/change/req/querySheetByquotation"},
            produces = {"application/javascript;charset=utf-8"}
    )
    @ResponseBody
    public String querySheet(HttpServletRequest request, Long quotationId, String documentCategory, Long documentId) {
        IRequest requestCtx = this.createRequestContext(request);
        return this.service.querySheetByQuotation(requestCtx, quotationId, documentCategory, documentId);
    }

    @RequestMapping({"/con/contract/change/req/contract/bp"})
    @ResponseBody
    public ResponseData contractBp(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest requestCtx = this.createRequestContext(request);
        return this.service.queryContractBp(requestCtx, requestData);
    }

    /**
     * 承租人变更变更前商业伙伴
     */
    @RequestMapping(value = "/con/contract/change/tenant/query")
    @ResponseBody
    public ResponseData queryTenantBeforeChange(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws NoSuchObjectException {
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.queryTenantBeforeChange(requestCtx, JSON.parseObject(requestData.get("parameter").toString()).getLong("change_req_id")));
    }

    /**
     * 当前变更信息获取
     * @param request
     * @return
     */
    @RequestMapping(value = "/contract/change/info/query")
    @ResponseBody
    public ResponseData contractChangeInfoQuery(HttpServletRequest request, @RequestParam HashMap params) {
        IRequest iRequest = createRequestContext(request);
        JSONObject paramJson = JSONObject.parseObject(params.get("_request_data").toString()).getJSONObject("parameter");
        Long changeReqId = Long.parseLong(paramJson.getString("changeReqId"));
        HlsCusConContractChangeReq contractChangeReq =new HlsCusConContractChangeReq();
        contractChangeReq.setChangeReqId(changeReqId);
        List<Long> list = new ArrayList<>();
        list.add(service.selectByPrimaryKey(iRequest,contractChangeReq).getChangeTerm());
        return new ResponseData(list);
    }

    /**
     * 变更前租赁物信息
     */
    @RequestMapping(value = "/con/contract/change/lease/item/prev")
    @ResponseBody
    public ResponseData prevContractLeaseItem(HttpServletRequest request, @Param("document_id") Long document_id) throws NoSuchObjectException {
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.prevContractLeaseItem(requestCtx, document_id));
    }

    /**
     * 租赁物变更保存时校验
     */
    @RequestMapping(value = "/con/contract/change/lease/item/validata")
    @ResponseBody
    public ResponseData validataLeaseItem(HttpServletRequest request, @RequestParam("contractId") Long contractId, @RequestParam("maxAmount") Double maxAmount) {
        IRequest requestCtx = createRequestContext(request);
        List<Boolean> list = new ArrayList<>(1);
        list.add(service.validata(requestCtx, contractId, maxAmount));
        return new ResponseData(list);
    }
}
