package com.hand.hls.utils;

import com.hand.hap.system.dto.BaseDTO;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by 王也 on 2017/7/31.
 */
public class HlsCusImportDataUtil {
    /**
     * 当传递的时间格式为空时，为时间格式初始化一个默认类型
     */
    private static String DEFAULT_DATE_FORMAT="yyyy/MM/dd HH:mm:ss";
    /**
     * 数据转换方法
     * 将批次定位的List<ImpData> 转换成对应的 List<dto>
     * 使用反射机制注入的bean中
     * 注意：由于此方法使用了Java BeanInfo获取的对象信息
     *      所以调用此方法时，DTO命名必须遵循Java 变量命名规范
     *      1.定义变量时必须使用包装类
     *      2.getter、setter方法 为get/set+变量名首字母大写
     *      3.考虑到一些特定的有意思的英文缩略词，java Bean规定，变量名必须满足 “变量的前两个字母要么全部大写，要么全部小写”
     *      4.setter方法不支持builder模式
     * @param clazz dto类名
     * @param dataMap 数据对象<对象属性值，对象值>
     * @param dateFormats 时间可能应用到的格式
     * @throws IntrospectionException 类无法解析异常
     * @throws IllegalAccessException 类不存在默认构造函数
     * @throws InstantiationException 类不是一个实现类，而是一个抽象类/接口
     * @throws InvocationTargetException setter方法调用失败，1、不存在setter方法；2、setter方法不是void类型；3、setter方法传递了多个参数
     * @throws ParseException 数据中存在时间，时间格式解析失败
     * @return List<Object> 返回的List和传入的List<Map>应该是一一对应关系
     */
    public static List<Object> dataToDto(Class clazz, List<Map<String, String>> dataMap,List<String> dateFormats)
            throws IntrospectionException,IllegalAccessException,
            InstantiationException,ParseException,InvocationTargetException{
        if(dateFormats==null){
            dateFormats=new ArrayList<>();
        }
        dateFormats.add(DEFAULT_DATE_FORMAT);
        if(clazz!=null){
            BeanInfo beanInfo=null;
            try{
                beanInfo= Introspector.getBeanInfo(clazz, BaseDTO.class);//类不是继承自BaseDTO会抛出IntrospectionException异常
            }catch (IntrospectionException e){
                beanInfo=Introspector.getBeanInfo(clazz);//如果无法解析这个类 依然会抛出IntrospectionException异常
            }
            if(beanInfo!=null){
                PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
                if (propertyDescriptors!=null){
                    List<Object> result=new ArrayList<>();
                    for(Map<String,String> m:dataMap){
                        /**
                         * 当该dto不存在默认构造参数时 抛出IllegalAccessException
                         * 当该dto是一个接口/抽象类时 抛出InstantiationException
                         */
                        Object dto=dataToDto(clazz,m,dateFormats,propertyDescriptors);
                        result.add(dto);
                    }
                    return result;
                }
            }
        }
        return null;
    }

    /**
     * 数据转换方法
     * 将批次定位的Map 转换成对应的 dto
     * 使用反射机制注入的bean中
     * 注意：由于此方法使用了Java BeanInfo获取的对象信息
     *      所以调用此方法时，DTO命名必须遵循Java 变量命名规范
     *      1.定义变量时必须使用包装类
     *      2.getter、setter方法 为get/set+变量名首字母大写
     *      3.考虑到一些特定的有意思的英文缩略词，java Bean规定，变量名必须满足 “变量的前两个字母要么全部大写，要么全部小写”
     *      4.setter方法不支持builder模式
     * @param clazz 目标Dto的类对象
     * @param map 要转换为Dto 的map对象
     * @param dateFormats 转换日期时使用的/可能使用到的时间格式
     * @return
     * @throws IntrospectionException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static Object dataToDto(Class clazz,Map<String,String> map,List<String> dateFormats)
            throws IntrospectionException,InstantiationException,IllegalAccessException,InvocationTargetException{
        if(clazz!=null){
            BeanInfo beanInfo=null;
            try{
                beanInfo= Introspector.getBeanInfo(clazz, BaseDTO.class);//类不是继承自BaseDTO会抛出IntrospectionException异常
            }catch (IntrospectionException e){
                beanInfo=Introspector.getBeanInfo(clazz);//如果无法解析这个类 依然会抛出IntrospectionException异常
            }
            if(beanInfo!=null){
                PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
                if (propertyDescriptors!=null){
                    return dataToDto(clazz,map,dateFormats,propertyDescriptors);
                }
            }
        }
        return null;
    }

    private static Object dataToDto(Class clazz,Map<String,String> map,List<String> dateFormats,PropertyDescriptor... propertyDescriptors)
            throws InstantiationException,IllegalAccessException,InvocationTargetException{
        /**
         * 当该dto不存在默认构造参数时 抛出IllegalAccessException
         * 当该dto是一个接口/抽象类时 抛出InstantiationException
         */
        Object dto = clazz.newInstance();

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat();
        simpleDateFormat.setLenient(false);

