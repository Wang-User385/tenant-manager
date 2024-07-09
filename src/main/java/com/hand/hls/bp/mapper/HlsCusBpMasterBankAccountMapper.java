//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.mapper;


import com.hand.hls.bp.dto.HlsCusBpMasterBankAccount;
import com.hand.hls.prj.dto.HlsBpMasterBankAccount;
import com.hand.hls.prj.mapper.HlsBpMasterBankAccountMapper;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.Map;

@Primary
public interface HlsCusBpMasterBankAccountMapper extends HlsBpMasterBankAccountMapper<HlsCusBpMasterBankAccount> {
    List<HlsCusBpMasterBankAccount> queryBpBankAccountIdByNum(HlsCusBpMasterBankAccount hlsCusBpMasterBankAccount);

    List<HlsCusBpMasterBankAccount> queryBpBankAccountInfo(HlsCusBpMasterBankAccount hlsCusBpMasterBankAccount);

    List<HlsCusBpMasterBankAccount> queryCusBpMasterBankByBpId(HlsCusBpMasterBankAccount hlsCusBpMasterBankAccount);

    List<HlsBpMasterBankAccount> queryBankDetails(Map map);
    List<HlsCusBpMasterBankAccount> query1();


    List<HlsCusBpMasterBankAccount> updateEbsCodeforBp(HlsCusBpMasterBankAccount hlsCusBpMasterBankAccount);

    List<HlsCusBpMasterBankAccount> queryBpBankAccountByProject(HlsCusBpMasterBankAccount hlsCusBpMasterBankAccount);
}
