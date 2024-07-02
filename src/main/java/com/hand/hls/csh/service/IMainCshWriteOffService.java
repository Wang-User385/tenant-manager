//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.csh.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.csh.dto.CshWriteOff;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.csh.exception.BeyondAmountLimitException;
import java.util.Date;
import java.util.List;

public interface IMainCshWriteOffService extends IBaseService<HlsCusCshWriteOff>, ProxySelf<IMainCshWriteOffService> {
    void updateOrigWriteOffAfterReverse(IRequest var1, Long var2, Long var3, Date var4) throws Exception;

    HlsCusCshWriteOff reverseCshWriteOff(IRequest var1, Long var2, Date var3, String var4) throws Exception;

    HlsCusCshWriteOff reverseCshWriteOffNew(IRequest var1, Long var2, Date var3, String var4, Long var5) throws Exception;

    HlsCusCshWriteOff cshWriteOff(IRequest var1, HlsCusCshWriteOff var2, Date var3) throws Exception;

    HlsCusCshWriteOff cshWriteOffPay(IRequest var1, HlsCusCshWriteOff var2, Date var3) throws Exception;

    void reverseCshWriteOffMain(IRequest var1, List<HlsCusCshWriteOff> var2, Date var3, String var4) throws Exception;

    void cshWriteOffMain(IRequest var1, List<HlsCusCshWriteOff> var2) throws Exception;

    List<HlsCusCshWriteOff> cshWriteOffMainPay(IRequest var1, List<HlsCusCshWriteOff> var2) throws Exception;

    void updateConCashflowAfterReverse(HlsCusCshWriteOff var1) throws Exception;

    void updateFctCashflowAfterReverse(HlsCusCshWriteOff var1) throws BeyondAmountLimitException;

    void updateConCashflowAfter(HlsCusCshWriteOff var1) throws Exception;

    void updateFctCashflowAfter(HlsCusCshWriteOff var1) throws BeyondAmountLimitException;

    void createReverseWriteOffJe(IRequest var1, CshWriteOff var2);

    void createWriteOffJe(IRequest var1, CshWriteOff var2);

    void checkCshWriteOffs(List<HlsCusCshWriteOff> var1) throws BeyondAmountLimitException;
}
