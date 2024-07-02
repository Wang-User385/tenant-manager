package com.hand.hls.gld.controllers;

import com.alibaba.fastjson.JSONArray;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractRiskFund;
import com.hand.hls.fin.dto.HlsCusLonContractWithdraw;
import com.hand.hls.gld.dto.HlsCusContractFinanceIncome;
import com.hand.hls.gld.dto.HlsCusGldLonContractFinCost;
import com.hand.hls.gld.service.IGldMonthSettleService;
import com.hand.hls.utils.ResMessageException;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 现金事务控制器
 */
@Controller
public class GldMonthSettleController extends BaseController {

    @Autowired
    private IGldMonthSettleService service;

    //预月结
    @RequestMapping(value = "/gld/contract/finance/income/preMonthSettle")
    @ResponseBody
    public ResponseData preMonthSettle(HttpServletRequest request,String periodName,
                             @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpSession session) throws ResMessageException {

        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsCusContractFinanceIncome> contractFinanceIncomeList = parameter.toJavaList(HlsCusContractFinanceIncome.class);
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.preFinIncomeRecognition(requestCtx, contractFinanceIncomeList,periodName));
    }

    //预月结反冲
    @RequestMapping(value = "/gld/contract/finance/income/preMonthSettleReverse")
    @ResponseBody
    public ResponseData preMonthSettleReverse(HttpServletRequest request,String periodName,
                                    @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpSession session) throws ResMessageException {

        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsCusContractFinanceIncome> contractFinanceIncomeList = parameter.toJavaList(HlsCusContractFinanceIncome.class);
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.reversePreFinIncome(requestCtx, contractFinanceIncomeList,periodName));
    }

    //月结
    @RequestMapping(value = "/gld/contract/finance/income/monthSettle")
    @ResponseBody
    public ResponseData monthSettle(HttpServletRequest request,String periodName,
                                    @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpSession session) throws ResMessageException {

        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsCusContractFinanceIncome> contractFinanceIncomeList = parameter.toJavaList(HlsCusContractFinanceIncome.class);
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.finIncomeRecognition(requestCtx, contractFinanceIncomeList,periodName));
    }

    //印花税计提
    @RequestMapping(value = "/gld/contract/stamp/duty/accrual")
    @ResponseBody
    public ResponseData stampDutyAccrual(HttpServletRequest request,
                                    @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpSession session) throws ResMessageException {

        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsCusConContract> cusConContractList = parameter.toJavaList(HlsCusConContract.class);
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.stampDutyAccrual(requestCtx, cusConContractList));
    }

    //风险金计提
    @RequestMapping(value = "/gld/risk/fund/accrual/check")
    @ResponseBody
    public ResponseData riskFundAccrual(HttpServletRequest request, String periodName,
                                        @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpSession session) throws ResMessageException {

        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsCusConContractRiskFund> conContractRiskFundList = parameter.toJavaList(HlsCusConContractRiskFund.class);
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.riskFundAccrual(requestCtx, conContractRiskFundList, periodName));
    }

    //月结印花税计提(租赁)
    @RequestMapping(value = "/gld/stamp/duty/accrual/check")
    @ResponseBody
    public ResponseData stampDutyAccrualLease(HttpServletRequest request,
                                         @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpSession session) throws ResMessageException {

        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsCusConContract> cusConContractList = parameter.toJavaList(HlsCusConContract.class);
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.stampDutyAccrualLease(cusConContractList));
    }

    //月结印花税计提(融资)
    @RequestMapping(value = "/gld/stamp/duty/accrual/financing/check")
    @ResponseBody
    public ResponseData stampDutyAccrualFinancing(HttpServletRequest request,
                                              @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpSession session) throws ResMessageException {
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsCusLonContractWithdraw> cusLonContractWithdraws = parameter.toJavaList(HlsCusLonContractWithdraw.class);
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.stampDutyAccrualFinancing(cusLonContractWithdraws));
    }

    //融资合同月结
    @RequestMapping(value = "/gld/lon/finance/cost/monthSettle")
    @ResponseBody
    public ResponseData lonMonthSettle(HttpServletRequest request, String periodName, String contractType,
                                       @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpSession session) throws ResMessageException {

        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsCusGldLonContractFinCost> lonContractFinCostList = parameter.toJavaList(HlsCusGldLonContractFinCost.class);
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.lonFinCostRecognition(requestCtx, lonContractFinCostList, periodName, contractType));
    }


}
