package com.hand.hls.abs.utils;

import com.hand.hap.system.dto.BaseDTO;
import com.hand.hls.fnd.dto.FndInterfaceLines;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class HlsCusExcelUtils {



    /**
     * excel 从中间表导入到业务表
     * @param linesList 中间表行表数据集合
     * @param cls 中间表实体类 class 类型
     * @param map 中间表字段和业务表字段对一个关系,如 key:userName,value:用户名,key是业务实体类属性名,value是excel列名
     * @param <T> 业务实体类,必须是 BaseDto 类的子类
     * @return 返回业务数据集合
     */
    public static <T extends BaseDTO> List<T> excelImportFromInterface(List<FndInterfaceLines> linesList, Class<T> cls, Map<String, String> map){
        if(CollectionUtils.isEmpty(linesList)){
            log.error("传入的中间表数据为空,请检查代码");
            return null;
        }
        if(map == null){
            log.error("请传递中间表和业务实体类之间的映射关系!");
            return null;
        }
        List<T> list = new ArrayList<>(10);  // 待返回的数据集合
        linesList.sort(Comparator.comparing(FndInterfaceLines::getHeaderId));
        FndInterfaceLines headLines = linesList.get(0);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            for(int i = 1; i < linesList.size(); i++) {
                T t = cls.newInstance();
                FndInterfaceLines lines = linesList.get(i);
                boolean flag = false;  // 用于判断这一行中间数据是否全空,如果有一个字段不是空值,那么flag置位true
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    String key = entry.getKey();  // 业务表属性名
                    Class fieldType = cls.getDeclaredField(key).getType();  // 当前属性的类型
                    Method m = cls.getDeclaredMethod("set" + key.substring(0, 1).toUpperCase() + key.substring(1), fieldType); // 当前属性的set方法
                    for (int j = 1; j < 500; j++) {
                        Method method = headLines.getClass().getDeclaredMethod("getAttributes_" + j);
                        String value = (String) method.invoke(headLines);  // 中间表第一行的列名
                        if (StringUtils.equals(entry.getValue(), value)) {
                            String fieldValue = (String)lines.getClass().getDeclaredMethod("getAttributes_"+j).invoke(lines);
                            if(StringUtils.isEmpty(fieldValue.trim())){
                                break;
                            }
                            flag = true;
                            if(fieldType == Integer.class){
                                m.invoke(t, Integer.valueOf(fieldValue));
                            }else if(fieldType == Long.class){
                                m.invoke(t, Long.valueOf(fieldValue));
                            }else if(fieldType == Double.class){
                                m.invoke(t, Double.valueOf(fieldValue));
                            }else if(fieldType == BigDecimal.class){
                                m.invoke(t, new BigDecimal(fieldValue));
                            }else if(fieldType == BigInteger.class){
                                m.invoke(t, new BigInteger(fieldValue));
                            }else if(fieldType == Date.class){
                                m.invoke(t, sdf.parse(fieldValue));
                            }else{
                                m.invoke(t, fieldValue);
                            }
                            break;
                        }
                    }
                }
                if(flag){
                    list.add(t);
                }
            }
            return list;
        } catch (NoSuchMethodException e) {
            log.error("获取反射方法异常");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            log.error("反射权限异常");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            log.error("invoke调用异常");
            e.printStackTrace();
        } catch (InstantiationException e) {
            log.error("创建 {} 类的对象异常", cls.getName());
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            log.error("获取反射属性异常");
            e.printStackTrace();
        } catch (ParseException e) {
            log.error("日期转化失败");
            e.printStackTrace();
        }
        return null;
    }

}

