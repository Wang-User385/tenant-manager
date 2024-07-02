package com.hand.hls.utils;

import static leaf.bean.LeafRequestData.FIELD_PARAMETER;
import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import leaf.bean.LeafRequestData;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.util.IRecordFilter;


/**
 * Created with IntelliJ IDEA.
 * User: yang
 * Date: 2018/8/22
 * Time: 10:48
 */
public class CompositeMapUtils {
    private static Logger logger = LoggerFactory.getLogger(CompositeMapUtils.class);

    private static final String COMPOSITE_MAP_ROOT_NAME = "parameter";
    private static final String COMPOSITE_MAP_DEFAULT_CHILD_NAME = "record";
    public static final String ANY_VALUE = "*";
    public static final String NULL_VALUE = "null";

    private static final String[] KEY_FIND_SCRIPT = {"server-script", "script"};


    public static CompositeMap fromMapList(List<Map<String, Object>> list) {
        CompositeMap compositeMap = new CompositeMap();
        if (list != null) {
            for (Map<String, Object> o : list) {
                if (o != null) {
                    CompositeMap map = new CompositeMap();
                    map.putAll(o);
                    compositeMap.addChild(map);
                }
            }
        }
        return compositeMap;
    }


    /**
     * 将一个 LeafRequestData 转换成CompositeMap
     *
     * @param requestData 需要转换的 LeafRequestData
     * @return CompositeMap
     */
    public static CompositeMap toCompositeMap(LeafRequestData requestData) {
        CompositeMap map = new CompositeMap(COMPOSITE_MAP_ROOT_NAME);
        requestData.forEach((k, v) -> {
            if (!k.equals("_request_data") && !k.equals(COMPOSITE_MAP_ROOT_NAME)) {
                map.put(k, v);
            }
        });
        Object object = requestData.get(FIELD_PARAMETER);
        addChild(map, object);
        return map;
    }

    /**
     * 将一个JavaBean转化为 CompositeMap
     *
     * @param o
     * @return
     */
    public static CompositeMap toCompositeMapAnswer(boolean success, Object o) {
        CompositeMap compositeMap = toCompositeMap(o);
        compositeMap.put("success", success);
        return compositeMap;
    }

    /**
     * 将一个JavaBean转化为 CompositeMap
     *
     * @param o
     * @return
     */
    public static CompositeMap toCompositeMap(Object o) {
        CompositeMap answer = new CompositeMap();
        if (o instanceof CompositeMap) {
            return answer;
        }
        if (o instanceof Map) {
            answer.putAll((Map) o);
            return answer;
        }
        //驼峰转下划线
        try {
            Map<String, String> describe = BeanUtils.describe(o);
            Map map = JSON.parseObject(JsonUtils.toSnakeJsonString4Map(describe), Map.class);
            answer.putAll(map);
        } catch (Exception e) {
            logger.error("error when converting object to CompositeMap ,return empty CompositeMap instead", e);
            return answer;
        }
        return answer;
    }

    /**
     * 添加 record 节点
     *
     * @param map
     * @param param
     */
    public static void addChild(CompositeMap map, Object param) {
        if (param == null) {
            logger.warn("error when adding Child into {} , param can not be null", map);
            return;
        }
        if (param instanceof Map) {
            addChild(map, param, COMPOSITE_MAP_DEFAULT_CHILD_NAME);
        } else if (param instanceof List) {
            List records = (List) param;
            records.forEach(record -> addChild(map, record, COMPOSITE_MAP_DEFAULT_CHILD_NAME));
        }
    }

    /**
     * 根据根属性 递归 加入Child
     */
    public static void addChild(CompositeMap map, Object param, String name) {
        if (param instanceof Map) {
            Map paramMap = (Map) param;
            //判断一个 Map 是否包含一个Child
            List<String> childsKey = getChildsKey(paramMap);
            CompositeMap childCompsite = new CompositeMap(name);
            //设置属性
            paramMap.forEach((k, v) -> {
                if (!childsKey.contains(k) && !k.equals(COMPOSITE_MAP_ROOT_NAME)) {
                    childCompsite.put(k, v);
                }
            });
            for (String key : childsKey) {
                Object o = paramMap.get(key);
                addChild(childCompsite, o, key);
            }
            map.addChild(childCompsite);
        } else if (param instanceof List) {
            CompositeMap childList = new CompositeMap(name);
            List paramList = (List) param;
            paramList.forEach(list -> {
                addChild(childList, list);
            });
            map.addChild(childList);
        }
    }

