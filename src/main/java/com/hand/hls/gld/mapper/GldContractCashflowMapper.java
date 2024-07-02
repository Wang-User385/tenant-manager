package com.hand.hls.gld.mapper;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @Description:
 * @author: congweijing
 * @date: 2021/5/6 10:46
 */
import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.gld.dto.GldContractCashflow;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GldContractCashflowMapper extends Mapper<GldContractCashflow> {
    /**
     * 查询合并后的计提现金流,租金、手续费、保证金、管理费、租前息、首付款
     * @param contractId
     * @return
     */
    List<GldContractCashflow> queryMergeCashflow(@Param("contractId") Long contractId);

    /**
     * 查询合并后的计提现金流  首付款和设备款的fin_income_date取设备款的核销日期
     * @param contractId
     * @return
     */
    List<GldContractCashflow> queryMergeCashflowRetail(@Param("contractId") Long contractId);

    Double queryTotalIncome(@Param("contractId") Long contractId);
    Double query0Times(@Param("contractId") Long contractId);

    Double queryLastCf(@Param("contractId") Long contractId);

    Double queryLastCfRetail(@Param("contractId") Long contractId);


    /**
     * 从数据库查询表
     * @return
     */
    List<GldContractCashflow> queryMergeCashflowLast(GldContractCashflow gldContractCashflow);

    /**
     * 查询未确认收入的起始现金流期数
     * @param contractId
     * @return
     */
    Long queryChangeTermCashflow(@Param("contractId") Long contractId);
}
