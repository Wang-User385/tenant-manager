package com.hand.hls.ruleengine.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.mybatis.util.StringUtil;
import com.hand.hls.ruleengine.dto.GldJeRuleEngineExtend;
import com.hand.hls.ruleengine.dto.HlsRuleEngineCondition;
import com.hand.hls.ruleengine.dto.HlsRuleEngineRoute;
import com.hand.hls.ruleengine.dto.HlsRuleEngineRouteResult;
import com.hand.hls.ruleengine.mapper.GldJeRuleEngineExtendMapper;
import com.hand.hls.ruleengine.mapper.HlsRuleEngineConditionMapper;
import com.hand.hls.ruleengine.mapper.HlsRuleEngineRouteMapper;
import com.hand.hls.ruleengine.mapper.HlsRuleEngineRouteResultMapper;
import com.hand.hls.ruleengine.service.IHLSRuleEngineInitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gaoyang on 2017/5/12.
 */
@Service
public class IHLSRuleEngineInitServiceImpl implements IHLSRuleEngineInitService {

    public static final String PROPERTITY_CONDITION_FILED = "FIELD";

    @Autowired
    private HlsRuleEngineRouteMapper hlsRuleEngineRouteMapper;
    @Autowired
    private HlsRuleEngineRouteResultMapper routeResultMapper;
    @Autowired
    private HlsRuleEngineConditionMapper conditionMapper;
    @Autowired
    private GldJeRuleEngineExtendMapper gldJeRuleEngineExtendMapper;

//    private int resultLength = 0;
//    private List<HlsRuleEngineRoute> routeList = new ArrayList<>();

    private String getConditionValue(String conditionType, String conditionValue, JSONObject jsonObject) {
        String value = null;
        if (PROPERTITY_CONDITION_FILED.equals(conditionType)) {
            String camelConditionValue = StringUtil.underlineToCamelhump(conditionValue);
            if (jsonObject.containsKey(conditionValue)) {
                value = jsonObject.getString(conditionValue);
            }else if(jsonObject.containsKey(camelConditionValue)){
                value = jsonObject.getString(camelConditionValue);
            }
        } else {
            value = conditionValue;
        }
        return value;
    }

