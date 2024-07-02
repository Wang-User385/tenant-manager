//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.cont.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.HlsCusBpMasterBankAccount;
import com.hand.hls.cont.dto.HlsCusHlsBpMasterBankAccount;

import java.util.List;

public interface HlsBpMasterBankAccountService extends IBaseService<HlsCusBpMasterBankAccount>, ProxySelf<HlsBpMasterBankAccountService> {
    List<HlsCusBpMasterBankAccount> selectAll(IRequest var1, HlsCusBpMasterBankAccount var2, int var3, int var4);

    List<HlsCusBpMasterBankAccount> debtAccountQuery(IRequest var1, HlsCusBpMasterBankAccount var2, int var3, int var4);

    void modifyBpMasterBankAccountDeptBankId(HlsCusBpMasterBankAccount var1);

    List<HlsCusHlsBpMasterBankAccount>queryHlsBpMasterBankAccountLov(IRequest requestCtx, HlsCusHlsBpMasterBankAccount hlsCusHlsBpMasterBankAccount, int pagenum, int pagesize);
}
