package com.hand.hls.lon.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.lon.dto.LonBankAccount;

import java.util.List;

public interface LonBankAccountMapper extends Mapper<LonBankAccount>{
    List<LonBankAccount> queryLonBankDetail(LonBankAccount lonBankAccount);

}