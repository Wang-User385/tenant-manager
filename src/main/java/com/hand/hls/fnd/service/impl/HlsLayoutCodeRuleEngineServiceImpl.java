package com.hand.hls.fnd.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hls.fnd.dto.HlsLayoutCodeRuleEngine;
import com.hand.hls.fnd.mapper.HlsLayoutCodeRuleEngineMapper;
import com.hand.hls.fnd.service.HlsLayoutCodeRuleEngineService;
import com.hand.hls.ruleengine.dto.HlsRuleEngine;
import com.hand.hls.ruleengine.dto.HlsRuleEngineCondition;
import com.hand.hls.ruleengine.dto.HlsRuleEngineRoute;
import com.hand.hls.ruleengine.dto.HlsRuleEngineRouteResult;
import com.hand.hls.ruleengine.mapper.HlsRuleEngineConditionMapper;
import com.hand.hls.ruleengine.mapper.HlsRuleEngineMapper;
import com.hand.hls.ruleengine.mapper.HlsRuleEngineRouteMapper;
import com.hand.hls.ruleengine.mapper.HlsRuleEngineRouteResultMapper;
import com.hand.hls.ruleengine.service.IHlsRuleEngineConditionService;
import com.hand.hls.ruleengine.service.IHlsRuleEngineRouteResultService;
import com.hand.hls.ruleengine.service.IHlsRuleEngineRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author Marshal
 * @date 2019-01-03 13:41
 * @description
 */
@Service
public class HlsLayoutCodeRuleEngineServiceImpl implements HlsLayoutCodeRuleEngineService {

    private static final long LAYOUT_CODE_RULE_ENGINE_ID = 161;

    @Autowired
    HlsLayoutCodeRuleEngineMapper hlsLayoutCodeRuleEngineMapper;

    @Autowired
    HlsRuleEngineMapper hlsRuleEngineMapper;

    @Autowired
    private IHlsRuleEngineRouteService hlsRuleEngineRouteService;

    @Autowired
    private HlsRuleEngineRouteMapper hlsRuleEngineRouteMapper;

    @Autowired
    private HlsRuleEngineConditionMapper hlsRuleEngineConditionMapper;

    @Autowired
    private IHlsRuleEngineConditionService hlsRuleEngineConditionService;

    @Autowired
    private HlsRuleEngineRouteResultMapper resultMapper;

    @Autowired
    private IHlsRuleEngineRouteResultService resultService;

    @Override
    public List<HlsLayoutCodeRuleEngine> query(int pagenum, int pagesize, HlsLayoutCodeRuleEngine condition) {
        PageHelper.startPage(pagenum, pagesize);
        return hlsLayoutCodeRuleEngineMapper.query(condition);
    }

    @Override
    public List<HlsLayoutCodeRuleEngine> submit(List<HlsLayoutCodeRuleEngine> list) {
        for (HlsLayoutCodeRuleEngine item : list) {
            if (item.getLayoutCodeEngineId() == null) {
                //新建hlsRuleEngine
                HlsRuleEngine hlsRuleEngine = new HlsRuleEngine();
                hlsRuleEngine.setRuleEngineType(item.getRuleEngineType());
                hlsRuleEngine.setRuleEngineCode(UUID.randomUUID().toString());
                hlsRuleEngine.setDataSourceId(item.getDataSourceId());
                hlsRuleEngineMapper.insertSelective(hlsRuleEngine);
                //设置ruleEngineCode
                hlsRuleEngine.setRuleEngineCode("layout_" + item.getLayoutCodeEngineCode() + "_" + hlsRuleEngine.getRuleEngineId());
                hlsRuleEngineMapper.updateByPrimaryKey(hlsRuleEngine);

                //为hlsLayoutCodeRuleEngine设置ruleEngineId
                item.setRuleEngineId(hlsRuleEngine.getRuleEngineId());
                hlsLayoutCodeRuleEngineMapper.insertSelective(item);

            } else {
                hlsLayoutCodeRuleEngineMapper.updateByPrimaryKey(item);
                HlsRuleEngine hlsRuleEngine = hlsRuleEngineMapper.selectByPrimaryKey(item.getRuleEngineId());
                hlsRuleEngine.setRuleEngineType(item.getRuleEngineType());
                hlsRuleEngine.setDataSourceId(item.getDataSourceId());
                hlsRuleEngineMapper.updateByPrimaryKey(hlsRuleEngine);
            }
        }
        return list;
    }

    @Override
    public List<HlsLayoutCodeRuleEngine> remove(List<HlsLayoutCodeRuleEngine> list) {
        list.forEach(item -> hlsLayoutCodeRuleEngineMapper.deleteByPrimaryKey(item));
        return list;
    }

