//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.csh.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.csh.dto.HlsCusCshBank;
import java.util.List;

public interface CshBankService extends IBaseService<HlsCusCshBank>, ProxySelf<CshBankService> {
    List<HlsCusCshBank> queryCshBank(IRequest var1, HlsCusCshBank var2, int var3, int var4);
}
