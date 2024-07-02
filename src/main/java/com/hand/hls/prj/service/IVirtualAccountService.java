package com.hand.hls.prj.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.VirtualAccount;
import java.util.List;
import java.util.Map;

public interface IVirtualAccountService extends IBaseService<VirtualAccount>, ProxySelf<IVirtualAccountService>{

    void batchImportVirtualAccount(IRequest iRequest, Long headerId) throws HlsCusException;

    /**
     *  分配虚拟账号
     * @param prjProject
     * @return
     * @throws HlsCusException
     */
    VirtualAccount assignVirtualAccount (HlsCusPrjProject prjProject);

    /**
     * 校验虚拟账号是否不足
     * @param project
     * @throws HlsCusException
     */
    void checkVirtualAccountReserve(HlsCusPrjProject project) throws HlsCusException;


    void transactionAutoClaim(IRequest iRequest , Long cshTransactionId) throws Exception;


    /**
     * 批量删除虚拟账户
     * @param iRequest 请求信息
     * @param virtualAccounts 需要删除的虚拟账号集合
     */
    void batchDeleteVirtualAccount(IRequest iRequest ,List<VirtualAccount> virtualAccounts);

    public String virtualAccountCheckBeforeSubmit(IRequest iRequest, Map params);

    void virtualAccountContractUpdate(IRequest iRequest, Map params);

}