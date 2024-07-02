//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.csh.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.csh.dto.HlsCusCshBankAccount;
import java.util.List;

public interface CshBankAccountService extends IBaseService<HlsCusCshBankAccount>, ProxySelf<CshBankAccountService> {
    List<HlsCusCshBankAccount> queryCshBankAccount(IRequest var1, HlsCusCshBankAccount var2, int var3, int var4);
}
