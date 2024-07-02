//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
package com.hand.hls.bp.components;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hls.bp.components.CalculateUtil;
import com.hand.hls.bp.dto.FndScoreResult;
import com.hand.hls.bp.dto.FndScoreResultDtl;
import com.hand.hls.bp.dto.FndScoreTargetValues;
import com.hand.hls.bp.dto.FndScoreTemplateHdValue;
import com.hand.hls.bp.dto.FndScoreTemplateLn;
import com.hand.hls.bp.dto.FndScoreTemplateLnValue;
import com.hand.hls.bp.dto.HlsScoreTarget;
import com.hand.hls.bp.mapper.FndScoreResultDtlMapper;
import com.hand.hls.bp.mapper.FndScoreResultMapper;
import com.hand.hls.bp.mapper.FndScoreTargetValuesMapper;
import com.hand.hls.bp.mapper.FndScoreTemplateHdValueMapper;
import com.hand.hls.bp.mapper.FndScoreTemplateLnMapper;
import com.hand.hls.bp.mapper.FndScoreTemplateLnValueMapper;
import com.hand.hls.bp.mapper.HlsScoreTargetMapper;
import com.hand.hls.bp.service.IFndScoreResultDtlService;
import com.hand.hls.bp.service.IFndScoreResultService;
import com.hand.hls.fnd.dto.HlsDbDataSourceColumn;
import com.hand.hls.fnd.mapper.HlsDbDataSourceColumnMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(
        rollbackFor = {Exception.class}
)
public class CustomerLevelCaculator {
    private Long scoreTargetId;
    private Long scoreTemplateLnId;
    private Long scoreTargetValueId;
    private FndScoreResultDtl scoreResultDtl;
    private HlsDbDataSourceColumn hlsDbDataSourceColumn;
    private HlsScoreTarget hlsScoreTarget;
    private String columnName;
    private String targetValueType;
    private FndScoreTemplateLnValue fndScoreTemplateLnValue = new FndScoreTemplateLnValue();
    private StringBuffer sqlpart;
    private StringBuffer sqlparam;
    @Autowired
    private FndScoreTemplateHdValueMapper fndScoreTemplateHdValueMapper;
    @Autowired
    private FndScoreTemplateLnMapper fndScoreTemplateLnMapper;
    @Autowired
    private FndScoreTemplateLnValueMapper fndScoreTemplateLnValueMapper;
    @Autowired
    private IFndScoreResultDtlService fndScoreResultDtlService;
    @Autowired
    private HlsDbDataSourceColumnMapper hlsDbDataSourceColumnMapper;
    @Autowired
    private HlsScoreTargetMapper hlsScoreTargetMapper;
    @Autowired
    private IFndScoreResultService fndScoreResultService;
    @Autowired
    private FndScoreResultMapper fndScoreResultMapper;
    @Autowired
    private FndScoreTargetValuesMapper fndScoreTargetValuesMapper;
    @Autowired
    private FndScoreResultDtlMapper fndScoreResultDtlMapper;

    public CustomerLevelCaculator() {
    }

