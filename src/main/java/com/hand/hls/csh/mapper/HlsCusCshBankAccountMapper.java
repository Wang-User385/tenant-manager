//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.csh.mapper;


import com.hand.hls.csh.dto.HlsCusCshBankAccount;

import java.util.List;

public interface HlsCusCshBankAccountMapper extends CshBankAccountMapper<HlsCusCshBankAccount> {

    List<HlsCusCshBankAccount> selectCshBankAccountLov(HlsCusCshBankAccount hlsCusCshBankAccount);

    List<HlsCusCshBankAccount> cshBankAccountDetailQuery(HlsCusCshBankAccount hlsCusCshBankAccount);

    HlsCusCshBankAccount selectOneData(HlsCusCshBankAccount bankAccount);
}