    @Override
    @Transactional
    public Map listQuery(IRequest iRequest, int pagenum, int pagesize, Map map) {
        Map result = new HashMap();
        List<HlsRuleEngineRoute> routeList = hlsRuleEngineRouteMapper.selectRuleEnginerRouteTree(LAYOUT_CODE_RULE_ENGINE_ID);
        Map<Long,Map> listMap = new HashMap<>();
        List<Map> listData = new ArrayList<>();
        final Object sortName = map.get("sort_name");
        final Object sortDesc = map.get("sort_order");

        //routeList 转 listMap
        routeList.forEach(node->{
            if (node.getParentRouteId() == null) {  //父节点 function_code
                List<HlsRuleEngineCondition> conditions = hlsRuleEngineConditionMapper.selectRuleEngineConditionByRouteId(node.getRouteId());
                if(conditions.size()>0){
                    String fnCode = conditions.get(0).getYconditionValue1();
                    String enabledFlag = conditions.get(0).getEnabledFlag();
                    if (listMap.get(node.getRouteId()) != null) {
                        listMap.get(node.getRouteId()).put("function_code",fnCode);
                        listMap.get(node.getRouteId()).put("enabled_flag",enabledFlag);
                    } else {
                        Map temp= new HashMap();
                        temp.put("function_code",fnCode);
                        temp.put("enabled_flag",enabledFlag);
                        listMap.put(node.getRouteId(),temp);
                    }
                }
            } else {    //子节点 layout_code
                List<HlsRuleEngineRouteResult> resultList = resultMapper.selectRouteResultByRouteId(node.getRouteId());
                if(resultList.size()>0){
                    String layoutCode = resultList.get(0).getRouteResultValue();
                    String layoutDesc = resultList.get(0).getRouteResultDescription();
                    if (listMap.get(node.getParentRouteId()) != null) {
                        listMap.get(node.getParentRouteId()).put("childRouteId" , node.getRouteId());
                        listMap.get(node.getParentRouteId()).put("layout_code" , layoutCode);
                        listMap.get(node.getParentRouteId()).put("layout_desc" , layoutDesc);
                    } else {
                        Map temp= new HashMap();
                        temp.put("childRouteId",node.getRouteId());
                        temp.put("layout_code",layoutCode);
                        temp.put("layout_desc",layoutDesc);
                        listMap.put(node.getParentRouteId(),temp);
                    }
                }
            }
        });

        //map存入list  过滤条件
        for (Map.Entry<Long, Map> entry : listMap.entrySet()) {
            if (entry.getValue().get("function_code") != null) {
                Map temp = new HashMap();
                Boolean filterFlag = false;
                temp.put("function_code", entry.getValue().get("function_code"));
                temp.put("layout_code", entry.getValue().get("layout_code"));
                temp.put("layout_desc", entry.getValue().get("layout_desc"));
                temp.put("enabled_flag", entry.getValue().get("enabled_flag"));
                temp.put("route_id", entry.getKey());
                temp.put("child_route_id", entry.getValue().get("childRouteId"));
                if(map.get("function_code") != null && temp.get("function_code").toString().toLowerCase().indexOf(map.get("function_code").toString().toLowerCase()) < 0){
                    filterFlag = true;
                }
                if(map.get("layout_code") != null && temp.get("layout_code").toString().toLowerCase().indexOf(map.get("layout_code").toString().toLowerCase()) < 0){
                    filterFlag = true;
                }
                if(map.get("layout_desc") != null && temp.get("layout_desc").toString().toLowerCase().indexOf(map.get("layout_desc").toString().toLowerCase()) < 0){
                    filterFlag = true;
                }
                if(!filterFlag){
                    listData.add(temp);
                }
            }
        }

        //默认按照function code排序
        listData.sort(new Comparator<Map>() {
            String sort_name = (sortName != null ? sortName.toString() : "function_code") ;
            String sort_desc = (sortDesc != null ? sortDesc.toString().toLowerCase() : "asc") ;
            @Override
            public int compare(Map o1, Map o2) {
                if(sort_desc.equals("asc")){
                    return o1.get(sort_name).toString().compareTo(o2.get(sort_name).toString());
                }else{
                    return o2.get(sort_name).toString().compareTo(o1.get(sort_name).toString());
                }
            }
        });


        result.put("total",listData.size());
        result.put("rows",getPageList(listData,pagenum,pagesize));

        return result;
    }

    private List<Map> getPageList(List<Map> allData, int pagenum, int pagesize) {
        Page<Map> pageList = new Page<Map>();
        Integer start = 0;
        Integer end = 0;
        Integer total = allData.size();
        Integer page = total/pagesize;

        if(pagenum == 0 || pagenum == 1 ){
            //返回第一页
            start = 1;
            if(page <= 1){
                end = total;
            }else{
                end = pagesize;
            }
        }else if(pagenum >1 && pagenum < page){
            start = (pagenum - 1) * pagesize + 1;
            end = start + pagesize;
        }else{
            //返回最后一页
            start = (pagenum - 1) * pagesize + 1;
            end = total;
        }

        for(int i=start-1;i<=end-1;i++){
            pageList.add(allData.get(i));
        }
        return pageList;
    }

