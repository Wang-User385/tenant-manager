package com.hand.hls.utils;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @Author:wenzheng.shao@hand-china.com
 * @CreateTime:2018-10-16 17:21;
 * @代码描述: 动态为类的属性赋值工具类
 * @主要参数: 类 bean
 **/
public class BeanRefUtil {
    public static DecimalFormat df =new DecimalFormat("###,##0.00");
    /**
     * 取Bean的属性和值对应关系的MAP
     * @param bean
     * @return Map
     */
    public static Map<String, String> getFieldValueMap(Object bean) {
        Class<?> cls = bean.getClass();
        Map<String, String> valueMap = new HashMap<>();
        // 取出bean里的所有方法
        Method[] methods = cls.getDeclaredMethods();
        //所有属性
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            try
            {
                //获取对象类型
                String fieldType = field.getType().getSimpleName();
                //得到getter方法的名称
                String fieldGetName = parGetName(field.getName());
                //校验
                if (!checkGetMet(methods, fieldGetName)) {
                    continue;
                }
                //getter方法
                Method fieldGetMet = cls.getMethod(fieldGetName);
                Object fieldVal = fieldGetMet.invoke(bean);
                String result = null;
                if ("Date".equals(fieldType)) {
                    result = fmtDate((Date) fieldVal);
                }
                else if ("Double".equalsIgnoreCase(fieldType)
                        ||"Long".equalsIgnoreCase(fieldType)
                        ||"BigDecimal".equalsIgnoreCase(fieldType)){
                    result=df.format(fieldVal);
                }
                else {
                    if (null != fieldVal) {
                        result = String.valueOf(fieldVal);
                    }else{
                        result=" ";
                    }
                }
                    valueMap.put(field.getName(),result);
            } catch (Exception ignored) {
            }
        }
        return valueMap;
    }
    public static Map<String, String> getFieldValueMap(Object bean,boolean ignoreNull){
        Map<String, String> map =getFieldValueMap(bean);
        Map<String, String> res=new HashMap<>();
        if (ignoreNull)
        {
            for (String key:map.keySet())
            {
                if (StringUtils.isNotEmpty(map.get(key))&&StringUtils.isNotBlank(map.get(key))){
                    res.put(key, map.get(key));
                }
            }
        }
        return res;
    }
    /**
     * set属性的值到Bean
     * @param bean 返回结果
     * @param valMap  需要转换的对象
     */
    public static void setFieldValue(Object bean, Map<String, String> valMap) {
        Class<?> cls = bean.getClass();
        // 取出bean里的所有方法
        Method[] methods = cls.getDeclaredMethods();
        Field[] fields = cls.getDeclaredFields();

        for (Field field : fields) {
            try {

                String fieldSetName = parSetName(field.getName());
                if (!checkSetMet(methods, fieldSetName)) {
                    //System.out.println(fieldSetName);
                    continue;
                }
                Method fieldSetMet = cls.getMethod(fieldSetName, field.getType());
                String value = valMap.get(field.getName());
                if (null != value && !"".equals(value)&&!"null".equals(value)) {
                    String fieldType = field.getType().getSimpleName();
                    if ("String".equals(fieldType)) {
                        fieldSetMet.invoke(bean, value);
                    } else if ("Date".equals(fieldType)) {
                        Date temp = parseDate(value);
                        fieldSetMet.invoke(bean, temp);
                    } else if ("Integer".equals(fieldType)
                                       || "int".equals(fieldType)) {
                        Integer int_val = Integer.parseInt(value);
                        fieldSetMet.invoke(bean, int_val);
                    } else if ("Long".equalsIgnoreCase(fieldType)||"long".equalsIgnoreCase(fieldType)) {
                        Long temp = Long.parseLong(value);
                        fieldSetMet.invoke(bean, temp);
                    } else if ("Double".equalsIgnoreCase(fieldType)||"double".equalsIgnoreCase(fieldType)) {
                        Double temp = Double.parseDouble(value);
                        fieldSetMet.invoke(bean, temp);
                    } else if ("Boolean".equalsIgnoreCase(fieldType)||"boolean".equalsIgnoreCase(fieldType)) {
                        Boolean temp = Boolean.parseBoolean(value);
                        fieldSetMet.invoke(bean, temp);
                    } else {
                        fieldSetMet.invoke(bean,String.valueOf(value));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 格式化string为Date
     * @param datestr
     * @return date
     */
    public static Date parseDate(String datestr) {
        if (null == datestr || "".equals(datestr)) {
            return null;
        }
        try {
            String fmts = null;
            if (datestr.indexOf(':') > 0) {
                fmts = "yyyy-MM-dd HH:mm:ss";
            } else {
                fmts = "yyyy-MM-dd";
            }
            SimpleDateFormat sdf = new SimpleDateFormat(fmts, Locale.CHINESE);
            return sdf.parse(datestr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 日期转化为String
     * @param date
     * @return date string
     */
    private static String fmtDate(Date date) {
        if (null == date) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINESE);
            return sdf.format(date);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断是否存在某属性的 set方法
     * @param methods
     * @param fieldSetMet
     * @return boolean
     */
    private static boolean checkSetMet(Method[] methods , String fieldSetMet) {
        for (Method met : methods) {
            if (fieldSetMet.equals(met.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否存在某属性的 get方法
     * @param methods
     * @param fieldGetMet
     * @return boolean
     */
    private static boolean checkGetMet(Method[] methods , String fieldGetMet) {
        for (Method met : methods) {
            if (fieldGetMet.equals(met.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 拼接某属性的 get方法
     * @param fieldName
     * @return String
     */
    private static String parGetName(String fieldName) {
        if (null == fieldName || "".equals(fieldName)) {
            return null;
        }
        return "get" + fieldName.substring(0, 1).toUpperCase()
                       + fieldName.substring(1);
    }

    /**
     * 拼接在某属性的 set方法
     * @param fieldName
     * @return String
     */
    private static String parSetName(String fieldName) {
        if (null == fieldName || "".equals(fieldName)) {
            return null;
        }
        return "set" + fieldName.substring(0, 1).toUpperCase()
                       + fieldName.substring(1);
    }


}


