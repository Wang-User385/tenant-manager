//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.bp.dto.FndScoreResult;
import com.hand.hls.bp.dto.FndScoreResultDtl;
import com.hand.hls.bp.dto.HlsScoreCalculation;
import com.hand.hls.bp.service.IFndScoreResultDtlService;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import com.hand.hls.bp.service.IFndScoreResultService;
import com.hand.hls.bp.service.IHlsScoreCalculationService;
import com.hand.hls.utils.ResMessageException;
import leaf.service.validation.ParameterNullException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FndScoreResultDtlController extends BaseController {
    @Autowired
    private IFndScoreResultDtlService service;
    @Autowired
    private IFndScoreResultService fndScoreResultService;
    @Autowired
    private IHlsScoreCalculationService hlsScoreCalculationService;

    public FndScoreResultDtlController() {
    }

    @RequestMapping({"/fnd/score/result/dtl/lnId"})
    @ResponseBody
    public ResponseData selectscoreTemplateLnId(HttpServletRequest request, Long scoreTemplateHdId) {
        IRequest iRequest = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        List<FndScoreResultDtl> list = this.service.selectscoreTemplateLnId(scoreTemplateHdId);
        return new ResponseData(list);
    }

    @RequestMapping({"/fnd/score/result/dtl/submit"})
    @ResponseBody
    public ResponseData update(HttpServletRequest request, @RequestBody Map parameter) {
        IRequest iRequest = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        if (parameter == null) {
            return new ResponseData(false, "未知错误!");
        } else {
            JSONObject jsonObject = (JSONObject)JSON.toJSON(parameter);
            FndScoreResultDtl dto = (FndScoreResultDtl)jsonObject.toJavaObject(FndScoreResultDtl.class);
            FndScoreResult fndScoreResult = dto.getFndScoreResult();
            dto.set__status("update");
            this.service.saveFnd(iRequest, dto);
            HlsScoreCalculation hlsScoreCalculation = new HlsScoreCalculation();
            hlsScoreCalculation.setScoreId(dto.getScoreId());
            HlsScoreCalculation scoreCalculation = hlsScoreCalculationService.selectByPrimaryKey(iRequest,hlsScoreCalculation);
            if(scoreCalculation!=null){
                Map map = null;
                try {
                    map = hlsScoreCalculationService.clacRiskMoney(iRequest,scoreCalculation);
                } catch (ResMessageException e) {
                    e.printStackTrace();
                } catch (ParameterNullException e) {
                    e.printStackTrace();
                }
                scoreCalculation.setRiskAmount(Double.valueOf(String.valueOf(map.get("riskAmount"))));
                scoreCalculation.setRate(String.valueOf(map.get("rate")));
                hlsScoreCalculationService.updateByPrimaryKey(iRequest, scoreCalculation);
            }
            String value = String.valueOf(jsonObject.get("scoreValue"));
            if(!"null".equals(value)){
                Double scoreValue = Double.valueOf(value);
                fndScoreResult.setScoreValue(scoreValue);
            }


            fndScoreResult.setScoreResultId(dto.getScoreResultId());
            if(jsonObject.get("scoreGrade")!=null){
                fndScoreResult.setScoreGrade(jsonObject.get("scoreGrade").toString());
            }
            fndScoreResultService.updateByPrimaryKey(iRequest,fndScoreResult);
            return new ResponseData(true, "保存成功!");
        }
    }

    @RequestMapping({"/fnd/score/result/dtl/remove"})
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<FndScoreResultDtl> dto) {
        IRequest iRequest = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        this.service.batchDelete(dto);
        return new ResponseData();
    }

    @RequestMapping({"/fnd/score/result/dtl/selectTotalScore"})
    @ResponseBody
    public Double selectTotalScore(FndScoreResultDtl dto, HttpServletRequest request) {
        IRequest iRequest = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        return dto != null && dto.getScoreTemplateLnId() != null && dto.getScoreResultId() != null ? this.service.selectTotalScore(dto) : 0.0D;
    }
}