    @Override
    public List<Map> listSubmit(IRequest iRequest, List<Map> list) {
        for(int i=0;i<list.size();i++){
            Boolean childNewFlag = false;
            String routeId = null;
            String childRouteId = null;
            if(list.get(i).get("route_id") != null){
                routeId = list.get(i).get("route_id").toString();
            }
            if(list.get(i).get("child_route_id") != null){
                childRouteId = list.get(i).get("child_route_id").toString();
            }

            HlsRuleEngineRoute route = new HlsRuleEngineRoute();    //条件节点
            HlsRuleEngineRoute childRoute = new HlsRuleEngineRoute();   //结果节点
            HlsRuleEngineCondition condition = new HlsRuleEngineCondition();    //条件
            HlsRuleEngineRouteResult result = new HlsRuleEngineRouteResult();   //结果

            route.setFinnalFlag("N");
            route.setRuleEngineId(LAYOUT_CODE_RULE_ENGINE_ID);
            if(routeId == null){    //新增条件节点
                hlsRuleEngineRouteService.insertSelective(iRequest,route);
            }else{                  //更新条件节点
                route.setRouteId(Long.parseLong(routeId));
                hlsRuleEngineRouteService.updateByPrimaryKeySelective(iRequest,route);
            }

            //保存条件
            condition.setRouteId(route.getRouteId());
            List<HlsRuleEngineCondition> conditions = hlsRuleEngineConditionService.selectSelective(iRequest,condition);
            if(conditions.size() > 0){
                condition.setConditionId(conditions.get(0).getConditionId());
                condition.setXconditionType("FIELD");
                condition.setXconditionValue("function_code");
                condition.setCalulateSymbol("=");
                condition.setYconditionType("MANUAL");
                condition.setEnabledFlag(list.get(0).get("enabled_flag").toString());
                condition.setYconditionValue1(list.get(0).get("function_code").toString());
                hlsRuleEngineConditionService.updateByPrimaryKeySelective(iRequest,condition);
            }else{
                condition.setXconditionType("FIELD");
                condition.setXconditionValue("function_code");
                condition.setCalulateSymbol("=");
                condition.setYconditionType("MANUAL");
                condition.setEnabledFlag(list.get(0).get("enabled_flag").toString());
                condition.setYconditionValue1(list.get(0).get("function_code").toString());
                hlsRuleEngineConditionService.insertSelective(iRequest,condition);
            }

            //保存结果节点
            childRoute.setFinnalFlag("Y");
            childRoute.setParentRouteId(route.getRouteId());
            childRoute.setRuleEngineId(LAYOUT_CODE_RULE_ENGINE_ID);
            if(childRouteId != null){
                childRoute.setRouteId(Long.parseLong(childRouteId));
                hlsRuleEngineRouteService.updateByPrimaryKeySelective(iRequest,childRoute);
            }else{
                hlsRuleEngineRouteService.insertSelective(iRequest,childRoute);
            }
            //保存结果
            result.setRouteId(childRoute.getRouteId());
            List<HlsRuleEngineRouteResult> results = resultService.selectSelective(iRequest,result);
            if(results.size() > 0){
                result.setRouteResultId(results.get(0).getRouteResultId());
                result.setRouteResultValue(list.get(0).get("layout_code").toString());
                result.setRouteResultDescription(list.get(0).get("layout_desc").toString());
                resultService.updateByPrimaryKeySelective(iRequest,result);
            }else{
                result.setRouteResultValue(list.get(0).get("layout_code").toString());
                result.setRouteResultDescription(list.get(0).get("layout_desc").toString());
                resultService.insertSelective(iRequest,result);
            }


        }
        return list;
    }

    @Override
    public List<Map> listRemove(IRequest iRequest, List<Map> list) {
        for(int i=0;i<list.size();i++){
            String routeId = list.get(i).get("route_id").toString();
            String childRouteId = list.get(i).get("child_route_id").toString();

            if(routeId != null){
                //删除条件
                HlsRuleEngineCondition condition = new HlsRuleEngineCondition();
                condition.setRouteId(Long.parseLong(routeId));
                List<HlsRuleEngineCondition> conditions = hlsRuleEngineConditionService.selectSelective(iRequest,condition);
                hlsRuleEngineConditionService.batchDelete(conditions);
            }
            if(childRouteId != null){
                //删除结果
                HlsRuleEngineRouteResult result = new HlsRuleEngineRouteResult();
                result.setRouteId(Long.parseLong(childRouteId));
                List<HlsRuleEngineRouteResult> results = resultService.selectSelective(iRequest,result);
                resultService.batchDelete(results);
            }

            //删除节点
            HlsRuleEngineRoute route = new HlsRuleEngineRoute();
            route.setRuleEngineId(LAYOUT_CODE_RULE_ENGINE_ID);
            route.setRouteId(Long.parseLong(routeId));
            hlsRuleEngineRouteMapper.deleteByPrimaryKey(route);

            HlsRuleEngineRoute childRoute = new HlsRuleEngineRoute();
            childRoute.setRuleEngineId(LAYOUT_CODE_RULE_ENGINE_ID);
            childRoute.setRouteId(Long.parseLong(childRouteId));
            hlsRuleEngineRouteMapper.deleteByPrimaryKey(childRoute);
        }
        return list;
    }
}
