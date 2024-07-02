//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.csh.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.csh.exception.BeyondAmountLimitException;
import com.hand.hls.utils.ResMessageException;
import java.util.Date;
import java.util.List;

public interface IMainCshTransactionService extends IBaseService<HlsCusCshTransaction>, ProxySelf<IMainCshTransactionService> {
    void updateCshTrxAfterWriteOffReverse(Long var1, Long var2) throws Exception;

    void updateCshTrxAfterWriteOff(Long var1, HlsCusCshWriteOff var2) throws Exception;

    HlsCusCshTransaction reverseCshTransaction(IRequest var1, Long var2, Date var3, String var4) throws Exception;

    void reverseCshTransactionMain(IRequest var1, List<HlsCusCshTransaction> var2, Date var3, String var4) throws Exception;

    void updateCshTrxAfterTrxReverse(Long var1, Date var2) throws Exception;

    void createCshTrxPostJe(IRequest var1, HlsCusCshTransaction var2);

    void createTrxWriteOffAllJe(IRequest var1, HlsCusCshTransaction var2);

    void checkCshTransaction(Long var1, Double var2) throws BeyondAmountLimitException;

    void cshTransactionMain(IRequest var1, List<HlsCusCshTransaction> var2) throws Exception;

    HlsCusCshTransaction createCshTransaction(IRequest var1, HlsCusCshTransaction var2);

    void postCshTransaction(IRequest var1, HlsCusCshTransaction var2) throws ResMessageException;
}
