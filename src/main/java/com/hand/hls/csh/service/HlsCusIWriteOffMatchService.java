package com.hand.hls.csh.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.csh.dto.HlsCusWriteOffMatch;
import com.hand.hls.csh.exception.BeyondAmountLimitException;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface HlsCusIWriteOffMatchService extends IBaseService<HlsCusWriteOffMatch>, ProxySelf<HlsCusIWriteOffMatchService> {


    /**
     * 已匹配数据写入表中
     * @param iRequest
     * @param cshWriteOffList
     * @param httpSession
     */
    void saveWriteOffMatch(IRequest iRequest, List<HlsCusCshWriteOff> cshWriteOffList, HttpSession httpSession)  throws BeyondAmountLimitException;

    /**
     * 已匹配的数据回退
     * @param iRequest
     * @param cshTransaction
     */
    void updateWriteOffMatchBack(IRequest iRequest,  HlsCusCshTransaction cshTransaction);

    /**
     * 查看为核销的数据
     * @param iRequest
     * @param hlsCusWriteOffMatch
     * @param page
     * @param pageSize
     * @return
     */
    List<HlsCusWriteOffMatch> selectWriteOffMatch(IRequest iRequest,HlsCusWriteOffMatch hlsCusWriteOffMatch, int page, int pageSize);



    /**
     * 核销数据回写状态
     * @param iRequest
     * @param cshWriteOffs
     */
    void updateWriteOffMatchCheck(IRequest iRequest, List<HlsCusCshWriteOff> cshWriteOffs);


    int selectMatchCount(Long cshTransactionId);


    void reversedWriteOffMatch(IRequest iRequest,HlsCusCshWriteOff cusCshWriteOff);
}