        for(PropertyDescriptor p:propertyDescriptors){

            /**
             * 需要解析的值
             */
            String value = map.get(p.getName());
            if(value==null){//无值，直接不循环
                continue;
            }
            if(!p.getPropertyType().isArray()){//通用注入不支持注入数组
                Class propertyType = p.getPropertyType();
                /**
                 * 获取setter方法
                 * 当执行该方法的invoke时，如果该方法发生异常 抛出InvocationTargetException
                 */
                Method setMethod = p.getWriteMethod();
                if(setMethod==null){
                    throw new NullPointerException("请检查"+p.getName()+"的setter方法是否存在或是否符合规则");
                }
                if(Number.class.isAssignableFrom(propertyType)){//数字类型
                    /**
                     * 数字支持Float,Double,Long,Short,Integer五种格式
                     */
                    BigDecimal decimal = null;
                    try{
                        decimal = new BigDecimal(value);
                    }catch (Exception e){
                        //do nothing
                    }
                    if(decimal!=null){
                        if(BigDecimal.class.isAssignableFrom(propertyType)){
                            setMethod.invoke(dto,decimal);
                        }else if(Float.class.isAssignableFrom(propertyType)){//Float
                            setMethod.invoke(dto, decimal.floatValue());
                        }else if(Double.class.isAssignableFrom(propertyType)){//Double
                            setMethod.invoke(dto,decimal.doubleValue());
                        }else if(Long.class.isAssignableFrom(propertyType)){//Long
                            setMethod.invoke(dto,decimal.longValue());
                        }else if(Short.class.isAssignableFrom(propertyType)){//Short
                            setMethod.invoke(dto,decimal.shortValue());
                        }else if(Integer.class.isAssignableFrom(propertyType)){//Integer
                            setMethod.invoke(dto,decimal.intValue());
                        }
                    }
                }else if(String.class.isAssignableFrom(propertyType)){//字符串类型
                    setMethod.invoke(dto,value);
                }else if(Date.class.isAssignableFrom(propertyType)){//时间类型
                    boolean parseSuccess=false;
                    Date date=null;
                    for(String format:dateFormats){
                        try{
                            simpleDateFormat.applyPattern(format);
                            date=simpleDateFormat.parse(value);
                            parseSuccess=true;
                        }catch (ParseException e){
                            continue;
                        }
                    }

                    if(parseSuccess){//解析成功
                        setMethod.invoke(dto, date);
                    }else {//遍历所有的formats后解析依然失败
                        //setMethod.invoke(dto,null);
                        //throw new ParseException("无匹配的格式",0);
                    }
                }
            }
        }
        return dto;
    }

    public static boolean isNumber(String value){
        boolean flag=true;
        try{
            Double.parseDouble(value);
        }catch (Exception ed){
            try{
                Float.parseFloat(value);
            }catch (Exception ef){
                try{
                    Long.parseLong(value);
                }catch (Exception el){
                    try {
                        Integer.parseInt(value);
                    }catch (Exception ei){
                        try{
                            Short.parseShort(value);
                        }catch (Exception es){
                            flag=false;
                        }
                    }
                }
            }
        }
        return flag;
    }
}
