package com.hand.hls.abs.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.abs.dto.HlsCusAbsBankAccount;

import java.util.List;

public interface HlsCusAbsBankAccountService extends IBaseService<HlsCusAbsBankAccount>, ProxySelf<HlsCusAbsBankAccountService> {


    /**
     * 查询
     * @param iRequest
     * @param bankAccount
     * @param page
     * @param pageSize
     * @return
     */
    List<HlsCusAbsBankAccount> selectAbsBankAccountData(IRequest iRequest, HlsCusAbsBankAccount bankAccount, int page, int pageSize);


    /**
     * 查询账户主体(去重)
     * @param bankAccount
     * @return
     */
    List<HlsCusAbsBankAccount>  selectDistinctAbsCompany(IRequest iRequest, HlsCusAbsBankAccount bankAccount);


    /**
     * 数据引用条数 限制是否可删除
     * @param iRequest
     * @param bankAccount
     * @return
     */
    int selectOtherDataQuoteCount(IRequest iRequest, HlsCusAbsBankAccount bankAccount);
}