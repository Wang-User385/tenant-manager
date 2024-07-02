//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsCusBpMasterBankAccount;
import com.hand.hls.cont.dto.HlsCusHlsBpMasterBankAccount;
import com.hand.hls.prj.dto.HlsBpMasterBankAccount;


import java.util.List;

public interface HlsBpMasterBankAccountMapper<T extends HlsCusBpMasterBankAccount> extends Mapper<HlsCusBpMasterBankAccount> {
    List<HlsCusBpMasterBankAccount> queryAll(HlsCusBpMasterBankAccount var1);

    List<HlsCusBpMasterBankAccount> debtAccountQuery(HlsCusBpMasterBankAccount var1);

    void modifyBpMasterBankAccountDeptBankId(HlsCusBpMasterBankAccount var1);

    List<HlsCusHlsBpMasterBankAccount>queryHlsBpMasterBankAccountLov(HlsCusHlsBpMasterBankAccount hlsCusHlsBpMasterBankAccount);

    List<HlsBpMasterBankAccount> query();
}
