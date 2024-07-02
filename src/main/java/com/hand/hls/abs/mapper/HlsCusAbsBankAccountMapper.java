package com.hand.hls.abs.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.abs.dto.HlsCusAbsBankAccount;

import java.util.List;

public interface HlsCusAbsBankAccountMapper extends Mapper<HlsCusAbsBankAccount> {

    /**
     * 查询
     * @param bankAccount
     * @return
     */
    List<HlsCusAbsBankAccount> selectAbsBankAccountData(HlsCusAbsBankAccount bankAccount);


    /**
     * 查询账户主体(去重)
     * @param bankAccount
     * @return
     */
    List<HlsCusAbsBankAccount>  selectDistinctAbsCompany(HlsCusAbsBankAccount bankAccount);


    /**
     * 数据引用条数 限制是否可删除
     * @param bankAccount
     * @return
     */
    int selectOtherDataQuoteCount(HlsCusAbsBankAccount bankAccount);

}