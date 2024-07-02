package com.hand.hls.app.user.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


public interface UserAppMapper {

    String selectByProcessId(@Param("workProcessId") Long workProcessId);

    /**
     * 查询下拉框的值
     * @param code
     * @return
     */
    List<Map> selectComboBox(@Param("code") String code);

    /**
     * 查询报价方式
     * @return
     */
    List<Map> selectPriceList();

    /**
     * 查询租赁类型
     * @return
     */
    List<Map> selectBusinessType();

    /**
     * 查询税种
     * @return
     */
    List<Map> selectTaxType();
}
