package com.hand.hls.fnd.controllers;

import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.BaseException;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fnd.dto.FndBaseRate;
import com.hand.hls.fnd.dto.FndBaseRateSet;
import com.hand.hls.fnd.mapper.FndBaseRateMapper;
import com.hand.hls.fnd.service.FndBaseRateService;
import com.hand.hls.fnd.service.FndBaseRateSetService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


@Controller
public class FndBaseRateSetController extends BaseController {
    @Autowired
    private   FndBaseRateSetService fndBaseRateSetService;
    @Autowired
    private FndBaseRateService fndBaseRateService;

    @Autowired
    private   FndBaseRateMapper fndBaseRateMapper;

    public FndBaseRateSetController() {
    }

    @RequestMapping({"/fnd/baserateset/query"})
    @ResponseBody
    public ResponseData selectByQuery(HttpServletRequest request, FndBaseRateSet fndBaseRateSet, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest iRequest = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        Map para = requestData.getParameter();
        if (para.get("base_rate_set") != null) {
            fndBaseRateSet.setBaseRateSet(para.get("base_rate_set").toString());
        }

        if (para.get("base_rate_type") != null) {
            fndBaseRateSet.setBaseRateType(para.get("base_rate_type").toString());
        }

        if (para.get("description") != null) {
            fndBaseRateSet.setDescription(para.get("description").toString());
        }

        if (para.get("base_rate_type") != null) {
            fndBaseRateSet.setBaseRateType(para.get("base_rate_type").toString());
        }

        if (para.get("currency_name") != null) {
            fndBaseRateSet.setCurrencyName(para.get("currency_name").toString());
        }

        if (para.get("currency_code") != null) {
            fndBaseRateSet.setCurrencyCode(para.get("currency_code").toString());
        }

        if (fndBaseRateSet.getSortname() == null) {
            fndBaseRateSet.setSortname("base_rate_type");
            fndBaseRateSet.setSortorder("asc");
        }

        PageHelper.startPage(pagenum, pagesize);
        List<FndBaseRateSet> datas = this.fndBaseRateSetService.selectByQuery(fndBaseRateSet, pagenum, pagesize);
        return new ResponseData(datas);
    }

    @RequestMapping({"/fnd/baserateset/save"})
    @ResponseBody
    public ResponseData saveHeadAndLine(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData, BindingResult result) throws BaseException {
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<FndBaseRateSet> fndBaseRateSets = parameter.toJavaList(FndBaseRateSet.class);
        this.getValidator().validate(fndBaseRateSets, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(this.getErrorMessage(result, request));
            return responseData;
        } else {
            IRequest iRequest = this.createRequestContext(request);
            RequestHelper.setCurrentRequest(iRequest);
            List<FndBaseRateSet> datas = this.fndBaseRateSetService.queryAll();
            List<FndBaseRate> fndbaserates = new ArrayList();

            for(int i = 0; i < fndBaseRateSets.size(); ++i) {
                FndBaseRateSet fndBaseRateSet = (FndBaseRateSet)fndBaseRateSets.get(i);
                if (fndBaseRateSet.getFndBaseRate() != null) {

                    for (FndBaseRate fndbaserate : fndBaseRateSet.getFndBaseRate()) {
                        FndBaseRate fndBaserate = fndbaserate;
                        System.out.println(fndBaserate.toString());
                        fndbaserates.add(fndBaserate);
                    }

                }

                fndBaseRateSet.setEnabledFlag("Y");
                this.fndBaseRateSetService.batchChildUpdate(iRequest, fndBaseRateSet, fndbaserates, datas);
            }

            return new ResponseData();
        }
    }

    @RequestMapping({"/fnd/baserateset/copy"})
    @ResponseBody
    public ResponseData copyHeadAndLine(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData, BindingResult result) throws BaseException {
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<FndBaseRateSet> fndBaseRateSets = parameter.toJavaList(FndBaseRateSet.class);
        this.getValidator().validate(fndBaseRateSets, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(this.getErrorMessage(result, request));
            return responseData;
        } else {
            IRequest iRequest = this.createRequestContext(request);
            RequestHelper.setCurrentRequest(iRequest);
            List<FndBaseRateSet> datas = this.fndBaseRateSetService.queryAll();
            List<FndBaseRate> fndbaserates = new ArrayList();

            for(int i = 0; i < fndBaseRateSets.size(); ++i) {
                FndBaseRateSet fndBaseRateSet = (FndBaseRateSet)fndBaseRateSets.get(i);
                if (fndBaseRateSet.getFndBaseRate() != null) {
                    for (FndBaseRate fndbaserate : fndBaseRateSet.getFndBaseRate()) {
                        FndBaseRate fndBaserate = fndbaserate;
                        fndbaserates.add(fndBaserate);
                    }
                }

                fndBaseRateSet.setEnabledFlag("Y");
                this.fndBaseRateSetService.batchChildUpdate(iRequest, fndBaseRateSet, fndbaserates, datas);
            }
            //复制明细
            if(fndBaseRateSets.size() >= 2){
                FndBaseRateSet fndBaseRateSet = (FndBaseRateSet)fndBaseRateSets.get(0);
                FndBaseRateSet fndBaseRateSetNew = (FndBaseRateSet)fndBaseRateSets.get(1);
                if (fndBaseRateSet.getBaseRateSet() != null) {
                        List<FndBaseRate> fndBaseRateList = fndBaseRateMapper.selectByBaseRateSet(fndBaseRateSet.getBaseRateSet() );
                        for(FndBaseRate baseRate : fndBaseRateList){
                            baseRate.setBaseRateId(null);
                            baseRate.setBaseRateSet(fndBaseRateSetNew.getBaseRateSet());
                            fndBaseRateService.insertSelective(iRequest , baseRate);
                        }

                }
            }

            return new ResponseData();
        }
    }

    @RequestMapping({"/fnd/baserateset/getCount"})
    @ResponseBody
    public ResponseData getCount() {
        return new ResponseData(this.fndBaseRateSetService.getCount());
    }

    @RequestMapping({"/fnd/baserateset/queryForFinanceContract"})
    @ResponseBody
    public ResponseData queryForFinanceContract(HttpServletRequest request, @RequestBody FndBaseRateSet fndBaseRateSet) {
        return new ResponseData(this.fndBaseRateSetService.queryForFinanceContract(fndBaseRateSet));
    }

    @RequestMapping(
            value = {"/fnd/baserateset/delete"},
            method = {RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<FndBaseRateSet> fndBaseRateSetList = parameter.toJavaList(FndBaseRateSet.class);
        IRequest iRequest = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        this.fndBaseRateSetService.batchDeleteLine(fndBaseRateSetList);
        this.fndBaseRateSetService.batchDelete(fndBaseRateSetList);
        return new ResponseData(fndBaseRateSetList);
    }
}