    public Long calEntry(IRequest request, Long scoreTemplateHdId, JSONObject jsonObject) {
        FndScoreResult result = new FndScoreResult();
        result = (FndScoreResult)this.fndScoreResultService.insert(request, result);
        FndScoreTemplateHdValue fnd = new FndScoreTemplateHdValue();
        fnd.setScoreTemplateHdId(scoreTemplateHdId);
        Double totalGrad = this.calMain(scoreTemplateHdId, (Long)null, jsonObject, result.getScoreResultId(), request);
        String scoreGrade = "";
        List<FndScoreTemplateHdValue> FndScoreTemplateHdValues = this.fndScoreTemplateHdValueMapper.select(fnd);
        Iterator var9 = FndScoreTemplateHdValues.iterator();

        while(var9.hasNext()) {
            FndScoreTemplateHdValue child = (FndScoreTemplateHdValue)var9.next();
            String leftBracket = child.getLeftBracket();
            String rightBracket = child.getRightBracket();
            String fromValue = child.getFromValue();
            String toValue = child.getToValue();
            if ("(".equals(child.getLeftBracket())) {
                leftBracket = "<";
            } else if ("[".equals(child.getLeftBracket())) {
                leftBracket = "<=";
            } else {
                if (child.getLeftBracket() != null && !"".equals(child.getLeftBracket())) {
                    throw new IllegalArgumentException("区间左定义出现错误");
                }

                leftBracket = "";
            }

            if (")".equals(child.getRightBracket())) {
                rightBracket = "<";
            } else if ("]".equals(child.getRightBracket())) {
                rightBracket = "<=";
            } else {
                if (child.getRightBracket() != null && !"".equals(child.getRightBracket())) {
                    throw new IllegalArgumentException("区间右定义出现错误");
                }

                rightBracket = "";
            }

            fromValue = child.getFromValue();
            if (fromValue == null) {
                fromValue = "0";
            }

            toValue = child.getToValue();
            if (toValue == null) {
                toValue = "0";
            }

            this.sqlparam = new StringBuffer("");
            if ("".equals(leftBracket) && "0".equals(fromValue)) {
                this.sqlparam.append("and").append(" ").append(totalGrad.toString()).append(rightBracket).append(toValue);
            } else if ("0".equals(toValue) && "".equals(rightBracket)) {
                this.sqlparam.append("and").append(" ").append(fromValue).append(leftBracket).append(totalGrad.toString());
            } else {
                this.sqlparam.append("and").append(" ").append(fromValue).append(leftBracket).append(totalGrad.toString()).append(" and ").append(totalGrad.toString()).append(rightBracket).append(toValue);
            }

            Map map = new HashMap();
            map.put("scoreTemplateHdId", scoreTemplateHdId);
            map.put("sqlparam", this.sqlparam);
            List<FndScoreTemplateHdValue> list = this.fndScoreTemplateHdValueMapper.getHdValue(map);
            if (list.size() > 0 && list != null) {
                scoreGrade = child.getScoreGrade();
                break;
            }
        }

        if(totalGrad!=null&&totalGrad!=0.0){
            result.setScoreValue(new Double(totalGrad));
            result.setScoreGrade(scoreGrade);
        }

        result.setScoreTemplateHdId(scoreTemplateHdId);
        result.setObjectVersionNumber((Long)null);
        result = (FndScoreResult)((IFndScoreResultService)this.fndScoreResultService.self()).updateByPrimaryKey(request, result);
        return result.getScoreResultId();
    }

