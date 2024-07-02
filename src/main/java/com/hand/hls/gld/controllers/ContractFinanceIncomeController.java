package com.hand.hls.gld.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fin.dto.HlsCusLonContractWithdraw;
import com.hand.hls.fin.service.HlsCusLonContractWithdrawService;
import com.hand.hls.gld.dto.HlsCusContractFinanceIncome;
import com.hand.hls.gld.dto.HlsCusFinIncomePkg;
import com.hand.hls.gld.dto.HlsCusGldLonContractFinCost;
import com.hand.hls.gld.dto.Period;
import com.hand.hls.gld.mapper.HlsCusGldLonContractFinCostMapper;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.gld.service.HlsCusCtDocumentFinIncomeService;
import com.hand.hls.gld.service.HlsCusGldLonContractFinCostService;
import com.hand.hls.gld.service.IContractFinanceIncomeService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: WuJun
 * @Date: 2020/02/07
 * @Time: 11:48
 */

@Controller
public class ContractFinanceIncomeController extends BaseController {

    @Autowired
    private IContractFinanceIncomeService service;

    @Autowired
    private HlsCusCtDocumentFinIncomeService hlsCusCtDocumentFinIncomeService;

    @Autowired
    private HlsCusConContractService hlsCusConContractService;

    @Autowired
    private HlsCusLonContractWithdrawService hlsCusLonContractWithdrawService;

    @Autowired
    private HlsCusGldLonContractFinCostService hlsCusGldLonContractFinCostService;