    /**
     * 获取传入Map的所有子属性的key
     *
     * @param param
     * @return
     */
    private static List<String> getChildsKey(Map<String, Object> param) {
        List<String> answer = new ArrayList<>();
        param.forEach((k, v) -> {
            if (isChildMap(param, k)) {
                answer.add(k);
            }
        });
        return answer;
    }


    public static boolean isChildMap(Map param, String key) {
        Object o = param.get(key);
        if (o == null) {
            return false;
        }
        if (o instanceof List) {
            return true;
        }
        return o instanceof Map;
    }

    /**
     * 根据指定的标签名（遍历所有标签和子标签），属性名，获取这个属性的值
     *
     * @param root        需要处理的CompositeMap
     * @param elementName 需要匹配的标签名
     * @param attributes  需要匹配的属性名
     * @param filter      自定义属性筛选
     * @return 包含符合条件属性值的 Set
     */
    public static List findChildsAttriValue(CompositeMap root, String elementName, String[] attributes, IRecordFilter filter) {
        List answer = new ArrayList();
        for (String attr : attributes) {
            List<CompositeMap> childs = CompositeUtil.findChilds(root, elementName, attr, ANY_VALUE);
            if (filter == null) {
                for (CompositeMap child : childs) {
                    Object o = child.get(attr);
                    if (o != null) {
                        answer.add(o);
                    }
                }
            } else {
                for (CompositeMap child : childs) {
                    if (filter.accepts(child)) {
                        Object o = child.get(attr);
                        if (o != null) {
                            answer.add(o);
                        }
                    }
                }
            }
        }
        return answer;
    }


    /**
     * 根据指定的属性名（遍历所有标签和子标签），获取这个属性的值
     *
     * @param root       需要处理的CompositeMap
     * @param attributes 需要匹配的属性名
     * @param filter     自定义属性筛选
     * @return 包含符合条件属性值的 List
     */
    public static List findChildsAttriValue(CompositeMap root, String[] attributes, IRecordFilter filter) {
        return findChildsAttriValue(root, ANY_VALUE, attributes, filter);
    }

    /**
     * 根据指定的属性名（遍历所有标签和子标签），获取这个属性的值
     *
     * @param root       需要处理的CompositeMap
     * @param attributes 需要匹配的属性名
     * @return 包含符合条件属性值的 List
     */
    public static List findChildsAttriValue(CompositeMap root, String[] attributes) {
        return removeDuplicate(findChildsAttriValue(root, ANY_VALUE, attributes, null));
    }


    /**
     * 去重
     */
    public static List removeDuplicate(List list) {
        HashSet h = new HashSet(list);
        list.clear();
        list.addAll(h);
        return list;
    }

    /**
     * 获取页面中的 server-script 和 script 标签下的url
     *
     * @param root   根CompositeMap
     * @param answer
     */
    public static void addServerScriptUrl(CompositeMap root, List<String> answer) {
        List<CompositeMap> childs = new ArrayList<>();
        for (String key : KEY_FIND_SCRIPT) {
            childs.addAll(CompositeUtil.findChilds(root, key));
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (CompositeMap child : childs) {
            stringBuilder.append(child.getText());
        }
        String s = stringBuilder.toString();
        if (s.equals("")) {
            return;
        }
        String[] split = StringUtils.split(s, "\n");
        List<String> strings = Arrays.asList(split);
        List<String> collect = strings.parallelStream()
                .filter(line -> line.contains("${/request/@context_path}") && !line.contains("/*") && !line.contains("//") && !line.contains("<!--") && !line.contains("-->"))
                .distinct()
                .collect(Collectors.toList());
        //TODO 脚本收集逻辑
        for (String line : collect) {
            String result = "";
            result = line.substring(line.indexOf("${/request/@context_path}"));
            if (result.contains("?")) {
                result = result.substring(0, result.indexOf("?"));
            } else if (result.contains(".lview")) {
                result = result.substring(0, result.indexOf(".lview"));
            } else {
                //controller情况，不包含？和lview,
                if (result.contains("'")) {
                    result = result.substring(0, result.indexOf("'"));
                } else if (result.contains("\"")) {
                    result = result.substring(0, result.indexOf("\""));
                } else {
                    continue;
                }
            }
            if (!answer.contains(result)) {
                answer.add(result);
            }
        }
    }

}
