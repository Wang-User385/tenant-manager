package com.hand.hls.fnd.service;

import com.hand.hap.core.IRequest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by 王也 on 2017/7/28.
 * 设计通用导入类接口
 * 实现验证方法
 * 实现导入方法
 */
public interface HlsCusImportInterface {
    /**
     * 数据导入方法
     * 将临时表中的数据提取出来，每条数据封装成一个Map对象
     * Map<属性名，属性值>
     * 每个Map应该可以转换成一个DTO对象
     * 该方法重写需要实现以下功能
     * 1.Map转换为DTO
     * 2.数据合法性校验
     * 3.将合法的数据插入到数据库中
     * @param iRequest
     * @param dataMap 临时表数据
     * @param descMap 获取字段描述用Map,<key=Dto属性名,value=模板中定义的显示用名>
     * @param lang 当导入涉及到多语言时（在模板定义时勾选了多语言选项）这里会传入一个在执行导入时勾选的多语言对应的Code
     * @return 执行成功的条数
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    int importData(IRequest iRequest, List<Map<String, String>> dataMap, Map<String, String> descMap, String lang);

}
