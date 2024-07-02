package com.hand.hls.bp.controllers;

import com.hand.hap.mybatis.util.StringUtil;
import com.hand.hls.utils.ResMessageException;
import leaf.service.validation.ParameterNullException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.bp.dto.HlsScoreCalculation;
import com.hand.hls.bp.service.IHlsScoreCalculationService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.validation.BindingResult;

import java.text.ParseException;
import java.util.*;

import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

    @Controller
    public class HlsScoreCalculationController extends BaseController{

    @Autowired
    private IHlsScoreCalculationService service;


    @RequestMapping(value = "/hls/score/calculation/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsScoreCalculation dto = param.toJavaObject(HlsScoreCalculation.class);
        return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));
    }

    @RequestMapping(value = "/hls/score/calculation/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsScoreCalculation> list = param.toJavaList(HlsScoreCalculation.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/hls/score/calculation/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<HlsScoreCalculation> dto = parameter.toJavaList(HlsScoreCalculation.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }



        @RequestMapping({"/hls/score/calculation/selectList"})
        @ResponseBody
        public ResponseData query(HlsScoreCalculation dto, HttpServletRequest request, HttpSession session) {
            IRequest iRequest = this.createRequestContext(request);
            RequestHelper.setCurrentRequest(iRequest);
            if (dto.getObjectId() != null&& dto.getScoreTemplateHdId() != null) {
                if (StringUtil.isEmpty(session.getAttribute("companyId").toString())) {
                    return new ResponseData(false, "请求参数有误！");
                } else {
                    dto.setCompanyId((Long)session.getAttribute("companyId"));
                    return new ResponseData(this.service.selectListOfHsc(dto));
                }
            } else {
                return new ResponseData(false, "请求参数有误！");
            }
        }



        @RequestMapping({"/hls/score/calculation/insertsc1"})
        @ResponseBody
        public ResponseData update(@RequestBody HlsScoreCalculation dto, HttpServletRequest request, HttpSession session) throws ParseException {
            IRequest requestCtx = this.createRequestContext(request);
            if (session.getAttribute("companyId") != null) {
                dto.setCompanyId((Long)session.getAttribute("companyId"));
            }

            if (session.getAttribute("userId") != null) {
                dto.setScoreUserId((Long)session.getAttribute("userId"));
            }

            if (dto.getScoreTemplateHdId() != null && dto.getScoreResultId() != null && dto.getCompanyId() != null && dto.getScoreUserId() != null && dto.getObjectId() != null) {
                dto.setScoreDate(new Date());



                dto = (HlsScoreCalculation)this.service.updateByPrimaryKey(requestCtx, dto);

                //自动计算风险限额
                try {
                    Map map = this.service.clacRiskMoney(requestCtx,dto);
                    dto.setRiskAmount(Double.valueOf(String.valueOf(map.get("riskAmount"))));
                    dto.setRate(String.valueOf(map.get("rate")));
                    dto = (HlsScoreCalculation)this.service.updateByPrimaryKey(requestCtx, dto);
                   // this.service.updateByPrimaryKeySelective()
                } catch (ResMessageException e) {
                    e.printStackTrace();
                } catch (ParameterNullException e) {
                    e.printStackTrace();
                }
                List<HlsScoreCalculation> list = new ArrayList();
                list.add(dto);
                return new ResponseData(list);
            } else {
                return new ResponseData(false, "请求参数有误！");
            }
        }


        @RequestMapping(value = "/hls/score/calculation/incept/sumbit")
        @ResponseBody
        public ResponseData conInceptSubmit(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, final HttpServletRequest request) throws ParameterNullException, ResMessageException {
            IRequest requestContext = createRequestContext(request);
            JSONObject param = (JSONObject) requestData.get("parameter");
            //param.put("modify_date", DateUtil.parseDate(param.get("modify_date").toString()));
            HlsScoreCalculation hlsScoreCalculation = param.toJavaObject(HlsScoreCalculation.class);


            service.conInceptSubmit(requestContext, hlsScoreCalculation);
            return new ResponseData();
        }

        @RequestMapping("/hls/score/calculation/generateAuthorityString")
        public ResponseData generateAuthorityString(HttpServletRequest request) {
            IRequest iRequest = createRequestContext(request);
            String authorityRuleString = service.generateAuthorityString(iRequest);
            return new ResponseData(Arrays.asList(authorityRuleString));
        }
}