    public void entrySec(IRequest request, Long scoreResultId) {
        FndScoreResult fndScoreResult = new FndScoreResult();
        fndScoreResult.setScoreResultId(scoreResultId);
        fndScoreResult = (FndScoreResult)this.fndScoreResultMapper.selectOne(fndScoreResult);
        Long hid = fndScoreResult.getScoreTemplateHdId();
        Double scorevalue = this.calc(request, scoreResultId, (Long)null);
        String scoreGrade = "";
        FndScoreTemplateHdValue fnd = new FndScoreTemplateHdValue();
        fnd.setScoreTemplateHdId(hid);
        List<FndScoreTemplateHdValue> FndScoreTemplateHdValues = this.fndScoreTemplateHdValueMapper.select(fnd);
        Iterator var9 = FndScoreTemplateHdValues.iterator();

        while(var9.hasNext()) {
            FndScoreTemplateHdValue child = (FndScoreTemplateHdValue)var9.next();
            String leftBracket = child.getLeftBracket();
            String rightBracket = child.getRightBracket();
            String fromValue = child.getFromValue();
            String toValue = child.getToValue();
            if ("(".equals(child.getLeftBracket())) {
                leftBracket = "<";
            } else if ("[".equals(child.getLeftBracket())) {
                leftBracket = "<=";
            } else {
                if (child.getLeftBracket() != null && !"".equals(child.getLeftBracket())) {
                    throw new IllegalArgumentException("区间左定义出现错误");
                }

                leftBracket = "";
            }

            if (")".equals(child.getRightBracket())) {
                rightBracket = "<";
            } else if ("]".equals(child.getRightBracket())) {
                rightBracket = "<=";
            } else {
                if (child.getRightBracket() != null && !"".equals(child.getRightBracket())) {
                    throw new IllegalArgumentException("区间右定义出现错误");
                }

                rightBracket = "";
            }

            fromValue = child.getFromValue();
            if (fromValue == null) {
                fromValue = "0";
            }

            toValue = child.getToValue();
            if (toValue == null) {
                toValue = "0";
            }

            this.sqlparam = new StringBuffer("");
            if ("".equals(leftBracket) && "0".equals(fromValue)) {
                this.sqlparam.append("and").append(" ").append(scorevalue.toString()).append(rightBracket).append(toValue);
            } else if ("0".equals(toValue) && "".equals(rightBracket)) {
                this.sqlparam.append("and").append(" ").append(fromValue).append(leftBracket).append(scorevalue.toString());
            } else {
                this.sqlparam.append("and").append(" ").append(fromValue).append(leftBracket).append(scorevalue.toString()).append(" and ").append(scorevalue.toString()).append(rightBracket).append(toValue);
            }

            Map map = new HashMap();
            map.put("scoreTemplateHdId", hid);
            map.put("sqlparam", this.sqlparam);
            List<FndScoreTemplateHdValue> list = this.fndScoreTemplateHdValueMapper.getHdValue(map);
            if (list.size() > 0 && list != null) {
                scoreGrade = child.getScoreGrade();
                break;
            }
        }

        if(scorevalue!=null){
            fndScoreResult.setScoreValue(new Double(scorevalue));
            fndScoreResult.setScoreGrade(scoreGrade);
        }

        fndScoreResult.setScoreTemplateHdId(hid);
        ((IFndScoreResultService)this.fndScoreResultService.self()).updateByPrimaryKey(request, fndScoreResult);
    }

    public Double calc(IRequest iRequest, Long scoreResultId, Long parentId) {
        FndScoreResultDtl dtl = new FndScoreResultDtl();
        dtl.setScoreResultId(scoreResultId);
        List<FndScoreResultDtl> dtls = null;
        FndScoreTemplateLn fstln = new FndScoreTemplateLn();
        List fndScoreTemplateLns;
        Iterator var10;
        FndScoreResultDtl d;
        if (parentId == null) {
            dtls = new ArrayList();
            FndScoreResult f = new FndScoreResult();
            f.setScoreResultId(scoreResultId);
            FndScoreResult fsr = (FndScoreResult)this.fndScoreResultMapper.selectOne(f);
            fstln.setScoreTemplateHdId(fsr.getScoreTemplateHdId());
            fndScoreTemplateLns = this.fndScoreTemplateLnMapper.selectLevelOne(fstln);
            var10 = fndScoreTemplateLns.iterator();

            while(var10.hasNext()) {
                FndScoreTemplateLn child = (FndScoreTemplateLn)var10.next();
                Long lineId = child.getScoreTemplateLnId();
                FndScoreResultDtl fsrd = new FndScoreResultDtl();
                fsrd.setScoreTemplateLnId(lineId);
                fsrd.setScoreResultId(scoreResultId);
                FndScoreResultDtl fndScoreResultDtl = (FndScoreResultDtl)this.fndScoreResultDtlMapper.selectOne(fsrd);
                if (fndScoreResultDtl != null) {
                    dtls.add(fndScoreResultDtl);
                }
            }
        } else {
            dtls = new ArrayList();
            fstln.setParentLnId(parentId);
            fndScoreTemplateLns = this.fndScoreTemplateLnMapper.select(fstln);
            Iterator var15 = fndScoreTemplateLns.iterator();

            while(var15.hasNext()) {
                FndScoreTemplateLn child = (FndScoreTemplateLn)var15.next();
                Long lineId = child.getScoreTemplateLnId();
                d = new FndScoreResultDtl();
                d.setScoreTemplateLnId(lineId);
                d.setScoreResultId(scoreResultId);
                dtls.add(this.fndScoreResultDtlMapper.selectOne(d));
            }
        }

        Double totalScore = 0.0D;
        Double targetScore = 0.0D;
        var10 = dtls.iterator();

        while(var10.hasNext()) {
            d = (FndScoreResultDtl)var10.next();
            if (d != null) {
                if ("N".equals(d.getSummaryFlag())) {
                    targetScore = d.getTargetScore();
                } else {
                    targetScore = this.calc(iRequest, scoreResultId, d.getScoreTemplateLnId());
                }

                FndScoreResultDtl newScore = (FndScoreResultDtl)this.fndScoreResultDtlMapper.selectByPrimaryKey(d.getScoreResultDtlId());
                newScore.setTargetScore(CalculateUtil.mul(newScore.getWeightValue(), targetScore));
                ((IFndScoreResultDtlService)this.fndScoreResultDtlService.self()).updateByPrimaryKeySelective(iRequest, newScore);
                totalScore = CalculateUtil.add(newScore.getTargetScore(), totalScore);
            }
        }

        return totalScore;
    }

