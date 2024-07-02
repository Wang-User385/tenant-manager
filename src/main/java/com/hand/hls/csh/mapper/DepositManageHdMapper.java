package com.hand.hls.csh.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.csh.dto.DepositManageHd;

import java.util.List;

public interface DepositManageHdMapper extends Mapper<DepositManageHd>{
    List<DepositManageHd> selectDepositManageByField(DepositManageHd dto);
    List<DepositManageHd> selectDepositManageByProject(DepositManageHd dto);
    List<DepositManageHd> selectDepositManageByFieldHistory(DepositManageHd dto);
    List<DepositManageHd> selectDepositManageByChangeField(DepositManageHd dto);
    List<DepositManageHd> selectManageHdById1(DepositManageHd depositManageHd);
    List<DepositManageHd> conRentCashQueryAll(DepositManageHd depositManageHd);
    List<DepositManageHd> conRentCashQueryOnlyAll(DepositManageHd depositManageHd);
    List<DepositManageHd> conRentCashQueryEnd(DepositManageHd depositManageHd);
    List<DepositManageHd> conRentCashQueryNormal(DepositManageHd depositManageHd);
    List<DepositManageHd> conRentCashQueryChange(DepositManageHd depositManageHd);
    List<DepositManageHd> conRentCashQueryHistory(DepositManageHd depositManageHd);
}