    @RequestMapping(value = "/gld/contract/finance/income/query")
    @ResponseBody
    public ResponseData queryContractFinance(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                             @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                             @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,
                                             HttpServletRequest request) {

        JSONObject param = (JSONObject) requestData.get("parameter");
        if (param == null) {
            return new ResponseData(false, "请求参数缺失!");
        }
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        HlsCusContractFinanceIncome dto = param.toJavaObject(HlsCusContractFinanceIncome.class);
        return new ResponseData(service.queryContractFinanceIncome(iRequest, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/gld/contract/finance/income/submit")
    @ResponseBody
    public ResponseData updateContractFinanceIncome(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                                    HttpServletRequest request, BindingResult result) {
        JSONArray jsonArray = (JSONArray) requestData.get("parameter");
        List<HlsCusContractFinanceIncome> dto = jsonArray.toJavaList(HlsCusContractFinanceIncome.class);
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        List<HlsCusContractFinanceIncome> incomes = new ArrayList<>();
        for (HlsCusContractFinanceIncome dt : dto) {
            dt = service.queryConFinanceIncomeByKey(requestCtx, dt);
            incomes.add(dt);
        }
        HlsCusFinIncomePkg hlsCusFinIncomePkg = new HlsCusFinIncomePkg();
        hlsCusFinIncomePkg.setHlsCusContractFinanceIncomeList(incomes);
        hlsCusFinIncomePkg.setFinType("CON_CONTRACT");
        hlsCusCtDocumentFinIncomeService.insertCtFin(requestCtx, hlsCusFinIncomePkg);
        return new ResponseData(dto);
    }

    @RequestMapping(value = "/gld/contract/finance/income/period/name/query")
    @ResponseBody
    public ResponseData query4lovPeriod(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int pageSize,
                                        Period dto, HttpServletRequest request, HttpSession session) {
        IRequest requestContext = this.createRequestContext(request);
        Long companyId = (Long) session.getAttribute("companyId");
        dto.setCompanyId(companyId);
        return new ResponseData(this.service.periodNameQueryForComb(requestContext, dto, page, pageSize));
    }


    @RequestMapping(value = "/gld/finance/income/sharing")
    @ResponseBody
    public ResponseData sharing(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws Exception {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);

        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusConContract> list = param.toJavaList(HlsCusConContract.class);
        service.financeIncomeSharing(iRequest, list);
        return new ResponseData();
    }

    @RequestMapping(value = "/gld/finance/income/report")
    @ResponseBody
    public ResponseData reportQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                    @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusContractFinanceIncome dto = param.toJavaObject(HlsCusContractFinanceIncome.class);
        return new ResponseData(service.reportQuery(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/gld/finance/income/select")
    @ResponseBody
    public ResponseData selectFeeByCfItem(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                          @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusContractFinanceIncome dto = param.toJavaObject(HlsCusContractFinanceIncome.class);
        return new ResponseData(service.selectFinanceIncome(requestContext, dto, pagenum, pagesize));

    }

    @RequestMapping(value = "/gld/cost/accrual/select")
    @ResponseBody
    public ResponseData selectCostAccrual(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                          @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request){
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusGldLonContractFinCost dto = param.toJavaObject(HlsCusGldLonContractFinCost.class);
        return new ResponseData(hlsCusGldLonContractFinCostService.selectCostAccrual(dto));
    }

    @RequestMapping(value = "/gld/risk/fund/accrual")
    @ResponseBody
    public ResponseData selectRiskFundAccrual(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request){
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusConContract dto = param.toJavaObject(HlsCusConContract.class);
        String yearMonth = param.getString("period_name");
        String sub = yearMonth.substring(yearMonth.indexOf("-") + 1);
        String periodName = yearMonth+"-01";
        String periodName2;
        if(Arrays.asList("01","03","05","07","08","10","12").contains(sub)){
            periodName2 = yearMonth + "-31";
        }else if(Arrays.asList("04","06","09","11").contains(sub)){
            periodName2 = yearMonth + "-30";
        }else{
            periodName2 = yearMonth + "-28";
        }
        dto.setInceptionOfLeaseFrom(periodName);
        dto.setInceptionOfLeaseTo(periodName2);
        List<HlsCusConContract> hlsCusConContracts = hlsCusConContractService.selectRiskFundAccrual(dto);
        return new ResponseData(hlsCusConContracts);
    }

    @RequestMapping(value = "/gld/stamp/duty/lease")
    @ResponseBody
    public ResponseData selectStampDutyLease(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                             @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) throws HlsCusException, ParseException {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusConContract dto = param.toJavaObject(HlsCusConContract.class);
        String yearMonth = param.getString("period_name");
        String sub = yearMonth.substring(yearMonth.indexOf("-") + 1);
        String periodName = yearMonth+"-01";
        String periodName2;
        if(Arrays.asList("01","03","05","07","08","10","12").contains(sub)){
            periodName2 = yearMonth + "-31";
        }else if(Arrays.asList("04","06","09","11").contains(sub)){
            periodName2 = yearMonth + "-30";
        }else{
            periodName2 = yearMonth + "-28";
        }
        dto.setInceptionOfLeaseFrom(periodName);
        dto.setInceptionOfLeaseTo(periodName2);
        List<HlsCusConContract> hlsCusConContracts = hlsCusConContractService.selectStampDutyLease(dto);
        List<HlsCusConContract> list = new ArrayList<HlsCusConContract>();
        for (HlsCusConContract hlsCusConContract : hlsCusConContracts) {
            list.add(hlsCusConContract);
            if (hlsCusConContract.getPurStampDuty()!= null) {
                HlsCusConContract contract = new HlsCusConContract();
                contract.setContractId(hlsCusConContract.getContractId());
                contract.setProjectId(hlsCusConContract.getProjectId());
                contract.setBpName(hlsCusConContract.getBpName());
                contract.setProjectName(hlsCusConContract.getProjectName());
                contract.setContractNumber(hlsCusConContract.getContractNumber());
                contract.setContractName(hlsCusConContract.getContractName());
                contract.setContractAmount(hlsCusConContract.getContractAmount());
                contract.setInceptionOfLease(hlsCusConContract.getInceptionOfLease());
                contract.setStampDuty(hlsCusConContract.getPurStampDuty());
                contract.setStampDutyAccruedAmount(hlsCusConContract.getPurStampDutyAccruedAmount());
                contract.setPostFlag(hlsCusConContract.getPurPostFlag());
                contract.setPurStampDuty(hlsCusConContract.getPurStampDuty());
                contract.setPurStampDutyAccruedAmount(hlsCusConContract.getPurStampDutyAccruedAmount());
                contract.setPurPostFlag(hlsCusConContract.getPurPostFlag());
                contract.setPeriodName(hlsCusConContract.getPeriodName());
                list.add(contract);
            }
        }
        return new ResponseData(list);
    }

    @RequestMapping(value = "/gld/stamp/duty/financing")
    @ResponseBody
    public ResponseData selectStampDutyFinancing(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                             @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) throws HlsCusException, ParseException {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusLonContractWithdraw dto = param.toJavaObject(HlsCusLonContractWithdraw.class);
        String yearMonth = param.getString("period_name");
        String sub = yearMonth.substring(yearMonth.indexOf("-") + 1);
        String periodName = yearMonth+"-01";
        String periodName2;
        if(Arrays.asList("01","03","05","07","08","10","12").contains(sub)){
            periodName2 = yearMonth + "-31";
        }else if(Arrays.asList("04","06","09","11").contains(sub)){
            periodName2 = yearMonth + "-30";
        }else{
            periodName2 = yearMonth + "-28";
        }
        dto.setDueDateFromL(periodName);
        dto.setDueDateToL(periodName2);
        return new ResponseData(hlsCusLonContractWithdrawService.selectStampDutyFinancing(dto));
    }

    @RequestMapping(value = "/gld/stamp/duty/select")
    @ResponseBody
    public ResponseData selectStampDuty(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusConContract dto = param.toJavaObject(HlsCusConContract.class);
        return new ResponseData(service.selectStampDuty(dto));
    }

    @RequestMapping(value = "/contract/custom/amortization/selectImportTempList")
    @ResponseBody
    public ResponseData selectImportTempList(HttpServletRequest request, @RequestParam Long headerId,@RequestParam Long contractId) throws ParseException {
        IRequest requestCtx = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        return new ResponseData(this.service.selectImportTempList(headerId, contractId));
    }

    @RequestMapping({"/contract/custom/amortization/import/confirm"})
    @ResponseBody
    public ResponseData confirmImport(@ModelAttribute("_request_data") LeafRequestData requestData, BindingResult result,
                                      HttpServletRequest request, Long contractId) {
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusContractFinanceIncome> dto = param.toJavaList(HlsCusContractFinanceIncome.class);
        this.getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(this.getErrorMessage(result, request));
            return responseData;
        } else {
            IRequest requestCtx = this.createRequestContext(request);
            this.service.importConfirm(requestCtx, dto, contractId);
            return new ResponseData();
        }
    }
}