    private Double calMain(Long scoreTemplateHdId, Long parentLnId, JSONObject jsonObject, Long scoreResultId, IRequest request) {
        Double total_score_value = 0.0D;
        Double dtl_score_value = 0.0D;
        new HashMap();
        FndScoreTemplateLn fstln = new FndScoreTemplateLn();
        fstln.setScoreTemplateHdId(scoreTemplateHdId);
        List fndScoreTemplateLns;
        if (parentLnId != null) {
            fstln.setParentLnId(parentLnId);
            fndScoreTemplateLns = this.fndScoreTemplateLnMapper.select(fstln);
        } else {
            fndScoreTemplateLns = this.fndScoreTemplateLnMapper.selectLevelOne(fstln);
        }

        Iterator var11 = fndScoreTemplateLns.iterator();

        while(var11.hasNext()) {
            FndScoreTemplateLn fndScoreTemplateLn = (FndScoreTemplateLn)var11.next();
            if (!"N".equals(fndScoreTemplateLn.getEnabledFlag()) && fndScoreTemplateLn.getEnabledFlag() != null) {
                this.scoreResultDtl = new FndScoreResultDtl();
                Long sid = null;
                Long dsColumnId = fndScoreTemplateLn.getDataSourceColumnId();
                String value = "";
                if (dsColumnId != null) {
                    this.hlsDbDataSourceColumn = new HlsDbDataSourceColumn();
                    this.hlsDbDataSourceColumn.setDataSourceColumnId(dsColumnId);
                    this.hlsDbDataSourceColumn = (HlsDbDataSourceColumn)this.hlsDbDataSourceColumnMapper.selectByPrimaryKey(this.hlsDbDataSourceColumn);
                    if (this.hlsDbDataSourceColumn != null) {
                        this.columnName = this.hlsDbDataSourceColumn.getColumnName();
                        Long dsId = this.hlsDbDataSourceColumn.getDataSourceId();
                        JSONObject defaultJson = JSON.parseObject(JSON.toJSONString(jsonObject.get(dsId.toString())));
                        if (defaultJson == null) {
                            throw new IllegalArgumentException("json中数据id无对应的数据");
                        }

                        JSONObject defaultValue = JSON.parseObject(JSON.toJSONString(defaultJson.get("default")));
                        if (defaultValue == null) {
                            throw new IllegalArgumentException("json中的default中没有对应的值");
                        }

                        value = (defaultValue.get(this.columnName) != null ? defaultValue.get(this.columnName) : "").toString();
                        if ("null".equals(value)) {
                            value = null;
                        }
                    }
                }

                if ("N".equals(fndScoreTemplateLn.getSummaryFlag())) {
                    if (dsColumnId != null) {
                        HashMap map = this.calcDtl(fndScoreTemplateLn, value);
                        sid = (Long)map.get("scoreTargetValueId");
                        dtl_score_value = (Double)map.get("value") != null ? (Double)map.get("value") : 0.0D;
                        String dtl_score_grade = (String)map.get("grade");
                        this.scoreResultDtl.setTargetScoreGrade(dtl_score_grade);
                    } else {
                        this.scoreResultDtl.setTargetScoreGrade((String)null);
                    }
                } else {
                    if (!"Y".equals(fndScoreTemplateLn.getSummaryFlag())) {
                        throw new IllegalArgumentException("汇总指标取值错误");
                    }

                    dtl_score_value = this.calMain(scoreTemplateHdId, fndScoreTemplateLn.getScoreTemplateLnId(), jsonObject, scoreResultId, request);
                    this.scoreResultDtl.setTargetScoreGrade((String)null);
                }

                dtl_score_value = CalculateUtil.mul(dtl_score_value, fndScoreTemplateLn.getWeightValue() != null ? fndScoreTemplateLn.getWeightValue() : 0.0D);
                total_score_value = CalculateUtil.add(dtl_score_value, total_score_value);
                this.scoreResultDtl.setScoreResultId(scoreResultId);
                this.scoreResultDtl.setParentResultDtlId(this.scoreResultDtl.getScoreResultDtlId());
                this.scoreResultDtl.setScoreTemplateLnId(fndScoreTemplateLn.getScoreTemplateLnId());
                this.scoreResultDtl.setSummaryFlag(fndScoreTemplateLn.getSummaryFlag());
                this.scoreResultDtl.setScoreTargetId(fndScoreTemplateLn.getScoreTargetId());
                this.scoreResultDtl.setWeightValue(fndScoreTemplateLn.getWeightValue());
                this.scoreResultDtl.setTargetValue(value);
                if (sid != null) {
                    this.scoreResultDtl.setScoreTargetValueId(sid);
                } else {
                    this.scoreResultDtl.setScoreTargetValueId((Long)null);
                }

                this.scoreResultDtl.setTargetScore(dtl_score_value);
                this.scoreResultDtl.setTargetScoreOriginal(dtl_score_value);
                this.scoreResultDtl.setScoreResultDtlId((Long)null);
                ((IFndScoreResultDtlService)this.fndScoreResultDtlService.self()).insert(request, this.scoreResultDtl);
            }
        }

        return total_score_value;
    }

