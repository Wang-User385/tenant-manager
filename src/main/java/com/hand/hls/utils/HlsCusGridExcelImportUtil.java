package com.hand.hls.utils;


import com.hand.hls.fnd.dto.FndInterfaceLines;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:grid的EXCEL导入
 * @Author: wutianyu
 * @Date: Created in 下午2:53 2018/7/18
 */
public final class HlsCusGridExcelImportUtil {
    private static final Logger logger = LoggerFactory.getLogger(HlsCusGridExcelImportUtil.class);

    /**
     * 第三行开始才是真正需要导入的数据
     */
    private static final int START_SIZE = 3;

    private HlsCusGridExcelImportUtil() {

    }

    /**
     * @Description:导入
     * @Author: Wty
     * @Date: Created om 下午4:40 2018/7/18
     * @param: [clazz, list, fndInterfaceLinesList]  clazz类对象CLass,list返回的集合对象,fndInterfaceLinesList导入的中间表数据
     * @param: [updateName, updateId, syscodeMap] updateName根据字段更新的fieldName,updateId根据字段更新的值,syscodeMap系统代码值
     * @return: java.util.List<org.apache.poi.ss.formula.functions.T>
     */
    public static <T> List<T> excelImport(Class<T> clazz, List<T> list, List<FndInterfaceLines> fndInterfaceLinesList,
                                          String updateName, Long updateId, Map<String, List<Map>> syscodeMap) throws Exception {
        logger.debug("------------------start turn to excel ------------");
        int startSize = START_SIZE;
        String[] fileds = getInsertFields(fndInterfaceLinesList.get(0));
        for (int i = startSize; i < fndInterfaceLinesList.size(); i++) {
            Boolean notNull = false;
            T t = clazz.newInstance();
            for (int j = 1; j < fileds.length + 1; j++) {
                String methodName = "getAttributes_" + j;
                Method m = fndInterfaceLinesList.get(i).getClass().getMethod(methodName);
                String value = (String) m.invoke(fndInterfaceLinesList.get(i));
                if (value != null) {
                    value = value.replace(" ", "");
                    if (!"".equals(value)) {
                        notNull = true;
                        String field = fileds[j - 1];
                        String fieldMethodName = "set" + field.substring(0, 1).toUpperCase() + field.substring(1);
                        Class fieldClass = clazz.getDeclaredField(field).getType();
                        Method method = clazz.getMethod(fieldMethodName, fieldClass);
                        if (fieldClass.equals(Double.class)) {
                            method.invoke(t, Double.parseDouble(value));
                        } else if (fieldClass.equals(Integer.class)) {
                            method.invoke(t, Integer.parseInt(value));
                        } else if (fieldClass.equals(Long.class)) {
                            if (value.indexOf(".") > 0) {
                                Double d = Double.parseDouble(value);
                                method.invoke(t, new Long((long) d.doubleValue()));
                            } else {
                                method.invoke(t, Long.parseLong(value));
                            }
                        } else if (fieldClass.equals(Boolean.class)) {
                            method.invoke(t, Boolean.parseBoolean(value));
                        } else if (fieldClass.equals(Float.class)) {
                            method.invoke(t, Float.parseFloat(value));
                        } else if (fieldClass.equals(Date.class)) {
                            if (value.indexOf("/") > 0) {
                                method.invoke(t, new SimpleDateFormat("yyyy/MM/dd").parse(value));
                            } else if (value.indexOf("-") > 0) {
                                method.invoke(t, new SimpleDateFormat("yyyy-MM-dd").parse(value));
                            }
                        } else {
                            method.invoke(t, (String) value);
                        }
                    }
                }
            }
            if (notNull) {
                logger.debug("-------- set superField and list ------------");
                Method m = clazz.getSuperclass().getDeclaredMethod("set__status", String.class);
                m.invoke(t, "add");
                String updateMethodName = "set" + updateName.substring(0, 1).toUpperCase() + updateName.substring(1);
                Class fieldClass = clazz.getDeclaredField(updateName).getType();
                Method updateMethod = t.getClass().getMethod(updateMethodName, fieldClass);
                updateMethod.invoke(t, updateId);
                setSysCode(t, syscodeMap);
                list.add(t);
            }
        }
        logger.debug("----------- return list ---------");
        return list;
    }

    /**
     * @Description:设置syscode值
     * @Author: Wty
     * @Date: Created om 下午4:39 2018/7/18
     * @param: [t, map]
     * @return: void
     */
    public static <T> void setSysCode(T t, Map<String, List<Map>> map) {
        logger.debug("------------- set syscode --------------");
        map.forEach((k, v) -> {
            try {
                Class fieldClass = t.getClass().getDeclaredField(k).getType();
                String upperFieldName = k.substring(0, 1).toUpperCase() + k.substring(1);
                String setMethodName = "set" + upperFieldName;
                String getMethodName = "get" + upperFieldName;
                Method setMethod = t.getClass().getMethod(setMethodName, fieldClass);
                Method getMethod = t.getClass().getMethod(getMethodName);
                String getValue = (String) getMethod.invoke(t);
                v.forEach((m) -> {
                    String meaning = (String) m.get("meaning");
                    String value = (String) m.get("value");
                    if (meaning.equals(getValue)) {
                        try {
                            setMethod.invoke(t, value);
                        } catch (Exception e) {
                            logger.error(e.getMessage());
                            e.printStackTrace();
                            throw new IllegalArgumentException("导入失败，请检查模版");
                        }
                    }
                });
            } catch (Exception e) {
                logger.error(e.getMessage());
                e.printStackTrace();
                throw new IllegalArgumentException("导入失败，请检查模版");
            }
        });
        logger.debug("----------- set syscode end ------------");
    }

    /**
     * @Description:得到需要导入的字段
     * @Author: Wty
     * @Date: Created om 下午4:40 2018/7/18
     * @param: [f] 导入的模版数据
     * @return: java.lang.String[]
     */
    public static String[] getInsertFields(FndInterfaceLines f) throws Exception {
        logger.debug("----------- get object fields ---------------");
        List<String> list = new ArrayList<>();
        Field[] fields = FndInterfaceLines.class.getDeclaredFields();
        for (int i = 1; i < fields.length + 1; i++) {
            Method m = FndInterfaceLines.class.getMethod("getAttributes_" + i);
            Object v = m.invoke(f);
            String value = (v == null ? "" : (String) v).replace(" ", "");
            if (value != null && !"".equals(value)) {
                list.add(value);
            } else {
                break;
            }
        }
        return list.toArray(new String[list.size()]);
    }

}
