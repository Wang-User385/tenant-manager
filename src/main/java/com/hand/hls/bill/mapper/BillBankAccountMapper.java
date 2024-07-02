package com.hand.hls.bill.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bill.dto.BillBankAccount;

import java.util.List;

public interface BillBankAccountMapper extends Mapper<BillBankAccount>{
    List<BillBankAccount> selectBillBankAccount(BillBankAccount account);
}