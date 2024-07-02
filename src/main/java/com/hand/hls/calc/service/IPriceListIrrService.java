package com.hand.hls.calc.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hls.calc.dto.HlsPriceListConfigBT;
import com.hand.hls.exception.HlsCusException;

public interface IPriceListIrrService {
    /**
     * 单变量求解，通过二分法实现excel中的单变量求解,目标单元格和可变单元格之间必须是函数相关
     *
     * @param sheetsObject 价目表Excel内容，从数据库中读取出来直接转成JSONObject传递即可
     * @param cellValues 计算前需要修改的单元格内容 [{field: xxx, value: xxx}]形式
     * @param targetCell 单变量求解配置信息
     *
     * @return 可变单元格的值
     */
    Object goalSeek(JSONObject sheetsObject, JSONArray cellValues, HlsPriceListConfigBT targetCell, Double targetIrr) throws HlsCusException;
}