    private HashMap calcDtl(FndScoreTemplateLn fndScoreTemplateLn, String value) {
        HashMap hash = new HashMap();
        this.scoreTargetId = fndScoreTemplateLn.getScoreTargetId();
        this.hlsScoreTarget = new HlsScoreTarget();
        this.hlsScoreTarget.setScoreTargetId(this.scoreTargetId);
        this.targetValueType = ((HlsScoreTarget)this.hlsScoreTargetMapper.selectOne(this.hlsScoreTarget)).getTargetValueType();
        if (value != null) {
            if ("REGION".equals(this.targetValueType)) {
                hash = this.calRegion(fndScoreTemplateLn, hash, value);
            } else if ("CHAR".equals(this.targetValueType)) {
                hash = this.calChar(fndScoreTemplateLn, hash, value);
            } else if (this.targetValueType == null) {
                throw new IllegalArgumentException("指标值类型丢失");
            }
        }

        return hash;
    }

    private HashMap calChar(FndScoreTemplateLn fndScoreTemplateLn, HashMap hash, String dsvalue) {
        this.scoreTemplateLnId = fndScoreTemplateLn.getScoreTemplateLnId();
        this.fndScoreTemplateLnValue.setScoreTemplateLnId(this.scoreTemplateLnId);
        List<FndScoreTemplateLnValue> fndScoreTemplateLnValues = this.fndScoreTemplateLnValueMapper.select(this.fndScoreTemplateLnValue);
        Long stid = fndScoreTemplateLn.getScoreTargetId();
        FndScoreTargetValues fndScoreTargetValues = new FndScoreTargetValues();
        fndScoreTargetValues.setScoreTargetId(stid);
        fndScoreTargetValues.setFixedTargetValue(dsvalue);
        fndScoreTargetValues = (FndScoreTargetValues)this.fndScoreTargetValuesMapper.selectOne(fndScoreTargetValues);
        Long id = fndScoreTargetValues.getScoreTargetValueId();
        Iterator var8 = fndScoreTemplateLnValues.iterator();

        while(var8.hasNext()) {
            FndScoreTemplateLnValue child = (FndScoreTemplateLnValue)var8.next();
            if (id == child.getScoreTargetValueId()) {
                this.scoreTargetValueId = child.getScoreTargetValueId();
                String grade = child.getScoreGrade();
                Double value = child.getScoreValue();
                Long scoreTargetValueId = child.getScoreTargetValueId();
                hash.put("scoreTargetValueId", scoreTargetValueId);
                hash.put("grade", grade);
                hash.put("value", value);
                break;
            }
        }

        return hash;
    }