    private boolean checkRuleCondition(HlsRuleEngineRoute engineRoute, JSONObject jsonObject) {
        boolean flag = true;
        List<HlsRuleEngineCondition> conditionList = conditionMapper.selectRuleEngineConditionByRouteId(engineRoute.getRouteId());
        if (conditionList != null) {
            for (HlsRuleEngineCondition condition : conditionList) {
                String xValue = getConditionValue(condition.getXconditionType(), condition.getXconditionValue(), jsonObject),
                        yValue1 = condition.getYconditionValue1(),
                        yValue2 = condition.getYconditionValue2();
                String calulateSymbol = condition.getCalulateSymbol();
                if ("=".equals(calulateSymbol)) {
                    if (xValue != null && yValue1 != null && xValue.equals(yValue1)) {
                        flag = true;
                    } else {
                        flag = false;
                        break;
                    }
                } else if ("!=".equals(calulateSymbol)) {
                    if (xValue != null && yValue1 != null && !xValue.equals(yValue1)) {
                        flag = true;
                    } else {
                        flag = false;
                        break;
                    }
                } else if ("<".equals(calulateSymbol)) {
                    try {
                        if (xValue != null && yValue1 != null && Float.parseFloat(xValue) < Float.parseFloat(yValue1)) {
                            flag = true;
                        } else {
                            flag = false;
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        flag = false;
                        break;
                    }
                } else if ("<=".equals(calulateSymbol)) {
                    try {
                        if (xValue != null && yValue1 != null && Float.parseFloat(xValue) <= Float.parseFloat(yValue1)) {
                            flag = true;
                        } else {
                            flag = false;
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        flag = false;
                        break;
                    }
                } else if (">".equals(calulateSymbol)) {
                    try {
                        if (xValue != null && yValue1 != null && Float.parseFloat(xValue) > Float.parseFloat(yValue1)) {
                            flag = true;
                        } else {
                            flag = false;
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        flag = false;
                        break;
                    }
                } else if (">=".equals(calulateSymbol)) {
                    try {
                        if (xValue != null && yValue1 != null && Float.parseFloat(xValue) >= Float.parseFloat(yValue1)) {
                            flag = true;
                        } else {
                            flag = false;
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        flag = false;
                        break;
                    }
                } else if ("between".equals(calulateSymbol)) {
                    try {
                        if (xValue != null && yValue1 != null && yValue2 != null & Float.parseFloat(xValue) >= Float.parseFloat(yValue1) && Float.parseFloat(xValue) <= Float.parseFloat(yValue2)) {
                            flag = true;
                        } else {
                            flag = false;
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        flag = false;
                        break;
                    }
                }
            }
        }
        return flag;
    }

    private int getRuleResult(HlsRuleEngineRoute engineRoute, String[] strArray, int resultLength) {
        List<HlsRuleEngineRouteResult> resultList = routeResultMapper.selectRouteResultByRouteId(engineRoute.getRouteId());
        if (resultList != null) {
            int i = 0;
            for (HlsRuleEngineRouteResult result : resultList) {
                strArray[resultLength + i] = result.getRouteResultValue();
                i++;
            }
            resultLength = resultLength + resultList.size();
        }
        return resultLength;
    }

    private int checkEngineRoute(HlsRuleEngineRoute engineRoute, String[] strArray, JSONObject jsonObject, List<HlsRuleEngineRoute> routeList, int resultLength) {
        List<HlsRuleEngineRoute> childrenList = hlsRuleEngineRouteMapper.selectRuleEngineChildRoute(engineRoute.getRouteId());
        if (childrenList.size() == 0) {
            resultLength = getRuleResult(engineRoute, strArray, resultLength);
            routeList.add(engineRoute);
        } else {
            boolean checkResult = checkRuleCondition(engineRoute, jsonObject);
            if (checkResult) {
                for (HlsRuleEngineRoute child : childrenList) {
                    resultLength = checkEngineRoute(child, strArray, jsonObject, routeList, resultLength);
                }
            }
        }
        return resultLength;
    }

    private int calcResult(IRequest iRequest, JSONObject jsonObject, String[] strArray, List<HlsRuleEngineRoute> routes) {
        int resultLength = 0;
        Long ruleEngineId = jsonObject.getLong("ruleEngineId");
        String getLayoutCodeFlag = jsonObject.getString("getLayoutCodeFlag");
        if ("Y".equals(getLayoutCodeFlag)) {
            //为了提高检索效率，第一个节点使用功能号，默认所有规则匹配必须含功能号
            String functionCode = jsonObject.getString("function_code");
            List<HlsRuleEngineRoute> routeListByLayoutCode = hlsRuleEngineRouteMapper.selectRuleEngineRouteByFunctionCode(ruleEngineId, functionCode);
            if (routeListByLayoutCode != null) {
                for (HlsRuleEngineRoute engineRoute : routeListByLayoutCode) {
                    resultLength = checkEngineRoute(engineRoute, strArray, jsonObject, routes, resultLength);
                }
            }
            if (resultLength > 0) {
                return resultLength;
            }
        }

        List<HlsRuleEngineRoute> routeList = hlsRuleEngineRouteMapper.selectRuleEngineRoute(ruleEngineId);
        if (routeList != null) {
            for (HlsRuleEngineRoute engineRoute : routeList) {
                resultLength = checkEngineRoute(engineRoute, strArray, jsonObject, routes, resultLength);
            }
        }
        return resultLength;
    }

    private void getGldRuleResult(HlsRuleEngineRoute engineRoute, Map<String,String> mapList) {
        GldJeRuleEngineExtend gldJeRuleEngineExtend = new GldJeRuleEngineExtend();
        gldJeRuleEngineExtend.setRouteId(engineRoute.getRouteId());
        List<GldJeRuleEngineExtend> gldResultList = gldJeRuleEngineExtendMapper.select(gldJeRuleEngineExtend);
        List<HlsRuleEngineRouteResult> routeResultList = routeResultMapper.selectRouteResultByRouteId(engineRoute.getRouteId());
        if (gldResultList.size()>0) {
            for(int i=0;i<routeResultList.size();i++){
                String resultValue = routeResultList.get(i).getRouteResultValue();
                String ruleEngineExtendId = gldResultList.get(0).getRuleEngineExtendId().toString();
                if(mapList.get(resultValue)!=null){
                    mapList.put(resultValue,resultValue+"$"+ruleEngineExtendId);
                }else {
                    mapList.put(resultValue, ruleEngineExtendId);
                }
            }
        }
    }

    private void gldCalcResult(IRequest iRequest, JSONObject jsonObject, Map<String, String> mapList, List<HlsRuleEngineRoute> routeList) {
        if (routeList != null) {
            for (HlsRuleEngineRoute engineRoute : routeList) {
                if("Y".equals(engineRoute.getFinnalFlag())){
                    getGldRuleResult(engineRoute, mapList);
                }
            }
        }
    }

    @Override
    public String[] ruleEngineInit(IRequest iRequest, JSONObject jsonObject, List<HlsRuleEngineRoute> routeList) {
        if(routeList == null){
            routeList = new ArrayList<>();
        }
        String[] strArray = new String[1000];
        int resultLength = calcResult(iRequest, jsonObject, strArray, routeList);
        String[] resultArray = new String[resultLength];
        for (int i = 0; i < strArray.length; i++) {
            if (strArray[i] != null) {
                resultArray[i] = strArray[i];
            }
        }
        return resultArray;
    }

    @Override
    public String[] ruleEngineInit(IRequest iRequest, JSONObject jsonObject) {
        return ruleEngineInit(iRequest, jsonObject, null);
    }

    private Map<String,String> gldRuleEngine(IRequest iRequest, JSONObject jsonObject, List<HlsRuleEngineRoute> routeList){
        Map<String,String> mapList = new HashMap<>();
        gldCalcResult(iRequest,jsonObject,mapList,routeList);
        return mapList;
    }

    @Override
    public Map<String,Object> gldRuleEngineInit(IRequest iRequest, JSONObject jsonObject) {
        Map<String,Object> resultMap = new HashMap();
        List<HlsRuleEngineRoute> routeList = new ArrayList<>();
        resultMap.put("routeResult",ruleEngineInit(iRequest,jsonObject,routeList));
        resultMap.put("gldRouteResult",gldRuleEngine(iRequest,jsonObject,routeList));
        return resultMap;
    }
}