    private HashMap calRegion(FndScoreTemplateLn fndScoreTemplateLn, HashMap hash, String dsValue) {
        this.scoreTemplateLnId = fndScoreTemplateLn.getScoreTemplateLnId();
        this.fndScoreTemplateLnValue.setScoreTemplateLnId(this.scoreTemplateLnId);
        List<FndScoreTemplateLnValue> fndScoreTemplateLnValues = this.fndScoreTemplateLnValueMapper.select(this.fndScoreTemplateLnValue);
        Long stid = fndScoreTemplateLn.getScoreTargetId();
        FndScoreTargetValues fndScoreTargetValues = new FndScoreTargetValues();
        fndScoreTargetValues.setScoreTargetId(stid);
        HashMap map = new HashMap();
        map.put("fndScoreTargetValues",fndScoreTargetValues);
        map.put("scoreTargetId",fndScoreTargetValues.getScoreTargetId());
        map.put("scoreTemplateHdId",fndScoreTemplateLn.getScoreTemplateHdId());
        List<FndScoreTargetValues> list = this.fndScoreTargetValuesMapper.getTargetValueObj1(map);
        String paramValue;
        if (dsValue != null && !"".equalsIgnoreCase(dsValue)) {
            paramValue = dsValue;
        } else {
            paramValue = "0";
        }

        Iterator var9 = list.iterator();

        while(var9.hasNext()) {
            FndScoreTargetValues child = (FndScoreTargetValues)var9.next();
            String leftBracket;
            if ("(".equals(child.getLeftBracket())) {
                leftBracket = "<";
            } else if ("[".equals(child.getLeftBracket())) {
                leftBracket = "<=";
            } else {
                if (child.getLeftBracket() != null && !"".equals(child.getLeftBracket())) {
                    throw new IllegalArgumentException("区间左定义出现错误");
                }

                leftBracket = "";
            }

            String rightBracket;
            if (")".equals(child.getRightBracket())) {
                rightBracket = "<";
            } else if ("]".equals(child.getRightBracket())) {
                rightBracket = "<=";
            } else {
                if (child.getRightBracket() != null && !"".equals(child.getRightBracket())) {
                    throw new IllegalArgumentException("区间右定义出现错误");
                }

                rightBracket = "";
            }

            String fromValue = child.getFromValue();
            if (fromValue == null) {
                fromValue = "0.0";
            }

            String toValue = child.getToValue();
            if (toValue == null) {
                toValue = "0.0";
            }
            //
            if(paramValue!=null){
                //当fromValue 为空时，paramValue大于toValue时直接跳过当前循环
                if("0.0".equals(toValue)||toValue==null){
                    BigDecimal toValue2 = new BigDecimal(0.0);
                    if("0.0".equals(fromValue)&&new BigDecimal(paramValue.trim()).compareTo(toValue2)==1){
                        continue;
                    }
                }else{
                    System.out.println("toValue---->"+toValue);
                    BigDecimal toValue2 = new BigDecimal(toValue.trim());

                    if("0.0".equals(fromValue)&&new BigDecimal(paramValue).compareTo(toValue2)==1){
                        continue;
                    }
                }


                //当toValue 为空时，paramValue小于toValue时直接跳过当前循环
                if("0.0".equals(fromValue)||fromValue==null){
                    BigDecimal fromValue2 = new BigDecimal(0.0);
                    if("0.0".equals(toValue)&&new BigDecimal(paramValue).compareTo(fromValue2)==-1){
                        continue;
                    }
                }else{
                    System.out.println("fromValue---->"+fromValue);
                    BigDecimal fromValue2 = new BigDecimal(fromValue.trim());

                    if("0.0".equals(toValue)&&new BigDecimal(paramValue).compareTo(fromValue2)==-1){
                        continue;
                    }
                }


            }


            this.sqlpart = new StringBuffer("");


           /* if ("".equals(leftBracket) && "0.0".equals(toValue)) {
                this.sqlpart.append(" and t.to_value is null ");
            } else {
                this.sqlpart.append(" and t.to_value ").append(leftBracket).append(paramValue);
            }

            if ("".equals(rightBracket) && "0.0".equals(fromValue)) {
                this.sqlpart.append(" and t.from_value is null ");
            } else {
                this.sqlpart.append(" and ").append(paramValue).append(rightBracket).append(" t.from_value ");
            }*/

            if ("".equals(leftBracket) && "0.0".equals(fromValue)) {
                this.sqlpart.append(" and t.from_value is null ");
            } else {
                this.sqlpart.append(" and t.from_value ").append(leftBracket).append(paramValue);
            }

            if ("".equals(rightBracket) && "0.0".equals(toValue)) {
                this.sqlpart.append(" and t.to_value is null ");
            } else {
                this.sqlpart.append(" and ").append(paramValue).append(rightBracket).append(" t.to_value ");
            }

            Map sqlMap = new HashMap();
            sqlMap.put("sqlPart", this.sqlpart.toString());
            sqlMap.put("scoreTargetId", stid);
            sqlMap.put("scoreTemplateHdId",fndScoreTemplateLn.getScoreTemplateHdId());
            List<FndScoreTargetValues> res = this.fndScoreTargetValuesMapper.getTargetValueObj2(sqlMap);
            if (res.size() > 0 && res != null) {
                Iterator var17 = fndScoreTemplateLnValues.iterator();
                Iterator r = res.iterator();
                FndScoreTargetValues fstv = (FndScoreTargetValues)r.next();
                while(var17.hasNext()) {
                    FndScoreTemplateLnValue fstlv = (FndScoreTemplateLnValue)var17.next();

                    if (fstv.getScoreTargetValueId().equals(fstlv.getScoreTargetValueId())) {

                            String grade = fstlv.getScoreGrade();
                            Double value = fstlv.getScoreValue();
                            Long scoreTargetValueId = fstlv.getScoreTargetValueId();
                            hash.put("scoreTargetValueId", scoreTargetValueId);
                            hash.put("grade", grade);
                            hash.put("value", value);


                        return hash;
                    }
                }
            }
        }

        return hash;
    }
}
